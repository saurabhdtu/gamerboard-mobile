package com.gamerboard.logging.utils

import java.util.UUID

object UuidHelper {

    fun identifier() = UUID.randomUUID().toString()
}