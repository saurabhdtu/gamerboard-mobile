package com.gamerboard.live.gamestatemachine.games.bgmi

import android.util.Log
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.games.LabelUtils.alphabetsOnly
import com.gamerboard.live.gamestatemachine.games.LabelUtils.correctDigitOcr
import com.gamerboard.live.gamestatemachine.games.LabelUtils.correctRomans
import com.gamerboard.live.gamestatemachine.games.LabelUtils.digitsOnly
import com.gamerboard.live.gamestatemachine.games.LabelUtils.lastChars
import com.gamerboard.live.gamestatemachine.games.LabelUtils.removeLeadingZeroes
import com.gamerboard.live.gamestatemachine.games.LabelUtils.trimSpace
import com.gamerboard.live.gamestatemachine.games.LabelUtils.validatePlayerScoring
import com.gamerboard.live.gamestatemachine.games.MachineInputValidator
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineResult
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants
import com.gamerboard.live.gamestatemachine.stateMachine.minimumTruePositives
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.models.db.GameInfo
import com.gamerboard.live.utils.RegexPatterns
import com.gamerboard.live.utils.ignoreException
import com.gamerboard.live.utils.logException
import com.gamerboard.live.utils.logMessage
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.Logger
import com.gamerboard.logger.gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.TreeMap
import kotlin.math.abs

class BGMIInputValidator() : MachineInputValidator() {
    private val profileIdSplitRegex = Regex(":|#+|%+|\\s+")
    private val rankRegex = Regex("((\\s)*\\/(.)*)")
    private val rankHashRegex = Regex("#|\\s")
    private val killSplitterRegex = Regex(":|#+|%+|\\s+|es")
    private val gameInfoSplitterRegex = Regex(":|#+|%+|-+|\\(|\\)|\\s+")
    private val gameInfoFromInputSplitterRegex = Regex(":|#+|%+|\\s+")
    private val ratingSplitterRegex = gameInfoFromInputSplitterRegex
    private val rankSplitter: Regex = "[./:]".toRegex()

    object LabelConstants {
        const val profileId = "PROFILE_ID"
        const val profileLevel = "PROFILE_LEVEL"
        const val classicRankGameInfo = "CLASSIC_RANK_GAME_INFO"
        const val rank = "RANK"
        const val rating = "CLASSIC_RATING"
        const val kill = "CLASSIC_ALL_KILLS"
        const val login = "GLOBAL_LOGIN"
        const val waiting = "CLASSIC_ALL_WAITING"
        const val ingame = "CLASSIC_ALL_GAMEPLAY"
        const val gameInfo = "GAME_INFO"
        const val self = "PROFILE_SELF"
        const val ffTempKill = "KILLS"
    }

    /**
     * Checks and validates the profile id ocr for the [_input]
     */
    override fun validateProfileId(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        val profileID: String
        val validIds = arrayListOf<String>()

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.profileId) {
                    val ocrText = label.ocr
                    Log.d("LOG", " AAX :ocr:${ocrText}")
                    LabelUtils.testLogGrey("validateProfileId:  splitted: ${
                        ocrText.split("[.]").joinToString { "$it, " }
                    }  from ${label.ocr}")

                    for (wordOfOcr in LabelUtils.splitByChars(
                        ocrText.split("[.]").joinToString { "$it " }, profileIdSplitRegex
                    ).reversed()) {
                        Log.d("LOG", " AAX :word:${wordOfOcr}")
                        var ocr = wordOfOcr.correctDigitOcr().digitsOnly()
                        if (ocr.length > 11) {
                            ocr = ocr.lastChars(11)
                        }
                        if (ocr.length >= 9) {
                            validIds.add(ocr)
                            LabelUtils.testLogGrey("validateProfileId:  truePositive: $ocr  from ${label.ocr}")
                            break
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

    override fun validateCharacterId(
        _input: ArrayList<ImageResultJsonFlat>, originalBGMIId: String?
    ): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        val charID: String
        val validCharIds = arrayListOf<String>()

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.profileId) {
                    val ocrText = label.ocr
                    Log.d("LOG", " AAX :ocr:${ocrText}")
                    var ocrCharId = ""
                    for (wordOfOcr in ocrText.split("[.]")) {
                        if (wordOfOcr.trim().isNotEmpty()) if (!originalBGMIId.isNullOrBlank()) {
                            if (LabelUtils.editDistance(
                                    wordOfOcr.replace("ID", "", ignoreCase = true).correctDigitOcr()
                                        .digitsOnly().trim(), originalBGMIId
                                ) <= 3
                            ) {
                                continue
                            }
                            ocrCharId += wordOfOcr.trimSpace()
                            break
                        }
                    }
                    validCharIds.add(ocrCharId)
                    LabelUtils.testLogGrey(ocrCharId)
                    Log.d("char_id_ocr", ocrCharId)
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
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        val profileID: String
        val profileLevel: String
        val validIds = arrayListOf<String>()

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.profileId) {
                    val ocrText = label.ocr
                    Log.d("LOG", " AAX :ocr:${ocrText}")
                    for (wordOfOcr in LabelUtils.splitByChars(
                        ocrText, profileIdSplitRegex
                    )) {
                        Log.d("LOG", " AAX ::word $wordOfOcr")
                        var ocr = wordOfOcr.replace("ID", "", ignoreCase = true).correctDigitOcr()
                            .digitsOnly()
                        if (ocr.length > 11) {
                            ocr = ocr.lastChars(11)
                        }
                        if (ocr.length >= 7) {
                            validIds.add(ocr)
                            LabelUtils.testLogGrey("validateProfileId:  truePositive: $ocr  from ${label.ocr}")
                            break
                        }
                    }
                }
            }
        }


        if (validIds.size > 0) {
            profileID = LabelUtils.findMostCommonInList(validIds)!!
            val cnt = validIds.count { it == profileID }
            if (cnt >= minimumTruePositives["id"]!!) {
                result.setId(profileID)
            }
            LabelUtils.testLogGreen("validateProfileId:  accepted:  $profileID with count $cnt")
        }


        val (check, matchLimit) = Pair("playerlv.", 3)
        val validPlayerLevel = arrayListOf<String>()

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.profileLevel) {
                    var ocrText = label.ocr
                    if (ocrText.correctDigitOcr().digitsOnly(2).isEmpty()) continue
                    LabelUtils.testLogGrey(
                        "validateProfileId:  check:${
                            ocrText.correctDigitOcr().digitsOnly(2)
                        } from $ocrText"
                    )
                    if (LabelUtils.editDistance(
                            ocrText.alphabetsOnly(), check
                        ) <= matchLimit
                    ) {
                        LabelUtils.testLogGrey(
                            "validateProfileId:  check :${
                                ocrText.correctDigitOcr().digitsOnly(2)
                            }"
                        )
                        ocrText = ocrText.correctDigitOcr().digitsOnly(2).removeLeadingZeroes()
                        if (ocrText.length > 2 && ocrText != "100") ocrText = ocrText.lastChars(2)
                        validPlayerLevel.add(ocrText)
                        LabelUtils.testLogGrey("validateProfileId:  truePositive:${ocrText}")
                        break
                    }
                }
            }
        }

        if (validPlayerLevel.size > 0) {
            profileLevel = LabelUtils.findMostCommonInList(validPlayerLevel)!!
            val cnt = validPlayerLevel.count { it == profileLevel }
            if (cnt >= minimumTruePositives["level"]!!) {
                result.setLevel(profileLevel)
            }
            LabelUtils.testLogGreen("validateProfileId:  accepted:  $profileLevel with count $cnt")
        }

        val finalResult = result.build()
        if (finalResult.id != null && finalResult.level != null) {
            result.setAccepted()
        }
        Log.d(
            "MachineInputValidator",
            "accept: ${finalResult.accept}, truePositive: ${finalResult.id}"
        )
        return result.build()
    }

    override fun validateRankRatingGameInfo(
        _input: ArrayList<ImageResultJsonFlat>,
        originalBGMIId: String?,
        originalBGBICharacterID: String?,
        isFromAutoMl: Boolean,
    ): MachineResult {
        if (BuildConfig.DEBUG || isFromAutoMl || Logger.loggingFlags.optBoolean("label_ocr_rank_kill")) com.gamerboard.logger.log {
            it.setMessage("validateRankRatingGameInfo")
            it.addContext("score", _input)
            it.addContext("is_ml", isFromAutoMl)
        }
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        result.setGameInfo(buildGameInfoFromInput(_input))
        /*val validRanks = arrayListOf<Pair<String, String>>()*/

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.classicRankGameInfo || MachineConstants.gameConstants.labelNameFromIndex(
                        label.label
                    ) == LabelConstants.rank
                ) {
                    //if ((!label.ocr.contains("[/:]".toRegex()))) continue
                    // this is to filter and ignore un wanted strings
                    //if (label.ocr.length > 45) continue
                    if (label.ocr.split("[.]").size > 4) continue
                    // split and process line by line of ocr output
                    for (ocrText in label.ocr.split(rankSplitter)) {
                        if (ocrText.correctDigitOcr().digitsOnly().isEmpty()) continue
//                        if ((!ocrText.contains('/')))
//                            continue
                        if (getRatingGroup(ocrText).first) break

                        // to save image
                        log("addingOCRRank: $ocrText")
                        addRankToMap(ocrText, isFromAutoMl)
                    }
                }
            }
        }

        val initialTier: String
        val finalTier: String

        val validRatings = arrayListOf<String>()

        LabelUtils.testLogRed("validateRatingChange: Rating Inputs  ${input.joinToString { it.labels[0].ocr + ", " }}")

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.rating) {
                    val ocrText = label.ocr

                    //To ignore un wanted strings, 45 is line length, the largest length we have is "100 / 111   II   ace dominator VVV"
                    if (label.ocr.split("[.]").size > 6) continue

                    val (acceptedGroup, ratingGroup) = getRatingGroup(ocrText)
                    var finalAcceptedLevel = "None"


                    if (acceptedGroup && ratingGroup == "Ace") {
                        finalAcceptedLevel = ratingGroup

                        val (acceptedGroupSuffix, AceSuffix) = getRatingGroup(ocrText, aceSuffix)
                        if (acceptedGroupSuffix) finalAcceptedLevel =
                            "$finalAcceptedLevel $AceSuffix"

                        if (finalAcceptedLevel != "None") validRatings.add(finalAcceptedLevel)
                        continue
                    }

                    if (acceptedGroup && ratingGroup == "Dominator") {
                        validRatings.add("Ace Dominator")
                    }

                    if (acceptedGroup && ratingGroup == "Master") {
                        validRatings.add("Ace Master")
                    }

                    if (acceptedGroup && ratingGroup == "Conqueror") {
                        validRatings.add(ratingGroup)
                        continue
                    }

                    for (line in ocrText.split("[.]")) {
                        for (wordOfOcr in line.split(RegexPatterns.whiteSpace)) {
                            val (acceptLevel, ocr) = getRatingLevel(wordOfOcr)
                            if (acceptLevel && acceptedGroup) finalAcceptedLevel =
                                "$ratingGroup $ocr"
                            LabelUtils.testLogGrey("validateRatingChange:  truePositive: $ocr  from ${label.ocr}")
                        }
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


    private fun addRankToMap(rank: String, isFromAutoMl: Boolean) {
        var ocrText =
            rank.replace(rankRegex, "")
        ocrText =
            ocrText.replace(rankHashRegex, "")
        var properRank = ""
        ocrText.forEach { c -> properRank = properRank.plus(c.toString().correctDigitOcr()) }
        val word = if (properRank.toIntOrNull() != null) "${properRank.toInt()}" else ""
        if (word.isNotEmpty() && word.toInt() > 0 && word.toInt() <= 100) maintainOCRCounter(
            isFromAutoMl,
            rankOCRCount,
            Pair(word, 1)
        )
    }

    override fun validateRankKillGameInfo(
        _input: ArrayList<ImageResultJsonFlat>,
        originalBGMIId: String?,
        originalBGBICharacterID: String?,
        isFromAutoMl: Boolean,
    ): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()

        if (BuildConfig.DEBUG || isFromAutoMl || Logger.loggingFlags.optBoolean("label_ocr_rank_kill")) com.gamerboard.logger.log {
            it.setMessage("validateRankKillGameInfo")
            it.addContext("score", _input)
            it.addContext("is_ml", isFromAutoMl)
        }
        val foundGameInfo = buildGameInfoFromInput(input)
        result.setGameInfo(foundGameInfo)
        val gameInfo = result.gameInfo ?: foundGameInfo


        val validKills = arrayListOf<Pair<String, String>>()

        if (gameInfo.group == "solo") {
            findKillInSolo(input, validKills)
            processKillsForSolo(isFromAutoMl, validKills, result)
        } else {
            val finalSquadScoringArray = findKillsInTeam(isFromAutoMl, input)
            // if we get kills even for one person, we set the kills and the squad scoring array for
            // the individual person. This happens in cases when i die early in a multiple players when players a
            // are my friends in bgmi or i am playing in squad without auto matching
            try {
                logMessage(finalSquadScoringArray?.toString())
                val finalPlayerCount = finalSquadScoringArray?.fold(0) { count, item ->
                    count + if (item.asJsonObject["username"] != null && item.asJsonObject["kills"] != null && item.asJsonObject["username"].asString.isNotEmpty() && item.asJsonObject["kills"].asInt != -1) 1 else 0
                }
                result.setKill("-1")
                val scoring = finalSquadScoringArray.toString()
                LabelUtils.testLogGrey("SquadScoring >> $scoring")
                //in case of any partial fields

                com.gamerboard.logger.log {
                    it.setMessage("FinalScoring")
                    it.addContext("score", finalSquadScoringArray)
                    it.addContext("is_ml", isFromAutoMl)
                }
                //in case of incomplete data
                if (finalPlayerCount != finalSquadScoringArray?.size() && isFromAutoMl.not()) {

                    com.gamerboard.logger.log {
                        it.setMessage("Squadscoring set as null")
                        it.addContext("player_count", finalPlayerCount)
                        it.addContext("group", gameInfo.group)
                        it.addContext("is_ml", isFromAutoMl)
                    }
                    result.setSquadScoring(null)
                } else result.setSquadScoring(scoring)
            } catch (ex: Exception) {
                logException(ex)
            }
        }

        /*val validRanks = arrayListOf<Pair<String, String>>()*/

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.classicRankGameInfo || MachineConstants.gameConstants.labelNameFromIndex(
                        label.label
                    ) == LabelConstants.rank
                ) {
                   // if ((!label.ocr.contains("[/:]".toRegex()))) continue
                    // this is to filter and ignore un wanted strings
                    //if (label.ocr.length > 45) continue
                    if (label.ocr.split("[.]").size > 4) continue

                    // split and process line by line of ocr output
                    for (ocrText in label.ocr.split(rankSplitter)) {
                        if (ocrText.correctDigitOcr().digitsOnly().isEmpty()) continue
//                        if ((!ocrText.contains('/')))
//                            continue
                        if (getRatingGroup(ocrText).first) break
                        log("addingOCRRank: $ocrText")
                        addRankToMap(ocrText, isFromAutoMl)
                    }
                }

            }
        }

//        getRank(validRanks, isTeamMatch, result)


        // For accept we must have Kills, Game Info and either Rank or Team Rank
        val finalResult = result.build()
        val accept =
            (finalResult.kill != null || finalResult.gameInfo != null || finalResult.rank != null)
        if (accept) result.setAccepted()
//        testLogGreen("validateGameResult:  accepted:${result.accept}  Result: $finalResult")
        return result.build()
    }

    private fun processKillsForSolo(
        isFromAutoMl: Boolean,
        validKills: ArrayList<Pair<String, String>>, result: MachineResult.Builder,
    ) {
        killOCRCount.putIfAbsent(0, HashMap())
        if (validKills.size > 0) {
            maintainOCRCounter(isFromAutoMl, killOCRCount[0]!!, validKills)
            getProminentValue(killOCRCount[0]!!)?.let { kills ->
                log("killOCRCount $killOCRCount")
                log("prominentKills $kills")
                com.gamerboard.logger.log {
                    it.setMessage("process_kills")
                    it.addContext("kill_count", killOCRCount)
                    it.addContext("prominent_kills", kills)
                    it.addContext("is_ml", isFromAutoMl)
                }
                result.setKill(kills)
            }
        }
    }

    private fun processKillsForSquad(
        isFromAutoMl: Boolean,
        validSquadKills: TreeMap<Int, HashMap<String, Pair<String, Int>>>,
    ): JsonArray {
        val finalSquadScoringArray = JsonArray()
        //to clear the current cache if observed count of players change due to missing labels
        if (validSquadKills.size > killOCRCount.size) {
            killOCRCount.clear()
            squadUserNames.clear()
        }
        validSquadKills.entries.forEach { userKills ->
            killOCRCount.putIfAbsent(userKills.key, HashMap<String, Int>())
            squadUserNames.putIfAbsent(userKills.key, HashMap<String, Int>())
            val killsHashMap = userKills.value
            if (killsHashMap.entries.isNotEmpty()) {
                killsHashMap.entries.forEach {
                    maintainOCRCounter(isFromAutoMl, killOCRCount[userKills.key]!!, it.value)
                    val playerName = it.key.split("[.]")[0]
                    maintainOCRCounter(
                        isFromAutoMl,
                        squadUserNames[userKills.key]!!,
                        Pair(playerName, 1)
                    )
                }
            }
        }

        killOCRCount.entries.forEach { player ->
            val squadMember = JsonObject()
            getProminentValueForUserName(squadUserNames[player.key]!!)?.let {
                squadMember.addProperty("username", it)
                squadMember.addProperty("kills", getProminentValue(player.value))
                finalSquadScoringArray.add(squadMember)
            }
        }

        com.gamerboard.logger.log {
            it.setMessage("process_kills")
            it.addContext("kill_ocr_count", killOCRCount)
            it.addContext("squad_user_names", squadUserNames)
            it.addContext("final_squad_scoring_array", finalSquadScoringArray)
            it.addContext("is_ml", isFromAutoMl)
        }

        return finalSquadScoringArray
    }


//    override fun validatePerformanceScreen(
//        _input: ArrayList<ImageResultJsonFlat>,
//        originalBGMIId: String?,
//        originalBGBICharacterID: String?,
//        isFromAutoMl: Boolean,
//    ): MachineResult {
//        val result = MachineResult.Builder()
//        return result.build()
//    }

    private fun findKillInSolo(
        input: ArrayList<ImageResultJsonFlat>, validKills: ArrayList<Pair<String, String>>,
    ) {
        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.kill) {
                    var record = false
                    var foundKill = false
                    val ocrText = label.ocr
                    for (line in ocrText.split("[.]")) {
                        val characters = LabelUtils.splitByChars(
                            line, killSplitterRegex
                        )
                        for (wordOfOcr in characters) {
                            if (record || MachineConstants.machineLabelUtils.containsKills(wordOfOcr)) {
                                if (!record) {
                                    record = true
                                    continue
                                }
                            } else continue

                            // to save image

                            var fixedKills = wordOfOcr
                            if (wordOfOcr.contains("hes")) {
                                fixedKills = wordOfOcr.split("hes").last()
                            }
                            var word = ""
                            for (c in fixedKills) {
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
                    logToFile("kill_label_empty")
                    // We received the label but couldn't fetch the correct ocr
                    /*if (validKills.size == 0) {
                        validKills.add(Pair("N", "kill_label_empty"))
                    }*/
                }
            }
        }
    }

    private fun findKillsInTeam(
        isFromAutoMl: Boolean,
        input: ArrayList<ImageResultJsonFlat>,
    ): JsonArray? {
        val bgmiLabelUtils = MachineConstants.machineLabelUtils as BGMILabelUtils
        val validSquadKills = TreeMap<Int, HashMap<String, Pair<String, Int>>>()
        try {
            for (i in 0 until input.size) {
                val labels = input[i]
                val squadScoringJsonArray = JsonArray()
                for (j in 0 until labels.labels.size) {
                    val label = labels.labels[j]
                    if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.kill) {
                        val ocrText = label.ocr


                        //remove noise from ocr where
                        val ocrTextBreakDown =
                            ocrText.split("[.]").filter { it.length >= 2 }
                        var recordedUserName = false
                        var recordedKill = false
                        try {
                            if (ocrTextBreakDown.size >= 2) {
                                val squadMember = JsonObject()
                                for (i in ocrTextBreakDown.indices) {
                                    if (i > 0 && bgmiLabelUtils.containsKillsAndAssists(
                                            ocrTextBreakDown[i]
                                        ) && recordedKill.not()
                                    ) {
                                        var kill = ""
                                        val morphed = ocrTextBreakDown[i].replace(" ", "")
                                        val startPoint = morphed.indexOf("es") + 2
                                        var endPoint = morphed.indexOfLast { c -> c == 'A' }
                                        if (endPoint == -1) endPoint = morphed.length
                                        if (endPoint > startPoint) {
                                            for (c in morphed.substring(
                                                startPoint,
                                                endPoint
                                            )) {
                                                kill += "$c".correctDigitOcr()
                                            }
                                            var wordAsInt = kill.toIntOrNull()
                                            if (wordAsInt != null && wordAsInt <= 100) {
                                                if (wordAsInt > 30) wordAsInt %= 10
                                                squadMember.add("kills", JsonPrimitive(wordAsInt))
                                                recordedKill = true
                                            }
                                        }
                                    } else if (recordedUserName.not()) {
                                        squadMember.add(
                                            "username",
                                            JsonPrimitive(ocrTextBreakDown[i].trim())
                                        )
                                        recordedUserName = true
                                    }
                                }
                                squadMember.add(
                                    "x",
                                    JsonPrimitive((label.box[1] * label.resolution.width).toInt())
                                )
                                squadScoringJsonArray.add(squadMember)
                            }
                        } catch (ex: Exception) {
                            logToFile(ex.toString(), LogCategory.E)
                            logException(ex)
                        }
                    }
                }

                squadScoringJsonArray.forEach { squadJson ->
                    val jObj = squadJson.asJsonObject

                    //to not count empty username strings in case of complicated username values where recurrence is unlikely
                    val squadKill =
                        jObj["kills"]?.asInt // kills might be null when there is no numeric value after `finishes`
                    val xCoordinate = jObj["x"]?.asInt
                    xCoordinate?.let {
                        val nearest = findNearestCoordinate(it, killOCRCount.keys)
                        squadKill?.let { kill ->
                            val map: HashMap<String, Pair<String, Int>>
                            if (validSquadKills[nearest].isNullOrEmpty()) {
                                map = HashMap()
                                validSquadKills[nearest] = map
                            } else {
                                map = validSquadKills[nearest]!!
                            }
                            val squadKillKey = "${jObj["username"].asString}[.]$nearest"
                            map.putIfAbsent(squadKillKey, Pair(kill.toString(), 0))
                            val squadKillCounter = map[squadKillKey]!!.second + 1
                            map[squadKillKey] = Pair(kill.toString(), squadKillCounter)
                        }
                    }
                }
            }
            return processKillsForSquad(isFromAutoMl, validSquadKills)

        } catch (e: Exception) {
            logToFile(
                "Error while fetching score for squad: message ${e.message}, ${input.joinToString { it.toString() }}",
                LogCategory.ENGINE
            )
            logException(e)
        }
        return null
    }

    private fun findNearestCoordinate(x: Int, keys: MutableSet<Int>): Int {
        var key = -1
        keys.forEach {
            if (key == -1 && abs(it - x) < 100) key = it
        }
        return if (key == -1) x else key
    }

    override fun sendForAutoMLHelper(
        result: MachineResult.Builder, validInputOcr: ArrayList<Pair<String, String>>,
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

    override fun compareUserId(ocrText: String, originalGameCharId: String?): Boolean {
        if (originalGameCharId == null) return false
        for (line in ocrText.split("[.]")) {
            if (LabelUtils.editDistance(line, originalGameCharId) <= 3) {
                return true
            }
        }
        return false
    }

    override fun validateGameInfo(gameInfo: String): Array<String> {

        val gameType = arrayListOf("Classic", "Deathmatch", "Practice")
        val gameGroup = arrayListOf("solo", "duo", "squad")
        val gameMode = arrayListOf("TPP", "FPP")
        val mapTypes =
            arrayListOf("Erangel", "Livik", "Sanhok", "Karakin", "Vikendi", "Miramar", "Nusa")

        var type = ""
        var group = ""
        var mode = ""
        var map = ""

        val splitList =
            LabelUtils.splitByChars(gameInfo, by = gameInfoSplitterRegex).toMutableList()
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

            if (group == "") {
                val newWord = word.replace("a", "o")//sauad => souod matches with solo first
                for (gGroup in gameGroup) {
                    if (newWord.length >= 3
                        && newWord.length == gGroup.length
                        && LabelUtils.editDistance(gGroup, newWord) <= 2
                    ) {
                        group = gGroup
                        used.add(i)
                        break
                    }
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
        }

        LabelUtils.testLogGreen("GameInfo: $type-$mode-$group :$map")
        return arrayOf(type, mode, group, map)
    }


    fun checkStringsInArrayWithRegex(
        arr1: Array<String>,
        arr2: Array<String>,
        targetString: String
    ): Boolean {
        val pattern1 = buildRegexPattern(arr1)
        val pattern2 = buildRegexPattern(arr2)
        return pattern1.toRegex().containsMatchIn(targetString) && pattern2.toRegex()
            .containsMatchIn(targetString)
    }

    private fun buildRegexPattern(arr: Array<String>): String {
        val escapedStrings = arr.map { Regex.escape(it) }
        return escapedStrings.joinToString("|")
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
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.login) {
                    val ocrText = label.ocr.split("[.]").joinToString { it }
                    if ((ocrText.length > 3) && checkStringsInArrayWithRegex(
                            arrayOf("google", "facebook", "twitter", "play"),
                            arrayOf("login", "log in", "signin", "sign in"),
                            ocrText.toLowerCase()
                        )
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
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        val (check, matchLimit) = Pair("matchstartsinseconds", 11)
        val validWaiting = arrayListOf<String>()

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.waiting) {
                    val ocrText = label.ocr
                    if (LabelUtils.editDistance(check, ocrText) <= matchLimit) {
                        validWaiting.add(ocrText)
                    } else if (ocrText.contains(
                            "match", ignoreCase = true
                        ) || ocrText.contains(
                            "starts", ignoreCase = true
                        ) || ocrText.contains("seconds", ignoreCase = true)
                    ) validWaiting.add(ocrText)
                }
            }
        }
        if (validWaiting.size > 0) {
            if (validWaiting.size >= minimumTruePositives["waiting"]!!) {
                result.setWaiting()
                result.setAccepted()
            }
        }
        val finalResult = result.build()
        Log.d("MachineInputValidator", "accept: ${finalResult.accept}")
        return finalResult
    }

    override fun validateProfileLevel(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
        input.addAll(_input)
        val result = MachineResult.Builder()
        val profileLevel: String

        val (check, matchLimit) = Pair("playerlv.", 5)
        val validPlayerLevel = arrayListOf<String>()

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.profileLevel) {
                    var ocrText = label.ocr
                    if (ocrText.correctDigitOcr().digitsOnly(2).isEmpty()) continue
                    if (LabelUtils.editDistance(
                            ocrText.alphabetsOnly(), check
                        ) <= matchLimit
                    ) {
                        if (ocrText.correctDigitOcr()
                                .digitsOnly(2).length > 2 && ocrText.correctDigitOcr()
                                .digitsOnly(3) != "100"
                        ) ocrText = ocrText.correctDigitOcr().digitsOnly(2).lastChars(2)
                        validPlayerLevel.add(ocrText.correctDigitOcr().digitsOnly(2))
                        break
                    }
                }
            }
        }

        if (validPlayerLevel.size > 0) {
            profileLevel = LabelUtils.findMostCommonInList(validPlayerLevel)!!
            val cnt = validPlayerLevel.count { it == profileLevel }
            if (cnt >= minimumTruePositives["level"]!!) {
                result.setAccepted()
                result.setLevel(profileLevel)
            }
            LabelUtils.testLogGreen("validateProfileId:  accepted:  $profileLevel with count $cnt")
        }

        val finalResult = result.build()
        Log.d("MachineInputValidator", "accept: ${finalResult.accept}")
        return result.build()
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

    private fun buildGameInfoFromInput(input: ArrayList<ImageResultJsonFlat>): GameInfo {
        val validTypes = arrayListOf<String>()
        val validModes = arrayListOf<String>()
        val validGroups = arrayListOf<String>()
        val validMaps = arrayListOf<String>()

        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.gameInfo) {
                    val ocrText = label.ocr
                    for (wordOfOcr in LabelUtils.splitByChars(
                        ocrText, gameInfoFromInputSplitterRegex
                    )) {
                        val (type, mode, group, map) = validateGameInfo(ocrText)

                        if (type.isNotEmpty()) validTypes.add(type)

                        if (mode.isNotEmpty()) validModes.add(mode)

                        if (group.isNotEmpty()) validGroups.add(group)

                        if (map.isNotEmpty()) validMaps.add(map)

                        break
                    }
                }
            }
        }

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

    private val tierGroups = arrayListOf(
        "Bronze" to 2,
        "Silver" to 3,
        "Gold" to 1,
        "Platinum" to 3,
        "Diamond" to 3,
        "Crown" to 2,
        "Ace" to 1,
        "Conqueror" to 3,
        "Dominator" to 3,
        "Master" to 2,
    )

    private val aceSuffix = arrayListOf(
        "Master" to 2,
        "Dominator" to 3,
    )

    private fun getRatingGroup(
        rating: String, groups: ArrayList<Pair<String, Int>> = tierGroups,
    ): Pair<Boolean, String> {

        for (group in groups) {
            for (line in rating.split("[.]")) {
                if (line.length > 45) continue
                for (wordOfOcr in LabelUtils.splitByChars(line, ratingSplitterRegex)) {

                    // temporary fix for the 'WARNINGI RED ZONE HAS STARTEO!' overlapping rating
                    if (wordOfOcr.contains("zone", true)) continue
                    if (wordOfOcr.contains("warning", true)) continue
                    if (wordOfOcr.contains("red", true)) continue
                    if (wordOfOcr.contains("has", true)) continue
                    if (wordOfOcr.contains("started", true)) continue
                    if (wordOfOcr.contains("reducing", true)) continue
                    if (wordOfOcr.contains("the", true)) continue
                    if (wordOfOcr.contains("play", true)) continue
                    if (wordOfOcr.contains("area", true)) continue
                    if (wordOfOcr.contains("meters", true)) continue


                    if (LabelUtils.editDistance(group.first, wordOfOcr) <= group.second) {
                        return Pair(true, group.first)
                    }
                }
            }
        }
        return Pair(false, "Un-Known")
    }

    override fun validateGameHistory(_input: ArrayList<ImageResultJsonFlat>): MachineResult {
        TODO()
//        val input: ArrayList<ImageResultJsonFlat> = arrayListOf()
//        input.addAll(_input)
//        val result = MachineResult.Builder()
//        for (i in 0 until input.size) {
//            val labels = input[i]
//            for (j in 0 until labels.labels.size) {
//                val label = labels.labels[j]
//                if (label.label == BGMIConstants.GameLabels.HISTORY_GAMES.ordinal) {
//                    //#25 Classic (TPP) Squad-Erangel I1/14 23:24 #95 0
//                    val game = getHistoryGame(label.ocr)
//                    if (game != null) {
//                        result.setAccepted()
//                        result.setHistoryGame(game)
//                    }
//                }
//                if (label.label == BGMIConstants.GameLabels.HISTORY_RANK_ONE.ordinal) {
//                    //#25 Classic (TPP) Squad-Erangel I1/14 23:24 #95 0
//                    val game = getHistoryGame(label.ocr)
//                    if (game != null) {
//                        result.setAccepted()
//                        result.setHistoryGame(game)
//                    }
//                }
//            }
//        }
//        return result.build()
    }

    private fun getHistoryGame(text: String): Game? {
        val rank = getRankFromHistory(text)
        val gameInfo = getGameInfoFromHistory(text)
        val kills = getPlayersDefeatedFromHistory(text)
        val (month, day, hour, minute) = getGameTimeStampFromHistory(text)
        val teamRank = getTeamRankFromHistory(text)

        if (rank.accept != null && rank.accept && kills.accept != null && kills.accept && gameInfo.accept != null && gameInfo.accept && month.isNotEmpty() && day.isNotEmpty() && hour.isNotEmpty() && minute.isNotEmpty()) {
            val startTimeStamp = LabelUtils.timeStampInUTC(month, day, hour, minute)

            return Game(
                rank = rank.rank,
                teamRank = teamRank.teamRank,
                kills = kills.kill,
                gameInfo = Json.encodeToString(gameInfo.gameInfo),
                startTimeStamp = startTimeStamp,
                endTimestamp = "",
                initialTier = StateMachineStringConstants.UNKNOWN,
                finalTier = StateMachineStringConstants.UNKNOWN,
                synced = 0,
                gameId = startTimeStamp,
                valid = true,
                userId = StateMachineStringConstants.UNKNOWN,
                metaInfoJson = StateMachineStringConstants.UNKNOWN
            )
        }

        return null
    }

    override fun getTeamRankFromHistory(history: String): MachineResult {
        val result: MachineResult.Builder = MachineResult.Builder()
        var inputForTeamRank = ""
        var firstHash = 100

        for ((j, text) in history.split(RegexPatterns.whiteSpaceContinuous).withIndex()) {
            if (text.length > 6) continue
            if (j > 3) break
            for ((i, c) in text.correctDigitOcr().toCharArray().withIndex()) {
                if (i - firstHash > 4) break
                if (c.isLetter()) break
                if (inputForTeamRank.length > 6) break
                if (c == '#') {
                    firstHash = i
                    continue
                }
                if (firstHash != 100 && c.isWhitespace()) {
                    break
                }
                if (!c.isWhitespace() && (firstHash != 100)) inputForTeamRank += c
            }
        }


        val teamRank = inputForTeamRank.digitsOnly()

        if (teamRank.isNotEmpty() && teamRank.length < 4) {
            result.setAccepted()
            LabelUtils.testLogGreen("teamRank from History: Team Rank: $teamRank")
            result.setRank(teamRank)
        }
        return result.build()
    }

    override fun getRankFromHistory(history: String): MachineResult {
        val result: MachineResult.Builder = MachineResult.Builder()
        var rank = ""
        for ((j, text) in history.split(RegexPatterns.whiteSpaceContinuous).reversed().withIndex()) {
            if (j > 2) break
            if (text.isEmpty()) continue
            if (text[0] == '#') {
                rank = text.digitsOnly()
            }
        }

        if (rank.isNotEmpty() && rank.length < 4) {
            result.setAccepted()
            result.setRank(rank)
        }

        return result.build()
    }

    override fun getPlayersDefeatedFromHistory(history: String): MachineResult {
        val result: MachineResult.Builder = MachineResult.Builder()
        var kills = ""
        var prev = ""
        for ((j, text) in history.split(RegexPatterns.whiteSpaceContinuous).reversed().withIndex()) {
            if (j > 2) break
            if (text.isEmpty()) continue
            if (text[0] == '#') {
                kills = prev.digitsOnly()
                break
            }
            prev = text

            if (prev.digitsOnly().isNotEmpty()) {
                kills = prev
                break
            }
        }

        if (kills.isNotEmpty() && kills.length < 4) {
            result.setAccepted()
            result.setKill(kills)
        }

        return result.build()
    }

    override fun getGameInfoFromHistory(history: String): MachineResult {
        val result: MachineResult.Builder = MachineResult.Builder()
        val (type, mode, group, name) = validateGameInfo(history.split(gameInfoSplitterRegex)
            .joinToString { it.alphabetsOnly() + " " })
        if (type.isNotEmpty() && group.isNotEmpty()) {
            result.setAccepted()
            result.setGameInfo(GameInfo(type = type, view = mode, group = group, mode = name))
        }
        return result.build()
    }

    override fun getGameTimeStampFromHistory(history: String): Array<String> {
        var date = ""
        var time = ""

        var month = ""
        var day = ""
        var hour = ""
        var minute = ""

        for ((_, text) in history.split(RegexPatterns.whiteSpaceContinuous).withIndex()) {

            Log.d("getTimeStamp", text)

            if (date == "" && text.contains('/') && text.length == 5) {
                val corrected = text.correctDigitOcr()
                if (corrected.digitsOnly().length == 4) date =
                    "${corrected[0]}${corrected[1]}:${corrected[3]}${corrected[4]}"
                month = "${corrected[0]}${corrected[1]}"
                day = "${corrected[3]}${corrected[4]}"

            }

            if (time == "" && text.contains(':') && text.length == 5) {
                val corrected = text.correctDigitOcr()
                if (corrected.digitsOnly().length == 4) time =
                    "${corrected[0]}${corrected[1]}:${corrected[3]}${corrected[4]}"
                hour = "${corrected[0]}${corrected[1]}"
                minute = "${corrected[3]}${corrected[4]}"
            }

            Log.d("getTimeStamp", arrayOf(month, day, hour, minute).toString())
        }

        return arrayOf(month, day, hour, minute)
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
                        result.setStart()
                        result.setAccepted()

                        //if (label.label == GameLabels.START.ordinal) {
//                    result.setStart()
//                    result.setAccepted()
//                    val ocrText = label.ocr.alphabetsOnly()
//                    if (LabelUtils.editDistance(check, ocrText) <= matchLimit) {
//                        validStart.add(ocrText)
//                        LabelUtils.testLogGrey("validateStart:  truePositive: $ocrText")
//                        break
                    }
                    //}
                }
            }
        }
        LabelUtils.testLogGreen("validateInGame:  accept: ${result.accept}, truePositive: ${result.start} ")
        return result.build()
    }

    override fun validateInGameWithNoOCR(input: ArrayList<ImageResultJsonFlat>): MachineResult {
        val validInGames = arrayListOf<String>()
        for (i in 0 until input.size) {
            val labels = input[i]
            for (j in 0 until labels.labels.size) {
                val label = labels.labels[j]
                if (MachineConstants.gameConstants.labelNameFromIndex(label.label) == LabelConstants.ingame) {
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
        return MachineResult.Builder().build()
    }

    override fun validateResultForTeamGames(game: Game): Boolean {
        if (!sendForAutoML) {
            game.squadScoring?.let {
                sendForAutoML = getSquadScoringArray(it).isEmpty()
                ignoreException { killOCRCount.entries.forEach {
                    sendForAutoML =
                        sendForAutoML || checkValidityOfFrequencyOfOccurrence(it.value)
                                || checkValidityOfFrequencyOfOccurrence(rankOCRCount)
                } }
            }
        }

        com.gamerboard.logger.log {
            it.setMessage("sendForAutoML")
            it.addContext("flag", sendForAutoML)
            it.setCategory(LogCategory.RANK_KILL)
        }
        if (sendForAutoML) return true
        val gameInfo = MachineConstants.machineLabelUtils.getGameInfo(game.gameInfo!!)
        if (gameInfo.group == StateMachineStringConstants.UNKNOWN) return true
        if (game.teamRank in arrayListOf(
                null, StateMachineStringConstants.UNKNOWN
            ) || (game.teamRank?.matches(RegexPatterns.numbers)?.not() != false)
        ) return true

        if (game.rank == StateMachineStringConstants.UNKNOWN || (game.rank?.matches(
                RegexPatterns.numbers
            )?.not() != false)
        ) return true

        if (game.squadScoring != null) {
            var isAnyValueMissing = false
            try {
                val squadPlayersArray = getSquadScoringArray(game.squadScoring!!)
                for (i in 0 until squadPlayersArray.size) {
                    val squadPlayer = squadPlayersArray[i]
                    val playerScore =
                        gson.fromJson(squadPlayer.toString(), JsonObject::class.java)
                    if (!playerScore.validatePlayerScoring() && !isAnyValueMissing) {
                        isAnyValueMissing = true
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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
        val gameInfo = MachineConstants.machineLabelUtils.getGameInfo(game.gameInfo!!)
        if (!sendForAutoML) killOCRCount[0]?.let {
            sendForAutoML = sendForAutoML || checkValidityOfFrequencyOfOccurrence(
                it
            ) || checkValidityOfFrequencyOfOccurrence(rankOCRCount)
        }

        com.gamerboard.logger.log {
            it.setMessage("sendForAutoML")
            it.addContext("flag", sendForAutoML)
            it.setCategory(LogCategory.RANK_KILL)
        }
        if (sendForAutoML) return true

        if (gameInfo.group == StateMachineStringConstants.UNKNOWN) return true

        if (game.rank == StateMachineStringConstants.UNKNOWN || game.rank?.matches(
                RegexPatterns.numbers
            )?.not() != false
        ) return true

        if (game.kills == StateMachineStringConstants.UNKNOWN || game.kills?.matches(
                RegexPatterns.numbers
            )?.not() != false
        ) return true

        if (game.initialTier == StateMachineStringConstants.UNKNOWN) return true

        if (game.finalTier == StateMachineStringConstants.UNKNOWN) return true

        if (gameInfo.group == StateMachineStringConstants.UNKNOWN) return true

        if (gameInfo.type == StateMachineStringConstants.UNKNOWN || gameInfo.type != "Classic") return true
        return false

    }

    private fun maintainOCRCounter(
        isFromAutoMl: Boolean,
        map: HashMap<String, Int>, list: List<Pair<String, String>>,
    ) = list.forEach {
        insertOrUpdateCountInMap(isFromAutoMl, map, it.first, 1)
    }

    private fun maintainOCRCounter(
        isFromAutoMl: Boolean,
        map: HashMap<String, Int>,
        count: Pair<String, Int>,
    ) =
        insertOrUpdateCountInMap(isFromAutoMl, map, count.first, count.second)

    private fun insertOrUpdateCountInMap(
        isFromAutoMl: Boolean,
        map: HashMap<String, Int>,
        key: String,
        value: Int,
    ) {

        com.gamerboard.logger.log {
            it.setMessage("insertOrUpdateCountInMap")
            it.addContext("map", map)
            it.addContext("key", key)
            it.addContext("value", value)
        }
        var count = value * baseWeight
        try {
            if (isFromAutoMl) count *= weightVisionOCR
            key.toIntOrNull()?.let {
                if (key.startsWith("1") && it / 10 >= 1)
                    count *= weightStartsWith1
                else if (key.contains("1") && it / 10 >= 1)//specific to numbers ending with 1
                    count *= contains1
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        if (key.isNotEmpty() && key != "-1") if (map.contains(key)) {
            map[key] = map[key]!! + count
        } else map[key] = count
    }

    private fun getProminentValue(map: HashMap<String, Int>): String? {

        com.gamerboard.logger.log {
            it.setMessage("getProminentValue")
            it.addContext("map", map)
            it.setCategory(LogCategory.RANK_KILL)
        }
        val maxEntry = map.entries.maxByOrNull { entry -> entry.value }
        /*val sortedOrder = map.entries.sortedByDescending { entry -> entry.value }
        //if the max value isn't the double the count of the next largest value
        if (sortedOrder.size > 1 && (sortedOrder[0].value.toFloat() / sortedOrder[1].value.toFloat()) < 2.5) sendForAutoML =
            true
        //if the max values count is less than 3 or it contains '1'
        maxEntry?.let {
            if (it.value < 4)
                sendForAutoML = true
        }*/
        val filtered = map.entries.filter { it.value == maxEntry?.value }
        if (filtered.size == 1) return maxEntry?.key
        else if (filtered.isNotEmpty()) {
            return filtered.maxByOrNull { it.key.length }?.key
        }
        return null
    }

    private fun checkValidityOfFrequencyOfOccurrence(map: HashMap<String, Int>): Boolean {
        logToFile("map: $map", LogCategory.RANK_KILL)
        val maxEntry = map.entries.maxByOrNull { entry -> entry.value }
        val sortedOrder = map.entries.sortedByDescending { entry -> entry.value }
        //if the max value isn't the double the count of the next largest value
        if (sortedOrder.size > 1 && (sortedOrder[0].value.toFloat() / sortedOrder[1].value.toFloat()) < 2.5) {
            com.gamerboard.logger.log {
                it.setMessage("autoML flag True; fraction less than 2.5")
                it.setCategory(LogCategory.RANK_KILL)
            }
            return true
        }
        //if the max values count is less than 4
        maxEntry?.let {
            if (it.value < 4) {
                logToFile(
                    "autoML flag True; observed value ${it.value} freq.< 4",
                    LogCategory.RANK_KILL
                )
                return true
            }
        }
        val filtered = map.entries.filter { it.value == maxEntry?.value }
        if (filtered.size > 1) {
            logToFile(
                "autoML flag True; two values with same frequency",
                LogCategory.RANK_KILL
            )
            return true
        }
        return false
    }

    private fun getProminentValueForUserName(map: HashMap<String, Int>): String? {
        logToFile("getUserName: $map", LogCategory.RANK_KILL)
        var userName = getProminentValue(map)
        if ((userName == null || userName.isEmpty()) && map.isNotEmpty()) {
            val avgLen =
                (map.entries.fold(0) { count, item -> return@fold (item.key.length + count) }) / map.size
            userName = map.entries.minByOrNull { abs(it.key.length - avgLen) }?.key
        }
        return userName
    }

    override fun getProcessedRankData(isFromAutoMl: Boolean): String? {
//        val playerCountMap = HashMap<String, Int>()
        val rankOCRCount = HashMap(rankOCRCount)
        val rankText = getProminentValue(rankOCRCount)
        logToFile("rankText: $rankText", LogCategory.RANK_KILL)
        log("rankText: $rankText")
        return if (rankText == null) {
            sendForAutoML = true

            com.gamerboard.logger.log {
                it.setMessage("autoML flag True; rank observed as null")
                it.setCategory(LogCategory.RANK_KILL)
            }
            null
        } else if (rankText.toIntOrNull() == null) {
            sendForAutoML = true


            com.gamerboard.logger.log {
                it.setMessage("autoML flag True; observed rank not a number")
                it.setCategory(LogCategory.RANK_KILL)
            }
            null
        } else {
            val rank = rankText.toInt()
            if (rankOCRCount.entries.any { it.key.toInt() > 9 && it.key.contains("1") }) {
                sendForAutoML = true
                logToFile(
                    "autoML flag True; observed rank contains 1 and a double digit",
                    LogCategory.RANK_KILL
                )
            }
            rank.toString()
        }
    }

    override fun compareAndMatchGameUsername(obtained: String?, expected: String?): Boolean {
        return expected != null && obtained != null && LabelUtils.editDistance(
            expected, obtained
        ) <= 2
    }

}

private fun log(message: String) {
    Log.i("RANK_KILL", message)
}
