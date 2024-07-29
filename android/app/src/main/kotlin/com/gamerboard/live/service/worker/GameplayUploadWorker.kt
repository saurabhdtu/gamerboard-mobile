package com.gamerboard.live.service.worker

//import com.gamerboard.live.utils.FFMpegUtil
import android.content.Context
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.utils.logException
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class GameplayUploadWorker(var ctx: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(ctx, workerParameters) {
    private var storageRef: StorageReference = Firebase.storage.reference
    private val uuid = (ctx as GamerboardApp).prefsHelper.getString(
        SharedPreferenceKeys.UUID
    )

    init {
        storageRef = storageRef.child("gameplays/android/$uuid")
    }

    override suspend fun doWork(): Result {
        if (!isStopped) {
            do {
                val file = getVideoFile()
                try {
                    if (file != null) {
                        val gameId = file.name.split(".").first()
//                        uploadGameplayNow(ctx, appDatabase, Long.parseLong(gameId), file)
                    }
                } catch (ex: Exception) {
                    logException(ex)
                }
            } while (file != null)
        }
        return Result.success()
    }


    private fun getVideoFile(): File? {
        val gameplayDir = ctx.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (gameplayDir?.exists() == true) {
            for (file in gameplayDir.listFiles()) {
                if (file.name.startsWith("temp").not()) {
                    return file
                }
            }
        }
        return null
    }
}