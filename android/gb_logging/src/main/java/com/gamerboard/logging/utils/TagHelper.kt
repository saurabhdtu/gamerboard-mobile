package com.gamerboard.logging.utils

object TagHelper {
     fun getTag() : String{
        return Throwable().stackTrace[2]?.let { "${it.className}::${it.methodName}.${it.lineNumber}" } ?: ""
     }
}