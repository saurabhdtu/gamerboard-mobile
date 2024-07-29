package com.gamerboard.live.gamestatemachine.games

import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.models.*
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.models.db.GameInfo
import com.gamerboard.live.utils.RegexPatterns
import com.gamerboard.live.utils.dateTimeFormat
import com.gamerboard.logger.gson
import com.google.gson.JsonObject
import com.google.mlkit.vision.text.Text
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.internal.immutableListOf
import okhttp3.internal.toImmutableList
import java.text.DateFormatSymbols
import java.util.*

abstract class MachineLabelUtils {


    fun setOcrAgainstLabel(
        input: ImageResultJsonFlat, labelOcrMap: Map<Int, String>
    ): ImageResultJsonFlat {
        for ((onLabel, setOcr) in labelOcrMap) {
            for (label in input.labels) {
                if (label.label == onLabel) {
                    label.ocr = setOcr
                }
            }
        }
        return input
    }

    abstract fun containsKills(value: String): Boolean

    abstract fun sortOCRValues(horizontalList: MutableList<Text.TextBlock>): String
    abstract fun getCorrectNumberFromTwoValues(new: String?, old: String?): String?
    fun getCorrectGameInfo(new: String?, old: String?): String? {
        val l = immutableListOf(UNKNOWN, null)
        val gameInfo = GameInfo(UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN)
        new?.let { n ->
            val newGameInfo = getGameInfo(n)
            old?.let { o ->
                val oldGameInfo = getGameInfo(o)
                if (l.contains(newGameInfo.group)) gameInfo.group = oldGameInfo.group
                else gameInfo.group = newGameInfo.group
                if (l.contains(newGameInfo.view)) gameInfo.view = oldGameInfo.view
                else gameInfo.view = newGameInfo.view
                if (l.contains(newGameInfo.mode)) gameInfo.mode = oldGameInfo.mode
                else gameInfo.mode = newGameInfo.mode
                if (l.contains(newGameInfo.type)) gameInfo.type = oldGameInfo.type
                else gameInfo.type = newGameInfo.type

                return Json.encodeToString(gameInfo)
            }
            return new
        }
        return null
    }

    fun getGameInfo(gameInfo: String): GameInfo = Json.decodeFromString(gameInfo)
}

object LabelUtils {

    private val valid0DigitCharacters = listOf('O', 'o', 'D', 'u', 'U', 'Q', ')', '(')
    private val valid1DigitCharacters = listOf('I', 'i', '|', 'l', '!', 't')
    private val valid2DigitCharacters = listOf('Z', '?')
    private val valid6DigitCharacters = listOf('G')
    private val valid7DigitCharacters = listOf('T')
    private val valid5DigitCharacters = listOf('S')
    private val valid8DigitCharacters = listOf('&')

    private val validRomanICharacters = listOf('I', 'l', 'i', '|', '1')
    private val validRomanXCharacters = listOf('x', 'X')
    private val validRomanVCharacters = listOf('V', 'v', 'u', 'U')

    fun editDistance(sA: String, sB: String): Int {
        val s1 = sA.lowercase().trimSpace()
        val s2 = sB.lowercase().trimSpace()
        val l1 = s1.length
        val l2 = s2.length

        val dp: Array<Array<Int>> = Array(l1 + 1) { Array(l2 + 1) { 0 } }
        for (i in 0..l1) dp[i][0] = i

        for (j in 0..l2) dp[0][j] = j

        for (i in 1..l1) {
            for (j in 1..l2) {

                if (s1[i - 1] == s2[j - 1]) dp[i][j] = dp[i - 1][j - 1]
                else dp[i][j] = 1 + minOf(dp[i - 1][j - 1], dp[i - 1][j], dp[i][j - 1])
            }
        }
        return dp[l1][l2]
    }

    fun String.trimSpace(): String {
        return replace(RegexPatterns.whiteSpaceContinuous, "")
    }

    fun String.correctRomans(): String {
        var s1 = ""
        s1 += this
        var s2 = ""

        for (c in s1) {
            s2 += when (c) {
                in validRomanICharacters -> 'I'
                in validRomanXCharacters -> 'X'
                in validRomanVCharacters -> 'V'
                else -> c
            }
        }
        return s2
    }


    fun String.isNumber(): Boolean {
        return try {
            this.toInt()
            true
        } catch (ex: NumberFormatException) {
            false
        }
    }

    fun String.digitsOnly(): String {

        return RegexPatterns.digitsOnly.replace(this, "")
    }

    fun String.alphaNumericOnly(): String {

        return RegexPatterns.alphaNumeric.replace(this, "")
    }

    fun String.alphabetsOnly(): String {

        return RegexPatterns.alphabetsOnly.replace(this, "")
    }

    fun splitByChars(split: String, by: Regex): List<String> {
        return by.split(split, 0)
    }

    fun String.removeLeadingZeroes(): String {
        return try {
            this.toInt().toString()
        } catch (e: Exception) {
            this
        }
    }

    fun <T> findMostCommonInList(list: List<T>): T? {
        val data = list.groupBy { it }
            .maxByOrNull { it.value.size }
        return data?.key
    }

    fun JsonObject.validatePlayerScoring(): Boolean {
        return this["username"] != null && this["kills"] != null && this["username"].asString.isNotEmpty() && this["kills"].asInt != -1
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> findMostCommonInListWithConflict(list: List<Pair<T, T>>): T? {
        val maxTimesSize = list.groupBy { it.first }.maxByOrNull { it.value.size }?.value?.size

        val withSame = list.groupBy { it.first }.count { it.value.size == maxTimesSize }

        if (withSame < 2) {
            return list.groupBy { it.first }.maxByOrNull { it.value.size }?.key.also {
                testLogGreen("Finally accepted maximum occurred: $it")
            }
        }

        val noCorrection = list.groupBy {
            it.second.toString().digitsOnly(3).removeLeadingZeroes()
        }.maxByOrNull { it.value.size }?.key

        var final = noCorrection
        testLogGrey("With No correction Dominates: $final")

        if (noCorrection == null || noCorrection.isEmpty()) {
            final = list.last().first as String
            testLogGrey("With No digits found Dominates the one which comes later: $final")
        }

        testLogGreen("Finally accepted: $final")
        return final as T
    }

    fun String.correctDigitOcr(): String {
        val result = this.toCharArray()
        for (i in result.indices) {
            if (result[i] in valid0DigitCharacters) result[i] = '0'
            if (result[i] in valid1DigitCharacters) result[i] = '1'
            if (result[i] in valid2DigitCharacters) result[i] = '2'
            if (result[i] in valid6DigitCharacters) result[i] = '6'
            if (result[i] in valid7DigitCharacters) result[i] = '7'
            if (result[i] in valid5DigitCharacters) result[i] = '5'
            if (result[i] in valid8DigitCharacters) result[i] = '8'
        }
        return String(result)
    }

    fun String.digitsOnly(mx: Int): String {
        val stk = Stack<Char>()
        var res = ""
        val str = this.alphaNumericOnly().trimSpace()
        val l = str.length

        var noDig = 0
        for (i in (l - 1) downTo 0 step 1) {

            if (str[i].isDigit()) {
                noDig = 0
                stk.push(str[i])
            } else {
                noDig++
                if (noDig == 2) break
            }

            if (stk.size == mx) break
        }
        for (c in stk) res = c + res
        return if (res.isEmpty()) res else res.toInt().toString()
    }

    fun String.removeStartDigits(): String {
        val res = this.toCharArray()
        val l = res.size
        for (i in 0..kotlin.math.min((l / 2), 3)) {
            if (res[i].toString().digitsOnly().isNotEmpty()) {
                res[i] = ' '
            }
        }
        return res.concatToString()
    }

    fun flattenResultJson(input: ImageResultJson): ImageResultJsonFlat {
        val resultJsonFlat = ImageResultJsonFlat(
            epochTimestamp = input.epochTimestamp, fileName = input.url, labels = arrayListOf()
        )

        // sort via x coordinate
        val mutableData = input.labels.toImmutableList()
        mutableData.sortedBy { it.box[3] }

        resultJsonFlat.labels = mutableData

        return resultJsonFlat
    }


    fun <T> List<T>.isSameAs(with: List<T>): Boolean =
        this.size <= with.size && with.containsAll(this)

    fun String.lastChars(l: Int): String {
        return this.substring(this.length - l, this.length)
    }

    fun String.stitchDigits(spaceLimit: Int = 1): String {
        val stk = Stack<Char>()
        var res = ""
        val str = this

        for (c in str) {

            if (c.isDigit() && stk.isNotEmpty()) {
                for (i in 1..spaceLimit) {
                    if (stk.isNotEmpty()) {
                        val back1 = stk.peek()
                        if (back1.isWhitespace()) {
                            stk.pop()
                        }
                    } else break
                }
                stk.push(c)
            } else stk.push(c)
        }

        for (c in stk.reversed()) res = c + res
        return res
    }


    fun testLogGreen(message: String) {
        if (debugMachine != DEBUGGER.DISABLED) {
            println("\u001B[32m$message")
        }
    }

    fun testLogRed(message: String) {
        if (debugMachine != DEBUGGER.DISABLED) {
            println("\u001B[31m$message")
        }
    }

    fun testLogGrey(message: String) {
        if (debugMachine != DEBUGGER.DISABLED) {
            println(message)
        }
    }


    fun <T> ArrayList<T>.alternateCount(): Int {
        var timesChanged: Int = -1
        var prev: T? = null
        for (input in this) {
            if (input != prev) timesChanged++
            prev = input
        }
        return timesChanged
    }

    fun getListOfLabels(input: List<TFResult>): List<Int> = input.map { it.label }

    fun Array<String>.buildGameInfo(): GameInfo {
        val (type, mode, group, name) = this
        return GameInfo(type, mode, group, name)
    }

    fun Int.hasLabel(checkFor: Int): Boolean {
        val checkIn: Int = this
        return ((checkIn and checkFor) > 0)
    }

    fun timeStampInUTC(month: String, day: String, hour: String, minute: String): String {
        return try {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            //"MMM dd, yyyy HH:mm:ss"
            //"Jul 16, 2013 12:08:59 AM"
            val dateStr =
                "${DateFormatSymbols().shortMonths[month.toInt() - 1]} $day, $year $hour:$minute:00"

            dateTimeFormat.timeZone = TimeZone.getDefault()
            val date: Date = dateTimeFormat.parse(dateStr) ?: return ""
            dateTimeFormat.timeZone = TimeZone.getTimeZone("UTC")
            val utcDate = dateTimeFormat.format(date)
            val dateS = dateTimeFormat.parse(utcDate) as Date
            testLogGreen("Date in UTC: $utcDate, timeStamp: ${dateS.time}")
            "${dateS.time}"
        } catch (e: Exception) {
            ""
        }
    }

    fun getAsBits(a: Int, b: Int): Int {
        return (a.shl(1) or b.shl(0))
    }

    fun String.putToMetaInfoJson(key: String, value: String): String {
        var metaInfoJson = this
        val updatedMeta =
            if (metaInfoJson == UNKNOWN) mutableListOf<Pair<String, String>>() else (Json.decodeFromString(
                metaInfoJson
            ) as MutableList<Pair<String, String>>)

        // updated not to append, but to update
        updatedMeta.removeIf { it.first == key }
        updatedMeta += Pair(key, value)

        metaInfoJson = Json.encodeToString(updatedMeta)
        return metaInfoJson
    }

    fun String.getFromMetaInfoJson(key: String): String {
        val metaInfoJson = this
        if (metaInfoJson != UNKNOWN) (Json.decodeFromString(metaInfoJson) as List<Pair<String, String>>).forEach {
            if (it.first == key) return it.second
        }
        return UNKNOWN
    }

    fun String.hasClearMajority(list: ArrayList<String>): Boolean {
        val total: Float = list.size.toFloat()
        val count: Float = list.count { it == this }.toFloat()

        testLogGreen("$count > ${(total * (1.0f / 3.0f))}")
        if (count == (total / 2.0f)) return false
        return (count > (total * (1.0f / 3.0f)))
    }


    fun decodeGameFromMap(map: Map<String, String>?): Game? {
        var game: Game? = null
        try {
            if (map?.contains("current_game") != null && map.contains("current_game")) {
                map["current_game"]?.let {
                    game = Json.decodeFromString(it) as Game?
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            game = null
        }
        return game
    }

    fun decodeServerGameFromMap(map: Map<String, String?>?): CustomGameResponse? {
        var game: CustomGameResponse? = null
        try {
            if (map?.contains("current_game") != null && map.contains("current_game")) {
                map["current_game"]?.let {
                    game = gson.fromJson(it, CustomGameResponse::class.java)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            game = null
        }
        return game
    }

    fun calculateGamePointsFromGame(game: Game): Array<String> {
        val rankPoints = getPointsForRank(game.rank)
        val killPoints = getKillPoints(game.kills)
        val totalPoints = maxOf(rankPoints, 0) + maxOf(killPoints, 0)
        val rank = game.rank
        val kills = game.kills
        return arrayOf("$rankPoints", "$killPoints", "$totalPoints", "$rank", "$kills")
    }

    fun getKillPoints(kills: String?): Int {
        var result = -1
        try {
            if (kills == null) return -1
            if (kills == UNKNOWN) return -1
            result = kills.toInt()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return result
    }


    fun getPointsForRank(rank: String?): Int {
        if (rank == null) return -1

        return when (true) {
            (rank == UNKNOWN) -> -1
            (rank == "1") -> 15
            (rank == "2") -> 12
            (rank == "3") -> 10
            (rank == "4") -> 8
            (rank == "5") -> 6
            (rank == "6") -> 4
            (rank == "7") -> 2
            (rank == "8") -> 1
            (rank == "9") -> 1
            (rank == "10") -> 1
            (rank == "11") -> 1
            (rank == "12") -> 1
            else -> 0
        }
    }

    fun updateGameInfo(infoStr: String?, updatedInfo: GameInfo?): String {
        if (infoStr == null || infoStr == UNKNOWN) return if (updatedInfo == null) Json.encodeToString(
            GameInfo(
                UNKNOWN, UNKNOWN, UNKNOWN, UNKNOWN
            )
        )
        else Json.encodeToString(updatedInfo)

        val originalInfo: GameInfo = Json.decodeFromString(infoStr)

        if (updatedInfo == null) return infoStr

        val mode = if (updatedInfo.mode == UNKNOWN) originalInfo.mode else updatedInfo.mode
        val view = if (updatedInfo.view == UNKNOWN) originalInfo.view else updatedInfo.view
        val type = if (updatedInfo.type == UNKNOWN) originalInfo.type else updatedInfo.type
        val group = if (updatedInfo.group == UNKNOWN) originalInfo.group else updatedInfo.group

        return Json.encodeToString(GameInfo(mode = mode, view = view, type = type, group = group))
    }


}