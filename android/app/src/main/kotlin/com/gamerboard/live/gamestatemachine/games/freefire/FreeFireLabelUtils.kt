package com.gamerboard.live.gamestatemachine.games.freefire

import android.graphics.Rect
import com.gamerboard.live.gamestatemachine.games.MachineLabelUtils
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants
import com.google.mlkit.vision.text.Text
import kotlin.math.abs
import kotlin.math.absoluteValue

class FreeFireLabelUtils : MachineLabelUtils() {
    override fun containsKills(wordOfOcr: String): Boolean {
        return wordOfOcr.lowercase().contains("eliminations")
//        return LabelUtils.editDistance(
//            wordOfOcr.alphabetsOnly(),
//            "eliminations"
//        ) <= 3
    }

    override fun getCorrectNumberFromTwoValues(new: String?, old: String?): String? {
        if (new == null || old == null)
            return new ?: old
        else if ((new.isEmpty() || new == StateMachineStringConstants.UNKNOWN) && old.isNotEmpty())
            return old
        else if (new.isNotEmpty() && (old.isEmpty() || old == StateMachineStringConstants.UNKNOWN))
            return new
        else {
            if (new.contains("1") && new.contains(old))
                return new
            else if (old.contains("1") && old.contains(new) && !old.contains("-1"))
                return old
            return new
        }
    }


    override fun sortOCRValues(horizontalList: MutableList<Text.TextBlock>): String {
        horizontalList.sortWith(
            compareBy({ it.boundingBox!!.left })
        )
        var nameDetails: Rect? = null
        horizontalList.forEach  {
            if(it.text == "NAME"){
                nameDetails = it.boundingBox!!
                return@forEach
            }
        }
        if (nameDetails == null){
            return "[..]"
        }

        var leftValue = horizontalList[0].boundingBox!!.left
        var minLeft = horizontalList[0].boundingBox!!.left
        var usernames = mutableListOf<Text.TextBlock>()
        var left = mutableListOf<Text.TextBlock>()

        horizontalList.forEach {
            if (nameDetails!!.left - 100< it.boundingBox!!.left && it.boundingBox!!.left < nameDetails!!.left + 100 ){
                usernames.add(it)
            }
            else if (!(nameDetails!!.bottom - 20 < it.boundingBox!!.bottom && it.boundingBox!!.bottom < nameDetails!!.bottom + 20)) {
                if (it.boundingBox!!.left - leftValue < 20) {
                    if (minLeft > it.boundingBox!!.left) minLeft = it.boundingBox!!.left
                    it.boundingBox!!.left = leftValue
                } else {
                    leftValue = it.boundingBox!!.left
                    left.add(it)
                }
            }
        }

        horizontalList.removeAll(usernames)
        horizontalList.sortWith(
            compareBy({ it.boundingBox!!.bottom })
        )
        var kills = mutableListOf<Text.TextBlock>()
        var count = 1
        var bottom = mutableListOf<Text.TextBlock>()
        var mintop = horizontalList[0].boundingBox!!.bottom
        var bottomValue = horizontalList[0].boundingBox!!.bottom
        horizontalList.forEach{
            if(nameDetails!!.left +400 - 50< it.boundingBox!!.left && it.boundingBox!!.left < nameDetails!!.left + 400 + 50){
                kills.add(it)
            }
            if( it.boundingBox!!.bottom - bottomValue < 20){
                if (mintop > it.boundingBox!!.bottom) mintop = it.boundingBox!!.bottom
                it.boundingBox!!.bottom = bottomValue
            }
            else{
                bottomValue = it.boundingBox!!.bottom
                bottom.add(it)
                count++
            }
        }

        usernames.sortWith(
            compareBy {
                it.boundingBox!!.top
            }
        )
        kills.sortWith(
            compareBy { it.boundingBox!!.top }
        )
        bottom.sortWith(
            compareBy { it.boundingBox!!.top }
        )

        var usernameText = MutableList(count){"None"}
        var killText = MutableList(count){"None"}

        var maxCount = count.absoluteValue
        bottom.forEach {
            count--
            var mean = (abs(it.boundingBox!!.bottom) + abs(it.boundingBox!!.top)).toDouble()/2
            usernames.forEach {username ->
                var text = ""
                username.lines.forEach {t->
                    text += "[.]${t.text.trim()}"
                }

                var usernameMean = (abs(username.boundingBox!!.bottom) + abs(username.boundingBox!!.top)).toDouble()/2

                if (abs(mean - username.boundingBox!!.bottom) <20 || abs(mean - username.boundingBox!!.top) <20 || abs(usernameMean - mean) <20){
                    usernameText[maxCount-count] =  text

                }
            }

            kills.forEach { kill ->
                var killMean = (abs(kill.boundingBox!!.bottom) + abs(kill.boundingBox!!.top)).toDouble()/2
                if (abs(mean - kill.boundingBox!!.bottom) <20 || abs(mean - kill.boundingBox!!.top) <20 || abs(mean - killMean) <20){
                    killText[maxCount-count] =  kill.text
                }
            }
        }


        var isSquad:Boolean = false
        if (count>2 ||  left.size>4){
            isSquad = true
        }
        var text = "$usernameText[..]$killText[..]$isSquad"
        return text
    }


}