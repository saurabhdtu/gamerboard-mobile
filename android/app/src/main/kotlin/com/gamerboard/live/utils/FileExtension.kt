package com.gamerboard.live.utils

import java.io.File

fun File.createIfNotExists(){
    if(!this.exists()){
        this.parentFile?.mkdirs()
        this.createNewFile()
    }
}

fun File.removeAndCreate(){
   if(this.exists()){
       this.delete()
   }
    this.createIfNotExists()
}