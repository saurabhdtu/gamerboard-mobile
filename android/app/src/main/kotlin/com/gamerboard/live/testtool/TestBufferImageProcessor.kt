package com.gamerboard.live.testtool

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.gamerboard.live.gamestatemachine.games.LabelUtils.digitsOnly
import com.gamerboard.live.service.screencapture.BufferFPS
import com.gamerboard.live.service.screencapture.CapturedImageBuffer
import com.gamerboard.live.service.screencapture.ImageBufferObject
import com.gamerboard.live.service.screencapture.ImageProcessor
import com.gamerboard.logging.LoggingAgent
import java.nio.ByteBuffer

class TestBufferImageProcessor(val ctx: Context, private val imageProcessor: ImageProcessor?, private val capturedImageBuffer: CapturedImageBuffer<ImageBufferObject>, val logger: LoggingAgent) {
    private var processedCount = 0
    private var imageIdx = 0

    init {
        BufferFPS.CPS = 2.0f
        processedCount = 0
        imageIdx = 0
    }

    fun acquireLatestImage(bucket:String="fullMatch"): ImageBufferObject?{
        Log.d(TAG, "Get latest Image from bucket $bucket for testing!")
        val files = getBucket(bucket)
        if(imageIdx>=files.size)
            return null

        val stream = ctx.assets.open("testImages/$bucket/${files[imageIdx]}")
        val bitmap = BitmapFactory.decodeStream(stream)
        stream.close()
        val imageBufferObject = ImageBufferObject(
            bitmap = bitmap,
            fileName = files[imageIdx].digitsOnly().toLong()
        )
        imageBufferObject.name = files[imageIdx]
        imageIdx++

        return imageBufferObject
    }

    private fun getBucket(bucketPath: String): ArrayList<String> {
        val images = ctx.assets.list("testImages/$bucketPath")!!
        images.sortBy {
            it.substring(0, it.indexOf('.')).toInt()
        }
        val imagesArrayList = arrayListOf<String>()
        imagesArrayList.addAll(images)
        if (imagesArrayList.isEmpty())
            throw Throwable("Images not found for bucket!")
        return imagesArrayList
    }

    fun resetImageIndex(){
        imageIdx = 0
    }

    companion object {
        private const val TAG = "BufferImageProcessor"
    }
}