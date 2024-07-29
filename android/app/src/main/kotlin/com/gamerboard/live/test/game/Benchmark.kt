package com.gamerboard.live.test.game

import android.util.Log

class Benchmark(val tag : String) {
    private var time : Long = 0L

    companion object{
        private val TAG = Benchmark::class.java.simpleName
    }
    init {
        time = System.currentTimeMillis()
    }

    fun finish(){
        val finishedAt = System.currentTimeMillis() - time
        Log.w(TAG, "$tag : finished in ${(finishedAt)}ms ${finishedAt/1000}s , ${finishedAt/60.times(1000)}m ")
    }
}