package com.gamerboard.live.gamestatemachine.bgmi.integrationTests

import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.models.db.Game
import com.google.common.truth.Truth
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class RemoteResultTest {

    private var inputs: ArrayList<Game>? = null

    @Before
    fun setup() {
        try {
            val file = File(ClassLoader.getSystemResource("result.json").toURI())
            inputs = Json.decodeFromString<ArrayList<Game>>(file.readText())
        } catch (e: Exception) {
            println(e.stackTrace)
        }
        debugMachine = DEBUGGER.DIRECT_HANDLE
    }

    @Test
    fun test1FileLoaded() {
        Truth.assertThat(inputs).isNotEqualTo(null)
    }

    @Test
    fun testShowResults() {

        if(inputs!=null){
            LabelUtils.testLogRed("Recorded Games")
            for(game in inputs!!){
                LabelUtils.testLogGreen("Game: ID: ${game.id},  Rank: ${game.rank},  Kill: ${game.kills},   startTimeStamp: ${Date(game.startTimeStamp!!.toLong())}  Game: ${game.gameInfo} \n\n")
            }
        }else{
            LabelUtils.testLogRed("No Recorded Games")
        }
    }

}