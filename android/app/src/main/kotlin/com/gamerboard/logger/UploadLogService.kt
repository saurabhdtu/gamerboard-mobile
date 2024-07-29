package com.gamerboard.logger

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.gamerboard.live.utils.FeatureFlag
import com.gamerboard.live.utils.FeatureHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class UploadLogService(context: Context, val workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {
    private val logProcessor: LogProcessor by inject()
    override suspend fun doWork(): Result {
        val interval = workerParameters.inputData.getLong(ARG_INTERVAL, MIN_INTERVAL)
        _isActive = true
       /* while (true) {
            logPublisher.process()
            delay(interval)
        }*/
        return Result.success()
    }

    companion object {
        private val workerId: String = "upload_log_worker"
        private const val ARG_INTERVAL = "interval"
        private const val MIN_INTERVAL = 200L
        private var _isActive = false
        val isActive = _isActive
        private val TAG = UploadLogService::class.java.simpleName
        fun start(context: Context, interval: Long = MIN_INTERVAL) {
            return
            if (FeatureHelper.getLoggingFlag(context) == FeatureFlag.OLD) {
                return
            }
            if (!_isActive) {
                val data = Data.Builder()
                data.putLong("interval", interval)
                val oneTimeWorkerBuilder = OneTimeWorkRequestBuilder<UploadLogService>()
                    .setInputData(data.build())
                WorkManager.getInstance(context).enqueueUniqueWork(
                    workerId, ExistingWorkPolicy.REPLACE, oneTimeWorkerBuilder.build()
                )
            }
            _isActive = true
        }

        fun stop(context: Context) {
            if (FeatureHelper.getLoggingFlag(context) == FeatureFlag.OLD) {
                return
            }
            _isActive = false
            try {
                WorkManager.getInstance(context).cancelAllWorkByTag(workerId)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }


}