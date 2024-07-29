package com.gamerboard.live.test.recorder

import android.graphics.Bitmap
import com.gamerboard.live.models.test.TestDataModel
import com.gamerboard.live.service.screencapture.ScreenRecorder

abstract class AbstractFakeScreenRecorder : ScreenRecorder(){
    protected var testDataModel : TestDataModel? = null
    protected var trackFrameCallback : TrackFrameCallback? = null

    fun setDataModel(testDataModel: TestDataModel){
        this.testDataModel = testDataModel
    }
    fun setOnTrackFrameCallback(callback: TrackFrameCallback) {
        trackFrameCallback = callback

    }


    interface TrackFrameCallback {
        fun onTrack(currentFrame : Int, totalFrames : Int )
    }
}