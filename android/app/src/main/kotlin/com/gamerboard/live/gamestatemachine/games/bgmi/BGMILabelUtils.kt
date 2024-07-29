package com.gamerboard.live.gamestatemachine.games.bgmi

import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.games.LabelUtils.alphabetsOnly
import com.gamerboard.live.gamestatemachine.games.MachineLabelUtils
import com.google.mlkit.vision.text.Text

class BGMILabelUtils : MachineLabelUtils() {

    override fun containsKills(value: String): Boolean {
        return LabelUtils.editDistance(
            value.alphabetsOnly(),
            "finishes"
        ) <= 3 || LabelUtils.editDistance(
            value.alphabetsOnly(),
            "eliminations"
        ) <= 4
    }

    override fun sortOCRValues(horizontalList: MutableList<Text.TextBlock>): String{
        return "[..]"
    }

    fun containsKillsAndAssists(value: String): Boolean {
        return value.contains("es") || value.contains("As") || value.lowercase().contains("fin")
    }

    override fun getCorrectNumberFromTwoValues(new: String?, old: String?): String? = new

}