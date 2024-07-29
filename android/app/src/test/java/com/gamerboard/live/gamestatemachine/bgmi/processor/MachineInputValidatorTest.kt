package com.gamerboard.live.gamestatemachine.bgmi.processor

import android.util.Log
import com.apollographql.apollo3.api.Optional
import com.gamerboard.live.ModelParamQuery
import com.gamerboard.live.SubmitBGMIGameMutation
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.bgmi.LOGIN_RAW_JSON
import com.gamerboard.live.gamestatemachine.bgmi.RESULT_RANK_RATING_JSON
import com.gamerboard.live.gamestatemachine.bgmi.START_JSON
import com.gamerboard.live.gamestatemachine.bgmi.TestUser1
import com.gamerboard.live.gamestatemachine.bgmi.USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON
import com.gamerboard.live.gamestatemachine.bgmi.USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON
import com.gamerboard.live.gamestatemachine.bgmi.USER1_GAME2_RANK_KILLS_GAME_INFO_RAW_JSON
import com.gamerboard.live.gamestatemachine.bgmi.USER1_ID_DETAILS_and_LEVEL_RAW_JSON
import com.gamerboard.live.gamestatemachine.bgmi.USER1_PROFILE_and_ID_RAW_JSON
import com.gamerboard.live.gamestatemachine.bgmi.USER1_WAITING_RAW_JSON
import com.gamerboard.live.gamestatemachine.bgmi.obj
import com.gamerboard.live.gamestatemachine.games.GameConstant
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIConstants
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.MAX_BUFFER_LIMIT
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineResult
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.models.Resolution
import com.gamerboard.live.models.TFResult
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.models.db.GameInfo
import com.gamerboard.live.service.screencapture.MLKitOCR
import com.gamerboard.live.type.BgmiGroups
import com.gamerboard.live.type.BgmiLevels
import com.gamerboard.live.type.BgmiMaps
import com.gamerboard.live.type.SquadMemberGameInfo
import com.google.common.reflect.TypeToken
import com.google.common.truth.Truth
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import com.gamerboard.live.repository.ModelParamConst
import com.gamerboard.live.type.ESports
import org.junit.Before
import org.junit.Test
import java.util.Date

class MachineInputValidatorTest {
    private val gson: Gson = Gson()
    @Before
    fun setup() {
        debugMachine = DEBUGGER.DIRECT_HANDLE
        val modelData = ModelParamConst(ESports.BGMI).modelParamValues()
        MachineConstants.gameConstants = modelData?.let { GameConstant(modelParam = it) }!!
        MachineConstants.loadConstants(SupportedGames.BGMI.packageName)/*Machine.stateMachine.transition(GameEvent.OnResetState)*/

    }

    @Test
    fun testValidateProfileId() {

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "ID:5552508I714 [.] pratyushtiwa [.]")
        )
        val result1 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data1, data1))
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.id).isEqualTo("55525081714")

        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "ID1678910")
        )
        val result2 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data2, data2))
        Truth.assertThat(result2.accept).isFalse()

        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "ID:1234567891D [.]")
        )
        val result3 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data3, data3))
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.id).isEqualTo("12345678910")

        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]ID:12345O789DD[.]")
        )
        val result4 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data4, data4))
        Truth.assertThat(result4.accept).isTrue()
        Truth.assertThat(result4.id).isEqualTo("12345078900")

        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "ID #: 12345O789DD")
        )
        val result5 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data5, data5))
        Truth.assertThat(result5.accept).isTrue()
        Truth.assertThat(result5.id).isEqualTo("12345078900")

        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]ID12345O789DD[.]")
        )
        val result6 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data6, data6))
        Truth.assertThat(result6.accept).isTrue()
        Truth.assertThat(result6.id).isEqualTo("12345078900")

        val data7 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]ID12345O789DD[.]")
        )
        val result7 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data7, data7))
        Truth.assertThat(result7.accept).isTrue()
        Truth.assertThat(result7.id).isEqualTo("12345078900")

        val data8 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]ID:12345O78DD[.]")
        )
        val result8 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data8, data8))
        Truth.assertThat(result8.accept).isTrue()
        Truth.assertThat(result8.id).isEqualTo("1234507800")

        val data9 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]ID125O789DD3[.]")
        )
        val result9 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data9, data9))
        Truth.assertThat(result9.accept).isTrue()
        Truth.assertThat(result9.id).isEqualTo("1250789003")
    }

    @Test
    fun testValidateProfileLevel() {

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "Player Lv. 23")
        )
        val result1 =
            MachineConstants.machineInputValidator.validateProfileLevel(arrayListOf(data1, data1))
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.level).isEqualTo("23")

        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "Plajijiyer Lhjiv. 23")
        )
        val result2 =
            MachineConstants.machineInputValidator.validateProfileLevel(arrayListOf(data2, data2))
        Truth.assertThat(result2.accept).isFalse()

        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "ayer Lv 2")
        )
        val result3 =
            MachineConstants.machineInputValidator.validateProfileLevel(arrayListOf(data3, data3))
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.level).isEqualTo("2")

        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "ayer Lv ")
        )
        val result4 =
            MachineConstants.machineInputValidator.validateProfileLevel(arrayListOf(data4, data4))
        Truth.assertThat(result4.accept).isFalse()

        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "Ploycr Lu. 23")
        )
        val result5 =
            MachineConstants.machineInputValidator.validateProfileLevel(arrayListOf(data5, data5))
        Truth.assertThat(result5.accept).isTrue()

        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "player1v 25")
        )
        val result6 =
            MachineConstants.machineInputValidator.validateProfileLevel(arrayListOf(data6, data6))
        Truth.assertThat(result6.accept).isTrue()
        Truth.assertThat(result6.level).isEqualTo("25")
    }

    @Test
    fun testValidateProfileIdAndLevel() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "Player Lv. 24",
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to "ID:5552508I714 pratyushtiwa"
            )
        )
        val result1 =
            MachineConstants.machineInputValidator.validateProfileIdAndLevel(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.id).isEqualTo("55525081714")
        Truth.assertThat(result1.level).isEqualTo("24")

        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "Plajijiyer Lhjiv. 23",
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to "ID:12345678910"
            )
        )
        val result2 = MachineConstants.machineInputValidator.validateProfileIdAndLevel(
            arrayListOf(
                data2, data2
            )
        )
        Truth.assertThat(result2.accept).isFalse()

        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "ayer Lv 2",
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to "ID:12345678910"
            )
        )
        val result3 = MachineConstants.machineInputValidator.validateProfileIdAndLevel(
            arrayListOf(
                data3, data3
            )
        )
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.id).isEqualTo("12345678910")
        Truth.assertThat(result3.level).isEqualTo("2")

        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "ayer Lv ",
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to "ID:12345678910"
            )
        )
        val result4 = MachineConstants.machineInputValidator.validateProfileIdAndLevel(
            arrayListOf(
                data4, data4
            )
        )
        Truth.assertThat(result4.accept).isFalse()

        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "Ploycr Lv 5",
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to "ID:12345678910"
            )
        )
        val result5 = MachineConstants.machineInputValidator.validateProfileIdAndLevel(
            arrayListOf(
                data5, data5
            )
        )
        Truth.assertThat(result5.accept).isTrue()
        Truth.assertThat(result5.level).isEqualTo("5")


        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "Ploycr Lv 5",
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to "ID:12345678D"
            )
        )
        val result6 = MachineConstants.machineInputValidator.validateProfileIdAndLevel(
            arrayListOf(
                data6, data6
            )
        )
        Truth.assertThat(result6.accept).isTrue()
        Truth.assertThat(result6.id).isEqualTo("123456780")
        Truth.assertThat(result6.level).isEqualTo("5")
    }

    @Test
    fun testValidateWaiting() {

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "Match starts in 4 seconds")
        )
        val result1 = MachineConstants.machineInputValidator.validateWaiting(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()

        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "mat str 21")
        )
        val result2 = MachineConstants.machineInputValidator.validateWaiting(arrayListOf(data2))
        Truth.assertThat(result2.accept).isFalse()

        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "atchstarts in 4 seconds")
        )
        val result3 = MachineConstants.machineInputValidator.validateWaiting(arrayListOf(data3))
        Truth.assertThat(result3.accept).isTrue()

        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "atchstarts")
        )
        val resul4 = MachineConstants.machineInputValidator.validateWaiting(arrayListOf(data4))
        Truth.assertThat(resul4.accept).isTrue()

    }

    @Test
    fun testValidateGameResult() {
        //UserHandler.originalBGBICharacterID = "pratyushtiwa"


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]Finishes Il[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "100 / 99"
            )
        )
        val result1 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.kill).isEqualTo("11")
        Truth.assertThat(Json.encodeToString(result1.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(result1.rank).isEqualTo("100")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val resul2 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data2))
        Truth.assertThat(resul2.accept).isTrue()
        Truth.assertThat(resul2.kill).isEqualTo("5")
        Truth.assertThat(Json.encodeToString(resul2.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(resul2.rank).isEqualTo("45")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes7[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "/99"
            )
        )
        val result3 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data3))
        Truth.assertThat(result3.rank).isNull()
        Truth.assertThat(result3.accept).isTrue()


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyoshtiw[.]finishes 7[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "23 /99"
            )
        )
        val result4 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data4))
        Truth.assertThat(result4.accept).isTrue()
        Truth.assertThat(result4.kill).isEqualTo("7")
        Truth.assertThat(Json.encodeToString(result4.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(result4.rank).isEqualTo("23")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "5/ 99"
            )
        )
        val result5 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data5))
        Truth.assertThat(result5.kill).isEqualTo(null)
        Truth.assertThat(result5.accept).isTrue()


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes 3[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "3 /99"
            )
        )
        val result6 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data6))
        Truth.assertThat(result6.accept).isTrue()
        Truth.assertThat(result6.kill).isEqualTo("3")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data7 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes 3[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2  / 99"
            )
        )
        val result7 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data7))
        Truth.assertThat(result7.accept).isTrue()
        Truth.assertThat(result7.kill).isEqualTo("3")
        Truth.assertThat(result7.rank).isEqualTo("2")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data8 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishe 1s[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2/99"
            )
        )
        val result8 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data8))
        Truth.assertThat(result8.accept).isTrue()
        Truth.assertThat(result8.kill).isNull()
        Truth.assertThat(result8.rank).isEqualTo("2")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data9 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishe |[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2/ 99"
            )
        )
        val result9 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data9))
        Truth.assertThat(result9.accept).isTrue()
        Truth.assertThat(result9.kill).isEqualTo("1")
        Truth.assertThat(result9.rank).isEqualTo("2")



        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data10 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes |[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to " 2  /99"
            )
        )
        val result10 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data10))
        Truth.assertThat(result10.accept).isTrue()
        Truth.assertThat(result10.kill).isEqualTo("1")
        Truth.assertThat(result10.rank).isEqualTo("2")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data11 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "1/99"
            )
        )
        val result11 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data11))
        Truth.assertThat(result11.accept).isTrue()
        Truth.assertThat(result11.kill).isNull()
        Truth.assertThat(result11.rank).isEqualTo("1")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data12 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "| /99"
            )
        )
        val result12 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data12))
        Truth.assertThat(result12.accept).isTrue()
        Truth.assertThat(result12.kill).isNull()
        Truth.assertThat(result12.rank).isEqualTo("1")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data13 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]elimination[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "100 / 99"
            )
        )
        val result13 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data13))
        Truth.assertThat(result13.accept).isTrue()
        Truth.assertThat(result13.kill).isNull()
        Truth.assertThat(Json.encodeToString(result13.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(result13.rank).isEqualTo("100")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data14 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]eliminAtION 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val resul14 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data14))
        Truth.assertThat(resul14.accept).isTrue()
        Truth.assertThat(resul14.kill).isEqualTo("5")
        Truth.assertThat(Json.encodeToString(resul14.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(resul14.rank).isEqualTo("45")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data15 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]liminations[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "/99"
            )
        )
        val result15 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data15))
        Truth.assertThat(result15.rank).isNull()
        Truth.assertThat(result15.accept).isTrue()


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data16 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyoshtiw[.]eliminations 7[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "23 /99"
            )
        )
        val result16 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data16))
        Truth.assertThat(result16.accept).isTrue()
        Truth.assertThat(result16.kill).isEqualTo("7")
        Truth.assertThat(Json.encodeToString(result16.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(result16.rank).isEqualTo("23")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data17 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]eliminations#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "5/ 99"
            )
        )
        val result17 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data17))
        Truth.assertThat(result17.kill).isEqualTo(null)
        Truth.assertThat(result17.accept).isTrue()


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data18 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]eliminations 3[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "3 /99"
            )
        )
        val result18 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data18))
        Truth.assertThat(result18.accept).isTrue()
        Truth.assertThat(result18.kill).isEqualTo("3")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data19 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]eliminations 3[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2  / 99"
            )
        )
        val result19 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data19))
        Truth.assertThat(result19.accept).isTrue()
        Truth.assertThat(result19.kill).isEqualTo("3")
        Truth.assertThat(result19.rank).isEqualTo("2")
    }

    @Test
    fun testSquadGameForOneUserNONAutomatched() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()

        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "[.]RT LEO Finishes 3[.]UCKY",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "nked Classic Mode (TPP) - Squad - Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#14/64"
            )
        )
        val result6 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data6))
        Truth.assertThat(result6.accept).isTrue()
        Truth.assertThat(result6.kill).isEqualTo("3")
        Truth.assertThat(result6.rank).isEqualTo("14")
        Truth.assertThat(result6.squadScoring).isEqualTo("[{\"username\":\"RT LEO\",\"kills\":3}]")


        val data7 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "[.]UCKY[.]RT LEO Finishes 3",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "nked Classic Mode (TPP) - Squad - Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#14/64"
            )
        )
        val result7 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data7))
        Truth.assertThat(result7.accept).isTrue()
        Truth.assertThat(result7.kill).isEqualTo("3")
        Truth.assertThat(result7.rank).isEqualTo("14")
        Truth.assertThat(result7.squadScoring)
            .isNotEqualTo("[{\"username\":\"RT LEO\",\"kills\":3}]")

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "[.]jarvisFriday[.]Finishes 3",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Ranked Classic Mode (TPP) - Squad - Livik",
                BGMIConstants.GameLabels.RANK.ordinal to "#2/52"
            )
        )
        val result1 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.kill).isEqualTo("3")
        Truth.assertThat(result1.rank).isEqualTo("2")
        Truth.assertThat(result1.squadScoring)
            .isEqualTo("[{\"username\":\"jarvisFriday\",\"kills\":3}]")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "[.]panther5103 Finishes D",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Ranked Classic Mode (TPP) - Squad - Livik",
                BGMIConstants.GameLabels.RANK.ordinal to "#2/52"
            )
        )
        val result2 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data2))
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.kill).isEqualTo("0")
        Truth.assertThat(result2.rank).isEqualTo("2")
        Truth.assertThat(result2.squadScoring)
            .isEqualTo("[{\"username\":\"panther5103\",\"kills\":0}]")

        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "[.]+[.]nmlss Finishes D",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Ranked Classic Mode (TPP) - Squad - Livik",
                BGMIConstants.GameLabels.RANK.ordinal to "#2/52"
            )
        )
        val result3 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data3))
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.kill).isEqualTo("0")
        Truth.assertThat(result3.rank).isEqualTo("2")
        Truth.assertThat(result3.squadScoring).isEqualTo("[{\"username\":\"nmlss\",\"kills\":0}]")

        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "[.]EKING KHONSI[.]Finishes 2",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Ranked Classic Mode (TPP) - Squad - Livik",
                BGMIConstants.GameLabels.RANK.ordinal to "#2/52"
            )
        )
        val result4 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data4))
        Truth.assertThat(result4.accept).isTrue()
        Truth.assertThat(result4.kill).isEqualTo("2")
        Truth.assertThat(result4.rank).isEqualTo("2")
        Truth.assertThat(result4.squadScoring)
            .isEqualTo("[{\"username\":\"EKING KHONSI\",\"kills\":2}]")

        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "[.]KINGSREDHOOD Finishes 3[.]N",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "nked Classic Mode (TPP) - Squad - Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#14/64"
            )
        )
        val result5 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data5))
        Truth.assertThat(result5.accept).isTrue()
        Truth.assertThat(result5.kill).isEqualTo("3")
        Truth.assertThat(result5.rank).isEqualTo("14")
        Truth.assertThat(result5.squadScoring)
            .isEqualTo("[{\"username\":\"KINGSREDHOOD\",\"kills\":3}]")

    }

    @Test
    fun testSquadGameWithImageResultJson() {
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        val list: ArrayList<ImageResultJsonFlat> = Gson().fromJson(
            "[{\"epochTimestamp\":1655181906670,\"fileName\":\"1655181906670.jpg\",\"labels\":[{\"box\":[0.60466284,0.2736416,0.6929428,0.3508389],\"confidence\":0.85546875,\"label\":7,\"ocr\":\"[.]PEl, shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.609338,0.44032907,0.69761795,0.5210567],\"confidence\":0.85546875,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0038352683,0.43361217,0.11523277,0.5638826],\"confidence\":0.9765625,\"label\":6,\"ocr\":\"#/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6092837,0.60749525,0.6995597,0.6882228],\"confidence\":0.66015625,\"label\":7,\"ocr\":\"[.]ZenGAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0067565814,0.7489126,0.05638662,0.9709389],\"confidence\":0.85546875,\"label\":8,\"ocr\":\"anked Classic Mode (TPP) - Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181907184,\"fileName\":\"1655181907184.jpg\",\"labels\":[{\"box\":[0.6082631,0.2709327,0.7005803,0.35166034],\"confidence\":0.91796875,\"label\":7,\"ocr\":\"[.]PEl shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6150432,0.43564153,0.6994626,0.5181944],\"confidence\":0.85546875,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0014933981,0.43201962,0.10801934,0.5622901],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"#1/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6092837,0.6093314,0.6995597,0.6882742],\"confidence\":0.75,\"label\":7,\"ocr\":\"[.]ZenGAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0074053966,0.74546146,0.057035435,0.94849336],\"confidence\":0.97265625,\"label\":8,\"ocr\":\"nked Classic Made (TPP) - Squad - Erange\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181907706,\"fileName\":\"1655181907706.jpg\",\"labels\":[{\"box\":[0.6082631,0.2708104,0.7005803,0.3480077],\"confidence\":0.90234375,\"label\":7,\"ocr\":\"[.]PEl, shubhan[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6169307,0.43564153,0.7013501,0.5181944],\"confidence\":0.90234375,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0014933981,0.43201962,0.10801934,0.5622901],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"#1/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6084699,0.6149503,0.69479805,0.6938931],\"confidence\":0.7890625,\"label\":7,\"ocr\":\"[.]ZenGAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0067565814,0.7458204,0.05638662,0.95344293],\"confidence\":0.97265625,\"label\":8,\"ocr\":\"nked Classic Made (TPP)- Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181908121,\"fileName\":\"1655181908121.jpg\",\"labels\":[{\"box\":[0.6082631,0.27088144,0.7005803,0.34982416],\"confidence\":0.93359375,\"label\":7,\"ocr\":\"[.]PEla shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6169307,0.43565196,0.7013501,0.5200714],\"confidence\":0.87890625,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0014933981,0.43361217,0.10801934,0.5638826],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"#|/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6112255,0.60838765,0.69950545,0.6873304],\"confidence\":0.7890625,\"label\":7,\"ocr\":\"[.]ZenbAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0067565814,0.74546146,0.05638662,0.94849336],\"confidence\":0.97265625,\"label\":8,\"ocr\":\"nked Classic Mode (TPP) - Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181908472,\"fileName\":\"1655181908472.jpg\",\"labels\":[{\"box\":[0.60627574,0.27175415,0.7006802,0.34895146],\"confidence\":0.93359375,\"label\":7,\"ocr\":\"[.]PEla shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.61598694,0.43658525,0.7004063,0.5191381],\"confidence\":0.90234375,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0014933981,0.43201962,0.10801934,0.5622901],\"confidence\":0.9765625,\"label\":6,\"ocr\":\"#1/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6092837,0.60838765,0.6995597,0.6873304],\"confidence\":0.7890625,\"label\":7,\"ocr\":\"[.]ZenGAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0067565814,0.74546146,0.05638662,0.94849336],\"confidence\":0.97265625,\"label\":8,\"ocr\":\"nked Classic Mode (TPP) - Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181908819,\"fileName\":\"1655181908819.jpg\",\"labels\":[{\"box\":[0.6101506,0.27088144,0.7024678,0.34982416],\"confidence\":0.91796875,\"label\":7,\"ocr\":\"[.]PEl shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.61787444,0.43470824,0.7022938,0.51912767],\"confidence\":0.87890625,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.00028911978,0.43201962,0.10922361,0.5622901],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6131129,0.60749525,0.7013929,0.6882228],\"confidence\":0.75,\"label\":7,\"ocr\":\"[.]ZenGAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0067565814,0.74546146,0.05638662,0.94849336],\"confidence\":0.97265625,\"label\":8,\"ocr\":\"nked Classic Mode (TPP) - Squad- Erange\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181909159,\"fileName\":\"1655181909159.jpg\",\"labels\":[{\"box\":[0.6082631,0.27088144,0.7005803,0.34982416],\"confidence\":0.87890625,\"label\":7,\"ocr\":\"[.]PEl shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6197514,0.43658525,0.7023043,0.5191381],\"confidence\":0.75,\"label\":7,\"ocr\":\"[.]puishpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0014933981,0.43361217,0.10801934,0.5638826],\"confidence\":0.96484375,\"label\":6,\"ocr\":\"#1/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6121149,0.6064292,0.7023909,0.68362653],\"confidence\":0.66015625,\"label\":7,\"ocr\":\"[.]ZenGAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0067565814,0.74316615,0.05638662,0.9507887],\"confidence\":0.96484375,\"label\":8,\"ocr\":\"nked Classic Mode (TPP)-Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181909509,\"fileName\":\"1655181909509.jpg\",\"labels\":[{\"box\":[0.6082631,0.27187642,0.7005803,0.35260406],\"confidence\":0.87890625,\"label\":7,\"ocr\":\"[.]PEla shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6197514,0.43659574,0.7023043,0.5210151],\"confidence\":0.85546875,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0014933981,0.43201962,0.10801934,0.5622901],\"confidence\":0.9765625,\"label\":6,\"ocr\":\"#1/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6121149,0.60548544,0.7023909,0.68268275],\"confidence\":0.7890625,\"label\":7,\"ocr\":\"[.]ZenGAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0074053966,0.74316615,0.057035435,0.9507887],\"confidence\":0.96484375,\"label\":8,\"ocr\":\"nked Classic Mode (TPP) -Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181909848,\"fileName\":\"1655181909848.jpg\",\"labels\":[{\"box\":[0.61022747,0.27182516,0.70050347,0.35076788],\"confidence\":0.87890625,\"label\":7,\"ocr\":\"[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.62066406,0.43564135,0.70139164,0.5219695],\"confidence\":0.82421875,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.00028911978,0.43361217,0.10922361,0.5638826],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"#1/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6121692,0.6055565,0.70044917,0.68449926],\"confidence\":0.75,\"label\":7,\"ocr\":\"[.]ZenGAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0074053966,0.74316615,0.057035435,0.9507887],\"confidence\":0.97265625,\"label\":8,\"ocr\":\"nked Classic Made (TPP) - Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181910198,\"fileName\":\"1655181910198.jpg\",\"labels\":[{\"box\":[0.61022747,0.27276888,0.70050347,0.3517116],\"confidence\":0.90234375,\"label\":7,\"ocr\":\"[.]PEl shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6169307,0.43749785,0.7013501,0.5182255],\"confidence\":0.85546875,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0014933981,0.43201962,0.10801934,0.5622901],\"confidence\":0.96484375,\"label\":6,\"ocr\":\"#/6\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6121692,0.6055565,0.70044917,0.68449926],\"confidence\":0.7890625,\"label\":7,\"ocr\":\"[.]ZenbAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0068443287,0.74316615,0.057596505,0.9507887],\"confidence\":0.97265625,\"label\":8,\"ocr\":\"nked Classic Made (TPP)- Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181910546,\"fileName\":\"1655181910546.jpg\",\"labels\":[{\"box\":[0.61022747,0.27269787,0.70050347,0.34989518],\"confidence\":0.91796875,\"label\":7,\"ocr\":\"[.]PEl shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6188077,0.437529,0.7013606,0.5200819],\"confidence\":0.87890625,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0014933981,0.43361217,0.10801934,0.5638826],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"#|/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6111712,0.60548544,0.7014472,0.68268275],\"confidence\":0.7890625,\"label\":7,\"ocr\":\"[.]ZenGAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0074053966,0.74316615,0.057035435,0.9507887],\"confidence\":0.96484375,\"label\":8,\"ocr\":\"nked Classic Mode (TPP) - Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181910901,\"fileName\":\"1655181910901.jpg\",\"labels\":[{\"box\":[0.61022747,0.27088144,0.70050347,0.34982416],\"confidence\":0.90234375,\"label\":7,\"ocr\":\"[.]PEla shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.61786395,0.43847272,0.70041686,0.5210256],\"confidence\":0.87890625,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0014933981,0.43361217,0.10801934,0.5638826],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"#|/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6112255,0.60548544,0.69950545,0.68268275],\"confidence\":0.75,\"label\":7,\"ocr\":\"[.]ZenGAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0067565814,0.74316615,0.05638662,0.9507887],\"confidence\":0.97265625,\"label\":8,\"ocr\":\"nked Classic Made (TPP)- Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1655181911262,\"fileName\":\"1655181911262.jpg\",\"labels\":[{\"box\":[0.6092837,0.27269787,0.6995597,0.34989518],\"confidence\":0.90234375,\"label\":7,\"ocr\":\"[.]PEla shubham[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.61972034,0.43658507,0.7004479,0.5229132],\"confidence\":0.90234375,\"label\":7,\"ocr\":\"[.]pushpendar2503[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0014933981,0.43201962,0.10801934,0.5622901],\"confidence\":0.9765625,\"label\":6,\"ocr\":\"#1/61\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6112255,0.6064292,0.69950545,0.68362653],\"confidence\":0.75,\"label\":7,\"ocr\":\"[.]ZenbAniket[.]Finishes 3\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0067565814,0.74546146,0.05638662,0.94849336],\"confidence\":0.96484375,\"label\":8,\"ocr\":\"nked Classic Mode (TPP) - Squad - Erangel\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        val result2 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            list
        )

        println(result2.squadScoring)
        Truth.assertThat(result2.squadScoring)
            .isEqualTo("[{\"username\":\"PEl shubham\",\"kills\":3},{\"username\":\"pushpendar2503\",\"kills\":13},{\"username\":\"ZenGAniket\",\"kills\":3}]")
    }

    @Test
    fun testSquadGameForImageResultJsonForTestImages() {
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        val list: ArrayList<ImageResultJsonFlat> = Gson().fromJson(
            "[{\"epochTimestamp\":1,\"fileName\":\"1.jpg\",\"labels\":[{\"box\":[0.6254243,0.26904523,0.7098437,0.34977287],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]suvDdip2820[.]Finishes U\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62352616,0.43838608,0.7098543,0.5286621],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]NEERAJPODDAR[.]Finishes D\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.00028911978,0.4350523,0.10922361,0.5624424],\"confidence\":0.95703125,\"label\":6,\"ocr\":\"#25 /97\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62352616,0.61594415,0.7098543,0.7042241],\"confidence\":0.96484375,\"label\":7,\"ocr\":\"[.]ROYALO-D[.]Finishes O\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0011599809,0.7811799,0.057607252,0.95773995],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP)-Squad - Erangel\",\"resolution\":{\"first\":1080,\"second\":486}}]},{\"epochTimestamp\":2,\"fileName\":\"2.jpg\",\"labels\":[{\"box\":[0.6216065,0.18483879,0.7098865,0.2692582],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]Finishes 0\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62352616,0.3560255,0.7098543,0.4463015],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]NEERAJPODDAR[.]Finishes 0\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0014933981,0.43361217,0.10801934,0.5638826],\"confidence\":0.96484375,\"label\":6,\"ocr\":\"#20 /99\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6291886,0.52702093,0.71551675,0.6153009],\"confidence\":0.9765625,\"label\":7,\"ocr\":\"[.]dirajeshrawatl[.]Finishes )\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62352616,0.6989279,0.7098543,0.7727492],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]GoDANUJYT[.]FnishesD\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0011599809,0.7772965,0.057607252,0.9578485],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP) -Squad - Erangel\",\"resolution\":{\"first\":1080,\"second\":486}}]},{\"epochTimestamp\":3,\"fileName\":\"3.jpg\",\"labels\":[{\"box\":[0.62255025,0.18065909,0.7108302,0.24521285],\"confidence\":0.97265625,\"label\":7,\"ocr\":\"[.]Finishes I\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6291021,0.35239118,0.7193781,0.43681055],\"confidence\":0.91796875,\"label\":7,\"ocr\":\"[.]2 BOOMBruH[.]Finishes D\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0014933981,0.4350523,0.10801934,0.5624424],\"confidence\":0.96484375,\"label\":6,\"ocr\":\"#4/51\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6291886,0.530828,0.71551675,0.61715615],\"confidence\":0.95703125,\"label\":7,\"ocr\":\"[.]DEATH RAVANA[.]Finishes 1\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6273011,0.699435,0.71362925,0.7684672],\"confidence\":0.95703125,\"label\":7,\"ocr\":\"[.]RAMbadY[.]Fnishes 0\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0021037161,0.77935684,0.058550987,0.95201313],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP)-Squad- Livik\",\"resolution\":{\"first\":1080,\"second\":486}}]},{\"epochTimestamp\":4,\"fileName\":\"4.jpg\",\"labels\":[{\"box\":[0.6254243,0.26904523,0.7098437,0.34977287],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]suvDdip2820[.]Finishes U\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62352616,0.43838608,0.7098543,0.5286621],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]NEERAJPODDAR[.]Finishes D\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.00028911978,0.4350523,0.10922361,0.5624424],\"confidence\":0.95703125,\"label\":6,\"ocr\":\"#25 /97\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62352616,0.61594415,0.7098543,0.7042241],\"confidence\":0.96484375,\"label\":7,\"ocr\":\"[.]ROYALO-D[.]Finishes O\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0011599809,0.7811799,0.057607252,0.95773995],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP)-Squad - Erangel\",\"resolution\":{\"first\":1080,\"second\":486}}]},{\"epochTimestamp\":5,\"fileName\":\"5.jpg\",\"labels\":[{\"box\":[0.6216065,0.26306105,0.7098865,0.3368824],\"confidence\":0.96484375,\"label\":7,\"ocr\":\"[.]nmisssss[.]Finishes 4\",\"resolution\":{\"first\":1080,\"second\":498}},{\"box\":[0.62541366,0.44122154,0.7117418,0.52016425],\"confidence\":0.97265625,\"label\":7,\"ocr\":\"[.]jarvisFriday[.]Finishes 3\",\"resolution\":{\"first\":1080,\"second\":498}},{\"box\":[0.0014933981,0.43345976,0.10801934,0.5608499],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"#2/96\",\"resolution\":{\"first\":1080,\"second\":498}},{\"box\":[0.6263252,0.6188075,0.71460515,0.70513564],\"confidence\":0.96484375,\"label\":7,\"ocr\":\"[.]pratyushtiwa[.]Finishes 2\",\"resolution\":{\"first\":1080,\"second\":498}},{\"box\":[0.0048784465,0.7998125,0.057663724,0.9542071],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP) Squad- Sanht\",\"resolution\":{\"first\":1080,\"second\":498}}]},{\"epochTimestamp\":6,\"fileName\":\"6.jpg\",\"labels\":[{\"box\":[0.6372671,0.34625894,0.7122748,0.41333312],\"confidence\":0.96484375,\"label\":7,\"ocr\":\"[.]nmsssss[.]Finishes I\",\"resolution\":{\"first\":1080,\"second\":511}},{\"box\":[0.0018816702,0.42592597,0.110816166,0.5683837],\"confidence\":0.984375,\"label\":6,\"ocr\":\"#2/99\",\"resolution\":{\"first\":1080,\"second\":511}},{\"box\":[0.6329326,0.5378975,0.7136602,0.6100866],\"confidence\":0.9453125,\"label\":7,\"ocr\":\"[.]jarvisfriday[.]Finishes 1\",\"resolution\":{\"first\":1080,\"second\":511}},{\"box\":[0.0028811786,0.77163404,0.06060473,0.95218605],\"confidence\":0.9765625,\"label\":8,\"ocr\":\"Classic (TPP) - Duo- Sanhok\",\"resolution\":{\"first\":1080,\"second\":511}}]},{\"epochTimestamp\":7,\"fileName\":\"7.jpg\",\"labels\":[{\"box\":[0.623494,0.18668342,0.711774,0.27496344],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]RedFrost yFalc[.]Finishes I\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6273013,0.3570557,0.7098542,0.44338384],\"confidence\":0.87890625,\"label\":7,\"ocr\":\"[.]PEL, VINTAGE[.]Finishes 9\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0014933981,0.4350523,0.10801934,0.5624424],\"confidence\":0.95703125,\"label\":6,\"ocr\":\"#5 /99\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62446994,0.5279146,0.7107981,0.60685736],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]KING2 Palu[.]Finishes 3\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62448055,0.6986546,0.7088999,0.7692476],\"confidence\":0.90234375,\"label\":7,\"ocr\":\"[.]7FI2VOID[.]Fnishes 0\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.001784008,0.7811799,0.056983225,0.95773995],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP) - Squad - Erangel\",\"resolution\":{\"first\":1080,\"second\":486}}]},{\"epochTimestamp\":8,\"fileName\":\"8.jpg\",\"labels\":[{\"box\":[0.62352616,0.17980023,0.7098543,0.25362158],\"confidence\":0.9765625,\"label\":7,\"ocr\":\"[.]nmlsssss[.]Finishes 2\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6215522,0.34955996,0.71182823,0.43397933],\"confidence\":0.96484375,\"label\":7,\"ocr\":\"[.]panther5103[.]fnishes 3\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0014933981,0.43361217,0.10801934,0.5638826],\"confidence\":0.96484375,\"label\":6,\"ocr\":\"#4/97\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6216065,0.52775323,0.7098865,0.6032437],\"confidence\":0.984375,\"label\":7,\"ocr\":\"[.]jarvisfriday[.]Finishes 4\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.622496,0.6974038,0.712772,0.78182316],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]pratyushtiwa[.]Finishes 2\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.001784008,0.78124434,0.056983225,0.95390064],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP) - Squad- Sanhok\",\"resolution\":{\"first\":1080,\"second\":486}}]},{\"epochTimestamp\":9,\"fileName\":\"9.jpg\",\"labels\":[{\"box\":[0.6372671,0.34625894,0.7122748,0.41333312],\"confidence\":0.96484375,\"label\":7,\"ocr\":\"[.]nmsssss[.]Finishes I\",\"resolution\":{\"first\":1080,\"second\":511}},{\"box\":[0.0018816702,0.42592597,0.110816166,0.5683837],\"confidence\":0.984375,\"label\":6,\"ocr\":\"#2/99\",\"resolution\":{\"first\":1080,\"second\":511}},{\"box\":[0.6329326,0.5378975,0.7136602,0.6100866],\"confidence\":0.9453125,\"label\":7,\"ocr\":\"[.]jarvisfriday[.]Finishes 1\",\"resolution\":{\"first\":1080,\"second\":511}},{\"box\":[0.0028811786,0.77163404,0.06060473,0.95218605],\"confidence\":0.9765625,\"label\":8,\"ocr\":\"Classic (TPP) - Duo- Sanhok\",\"resolution\":{\"first\":1080,\"second\":511}}]},{\"epochTimestamp\":10,\"fileName\":\"10.jpg\",\"labels\":[{\"box\":[0.6216065,0.18482816,0.7098865,0.27115628],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]Finishes 0\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6216065,0.35594863,0.7098865,0.4482658],\"confidence\":0.984375,\"label\":7,\"ocr\":\"[.]NEERAJPODDAR[.]SFnishes 0\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0014933981,0.43361217,0.10801934,0.5638826],\"confidence\":0.95703125,\"label\":6,\"ocr\":\"#20 /99\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6282127,0.52799684,0.71649265,0.614325],\"confidence\":0.97265625,\"label\":7,\"ocr\":\"[.]djrajeshrawatl[.]Fnishes O\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62352616,0.6989279,0.7098543,0.7727492],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]GoDANUJYT[.]-Fnishes0\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0013121422,0.77929246,0.056511357,0.9558525],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP) -Squad - Erangel\",\"resolution\":{\"first\":1080,\"second\":486}}]},{\"epochTimestamp\":11,\"fileName\":\"11.jpg\",\"labels\":[{\"box\":[0.62258244,0.26966718,0.7089106,0.34348854],\"confidence\":0.984375,\"label\":7,\"ocr\":\"[.]Finishes 1\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62448055,0.4418099,0.7088999,0.50636363],\"confidence\":0.91796875,\"label\":7,\"ocr\":\"[.]KLiffop[.]Finishes 1\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0014933981,0.4350523,0.10801934,0.5624424],\"confidence\":0.95703125,\"label\":6,\"ocr\":\"#26 /97\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62448055,0.6178638,0.7088999,0.7041919],\"confidence\":0.9765625,\"label\":7,\"ocr\":\"[.]kilercobra786[.]Finishes O\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0013121422,0.7830674,0.056511357,0.95962745],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP) -Squad - Erangel\",\"resolution\":{\"first\":1080,\"second\":486}}]},{\"epochTimestamp\":12,\"fileName\":\"12.jpg\",\"labels\":[{\"box\":[0.62255025,0.2724135,0.7108302,0.3369673],\"confidence\":0.97265625,\"label\":7,\"ocr\":\"[.]nmlsssss[.]Finishes 1\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6216065,0.43938532,0.7098865,0.52011293],\"confidence\":0.97265625,\"label\":7,\"ocr\":\"[.]panther5|03[.]Finishes 1\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0014933981,0.4350523,0.10801934,0.5624424],\"confidence\":0.96484375,\"label\":6,\"ocr\":\"#22 /98\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6216387,0.6131451,0.70796686,0.69947326],\"confidence\":0.98046875,\"label\":7,\"ocr\":\"[.]pratyushtiwa[.]Finisthes D\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.0013121422,0.77935684,0.056511357,0.95201313],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP) -Squad - Sanhok\",\"resolution\":{\"first\":1080,\"second\":486}}]},{\"epochTimestamp\":13,\"fileName\":\"13.jpg\",\"labels\":[{\"box\":[0.622496,0.17872886,0.712772,0.250918],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]jdIOD7[.]Finishes\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62255025,0.35135457,0.7108302,0.4302973],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]RichakRasoi[.]Finishes 2\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.00028911978,0.4350523,0.10922361,0.5624424],\"confidence\":0.9453125,\"label\":6,\"ocr\":\"#1/00\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.62258244,0.5260229,0.7089106,0.6162989],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]Santhusgpwda[.]Finishes 8\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.6216065,0.6964172,0.7098865,0.7846972],\"confidence\":0.98828125,\"label\":7,\"ocr\":\"[.]OPYASHKARAN[.]Finishes 8\",\"resolution\":{\"first\":1080,\"second\":486}},{\"box\":[0.001784008,0.77929246,0.056983225,0.9558525],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP) - Squad - Erangel\",\"resolution\":{\"first\":1080,\"second\":486}}]},{\"epochTimestamp\":14,\"fileName\":\"14.jpg\",\"labels\":[{\"box\":[0.62919927,0.17535208,0.71361864,0.2542948],\"confidence\":0.90234375,\"label\":7,\"ocr\":\"[.]dnmlsssss[.]Fnishes 2\",\"resolution\":{\"first\":1080,\"second\":498}},{\"box\":[0.62532717,0.3522983,0.7156032,0.431241],\"confidence\":0.90234375,\"label\":7,\"ocr\":\"[.]jarvisFriday[.]Finishes 4\",\"resolution\":{\"first\":1080,\"second\":498}},{\"box\":[0.0014933981,0.43345976,0.10801934,0.5608499],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"#4/97\",\"resolution\":{\"first\":1080,\"second\":498}},{\"box\":[0.62430656,0.5308282,0.7166238,0.6133811],\"confidence\":0.7890625,\"label\":7,\"ocr\":\"[.]panther5103[.]Finishes3\",\"resolution\":{\"first\":1080,\"second\":498}},{\"box\":[0.62326294,0.7030234,0.7176674,0.79130334],\"confidence\":0.9453125,\"label\":7,\"ocr\":\"[.]pratyushtiwa[.]Finishes 2\",\"resolution\":{\"first\":1080,\"second\":498}},{\"box\":[0.0054619927,0.8052509,0.05708018,0.9562317],\"confidence\":0.98828125,\"label\":8,\"ocr\":\"Classic (TPP) Squad - Sanho\",\"resolution\":{\"first\":1080,\"second\":498}}]}]",
            type
        )
        for ((i, item) in list.withIndex()) {
            println(
                "result ${i + 1}:" + MachineConstants.machineInputValidator.validateRankKillGameInfo(
                    arrayListOf(item)
                ).squadScoring
            )
        }
    }


    @Test
    fun testRankIgnoreUnlikelyCasesFromKillsScreen() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#IN00[.]"
            )
        )
        val result1 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.rank).isNull()

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#IN 00[.]"
            )
        )
        val result2 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data2))
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.rank).isNull()

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#I00 [.]"
            )
        )
        val result3 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data3))
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.rank).isNull()

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "[.]1 1 /100"
            )
        )
        val result4 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data4))
        Truth.assertThat(result4.accept).isTrue()
        Truth.assertThat(result4.rank).isEqualTo("11")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "[.]#I0/ 0 [.]"
            )
        )
        val result5 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data5))
        Truth.assertThat(result5.accept).isTrue()
        Truth.assertThat(result5.rank).isEqualTo("10")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "[.]#I0/100 [.]"
            )
        )
        val result6 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data6))
        Truth.assertThat(result6.accept).isTrue()
        Truth.assertThat(result6.rank).isEqualTo("10")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data7 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "[.]#b0/100 [.]"
            )
        )
        val result7 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data7))
        Truth.assertThat(result7.accept).isTrue()
        Truth.assertThat(result7.rank).isEqualTo("b0")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data8 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "[.]##4T/64[.]"
            )
        )
        val result8 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data8))
        Truth.assertThat(result8.accept).isTrue()
        Truth.assertThat(result8.rank).isEqualTo("4T")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data9 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "[.]&7/64[.]"
            )
        )
        val result9 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data9))
        Truth.assertThat(result9.accept).isTrue()
        Truth.assertThat(result9.rank).isEqualTo("&7")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data10 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "[.]47/64[.]"
            )
        )
        val result10 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data10))
        Truth.assertThat(result10.accept).isTrue()
        Truth.assertThat(result10.rank).isEqualTo("47")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data11 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "[.]4&/64[.]"
            )
        )
        val result11 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data11))
        Truth.assertThat(result11.accept).isTrue()
        Truth.assertThat(result11.rank).isEqualTo("4&")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data12 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "[.]4 z/64[.]"
            )
        )
        val result12 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data12))
        Truth.assertThat(result12.accept).isTrue()
        Truth.assertThat(result12.rank).isEqualTo("4z")
    }

    @Test
    fun testForKillsReadOnlyOnceFromALabel() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data13 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(

                // here first we read 1, it should not get updated via 를

                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "[.]KINGS REDHOOD Finishes I[.]를",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "[.]4 z/64[.]"
            )
        )
        val result13 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data13))
        Truth.assertThat(result13.accept).isTrue()
        Truth.assertThat(result13.rank).isEqualTo("4z")
        Truth.assertThat(result13.kill).isEqualTo("1")
    }

    @Test
    fun testListOfKillsAgainstOutput() {
        val inputRanks = arrayListOf<Pair<String, Any?>>(
            "finishes 3" to "3",
            "Finishes 18" to "18",
            "Finishes l8" to "N",
            "Finishes 0" to "0",
            "Finishes D" to "0",
            "Finishes U" to "0",
            "Fnishes" to null,
            "Finishes" to null,
            "„Finishes 9" to "9",
            "Finishes" to null,
            "Finishes 1" to "1",
            "Finishes |" to "1",
            "Finishes 11" to "11",
            "UFishes r" to "r",
            "Einishes 4" to "4",
            "Fnishes 22" to "22",
            "Finishes 10" to "10",
            "Finishes 1O" to "10",
            "Finishes I0" to "10",
            "Finishes 0" to "0",
            "Finishes D" to "0",
            "Fnishes D" to "0",
            "Fnishes O" to "0",
            "Finishes 0" to "0",
            "Finishes 1" to "1",
            "Finishes D" to "0",
            "Finishes I" to "1",
            "Finishes O" to "0",
            "Finishes" to null,
            "Finishes D" to "0",
            "\\LFinishes D\\" to "0",
            "Fnishes2" to null,
            "Finishes0" to null,
            "\\\\FinishesI" to null,
            "\\\"Finishes G" to "6",
            "Finishes \\G" to "6",
            "Finishes \\\"G\\" to "6",
            "Finishes \\G\\" to "6",
        )

        for (input in inputRanks) {
            MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
            val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
                USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(),
                mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]${input.first}[.]")
            )
            val result1 =
                MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data1))
            if (result1.kill != input.second) LabelUtils.testLogGreen("---> ${input.first} to ${input.second}")
            Truth.assertThat(result1.kill).isEqualTo(input.second)
        }
    }


    @Test
    fun testListOfRankAgainstOutput() {
        val inputRanks = arrayListOf<Pair<String, Any?>>(
            "#52/98" to "52",
            "#5Z/98" to "5Z",
            "#6/52" to "6",
            "#35/52" to "35",
            " #35/52" to "35",
            " #3J/52" to "3J",
            " #3a/52" to "3a",
            "#26/52" to "26",
            " #26/52" to "26",
            " #2h/52" to "2h",
            " #26 /52" to "26",
            " #29/10" to "29",
            "#29/100" to "29",
            " #29 /00" to "29",
            " #10/64" to "10",
            " #I0/64" to "10",
            "#70/100" to "70",
            "#70/A00" to "70",
            "#70/AD0" to "70",
            "#7U/A00" to "70",
            " #77/00" to "77",
            "#100/i00" to "100",
            "#B1/i00" to "B1",
            "#7\\A00" to null,
            "#/1/I00" to null,
            "#71/100" to "71",
            "#71/A00" to "71",
            " #83 /100" to "83",
            " #B3 /100" to "B3",
            "#1/52" to "1",
            "#|/52" to "1",
            " #1/52" to "1",
            " #||/52" to "11",
            "#I00/100" to "100",
            " #IUL /O0" to "10L",
            " #E9/99" to "E9",
            " #Og/A9" to "0g",
            "I00/100" to "100",
            " t4/52" to "t4",
            " #/G4" to null,
            " H/64" to "H",
            " #0/64" to "0",
            " #O/E." to "0",
            " #U/64" to "0",
            " #R4 /99" to "R4",
            "&7/64" to "&7",
            " #30/52" to "30",
            " #3U/52" to "30",
            "&7\\/64" to "&7",
            " #30\\/52" to "30",
            "#3U\\/52" to "30",
            "\"66/86#" to "66",
            "\"66/10*\"" to "66",
            "\\\"66/10**\"" to "66",
            "\"66/10*\"" to "66",
            "\"66/10**\"" to "66",
            "This is a great place" to null,
            "#100 /100" to "100",
            "# 100 /100" to "100",
            "#100 /100" to "100",
            "#100 /100" to "100"
        )

        for ((i, input) in inputRanks.withIndex()) {
            MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
            val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
                USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(),
                mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]${input.first}[.]IV[.]Gold V")
            )
            val result1 =
                MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data1))
            Truth.assertThat(result1.accept).isTrue()

            if (result1.rank != input.second) LabelUtils.testLogGreen("---> ${input.first} to ${input.second}")
            Truth.assertThat(result1.rank).isEqualTo(input.second)
        }

        for ((i, input) in inputRanks.withIndex()) {
            MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
            val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
                USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                    BGMIConstants.GameLabels.RANK.ordinal to "[.]${input.first}[.]"
                )
            )
            val result1 =
                MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data1))
            if (result1.rank != input.second) LabelUtils.testLogGreen("---> ${input.first} to ${input.second}")
            Truth.assertThat(result1.rank).isEqualTo(input.second)
        }
    }


    @Test
    fun testRankIgnoreUnlikelyCasesFromRatingScreen() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]#IN00[.]IV[.]Gold V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"
            )
        )
        val result1 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
        Truth.assertThat(result1.rank).isEqualTo("1")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]#I N00[.]IV[.]Gold V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"
            )
        )
        val result2 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data2))
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
        Truth.assertThat(result2.rank).isNull()

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]#IN 00[.]IV[.]Gold V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"
            )
        )
        val result3 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data3))
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
        Truth.assertThat(result3.rank).isNull()

        // we made the `/` to be present in rank

        /*MachineLabelProcessor.clearValidatorScreenCache()
        val data4 = setOcrAgainstLabel(USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(BGMIConstants.GameLabels.RATING_RANK.ordinal to "[.]#I00 [.]IV[.]Gold V",
            BGMIConstants.GameLabels.CLASSIC_ALL_GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"))
        val result4 = validateRankRatingGameInfo(arrayListOf(data4))
        assertThat(result4.accept).isTrue()
        assertThat(result4.gameInfo).isEqualTo(GameInfo(type="Classic", view="TPP", group="squad", mode="Erangel"))
        assertThat(result4.rank).isEqualTo("100")*/

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]#I00 [.]IV[.]Gold V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"
            )
        )
        val result4 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data4))
        Truth.assertThat(result4.accept).isTrue()
        Truth.assertThat(result4.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
        Truth.assertThat(result4.rank).isNull()

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]#I/100 [.]IV[.]Gold V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"
            )
        )
        val result5 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data5))
        Truth.assertThat(result5.accept).isTrue()
        Truth.assertThat(result5.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
        Truth.assertThat(result5.rank).isEqualTo("1")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]#I0N [.]IV[.]Gold V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"
            )
        )
        val result6 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data6))
        Truth.assertThat(result6.accept).isTrue()
        Truth.assertThat(result6.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
        Truth.assertThat(result6.rank).isNull()

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data7 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]#I0/ 0 [.]IV[.]Gold V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"
            )
        )
        val result7 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data7))
        Truth.assertThat(result7.accept).isTrue()
        Truth.assertThat(result7.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
        Truth.assertThat(result7.rank).isEqualTo("10")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data8 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]f1/I0U [.]IV[.]Gold V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"
            )
        )
        val result8 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data8))
        Truth.assertThat(result8.accept).isTrue()
        Truth.assertThat(result8.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
        Truth.assertThat(result8.rank).isEqualTo("f1")

        // this case is also made to not include the value fetched
        /*MachineLabelProcessor.clearValidatorScreenCache()
        val data8 = setOcrAgainstLabel(USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(BGMIConstants.GameLabels.RATING_RANK.ordinal to "[.]f1/I0U [.]IV[.]Gold V",
            BGMIConstants.GameLabels.CLASSIC_ALL_GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"))
        val result8 = validateRankRatingGameInfo(arrayListOf(data8))
        assertThat(result8.accept).isTrue()
        assertThat(result8.gameInfo).isEqualTo(GameInfo(type="Classic", view="TPP", group="squad", mode="Erangel"))
        assertThat(result8.rank).isEqualTo("1")*/


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data9 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]1 1 /100 [.]IV[.]Gold V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"
            )
        )
        val result9 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data9))
        Truth.assertThat(result9.accept).isTrue()
        Truth.assertThat(result9.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
        Truth.assertThat(result9.rank).isEqualTo("11")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data10 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]#1 1 /100 [.]IV[.]Gold V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Squad-Erangel"
            )
        )
        val result10 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data10))
        Truth.assertThat(result10.accept).isTrue()
        Truth.assertThat(result10.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
        Truth.assertThat(result10.rank).isEqualTo("11")
    }

    @Test
    fun testFetchGameInfoFromRatingScreen() {

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "Dominator Legend",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Ranked Classic Mode (TPP) - Squad Miramar",
                BGMIConstants.GameLabels.RANK.ordinal to "#25 /97"
            )
        )
        val result1 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Miramar"
            )
        )
        Truth.assertThat(result1.initialTier).isEqualTo(
            "Dominator"
        )

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "Gold V1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (FPP) Squad-Livk",
                BGMIConstants.GameLabels.RANK.ordinal to "| 1/99"
            )
        )
        val result2 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data2))
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Livik"
            )
        )

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "Gold V1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) Solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "| 1/99"
            )
        )
        val result3 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data3))
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "solo", mode = "Erangel"
            )
        )

        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]CLEAR[.]UAZ (347[.]3234/3300[.]Diamond V",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Ranked Classic Made (TPP) - Solo - Livik",
                BGMIConstants.GameLabels.RANK.ordinal to "#| /51"
            )
        )
        val result4 =
            MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data4))
        Truth.assertThat(result4.accept).isTrue()
        Truth.assertThat(result4.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "solo", mode = "Livik"
            )
        )
        Truth.assertThat(result4.finalTier).isEqualTo("Diamond V")
    }


    @Test
    fun testValidateRankKillGameInfo() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.RANK.ordinal to "-#1/52 Ranked Cla",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Ranked Classic Made (TPP) - Sala - Livik",
                BGMIConstants.GameLabels.CLASSIC_RANK_GAME_INFO.ordinal to "t52.Winner Wianer Bhicken Dinags Ranked Classic Mode (TPP) - Solo -Livik",
                BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to ".]TURGINAL[.]1634/1700[.]Bronze Il"
            )
        )
        val resul4 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data4))
        Truth.assertThat(resul4.accept).isTrue()
        Truth.assertThat(resul4.rank).isEqualTo("1")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classlc (TP) solo-Erael",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val resul1 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data1))
        Truth.assertThat(resul1.accept).isTrue()
        Truth.assertThat(resul1.kill).isEqualTo("5")
        Truth.assertThat(Json.encodeToString(resul1.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(resul1.rank).isEqualTo("45")

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val resul2 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data2))
        Truth.assertThat(resul2.accept).isTrue()
        Truth.assertThat(resul2.kill).isEqualTo("5")
        Truth.assertThat(Json.encodeToString(resul2.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(resul2.rank).isEqualTo("45")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Clasic (TPP solo-livk",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val resul3 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data3))
        Truth.assertThat(resul3.accept).isTrue()
        Truth.assertThat(resul3.kill).isEqualTo("5")
        Truth.assertThat(Json.encodeToString(resul3.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Livik\"}")
        Truth.assertThat(resul3.rank).isEqualTo("45")


    }

    @Test
    fun testValidateRankKillGameInfoWithList() {

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to " (TP) solo-Eranel",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classlc (TP) solo-",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classlc  solo-Eranel",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classlc (TP) solo-",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classlc (TP) solo-",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )

        val resul1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3, data4, data5
            )
        )
        Truth.assertThat(resul1.accept).isTrue()
        Truth.assertThat(resul1.kill).isEqualTo("5")
        Truth.assertThat(Json.encodeToString(resul1.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(resul1.rank).isEqualTo("45")
    }


    @Test
    fun testValidateRankKillGameINfoWithEnoughClassics() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to " (TP) Suad-Eranel",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to " (TPP) Suad-",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic  -Eanel",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to " (TPP) Suad-",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )

        // here not all of them contain classic, if we get enough classics..we should start extracting useful information from the ones without classic also
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic Suad-",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )

        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]inish 5[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to " (TPP) Suad-",
                BGMIConstants.GameLabels.RANK.ordinal to "45/99"
            )
        )


        val resul1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3, data4, data5, data6
            )
        )
        Truth.assertThat(resul1.accept).isTrue()
        Truth.assertThat(resul1.kill).isEqualTo("5")
        Truth.assertThat(Json.encodeToString(resul1.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(resul1.rank).isEqualTo("45")
    }


    @Test
    fun testResultWithList() {
        //UserHandler.originalBGBICharacterID = "pratyushtiwa"

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishe|#[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "Rank # 1 |"
            )
        )

        val listOfData1 = arrayListOf<ImageResultJsonFlat>()
        for (i in 1..MAX_BUFFER_LIMIT) {
            listOfData1.add(data1)
        }
        val result1 =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.kill).isEqualTo(null)
    }

    @Test
    fun test_game_result_with_multiple_inputs_is_valid() {
        //UserHandler.originalBGBICharacterID = "pratyushtiwa"


        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME2_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.RANK.ordinal to "32/99",
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes 43[.]"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME2_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.RANK.ordinal to "2/99",
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes 43[.]"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME2_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.RANK.ordinal to "32/ 99",
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes 3[.]"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME2_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.RANK.ordinal to "32/9",
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiwa[.]finishes43[.]"
            )
        )

        val result1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3, data4
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.rank).isEqualTo("32")
    }

    @Test
    fun test_id_with_multiple_inputs_is_valid() {
        //UserHandler.originalBGBICharacterID = "pratyushtiwa"


        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "pratyushtiwa[.]ID:1234567891O[.]")
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "pratyushtiwa[.]ID1678910[.]")
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "ID 1234589i0[.]pratyushtiwa[.]")
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "pratyushtiwa[.]ID12345O789DD[.]")
        )
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "ID#:##1234 5O789DD pratyushtiwa[.]")
        )
        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "ID:1234567891D[.]pratyushtiwa[.]")
        )

        val result1 = MachineConstants.machineInputValidator.validateProfileId(
            arrayListOf(
                data1, data2, data3, data4, data5, data6
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.id).isEqualTo("12345678910")
    }

    @Test
    fun test_id_level_with_multiple_inputs_is_valid() {

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "Player Lv. 20",
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to "ID:1234567891O"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "Plajijiyer Lhjiv. 2o",
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to "ID 1234589i0"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "ayer Lv 2 0",
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to "ID:1234567891O"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to "ayer Lv ",
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to "ID1234567891O"
            )
        )

        val result1 = MachineConstants.machineInputValidator.validateProfileIdAndLevel(
            arrayListOf(
                data1, data2, data3, data4
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.id).isEqualTo("12345678910")
        Truth.assertThat(result1.level).isEqualTo("20")
    }

    @Test
    fun test_waiting_with_multiple_inputs_is_valid() {

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "Match starts in 4 seconds")
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "mat str 21")
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "atchstarts in 4 seconds")
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "atchstarts")
        )

        val result2 = MachineConstants.machineInputValidator.validateWaiting(
            arrayListOf(
                data1, data2, data3, data4
            )
        )
        Truth.assertThat(result2.accept).isTrue()
    }

    @Test
    fun test_waiting_with_multiple_inputs_is_not_valid() {

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "M in nds")
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "mat str ")
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "atchsn 4 se")
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_WAITING_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal to "atchsts")
        )

        val result1 = MachineConstants.machineInputValidator.validateWaiting(
            arrayListOf(
                data1, data2, data3, data4
            )
        )
        Truth.assertThat(result1.accept).isFalse()
    }

    @Test
    fun test_validate_results_with_multiple_input() {
        //UserHandler.originalBGBICharacterID = "GBG Roco"

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "GBG Roco[.] finish 5",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 3/ 99"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "GBG Roco[.] finish 5",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "23/99"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "GBG Roco[.] finish 5",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "23  /99"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "GBG Roco[.] inish 5",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "23/99"
            )
        )
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "GBG Roc[.] finish",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 3 /99"
            )
        )

        val result1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3, data4, data5
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.kill).isEqualTo("5")
        Truth.assertThat(Json.encodeToString(result1.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(result1.rank).isEqualTo("23")
    }

    @Test
    fun test_validate_results_with_multiple_input_() {
        //UserHandler.originalBGBICharacterID = "pratyushtiwa"
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()

        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "28/99"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "28 /99"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 8/99"
            )
        )


        val result1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.kill).isEqualTo("1")
        Truth.assertThat(Json.encodeToString(result1.gameInfo))
            .isEqualTo("{\"type\":\"Classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"Erangel\"}")
        Truth.assertThat(result1.rank).isEqualTo("28")

    }

    @Test
    fun test_sent_for_auto_ml_if_character_found() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish B[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "28/99"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish  B[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "28 /99"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish B[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 8/99"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish B[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 8/99"
            )
        )
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish B[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 8/99"
            )
        )

        val result1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3, data4, data5
            )
        )
        Truth.assertThat(result1.accept).isTrue()
    }

    @Test
    fun test_not_sent_for_auto_ml() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "28/99"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish  1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "28 /99"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 8/99"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 8/99"
            )
        )

        val result1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3, data4
            )
        )
        Truth.assertThat(result1.accept).isTrue()
    }

    @Test
    fun test_sent_for_auto_ml_if_missing_kills() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "28/99"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish [.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "28 /99"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish B[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 8/99"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish g[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 8/99"
            )
        )
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish [.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "2 8/99"
            )
        )

        val result1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3, data4, data5
            )
        )
        Truth.assertThat(result1.accept).isTrue()
    }

    @Test
    fun test_sent_for_auto_ml_if_rank_0() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 2[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "00/99"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 2[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "00 /99"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 2[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "0 0/99"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 2[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "00/99"
            )
        )
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 2[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "00 /99"
            )
        )

        val result1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3, data4, data5
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.kill).isEqualTo("2")
        Truth.assertThat(result1.rank).isEqualTo("0")
    }

    @Test
    fun test_sent_for_auto_ml_if_rank_greater_than_100() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 2[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "102/99"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 2[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "102 /99"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 2[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "102/99"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 2[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "102/99"
            )
        )
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "pratyushtiw[.]finish 2[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "102/99"
            )
        )

        val result1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3, data4, data5
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.kill).isEqualTo("2")
        Truth.assertThat(result1.rank).isNull()
    }

    @Test
    fun test_validate_gameInfo() {
        val data1 = "Classic (TPP) Squad-Erangel"
        val result1 = MachineConstants.machineInputValidator.validateGameInfo(data1)
        Truth.assertThat(get_game_info(result1)).isEqualTo("Classic-TPP-squad :Erangel")

        val data2 = "Classic (TPP) Sola-Erangel"
        val result2 = MachineConstants.machineInputValidator.validateGameInfo(data2)
        Truth.assertThat(get_game_info(result2)).isEqualTo("Classic-TPP-solo :Erangel")

        val data3 = "assic (TPP) Sola-Erangel"
        val result3 = MachineConstants.machineInputValidator.validateGameInfo(data3)
        Truth.assertThat(get_game_info(result3)).isEqualTo("Classic-TPP-solo :Erangel")

        val data4 = "assic (TP P Sola - Erangel"
        val result4 = MachineConstants.machineInputValidator.validateGameInfo(data4)
        Truth.assertThat(get_game_info(result4)).isEqualTo("Classic-TPP-solo :Erangel")

        val data5 = "assic (TP P ola - Erangel"
        val result5 = MachineConstants.machineInputValidator.validateGameInfo(data5)
        Truth.assertThat(get_game_info(result5)).isEqualTo("Classic-TPP-solo :Erangel")

        val data6 = "assic (TP Psola - Erangel"
        val result6 = MachineConstants.machineInputValidator.validateGameInfo(data6)
        Truth.assertThat(get_game_info(result6)).isEqualTo("Classic-TPP-solo :Erangel")

        val data7 = "Classic-TPP-solo :Erangel"
        val result7 = MachineConstants.machineInputValidator.validateGameInfo(data7)
        Truth.assertThat(get_game_info(result7)).isEqualTo("Classic-TPP-solo :Erangel")

        val data8 = "Classic-ascxaw :Erangel"
        val result8 = MachineConstants.machineInputValidator.validateGameInfo(data8)
        Truth.assertThat(get_game_info(result8)).isEqualTo("Classic-- :Erangel")

    }

    @Test
    fun test_game_info_for_maps() {
        val data1 = "Classic-TPP-solo :Ergel"
        val result1 = MachineConstants.machineInputValidator.validateGameInfo(data1)
        Truth.assertThat(get_game_info(result1)).isEqualTo("Classic-TPP-solo :Erangel")

        val data2 = "Classic-TPP-solo :Ergel"
        val result2 = MachineConstants.machineInputValidator.validateGameInfo(data2)
        Truth.assertThat(get_game_info(result2)).isEqualTo("Classic-TPP-solo :Erangel")

        val data3 = "Classic-TPP-solo :Livk"
        val result3 = MachineConstants.machineInputValidator.validateGameInfo(data3)
        Truth.assertThat(get_game_info(result3)).isEqualTo("Classic-TPP-solo :Livik")

        val data4 = "Classic-TPP-solo :KARAKIN"
        val result4 = MachineConstants.machineInputValidator.validateGameInfo(data4)
        Truth.assertThat(get_game_info(result4)).isEqualTo("Classic-TPP-solo :Karakin")

        val data5 = "Classic-TPP-solo :KENDI"
        val result5 = MachineConstants.machineInputValidator.validateGameInfo(data5)
        Truth.assertThat(get_game_info(result5)).isEqualTo("Classic-TPP-solo :Vikendi")

        val data6 = "Classic-TPP-solo :Sah0k"
        val result6 = MachineConstants.machineInputValidator.validateGameInfo(data6)
        Truth.assertThat(get_game_info(result6)).isEqualTo("Classic-TPP-solo :Sanhok")
    }


    private fun get_game_info(info: Array<String>): String {
        val (type, mode, group, name) = info
        return "$type-$mode-$group :$name"
    }

    @Test
    fun test_validate_start() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            START_JSON.obj(), mapOf(BGMIConstants.GameLabels.CLASSIC_START.ordinal to "START")
        )
        val result1 = MachineConstants.machineInputValidator.validateStart(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()

        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            START_JSON.obj(), mapOf(BGMIConstants.GameLabels.CLASSIC_START.ordinal to "STAR")
        )
        val result2 = MachineConstants.machineInputValidator.validateStart(arrayListOf(data2))
        Truth.assertThat(result2.accept).isTrue()

        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            START_JSON.obj(), mapOf(BGMIConstants.GameLabels.CLASSIC_START.ordinal to "TART")
        )
        val result3 = MachineConstants.machineInputValidator.validateStart(arrayListOf(data3))
        Truth.assertThat(result3.accept).isTrue()

        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            START_JSON.obj(), mapOf(BGMIConstants.GameLabels.CLASSIC_START.ordinal to "StaRT")
        )
        val resul4 = MachineConstants.machineInputValidator.validateStart(arrayListOf(data4))
        Truth.assertThat(resul4.accept).isTrue()
    }

    @Test
    fun test_validate_login() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            LOGIN_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.GLOBAL_LOGIN.ordinal to "Twitter login with facebook")
        )
        val result1 = MachineConstants.machineInputValidator.validateLogin(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()

        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            LOGIN_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.GLOBAL_LOGIN.ordinal to "witter flogin with faceb")
        )
        val result2 = MachineConstants.machineInputValidator.validateLogin(arrayListOf(data2))
        Truth.assertThat(result2.accept).isTrue()
    }

    @Test
    fun test_validate_start_with_start_cancel_matching() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            START_JSON.obj(), mapOf(BGMIConstants.GameLabels.CLASSIC_START.ordinal to "START")
        )
        val result1 = MachineConstants.machineInputValidator.validateStart(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()

        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            START_JSON.obj(), mapOf(BGMIConstants.GameLabels.CLASSIC_START.ordinal to "CANCEL")
        )
        val result2 = MachineConstants.machineInputValidator.validateStart(arrayListOf(data2))
        Truth.assertThat(result2.accept).isTrue()

        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            START_JSON.obj(), mapOf(BGMIConstants.GameLabels.CLASSIC_START.ordinal to "MatChiNG")
        )
        val result3 = MachineConstants.machineInputValidator.validateStart(arrayListOf(data3))
        Truth.assertThat(result3.accept).isTrue()
    }

    @Test
    fun test_validate_with_multiple_labels() {
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            START_JSON.obj(), mapOf(BGMIConstants.GameLabels.CLASSIC_START.ordinal to "START")
        )
        val result1 = MachineConstants.machineInputValidator.validateWithNoOcr(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()

        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            LOGIN_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.GLOBAL_LOGIN.ordinal to "twitterfloginwithfacebook")
        )
        val result2 = MachineConstants.machineInputValidator.validateWithNoOcr(arrayListOf(data2))
        Truth.assertThat(result2.accept).isTrue()

        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_ID_DETAILS_and_LEVEL_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal to TestUser1.idNumeric,
                BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal to TestUser1.level
            )
        )
        val result3 =
            MachineConstants.machineInputValidator.validateWithNoOcr(arrayListOf(data3, data3))
        Truth.assertThat(result3.accept).isTrue()
    }

    @Test
    fun test_validate_rating() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "100/ 99[.]IV Gold IV[.]")
        )
        val result1 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data1, data1
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.rank).isEqualTo("100")
        Truth.assertThat(result1.initialTier).isEqualTo("Gold IV")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "31/ 99[.]old IV[.]")
        )
        val result2 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data2, data2
            )
        )
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.rank).isEqualTo("31")
        Truth.assertThat(result2.initialTier).isEqualTo("Gold IV")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "34/ 99[.]gol II[.]")
        )
        val result3 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data3, data3
            )
        )
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.rank).isEqualTo("34")
        Truth.assertThat(result3.initialTier).isEqualTo("Gold II")


        // this case we need to ignore, breaks, this is un expected data
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "#2\\/50 WARNINGI RED ZONE HAS STARTED! Platinum lI II")
        )
        val result4 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data4, data4
            )
        )
        Truth.assertThat(result4.accept).isTrue()
        Truth.assertThat(result4.rank).isNull()
        Truth.assertThat(result4.initialTier).isNull()


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "82/9 9[.]Ace II[.]")
        )
        val result5 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data5, data5
            )
        )
        Truth.assertThat(result5.accept).isTrue()
        Truth.assertThat(result5.rank).isEqualTo("82")
        Truth.assertThat(result5.initialTier).isEqualTo("Ace")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "43/ 99[.]ce lv[.]")
        )
        val result6 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data6, data6
            )
        )
        Truth.assertThat(result6.accept).isTrue()
        Truth.assertThat(result6.rank).isEqualTo("43")
        Truth.assertThat(result6.initialTier).isEqualTo("Ace")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data7 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "18/99[.]Bronze V VII[.]")
        )
        val result7 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data7, data7
            )
        )
        Truth.assertThat(result7.accept).isTrue()
        Truth.assertThat(result7.rank).isEqualTo("18")
        Truth.assertThat(result7.initialTier).isEqualTo("Bronze VII")
    }


    @Test
    fun test_validate_rating_with_rank() {

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "19/ 99[.] IV Gold IV[.]")
        )
        val result1 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data1, data1
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.initialTier).isEqualTo("Gold IV")
        Truth.assertThat(result1.rank).isEqualTo("19")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "31/ 99[.]old IV[.]")
        )
        val result2 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data2, data2
            )
        )
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.initialTier).isEqualTo("Gold IV")
        Truth.assertThat(result2.rank).isEqualTo("31")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "34/ 99[.]gol II[.]")
        )
        val result3 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data3, data3
            )
        )
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.initialTier).isEqualTo("Gold II")
        Truth.assertThat(result3.rank).isEqualTo("34")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "# 3/99[.]latinu II[.]")
        )
        val result4 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data4, data4
            )
        )
        Truth.assertThat(result4.accept).isTrue()
        Truth.assertThat(result4.initialTier).isEqualTo("Platinum II")
        Truth.assertThat(result4.rank).isEqualTo("3")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "#82/9 9[.]Ace II[.]")
        )
        val result5 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data5, data5
            )
        )
        Truth.assertThat(result5.accept).isTrue()
        Truth.assertThat(result5.initialTier).isEqualTo("Ace")
        Truth.assertThat(result5.rank).isEqualTo("82")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "#43/ 99[.]ce lv[.]")
        )
        val result6 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data6, data6
            )
        )
        Truth.assertThat(result6.accept).isTrue()
        Truth.assertThat(result6.initialTier).isEqualTo("Ace")
        Truth.assertThat(result6.rank).isEqualTo("43")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data7 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "18/99[.]Bronze V[.]")
        )
        val result7 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data7, data7
            )
        )
        Truth.assertThat(result7.accept).isTrue()
        Truth.assertThat(result7.initialTier).isEqualTo("Bronze V")
        Truth.assertThat(result7.rank).isEqualTo("18")
    }


    @Test
    fun test_validate_rating_with_rank_for_ace() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "19/ 99[.] Ace[.]")
        )

        val result1 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data1, data1
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.initialTier).isEqualTo("Ace")
        Truth.assertThat(result1.rank).isEqualTo("19")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "31/ 99[.]Ace Master[.]")
        )

        val result2 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data2, data2
            )
        )
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.initialTier).isEqualTo("Ace Master")
        Truth.assertThat(result2.rank).isEqualTo("31")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "31/ 99[.]Ace Dominator[.]")
        )

        val result3 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data3, data3
            )
        )
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.initialTier).isEqualTo("Ace Dominator")
        Truth.assertThat(result3.rank).isEqualTo("31")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "31/ 99[.]Ace Mast[.]")
        )

        val result4 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data4, data4
            )
        )
        Truth.assertThat(result4.accept).isTrue()
        Truth.assertThat(result4.initialTier).isEqualTo("Ace Master")
        Truth.assertThat(result4.rank).isEqualTo("31")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "31/ 99[.]Ace Domina|0r[.]")
        )

        val result5 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data5, data5
            )
        )
        Truth.assertThat(result5.accept).isTrue()
        Truth.assertThat(result5.initialTier).isEqualTo("Ace Dominator")
        Truth.assertThat(result5.rank).isEqualTo("31")

    }


    @Test
    fun test_validate_rating_with_rank_for_conqueror() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "19/ 99[.] Conqueror[.]")
        )

        val result1 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data1, data1
            )
        )
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.initialTier).isEqualTo("Conqueror")
        Truth.assertThat(result1.rank).isEqualTo("19")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "31/ 99[.]|v Con9uero[.]")
        )

        val result2 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data2, data2
            )
        )
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.initialTier).isEqualTo("Conqueror")
        Truth.assertThat(result2.rank).isEqualTo("31")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "31/ 99[.] Con9uero[.]")
        )

        val result3 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data3, data3
            )
        )
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.initialTier).isEqualTo("Conqueror")
        Truth.assertThat(result3.rank).isEqualTo("31")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal to "31/ 99[.] Coqueror V/[.]")
        )

        val result4 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            arrayListOf(
                data4, data4
            )
        )
        Truth.assertThat(result4.accept).isTrue()
        Truth.assertThat(result4.initialTier).isEqualTo("Conqueror")
        Truth.assertThat(result4.rank).isEqualTo("31")
    }

    @Test
    fun testGetGameInfoFormHistory() {
        val history1 = "#25 Classic (TPP) Squad-Erangel I1/14 23:24 #95 0"
        val result = MachineConstants.machineInputValidator.getGameInfoFromHistory(history1)
        Truth.assertThat(result.accept).isTrue()
        Truth.assertThat(result.gameInfo).isEqualTo(
            GameInfo(
                type = "Classic", view = "TPP", group = "squad", mode = "Erangel"
            )
        )
    }

//    @Test
//    fun test_get_teamRankFromHistory() {
//        val history1 = "#25 Classic (TPP) Squad-Erangel I1/14 23:24 #95 0"
//        val result1 = MachineConstants.machineInputValidator.getTeamRankFromHistory(history1)
//        Truth.assertThat(result1.accept).isTrue()
//        Truth.assertThat(result1.teamRank).isEqualTo("25")
//
//        val history2 = "       #5 Classic (TPP) Squad-Erangel I1/14 23:24 #95 0"
//        val result2 = MachineConstants.machineInputValidator.getTeamRankFromHistory(history2)
//        Truth.assertThat(result2.accept).isTrue()
//        Truth.assertThat(result2.teamRank).isEqualTo("5")
//
//        val history3 = "# 0lassic (TPP) Squad-Erangel I1/14 23:24 #95 0"
//        val result3 = MachineConstants.machineInputValidator.getTeamRankFromHistory(history3)
//        Truth.assertThat(result3.accept).isFalse()
//
//        val history4 = "#2 0lassic (TPP) Squad-Erangel I1/14 23:24 #95 0"
//        val result4 = MachineConstants.machineInputValidator.getTeamRankFromHistory(history4)
//        Truth.assertThat(result4.accept).isTrue()
//        Truth.assertThat(result4.teamRank).isEqualTo("2")
//
//        val history5 = "#0lassic (TPP) Squad-Erangel I1/14 23:24 #95 0"
//        val result5 = MachineConstants.machineInputValidator.getTeamRankFromHistory(history5)
//        Truth.assertThat(result5.accept).isFalse()
//
//        val history6 = "# Classic (TPP) Squad-Erangel I1/14 23:24 #95 0"
//        val result6 = MachineConstants.machineInputValidator.getTeamRankFromHistory(history6)
//        Truth.assertThat(result6.accept).isFalse()
//
//        val history7 = "#2 3 Classic (TPP) Squad-Erangel I1/14 23:24 #95 0"
//        val result7 = MachineConstants.machineInputValidator.getTeamRankFromHistory(history7)
//        Truth.assertThat(result7.accept).isTrue()
//        Truth.assertThat(result7.teamRank).isEqualTo("23")
//
//        val history8 = "#2 0lassic (TPP) Squad-Erangel I1/14 23:24 #95 0"
//        val result8 = MachineConstants.machineInputValidator.getTeamRankFromHistory(history8)
//        Truth.assertThat(result8.accept).isTrue()
//        Truth.assertThat(result8.teamRank).isEqualTo("2")
//    }


    @Test
    fun test_get_playersDefeatedFromHistory() {
        val history1 = "#25 Classic (TPP) Squad-Erangel I1/14 23:24 #95 0"
        val result1 = MachineConstants.machineInputValidator.getPlayersDefeatedFromHistory(history1)
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.kill).isEqualTo("0")

        val history2 = "#25 Classic (TPP) Squad-Erangel I1/14 23:24 #95 99"
        val result2 = MachineConstants.machineInputValidator.getPlayersDefeatedFromHistory(history2)
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.kill).isEqualTo("99")

        val history3 = "#25 Classic (TPP) Squad-Erangel I1/14 23:24 #95 "
        val result3 = MachineConstants.machineInputValidator.getPlayersDefeatedFromHistory(history3)
        Truth.assertThat(result3.accept).isFalse()

        val history4 = "#25 Classic (TPP) Squad-Erangel I1/14 23:24 #9599"
        val result4 = MachineConstants.machineInputValidator.getPlayersDefeatedFromHistory(history4)
        Truth.assertThat(result4.accept).isFalse()

        val history5 = "#25 Classic (TPP) Squad-Erangel I1/14 23:24 #9 5 2"
        val result5 = MachineConstants.machineInputValidator.getPlayersDefeatedFromHistory(history5)
        Truth.assertThat(result5.accept).isTrue()
        Truth.assertThat(result5.kill).isEqualTo("2")
    }


    @Test
    fun test_getRankFromHistory() {
        val history1 = "#25 Classic (TPP) Squad-Erangel I1/14 23:24 #95 0"
        val result1 = MachineConstants.machineInputValidator.getRankFromHistory(history1)
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.rank).isEqualTo("95")

        val history2 = "#25 Classic (TPP) Squad-Erangel I1/14 23:24  #5  99"
        val result2 = MachineConstants.machineInputValidator.getRankFromHistory(history2)
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.rank).isEqualTo("5")
    }

    @Test
    fun test_get_history_game_timeStamp() {
        val history1 = "#25 Classic (TPP) Squad-Erangel I1/14 23:24 #95 0"
        val result1 = MachineConstants.machineInputValidator.getGameTimeStampFromHistory(history1)
        Truth.assertThat(result1).isEqualTo(arrayOf("11", "14", "23", "24"))
    }

    @Test
    fun test_get_user_name() {
        //UserHandler.originalBGMIId = "1234567891O"
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "pratyushtiwa [.] ID:1234567891O")
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "pratyushtiwa [.] ID1678910")
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "ID 1234589i0 [.] pratyushtiwa  ")
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "pratyushtiwa [.] ID12345O789DD")
        )
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "ID#:##1234 5O789DD [.] pratyushtiwa")
        )
        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.] pratyushtiwa [.] D:1234567891D ")
        )

        /*
        [.]remworldjan[.]55558418901
        [.]ramworld jan[.]555584189OI
        [.]-DRAXX[.]5404D94564
        [.]-DRAXX[.]54D4094564
        [.]xtreme AIM[.]56086O3749
        [.]xtreme AIM[.]5608 603749
        [.]-0| TAPAN[.]5350150161
        [.]F0| TAIPAN[.]5350150161
        [.]ÇRimỀS2 FOSIL[.]5995724816
        [.]ÇRimĚ52FOSIL[.]5995724816
        */

        val result1 = MachineConstants.machineInputValidator.validateProfileId(
            arrayListOf(
                data1, data2, data3, data4, data5, data6
            )
        )
        val result2 = MachineConstants.machineInputValidator.validateCharacterId(
            arrayListOf(
                data1, data2, data3, data4, data5, data6
            ), originalBGMIId = "1234567891O"
        )

        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.id).isEqualTo("12345678910")

        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.charId).isEqualTo("pratyushtiwa")
    }


    @Test
    fun test_get_user_name_1() {
        //UserHandler.originalBGMIId = "55558418901"
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]remworldjan[.]55558418901")
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]ramworld jan[.]555584189OI")
        )

        val result1 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data1, data2))
        val result2 = MachineConstants.machineInputValidator.validateCharacterId(
            arrayListOf(data1, data2), originalBGMIId = "55558418901"
        )

        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.id).isEqualTo("55558418901")

        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.charId).isEqualTo("remworldjan")
    }


    @Test
    fun test_get_user_name_2() {
        //UserHandler.originalBGMIId = "5404094564"
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]-DRAXX[.]5404D94564")
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]-DRAXX[.]5404D94564")
        )

        val result1 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data1, data2))
        val result2 = MachineConstants.machineInputValidator.validateCharacterId(
            arrayListOf(data1, data2), originalBGMIId = "5404094564"
        )

        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.id).isEqualTo("5404094564")

        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.charId).isEqualTo("-DRAXX")
    }


    @Test
    fun test_get_user_name_3() {
        //UserHandler.originalBGMIId = "5995724816"
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]ÇRimỀS2 FOSIL[.]5995724816")
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(BGMIConstants.GameLabels.PROFILE_ID.ordinal to "[.]ÇRimĚ52FOSIL[.]5995724816")
        )

        val result1 =
            MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data1, data2))
        val result2 = MachineConstants.machineInputValidator.validateCharacterId(
            arrayListOf(data1, data2), originalBGMIId = "5995724816"
        )

        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.id).isEqualTo("5995724816")

        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.charId).isEqualTo("ÇRimỀS2FOSIL")
    }


    @Test
    fun test_get_kills_for_correct_user_for_team_matches() {
        val result1 = MachineConstants.machineInputValidator.compareUserId(
            "pratyushtiw[.]finish 1[.]", "pratyushtiwa"
        )
        Truth.assertThat(result1).isTrue()

        val result2 = MachineConstants.machineInputValidator.compareUserId(
            "pratyush[.]finish 1[.]", "pratyushtiwa"
        )
        Truth.assertThat(result2).isFalse()

        val result3 = MachineConstants.machineInputValidator.compareUserId(
            "pritammishra[.]finish 1[.]", "pratyushtiwa"
        )
        Truth.assertThat(result3).isFalse()

        val result4 = MachineConstants.machineInputValidator.compareUserId(
            "proty0shtiwa[.]finish 1[.]", "pratyushtiwa"
        )
        Truth.assertThat(result4).isTrue()

        val result5 = MachineConstants.machineInputValidator.compareUserId(
            "finisherpratyush[.]finish 1[.]", "finisherpratyush"
        )
        Truth.assertThat(result5).isTrue()
    }

    @Test
    fun test_send_for_auto_ml() {

        //if number_labels <= 3 return true
        val dataA1 = Pair("1", "")
        val dataB1 = Pair("11", "")
        val result1: MachineResult.Builder = MachineResult.Builder()
        val shouldSendForAutoMl1 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            result1, arrayListOf(dataA1, dataA1, dataA1)
        )
        Truth.assertThat(shouldSendForAutoMl1).isTrue()
        Truth.assertThat(result1.hasLowConfidence).isTrue()
        Truth.assertThat(result1.noClearMajority).isFalse()

        val dataA5 = Pair("1", "5")
        val dataB5 = Pair("11", "4")
        val dataC5 = Pair("12", "11")

        val result5: MachineResult.Builder = MachineResult.Builder()
        val shouldSendForAutoMl5 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            result5, arrayListOf(
                dataA5,
                dataB5,
                dataA5,
                dataB5,
                dataA5,
                dataB5,
                dataA5,
                dataB5,
                dataA5,
                dataC5,
                dataC5,
                dataC5,
                dataC5,
                dataC5,
                dataC5,
                dataC5,
                dataC5,
                dataC5,
                dataC5,
                dataC5
            )
        )
        Truth.assertThat(shouldSendForAutoMl5).isFalse()
        Truth.assertThat(result5.hasLowConfidence).isFalse()
        Truth.assertThat(result5.noClearMajority).isFalse()


        val datac1 = Pair("1", "")
        val datac2 = Pair("11", "")
        val result0: MachineResult.Builder = MachineResult.Builder()

        // 1 : 6,  11: 3
        val shouldSendForAutoMl0 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            result0,
            arrayListOf(datac1, datac1, datac1, datac1, datac1, datac1, datac2, datac2, datac2)
        )
        Truth.assertThat(shouldSendForAutoMl0).isTrue()

        // 1:5, 11: 2

        val shouldSendForAutoMl6 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            result0, arrayListOf(datac1, datac1, datac1, datac1, datac1, datac2, datac2)
        )
        Truth.assertThat(shouldSendForAutoMl6).isTrue()

        // 1:8 11: 3
        val shouldSendForAutoMl7 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            result0, arrayListOf(
                datac1,
                datac1,
                datac1,
                datac1,
                datac1,
                datac1,
                datac1,
                datac1,
                datac2,
                datac2,
                datac2
            )
        )
        Truth.assertThat(shouldSendForAutoMl7).isTrue()

    }


    @Test
    fun test_not_send_for_auto_ml() {


        val dataA1 = Pair("1", "")
        val dataB1 = Pair("11", "")
        val result1: MachineResult.Builder = MachineResult.Builder()
        val shouldSendForAutoMl1 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            result1, arrayListOf(
                dataA1,
                dataA1,
                dataA1,
                dataA1,
                dataB1,
                dataB1,
                dataB1,
                dataB1,
                dataB1,
                dataB1,
                dataB1
            )
        )
        Truth.assertThat(shouldSendForAutoMl1).isFalse()
        Truth.assertThat(result1.hasLowConfidence).isFalse()
        Truth.assertThat(result1.noClearMajority).isFalse()

        val dataA2 = Pair("1", "")
        val dataB2 = Pair("11", "")
        val result2: MachineResult.Builder = MachineResult.Builder()
        val shouldSendForAutoMl2 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            result2, arrayListOf(dataA2, dataA2, dataA2, dataA2, dataA2, dataA2, dataA2, dataA2)
        )
        Truth.assertThat(shouldSendForAutoMl2).isFalse()
        Truth.assertThat(result2.hasLowConfidence).isFalse()
        Truth.assertThat(result2.noClearMajority).isFalse()
    }

    @Test
    fun test_sent_for_automl_helper() {
        val result1 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            MachineResult.Builder(),
            arrayListOf<Pair<String, String>>(Pair("B", "B"), Pair("6", "6"), Pair("B", "B"))
        )
        Truth.assertThat(result1).isTrue()
        LabelUtils.testLogGreen("B 6 B  -> True")

        val result2 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            MachineResult.Builder(), arrayListOf<Pair<String, String>>(
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("6", "6"),
            )
        )
        Truth.assertThat(result2).isFalse()
        LabelUtils.testLogGreen("6 6 6 6 6 6-> False")

        val result3 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            MachineResult.Builder(),
            arrayListOf<Pair<String, String>>(Pair("6", "6"), Pair("6", "6"), Pair("B", "B"))
        )
        Truth.assertThat(result3).isTrue()
        LabelUtils.testLogGreen("6 6 B  -> True")

        val result4 = MachineConstants.machineInputValidator.sendForAutoMLHelper(
            MachineResult.Builder(), arrayListOf<Pair<String, String>>(
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("6", "6"),
                Pair("B", "B")
            )
        )
        Truth.assertThat(result4).isFalse()
        LabelUtils.testLogGreen("6 6 6 6 6 6 6 B  -> False")
    }

    // Secondary tests
    @Test
    fun testScoringEncodingDecoding() {
        val finalSquadScoringArray = com.google.gson.JsonArray()
        for (i in 0..3) {
            val squadMember = com.google.gson.JsonObject()
            squadMember.addProperty("username", "Some user $i[.]extra".split("[.]")[0])
            squadMember.addProperty("kills", i)
            finalSquadScoringArray.add(squadMember)
        }

        LabelUtils.testLogGreen(finalSquadScoringArray.toString())
        val squadPlayersArray = Json.decodeFromString<JsonArray>(finalSquadScoringArray.toString())
        LabelUtils.testLogGreen(squadPlayersArray.toString())

        Log.d("", finalSquadScoringArray.toString())
        Log.d("", squadPlayersArray.toString())

        val mutation = SubmitBGMIGameMutation(
            finalTier = BgmiLevels.ACE,
            initialTier = BgmiLevels.ACE,
            kills = -1, // default as -1, not individual kills only scoring will have the kills
            rank = 1,
            teamRank = Optional.presentIfNotNull(1),
            group = BgmiGroups.solo,
            map = BgmiMaps.karakin,
            playedAt = Date(System.currentTimeMillis()),
            squadScoring = getBGMISquadScoringForValue(finalSquadScoringArray.toString())
        )

        LabelUtils.testLogGreen("finalSquadScoringArray:  " + Gson().toJson(finalSquadScoringArray))
        LabelUtils.testLogGreen("squadScoring:  " + Gson().toJson(mutation.squadScoring))
    }


    @Test
    fun testForAutomlWhenNoRankIsAvailable() {
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        val list: ArrayList<ImageResultJsonFlat> = Gson().fromJson(
            "[{\"epochTimestamp\":1658305984795,\"fileName\":\"1658305984795.jpg\",\"labels\":[{\"box\":[0.023160584,0.041045092,0.13254091,0.09469547],\"confidence\":0.546875,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.079032555,0.09567319,0.12707894,0.28785872],\"confidence\":0.953125,\"label\":9,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.011601344,0.035809085,0.14159948,0.39607775],\"confidence\":0.93359375,\"label\":10,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik /52 Winner Winner Chicken Dinner!!\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.73210114,0.43591273,0.8165205,0.52033216],\"confidence\":0.9453125,\"label\":0,\"ocr\":\"[.]Finishes G\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305985137,\"fileName\":\"1658305985137.jpg\",\"labels\":[{\"box\":[0.07850835,0.09567319,0.12760314,0.28785872],\"confidence\":0.93359375,\"label\":9,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.01251249,0.039453655,0.14251062,0.39972234],\"confidence\":0.953125,\"label\":10,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik /52 Winner Winner Chicken Dinner!!\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7314267,0.43683144,0.81761587,0.51937884],\"confidence\":0.953125,\"label\":0,\"ocr\":\"[.]SDNU88[.]Finishes G\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305985475,\"fileName\":\"1658305985475.jpg\",\"labels\":[{\"box\":[0.021967202,0.041045092,0.13373429,0.09469547],\"confidence\":0.5,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07948813,0.093850896,0.12753451,0.28603643],\"confidence\":0.9453125,\"label\":9,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.013900533,0.035809085,0.14112258,0.39607775],\"confidence\":0.93359375,\"label\":10,\"ocr\":\"#1752 Winner Winner Chicken Dinner! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7335907,0.43683144,0.8197799,0.51937884],\"confidence\":0.9609375,\"label\":0,\"ocr\":\"[.]SDn88[.]Finishes 6\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305985813,\"fileName\":\"1658305985813.jpg\",\"labels\":[{\"box\":[0.07896392,0.093850896,0.12805872,0.28603643],\"confidence\":0.953125,\"label\":9,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.013423637,0.035809085,0.14342177,0.39607775],\"confidence\":0.9453125,\"label\":10,\"ocr\":\"#1/52 Winner Winner Chicken Dinner!! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.73451096,0.4370128,0.81885964,0.5213614],\"confidence\":0.9609375,\"label\":0,\"ocr\":\"[.]SON88[.]Finishes G\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305986150,\"fileName\":\"1658305986150.jpg\",\"labels\":[{\"box\":[0.07896392,0.093850896,0.12805872,0.28603643],\"confidence\":0.953125,\"label\":9,\"ocr\":\"Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.01251249,0.035809085,0.14251062,0.39607775],\"confidence\":0.93359375,\"label\":10,\"ocr\":\"Ranked Classic Made (TPP) - Solo - Livik /52 Winner Winner Chicken Dinner!\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7335907,0.43791342,0.8197799,0.5204608],\"confidence\":0.96875,\"label\":0,\"ocr\":\"[.]SDNU88[.]Finishes 6\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305986487,\"fileName\":\"1658305986487.jpg\",\"labels\":[{\"box\":[0.022878341,0.04252908,0.13464543,0.095033765],\"confidence\":0.546875,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07850835,0.09590294,0.12760314,0.2839844],\"confidence\":0.9609375,\"label\":9,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.013900533,0.035809085,0.14112258,0.39607775],\"confidence\":0.953125,\"label\":10,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik /52 Winner Winner Chicken Dinner!!\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7335907,0.43899542,0.8197799,0.5215428],\"confidence\":0.9609375,\"label\":0,\"ocr\":\"[.]SDnU88[.]Finishes G\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305986829,\"fileName\":\"1658305986829.jpg\",\"labels\":[{\"box\":[0.0223477,0.042736575,0.12797117,0.091295585],\"confidence\":0.5,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07896392,0.093850896,0.12805872,0.28603643],\"confidence\":0.953125,\"label\":9,\"ocr\":\"Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.013900533,0.039453655,0.14112258,0.39972234],\"confidence\":0.9453125,\"label\":10,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik /52 Winner Winner Chicken Dinner!!\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7325087,0.4380948,0.81869787,0.5224434],\"confidence\":0.96875,\"label\":0,\"ocr\":\"[.]SDnU88[.]Finishes 6\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305987171,\"fileName\":\"1658305987171.jpg\",\"labels\":[{\"box\":[0.024071723,0.04161794,0.13345204,0.09412262],\"confidence\":0.5,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07896392,0.09567319,0.12805872,0.28785872],\"confidence\":0.953125,\"label\":9,\"ocr\":\"Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.013423637,0.03965582,0.14342177,0.39223105],\"confidence\":0.953125,\"label\":10,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik /52 Winner Winner Chicken Dinner!\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7325087,0.4370128,0.81869787,0.5213614],\"confidence\":0.953125,\"label\":0,\"ocr\":\"[.]SDNU88[.]Finishes \\u0026\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305987509,\"fileName\":\"1658305987509.jpg\",\"labels\":[{\"box\":[0.024626907,0.044217184,0.13255517,0.092776194],\"confidence\":0.5,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07896392,0.093850896,0.12805872,0.28603643],\"confidence\":0.953125,\"label\":9,\"ocr\":\"Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.011601344,0.039453655,0.14159948,0.39972234],\"confidence\":0.953125,\"label\":10,\"ocr\":\"H/52 Winner Winner Chicken Dinner!! Renked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7325087,0.4370128,0.81869787,0.5213614],\"confidence\":0.9609375,\"label\":0,\"ocr\":\"[.]SDMU88[.]Finishes G\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305987857,\"fileName\":\"1658305987857.jpg\",\"labels\":[{\"box\":[0.021967202,0.043089695,0.13373429,0.094473146],\"confidence\":0.546875,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07850835,0.093576364,0.12760314,0.28995556],\"confidence\":0.953125,\"label\":9,\"ocr\":\"Ranked Classic Mode (TPp) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.012989387,0.039453655,0.14021143,0.39972234],\"confidence\":0.9453125,\"label\":10,\"ocr\":\"/52 Winner Winner Chicken Dinner!! Renked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.73210114,0.43681413,0.8165205,0.51943076],\"confidence\":0.953125,\"label\":0,\"ocr\":\"[.]SDNU88[.]Finishes 6\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305988207,\"fileName\":\"1658305988207.jpg\",\"labels\":[{\"box\":[0.021967202,0.04252908,0.13373429,0.095033765],\"confidence\":0.5,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07850835,0.093850896,0.12760314,0.28603643],\"confidence\":0.9453125,\"label\":9,\"ocr\":\"Ranked Classic Mode (TPPp) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.013900533,0.039453655,0.14112258,0.39972234],\"confidence\":0.953125,\"label\":10,\"ocr\":\"H/52 Winner Winner Chicken Dinner! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7330025,0.43772525,0.8156192,0.5203419],\"confidence\":0.9453125,\"label\":0,\"ocr\":\"[.]S[n88[.]Finishes G\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305988549,\"fileName\":\"1658305988549.jpg\",\"labels\":[{\"box\":[0.07850835,0.09567319,0.12760314,0.28785872],\"confidence\":0.9453125,\"label\":9,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.013900533,0.039453655,0.14112258,0.39972234],\"confidence\":0.953125,\"label\":10,\"ocr\":\"H/52 Winner Winner Chicken Dinner! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.73210114,0.43772525,0.8165205,0.5203419],\"confidence\":0.953125,\"label\":0,\"ocr\":\"[.]Finishes 6\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305988886,\"fileName\":\"1658305988886.jpg\",\"labels\":[{\"box\":[0.021967202,0.042178556,0.13373429,0.09356201],\"confidence\":0.5,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07850835,0.093850896,0.12760314,0.28603643],\"confidence\":0.953125,\"label\":9,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.011601344,0.04330039,0.14159948,0.3958756],\"confidence\":0.953125,\"label\":10,\"ocr\":\"/52 Winner Winner Chicken Dinner!! Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.73210114,0.43772525,0.8165205,0.5203419],\"confidence\":0.9453125,\"label\":0,\"ocr\":\"[.]SDNU88[.]Finishes G\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1658305989222,\"fileName\":\"1658305989222.jpg\",\"labels\":[{\"box\":[0.024626907,0.044217184,0.13255517,0.092776194],\"confidence\":0.546875,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.079032555,0.09175407,0.12707894,0.28813326],\"confidence\":0.9453125,\"label\":9,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.013900533,0.039453655,0.14112258,0.39972234],\"confidence\":0.93359375,\"label\":10,\"ocr\":\"|/52 Winner Winner Chicken Dinner!! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7339136,0.43682384,0.8165303,0.5212433],\"confidence\":0.953125,\"label\":0,\"ocr\":\"[.]SDnUs88[.]Finishes 6\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        val result =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(list, null, null, false)
        Truth.assertThat(result.accept).isTrue()
        val result1 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(
            list, null, null, false
        )
        Truth.assertThat(result1.accept).isTrue()
    }

    @Test
    fun rankDetection() {
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        val list: ArrayList<ImageResultJsonFlat> = Gson().fromJson(
            "[{\"epochTimestamp\":1673674376612,\"fileName\":\"1673674376612.jpg\",\"labels\":[{\"box\":[0.018811151,0.037510965,0.13124008,0.15656857],\"confidence\":0.703125,\"label\":7,\"ocr\":\"#5/52\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.07624778,0.19398566,0.132457,0.36939728],\"confidence\":0.78515625,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP)- Solo - Livik\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.73009527,0.44086704,0.81451464,0.5106119],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]Finishes5\",\"resolution\":{\"first\":1605,\"second\":720}}]}]",
            type
        )
        val result =
            MachineConstants.machineInputValidator.validateRankKillGameInfo(list, null, null, false)
        Truth.assertThat(result.accept).isTrue()
        Truth.assertThat(result.kill).isEqualTo("5")
    }

    private fun getBGMISquadScoringForValue(scoring: String?): Optional<List<SquadMemberGameInfo>?> {
        if (scoring in arrayListOf(
                null,
                StateMachineStringConstants.UNKNOWN
            )
        ) return Optional.Absent
        val squadPlayersArray = Gson().fromJson(scoring!!, com.google.gson.JsonArray::class.java)
        LabelUtils.testLogGreen("testEncode squadPlayersArray" + Gson().toJson(squadPlayersArray) + "\n\n")
        val scoringList: MutableList<SquadMemberGameInfo> = mutableListOf()
        for (i in 0 until squadPlayersArray.size()) {
            val squadPlayer = squadPlayersArray[i]
            val test =
                Gson().fromJson(squadPlayer.toString(), com.google.gson.JsonObject::class.java)
            val userName = test["username"].asString
            val kills = test["kills"].asInt
            LabelUtils.testLogGreen(userName)
            scoringList.add(SquadMemberGameInfo(username = userName, kills = kills))
        }
        return Optional.presentIfNotNull(scoringList)
    }

    @Test
    fun testRankKillCaching() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes 14[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#14/99"
            )
        )
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes 14[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#1499"
            )
        )
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes 1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#11/99"
            )
        )
        val data4 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes |[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#|1/99"
            )
        )
        val data5 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes |[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#/99"
            )
        )
        val data6 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes |[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#||/99"
            )
        )
        val data7 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes 1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#|/99"
            )
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data1, data2, data3, data4, data5, data6, data7
            )
        )
        val data8 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes 14[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) salo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#1/99"
            )
        )
        val data9 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes 14[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) sola-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#14/99"
            )
        )
        val data10 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes 1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#|l/99"
            )
        )
        val data11 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes l[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "|l/99"
            )
        )
        val data12 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes l[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "|199"
            )
        )
        val data13 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes 1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "99"
            )
        )
        val data14 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal to "jarvisFriday[.]finishes 1[.]",
                BGMIConstants.GameLabels.GAME_INFO.ordinal to "Classic (TPP) solo-Erangel",
                BGMIConstants.GameLabels.RANK.ordinal to "#|/99"
            )
        )
        val result = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            arrayListOf(
                data8, data9, data10, data11, data12, data13, data14
            )
        )
        Truth.assertThat(result.accept).isTrue()
        Truth.assertThat(result.kill).isEqualTo("1")
        Truth.assertThat(result.rank).isEqualTo("11")
    }


    @Test
    fun testRankKillCachingForSquad() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        val gson = Gson()
        val data1: ArrayList<ImageResultJsonFlat> = gson.fromJson(
            "[{\"epochTimestamp\":1676333926608,\"fileName\":\"1676333926608.jpg\",\"labels\":[{\"box\":[0.014304355,0.035844635,0.14038144,0.14201395],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"#5/97 Ranked C|\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.7233592,0.18841264,0.81802666,0.25302863],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]jarvisFriday[.]Finishes 1\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72603405,0.35873672,0.8137399,0.43694815],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]roxxior[.]Finishes 11\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72732353,0.5365915,0.81194663,0.59775716],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]iNouTaNaX[.]Finishes &\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72847563,0.7080819,0.8112983,0.78054184],\"confidence\":0.83203125,\"label\":8,\"ocr\":\"[.]55mukDp[.]Finishes I\",\"resolution\":{\"first\":1605,\"second\":720}}]},{\"epochTimestamp\":1676333927471,\"fileName\":\"1676333927471.jpg\",\"labels\":[{\"box\":[0.0067185313,0.03189757,0.14917628,0.14303923],\"confidence\":0.921875,\"label\":7,\"ocr\":\"#5/87 Ranked Ch\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.716828,0.19115134,0.82097864,0.2664315],\"confidence\":0.80859375,\"label\":8,\"ocr\":\"[.]jarvisFridy[.]Finishes l\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.7282846,0.35249963,0.8211614,0.44361976],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]roxxior[.]Finishes 1l\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72731286,0.52738756,0.82197946,0.6055984],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]SiNouTaNaX[.]Finishes 8\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.7284126,0.7052958,0.81780934,0.78655195],\"confidence\":0.80859375,\"label\":8,\"ocr\":\"[.]55muklp[.]Finishes l\",\"resolution\":{\"first\":1605,\"second\":720}}]},{\"epochTimestamp\":1676333927986,\"fileName\":\"1676333927986.jpg\",\"labels\":[{\"box\":[0.008142076,0.033527356,0.14422643,0.13969667],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"#5/97 Ranked C\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.7313982,0.18713373,0.8144207,0.2506805],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]Finishes 2\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.08149156,0.106995076,0.12882525,0.28578842],\"confidence\":0.78515625,\"label\":5,\"ocr\":\"anked Classit Made (TPP)Squad- Erangel\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.0023765415,0.04179342,0.1437454,0.39261663],\"confidence\":0.734375,\"label\":4,\"ocr\":\"#5/87 You've made it to top 10! Keep Ranked Classic Mode (TPP) Squad- Erangel\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.73009527,0.3571953,0.81451464,0.43247607],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]roxxior[.]Finishes 1\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.73742473,0.53663856,0.8104093,0.5957958],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]NouTaNaX[.]Finishes 8\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.73648804,0.70699394,0.8119505,0.7755847],\"confidence\":0.80859375,\"label\":8,\"ocr\":\"[.]55mukDp[.]Finishes 1\",\"resolution\":{\"first\":1605,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data1)
        val data2: ArrayList<ImageResultJsonFlat> = gson.fromJson(
            "[{\"epochTimestamp\":1676333926608,\"fileName\":\"1676333926608.jpg\",\"labels\":[{\"box\":[0.08149156,0.106995076,0.12882525,0.28578842],\"confidence\":0.78515625,\"label\":5,\"ocr\":\"anked Classit Made (TPP)Squad- Erangel\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.014304355,0.035844635,0.14038144,0.14201395],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"#5/97 Ranked C|\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.7233592,0.18841264,0.81802666,0.25302863],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]jarvsfriday[.]Finishes l\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72603405,0.35873672,0.8137399,0.43694815],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]roxior[.]Finishes 11\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72732353,0.5365915,0.81194663,0.59775716],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]iNouTaNaX[.]Finishes &\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72847563,0.7080819,0.8112983,0.78054184],\"confidence\":0.83203125,\"label\":8,\"ocr\":\"[.]55mukDp[.]Finishes I\",\"resolution\":{\"first\":1605,\"second\":720}}]},{\"epochTimestamp\":1676333927471,\"fileName\":\"1676333927471.jpg\",\"labels\":[{\"box\":[0.0067185313,0.03189757,0.14917628,0.14303923],\"confidence\":0.921875,\"label\":7,\"ocr\":\"#5/87 Ranked Ch\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.716828,0.19115134,0.82097864,0.2664315],\"confidence\":0.80859375,\"label\":8,\"ocr\":\"[.]jarvisFriday[.]Finishes l\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.7282846,0.35249963,0.8211614,0.44361976],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]roxxior[.]Finishes l\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72731286,0.52738756,0.82197946,0.6055984],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]SiNouTaNaX[.]Finishes 8\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.7284126,0.7052958,0.81780934,0.78655195],\"confidence\":0.80859375,\"label\":8,\"ocr\":\"[.]55muklp[.]Finishes 1\",\"resolution\":{\"first\":1605,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data2)
        val data3: ArrayList<ImageResultJsonFlat> = gson.fromJson(
            "[{\"epochTimestamp\":1676333926608,\"fileName\":\"1676333926608.jpg\",\"labels\":[{\"box\":[0.014304355,0.035844635,0.14038144,0.14201395],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"#5/97 Ranked C|\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.7233592,0.18841264,0.81802666,0.25302863],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]arvisFriday[.]Finishes 15\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.08137623,0.09965104,0.13055259,0.2963565],\"confidence\":0.8515625,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) Squad- Erangel\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.0075373054,0.035677478,0.1410342,0.41434395],\"confidence\":0.78515625,\"label\":4,\"ocr\":\"#5/97 You've made it to top 10! Keep it Ranked Classic Mode (TPP) Squat- Erangel\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72603405,0.35873672,0.8137399,0.43694815],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]roxxior[.]Finishes l|\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72732353,0.5365915,0.81194663,0.59775716],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]iNouTaNaX[.]Finishes &\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72847563,0.7080819,0.8112983,0.78054184],\"confidence\":0.83203125,\"label\":8,\"ocr\":\"[.]55mukDp[.]Finishes I\",\"resolution\":{\"first\":1605,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data3)
        val data4: ArrayList<ImageResultJsonFlat> = gson.fromJson(
            "[{\"epochTimestamp\":1676333926257,\"fileName\":\"1676333926257.jpg\",\"labels\":[{\"box\":[0.017814096,0.03503211,0.1368717,0.14746104],\"confidence\":0.8828125,\"label\":7,\"ocr\":\"#5/97 Y Ranked Ch\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.7251329,0.18859579,0.816253,0.2544575],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]jarvisFriday[.]Finishes l\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.081305206,0.10287508,0.13142963,0.29958054],\"confidence\":0.8671875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP)-Squad- Erangel\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.010667205,0.04179342,0.14673887,0.39261663],\"confidence\":0.60546875,\"label\":4,\"ocr\":\"#5/97 You've made it to top 10! Keep Ranked Classic Mode (TPP) - Squad - Erangel\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.72848326,0.35959485,0.8129026,0.4393141],\"confidence\":0.80859375,\"label\":8,\"ocr\":\"[.]roxxior[.]Finishes\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.73117644,0.5394954,0.8080937,0.5961631],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]iNouTaNaX[.]Finishes &\",\"resolution\":{\"first\":1605,\"second\":720}},{\"box\":[0.73392725,0.7082485,0.8079624,0.7730204],\"confidence\":0.8671875,\"label\":8,\"ocr\":\"[.]55mukOp[.]Finishes I\",\"resolution\":{\"first\":1605,\"second\":720}}]}]",
            type
        )
        var result = MachineConstants.machineInputValidator.validateRankKillGameInfo(data4)
        gson.fromJson(result.squadScoring ?: "[]", com.google.gson.JsonArray::class.java).apply {
            Truth.assertThat(this.size()).isEqualTo(4)
            Truth.assertThat((this[0] as JsonObject)["kills"].asString).isEqualTo("1")
            Truth.assertThat((this[1] as JsonObject)["kills"].asString).isEqualTo("11")
            Truth.assertThat((this[2] as JsonObject)["kills"].asString).isEqualTo("8")
            Truth.assertThat((this[3] as JsonObject)["kills"].asString).isEqualTo("1")
        }
        var rank = MachineConstants.machineInputValidator.getProcessedRankData()
        var autoMl = MachineConstants.machineInputValidator.sendForAutoML
        Truth.assertThat(rank).isEqualTo("5")
        Truth.assertThat(autoMl).isTrue()
        Truth.assertThat(result.accept).isTrue()

        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        var data5: ArrayList<ImageResultJsonFlat> = gson.fromJson(
            "[{\"epochTimestamp\":1676633755419,\"fileName\":\"1676633755419.jpg\",\"labels\":[{\"box\":[0.0072157383,0.04075087,0.13820094,0.13015589],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"#1151 Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07942326,0.10029089,0.1276695,0.28604454],\"confidence\":0.921875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0053255036,0.03866312,0.14402047,0.3828508],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"751 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7277945,0.4462823,0.81384075,0.5244931],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]SpMU88[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633755757,\"fileName\":\"1676633755757.jpg\",\"labels\":[{\"box\":[0.0047944784,0.04075087,0.13830492,0.13015589],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"#1 /51 Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07942326,0.09775491,0.1276695,0.29825264],\"confidence\":0.91015625,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.003988564,0.04179342,0.14535742,0.39261663],\"confidence\":0.921875,\"label\":4,\"ocr\":\"I751 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.72860825,0.44723943,0.813027,0.5254502],\"confidence\":0.80859375,\"label\":8,\"ocr\":\"[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633756096,\"fileName\":\"1676633756096.jpg\",\"labels\":[{\"box\":[0.008511156,0.04075087,0.13458824,0.13015589],\"confidence\":0.8984375,\"label\":7,\"ocr\":\"Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0789582,0.10011235,0.12813456,0.28944713],\"confidence\":0.921875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0045194924,0.038569376,0.14321446,0.3893926],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"/51 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7325057,0.44226956,0.81532836,0.5333897],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]S[u88[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633756437,\"fileName\":\"1676633756437.jpg\",\"labels\":[{\"box\":[0.009669796,0.04075087,0.13574688,0.13015589],\"confidence\":0.8984375,\"label\":7,\"ocr\":\"Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07942326,0.09989928,0.1276695,0.29288423],\"confidence\":0.91015625,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.006637156,0.03866312,0.14270882,0.3828508],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"/5 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7325057,0.4447835,0.81532836,0.52603966],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]SpnU88[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633756793,\"fileName\":\"1676633756793.jpg\",\"labels\":[{\"box\":[0.015075456,0.044997282,0.13412194,0.13439396],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"#11/51 Ranked\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0789582,0.1015113,0.12813456,0.29449624],\"confidence\":0.921875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Sola - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0053255036,0.038569376,0.14402047,0.3893926],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"751 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7356173,0.44515154,0.8138287,0.5176115],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]SpuUB8[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633757134,\"fileName\":\"1676633757134.jpg\",\"labels\":[{\"box\":[0.0072157383,0.04075087,0.13820094,0.13015589],\"confidence\":0.8828125,\"label\":7,\"ocr\":\"Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07982627,0.09904627,0.1280725,0.30340937],\"confidence\":0.8984375,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.003988564,0.038569376,0.14535742,0.3893926],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"/5 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.73486334,0.44535315,0.81458265,0.52063394],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633757483,\"fileName\":\"1676633757483.jpg\",\"labels\":[{\"box\":[0.0072157383,0.041596383,0.13820094,0.12931037],\"confidence\":0.8828125,\"label\":7,\"ocr\":\"Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0789582,0.100978956,0.12813456,0.30147666],\"confidence\":0.8984375,\"label\":5,\"ocr\":\"Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.003988564,0.04188715,0.14535742,0.38607484],\"confidence\":0.94921875,\"label\":4,\"ocr\":\"I/5 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7325057,0.4446275,0.81532836,0.5213596],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]SONU88[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633757826,\"fileName\":\"1676633757826.jpg\",\"labels\":[{\"box\":[0.0108621195,0.039889056,0.13455456,0.1310177],\"confidence\":0.8984375,\"label\":7,\"ocr\":\"#1151 Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0789582,0.10172436,0.12813456,0.29105914],\"confidence\":0.9296875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Sola - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0053255036,0.04188715,0.14402047,0.38607484],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"/5 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7325057,0.4421972,0.81532836,0.53507406],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633758170,\"fileName\":\"1676633758170.jpg\",\"labels\":[{\"box\":[0.00970348,0.04075087,0.13339591,0.13015589],\"confidence\":0.8984375,\"label\":7,\"ocr\":\"#1151 Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0789582,0.09828727,0.12813456,0.29127222],\"confidence\":0.921875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0053255036,0.038569376,0.14402047,0.3893926],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"#/5| BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.73486334,0.44239587,0.81458265,0.5268153],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]S[u88[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633758512,\"fileName\":\"1676633758512.jpg\",\"labels\":[{\"box\":[0.012315892,0.045036703,0.13365746,0.13274251],\"confidence\":0.8828125,\"label\":7,\"ocr\":\"Ranked\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07936121,0.09965104,0.12853757,0.2963565],\"confidence\":0.91015625,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0053255036,0.04179342,0.14402047,0.39261663],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"1/51 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Sola - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.73169965,0.44320187,0.8145223,0.52762127],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]Finishes2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633758852,\"fileName\":\"1676633758852.jpg\",\"labels\":[{\"box\":[0.0072157383,0.04075087,0.13820094,0.13015589],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07902026,0.09775491,0.1272665,0.29825264],\"confidence\":0.91015625,\"label\":5,\"ocr\":\"Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0031825528,0.038569376,0.1445514,0.3893926],\"confidence\":0.94140625,\"label\":4,\"ocr\":\"/51 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Sola - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.73114705,0.44702193,0.8124025,0.5237534],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633759189,\"fileName\":\"1676633759189.jpg\",\"labels\":[{\"box\":[0.008511156,0.03959223,0.13458824,0.12899724],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07936121,0.09965104,0.12853757,0.2963565],\"confidence\":0.91015625,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.003988564,0.04188715,0.14535742,0.38607484],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"I/5 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.72940665,0.445236,0.8122286,0.5312822],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]SOnU88[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633759530,\"fileName\":\"1676633759530.jpg\",\"labels\":[{\"box\":[0.009669796,0.04075087,0.13574688,0.13015589],\"confidence\":0.8984375,\"label\":7,\"ocr\":\"#1151 Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0785552,0.09989928,0.12773156,0.29288423],\"confidence\":0.91015625,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.006131515,0.04188715,0.14482649,0.38607484],\"confidence\":0.94140625,\"label\":4,\"ocr\":\"/51 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7356173,0.44543353,0.8138287,0.52216566],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]snU88[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633759892,\"fileName\":\"1676633759892.jpg\",\"labels\":[{\"box\":[0.0108621195,0.04190951,0.13455456,0.13131452],\"confidence\":0.8984375,\"label\":7,\"ocr\":\"/51 Ranker\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0785552,0.1015113,0.12773156,0.29449624],\"confidence\":0.91015625,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.006637156,0.038569376,0.14270882,0.3893926],\"confidence\":0.94921875,\"label\":4,\"ocr\":\"/51 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7348113,0.44595754,0.81302273,0.51841754],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633760233,\"fileName\":\"1676633760233.jpg\",\"labels\":[{\"box\":[0.019208055,0.044997282,0.13600287,0.13439396],\"confidence\":0.8984375,\"label\":7,\"ocr\":\"Ranked\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0785552,0.10312332,0.12773156,0.29610825],\"confidence\":0.91015625,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0053255036,0.035345346,0.14402047,0.38616857],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"|/51 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Salo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7356173,0.44489712,0.8138287,0.51464194],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633761954,\"fileName\":\"1676633761954.jpg\",\"labels\":[{\"box\":[0.014338177,0.039889056,0.13339578,0.1310177],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07902026,0.10011235,0.1272665,0.28944713],\"confidence\":0.921875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0074431673,0.038569376,0.14351484,0.3893926],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"I/51 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7341177,0.4427335,0.81694037,0.51519346],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]S[M88[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633762295,\"fileName\":\"1676633762295.jpg\",\"labels\":[{\"box\":[0.010828435,0.039889056,0.13690552,0.1310177],\"confidence\":0.91015625,\"label\":7,\"ocr\":\"Ranke\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07861726,0.09936693,0.1268635,0.29986465],\"confidence\":0.8828125,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.006131515,0.035187647,0.14482649,0.39277434],\"confidence\":0.921875,\"label\":4,\"ocr\":\"/5 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7341177,0.44341877,0.81694037,0.51450825],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        data5 = gson.fromJson(
            "[{\"epochTimestamp\":1676633762637,\"fileName\":\"1676633762637.jpg\",\"labels\":[{\"box\":[0.010828435,0.039889056,0.13690552,0.1310177],\"confidence\":0.921875,\"label\":7,\"ocr\":\"#|5\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0785552,0.10172436,0.12773156,0.29105914],\"confidence\":0.921875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.006131515,0.04188715,0.14482649,0.38607484],\"confidence\":0.9296875,\"label\":4,\"ocr\":\"/51 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.73492366,0.44341877,0.81774634,0.51450825],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]S[u88[.]Finishes 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(data5)
        rank = MachineConstants.machineInputValidator.getProcessedRankData()
        autoMl = MachineConstants.machineInputValidator.sendForAutoML
        Truth.assertThat(result.teamRank).isNull()
        Truth.assertThat(result.rank).isNull()
        Truth.assertThat(result.kill).isEqualTo("2")
        Truth.assertThat(rank).isEqualTo("11")
        Truth.assertThat(autoMl).isTrue()
    }

    @Test
    fun validateKillsContaining1() {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        MachineConstants.machineInputValidator.clear()
        var input: ArrayList<ImageResultJsonFlat> = gson.fromJson(
            "[{\"epochTimestamp\":1677780338633,\"fileName\":\"1677780338633.jpg\",\"labels\":[{\"box\":[0.016990773,0.03755165,0.14162433,0.164588],\"confidence\":0.80859375,\"label\":7,\"ocr\":\"#G/52\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.081035286,0.22524963,0.12928152,0.39734346],\"confidence\":0.76171875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.74134153,0.44151926,0.8180737,0.5309159],\"confidence\":0.80859375,\"label\":8,\"ocr\":\"[.]ROCK Y 376[.]Finishes 1O\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(input)
        input = gson.fromJson(
            "[{\"epochTimestamp\":1677780338975,\"fileName\":\"1677780338975.jpg\",\"labels\":[{\"box\":[0.009147577,0.03684869,0.14785549,0.1410099],\"confidence\":0.953125,\"label\":7,\"ocr\":\"#6/52 Ranked |\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.08361657,0.10389675,0.12347619,0.2759906],\"confidence\":0.8828125,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.008549541,0.0386941,0.14724451,0.37637174],\"confidence\":0.91015625,\"label\":4,\"ocr\":\"H/52 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Sola - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7342739,0.44400787,0.8186933,0.5284273],\"confidence\":0.8515625,\"label\":8,\"ocr\":\"[.]ROCKY376[.]Finishes 10\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(input)
        input = gson.fromJson(
            "[{\"epochTimestamp\":1677780339315,\"fileName\":\"1677780339315.jpg\",\"labels\":[{\"box\":[0.007988937,0.03569005,0.14669687,0.13985126],\"confidence\":0.94921875,\"label\":7,\"ocr\":\"#G/52\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.08467915,0.10389675,0.12160761,0.2759906],\"confidence\":0.83203125,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.009861194,0.04188755,0.14593285,0.37317827],\"confidence\":0.8671875,\"label\":4,\"ocr\":\"H /52 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Sola - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7334601,0.44480625,0.81950706,0.5276289],\"confidence\":0.8515625,\"label\":8,\"ocr\":\"[.]ROCK Y 376[.]Finishes IO\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(input)
        input = gson.fromJson(
            "[{\"epochTimestamp\":1677780339655,\"fileName\":\"1677780339655.jpg\",\"labels\":[{\"box\":[0.007988937,0.036675114,0.14669687,0.1388662],\"confidence\":0.953125,\"label\":7,\"ocr\":\"\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.08502839,0.105524264,0.12125837,0.27436307],\"confidence\":0.76171875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo -Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.010667205,0.038663507,0.14673887,0.36995423],\"confidence\":0.8828125,\"label\":4,\"ocr\":\"HG/52 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Salo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7334601,0.44400787,0.81950706,0.5284273],\"confidence\":0.8515625,\"label\":8,\"ocr\":\"[.]ROCK Y 376[.]Finishes I0\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(input)
        input = gson.fromJson(
            "[{\"epochTimestamp\":1677780340007,\"fileName\":\"1677780340007.jpg\",\"labels\":[{\"box\":[0.007988937,0.037641548,0.14669687,0.13789976],\"confidence\":0.953125,\"label\":7,\"ocr\":\"Ranked\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.08537102,0.10712099,0.12091574,0.27276635],\"confidence\":0.734375,\"label\":5,\"ocr\":\"Ranked Classic Made (TPP) - Salo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.008549541,0.03857252,0.14724451,0.36359712],\"confidence\":0.8515625,\"label\":4,\"ocr\":\"R/52 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Sala - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.73507226,0.44720155,0.81789494,0.5284577],\"confidence\":0.83203125,\"label\":8,\"ocr\":\"[.]ROCK Y 376[.]Finishes 10\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(input)
        input = gson.fromJson(
            "[{\"epochTimestamp\":1677780340354,\"fileName\":\"1677780340354.jpg\",\"labels\":[{\"box\":[0.007988937,0.037641548,0.14669687,0.13789976],\"confidence\":0.94921875,\"label\":7,\"ocr\":\"#6/52\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.085707165,0.105524264,0.12057959,0.27436307],\"confidence\":0.76171875,\"label\":5,\"ocr\":\"Ranked Classic Made (TPP) - Salo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.009861194,0.041796565,0.14593285,0.36682117],\"confidence\":0.83203125,\"label\":4,\"ocr\":\"H/52 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Sola - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7342739,0.4455895,0.8186933,0.5268457],\"confidence\":0.83203125,\"label\":8,\"ocr\":\"[.]ROCK Y 376[.]Finishes 0\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(input)
        input = gson.fromJson(
            "[{\"epochTimestamp\":1677780340695,\"fileName\":\"1677780340695.jpg\",\"labels\":[{\"box\":[0.006651871,0.037641548,0.14803392,0.13789976],\"confidence\":0.94921875,\"label\":7,\"ocr\":\"Ranked\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.085707165,0.105524264,0.12057959,0.27436307],\"confidence\":0.76171875,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP) - Solo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.008549541,0.038663507,0.14724451,0.36995423],\"confidence\":0.83203125,\"label\":4,\"ocr\":\"/52 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7342739,0.44480625,0.8186933,0.5276289],\"confidence\":0.8515625,\"label\":8,\"ocr\":\"[.]ROCK Y 376[.]Finishes IO\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(input)
        input = gson.fromJson(
            "[{\"epochTimestamp\":1677780341032,\"fileName\":\"1677780341032.jpg\",\"labels\":[{\"box\":[0.007988937,0.037641548,0.14669687,0.13789976],\"confidence\":0.953125,\"label\":7,\"ocr\":\"Ranked\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.08537102,0.10713629,0.12091574,0.2759751],\"confidence\":0.76171875,\"label\":5,\"ocr\":\"Ranked Classic Made (TPP) - Salo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.008549541,0.038663507,0.14724451,0.36995423],\"confidence\":0.8515625,\"label\":4,\"ocr\":\"R/52 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Sala - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7334601,0.44400787,0.81950706,0.5284273],\"confidence\":0.8515625,\"label\":8,\"ocr\":\"[.]ROCK Y376[.]Finishes I0\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(input)
        input = gson.fromJson(
            "[{\"epochTimestamp\":1677780341377,\"fileName\":\"1677780341377.jpg\",\"labels\":[{\"box\":[0.005493231,0.037641548,0.14687529,0.13789976],\"confidence\":0.94921875,\"label\":7,\"ocr\":\"Ranked\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.08537102,0.10712099,0.12091574,0.27276635],\"confidence\":0.76171875,\"label\":5,\"ocr\":\"Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.010667205,0.038663507,0.14673887,0.36995423],\"confidence\":0.83203125,\"label\":4,\"ocr\":\"HA/52 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP) - Sola - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7334601,0.44561225,0.81950706,0.52843493],\"confidence\":0.83203125,\"label\":8,\"ocr\":\"[.]ROCK Y 376[.]Finishes I0\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(input)
        input = gson.fromJson(
            "[{\"epochTimestamp\":1677780344204,\"fileName\":\"1677780344204.jpg\",\"labels\":[{\"box\":[0.007988937,0.037641548,0.14669687,0.13789976],\"confidence\":0.94140625,\"label\":7,\"ocr\":\"Ranked\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.08537102,0.105524264,0.12091574,0.27436307],\"confidence\":0.76171875,\"label\":5,\"ocr\":\"Ranked Classic Made (TPP) - Salo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.008549541,0.03543946,0.14724451,0.36673018],\"confidence\":0.83203125,\"label\":4,\"ocr\":\"R/52 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP) - Solo - Livik\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.73263067,0.44400787,0.8203365,0.5284273],\"confidence\":0.83203125,\"label\":8,\"ocr\":\"[.]ROCK Y 376[.]Finishes ID\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        val result = MachineConstants.machineInputValidator.validateRankKillGameInfo(input)
        val rank = MachineConstants.machineInputValidator.getProcessedRankData()
        Truth.assertThat(MachineConstants.machineInputValidator.sendForAutoML).isTrue()
        Truth.assertThat(result.kill).isEqualTo("10")
        Truth.assertThat(rank).isEqualTo("6")
    }

    @Test
    fun testVisionCall() {
        val gson = Gson()
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val type = object : TypeToken<ArrayList<ArrayList<TFResult>>>() {}.type
        val pathType = object : TypeToken<ArrayList<String>>() {}.type
        val annotation: JsonObject = gson.fromJson(
            "{\"pages\":[{\"blocks\":[{\"boundingBox\":{\"vertices\":[{\"x\":4,\"y\":74},{\"x\":4,\"y\":17},{\"x\":186,\"y\":17},{\"x\":186,\"y\":74}],\"normalizedVertices\":[]},\"blockType\":\"TEXT\",\"confidence\":0.8792747855186462,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":4,\"y\":74},{\"x\":4,\"y\":17},{\"x\":186,\"y\":17},{\"x\":186,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.8792747855186462,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":4,\"y\":74},{\"x\":4,\"y\":17},{\"x\":49,\"y\":17},{\"x\":49,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.9558144807815552,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":4,\"y\":74},{\"x\":4,\"y\":17},{\"x\":49,\"y\":17},{\"x\":49,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.9558144807815552,\"text\":\"#\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":47,\"y\":74},{\"x\":47,\"y\":17},{\"x\":117,\"y\":17},{\"x\":117,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.9554185271263123,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":47,\"y\":74},{\"x\":47,\"y\":17},{\"x\":80,\"y\":17},{\"x\":80,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.9642242193222046,\"text\":\"6\"},{\"boundingBox\":{\"vertices\":[{\"x\":83,\"y\":74},{\"x\":83,\"y\":17},{\"x\":117,\"y\":17},{\"x\":117,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.9466128349304199,\"text\":\"0\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":113,\"y\":74},{\"x\":113,\"y\":17},{\"x\":140,\"y\":17},{\"x\":140,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.7826904058456421,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":113,\"y\":74},{\"x\":113,\"y\":17},{\"x\":140,\"y\":17},{\"x\":140,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.7826904058456421,\"text\":\"/\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":131,\"y\":74},{\"x\":131,\"y\":17},{\"x\":186,\"y\":17},{\"x\":186,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.8351938724517822,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":131,\"y\":74},{\"x\":131,\"y\":17},{\"x\":155,\"y\":17},{\"x\":155,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.7723173499107361,\"text\":\"1\"},{\"boundingBox\":{\"vertices\":[{\"x\":142,\"y\":74},{\"x\":142,\"y\":17},{\"x\":167,\"y\":17},{\"x\":167,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.8397254347801208,\"text\":\"0\"},{\"boundingBox\":{\"vertices\":[{\"x\":165,\"y\":74},{\"x\":165,\"y\":17},{\"x\":186,\"y\":17},{\"x\":186,\"y\":74}],\"normalizedVertices\":[]},\"confidence\":0.893538773059845,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"0\"}]}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":17,\"y\":185},{\"x\":283,\"y\":188},{\"x\":283,\"y\":204},{\"x\":17,\"y\":201}],\"normalizedVertices\":[]},\"blockType\":\"TEXT\",\"confidence\":0.8889424800872803,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":17,\"y\":185},{\"x\":283,\"y\":188},{\"x\":283,\"y\":204},{\"x\":17,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.8889424800872803,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":17,\"y\":186},{\"x\":62,\"y\":186},{\"x\":62,\"y\":201},{\"x\":17,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.986047625541687,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":17,\"y\":186},{\"x\":25,\"y\":186},{\"x\":25,\"y\":201},{\"x\":17,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9824580550193787,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":25,\"y\":186},{\"x\":32,\"y\":186},{\"x\":32,\"y\":201},{\"x\":25,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9803527593612671,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":33,\"y\":186},{\"x\":39,\"y\":186},{\"x\":39,\"y\":201},{\"x\":33,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9954618811607361,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":41,\"y\":186},{\"x\":47,\"y\":186},{\"x\":47,\"y\":201},{\"x\":41,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9952878355979919,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":47,\"y\":186},{\"x\":53,\"y\":186},{\"x\":53,\"y\":201},{\"x\":47,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9886012077331543,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":55,\"y\":186},{\"x\":62,\"y\":186},{\"x\":62,\"y\":201},{\"x\":55,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9741241335868835,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":67,\"y\":186},{\"x\":106,\"y\":186},{\"x\":106,\"y\":201},{\"x\":67,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9419093132019043,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":67,\"y\":186},{\"x\":74,\"y\":186},{\"x\":74,\"y\":201},{\"x\":67,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.6946459412574768,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":73,\"y\":186},{\"x\":77,\"y\":186},{\"x\":77,\"y\":201},{\"x\":73,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9772183895111084,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":77,\"y\":186},{\"x\":82,\"y\":186},{\"x\":82,\"y\":201},{\"x\":77,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9713494181632996,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":83,\"y\":186},{\"x\":89,\"y\":186},{\"x\":89,\"y\":201},{\"x\":83,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9935002326965332,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":91,\"y\":186},{\"x\":97,\"y\":186},{\"x\":97,\"y\":201},{\"x\":91,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9927652478218079,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":97,\"y\":186},{\"x\":101,\"y\":186},{\"x\":101,\"y\":201},{\"x\":97,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9935775995254517,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":101,\"y\":186},{\"x\":106,\"y\":186},{\"x\":106,\"y\":201},{\"x\":101,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9703083634376526,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"c\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":111,\"y\":186},{\"x\":143,\"y\":186},{\"x\":143,\"y\":202},{\"x\":111,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.856032133102417,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":111,\"y\":186},{\"x\":120,\"y\":186},{\"x\":120,\"y\":201},{\"x\":111,\"y\":201}],\"normalizedVertices\":[]},\"confidence\":0.9266049861907959,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":121,\"y\":187},{\"x\":128,\"y\":187},{\"x\":128,\"y\":202},{\"x\":121,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.6171629428863525,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":129,\"y\":187},{\"x\":136,\"y\":187},{\"x\":136,\"y\":202},{\"x\":129,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.9542874693870544,\"text\":\"d\"},{\"boundingBox\":{\"vertices\":[{\"x\":137,\"y\":187},{\"x\":143,\"y\":187},{\"x\":143,\"y\":202},{\"x\":137,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.9260730743408203,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"e\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":147,\"y\":187},{\"x\":152,\"y\":187},{\"x\":152,\"y\":202},{\"x\":147,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.6980116963386536,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":147,\"y\":187},{\"x\":152,\"y\":187},{\"x\":152,\"y\":202},{\"x\":147,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.6980116963386536,\"text\":\"(\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":153,\"y\":187},{\"x\":175,\"y\":187},{\"x\":175,\"y\":202},{\"x\":153,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.7673265337944031,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":153,\"y\":187},{\"x\":160,\"y\":187},{\"x\":160,\"y\":202},{\"x\":153,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.6844097375869751,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":159,\"y\":187},{\"x\":166,\"y\":187},{\"x\":166,\"y\":202},{\"x\":159,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.8332409858703613,\"text\":\"P\"},{\"boundingBox\":{\"vertices\":[{\"x\":167,\"y\":187},{\"x\":175,\"y\":187},{\"x\":175,\"y\":202},{\"x\":167,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.784328818321228,\"text\":\"P\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":176,\"y\":187},{\"x\":181,\"y\":187},{\"x\":181,\"y\":202},{\"x\":176,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.8937036395072937,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":176,\"y\":187},{\"x\":181,\"y\":187},{\"x\":181,\"y\":202},{\"x\":176,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.8937036395072937,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\")\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":197,\"y\":187},{\"x\":223,\"y\":187},{\"x\":223,\"y\":202},{\"x\":197,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.7440734505653381,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":197,\"y\":187},{\"x\":204,\"y\":187},{\"x\":204,\"y\":202},{\"x\":197,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.9551774263381958,\"text\":\"S\"},{\"boundingBox\":{\"vertices\":[{\"x\":206,\"y\":187},{\"x\":211,\"y\":187},{\"x\":211,\"y\":202},{\"x\":206,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.515491783618927,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":213,\"y\":187},{\"x\":217,\"y\":187},{\"x\":217,\"y\":202},{\"x\":213,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.9511380195617676,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":217,\"y\":187},{\"x\":223,\"y\":187},{\"x\":223,\"y\":202},{\"x\":217,\"y\":202}],\"normalizedVertices\":[]},\"confidence\":0.5544865131378174,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"a\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":235,\"y\":188},{\"x\":283,\"y\":188},{\"x\":283,\"y\":203},{\"x\":235,\"y\":203}],\"normalizedVertices\":[]},\"confidence\":0.9330476522445679,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":235,\"y\":188},{\"x\":241,\"y\":188},{\"x\":241,\"y\":203},{\"x\":235,\"y\":203}],\"normalizedVertices\":[]},\"confidence\":0.9558765888214111,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":243,\"y\":188},{\"x\":248,\"y\":188},{\"x\":248,\"y\":203},{\"x\":243,\"y\":203}],\"normalizedVertices\":[]},\"confidence\":0.9676815271377563,\"text\":\"r\"},{\"boundingBox\":{\"vertices\":[{\"x\":249,\"y\":188},{\"x\":255,\"y\":188},{\"x\":255,\"y\":203},{\"x\":249,\"y\":203}],\"normalizedVertices\":[]},\"confidence\":0.6631134152412415,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":257,\"y\":188},{\"x\":263,\"y\":188},{\"x\":263,\"y\":203},{\"x\":257,\"y\":203}],\"normalizedVertices\":[]},\"confidence\":0.986097514629364,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":265,\"y\":188},{\"x\":271,\"y\":188},{\"x\":271,\"y\":203},{\"x\":265,\"y\":203}],\"normalizedVertices\":[]},\"confidence\":0.9904541969299316,\"text\":\"g\"},{\"boundingBox\":{\"vertices\":[{\"x\":273,\"y\":188},{\"x\":279,\"y\":188},{\"x\":279,\"y\":203},{\"x\":273,\"y\":203}],\"normalizedVertices\":[]},\"confidence\":0.990990400314331,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":279,\"y\":188},{\"x\":283,\"y\":188},{\"x\":283,\"y\":203},{\"x\":279,\"y\":203}],\"normalizedVertices\":[]},\"confidence\":0.9771199822425842,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"l\"}]}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":15,\"y\":304},{\"x\":531,\"y\":300},{\"x\":532,\"y\":372},{\"x\":16,\"y\":376}],\"normalizedVertices\":[]},\"blockType\":\"TEXT\",\"confidence\":0.9197569489479065,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":15,\"y\":311},{\"x\":530,\"y\":300},{\"x\":531,\"y\":355},{\"x\":16,\"y\":366}],\"normalizedVertices\":[]},\"confidence\":0.9479997158050537,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":15,\"y\":312},{\"x\":53,\"y\":311},{\"x\":54,\"y\":365},{\"x\":16,\"y\":366}],\"normalizedVertices\":[]},\"confidence\":0.8811274766921997,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":15,\"y\":312},{\"x\":53,\"y\":311},{\"x\":54,\"y\":365},{\"x\":16,\"y\":366}],\"normalizedVertices\":[]},\"confidence\":0.8811274766921997,\"text\":\"#\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":54,\"y\":311},{\"x\":120,\"y\":310},{\"x\":121,\"y\":364},{\"x\":55,\"y\":365}],\"normalizedVertices\":[]},\"confidence\":0.8283413052558899,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":54,\"y\":311},{\"x\":85,\"y\":310},{\"x\":86,\"y\":364},{\"x\":55,\"y\":365}],\"normalizedVertices\":[]},\"confidence\":0.7980232238769531,\"text\":\"6\"},{\"boundingBox\":{\"vertices\":[{\"x\":89,\"y\":310},{\"x\":120,\"y\":309},{\"x\":121,\"y\":363},{\"x\":90,\"y\":364}],\"normalizedVertices\":[]},\"confidence\":0.8586593866348267,\"text\":\"0\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":124,\"y\":309},{\"x\":145,\"y\":309},{\"x\":146,\"y\":363},{\"x\":125,\"y\":363}],\"normalizedVertices\":[]},\"confidence\":0.7941332459449768,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":124,\"y\":309},{\"x\":145,\"y\":309},{\"x\":146,\"y\":363},{\"x\":125,\"y\":363}],\"normalizedVertices\":[]},\"confidence\":0.7941332459449768,\"text\":\"/\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":140,\"y\":309},{\"x\":189,\"y\":308},{\"x\":190,\"y\":362},{\"x\":141,\"y\":363}],\"normalizedVertices\":[]},\"confidence\":0.9631096720695496,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":140,\"y\":309},{\"x\":152,\"y\":309},{\"x\":153,\"y\":363},{\"x\":141,\"y\":363}],\"normalizedVertices\":[]},\"confidence\":0.9597200751304626,\"text\":\"1\"},{\"boundingBox\":{\"vertices\":[{\"x\":154,\"y\":309},{\"x\":171,\"y\":309},{\"x\":172,\"y\":363},{\"x\":155,\"y\":363}],\"normalizedVertices\":[]},\"confidence\":0.9610674381256104,\"text\":\"0\"},{\"boundingBox\":{\"vertices\":[{\"x\":173,\"y\":308},{\"x\":189,\"y\":308},{\"x\":190,\"y\":362},{\"x\":174,\"y\":362}],\"normalizedVertices\":[]},\"confidence\":0.9685414433479309,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"0\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":199,\"y\":307},{\"x\":299,\"y\":305},{\"x\":300,\"y\":360},{\"x\":200,\"y\":362}],\"normalizedVertices\":[]},\"confidence\":0.9593480825424194,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":199,\"y\":308},{\"x\":216,\"y\":308},{\"x\":217,\"y\":362},{\"x\":200,\"y\":362}],\"normalizedVertices\":[]},\"confidence\":0.9731853604316711,\"text\":\"B\"},{\"boundingBox\":{\"vertices\":[{\"x\":219,\"y\":307},{\"x\":233,\"y\":307},{\"x\":234,\"y\":361},{\"x\":220,\"y\":361}],\"normalizedVertices\":[]},\"confidence\":0.9511810541152954,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":232,\"y\":307},{\"x\":250,\"y\":307},{\"x\":251,\"y\":361},{\"x\":233,\"y\":361}],\"normalizedVertices\":[]},\"confidence\":0.9489949345588684,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":247,\"y\":307},{\"x\":265,\"y\":307},{\"x\":266,\"y\":361},{\"x\":248,\"y\":361}],\"normalizedVertices\":[]},\"confidence\":0.9530544877052307,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":267,\"y\":306},{\"x\":281,\"y\":306},{\"x\":282,\"y\":360},{\"x\":268,\"y\":360}],\"normalizedVertices\":[]},\"confidence\":0.9611271023750305,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":281,\"y\":306},{\"x\":299,\"y\":306},{\"x\":300,\"y\":360},{\"x\":282,\"y\":360}],\"normalizedVertices\":[]},\"confidence\":0.9685454368591309,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"R\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":308,\"y\":305},{\"x\":379,\"y\":303},{\"x\":380,\"y\":358},{\"x\":309,\"y\":360}],\"normalizedVertices\":[]},\"confidence\":0.9593885540962219,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":308,\"y\":305},{\"x\":321,\"y\":305},{\"x\":322,\"y\":359},{\"x\":309,\"y\":359}],\"normalizedVertices\":[]},\"confidence\":0.9682340025901794,\"text\":\"L\"},{\"boundingBox\":{\"vertices\":[{\"x\":323,\"y\":305},{\"x\":340,\"y\":305},{\"x\":341,\"y\":359},{\"x\":324,\"y\":359}],\"normalizedVertices\":[]},\"confidence\":0.9350464344024658,\"text\":\"U\"},{\"boundingBox\":{\"vertices\":[{\"x\":340,\"y\":305},{\"x\":358,\"y\":305},{\"x\":359,\"y\":359},{\"x\":341,\"y\":359}],\"normalizedVertices\":[]},\"confidence\":0.9587975740432739,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":360,\"y\":304},{\"x\":379,\"y\":304},{\"x\":380,\"y\":358},{\"x\":361,\"y\":358}],\"normalizedVertices\":[]},\"confidence\":0.9754762053489685,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"K\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":386,\"y\":303},{\"x\":451,\"y\":302},{\"x\":452,\"y\":357},{\"x\":387,\"y\":358}],\"normalizedVertices\":[]},\"confidence\":0.9879938364028931,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":386,\"y\":304},{\"x\":401,\"y\":304},{\"x\":402,\"y\":358},{\"x\":387,\"y\":358}],\"normalizedVertices\":[]},\"confidence\":0.9937543869018555,\"text\":\"N\"},{\"boundingBox\":{\"vertices\":[{\"x\":405,\"y\":303},{\"x\":418,\"y\":303},{\"x\":419,\"y\":357},{\"x\":406,\"y\":357}],\"normalizedVertices\":[]},\"confidence\":0.9918807744979858,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":417,\"y\":303},{\"x\":435,\"y\":303},{\"x\":436,\"y\":357},{\"x\":418,\"y\":357}],\"normalizedVertices\":[]},\"confidence\":0.9828795790672302,\"text\":\"X\"},{\"boundingBox\":{\"vertices\":[{\"x\":436,\"y\":303},{\"x\":451,\"y\":303},{\"x\":452,\"y\":357},{\"x\":437,\"y\":357}],\"normalizedVertices\":[]},\"confidence\":0.9834606051445007,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"T\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":461,\"y\":302},{\"x\":521,\"y\":301},{\"x\":522,\"y\":355},{\"x\":462,\"y\":356}],\"normalizedVertices\":[]},\"confidence\":0.9787441492080688,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":461,\"y\":302},{\"x\":474,\"y\":302},{\"x\":475,\"y\":356},{\"x\":462,\"y\":356}],\"normalizedVertices\":[]},\"confidence\":0.9797713756561279,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":477,\"y\":302},{\"x\":483,\"y\":302},{\"x\":484,\"y\":356},{\"x\":478,\"y\":356}],\"normalizedVertices\":[]},\"confidence\":0.9837436079978943,\"text\":\"I\"},{\"boundingBox\":{\"vertices\":[{\"x\":486,\"y\":301},{\"x\":503,\"y\":301},{\"x\":504,\"y\":355},{\"x\":487,\"y\":355}],\"normalizedVertices\":[]},\"confidence\":0.9663184285163879,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":507,\"y\":301},{\"x\":521,\"y\":301},{\"x\":522,\"y\":355},{\"x\":508,\"y\":355}],\"normalizedVertices\":[]},\"confidence\":0.9851433038711548,\"text\":\"E\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":523,\"y\":301},{\"x\":530,\"y\":301},{\"x\":531,\"y\":355},{\"x\":524,\"y\":355}],\"normalizedVertices\":[]},\"confidence\":0.9661259651184082,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":523,\"y\":301},{\"x\":530,\"y\":301},{\"x\":531,\"y\":355},{\"x\":524,\"y\":355}],\"normalizedVertices\":[]},\"confidence\":0.9661259651184082,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"!\"}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":141,\"y\":354},{\"x\":406,\"y\":355},{\"x\":406,\"y\":372},{\"x\":141,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.8981595039367676,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":141,\"y\":355},{\"x\":185,\"y\":355},{\"x\":185,\"y\":371},{\"x\":141,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9857678413391113,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":141,\"y\":355},{\"x\":148,\"y\":355},{\"x\":148,\"y\":371},{\"x\":141,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9694616794586182,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":149,\"y\":355},{\"x\":155,\"y\":355},{\"x\":155,\"y\":371},{\"x\":149,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9697219133377075,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":156,\"y\":355},{\"x\":162,\"y\":355},{\"x\":162,\"y\":371},{\"x\":156,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9952802658081055,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":164,\"y\":355},{\"x\":170,\"y\":355},{\"x\":170,\"y\":371},{\"x\":164,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9958299994468689,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":171,\"y\":355},{\"x\":177,\"y\":355},{\"x\":177,\"y\":371},{\"x\":171,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9951652884483337,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":178,\"y\":355},{\"x\":185,\"y\":355},{\"x\":185,\"y\":371},{\"x\":178,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9891479015350342,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":190,\"y\":355},{\"x\":230,\"y\":355},{\"x\":230,\"y\":371},{\"x\":190,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9807137846946716,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":190,\"y\":355},{\"x\":196,\"y\":355},{\"x\":196,\"y\":371},{\"x\":190,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9167952537536621,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":197,\"y\":355},{\"x\":201,\"y\":355},{\"x\":201,\"y\":371},{\"x\":197,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9864477515220642,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":201,\"y\":355},{\"x\":207,\"y\":355},{\"x\":207,\"y\":371},{\"x\":201,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.988124430179596,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":207,\"y\":355},{\"x\":213,\"y\":355},{\"x\":213,\"y\":371},{\"x\":207,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9930729269981384,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":214,\"y\":355},{\"x\":219,\"y\":355},{\"x\":219,\"y\":371},{\"x\":214,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9941148161888123,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":221,\"y\":355},{\"x\":224,\"y\":355},{\"x\":224,\"y\":371},{\"x\":221,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.992881178855896,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":225,\"y\":355},{\"x\":230,\"y\":355},{\"x\":230,\"y\":371},{\"x\":225,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9935600161552429,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"c\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":234,\"y\":355},{\"x\":266,\"y\":355},{\"x\":266,\"y\":371},{\"x\":234,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.836998701095581,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":234,\"y\":355},{\"x\":243,\"y\":355},{\"x\":243,\"y\":371},{\"x\":234,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.936514675617218,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":245,\"y\":355},{\"x\":251,\"y\":355},{\"x\":251,\"y\":371},{\"x\":245,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.510444164276123,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":251,\"y\":355},{\"x\":258,\"y\":355},{\"x\":258,\"y\":371},{\"x\":251,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9720860123634338,\"text\":\"d\"},{\"boundingBox\":{\"vertices\":[{\"x\":260,\"y\":355},{\"x\":266,\"y\":355},{\"x\":266,\"y\":371},{\"x\":260,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.9289500117301941,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"e\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":270,\"y\":355},{\"x\":276,\"y\":355},{\"x\":276,\"y\":371},{\"x\":270,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.6191214919090271,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":270,\"y\":355},{\"x\":276,\"y\":355},{\"x\":276,\"y\":371},{\"x\":270,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.6191214919090271,\"text\":\"(\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":276,\"y\":355},{\"x\":298,\"y\":355},{\"x\":298,\"y\":371},{\"x\":276,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.6503254771232605,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":276,\"y\":355},{\"x\":284,\"y\":355},{\"x\":284,\"y\":371},{\"x\":276,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.6179072260856628,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":283,\"y\":355},{\"x\":290,\"y\":355},{\"x\":290,\"y\":371},{\"x\":283,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.7083690166473389,\"text\":\"P\"},{\"boundingBox\":{\"vertices\":[{\"x\":290,\"y\":355},{\"x\":298,\"y\":355},{\"x\":298,\"y\":371},{\"x\":290,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.624700129032135,\"text\":\"P\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":299,\"y\":355},{\"x\":305,\"y\":355},{\"x\":305,\"y\":371},{\"x\":299,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.8691544532775879,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":299,\"y\":355},{\"x\":305,\"y\":355},{\"x\":305,\"y\":371},{\"x\":299,\"y\":371}],\"normalizedVertices\":[]},\"confidence\":0.8691544532775879,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\")\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":320,\"y\":356},{\"x\":345,\"y\":356},{\"x\":345,\"y\":372},{\"x\":320,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.8630936145782471,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":320,\"y\":356},{\"x\":328,\"y\":356},{\"x\":328,\"y\":372},{\"x\":320,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.945544421672821,\"text\":\"S\"},{\"boundingBox\":{\"vertices\":[{\"x\":328,\"y\":356},{\"x\":334,\"y\":356},{\"x\":334,\"y\":372},{\"x\":328,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.7977296710014343,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":335,\"y\":356},{\"x\":340,\"y\":356},{\"x\":340,\"y\":372},{\"x\":335,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.9313060641288757,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":339,\"y\":356},{\"x\":345,\"y\":356},{\"x\":345,\"y\":372},{\"x\":339,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.7777941823005676,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"o\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":349,\"y\":356},{\"x\":355,\"y\":356},{\"x\":355,\"y\":372},{\"x\":349,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.6293392181396484,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":349,\"y\":356},{\"x\":355,\"y\":356},{\"x\":355,\"y\":372},{\"x\":349,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.6293392181396484,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":359,\"y\":356},{\"x\":406,\"y\":356},{\"x\":406,\"y\":372},{\"x\":359,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.9841226935386658,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":359,\"y\":356},{\"x\":366,\"y\":356},{\"x\":366,\"y\":372},{\"x\":359,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.9615768194198608,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":365,\"y\":356},{\"x\":371,\"y\":356},{\"x\":371,\"y\":372},{\"x\":365,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.9811434149742126,\"text\":\"r\"},{\"boundingBox\":{\"vertices\":[{\"x\":373,\"y\":356},{\"x\":379,\"y\":356},{\"x\":379,\"y\":372},{\"x\":373,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.9881922006607056,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":380,\"y\":356},{\"x\":387,\"y\":356},{\"x\":387,\"y\":372},{\"x\":380,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.991172730922699,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":388,\"y\":356},{\"x\":395,\"y\":356},{\"x\":395,\"y\":372},{\"x\":388,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.9935870170593262,\"text\":\"g\"},{\"boundingBox\":{\"vertices\":[{\"x\":395,\"y\":356},{\"x\":402,\"y\":356},{\"x\":402,\"y\":372},{\"x\":395,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.9916201829910278,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":400,\"y\":356},{\"x\":406,\"y\":356},{\"x\":406,\"y\":372},{\"x\":400,\"y\":372}],\"normalizedVertices\":[]},\"confidence\":0.9815663695335388,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"l\"}]}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":18,\"y\":482},{\"x\":92,\"y\":483},{\"x\":91,\"y\":519},{\"x\":17,\"y\":518}],\"normalizedVertices\":[]},\"blockType\":\"TEXT\",\"confidence\":0.9546774625778198,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":18,\"y\":482},{\"x\":92,\"y\":483},{\"x\":91,\"y\":519},{\"x\":17,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.9546774625778198,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":18,\"y\":482},{\"x\":92,\"y\":484},{\"x\":92,\"y\":500},{\"x\":18,\"y\":498}],\"normalizedVertices\":[]},\"confidence\":0.9351414442062378,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":18,\"y\":483},{\"x\":26,\"y\":483},{\"x\":26,\"y\":498},{\"x\":18,\"y\":498}],\"normalizedVertices\":[]},\"confidence\":0.9678894281387329,\"text\":\"S\"},{\"boundingBox\":{\"vertices\":[{\"x\":26,\"y\":483},{\"x\":33,\"y\":483},{\"x\":33,\"y\":498},{\"x\":26,\"y\":498}],\"normalizedVertices\":[]},\"confidence\":0.9829544425010681,\"text\":\"h\"},{\"boundingBox\":{\"vertices\":[{\"x\":34,\"y\":483},{\"x\":39,\"y\":483},{\"x\":39,\"y\":498},{\"x\":34,\"y\":498}],\"normalizedVertices\":[]},\"confidence\":0.9807009100914001,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":37,\"y\":483},{\"x\":44,\"y\":483},{\"x\":44,\"y\":498},{\"x\":37,\"y\":498}],\"normalizedVertices\":[]},\"confidence\":0.9425870776176453,\"text\":\"t\"},{\"boundingBox\":{\"vertices\":[{\"x\":44,\"y\":483},{\"x\":52,\"y\":483},{\"x\":52,\"y\":498},{\"x\":44,\"y\":498}],\"normalizedVertices\":[]},\"confidence\":0.7831932306289673,\"text\":\"u\"},{\"boundingBox\":{\"vertices\":[{\"x\":52,\"y\":483},{\"x\":62,\"y\":483},{\"x\":62,\"y\":498},{\"x\":52,\"y\":498}],\"normalizedVertices\":[]},\"confidence\":0.8612686395645142,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":62,\"y\":483},{\"x\":70,\"y\":483},{\"x\":70,\"y\":498},{\"x\":62,\"y\":498}],\"normalizedVertices\":[]},\"confidence\":0.930591881275177,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":68,\"y\":484},{\"x\":76,\"y\":484},{\"x\":76,\"y\":499},{\"x\":68,\"y\":499}],\"normalizedVertices\":[]},\"confidence\":0.9551851749420166,\"text\":\"h\"},{\"boundingBox\":{\"vertices\":[{\"x\":77,\"y\":484},{\"x\":84,\"y\":484},{\"x\":84,\"y\":499},{\"x\":77,\"y\":499}],\"normalizedVertices\":[]},\"confidence\":0.9825655817985535,\"text\":\"t\"},{\"boundingBox\":{\"vertices\":[{\"x\":83,\"y\":484},{\"x\":92,\"y\":484},{\"x\":92,\"y\":499},{\"x\":83,\"y\":499}],\"normalizedVertices\":[]},\"confidence\":0.9644781351089478,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"EOL_SURE_SPACE\",\"isPrefix\":false}},\"text\":\"a\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":18,\"y\":505},{\"x\":66,\"y\":505},{\"x\":66,\"y\":518},{\"x\":18,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.9749599099159241,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":18,\"y\":505},{\"x\":24,\"y\":505},{\"x\":24,\"y\":518},{\"x\":18,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.9725132584571838,\"text\":\"F\"},{\"boundingBox\":{\"vertices\":[{\"x\":24,\"y\":505},{\"x\":28,\"y\":505},{\"x\":28,\"y\":518},{\"x\":24,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.9737271666526794,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":27,\"y\":505},{\"x\":34,\"y\":505},{\"x\":34,\"y\":518},{\"x\":27,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.9863051772117615,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":34,\"y\":505},{\"x\":38,\"y\":505},{\"x\":38,\"y\":518},{\"x\":34,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.975512683391571,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":37,\"y\":505},{\"x\":44,\"y\":505},{\"x\":44,\"y\":518},{\"x\":37,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.9839464426040649,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":44,\"y\":505},{\"x\":51,\"y\":505},{\"x\":51,\"y\":518},{\"x\":44,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.9869117736816406,\"text\":\"h\"},{\"boundingBox\":{\"vertices\":[{\"x\":51,\"y\":505},{\"x\":59,\"y\":505},{\"x\":59,\"y\":518},{\"x\":51,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.949411928653717,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":59,\"y\":505},{\"x\":66,\"y\":505},{\"x\":66,\"y\":518},{\"x\":59,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.9713507890701294,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"s\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":68,\"y\":505},{\"x\":76,\"y\":505},{\"x\":76,\"y\":518},{\"x\":68,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.9877776503562927,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":68,\"y\":505},{\"x\":76,\"y\":505},{\"x\":76,\"y\":518},{\"x\":68,\"y\":518}],\"normalizedVertices\":[]},\"confidence\":0.9877776503562927,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"2\"}]}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":127,\"y\":677},{\"x\":204,\"y\":677},{\"x\":204,\"y\":690},{\"x\":127,\"y\":690}],\"normalizedVertices\":[]},\"blockType\":\"TEXT\",\"confidence\":0.9862262010574341,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":127,\"y\":677},{\"x\":204,\"y\":677},{\"x\":204,\"y\":690},{\"x\":127,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9862262010574341,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":127,\"y\":677},{\"x\":169,\"y\":677},{\"x\":169,\"y\":690},{\"x\":127,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9868296980857849,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":127,\"y\":677},{\"x\":134,\"y\":677},{\"x\":134,\"y\":690},{\"x\":127,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9815020561218262,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":134,\"y\":677},{\"x\":141,\"y\":677},{\"x\":141,\"y\":690},{\"x\":134,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9837753176689148,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":141,\"y\":677},{\"x\":147,\"y\":677},{\"x\":147,\"y\":690},{\"x\":141,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.98973149061203,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":148,\"y\":677},{\"x\":155,\"y\":677},{\"x\":155,\"y\":690},{\"x\":148,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9890953302383423,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":155,\"y\":677},{\"x\":162,\"y\":677},{\"x\":162,\"y\":690},{\"x\":155,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9913386702537537,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":162,\"y\":677},{\"x\":169,\"y\":677},{\"x\":169,\"y\":690},{\"x\":162,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9855353832244873,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":173,\"y\":677},{\"x\":204,\"y\":677},{\"x\":204,\"y\":690},{\"x\":173,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9855020046234131,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":173,\"y\":677},{\"x\":179,\"y\":677},{\"x\":179,\"y\":690},{\"x\":173,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9785372614860535,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":181,\"y\":677},{\"x\":185,\"y\":677},{\"x\":185,\"y\":690},{\"x\":181,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.992583692073822,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":184,\"y\":677},{\"x\":190,\"y\":677},{\"x\":190,\"y\":690},{\"x\":184,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.985403299331665,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":190,\"y\":677},{\"x\":196,\"y\":677},{\"x\":196,\"y\":690},{\"x\":190,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9885035157203674,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":197,\"y\":677},{\"x\":204,\"y\":677},{\"x\":204,\"y\":690},{\"x\":197,\"y\":690}],\"normalizedVertices\":[]},\"confidence\":0.9824823141098022,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"s\"}]}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":14,\"y\":802},{\"x\":273,\"y\":804},{\"x\":273,\"y\":821},{\"x\":14,\"y\":819}],\"normalizedVertices\":[]},\"blockType\":\"TEXT\",\"confidence\":0.945541262626648,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":14,\"y\":802},{\"x\":273,\"y\":804},{\"x\":273,\"y\":821},{\"x\":14,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.945541262626648,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":14,\"y\":803},{\"x\":57,\"y\":803},{\"x\":57,\"y\":819},{\"x\":14,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9753842353820801,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":14,\"y\":803},{\"x\":22,\"y\":803},{\"x\":22,\"y\":819},{\"x\":14,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9605990648269653,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":23,\"y\":803},{\"x\":29,\"y\":803},{\"x\":29,\"y\":819},{\"x\":23,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9549709558486938,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":30,\"y\":803},{\"x\":36,\"y\":803},{\"x\":36,\"y\":819},{\"x\":30,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9923012256622314,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":37,\"y\":803},{\"x\":44,\"y\":803},{\"x\":44,\"y\":819},{\"x\":37,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9935225248336792,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":43,\"y\":803},{\"x\":49,\"y\":803},{\"x\":49,\"y\":819},{\"x\":43,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9806501269340515,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":50,\"y\":803},{\"x\":57,\"y\":803},{\"x\":57,\"y\":819},{\"x\":50,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9702615141868591,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":61,\"y\":803},{\"x\":101,\"y\":803},{\"x\":101,\"y\":819},{\"x\":61,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9833044409751892,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":61,\"y\":803},{\"x\":67,\"y\":803},{\"x\":67,\"y\":819},{\"x\":61,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9413865804672241,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":69,\"y\":803},{\"x\":72,\"y\":803},{\"x\":72,\"y\":819},{\"x\":69,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9920915961265564,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":72,\"y\":803},{\"x\":78,\"y\":803},{\"x\":78,\"y\":819},{\"x\":72,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9876295328140259,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":79,\"y\":803},{\"x\":84,\"y\":803},{\"x\":84,\"y\":819},{\"x\":79,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9894824624061584,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":86,\"y\":803},{\"x\":91,\"y\":803},{\"x\":91,\"y\":819},{\"x\":86,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9914233684539795,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":93,\"y\":803},{\"x\":96,\"y\":803},{\"x\":96,\"y\":819},{\"x\":93,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9893298149108887,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":96,\"y\":803},{\"x\":101,\"y\":803},{\"x\":101,\"y\":819},{\"x\":96,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9917879104614258,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"c\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":106,\"y\":803},{\"x\":135,\"y\":803},{\"x\":135,\"y\":819},{\"x\":106,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9648129940032959,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":106,\"y\":803},{\"x\":114,\"y\":803},{\"x\":114,\"y\":819},{\"x\":106,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9699316024780273,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":115,\"y\":803},{\"x\":120,\"y\":803},{\"x\":120,\"y\":819},{\"x\":115,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9153110980987549,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":122,\"y\":803},{\"x\":128,\"y\":803},{\"x\":128,\"y\":819},{\"x\":122,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9869595766067505,\"text\":\"d\"},{\"boundingBox\":{\"vertices\":[{\"x\":129,\"y\":803},{\"x\":135,\"y\":803},{\"x\":135,\"y\":819},{\"x\":129,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9870498180389404,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"e\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":139,\"y\":803},{\"x\":143,\"y\":803},{\"x\":143,\"y\":819},{\"x\":139,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9906935095787048,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":139,\"y\":803},{\"x\":143,\"y\":803},{\"x\":143,\"y\":819},{\"x\":139,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9906935095787048,\"text\":\"(\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":145,\"y\":803},{\"x\":166,\"y\":803},{\"x\":166,\"y\":819},{\"x\":145,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9728409647941589,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":145,\"y\":803},{\"x\":150,\"y\":803},{\"x\":150,\"y\":819},{\"x\":145,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9842888116836548,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":153,\"y\":803},{\"x\":158,\"y\":803},{\"x\":158,\"y\":819},{\"x\":153,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9900275468826294,\"text\":\"P\"},{\"boundingBox\":{\"vertices\":[{\"x\":160,\"y\":803},{\"x\":166,\"y\":803},{\"x\":166,\"y\":819},{\"x\":160,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9442064762115479,\"text\":\"P\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":168,\"y\":803},{\"x\":172,\"y\":803},{\"x\":172,\"y\":819},{\"x\":168,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9791264533996582,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":168,\"y\":803},{\"x\":172,\"y\":803},{\"x\":172,\"y\":819},{\"x\":168,\"y\":819}],\"normalizedVertices\":[]},\"confidence\":0.9791264533996582,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\")\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":179,\"y\":804},{\"x\":185,\"y\":804},{\"x\":185,\"y\":820},{\"x\":179,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.7138687968254089,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":179,\"y\":804},{\"x\":185,\"y\":804},{\"x\":185,\"y\":820},{\"x\":179,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.7138687968254089,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":188,\"y\":804},{\"x\":212,\"y\":804},{\"x\":212,\"y\":820},{\"x\":188,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.8163852095603943,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":188,\"y\":804},{\"x\":195,\"y\":804},{\"x\":195,\"y\":820},{\"x\":188,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.9483979940414429,\"text\":\"S\"},{\"boundingBox\":{\"vertices\":[{\"x\":197,\"y\":804},{\"x\":202,\"y\":804},{\"x\":202,\"y\":820},{\"x\":197,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.5695098638534546,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":204,\"y\":804},{\"x\":207,\"y\":804},{\"x\":207,\"y\":820},{\"x\":204,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.9593292474746704,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":207,\"y\":804},{\"x\":212,\"y\":804},{\"x\":212,\"y\":820},{\"x\":207,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.7883037328720093,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"o\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":217,\"y\":804},{\"x\":222,\"y\":804},{\"x\":222,\"y\":820},{\"x\":217,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.7426162958145142,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":217,\"y\":804},{\"x\":222,\"y\":804},{\"x\":222,\"y\":820},{\"x\":217,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.7426162958145142,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":225,\"y\":804},{\"x\":273,\"y\":804},{\"x\":273,\"y\":820},{\"x\":225,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.984126627445221,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":225,\"y\":804},{\"x\":230,\"y\":804},{\"x\":230,\"y\":820},{\"x\":225,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.9852955937385559,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":233,\"y\":804},{\"x\":238,\"y\":804},{\"x\":238,\"y\":820},{\"x\":233,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.9898634552955627,\"text\":\"r\"},{\"boundingBox\":{\"vertices\":[{\"x\":240,\"y\":804},{\"x\":246,\"y\":804},{\"x\":246,\"y\":820},{\"x\":240,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.9924266934394836,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":247,\"y\":804},{\"x\":252,\"y\":804},{\"x\":252,\"y\":820},{\"x\":247,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.9871536493301392,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":254,\"y\":804},{\"x\":259,\"y\":804},{\"x\":259,\"y\":820},{\"x\":254,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.9885673522949219,\"text\":\"g\"},{\"boundingBox\":{\"vertices\":[{\"x\":260,\"y\":804},{\"x\":268,\"y\":804},{\"x\":268,\"y\":820},{\"x\":260,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.9735172390937805,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":267,\"y\":804},{\"x\":273,\"y\":804},{\"x\":273,\"y\":820},{\"x\":267,\"y\":820}],\"normalizedVertices\":[]},\"confidence\":0.9720625877380371,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"l\"}]}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":6,\"y\":915},{\"x\":536,\"y\":910},{\"x\":537,\"y\":987},{\"x\":7,\"y\":992}],\"normalizedVertices\":[]},\"blockType\":\"TEXT\",\"confidence\":0.9235497713088989,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":6,\"y\":924},{\"x\":535,\"y\":911},{\"x\":536,\"y\":968},{\"x\":7,\"y\":981}],\"normalizedVertices\":[]},\"confidence\":0.9120545387268066,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":6,\"y\":925},{\"x\":46,\"y\":924},{\"x\":47,\"y\":980},{\"x\":7,\"y\":981}],\"normalizedVertices\":[]},\"confidence\":0.7746081948280334,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":6,\"y\":925},{\"x\":46,\"y\":924},{\"x\":47,\"y\":980},{\"x\":7,\"y\":981}],\"normalizedVertices\":[]},\"confidence\":0.7746081948280334,\"text\":\"#\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":45,\"y\":924},{\"x\":115,\"y\":922},{\"x\":116,\"y\":978},{\"x\":46,\"y\":980}],\"normalizedVertices\":[]},\"confidence\":0.7780914306640625,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":45,\"y\":924},{\"x\":77,\"y\":923},{\"x\":78,\"y\":979},{\"x\":46,\"y\":980}],\"normalizedVertices\":[]},\"confidence\":0.7307583093643188,\"text\":\"6\"},{\"boundingBox\":{\"vertices\":[{\"x\":83,\"y\":923},{\"x\":115,\"y\":922},{\"x\":116,\"y\":978},{\"x\":84,\"y\":979}],\"normalizedVertices\":[]},\"confidence\":0.8254246115684509,\"text\":\"0\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":125,\"y\":922},{\"x\":148,\"y\":921},{\"x\":149,\"y\":977},{\"x\":126,\"y\":978}],\"normalizedVertices\":[]},\"confidence\":0.6379895210266113,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":125,\"y\":922},{\"x\":148,\"y\":921},{\"x\":149,\"y\":977},{\"x\":126,\"y\":978}],\"normalizedVertices\":[]},\"confidence\":0.6379895210266113,\"text\":\"/\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":141,\"y\":921},{\"x\":196,\"y\":920},{\"x\":197,\"y\":976},{\"x\":142,\"y\":977}],\"normalizedVertices\":[]},\"confidence\":0.875265896320343,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":141,\"y\":921},{\"x\":156,\"y\":921},{\"x\":157,\"y\":977},{\"x\":142,\"y\":977}],\"normalizedVertices\":[]},\"confidence\":0.8977308869361877,\"text\":\"1\"},{\"boundingBox\":{\"vertices\":[{\"x\":157,\"y\":921},{\"x\":175,\"y\":921},{\"x\":176,\"y\":977},{\"x\":158,\"y\":977}],\"normalizedVertices\":[]},\"confidence\":0.8514923453330994,\"text\":\"0\"},{\"boundingBox\":{\"vertices\":[{\"x\":178,\"y\":920},{\"x\":196,\"y\":920},{\"x\":197,\"y\":976},{\"x\":179,\"y\":976}],\"normalizedVertices\":[]},\"confidence\":0.8765745162963867,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"0\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":204,\"y\":919},{\"x\":302,\"y\":917},{\"x\":303,\"y\":974},{\"x\":205,\"y\":976}],\"normalizedVertices\":[]},\"confidence\":0.9506019353866577,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":204,\"y\":920},{\"x\":221,\"y\":920},{\"x\":222,\"y\":976},{\"x\":205,\"y\":976}],\"normalizedVertices\":[]},\"confidence\":0.9601397514343262,\"text\":\"B\"},{\"boundingBox\":{\"vertices\":[{\"x\":222,\"y\":919},{\"x\":236,\"y\":919},{\"x\":237,\"y\":975},{\"x\":223,\"y\":975}],\"normalizedVertices\":[]},\"confidence\":0.9414055943489075,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":235,\"y\":919},{\"x\":255,\"y\":919},{\"x\":256,\"y\":974},{\"x\":236,\"y\":975}],\"normalizedVertices\":[]},\"confidence\":0.9002711772918701,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":252,\"y\":918},{\"x\":269,\"y\":918},{\"x\":270,\"y\":974},{\"x\":253,\"y\":974}],\"normalizedVertices\":[]},\"confidence\":0.9360558390617371,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":270,\"y\":918},{\"x\":283,\"y\":918},{\"x\":284,\"y\":974},{\"x\":271,\"y\":974}],\"normalizedVertices\":[]},\"confidence\":0.9777674674987793,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":284,\"y\":918},{\"x\":302,\"y\":918},{\"x\":303,\"y\":974},{\"x\":285,\"y\":974}],\"normalizedVertices\":[]},\"confidence\":0.9879717826843262,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"R\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":311,\"y\":917},{\"x\":383,\"y\":915},{\"x\":384,\"y\":971},{\"x\":312,\"y\":973}],\"normalizedVertices\":[]},\"confidence\":0.9393632411956787,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":311,\"y\":917},{\"x\":327,\"y\":917},{\"x\":328,\"y\":973},{\"x\":312,\"y\":973}],\"normalizedVertices\":[]},\"confidence\":0.9478280544281006,\"text\":\"L\"},{\"boundingBox\":{\"vertices\":[{\"x\":326,\"y\":917},{\"x\":345,\"y\":917},{\"x\":346,\"y\":973},{\"x\":327,\"y\":973}],\"normalizedVertices\":[]},\"confidence\":0.9305323958396912,\"text\":\"U\"},{\"boundingBox\":{\"vertices\":[{\"x\":345,\"y\":916},{\"x\":363,\"y\":916},{\"x\":364,\"y\":972},{\"x\":346,\"y\":972}],\"normalizedVertices\":[]},\"confidence\":0.9315751791000366,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":361,\"y\":916},{\"x\":383,\"y\":915},{\"x\":384,\"y\":971},{\"x\":362,\"y\":972}],\"normalizedVertices\":[]},\"confidence\":0.9475172162055969,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"K\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":389,\"y\":914},{\"x\":456,\"y\":912},{\"x\":457,\"y\":969},{\"x\":390,\"y\":971}],\"normalizedVertices\":[]},\"confidence\":0.9804129004478455,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":389,\"y\":915},{\"x\":405,\"y\":915},{\"x\":406,\"y\":971},{\"x\":390,\"y\":971}],\"normalizedVertices\":[]},\"confidence\":0.9850728511810303,\"text\":\"N\"},{\"boundingBox\":{\"vertices\":[{\"x\":408,\"y\":914},{\"x\":422,\"y\":914},{\"x\":423,\"y\":970},{\"x\":409,\"y\":970}],\"normalizedVertices\":[]},\"confidence\":0.9856348037719727,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":422,\"y\":914},{\"x\":442,\"y\":914},{\"x\":443,\"y\":969},{\"x\":423,\"y\":970}],\"normalizedVertices\":[]},\"confidence\":0.9763633012771606,\"text\":\"X\"},{\"boundingBox\":{\"vertices\":[{\"x\":439,\"y\":914},{\"x\":456,\"y\":914},{\"x\":457,\"y\":970},{\"x\":440,\"y\":970}],\"normalizedVertices\":[]},\"confidence\":0.974580705165863,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"T\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":464,\"y\":913},{\"x\":523,\"y\":912},{\"x\":524,\"y\":968},{\"x\":465,\"y\":969}],\"normalizedVertices\":[]},\"confidence\":0.9488906860351562,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":464,\"y\":913},{\"x\":479,\"y\":913},{\"x\":480,\"y\":969},{\"x\":465,\"y\":969}],\"normalizedVertices\":[]},\"confidence\":0.9611458778381348,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":480,\"y\":913},{\"x\":489,\"y\":913},{\"x\":490,\"y\":969},{\"x\":481,\"y\":969}],\"normalizedVertices\":[]},\"confidence\":0.9523510336875916,\"text\":\"I\"},{\"boundingBox\":{\"vertices\":[{\"x\":488,\"y\":912},{\"x\":507,\"y\":912},{\"x\":508,\"y\":968},{\"x\":489,\"y\":968}],\"normalizedVertices\":[]},\"confidence\":0.9298186898231506,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":510,\"y\":912},{\"x\":523,\"y\":912},{\"x\":524,\"y\":968},{\"x\":511,\"y\":968}],\"normalizedVertices\":[]},\"confidence\":0.9522470831871033,\"text\":\"E\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":524,\"y\":912},{\"x\":535,\"y\":912},{\"x\":536,\"y\":968},{\"x\":525,\"y\":968}],\"normalizedVertices\":[]},\"confidence\":0.9405614137649536,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":524,\"y\":912},{\"x\":535,\"y\":912},{\"x\":536,\"y\":968},{\"x\":525,\"y\":968}],\"normalizedVertices\":[]},\"confidence\":0.9405614137649536,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"!\"}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":128,\"y\":969},{\"x\":386,\"y\":971},{\"x\":386,\"y\":989},{\"x\":128,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.93208909034729,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":128,\"y\":970},{\"x\":171,\"y\":970},{\"x\":171,\"y\":987},{\"x\":128,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9853013753890991,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":128,\"y\":970},{\"x\":136,\"y\":970},{\"x\":136,\"y\":987},{\"x\":128,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9811174273490906,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":136,\"y\":970},{\"x\":142,\"y\":970},{\"x\":142,\"y\":987},{\"x\":136,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9887033700942993,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":144,\"y\":970},{\"x\":149,\"y\":970},{\"x\":149,\"y\":987},{\"x\":144,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9961882829666138,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":150,\"y\":970},{\"x\":156,\"y\":970},{\"x\":156,\"y\":987},{\"x\":150,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9951030015945435,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":158,\"y\":970},{\"x\":164,\"y\":970},{\"x\":164,\"y\":987},{\"x\":158,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9858059287071228,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":164,\"y\":970},{\"x\":171,\"y\":970},{\"x\":171,\"y\":987},{\"x\":164,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9648903012275696,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":174,\"y\":970},{\"x\":215,\"y\":970},{\"x\":215,\"y\":987},{\"x\":174,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.985955536365509,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":174,\"y\":970},{\"x\":181,\"y\":970},{\"x\":181,\"y\":987},{\"x\":174,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9560347199440002,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":183,\"y\":970},{\"x\":186,\"y\":970},{\"x\":186,\"y\":987},{\"x\":183,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9919337630271912,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":186,\"y\":970},{\"x\":192,\"y\":970},{\"x\":192,\"y\":987},{\"x\":186,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9856463074684143,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":192,\"y\":970},{\"x\":198,\"y\":970},{\"x\":198,\"y\":987},{\"x\":192,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9937083125114441,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":200,\"y\":970},{\"x\":206,\"y\":970},{\"x\":206,\"y\":987},{\"x\":200,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9930877685546875,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":206,\"y\":970},{\"x\":209,\"y\":970},{\"x\":209,\"y\":987},{\"x\":206,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9929704666137695,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":210,\"y\":970},{\"x\":215,\"y\":970},{\"x\":215,\"y\":987},{\"x\":210,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9883072376251221,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"c\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":220,\"y\":970},{\"x\":248,\"y\":970},{\"x\":248,\"y\":987},{\"x\":220,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9610798358917236,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":220,\"y\":970},{\"x\":228,\"y\":970},{\"x\":228,\"y\":987},{\"x\":220,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9753265380859375,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":229,\"y\":970},{\"x\":234,\"y\":970},{\"x\":234,\"y\":987},{\"x\":229,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9249380826950073,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":236,\"y\":970},{\"x\":242,\"y\":970},{\"x\":242,\"y\":987},{\"x\":236,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.968466579914093,\"text\":\"d\"},{\"boundingBox\":{\"vertices\":[{\"x\":243,\"y\":970},{\"x\":248,\"y\":970},{\"x\":248,\"y\":987},{\"x\":243,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9755880236625671,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"e\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":253,\"y\":970},{\"x\":256,\"y\":970},{\"x\":256,\"y\":987},{\"x\":253,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9893989562988281,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":253,\"y\":970},{\"x\":256,\"y\":970},{\"x\":256,\"y\":987},{\"x\":253,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9893989562988281,\"text\":\"(\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":258,\"y\":970},{\"x\":279,\"y\":970},{\"x\":279,\"y\":988},{\"x\":258,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9801854491233826,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":258,\"y\":970},{\"x\":263,\"y\":970},{\"x\":263,\"y\":987},{\"x\":258,\"y\":987}],\"normalizedVertices\":[]},\"confidence\":0.9938499331474304,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":266,\"y\":971},{\"x\":271,\"y\":971},{\"x\":271,\"y\":988},{\"x\":266,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9909331202507019,\"text\":\"P\"},{\"boundingBox\":{\"vertices\":[{\"x\":274,\"y\":971},{\"x\":279,\"y\":971},{\"x\":279,\"y\":988},{\"x\":274,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9557732343673706,\"text\":\"P\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":282,\"y\":971},{\"x\":286,\"y\":971},{\"x\":286,\"y\":988},{\"x\":282,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9756549000740051,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":282,\"y\":971},{\"x\":286,\"y\":971},{\"x\":286,\"y\":988},{\"x\":282,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9756549000740051,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\")\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":293,\"y\":971},{\"x\":299,\"y\":971},{\"x\":299,\"y\":988},{\"x\":293,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.615188717842102,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":293,\"y\":971},{\"x\":299,\"y\":971},{\"x\":299,\"y\":988},{\"x\":293,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.615188717842102,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":302,\"y\":971},{\"x\":326,\"y\":971},{\"x\":326,\"y\":988},{\"x\":302,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.7408493757247925,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":302,\"y\":971},{\"x\":308,\"y\":971},{\"x\":308,\"y\":988},{\"x\":302,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9608851671218872,\"text\":\"S\"},{\"boundingBox\":{\"vertices\":[{\"x\":311,\"y\":971},{\"x\":316,\"y\":971},{\"x\":316,\"y\":988},{\"x\":311,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.5138400793075562,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":318,\"y\":971},{\"x\":321,\"y\":971},{\"x\":321,\"y\":988},{\"x\":318,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9723050594329834,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":321,\"y\":971},{\"x\":326,\"y\":971},{\"x\":326,\"y\":988},{\"x\":321,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.5163673162460327,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"o\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":331,\"y\":971},{\"x\":335,\"y\":971},{\"x\":335,\"y\":988},{\"x\":331,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.5644825100898743,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":331,\"y\":971},{\"x\":335,\"y\":971},{\"x\":335,\"y\":988},{\"x\":331,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.5644825100898743,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":341,\"y\":971},{\"x\":386,\"y\":971},{\"x\":386,\"y\":988},{\"x\":341,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9880889058113098,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":341,\"y\":971},{\"x\":346,\"y\":971},{\"x\":346,\"y\":988},{\"x\":341,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9904013872146606,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":347,\"y\":971},{\"x\":351,\"y\":971},{\"x\":351,\"y\":988},{\"x\":347,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.988985002040863,\"text\":\"r\"},{\"boundingBox\":{\"vertices\":[{\"x\":354,\"y\":971},{\"x\":359,\"y\":971},{\"x\":359,\"y\":988},{\"x\":354,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9916059970855713,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":361,\"y\":971},{\"x\":366,\"y\":971},{\"x\":366,\"y\":988},{\"x\":361,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9897883534431458,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":367,\"y\":971},{\"x\":374,\"y\":971},{\"x\":374,\"y\":988},{\"x\":367,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9893269538879395,\"text\":\"g\"},{\"boundingBox\":{\"vertices\":[{\"x\":374,\"y\":971},{\"x\":382,\"y\":971},{\"x\":382,\"y\":988},{\"x\":374,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.9883918762207031,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":381,\"y\":971},{\"x\":386,\"y\":971},{\"x\":386,\"y\":988},{\"x\":381,\"y\":988}],\"normalizedVertices\":[]},\"confidence\":0.978122889995575,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"l\"}]}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":237,\"y\":1514},{\"x\":308,\"y\":1514},{\"x\":308,\"y\":1554},{\"x\":237,\"y\":1554}],\"normalizedVertices\":[]},\"blockType\":\"TEXT\",\"confidence\":0.7035821080207825,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":239,\"y\":1514},{\"x\":308,\"y\":1513},{\"x\":308,\"y\":1532},{\"x\":239,\"y\":1533}],\"normalizedVertices\":[]},\"confidence\":0.7866867780685425,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":239,\"y\":1514},{\"x\":287,\"y\":1513},{\"x\":287,\"y\":1532},{\"x\":239,\"y\":1533}],\"normalizedVertices\":[]},\"confidence\":0.9767838716506958,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":239,\"y\":1515},{\"x\":249,\"y\":1515},{\"x\":249,\"y\":1533},{\"x\":239,\"y\":1533}],\"normalizedVertices\":[]},\"confidence\":0.9798133969306946,\"text\":\"S\"},{\"boundingBox\":{\"vertices\":[{\"x\":249,\"y\":1514},{\"x\":255,\"y\":1514},{\"x\":255,\"y\":1532},{\"x\":249,\"y\":1532}],\"normalizedVertices\":[]},\"confidence\":0.9792197346687317,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":255,\"y\":1514},{\"x\":261,\"y\":1514},{\"x\":261,\"y\":1532},{\"x\":255,\"y\":1532}],\"normalizedVertices\":[]},\"confidence\":0.983060896396637,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":259,\"y\":1514},{\"x\":269,\"y\":1514},{\"x\":269,\"y\":1532},{\"x\":259,\"y\":1532}],\"normalizedVertices\":[]},\"confidence\":0.9851507544517517,\"text\":\"v\"},{\"boundingBox\":{\"vertices\":[{\"x\":268,\"y\":1514},{\"x\":278,\"y\":1514},{\"x\":278,\"y\":1532},{\"x\":268,\"y\":1532}],\"normalizedVertices\":[]},\"confidence\":0.980600118637085,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":278,\"y\":1514},{\"x\":287,\"y\":1514},{\"x\":287,\"y\":1532},{\"x\":278,\"y\":1532}],\"normalizedVertices\":[]},\"confidence\":0.9528583884239197,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"SPACE\",\"isPrefix\":false}},\"text\":\"r\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":292,\"y\":1514},{\"x\":308,\"y\":1514},{\"x\":308,\"y\":1532},{\"x\":292,\"y\":1532}],\"normalizedVertices\":[]},\"confidence\":0.4064926207065582,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":292,\"y\":1514},{\"x\":297,\"y\":1514},{\"x\":297,\"y\":1532},{\"x\":292,\"y\":1532}],\"normalizedVertices\":[]},\"confidence\":0.4460776746273041,\"text\":\"I\"},{\"boundingBox\":{\"vertices\":[{\"x\":299,\"y\":1514},{\"x\":305,\"y\":1514},{\"x\":305,\"y\":1532},{\"x\":299,\"y\":1532}],\"normalizedVertices\":[]},\"confidence\":0.3852933943271637,\"text\":\"I\"},{\"boundingBox\":{\"vertices\":[{\"x\":302,\"y\":1514},{\"x\":308,\"y\":1514},{\"x\":308,\"y\":1532},{\"x\":302,\"y\":1532}],\"normalizedVertices\":[]},\"confidence\":0.3881067931652069,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"I\"}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":237,\"y\":1544},{\"x\":305,\"y\":1544},{\"x\":305,\"y\":1554},{\"x\":237,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.6204774379730225,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":237,\"y\":1544},{\"x\":305,\"y\":1544},{\"x\":305,\"y\":1554},{\"x\":237,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.6204774379730225,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":237,\"y\":1544},{\"x\":241,\"y\":1544},{\"x\":241,\"y\":1554},{\"x\":237,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.8158155083656311,\"text\":\"1\"},{\"boundingBox\":{\"vertices\":[{\"x\":243,\"y\":1544},{\"x\":250,\"y\":1544},{\"x\":250,\"y\":1554},{\"x\":243,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.6170766353607178,\"text\":\"9\"},{\"boundingBox\":{\"vertices\":[{\"x\":251,\"y\":1544},{\"x\":258,\"y\":1544},{\"x\":258,\"y\":1554},{\"x\":251,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.37682995200157166,\"text\":\"9\"},{\"boundingBox\":{\"vertices\":[{\"x\":259,\"y\":1544},{\"x\":266,\"y\":1544},{\"x\":266,\"y\":1554},{\"x\":259,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.3790741562843323,\"text\":\"9\"},{\"boundingBox\":{\"vertices\":[{\"x\":268,\"y\":1544},{\"x\":273,\"y\":1544},{\"x\":273,\"y\":1554},{\"x\":268,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.6076504588127136,\"text\":\"/\"},{\"boundingBox\":{\"vertices\":[{\"x\":275,\"y\":1544},{\"x\":282,\"y\":1544},{\"x\":282,\"y\":1554},{\"x\":275,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.5851329565048218,\"text\":\"2\"},{\"boundingBox\":{\"vertices\":[{\"x\":282,\"y\":1544},{\"x\":289,\"y\":1544},{\"x\":289,\"y\":1554},{\"x\":282,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.6903365850448608,\"text\":\"0\"},{\"boundingBox\":{\"vertices\":[{\"x\":291,\"y\":1544},{\"x\":298,\"y\":1544},{\"x\":298,\"y\":1554},{\"x\":291,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.7442842721939087,\"text\":\"0\"},{\"boundingBox\":{\"vertices\":[{\"x\":299,\"y\":1544},{\"x\":305,\"y\":1544},{\"x\":305,\"y\":1554},{\"x\":299,\"y\":1554}],\"normalizedVertices\":[]},\"confidence\":0.7680962085723877,\"property\":{\"detectedLanguages\":[],\"detectedBreak\":{\"type\":\"LINE_BREAK\",\"isPrefix\":false}},\"text\":\"0\"}]}]}]}],\"confidence\":0.9001938700675964,\"property\":{\"detectedLanguages\":[{\"confidence\":0.9048933386802673,\"languageCode\":\"en\"}]},\"width\":580,\"height\":1634}],\"text\":\"#60/100\\nRanked Classic Mode (TPP) Sala Erangel\\n#60/100 BETTER LUCK NEXT TIME!\\nRanked Classic Mode (TPP) Solo - Erangel\\nShituMehta\\nFinishes 2\\nRanked Class\\nRanked Classic Mode (TPP) - Solo - Erangel\\n#60/100 BETTER LUCK NEXT TIME!\\nRanked Classic Mode (TPP) - Solo - Erangel\\nSilver III\\n1999/2000\"}",
            JsonObject::class.java
        )
        val label1 = TFResult(
            8.0f,
            0.359375f,
            floatArrayOf(0.7250951f, 0.43908218f, 0.8050067f, 0.502629f),
            Resolution(1600, 720),
            "[.]ShituMehta[.]Finishes 2"
        )
        val label2 = TFResult(
            5.0f,
            0.78515625f,
            floatArrayOf(0.080632284f, 0.118171684f, 0.12887852f, 0.30392534f),
            Resolution(1600, 720),
            "Ranked Classic Mạde (TPP) Solo - Erange"
        )
        val label3 = TFResult(
            4.0f,
            0.8515625f,
            floatArrayOf(0.0044608936f, 0.04179342f, 0.15133315f, 0.39261663f),
            Resolution(1600, 720),
            "EHIA0D BETTER LUCK NEXT TIME! Ranked Classic Made (TPP)- Sola - Erangel"
        )
        val label4 = TFResult(
            7.0f,
            0.953125f,
            floatArrayOf(0.009324133f, 0.04601962f, 0.14385039f, 0.16371405f),
            Resolution(1600, 720),
            "#G000 Ranked"
        )
        val label5 = TFResult(
            5.0f,
            0.734375f,
            floatArrayOf(0.08427615f, 0.11670433f, 0.12120461f, 0.2989446f),
            Resolution(1600, 720),
            "Ranked Classic Mode (TPP) - Salo - Erangel"
        )
        val label6 = TFResult(
            7.0f,
            0.9765625f,
            floatArrayOf(0.0067185313f, 0.046789236f, 0.14917628f, 0.17382559f),
            Resolution(1600, 720),
            "Ranked Class"
        )
        val label7 = TFResult(
            6.0f,
            0.98046875f,
            floatArrayOf(0.07469031f, 0.33201265f, 0.71923506f, 0.68860304f),
            Resolution(1600, 720),
            ".]Silver ll[.]1929/9nnn"
        )
        val label8 = TFResult(
            4.0f,
            0.8984375f,
            floatArrayOf(0.0064065903f, 0.045111194f, 0.14777544f, 0.38929886f),
            Resolution(1600, 720),
            "HI ADD BETTER LUCK NEXT TIME! Ranked Classic Mode (TPp) - Solo - Erangel"
        )
        val tfResultList = arrayListOf(
            arrayListOf(label4, label2, label3, label1), arrayListOf(label6, label5, label8, label7)
        )
        var tfCoordinateList: ArrayList<HashMap<TFResult, Pair<Int, Int>>> = arrayListOf(
            hashMapOf(
                label4 to Pair(0, 97),
                label2 to Pair(177, 211),
                label3 to Pair(291, 396),
                label1 to Pair(476, 533)
            ), hashMapOf(
                label6 to Pair(613, 716),
                label5 to Pair(796, 823),
                label8 to Pair(908, 1010),
                label7 to Pair(1090, 1554)
            )
        )
        var imagePaths: ArrayList<String> = gson.fromJson(
            "[\"rank_kills_screen_1685893769554.jpg\",\"rank_ratings_screen_1685893756477.jpg\"]",
            pathType
        )
        var originalGame: Game = gson.fromJson(
            "{\"id\":2,\"userId\":\"55632813178\",\"valid\":true,\"rank\":\"Un-Known\",\"gameInfo\":\"{\\\"type\\\":\\\"Classic\\\",\\\"view\\\":\\\"TPP\\\",\\\"group\\\":\\\"solo\\\",\\\"mode\\\":\\\"Erangel\\\"}\",\"kills\":\"2\",\"teamRank\":\"Un-Known\",\"initialTier\":\"Silver II\",\"finalTier\":\"Silver II\",\"endTimestamp\":\"1685893756548\",\"startTimeStamp\":\"1685893327554\",\"gameId\":\"1685893327554\",\"synced\":0,\"metaInfoJson\":\"Un-Known\"}",
            Game::class.java
        )
        var obtainedGame = MLKitOCR.updateResultWithResponse(
            annotation,
            tfResultList,
            tfCoordinateList,
            imagePaths,
            originalGame,
            "55621161615",
            "ROSHANBETA"
        )
        Truth.assertThat(obtainedGame.rank).isEqualTo("60")
    }

    @Test
    fun validateSquadScoringInUpdateUI() {
        val gson = Gson()
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        var tfList: ArrayList<ImageResultJsonFlat> = gson.fromJson(
            "[{\"epochTimestamp\":1686480541271,\"fileName\":\"1686480541271.jpg\",\"labels\":[{\"box\":[0.0024840161,0.036675114,0.15220177,0.1388662],\"confidence\":0.703125,\"label\":7,\"ocr\":\" #732 You enked DlessicM\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.08057022,0.10172436,0.12974659,0.29105914],\"confidence\":0.53515625,\"label\":5,\"ocr\":\" Ranked Classic Made (TPP)- SquadNusa\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0059283897,0.043334574,0.1369003,0.42200106],\"confidence\":0.53515625,\"label\":4,\"ocr\":\" #732 You made the Top 10! Keep it up! anked Classi: Mode (TPP}- Squad \\u003dNusa\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7235792,0.35965514,0.80329853,0.44247782],\"confidence\":0.5,\"label\":8,\"ocr\":\"[.]AXOM\\u003d LUCIFER[.]ishes \\u0026 Assist:\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.72364724,0.5216223,0.8080666,0.5913671],\"confidence\":0.359375,\"label\":8,\"ocr\":\"[.]J AME9ERX[.]ishes 0 Assist:\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        var result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        var scoring =
            MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(2)
        var score1: JsonObject = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        var score2: JsonObject = gson.fromJson(scoring[1].toString(), JsonObject::class.java)
        Truth.assertThat(score1["kills"].asInt).isEqualTo(0)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(8)

        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686480542032,\"fileName\":\"1686480542032.jpg\",\"labels\":[{\"box\":[0.0015826374,0.036482908,0.1484686,0.13674113],\"confidence\":0.80859375,\"label\":7,\"ocr\":\" #32 You nked Dassic h\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.08068554,0.10512694,0.12801924,0.2908806],\"confidence\":0.5703125,\"label\":5,\"ocr\":\" Ranked Classic Mode (TPP)- Sauad\\u003dNuS8\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0068855286,0.04308711,0.13785744,0.41459143],\"confidence\":0.5,\"label\":4,\"ocr\":\" #732 You made the Top 0! Keep it up! anked Classic Mode (TPP)- Shuad \\u003dNusa\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7228108,0.3604384,0.80406696,0.44169456],\"confidence\":0.5,\"label\":8,\"ocr\":\"[.]AXOM LUCIFER[.]1es 8 Assists 0 M\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.72363955,0.519338,0.8064622,0.5904274],\"confidence\":0.39453125,\"label\":8,\"ocr\":\"[.]AME9FRX[.]Ies D Assists 2\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(2)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)
        Truth.assertThat(score1["kills"].asInt).isEqualTo(0)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(8)

        MachineConstants.machineInputValidator.clear()

        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686786754893,\"fileName\":\"1686786754893.jpg\",\"labels\":[{\"box\":[0.009124249,0.04849281,0.1386097,0.16396114],\"confidence\":0.9296875,\"label\":7,\"ocr\":\" Ranked Classic N\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.71558714,0.17751852,0.8000065,0.25425065],\"confidence\":0.60546875,\"label\":8,\"ocr\":\"[.]| JÆČDB[.]Finishes D Assi\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.081035286,0.12530784,0.12928152,0.32580554],\"confidence\":0.80859375,\"label\":5,\"ocr\":\" Ranked Classic Mode (TPP) -Squad - Erangel\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.013252348,0.044859767,0.13931768,0.40244645],\"confidence\":0.60546875,\"label\":4,\"ocr\":\" #85 96 BETTER LUCK NEXT TIME! Ranked Cassc Mode(TPP) - Squad-Erangel\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.7241865,0.34686384,0.79946727,0.42507526],\"confidence\":0.359375,\"label\":8,\"ocr\":\"[.]STROMEDY999[.]Finishes D Assi\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.72150815,0.4859689,0.8143842,0.57884496],\"confidence\":0.39453125,\"label\":8,\"ocr\":\"[.]Helofrel[.]Finishes 1 A[.]3\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.72104275,0.6812952,0.7977749,0.7610145],\"confidence\":0.53515625,\"label\":8,\"ocr\":\"[.]Finishes D Assi\",\"resolution\":{\"first\":1520,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(3)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)
        var score3 = gson.fromJson(scoring[2].toString(), JsonObject::class.java)
        Truth.assertThat(score1["kills"].asInt).isEqualTo(1)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(0)
        Truth.assertThat(score3["kills"].asInt).isEqualTo(0)


        MachineConstants.machineInputValidator.clear()

        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686797280397,\"fileName\":\"1686797280397.jpg\",\"labels\":[{\"box\":[0.016605534,0.04465948,0.136569,0.16235392],\"confidence\":0.8828125,\"label\":7,\"ocr\":\" anked Elassic Mt\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.08283705,0.1296005,0.13392782,0.31184077],\"confidence\":0.8828125,\"label\":5,\"ocr\":\" Ranked Elassic Mode (1PP) Squad - Erangel\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.0038511455,0.04179342,0.15355493,0.39261663],\"confidence\":0.83203125,\"label\":4,\"ocr\":\" #25 97 BEIER LUCK NEXT TIME! \\\"ankad Clsi Milef Syund - Erangel\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7260576,0.35175672,0.80888027,0.42018238],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]RABBITTZ[.]Finishes 1 Assi\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7290225,0.5275188,0.8043033,0.59997874],\"confidence\":0.8515625,\"label\":8,\"ocr\":\"[.]NiGhTWalf9792[.]Finishes D Ass\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(2)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(1)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(0)

        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686797280744,\"fileName\":\"1686797280744.jpg\",\"labels\":[{\"box\":[0.010348804,0.042368595,0.13738516,0.16464481],\"confidence\":0.91015625,\"label\":7,\"ocr\":\" #25 97 BET Ranked Elassic Mi\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.08218224,0.12623179,0.13135861,0.31198543],\"confidence\":0.8828125,\"label\":5,\"ocr\":\" Ranked Elassic Mode (TAP)- Squad - Erangel\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.0030451342,0.034964755,0.15274891,0.3994453],\"confidence\":0.8515625,\"label\":4,\"ocr\":\" #25 97 BETTER LUCK NEXT TIME! Renked Elassic Mode TPP)- Squad - Erangel\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7260652,0.34778225,0.8104846,0.43060493],\"confidence\":0.80859375,\"label\":8,\"ocr\":\"[.]RABBITTZ[.]Finishes 1 Assi:\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7260348,0.5255012,0.807291,0.6052205],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]NiGhTWaLf9792[.]Finishes D Assi\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(2)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(1)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(0)

        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686797297281,\"fileName\":\"1686797297281.jpg\",\"labels\":[{\"box\":[0.0065159425,0.039648306,0.13849774,0.16192451],\"confidence\":0.94140625,\"label\":7,\"ocr\":\" #2597 BET ankad Classic M\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.082585245,0.1278438,0.13176161,0.31359747],\"confidence\":0.8515625,\"label\":5,\"ocr\":\" Ranked Classic Made (1HP) Syuad - Erangel\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.0020428672,0.038569376,0.14891514,0.3893926],\"confidence\":0.83203125,\"label\":4,\"ocr\":\" #25 97 BETIER LUCK NEXT TIME! anked Glassic Mode fi Squad- Erangel\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7220274,0.35190314,0.80807436,0.42164797],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]RABBITTZ[.]inishes 1 Assis\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7236168,0.5275188,0.80487293,0.59997874],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]NiGhTWalf9792[.]inishes D Assis\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(2)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(1)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(0)


        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686797310466,\"fileName\":\"1686797310466.jpg\",\"labels\":[{\"box\":[0.006603852,0.039255224,0.1411301,0.15472355],\"confidence\":0.94140625,\"label\":7,\"ocr\":\" #2597 BET enked BlassiC\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.08339126,0.12682053,0.13256761,0.3125742],\"confidence\":0.8515625,\"label\":5,\"ocr\":\" Ranked lassic Mode (1PP) Shuad - Erangel\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.0020428672,0.04188715,0.14891514,0.38607484],\"confidence\":0.83203125,\"label\":4,\"ocr\":\" #25/97 BETTER LUCK NEXT TIME! Rankat Clasic Moe (iP)- Squad- Erangal\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.7212214,0.35175672,0.8072683,0.42018238],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]RABBITTZ[.]inishes I Assis\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.72522885,0.526592,0.806485,0.5976815],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]!NiGhTWolLf9792[.]inishes D Assis\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(2)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(1)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(0)

        MachineConstants.machineInputValidator.clear()

        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686731980723,\"fileName\":\"1686731980723.jpg\",\"labels\":[{\"box\":[0.007963993,0.038698282,0.14249024,0.14983994],\"confidence\":0.9296875,\"label\":7,\"ocr\":\" #22/32 BET ORanked Clas\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07710551,0.117993146,0.12918125,0.30732793],\"confidence\":0.76171875,\"label\":5,\"ocr\":\" ORanked Classic Mode (TPP) Duo - Nusa\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0058311448,0.0386941,0.1419028,0.37637174],\"confidence\":0.60546875,\"label\":4,\"ocr\":\" #22 32 BETTER LUCK NEXT TIME! O Ranked Llassic Mode (|PP\\u003d Dup- Nuse\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.72352713,0.34766984,0.80173856,0.42588127],\"confidence\":0.5703125,\"label\":8,\"ocr\":\"[.]TSxAbhay[.]Finishes 1Assi\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7244228,0.51663506,0.80567896,0.59635437],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]TSxPrakash[.]Finishes 5 Ass\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(2)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(5)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(1)

        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686731981091,\"fileName\":\"1686731981091.jpg\",\"labels\":[{\"box\":[0.006667234,0.038698282,0.143787,0.14983994],\"confidence\":0.921875,\"label\":7,\"ocr\":\" ORanked Clas\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07767816,0.11616807,0.1278026,0.30915302],\"confidence\":0.734375,\"label\":5,\"ocr\":\" Ranked Classic Mode (TPP) Dup - Nusa\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.006637156,0.038663507,0.14270882,0.36995423],\"confidence\":0.5703125,\"label\":4,\"ocr\":\" #22/32 BETTER LUCK NEXT TIME! O Ranked Classic Made (TPP) Dup - Nusa\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7227732,0.34921554,0.8024925,0.42594764],\"confidence\":0.640625,\"label\":8,\"ocr\":\"[.]TSxAbhay[.]inishes 1 Assist\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.72203517,0.51663506,0.80645454,0.59635437],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]SxPrakash[.]inishes 5 Assis\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(2)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(5)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(1)


        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686731981448,\"fileName\":\"1686731981448.jpg\",\"labels\":[{\"box\":[0.007963993,0.038698282,0.14249024,0.14983994],\"confidence\":0.921875,\"label\":7,\"ocr\":\" #22/32 BET\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07710551,0.117993146,0.12918125,0.30732793],\"confidence\":0.76171875,\"label\":5,\"ocr\":\" Ranked Classic Mode(TPP) Duo - Nusa\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0050251335,0.035439074,0.1410968,0.37962675],\"confidence\":0.640625,\"label\":4,\"ocr\":\" #22/32 BETTER LUCK NEXT TIME! O Ranked Classic Mode (TPP) Dup - Nuss\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7235792,0.34614748,0.80329853,0.42740363],\"confidence\":0.640625,\"label\":8,\"ocr\":\"[.]TSxAbhay[.]shes 1 Assists\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7220274,0.51586664,0.80807436,0.5971228],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]TSxPrakash[.]ishes 5 Assists\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(2)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(5)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(1)

        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686731970730,\"fileName\":\"1686731970730.jpg\",\"labels\":[{\"box\":[0.005243711,0.037895083,0.13976997,0.15336342],\"confidence\":0.8828125,\"label\":7,\"ocr\":\" #22/32 BET Ranked Dlasi\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.07579751,0.123263866,0.12887722,0.3020572],\"confidence\":0.640625,\"label\":5,\"ocr\":\" ORanked Classic Made (TPP- Duo - Nusa\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0010138229,0.044870347,0.1451081,0.3637474],\"confidence\":0.46484375,\"label\":4,\"ocr\":\" #22/32 BETTER LUCK NEXT TIME! ) Rankad lassic Mode (TPP)- Duo - Nusa\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7211988,0.34852797,0.80245495,0.4282472],\"confidence\":0.60546875,\"label\":8,\"ocr\":\"[.]TSxAbhay[.]Finishes 1 Assi\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7212214,0.51586664,0.8072683,0.5971228],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]SxPrakash[.]Finishes 5 Ass\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(2)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(5)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(1)

        MachineConstants.machineInputValidator.clear()
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686816325105,\"fileName\":\"1686816325105.jpg\",\"labels\":[{\"box\":[0.012027033,0.038357574,0.13570693,0.1227763],\"confidence\":0.8828125,\"label\":7,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.080327176,0.08721618,0.1267656,0.27655095],\"confidence\":0.78515625,\"label\":5,\"ocr\":\"Ranked Classic Mode (TPP)6SquadkErangl\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.005505964,0.03866312,0.13900395,0.3828508],\"confidence\":0.8828125,\"label\":4,\"ocr\":\"/97 Winer Winner Chicken Dinner! Ranked Classic Mode(TPP) Squad Erangal\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.71874315,0.43749186,0.79846245,0.51721114],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]lickydancerg[.]Finishes 13\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(1)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(13)

        MachineConstants.machineInputValidator.clear()
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686828638571,\"fileName\":\"1686828638571.jpg\",\"labels\":[{\"box\":[0.0154968165,0.03966666,0.13455442,0.15209559],\"confidence\":0.8828125,\"label\":7,\"ocr\":\" anked Classic A\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.7243852,0.17521894,0.8041045,0.2549382],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]GodleJonathan[.]inishes 2 Assis\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.079613015,0.12334271,0.13070379,0.29875433],\"confidence\":0.921875,\"label\":5,\"ocr\":\" Ranked Classic Mode (TPP) - Squad - Livik\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.0093555525,0.044859767,0.14805052,0.40244645],\"confidence\":0.80859375,\"label\":4,\"ocr\":\" #O52 BETTER LUCK NEXT TIME! anked Classic Mode (TPP)- Squad - Livk\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.7259452,0.34858826,0.8041566,0.43141094],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]aagriZ amkar[.]inishes 2 Assis\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.7267512,0.5307429,0.80496264,0.6032028],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]wkinge- Akki[.]inishes 5 Assis\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.72522885,0.7025474,0.806485,0.7764043],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]IOXIC 1SM4[.]inishes 1 Assis\",\"resolution\":{\"first\":1520,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(4)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)
        score3 = gson.fromJson(scoring[2].toString(), JsonObject::class.java)
        var score4 = gson.fromJson(scoring[3].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(2)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(5)
        Truth.assertThat(score3["kills"].asInt).isEqualTo(2)
        Truth.assertThat(score4["kills"].asInt).isEqualTo(1)

        MachineConstants.machineInputValidator.clear()
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686763251184,\"fileName\":\"1686763251184.jpg\",\"labels\":[{\"box\":[0.006667234,0.03430879,0.143787,0.14334829],\"confidence\":0.8984375,\"label\":7,\"ocr\":\" #8/97 You Ranked Classie\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.72845286,0.17441294,0.809709,0.25413218],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]éternatus[.]Finishes 3 Ass\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.083506584,0.104487106,0.13084027,0.30119258],\"confidence\":0.8828125,\"label\":5,\"ocr\":\" Ranked Classe Made (TPP) Sauad-Erangel\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.0039816946,0.031601474,0.14267555,0.4031058],\"confidence\":0.671875,\"label\":4,\"ocr\":\" #897 You made the Top 10! Keep it up! Ranked Dlasse Made(TP Suad- tr angal\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.725228,0.3436398,0.81293386,0.42185122],\"confidence\":0.8515625,\"label\":8,\"ocr\":\"[.]ShubhSaraogit[.]Finishes 3 Ass[.]S\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.72363955,0.5222771,0.8064622,0.6019964],\"confidence\":0.8671875,\"label\":8,\"ocr\":\"[.]ShivamNegi279[.]Finishes D Ass\",\"resolution\":{\"first\":1560,\"second\":720}},{\"box\":[0.72525144,0.6906917,0.8112984,0.78181183],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]SHiVAmSaraoGl[.]Finishes 7 Assi\",\"resolution\":{\"first\":1560,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(4)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)
        score3 = gson.fromJson(scoring[2].toString(), JsonObject::class.java)
        score4 = gson.fromJson(scoring[3].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(3)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(7)
        Truth.assertThat(score3["kills"].asInt).isEqualTo(3)
        Truth.assertThat(score4["kills"].asInt).isEqualTo(0)


        MachineConstants.machineInputValidator.clear()
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686908938656,\"fileName\":\"1686908938656.jpg\",\"labels\":[{\"box\":[0.006667234,0.038698282,0.143787,0.14983994],\"confidence\":0.8984375,\"label\":7,\"ocr\":\" anked Cassc Mt\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7244228,0.20188487,0.80567896,0.2729743],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]DRKZISHAN[.]hes1 Asssts |\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.080096185,0.11509231,0.13022062,0.29733258],\"confidence\":0.8515625,\"label\":5,\"ocr\":\" Ranked Classic Mode (TPP) Squad- Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.0026447698,0.042771935,0.14401248,0.4072495],\"confidence\":0.78515625,\"label\":4,\"ocr\":\" RD You made the Top 10! Keep it up!\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7266848,0.36390975,0.80341697,0.4349992],\"confidence\":0.640625,\"label\":8,\"ocr\":\"[.]7EAGLECRE[.]hes I Assists 0[.]WOI\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72522885,0.52279025,0.806485,0.5966472],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]TicketwalaAbh[.]Ies D Assists 0\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7258508,0.6825529,0.80425096,0.7485735],\"confidence\":0.5,\"label\":8,\"ocr\":\"[.]DRKZ JUNAID[.]Jhes 2 Assists I\",\"resolution\":{\"first\":1650,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(4)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)
        score3 = gson.fromJson(scoring[2].toString(), JsonObject::class.java)
        score4 = gson.fromJson(scoring[3].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(2)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(1)
        Truth.assertThat(score3["kills"].asInt).isEqualTo(1)
        Truth.assertThat(score4["kills"].asInt).isEqualTo(0)

        MachineConstants.machineInputValidator.clear()
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686913714495,\"fileName\":\"1686913714495.jpg\",\"labels\":[{\"box\":[0.016655456,0.04383702,0.13571306,0.17234474],\"confidence\":0.9609375,\"label\":7,\"ocr\":\" #43 99 BETT Ranked Classic- Mod\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.73071486,0.19338572,0.807447,0.26051718],\"confidence\":0.46484375,\"label\":8,\"ocr\":\"[.]inishes 0 As\\u003d\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.08082203,0.13326862,0.1319128,0.31902227],\"confidence\":0.8671875,\"label\":5,\"ocr\":\" Ranked Classic Made (TPP)- Squad- Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.0072126016,0.03789945,0.14858145,0.40940678],\"confidence\":0.8828125,\"label\":4,\"ocr\":\" #A3\\u002799 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP)-Squad- Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72519124,0.35643387,0.80491054,0.42356533],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]SexDViNeWNL[.]nishes I Assis\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72593737,0.52240086,0.80285466,0.5909916],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]JuniorfTR[.]inishes D Assis\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72934306,0.68313485,0.8033782,0.74668163],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]Pratiksha[.]inishes 3 Assis\",\"resolution\":{\"first\":1650,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686913710286,\"fileName\":\"1686913710286.jpg\",\"labels\":[{\"box\":[0.006667234,0.050529446,0.143787,0.17280565],\"confidence\":0.96484375,\"label\":7,\"ocr\":\" #43/99 BETI Ranked Dlassic Mot\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.08052493,0.1301439,0.13462795,0.33064163],\"confidence\":0.76171875,\"label\":5,\"ocr\":\" Ranked Classic Made (TPP) - Squad- Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.0093555525,0.04833524,0.14805052,0.39252293],\"confidence\":0.359375,\"label\":4,\"ocr\":\" #43/99 BETTER LUCK NEXT TIME! Ranked Dlassic Mode(1PP) - Squad-trangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7272471,0.36119485,0.8041644,0.4235397],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]| ScxOVNeWN[.]Finishes I Ass\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7287909,0.5286565,0.79738164,0.58866525],\"confidence\":0.671875,\"label\":8,\"ocr\":\"[.]JuniorfTR[.]Finishes 0 Ass\",\"resolution\":{\"first\":1650,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686913710634,\"fileName\":\"1686913710634.jpg\",\"labels\":[{\"box\":[0.010587677,0.0474033,0.14409812,0.17109573],\"confidence\":0.9609375,\"label\":7,\"ocr\":\" #43 99 BETI Ranked PaEeie Mod\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7276773,0.19596265,0.81209666,0.26438832],\"confidence\":0.640625,\"label\":8,\"ocr\":\"[.]ADDJ[.]Finishes 0 Ass\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.08137623,0.13429189,0.13055259,0.32004553],\"confidence\":0.8671875,\"label\":5,\"ocr\":\" Ranked ClassicMade (TPP) Squad- Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.006072916,0.048241496,0.15294519,0.39906472],\"confidence\":0.83203125,\"label\":4,\"ocr\":\" #43\\u002799 BETTER LUCK NEXT TIME! Ranked Casie Made (TPPI Squad - Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7170728,0.3545756,0.8081929,0.4270356],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]SexDiViNeWNL[.]Finishes 1 Assi\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7196094,0.52026474,0.8056563,0.5927247],\"confidence\":0.80859375,\"label\":8,\"ocr\":\"[.]JuniorfTR[.]Finishes D Ass\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7187799,0.6851711,0.8064858,0.7523025],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]Pratiksha[.]Finishes 3 Ass\",\"resolution\":{\"first\":1650,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686913710983,\"fileName\":\"1686913710983.jpg\",\"labels\":[{\"box\":[0.01434917,0.043893695,0.13570207,0.16997078],\"confidence\":0.9609375,\"label\":7,\"ocr\":\" #4399 BETI Ranked lascicMud\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7326628,0.19910736,0.8052976,0.26265416],\"confidence\":0.53515625,\"label\":8,\"ocr\":\"[.]inishes 0 Assi\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.07992654,0.13502531,0.1320023,0.31726557],\"confidence\":0.8828125,\"label\":5,\"ocr\":\" Ranked Clasic Made (TPP) Squad- Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.0064065903,0.04141283,0.14777544,0.4058934],\"confidence\":0.8828125,\"label\":4,\"ocr\":\" #A3\\u002799 BETTER LUCK NEXT TIME! Ranked Classic Made (TPP)-Squad- Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72363955,0.35465074,0.8064622,0.42051244],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]SexDViNeWN[.]A inishes I Ass\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72731966,0.52365535,0.8027821,0.58842725],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]| JuniorFTR[.]J inishes D Assis\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7274237,0.68248,0.80005854,0.74602675],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]Pratiksha[.]inishes 3 Assis\",\"resolution\":{\"first\":1650,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686913713442,\"fileName\":\"1686913713442.jpg\",\"labels\":[{\"box\":[0.011987075,0.04267838,0.13806416,0.17118609],\"confidence\":0.9609375,\"label\":7,\"ocr\":\" #43-99 BETT Ranked Clssir Mad\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.7319627,0.19910736,0.8059977,0.26265416],\"confidence\":0.5,\"label\":8,\"ocr\":\"[.]ADDJ[.]Finishes 0 As:\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.07982756,0.13165659,0.13290727,0.31741026],\"confidence\":0.8671875,\"label\":5,\"ocr\":\" Ranked Classic Mude (TPP) Squad- Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.0044608936,0.04141283,0.15133315,0.4058934],\"confidence\":0.8671875,\"label\":4,\"ocr\":\" #43-99 BETTER LUCK NEXT TIME! Ranked Claste Made (0PP, Squd Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72522885,0.35659277,0.806485,0.42501843],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]SexDViNeWNL[.]Finishes Assi\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72519124,0.5216223,0.80491054,0.5913671],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]JuniorfTR[.]Finishes O Ass\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72797453,0.68248,0.803437,0.74602675],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]Pratiksha[.]Finishes 3 Ass\",\"resolution\":{\"first\":1650,\"second\":720}}]}]",
            type
        )
        MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686913714495,\"fileName\":\"1686913714495.jpg\",\"labels\":[{\"box\":[0.016655456,0.04383702,0.13571306,0.17234474],\"confidence\":0.9609375,\"label\":7,\"ocr\":\" #43 99 BETT Ranked Classic- Mod\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.73071486,0.19338572,0.807447,0.26051718],\"confidence\":0.46484375,\"label\":8,\"ocr\":\"[.]inishes 0 As\\u003d\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.08082203,0.13326862,0.1319128,0.31902227],\"confidence\":0.8671875,\"label\":5,\"ocr\":\" Ranked Classic Made (TPP)- Squad- Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.0072126016,0.03789945,0.14858145,0.40940678],\"confidence\":0.8828125,\"label\":4,\"ocr\":\" #A3\\u002799 BETTER LUCK NEXT TIME! Ranked Classic Mode (TPP)-Squad- Erangel\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72519124,0.35643387,0.80491054,0.42356533],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]SexDViNeWNL[.]nishes I Assis\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72593737,0.52240086,0.80285466,0.5909916],\"confidence\":0.76171875,\"label\":8,\"ocr\":\"[.]JuniorfTR[.]inishes D Assis\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.72934306,0.68313485,0.8033782,0.74668163],\"confidence\":0.734375,\"label\":8,\"ocr\":\"[.]Pratiksha[.]inishes 3 Assis\",\"resolution\":{\"first\":1650,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(4)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)
        score3 = gson.fromJson(scoring[2].toString(), JsonObject::class.java)
        score4 = gson.fromJson(scoring[3].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(0)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(3)
        Truth.assertThat(score3["kills"].asInt).isEqualTo(1)
        Truth.assertThat(score4["kills"].asInt).isEqualTo(0)

        MachineConstants.machineInputValidator.clear()
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686989518788,\"fileName\":\"1686989518788.jpg\",\"labels\":[{\"box\":[0.006603852,0.031341463,0.1411301,0.1490359],\"confidence\":0.94921875,\"label\":7,\"ocr\":\" #2-100 BET lanked Llessic M\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.72202754,0.1798562,0.8048502,0.255137],\"confidence\":0.5703125,\"label\":8,\"ocr\":\"[.]AARS H[.]Ies 4 Assists\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.080632284,0.11528261,0.12887852,0.29069424],\"confidence\":0.8515625,\"label\":5,\"ocr\":\" Ranked Classic Mad:(FeP) Squad- Erange\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.006580174,0.031286284,0.14007707,0.39576387],\"confidence\":0.8515625,\"label\":4,\"ocr\":\" #2Y100 BETTER LUCK NEXT TIME! anked Lbsst Madte(7epy Squad- Erangel\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7276696,0.34772196,0.8104923,0.4274412],\"confidence\":0.78515625,\"label\":8,\"ocr\":\"[.]SIDDHESHop2003[.]Ies 0 Assists 0\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7291692,0.50988305,0.8073806,0.58097255],\"confidence\":0.46484375,\"label\":8,\"ocr\":\"[.]DAKL 22[.]Finishes 3 A\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.72923267,0.6835183,0.8104882,0.76024973],\"confidence\":0.328125,\"label\":8,\"ocr\":\"[.]Parveer[.]Finishes 1 Assi\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(4)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)
        score3 = gson.fromJson(scoring[2].toString(), JsonObject::class.java)
        score4 = gson.fromJson(scoring[3].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(1)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(0)
        Truth.assertThat(score3["kills"].asInt).isEqualTo(4)
        Truth.assertThat(score4["kills"].asInt).isEqualTo(3)


 MachineConstants.machineInputValidator.clear()
        tfList = gson.fromJson(
            "[{\"epochTimestamp\":1686986587792,\"fileName\":\"1686986587792.jpg\",\"labels\":[{\"box\":[0.01048439,0.035421893,0.13996986,0.15311633],\"confidence\":0.94140625,\"label\":7,\"ocr\":\" #2496 BET Ranked ElassicM\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.08097322,0.12165184,0.13014959,0.3004452],\"confidence\":0.8671875,\"label\":5,\"ocr\":\" Ranked\\nElassic Mnde (PP) Squad- Erangel\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7290225,0.2721245,0.8043033,0.3488566],\"confidence\":0.5703125,\"label\":8,\"ocr\":\"[.]AXOM\\u003d LUCIFER[.]Finishes D0 Ass\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.0082491785,0.0386941,0.14432085,0.37637174],\"confidence\":0.8671875,\"label\":4,\"ocr\":\" #24 96 BETTER LUCK NEXT TIME! Ranked Dlasst Mhade(P) Squad- Erangel\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.72203517,0.43674615,0.80645454,0.5195688],\"confidence\":0.5703125,\"label\":8,\"ocr\":\"[.]BAJRANGBÁLI[.]| Finishes D Ass\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.7228335,0.60185415,0.8056562,0.6862735],\"confidence\":0.703125,\"label\":8,\"ocr\":\"[.]SIDOHESHop2003[.]-Finishes0 Ass\",\"resolution\":{\"first\":1600,\"second\":720}}]}]",
            type
        )
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(tfList)
        scoring = MachineConstants.machineInputValidator.getSquadScoringArray(result.squadScoring!!)
        Truth.assertThat(scoring.size).isEqualTo(3)
        score1 = gson.fromJson(scoring[0].toString(), JsonObject::class.java)
        score2 = gson.fromJson(scoring[1].toString(), JsonObject::class.java)
        score3 = gson.fromJson(scoring[2].toString(), JsonObject::class.java)

        Truth.assertThat(score1["kills"].asInt).isEqualTo(0)
        Truth.assertThat(score2["kills"].asInt).isEqualTo(0)
        Truth.assertThat(score3["kills"].asInt).isEqualTo(0)


    }


}
