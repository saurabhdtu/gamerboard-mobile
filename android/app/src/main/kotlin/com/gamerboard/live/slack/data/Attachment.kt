package com.gamerboard.live.slack.data


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    @SerialName("color")
    val color: String? = null,
    @SerialName("fallback")
    val fallback: String?,
    @SerialName("filename")
    val filename: String?,
    @SerialName("text")
    val text: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("type")
    val type: String?
)