package com.gamerboard.live.pool

import android.graphics.Bitmap
import kotlin.math.max

class PoolManager {
    private var _bufferedBitmapPool: BufferedBitmapPool? = null
    private var _bitmapPool: BitmapPool? = null
    private var maxLabelWidth: Int = 0
    private var maxLabelHeight: Int = 0
    val bufferedBitmapPool: BufferedBitmapPool? get() = _bufferedBitmapPool
    val bitmapPool: BitmapPool? get() = _bitmapPool
    fun initBufferedBitmapPool(width: Int, height: Int): BufferedBitmapPool {
        if (_bufferedBitmapPool == null || _bufferedBitmapPool?.elements()?.isEmpty() == true) {
            //Clean existing pool
            _bufferedBitmapPool?.clean()

            val factory = BitmapFactory(width, height)
            _bufferedBitmapPool = BufferedBitmapPool(20, factory)
        }
        return _bufferedBitmapPool!!
    }


    fun initBitmapPool(width: Int, height: Int) {
        if (width > maxLabelWidth || height > maxLabelHeight || _bitmapPool?.elements()
                ?.isEmpty() == true
        ) {
            //Clean existing pool
            _bitmapPool?.clean()
            //Recreate pool
            _bitmapPool = BitmapPool(5, BitmapFactory(width, height))
        }

        maxLabelWidth = max(maxLabelWidth, width)
        maxLabelHeight = max(maxLabelWidth, height)
    }

    fun getBitmapFromBitmapPool(): Bitmap? {
        return _bitmapPool?.get(BitmapPool.Param(maxLabelWidth, maxLabelHeight))
    }
}