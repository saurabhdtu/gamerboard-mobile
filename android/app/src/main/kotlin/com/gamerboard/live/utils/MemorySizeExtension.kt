package com.gamerboard.live.utils

fun Long.mb() : Long{
   return this.div(1024).div(1024)
}

fun Long.gb() : Long{
   return this.mb().div(1024)
}