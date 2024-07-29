package com.gamerboard.live.pool

import android.graphics.Bitmap

class BitmapPool (size : Int, factory: BitmapFactory) : Pool<Bitmap, BitmapPool.Param> (size, factory){
    override fun poll(obj: Bitmap?, parameter: Param): Bitmap? {
        return obj
    }

    override fun cleanEach(obj: Bitmap) {
        if(obj.isRecycled) return
        obj.recycle()
    }

    data class Param(
        val width: Int,
        val height: Int,
    )

}