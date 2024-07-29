package com.gamerboard.live.gamestatemachine.games.bgmi.processor

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.os.Environment
import android.util.Log
import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.gamestatemachine.games.LabelImageProcessor
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIConstants
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.service.screencapture.MLKitOCR
import com.gamerboard.logger.gson
import com.google.common.reflect.TypeToken
import com.google.gson.JsonObject
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import java.lang.reflect.Type
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

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
open class BGMIKillPerFrameLabelImageProcessor(override val keyMultiplier: Int) : LabelImageProcessor {

    protected val prefsHelper : PrefsHelper by inject(PrefsHelper::class.java)

    private val mlKitOCR: MLKitOCR by inject(MLKitOCR::class.java)
    protected val rectListType: Type = object : TypeToken<List<Rect?>?>() {}.type


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
        private val TAG = BGMIKillPerFrameLabelImageProcessor::class.java.simpleName
        private val pixelValues  = arrayListOf<String>()


        fun writePixelValuesToFile(context : Context){
            val file  = File(context.getExternalFilesDir(Environment.DIRECTORY_ALARMS), "pixel_data.txt").apply {
                if(exists()){
                    delete()
                }
                createNewFile()
            }
            file.appendText(pixelValues.joinToString("\n\n"))
        }
    }


    override fun shouldRun(label: TFResult): Boolean {
        return label.label == BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal
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

        val boundingBoxes = calculateBoundingBoxes(labelInfo, labelImage)


        val canvas = Canvas(out)

        if (boundingBoxes.isEmpty()) {
            throw InvalidLabelProcessorException("Couldn't found text in the given image")
        }

        canvas.drawColor(Color.BLACK)
        if (labelImage.height > 0 && labelImage.width > 0) {
            drawImageFromBoundingBoxes(boundingBoxes, labelImage, out, canvas)
        }
    }

    open suspend fun calculateBoundingBoxes(
        labelInfo: TFResult,
        labelImage: Bitmap
    ): List<Rect> {
        val cachedBoundingBoxes: List<Rect> = getCachedBoundingBox(labelInfo)

        val boundingBoxes = cachedBoundingBoxes.ifEmpty {
            val canvas = Canvas(labelImage)
            canvas.drawBitmap(labelImage, 0f, 0f, thresholdPaint)
            Log.d("VISION_CALL", "call performed BGMIFRAMELABELIMAGE")
            val json = mlKitOCR.performVision(labelImage)
            val formatted = json?.let { MLKitOCR.formatResponse(it) }
            getBoundingBoxes(
                formatted,
                bottomTolerance = (13f / 89 * labelImage.height).roundToInt()
            ).also {
                if(it.isNotEmpty()){
                    cacheBoundingBoxesResponse(labelInfo, it)
                }else{
                    cacheBoundingBoxesResponse(labelInfo, listOf(Rect(0, 0, labelImage.width, labelImage.height)))
                }
            }
        }
        return boundingBoxes
    }

    open fun cacheBoundingBoxesResponse(
        labelInfo: TFResult,
        rectList: List<Rect>
    ) {
        labelInfo.cachedMlRects = gson.toJson(rectList)
    }

    open fun getCachedBoundingBox(labelInfo: TFResult) =
        if (labelInfo.cachedMlRects != null) gson.fromJson(
            labelInfo.cachedMlRects,
            rectListType
        ) else emptyList<Rect>()

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
        out: Bitmap,
        canvas: Canvas
    ) {
        var scaleFactor = 0.87f
        var widthScaleFactor = 1f
        var offsetY = 0
        val verticalPadding = 4
        val horizontalPadding = 2
        var heightSubtract = 0


        //Prepare threshold matrix
        thresholdPaint.apply {
            colorFilter =
                ColorMatrixColorFilter(createThresholdMatrix(1))
        }

        if(boundingBoxes.size == 1){
            //If it couldn't detect atleast two bounding boxes
            val drawRect = Rect(0, 0,boundingBoxes.first().right, boundingBoxes.first().bottom)
            canvas.drawBitmap(labelBitmap, null, drawRect, thresholdPaint)
            canvas.drawBitmap(labelBitmap, null, drawRect, multiplyPaint)
            return
        }

        boundingBoxes.forEach { currentRect ->
            var addOffsetY = 0
            val offsetX = 0
            val srcRect = currentRect.apply {
                left -= horizontalPadding
                right = labelBitmap.width
                top -= verticalPadding
                bottom += verticalPadding
            }
            val scaledRect = srcRect.createdScaled(scaleFactor)
            val dstRect = Rect(
                offsetX,
                offsetY,
                min((offsetX + scaledRect.width()).times(widthScaleFactor).roundToInt(), out.width),
                min(offsetY + scaledRect.height(), out.height) - heightSubtract
            )

            //Use threshold matrix to draw the image.
            //Purpose leveling down the gray values
            canvas.drawBitmap(
                labelBitmap,
                srcRect,
                dstRect,
                thresholdPaint
            )

            // Using PorterDuff.Mode.DARKEN mode and drawing again the image which darkens the pixels
            // according to above drawn layer of image's pixels
            canvas.drawBitmap(
                labelBitmap,
                srcRect,
                dstRect,
                multiplyPaint
            )

            addOffsetY = scaledRect.height() + 5
            widthScaleFactor -= 0.02f
            offsetY += addOffsetY
            scaleFactor *= 1.7f
            heightSubtract += (0.1f * labelBitmap.height).roundToInt()
        }
    }


    private fun Rect.createdScaled(factor: Float, padding: Int = 2): Rect {
        return Rect(
            (left * factor).toInt() - padding,
            (top * factor).toInt() - padding,
            (right * factor).toInt() + padding,
            (bottom * factor).toInt() + padding
        )
    }

    private fun getBoundingBoxes(
        visionResponse: ArrayList<JsonObject>?,
        bottomTolerance: Int = 10
    ): List<Rect> {
        val heightThreshold = 10
        val boundingBoxes = arrayListOf<Rect>()
        visionResponse?.forEach {
            it["wordBoundingBoxes"].asJsonArray.forEach boundingBoxScope@{ jsonElement ->
                if (jsonElement.asJsonArray.isEmpty) return@boundingBoxScope

                val firstBoundingBox = jsonElement.asJsonArray.first()
                val lastBoundingBox = jsonElement.asJsonArray.last()

                val topLeft = firstBoundingBox.asJsonArray.get(0).asJsonObject
                val bottomLeft = firstBoundingBox.asJsonArray.get(3).asJsonObject
                val topRight = lastBoundingBox.asJsonArray.get(1).asJsonObject
                val bottomRight = lastBoundingBox.asJsonArray.get(2).asJsonObject

                boundingBoxes.add(
                    Rect(
                        min(topLeft["x"].asInt, bottomLeft["x"].asInt),
                        min(topRight["y"].asInt, topLeft["y"].asInt),
                        max(bottomRight["x"].asInt, topRight["x"].asInt),
                        max(bottomRight["y"].asInt, bottomLeft["y"].asInt),
                    )
                )

            }
        }

        val mergedBoxes = arrayListOf<Rect>()


        val commonBoxes = boundingBoxes.filter { it.height() > heightThreshold }.groupBy { rect ->
            boundingBoxes.filter { other ->
                abs(rect.bottom - other.bottom) <= bottomTolerance
            }.minByOrNull { it.bottom }
        }.filterKeys { it != null }
        commonBoxes.forEach {
            val left = it.value.minBy { it.left }.left
            val top = it.value.minBy { it.top }.top
            val right = it.value.maxBy { it.right }.right
            val bottom = it.value.maxBy { it.bottom }.bottom

            mergedBoxes.add(Rect(left, top, right, bottom))
        }
        return mergedBoxes
    }


}