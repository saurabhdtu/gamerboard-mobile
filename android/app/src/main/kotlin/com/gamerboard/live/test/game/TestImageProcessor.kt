package com.gamerboard.live.test.game

import android.content.Context
import android.graphics.Point
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.service.screencapture.ImageProcessor

class TestImageProcessor(ctx: Context, display: Point, sessionId: Int, apiClient: ApiClient) :
    ImageProcessor(ctx, display, sessionId) {

}