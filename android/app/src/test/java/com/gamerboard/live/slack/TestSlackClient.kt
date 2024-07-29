package com.gamerboard.live.slack

import com.gamerboard.live.slack.data.SlackRequestBody
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

class TestSlackClient {

    private val slackClient = SlackClient()

    @Test
    fun testTextMessageRequest() {
        val response = slackClient.post(SlackRequestBody(attachments = null, text = "Test Message"))
        Assert.assertEquals(response, "ok")
    }
}