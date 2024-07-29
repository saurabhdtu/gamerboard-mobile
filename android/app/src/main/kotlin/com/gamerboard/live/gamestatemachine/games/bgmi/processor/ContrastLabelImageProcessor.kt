package com.gamerboard.live.gamestatemachine.games.bgmi.processor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import com.gamerboard.live.gamestatemachine.games.LabelImageProcessor
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIConstants
import com.gamerboard.live.models.TFResult

/**
 * `ContrastLabelImageProcessor` is responsible for applying contrast values for a given image
 */
class ContrastLabelImageProcessor(override val keyMultiplier: Int) : LabelImageProcessor {

    private var cm = ColorMatrix(
        floatArrayOf(
            1f, 0f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f, 0f,
            0f, 0f, 1f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        )
    )
    private val contrastPaint = Paint().apply {
        colorFilter = ColorMatrixColorFilter(cm)
    }
    override fun shouldRun(label: TFResult): Boolean {
        return label.label == BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal
    }

    override suspend fun newLabelImage(
        labelInfo: TFResult,
        labelImage: Bitmap,
    ): Bitmap {
        if(shouldRun(labelInfo).not()) return labelImage

        val canvas = Canvas(labelImage)
        canvas.drawBitmap(labelImage, 0f, 0f, contrastPaint)
        return labelImage
    }

    override suspend fun newLabelImage(labelInfo: TFResult, labelImage: Bitmap, out: Bitmap) {
        if(shouldRun(labelInfo).not()){
            throw InvalidLabelProcessorException("$this is invalid processor for ${labelInfo.label}. Use newLabelImage(labelInfo, labelBitmap) : Bitmap function")
        }
    }
}