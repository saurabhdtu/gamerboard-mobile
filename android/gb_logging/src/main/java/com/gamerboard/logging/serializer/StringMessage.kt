package com.gamerboard.logging.serializer

class StringMessage(val message : String)  : LogMessage{
    override fun serialize(): String {
        return message
    }

}