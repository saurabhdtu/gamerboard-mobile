package com.gamerboard.live.models

import androidx.annotation.Keep
import kotlinx.serialization.Serializable
import java.util.ArrayList


@Keep
@Serializable
data class ImageSavingConfig(

    val save: Boolean,
    val maxImages: Int,
    val timeGap: Int,
 val allowedLabels: ArrayList<String>
)