package com.gamerboard.live.gamestatemachine.unitTests.EngineTest

import com.google.common.truth.Truth.assertThat
import com.tinder.StateMachine
import org.junit.Test

class MachineInputHandlerTest {

    /*@Before
    fun setup() {
        debugMachine = DEBUGGER.DIRECT_HANDLE
        UserHandler.resetUser()
        Machine.stateMachine.transition(GameEvent.OnResetState)
    }

    private fun loginScreen(fromStart: Boolean = true) {
        if (fromStart)
            assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                LOGIN_RAW.build(),
                getBucket(getFromLabels(LOGIN_RAW_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[loginScreenBucket]!!)
                processInputBuffer(loginScreenJsonBuilder().obj())
        assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
    }

    private fun homeScreen(fromStart: Boolean = true) {
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                START.build(),
                getBucket(getFromLabels(START_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[homeScreenBucket]!!)
                processInputBuffer(homeScreenJsonBuilder().obj())
        assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
    }

    private fun waitingScreen(fromStart: Boolean = true) {
        if (fromStart)
            homeScreen(fromStart)
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                USER1_WAITING_RAW.build(),
                getBucket(getFromLabels(USER1_WAITING_RAW_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[waitingScreenBucket]!!)
                processInputBuffer(waitingScreenJsonBuilder().obj())
        assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
    }


    private fun gameScreen(fromStart: Boolean = true) {
        if (fromStart) {
            waitingScreen(fromStart)
            assertThat(CurrentGame.game.startTimeStamp).isNotEqualTo("Un-Known")
        }
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                CLASSIC_ALL_IN_GAME.build(),
                getBucket(getFromLabels(CLASSIC_ALL_IN_GAME_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[gameScreenBucket]!!)
                processInputBuffer(gameScreenJsonBuilder().obj())
    }

    private fun resultRankKillsGameInfoScreen(fromStart: Boolean = true) {
        if (fromStart) {
            gameScreen(fromStart)
            assertThat(CurrentGame.game.startTimeStamp).isNotEqualTo("Un-Known")
        }
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                USER1_GAME1_RANK_KILLS_GAME_INFO_RAW.build(),
                getBucket(getFromLabels(USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[resultRankRating]!!)
                processInputBuffer(resultRankKillsScreenJsonBuilder().obj())
    }

    private fun resultRankRatingsGameInfoScreen(fromStart: Boolean = true) {
        if (fromStart) {
            resultRankKillsGameInfoScreen(fromStart)
        }
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                RATING_RAW.build(),
                getBucket(getFromLabels(RESULT_RANK_RATING_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[resultRankRating]!!)
                processInputBuffer(resultRankRatingsBuilder().obj())
    }


    @Test
    fun happy_path() {
        homeScreen(false)
        waitingScreen(false)
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)
        resultRankKillsGameInfoScreen(false)
        assertThat(CurrentGame.game.startTimeStamp).isEqualTo(UNKNOWN)
        assertThat(UserHandler.user.games).hasSize(1)
        assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo(UNKNOWN)
        assertThat(UserHandler.user.games[0].kills).isNotEqualTo(UNKNOWN)
        assertThat(UserHandler.user.games[0].initialTier).isNotEqualTo(UNKNOWN)
        assertThat(UserHandler.user.games[0].gameInfo).isNotEqualTo(UNKNOWN)
    }


    @Test
    fun single_game_no_result() {
        gameScreen(true)
        homeScreen(false)
        assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun single_game_no_rank_kills() {
        gameScreen(true)
        resultRankRatingsGameInfoScreen(false)
        assertThat(UserHandler.user.games).hasSize(1)

        assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo(UNKNOWN)
        assertThat(UserHandler.user.games[0].initialTier).isNotEqualTo(UNKNOWN)
        assertThat(UserHandler.user.games[0].gameInfo).isNotEqualTo(UNKNOWN)
    }

    @Test
    fun single_game_no_rank_rating() {
        gameScreen(true)
        resultRankKillsGameInfoScreen(false)
        assertThat(UserHandler.user.games).hasSize(1)

        assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo(UNKNOWN)
        assertThat(UserHandler.user.games[0].kills).isNotEqualTo(UNKNOWN)
        assertThat(UserHandler.user.games[0].gameInfo).isNotEqualTo(UNKNOWN)
    }

    @Test
    fun single_game_invalid_start_no_in_game_label() {
        homeScreen(true)
        MachineInputHandler.handleInput(
            USER1_GAME2_RANK_RATINGS_GAME_INFO_RAW.build(),
            getBucket(getFromLabels(USER1_GAME2_RANK_KILLS_GAME_INFO_RAW_JSON.obj().labels))
        )
        assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        assertThat(UserHandler.user.games).hasSize(0)
    }


    @Test
    fun play_multiple_games_all_passed() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            assertThat(game.kills).isEqualTo(TestGame1.kills)
            assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        Thread.sleep(5)

        // GAME 2
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(2)
        UserHandler.user.games[1].also { game ->
            assertThat(game.kills).isEqualTo(TestGame1.kills)
            assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo(UserHandler.user.games[1].startTimeStamp)
        assertThat(UserHandler.user.games[0].endTimestamp).isNotEqualTo(UserHandler.user.games[1].endTimestamp)
    }


    @Test
    fun play_multiple_games_start_from_start_all_passed() {
        homeScreen(false)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            assertThat(game.kills).isEqualTo(TestGame1.kills)
            assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        Thread.sleep(5)

        // GAME 2
        homeScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(2)
        UserHandler.user.games[1].also { game ->
            assertThat(game.kills).isEqualTo(TestGame1.kills)
            assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo(UserHandler.user.games[1].startTimeStamp)
        assertThat(UserHandler.user.games[0].endTimestamp).isNotEqualTo(UserHandler.user.games[1].endTimestamp)
    }


    @Test
    fun play_multiple_game_second_failed_no_result() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            assertThat(game.kills).isEqualTo(TestGame1.kills)
            assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        Thread.sleep(5)


        // GAME 2 : No Result Screen
        waitingScreen(false)
        gameScreen(false)
        homeScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo("Un-Known")
        assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")
    }


    @Test
    fun play_multiple_games_second_failed_no_valid_start() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            assertThat(game.kills).isEqualTo(TestGame1.kills)
            assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        Thread.sleep(5)

        // GAME 2 : No In-Game Screen
        waitingScreen(false)
        resultRankKillsGameInfoScreen(false)
        homeScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo("Un-Known")
        assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")
    }


    @Test
    fun reset_game_on_login_after_waiting() {
        waitingScreen(true)
        loginScreen(false)
        assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")
        assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun reset_game_on_login_after_in_game() {
        gameScreen(true)
        loginScreen(false)
        assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")
        assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun do_not_count_multiple_games_on_spectate() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)
        assertThat(UserHandler.user.games).hasSize(1)

        resultRankKillsGameInfoScreen(false)
        assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            assertThat(game.kills).isEqualTo(TestGame1.kills)
            assertThat(game.rank).isEqualTo(TestGame1.rank)
        }
        Thread.sleep(5)

        // GAME 2
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)
        homeScreen(false)

        assertThat(UserHandler.user.games).hasSize(2)
        assertThat(UserHandler.user.games[1].startTimeStamp).isNotEqualTo("Un-Known")
        assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")

        // GAME 3
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(3)
        assertThat(UserHandler.user.games[2].startTimeStamp).isNotEqualTo("Un-Known")
        assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")

        // GAME 3 : Spectate
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(3)
        assertThat(UserHandler.user.games[2].startTimeStamp).isNotEqualTo("Un-Known")
        assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")
    }


    @Test
    fun record_multiple_games_on_start_from_waiting() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            assertThat(game.kills).isEqualTo(TestGame1.kills)
            assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        Thread.sleep(5)

        // GAME 2 : From Waiting, Start Screen not visited
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        homeScreen(false)

        assertThat(UserHandler.user.games).hasSize(2)
        assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo(UserHandler.user.games[1].startTimeStamp)
    }

    @Test
    fun do_not_count_multiple_games_on_no_spectate_start_from_start() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            assertThat(game.kills).isEqualTo(TestGame1.kills)
            assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        Thread.sleep(5)


        // GAME 2 : Spectating.
        gameScreen(false)
        homeScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo("Un-Known")

        Thread.sleep(5)


        // GAME 3 : Spectating, from home screen.
        homeScreen(false)
        gameScreen(false)
        homeScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo("Un-Known")
    }

    @Test
    fun do_not_record_in_valid_games() {

        // GAME 1 : Valid
        homeScreen(true)
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)

        Thread.sleep(5)


        // GAME 2 : Valid
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(2)

        Thread.sleep(5)

        // GAME 3 : In-Valid, Spectate
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(3)

        Thread.sleep(5)

        // GAME 4 : In-Valid, No Result
        homeScreen(true)
        waitingScreen(false)
        gameScreen(false)
        homeScreen(false)

        assertThat(UserHandler.user.games).hasSize(3)

        Thread.sleep(5)

        // GAME 5 : Valid
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(4)

        Thread.sleep(5)

        // GAME 6 : In-Valid, Spectate
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(5)
    }

    @Test
    fun do_not_record_in_valid_games_with_no_rank_spectate() {

        // GAME 1 : Valid
        homeScreen(true)
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)

        Thread.sleep(5)


        // GAME 2 : Valid
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(2)

        Thread.sleep(5)

        // GAME 3 : In-Valid, Spectate
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(3)

        Thread.sleep(5)

        // GAME 3 : In-Valid, Spectate
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(3)

        Thread.sleep(5)

        // GAME 4 : In-Valid, No Result
        homeScreen(true)
        waitingScreen(false)
        gameScreen(false)
        homeScreen(false)

        assertThat(UserHandler.user.games).hasSize(3)

        Thread.sleep(5)

        // GAME 5 : Valid
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(4)

        Thread.sleep(5)

        // GAME 6 : In-Valid, Spectate
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(5)

        // GAME 3 : In-Valid, Spectate
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(6)

        Thread.sleep(5)

        // GAME 3 : In-Valid, Spectate
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(6)

        Thread.sleep(5)


        // GAME 3 : In-Valid, Spectate
        waitingScreen()
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(7)
        Thread.sleep(5)

        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(7)
        Thread.sleep(5)
    }

    @Test
    fun do_not_record_game_if_started_from_game_screen() {
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        homeScreen(false)
        assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun record_valid_game_after_spectate_ended() {
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        homeScreen(false)
        assertThat(UserHandler.user.games).hasSize(0)

        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        assertThat(UserHandler.user.games).hasSize(1)
    }

    @Test
    fun record_valid_game_from_waiting_after_spectate_ended() {
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        assertThat(UserHandler.user.games).hasSize(0)

        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        assertThat(UserHandler.user.games).hasSize(1)
    }

    @Test
    fun record_valid_game_if_login_appeared_after_result() {
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        loginScreen(false)
        assertThat(UserHandler.user.games).hasSize(1)
    }

    @Test
    fun do_not_record_valid_game_if_login_appeared_after_waiting() {
        waitingScreen(false)
        loginScreen(false)

        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun record_valid_game_if_login_appeared_before_waiting() {
        loginScreen(false)

        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
    }


    @Test
    fun check_timestamp_correct_order_for_each_game() {

        gameScreen(true)
        val t1 = CurrentGame.game.startTimeStamp
        resultRankKillsGameInfoScreen(false)

        Thread.sleep(5)

        gameScreen(true)
        val t2 = CurrentGame.game.startTimeStamp
        resultRankKillsGameInfoScreen(false)

        Thread.sleep(5)

        gameScreen(true)
        val t3 = CurrentGame.game.startTimeStamp
        resultRankKillsGameInfoScreen(false)

        Thread.sleep(5)

        gameScreen(true)
        val t4 = CurrentGame.game.startTimeStamp
        resultRankKillsGameInfoScreen(false)

        Thread.sleep(5)

        val t5 = CurrentGame.game.startTimeStamp

        assertThat(t1).isNotEqualTo(t2)
        assertThat(t2).isNotEqualTo(t3)
        assertThat(t3).isNotEqualTo(t4)
        assertThat(t4).isNotEqualTo(t1)

        assertThat(t2).isGreaterThan(t1)
        assertThat(t3).isGreaterThan(t2)
        assertThat(t4).isGreaterThan(t3)
        assertThat(t2).isGreaterThan(t1)

        assertThat(t5).isEqualTo("Un-Known")
    }

    @Test
    fun check_timestamp_reset_for_valid_game_after_invalid() {

        gameScreen(true)
        val t1 = CurrentGame.game.startTimeStamp
        resultRankKillsGameInfoScreen()

        Thread.sleep(5)

        homeScreen(false)
        waitingScreen(false)
        val t2 = CurrentGame.game.startTimeStamp
        resultRankKillsGameInfoScreen(false)

        Thread.sleep(5)

        waitingScreen(true)
        gameScreen(false)
        val t3 = CurrentGame.game.startTimeStamp
        resultRankKillsGameInfoScreen(false)

        Thread.sleep(5)

        val t4 = CurrentGame.game.startTimeStamp

        assertThat(t1).isNotEqualTo(t2)
        assertThat(t2).isNotEqualTo(t3)
        assertThat(t3).isNotEqualTo(t4)
        assertThat(t4).isNotEqualTo(t1)

        assertThat(t2).isGreaterThan(t1)
        assertThat(t3).isGreaterThan(t2)
        assertThat(t4).isGreaterThan(t3)

        assertThat(t4).isEqualTo("Un-Known")
    }

    @Test
    fun check_timestamp_different_for_game_after_spectate() {
        homeScreen(true)
        gameScreen(false)

        val t1 = CurrentGame.game.startTimeStamp

        Thread.sleep(5)

        homeScreen(true)
        waitingScreen(false)

        val t2 = CurrentGame.game.startTimeStamp
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games.size).isEqualTo(1)
        assertThat(t1).isNotEqualTo(t2)
        assertThat(t2).isGreaterThan(t1)
    }


    @Test
    fun tempCheck() {
        homeScreen(false)
        assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        gameScreen(false)
        gameScreen(false)
        gameScreen(false)
        gameScreen(false)
        gameScreen(false)

        assertThat(Machine.stateMachine.state).isEqualTo(GameState.GAME_STARTED)

        homeScreen(false)
        homeScreen(false)
        homeScreen(false)
        homeScreen(false)


        assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        assertThat(UserHandler.user.games).hasSize(0)

        gameScreen(false)
        gameScreen(false)
        gameScreen(false)
        gameScreen(false)

        assertThat(Machine.stateMachine.state).isEqualTo(GameState.GAME_STARTED)
        assertThat(UserHandler.user.games).hasSize(0)

        homeScreen(false)
        homeScreen(false)
        homeScreen(false)

        assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        assertThat(UserHandler.user.games).hasSize(0)

        waitingScreen(false)
        waitingScreen(false)
        waitingScreen(false)
        waitingScreen(false)



        gameScreen(false)
        gameScreen(false)

        assertThat(Machine.stateMachine.state).isEqualTo(GameState.GAME_STARTED)
        assertThat(UserHandler.user.games).hasSize(0)

        gameScreen(false)
        gameScreen(false)

        assertThat(Machine.stateMachine.state).isEqualTo(GameState.GAME_STARTED)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
    }

    @Test
    fun rating_change() {
        debugMachine = DEBUGGER.TO_PROCESSOR_TEST

        homeScreen(false)
        waitingScreen(false)
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        assertThat(UserHandler.user.games[0].initialTier).isEqualTo("Gold VI")
        assertThat(UserHandler.user.games[0].finalTier).isEqualTo("Gold VI")
    }

    @Test
    fun rating_change_single_game() {
        debugMachine = DEBUGGER.TO_PROCESSOR_TEST

        homeScreen(false)
        waitingScreen(false)
        gameScreen(false)
        for (i in 0..LabelBufferSize[resultRankRating]!! + 2)
            processInputBuffer(resultRankRatingsBuilder(rating = "Gold III").obj())

        for (i in 0..LabelBufferSize[resultRankRating]!! + 2)
            processInputBuffer(resultRankRatingsBuilder(rating = "Gold VI").obj())

        resultRankKillsGameInfoScreen(false)
        assertThat(UserHandler.user.games).hasSize(1)
        assertThat(UserHandler.user.games[0].initialTier).isEqualTo("Gold III")
        assertThat(UserHandler.user.games[0].finalTier).isEqualTo("Gold VI")
        assertThat(UserHandler.user.games[0].kills).isEqualTo("1")
    }

    @Test
    fun rating_change_single_game_single_image() {
        debugMachine = DEBUGGER.TO_PROCESSOR_TEST

        homeScreen(false)
        waitingScreen(false)
        gameScreen(false)
        processInputBuffer(resultRankRatingsBuilder(rating = "Gold III").obj())

        resultRankKillsGameInfoScreen(false)
        assertThat(UserHandler.user.games).hasSize(1)
        assertThat(UserHandler.user.games[0].initialTier).isEqualTo("Gold III")
        assertThat(UserHandler.user.games[0].kills).isEqualTo("1")
    }

    @Test
    fun query_auto_ml_test_called_post_game_to_start() {
        gameScreen(true)
        UserHandler.runQuery = false

        resultRankRatingsGameInfoScreen(false)
        assertThat(UserHandler.runQuery).isFalse()

        resultRankKillsGameInfoScreen(false)
        assertThat(UserHandler.runQuery).isFalse()

        homeScreen(false)
        assertThat(UserHandler.runQuery).isTrue()
    }


    @Test
    fun query_auto_ml_test_called_post_game_to_minimize() {
        gameScreen(true)
        UserHandler.runQuery = false

        resultRankRatingsGameInfoScreen(false)
        assertThat(UserHandler.runQuery).isFalse()

        resultRankKillsGameInfoScreen(false)
        assertThat(UserHandler.runQuery).isFalse()

        UserHandler.runQuery = false

        Machine.stateMachine.transition(GameEvent.OnGameEnd)
        assertThat(UserHandler.runQuery).isTrue()
    }

    @Test
    fun record_next_game_if_reached_kills_screen_in_between() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            assertThat(game.kills).isEqualTo(TestGame1.kills)
            assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        Thread.sleep(5)

        // GAME 2 : No Result Screen
        //waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        homeScreen(false)

        assertThat(UserHandler.user.games).hasSize(2)
        assertThat(UserHandler.user.games[1].startTimeStamp).isNotEqualTo("Un-Known")
    }*/

    @Test
    fun testSM() {
        assertThat((SM.machine.state as State.HOME).HomeState).isEqualTo("Initial state!")
        SM.machine.transition(Event.FromHome(987878877))
        assertThat((SM.machine.state as State.IDLE).IdleState).isEqualTo("I was in HOME, got verified with 987878877")
        SM.machine.transition(Event.ToSameState())
        SM.machine.transition(Event.ToSameState())
        SM.machine.transition(Event.ToHome(6))
        assertThat((SM.machine.state as State.HOME).HomeState).isEqualTo("I was IDLE for 10 minutes!")
        SM.machine.transition(Event.FromHome(10000009))


        when (val sm = SM.machine.state) {
            is State.HOME -> {
                println("Home state ${sm.HomeState}")
            }
            is State.IDLE -> {
                println("Idle state ${sm.IdleState}")
            }
        }
    }
}


sealed class State {
    class HOME(val HomeState: String) : State()
    class IDLE(val IdleState: String, var counter: Int) : State()
}

sealed class Event {
    class FromHome(val homeData: Int) : Event()
    class ToHome(val toHomeData: Int) : Event()
    class ToSameState() : Event()
}

open class Effect {
    class DoOnHome(val toHomeData: Int, val previousStateData: String) : Effect()
    class DoOnIdle(val homeData: Int, val previousStateData: String) : Effect()
    class PrintCounter(val counter: Int) : Effect()
}

object Handle {
    fun handle(effect: Effect) {

        when (effect) {

            is Effect.DoOnHome -> {
                println("IDLE SCREEN ${effect.previousStateData} ${effect.toHomeData}")
            }
            is Effect.DoOnIdle -> {
                println("HOME SCREEN ${effect.previousStateData} ${effect.homeData}")
            }
            is Effect.PrintCounter -> {
                println("IDLE SCREEN Counter: ${effect.counter}")
            }
            else -> {

            }
        }
    }
}

object SM {
    var machine = StateMachine.create<State, Event, Effect> {
        initialState(State.HOME("Initial state!"))

        state<State.HOME> {
            on<Event.FromHome> {
                transitionTo(
                    State.IDLE("I was in HOME, got verified with ${it.homeData}", 0),
                    Effect.DoOnIdle(homeData = it.homeData, previousStateData = this.HomeState)
                )
            }
        }

        state<State.IDLE> {
            on<Event.ToHome> {
                transitionTo(
                    State.HOME("I was IDLE for 10 minutes!"),
                    Effect.DoOnHome(toHomeData = it.toHomeData, previousStateData = this.IdleState)
                )
            }
            on<Event.ToSameState> {
                counter++
                transitionTo(this, Effect.PrintCounter(counter))
            }
        }

        onTransition { nextStep ->
            val validStep = nextStep as? StateMachine.Transition.Valid ?: return@onTransition
            validStep.sideEffect?.let { Handle.handle(it) }
        }
    }
}

