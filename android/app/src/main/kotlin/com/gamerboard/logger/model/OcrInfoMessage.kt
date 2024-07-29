package com.gamerboard.logger.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class OcrInfoMessage (
    @SerialName("v")
    val vision : Vision? = null,
    @SerialName("l")
    val local : Local? = null,
    @SerialName("i")
    val imagesPath : String? = null,
){
    @Serializable
    data class Vision(
        @SerialName("s")
        val success : Boolean = false,
        @SerialName("e")
        val failureReason : String? = null,
        @SerialName("i")
        val visionImage : String? = null,
        @SerialName("o")
        val ocrTextList : List<Ocr> = emptyList()
    )
    @Serializable
    data class Local(
        @SerialName("o")
        val ocrTextList : List<Ocr> = emptyList()
    )


    @Serializable
    data class Ocr(
        @SerialName("ot")
        val ocrText : String,
        @SerialName("l")
        val label : Int
    )
}