package com.gamerboard.live.gamestatemachine.games.freefire

import com.gamerboard.live.gamestatemachine.games.GameConstants
import com.gamerboard.live.gamestatemachine.games.MachineLabelProcessor
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIConstants
import com.gamerboard.live.models.TFResult
import okhttp3.internal.immutableListOf

class FreeFireConstants  {

//    companion object {
//        val resultRankRating = immutableListOf(GameLabels.BR_RATING.ordinal)
//
//        val resultRankKills = immutableListOf(
//            GameLabels.BR_RANK.ordinal,
//            GameLabels.BR_GAME_INFO.ordinal,
//            GameLabels.BR_PERFORMANCE.ordinal
//        )
//
//        val homeScreen = immutableListOf(
//            GameLabels.PROFILE.ordinal,
//            GameLabels.START.ordinal,
//            GameLabels.SELF_MENU.ordinal
//        )
//
//        val inGame = immutableListOf(GameLabels.IN_GAME.ordinal)
//        val login = immutableListOf(GameLabels.LOGIN.ordinal)
//        val profile = immutableListOf(
//            GameLabels.PROFILE.ordinal,
//            GameLabels.SELF_MENU.ordinal
//        )
//    }

    enum class GameLabels {
        UID,//0
        PROFILE,//1
        IN_GAME,//2
        SELF_MENU,//3
        SELF_USERNAME,//4
        BR_KILL,//5
        USERNAME,//6
        BR_IS_SQUAD,//7
        BR_RANK,//8
        START,//9
        SQUAD_EXIT,//10
        BR_PERFORMANCE,//11
        BR_RATING,//12
        BR_GAME_INFO,//13
        LOGIN;//14
    }

//    override fun resultRankRating() = resultRankRating
//
//    override fun resultRankKills() = resultRankKills
////    , GameLabels.BR_GAME_INFO.i
//
//    override fun resultRank() = immutableListOf(-2)
//
//
//    override fun homeScreenBucket() = homeScreen
//
//    override fun waitingScreenBucket() = immutableListOf(-3)
//
//    override fun gameScreenBucket() = inGame
//
//    override fun loginScreenBucket() = login
//
//    override fun historyScreenBucket() = immutableListOf(-2)
//
//    override fun myProfileScreen() = profile
//
//    override fun getSortOrderForLabel(tfResult: TFResult): MachineLabelProcessor.LabelSortOrder {
//        when (tfResult.label.toInt()) {
//            GameLabels.LOGIN.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            GameLabels.PROFILE.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.VERTICAL
//            }
//
//            GameLabels.BR_RATING.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.VERTICAL
//            }
//
//            GameLabels.START.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.SKIP
//            }
//
//            GameLabels.SELF_MENU.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.VERTICAL
//            }
//
//            GameLabels.BR_IS_SQUAD.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.SKIP
//            }
//
//            GameLabels.BR_RANK.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.HORIZONTAL
//            }
//
//            GameLabels.BR_KILL.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.VERTICAL
//            }
//
//            GameLabels.BR_PERFORMANCE.ordinal -> {
//                return MachineLabelProcessor.LabelSortOrder.PERFORMANCE
//            }
//
//            GameLabels.BR_GAME_INFO.ordinal -> {
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
//        GameLabels.START.ordinal to 0.40,
//        GameLabels.BR_RATING.ordinal to 0.40,
//        GameLabels.PROFILE.ordinal to 0.40,
//        GameLabels.SELF_MENU.ordinal to 0.05,
//        GameLabels.IN_GAME.ordinal to 0.40,
//        GameLabels.SQUAD_EXIT.ordinal to 0.40,
//        GameLabels.LOGIN.ordinal to 0.05,
//        GameLabels.BR_PERFORMANCE.ordinal to 0.40,
//        GameLabels.BR_IS_SQUAD.ordinal to 0.40,
//        GameLabels.BR_GAME_INFO.ordinal to 0.40,
//        GameLabels.BR_KILL.ordinal to 0.40,
//        GameLabels.BR_RANK.ordinal to 0.40,
//    )
//
//    override fun ratingLabel() = GameLabels.BR_RATING.ordinal
//
//    override fun killLabel() = GameLabels.BR_KILL.ordinal
//    override fun profileIdLabel() = GameLabels.PROFILE.ordinal
//    override fun labelsForIndividualOCR(): List<Int> =
//        immutableListOf(GameLabels.BR_KILL.ordinal, GameLabels.PROFILE.ordinal)
//
//    override fun shouldPerformScaleAndStitching(label: Float): Boolean {
//        return false
//    }
//
//    override fun getGameInfoLabelFilter(label: Float): Boolean {
//        return  false
//    }
//    override fun labelNameFromIndex(label: Int): String{
//        return "None"
//    }
}