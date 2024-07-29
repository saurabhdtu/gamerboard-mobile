package com.gamerboard.live.gamestatemachine.bgmi

import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.models.db.Game
import org.junit.Before
import org.junit.Test
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RestoreMachineKtTest{
    @Before
    fun setUp(){

    }


    @Test
    fun testGetPointsForRank(){
        val rank1 = "4"
        val pts1 = LabelUtils.getPointsForRank(rank1)
        assertThat(pts1).isEqualTo(8)

        val rank2 = "10"
        val pts2 = LabelUtils.getPointsForRank(rank2)
        assertThat(pts2).isEqualTo(1)
    }

    @Test
    fun testDecodeGameFormMap(){
        val gameOrig1: Game = gameBuilder(kills = "2323", rank = "2343")
        val gameFetch1  = LabelUtils.decodeGameFromMap(mapOf("current_game" to Json.encodeToString(gameOrig1)))
        assertThat(gameFetch1).isNotNull()
        gameFetch1?.let {
            assertThat(gameOrig1.kills).isEqualTo(gameFetch1.kills)
            assertThat(gameFetch1.rank).isEqualTo("2343")
        }
    }

    @Test
    fun testCalculateGamePointsFromGame(){
        val gameOrig1: Game = gameBuilder(kills = "2", rank = "9")
        val (rankPt, killsPt, totalPt, rank, kills) = LabelUtils.calculateGamePointsFromGame(gameOrig1)
        assertThat(rank).isEqualTo(gameOrig1.rank)
        assertThat(kills).isEqualTo(gameOrig1.kills)
        assertThat(rankPt).isEqualTo("1")
        assertThat(killsPt).isEqualTo("2")
        assertThat(totalPt).isEqualTo("3")
    }

}