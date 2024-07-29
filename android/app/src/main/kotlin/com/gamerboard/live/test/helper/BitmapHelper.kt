package com.gamerboard.live.test.helper

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.DisplayMetrics
import java.nio.ByteBuffer

object BitmapHelper {
    fun fromBufferToBitmap(buffer: ByteBuffer, width: Int, height: Int): Bitmap? {
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        buffer.rewind()
        result.copyPixelsFromBuffer(buffer)
        val transformMatrix = Matrix()
        val outputBitmap =
                Bitmap.createBitmap(result, 0, 0, result.width, result.height, transformMatrix, false)
        outputBitmap.density = DisplayMetrics.DENSITY_DEFAULT
        return outputBitmap
    }
}