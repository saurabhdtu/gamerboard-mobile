package com.gamerboard.live.service.screencapture.ui

import android.content.Context
import android.graphics.Point
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.net.Uri
import android.os.Environment
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.RemoteConfigConstants
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.gamestatemachine.showToast
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.io.File
import kotlin.math.min


interface GameVideoRecording {
    fun startRecordingGame(gameId: String)
    fun stopRecordingGame()
    fun deleteRecording()
}

interface MediaProjectionProvider {
    fun setMediaProjection(mediaProjection: MediaProjection?, densityDPI: Int)
    fun clearMediaProjection()
}

class VideoRecorder(val ctx: Context, val display: Point) : GameVideoRecording,
    MediaProjectionProvider {

    private var mediaRecorder: MediaRecorder? = null
    var virtualDisplay: VirtualDisplay? = null
    var currentGameId: String? = null

    private val imgDir =
        File("${ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/game_play_videos/")
    private var displayRes: Point
    private var jsonObject = JSONObject(
        FirebaseRemoteConfig.getInstance()
            .getString(RemoteConfigConstants.REC_CONFIG)
    )
    private var bitRate: Double = jsonObject.getDouble("game_play_bit_rate")
    private var captureFps: Int = jsonObject.getInt("game_play_fps")
    private var resHeight = jsonObject.getInt("game_play_res_height")

    init {
        displayRes = getProportionalDisplay(display)
        showToast(
            "Configured at: FPS:$captureFps, Rate:$bitRate, ResHeight:$resHeight",
            force = true
        )
        initializeRecorderFile()
        setUpMediaRecorder()
    }

    private fun setUpMediaRecorder() {
        mediaRecorder = MediaRecorder()
        mediaRecorder?.setVideoSource(MediaRecorder.VideoSource.SURFACE)
        mediaRecorder?.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder?.setVideoSize(displayRes.x, displayRes.y)

        mediaRecorder?.setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        mediaRecorder?.setVideoEncodingBitRate((1024 * bitRate).toInt() * 1000)
        mediaRecorder?.setVideoFrameRate(captureFps)

        prepareFileToRecordGame()
    }

    private fun getProportionalDisplay(display: Point, captureHeight: Int = resHeight): Point {
        val heightOfScreen = min(display.x, display.y)
        val y = min(heightOfScreen, captureHeight) // x, 720
        val x = ((display.x.toFloat() / display.y.toFloat()) * y).toInt()
        return Point(x, y)
    }

    private fun prepareFileToRecordGame() {
        mediaRecorder?.setOutputFile(File(imgDir, "temp.mp4").path)
        mediaRecorder?.prepare()
    }

    override fun startRecordingGame(gameId: String) {
        if (BuildConfig.DEBUG)
            showToast("Recording your game play!", force = true)
        currentGameId = gameId
        mediaRecorder?.start()
    }

    override fun stopRecordingGame() {
        if (BuildConfig.DEBUG)
            showToast("Finished recording!", force = true)
        mediaRecorder?.stop()

        //start uploading the file
        currentGameId?.let { startUploading(it) }

        //rename file
        currentGameId?.let { File(imgDir, "temp.mp4").renameTo(File(imgDir, "$it.mp4")) }
        resetRecorderFile()
    }

    override fun deleteRecording() {
        mediaRecorder?.stop()
        resetRecorderFile()
    }

    private fun startUploading(gameId: String) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                if (BuildConfig.DEBUG)
                    showToast("Starting to upload video ", force = true)
                val file = File(imgDir, "$gameId.mp4")
                val fileUri = Uri.fromFile(file)
                val uploadRef =
                    Firebase.storage.reference.child(
                        "gameplays/android/${
                            GamerboardApp.instance.prefsHelper.getString(
                                SharedPreferenceKeys.UUID
                            )
                        }"
                    ).child("${fileUri.lastPathSegment}")
                uploadRef.putFile(fileUri).await()
                if (BuildConfig.DEBUG)
                    showToast("Upload finished!", force = true)
            }
        } catch (e: Exception) {
            if (BuildConfig.DEBUG)
                showToast("Failed to upload!", force = true)
        }
    }

    private fun resetRecorderFile() {
        File(imgDir, "temp.mp4").delete()
        currentGameId = null
        initializeRecorderFile()
    }

    fun initializeRecorderFile() {
        if (!imgDir.exists())
            imgDir.mkdirs()
    }

    override fun setMediaProjection(mediaProjection: MediaProjection?, densityDPI: Int) {
        assert(mediaRecorder != null)
        assert(mediaProjection != null)

        virtualDisplay = mediaProjection!!.createVirtualDisplay(
            "video_game_play", displayRes.x, displayRes.y, densityDPI,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            mediaRecorder?.surface, null, null
        )
    }

    override fun clearMediaProjection() {
        virtualDisplay?.release()
    }
}