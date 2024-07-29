package com.gamerboard.live.gamestatemachine.bgmi.screencapture

import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.google.common.truth.Truth.assertThat
import org.apache.commons.collections4.queue.CircularFifoQueue
import org.junit.Before
import org.junit.Test

class CustomCircularBufferTest {

    var maxSize = 5
    var circularBuffer: CircularFifoQueue<Int>? = null

    @Before
    fun setup() {
        debugMachine = DEBUGGER.DIRECT_HANDLE
        circularBuffer = CircularFifoQueue(maxSize)
    }

    @Test
    fun testBufferSize() {
        circularBuffer?.also { circularBuffer ->
            circularBuffer.add(1)
            circularBuffer.add(2)
            circularBuffer.add(3)
            circularBuffer.add(4)

            assertThat(circularBuffer.size).isEqualTo(4)
            LabelUtils.testLogGreen("Queue: $circularBuffer , size = ${circularBuffer.size}")
        }
    }


    @Test
    fun testMaxBufferSize() {
        circularBuffer?.also { circularBuffer ->
            circularBuffer.add(1)
            circularBuffer.add(2)
            circularBuffer.add(3)
            circularBuffer.add(4)
            circularBuffer.add(5)
            circularBuffer.add(6)
            circularBuffer.add(7)
            circularBuffer.add(8)

            assertThat(circularBuffer.size).isEqualTo(maxSize)
            LabelUtils.testLogGreen("Queue: $circularBuffer , size = ${circularBuffer.size}")
        }
    }


    @Test
    fun getOldestFrame() {
        circularBuffer?.also { circularBuffer ->
            circularBuffer.add(1)
            circularBuffer.add(2)
            circularBuffer.add(3)
            circularBuffer.add(4)
            circularBuffer.add(5)
            circularBuffer.add(6)
            circularBuffer.add(7)
            circularBuffer.add(8)

            assertThat(circularBuffer.peek()).isEqualTo(4)
            LabelUtils.testLogGreen("Queue: $circularBuffer , size = ${circularBuffer.size}")
        }
    }

    @Test
    fun getAllFramesAsArray() {
        circularBuffer?.also { circularBuffer ->
            circularBuffer.add(1)
            circularBuffer.add(2)
            circularBuffer.add(3)
            circularBuffer.add(4)
            circularBuffer.add(5)
            circularBuffer.add(6)
            circularBuffer.add(7)
            circularBuffer.add(8)

            val framesArray: ArrayList<Int> = arrayListOf()
            LabelUtils.testLogGreen("Queue: $circularBuffer , size = ${circularBuffer.size}")
            while (circularBuffer.peek() != null) {
                framesArray.add(circularBuffer.poll()!!)
                LabelUtils.testLogGreen("Queue: $circularBuffer , size = ${circularBuffer.size}")
            }
            assertThat(framesArray).isEqualTo(arrayListOf(4, 5, 6, 7, 8))
        }
    }


}