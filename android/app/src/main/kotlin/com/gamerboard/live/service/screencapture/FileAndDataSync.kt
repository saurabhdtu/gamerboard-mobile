package com.gamerboard.live.service.screencapture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.models.db.AppDatabase
import com.gamerboard.live.service.worker.DataSyncWorker
import com.gamerboard.live.utils.logException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.dsl.module
import java.io.File
import java.io.FileOutputStream


/**
 * Created by saurabh.lahoti on 27/08/21
 */
val FileAndDataSyncModule = module {
    factory {
        FileAndDataSync(get())
    }
}

class FileAndDataSync(private val ctx: Context) : KoinComponent {
    val db: AppDatabase by inject()

    companion object {
        fun getModelFile(ctx: Context, modelName: String?) =
            File("${ctx.getExternalFilesDir(null)?.path}/${modelName}")

        fun startZipSync(ctx: Context) {
            val internetConstants =
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            val uploadZipRequest =
                OneTimeWorkRequestBuilder<DataSyncWorker>().setConstraints(internetConstants)
                    .setInputData(
                        Data.Builder().putString("game", MachineConstants.currentGame.packageName)
                            .build()
                    )
                    .build()
            WorkManager.getInstance(ctx).enqueueUniqueWork(
                "com.gamerboard.live-sync-zip",
                ExistingWorkPolicy.KEEP,
                uploadZipRequest
            )
        }
    }

    ///////// TODO: temporary code to get b/w post game result/rating image
    fun captureImageForUse(bmp: Bitmap, fileName: String, quality: Int = 70): File {
        val file = File(
            "${ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/ratings",
            fileName
        )

        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        try {
            FileOutputStream(file).use { out ->
                bmp.compress(
                    Bitmap.CompressFormat.JPEG,
                    quality,
                    out
                )
                out.close()// bmp is your Bitmap instance
            }
        } catch (e: Exception) {
            logException(e)
        }
        return file
    }

    fun captureImageForUpload(
        bmp: Bitmap,
        fileName: String,
        quality: Int = 70,
        gameId: String
    ): File {
        val file = File(
            "${ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/images/$gameId",
            fileName
        )

        if (!file.exists()) {
            file.parentFile.mkdirs()
            file.createNewFile()
        }

        try {
            FileOutputStream(file).use { out ->
                bmp.compress(
                    Bitmap.CompressFormat.JPEG,
                    quality,
                    out
                )
                out.close()// bmp is your Bitmap instance
            }
        } catch (e: Exception) {
            logException(e)
        }
        return file
    }

    fun saveBitmap(
        bitmap: Bitmap,
        name: String = "${System.currentTimeMillis()}.jpg",
        filePath: String? = null
    ) {
        try {
            val file: File?
            if (filePath != null) {
                filePath.let {
                    File(filePath).apply {
                        File(this.parent).let {
                            if (!it.exists()) it.mkdirs()
                        }
                        file = this
                    }
                }
            } else {
                File(
                    "${ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/saved-images"
                ).apply {
                    this.mkdirs()
                    file = File(this, name)
                }
            }
            file?.createNewFile()
            file?.let {
                val fos = FileOutputStream(it)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                fos.close()
            }

        } catch (e: Exception) {
            logException(e)
        }
    }

    fun getBitmapFromFile(filePath: String): Bitmap? {
        return try {
            val options =
                BitmapFactory.Options().apply { inPreferredConfig = Bitmap.Config.ARGB_8888 }
            val bitmap = BitmapFactory.decodeFile(filePath, options)
            bitmap
        } catch (e: Exception) {
            logException(e)
            null
        }
    }

    suspend fun logCurrentSessionGamesToFile(outputFile: String) {
        val gameDao = db.getGamesDao()
        val unSyncedGames = gameDao.getUnSyncedGames()

        if (unSyncedGames.isEmpty())
            return

        val file = File(outputFile).apply {
            if (!exists()) {
                parentFile.mkdirs()
                createNewFile()
            }
        }

        val json = Json { encodeDefaults = true }

        if (!file.exists())
            return

        val outputData = json.encodeToString(unSyncedGames)
        file.writeText(outputData)


        // mark synced
        for (game in unSyncedGames)
            gameDao.setSynced(game.id)
    }
}