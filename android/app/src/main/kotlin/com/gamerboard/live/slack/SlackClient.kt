package com.gamerboard.live.slack

import com.gamerboard.live.BuildConfig
import com.gamerboard.live.slack.data.SlackRequestBody
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class SlackClient {
    private val url = BuildConfig.SLACK_INCOMING_HOOK
    private val okHttpClient = OkHttpClient()

    companion object {
        private val mediaType = "application/json; charset=utf-8".toMediaType()
    }


    fun post(slackRequestBody: SlackRequestBody): String? {
        val requestBody = Json.encodeToString(slackRequestBody).toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()
        try {
            val response = okHttpClient.newCall(request).execute()
            return response.body?.string()
        } catch (ex: Exception) {

        }
        return null
    }


}