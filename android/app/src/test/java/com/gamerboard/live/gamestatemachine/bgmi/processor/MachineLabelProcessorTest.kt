package com.gamerboard.live.gamestatemachine.bgmi.processor

import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.google.common.truth.Truth.assertThat
import okhttp3.internal.immutableListOf
import org.junit.Test

class MachineLabelProcessorTest {

    /*@Before
    fun setup() {
        debugMachine = DEBUGGER.TO_PROCESSOR_TEST
        UserHandler.resetUser()
        Machine.stateMachine.transition(GameEvent.OnResetState)
    }

    private fun loginScreen(fromStart: Boolean = true) {
        if (fromStart)
            Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                LOGIN_RAW.build(),
                getBucket(getFromLabels(LOGIN_RAW_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[loginScreenBucket]!!)
                processInputBuffer(loginScreenJsonBuilder().obj())
        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
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
        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
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
        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
    }


    private fun gameScreen(fromStart: Boolean = true) {
        if (fromStart) {
            waitingScreen(fromStart)
            Truth.assertThat(CurrentGame.game.startTimeStamp).isNotEqualTo("Un-Known")
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
            Truth.assertThat(CurrentGame.game.startTimeStamp).isNotEqualTo("Un-Known")
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
        Truth.assertThat(CurrentGame.game.startTimeStamp).isEqualTo(CurrentGame.UNKNOWN)
        Truth.assertThat(UserHandler.user.games).hasSize(1)
        Truth.assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo(CurrentGame.UNKNOWN)
        Truth.assertThat(UserHandler.user.games[0].kills).isNotEqualTo(CurrentGame.UNKNOWN)
        Truth.assertThat(UserHandler.user.games[0].initialTier).isNotEqualTo(CurrentGame.UNKNOWN)
        Truth.assertThat(UserHandler.user.games[0].gameInfo).isNotEqualTo(CurrentGame.UNKNOWN)
    }

    @Test
    fun single_game_no_result() {
        gameScreen(true)
        homeScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun single_game_no_rank_kills() {
        gameScreen(true)
        resultRankRatingsGameInfoScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(1)

        Truth.assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo(CurrentGame.UNKNOWN)
        Truth.assertThat(UserHandler.user.games[0].initialTier).isNotEqualTo(CurrentGame.UNKNOWN)
        Truth.assertThat(UserHandler.user.games[0].gameInfo).isNotEqualTo(CurrentGame.UNKNOWN)
    }

    @Test
    fun single_game_no_rank_rating() {
        gameScreen(true)
        resultRankKillsGameInfoScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(1)

        Truth.assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo(CurrentGame.UNKNOWN)
        Truth.assertThat(UserHandler.user.games[0].kills).isNotEqualTo(CurrentGame.UNKNOWN)
        Truth.assertThat(UserHandler.user.games[0].gameInfo).isNotEqualTo(CurrentGame.UNKNOWN)
    }

    @Test
    fun single_game_invalid_start_no_in_game_label() {
        homeScreen(true)
        MachineInputHandler.handleInput(
            USER1_GAME2_RANK_RATINGS_GAME_INFO_RAW.build(),
            getBucket(getFromLabels(USER1_GAME2_RANK_KILLS_GAME_INFO_RAW_JSON.obj().labels))
        )
        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        Truth.assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun play_multiple_games_all_passed() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Thread.sleep(5)

        // GAME 2
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(2)
        UserHandler.user.games[1].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Truth.assertThat(UserHandler.user.games[0].startTimeStamp)
            .isNotEqualTo(UserHandler.user.games[1].startTimeStamp)
        Truth.assertThat(UserHandler.user.games[0].endTimestamp)
            .isNotEqualTo(UserHandler.user.games[1].endTimestamp)
    }


    @Test
    fun play_multiple_games_start_from_start_all_passed() {
        homeScreen(false)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Thread.sleep(5)

        // GAME 2
        homeScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(2)
        UserHandler.user.games[1].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Truth.assertThat(UserHandler.user.games[0].startTimeStamp)
            .isNotEqualTo(UserHandler.user.games[1].startTimeStamp)
        Truth.assertThat(UserHandler.user.games[0].endTimestamp)
            .isNotEqualTo(UserHandler.user.games[1].endTimestamp)
    }


    @Test
    fun play_multiple_game_second_failed_no_result() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Thread.sleep(5)


        // GAME 2 : No Result Screen
        waitingScreen(false)
        gameScreen(false)
        homeScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        Truth.assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo("Un-Known")
        Truth.assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")
    }


    @Test
    fun play_multiple_games_second_failed_no_valid_start() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Thread.sleep(5)

        // GAME 2 : No In-Game Screen
        waitingScreen(false)
        resultRankKillsGameInfoScreen(false)
        homeScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        Truth.assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo("Un-Known")
        Truth.assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")
    }


    @Test
    fun reset_game_on_login_after_waiting() {
        waitingScreen(true)
        loginScreen(false)
        Truth.assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")
        Truth.assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun reset_game_on_login_after_in_game() {
        gameScreen(true)
        loginScreen(false)
        Truth.assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")
        Truth.assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun do_not_count_multiple_games_on_spectate() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Thread.sleep(5)

        // GAME 2 : Spectate
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        homeScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        Truth.assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo("Un-Known")
        Truth.assertThat(CurrentGame.game.startTimeStamp).isEqualTo("Un-Known")
    }


    @Test
    fun record_multiple_games_on_start_from_waiting() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Thread.sleep(5)

        // GAME 2 : From Waiting, Start Screen not visited
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        homeScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(2)
        Truth.assertThat(UserHandler.user.games[0].startTimeStamp)
            .isNotEqualTo(UserHandler.user.games[1].startTimeStamp)
    }

    @Test
    fun do_not_count_multiple_games_on_no_spectate_start_from_start() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Thread.sleep(5)


        // GAME 2 : Spectating.
        gameScreen(false)
        homeScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        Truth.assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo("Un-Known")

        Thread.sleep(5)


        // GAME 3 : Spectating, from home screen.
        homeScreen(false)
        gameScreen(false)
        homeScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        Truth.assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo("Un-Known")
    }

    @Test
    fun do_not_record_in_valid_games() {

        // GAME 1 : Valid
        homeScreen(true)
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)

        Thread.sleep(5)


        // GAME 2 : Valid
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(2)

        Thread.sleep(5)

        // GAME 3 : In-Valid, Spectate
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(2)

        Thread.sleep(5)

        // GAME 4 : In-Valid, No Result
        homeScreen(true)
        waitingScreen(false)
        gameScreen(false)
        homeScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(2)

        Thread.sleep(5)

        // GAME 5 : Valid
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(3)

        Thread.sleep(5)

        // GAME 6 : In-Valid, Spectate
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(3)
    }

    @Test
    fun reset_match_with_waiting_label() {
        homeScreen(true)

        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Thread.sleep(5)

        // GAME 2
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(2)
        UserHandler.user.games[1].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("74")
        }

        Truth.assertThat(UserHandler.user.games[0].startTimeStamp)
            .isNotEqualTo(UserHandler.user.games[1].startTimeStamp)
        Truth.assertThat(UserHandler.user.games[0].endTimestamp)
            .isNotEqualTo(UserHandler.user.games[1].endTimestamp)
    }

    @Test
    fun do_not_record_game_if_started_from_game_screen() {
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        homeScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun record_valid_game_after_spectate_ended() {
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        homeScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(0)

        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(1)
    }

    @Test
    fun record_valid_game_from_waiting_after_spectate_ended() {
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(0)

        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(1)
    }

    @Test
    fun record_valid_game_if_login_appeared_after_result() {
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        loginScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(1)
    }

    @Test
    fun do_not_record_valid_game_if_login_appeared_after_waiting() {
        waitingScreen(false)
        loginScreen(false)

        gameScreen(false)
        loginScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(0)
    }

    @Test
    fun record_valid_game_if_login_appeared_before_waiting() {
        loginScreen(false)

        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
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

        Truth.assertThat(t1).isNotEqualTo(t2)
        Truth.assertThat(t2).isNotEqualTo(t3)
        Truth.assertThat(t3).isNotEqualTo(t4)
        Truth.assertThat(t4).isNotEqualTo(t1)

        Truth.assertThat(t2).isGreaterThan(t1)
        Truth.assertThat(t3).isGreaterThan(t2)
        Truth.assertThat(t4).isGreaterThan(t3)
        Truth.assertThat(t2).isGreaterThan(t1)

        Truth.assertThat(t5).isEqualTo("Un-Known")
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

        Truth.assertThat(t1).isNotEqualTo(t2)
        Truth.assertThat(t2).isNotEqualTo(t3)
        Truth.assertThat(t3).isNotEqualTo(t4)
        Truth.assertThat(t4).isNotEqualTo(t1)

        Truth.assertThat(t2).isGreaterThan(t1)
        Truth.assertThat(t3).isGreaterThan(t2)
        Truth.assertThat(t4).isGreaterThan(t3)

        Truth.assertThat(t4).isEqualTo("Un-Known")
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

        Truth.assertThat(UserHandler.user.games.size).isEqualTo(1)
        Truth.assertThat(t1).isNotEqualTo(t2)
        Truth.assertThat(t2).isGreaterThan(t1)
    }


    @Test
    fun tempCheck() {
        homeScreen(false)
        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        gameScreen(false)
        gameScreen(false)
        gameScreen(false)
        gameScreen(false)
        gameScreen(false)

        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.GAME_STARTED)

        homeScreen(false)
        homeScreen(false)
        homeScreen(false)
        homeScreen(false)


        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        Truth.assertThat(UserHandler.user.games).hasSize(0)

        gameScreen(false)
        gameScreen(false)
        gameScreen(false)
        gameScreen(false)

        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.GAME_STARTED)
        Truth.assertThat(UserHandler.user.games).hasSize(0)

        homeScreen(false)
        homeScreen(false)
        homeScreen(false)

        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        Truth.assertThat(UserHandler.user.games).hasSize(0)

        waitingScreen(false)
        waitingScreen(false)
        waitingScreen(false)
        waitingScreen(false)



        gameScreen(false)
        gameScreen(false)

        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.GAME_STARTED)
        Truth.assertThat(UserHandler.user.games).hasSize(0)

        gameScreen(false)
        gameScreen(false)

        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.GAME_STARTED)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
    }

    @Test
    fun rating_change() {
        debugMachine = DEBUGGER.TO_PROCESSOR_TEST

        homeScreen(false)
        waitingScreen(false)
        gameScreen(false)
        resultRankRatingsGameInfoScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        Truth.assertThat(UserHandler.user.games[0].initialTier).isEqualTo("Gold VI")
        Truth.assertThat(UserHandler.user.games[0].finalTier).isEqualTo("Gold VI")
    }

    @Test
    fun rating_change_single_game() {
        debugMachine = DEBUGGER.TO_PROCESSOR_TEST

        homeScreen(false)
        waitingScreen(false)
        gameScreen(false)
        for (i in 0..LabelBufferSize[resultRankRating]!! + 2)
            MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder(rating = "Gold III").obj())

        for (i in 0..LabelBufferSize[resultRankRating]!! + 2)
            MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder(rating = "Gold VI").obj())

        resultRankKillsGameInfoScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(1)
        Truth.assertThat(UserHandler.user.games[0].initialTier).isEqualTo("Gold III")
        Truth.assertThat(UserHandler.user.games[0].finalTier).isEqualTo("Gold VI")
        Truth.assertThat(UserHandler.user.games[0].kills).isEqualTo("1")
    }

    @Test
    fun rating_change_single_game_single_image() {
        debugMachine = DEBUGGER.TO_PROCESSOR_TEST

        homeScreen(false)
        waitingScreen(false)
        gameScreen(false)
        MachineLabelProcessor.processInputBuffer(resultRankRatingsBuilder(rating = "Gold III").obj())

        resultRankKillsGameInfoScreen(false)
        Truth.assertThat(UserHandler.user.games).hasSize(1)
        Truth.assertThat(UserHandler.user.games[0].initialTier).isEqualTo("Gold III")
        Truth.assertThat(UserHandler.user.games[0].kills).isEqualTo("1")
    }

    @Test
    fun query_auto_ml_test_called_post_game_to_start() {
        gameScreen(true)
        UserHandler.runQuery = false

        resultRankRatingsGameInfoScreen(false)
        Truth.assertThat(UserHandler.runQuery).isFalse()

        resultRankKillsGameInfoScreen(false)
        Truth.assertThat(UserHandler.runQuery).isFalse()

        homeScreen(false)
        Truth.assertThat(UserHandler.runQuery).isTrue()
    }

    @Test
    fun query_auto_ml_test_called_post_game_to_minimize() {
        gameScreen(true)
        UserHandler.runQuery = false

        resultRankRatingsGameInfoScreen(false)
        Truth.assertThat(UserHandler.runQuery).isFalse()

        resultRankKillsGameInfoScreen(false)
        Truth.assertThat(UserHandler.runQuery).isFalse()

        UserHandler.runQuery = false

        Machine.stateMachine.transition(GameEvent.OnGameEnd)
        Truth.assertThat(UserHandler.runQuery).isTrue()
    }

    @Test
    fun test_game_should_end_if_exists_before_reset() {
        UserHandler.runQuery = false
        resultRankKillsGameInfoScreen(true)
        Machine.stateMachine.transition(GameEvent.OnResetState)
        Truth.assertThat(UserHandler.runQuery).isTrue()
        Truth.assertThat(UserHandler.user.games).hasSize(1)
    }

    @Test
    fun test_game_should_finish_incomplete_game_if_exists_before_reset() {
        UserHandler.runQuery = false
        gameScreen(true)
        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.GAME_STARTED)
        Machine.stateMachine.transition(GameEvent.OnResetState)
        Truth.assertThat(UserHandler.runQuery).isFalse()
        Truth.assertThat(UserHandler.user.games).hasSize(0)
    }

}

class MachineLabelProcessorTestBuckets {

    @Before
    fun setup() {
        debugMachine = DEBUGGER.DIRECT_HANDLE
        UserHandler.resetUser()
        MachineLabelProcessor.currentBuffer.clear()
        MachineLabelProcessor.bufferCount = 0
        Machine.stateMachine.transition(GameEvent.OnResetState)
    }


    private fun loginScreen(fromStart: Boolean = true) {
        if (fromStart)
            Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                LOGIN_RAW.build(),
                getBucket(MachineLabelUtils.getFromLabels(LOGIN_RAW_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[loginScreenBucket]!!)
                processInputBuffer(loginScreenJsonBuilder().obj())
        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
    }

    private fun homeScreen(fromStart: Boolean = true) {
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                START.build(),
                getBucket(MachineLabelUtils.getFromLabels(START_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[homeScreenBucket]!!)
                processInputBuffer(homeScreenJsonBuilder().obj())
        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
    }

    private fun waitingScreen(fromStart: Boolean = true) {
        if (fromStart)
            homeScreen(fromStart)
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                USER1_WAITING_RAW.build(),
                getBucket(MachineLabelUtils.getFromLabels(USER1_WAITING_RAW_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[waitingScreenBucket]!!)
                processInputBuffer(waitingScreenJsonBuilder().obj())
        Truth.assertThat(Machine.stateMachine.state).isEqualTo(GameState.PROFILE_VERIFIED)
    }


    private fun gameScreen(fromStart: Boolean = true) {
        if (fromStart) {
            waitingScreen(fromStart)
            Truth.assertThat(CurrentGame.game.startTimeStamp).isNotEqualTo("Un-Known")
        }
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                CLASSIC_ALL_IN_GAME.build(),
                getBucket(MachineLabelUtils.getFromLabels(CLASSIC_ALL_IN_GAME_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[gameScreenBucket]!!)
                processInputBuffer(gameScreenJsonBuilder().obj())
    }

    private fun resultRankKillsGameInfoScreen(fromStart: Boolean = true) {
        if (fromStart) {
            gameScreen(fromStart)
            Truth.assertThat(CurrentGame.game.startTimeStamp).isNotEqualTo("Un-Known")
        }
        if (debugMachine == DEBUGGER.DIRECT_HANDLE)
            MachineInputHandler.handleInput(
                USER1_GAME1_RANK_KILLS_GAME_INFO_RAW.build(),
                getBucket(MachineLabelUtils.getFromLabels(USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj().labels))
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
                getBucket(MachineLabelUtils.getFromLabels(RESULT_RANK_RATING_JSON.obj().labels))
            )
        else if (debugMachine == DEBUGGER.TO_PROCESSOR_TEST)
            for (i in 0..LabelBufferSize[resultRankRating]!!)
                processInputBuffer(resultRankRatingsBuilder().obj())
    }

    @Test
    fun test_buffer_count_less_than_required() {
        val minimumRequired: Int = LabelBufferSize[getBucket(GameLabels.CLASSIC_ALL_WAITING.i)]!!
        for (i in 1..minimumRequired - 2)
            processInputBuffer(USER1_WAITING_RAW_JSON.obj())

        Truth.assertThat(MachineLabelProcessor.bufferCount).isNotEqualTo(0)
        Truth.assertThat(MachineLabelProcessor.currentBuffer[getBucket(GameLabels.CLASSIC_ALL_WAITING.i)])
            .hasSize(
            minimumRequired - 2
        )
    }

    @Test
    fun test_buffer_max_size() {
        for (i in 1 until MAX_BUFFER_LIMIT) {
            processInputBuffer(USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj())
            Truth.assertThat(MachineLabelProcessor.currentBuffer[resultRankKills]).hasSize(i)
        }
        processInputBuffer(USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj())
        Truth.assertThat(MachineLabelProcessor.currentBuffer[resultRankKills]).hasSize(MAX_BUFFER_LIMIT - 1)

        processInputBuffer(USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj())
        Truth.assertThat(MachineLabelProcessor.currentBuffer[resultRankKills]).hasSize(MAX_BUFFER_LIMIT - 1)

        processInputBuffer(USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON.obj())
        Truth.assertThat(MachineLabelProcessor.currentBuffer[resultRankKills]).hasSize(MAX_BUFFER_LIMIT - 1)
    }

    @Test
    fun test_previous_result_preserved_after_new_coming_incorrect_values() {
        homeScreen(false)
        Truth.assertThat(MachineLabelProcessor.currentBuffer).isEmpty()
        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        processInputBuffer(
            resultRankKillsScreenJsonBuilder(
                kills = "pratyush finishes 1",
                rank = "3/99"
            ).obj()
        )
        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("3")
        }

        for (i in 1..MAX_BUFFER_LIMIT / 2)
            processInputBuffer(
                resultRankKillsScreenJsonBuilder(
                    kills = "pratyush finishes 1",
                    rank = "3 /99"
                ).obj()
            )


        for (i in 1..MAX_BUFFER_LIMIT / 2)
            processInputBuffer(
                resultRankKillsScreenJsonBuilder(
                    kills = "pratyush finishes ",
                    rank = "3/99"
                ).obj()
            )

        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("3")
        }

        for (i in 1..5)
            processInputBuffer(
                resultRankKillsScreenJsonBuilder(
                    kills = "Players defeated ",
                    rank = "3/99"
                ).obj()
            )

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo("1")
            Truth.assertThat(game.rank).isEqualTo("3")
        }
    }


    @Test
    fun test_get_bucket_true() {
        val testBucket1 = (GameLabels.CLASSIC_ALL_GAME_INFO.i or
                GameLabels.CLASSIC_ALL_RANK.i)

        Truth.assertThat(getBucket(testBucket1)).isEqualTo(resultRankKills)

        val testBucket2 = (GameLabels.PROFILE_SELF.i or
                GameLabels.PROFILE_ID_DETAILS.i)

        Truth.assertThat(getBucket(testBucket2)).isEqualTo(homeScreenBucket)

        val testBucket3 = (GameLabels.CLASSIC_ALL_GAME_INFO.i)
        Truth.assertThat(getBucket(testBucket3)).isEqualTo(resultRankRating)

        val testBucket4 = (GameLabels.CLASSIC_ALL_GAME_INFO.i or GameLabels.CLASSIC_ALL_RANK.i)
        Truth.assertThat(getBucket(testBucket4)).isEqualTo(resultRankKills)

        val testBucket5 = (GameLabels.CLASSIC_ALL_GAME_INFO.i)
        Truth.assertThat(getBucket(testBucket5)).isEqualTo(resultRankRating)

        val testBucket6 = (GameLabels.CLASSIC_ALL_WAITING.i)
        Truth.assertThat(getBucket(testBucket6)).isEqualTo(waitingScreenBucket)

        val testBucket7 = (GameLabels.LOGIN.i)
        Truth.assertThat(getBucket(testBucket7)).isEqualTo(loginScreenBucket)

        val testBucket8 = (GameLabels.CLASSIC_ALL_WAITING.i)
        Truth.assertThat(getBucket(testBucket8)).isEqualTo(waitingScreenBucket)

        val testBucket9 = (GameLabels.CLASSIC_ALL_IN_GAME.i)
        Truth.assertThat(getBucket(testBucket9)).isEqualTo(gameScreenBucket)

        val testBucket10 = (GameLabels.RATING_RANK.i)
        Truth.assertThat(getBucket(testBucket10)).isEqualTo(resultRankRating)
    }

    @Test
    fun test_get_bucket_false() {
        val testBucket1 = (GameLabels.CLASSIC_ALL_GAME_INFO.i or
                GameLabels.PROFILE_SELF.i or
                GameLabels.PROFILE_ID.i)

        Truth.assertThat(getBucket(testBucket1)).isNotEqualTo(gameScreenBucket)

        val testBucket2 = (GameLabels.CLASSIC_ALL_GAME_INFO.i or
                GameLabels.CLASSIC_ALL_KILLS.i or
                GameLabels.CLASSIC_ALL_RANK.i)

        Truth.assertThat(getBucket(testBucket2)).isNotEqualTo(gameScreenBucket)

        val testBucket3 = (GameLabels.PROFILE_SELF.i or
                GameLabels.PROFILE_ID_DETAILS.i or
                GameLabels.PROFILE_LEVEL.i)

        Truth.assertThat(getBucket(testBucket3)).isNotEqualTo(gameScreenBucket)

        val testBucket4 = (GameLabels.PROFILE_SELF.i or
                GameLabels.PROFILE_ID.i)

        Truth.assertThat(getBucket(testBucket4)).isNotEqualTo(loginScreenBucket)

        val testBucket5 = (GameLabels.PROFILE_ID_DETAILS.i)

        Truth.assertThat(getBucket(testBucket5)).isNotEqualTo(gameScreenBucket)

        val testBucket6 = (GameLabels.PROFILE_ID_DETAILS.i or
                GameLabels.PROFILE_LEVEL.i)

        Truth.assertThat(getBucket(testBucket6)).isNotEqualTo(gameScreenBucket)

        val testBucket7 = (GameLabels.CLASSIC_ALL_GAME_INFO.i or
                GameLabels.CLASSIC_ALL_RANK.i or
                GameLabels.CLASSIC_ALL_KILLS.i)

        Truth.assertThat(getBucket(testBucket7)).isNotEqualTo(resultRankRating)

        val testBucket8 = (GameLabels.CLASSIC_ALL_GAME_INFO.i or
                GameLabels.RATING_RANK.i)

        Truth.assertThat(getBucket(testBucket8)).isNotEqualTo(resultRankKills)
    }


    @Test
    fun test_buffer_resets_for_multiple_games() {
        homeScreen(false)
        Truth.assertThat(MachineLabelProcessor.currentBuffer).isEmpty()
        // GAME 1
        waitingScreen(false)
        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(1)
        UserHandler.user.games[0].also { game ->
            Truth.assertThat(game.kills).isEqualTo(TestGame1.kills)
            Truth.assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        Thread.sleep(5)

        // GAME 2
        homeScreen(false)

        Truth.assertThat(MachineLabelProcessor.currentBuffer).isEmpty()

        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(2)
        UserHandler.user.games[1].also { game ->
            Truth.assertThat(game.kills).isEqualTo(TestGame1.kills)
            Truth.assertThat(game.rank).isEqualTo(TestGame1.rank)
        }

        Truth.assertThat(UserHandler.user.games[0].startTimeStamp).isNotEqualTo(UserHandler.user.games[1].startTimeStamp)
        Truth.assertThat(UserHandler.user.games[0].endTimestamp).isNotEqualTo(UserHandler.user.games[1].endTimestamp)

        waitingScreen(false)

        Truth.assertThat(MachineLabelProcessor.currentBuffer).isEmpty()

        gameScreen(false)
        resultRankKillsGameInfoScreen(false)

        Truth.assertThat(UserHandler.user.games).hasSize(3)
        UserHandler.user.games[2].also { game ->
            Truth.assertThat(game.kills).isEqualTo(TestGame1.kills)
            Truth.assertThat(game.rank).isEqualTo(TestGame1.rank)
        }
    }

    @Test
    fun test_clear_buffer_between_waiting() {
        /*
        homeScreen(true)
        processInputBuffer(waitingScreenJsonBuilder().obj())
        processInputBuffer(waitingScreenJsonBuilder().obj())

        assertThat(currentBuffer[waitingScreenBucket]).hasSize(2)

        processInputBuffer(gameScreenJsonBuilder().obj())
        processInputBuffer(gameScreenJsonBuilder().obj())

        processInputBuffer(waitingScreenJsonBuilder().obj())

        assertThat(currentBuffer[waitingScreenBucket]).hasSize(1)
        assertThat(currentBuffer[gameScreenBucket]).hasSize(0)


        processInputBuffer(gameScreenJsonBuilder().obj())
        processInputBuffer(gameScreenJsonBuilder().obj())
        processInputBuffer(gameScreenJsonBuilder().obj())

        assertThat(currentBuffer[waitingScreenBucket]).hasSize(0)
        assertThat(currentBuffer[gameScreenBucket]).hasSize(3)
        assertThat(currentBuffer[gameScreenBucket]).hasSize(3)
        */
    }*/

    @Test
    fun testGetBucket() {
        val bucket1 =
            MachineConstants.machineLabelProcessor.getBucket(immutableListOf(BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal))
        assertThat(bucket1).isNotEqualTo(MachineConstants.gameConstants.resultRankKills())

        // val bucket2 = getBucket(GameLabels.CLASSIC_ALL_RANK.i)  // This case was removed because, in some cases the rank label
        // assertThat(bucket2).isEqualTo(resultRankKills)          // might be detected form the rating screen. On rating screen we have rankRating label,
        // on kills screen we have rank label.

        val bucket2 =
            MachineConstants.machineLabelProcessor.getBucket(immutableListOf(BGMIConstants.GameLabels.RANK.ordinal))
        assertThat(bucket2).isNotEqualTo(MachineConstants.gameConstants.resultRankRating())

        val bucket3 =
            MachineConstants.machineLabelProcessor.getBucket(immutableListOf(BGMIConstants.GameLabels.CLASSIC_RATING.ordinal))
        assertThat(bucket3).isNotEqualTo(MachineConstants.gameConstants.resultRankRating())

        val bucket4 =
            MachineConstants.machineLabelProcessor.getBucket(immutableListOf(BGMIConstants.GameLabels.GAME_INFO.ordinal))
        assertThat(bucket4).isNotEqualTo(MachineConstants.gameConstants.resultRankRating())

        val bucket5 =
            MachineConstants.machineLabelProcessor.getBucket(immutableListOf(BGMIConstants.GameLabels.CLASSIC_ALL_GAMEPLAY.ordinal))
        assertThat(bucket5).isNotEqualTo(MachineConstants.gameConstants.gameScreenBucket())
    }
}