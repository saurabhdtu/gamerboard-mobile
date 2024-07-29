package com.gamerboard.live.service.screencapture

import android.graphics.Bitmap
import android.util.Log
import com.gamerboard.live.gamestatemachine.stateMachine.test
import com.gamerboard.live.pool.BufferedBitmapPool
import com.gamerboard.logger.Logger
import com.gamerboard.logger.log
import org.koin.java.KoinJavaComponent
import java.io.File

open class Info {
    var name = "no-name.jpg"
}

interface Copyable<out T> where T : Copyable<T> {
    fun copyObj(): T
}

data class ImageBufferObject(
    val pool: BufferedBitmapPool? = null,
    val bitmap: Bitmap? = null,
    var path: String? = null,
    val fileName: Long = -1L
) : Info(), Copyable<ImageBufferObject> {
    override fun copyObj(): ImageBufferObject {
        return copy()
    }
}

class CapturedImageBuffer<T> where T : Info, T : Copyable<T> {
    private val _logger: Logger by KoinJavaComponent.inject(Logger::class.java)
    private val copiedArrays = arrayListOf<List<T>>()

    companion object {
        var MAX_BUFFER_SIZE = 10
        const val TAG_TEST_CAPTURE = "testImagesCapture"
    }

    var logger: Logger? = null

    init {
        if (!test())
            logger = _logger
    }

    private val circularBuffer: ArrayList<T> = arrayListOf()
    private val preserveBitmaps = linkedSetOf<Bitmap>()


    fun size() = circularBuffer.size
    fun clear() = circularBuffer.clear()

    fun addFrame(frame: T) {
        circularBuffer.add(frame)
        if (circularBuffer.size > MAX_BUFFER_SIZE) {
            val first = circularBuffer.first()
            if (first is ImageBufferObject) {
                first.bitmap?.let { first.pool?.putBack(it) }
                first.path?.let { File(it).delete() }
            }
            circularBuffer.removeAt(0)
        }
    }

    fun latestFrame(): T? {
        if (circularBuffer.isNotEmpty())
            return circularBuffer.last()
        return null
    }


    fun cleanUnusedFromPool(pool: BufferedBitmapPool) {
        try {
            synchronized(preserveBitmaps) {
                preserveBitmaps.clear()

                circularBuffer.forEach { item ->
                    if (item is ImageBufferObject && item.bitmap != null) {
                        preserveBitmaps.add(item.bitmap)
                    }
                }

                copiedArrays.forEach { tList ->
                    tList.forEach { item ->
                        if (item is ImageBufferObject && item.bitmap != null) {
                            preserveBitmaps.add(item.bitmap)
                        }
                    }
                }

                pool.elements().forEach {
                    if (preserveBitmaps.contains(it).not()) {
                        pool.putBack(it)
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }


    fun copyFramesAsArray(): ArrayList<T> {
        val framesArray: ArrayList<T> = arrayListOf()
        try {
            if (circularBuffer.size > 0)
                Log.d(
                    TAG_TEST_CAPTURE,
                    "copyFramesAsArray from Queue:[ ${circularBuffer.joinToString { "${it.name} " }} ] , size = ${circularBuffer.size}"
                )
            getCopiedBuffer()?.let { array ->
                framesArray.addAll(array.asIterable())
            }

            if (framesArray.isNotEmpty()) {
                copiedArrays.add(framesArray)
            }

            copiedArrays.removeIf { it.isEmpty() }
        } catch (e: Exception) {
            log(e.printStackTrace().toString())
        }
        return framesArray
    }

    private fun getCopiedBuffer(): ArrayList<T>? {
        try {
            return ArrayList<T>(MAX_BUFFER_SIZE).apply {
                circularBuffer.forEach { add(it.copyObj()) }
                circularBuffer.clear()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getAt(i: Int): T {
        return circularBuffer[i]
    }
}