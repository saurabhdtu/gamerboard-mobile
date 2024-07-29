package com.gamerboard.live.gamestatemachine.bgmi.processor

import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.gamestatemachine.bgmi.*
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.processor.Handle
import com.gamerboard.live.gamestatemachine.stateMachine.*
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.google.common.truth.Truth
import org.junit.After
import org.junit.Before
import org.junit.Test

class HandleTest {
    lateinit var machine: com.tinder.StateMachine<State, Event, Effect>

    @Before
    fun setUp() {
        MachineConstants.loadConstants(SupportedGames.BGMI.packageName)
        machine = StateMachine.machine
        LabelUtils.testLogRed("Starting state machine")
    }


    private fun loginScreen() {
        Handle.handle(
            resultObjectWith_Login.build(),
            MachineConstants.machineLabelProcessor.getBucket(LabelUtils.getListOfLabels(LOGIN_RAW_JSON.obj().labels))
        )
    }

    private fun homeScreen() {
        Handle.handle(
            resultObjectWith_Start.build(),
            MachineConstants.machineLabelProcessor.getBucket(LabelUtils.getListOfLabels(START_JSON.obj().labels))
        )
    }

    private fun waitingScreen() {
        Handle.handle(
            resultObjectWith_Waiting.build(),
            MachineConstants.machineLabelProcessor.getBucket(
                LabelUtils.getListOfLabels(
                    USER1_WAITING_RAW_JSON.obj().labels
                )
            )
        )
    }

    private fun gameScreen() {
        Handle.handle(
            resultObjectWith_InGame.build(),
            MachineConstants.machineLabelProcessor.getBucket(
                LabelUtils.getListOfLabels(
                    CLASSIC_ALL_IN_GAME_JSON.obj().labels
                )
            )
        )
    }

    private fun resultRankKillsGameInfoScreen() {
        Handle.handle(
            USER1_GAME1_RANK_KILLS_GAME_INFO_RAW.build(),
            MachineConstants.machineLabelProcessor.getBucket(
                LabelUtils.getListOfLabels(
                    USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj().labels
                )
            )
        )
    }

    private fun resultRankRatingsGameInfoScreen() {
        Handle.handle(
            RATING_RAW.build(),
            MachineConstants.machineLabelProcessor.getBucket(
                LabelUtils.getListOfLabels(
                    RESULT_RANK_RATING_JSON.obj().labels
                )
            )
        )
    }

    private fun helper_verify_user() {
        val userId = "100"
        val gameProfileId1 = "555555559"
        val gameCharId1 = "character"

        if (machine.state is State.UnInitialized) {
            Truth.assertThat(machine.state).isInstanceOf(State.UnInitialized::class.java)
            //machine.transition(Event.InitializeUser(userId, false))
            machine.transition(Event.SetOriginalGameProfile(userId, gameCharId1))
            machine.transition(Event.SetOnBoarding(false))
            Truth.assertThat(machine.state).isInstanceOf(State.Idle::class.java)
        }

        // First time verify
        machine.transition(Event.VerifyUser(gameProfileId = gameProfileId1, gameCharId = gameCharId1))
        Truth.assertThat(machine.state).isInstanceOf(State.Verified::class.java)
    }

    private fun helper_initialize_user() {
        val userId = "100"

        Truth.assertThat(machine.state).isInstanceOf(State.UnInitialized::class.java)
        //machine.transition(Event.InitializeUser(userId, false))
        machine.transition(Event.SetOriginalGameProfile(userId, "abc"))
        machine.transition(Event.SetOnBoarding(false))
        Truth.assertThat(machine.state).isInstanceOf(State.Idle::class.java)
    }


    @Test
    fun test_play_single_game() {
        homeScreen()
        helper_verify_user()
        Truth.assertThat(machine.state is VerifiedUser).isTrue()
        waitingScreen()
        Truth.assertThat(machine.state is State.Lobby).isTrue()
        gameScreen()
        Truth.assertThat(machine.state is State.GameStarted).isTrue()
        resultRankRatingsGameInfoScreen()
        Truth.assertThat(machine.state is State.FetchResult).isTrue()
        resultRankKillsGameInfoScreen()
        Truth.assertThat(machine.state is State.FetchResult).isTrue()
        homeScreen()

        Truth.assertThat(machine.state is State.Verified).isTrue()
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
            Truth.assertThat(gamesPlayed[0].kills).isNotEqualTo(UNKNOWN)
            Truth.assertThat(gamesPlayed[0].rank).isNotEqualTo(UNKNOWN)
        }
    }

    @Test
    fun test_single_game_no_result_directly_moved_to_home_no_game_saved() {
        helper_verify_user()
        gameScreen()
        Truth.assertThat(machine.state is State.GameStarted).isTrue()
        homeScreen()
        Truth.assertThat(machine.state is State.Verified).isTrue()
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(0)
        }
    }

    @Test
    fun test_single_game_no_kills_saved_game_with_default_value() {
        helper_verify_user()
        Truth.assertThat(machine.state is State.Verified).isTrue()
        gameScreen()
        Truth.assertThat(machine.state is State.GameStarted).isTrue()
        resultRankRatingsGameInfoScreen()
        Truth.assertThat(machine.state is State.FetchResult).isTrue()
        homeScreen()

        Truth.assertThat(machine.state is State.Verified).isTrue()
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
            // kills are unknown
            Truth.assertThat(gamesPlayed[0].kills).isEqualTo(UNKNOWN)
            Truth.assertThat(gamesPlayed[0].rank).isNotEqualTo(UNKNOWN)
        }
    }

    @Test
    fun test_single_game_no_ratings_saved_game_with_default_value() {
        helper_verify_user()
        Truth.assertThat(machine.state is State.Verified).isTrue()
        gameScreen()
        Truth.assertThat(machine.state is State.GameStarted).isTrue()
        resultRankKillsGameInfoScreen()
        Truth.assertThat(machine.state is State.FetchResult).isTrue()
        homeScreen()

        Truth.assertThat(machine.state is State.Verified).isTrue()
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
            Truth.assertThat(gamesPlayed[0].kills).isNotEqualTo(UNKNOWN)
            Truth.assertThat(gamesPlayed[0].rank).isNotEqualTo(UNKNOWN)

            // rating is unknown
            Truth.assertThat(gamesPlayed[0].initialTier).isEqualTo(UNKNOWN)
        }
    }

    @Test
    fun test_play_2_game_in_succession_recorded_correctly() {
        helper_verify_user()
        Truth.assertThat(machine.state is State.Verified).isTrue()

        // Game 1
        waitingScreen()
        gameScreen()
        Truth.assertThat(machine.state is State.GameStarted).isTrue()
        resultRankKillsGameInfoScreen()
        Truth.assertThat(machine.state is State.FetchResult).isTrue()
        homeScreen()

        Truth.assertThat(machine.state is State.Verified).isTrue()
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
        }

        // Game 2
        gameScreen()
        Truth.assertThat(machine.state is State.GameStarted).isTrue()
        resultRankKillsGameInfoScreen()
        Truth.assertThat(machine.state is State.FetchResult).isTrue()

        homeScreen()
        Truth.assertThat(machine.state is State.Verified).isTrue()
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(2)
        }
    }

    @Test
    fun test_play_2_games_second_game_incomplete_should_not_save() {
        helper_verify_user()
        Truth.assertThat(machine.state is State.Verified).isTrue()

        // Game 1
        waitingScreen()
        gameScreen()
        Truth.assertThat(machine.state is State.GameStarted).isTrue()
        resultRankRatingsGameInfoScreen()
        resultRankKillsGameInfoScreen()
        Truth.assertThat(machine.state is State.FetchResult).isTrue()
        homeScreen()

        Truth.assertThat(machine.state is State.Verified).isTrue()
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
        }

        waitingScreen()
        resultRankRatingsGameInfoScreen()
        resultRankKillsGameInfoScreen()

        // User did not transition over the Results screen,
        // because there was no game screen in between
        Truth.assertThat(machine.state is State.FetchResult).isFalse()

        homeScreen()
        Truth.assertThat(machine.state is State.Verified).isTrue()
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
        }
    }

    @Test
    fun test_un_verify_user_on_login_screen() {
        helper_verify_user()
        Truth.assertThat(machine.state is State.Verified).isTrue()

        loginScreen()
        Truth.assertThat(machine.state is State.Idle).isTrue()
    }

    @Test
    fun test_un_verify_user_on_login_detected_after_game_screen() {
        helper_verify_user()
        Truth.assertThat(machine.state is State.Verified).isTrue()

        // this will start the game
        gameScreen()

        loginScreen()
        Truth.assertThat(machine.state is State.Idle).isTrue()
    }

    // SPECTATE CASE
    @Test
    fun test_do_not_record_multiple_games_on_spectate() {
        helper_verify_user()

        // This case tests if a user starts spectating form game screen,
        // that should not start a new game and should consider the
        // kills after user exits the game

        // GAME 1
        waitingScreen()
        gameScreen()
        resultRankRatingsGameInfoScreen()

        Truth.assertThat(machine.state is State.FetchResult).isTrue()

        // starts spectating
        gameScreen()

        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(0)
        }

        resultRankRatingsGameInfoScreen()
        resultRankKillsGameInfoScreen()
        homeScreen()

        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
        }
    }

    @Test
    fun test_not_a_case_of_spectate_if_home_in_between() {
        // If we found start, home screen in between
        // then finish the game if existing
        helper_verify_user()

        // GAME 1
        waitingScreen()
        gameScreen()
        resultRankRatingsGameInfoScreen()

        Truth.assertThat(machine.state is State.FetchResult).isTrue()

        // entered the home screen in between
        homeScreen()

        // the was finished and recorded
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
        }
    }

    @Test
    fun test_not_a_case_of_spectate_if_waiting_in_between() {
        // If we found start, home screen in between
        // then finish the game if existing
        helper_verify_user()

        // GAME 1
        waitingScreen()
        gameScreen()
        resultRankRatingsGameInfoScreen()

        Truth.assertThat(machine.state is State.FetchResult).isTrue()

        // entered the home screen in between
        waitingScreen()

        // the was finished and recorded
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
        }
    }

    @Test
    fun test_record_valid_game_if_appeared_after_spectate_game_completed() {
        helper_verify_user()

        // This case tests if a user starts spectating form game screen,
        // that should not start a new game and should consider the
        // kills after user exits the game

        // GAME 1
        waitingScreen()
        gameScreen()
        resultRankRatingsGameInfoScreen()

        Truth.assertThat(machine.state is State.FetchResult).isTrue()

        // starts spectating
        gameScreen()

        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(0)
        }

        homeScreen()

        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
        }
    }

    @Test
    fun test_record_valid_game_if_login_appeared_after_result() {
        helper_verify_user()
        waitingScreen()
        gameScreen()

        resultRankRatingsGameInfoScreen()
        resultRankKillsGameInfoScreen()

        // this prints the last game in logs, find a way to assert condition
        loginScreen()
    }

    @Test
    fun test_record_multiple_games_one_by_one() {
        helper_verify_user()
        waitingScreen()
        gameScreen()

        resultRankRatingsGameInfoScreen()
        resultRankKillsGameInfoScreen()
        homeScreen()

        // Game 1
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(1)
        }

        gameScreen()
        resultRankRatingsGameInfoScreen()
        resultRankKillsGameInfoScreen()
        homeScreen()

        // Game 2
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(2)
        }

        waitingScreen()
        gameScreen()
        resultRankRatingsGameInfoScreen()
        waitingScreen()

        // Game 3
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(3)
            // the third game did not have kills
            Truth.assertThat(gamesPlayed[2].kills).isEqualTo(UNKNOWN)
            Truth.assertThat(gamesPlayed[2].initialTier).isNotEqualTo(UNKNOWN)
        }

        gameScreen()
        resultRankKillsGameInfoScreen()
        waitingScreen()

        // Game 4
        (machine.state as VerifiedUser).verifiedUserDetails.apply {
            Truth.assertThat(gamesPlayed.size).isEqualTo(4)
            // the third game did not have rating for this
            Truth.assertThat(gamesPlayed[3].kills).isNotEqualTo(UNKNOWN)

            // no tier
            Truth.assertThat(gamesPlayed[3].initialTier).isEqualTo(UNKNOWN)
        }
    }

    @After
    fun cleanUp() {
        LabelUtils.testLogGreen("Test passed!")
        machine.transition(Event.UnVerifyUser("Reset State machine!"))
        machine.transition(Event.UnInitializedUser("Reset State machine!"))
    }

    @Test
    fun handle() {
    }
}