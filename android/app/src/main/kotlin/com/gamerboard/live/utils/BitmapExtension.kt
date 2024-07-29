package com.gamerboard.live.utils

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import java.nio.ByteBuffer


/**
 * Extension function to create gray scale image from bitmap
 * @return Gray Scale Image
 */
fun Bitmap.asGrayScale() : Bitmap{
    val gray = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )
    val c: Canvas? = Canvas(gray)
    val paint = Paint()
    val cm: ColorMatrix? = ColorMatrix()
    cm?.setSaturation(0f)
    val f: ColorMatrixColorFilter? = cm?.let { ColorMatrixColorFilter(it) }
    paint.colorFilter = f
    c?.drawBitmap(this, 0f, 0f, paint)
    recycle()
    return gray
}

/**
 * Test image correction use only
 */
fun Bitmap.landscaped() : Bitmap{
    if(height > width){
        val matrix = Matrix()
        matrix.postRotate(-90f)
        return Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, true)
    }
    return this
}
fun Bitmap.asBuffer(): ByteBuffer? {
    val byteBuffer = ByteBuffer.allocate(byteCount)
    return asByteBuffer(byteBuffer)
}

fun Bitmap.cropBitmap(rect:Rect): Bitmap? {
    return Bitmap.createBitmap(this, rect.left, rect.top, rect.width(), rect.height())
}

fun Bitmap.asByteBuffer(byteBuffer: ByteBuffer): ByteBuffer {
    byteBuffer.rewind()
    copyPixelsToBuffer(byteBuffer)
    byteBuffer.rewind()
    this.recycle()
    return byteBuffer
}