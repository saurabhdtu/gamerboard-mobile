package com.gamerboard.logger

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.SharedPreferenceKeys
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.File


@Deprecated("Use UploadLogService ")
class LogSyncWorker(var ctx: Context, workerParameters: WorkerParameters) : CoroutineWorker(
    ctx,
    workerParameters
) {

    private val deviceId: String = inputData.getString("deviceId") ?: Settings.Secure.getString(
        ctx.contentResolver,
        Settings.Secure.ANDROID_ID
    )

    private val logDirectory = File(
        "${ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}/logs"
    )

    override suspend fun doWork(): Result {
        val prefs = (ctx.applicationContext as GamerboardApp).prefsHelper
        if (logDirectory.exists()) {
            val files = logDirectory.listFiles() ?: return Result.success()
            for (uploadFile in files) {
                /*if(uploadFile.name == "memory-usage.txt")
                    continue*/
                if (uploadFile.exists() && uploadFile.name.contains("-")) {
                    try {
                        val fullName = uploadFile.name.replace(".txt", "")
                        val fName = fullName.split("|").first()
                        val dateTime = fullName.split("|").last()
                        val fileUri = Uri.fromFile(uploadFile)
                        val storageRef = Firebase.storage.reference
                        val nameArr = dateTime.split(" ")
                        val uploadRef =
                            storageRef.child("logs/${deviceId}/${nameArr[0]}/$fName-${nameArr[1]}.txt")
                        val taskResult = uploadRef.putFile(fileUri).await()
                        if (taskResult.error == null)
                            uploadFile.delete()
                        uploadRef.updateMetadata(
                            StorageMetadata.Builder().setCustomMetadata(
                                "userId",
                                prefs.getString(SharedPreferenceKeys.USER_ID)
                            ).build()
                        ).await()
                    } catch (e: Exception) {
                        if (e is IndexOutOfBoundsException) {
                            uploadFile.delete()
                            FirebaseCrashlytics.getInstance().log(e.message + "${uploadFile.name}")
                        } else {
                            FirebaseCrashlytics.getInstance().recordException(e)
                        }
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(ctx, "No Logs recorded!", Toast.LENGTH_SHORT).show()
            }
        }
        return Result.success()
    }
}