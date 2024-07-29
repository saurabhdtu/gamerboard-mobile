package com.gamerboard.live.gamestatemachine.bgmi.stateMachine

import com.gamerboard.live.gamestatemachine.games.LabelUtils.testLogGreen
import com.gamerboard.live.gamestatemachine.games.LabelUtils.testLogRed
import com.gamerboard.live.gamestatemachine.stateMachine.*
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.gamerboard.live.models.db.GameInfo
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class StateMachineTest {

    lateinit var machine: com.tinder.StateMachine<State, Event, Effect>
    private lateinit var visionStateMachine: com.tinder.StateMachine<VisionState, VisionEvent, VisionEffect>

    @Before
    fun setUp() {
        machine = StateMachine.machine
        visionStateMachine = VisionStateMachine.visionImageSaver
        testLogRed("Starting state machine")
    }

    @Test
    fun test_user_initialization() {
        //constants
        val userId = "100"
        assertThat(machine.state).isInstanceOf(State.UnInitialized::class.java)
        //machine.transition(Event.InitializeUser(userId, false))
        machine.transition(Event.SetOriginalGameProfile(userId, "abc"))
        machine.transition(Event.SetOnBoarding(false))
        assertThat(machine.state).isInstanceOf(State.Idle::class.java)

        val unVerifiedUser = (machine.state as UserDetails).unVerifiedUserDetails
        assertThat(unVerifiedUser.originalGameId).isEqualTo(userId)
        assertThat(unVerifiedUser.onBoarding).isEqualTo(false)
    }

    @Test
    fun test_user_verification() {
        //constants
        val userId = "100"
        val gameProfileId = "555555559"
        val gameCharId = "character"

        assertThat(machine.state).isInstanceOf(State.UnInitialized::class.java)
        //machine.transition(Event.InitializeUser(userId, false))
        machine.transition(Event.SetOriginalGameProfile(userId, "character"))
        machine.transition(Event.SetOnBoarding(false))
        assertThat(machine.state).isInstanceOf(State.Idle::class.java)

        val unVerifiedUser = (machine.state as UserDetails).unVerifiedUserDetails
        assertThat(unVerifiedUser.originalGameId).isEqualTo(userId)
        assertThat(unVerifiedUser.onBoarding).isEqualTo(false)

        machine.transition(Event.VerifyUser(gameProfileId = gameProfileId, gameCharId = gameCharId))
        assertThat(machine.state).isInstanceOf(State.Verified::class.java)

        val verifiedUser = (machine.state as VerifiedUser).verifiedUserDetails

        assertThat(verifiedUser.userId).isEqualTo(userId)
        assertThat(verifiedUser.onBoarding).isEqualTo(false)
        assertThat(verifiedUser.gameCharId).isEqualTo(gameCharId)
        assertThat(verifiedUser.originalGameId).isEqualTo(gameProfileId)
        assertThat(verifiedUser.canStartGame).isEqualTo(true)
    }

    @Test
    fun test_user_can_not_be_verified_multiple_times() {
        val userId = "100"
        val gameProfileId1 = "555555559"
        val gameCharId1 = "character"

        assertThat(machine.state).isInstanceOf(State.UnInitialized::class.java)
        //machine.transition(Event.OnInitialized(userId, false))
        machine.transition(Event.SetOriginalGameProfile(userId, "character"))
        machine.transition(Event.SetOnBoarding(false))
        assertThat(machine.state).isInstanceOf(State.Idle::class.java)

        // First time verify
        machine.transition(
            Event.VerifyUser(
                gameProfileId = gameProfileId1,
                gameCharId = gameCharId1
            )
        )
        assertThat(machine.state).isInstanceOf(State.Verified::class.java)

        val verifiedUser1 = (machine.state as VerifiedUser).verifiedUserDetails

        assertThat(verifiedUser1.userId).isEqualTo(userId)
        assertThat(verifiedUser1.gameCharId).isEqualTo(gameCharId1)
        assertThat(verifiedUser1.originalGameId).isEqualTo(gameProfileId1)

        val gameProfileId2 = "555555558"
        val gameCharId2 = "other character"

        // Verified the second time
        machine.transition(
            Event.VerifyUser(
                gameProfileId = gameProfileId2,
                gameCharId = gameCharId2
            )
        )
        assertThat(machine.state).isInstanceOf(State.Verified::class.java)

        val verifiedUserAgain = (machine.state as VerifiedUser).verifiedUserDetails

        // bgmi id, bgmi char id will not change once verified
        assertThat(verifiedUserAgain.userId).isEqualTo(userId)
        assertThat(verifiedUserAgain.gameCharId).isNotEqualTo(gameCharId2)
        assertThat(verifiedUserAgain.originalGameId).isNotEqualTo(gameProfileId2)
    }

    @Test
    fun test_user_un_verified_and_bgmi_id_gone() {
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()
        machine.transition(Event.UnVerifyUser(reason = "Testing user un verified"))
        assertThat(machine.state is VerifiedUser).isFalse()
    }

    @Test
    fun test_user_id_preserved_after_un_verified() {
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()
        val userIdOld = (machine.state as VerifiedUser).verifiedUserDetails.userId
        machine.transition(Event.UnVerifyUser(reason = "Testing user un verified"))
        assertThat(machine.state is VerifiedUser).isFalse()
        val userIdNew = (machine.state as UserDetails).unVerifiedUserDetails.originalGameId

        assertThat(userIdOld).isNotNull()
        assertThat(userIdOld).isEqualTo(userIdNew)
    }

    @Test
    fun test_user_can_not_enter_game_if_not_verified() {
        helper_initialize_user()
        assertThat(machine.state is State.Idle).isTrue()

        val startInfo = GameStartInfo("0004", "1")
        machine.transition(Event.EnteredGame(gameStartInfo = startInfo))

        assertThat(machine.state is State.GameStarted).isFalse()
    }

    @Test
    fun test_user_can_enter_game_from_verified() {
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()

        val gameStartInfo = GameStartInfo("0004", "1")
        machine.transition(Event.EnteredGame(gameStartInfo = gameStartInfo))

        assertThat(machine.state is VerifiedUser).isTrue()
        assertThat(machine.state is State.GameStarted).isTrue()

        val gameEndInfo = GameEndInfo("0006")
        machine.transition(Event.GameEnded(gameEndInfo = gameEndInfo))

        assertThat(machine.state is VerifiedUser).isTrue()
        assertThat(machine.state is State.GameEnded).isTrue()

        val gameEndState = machine.state as State.GameEnded
        assertThat(gameEndState.gameStartInfo).isEqualTo(gameStartInfo)
        assertThat(gameEndState.gameEndInfo).isEqualTo(gameEndInfo)
    }

    @Test
    fun test_user_can_enter_game_from_lobby() {
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()

        machine.transition(Event.EnteredLobby())

        assertThat(machine.state is VerifiedUser).isTrue()
        assertThat(machine.state is State.Lobby).isTrue()

        val gameStartInfo = GameStartInfo("0004", "1")
        machine.transition(Event.EnteredGame(gameStartInfo = gameStartInfo))

        assertThat(machine.state is State.GameStarted).isTrue()

        val gameEndInfo = GameEndInfo("0006")
        machine.transition(Event.GameEnded(gameEndInfo = gameEndInfo))

        assertThat(machine.state is VerifiedUser).isTrue()
        assertThat(machine.state is State.GameEnded).isTrue()

        val gameEndState = machine.state as State.GameEnded
        assertThat(gameEndState.gameStartInfo).isEqualTo(gameStartInfo)
        assertThat(gameEndState.gameEndInfo).isEqualTo(gameEndInfo)
    }

    @Test
    fun test_user_exit_from_game_started_does_not_record_game() {
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()

        val gameStartInfo = GameStartInfo("0004", "1")
        machine.transition(Event.EnteredGame(gameStartInfo = gameStartInfo))

        assertThat(machine.state is VerifiedUser).isTrue()
        assertThat(machine.state is State.GameStarted).isTrue()

        machine.transition(Event.OnHomeScreenDirectlyFromGameStarted(reason = "Test: User exits from game"))
        assertThat(machine.state is State.Verified).isTrue()
        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed).hasSize(0)
    }

    @Test
    fun test_user_exit_from_lobby_does_not_record_game() {
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()

        machine.transition(Event.EnteredLobby())

        assertThat(machine.state is VerifiedUser).isTrue()
        assertThat(machine.state is State.Lobby).isTrue()

        machine.transition(Event.OnHomeScreenDirectlyFromLobby(reason = "Test: User exits from lobby"))
        assertThat(machine.state is State.Verified).isTrue()
        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed).hasSize(0)
    }

    @Test
    fun test_user_plays_a_complete_game() {
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()
        machine.transition(Event.EnteredGame(GameStartInfo("001", "2")))
        machine.transition(Event.GameEnded(GameEndInfo("004")))

        machine.transition(Event.OnGameResultScreen())
        assertThat((machine.state is State.FetchResult)).isTrue()


        // up till now we haven't received any data so, UNKNOWNS
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult).isEqualTo(
            initialGameResult
        )

        machine.transition(Event.GotKill("4"))
        machine.transition(Event.GotRank("21"))
        machine.transition(Event.GotTier("Gold I", "Gold II"))
        machine.transition(Event.GotTeamRank("False"))
        machine.transition(
            Event.GotGameInfo(
                GameInfo(
                    mode = "classic",
                    type = "livik",
                    group = "solo",
                    view = "TPP"
                )
            )
        )

        (machine.state as State.FetchResult).gameResultDetails.gameResult.let { result->
            assertThat(result.kills).isEqualTo("4")
            assertThat(result.rank).isEqualTo("21")
            assertThat(result.initialTier).isEqualTo("Gold I")
            assertThat(result.finalTier).isEqualTo("Gold II")
            assertThat(result.gameInfo).isEqualTo(GameInfo(mode = "classic", type = "livik", group = "solo", view = "TPP"))
            assertThat(result.teamRank).isEqualTo("False")
        }

        // Till now the game is not concluded
        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed).hasSize(0)

        // Conclude the game
        machine.transition(Event.GameCompleted(reason = "Test: Game completed"))

        // Now it has the game
        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed).hasSize(1)
    }

    @Test
    fun test_single_game_did_not_go_to_result_screen(){
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()
        machine.transition(Event.EnteredGame(GameStartInfo("001", "2")))
        machine.transition(Event.OnHomeScreenDirectlyFromGameStarted(reason = "Test: User exits game from Game!"))

        assertThat((machine.state is State.Verified)).isTrue()
        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed).hasSize(0)
    }

    @Test
    fun test_single_game_did_not_visit_kills_screen_should_contain_incomplete_game(){
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()
        machine.transition(Event.EnteredGame(GameStartInfo("001", "2")))
        machine.transition(Event.GameEnded(GameEndInfo("004")))

        machine.transition(Event.OnGameResultScreen())

        machine.transition(Event.GotTier("Gold I", "Gold II"))
        (machine.state as State.FetchResult).gameResultDetails.gameResult.let { result->
            assertThat(result.initialTier).isEqualTo("Gold I")
            assertThat(result.finalTier).isEqualTo("Gold II")

            // could not fetch kills so this is UNKNOWN
            assertThat(result.kills).isEqualTo(UNKNOWN)
        }

        machine.transition(Event.GameCompleted(reason = "Test: Game completed, did not fetch kills"))
        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed).hasSize(1)
    }

    @Test
    fun test_single_game_user_did_not_enter_the_game_should_not_record_game(){
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()

        machine.transition(Event.OnGameResultScreen())
        assertThat((machine.state is State.FetchResult)).isFalse()
        assertThat((machine.state is State.Verified)).isTrue()
    }

    @Test
    fun test_play_multiple_games_one_after_the_other_all_complete(){
        helper_verify_user()

        machine.transition(Event.EnteredGame(GameStartInfo("001", "2")))
        machine.transition(Event.GameEnded(GameEndInfo("004")))
        machine.transition(Event.OnGameResultScreen())

        machine.transition(Event.GotKill("1"))
        machine.transition(Event.GameCompleted(reason = "User complete 1st game!"))
        assertThat(machine.state is State.Verified).isTrue()


        machine.transition(Event.EnteredLobby())
        machine.transition(Event.EnteredGame(GameStartInfo("005", "3")))
        machine.transition(Event.GameEnded(GameEndInfo("007")))
        machine.transition(Event.OnGameResultScreen())

        machine.transition(Event.GotKill("2"))
        machine.transition(Event.GameCompleted(reason = "User complete 1st game!"))
        assertThat(machine.state is State.Verified).isTrue()

        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed).hasSize(2)
        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed[0].gameId).isEqualTo("2")
        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed[1].gameId).isEqualTo("3")
    }

    @Test
    fun tier_and_kills_fetch_should_not_depend_on_the_order_of_screen_events(){
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()
        machine.transition(Event.EnteredGame(GameStartInfo("001", "2")))
        machine.transition(Event.GameEnded(GameEndInfo("004")))
        machine.transition(Event.OnGameResultScreen())

        machine.transition(Event.GotKill("4"))
        machine.transition(Event.GotRank("21"))
        machine.transition(Event.GotTier("Gold I", "Gold II"))
        machine.transition(Event.GotTeamRank("False"))
        machine.transition(
            Event.GotGameInfo(
                GameInfo(
                    mode = "classic",
                    type = "livik",
                    group = "solo",
                    view = "TPP"
                )
            )
        )

        (machine.state as State.FetchResult).gameResultDetails.gameResult.let { result->
            assertThat(result.kills).isEqualTo("4")
            assertThat(result.rank).isEqualTo("21")
            assertThat(result.initialTier).isEqualTo("Gold I")
            assertThat(result.finalTier).isEqualTo("Gold II")
            assertThat(result.gameInfo).isEqualTo(GameInfo(mode = "classic", type = "livik", group = "solo", view = "TPP"))
            assertThat(result.teamRank).isEqualTo("False")
        }

        // should not get override by UNKNOWN
        machine.transition(Event.GotKill(UNKNOWN))
        machine.transition(Event.GotRank(UNKNOWN))
        machine.transition(Event.GotTier(UNKNOWN, UNKNOWN))
        machine.transition(Event.GotTeamRank(UNKNOWN))
        machine.transition(
            Event.GotGameInfo(
                GameInfo(
                    mode = UNKNOWN,
                    type = "livik",
                    group = UNKNOWN,
                    view = "TPP"
                )
            )
        )

        (machine.state as State.FetchResult).gameResultDetails.gameResult.let { result->
            assertThat(result.kills).isEqualTo("4")
            assertThat(result.rank).isEqualTo("21")
            assertThat(result.initialTier).isEqualTo("Gold I")
            assertThat(result.finalTier).isEqualTo("Gold II")
            assertThat(result.gameInfo).isEqualTo(GameInfo(mode = "classic", type = "livik", group = "solo", view = "TPP"))
            assertThat(result.teamRank).isEqualTo("False")
        }
    }

    @Test
    fun test_send_for_automl_flag_if_final_true(){
        helper_verify_user()

        machine.transition(Event.EnteredGame(GameStartInfo("001", "2")))
        machine.transition(Event.GameEnded(GameEndInfo("004")))
        machine.transition(Event.OnGameResultScreen())

        machine.transition(Event.GotKill("1"))


        machine.transition(Event.GotKill("1"))
        machine.transition(Event.GotKill("1"))
        machine.transition(Event.GotKill("1"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlKills).isFalse()

        machine.transition(Event.GotKill("1"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlKills).isTrue()

        machine.transition(Event.GameCompleted(reason = "User completed the game, check the final automl flag for true!"))
    }

    @Test
    fun test_send_for_automl_flag_if_either_is_true_and_should_not_get_reset(){
        helper_verify_user()

        machine.transition(Event.EnteredGame(GameStartInfo("001", "2")))
        machine.transition(Event.GameEnded(GameEndInfo("004")))
        machine.transition(Event.OnGameResultScreen())

        machine.transition(Event.GotKill("1"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlKills).isFalse()

        machine.transition(Event.GotKill("1"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlKills).isTrue()

        machine.transition(Event.GotKill("1"))
        machine.transition(Event.GotKill("1"))
        machine.transition(Event.GotKill("1"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlKills).isFalse()

        machine.transition(Event.GotKill("1"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlKills).isFalse()

        machine.transition(Event.GotRank("1"))
        machine.transition(Event.GotTier("Gold I", "Gold II"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlRank).isFalse()
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlTier).isFalse()

        machine.transition(Event.GameCompleted(reason = "User completed the game, check the final automl flag for false!"))
    }

    @Test
    fun test_do_not_send_for_auto_ml_if_flags_unset_finally(){
        helper_verify_user()

        machine.transition(Event.EnteredGame(GameStartInfo("001", "2")))
        machine.transition(Event.GameEnded(GameEndInfo("004")))
        machine.transition(Event.OnGameResultScreen())

        // this remains true
        machine.transition(Event.GotRank("1"))

        machine.transition(Event.GotKill("1"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlKills).isFalse()

        machine.transition(Event.GotKill("1"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlKills).isTrue()

        machine.transition(Event.GotKill("1"))
        machine.transition(Event.GotKill("1"))
        machine.transition(Event.GotKill("1"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlKills).isFalse()

        machine.transition(Event.GotKill("1"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlKills).isFalse()

        machine.transition(Event.GotTier("Gold I", "Gold II"))
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlRank).isTrue()
        assertThat((machine.state as State.FetchResult).gameResultDetails.gameResult.autoMlTier).isFalse()

        machine.transition(Event.GameCompleted(reason = "User completed the game, check the final automl flag for true!"))
    }


    // AUTO ML + STATE MACHINE
    @Test
    fun test_images_saved_get_deleted_after_game_send_to_server(){
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()
        machine.transition(Event.EnteredGame(GameStartInfo("001", "2")))
        machine.transition(Event.GameEnded(GameEndInfo("004")))
        machine.transition(Event.OnGameResultScreen())


        // Kills image send to vision
        visionStateMachine.transition(
            VisionEvent.ReceivedKillsScreen(
                labelsKills = arrayListOf(),
                imageKills = "1_vc_test_kills.jpg"
            )
        )
        machine.transition(Event.GotKill("4"))

        // Ratings image send to vision
        visionStateMachine.transition(
            VisionEvent.ReceivedRatingsScreen(
                labelsRatings = arrayListOf(),
                imageRatings = "1_vc_test_ratings.jpg"
            )
        )
        machine.transition(Event.GotRank("21"))


        machine.transition(Event.GotTier("Gold I", "Gold II"))
        machine.transition(Event.GotTeamRank("False"))
        machine.transition(
            Event.GotGameInfo(
                GameInfo(
                    mode = "classic",
                    type = "livik",
                    group = "solo",
                    view = "TPP"
                )
            )
        )

        (machine.state as State.FetchResult).gameResultDetails.gameResult.let { result->
            assertThat(result.kills).isEqualTo("4")
            assertThat(result.rank).isEqualTo("21")
            assertThat(result.initialTier).isEqualTo("Gold I")
            assertThat(result.finalTier).isEqualTo("Gold II")
            assertThat(result.gameInfo).isEqualTo(GameInfo(mode = "classic", type = "livik", group = "solo", view = "TPP"))
            assertThat(result.teamRank).isEqualTo("False")
        }

        // After the completion of the game the event to delete the vc images should be triggered

        machine.transition(Event.GameCompleted(reason = "Test: Game completed"))
        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed).hasSize(1)
    }

    @Test
    fun test_images_saved_do_not_get_deleted_if_game_send_for_automl(){
        helper_verify_user()
        assertThat(machine.state is VerifiedUser).isTrue()
        machine.transition(Event.EnteredGame(GameStartInfo("001", "2")))
        machine.transition(Event.GameEnded(GameEndInfo("004")))
        machine.transition(Event.OnGameResultScreen())


        // Kills image send to vision
        visionStateMachine.transition(
            VisionEvent.ReceivedKillsScreen(
                labelsKills = arrayListOf(),
                imageKills = "1_vc_test_kills.jpg"
            )
        )
        machine.transition(Event.GotKill("4"))

        // Ratings image send to vision
        visionStateMachine.transition(
            VisionEvent.ReceivedRatingsScreen(
                labelsRatings = arrayListOf(),
                imageRatings = "1_vc_test_ratings.jpg"
            )
        )
        machine.transition(Event.GotRank("21"))


        machine.transition(Event.GotTier("Gold I", "Gold II"))
        machine.transition(Event.GotTeamRank("False"))
        machine.transition(
            Event.GotGameInfo(
                GameInfo(
                    mode = "classic",
                    type = "livik",
                    group = "solo",
                    view = "TPP"
                )
            )
        )

        (machine.state as State.FetchResult).gameResultDetails.gameResult.let { result->
            assertThat(result.kills).isEqualTo("4")
            assertThat(result.rank).isEqualTo("21")
            assertThat(result.initialTier).isEqualTo("Gold I")
            assertThat(result.finalTier).isEqualTo("Gold II")
            assertThat(result.gameInfo).isEqualTo(GameInfo(mode = "classic", type = "livik", group = "solo", view = "TPP"))
            assertThat(result.teamRank).isEqualTo("False")
        }

        // After the completion of the game the
        // the vc images will NOT deleted

        machine.transition(Event.GameCompleted(reason = "Test: Game completed"))
        assertThat((machine.state as VerifiedUser).verifiedUserDetails.gamesPlayed).hasSize(1)
    }

    @After
    fun cleanUp() {
        testLogGreen("Test passed!")
        machine.transition(Event.UnVerifyUser("Reset State machine!"))
        machine.transition(Event.UnInitializedUser("Reset State machine!"))
    }

    private fun helper_verify_user() {
        val userId = "100"
        val gameProfileId1 = "555555559"
        val gameCharId1 = "character"

        assertThat(machine.state).isInstanceOf(State.UnInitialized::class.java)
        //machine.transition(Event.InitializeUser(userId))
        machine.transition(Event.SetOriginalGameProfile(userId, "character"))
        machine.transition(Event.SetOnBoarding(false))
        assertThat(machine.state).isInstanceOf(State.Idle::class.java)

        // First time verify
        machine.transition(
            Event.VerifyUser(
                gameProfileId = gameProfileId1,
                gameCharId = gameCharId1
            )
        )
        assertThat(machine.state).isInstanceOf(State.Verified::class.java)
    }

    private fun helper_initialize_user() {
        val userId = "100"

        assertThat(machine.state).isInstanceOf(State.UnInitialized::class.java)
        //machine.transition(Event.InitializeUser(userId))
        machine.transition(Event.SetOriginalGameProfile(userId, "character"))
        machine.transition(Event.SetOnBoarding(false))
        assertThat(machine.state).isInstanceOf(State.Idle::class.java)
    }

    private val initialGameResult = GameResult(
        kills = UNKNOWN,
        rank = UNKNOWN,
        teamRank = UNKNOWN,
        initialTier = UNKNOWN,
        finalTier = UNKNOWN,
        gameInfo = GameInfo(
            UNKNOWN,
            UNKNOWN,
            UNKNOWN,
            UNKNOWN
        )
    )
}