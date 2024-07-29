package com.gamerboard.live.mock

import com.gamerboard.live.GetGameScoringQuery
import com.gamerboard.live.fragment.GameResponse
import com.gamerboard.live.fragment.LeaderboardUser
import com.gamerboard.live.models.CustomGameResponse
import com.gamerboard.live.type.BgmiGroups
import com.gamerboard.live.type.BgmiLevels
import com.gamerboard.live.type.BgmiMaps
import com.gamerboard.live.type.ESports

/**
 * Created by saurabh.lahoti on 26/05/22
 */
object MockData {
    fun getGameResponse(): CustomGameResponse {
        return CustomGameResponse(serverGame = getServerGame(), scoring = getScoring())
    }

    private fun getScoring(): GetGameScoringQuery.Data {
        return GetGameScoringQuery.Data(
            scoring = GetGameScoringQuery.Scoring(
                killPoints = 1,
                rankPoints = listOf(
                    GetGameScoringQuery.RankPoint(points = 4, rank = 7),
                    GetGameScoringQuery.RankPoint(points = 3, rank = 8),
                    GetGameScoringQuery.RankPoint(points = 2, rank = 9),
                    GetGameScoringQuery.RankPoint(points = 1, rank = 10)
                )
            )
        )
    }

    private fun getServerGame(): GameResponse {
        return GameResponse(
            game = GameResponse.Game(
                id = 1, eSport = ESports.BGMI,
                rank = 1,
                score = 20.0,
                userId = 200,
                teamRank = 1,
                metadata = GameResponse.Metadata(
                    __typename = "GameResponse.OnBgmiMetadata",
                    onBgmiMetadata = GameResponse.OnBgmiMetadata(
                        initialTier = BgmiLevels.SILVER_THREE.ordinal,
                        finalTier = BgmiLevels.SILVER_THREE.ordinal,
                        kills = 1,
                        bgmiGroup = BgmiGroups.solo,
                        bgmiMap = BgmiMaps.karakin
                    ),
                    onFfMaxMetadata = null
                )
            ),
            tournaments = listOf(
                /*    SubmitBGMIGameMutation.Tournament(
                        isAdded = true,
                        isTop = true,
                        exclusionReason = null,
                        tournament = SubmitBGMIGameMutation.Tournament1(
                            name = "Mocked Tournament"
                        ),
                        squadScores = listOf(
                            SubmitBGMIGameMutation.SquadScore(
                                kills = 3,
                                SubmitBGMIGameMutation.User(
                                    username = "Saurabh",
                                    image = "https://i.pravatar.cc/150?img=3"
                                )
                            ),
                            SubmitBGMIGameMutation.SquadScore(
                                kills = 4,
                                SubmitBGMIGameMutation.User(
                                    username = "Sam",
                                    image = "https://i.pravatar.cc/150?img=3"
                                )
                            ),
                            SubmitBGMIGameMutation.SquadScore(
                                kills = 4,
                                SubmitBGMIGameMutation.User(
                                    username = "Ravi",
                                    image = "https://i.pravatar.cc/150?img=3"
                                )
                            )
                        )
                    ),*/
                GameResponse.Tournament(
                    isAdded = true,
                    isTop = false,
                    exclusionReason = "Game not qualified to be added",
                    tournament = GameResponse.Tournament1(
                        id = 1,
                        name = "Tournament 2",

                        ),

                    submissionState =
                    listOf(
                        GameResponse.SubmissionState(
                            1,

                            true,
                            GameResponse.User(
                                leaderboardUser = LeaderboardUser(
                                    id = 1,
                                    name = "demo",
                                    image = "https://picsum.photos/200",
                                    phone = "",
                                    username = "demo"
                                ),
                                __typename = ""
                            )
                        ),
                        GameResponse.SubmissionState(
                            1,

                            true,
                            GameResponse.User(
                                leaderboardUser = LeaderboardUser(
                                    id = 1,
                                    name = "demo",
                                    image = "https://picsum.photos/200",
                                    phone = "",
                                    username = "demo"
                                ),
                                __typename = ""
                            )
                        ),
                        GameResponse.SubmissionState(
                            1,

                            true,
                            GameResponse.User(
                                leaderboardUser = LeaderboardUser(
                                    id = 1,
                                    name = "demo",
                                    image = "https://picsum.photos/200",
                                    phone = "",
                                    username = "demo"
                                ),
                                __typename = ""
                            )
                        ),
                        GameResponse.SubmissionState(
                            2,

                            true,
                            GameResponse.User(
                                leaderboardUser = LeaderboardUser(
                                    id = 1,
                                    name = "demo",
                                    image = "https://picsum.photos/200",
                                    phone = "",
                                    username = "demo"
                                ),
                                __typename = ""
                            )
                        )

                    ),

                    squadScores = listOf(
                        GameResponse.SquadScore(
                            kills = 5,
                            user = GameResponse.User1(
                                username = "demo",
                                image = "https://picsum.photos/200",
                            )
                        ),
                        GameResponse.SquadScore(
                            kills = 5,
                            user = GameResponse.User1(
                                username = "demo",
                                image = "https://picsum.photos/200",
                            )
                        ),
                        GameResponse.SquadScore(
                            kills = 5,
                            user = GameResponse.User1(
                                username = "demo",
                                image = "https://picsum.photos/200",
                            )
                        ),
                        GameResponse.SquadScore(
                            kills = 5,
                            user = GameResponse.User1(
                                username = "demo",
                                image = "https://picsum.photos/200",
                            )
                        ),
                    )
                ),
                GameResponse.Tournament(
                    isAdded = false,
                    isTop = false,
                    exclusionReason = "User not qualified to be added",
                    tournament = GameResponse.Tournament1(
                        id = 2,
                        name = "Tournament 3"
                    ),
                    submissionState = listOf(
                        GameResponse.SubmissionState(
                            1,

                            false,
                            GameResponse.User(
                                leaderboardUser = LeaderboardUser(
                                    id = 1,
                                    name = "demo",
                                    image = "https://picsum.photos/200",
                                    phone = "",
                                    username = "demo"
                                ),
                                __typename = ""
                            )
                        ),
                        GameResponse.SubmissionState(
                            1,

                            true,
                            GameResponse.User(
                                leaderboardUser = LeaderboardUser(
                                    id = 1,
                                    name = "demo",
                                    image = "https://picsum.photos/200",
                                    phone = "",
                                    username = "demo"
                                ),
                                __typename = ""
                            )
                        ),
                        GameResponse.SubmissionState(
                            1,

                            true,
                            GameResponse.User(
                                leaderboardUser = LeaderboardUser(
                                    id = 1,
                                    name = "demo",
                                    image = "https://picsum.photos/200",
                                    phone = "",
                                    username = "demo"
                                ),
                                __typename = ""
                            )
                        ),
                        GameResponse.SubmissionState(
                            2,

                            true,
                            GameResponse.User(
                                leaderboardUser = LeaderboardUser(
                                    id = 1,
                                    name = "demo",
                                    image = "https://picsum.photos/200",
                                    phone = "",
                                    username = "demo"
                                ),
                                __typename = ""
                            )
                        )

                    ),

                    squadScores = listOf(
                        GameResponse.SquadScore(
                            kills = 5,
                            user = GameResponse.User1(
                                username = "demo",
                                image = "https://picsum.photos/200",
                            )
                        ),
                        GameResponse.SquadScore(
                            kills = 5,
                            user = GameResponse.User1(
                                username = "demo",
                                image = "https://picsum.photos/200",
                            )
                        ),
                        GameResponse.SquadScore(
                            kills = 5,
                            user = GameResponse.User1(
                                username = "demo",
                                image = "https://picsum.photos/200",
                            )
                        ),
                        GameResponse.SquadScore(
                            kills = 5,
                            user = GameResponse.User1(
                                username = "demo",
                                image = "https://picsum.photos/200",
                            )
                        ),
                    )

                )
            )
        )
    }
}