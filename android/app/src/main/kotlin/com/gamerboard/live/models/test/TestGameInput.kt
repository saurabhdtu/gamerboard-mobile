package com.gamerboard.live.models.test

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable

data class TestGameInput(
    @SerializedName("username")
    val username : String,
    @SerializedName("id")
    val id : String
)
