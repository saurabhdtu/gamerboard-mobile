package com.gamerboard.live.models

import android.graphics.Bitmap
import android.graphics.Rect
import androidx.annotation.Keep
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.type.SortOrder
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.math.max
import kotlin.math.min

/**
 * Created by saurabh.lahoti on 18/08/21
 */


@Keep
@Serializable
data class TFResult(
    val label: Int,
    val confidence: Float,
    val box: FloatArray,
    var resolution: Resolution,
    var ocr: String,
    var cachedMlRects: String? = null,
) {


    @Transient
    private var rect: Rect? = null


    companion object {
        private val TAG = TFResult::class.java.simpleName
    }


    fun clear(){
        rect = null
    }


    fun evaluatedHeight(bounding: Rect, from: Bitmap) =
        max(32, min(bounding.height(), from.height - bounding.top))

    fun evaluatedWidth(bounding: Rect, from: Bitmap) =
        evaluatedWidth(bounding, from.width)

    private fun evaluatedWidth(bounding: Rect, width: Int) =
        max(32, min(bounding.width(), width - bounding.left))

    fun shouldSkip(): Boolean {
        return (MachineConstants.gameConstants.getSortOrderForLabel(this) == SortOrder.SKIP)
    }

    fun shouldPerformIndividualOcr(): Boolean {
        return MachineConstants.gameConstants.labelsForIndividualOCR().contains(label)
    }

    fun getBoundingBox(): Rect {
        val y1 = (box[0] * resolution.height).toInt()
        val x1 = (box[1] * resolution.width).toInt()
        val y2 = (box[2] * resolution.height).toInt()
        val x2 = (box[3] * resolution.width).toInt()
        return Rect(
            x1,
            y1,
            x2,
            y2
        )
    }
}

@Keep
@Serializable
data class ImageResultJson(
    val epochTimestamp: Long,
    val url: String,
    var labels: List<TFResult>,
    var verifiedProfile: Boolean?,
)

@Keep
@Serializable
data class Resolution(val width: Int, val height: Int)

@Keep
@Serializable
data class ImageResultJsonFlat(
    val epochTimestamp: Long,
    val fileName: String,
    var labels: List<TFResult>,
)

