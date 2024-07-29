package com.gamerboard.live.service.worker

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.gamestatemachine.games.GameHelper
import com.gamerboard.live.utils.logException
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.logWithIdentifier
import com.gamerboard.logger.model.OcrInfoMessage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DataSyncWorker(var ctx: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(ctx, workerParameters) {
    private var storageRef: StorageReference = Firebase.storage.reference
    val directory = File(
        "${ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/images",
    )

    init {
        storageRef = storageRef.child(
            "sessionsV2/android/${workerParameters.inputData.getString("game")}/${BuildConfig.VERSION_NAME}/${
                (ctx as GamerboardApp).prefsHelper.getString(
                    SharedPreferenceKeys.UUID
                )
            }/"
        )
    }


    override suspend fun doWork(): Result {
        if (!isStopped) {
            syncZipFile()
        }
        return Result.success()
    }

    private suspend fun syncZipFile() {
        directory.listFiles().let { list ->
            list?.forEach {
                val zip = createZipFile(it)
                if (zip != null) {
                    val file = Uri.fromFile(zip)
                    if (!isStopped) {
                        val zipRef =
                            storageRef.child("${file.lastPathSegment}")
                        try {
                            zipRef.putFile(file).await()
                        } catch (ex: Exception) {
                            logException(ex)
                        } finally {
                            val downloadUrl = zipRef.downloadUrl.await()
                            logWithIdentifier(GameHelper.getOriginalGameId()) { builder ->
                                builder.setMessage("Uploaded images to firebase")
                                builder.setOcrInfo(
                                    OcrInfoMessage(
                                        imagesPath = downloadUrl.toString()
                                    )
                                )
                                builder.setCategory(LogCategory.SC)
                            }
                        }
                        deleteZipFile()
                    }
                    zip.delete()
                }
            }
        }
    }


    private fun createZipFile(gameImages: File): File? {
        val BUFFER_SIZE = 6 * 1024
        var zipFile: File? = null
        if (gameImages.exists()) {
            zipFile = File(
                    ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "images/${Uri.fromFile(gameImages).lastPathSegment}.zip"
            )
            val out = ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFile)))
            try {
                val data = ByteArray(BUFFER_SIZE)
                gameImages.listFiles()?.forEach { file ->
                    val fi = FileInputStream(file)
                    val origin = BufferedInputStream(fi, BUFFER_SIZE)
                    try {
                        val entry =
                            ZipEntry(file.path.substring(file.path.lastIndexOf("/") + 1))
                        out.putNextEntry(entry)
                        var count: Int
                        while (origin.read(data, 0, BUFFER_SIZE).also { count = it } != -1) {
                            out.write(data, 0, count)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    } finally {
                        origin.close()
                    }
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                out.close()
            }
        }
        return zipFile
    }

    private fun deleteZipFile() {
        File(
            "${ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}/images",
        ).deleteRecursively()
    }

}