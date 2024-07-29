package com.gamerboard.live.utils

import android.content.Context
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.service.screencapture.FileAndDataSync
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.math.roundToInt

object ModelDownloadHelper {
    suspend fun downloadModelFile(context : Context, packageName: String) {
        val gameModel = (when (packageName) {
            SupportedGames.BGMI.packageName ->
                SupportedGames.BGMI

            SupportedGames.FREEFIRE.packageName ->
                SupportedGames.FREEFIRE

            else -> SupportedGames.BGMI
        }).modelURL
        val fileRef = FirebaseStorage.getInstance().getReferenceFromUrl(gameModel)
        if (!FileAndDataSync.getModelFile(context, fileRef.name).exists()) {
            val tempFile = File("${context.getExternalFilesDir(null)?.path}/temp_file.tmp")
            fileRef.getFile(tempFile).addOnProgressListener { task ->
                val percentage =
                    ((task.bytesTransferred.toFloat() / task.totalByteCount.toFloat()) * 100).roundToInt()
                println("Downloading Model ${percentage}%")
            }.await()
            tempFile.renameTo(FileAndDataSync.getModelFile(context, fileRef.name))
        }
    }
}