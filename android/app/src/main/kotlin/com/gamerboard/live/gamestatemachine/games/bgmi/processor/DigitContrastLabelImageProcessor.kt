package com.gamerboard.live.gamestatemachine.games.bgmi.processor

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.gamestatemachine.games.LabelImageProcessor
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIConstants
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.service.screencapture.MLKitOCR
import com.gamerboard.logger.gson
import com.google.common.reflect.TypeToken
import com.google.gson.JsonObject
import org.koin.java.KoinJavaComponent.inject
import java.lang.reflect.Type
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * `BGMIKillLabelImageProcessor` is responsible for creating an image that can be accurately processed by optical character recognition (OCR). This functionality is introduced to address an issue where the text "kill = 1" is misread as "17" or "11" due to the background of the label image. The number "1" is often not recognized properly due to its proportion relative to the background.
 *
 * To solve these problems, this class performs the following operations:
 *
 * 1. Detection of Character Bounding Boxes: It detects the bounding boxes of each character and groups them based on each line by using the bottom of each rectangle.
 *
 * 2. Line Drawing: It draws each line using the source bounding boxes as the source rectangle and enlarges the destination rectangle slightly for each line. This operation is performed on a canvas with the same width and height as the source label image.
 *
 * 3. Threshold Color Matrix Application: On each text line, a color matrix called `thresholdControlMatrix` is applied. This matrix operation reduces the gray values while increasing the white values. However, this cannot be applied directly to the source image.
 *
 * 4. Image Masking: The image generated in the previous step is used to mask the current image, effectively applying the desired thresholding effect to the original image.
 */
open class DigitContrastLabelImageProcessor(override val keyMultiplier: Int) : LabelImageProcessor {

    protected val prefsHelper: PrefsHelper by inject(PrefsHelper::class.java)

    private val rectListType: Type = object : TypeToken<List<Rect?>?>() {}.type


    /**
     * Contrast matrix
     */
    private var contrastColorMatrix = ColorMatrix(
        floatArrayOf(
            //0   1   2    3  4
            1.5f, 0f, 0f, 0f, 0f,
            //5   6   7    8  9
            0f, 1.5f, 0f, 0f, 0f,
            //10 11  12   13  14
            0f, 0f, 1.5f, 0f, 0f,
            //15 16 17  18  19
            0f, 0f, 0f, 1f, 0f
        )
    )

    private val thresholdPaint = Paint().apply {

    }

    private val multiplyPaint = Paint().apply {
        val mode: PorterDuff.Mode = PorterDuff.Mode.MULTIPLY
        colorFilter = ColorMatrixColorFilter(contrastColorMatrix)
        xfermode = PorterDuffXfermode(mode)
    }


    private var contrastValue = 0.0032f * 255f

    /**
     * Color threshold color matrix
     */
    private val thresholdControlMatrix = floatArrayOf(
        contrastValue, contrastValue, contrastValue, 0f, -255f,
        contrastValue, contrastValue, contrastValue, 0f, -255f,
        contrastValue, contrastValue, contrastValue, 0f, -255f,
        0f, 0f, 0f, 1f, 0f
    )

    private val currentThreshold: Int = 150
    private var lastColorThresholdColorMatrix: ColorMatrix? = null

    companion object {
        private val TAG = DigitContrastLabelImageProcessor::class.java.simpleName
    }


    override fun shouldRun(label: TFResult): Boolean {
        return label.label == BGMIConstants.GameLabels.PROFILE_ID.ordinal
    }

    private fun createThresholdMatrix(threshold: Int): ColorMatrix {
        if (lastColorThresholdColorMatrix == null || threshold != currentThreshold) {
            val endThreshold = -255f * threshold
            thresholdControlMatrix[4] = endThreshold
            thresholdControlMatrix[9] = endThreshold
            thresholdControlMatrix[14] = endThreshold

            lastColorThresholdColorMatrix = ColorMatrix(thresholdControlMatrix)
        }
        return lastColorThresholdColorMatrix!!
    }


    /**
     * Create new processed image for kill label image
     * @param labelInfo tensorflow result
     * @param labelImage Cropped label image from actual image
     * @param out Result image is drawn to this bitmap
     * @throws InvalidLabelProcessorException
     */
    override suspend fun newLabelImage(labelInfo: TFResult, labelImage: Bitmap, out: Bitmap) {
        if (shouldRun(labelInfo).not()) {
            throw InvalidLabelProcessorException("$this is invalid processor for ${labelInfo.label}")
        }

        val boundingBoxes = arrayListOf(Rect(0, 0, labelImage.width, labelImage.height))

        val canvas = Canvas(out)

        canvas.drawColor(Color.BLACK)
        if (labelImage.height > 0 && labelImage.width > 0) {
            drawImageFromBoundingBoxes(boundingBoxes, labelImage, canvas)
        }
    }


    override suspend fun newLabelImage(
        labelInfo: TFResult,
        labelImage: Bitmap,
    ): Bitmap {
        if (shouldRun(label = labelInfo).not()) return labelImage

        if (shouldRun(labelInfo).not())
            return labelImage

        val bitmap = Bitmap.createBitmap(
            labelImage.width + 50,
            labelImage.height,
            Bitmap.Config.ARGB_8888
        )

        newLabelImage(labelInfo, labelImage, bitmap)

        return bitmap
    }


    /**
     * Go through each bounding box and draw on given out bitmap. It draw each row with a slight scaling
     * which is distinguished properly as a line by the OCR
     */
    private fun drawImageFromBoundingBoxes(
        boundingBoxes: List<Rect>,
        labelBitmap: Bitmap,
        canvas: Canvas
    ) {
        //Prepare threshold matrix
        thresholdPaint.apply {
            colorFilter =
                ColorMatrixColorFilter(createThresholdMatrix(1))
        }

        if (boundingBoxes.size == 1) {
            //If it couldn't detect atleast two bounding boxes
            val drawRect = Rect(0, 0, boundingBoxes.first().right, boundingBoxes.first().bottom)
            canvas.drawBitmap(labelBitmap, null, drawRect, thresholdPaint)
            canvas.drawBitmap(labelBitmap, null, drawRect, multiplyPaint)
            return
        }
    }



}