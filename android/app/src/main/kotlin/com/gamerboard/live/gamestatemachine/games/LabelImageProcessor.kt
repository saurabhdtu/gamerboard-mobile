package com.gamerboard.live.gamestatemachine.games

import android.graphics.Bitmap
import com.gamerboard.live.models.TFResult

interface LabelImageProcessor {
    val keyMultiplier : Int
    fun shouldRun(label : TFResult) : Boolean
    suspend fun newLabelImage(labelInfo : TFResult, labelImage : Bitmap) : Bitmap
    suspend fun newLabelImage(labelInfo : TFResult, labelImage : Bitmap, out : Bitmap)
}