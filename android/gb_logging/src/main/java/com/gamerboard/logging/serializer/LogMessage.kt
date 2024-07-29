package com.gamerboard.logging.serializer

import com.gamerboard.logging.utils.TagHelper
import kotlinx.serialization.Serializable

interface LogMessage {
    fun serialize() : String
}