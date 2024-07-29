package com.gamerboard.live.gamestatemachine.games.bgmi.processor

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import com.gamerboard.live.caching.GameValueCache
import com.gamerboard.live.models.TFResult
import com.gamerboard.logger.gson
import org.koin.java.KoinJavaComponent.inject

class BGMIKillPerGameLabelImageProcessor : BGMIKillPerFrameLabelImageProcessor(100) {

    private val gameValueCache  : GameValueCache by inject(GameValueCache::class.java)

    companion object{
        private const val killBoundingBoxCache = "kill_bounding_box_cache"
    }

    override suspend fun calculateBoundingBoxes(
        labelInfo: TFResult,
        labelImage: Bitmap
    ): List<Rect> {
        return super.calculateBoundingBoxes(labelInfo, labelImage)
    }

    override fun cacheBoundingBoxesResponse(labelInfo: TFResult, rectList: List<Rect>) {
        Log.i("GameValueCache", "cachingResponse}")
        gameValueCache.putValue(killBoundingBoxCache, gson.toJson(rectList))
    }

    override fun getCachedBoundingBox(labelInfo: TFResult): List<Rect> {
        Log.i("GameValueCache", "gettingFromCache")
        val value =  gameValueCache.getValue(killBoundingBoxCache)?.let{ cachedRects ->
           return gson.fromJson(
                cachedRects,
                rectListType
            )
        } ?:  emptyList<Rect>()

        return value
    }
}