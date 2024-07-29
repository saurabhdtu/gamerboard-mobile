package com.gamerboard.live.gamestatemachine.ff.processor

import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.ff.stateMachine.*
import com.gamerboard.live.gamestatemachine.games.GameConstant
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.games.freefire.FreeFireConstants
import com.gamerboard.live.gamestatemachine.games.freefire.FreeFireInputValidator
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.repository.ModelParamConst
import com.gamerboard.live.type.ESports
import com.google.common.reflect.TypeToken
import com.google.common.truth.Truth
import com.google.gson.Gson
import org.junit.Before
import org.junit.Test
import java.util.ArrayList

class MachineInputValidatorTest {
    @Before
    fun setup() {
        debugMachine = DEBUGGER.DIRECT_HANDLE
        val modelData = ModelParamConst(ESports.FREEFIREMAX).modelParamValues()
        MachineConstants.gameConstants = modelData?.let { GameConstant(modelParam = it) }!!
        MachineConstants.loadConstants(SupportedGames.FREEFIRE.packageName)/*Machine.stateMachine.transition(GameEvent.OnResetState)*/

    }

    @Test
    fun testValidateProfileId(){
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
        USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(FreeFireConstants.GameLabels.PROFILE.ordinal to "'Lv. 63 [.] OSM LORD [.] English [.] (83 [.] 8227 [.] UID: 905564452'")
        )

        val result1 = MachineConstants.machineInputValidator.validateProfileId(arrayListOf(data1, data1))
        Truth.assertThat(result1.accept).isFalse()
//        Truth.assertThat(result1.id).isEqualTo("905564452")
    }

    @Test
    fun testValidateCharId(){
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_PROFILE_and_ID_RAW_JSON.obj(),
            mapOf(FreeFireConstants.GameLabels.PROFILE.ordinal to "'Lv. 63 [.] OSM LORD [.] English [.] (83 [.] 8227 [.] UID: 905564452'")
        )

        val result1 = MachineConstants.machineInputValidator.validateCharacterId(arrayListOf(data1, data1), "905564452")
        Truth.assertThat(result1.accept).isFalse()
//        Truth.assertThat(result1.charId).isEqualTo("OSM LORD")
    }
    @Test
    fun testValidateStart(){
        val data1  = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            START_JSON.obj(),
            mapOf(FreeFireConstants.GameLabels.START.ordinal to "start")
        )
        val result1 = MachineConstants.machineInputValidator.validateStart(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()


        val data2  = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            START_JSON.obj(),
            mapOf(FreeFireConstants.GameLabels.START.ordinal to "matching")
        )
        val result2 = MachineConstants.machineInputValidator.validateStart(arrayListOf(data2))
        Truth.assertThat(result2.accept).isTrue()
    }

    @Test
    fun testValidateLogin(){
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(LOGIN_RAW_JSON.obj(),
        mapOf(FreeFireConstants.GameLabels.LOGIN.ordinal to "Guest Sign in with Facebook or More,")
        )

        val result1 = MachineConstants.machineInputValidator.validateLogin(arrayListOf(data1))
        Truth.assertThat(result1.accept).isTrue()

        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(LOGIN_RAW_JSON.obj(),
            mapOf(FreeFireConstants.GameLabels.LOGIN.ordinal to "Guest Signwith Facebook or More,")
        )

        val result2 = MachineConstants.machineInputValidator.validateLogin(arrayListOf(data2))
        Truth.assertThat(result2.accept).isTrue()

        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(LOGIN_RAW_JSON.obj(),
            mapOf(FreeFireConstants.GameLabels.LOGIN.ordinal to "Guest    SignFacebook or More,")
        )

        val result3 = MachineConstants.machineInputValidator.validateLogin(arrayListOf(data3))
        Truth.assertThat(result3.accept).isTrue()
    }

    @Test
    fun test_validate_rating() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(FreeFireConstants.GameLabels.BR_RATING.ordinal to "Rank Result[.]GOLD II")
        )
        val result1 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data1, data1))
        Truth.assertThat(result1.accept).isTrue()
        Truth.assertThat(result1.initialTier).isEqualTo("Gold II")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data2 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(FreeFireConstants.GameLabels.BR_RATING.ordinal to "Rank Resut[.]old IV[.]")
        )
        val result2 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data2, data2))
        Truth.assertThat(result2.accept).isTrue()
        Truth.assertThat(result2.initialTier).isEqualTo("Gold IV")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data3 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(FreeFireConstants.GameLabels.BR_RATING.ordinal to "Rank Resu[.]gol III[.]")
        )
        val result3 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data3, data3))
        Truth.assertThat(result3.accept).isTrue()
        Truth.assertThat(result3.initialTier).isEqualTo("Gold III")


        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data7 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            RESULT_RANK_RATING_JSON.obj(),
            mapOf(FreeFireConstants.GameLabels.BR_RATING.ordinal to "Rank Result[.]Bronze V VII[.]")
        )
        val result7 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data7, data7))
        Truth.assertThat(result7.accept).isTrue()
        Truth.assertThat(result7.initialTier).isEqualTo("Bronze VII")
        val gson = Gson()
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        val data8 = gson.fromJson<ArrayList<ImageResultJsonFlat>>("[{\"epochTimestamp\":1684403931504,\"fileName\":\"1684403931504.jpg\",\"labels\":[{\"box\":[0.09681025,0.36871308,0.73115873,0.6337674],\"confidence\":0.94921875,\"label\":12,\"ocr\":\"[.]HEROIC\",\"resolution\":{\"first\":1600,\"second\":720}}]}]", type)
        val result8 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(data8)
        Truth.assertThat(result8.accept).isTrue()
        Truth.assertThat(result8.initialTier).isEqualTo("Heroic")
    }

    @Test
    fun testForKillsReadOnlyOnceFromALabel() {
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        val data13 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(

                // here first we read 1, it should not get updated via 를

                FreeFireConstants.GameLabels.BR_KILL.ordinal to "[.]epickparo0[.]ELIMINATIONS[.]1[.]DAMAGE[.]346",
                FreeFireConstants.GameLabels.BR_GAME_INFO.ordinal to "BR-RANKED BERMUDA",
                FreeFireConstants.GameLabels.BR_RANK.ordinal to "[.]* # 9 / 50"
            )
        )
        val result13 = MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data13))
        Truth.assertThat(result13.accept).isTrue()
        Truth.assertThat(result13.rank).isEqualTo("9")
        Truth.assertThat(result13.squadScoring).isEqualTo("[{\"username\":\"epickparo0\",\"kills\":1}]")
    }

    @Test
    fun testListOfKillsAgainstOutput() {
        val inputRanks = arrayListOf<Pair<String, Any?>>(
            "[.]epickparo[.]ELIMINATIONS[.]DAMAGE[.]3[.]0[.]" to "0",
            "[.]epickparo[.]ELIMINATIONS[.]4[.]DAMAGE[.]107[.]" to "4",
            "[.]epickparo[.]ELIMINATIONS[.]7[.]DAMAGE[.]120[.]를" to "7",
            "[.]epickparo[.]ELIMINATIONS[.]10[.]DAMAGE[.]102[.]를" to "10",
        )

        for (input in inputRanks) {
            MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
            val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
                USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(),
                mapOf(FreeFireConstants.GameLabels.BR_KILL.ordinal to "${input.first}[.]")
            )
            val result1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data1))
            if (result1.kill != input.second)
                LabelUtils.testLogGreen("---> ${input.first} to ${input.second}")
            Truth.assertThat(result1.squadScoring).isEqualTo("[{\"username\":\"epickparo\",\"kills\":${input.second}}]")
        }
    }


    @Test
    fun testListOfRankAgainstOutput() {
        val inputRanks = arrayListOf<Pair<String, Any?>>(
            "#52/98" to "52",
            "#6/52" to "6",
            "#35/52" to "35",
            " #35/52" to "35",
            "#26/52" to "26",
            " #26/52" to "26",
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
            "#7\\A00" to "7",
            "#/1/I00" to null,
            "#71/100" to "71",
            "#71/A00" to "71",
            " #83 /100" to "83",
            "#1/52" to "1",
            "#|/52" to "1",
            " #1/52" to "1",
            " #||/52" to "11",
            "#I00/100" to "100",
            " #/G4" to null,
            " #0/64" to "0",
            " #O/E." to "0",
            " #U/64" to "0",
            " #30/52" to "30",
            " #3U/52" to "30",
            " #30\\/52" to "30",
            "#3U\\/52" to "30",
            "This is a great place" to null,
            "#100 /100" to "100",
            "# 100 /100" to "100",
            "#100 /100" to "100",
            "#100 /100" to "100"
        )

//        for ((i, input) in inputRanks.withIndex()) {
//            MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
//            val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
//                USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON.obj(),
//                mapOf(FreeFireConstants.GameLabels.CLASSIC_RATING.ordinal to "[.]${input.first}[.]IV[.]Gold V")
//            )
//            val result1 = MachineConstants.machineInputValidator.validateRankRatingGameInfo(arrayListOf(data1))
//            Truth.assertThat(result1.accept).isTrue()
//
//            if (result1.rank != input.second)
//                LabelUtils.testLogGreen("---> ${input.first} to ${input.second}")
//            Truth.assertThat(result1.rank).isEqualTo(input.second)
//        }

        for ((i, input) in inputRanks.withIndex()) {
            MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
            val data1 = MachineConstants.machineLabelUtils.setOcrAgainstLabel(
                USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj(), mapOf(
                    FreeFireConstants.GameLabels.BR_RANK.ordinal to "[.]${input.first}[.]"
                )
            )
            val result1 = MachineConstants.machineInputValidator.validateRankKillGameInfo(arrayListOf(data1))
            if (result1.rank != input.second)
                LabelUtils.testLogGreen("---> ${input.first} to ${input.second}")
            Truth.assertThat(result1.rank).isEqualTo(input.second)
        }
    }

    @Test
    fun testPerformanceValidatorFromInput() {
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        val list: ArrayList<ImageResultJsonFlat> = Gson().fromJson(
            "[{\"epochTimestamp\":1676007685323,\"fileName\":\"1676007685323.jpg\",\"labels\":[{\"box\":[0.0040800534,0.06754581,0.12342702,0.20391661],\"confidence\":0.93359375,\"label\":13,\"ocr\":\"BR-RANKED\\nBERMUDA\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.037804917,0.40109885,0.19575408,0.5908321],\"confidence\":0.953125,\"label\":8,\"ocr\":\"#10/50\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.3450483,0.085109234,0.4928102,0.8900283],\"confidence\":0.69921875,\"label\":11,\"ocr\":\"[[.]NAME, [.]CHARANSSVT[.]GANGSTER'A][..][-1, 1]\",\"resolution\":{\"first\":1520,\"second\":720}}]}]",
            type
        )
        val result2 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            list
        )

        Truth.assertThat(result2.kill).isEqualTo("1")

        val list2: ArrayList<ImageResultJsonFlat> = Gson().fromJson(
            "[{\"epochTimestamp\":1676007685323,\"fileName\":\"1676007685323.jpg\",\"labels\":[{\"box\":[0.0040800534,0.06754581,0.12342702,0.20391661],\"confidence\":0.93359375,\"label\":13,\"ocr\":\"BR-RANKED\\nBERMUDA\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.037804917,0.40109885,0.19575408,0.5908321],\"confidence\":0.953125,\"label\":8,\"ocr\":\"#10/50\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.3450483,0.085109234,0.4928102,0.8900283],\"confidence\":0.69921875,\"label\":11,\"ocr\":\"\",\"resolution\":{\"first\":1520,\"second\":720}}]}]",
            type
        )
        val result3 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            list2
        )

        Truth.assertThat(result3.kill).isNull()


        val list3: ArrayList<ImageResultJsonFlat> = Gson().fromJson(
            "[{\"epochTimestamp\":1676007685323,\"fileName\":\"1676007685323.jpg\",\"labels\":[{\"box\":[0.0040800534,0.06754581,0.12342702,0.20391661],\"confidence\":0.93359375,\"label\":13,\"ocr\":\"BR-RANKED\\nBERMUDA\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.037804917,0.40109885,0.19575408,0.5908321],\"confidence\":0.953125,\"label\":8,\"ocr\":\"#10/50\",\"resolution\":{\"first\":1520,\"second\":720}},{\"box\":[0.3450483,0.085109234,0.4928102,0.8900283],\"confidence\":0.69921875,\"label\":11,\"ocr\":\"[.]\",\"resolution\":{\"first\":1520,\"second\":720}}]}]",
            type
        )
        val result4 = MachineConstants.machineInputValidator.validateRankKillGameInfo(
            list3
        )

        Truth.assertThat(result4.kill).isNull()
    }


    @Test
    fun testupdateKillResultFromSquadScoringForSolo() {

        val result2 =  (MachineConstants.machineInputValidator as FreeFireInputValidator).updateKillResultFromSquadScoringForSolo(
            "[{\"username\":\"ishanmalik_\",\"kills\":1}]"
        )

        val result3 =  (MachineConstants.machineInputValidator as FreeFireInputValidator).updateKillResultFromSquadScoringForSolo(
            "[{\"username\":\"INSNEXBUDDY\",\"kills\":2}]"
        )
        Truth.assertThat(result2).isEqualTo("1")
        Truth.assertThat(result3).isEqualTo("2")


    }

    @Test
    fun testValidateRankKillGameInfo(){
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        var imageResultJson = gson.fromJson<ArrayList<ImageResultJsonFlat>>("[{\"epochTimestamp\":1684843467991,\"fileName\":\"1684843467991.jpg\",\"labels\":[{\"box\":[0.015703421,0.088449776,0.11712003,0.19365624],\"confidence\":0.9140625,\"label\":13,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6312151,0.42907885,0.74398243,0.5696136],\"confidence\":0.9375,\"label\":5,\"ocr\":\"[.]ELIMINATIONS[.]DAMAGE[.]4[.]930\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.042892143,0.38922325,0.18868588,0.5883696],\"confidence\":0.9140625,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}}]}]", type)
        var result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.accept).isTrue()
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        imageResultJson = gson.fromJson<ArrayList<ImageResultJsonFlat>>("[{\"epochTimestamp\":1680796542600,\"fileName\":\"1680796542600.jpg\",\"labels\":[{\"box\":[0.0040550865,0.0961686,0.121429294,0.22374383],\"confidence\":0.92578125,\"label\":11,\"ocr\":\"BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.045526884,0.39506894,0.1908463,0.5912337],\"confidence\":0.91796875,\"label\":13,\"ocr\":\"#38/50\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.34266716,0.09734613,0.4980055,0.8889601],\"confidence\":0.79296875,\"label\":9,\"ocr\":\"\",\"resolution\":{\"first\":1650,\"second\":720}}]}," +
                "{\"epochTimestamp\":1680796542939,\"fileName\":\"1680796542939.jpg\",\"labels\":[{\"box\":[0.0040550865,0.09515726,0.121429294,0.22273248],\"confidence\":0.92578125,\"label\":11,\"ocr\":\"BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.046727926,0.39366186,0.18964525,0.5898266],\"confidence\":0.91796875,\"label\":13,\"ocr\":\"#38/50\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.3454814,0.1103231,0.5008197,0.8759831],\"confidence\":0.79296875,\"label\":9,\"ocr\":\"\",\"resolution\":{\"first\":1650,\"second\":720}}]}," +
                "{\"epochTimestamp\":0,\"fileName\":\"rank_kills_screen_1680796532620.jpg\",\"labels\":[{\"box\":[0.017194632,0.09970607,0.113655746,0.20631282],\"confidence\":0.58984375,\"label\":11,\"ocr\":\"[.]NEXTERRA [.]BR - RANKED\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.6247337,0.4249906,0.747347,0.5650936],\"confidence\":0.62109375,\"label\":12,\"ocr\":\"[.]Dark1C4ON ELIMINATIONS[.]DAMAGE[.]288\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.059779346,0.38330168,0.19347888,0.5861159],\"confidence\":0.67578125,\"label\":13,\"ocr\":\"[.]# 38750\",\"resolution\":{\"first\":1650,\"second\":720}}]},"+
                "{\"epochTimestamp\":0,\"fileName\":\"performance_screen_1680796541899.jpg\",\"labels\":[{\"box\":[0.0040550865,0.09509649,0.121429294,0.22481593],\"confidence\":0.91796875,\"label\":11,\"ocr\":\"[.]BR - RANKED NEXTERRA\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.04571274,0.39506894,0.1934746,0.5912337],\"confidence\":0.91015625,\"label\":13,\"ocr\":\"[.]# 38 / 50\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.3440743,0.10388869,0.4994126,0.88241756],\"confidence\":0.79296875,\"label\":9,\"ocr\":\"\",\"resolution\":{\"first\":1650,\"second\":720}}]}]", type)
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.gameInfo?.type).isEqualTo("BR RANKED")
        Truth.assertThat(result.rank).isEqualTo("38")
        Truth.assertThat(result.teamRank).isEqualTo("38")
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        imageResultJson = gson.fromJson("[{\"epochTimestamp\":1681538232364,\"fileName\":\"1681538232364.jpg\",\"labels\":[{\"box\":[0.01527651,0.06585983,0.11838804,0.16559073],\"confidence\":0.828125,\"label\":11,\"ocr\":\"BR-RANKED\\nBERMUDA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.62296724,0.40718216,0.7562368,0.54728514],\"confidence\":0.76953125,\"label\":12,\"ocr\":\"[.]Reed5jOU1N[.]ELIMINATIONS[.]DAMAGE[.]0\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.06259352,0.36403117,0.19629306,0.57724464],\"confidence\":0.5,\"label\":13,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}}]}]", type)
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.gameInfo?.group).isEqualTo(StateMachineStringConstants.UNKNOWN)
        Truth.assertThat(result.gameInfo?.type).isEqualTo("BR RANKED")
        Truth.assertThat(result.gameInfo?.mode).isEqualTo("BERMUDA")
        Truth.assertThat(result.squadScoring).isEqualTo("[{\"username\":\"Reed5jOU1N\",\"kills\":0}]")
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        imageResultJson = gson.fromJson("[{\"epochTimestamp\":1681538232364,\"fileName\":\"1681538232364.jpg\",\"labels\":[{\"box\":[0.01527651,0.06585983,0.11838804,0.16559073],\"confidence\":0.828125,\"label\":11,\"ocr\":\"BR-RANKED\\nBERMUDA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.62296724,0.40718216,0.7562368,0.54728514],\"confidence\":0.76953125,\"label\":12,\"ocr\":\"[.]Reed5jOU1N[.]ELIMINATIONS[.]DAMAGE[.]0\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.06259352,0.36403117,0.19629306,0.57724464],\"confidence\":0.5,\"label\":13,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}}]}]", type)
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.gameInfo?.group).isEqualTo(StateMachineStringConstants.UNKNOWN)
        Truth.assertThat(result.gameInfo?.type).isEqualTo("BR RANKED")
        Truth.assertThat(result.gameInfo?.mode).isEqualTo("BERMUDA")
        Truth.assertThat(result.squadScoring).isEqualTo("[{\"username\":\"Reed5jOU1N\",\"kills\":0}]")
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        imageResultJson = gson.fromJson("[{\"epochTimestamp\":1681538234477,\"fileName\":\"1681538234477.jpg\",\"labels\":[{\"box\":[0.0030437447,0.06587806,0.12041795,0.1813121],\"confidence\":0.93359375,\"label\":11,\"ocr\":\"BR-RANKED\\nBERMUDA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.04320836,0.37136263,0.19597898,0.56428486],\"confidence\":0.92578125,\"label\":13,\"ocr\":\"#10/49\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.34747058,0.06942424,0.49038792,0.8610382],\"confidence\":0.75,\"label\":9,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}}]}]", type)
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.gameInfo?.group).isEqualTo("solo")
        Truth.assertThat(result.gameInfo?.type).isEqualTo("BR RANKED")
        Truth.assertThat(result.gameInfo?.mode).isEqualTo("BERMUDA")
        Truth.assertThat(result.rank).isEqualTo("10")
        Truth.assertThat(result.teamRank).isEqualTo(null)
        Truth.assertThat(result.squadScoring).isEqualTo(null)
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        imageResultJson = gson.fromJson("[{\"epochTimestamp\":1684843467991,\"fileName\":\"1684843467991.jpg\",\"labels\":[{\"box\":[0.015703421,0.088449776,0.11712003,0.19365624],\"confidence\":0.9140625,\"label\":13,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6312151,0.42907885,0.74398243,0.5696136],\"confidence\":0.9375,\"label\":5,\"ocr\":\"[.]ELIMINATIONS[.]DAMAGE[.]4[.]930\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.042892143,0.38922325,0.18868588,0.5883696],\"confidence\":0.9140625,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}}]}]", type)
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.accept).isTrue()

    }

    @Test
    fun testValidateRankKillGameInfoWhenKilIsNotDetected(){
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ImageResultJsonFlat>>() {}.type
        var imageResultJson = gson.fromJson<ArrayList<ImageResultJsonFlat>>("[{\"epochTimestamp\":1685174169511,\"fileName\":\"1685174169511.jpg\",\"labels\":[{\"box\":[0.015703421,0.08708304,0.11712003,0.1903772],\"confidence\":0.9140625,\"label\":13,\"ocr\":\"BR-RANKED\\nALAHARI\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6291081,0.4286791,0.74608946,0.5744655],\"confidence\":0.8359375,\"label\":5,\"ocr\":\"[.]NOISYBOYI018[.]ELIMINATIONS[.]39[.]DAMAGE\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.03999401,0.39258176,0.18848684,0.5881083],\"confidence\":0.8984375,\"label\":8,\"ocr\":\"9744\",\"resolution\":{\"first\":1600,\"second\":720}}]}]", type)
        var result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.accept).isTrue()
        Truth.assertThat(result.squadScoring).doesNotContain("0")
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        imageResultJson = gson.fromJson("[{\"epochTimestamp\":1685174169863,\"fileName\":\"1685174169863.jpg\",\"labels\":[{\"box\":[0.016755804,0.088021815,0.114519075,0.18943843],\"confidence\":0.9140625,\"label\":13,\"ocr\":\"BR-RANKED\\nALAHARI\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.63017124,0.43045297,0.7450263,0.57358944],\"confidence\":0.859375,\"label\":5,\"ocr\":\"[.]NOISYBOYI018[.]ELIMINATIONS[.]0[.]DAMAGE[.]39\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.041343562,0.39232042,0.1871373,0.5914668],\"confidence\":0.9140625,\"label\":8,\"ocr\":\"3T44\",\"resolution\":{\"first\":1600,\"second\":720}}]}]", type)
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.accept).isTrue()
        Truth.assertThat(result.squadScoring).contains("0")
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        imageResultJson = gson.fromJson("[{\"epochTimestamp\":1685099368620,\"fileName\":\"1685099368620.jpg\",\"labels\":[{\"box\":[0.016968083,0.09714885,0.111209616,0.20044301],\"confidence\":0.8984375,\"label\":13,\"ocr\":\"BR-RANKED\\nKALAHARI\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.63025135,0.4316101,0.7493984,0.5670823],\"confidence\":0.859375,\"label\":5,\"ocr\":\"[.]Dark1C4cON[.]ELIMINATIONS[.]7[.]DAMAGE[.]1400\",\"resolution\":{\"first\":1650,\"second\":720}},{\"box\":[0.042334042,0.39882067,0.1847918,0.57964337],\"confidence\":0.8984375,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1650,\"second\":720}}]}]", type)
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.accept).isTrue()
        Truth.assertThat(result.squadScoring).contains("7")
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        imageResultJson = gson.fromJson("[{\"epochTimestamp\":1685174170211,\"fileName\":\"1685174170211.jpg\",\"labels\":[{\"box\":[0.014929134,0.08816923,0.11634575,0.18774243],\"confidence\":0.9140625,\"label\":13,\"ocr\":\"BR-RANKED\\n1ALAHARI\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.6299596,0.43156666,0.7442701,0.5740244],\"confidence\":0.859375,\"label\":5,\"ocr\":\"[.]NOISYBOY1018[.]ELIMINATIONS[.]0[.]39[.]DAMAGE\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.03999401,0.39103317,0.18848684,0.5865597],\"confidence\":0.8984375,\"label\":8,\"ocr\":\"\",\"resolution\":{\"first\":1600,\"second\":720}}]}]", type)
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.accept).isTrue()
        Truth.assertThat(result.squadScoring).contains("0")
        MachineConstants.machineLabelProcessor.clearValidatorScreenCache()
        imageResultJson = gson.fromJson("[{\"epochTimestamp\":1694080797632,\"fileName\":\"1694080797632.jpg\",\"labels\":[{\"box\":[0.017324261,0.097239465,0.115809955,0.19273585],\"confidence\":0.59375,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.3739476,0.0,0.5142267,0.50903344],\"confidence\":0.734375,\"label\":6,\"ocr\":\"[..]\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.10039534,0.4040419,0.23027094,0.58080405],\"confidence\":0.94140625,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080797976,\"fileName\":\"1694080797976.jpg\",\"labels\":[{\"box\":[0.014994107,0.097239465,0.11814011,0.19273585],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.4040419,0.22697774,0.58080405],\"confidence\":0.94921875,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.3739475,0.13136467,0.51861775,0.7046875],\"confidence\":0.9765625,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080798318,\"fileName\":\"1694080798318.jpg\",\"labels\":[{\"box\":[0.014193136,0.097239465,0.11894108,0.19273585],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.40513963,0.22697774,0.5819018],\"confidence\":0.94140625,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.37505376,0.1270144,0.5175115,0.7003372],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"[None, [.]TGX-6AMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080799011,\"fileName\":\"1694080799011.jpg\",\"labels\":[{\"box\":[0.015644617,0.098688744,0.11879062,0.19128656],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.40513963,0.22697774,0.5819018],\"confidence\":0.953125,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.37395602,0.13136467,0.51641375,0.7046875],\"confidence\":0.9765625,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080799354,\"fileName\":\"1694080799354.jpg\",\"labels\":[{\"box\":[0.014343597,0.09796968,0.1174896,0.19200563],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.4040419,0.22697774,0.58080405],\"confidence\":0.94140625,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.3739475,0.1270144,0.51861775,0.7003372],\"confidence\":0.96875,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080800035,\"fileName\":\"1694080800035.jpg\",\"labels\":[{\"box\":[0.014193136,0.097239465,0.11894108,0.19273585],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.40513963,0.22697774,0.5819018],\"confidence\":0.953125,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.37395602,0.13139838,0.51641375,0.6959532],\"confidence\":0.9765625,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080800378,\"fileName\":\"1694080800378.jpg\",\"labels\":[{\"box\":[0.013542626,0.09796968,0.11829057,0.19200563],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.40513963,0.22697774,0.5819018],\"confidence\":0.953125,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.37175202,0.13574865,0.5164223,0.70030344],\"confidence\":0.96875,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080801064,\"fileName\":\"1694080801064.jpg\",\"labels\":[{\"box\":[0.01272922,0.09796968,0.119103976,0.19200563],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.4040419,0.22697774,0.58080405],\"confidence\":0.94921875,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.3728583,0.1270144,0.515316,0.7003372],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080801749,\"fileName\":\"1694080801749.jpg\",\"labels\":[{\"box\":[0.014193136,0.09796968,0.11894108,0.19200563],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.40649128,0.22697774,0.58055013],\"confidence\":0.94921875,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.37284976,0.1270144,0.51752,0.7003372],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"[None, [.]A TGX-GAMING][..][None, None][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080802105,\"fileName\":\"1694080802105.jpg\",\"labels\":[{\"box\":[0.014193136,0.098688744,0.11894108,0.19128656],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.40513963,0.22697774,0.5819018],\"confidence\":0.953125,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.3728583,0.13139838,0.515316,0.6959532],\"confidence\":0.96875,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080802783,\"fileName\":\"1694080802783.jpg\",\"labels\":[{\"box\":[0.013542626,0.09796968,0.11829057,0.19200563],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.40513963,0.22697774,0.5819018],\"confidence\":0.94921875,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.37284976,0.1270144,0.51752,0.7003372],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080803473,\"fileName\":\"1694080803473.jpg\",\"labels\":[{\"box\":[0.013542626,0.09862019,0.11829057,0.19265614],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.4040419,0.22697774,0.58080405],\"confidence\":0.94921875,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.37284976,0.1270144,0.51752,0.7003372],\"confidence\":0.96875,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080804153,\"fileName\":\"1694080804153.jpg\",\"labels\":[{\"box\":[0.013542626,0.097239465,0.11829057,0.19273585],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.40649128,0.22697774,0.58055013],\"confidence\":0.94921875,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.3728583,0.13139838,0.515316,0.6959532],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"[None, [.]TGX-6AMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080804493,\"fileName\":\"1694080804493.jpg\",\"labels\":[{\"box\":[0.013542626,0.09796968,0.11829057,0.19200563],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.40649128,0.22697774,0.58055013],\"confidence\":0.953125,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.37284976,0.13139838,0.51752,0.6959532],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080804834,\"fileName\":\"1694080804834.jpg\",\"labels\":[{\"box\":[0.014994107,0.09796968,0.11814011,0.19200563],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.40649128,0.22697774,0.58055013],\"confidence\":0.953125,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.3739475,0.13139838,0.51861775,0.6959532],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]},{\"epochTimestamp\":1694080805527,\"fileName\":\"1694080805527.jpg\",\"labels\":[{\"box\":[0.013542626,0.09796968,0.11829057,0.19200563],\"confidence\":0.78125,\"label\":8,\"ocr\":\" BR-RANKED\\nNEXTERRA\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.097102135,0.4040419,0.22697774,0.58080405],\"confidence\":0.953125,\"label\":4,\"ocr\":\" #18/50\",\"resolution\":{\"first\":1600,\"second\":720}},{\"box\":[0.37395602,0.13574865,0.51641375,0.70030344],\"confidence\":0.97265625,\"label\":6,\"ocr\":\"[None, [.]TGX-GAMING][..][None, 2][..]false\",\"resolution\":{\"first\":1600,\"second\":720}}]}]", type)
        result = MachineConstants.machineInputValidator.validateRankKillGameInfo(imageResultJson, "","", true)
        Truth.assertThat(result.accept).isTrue()
        Truth.assertThat(result.squadScoring).contains("0")
    }


}