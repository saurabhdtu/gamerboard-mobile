package com.gamerboard.live.service.screencapture

import android.graphics.Bitmap
import com.gamerboard.framedecoder.decoder.Frame
import java.nio.ByteBuffer

abstract class ScreenRecorder {
    protected var callback : FrameCaptureCallback? = null

    var currentState : State = State.Idle
    abstract fun starRecording()
    abstract fun stopRecording()
    fun setOnCaptureFrameCallback(callback: FrameCaptureCallback){
        this.callback = callback
    }

    interface FrameCaptureCallback  {
        fun onCapture(frame : Frame)
        fun onComplete()
    }


    sealed class State{
        object Started : State()
        object Paused : State()
        object Completed : State()
        object Stopped : State()
        object Idle : State()
        data class Recording(val frame : Bitmap) : State()

    }
}