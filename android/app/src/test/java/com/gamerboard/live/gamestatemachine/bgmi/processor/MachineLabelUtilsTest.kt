package com.gamerboard.live.gamestatemachine.bgmi.processor


import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIConstants
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.gamestatemachine.games.LabelUtils.alphaNumericOnly
import com.gamerboard.live.gamestatemachine.games.LabelUtils.alphabetsOnly
import com.gamerboard.live.gamestatemachine.games.LabelUtils.alternateCount
import com.gamerboard.live.gamestatemachine.games.LabelUtils.correctRomans
import com.gamerboard.live.gamestatemachine.games.LabelUtils.digitsOnly
import com.gamerboard.live.gamestatemachine.games.LabelUtils.getFromMetaInfoJson
import com.gamerboard.live.gamestatemachine.games.LabelUtils.hasClearMajority
import com.gamerboard.live.gamestatemachine.games.LabelUtils.hasLabel
import com.gamerboard.live.gamestatemachine.games.LabelUtils.isSameAs
import com.gamerboard.live.gamestatemachine.games.LabelUtils.lastChars
import com.gamerboard.live.gamestatemachine.games.LabelUtils.putToMetaInfoJson
import com.gamerboard.live.gamestatemachine.games.LabelUtils.removeStartDigits
import com.gamerboard.live.gamestatemachine.games.LabelUtils.stitchDigits
import com.gamerboard.live.gamestatemachine.games.LabelUtils.trimSpace
import com.gamerboard.live.gamestatemachine.games.LabelUtils.updateGameInfo
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.models.db.GameInfo
import com.google.common.truth.Truth
import okhttp3.internal.immutableListOf
import org.junit.Before
import org.junit.Test
import kotlin.math.ceil

class MachineLabelUtilsTest {

    @Before
    fun setup() {
        debugMachine = DEBUGGER.DIRECT_HANDLE
    }


    @Test
    fun testHasLabel() {
        val a = (1.shl(2) or 1.shl(3))
        val b = (1.shl(2) or 1.shl(4))
        Truth.assertThat(a.hasLabel(1.shl(2))).isTrue()
        Truth.assertThat(b.hasLabel(1.shl(3))).isFalse()
    }

    @Test
    fun testSplitByChars() {
        val s = "123:AA # #  4%5"
        val result = LabelUtils.splitByChars(s, Regex(":|#+|%+|\\s+"))
        Truth.assertThat(result).isEqualTo(listOf("123", "AA", "", "", "", "", "4", "5"))
    }

    @Test
    fun testEditDistance() {
        val s1 = "Amazing"
        val s2 = "NotAmazzinggg"
        val dist = LabelUtils.editDistance(s1, s2)
        Truth.assertThat(dist).isEqualTo(6)
    }

    @Test
    fun testOCRTrimSpace() {
        var s = "A  B C    D  "
        s = s.trimSpace().lowercase()
        Truth.assertThat(s).isEqualTo("abcd")
    }

    @Test
    fun testDigitOnly() {
        var s = "Rank # 45"
        s = s.digitsOnly()
        Truth.assertThat(s).isEqualTo("45")
    }

    @Test
    fun testDigitOnlyWithCount() {
        var s = "3 players defeated"
        s = s.digitsOnly(2)
        Truth.assertThat(s).isEmpty()

        s = "p1ayers defe0ted"
        s = s.digitsOnly(2)
        Truth.assertThat(s).isEmpty()

        s = "3 players defeated 2"
        s = s.digitsOnly(2)
        Truth.assertThat(s).isEqualTo("2")

        s = "3 p1ayers defeat0d 10"
        s = s.digitsOnly(2)
        Truth.assertThat(s).isEqualTo("10")

        s = "P1ayer leve1 23"
        s = s.digitsOnly(2)
        Truth.assertThat(s).isEqualTo("23")

        s = "3 ank #23"
        s = s.digitsOnly(2)
        Truth.assertThat(s).isEqualTo("23")

        s = "ab88c"
        s = s.digitsOnly(2)
        Truth.assertThat(s).isEqualTo("88")

        s = "ab88c"
        s = s.digitsOnly(1)
        Truth.assertThat(s).isEqualTo("8")

        s = "ab88c"
        s = s.digitsOnly(0)
        Truth.assertThat(s).isEqualTo("")
    }

    @Test
    fun testAlphabetsOnly() {
        var s = "Alive 34"
        s = s.alphabetsOnly()
        Truth.assertThat(s).isEqualTo("Alive")
    }

    @Test
    fun testAlphaNumericOnly() {
        var s = "ID:3434345"
        s = s.alphaNumericOnly()
        Truth.assertThat(s).isEqualTo("ID3434345")
    }

    @Test
    fun testCorrectRomans() {
        val a = "lV".correctRomans()
        Truth.assertThat(a).isEqualTo("IV")

        val b = "1V".correctRomans()
        Truth.assertThat(b).isEqualTo("IV")

        val c = "lv".correctRomans()
        Truth.assertThat(c).isEqualTo("IV")

        val d = "iv".correctRomans()
        Truth.assertThat(d).isEqualTo("IV")

        val e = "ll1".correctRomans()
        Truth.assertThat(e).isEqualTo("III")

        val f = "Vl1".correctRomans()
        Truth.assertThat(f).isEqualTo("VII")
    }

    @Test
    fun testIsSubsetOf_true() {
        val cur =
            immutableListOf(BGMIConstants.GameLabels.RANK.ordinal,  BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal, BGMIConstants.GameLabels.GAME_INFO.ordinal)
        val prev =
            immutableListOf(BGMIConstants.GameLabels.RANK.ordinal, BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal, BGMIConstants.GameLabels.GAME_INFO.ordinal, BGMIConstants.GameLabels.CLASSIC_ALL_GAMEPLAY.ordinal
            )
        Truth.assertThat(cur.isSameAs(prev)).isTrue()
    }

    @Test
    fun testIsSubsetOf_false() {
        val cur =
            immutableListOf(BGMIConstants.GameLabels.RANK.ordinal, BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal, BGMIConstants.GameLabels.GAME_INFO.ordinal)
        val prev = immutableListOf(BGMIConstants.GameLabels.CLASSIC_ALL_GAMEPLAY.ordinal)
        Truth.assertThat(cur.isSameAs(prev)).isFalse()
    }

    @Test
    fun testLastChars() {
        var s = "123456"
        s = s.lastChars(3)
        Truth.assertThat(s.length).isEqualTo(3)
        Truth.assertThat(s).isEqualTo("456")
    }

    @Test
    fun testRemoveStartDigits() {
        var s = "4 players defeated"
        s = s.removeStartDigits()
        Truth.assertThat(s).isEqualTo("  players defeated")

        s = " 4 Players defeated 3"
        s = s.removeStartDigits()
        Truth.assertThat(s).isEqualTo("   Players defeated 3")

        s = "24 Rank #2"
        s = s.removeStartDigits()
        Truth.assertThat(s).isEqualTo("   Rank #2")

        s = "  3 players defeated"
        s = s.removeStartDigits()
        Truth.assertThat(s).isEqualTo("    players defeated")
    }

    @Test
    fun testStitchDigits() {

        var s = "kill 1 2"
        s = s.stitchDigits(1)
        Truth.assertThat(s).isEqualTo("kill12")

        s = "Rank #1 2"
        s = s.stitchDigits(1)
        Truth.assertThat(s).isEqualTo("Rank #12")

        s = "Rank #12"
        s = s.stitchDigits(1)
        Truth.assertThat(s).isEqualTo("Rank #12")

        s = "Rank #1   2"
        s = s.stitchDigits(1)
        Truth.assertThat(s).isEqualTo("Rank #1  2")

        s = "Rank #1   2"
        s = s.stitchDigits(2)
        Truth.assertThat(s).isEqualTo("Rank #1 2")

        s = "Rank #1   2"
        s = s.stitchDigits(3)
        Truth.assertThat(s).isEqualTo("Rank #12")

        s = "1 2"
        s = s.stitchDigits(1)
        Truth.assertThat(s).isEqualTo("12")

        s = ""
        s = s.stitchDigits(1)
        Truth.assertThat(s).isEqualTo("")
    }


    @Test
    fun testFindMostCommonValue() {
        val res1 = LabelUtils.findMostCommonInList(listOf("2", "2", "2", "2", "3", "3"))
        Truth.assertThat(res1).isEqualTo("2")

        val res2 = LabelUtils.findMostCommonInList(listOf("2", "3", "3", "3", "2", "3"))
        Truth.assertThat(res2).isEqualTo("3")

        val res3 = LabelUtils.findMostCommonInList(listOf("3", "3", "3", "2", "2", "2"))
        Truth.assertThat(res3).isEqualTo("3")
    }


    @Test
    fun testFindMostCommonValueWithConflict() {
        val res1 = LabelUtils.findMostCommonInListWithConflict(
            listOf(
                Pair("21", "2l"),
                Pair("2", "2"),
                Pair("2", "2"),
                Pair("21", "2l")
            )
        )
        Truth.assertThat(res1).isEqualTo("2")

        val res2 = LabelUtils.findMostCommonInListWithConflict(
            listOf(
                Pair("1", "1"),
                Pair("1", "1"),
                Pair("11", "1l"),
                Pair("11", "1l")
            )
        )
        Truth.assertThat(res2).isEqualTo("1")

        val res3 = LabelUtils.findMostCommonInListWithConflict(
            listOf(
                Pair("1", "I"),
                Pair("11", "II"),
                Pair("1", "I"),
                Pair("11", "II")
            )
        )
        Truth.assertThat(res3).isEqualTo("11")

        val res4 = LabelUtils.findMostCommonInListWithConflict(
            listOf(
                Pair("1", "I"),
                Pair("1", "I"),
                Pair("1", "I"),
                Pair("11", "II")
            )
        )
        Truth.assertThat(res4).isEqualTo("1")

        val res5 = LabelUtils.findMostCommonInListWithConflict(
            listOf(
                Pair("2", "2"),
                Pair("2", "2"),
                Pair("3", "3"),
                Pair("2", "2")
            )
        )
        Truth.assertThat(res5).isEqualTo("2")
    }

    @Test
    fun testTimeStampInUTC() {
        val timeStamp = LabelUtils.timeStampInUTC("11", "16", "14", "31")
        LabelUtils.testLogGreen(timeStamp)
    }

    @Test
    fun testGetAsBits() {
        val bits1 = LabelUtils.getAsBits(1, 1)
        val show1 = (bits1 and 1) == 1
        Truth.assertThat(bits1).isEqualTo(3)
        Truth.assertThat(show1).isTrue()

        val bits2 = LabelUtils.getAsBits(1, 0)
        val show2 = (bits2 and 1) == 1
        Truth.assertThat(bits2).isEqualTo(2)
        Truth.assertThat(show2).isFalse()

        val bits3 = LabelUtils.getAsBits(0, 1)
        Truth.assertThat(bits3).isEqualTo(1)
        val show3 = (bits3 and 1) == 1
        Truth.assertThat(show3).isTrue()

        val bits4 = LabelUtils.getAsBits(0, 0)
        Truth.assertThat(bits4).isEqualTo(0)
    }

    @Test
    fun testAddMetaINfo() {
        val data1 = StateMachineStringConstants.UNKNOWN.putToMetaInfoJson(
            key = "Auto ML correctRankRating",
            value = "True"
        )
        LabelUtils.testLogGreen(data1)
        Truth.assertThat(data1)
            .isEqualTo("[{\"first\":\"Auto ML correctRankRating\",\"second\":\"True\"}]")

        val data2 = data1.putToMetaInfoJson(key = "Auto ML correctRankKills", value = "True")
        LabelUtils.testLogGreen(data2)
        Truth.assertThat(data2)
            .isEqualTo("[{\"first\":\"Auto ML correctRankRating\",\"second\":\"True\"},{\"first\":\"Auto ML correctRankKills\",\"second\":\"True\"}]")
    }


    @Test
    fun testGetMetaINfo() {
        val data1 = StateMachineStringConstants.UNKNOWN.putToMetaInfoJson(
            key = "query_auto_ml",
            value = "True"
        )
        LabelUtils.testLogGreen(data1)
        Truth.assertThat(data1).isEqualTo("[{\"first\":\"query_auto_ml\",\"second\":\"True\"}]")
        val fetchData1 = data1.getFromMetaInfoJson("query_auto_ml")
        Truth.assertThat(fetchData1).isEqualTo("True")
    }

    @Test
    fun testHasClearMajority() {
        val list1 = arrayListOf("I", "I", "II", "I", "II", "I")
        Truth.assertThat("I".hasClearMajority(list1)).isTrue()
        Truth.assertThat("II".hasClearMajority(list1)).isFalse()

        /*val list2 = arrayListOf("I","I","I","I","II","I")
        assertThat("I".hasClearMajority(list2)).isTrue()
        assertThat("II".hasClearMajority(list2)).isFalse()

        val list3 = arrayListOf("I","I","II","II","II","I")
        assertThat("I".hasClearMajority(list3)).isTrue()
        assertThat("II".hasClearMajority(list3)).isTrue()*/
    }

    @Test
    fun testAlternateCount() {
        val list1 = arrayListOf("A", "A", "A", "A")
        Truth.assertThat(list1.alternateCount()).isEqualTo(0)

        val list2 = arrayListOf("A", "B", "A", "A")
        Truth.assertThat(list2.alternateCount()).isEqualTo(2)

        val list3 = arrayListOf("A", "B", "A", "A", "B", "A", "B")
        Truth.assertThat(list3.alternateCount()).isEqualTo(5)

        val list4 = arrayListOf("A", "B", "A", "A", "B", "A", "A")
        Truth.assertThat(list4.alternateCount()).isEqualTo(4)
    }

    @Test
    fun testUpdateGameInfo() {
        val result1 = updateGameInfo(null, GameInfo("classic", "TPP", "solo", "earth"))
        Truth.assertThat(result1)
            .isEqualTo("{\"type\":\"classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"earth\"}")

        val result2 = updateGameInfo(
            "{\"type\":\"classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"earth\"}",
            GameInfo(UNKNOWN, "TPP", UNKNOWN, UNKNOWN)
        )
        Truth.assertThat(result2)
            .isEqualTo("{\"type\":\"classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"earth\"}")

        val result3 = updateGameInfo(
            "{\"type\":\"classic\",\"view\":\"UNKNOWN\",\"group\":\"UNKNOWN\",\"mode\":\"earth\"}",
            GameInfo(UNKNOWN, "TPP", "solo", UNKNOWN)
        )
        Truth.assertThat(result3)
            .isEqualTo("{\"type\":\"classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"earth\"}")

        val result4 = updateGameInfo(
            "{\"type\":\"classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"earth\"}",
            null
        )
        Truth.assertThat(result4)
            .isEqualTo("{\"type\":\"classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"earth\"}")

        val result5 = updateGameInfo(UNKNOWN, GameInfo("classic", "TPP", "solo", "earth"))
        Truth.assertThat(result5)
            .isEqualTo("{\"type\":\"classic\",\"view\":\"TPP\",\"group\":\"solo\",\"mode\":\"earth\"}")
    }

    @Test
    fun testCeilFileSize0_5() {
        val size1 = ceil((3.1 / 0.5)) * 0.5
        Truth.assertThat(size1).isEqualTo(3.5)

        val size2 = ceil((3.5 / 0.5)) * 0.5
        Truth.assertThat(size2).isEqualTo(3.5)

        val size3 = ceil((3.7 / 0.5)) * 0.5
        Truth.assertThat(size3).isEqualTo(4.0)
    }

    @Test
    fun testCorrectNumberFromTwoValues() {
        val result1 = MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues(null, UNKNOWN)
        Truth.assertThat(result1).isEqualTo(UNKNOWN)
        val result2 = MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues(UNKNOWN, UNKNOWN)
        Truth.assertThat(result2).isEqualTo(UNKNOWN)
        val result3 = MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues(UNKNOWN, "3")
        Truth.assertThat(result3).isEqualTo("3")
        val result4 = MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues("3", UNKNOWN)
        Truth.assertThat(result4).isEqualTo("3")
        val result5 = MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues("13", "3")
        Truth.assertThat(result5).isEqualTo("13")
        val result6 = MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues("3", "13")
        Truth.assertThat(result6).isEqualTo("13")
        val result7 = MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues("13", "13")
        Truth.assertThat(result7).isEqualTo("13")
        val result8 = MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues("73", "13")
        Truth.assertThat(result8).isEqualTo("73")
        val result9 = MachineConstants.machineLabelUtils.getCorrectNumberFromTwoValues(null, null)
        Truth.assertThat(result9).isEqualTo(null)
    }
}
