package com.gamerboard.live.gamestatemachine.games.freefire

import android.util.Log
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.games.LabelUtils.alphabetsOnly
import com.gamerboard.live.gamestatemachine.games.LabelUtils.alternateCount
import com.gamerboard.live.gamestatemachine.games.LabelUtils.correctDigitOcr
import com.gamerboard.live.gamestatemachine.games.LabelUtils.correctRomans
import com.gamerboard.live.gamestatemachine.games.LabelUtils.digitsOnly
import com.gamerboard.live.gamestatemachine.games.LabelUtils.isNumber
import com.gamerboard.live.gamestatemachine.games.LabelUtils.validatePlayerScoring
import com.gamerboard.live.gamestatemachine.games.MachineInputValidator
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIInputValidator
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineResult
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants
import com.gamerboard.live.gamestatemachine.stateMachine.minimumTruePositives
import com.gamerboard.live.gamestatemachine.stateMachine.updateGameInDatabase
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.models.db.GameInfo
import com.gamerboard.live.type.FfMaxLevels
import com.gamerboard.live.utils.RegexPatterns
import com.gamerboard.live.utils.logException
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.Logger
import com.gamerboard.logger.gson
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.serialization.Serializable
import okhttp3.internal.immutableListOf
import java.util.TreeMap
import kotlin.math.abs

class FreeFireInputValidator : MachineInputValidator() {

    private val rantingSplitterRegex: Regex = Regex(":|#+|%+|\\s+")
    private val uidRegex = Regex("[A-Z]*(ID|OD|UD|VD|0D|D)", RegexOption.IGNORE_CASE)
    private val seperaterRegex = Regex("[:|.|,|;]")
    private val rankRatingSplitterRegex =  "\\[\\.\\]|\\s+".toRegex()

    override fun validateProfileId(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        val profileID: String
        val validIds = arrayListOf<String>()
        val isSelf = validateSelf(input)
//        val isSelf = true

        if (isSelf) {
            for (i in 0 until input.size) {
                val labels = input[i]
                for (j in 0 until labels.labels.size) {
                    val label = labels.labels[j]
                    if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.profileId) {
                        val ocrText = label.ocr
                        Log.d("LOG", " AAX :ocr:${ocrText}")
                        LabelUtils.testLogGrey("validateProfileId:  splitted: ${
                            ocrText.split("[.]").joinToString { "$it, " }
                        }  from ${label.ocr}")
                        for (wordOfOcr in ocrText.split("[.]")) {
                            if (uidRegex.find(wordOfOcr)?.groups?.isNotEmpty() == true) {
                                Log.d("LOG", " AAX :word:${wordOfOcr}")
                                val ocr =
                                    wordOfOcr.replace(uidRegex, "").replace(seperaterRegex, "")
                                        .correctDigitOcr().digitsOnly()
                                if (ocr.length >= 6) {
                                    validIds.add(ocr)
                                    LabelUtils.testLogGrey("validateProfileId:  truePositive: $ocr  from ${label.ocr}")
                                    break
                                }
                            }
                        }
                    }
                }
            }
        }

        if (validIds.size > 0) {
            profileID = LabelUtils.findMostCommonInList(validIds)!!
            val cnt = validIds.count { it == profileID }
            if (cnt >= minimumTruePositives["id"]!!) {
                result.setAccepted()
                result.setId(profileID)
            }
            LabelUtils.testLogGreen("validateProfileId:  accepted:  $profileID with count $cnt")
        }

        val finalResult = result.build()
        LabelUtils.testLogGrey("validateProfileId:  accept: ${finalResult.accept}, truePositive: ${finalResult.id}")
        return result.build()
    }

    private fun validateSelf(input: ArrayList<ImageResultJsonFlat>): Boolean {
        var istrue = 0

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.self) {
                    val ocrText = label.ocr

                    for (wordOfOcr in ocrText.split("[.]")) {
                        if (wordOfOcr == "RECORDINGS") {
                            istrue += 1
                        }
                        if (wordOfOcr == "HISTORY") {
                            istrue += 1
                        }
                        if (wordOfOcr == "HONOR SCORE") {
                            istrue += 1
                        }
                        if (istrue >= 2) {
                            return true
                        }
                    }
                }

            }
        }
        return false
    }

    override fun validateCharacterId(
        _input: ArrayList<ImageResultJsonFlat>, originalFFId: String?
    ): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        val charID: String
        val validCharIds = arrayListOf<String>()
        val isSelf = validateSelf(input)
//        val isSelf = true

        if (isSelf) {
            for (i in 0 until input.size) {
                val labels = input[i]
                for (j in 0 until labels.labels.size) {
                    val label = labels.labels[j]
                    if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.profileId) {
                        val ocrText = label.ocr
                        Log.d("LOG", " AAX :ocr:${ocrText}")
                        var ocrCharId = ""
                        var foundLevelOrEnglish = false
                        val words = ocrText.split("[.]")
                        for (i in words.indices) {
                            val wordOfOcr = words[i].trim()
                            val wordOfOcrCaps = wordOfOcr.uppercase()
                            if (foundLevelOrEnglish || wordOfOcr.isEmpty())
                                continue
                            if (LabelUtils.editDistance(wordOfOcrCaps, "ENGLISH") < 2
                                || wordOfOcrCaps.contains("LV.")
                            ) {
                                foundLevelOrEnglish = true
                            } else if (i < 3) {
                                ocrCharId = wordOfOcr
                            }
                        }
                        validCharIds.add(ocrCharId)
                        LabelUtils.testLogGrey(ocrCharId)
                        Log.d("char_id_ocr", ocrCharId)
                    }
                }
            }
        }
        if (validCharIds.size > 0) {
            charID = LabelUtils.findMostCommonInList(validCharIds)!!
            val cnt = validCharIds.count { it == charID }
            if (cnt >= minimumTruePositives["char-id"]!!) {
                result.setCharId(charID)
            }
            LabelUtils.testLogGreen("validateProfileId:  accepted:  $charID with count $cnt")
        }
        val finalResult = result.build()
        if (finalResult.charId != null) {
            result.setAccepted()
        }
        return result.build()
    }

    override fun validateProfileIdAndLevel(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        return MachineResult.Builder().build()
    }

    override fun validateRankRatingGameInfo(
        _input: ArrayList<ImageResultJsonFlat>,
        originalBGMIId: String?,
        originalBGBICharacterID: String?,
        isFromAutoMl: Boolean
    ): MachineResult {

        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()

        val initialTier: String
        val finalTier: String

        val validRatings = arrayListOf<String>()
        if (BuildConfig.DEBUG || isFromAutoMl || Logger.loggingFlags.optBoolean("label_ocr_rank_kill")) com.gamerboard.logger.log {
            it.setMessage("validateRankRatingGameInfo")
            it.addContext("input", _input)
            it.addContext("is_ml", isFromAutoMl)
        }
        LabelUtils.testLogRed("validateRatingChange: Rating Inputs  ${input.joinToString { it.labels[0].ocr + ", " }}")

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.rating) {
                    val ocrText = label.ocr

                    val (acceptedGroup, ratingGroup) = getRatingGroup(ocrText)
                    var finalAcceptedLevel = "None"


                    if (acceptedGroup && immutableListOf(
                            FfMaxLevels.MASTER.rawValue.lowercase(),
                            FfMaxLevels.HEROIC.rawValue.lowercase()
                        ).contains(ratingGroup.lowercase())
                    ) {
                        finalAcceptedLevel = ratingGroup
                        validRatings.add(finalAcceptedLevel)
                        continue
                    }

                    if (acceptedGroup && ratingGroup == "Conqueror") {
                        validRatings.add(ratingGroup)
                        continue
                    }

                    for (line in ocrText.split(rankRatingSplitterRegex)) {
                        val (acceptLevel, ocr) = getRatingLevel(line)
                        if (acceptLevel && acceptedGroup) finalAcceptedLevel = "$ratingGroup $ocr"
                        LabelUtils.testLogGrey("validateRatingChange:  truePositive: $ocr  from ${label.ocr}")
                    }
                    if (finalAcceptedLevel != "None") validRatings.add(finalAcceptedLevel)
                }
            }
        }

        if (validRatings.size > 0) {
            initialTier = LabelUtils.findMostCommonInList(arrayListOf(validRatings[0]))!!
            finalTier =
                LabelUtils.findMostCommonInList(arrayListOf(validRatings[validRatings.size - 1]))!!

            val initialCnt = validRatings.count { it == initialTier }
            if (initialCnt >= minimumTruePositives["initial-tier"]!!) {
                result.setAccepted()
                result.setInitialTier(initialTier)
            }

            val finalCnt = validRatings.count { it == finalTier }
            if (finalCnt >= minimumTruePositives["final-tier"]!!) {
                result.setAccepted()
                result.setFinalTier(finalTier)
            }

            LabelUtils.testLogGreen("validateRatingChange:  accepted:  $initialTier with count $initialCnt, $finalTier with count $initialCnt")
        }

        val finalResult = result.build()
        if (finalResult.gameInfo != null) result.setAccepted()

        LabelUtils.testLogGrey("validateRatingChange:  accept: ${finalResult.accept}, truePositive: ${finalResult.id}")

        return result.build()

    }

    private fun sendTierForAutoMLHelper(
        result: MachineResult.Builder, validInputOcr: ArrayList<String>
    ): Boolean {
        if (validInputOcr.size <= 3) return true
        if (result.initialTier == null || result.initialTier == StateMachineStringConstants.UNKNOWN || result.initialTier == "None") return true
        if (validInputOcr.alternateCount() >= 7) // number of times the value changes
            return true
        return false
    }


    private fun getRatingLevel(ocrText: String): Pair<Boolean, String> {
        LabelUtils.testLogGrey("validateRatingChange:  truePositive: word $ocrText")
        val levels = arrayListOf(
            "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII"
        )

        for (level in levels) {
            if (level == ocrText.correctRomans()) {
                return Pair(true, level)
            }
        }
        return Pair(false, "None")
    }


    private fun getRatingGroup(
        rating: String, groups: ArrayList<Pair<String, Int>> = tierGroups
    ): Pair<Boolean, String> {

        for (group in groups) {
            for (line in rating.split("[.]")) {
                for (wordOfOcr in LabelUtils.splitByChars(line, rantingSplitterRegex)) {
                    if (LabelUtils.editDistance(group.first, wordOfOcr) <= group.second) {
                        return Pair(true, group.first)
                    }
                }
            }
        }
        return Pair(false, "Un-Known")
    }


    @Serializable
    data class UserKillData(
        val username: String, val kills: String
    )

    private val tierGroups = arrayListOf(
        "Bronze" to 2,
        "Silver" to 3,
        "Gold" to 1,
        "Platinum" to 3,
        "Diamond" to 3,
        "Heroic" to 2,
        "Grand Master" to 1,
//        "Conqueror" to 3
    )

    private val aceSuffix = arrayListOf(
        "Master" to 2,
        "Dominator" to 3,
    )

    override fun validateRankKillGameInfo(
        _input: ArrayList<ImageResultJsonFlat>,
        originalBGMIId: String?,
        originalBGBICharacterID: String?,
        isFromAutoMl: Boolean
    ): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        val rank: String
        var sendForAutoMl = false

        if (BuildConfig.DEBUG || isFromAutoMl || Logger.loggingFlags.optBoolean("label_ocr_rank_kill")) com.gamerboard.logger.log {
            it.setMessage("validatePerformanceScreen")
            it.addContext("input", _input)
            it.addContext("is_ml", isFromAutoMl)
        }
        val validKills = arrayListOf<Pair<String, String>>()
        findKillsFromPerformanceScreen(input, validKills)
        var isTeamMatch = false

        val (checkClassic, matchLimitClassic) = Pair("classic", 4)
        val gameInfo = buildGameInfoFromInput(input)

        val validRanks = arrayListOf<Pair<String, String>>()

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.rank) {
                    if ((!label.ocr.contains('/'))) continue
                    // this is to filter and ignore un wanted strings
                    if (label.ocr.length > 45) continue
                    if (label.ocr.split("[.]").size > 4) continue

                    // split and process line by line of ocr output
                    for (ocrText in label.ocr.split("[.]")) {
                        if (ocrText.correctDigitOcr().digitsOnly().isEmpty()) continue
                        if ((!ocrText.contains('#'))) continue
                        if (getRatingGroup(ocrText).first) break

                        var word = ""
                        for (c in ocrText) {
                            if ((c in arrayListOf('/', ' ')) && word.isNumber()) break
                            if (c in arrayListOf(' ', '#', '"', '*')) continue
                            if (c in arrayListOf('\\')) {
                                if (word.isEmpty()) continue else break
                            }
                            word += "$c".correctDigitOcr()
                        }
                        word = if (word.toIntOrNull() != null) "${word.toInt()}" else ""
                        if (word.isNotEmpty() && word.toInt() <= 100) {
                            validRanks.add(Pair(word, word))
                            LabelUtils.testLogGrey("validateGameResult:  truePositive rank: $word  from ${label.ocr}")
                            break
                        }
                    }
                }
//                else if (TODO()) {
//                    if ((!label.ocr.contains('/'))) continue
//                    // this is to filter and ignore un wanted strings
//                    if (label.ocr.length > 45) continue
//                    if (label.ocr.split("[.]").size > 4) continue
//
//
//                    val ocrText = label.ocr
//                    for (line in ocrText.split("[.]")) {
//                        for (wordOfOcr in LabelUtils.splitByChars(
//                            line, Regex(":|#+|%+|\\s+|F")
//                        )) {
//                            if ((LabelUtils.editDistance(
//                                    wordOfOcr.alphabetsOnly(), "details"
//                                ) <= 3)
//                            ) {
//                                isTeamMatch = true
//                            } else continue
//                        }
//                    }
//                }
            }
        }

        if (!isTeamMatch) {
            gameInfo.group = "solo"
        } else {
            gameInfo.group = "squad"
        }

        result.setGameInfo(gameInfo)


        if (validRanks.size > 0) {
            rank = LabelUtils.findMostCommonInListWithConflict(validRanks)!!
            val cnt = validRanks.count { it.first == rank }/* if (cnt >= minimumTruePositives["rank"]!!) {
                 result.setRank(rank)
             }*/
            var parsedRank = rank.toIntOrNull()
            if (parsedRank != null) {
                if (parsedRank > 100) {
                    parsedRank %= 10
                }
                if (isTeamMatch) result.setTeamRank(parsedRank.toString())
                result.setRank(parsedRank.toString())
            }
            LabelUtils.testLogGreen("validateRank:  accepted Rank:  $rank with count $cnt")

        }
        val kills: String
        if (validKills.size > 0) {
            kills = LabelUtils.findMostCommonInListWithConflict(validKills)!!
            val cnt = validKills.count { it.first == kills }
            if (cnt >= minimumTruePositives["kill"]!!) result.setKill(kills)
        }


        // For accept we must have Kills, Game Info and either Rank or Team Rank
        val finalResult = result.build()
        val accept =
            (finalResult.kill != null || finalResult.gameInfo != null || finalResult.rank != null)
        if (accept) result.setAccepted()
        return result.build()
    }

//    override fun validatePerformanceScreen(
//        _input: ArrayList<ImageResultJsonFlat>,
//        originalBGMIId: String?,
//        originalBGBICharacterID: String?,
//        isFromAutoMl: Boolean
//    ): MachineResult {
//        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
//        input.addAll(_input)
//        val result = MachineResult.Builder()
//        val rank: String
//        var sendForAutoMl = false
//        if (BuildConfig.DEBUG || isFromAutoMl || Logger.loggingFlags.optBoolean("label_ocr_rank_kill")) MachineMessageBroadcaster.invoke()
//            ?.logToFile("validatePerformanceScreen: ${Gson().toJson(_input)}")
//        val validKills = arrayListOf<Pair<String, String>>()
//        findKillsFromPerformanceScreen(input, validKills)
//        var isTeamMatch = false
//
//        val (checkClassic, matchLimitClassic) = Pair("classic", 4)
//        val gameInfo = buildGameInfoFromInput(input)
//
//        val validRanks = arrayListOf<Pair<String, String>>()
//
//        for (i in 0 until input.size) {
//            val labels = input[i]
//            for (j in 0 until labels.labels.size) {
//                val label = labels.labels[j]
//                if (label.label == FreeFireConstants.GameLabels.BR_RANK.ordinal) {
//                    if ((!label.ocr.contains('/'))) continue
//                    // this is to filter and ignore un wanted strings
//                    if (label.ocr.length > 45) continue
//                    if (label.ocr.split("[.]").size > 4) continue
//
//                    // split and process line by line of ocr output
//                    for (ocrText in label.ocr.split("[.]")) {
//                        if (ocrText.correctDigitOcr().digitsOnly().isEmpty()) continue
//                        if ((!ocrText.contains('#'))) continue
//                        if (getRatingGroup(ocrText).first) break
//
//                        var word = ""
//                        for (c in ocrText) {
//                            if ((c in arrayListOf('/', ' ')) && word.isNumber()) break
//                            if (c in arrayListOf(' ', '#', '"', '*')) continue
//                            if (c in arrayListOf('\\')) {
//                                if (word.isEmpty()) continue else break
//                            }
//                            word += "$c".correctDigitOcr()
//                        }
//                        word = if (word.toIntOrNull() != null) "${word.toInt()}" else ""
//                        if (word.isNotEmpty() && word.toInt() <= 100) {
//                            validRanks.add(Pair(word, word))
//                            LabelUtils.testLogGrey("validateGameResult:  truePositive rank: $word  from ${label.ocr}")
//                            break
//                        }
//                    }
//                } else if (label.label == FreeFireConstants.GameLabels.BR_IS_SQUAD.ordinal) {
//                    if ((!label.ocr.contains('/'))) continue
//                    // this is to filter and ignore un wanted strings
//                    if (label.ocr.length > 45) continue
//                    if (label.ocr.split("[.]").size > 4) continue
//
//
//                    val ocrText = label.ocr
//                    for (line in ocrText.split("[.]")) {
//                        for (wordOfOcr in LabelUtils.splitByChars(
//                            line, Regex(":|#+|%+|\\s+|F")
//                        )) {
//                            if ((LabelUtils.editDistance(
//                                    wordOfOcr.alphabetsOnly(), "details"
//                                ) <= 3)
//                            ) {
//                                isTeamMatch = true
//                            } else continue
//                        }
//                    }
//                }
//            }
//        }
//
//        if (!isTeamMatch) {
//            gameInfo.group = "solo"
//        } else {
//            gameInfo.group = "squad"
//        }
//
//        result.setGameInfo(gameInfo)
//
//
//        if (validRanks.size > 0) {
//            rank = LabelUtils.findMostCommonInListWithConflict(validRanks)!!
//            val cnt = validRanks.count { it.first == rank }/* if (cnt >= minimumTruePositives["rank"]!!) {
//                 result.setRank(rank)
//             }*/
//            var parsedRank = rank.toIntOrNull()
//            if (parsedRank != null) {
//                if (parsedRank > 100) {
//                    parsedRank %= 10
//                }
//                if (isTeamMatch) result.setTeamRank(parsedRank.toString())
//                result.setRank(parsedRank.toString())
//            }
//            LabelUtils.testLogGreen("validateRank:  accepted Rank:  $rank with count $cnt")
//
//        }
//        val kills: String
//        if (validKills.size > 0) {
//            kills = LabelUtils.findMostCommonInListWithConflict(validKills)!!
//            val cnt = validKills.count { it.first == kills }
//            if (cnt >= minimumTruePositives["kill"]!!) result.setKill(kills)
//        }
//
//
//
//        // For accept we must have Kills, Game Info and either Rank or Team Rank
//        val finalResult = result.build()
//        val accept =
//            (finalResult.kill != null || finalResult.gameInfo != null || finalResult.rank != null)
//        if (accept) result.setAccepted()
//        return result.build()
//    }


   /* private fun findKillsInTeam(
        input: ArrayList<ImageResultJsonFlat>,
        validSquadKills: TreeMap<Int, HashMap<String, Pair<Int, Int>>>
    ): JsonArray? {
        try {
            for (i in 0 until input.size) {
                val labels = input[i]
                val squadScoringJsonArray = JsonArray()
                for (j in 0 until labels.labels.size) {
                    val label = labels.labels[j]
                    if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.ffTempKill) {
                        val ocrText = label.ocr

                        var recordForSquad = false

                        //remove noise from ocr where
                        var ocrTextBreakDown =
                            ocrText.split("[.]").filter { it.isNotEmpty() }
                        ocrTextBreakDown = ocrTextBreakDown.sortedByDescending { it.length }
                        var numberCount = 0
                        for (wordOfOcr in ocrTextBreakDown) {
                            var word = ""
                            for (c in wordOfOcr) {
                                if (c in arrayListOf(
                                        ' ', '#', ':', '"', '\\', '/', '\"'
                                    )
                                ) continue
                                word += "$c".correctDigitOcr()
                            }
                            if (word.toIntOrNull() != null)
                                numberCount++
                        }
                        if (ocrTextBreakDown.isNotEmpty() && numberCount == 2) {
                            val squadMember = JsonObject()
                            var isKillZero = false
                            for (line in ocrTextBreakDown.sortedByDescending { it.length }) {
                                if (line.isNotEmpty()) for (wordOfOcr in LabelUtils.splitByChars(
                                    line, Regex(":|#+|%+|\\s+|F")
                                )) {
                                    val matchesFinishes =
                                        MachineConstants.machineLabelUtils.containsKills(
                                            wordOfOcr
                                        )

                                    if (recordForSquad || matchesFinishes) {
                                        // start recording for the finishes digit after the match.
                                        if (!recordForSquad) {
                                            recordForSquad = true; continue
                                        } else if (matchesFinishes) continue
                                    } else continue

                                    var word = ""
                                    for (c in wordOfOcr) {
                                        if (c in arrayListOf(
                                                ' ', '#', ':', '"', '\\', '/', '\"'
                                            )
                                        ) continue
                                        word += "$c".correctDigitOcr()
                                    }
                                    var wordAsInt = word.toIntOrNull()
                                    if (wordAsInt != null && wordAsInt <= 40 && !isKillZero) {
                                        if (wordAsInt > 30) wordAsInt %= 10
                                        if (wordAsInt == 0) {
                                            isKillZero = true
                                        }
                                        squadMember.add("kills", JsonPrimitive(wordAsInt))
                                        break
                                    }
//                                        break
                                }
                            }

                            var finishesIndex = -1
                            val wordList = mutableListOf<String>()
                            ocrTextBreakDown.forEach {
                                wordList.addAll(it.split(" ").filter { s -> s.isNotEmpty() })
                            }
                            wordList.withIndex().forEach {
                                if (MachineConstants.machineLabelUtils.containsKills(it.value.alphabetsOnly())) finishesIndex =
                                    it.index
                            }
                            var username = ""
                            for (word in wordList.withIndex()) {
                                // if this is the last position where we have finishes..break from here
                                if (word.index == finishesIndex) break
                                if (word.value.uppercase()
                                        .contains("ELIMINATIONS") || word.value.uppercase()
                                        .contains("DAMAGE") || word.value.isNumber()
                                ) continue
                                username += "${word.value} "
                            }
//                                var obj: String
                            //for cases "[.]jarvisFriday[.]finishes 3" when the username is at the 0 array index
                            // and finishes in t[he 1]
                            if (finishesIndex == 0 && username.isEmpty()) username =
                                ocrTextBreakDown[0]*//*   }
                           }*//*

                            squadMember.add("username", JsonPrimitive(username.trim()))
                            squadMember.add(
                                "x", JsonPrimitive((label.box[1] * label.resolution.width).toInt())
                            )
//                                }
                            if (squadMember["kills"] == null) squadMember.add(
                                "kills", JsonPrimitive(-1)
                            )*//* try {
                                 squadMember.get("kills")
                             } catch (e: Exception) {
                                 squadMember.add("kills", JsonPrimitive(-1))
                             }*//*
                            squadScoringJsonArray.add(squadMember)
                        }
                    }
                }

                squadScoringJsonArray.forEach { squadJson ->
                    val jObj = squadJson.asJsonObject

                    //to not count empty username strings in case of complicated username values where recurrence is unlikely
                    val squadKill =
                        jObj["kills"]?.asInt // kills might be null when there is no numeric value after `finishes`
                    if (squadKill != -1) {
                        val xCoordinate = jObj["x"]?.asInt
                        xCoordinate?.let {
                            val nearest = findNearestCoordinate(it, validSquadKills.keys)
                            squadKill?.let { kill ->
                                val squadKillKey = "${jObj["username"].asString}[.]$squadKill"
                                val map: HashMap<String, Pair<Int, Int>>
                                if (nearest == -1 || validSquadKills[nearest].isNullOrEmpty()) {
                                    map = HashMap()
                                    validSquadKills[it] = map
                                } else {
                                    map = validSquadKills[nearest]!!
                                }
                                map.putIfAbsent(squadKillKey, Pair(0, kill))
                                val squadKillCounter = map[squadKillKey]!!.first + 1
                                map[squadKillKey] = Pair(squadKillCounter, kill)
                            }
                        }
                    }
                }
            }
            val finalSquadScoringArray = JsonArray()
            validSquadKills.entries.forEach { userKills ->
                val killsHashMap = userKills.value
                if (killsHashMap.entries.isNotEmpty()) {
                    val squadMember = JsonObject()
                    var maxUserKillsCount = 0
                    killsHashMap.entries.forEach {
                        if (it.value.first > maxUserKillsCount) {
                            maxUserKillsCount = it.value.first
                            squadMember.addProperty("username", it.key.split("[.]")[0])
                            squadMember.addProperty("kills", it.value.second)
                        }
                    }
                    finalSquadScoringArray.add(squadMember)
                }
            }

            return finalSquadScoringArray

        } catch (e: Exception) {
            logToFile(
                "Error while fetching score for squad: message ${e.message}, ${input.joinToString { it.toString() }}",
                LogCategory.ENGINE
            )
            logException(e)
        }
        return null
    }*/

    fun stringToArray(stringRepresentation: String): Array<String> {
        val valueString = stringRepresentation.substring(1, stringRepresentation.length - 1)

        // Splitting the string by comma and converting the resulting strings to integers
        return valueString.split(",")
            .map { it.trim() }
            .toTypedArray()

    }

    fun findKillsFromPerformanceScreen(
        input: ArrayList<ImageResultJsonFlat>, validKills: ArrayList<Pair<String, String>>
    ) {
        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.kill) {
                    var record = false
                    var nextKill = false
                    var foundKill = false
                    val ocrText = label.ocr
                    val ocrValues = ocrText.split("[..]")
                    if (ocrValues.size < 2 || ocrText == "[..]") {
                        continue
                    }
                    val usernames = stringToArray(ocrValues[0])
                    val kills = stringToArray(ocrValues[1])
                    kills.forEach {
                        if (it.isNumber() && it.toInt() != -1 && it.toInt() < 30) {
                            validKills.add(Pair(it, it))
                            foundKill = true
                        }
                    }

                    // We received the label but couldn't fetch the correct ocr
//                    if (validKills.size == 0) {
//                        validKills.add(Pair("N", "kill_label_empty"))
//                    }
                }
            }
        }

    }

    private fun findNearestCoordinate(x: Int, keys: MutableSet<Int>): Int {
        var key = -1
        keys.forEach {
            if (key == -1 && abs(it - x) < 100) key = it
        }
        return key
    }

  /*  private fun findKillInSolo(
        input: ArrayList<ImageResultJsonFlat>, validKills: ArrayList<Pair<String, String>>
    ) {
        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.ffTempKill) {
                    var record = false
                    var foundKill = false
                    val ocrText = label.ocr
                    for (line in ocrText.split("[.]")) {
                        for (wordOfOcr in LabelUtils.splitByChars(
                            line, Regex(":|#+|%+|\\s+|F")
                        )) {
                            if (record || MachineConstants.machineLabelUtils.containsKills(wordOfOcr.alphabetsOnly())) {
                                if (!record) {
                                    record = true; continue
                                }
                            } else continue

                            // to save image

                            var word = ""
                            for (c in wordOfOcr) {
                                if (c in arrayListOf(' ', '#', ':', '"', '\\', '/', '\"')) continue
                                word += "$c".correctDigitOcr()
                            }
                            word = if (word.toIntOrNull() != null) "${word.toInt()}" else ""

                            if (word.isNotEmpty() && (word.length <= 2) && record) {
                                validKills.add(Pair(word, wordOfOcr))
                                foundKill = true
                                break
                            }
                        }
                        if (foundKill) break
                    }
                    // We received the label but couldn't fetch the correct ocr
                    if (validKills.size == 0) {
                        validKills.add(Pair("N", "kill_label_empty"))
                    }
                }
            }
        }
    }*/


    private fun buildGameInfoFromInput(input: ArrayList<ImageResultJsonFlat>): GameInfo {
        val validTypes = arrayListOf<String>()
        val validModes = arrayListOf<String>()
        val validGroups = arrayListOf<String>()
        val validMaps = arrayListOf<String>()

//        var group = "squad"
        val mode = "TPP"
        var numberOfKillLabel = 0

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.gameInfo) {
                    val ocrText = label.ocr
                    for (wordOfOcr in LabelUtils.splitByChars(
                        ocrText, Regex(":|#+|%+|\\s+")
                    )) {
                        val (type, map) = validateGameInfo(ocrText)

                        if (type.isNotEmpty()) validTypes.add(type)
//
                        if (mode.isNotEmpty()) validModes.add(mode)
//
//                        if (group.isNotEmpty())
//                            validGroups.add(group)

                        if (map.isNotEmpty()) validMaps.add(map)

                        break
                    }
                }
            }
        }

//        if (group.isNotEmpty())
//            validGroups.add(group)
        validTypesCache.addAll(validTypes)
        validModesCache.addAll(validModes)
        validGroupsCache.addAll(validGroups)
        validMapsCache.addAll(validMaps)

        val maxType =
            LabelUtils.findMostCommonInList(validTypesCache) ?: StateMachineStringConstants.UNKNOWN
        val maxMode =
            LabelUtils.findMostCommonInList(validModesCache) ?: StateMachineStringConstants.UNKNOWN
        val maxGroup =
            LabelUtils.findMostCommonInList(validGroupsCache) ?: StateMachineStringConstants.UNKNOWN
        val maxMap =
            LabelUtils.findMostCommonInList(validMapsCache) ?: StateMachineStringConstants.UNKNOWN

        return GameInfo(
            type = maxType, view = maxMode, group = maxGroup, mode = maxMap
        )
    }

    override fun sendForAutoMLHelper(
        result: MachineResult.Builder, validInputOcr: ArrayList<Pair<String, String>>
    ): Boolean {
        if (validInputOcr.size == 0) return false


        val numLabels = validInputOcr.size

        result.setFramesReceived(numLabels)

        if (numLabels <= 3) {
            LabelUtils.testLogGrey("label less than 4")
            result.setHasLowConfidence(true)
            return true
        }

        var mostFrequent = Int.MIN_VALUE
        var secondMostFrequent = Int.MIN_VALUE
        var mostFrequentElement = String()
        var singleDigit = 0
        var doubleDigit = 0

        val freq = validInputOcr.groupBy { it.first }

        for (f in freq) {

            val len = f.value.size

            if (f.key.length == 1) {
                singleDigit += len
            } else if (f.key.length == 2) {
                doubleDigit += len
            }

            if (len > mostFrequent) {
                secondMostFrequent = mostFrequent
                mostFrequent = len

                mostFrequentElement = f.key
            } else if (len > secondMostFrequent) {
                secondMostFrequent = len
            }
        }

        val freqDiff =
            mostFrequent - (if (secondMostFrequent != Int.MIN_VALUE) secondMostFrequent else 0)

        if (numLabels <= 5 && freqDiff <= 1) {
            LabelUtils.testLogGrey("lebel less than 6 and frequency difference less than 2")
            result.setNoClearMajority(true)
            return true
        }

        if (freqDiff <= 2) {
            LabelUtils.testLogGrey("frequency difference less than 3")
            result.setNoClearMajority(true)
            return true
        }

        if (mostFrequentElement.toIntOrNull() == null) {
            result.setNoClearMajority(true)
            LabelUtils.testLogGrey("not integer case")
            return true
        }


        val ratio = (doubleDigit.toDouble() / (doubleDigit + singleDigit))
        LabelUtils.testLogGrey("$ratio")

        if ((doubleDigit.toDouble() / (doubleDigit + singleDigit)) > 0.25 && doubleDigit < singleDigit) {
            result.setNoClearMajority(true)
            LabelUtils.testLogGrey("double digit case")
            return true
        }

        LabelUtils.testLogGrey(" Pass ")
        result.setHasLowConfidence(false)
        result.setNoClearMajority(false)
        return false
    }

    override fun compareUserId(ocrText: String, originalGameCharacterID: String?): Boolean {
        if (originalGameCharacterID == null) return false
        for (line in ocrText.split("[.]")) {
            if (LabelUtils.editDistance(line, originalGameCharacterID) <= 3) {
                return true
            }
        }
        return false
    }


    override fun validateGameInfo(gameInfo: String): Array<String> {

        val gameType = arrayListOf("BR RANKED", "BATTLE ROYALE")
        val gameGroup = arrayListOf("solo", "duo", "squad")
        val gameMode = arrayListOf("TPP", "FPP")
        val mapTypes = arrayListOf(
            "BERMUDA", "ALPINE", "PURGATORY", "NEXTERRA", "KALAHARI", "BERMUDA REMASTERED"
        )

        var type = ""
        var group = ""
        var mode = ""
        var map = ""

        val splitList =
            LabelUtils.splitByChars(gameInfo, by = Regex("\\[\\.\\]|:+|#+|%+|-+|\\(|\\)|\\s+"))
                .toMutableList()
        val used = arrayListOf<Int>()
        for ((i, word) in splitList.withIndex()) {

            if (type == "") for (gType in gameType) {
                if (word.length > gType.length / 2 && LabelUtils.editDistance(
                        gType, word
                    ) <= 3
                ) {
                    type = gType
                    used.add(i)
                    break
                }
            }

            if (group == "") for (gGroup in gameGroup) {
                if (word.length > gGroup.length / 2 && LabelUtils.editDistance(
                        gGroup, word
                    ) <= 2
                ) {
                    group = gGroup
                    used.add(i)
                    break
                }
            }

            if (mode == "") for (gMode in gameMode) {
                if (word.length > gMode.length / 2 && LabelUtils.editDistance(
                        gMode, word
                    ) <= 2
                ) {
                    mode = gMode
                    used.add(i)
                    break
                }
            }

            if (map == "") for (mType in mapTypes) {
                if (word.length > mType.length / 2 && LabelUtils.editDistance(
                        mType, word
                    ) <= 2
                ) {
                    map = mType
                    used.add(i)
                    break
                }
            }

            /*if (!used.contains(i) && word.trimSpace().alphaNumericOnly().length > 2)
                name += word.trimSpace().alphaNumericOnly()*/
        }

        LabelUtils.testLogGreen("GameInfo: $type-$mode-$group :$map")
        return arrayOf(type, map)
    }

    private fun containsWordsUsingRegex(input: String, words: Set<String>): Boolean {
        val pattern = "\\b(${words.joinToString("|")})\\b".toRegex()
        return pattern.containsMatchIn(input)
    }

    override fun validateLogin(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        val validLogins = arrayListOf<String>()

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.login) {
                    val ocrText = label.ocr.split("[.]").joinToString { it }
                    if ((ocrText.length > 3) &&
                        containsWordsUsingRegex(ocrText, setOf("Guest", "Sign", "Facebook"))
                    ) {
                        validLogins.add(ocrText)
                        break
                    }
                }
            }
        }

        if (validLogins.size >= minimumTruePositives["login"]!!) {
            result.setLogin()
            result.setAccepted()
        }

        val finalResult = result.build()
        Log.d("MachineInputValidator", "accept: ${finalResult.accept} Result: $finalResult")
        return finalResult
    }

    override fun validateWaiting(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        TODO("Not yet implemented")
    }

    override fun validateProfileLevel(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        return MachineResult.Builder().build()
    }

    override fun validateGameHistory(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        TODO("Not yet implemented")
    }

    override fun getTeamRankFromHistory(history: String): MachineResult {
        TODO("Not yet implemented")
    }

    override fun getRankFromHistory(history: String): MachineResult {
        TODO("Not yet implemented")
    }

    override fun getPlayersDefeatedFromHistory(history: String): MachineResult {
        TODO("Not yet implemented")
    }

    override fun getGameInfoFromHistory(history: String): MachineResult {
        TODO("Not yet implemented")
    }

    override fun getGameTimeStampFromHistory(history: String): Array<String> {
        TODO("Not yet implemented")
    }

    override fun validateStart(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        for (checkString in arrayOf("start", "cancel", "matching")) {

            for (i in 0 until input.size) {
                val labels = input[i]
                for (j in 0 until labels.labels.size) {
                    val label = labels.labels[j]
                    // This was removed to validate the home screen label with just the labels.

                    if (MachineConstants.gameConstants.homeScreenBucket().contains(label.label)) {
                        //if (label.label == GameLabels.START.ordinal) {
                        result.setStart()
                        result.setAccepted()
//                    val ocrText = label.ocr.alphabetsOnly()
//                    if (LabelUtils.editDistance(check, ocrText) <= matchLimit) {
//                        validStart.add(ocrText)
//                        LabelUtils.testLogGrey("validateStart:  truePositive: $ocrText")
//                        break
//                    }
                    }
                }
            }

//            if (validStart.size > 0) {
//                if (validStart.size >= minimumTruePositives["start"]!!) {
//                    result.setStart()
//                    result.setAccepted()
//                }
//            }
        }
        LabelUtils.testLogGreen("validateInGame:  accept: ${result.accept}, truePositive: ${result.start} ")
        return result.build()
    }

    override fun validateInGameWithNoOCR(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val validInGames = arrayListOf<String>()

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == BGMIInputValidator.LabelConstants.ingame) {
                    val ocrText = label.ocr.alphabetsOnly()
                    validInGames.add(ocrText)
                    LabelUtils.testLogGrey("validateInGame:  truePositive: $ocrText")
                    break
                }
            }
        }

        val result = MachineResult.Builder()
        if (validInGames.size > 0) {
            if (validInGames.size >= minimumTruePositives["in-game"]!!) {
                result.setInGame()
                result.setAccepted()
            }
        }
        LabelUtils.testLogGreen("validateInGame:  accept: ${result.accept}, truePositive: ${result.inGame} ")
        return result.build()
    }

    override fun validateGameEnd(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        Log.d("GAME_END", gson.toJson(_input))
        var foundGameEnd = false
        for (resultJson in _input) {
            resultJson.labels.forEach {
                if (!foundGameEnd && (it.ocr.lowercase().contains("play again")
                            || it.ocr.lowercase().contains("matching"))
                ) {
                    foundGameEnd = true
                }
            }
        }
        val builder = MachineResult.Builder()
        if (foundGameEnd)
            builder.setAccepted()
        return builder.build()
    }

    override fun validateResultForTeamGames(game: Game): Boolean {
        val gameInfo = MachineConstants.machineLabelUtils.getGameInfo(game.gameInfo!!)
        logToFile("Game at validation: $game")
        if (gameInfo.group == StateMachineStringConstants.UNKNOWN) return true
        if (game.teamRank in arrayListOf(
                null, StateMachineStringConstants.UNKNOWN
            ) || game.teamRank?.matches(RegexPatterns.numbers)?.not() != false
        ) return true

        if (game.rank == StateMachineStringConstants.UNKNOWN || game.rank?.matches(RegexPatterns.numbers)
                ?.not() != false
        ) return true

        if (game.squadScoring != null) {
            var isAnyValueMissing = false
            try {
                val squadPlayersArray = getSquadScoringArray(game.squadScoring!!)
                for (i in 0 until squadPlayersArray.size) {
                    val squadPlayer = squadPlayersArray[i]
                    val playerScore =
                        Gson().fromJson(squadPlayer.toString(), JsonObject::class.java)
                    if (!playerScore.validatePlayerScoring() && !isAnyValueMissing) {
                        isAnyValueMissing = true
                    }
                }
            } catch (e: Exception) {
                logException(e)
            }
            if (isAnyValueMissing) return true
        }


        if (game.initialTier == StateMachineStringConstants.UNKNOWN) return true

        if (game.finalTier == StateMachineStringConstants.UNKNOWN) return true

        if (gameInfo.group == StateMachineStringConstants.UNKNOWN) return true

        if (gameInfo.type == StateMachineStringConstants.UNKNOWN || gameInfo.type != "Classic") return true
        return false

    }


    override fun validateResultForSoloGames(game: Game): Boolean {
        return true
        val gameInfo = MachineConstants.machineLabelUtils.getGameInfo(game.gameInfo!!)
        var kills = game.kills
        if (game.squadScoring != null) {
            kills = updateKillResultFromSquadScoringForSolo(game.squadScoring!!) ?: "-1"
            updateGameInDatabase(
                Game(
                    userId = game.userId,
                    valid = true,
                    rank = game.rank,
                    gameInfo = game.gameInfo,
                    kills = kills,
                    teamRank = null,
                    startTimeStamp = game.startTimeStamp,
                    endTimestamp = game.endTimestamp,
                    gameId = game.gameId,
                    initialTier = game.initialTier,
                    finalTier = game.finalTier,
                    metaInfoJson = game.metaInfoJson,
                    squadScoring = game.squadScoring,
                    synced = 0
                )
            )
//            game.squadScoring = null
        }
        logToFile("Game at validation: $game")
//        return true
        if (gameInfo.group == StateMachineStringConstants.UNKNOWN) return true

        if (game.rank == StateMachineStringConstants.UNKNOWN || game.rank?.matches(RegexPatterns.numbers)
                ?.not() != false
        ) return true

        if (kills == StateMachineStringConstants.UNKNOWN || kills?.matches(RegexPatterns.numbers)
                ?.not() != false
        ) return true

        if (game.initialTier == StateMachineStringConstants.UNKNOWN) return true

        if (game.finalTier == StateMachineStringConstants.UNKNOWN) return true

        if (gameInfo.group == StateMachineStringConstants.UNKNOWN) return true

        if (gameInfo.type == StateMachineStringConstants.UNKNOWN) return true
        return false

    }

    override fun getProcessedRankData(isFromAutoMl: Boolean): String? = null
    override fun compareAndMatchGameUsername(obtained: String?, expected: String?): Boolean {
        return obtained != null && expected != null && LabelUtils.editDistance(
            obtained,
            expected
        ) <= 2
    }

    fun updateKillResultFromSquadScoringForSolo(squadScoring: String): String? {
        try {
            val resultD = getSquadScoringArray(squadScoring)
            if (resultD.isNotEmpty()) {
                val squadPlayer = resultD[0]
                val playerScore = gson.fromJson(squadPlayer.toString(), JsonObject::class.java)

                return playerScore["kills"].toString()

            }
        } catch (e: Exception) {
            logException(e)
        }
        return null
    }
}
