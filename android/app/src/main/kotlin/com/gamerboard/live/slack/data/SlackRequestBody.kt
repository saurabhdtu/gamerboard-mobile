package com.gamerboard.live.slack.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SlackRequestBody(
    @SerialName("attachments")
    val attachments: List<Attachment?>?,
    @SerialName("text")
    val text: String?
)