package com.gamerboard.live.gamestatemachine.games

import android.graphics.Bitmap
import android.graphics.Rect
import com.gamerboard.live.gamestatemachine.games.LabelUtils.trimSpace
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.caching.BitmapCache
import com.gamerboard.live.type.SortOrder
import com.gamerboard.logger.model.GameLogMessage
import com.gamerboard.logger.LogCategory
import com.gamerboard.logging.LoggingAgent
import com.google.mlkit.vision.text.Text
import kotlinx.serialization.Transient
import org.koin.java.KoinJavaComponent

object LabelHelper {


    @Transient
    private val bitmapCache: BitmapCache by KoinJavaComponent.inject(BitmapCache::class.java)
    suspend fun getCroppedImage(
        tfResult: TFResult,
        original: Bitmap,
        gameInfoBoundingBox: Rect? = null,
    ): Bitmap {
        val croppedBitmap =
            if (MachineConstants.gameConstants.shouldPerformScaleAndStitching(tfResult.label.toFloat())) {
                bitmapCache.createLabelImageFromBitmap(tfResult, original, gameInfoBoundingBox)
            } else {
                bitmapCache.createLabelImageFromBitmap(tfResult, original)
            }

        return croppedBitmap
    }

    fun readTextFromTextBlocks(
        logger: LoggingAgent,
        tfResult: TFResult,
        horizontalList: MutableList<Text.TextBlock>,
    ): String {
        var text = ""
        val sortOrder = MachineConstants.gameConstants.getSortOrderForLabel(tfResult)

        if (sortOrder == SortOrder.HORIZONTAL) {
            horizontalList.sortWith(compareBy { it.boundingBox?.left })
            horizontalList.forEach { text += " ${it.text}" }
            tfResult.ocr = text.trim()
        } else if (sortOrder == SortOrder.VERTICAL) {
            horizontalList.sortWith(
                compareBy({ it.boundingBox!!.top },
                    { it.boundingBox!!.left })
            )
            horizontalList.forEach {
                it.lines.forEach { t ->
                    if (t.text.trim()
                            .isNotEmpty() && t.text.trimSpace().length <= 60 // to avoid un wanted line 60 is the line length,
                    ) {
                        text += "[.]${t.text.trim()}"
                    } else {
                        // the largest length we have is "ranked Classic mode: (TPP) squad random map name"
                        logger.log(
                            GameLogMessage.Builder()
                                .setMessage("Text ignored for processing, length limit exceeded")
                                .setCategory(LogCategory.ENGINE).build()
                        )
                    }

                }
            }
        } else if (sortOrder == SortOrder.PERFORMANCE) {
            text = MachineConstants.machineLabelUtils.sortOCRValues(horizontalList)
        }
        return text
    }

    fun validateTextBlocks(
        response: List<Text.TextBlock>,
        coordinates: Pair<Int, Int>,
    ): MutableList<Text.TextBlock> {

        return response.filter { item -> item.boundingBox!!.top > coordinates.first - 5 && item.boundingBox!!.bottom < coordinates.second + 10 }
            .toMutableList()
    }
}