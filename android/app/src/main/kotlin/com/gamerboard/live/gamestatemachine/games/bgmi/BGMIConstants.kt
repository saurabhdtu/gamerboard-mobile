package com.gamerboard.live.gamestatemachine.games.bgmi

import com.gamerboard.live.gamestatemachine.games.GameConstants
import com.gamerboard.live.gamestatemachine.games.MachineLabelProcessor
import com.gamerboard.live.models.TFResult
import okhttp3.internal.immutableListOf

class BGMIConstants  {
//    companion object {
//        val resultRankRatingLabels = immutableListOf(
//            GameLabels.CLASSIC_RATING.ordinal, GameLabels.RANK.ordinal, GameLabels.GAME_INFO.ordinal
//        )
//        val resultRankKills = immutableListOf(
//            GameLabels.CLASSIC_ALL_KILLS.ordinal,
//            GameLabels.RANK.ordinal,
//            GameLabels.GAME_INFO.ordinal
//        )
//
//        val resultRank = immutableListOf(
//            GameLabels.GAME_INFO.ordinal, GameLabels.RANK.ordinal
//        )
//
//        val homeScreen = immutableListOf(
//            GameLabels.CLASSIC_START.ordinal,
//            GameLabels.PROFILE_SELF.ordinal,
//            GameLabels.PROFILE_ID.ordinal,
//        )
//
//        val waiting = immutableListOf(GameLabels.CLASSIC_ALL_WAITING.ordinal)
//        val inGame = immutableListOf(GameLabels.CLASSIC_ALL_GAMEPLAY.ordinal)
//        val login = immutableListOf(GameLabels.GLOBAL_LOGIN.ordinal)
//        val profile = immutableListOf(
//            GameLabels.PROFILE_ID.ordinal,
//            GameLabels.PROFILE_SELF.ordinal,
//            GameLabels.CLASSIC_START.ordinal
//        )
//    }

    enum class GameLabels {
        CLASSIC_RANK_GAME_INFO,//0
        CLASSIC_ALL_KILLS,//1
        CLASSIC_START,//2
        CLASSIC_ALL_WAITING,//3
        GAME_INFO,//4
        PROFILE_ID,//5
        GLOBAL_LOGIN,//6
        PROFILE_SELF, //7
        CLASSIC_RATING,//8
        CLASSIC_ALL_GAMEPLAY,
        RANK,
        // not used
        PROFILE_ID_DETAILS, HISTORY_GAMES, HISTORY_RANK_ONE, PROFILE_LEVEL;
    }
//
//    override fun resultRankRating() = resultRankRatingLabels
//
//
//    override fun resultRankKills() = resultRankKills
//
//    override fun resultRank() = resultRank
//
//    override fun homeScreenBucket() = homeScreen
//
//    override fun waitingScreenBucket() = waiting
//
//    override
//    fun gameScreenBucket() = inGame
//
//    override
//    fun loginScreenBucket() = login
//
//    override
//    fun historyScreenBucket() = immutableListOf(-1)
//
//    override fun myProfileScreen() = profile
//
//    override
//    fun getSortOrderForLabel(tfResult: TFResult): MachineLabelProcessor.LabelSortOrder {
//        when (tfResult.label.toInt()) {
//            GameLabels.CLASSIC_ALL_WAITING.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            GameLabels.CLASSIC_RANK_GAME_INFO.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            GameLabels.PROFILE_ID_DETAILS.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.VERTICAL
//            }
//
//            GameLabels.PROFILE_LEVEL.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            GameLabels.HISTORY_RANK_ONE.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            GameLabels.HISTORY_GAMES.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            GameLabels.GLOBAL_LOGIN.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            GameLabels.PROFILE_ID.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.VERTICAL
//            }
//
//            GameLabels.CLASSIC_RATING.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.VERTICAL
//            }
//
//            GameLabels.CLASSIC_START.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.SKIP
//            }
//
//            GameLabels.PROFILE_SELF.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.SKIP
//            }
//
//            GameLabels.CLASSIC_ALL_KILLS.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.VERTICAL
//            }
//
//            GameLabels.RANK.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            GameLabels.GAME_INFO.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            GameLabels.CLASSIC_ALL_GAMEPLAY.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            else -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//        }
//    }
//
//    override fun labelThreshold() = mapOf(
//        GameLabels.CLASSIC_RATING.ordinal to 0.35,
//        GameLabels.CLASSIC_START.ordinal to 0.35,
//        GameLabels.CLASSIC_ALL_KILLS.ordinal to 0.35,
//        GameLabels.RANK.ordinal to 0.35,
//        GameLabels.GAME_INFO.ordinal to 0.35,
//        GameLabels.PROFILE_SELF.ordinal to 0.35,
//        GameLabels.HISTORY_RANK_ONE.ordinal to 0.35,
//        GameLabels.PROFILE_LEVEL.ordinal to 0.35,
//        GameLabels.GLOBAL_LOGIN.ordinal to 0.35,
//        GameLabels.HISTORY_GAMES.ordinal to 0.35,
//        GameLabels.PROFILE_ID.ordinal to 0.35,
//        GameLabels.PROFILE_ID_DETAILS.ordinal to 0.35,
//        GameLabels.CLASSIC_ALL_WAITING.ordinal to 0.35,
//        GameLabels.CLASSIC_ALL_GAMEPLAY.ordinal to 0.45,
//        GameLabels.CLASSIC_RANK_GAME_INFO.ordinal to 0.35
//    )
//
//    override fun ratingLabel() = GameLabels.CLASSIC_RATING.ordinal
//    override fun killLabel() = GameLabels.CLASSIC_ALL_KILLS.ordinal
//    override fun profileIdLabel(): Int = GameLabels.PROFILE_ID.ordinal
//
//    override fun labelsForIndividualOCR(): List<Int> =
//        immutableListOf(GameLabels.CLASSIC_ALL_KILLS.ordinal)
//
//    override fun shouldPerformScaleAndStitching(label: Float): Boolean {
//        return (GameLabels.RANK.ordinal == label.toInt()) || (GameLabels.CLASSIC_RANK_GAME_INFO.ordinal == label.toInt())
//    }
//
//    override fun getGameInfoLabelFilter(label: Float): Boolean {
//        return GameLabels.GAME_INFO.ordinal == label.toInt()
//    }
//    override fun labelNameFromIndex(label: Int): String{
//        return "None"
//    }
//    /*
//
//        override fun ratingLabel(): GameLabels =GameLabels.CLASSIC_RATING
//
//        override fun killLabel(): GameLabels = GameLabels.CLASSIC_ALL_KILLS
//    */
//

}