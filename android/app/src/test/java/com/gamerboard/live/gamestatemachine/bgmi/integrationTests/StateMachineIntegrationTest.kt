package com.gamerboard.live.gamestatemachine.bgmi.integrationTests

import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.stateMachine.DEBUGGER
import com.gamerboard.live.gamestatemachine.stateMachine.debugMachine
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.models.ImageResultJson
import com.gamerboard.live.models.db.Game
import com.google.common.truth.Truth.assertThat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.io.File


@Serializable
data class MResult(
    val createdAt: String,
    @SerialName("deviceId")
    val deviceID: String,
    val data: List<ImageResultJson>,
    val sessionId: Long
)

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class StateMachineIntegrationTest {

    private var inputs: MResult? = null

    @Before
    fun setup() {
        try {
            val file = File(ClassLoader.getSystemResource("machine_input.json").toURI())
            MachineConstants.loadConstants(SupportedGames.BGMI.packageName)
            inputs = Json.decodeFromString<MResult>(file.readText())
        } catch (e: Exception) {
            println(e.stackTrace)
        }
    }

    @Test
    fun test1FileLoaded() {
        assertThat(inputs).isNotEqualTo(null)
    }

}

    /*private fun getLatestGame(gameOld: Game): Game {
        for (game in UserHandler.user.games) {
            if (game.gameId == gameOld.gameId)
                return game
        }
        return gameOld
    }*/