package com.gamerboard.live.models

data class FeedBackData(val destination: FeedBackFrom, val counter: Int)

enum class FeedBackFrom {
    GAME_COMPLETION,
    BACK_TO_GB_FROM_GAME
}
