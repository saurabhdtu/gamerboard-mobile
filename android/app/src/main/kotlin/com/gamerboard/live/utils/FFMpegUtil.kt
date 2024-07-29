package com.gamerboard.live.utils

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.gamestatemachine.stateMachine.MachineMessageBroadcaster
import com.gamerboard.live.models.db.AppDatabase
import com.gamerboard.logger.ILogger
import com.gamerboard.logger.log
import com.gamerboard.logger.logger
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.metrics.AddTrace
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import java.io.File
import java.lang.Long

val FFMpegModule = module {
    factory {
        FFMpegUtil(androidContext())
    }
}


/**
 * Created by saurabh.lahoti on 02/12/21
 */
class FFMpegUtil(val ctx: Context) : KoinComponent {
    private val LOG_TAG = "gameplay-create"
    private val LOG_TAG_TEST = "gameplay-create-test"
    private var counter = 0
    private var MAX_RUN = 2
    private val logger: ILogger by inject()
    private val imageListFile = "images.txt"
    private val gameIds = ArrayList<String>()
    private val VIDEO_WIDTH = 720


    //Root folder which contains images for each gameplay
    private val imgDir =
        File("${ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/gameplays")

    //Root folder which contains videos for each gameplay
    private val outputDir = ctx.getExternalFilesDir(Environment.DIRECTORY_MOVIES)

    init {
        imgDir.mkdirs()
        outputDir?.mkdirs()
    }

    private fun createVideo() {
        var run = 0
        for (folder in imgDir.listFiles()) {
            if (folder.isDirectory) {
                if (folder.isDirectory && run < MAX_RUN) {
                    if (runFFmpeg(folder))
                        run++
                }
            }
        }
    }

    @AddTrace(name = "FFMpeg_video", enabled = true)
    private fun runFFmpeg(folder: File): Boolean {
        try {
            val gameId = Long.parseLong(folder.name)
            //                    val input =
//                        FFmpegKitConfig.getSafParameterForRead(ctx, Uri.fromFile(folder))
            val imageList = File(folder, imageListFile)
            imageList.delete()
            val fileList = arrayListOf<File>().also { it.addAll(folder.listFiles()) }
            val sortedList =
                fileList.sortedWith(compareBy { Integer.parseInt(it.name.split(".")[0]) })
            var height = -1
            for (file in sortedList) {
                if (height == -1) {
                    val options: BitmapFactory.Options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(file.absolutePath, options)
                    val imageHeight: Int = options.outHeight
                    val imageWidth: Int = options.outWidth
                    val scaleRatio = imageWidth / 480
                    height = imageHeight / scaleRatio
                    if (height % 2 != 0)
                        height++
                }
                if (file.path.endsWith(".jpg"))//excluding the text file itself
                    imageList.appendText("file '${file.path}'\n")
            }
            val outFile = File(outputDir, "temp-${folder.name}.mp4")
            outFile.delete()
            val startTime = System.currentTimeMillis()
            log(LOG_TAG + " video creation started: gameId:${folder.name}")
            Log.i(LOG_TAG, "Started video creation for game id ${folder.name}")
            MachineMessageBroadcaster.invoke()
                ?.showLoader(
                    true,
                    "We are processing your game. Please wait for your next game"
                )
            /*FFmpegKit.executeAsync(
                "-f concat -safe 0 -r 6 -i ${imageList.path} -r 6 -b:v 400k -vf \"scale=$VIDEO_WIDTH:$height\"  -preset ultrafast -vcodec libx264 ${outFile.path}",
                { session ->
                    Log.i(LOG_TAG, "callback--> session:$session")
                    if (session.returnCode.isSuccess) {
                        val output = File(outputDir, "${folder.name}.mp4")
                        outFile.renameTo(output)
                        val timeTaken = System.currentTimeMillis() - startTime
                        EventUtils.instance().logFirebaseEvent(
                            "video_created",
                            Bundle().apply {
                                putString("game_id", folder.name)
                                putLong("tt", timeTaken)
                                putLong("file_size", output.length())
                                putString("session_duration", session.duration.toString())
                            })
                        logger.log(LOG_TAG + " Video created: gameId:${folder.name}; tt:$timeTaken")
                        Log.i(
                            LOG_TAG,
                            "video created for game id ${folder.name} in $timeTaken ms"
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            folder.deleteRecursively()
                            //showLoader(false, "")
                            EventUtils.instance().logFirebaseEvent(
                                "gameplay_upload_started",
                                Bundle().apply { putString("game_id", folder.name) })
                            uploadGameplayNow(ctx, appDatabase, gameId, output)
                        }
                    } else {
                        logger.log("Video failed: gameId:${folder.name}: ${session.allLogsAsString}")
                        Log.i(LOG_TAG, "video failed for game id ${folder.name}")
                    }

                },
                { log ->
                    logger.log(log.message)
                    Log.i(LOG_TAG, "log-->${log.message}")
                },
                {
                    //showLoader(false, "")
                })*/
            return true
        } catch (e: Exception) {
            //showLoader(false, "")
            logException(e, logger = logger())
            e.printStackTrace()
        }
        return false
    }

    fun resetImages() {
        counter = 0
        imgDir.listFiles().forEach { it.deleteRecursively() }
    }

    fun updateGameId(gameId: String) {
        /*if (gameIds.contains(gameId).not()) {
            gameIds.add(gameId)
            Log.i(LOG_TAG, "updateGameId called")
            val file = File(imgDir, StateMachineStringConstants.UNKNOWN)
            file.renameTo(File(imgDir, gameId))
            createVideo()
        }*/
    }

    fun scheduleUpload() {
       /* CoroutineScope(Dispatchers.IO).launch {
            for (file in outputDir!!.listFiles()) {
                uploadGameplayNow(ctx, appDatabase, file.nameWithoutExtension.toLong(), file)
                break
            }
        }*/
        /*  val internetConstants =
              Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
          val gameplayUploadWork =
              OneTimeWorkRequestBuilder<GameplayUploadWorker>().setConstraints(
                  internetConstants
              )
                  .build()
          WorkManager.getInstance(ctx).enqueueUniqueWork(
              "com.gamerboard.gameplay-uploader",
              ExistingWorkPolicy.KEEP,
              gameplayUploadWork
          )*/
    }

    /*private fun showLoader(show: Boolean, message: String) {
        ctx.sendBroadcast(Intent(BroadcastFilters.SERVICE_COM).apply {
            putExtra("action", "screen_loader")
            putExtra("show", show)
            putExtra("message", message)
        })
    }*/

    @AddTrace(name = "upload_gameplay", enabled = true)
    suspend fun uploadGameplayNow(
        ctx: Context,
        appDatabase: AppDatabase,
        gameId: kotlin.Long,
        file: File
    ) {
        Log.i(LOG_TAG, "to upload ${file.path}")
        val startTime = System.currentTimeMillis()
        val upload = Uri.fromFile(file)
        val games = appDatabase.getGamesDao().getGameById(gameId = gameId.toString())
        if (games.isNotEmpty()) {
            val game = games.first()
            if (game.serverGameId != null && game.userId != null) {

                val uploadRef =
                    Firebase.storage.reference.child(
                        "gameplays/android/${
                            (ctx as GamerboardApp).prefsHelper.getString(
                                SharedPreferenceKeys.UUID
                            )
                        }"
                    ).child("${upload.lastPathSegment}")
                Log.i(LOG_TAG, "uploading at $uploadRef")
                MachineMessageBroadcaster.invoke()?.showLoader(
                    true,
                    "We are processing your game. Please wait for your next game")
                try {
                    uploadRef.putFile(upload).await()
                    val jsonObj = JSONObject().apply {
                        put("gameId", game.serverGameId)
                        put("userId", game.serverUserId)
                    }
                    uploadRef.updateMetadata(
                        StorageMetadata.Builder().setCustomMetadata("meta", jsonObj.toString())
                            .build()
                    ).await()
                } catch (ex: Exception) {
                    logException(ex)
                } finally {
                    //showLoader(false, "")

                }
                Log.i(
                    LOG_TAG,
                    "to delete ${File(imgDir, file.name.split(".")[0]).path}"
                )
                file.delete()
            }
        }
    }

    fun testVideoCreation() {
        var output = 0
        for (folder in imgDir.listFiles()) {
            if (folder.isDirectory) {
                try {
                    Long.parseLong(folder.name)
//                    val input =
//                        FFmpegKitConfig.getSafParameterForRead(ctx, Uri.fromFile(folder))
                    val imageList = File(folder, imageListFile)
                    imageList.delete()
                    val fileList =
                        arrayListOf<File>().also { it.addAll(folder.listFiles()) }
                    val sortedList =
                        fileList.sortedWith(compareBy { Integer.parseInt(it.name.split(".")[0]) })
                    var height = -1
                    for (file in sortedList) {
                        if (height == -1) {
                            val options: BitmapFactory.Options = BitmapFactory.Options()
                            options.inJustDecodeBounds = true
                            BitmapFactory.decodeFile(file.absolutePath, options)
                            val imageHeight: Int = options.outHeight
                            val imageWidth: Int = options.outWidth
                            val scaleRatio = imageWidth / 480
                            height = imageHeight / scaleRatio
                            if (height % 2 != 0)
                                height++
                        }
                        if (file.path.endsWith(".jpg"))//excluding the text file itself
                            imageList.appendText("file '${file.path}'\n")
                    }
                    Log.d(LOG_TAG_TEST, "scale=$VIDEO_WIDTH:$height")
                    /*  val commands = arrayOf(
                  "-f concat -safe 0 -r 5 -i ${imageList.path} -r 5 -b:v 1M -preset ultrafast -vcodec libx265",
                  "-f concat -safe 0 -r 5 -i ${imageList.path} -r 5 -b:v 800K -preset ultrafast -vcodec libx265",
                  "-f concat -safe 0 -r 5 -i ${imageList.path} -r 5 -b:v 600k -preset ultrafast -vcodec libx265",
                  "-f concat -safe 0 -r 5 -i ${imageList.path} -r 5 -b:v 500k -preset ultrafast -vcodec libx265")*/
                    val commands = arrayOf(
                        "-f concat -safe 0 -r 6 -i ${imageList.path} -r 6 -b:v 1M -vf \"scale=$VIDEO_WIDTH:$height\"  -preset ultrafast -vcodec libx264",
//                        "-f concat -safe 0 -r 5 -i ${imageList.path} -r 5 -b:v 1M -preset veryfast -vcodec libx264",
//                        "-f concat -safe 0 -r 10 -i ${imageList.path} -r 10 -b:v 1M -preset ultrafast -vcodec libx264"
                    )
                  /*  for (i in commands.indices) {
                        val outFile = File(outputDir, "test-$output-$i-264.mp4")
                        outFile.delete()
                        val startTime = System.currentTimeMillis()

                        val session = FFmpegKit.execute("${commands[i]} ${outFile.path}")
                        Log.i(
                            LOG_TAG_TEST,
                            "session ${session.getAllLogs()}"
                        )
                        val timeTaken = System.currentTimeMillis() - startTime
                        Log.i(
                            LOG_TAG_TEST,
                            "test $i => $timeTaken ms"
                        )
                    }
                    output++*/

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

