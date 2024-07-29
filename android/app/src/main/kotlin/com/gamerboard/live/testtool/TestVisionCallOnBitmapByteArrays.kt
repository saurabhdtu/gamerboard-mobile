package com.gamerboard.live.testtool

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.service.screencapture.ImageProcessor
import com.gamerboard.live.service.screencapture.MLKitOCR
import java.io.File
import java.io.FileOutputStream

/**
 * Created by saurabh.lahoti on 12/06/22
 */
class TestVisionCallOnBitmapByteArrays(private val ctx: Context) {
    private val mlKitOCR = MLKitOCR()
    private val testFolder = "testImages/visionTest3"
    private val dir = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    private lateinit var byteArrays: ArrayList<String>
    private lateinit var imageProcessor: ImageProcessor
    var positiveCounter = 0
    private val TestVisionCallOnBitmapByteArrays = "TestVisionCallOnBitmapByteArrays"
    suspend fun startTest() {

        MachineConstants.loadConstants(SupportedGames.FREEFIRE.packageName)
        debugMachine = DEBUGGER.DIRECT_HANDLE

        val testImagesFolder = ctx.assets.list(testFolder)
        testImagesFolder?.let{
            for (i in it.indices) {
                val stream =
                    ctx.assets.open("$testFolder/${it[i]}")
                val bitmap = BitmapFactory.decodeStream(stream)
                generateDifferentQualityFiles(bitmap)
                bitmap.recycle()
            }
        }
        Log.d(TestVisionCallOnBitmapByteArrays, "test started")
    }

    private fun generateDifferentQualityFiles(bitmap: Bitmap) {
        val timeStamp = System.currentTimeMillis()
//        val file100 = File(dir, "$timeStamp-100.jpg")
        val file40 = File(dir, "$timeStamp-40.jpg")
//        val fos100 = FileOutputStream(file100)
        val fos40 = FileOutputStream(file40)
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos100)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, fos40)
//        fos100.close()
        fos40.close()
    }

}