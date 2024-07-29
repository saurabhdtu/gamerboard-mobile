package com.gamerboard.live.test.recorder

import com.gamerboard.framedecoder.decoder.Frame
import com.gamerboard.framedecoder.decoder.FrameExtractor
import com.gamerboard.framedecoder.decoder.IVideoFrameExtractor
import com.gamerboard.live.service.screencapture.BufferFPS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.LinkedList

class FakeScreenRecorder : AbstractFakeScreenRecorder(), IVideoFrameExtractor {
    private lateinit var frameExtractor: FrameExtractor
    private lateinit var job: Job
    private  val dispatcher = Dispatchers.IO + SupervisorJob()

    private var currentTrack = 0L
    private var duration = 0
    private var frames  = LinkedList<Frame>()

    companion object {
        private val TAG = FakeScreenRecorder::class.java.simpleName
    }

    override fun starRecording() {
        currentState = State.Started
        currentTrack = 0L
        duration = 0
        frameExtractor = FrameExtractor(this)
        testDataModel?.videoFile?.absolutePath?.let { filePath ->
            job = CoroutineScope(dispatcher).launch {
                try {
                    frameExtractor.extractFrames(filePath)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            }
           CoroutineScope(dispatcher + SupervisorJob()).launch {
                while(currentState == State.Started && !job.isCancelled){
                   try{
                       duration = frameExtractor.maxFrames
                       if(frames.isEmpty()){
                           continue
                       }
                       val frame = frames.pop()
                       val fpsFactor = (frameExtractor.fps / BufferFPS.MAX_FPS).toInt()
                       trackFrameCallback?.onTrack(currentFrame = frame.position.toInt() / fpsFactor, totalFrames = duration/fpsFactor)
                       callback?.onCapture(frame)
                       delay(200L)
                   }catch (ex : Exception){
                       ex.printStackTrace()
                   }
                }
               delay(5000L)
            }
        } ?: run {
            currentState = State.Completed
        }

    }

    override fun stopRecording() {
       try{
           callback?.onComplete()
           currentState = State.Stopped
           job.cancel()
       }catch (ex : Exception){
           ex.printStackTrace()
       }
    }


    override fun onCurrentFrameExtracted(currentFrame: Frame) {
        try{
            currentTrack = currentFrame.timestamp / 1000
            val fpsFactor = (frameExtractor.fps / BufferFPS.MAX_FPS).toInt()
            if (currentFrame.position % fpsFactor == 0) {
                frames.add(currentFrame)
            }
        }catch (ex : Exception){
            ex.printStackTrace()
        }
    }

    override fun onAllFrameExtracted(processedFrameCount: Int, processedTimeMs: Long) {
       try{
           currentState = State.Completed
           frameExtractor.terminate()
           callback?.onComplete()
       }catch (ex : Exception){
           ex.printStackTrace()
       }
    }


}

