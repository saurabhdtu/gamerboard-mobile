package com.gamerboard.live.test.helper

import android.media.MediaMetadataRetriever
import java.util.Locale
import java.util.concurrent.TimeUnit

class MediaRetrieverHelper {

}

object MediaDurationHelper {
    fun getMediaDuration(filePath: String): String {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(filePath)
            val durationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val duration = durationString?.toLong()
            formatDuration(duration ?: 0)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            retriever.release()
        }
    }

    private fun formatDuration(duration: Long): String {
        val seconds = TimeUnit.MILLISECONDS.toSeconds(duration) % 60
        val minutes = TimeUnit.MILLISECONDS.toMinutes(duration) % 60
        val hours = TimeUnit.MILLISECONDS.toHours(duration)

        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    }
}