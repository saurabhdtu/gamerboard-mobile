package com.gamerboard.live.repository

import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.apollographql.apollo3.ApolloCall
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.api.Optional
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.GetLeaderboardQuery
import com.gamerboard.live.GetMyActiveTournamentQuery
import com.gamerboard.live.GetGameScoringQuery
import com.gamerboard.live.GetSquadLeaderboardQuery
import com.gamerboard.live.SubmitBGMIGameMutation
import com.gamerboard.live.SubmitFFMaxGameMutation
import com.gamerboard.live.UpdateGameProfileMutation
import com.gamerboard.live.common.BroadcastFilters
import com.gamerboard.live.common.PlatformChannels
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.common.SupportedGames
import com.gamerboard.live.fragment.GameResponse
import com.gamerboard.live.fragment.LeaderboardRank
import com.gamerboard.live.fragment.SquadLeaderboard
import com.gamerboard.live.gamestatemachine.games.LabelUtils.validatePlayerScoring
import com.gamerboard.live.gamestatemachine.games.updateGameIdActive
import com.gamerboard.live.gamestatemachine.showToast
import com.gamerboard.live.gamestatemachine.stateMachine.Event
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineMessageBroadcaster
import com.gamerboard.live.gamestatemachine.stateMachine.State
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.gamerboard.live.gamestatemachine.stateMachine.UserDetails
import com.gamerboard.live.models.CustomGameResponse
import com.gamerboard.live.models.FeedBackFrom
import com.gamerboard.live.models.GameResult
import com.gamerboard.live.models.LeaderBoardElement
import com.gamerboard.live.models.LeaderBoardScoring
import com.gamerboard.live.models.ServerTournamentElement
import com.gamerboard.live.models.TopGames
import com.gamerboard.live.models.db.AppDatabase
import com.gamerboard.live.models.db.Game
import com.gamerboard.live.models.db.GameInfo
import com.gamerboard.live.service.screencapture.FlutterEngineHolder
import com.gamerboard.live.service.screencapture.ui.ScreenOfType
import com.gamerboard.live.service.screencapture.ui.ServiceUIHelper
import com.gamerboard.live.type.BgmiGroups
import com.gamerboard.live.type.BgmiLevels
import com.gamerboard.live.type.BgmiMaps
import com.gamerboard.live.type.BgmiProfileInput
import com.gamerboard.live.type.ESports
import com.gamerboard.live.type.FFMaxProfileInput
import com.gamerboard.live.type.FfMaxGroups
import com.gamerboard.live.type.FfMaxLevels
import com.gamerboard.live.type.FfMaxMaps
import com.gamerboard.live.type.LeaderboardDirection
import com.gamerboard.live.type.SquadMemberGameInfo
import com.gamerboard.live.utils.EventUtils
import com.gamerboard.live.utils.Events
import com.gamerboard.live.utils.FeedbackUtils
import com.gamerboard.live.utils.logException
import com.gamerboard.logger.LogCategory
import com.gamerboard.logger.LogHelper
import com.gamerboard.logger.gson
import com.gamerboard.logger.logWithIdentifier
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.java.KoinJavaComponent.inject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date


/**
 * Created by saurabh.lahoti on 30/12/21
 */


object GameRepository : KoinComponent {
    val db: AppDatabase by inject()

    var scoring: ApolloResponse<GetGameScoringQuery.Data>? = null
    private val logHelper: LogHelper by inject(LogHelper::class.java)
    suspend fun pushGameOrProfile(
        gameId: String?,
        context: Context?,
        apiClient: ApiClient,
        serviceUIHelper: ServiceUIHelper?,
        retry: Int = 0,
        wildPackages: List<Pair<String, Long>>?
    ) {
        if (gameId != null) {
            withContext(Dispatchers.Main) {
                serviceUIHelper?.loaderOnScreen(show = true, "Sending scores..")
            }
            val gameList = db.getGamesDao().getGameById(gameId)
            if (gameList.isNotEmpty()) {
                val game = gameList.first()
                if (game.userId == null) {
                    EventUtils.instance().logAnalyticsEvent(
                        Events.GAME_ERROR,
                        mapOf("message" to "Player Id Null for game $game, userId: ${game.userId}")
                    )

                    FirebaseCrashlytics.getInstance()
                        .recordException(Throwable("Player Id Null for game $game, userId: ${game.userId}"))
                }
                logWithIdentifier(game.gameId) {
                    it.setMessage("Game for api call")
                    it.addContext("game", game)
                }
                val hasFailed = checkValidGame(game = game)
                if (hasFailed != null) {
                    EventUtils.instance().logAnalyticsEvent(
                        Events.GAME_ERROR,
                        mapOf("message" to "Match failed with game: $game, userId: ${game.userId}")
                    )

                    logWithIdentifier(game.gameId) {
                        it.setMessage("Game for api call")
                        it.addContext("game", game)
                        it.addContext("reason", hasFailed)
                        it.addContext("game_id", gameId)
                        it.setCategory(LogCategory.CME)
                    }
                    logHelper.completeLogging()
                    withContext(Dispatchers.Main) {
                        serviceUIHelper?.retrySubmitGame(
                            true, 0, 0, "Game submission failed", hasFailed.getMessage(), null
                        )
                    }
                    return
                }
                try {
                    var error = ""
                    var submitGameResponse: GameResponse? = null
                    try {
                        submitGameResponse = getGameSubmitMutation(game, apiClient)
                    } catch (ex: Exception) {
                        error += ex.message
                    }
                    val scoringData = getScoring(apiClient)
                    if (scoringData.hasErrors()) error += scoringData.errors!!.first().message
                    if (error.isEmpty()) {
                        val gameResponse =
                            CustomGameResponse(submitGameResponse!!, scoringData.data!!)
                        val serverGameResponse = gson.toJson(gameResponse)
                        val map = hashMapOf(
                            "game" to MachineConstants.currentGame.gameName.lowercase(),
                            "group" to gameResponse.serverGame.game.getGroup(),
                            "tournament_id" to "NA",
                            "map" to gameResponse.serverGame.game.getMap(),
                            "gameTier" to gameResponse.serverGame.game.getFinalTier(),
                        )
                        wildPackages?.let {
                            for (i in it.indices) {
                                map["flagged_package_${i + 1}"] = it[i].first
                            }
                        }
                        if (submitGameResponse.tournaments.isNotEmpty()) {
                            val tournament = submitGameResponse.tournaments.find {
                                it?.isAdded ?: false
                            }
                            tournament?.let { map["tournament_id"] = it.tournament.id.toString() }
                        }
                        EventUtils.instance().logAnalyticsEvent(
                            Events.GAME_SUBMITTED, map
                        )
                        if (BuildConfig.IS_TEST.not()) {
                            db.getGamesDao().mapServerGameToLocal(
                                game.gameId,
                                submitGameResponse.game.id,
                                submitGameResponse.game.userId,
                            )
                        }
                        withContext(Dispatchers.Main) {
                            context?.sendBroadcast(Intent(BroadcastFilters.NATIVE_TO_FLUTTER).apply {
                                putExtra("action", "local_method")
                                putExtra("method", "refresh_page")
                            })
                            showToast("Game Ended!", force = true)
                            val gameGroup = gameResponse.serverGame.game.getGroup()
                            if (listOf(BgmiGroups.solo.name, FfMaxGroups.solo.name).contains(
                                    gameGroup
                                )
                            ) {
                                serviceUIHelper?.actionOnScreen(
                                    ScreenOfType.GAME_END,
                                    map = mapOf("current_game" to serverGameResponse)
                                )
                            } else {
                                if (submitGameResponse.tournaments.isNotEmpty()) {
                                    val isTournamentsComplete = submitGameResponse.tournaments.fold(
                                        false
                                    ) { tournamentsResult, tournament ->
                                        ((tournament?.submissionState?.fold(
                                            true
                                        ) { result, t ->
                                            (t.hasSubmitted && result) && (tournament.isAdded || tournamentsResult)
                                        }) ?: tournamentsResult)

                                    }
                                    if (isTournamentsComplete) {
                                        serviceUIHelper?.actionOnScreen(
                                            ScreenOfType.MULTIPLAYER_GAME_END_WITH_KILLS,
                                            map = mapOf("current_game" to serverGameResponse)
                                        )
                                    } else {
                                        serviceUIHelper?.actionOnScreen(
                                            ScreenOfType.MULTIPLAYER_GAME_END_WITHOUT_KILLS,
                                            map = mapOf("current_game" to serverGameResponse)
                                        )
                                    }
                                } else {
                                    showGameError(
                                        serviceUIHelper,
                                        GameFailureReason.NO_TOURNAMENTS_IN_RESPONSE,
                                        null
                                    )
                                }
                            }
                        }


                        logWithIdentifier(game.gameId) {
                            it.setMessage("Game successfully submitted.")
                            it.addContext("game", game)
                            it.addContext("reason", GameFailureReason.GAME_SUCCESSFUL)
                            it.addContext("game_id", gameId)
                            it.setCategory(LogCategory.CM)
                        }
                        logHelper.completeLogging()

                    } else {
                        FirebaseCrashlytics.getInstance().log("Apollo-error: $error")

                        logWithIdentifier(game.gameId) {
                            it.setMessage("Updated: We re-tried to submit game, previous request result")
                            it.addContext("game", game)
                            it.addContext("reason", error)
                            it.addContext("game_id", gameId)
                            it.setCategory(LogCategory.API_CALL)
                        }
                        if (retry < 3) {
                            // retry to submit request, will display the result on foreground
                            withContext(Dispatchers.Main) {
                                serviceUIHelper?.retrySubmitGame(head = "Game Submission failed!",
                                    message = "Reason:$error.\nAlso please make sure you have internet \n connection and press [Retry]. " + "If the issue persists, please report to Gamerboard via help from menu.",
                                    shouldCenter = true,
                                    x = 0,
                                    y = 0,
                                    retryCallBack = object : ReTryGameSubmit {
                                        override fun retry() {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                logWithIdentifier(game.gameId) {
                                                    it.setMessage("User clicked re-try, previous request result")
                                                    it.addContext("game", game)
                                                    it.addContext("reason", error)
                                                    it.addContext("game_id", gameId)
                                                    it.setCategory(LogCategory.API_CALL)
                                                }
                                                pushGameOrProfile(
                                                    gameId,
                                                    context,
                                                    apiClient,
                                                    serviceUIHelper,
                                                    retry = retry + 1,
                                                    wildPackages
                                                )
                                            }
                                        }

                                        override fun cancel() {

                                            logWithIdentifier(game.gameId) {
                                                it.setMessage("User did not re-try clicked cancel, upload logs, previous request result")
                                                it.addContext("reason", error)
                                                it.setCategory(LogCategory.API_CALL)
                                            }
                                            logHelper.completeLogging()
                                            logWithIdentifier(game.gameId) {
                                                it.setMessage("User clicked on retry")
                                                it.addContext(
                                                    "reason",
                                                    GameFailureReason.CLICKED_CANCEL_ON_RETRY
                                                )
                                                it.setCategory(LogCategory.CME)
                                            }
                                            context?.let {
                                                FeedbackUtils.requestFeedback(
                                                    ctx = it,
                                                    feedbackFrom = FeedBackFrom.GAME_COMPLETION,
                                                )
                                            }
                                        }
                                    })
                            }
                            return
                        }
                        logException(java.lang.Exception("Server error while submitting game! gameId: $gameId :$error"))
                        showGameError(serviceUIHelper, GameFailureReason.SOME_ERROR_OCCURRED, error)



                        logWithIdentifier(game.gameId) {
                            it.setMessage("Error from remote")
                            it.addContext("reason", GameFailureReason.CLICKED_CANCEL_ON_RETRY)
                            it.addContext("game", game)
                            it.addContext("game_id", gameId)
                            it.setCategory(LogCategory.CME)
                        }
                        logHelper.completeLogging()
                        EventUtils.instance().logAnalyticsEvent(
                            Events.GAME_ERROR, mapOf("message" to error)
                        )
                        if (retry <= 5) {
                            logWithIdentifier(game.gameId) {
                                it.setMessage("We enqueued a worker to submit the game")
                                it.addContext("reason", error)
                                it.setCategory(LogCategory.ENGINE)
                            }
                            context?.let { ctx ->
                                GameSubmitApi(ctx).submitGame(
                                    gameId, retry + 1
                                )
                            }
                            return
                        }
                    }
                } catch (e: Exception) {
                    logException(e)
                    showGameError(serviceUIHelper, GameFailureReason.SOME_ERROR_OCCURRED, e.message)

                    logWithIdentifier(game.gameId) {
                        it.setMessage("Error Occurred while submit")
                        it.addContext("reason", GameFailureReason.SOME_ERROR_OCCURRED)
                        it.addContext("error", e.message)
                        it.setCategory(LogCategory.CME)
                    }
                    logHelper.completeLogging()
                }
            }
            withContext(Dispatchers.Main) {
                serviceUIHelper?.loaderOnScreen(show = false, "")
            }
        } else {

            logWithIdentifier(gameId) {
                it.setMessage("Game Id was null for the game.")
                it.addContext("reason", GameFailureReason.GAME_ID_NULL)
                it.setCategory(LogCategory.CME)
            }
            logHelper.completeLogging()
        }

    }

    private suspend fun getGameSubmitMutation(game: Game, apiClient: ApiClient): GameResponse {
        val gameInfo = JSONObject(game.gameInfo!!)
        val dateTimeDiff = System.currentTimeMillis() - getTime()
        FirebaseCrashlytics.getInstance().log("timeDiff: $dateTimeDiff")
        logWithIdentifier(game.gameId) {
            it.setMessage("timeDiff: $dateTimeDiff\"")
            it.setCategory(LogCategory.API_CALL)
        }
        if (MachineConstants.currentGame == SupportedGames.BGMI) {
            val gameSubmit =
                if (getBGMIGroupForValue(gameInfo.getString("group")) == BgmiGroups.solo) {
                    SubmitBGMIGameMutation(
                        finalTier = getBGMILevelForValue(game.finalTier!!),
                        initialTier = getBGMILevelForValue(game.initialTier!!),
                        kills = game.kills!!.toInt(),
                        rank = game.rank!!.toInt(),
                        group = getBGMIGroupForValue(gameInfo.getString("group")),
                        map = getBGMIMapForValue(gameInfo.getString("mode")),
                        playedAt = Date(game.startTimeStamp!!.toLong() - dateTimeDiff),
                    )
                } else {
                    SubmitBGMIGameMutation(
                        finalTier = getBGMILevelForValue(game.finalTier!!),
                        initialTier = getBGMILevelForValue(game.initialTier!!),
                        kills = if (game.kills == UNKNOWN) -1 else game.kills!!.toInt(), // default as -1, if the user has the individual kills send them else send -1
                        rank = game.rank!!.toInt(),
                        teamRank = getTeamRankForValue(game.teamRank),
                        group = getBGMIGroupForValue(gameInfo.getString("group")),
                        map = getBGMIMapForValue(gameInfo.getString("mode")),
                        playedAt = Date(game.startTimeStamp!!.toLong() - dateTimeDiff),
                        squadScoring = getSquadScoring(game.squadScoring)
                    )
                }

            logWithIdentifier(game.gameId) {
                it.setMessage("Game Submitted")
                it.addContext("game", gameSubmit)
                it.setCategory(LogCategory.API_CALL)
            }
            val response = apiClient.mutation(gameSubmit).execute()
            if (response.hasErrors()) throw (Exception(response.errors!!.first().message))
            return response.data!!.submitBgmiGame.gameResponse
        } else {
            val gameSubmit =
                if (getFFGroupForValue(gameInfo.getString("group")) == FfMaxGroups.solo) {
                    SubmitFFMaxGameMutation(
                        finalTier = getFFLevelForValue(game.finalTier!!),
                        initialTier = getFFLevelForValue(game.initialTier!!),
                        kills = game.kills!!.toInt(),
                        rank = game.rank!!.toInt(),
                        group = getFFGroupForValue(gameInfo.getString("group")),
                        map = getFFMapForValue(gameInfo.getString("mode")),
                        playedAt = Date()//Date(game.startTimeStamp!!.toLong() - dateTimeDiff),
                    )
                } else {
                    SubmitFFMaxGameMutation(
                        finalTier = getFFLevelForValue(game.finalTier!!),
                        initialTier = getFFLevelForValue(game.initialTier!!),
                        kills = if (game.kills == UNKNOWN) -1 else game.kills!!.toInt(), // default as -1, if the user has the individual kills send them else send -1
                        rank = game.rank!!.toInt(),
                        teamRank = getTeamRankForValue(game.teamRank),
                        group = getFFGroupForValue(gameInfo.getString("group")),
                        map = getFFMapForValue(gameInfo.getString("mode")),
                        playedAt = Date(game.startTimeStamp!!.toLong() - dateTimeDiff),
                        squadScoring = getSquadScoring(game.squadScoring)
                    )
                }
            logWithIdentifier(game.gameId) {
                it.setMessage("Game Submitted")
                it.addContext("game", gameSubmit)
                it.setCategory(LogCategory.API_CALL)
            }
            val response = apiClient.mutation(gameSubmit).execute()
            if (response.hasErrors()) throw (Exception(response.errors!!.first().message))
            return response.data!!.submitFfMaxGame.gameResponse
        }
    }


    suspend fun updateGameProfile(
        gameUserName: String?,
        apiClient: ApiClient,
        gameId: String? = null,
        verifyOnSuccess: Boolean = true,
        response: ((String?) -> Unit)? = null
    ) {
        val gameProfileId = when (StateMachine.machine.state) {
            is State.UnInitialized -> {
                (StateMachine.machine.state as State.UnInitialized).originalGameId ?: gameId
            }

            is UserDetails -> {
                (StateMachine.machine.state as UserDetails).unVerifiedUserDetails.originalGameId
                    ?: gameId
            }

            else -> {
                gameId
            }
        }
        if (gameProfileId == null) response?.invoke("Null profile id")
        gameProfileId?.let {
            lateinit var call: ApolloCall<UpdateGameProfileMutation.Data>
            try {
                if (MachineConstants.currentGame == SupportedGames.BGMI) {
                    call = apiClient.mutation(
                        UpdateGameProfileMutation(
                            eSports = ESports.BGMI,
                            bgmiProfileMetadataInput = Optional.presentIfNotNull(
                                BgmiProfileInput(
                                    username = Optional.presentIfNotNull(gameUserName),
                                    profileId = Optional.presentIfNotNull(gameProfileId)
                                )
                            )
                        )
                    )
                } else if (MachineConstants.currentGame == SupportedGames.FREEFIRE) {
                    call = apiClient.mutation(
                        UpdateGameProfileMutation(
                            eSports = ESports.FREEFIREMAX,
                            ffmaxProfileInput = Optional.presentIfNotNull(
                                FFMaxProfileInput(
                                    username = Optional.presentIfNotNull(gameUserName),
                                    profileId = Optional.presentIfNotNull(gameProfileId)
                                )
                            )
                        )
                    )
                }
                val result = call.execute()

                updateGameIdActive = if (result.hasErrors().not()) {
                    response?.invoke(null)
                    if (verifyOnSuccess)
                        StateMachine.machine.transition(
                            Event.VerifyUser(
                                gameProfileId = gameProfileId, gameCharId = gameUserName!!
                            )
                        )
                    1
                } else {
                    response?.invoke(result.errors?.first()?.message)

                    logWithIdentifier(gameId) {
                        it.setMessage("Updating ${MachineConstants.currentGame.gameName} character id failed with error")
                        it.addContext("error", result.errors?.first())
                        it.setCategory(LogCategory.ENGINE)
                    }
                    MachineMessageBroadcaster.invoke()
                        ?.showVerificationError("Not verified: ${result.errors?.first()}")
                    1
                }
            } catch (ex: Exception) {
                response?.invoke(ex.message)
                updateGameIdActive = 0
                logException(ex)
            }
        }

    }


    private suspend fun getScoring(apiClient: ApiClient): ApolloResponse<GetGameScoringQuery.Data> {
        if (scoring == null) scoring =
            apiClient.query(GetGameScoringQuery(MachineConstants.currentGame.eSport)).execute()
        return scoring!!
    }


    private suspend fun showGameError(
        serviceUIHelper: ServiceUIHelper?, gameFailureReason: GameFailureReason?, message: String?
    ) {
        withContext(Dispatchers.Main) {
            message?.let {
                showToast("Game Failed, $message!", force = true)
            }
            serviceUIHelper?.gameFailed(gameFailureReason, message)
        }
    }

    suspend fun getLeaderboard(
        tournamentId: Long?, pageNum: Int?, apiClient: ApiClient, tournamentType: String
    ): LeaderBoardScoring? {
        try {
            val scoringData = getScoring(apiClient)

            if (BgmiGroups.solo == getBGMIGroupForValue(tournamentType.uppercase())) {
                val list = arrayListOf<LeaderBoardElement>()

                var response = apiClient.query(
                    GetLeaderboardQuery(
                        tournamentId = tournamentId!!.toInt(),
                        page = Optional.presentIfNotNull(pageNum),
                        direction = Optional.presentIfNotNull(LeaderboardDirection.Next),
                    )
                ).execute()

                response.data!!.leaderboard.forEach {
                    list.add(
                        LeaderBoardElement(
                            behindBy = it.leaderboardRank.behindBy.toLong(),
                            matchesPlayed = it.leaderboardRank.details!!.gamesPlayed.toLong(),
                            score = it.leaderboardRank.score.toLong(),
                            name = it.leaderboardRank.user.leaderboardUser.username,
                            topGames = TopGames(gameResults = getTopGamesForSolo(it.leaderboardRank.details.top)),
                            rank = it.leaderboardRank.rank.toLong(),
                            myId = it.leaderboardRank.user.leaderboardUser.id.toString(),
                            myPhoto = it.leaderboardRank.user.leaderboardUser.image,
                            tournamentId = tournamentId.toInt()

                        )
                    )
                }
                return LeaderBoardScoring(
                    list, scoringData.data!!.scoring, tournamentId.toInt()
                )


            } else {
                val list = arrayListOf<LeaderBoardElement>()

                var response = apiClient.query(
                    GetSquadLeaderboardQuery(
                        tournamentId = tournamentId!!.toInt(),
                        page = Optional.presentIfNotNull(pageNum),
                        direction = Optional.presentIfNotNull(LeaderboardDirection.Next),
                    )
                ).execute()

                response.data!!.squadLeaderboard.forEach {
                    list.add(
                        LeaderBoardElement(
                            behindBy = it.squadLeaderboard.behindBy.toLong(),
                            matchesPlayed = it.squadLeaderboard.details!!.gamesPlayed.toLong(),
                            score = it.squadLeaderboard.score.toLong(),
                            name = it.squadLeaderboard.squad!!.leaderboardSquad.name,
                            topGames = TopGames(gameResults = getTopGamesForSquad(if (it.squadLeaderboard.details.top.isNotEmpty()) it.squadLeaderboard.details.top.first() else null)),
                            rank = it.squadLeaderboard.rank.toLong(),
                            myId = it.squadLeaderboard.squad.leaderboardSquad.id.toString(),
                            tournamentId = tournamentId.toInt()

                        )
                    )
                }
                return LeaderBoardScoring(
                    list, scoringData.data!!.scoring, tournamentId.toInt()
                )
            }
        } catch (e: Throwable) {
            FirebaseCrashlytics.getInstance().log(e.toString())
            e.printStackTrace()
        }

        return null
    }

    suspend fun getMyJoinTournaments(
        eSports: ESports, apiClient: ApiClient
    ): List<ServerTournamentElement>? {
        arrayListOf<LeaderBoardElement>()
        try {
            val serverTournaments = arrayListOf<ServerTournamentElement>()


            val activeTournaments = apiClient.query(
                GetMyActiveTournamentQuery(
                    eSports
                )
            ).execute()
            activeTournaments.data?.let { data ->
                data.active.forEach { active ->
                    active.tournaments.forEach { tournament ->
                        if (tournament.userTournament.joinedAt != null) serverTournaments.add(
                            ServerTournamentElement.getServerTournamentElement(
                                tournament.userTournament
                            )
                        )

                    }
                }
            }
            return serverTournaments
        } catch (e: Throwable) {
            FirebaseCrashlytics.getInstance().log(e.toString())
            e.printStackTrace()
        }

        return null
    }

    private fun getTopGamesForSolo(topGames: List<LeaderboardRank.Top?>): List<GameResult> {
        val gameResult = arrayListOf<GameResult>()
        topGames.forEach {
            gameResult.add(
                GameResult(
                    rank = it!!.rank, score = it.score.toInt()
                )
            )
        }
        return gameResult
    }

    private fun getTopGamesForSquad(topGames: List<SquadLeaderboard.Top?>?): List<GameResult> {
        val gameResult = arrayListOf<GameResult>()
        topGames?.forEach {
            gameResult.add(
                GameResult(
                    rank = it!!.teamRank!!, score = it.score.toInt()
                )
            )
        }
        return gameResult
    }


    private fun checkValidGame(game: Game): GameFailureReason? {
        if (game.gameInfo == null || game.gameInfo == UNKNOWN) return GameFailureReason.MISSING_GAME_INFO

        val gameInfo: GameInfo = Json.decodeFromString(game.gameInfo!!)

        if (gameInfo.group == BgmiGroups.solo.rawValue || gameInfo.group == FfMaxGroups.solo.rawValue) {
            // for solo matches, kills is required value
            if (game.kills == UNKNOWN || game.kills == "-1") return GameFailureReason.MISSING_KILLS
        } /*else {
            // for non solo matches, if the user died first we will not have his kills, so it will be submitted without kills.
            // For non solo teamRank must be present
            if (game.teamRank in arrayListOf(null, UNKNOWN) && game.squadScoring !in arrayListOf(
                    null,
                    UNKNOWN
                )
            )
                return GameFailureReason.MISSING_TEAM_RANK
            if (game.squadScoring in arrayListOf(null, UNKNOWN) && game.teamRank !in arrayListOf(
                    null,
                    UNKNOWN
                )
            )
                return GameFailureReason.MISSING_KILLS
        }*/

        if (game.rank == UNKNOWN) return GameFailureReason.MISSING_RANK

        if (game.initialTier == UNKNOWN) return GameFailureReason.MISSING_GROUP

        if (game.finalTier == UNKNOWN) return GameFailureReason.MISSING_TIER

        if (gameInfo.group == UNKNOWN) return GameFailureReason.MISSING_GROUP

        if (gameInfo.mode == UNKNOWN) return GameFailureReason.MISSING_MAP

        if (gameInfo.type == UNKNOWN || (gameInfo.type != "Classic" && gameInfo.type != "BR RANKED")) return GameFailureReason.NON_CLASSIC

        return null
    }

    @Serializable
    enum class GameFailureReason {
        GAME_NOT_COMPLETED, MISSING_RANK, MISSING_TEAM_RANK, MISSING_KILLS, MISSING_TIER, MISSING_GAME_INFO, MISSING_GROUP, MISSING_MAP, NON_CLASSIC, SOME_ERROR_OCCURRED, COULD_NOT_COMPLETE_SUBMIT_REQUEST, SOME_ERROR_FROM_REMOTE, GAME_ID_NULL, GAME_SUCCESSFUL, CLOSED_SERVICE, SWITCHED_TO_GB, SWITCHED_TO_BGMI, STARTED_WITHOUT_VERIFYING, STARTED_WITHOUT_VERIFYING_VARIANT, ENDED_WITHOUT_VERIFYING, MISSING_AUTOML_IMAGES, USER_NOT_VERIFIED_ON_GAME_SUBMIT, CLICKED_CANCEL_ON_RETRY, USER_UNVERIFIED_LOGIN, AUTO_ML_CALL_FAILED, AUTOML_TASK_WORKER_FAILED, NO_TOURNAMENTS_IN_RESPONSE, OTHER;

        fun getMessage(): String {
            return when (this) {
                MISSING_RANK -> "We weren't able to fetch your rank. Try to stay on the rank and result screens for a longer time."
                GAME_NOT_COMPLETED -> "We detected that you didn't complete your game."
                MISSING_TEAM_RANK -> "We weren't able to fetch your rank. Try to stay on the rank and result screens for a longer time."
                MISSING_KILLS -> "We weren't able to fetch your rank. Try to stay on the rank and result screens for a longer time."
                MISSING_TIER -> "We weren't able to fetch your tier. Try to stay on the rating screen for a longer time."
                MISSING_GAME_INFO -> "We weren't able to fetch the game information. Try to stay on the rating screen for a longer time."
                MISSING_GROUP -> "We weren't able to fetch the game information. Try to stay on the rating screen for a longer time."
                MISSING_MAP -> "We weren't able to fetch the game information. Try to stay on the rating screen for a longer time."
                NON_CLASSIC -> if (MachineConstants.currentGame == SupportedGames.FREEFIRE) "We support only BR Ranked matches. We detected that you played a Battle Royale match." else "We support only ranked classic matches. We detected that you played a non-classic match."
                SOME_ERROR_OCCURRED -> "Some error occurred"
                COULD_NOT_COMPLETE_SUBMIT_REQUEST -> ""
                SOME_ERROR_FROM_REMOTE -> ""
                GAME_ID_NULL -> ""
                GAME_SUCCESSFUL -> ""
                CLOSED_SERVICE -> ""
                SWITCHED_TO_GB -> ""
                SWITCHED_TO_BGMI -> ""
                STARTED_WITHOUT_VERIFYING -> "You are NOT verified,\n please verify your profile to record your games in the leaderboard!.\n"
                STARTED_WITHOUT_VERIFYING_VARIANT -> "You have not verified your Gamerboard Profile. This game and it’s score won’t be counted.\n"
                ENDED_WITHOUT_VERIFYING -> "Sadly, you have not verified your Gamerboard Profile so this game and it’s score won’t be counted.\n" +
                        "Make sure you verify your profile before the next Game\n"

                MISSING_AUTOML_IMAGES -> "We were not able to fetch the screens. Try being on the results screen for more time." +
                        "\nAlso make sure you are playing only ${if (MachineConstants.currentGame == SupportedGames.BGMI) "BGMI classic ranked" else "Freefire Max BR-Ranked"} matches"

                USER_NOT_VERIFIED_ON_GAME_SUBMIT -> "Your profile wasn't verified before game start. Please verify your profile before playing the game."
                CLICKED_CANCEL_ON_RETRY -> ""
                USER_UNVERIFIED_LOGIN -> "Your profile wasn't verified before game start. Please verify your profile before playing the game."
                AUTO_ML_CALL_FAILED -> "There was some error contacting server. Please check the internet connection."
                AUTOML_TASK_WORKER_FAILED -> "There was some error contacting server. Please check the internet connection."
                NO_TOURNAMENTS_IN_RESPONSE -> "Game submission failed. Please join a tournament before playing."
                OTHER -> "Game submission failed.\nStay on the results screen for a more time so that they are properly recorded.\nPlay the game after verifying your profile." +
                        "\nPlay only ${if (MachineConstants.currentGame == SupportedGames.BGMI) "BGMI classic ranked" else "Freefire Max BR-Ranked"} matches"
            }
        }
    }

    @Serializable
    data class MachineGameEndLog(
        val reason: GameFailureReason, val game: Game?, val gameId: String?, val message: String
    )

    private fun getBGMILevelForValue(level: String): BgmiLevels {
        when (level.uppercase()) {
            "BRONZE V" -> return BgmiLevels.BRONZE_FIVE
            "BRONZE IV" -> return BgmiLevels.BRONZE_FOUR
            "BRONZE III" -> return BgmiLevels.BRONZE_THREE
            "BRONZE II" -> return BgmiLevels.BRONZE_TWO
            "BRONZE I" -> return BgmiLevels.BRONZE_TWO
            "SILVER V" -> return BgmiLevels.SILVER_FIVE
            "SILVER IV" -> return BgmiLevels.SILVER_FOUR
            "SILVER III" -> return BgmiLevels.SILVER_THREE
            "SILVER II" -> return BgmiLevels.SILVER_TWO
            "SILVER I" -> return BgmiLevels.SILVER_ONE
            "GOLD V" -> return BgmiLevels.GOLD_FIVE
            "GOLD IV" -> return BgmiLevels.GOLD_FOUR
            "GOLD III" -> return BgmiLevels.GOLD_THREE
            "GOLD II" -> return BgmiLevels.GOLD_TWO
            "GOLD I" -> return BgmiLevels.GOLD_ONE
            "PLATINUM V" -> return BgmiLevels.PLATINUM_FIVE
            "PLATINUM IV" -> return BgmiLevels.PLATINUM_FOUR
            "PLATINUM III" -> return BgmiLevels.PLATINUM_THREE
            "PLATINUM II" -> return BgmiLevels.PLATINUM_TWO
            "PLATINUM I" -> return BgmiLevels.PLATINUM_ONE
            "DIAMOND V" -> return BgmiLevels.DIAMOND_FIVE
            "DIAMOND IV" -> return BgmiLevels.DIAMOND_FOUR
            "DIAMOND III" -> return BgmiLevels.DIAMOND_THREE
            "DIAMOND II" -> return BgmiLevels.DIAMOND_TWO
            "DIAMOND I" -> return BgmiLevels.DIAMOND_ONE
            "CROWN V" -> return BgmiLevels.CROWN_FIVE
            "CROWN IV" -> return BgmiLevels.CROWN_FOUR
            "CROWN III" -> return BgmiLevels.CROWN_THREE
            "CROWN II" -> return BgmiLevels.CROWN_TWO
            "CROWN I" -> return BgmiLevels.CROWN_ONE
            "ACE" -> return BgmiLevels.ACE
            "ACE MASTER" -> return BgmiLevels.ACE_MASTER
            "ACE DOMINATOR" -> return BgmiLevels.ACE_DOMINATOR
            "CONQUEROR" -> return BgmiLevels.CONQUEROR
        }
        return BgmiLevels.BRONZE_FIVE
    }

    private fun getBGMIMapForValue(map: String): BgmiMaps {
        when (map.uppercase()) {
            "ERANGEL" -> return BgmiMaps.erangel
            "LIVIK" -> return BgmiMaps.livik
            "SANHOK" -> return BgmiMaps.sanhok
            "KARAKIN" -> return BgmiMaps.karakin
            "VIKENDI" -> return BgmiMaps.vikendi
            "NUSA" -> return BgmiMaps.nusa
        }
        return BgmiMaps.erangel
    }

    private fun getBGMIGroupForValue(group: String): BgmiGroups {
        when (group.uppercase()) {
            "SOLO" -> return BgmiGroups.solo
            "DUO" -> return BgmiGroups.duo
            "SQUAD" -> return BgmiGroups.squad
        }
        return BgmiGroups.solo
    }

    private fun getFFLevelForValue(level: String): FfMaxLevels {
        when (level.uppercase()) {
            "BRONZE I" -> return FfMaxLevels.BRONZE_ONE
            "BRONZE II" -> return FfMaxLevels.BRONZE_TWO
            "BRONZE III" -> return FfMaxLevels.BRONZE_THREE
            "SILVER I" -> return FfMaxLevels.SILVER_ONE
            "SILVER II" -> return FfMaxLevels.SILVER_TWO
            "SILVER III" -> return FfMaxLevels.SILVER_THREE
            "GOLD I" -> return FfMaxLevels.GOLD_ONE
            "GOLD II" -> return FfMaxLevels.GOLD_TWO
            "GOLD III" -> return FfMaxLevels.GOLD_THREE
            "GOLD IV" -> return FfMaxLevels.GOLD_FOUR
            "PLATINUM I" -> return FfMaxLevels.PLATINUM_ONE
            "PLATINUM II" -> return FfMaxLevels.PLATINUM_TWO
            "PLATINUM III" -> return FfMaxLevels.PLATINUM_THREE
            "PLATINUM IV" -> return FfMaxLevels.PLATINUM_FOUR
            "DIAMOND I" -> return FfMaxLevels.DIAMOND_ONE
            "DIAMOND II" -> return FfMaxLevels.DIAMOND_TWO
            "DIAMOND III" -> return FfMaxLevels.DIAMOND_THREE
            "DIAMOND IV" -> return FfMaxLevels.DIAMOND_FOUR
            "HEROIC" -> return FfMaxLevels.HEROIC
            "MASTER" -> return FfMaxLevels.MASTER
            "GRANDMASTER I" -> return FfMaxLevels.GRANDMASTER_ONE
            "GRANDMASTER II" -> return FfMaxLevels.GRANDMASTER_TWO
            "GRANDMASTER III" -> return FfMaxLevels.GRANDMASTER_THREE
            "GRANDMASTER IV" -> return FfMaxLevels.GRANDMASTER_FOUR
            "GRANDMASTER V" -> return FfMaxLevels.GRANDMASTER_FIVE
            "GRANDMASTER VI" -> return FfMaxLevels.GRANDMASTER_SIX
        }
        return FfMaxLevels.BRONZE_ONE
    }

    private fun getFFMapForValue(map: String): FfMaxMaps {
        when (map.uppercase()) {
            "ALPINE" -> return FfMaxMaps.alpine
            "BERMUDA" -> return FfMaxMaps.bermuda
            "NEXTERRA" -> return FfMaxMaps.nexterra
            "PURGATORY" -> return FfMaxMaps.purgatory
        }
        return FfMaxMaps.alpine
    }

    private fun getFFGroupForValue(group: String): FfMaxGroups {
        when (group.uppercase()) {
            "SOLO" -> return FfMaxGroups.solo
            "DUO" -> return FfMaxGroups.duo
            "SQUAD" -> return FfMaxGroups.squad
        }
        return FfMaxGroups.solo
    }

    private fun getSquadScoring(scoring: String?): Optional<List<SquadMemberGameInfo>?> {
        if (scoring in arrayListOf(null, UNKNOWN)) return Optional.Absent
        val squadPlayersArray =
            MachineConstants.machineInputValidator.getSquadScoringArray(scoring!!)
        val scoringList: MutableList<SquadMemberGameInfo> = mutableListOf()
        for (i in 0 until squadPlayersArray.size) {
            val squadPlayer = squadPlayersArray[i]
            val test = gson.fromJson(squadPlayer.toString(), JsonObject::class.java)
            if (test.validatePlayerScoring()) {
                val userName = test["username"].asString
                val kills = test["kills"].asInt
                scoringList.add(SquadMemberGameInfo(username = userName, kills = kills))
            }
        }
        return Optional.presentIfNotNull(scoringList)
    }

    private fun getTeamRankForValue(teamRank: String?): Optional<Int?> {
        return if (teamRank in arrayListOf(
                UNKNOWN, null
            )
        ) Optional.Absent else Optional.presentIfNotNull(teamRank!!.toInt())
    }

}

class GameSubmitApi(val ctx: Context) {
    fun submitGame(gameId: String, retry: Int) {
        val data = Data.Builder()
        data.putString("gameId", gameId)
        data.putInt("retry", retry)

        val internetConstants =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        val submitGameRequest =
            OneTimeWorkRequestBuilder<SubmitGameWorker>().setInputData(data.build())
                .setConstraints(internetConstants).build()
        WorkManager.getInstance(ctx.applicationContext).enqueueUniqueWork(
            "com.gamerboard.live-submit-game-api",
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            submitGameRequest
        )
    }
}

class SubmitGameWorker(var ctx: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(ctx, workerParameters), KoinComponent {
    private val appDatabase: AppDatabase by inject()
    private lateinit var mBackgroundChannel: MethodChannel
    private val logHelper: LogHelper by inject(LogHelper::class.java)
    val apiClient = ApiClient(ctx)
    override suspend fun doWork(): Result {
        val gameId = inputData.getString("gameId")
        try {
            withContext(Dispatchers.Main) {
                val retry = inputData.getInt("retry", 4)

                mBackgroundChannel = MethodChannel(
                    FlutterEngineHolder.flutterEngine!!.dartExecutor.binaryMessenger,
                    PlatformChannels.BG_PLUGIN_SERVICE
                )


                logWithIdentifier(gameId, "Broadcast gameEndedBroadcast for gameId ${gameId}")

                mBackgroundChannel.setMethodCallHandler { call, result ->
                    when (call.method) {
                        "init_isolate" -> {
                            result.success(
                                mapOf(
                                    "api_endpoint" to BuildConfig.API_ENDPOINT,
                                    "auth_token" to ((ctx.applicationContext) as GamerboardApp).prefsHelper.getString(
                                        SharedPreferenceKeys.AUTH_TOKEN
                                    ),
                                    "build_version_code" to BuildConfig.VERSION_CODE
                                )
                            )
                        }
                    }
                }


                withContext(Dispatchers.IO) {
                    GameRepository.pushGameOrProfile(
                        gameId,
                        ctx,
                        apiClient,
                        null,
                        retry = retry,
                        emptyList() // exceeds max retries, prevent endless loops
                    )
                }

                return@withContext Result.success()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()

            logWithIdentifier(gameId) {
                it.setMessage("Submit game, work manager task failed")
                it.addContext("error", e.message)
                it.addContext("stack_trace", e.stackTraceToString())
                it.setCategory(LogCategory.ENGINE)
            }

            logWithIdentifier(gameId) {
                it.setMessage("Could not complete game submit request")
                it.addContext(
                    "reason",
                    GameRepository.GameFailureReason.COULD_NOT_COMPLETE_SUBMIT_REQUEST
                )
                it.addContext("error", e.message)
                it.addContext("stack_trace", e.stackTraceToString())
                it.setCategory(LogCategory.ICM)
            }
            logHelper.completeLogging()
        }

        return Result.success()
    }
}


private fun getTime(): Long {
    try {
        FirebaseCrashlytics.getInstance().log("getTime()")
        var urlConnection: HttpURLConnection? = null
        val result = StringBuilder()
        val url = URL("https://worldtimeapi.org/api/timezone/Asia/Kolkata")
        urlConnection = url.openConnection() as (HttpURLConnection)
        val code = urlConnection.responseCode
        if (code == 200) {
            val inputS = BufferedInputStream(urlConnection.inputStream)
            val bufferedReader = BufferedReader(InputStreamReader(inputS))
            var line = ""
            while ((bufferedReader.readLine()?.also { line = it }) != null) result.append(line)
            inputS.close()
        }
        urlConnection.disconnect()
        return JSONObject(result.toString()).getLong("unixtime") * 1000
    } catch (e: Exception) {
        FirebaseCrashlytics.getInstance().log(e.toString())
        e.printStackTrace()
        return System.currentTimeMillis()
    }
}

interface ReTryGameSubmit {
    fun retry()
    fun cancel()
}


fun GameResponse.Game.getInitialTier(): String {
    return if (this.metadata.onBgmiMetadata != null) {
        BgmiLevels.values()[this.metadata.onBgmiMetadata.initialTier].name
    } else if (this.metadata.onFfMaxMetadata != null) {
        FfMaxLevels.values()[this.metadata.onFfMaxMetadata.initialTier].name
    } else BgmiLevels.UNKNOWN__.name
}

fun GameResponse.Game.getFinalTier(): String {
    return if (this.metadata.onBgmiMetadata != null) {
        BgmiLevels.values()[this.metadata.onBgmiMetadata.finalTier].name
    } else if (this.metadata.onFfMaxMetadata != null) {
        FfMaxLevels.values()[this.metadata.onFfMaxMetadata.finalTier].name
    } else BgmiLevels.UNKNOWN__.name
}

fun GameResponse.Game.getGroup(): String {
    return if (this.metadata.onBgmiMetadata != null) {
        this.metadata.onBgmiMetadata.bgmiGroup.name
    } else if (this.metadata.onFfMaxMetadata != null) {
        this.metadata.onFfMaxMetadata.ffGroup.name
    } else
        BgmiLevels.UNKNOWN__.name
}

fun GameResponse.Game.getMap(): String {
    return if (this.metadata.onBgmiMetadata != null) {
        this.metadata.onBgmiMetadata.bgmiMap.name
    } else if (this.metadata.onFfMaxMetadata != null) {
        this.metadata.onFfMaxMetadata.ffMap.name
    } else
        BgmiLevels.UNKNOWN__.name
}

fun GameResponse.Game.getKills(): Int {
    return if (this.metadata.onBgmiMetadata != null) {
        this.metadata.onBgmiMetadata.kills
    } else if (this.metadata.onFfMaxMetadata != null) {
        this.metadata.onFfMaxMetadata.kills
    } else 0
}
