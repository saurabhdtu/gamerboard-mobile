package com.gamerboard.live.utils

import java.lang.Exception

inline fun ignoreException( call : () -> Unit){
    try{
        call()
    }catch (ex : Exception){
        ex.printStackTrace()
    }
}