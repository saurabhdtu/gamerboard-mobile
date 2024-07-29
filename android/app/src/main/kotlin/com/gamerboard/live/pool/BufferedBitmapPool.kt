package com.gamerboard.live.pool

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import java.nio.ByteBuffer

class BufferedBitmapPool(size: Int, factory: BitmapFactory) :
    Pool<Bitmap, BufferedBitmapPool.Param>(size, factory) {

    private val cm = ColorMatrix().apply { this.setSaturation(0f) }
    private val paint = Paint().apply { this.colorFilter = ColorMatrixColorFilter(cm) }


    override fun poll(obj: Bitmap?, parameter: Param): Bitmap? {
        if (obj != null) {
            obj.reconfigure(parameter.width, parameter.height, Bitmap.Config.ARGB_8888)
            obj.copyPixelsFromBuffer(parameter.byteBuffer)

            val canvas = Canvas(obj)
            canvas.drawBitmap(obj, 0f, 0f, paint)
            return obj
        }
        return null
    }

    override fun cleanEach(obj: Bitmap) {
        if (obj.isRecycled) return
        obj.recycle()
    }

    data class Param(
        val byteBuffer: ByteBuffer,
        val width: Int,
        val height: Int,
    )

}