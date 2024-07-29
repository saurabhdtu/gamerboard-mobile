package com.gamerboard.live.pool

import android.graphics.Bitmap

class BitmapFactory(private val width: Int, private val height: Int) : Pool.Factory<Bitmap> {

    override fun create(): Bitmap {
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    }
    override fun clean(data: Bitmap) {
        data.recycle()
    }
}