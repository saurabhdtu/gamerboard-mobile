package com.gamerboard.live.gamestatemachine.bgmi.screencapture

import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.service.screencapture.CapturedImageBuffer
import com.gamerboard.live.service.screencapture.Copyable
import com.gamerboard.live.service.screencapture.Info
import com.google.common.truth.Truth
import org.junit.Before
import org.junit.Test

data class TestObj(val int: Int) : Info(), Copyable<TestObj> {
    override fun copyObj(): TestObj {
        return copy()
    }
}

class CapturedImageBufferTest {

    private var capturedImageBuffer: CapturedImageBuffer<TestObj> = CapturedImageBuffer()
    private val maxSize = CapturedImageBuffer.MAX_BUFFER_SIZE


    @Before
    fun setUp() {
        debugMachine = DEBUGGER.DIRECT_HANDLE
        capturedImageBuffer.clear()
    }

    @Test
    fun size() {
        capturedImageBuffer.addFrame(TestObj(1))
        Truth.assertThat(capturedImageBuffer.size() == 1).isTrue()

        capturedImageBuffer.addFrame(TestObj(2))
        Truth.assertThat(capturedImageBuffer.size() == 2).isTrue()

        for (i in 3..2 * maxSize)
            capturedImageBuffer.addFrame(TestObj(i))
        Truth.assertThat(capturedImageBuffer.size()).isEqualTo(maxSize)
        LabelUtils.testLogGreen("Test Passed!")
    }

    @Test
    fun clear() {
        capturedImageBuffer.addFrame(TestObj(1))
        capturedImageBuffer.addFrame(TestObj(2))
        capturedImageBuffer.addFrame(TestObj(3))
        val frames = capturedImageBuffer.copyFramesAsArray()
        Truth.assertThat(capturedImageBuffer.size()).isEqualTo(0)

        capturedImageBuffer.clear()

        Truth.assertThat(frames).hasSize(3)
        Truth.assertThat(frames).isEqualTo(arrayListOf(TestObj(1), TestObj(2), TestObj(3)))

        capturedImageBuffer.addFrame(TestObj(1))
        capturedImageBuffer.addFrame(TestObj(2))
        capturedImageBuffer.addFrame(TestObj(3))

        Truth.assertThat(capturedImageBuffer.size()).isEqualTo(3)
        LabelUtils.testLogGreen("Test Passed!")
    }

    @Test
    fun clearCopiedBuffer() {
        capturedImageBuffer.addFrame(TestObj(1))
        capturedImageBuffer.addFrame(TestObj(2))
        capturedImageBuffer.addFrame(TestObj(3))

        capturedImageBuffer.copyFramesAsArray()

        capturedImageBuffer.addFrame(TestObj(4))
        capturedImageBuffer.addFrame(TestObj(5))

        Truth.assertThat(capturedImageBuffer.size()).isEqualTo(2)
        LabelUtils.testLogGreen("Test Passed!")
    }

    @Test
    fun addFrame() {
        capturedImageBuffer.addFrame(TestObj(1))
        capturedImageBuffer.addFrame(TestObj(2))
        LabelUtils.testLogGreen("Test Passed!")
    }

    @Test
    fun copyFramesAsArray() {
        capturedImageBuffer.addFrame(TestObj(1))
        capturedImageBuffer.addFrame(TestObj(2))
        capturedImageBuffer.addFrame(TestObj(3))
        capturedImageBuffer.addFrame(TestObj(4))
        capturedImageBuffer.addFrame(TestObj(5))

        val frames = capturedImageBuffer.copyFramesAsArray()
        Truth.assertThat(frames.size).isEqualTo(5)

        Truth.assertThat(capturedImageBuffer.size()).isEqualTo(0)
        capturedImageBuffer.addFrame(TestObj(6))
        capturedImageBuffer.addFrame(TestObj(7))
        capturedImageBuffer.addFrame(TestObj(8))
        capturedImageBuffer.addFrame(TestObj(9))

        Truth.assertThat(capturedImageBuffer.size()).isEqualTo(4)
    }

    @Test
    fun copyObj() {
        capturedImageBuffer.addFrame(TestObj(1))
        capturedImageBuffer.addFrame(TestObj(2))
        capturedImageBuffer.addFrame(TestObj(3))

        for (i in 4..20)
            capturedImageBuffer.addFrame(TestObj(i))
        capturedImageBuffer.clear()
    }

    @Test
    fun testCopyFramesAsArray() {
        val input1 = arrayListOf(TestObj(1), TestObj(2), TestObj(3))
        input1.forEach { capturedImageBuffer.addFrame(it) }
        Truth.assertThat(input1[0]).isSameInstanceAs(capturedImageBuffer.getAt(0))
        val copyImage1 = capturedImageBuffer.copyFramesAsArray()
        Truth.assertThat(input1[0]).isNotSameInstanceAs(copyImage1[0])
    }

}