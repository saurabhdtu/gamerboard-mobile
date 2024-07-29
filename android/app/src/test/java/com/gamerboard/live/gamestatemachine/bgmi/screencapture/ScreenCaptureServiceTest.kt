package com.gamerboard.live.gamestatemachine.bgmi.screencapture

import com.gamerboard.live.service.screencapture.ImageBufferObject
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.nio.ByteBuffer

class ScreenCaptureServiceTest{

    @Test
    fun testCreateTwoCopiesOfByteBuffer(){
       val bufOrig = byteArrayOf(1, 2, 3, 4)
       val (buf1, buf2) = getTwoBufferCopies(ByteBuffer.wrap(bufOrig),  0, 0)
       assertThat(buf1===buf2).isFalse()

       assertThat(buf1===buf1).isTrue()
       assertThat(buf2===buf2).isTrue()
    }

    private fun getTwoBufferCopies(_byteBuffer: ByteBuffer, pixelStride:Int, rowStride:Int):Pair<ImageBufferObject, ImageBufferObject>{
        val fileID = System.currentTimeMillis()
        val byteBuffer1 = ByteBuffer.allocate(_byteBuffer.capacity())
        _byteBuffer.rewind()
        byteBuffer1.put(_byteBuffer)
        _byteBuffer.rewind()
        byteBuffer1.flip()

        val imageBufferObject1 = ImageBufferObject(
            byteBuffer1,
            pixelStride,
            rowStride,
            fileName = fileID
        ).also { it.name = "${fileID}.jpg" }


        val byteBuffer2 = ByteBuffer.allocate(_byteBuffer.capacity())
        _byteBuffer.rewind()
        byteBuffer2.put(_byteBuffer)
        _byteBuffer.rewind()
        byteBuffer2.flip()

        val imageBufferObject2 = ImageBufferObject(
            byteBuffer2,
            pixelStride,
            rowStride,
            fileName = fileID
        ).also { it.name = "${fileID}.jpg" }

        return Pair(imageBufferObject1, imageBufferObject2)
    }
}