/**/package com.gamerboard.live.gamestatemachine.bgmi

import androidx.annotation.Keep
import com.gamerboard.live.gamestatemachine.games.LabelUtils
import com.gamerboard.live.gamestatemachine.games.LabelUtils.buildGameInfo
import com.gamerboard.live.gamestatemachine.games.bgmi.BGMIConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.gamestatemachine.stateMachine.MachineResult
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachineStringConstants.UNKNOWN
import com.gamerboard.live.models.ImageResultJson
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.models.TFResultFlat
import com.gamerboard.live.models.db.Game
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


@Keep
@Serializable
data class OCROld(
    val label: String,
    val box: FloatArray,
    var language: String
)

@Keep
@Serializable
data class TFResultOld(
    val label: Float,
    val confidence: Float,
    val box: FloatArray,
    var ocr: ArrayList<OCROld>
)

@Keep
@Serializable
data class ImageResultJsonOld(
    val ttLabelling: Long,
    val ttOCR: Long,
    val resolution: ResolutionOld,
    val epochTimestamp: Long,
    val url: String,
    var labels: ArrayList<TFResultOld>,
    var verifiedProfile: Boolean = false
)

@Keep
@Serializable
data class ResolutionOld(val width: Int, val height: Int)


// Helper methods to map the old object JSON to new Object's JSON.
// This is need whenever a new field is added to the saved JSONs,
// You can also update the previous JSONs :).

fun flattenResultJsonOld(input: ImageResultJsonOld): ImageResultJsonFlat {
    val resultJsonFlat = ImageResultJsonFlat(
        epochTimestamp = input.epochTimestamp,
        fileName = input.url,
        labels = arrayListOf()
    )

    for (label in input.labels) {
        var ocrText = ""
        for (text in label.ocr) {
            ocrText += text.label
        }
        resultJsonFlat.labels.add(
            TFResultFlat(
                label = label.label.toInt(),
                confidence = label.confidence,
                ocr = ocrText,
                box = label.box,
                resolution = Pair(1280, 720)
            )
        )
    }
    return resultJsonFlat
}

fun String.obj(): ImageResultJsonFlat {
    return flattenResultJsonOld(Json.decodeFromString(this) as ImageResultJsonOld)
}

fun String.objNew(): ImageResultJsonFlat {
    return LabelUtils.flattenResultJson(Json.decodeFromString(this) as ImageResultJson)
}


// Test users and games with dummy data.
object TestUser1 {
    const val id: String = "pratyushtiwari"
    const val level: String = "Player Lv. 22"
    const val idNumeric: String = "123456798"
    const val initialTier = "Gold VI"
    const val finalTier = "old III"

}

object TestUser2 {
    const val id: String = "etika"
    const val level: String = "Player Lv. 22"
    const val idNumeric: String = "1234568983"
    const val initialTier = "old III"
    const val finalTier = "Gold VI"
}

val TestGame1 = Game(
    userId = TestUser1.id,
    valid = true,
    rank = "74/99",
    gameInfo = "Classic (TPP) solo-Erangel",
    kills = "finishes 1",
    teamRank = "Team Rank # 23",
    startTimeStamp = "Un-Known",
    endTimestamp = "Un-Known",
    gameId = "Un-Known",
    initialTier = "Gold VI",
    finalTier = "Gold III",
    metaInfoJson = "Un-Known"
)
val TestGame2 = Game(
    userId = TestUser1.id,
    valid = true,
    rank = "34 /99",
    gameInfo = "Classic (TPP) solo-Erangel",
    kills = "finishes 1",
    teamRank = "Team Rank # 45",
    startTimeStamp = "Un-Known",
    endTimestamp = "Un-known",
    gameId = "Un-Known",
    initialTier = "Gold III",
    finalTier = "Gold VI",
    metaInfoJson = "Un-Known"
)


fun gameBuilder(
    id: Int = 0, userId: String = "1",
    valid: Boolean = true,
    rank: String = UNKNOWN,
    gameInfo: String = UNKNOWN,
    kills: String = UNKNOWN,
    teamRank: String = UNKNOWN,
    startTimeStamp: String = "0",
    endTimeStamp: String = "1",
    gameId: String = "0",
    initialTier: String = UNKNOWN,
    finalTier: String = UNKNOWN,
    metaInfoJson: String = UNKNOWN,
    synced: Int = 0,
): Game {
    return Game(
        id = id,
        userId = userId,
        valid = valid,
        rank = rank,
        gameInfo = gameInfo,
        kills = kills,
        teamRank = teamRank,
        startTimeStamp = startTimeStamp,
        endTimestamp = endTimeStamp,
        gameId = gameId,
        initialTier = initialTier,
        finalTier = finalTier,
        metaInfoJson = metaInfoJson,
        synced = synced
    )
}


// Result object mocks, these are returned from the validators as a source of truth to the machine with acceptance info and the data extracted

val resultObjectWith_Start: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()
    .setStart()

val resultObjectWith_InGame: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()
    .setInGame()

val resultObjectWith_Login: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()
    .setLogin()

val resultObjectWith_Profile: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()

val resultObjectWith_ProfileId: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()
    .setId(TestUser1.idNumeric)

val resultObjectWith_IdAndLevel: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()
    .setId(TestUser1.idNumeric)
    .setLevel(TestUser1.level)

val resultObjectWith_Waiting: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()
    .setWaiting()

// Below are the mocks of JSON's and their builders to provide test inputs for the validators.

// See `obj()` method above, that creates the Objects from the Json by decoding them.
/* fun String.obj(): ImageResultJsonFlat {
   return flattenResultJsonOld(Json.decodeFromString(this) as ImageResultJsonOld)
}*/


// JSON are begin saved from
/*
Check out `saveResult` method in ImageProcessor.kt
    private fun saveResult(bitmap: Bitmap, result: ImageResultJson, fileId: Long)

    Uncomment it and save the result object's JSON in a file.
*/


fun resultScreenResultBuilder(
    kills: String? = TestGame1.kills,
    gameInfo: String? = TestGame1.gameInfo,
    rank: String? = TestGame1.rank
): MachineResult.Builder {
    return MachineResult.Builder()
        .setAccepted()
        .setRank(rank!!)
        .setKill(kills!!)
        .setGameInfo(
            MachineConstants.machineInputValidator.validateGameInfo(gameInfo!!).buildGameInfo()
        )
}

val USER1_GAME1_RANK_RATINGS_GAME_INFO_RAW: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()
    .setRank(TestGame1.rank!!)
    .setInitialTier(TestGame1.initialTier!!)
    .setFinalTier(TestGame1.finalTier!!)
    .setGameInfo(
        MachineConstants.machineInputValidator.validateGameInfo(TestGame1.gameInfo!!)
            .buildGameInfo()
    )

val USER1_GAME2_RANK_RATINGS_GAME_INFO_RAW: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()
    .setRank(TestGame2.rank!!)
    .setInitialTier(TestGame2.initialTier!!)
    .setFinalTier(TestGame2.finalTier!!)
    .setGameInfo(
        MachineConstants.machineInputValidator.validateGameInfo(TestGame2.gameInfo!!)
            .buildGameInfo()
    )


val USER1_GAME1_RANK_KILLS_GAME_INFO_RAW: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()
    .setRank(TestGame1.rank!!)
    .setKill(TestGame1.kills!!)
    .setInitialTier(TestGame2.initialTier!!)
    .setFinalTier(TestGame2.finalTier!!)
    .setGameInfo(
        MachineConstants.machineInputValidator.validateGameInfo(TestGame2.gameInfo!!)
            .buildGameInfo()
    )

val USER1_GAME2_RANK_and_TEAM_RANK_and_KILLS_and_GAME_INFO_RAW: MachineResult.Builder =
    MachineResult.Builder()
        .setAccepted()
        .setRank(TestGame2.rank!!)
        //.setTeamRank(TestGame2.teamRank!!)
        .setKill(TestGame2.kills!!)
        .setGameInfo(
            MachineConstants.machineInputValidator.validateGameInfo(TestGame2.gameInfo!!)
                .buildGameInfo()
        )

val USER2_PROFILE_and_ID_RAW: MachineResult.Builder = MachineResult.Builder()
    .setAccepted()
    .setId(TestUser2.idNumeric)

val FRIEND_ID_RAW: MachineResult.Builder = MachineResult.Builder()
    .setId(TestUser1.idNumeric)

val RATING_RAW: MachineResult.Builder = MachineResult.Builder()
    .setInitialTier(TestGame1.initialTier!!)
    .setFinalTier(TestGame1.finalTier!!)
    .setRank(TestGame1.rank!!)
    .setGameInfo(
        MachineConstants.machineInputValidator.validateGameInfo(TestGame2.gameInfo!!)
            .buildGameInfo()
    )

fun resultRankRatingsBuilder(
    rating: String? = null,
    gameInfo: String? = null,
    rank: String? = null
): String {

    return """{
      "epochTimestamp": 1631522911504,
      "labels": [
        {
          "box": [
            0.0030429102,
            0.44486108,
            0.07074162,
            0.5655534
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.CLASSIC_RATING.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": "${rank ?: TestGame1.rank}[.]${rating ?: TestGame1.initialTier}",
              "language": "Un-Known"
            }
          ]
        },
        {
          "box": [
            0.0030429102,
            0.44486108,
            0.07074162,
            0.5655534
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.GAME_INFO.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": "${gameInfo ?: TestGame1.gameInfo}",
              "language": "Un-Known"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 155,
      "ttOCR": 1,
      "url": "image.jpg"
    }"""
}

val RESULT_RANK_RATING_JSON = """{
      "epochTimestamp": 1631522911504,
      "labels": [
        {
          "box": [
            0.0030429102,
            0.44486108,
            0.07074162,
            0.5655534
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.CLASSIC_RATING.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": "[.]${TestGame1.rank}[.]${TestGame1.initialTier}[.]",
              "language": "Un-Known"
            }
          ]
        },
        {
          "box": [
            0.0030429102,
            0.44486108,
            0.07074162,
            0.5655534
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.GAME_INFO.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": "${TestGame1.gameInfo}",
              "language": "Un-Known"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 155,
      "ttOCR": 1,
      "url": "image.jpg"
    }"""

fun resultRankKillsScreenJsonBuilder(
    kills: String? = TestGame1.kills,
    rank: String? = TestGame1.rank,
    teamRank: String? = TestGame1.teamRank,
    gameinfo: String? = TestGame1.gameInfo
): String {

    return """{
      "epochTimestamp": 1631523345417,
      "labels": [
        {
          "box": [
            0.24927466,
            0.11020344,
            0.30000263,
            0.24557835
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal},
          "ocr": [
            {
              "box": [
                19,
                18,
                287,
                54
              ],
              "label": "$kills",
              "language": "en"
            }
          ]
        },
        {
          "box": [
            0.25263366,
            0.019807339,
            0.30048797,
            0.10762359
          ],
          "confidence": 0.6640625,
          "label": ${BGMIConstants.GameLabels.RANK.ordinal},
          "ocr": [
            {
              "box": [
                13,
                4,
                177,
                48
              ],
              "label": "$rank",
              "language": "en"
            }
          ]
        },
        {
          "box": [
            0.14962812,
            0.80743456,
            0.20109464,
            0.9830378
          ],
          "confidence": 0.38671875,
          "label": ${BGMIConstants.GameLabels.GAME_INFO.ordinal},
          "ocr": [
            {
              "box": [
                23,
                8,
                333,
                51
              ],
              "label": "$gameinfo",
              "language": "en"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 152,
      "ttOCR": 281,
      "url": "image.jpg"
    }"""
}

val USER1_GAME1_RANK_KILLS_GAME_INFO_RAW_JSON: String =
    """{
      "epochTimestamp": 1631523345417,
      "labels": [
        {
          "box": [
            0.24927466,
            0.11020344,
            0.30000263,
            0.24557835
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.RANK.ordinal},
          "ocr": [
            {
              "box": [
                19,
                18,
                287,
                54
              ],
              "label": "${TestGame1.rank}",
              "language": "en"
            }
          ]
        },
        {
          "box": [
            0.14962812,
            0.80743456,
            0.20109464,
            0.9830378
          ],
          "confidence": 0.38671875,
          "label": ${BGMIConstants.GameLabels.GAME_INFO.ordinal},
          "ocr": [
            {
              "box": [
                23,
                8,
                333,
                51
              ],
               "label": "${TestGame1.gameInfo}",
              "language": "en"
            }
          ]
        },
         {
          "box": [
            0.14962812,
            0.80743456,
            0.20109464,
            0.9830378
          ],
          "confidence": 0.38671875,
          "label": ${BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal},
          "ocr": [
            {
              "box": [
                23,
                8,
                333,
                51
              ],
               "label": "${TestUser2.id}[.]${TestGame1.kills}",
                "language": "en"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 152,
      "ttOCR": 281,
      "url": "image.jpg"
    }"""


val USER1_GAME1_RANK_RATING_GAME_INFO_TEAM_RAW_JSON: String =
    """{
      "epochTimestamp": 1631523345417,
      "labels": [
        {
          "box": [
            0.24927466,
            0.11020344,
            0.30000263,
            0.24557835
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.RANK.ordinal},
          "ocr": [
            {
              "box": [
                19,
                18,
                287,
                54
              ],
              "label": "${TestGame1.rank}",
              "language": "en"
            }
          ]
        },
        {
          "box": [
            0.14962812,
            0.80743456,
            0.20109464,
            0.9830378
          ],
          "confidence": 0.38671875,
          "label": ${BGMIConstants.GameLabels.CLASSIC_RATING.ordinal},
          "ocr": [
            {
              "box": [
                23,
                8,
                333,
                51
              ],
              "label": "${TestGame1.rank}[.]${TestGame1.initialTier}",
              "language": "en"
            }
          ]
        },
         {
          "box": [
            0.14962812,
            0.80743456,
            0.20109464,
            0.9830378
          ],
          "confidence": 0.38671875,
          "label": ${BGMIConstants.GameLabels.GAME_INFO.ordinal},
          "ocr": [
            {
              "box": [
                23,
                8,
                333,
                51
              ],
              "label": "${TestGame1.gameInfo}",
              "language": "en"
            }
          ]
        }, 
        {
          "box": [
            0.24927466,
            0.11020344,
            0.30000263,
            0.24557835
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.CLASSIC_RANK_GAME_INFO.ordinal},
          "ocr": [
            {
              "box": [
                19,
                18,
                287,
                54
              ],
              "label": "${BGMIConstants.GameLabels.CLASSIC_RANK_GAME_INFO}",
              "language": "en"
            }
          ]
        }
       
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 152,
      "ttOCR": 281,
      "url": "image.jpg"
    }"""


val USER1_GAME1_RANK_KILLS_GAME_INFO_TEAM_RAW_JSON: String =
    """{
      "epochTimestamp": 1631523345417,
      "labels": [
        {
          "box": [
            0.24927466,
            0.11020344,
            0.30000263,
            0.24557835
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.RANK.ordinal},
          "ocr": [
            {
              "box": [
                19,
                18,
                287,
                54
              ],
              "label": "${TestGame1.rank}",
              "language": "en"
            }
          ]
        },
        {
          "box": [
            0.14962812,
            0.80743456,
            0.20109464,
            0.9830378
          ],
          "confidence": 0.38671875,
          "label": ${BGMIConstants.GameLabels.GAME_INFO.ordinal},
          "ocr": [
            {
              "box": [
                23,
                8,
                333,
                51
              ],
              "label": "${TestGame1.gameInfo}",
              "language": "en"
            }
          ]
        },
         {
          "box": [
            0.14962812,
            0.80743456,
            0.20109464,
            0.9830378
          ],
          "confidence": 0.38671875,
          "label": ${BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal},
          "ocr": [
            {
              "box": [
                23,
                8,
                333,
                51
              ],
              "label": "${TestUser1.id} ${TestGame1.kills}",
              "language": "en"
            }
          ]
        },
           {
          "box": [
            0.14962812,
            0.80743456,
            0.20109464,
            0.9830378
          ],
          "confidence": 0.38671875,
          "label": ${BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal},
          "ocr": [
            {
              "box": [
                23,
                8,
                333,
                51
              ],
              "label": "${TestUser2.id}[.]${TestGame1.kills}",
              "language": "en"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 152,
      "ttOCR": 281,
      "url": "image.jpg"
    }"""


val USER1_GAME2_RANK_KILLS_GAME_INFO_RAW_JSON: String =
    """{
      "epochTimestamp": 1631523345417,
      "labels": [
        {
          "box": [
            0.24927466,
            0.11020344,
            0.30000263,
            0.24557835
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.RANK.ordinal},
          "ocr": [
            {
              "box": [
                19,
                18,
                287,
                54
              ],
              "label": "${TestGame2.rank}",
              "language": "en"
            }
          ]
        },
        {
          "box": [
            0.14962812,
            0.80743456,
            0.20109464,
            0.9830378
          ],
          "confidence": 0.38671875,
          "label": ${BGMIConstants.GameLabels.GAME_INFO.ordinal},
          "ocr": [
            {
              "box": [
                23,
                8,
                333,
                51
              ],
              "label": "${TestGame2.gameInfo}",
              "language": "en"
            }
          ]
        },
         {
          "box": [
            0.14962812,
            0.80743456,
            0.20109464,
            0.9830378
          ],
          "confidence": 0.38671875,
          "label": ${BGMIConstants.GameLabels.CLASSIC_ALL_KILLS.ordinal},
          "ocr": [
            {
              "box": [
                23,
                8,
                333,
                51
              ],
              "label": "${TestGame2.kills}",
              "language": "en"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 152,
      "ttOCR": 281,
      "url": "image.jpg"
    }"""

const val USER1_SQUAD_RANK_KILLS_GAME_INFO_RAW_JSON: String =
    """
    {
      "ttLabelling": 181,
      "ttOCR": 560,
      "resolution": {
        "width": 1440,
        "height": 720
      },
      "epochTimestamp": 1648107544357,
      "url": "1648107544357.jpg",
      "labels": [
        {
          "label": 7,
          "confidence": 0.9765625,
          "box": [
            0.60658246,
            0.33617374,
            0.6929106,
            0.4284909
          ],
          "ocr": "[.]pratyushtiwa[.]Finishes D"
        },
        {
          "label": 8,
          "confidence": 0.9765625,
          "box": [
            0.0074053966,
            0.7670386,
            0.057035435,
            0.97935545
          ],
          "ocr": "ed Classic Mode (TPP)- Squad - Erange"
        },
        {
          "label": 7,
          "confidence": 0.97265625,
          "box": [
            0.60470563,
            0.14567253,
            0.689125,
            0.2379897
          ],
          "ocr": "[.]T SalmanSiddDl[.]Finishes 8"
        },
        {
          "label": 6,
          "confidence": 0.96484375,
          "box": [
            0.00028911978,
            0.42592597,
            0.10922361,
            0.5683837
          ],
          "ocr": "#|/97"
        },
        {
          "label": 7,
          "confidence": 0.9453125,
          "box": [
            0.60272104,
            0.71417356,
            0.69299704,
            0.8064908
          ],
          "ocr": "[.]ANTAŁ772[.]Finishes 3"
        },
        {
          "label": 7,
          "confidence": 0.82421875,
          "box": [
            0.60469496,
            0.5257226,
            0.6910231,
            0.6222615
          ],
          "ocr": "[.]manishgoudaluk[.]Finishes I0"
        }
      ],
      "verifiedProfile": true
    }
"""

fun resultSquadRankKillsScreenJsonBuilder(
    username1Kills1: String,
    username2kills2: String,
    username3Kills3: String,
    username4Kills4: String,
): String {

    return """{
      "ttLabelling": 181,
      "ttOCR": 560,
      "resolution": {
        "width": 1440,
        "height": 720
      },
      "epochTimestamp": 1648107544357,
      "url": "1648107544357.jpg",
      "labels": [
        {
          "label": 7,
          "confidence": 0.9765625,
          "box": [
            0.60658246,
            0.33617374,
            0.6929106,
            0.4284909
          ],
          "ocr": "$username1Kills1"
        },
        {
          "label": 8,
          "confidence": 0.9765625,
          "box": [
            0.0074053966,
            0.7670386,
            0.057035435,
            0.97935545
          ],
          "ocr": "ed Classic Mode (TPP)- Squad - Erange"
        },
        {
          "label": 7,
          "confidence": 0.97265625,
          "box": [
            0.60470563,
            0.14567253,
            0.689125,
            0.2379897
          ],
          "ocr": "$username2kills2"
        },
        {
          "label": 6,
          "confidence": 0.96484375,
          "box": [
            0.00028911978,
            0.42592597,
            0.10922361,
            0.5683837
          ],
          "ocr": "#|/97"
        },
        {
          "label": 7,
          "confidence": 0.9453125,
          "box": [
            0.60272104,
            0.71417356,
            0.69299704,
            0.8064908
          ],
          "ocr": "$username3Kills3"
        },
        {
          "label": 7,
          "confidence": 0.82421875,
          "box": [
            0.60469496,
            0.5257226,
            0.6910231,
            0.6222615
          ],
          "ocr": "$username4Kills4"
        }
      ],
      "verifiedProfile": true
    }"""
}

fun gameScreenJsonBuilder(aliveFinish: String? = null): String {
    return """{
      "epochTimestamp": 1631522911504,
      "labels": [
        {
          "box": [
            0.0030429102,
            0.44486108,
            0.07074162,
            0.5655534
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.CLASSIC_ALL_GAMEPLAY.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": ${aliveFinish ?: "\"No OCR performed for In-Game\""},
              "language": "Un-Known"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 155,
      "ttOCR": 1,
      "url": "image.jpg"
    }"""
}

val CLASSIC_ALL_IN_GAME_JSON: String =
    """{
      "epochTimestamp": 1631522911504,
      "labels": [
        {
          "box": [
            0.0030429102,
            0.44486108,
            0.07074162,
            0.5655534
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.CLASSIC_ALL_GAMEPLAY.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": "No OCR performed for In-Game",
              "language": "Un-Known"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 155,
      "ttOCR": 1,
      "url": "image.jpg"
    }"""

fun loginScreenJsonBuilder(login: String? = null): String {
    return """{
      "epochTimestamp": 1631522911504,
      "labels": [
        {
          "box": [
            0.0030429102,
            0.44486108,
            0.07074162,
            0.5655534
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.GLOBAL_LOGIN.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": ${login ?: "\"twitter login with facebook\""},
              "language": "Un-Known"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 155,
      "ttOCR": 1,
      "url": "image.jpg"
    }"""
}

val LOGIN_RAW_JSON: String =
    """{
      "epochTimestamp": 1631522911504,
      "labels": [
        {
          "box": [
            0.0030429102,
            0.44486108,
            0.07074162,
            0.5655534
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.GLOBAL_LOGIN.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": "No OCR performed for Login",
              "language": "Un-Known"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 155,
      "ttOCR": 1,
      "url": "image.jpg"
    }"""


val USER1_PROFILE_RAW_JSON: String =
    """{
      "epochTimestamp": 1631522911504,
      "labels": [
        {
          "box": [
            0.0030429102,
            0.44486108,
            0.07074162,
            0.5655534
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.PROFILE_SELF.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": "No OCR performed for Profile",
              "language": "Un-Known"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 155,
      "ttOCR": 1,
      "url": "image.jpg"
    }"""


val USER1_PROFILE_and_ID_RAW_JSON: String =
    """{
      "epochTimestamp": 1631522916674,
      "labels": [
        {
          "box": [
            0.009325866,
            0.4186703,
            0.06628187,
            0.5684844
          ],
          "confidence": 0.8125,
          "label": ${BGMIConstants.GameLabels.PROFILE_SELF.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": "No OCR performed for Profile",
              "language": "Un-Known"
            }
          ]
        },
        {
          "box": [
            0.025245398,
            0.05652427,
            0.11434023,
            0.15510559
          ],
          "confidence": 0.77734375,
          "label": ${BGMIConstants.GameLabels.PROFILE_ID.ordinal},
          "ocr": [
            {
              "box": [
                14,
                15,
                135,
                41
              ],
              "label": "${TestUser1.id}",
              "language": "und"
            },
            {
              "box": [
                11,
                56,
                135,
                77
              ],
              "label": "${TestUser1.idNumeric}",
              "language": "und"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 154,
      "ttOCR": 149,
      "url": "image.jpg"
    }"""


val USER1_ID_DETAILS_and_LEVEL_RAW_JSON: String =
    """{
      "epochTimestamp": 1631522925439,
      "labels": [
        {
          "box": [
            0.065442145,
            0.0984662,
            0.17768215,
            0.25497168
          ],
          "confidence": 0.84375,
          "label": ${BGMIConstants.GameLabels.PROFILE_ID_DETAILS.ordinal},
          "ocr": [
            {
              "box": [
                38,
                28,
                252,
                59
              ],
              "label": "${TestUser1.id}",
              "language": "und"
            },
            {
              "box": [
                30,
                85,
                223,
                119
              ],
              "label": "${TestUser1.idNumeric}",
              "language": "und"
            }
          ]
        },
        {
          "box": [
            0.37722617,
            0.005425021,
            0.43501145,
            0.15742032
          ],
          "confidence": 0.734375,
          "label": ${BGMIConstants.GameLabels.PROFILE_LEVEL.ordinal},
          "ocr": [
            {
              "box": [
                7,
                16,
                158,
                52
              ],
              "label": "${TestUser1.level}",
              "language": "en"
            }
          ]
        },
        {
          "box": [
            0.0032655485,
            0.43296033,
            0.07294993,
            0.55540985
          ],
          "confidence": 0.5859375,
          "label": ${BGMIConstants.GameLabels.PROFILE_SELF.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": "No OCR performed for Profile",
              "language": "Un-Known"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 149,
      "ttOCR": 230,
      "url": "image.jpg"
    }"""


fun homeScreenJsonBuilder(start: String? = null): String {
    return """{
        "epochTimestamp": 1630505786366,
        "labels": [
        {
            "box": [
            0.051176842,
            0.32886404,
            0.124682754,
            0.74294823
            ],
            "confidence": 0.54296875,
            "label": ${BGMIConstants.GameLabels.CLASSIC_START.ordinal},
            "ocr": [
            {
                "box": [
                8,
                30,
                928,
                75
                ],
                "label": ${start ?: "\"No OCR Performed for START\""},
                "language": "en"
            }
            ]
        }
        ],
        "resolution": {
        "height": 1080,
        "width": 2368
    },
        "ttLabelling": 128,
        "ttOCR": 115,
        "url": "1630505786366.jpg"
    }"""
}


val START_JSON: String =
    """{
        "epochTimestamp": 1630505786366,
        "labels": [
        {
            "box": [
            0.051176842,
            0.32886404,
            0.124682754,
            0.74294823
            ],
            "confidence": 0.54296875,
            "label": ${BGMIConstants.GameLabels.CLASSIC_START.ordinal},
            "ocr": [
            {
                "box": [
                8,
                30,
                928,
                75
                ],
                "label": "No OCR Performed for START",
                "language": "en"
            }
            ]
        }
        ],
        "resolution": {
        "height": 1080,
        "width": 2368
    },
        "ttLabelling": 128,
        "ttOCR": 115,
        "url": "1630505786366.jpg"
    }"""


fun waitingScreenJsonBuilder(waiting: String? = null): String {
    return """{
        "epochTimestamp": 1630505786366,
        "labels": [
        {
            "box": [
            0.051176842,
            0.32886404,
            0.124682754,
            0.74294823
            ],
            "confidence": 0.54296875,
            "label": ${BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal},
            "ocr": [
            {
                "box": [
                8,
                30,
                928,
                75
                ],
                "label": ${waiting ?: "\"Match Starts in x second\""},
                "language": "en"
            }
            ]
        }
        ],
        "resolution": {
        "height": 1080,
        "width": 2368
    },
        "ttLabelling": 128,
        "ttOCR": 115,
        "url": "1630505786366.jpg"
    }"""
}


val USER1_WAITING_RAW_JSON: String =
    """{
        "epochTimestamp": 1630505786366,
        "labels": [
        {
            "box": [
            0.051176842,
            0.32886404,
            0.124682754,
            0.74294823
            ],
            "confidence": 0.54296875,
            "label": ${BGMIConstants.GameLabels.CLASSIC_ALL_WAITING.ordinal},
            "ocr": [
            {
                "box": [
                8,
                30,
                928,
                75
                ],
                "label": "Match Starts in x second",
                "language": "en"
            }
            ]
        }
        ],
        "resolution": {
        "height": 1080,
        "width": 2368
    },
        "ttLabelling": 128,
        "ttOCR": 115,
        "url": "1630505786366.jpg"
    }"""

val USER2_PROFILE_and_ID_RAW_JSON: String =
    """{
      "epochTimestamp": 1631522916674,
      "labels": [
        {
          "box": [
            0.009325866,
            0.4186703,
            0.06628187,
            0.5684844
          ],
          "confidence": 0.8125,
          "label": ${BGMIConstants.GameLabels.PROFILE_SELF.ordinal},
          "ocr": [
            {
              "box": [
                -1,
                -1,
                -1,
                -1
              ],
              "label": "No OCR performed for Profile",
              "language": "Un-Known"
            }
          ]
        },
        {
          "box": [
            0.025245398,
            0.05652427,
            0.11434023,
            0.15510559
          ],
          "confidence": 0.77734375,
          "label": ${BGMIConstants.GameLabels.PROFILE_ID.ordinal},
          "ocr": [
            {
              "box": [
                14,
                15,
                135,
                41
              ],
              "label": "${TestUser2.id}",
              "language": "und"
            },
            {
              "box": [
                11,
                56,
                135,
                77
              ],
              "label": "${TestUser2.idNumeric}",
              "language": "und"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 154,
      "ttOCR": 149,
      "url": "image.jpg"
    }"""


val FRIEND_ID_RAW_JSON: String =
    """{
      "epochTimestamp": 1631522916674,
      "labels": [
        {
          "box": [
            0.025245398,
            0.05652427,
            0.11434023,
            0.15510559
          ],
          "confidence": 0.77734375,
          "label": ${BGMIConstants.GameLabels.PROFILE_ID.ordinal},
          "ocr": [
            {
              "box": [
                14,
                15,
                135,
                41
              ],
              "label": "${TestUser1.id}",
              "language": "und"
            },
            {
              "box": [
                11,
                56,
                135,
                77
              ],
              "label": "${TestUser1.idNumeric}",
              "language": "und"
            }
          ]
        }
      ],
      "resolution": {
        "height": 1080,
        "width": 2160
      },
      "ttLabelling": 154,
      "ttOCR": 149,
      "url": "image.jpg"
    }"""

val sampleMLKitOcr =
    "{\"pages\":[{\"blocks\":[{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":23,\"y\":21},{\"x\":128,\"y\":18},{\"x\":130,\"y\":71},{\"x\":25,\"y\":74}]},\"confidence\":0.8594449,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":23,\"y\":21},{\"x\":128,\"y\":18},{\"x\":130,\"y\":71},{\"x\":25,\"y\":74}]},\"confidence\":0.8594449,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":23,\"y\":22},{\"x\":62,\"y\":21},{\"x\":64,\"y\":73},{\"x\":25,\"y\":74}]},\"confidence\":0.9576933,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":23,\"y\":22},{\"x\":62,\"y\":21},{\"x\":64,\"y\":73},{\"x\":25,\"y\":74}]},\"confidence\":0.9576933,\"text\":\"#\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":64,\"y\":20},{\"x\":128,\"y\":18},{\"x\":130,\"y\":71},{\"x\":66,\"y\":73}]},\"confidence\":0.82669544,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":64,\"y\":20},{\"x\":81,\"y\":19},{\"x\":83,\"y\":71},{\"x\":66,\"y\":72}]},\"confidence\":0.9387495,\"text\":\"1\"},{\"boundingBox\":{\"vertices\":[{\"x\":81,\"y\":20},{\"x\":99,\"y\":19},{\"x\":101,\"y\":71},{\"x\":83,\"y\":72}]},\"confidence\":0.95763505,\"text\":\"1\"},{\"boundingBox\":{\"vertices\":[{\"x\":106,\"y\":19},{\"x\":128,\"y\":18},{\"x\":130,\"y\":70},{\"x\":108,\"y\":71}]},\"confidence\":0.5837018,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"4\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":13,\"y\":183},{\"x\":78,\"y\":182},{\"x\":78,\"y\":221},{\"x\":13,\"y\":222}]},\"confidence\":0.95799655,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":13,\"y\":183},{\"x\":78,\"y\":182},{\"x\":78,\"y\":221},{\"x\":13,\"y\":222}]},\"confidence\":0.95799655,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":13,\"y\":183},{\"x\":75,\"y\":182},{\"x\":75,\"y\":197},{\"x\":13,\"y\":198}]},\"confidence\":0.92572606,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":13,\"y\":184},{\"x\":20,\"y\":184},{\"x\":20,\"y\":198},{\"x\":13,\"y\":198}]},\"confidence\":0.9345203,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":21,\"y\":183},{\"x\":32,\"y\":183},{\"x\":32,\"y\":197},{\"x\":21,\"y\":197}]},\"confidence\":0.92152697,\"text\":\"m\"},{\"boundingBox\":{\"vertices\":[{\"x\":32,\"y\":183},{\"x\":36,\"y\":183},{\"x\":36,\"y\":197},{\"x\":32,\"y\":197}]},\"confidence\":0.7304162,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":35,\"y\":183},{\"x\":43,\"y\":183},{\"x\":43,\"y\":197},{\"x\":35,\"y\":197}]},\"confidence\":0.90975404,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":43,\"y\":183},{\"x\":51,\"y\":183},{\"x\":51,\"y\":197},{\"x\":43,\"y\":197}]},\"confidence\":0.9357965,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":51,\"y\":183},{\"x\":59,\"y\":183},{\"x\":59,\"y\":197},{\"x\":51,\"y\":197}]},\"confidence\":0.98844963,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":59,\"y\":183},{\"x\":67,\"y\":183},{\"x\":67,\"y\":197},{\"x\":59,\"y\":197}]},\"confidence\":0.9942295,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":67,\"y\":183},{\"x\":75,\"y\":183},{\"x\":75,\"y\":197},{\"x\":67,\"y\":197}]},\"confidence\":0.99111515,\"property\":{\"detectedBreak\":{\"type\":\"EOL_SURE_SPACE\"}},\"text\":\"s\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":13,\"y\":206},{\"x\":65,\"y\":206},{\"x\":65,\"y\":222},{\"x\":13,\"y\":222}]},\"confidence\":0.9870106,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":13,\"y\":207},{\"x\":20,\"y\":207},{\"x\":20,\"y\":222},{\"x\":13,\"y\":222}]},\"confidence\":0.9842544,\"text\":\"F\"},{\"boundingBox\":{\"vertices\":[{\"x\":19,\"y\":206},{\"x\":24,\"y\":206},{\"x\":24,\"y\":221},{\"x\":19,\"y\":221}]},\"confidence\":0.99095047,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":22,\"y\":206},{\"x\":29,\"y\":206},{\"x\":29,\"y\":221},{\"x\":22,\"y\":221}]},\"confidence\":0.9958271,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":31,\"y\":206},{\"x\":35,\"y\":206},{\"x\":35,\"y\":221},{\"x\":31,\"y\":221}]},\"confidence\":0.9953847,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":33,\"y\":206},{\"x\":40,\"y\":206},{\"x\":40,\"y\":221},{\"x\":33,\"y\":221}]},\"confidence\":0.9936349,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":41,\"y\":206},{\"x\":49,\"y\":206},{\"x\":49,\"y\":221},{\"x\":41,\"y\":221}]},\"confidence\":0.99109215,\"text\":\"h\"},{\"boundingBox\":{\"vertices\":[{\"x\":49,\"y\":206},{\"x\":57,\"y\":206},{\"x\":57,\"y\":221},{\"x\":49,\"y\":221}]},\"confidence\":0.9742788,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":57,\"y\":206},{\"x\":65,\"y\":206},{\"x\":65,\"y\":221},{\"x\":57,\"y\":221}]},\"confidence\":0.9706625,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"s\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":69,\"y\":206},{\"x\":78,\"y\":206},{\"x\":78,\"y\":221},{\"x\":69,\"y\":221}]},\"confidence\":0.98404855,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":69,\"y\":206},{\"x\":78,\"y\":206},{\"x\":78,\"y\":221},{\"x\":69,\"y\":221}]},\"confidence\":0.98404855,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"2\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"y\":131},{\"x\":240,\"y\":130},{\"x\":240,\"y\":146},{\"y\":147}]},\"confidence\":0.955511,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"y\":131},{\"x\":240,\"y\":130},{\"x\":240,\"y\":146},{\"y\":147}]},\"confidence\":0.955511,\"words\":[{\"boundingBox\":{\"vertices\":[{\"y\":131},{\"x\":43,\"y\":131},{\"x\":43,\"y\":147},{\"y\":147}]},\"confidence\":0.99073994,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"y\":132},{\"x\":8,\"y\":132},{\"x\":8,\"y\":147},{\"y\":147}]},\"confidence\":0.99023086,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":8,\"y\":131},{\"x\":14,\"y\":131},{\"x\":14,\"y\":146},{\"x\":8,\"y\":146}]},\"confidence\":0.9887511,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":15,\"y\":131},{\"x\":20,\"y\":131},{\"x\":20,\"y\":146},{\"x\":15,\"y\":146}]},\"confidence\":0.9937201,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":23,\"y\":131},{\"x\":29,\"y\":131},{\"x\":29,\"y\":146},{\"x\":23,\"y\":146}]},\"confidence\":0.9931533,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":30,\"y\":131},{\"x\":36,\"y\":131},{\"x\":36,\"y\":146},{\"x\":30,\"y\":146}]},\"confidence\":0.99086565,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":37,\"y\":131},{\"x\":43,\"y\":131},{\"x\":43,\"y\":146},{\"x\":37,\"y\":146}]},\"confidence\":0.9877186,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":48,\"y\":131},{\"x\":88,\"y\":131},{\"x\":88,\"y\":146},{\"x\":48,\"y\":146}]},\"confidence\":0.9906518,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":48,\"y\":131},{\"x\":54,\"y\":131},{\"x\":54,\"y\":146},{\"x\":48,\"y\":146}]},\"confidence\":0.98268473,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":56,\"y\":131},{\"x\":59,\"y\":131},{\"x\":59,\"y\":146},{\"x\":56,\"y\":146}]},\"confidence\":0.9939361,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":59,\"y\":131},{\"x\":65,\"y\":131},{\"x\":65,\"y\":146},{\"x\":59,\"y\":146}]},\"confidence\":0.9912242,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":66,\"y\":131},{\"x\":71,\"y\":131},{\"x\":71,\"y\":146},{\"x\":66,\"y\":146}]},\"confidence\":0.9932312,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":73,\"y\":131},{\"x\":78,\"y\":131},{\"x\":78,\"y\":146},{\"x\":73,\"y\":146}]},\"confidence\":0.9926291,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":79,\"y\":131},{\"x\":82,\"y\":131},{\"x\":82,\"y\":146},{\"x\":79,\"y\":146}]},\"confidence\":0.99363506,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":83,\"y\":131},{\"x\":88,\"y\":131},{\"x\":88,\"y\":146},{\"x\":83,\"y\":146}]},\"confidence\":0.9872221,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"c\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":93,\"y\":131},{\"x\":125,\"y\":131},{\"x\":125,\"y\":146},{\"x\":93,\"y\":146}]},\"confidence\":0.96613896,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":93,\"y\":131},{\"x\":101,\"y\":131},{\"x\":101,\"y\":146},{\"x\":93,\"y\":146}]},\"confidence\":0.9828441,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":104,\"y\":131},{\"x\":109,\"y\":131},{\"x\":109,\"y\":146},{\"x\":104,\"y\":146}]},\"confidence\":0.90512323,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":111,\"y\":131},{\"x\":116,\"y\":131},{\"x\":116,\"y\":146},{\"x\":111,\"y\":146}]},\"confidence\":0.98944503,\"text\":\"d\"},{\"boundingBox\":{\"vertices\":[{\"x\":119,\"y\":131},{\"x\":125,\"y\":131},{\"x\":125,\"y\":146},{\"x\":119,\"y\":146}]},\"confidence\":0.9871434,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"e\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":131,\"y\":131},{\"x\":134,\"y\":131},{\"x\":134,\"y\":146},{\"x\":131,\"y\":146}]},\"confidence\":0.9847044,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":131,\"y\":131},{\"x\":134,\"y\":131},{\"x\":134,\"y\":146},{\"x\":131,\"y\":146}]},\"confidence\":0.9847044,\"text\":\"(\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":135,\"y\":131},{\"x\":157,\"y\":131},{\"x\":157,\"y\":146},{\"x\":135,\"y\":146}]},\"confidence\":0.9801263,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":135,\"y\":131},{\"x\":141,\"y\":131},{\"x\":141,\"y\":146},{\"x\":135,\"y\":146}]},\"confidence\":0.98744875,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":142,\"y\":131},{\"x\":148,\"y\":131},{\"x\":148,\"y\":146},{\"x\":142,\"y\":146}]},\"confidence\":0.9932046,\"text\":\"P\"},{\"boundingBox\":{\"vertices\":[{\"x\":151,\"y\":131},{\"x\":157,\"y\":131},{\"x\":157,\"y\":146},{\"x\":151,\"y\":146}]},\"confidence\":0.9597257,\"text\":\"P\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":158,\"y\":131},{\"x\":162,\"y\":131},{\"x\":162,\"y\":146},{\"x\":158,\"y\":146}]},\"confidence\":0.9793875,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":158,\"y\":131},{\"x\":162,\"y\":131},{\"x\":162,\"y\":146},{\"x\":158,\"y\":146}]},\"confidence\":0.9793875,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\")\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":167,\"y\":131},{\"x\":171,\"y\":131},{\"x\":171,\"y\":146},{\"x\":167,\"y\":146}]},\"confidence\":0.7903377,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":167,\"y\":131},{\"x\":171,\"y\":131},{\"x\":171,\"y\":146},{\"x\":167,\"y\":146}]},\"confidence\":0.7903377,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":176,\"y\":131},{\"x\":199,\"y\":131},{\"x\":199,\"y\":146},{\"x\":176,\"y\":146}]},\"confidence\":0.8040474,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":176,\"y\":131},{\"x\":183,\"y\":131},{\"x\":183,\"y\":146},{\"x\":176,\"y\":146}]},\"confidence\":0.94848084,\"text\":\"D\"},{\"boundingBox\":{\"vertices\":[{\"x\":185,\"y\":131},{\"x\":190,\"y\":131},{\"x\":190,\"y\":146},{\"x\":185,\"y\":146}]},\"confidence\":0.87949544,\"text\":\"u\"},{\"boundingBox\":{\"vertices\":[{\"x\":193,\"y\":131},{\"x\":199,\"y\":131},{\"x\":199,\"y\":146},{\"x\":193,\"y\":146}]},\"confidence\":0.584166,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"o\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":203,\"y\":131},{\"x\":208,\"y\":131},{\"x\":208,\"y\":146},{\"x\":203,\"y\":146}]},\"confidence\":0.7810504,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":203,\"y\":131},{\"x\":208,\"y\":131},{\"x\":208,\"y\":146},{\"x\":203,\"y\":146}]},\"confidence\":0.7810504,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":213,\"y\":130},{\"x\":240,\"y\":130},{\"x\":240,\"y\":145},{\"x\":213,\"y\":145}]},\"confidence\":0.9889584,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":213,\"y\":130},{\"x\":219,\"y\":130},{\"x\":219,\"y\":145},{\"x\":213,\"y\":145}]},\"confidence\":0.98704416,\"text\":\"L\"},{\"boundingBox\":{\"vertices\":[{\"x\":219,\"y\":130},{\"x\":222,\"y\":130},{\"x\":222,\"y\":145},{\"x\":219,\"y\":145}]},\"confidence\":0.9912541,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":222,\"y\":130},{\"x\":228,\"y\":130},{\"x\":228,\"y\":145},{\"x\":222,\"y\":145}]},\"confidence\":0.99092096,\"text\":\"v\"},{\"boundingBox\":{\"vertices\":[{\"x\":229,\"y\":130},{\"x\":232,\"y\":130},{\"x\":232,\"y\":145},{\"x\":229,\"y\":145}]},\"confidence\":0.99147123,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":233,\"y\":130},{\"x\":240,\"y\":130},{\"x\":240,\"y\":145},{\"x\":233,\"y\":145}]},\"confidence\":0.98410165,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"k\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":3,\"y\":379},{\"x\":82,\"y\":379},{\"x\":82,\"y\":417},{\"x\":3,\"y\":417}]},\"confidence\":0.97742355,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":3,\"y\":379},{\"x\":82,\"y\":379},{\"x\":82,\"y\":417},{\"x\":3,\"y\":417}]},\"confidence\":0.97742355,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":3,\"y\":379},{\"x\":40,\"y\":379},{\"x\":40,\"y\":396},{\"x\":3,\"y\":396}]},\"confidence\":0.98507905,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":3,\"y\":379},{\"x\":7,\"y\":379},{\"x\":7,\"y\":396},{\"x\":3,\"y\":396}]},\"confidence\":0.9742283,\"text\":\"j\"},{\"boundingBox\":{\"vertices\":[{\"x\":6,\"y\":379},{\"x\":13,\"y\":379},{\"x\":13,\"y\":396},{\"x\":6,\"y\":396}]},\"confidence\":0.98903036,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":15,\"y\":379},{\"x\":21,\"y\":379},{\"x\":21,\"y\":396},{\"x\":15,\"y\":396}]},\"confidence\":0.9953859,\"text\":\"r\"},{\"boundingBox\":{\"vertices\":[{\"x\":21,\"y\":379},{\"x\":28,\"y\":379},{\"x\":28,\"y\":396},{\"x\":21,\"y\":396}]},\"confidence\":0.9952227,\"text\":\"v\"},{\"boundingBox\":{\"vertices\":[{\"x\":29,\"y\":379},{\"x\":32,\"y\":379},{\"x\":32,\"y\":396},{\"x\":29,\"y\":396}]},\"confidence\":0.9882869,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":33,\"y\":379},{\"x\":40,\"y\":379},{\"x\":40,\"y\":396},{\"x\":33,\"y\":396}]},\"confidence\":0.9683203,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"s\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":40,\"y\":379},{\"x\":82,\"y\":379},{\"x\":82,\"y\":396},{\"x\":40,\"y\":396}]},\"confidence\":0.96630764,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":40,\"y\":379},{\"x\":46,\"y\":379},{\"x\":46,\"y\":396},{\"x\":40,\"y\":396}]},\"confidence\":0.8465846,\"text\":\"F\"},{\"boundingBox\":{\"vertices\":[{\"x\":47,\"y\":379},{\"x\":53,\"y\":379},{\"x\":53,\"y\":396},{\"x\":47,\"y\":396}]},\"confidence\":0.9871298,\"text\":\"r\"},{\"boundingBox\":{\"vertices\":[{\"x\":54,\"y\":379},{\"x\":58,\"y\":379},{\"x\":58,\"y\":396},{\"x\":54,\"y\":396}]},\"confidence\":0.9945687,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":58,\"y\":379},{\"x\":65,\"y\":379},{\"x\":65,\"y\":396},{\"x\":58,\"y\":396}]},\"confidence\":0.99351263,\"text\":\"d\"},{\"boundingBox\":{\"vertices\":[{\"x\":65,\"y\":379},{\"x\":73,\"y\":379},{\"x\":73,\"y\":396},{\"x\":65,\"y\":396}]},\"confidence\":0.98963016,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":73,\"y\":379},{\"x\":82,\"y\":379},{\"x\":82,\"y\":396},{\"x\":73,\"y\":396}]},\"confidence\":0.9864199,\"property\":{\"detectedBreak\":{\"type\":\"EOL_SURE_SPACE\"}},\"text\":\"y\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":3,\"y\":403},{\"x\":55,\"y\":403},{\"x\":55,\"y\":417},{\"x\":3,\"y\":417}]},\"confidence\":0.98093307,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":3,\"y\":403},{\"x\":10,\"y\":403},{\"x\":10,\"y\":417},{\"x\":3,\"y\":417}]},\"confidence\":0.9675796,\"text\":\"F\"},{\"boundingBox\":{\"vertices\":[{\"x\":10,\"y\":403},{\"x\":14,\"y\":403},{\"x\":14,\"y\":417},{\"x\":10,\"y\":417}]},\"confidence\":0.9742487,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":14,\"y\":403},{\"x\":21,\"y\":403},{\"x\":21,\"y\":417},{\"x\":14,\"y\":417}]},\"confidence\":0.99004525,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":21,\"y\":403},{\"x\":25,\"y\":403},{\"x\":25,\"y\":417},{\"x\":21,\"y\":417}]},\"confidence\":0.98886293,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":25,\"y\":403},{\"x\":32,\"y\":403},{\"x\":32,\"y\":417},{\"x\":25,\"y\":417}]},\"confidence\":0.98982626,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":32,\"y\":403},{\"x\":39,\"y\":403},{\"x\":39,\"y\":417},{\"x\":32,\"y\":417}]},\"confidence\":0.9888679,\"text\":\"h\"},{\"boundingBox\":{\"vertices\":[{\"x\":40,\"y\":403},{\"x\":48,\"y\":403},{\"x\":48,\"y\":417},{\"x\":40,\"y\":417}]},\"confidence\":0.9792856,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":48,\"y\":403},{\"x\":55,\"y\":403},{\"x\":55,\"y\":417},{\"x\":48,\"y\":417}]},\"confidence\":0.9687482,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"s\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":61,\"y\":403},{\"x\":69,\"y\":403},{\"x\":69,\"y\":417},{\"x\":61,\"y\":417}]},\"confidence\":0.97010976,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":61,\"y\":403},{\"x\":69,\"y\":403},{\"x\":69,\"y\":417},{\"x\":61,\"y\":417}]},\"confidence\":0.97010976,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"4\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":108,\"y\":23},{\"x\":152,\"y\":23},{\"x\":152,\"y\":49},{\"x\":108,\"y\":49}]},\"confidence\":0.90949297,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":108,\"y\":23},{\"x\":152,\"y\":23},{\"x\":152,\"y\":49},{\"x\":108,\"y\":49}]},\"confidence\":0.90949297,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":108,\"y\":23},{\"x\":126,\"y\":23},{\"x\":126,\"y\":49},{\"x\":108,\"y\":49}]},\"confidence\":0.771368,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":108,\"y\":23},{\"x\":126,\"y\":23},{\"x\":126,\"y\":49},{\"x\":108,\"y\":49}]},\"confidence\":0.771368,\"text\":\"/\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":125,\"y\":23},{\"x\":152,\"y\":23},{\"x\":152,\"y\":49},{\"x\":125,\"y\":49}]},\"confidence\":0.97855544,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":125,\"y\":23},{\"x\":142,\"y\":23},{\"x\":142,\"y\":49},{\"x\":125,\"y\":49}]},\"confidence\":0.97360003,\"text\":\"5\"},{\"boundingBox\":{\"vertices\":[{\"x\":142,\"y\":23},{\"x\":152,\"y\":23},{\"x\":152,\"y\":49},{\"x\":142,\"y\":49}]},\"confidence\":0.98351085,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"1\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":113,\"y\":64},{\"x\":158,\"y\":64},{\"x\":158,\"y\":77},{\"x\":113,\"y\":77}]},\"confidence\":0.96915835,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":113,\"y\":64},{\"x\":158,\"y\":64},{\"x\":158,\"y\":77},{\"x\":113,\"y\":77}]},\"confidence\":0.96915835,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":113,\"y\":64},{\"x\":158,\"y\":64},{\"x\":158,\"y\":77},{\"x\":113,\"y\":77}]},\"confidence\":0.96915835,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":113,\"y\":65},{\"x\":122,\"y\":65},{\"x\":122,\"y\":77},{\"x\":113,\"y\":77}]},\"confidence\":0.97374076,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":120,\"y\":64},{\"x\":128,\"y\":64},{\"x\":128,\"y\":76},{\"x\":120,\"y\":76}]},\"confidence\":0.94889784,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":127,\"y\":64},{\"x\":134,\"y\":64},{\"x\":134,\"y\":76},{\"x\":127,\"y\":76}]},\"confidence\":0.97102714,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":135,\"y\":64},{\"x\":142,\"y\":64},{\"x\":142,\"y\":76},{\"x\":135,\"y\":76}]},\"confidence\":0.96604484,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":142,\"y\":64},{\"x\":150,\"y\":64},{\"x\":150,\"y\":76},{\"x\":142,\"y\":76}]},\"confidence\":0.9695083,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":150,\"y\":64},{\"x\":158,\"y\":64},{\"x\":158,\"y\":76},{\"x\":150,\"y\":76}]},\"confidence\":0.98573124,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"d\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":6,\"y\":739},{\"x\":71,\"y\":739},{\"x\":71,\"y\":746},{\"x\":6,\"y\":746}]},\"confidence\":0.31174326,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":6,\"y\":739},{\"x\":71,\"y\":739},{\"x\":71,\"y\":746},{\"x\":6,\"y\":746}]},\"confidence\":0.31174326,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":6,\"y\":739},{\"x\":71,\"y\":739},{\"x\":71,\"y\":746},{\"x\":6,\"y\":746}]},\"confidence\":0.31174326,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"is\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":6,\"y\":739},{\"x\":19,\"y\":739},{\"x\":19,\"y\":746},{\"x\":6,\"y\":746}]},\"confidence\":0.27420923,\"text\":\"H\"},{\"boundingBox\":{\"vertices\":[{\"x\":13,\"y\":739},{\"x\":28,\"y\":739},{\"x\":28,\"y\":746},{\"x\":13,\"y\":746}]},\"confidence\":0.25836572,\"text\":\"A\"},{\"boundingBox\":{\"vertices\":[{\"x\":25,\"y\":739},{\"x\":38,\"y\":739},{\"x\":38,\"y\":746},{\"x\":25,\"y\":746}]},\"confidence\":0.33012378,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":47,\"y\":739},{\"x\":58,\"y\":739},{\"x\":58,\"y\":746},{\"x\":47,\"y\":746}]},\"confidence\":0.38865995,\"text\":\"L\"},{\"boundingBox\":{\"vertices\":[{\"x\":63,\"y\":739},{\"x\":71,\"y\":739},{\"x\":71,\"y\":746},{\"x\":63,\"y\":746}]},\"confidence\":0.3073576,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"I\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":18,\"y\":459},{\"x\":113,\"y\":461},{\"x\":112,\"y\":520},{\"x\":17,\"y\":518}]},\"confidence\":0.8996792,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":18,\"y\":459},{\"x\":113,\"y\":461},{\"x\":112,\"y\":520},{\"x\":17,\"y\":518}]},\"confidence\":0.8996792,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":18,\"y\":460},{\"x\":56,\"y\":461},{\"x\":55,\"y\":519},{\"x\":17,\"y\":518}]},\"confidence\":0.963692,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":18,\"y\":460},{\"x\":56,\"y\":461},{\"x\":55,\"y\":519},{\"x\":17,\"y\":518}]},\"confidence\":0.963692,\"text\":\"#\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":57,\"y\":460},{\"x\":92,\"y\":461},{\"x\":91,\"y\":519},{\"x\":56,\"y\":518}]},\"confidence\":0.9601085,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":57,\"y\":460},{\"x\":74,\"y\":460},{\"x\":73,\"y\":518},{\"x\":56,\"y\":518}]},\"confidence\":0.96105975,\"text\":\"1\"},{\"boundingBox\":{\"vertices\":[{\"x\":76,\"y\":460},{\"x\":92,\"y\":460},{\"x\":91,\"y\":518},{\"x\":75,\"y\":518}]},\"confidence\":0.9591572,\"text\":\"1\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":104,\"y\":461},{\"x\":113,\"y\":461},{\"x\":112,\"y\":519},{\"x\":103,\"y\":519}]},\"confidence\":0.71480775,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":104,\"y\":461},{\"x\":113,\"y\":461},{\"x\":112,\"y\":519},{\"x\":103,\"y\":519}]},\"confidence\":0.71480775,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\";\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":154,\"y\":266},{\"x\":485,\"y\":259},{\"x\":486,\"y\":308},{\"x\":155,\"y\":315}]},\"confidence\":0.9650699,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":154,\"y\":266},{\"x\":485,\"y\":259},{\"x\":486,\"y\":308},{\"x\":155,\"y\":315}]},\"confidence\":0.9650699,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":154,\"y\":266},{\"x\":251,\"y\":264},{\"x\":252,\"y\":313},{\"x\":155,\"y\":315}]},\"confidence\":0.9496914,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":154,\"y\":267},{\"x\":170,\"y\":267},{\"x\":171,\"y\":315},{\"x\":155,\"y\":315}]},\"confidence\":0.9344716,\"text\":\"B\"},{\"boundingBox\":{\"vertices\":[{\"x\":170,\"y\":266},{\"x\":185,\"y\":266},{\"x\":186,\"y\":314},{\"x\":171,\"y\":314}]},\"confidence\":0.93933314,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":185,\"y\":266},{\"x\":204,\"y\":266},{\"x\":205,\"y\":314},{\"x\":186,\"y\":314}]},\"confidence\":0.916021,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":201,\"y\":265},{\"x\":220,\"y\":265},{\"x\":221,\"y\":313},{\"x\":202,\"y\":313}]},\"confidence\":0.9545304,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":219,\"y\":265},{\"x\":233,\"y\":265},{\"x\":234,\"y\":313},{\"x\":220,\"y\":313}]},\"confidence\":0.9754051,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":233,\"y\":265},{\"x\":251,\"y\":265},{\"x\":252,\"y\":313},{\"x\":234,\"y\":313}]},\"confidence\":0.9783874,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"R\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":260,\"y\":264},{\"x\":332,\"y\":262},{\"x\":333,\"y\":310},{\"x\":261,\"y\":312}]},\"confidence\":0.9816863,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":260,\"y\":264},{\"x\":274,\"y\":264},{\"x\":275,\"y\":312},{\"x\":261,\"y\":312}]},\"confidence\":0.9884107,\"text\":\"L\"},{\"boundingBox\":{\"vertices\":[{\"x\":276,\"y\":264},{\"x\":292,\"y\":264},{\"x\":293,\"y\":312},{\"x\":277,\"y\":312}]},\"confidence\":0.9821767,\"text\":\"U\"},{\"boundingBox\":{\"vertices\":[{\"x\":294,\"y\":263},{\"x\":311,\"y\":263},{\"x\":312,\"y\":311},{\"x\":295,\"y\":311}]},\"confidence\":0.97661096,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":314,\"y\":263},{\"x\":332,\"y\":263},{\"x\":333,\"y\":311},{\"x\":315,\"y\":311}]},\"confidence\":0.97954684,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"K\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":336,\"y\":262},{\"x\":405,\"y\":261},{\"x\":406,\"y\":309},{\"x\":337,\"y\":311}]},\"confidence\":0.982297,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":336,\"y\":263},{\"x\":353,\"y\":263},{\"x\":354,\"y\":311},{\"x\":337,\"y\":311}]},\"confidence\":0.9910786,\"text\":\"N\"},{\"boundingBox\":{\"vertices\":[{\"x\":355,\"y\":262},{\"x\":372,\"y\":262},{\"x\":373,\"y\":310},{\"x\":356,\"y\":310}]},\"confidence\":0.98147637,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":369,\"y\":262},{\"x\":389,\"y\":262},{\"x\":390,\"y\":310},{\"x\":370,\"y\":310}]},\"confidence\":0.9785144,\"text\":\"X\"},{\"boundingBox\":{\"vertices\":[{\"x\":389,\"y\":261},{\"x\":405,\"y\":261},{\"x\":406,\"y\":309},{\"x\":390,\"y\":309}]},\"confidence\":0.97811866,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"T\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":412,\"y\":261},{\"x\":472,\"y\":260},{\"x\":473,\"y\":308},{\"x\":413,\"y\":309}]},\"confidence\":0.95782775,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":412,\"y\":261},{\"x\":427,\"y\":261},{\"x\":428,\"y\":309},{\"x\":413,\"y\":309}]},\"confidence\":0.9684857,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":429,\"y\":261},{\"x\":438,\"y\":261},{\"x\":439,\"y\":309},{\"x\":430,\"y\":309}]},\"confidence\":0.97962314,\"text\":\"I\"},{\"boundingBox\":{\"vertices\":[{\"x\":438,\"y\":260},{\"x\":456,\"y\":260},{\"x\":457,\"y\":308},{\"x\":439,\"y\":308}]},\"confidence\":0.91763705,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":461,\"y\":260},{\"x\":472,\"y\":260},{\"x\":473,\"y\":308},{\"x\":462,\"y\":308}]},\"confidence\":0.9655651,\"text\":\"E\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":474,\"y\":260},{\"x\":485,\"y\":260},{\"x\":486,\"y\":308},{\"x\":475,\"y\":308}]},\"confidence\":0.9509354,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":474,\"y\":260},{\"x\":485,\"y\":260},{\"x\":486,\"y\":308},{\"x\":475,\"y\":308}]},\"confidence\":0.9509354,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"!\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":107,\"y\":312},{\"x\":348,\"y\":312},{\"x\":348,\"y\":327},{\"x\":107,\"y\":327}]},\"confidence\":0.97655404,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":107,\"y\":312},{\"x\":348,\"y\":312},{\"x\":348,\"y\":327},{\"x\":107,\"y\":327}]},\"confidence\":0.97655404,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":107,\"y\":312},{\"x\":152,\"y\":312},{\"x\":152,\"y\":327},{\"x\":107,\"y\":327}]},\"confidence\":0.9892534,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":107,\"y\":312},{\"x\":115,\"y\":312},{\"x\":115,\"y\":327},{\"x\":107,\"y\":327}]},\"confidence\":0.9891746,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":115,\"y\":312},{\"x\":121,\"y\":312},{\"x\":121,\"y\":327},{\"x\":115,\"y\":327}]},\"confidence\":0.98808616,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":123,\"y\":312},{\"x\":128,\"y\":312},{\"x\":128,\"y\":327},{\"x\":123,\"y\":327}]},\"confidence\":0.99538434,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":131,\"y\":312},{\"x\":137,\"y\":312},{\"x\":137,\"y\":327},{\"x\":131,\"y\":327}]},\"confidence\":0.9933285,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":137,\"y\":312},{\"x\":143,\"y\":312},{\"x\":143,\"y\":327},{\"x\":137,\"y\":327}]},\"confidence\":0.98962796,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":145,\"y\":312},{\"x\":152,\"y\":312},{\"x\":152,\"y\":327},{\"x\":145,\"y\":327}]},\"confidence\":0.97991884,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":156,\"y\":312},{\"x\":196,\"y\":312},{\"x\":196,\"y\":327},{\"x\":156,\"y\":327}]},\"confidence\":0.9912751,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":156,\"y\":312},{\"x\":162,\"y\":312},{\"x\":162,\"y\":327},{\"x\":156,\"y\":327}]},\"confidence\":0.986764,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":164,\"y\":312},{\"x\":168,\"y\":312},{\"x\":168,\"y\":327},{\"x\":164,\"y\":327}]},\"confidence\":0.9941843,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":167,\"y\":312},{\"x\":173,\"y\":312},{\"x\":173,\"y\":327},{\"x\":167,\"y\":327}]},\"confidence\":0.99051404,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":174,\"y\":312},{\"x\":179,\"y\":312},{\"x\":179,\"y\":327},{\"x\":174,\"y\":327}]},\"confidence\":0.992267,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":181,\"y\":312},{\"x\":186,\"y\":312},{\"x\":186,\"y\":327},{\"x\":181,\"y\":327}]},\"confidence\":0.9915915,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":188,\"y\":312},{\"x\":191,\"y\":312},{\"x\":191,\"y\":327},{\"x\":188,\"y\":327}]},\"confidence\":0.99257076,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":191,\"y\":312},{\"x\":196,\"y\":312},{\"x\":196,\"y\":327},{\"x\":191,\"y\":327}]},\"confidence\":0.99103385,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"c\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":201,\"y\":312},{\"x\":233,\"y\":312},{\"x\":233,\"y\":327},{\"x\":201,\"y\":327}]},\"confidence\":0.97219,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":201,\"y\":312},{\"x\":209,\"y\":312},{\"x\":209,\"y\":327},{\"x\":201,\"y\":327}]},\"confidence\":0.9763578,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":211,\"y\":312},{\"x\":217,\"y\":312},{\"x\":217,\"y\":327},{\"x\":211,\"y\":327}]},\"confidence\":0.9453312,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":219,\"y\":312},{\"x\":225,\"y\":312},{\"x\":225,\"y\":327},{\"x\":219,\"y\":327}]},\"confidence\":0.9849708,\"text\":\"d\"},{\"boundingBox\":{\"vertices\":[{\"x\":227,\"y\":312},{\"x\":233,\"y\":312},{\"x\":233,\"y\":327},{\"x\":227,\"y\":327}]},\"confidence\":0.9821002,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"e\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":237,\"y\":312},{\"x\":240,\"y\":312},{\"x\":240,\"y\":327},{\"x\":237,\"y\":327}]},\"confidence\":0.9814107,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":237,\"y\":312},{\"x\":240,\"y\":312},{\"x\":240,\"y\":327},{\"x\":237,\"y\":327}]},\"confidence\":0.9814107,\"text\":\"(\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":242,\"y\":312},{\"x\":264,\"y\":312},{\"x\":264,\"y\":327},{\"x\":242,\"y\":327}]},\"confidence\":0.97439766,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":242,\"y\":312},{\"x\":248,\"y\":312},{\"x\":248,\"y\":327},{\"x\":242,\"y\":327}]},\"confidence\":0.9876199,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":250,\"y\":312},{\"x\":256,\"y\":312},{\"x\":256,\"y\":327},{\"x\":250,\"y\":327}]},\"confidence\":0.9900825,\"text\":\"P\"},{\"boundingBox\":{\"vertices\":[{\"x\":258,\"y\":312},{\"x\":264,\"y\":312},{\"x\":264,\"y\":327},{\"x\":258,\"y\":327}]},\"confidence\":0.9454906,\"text\":\"P\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":266,\"y\":312},{\"x\":270,\"y\":312},{\"x\":270,\"y\":327},{\"x\":266,\"y\":327}]},\"confidence\":0.97628635,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":266,\"y\":312},{\"x\":270,\"y\":312},{\"x\":270,\"y\":327},{\"x\":266,\"y\":327}]},\"confidence\":0.97628635,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\")\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":275,\"y\":312},{\"x\":278,\"y\":312},{\"x\":278,\"y\":327},{\"x\":275,\"y\":327}]},\"confidence\":0.9559155,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":275,\"y\":312},{\"x\":278,\"y\":312},{\"x\":278,\"y\":327},{\"x\":275,\"y\":327}]},\"confidence\":0.9559155,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":284,\"y\":312},{\"x\":307,\"y\":312},{\"x\":307,\"y\":327},{\"x\":284,\"y\":327}]},\"confidence\":0.92584807,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":284,\"y\":312},{\"x\":291,\"y\":312},{\"x\":291,\"y\":327},{\"x\":284,\"y\":327}]},\"confidence\":0.96368253,\"text\":\"D\"},{\"boundingBox\":{\"vertices\":[{\"x\":293,\"y\":312},{\"x\":298,\"y\":312},{\"x\":298,\"y\":327},{\"x\":293,\"y\":327}]},\"confidence\":0.919595,\"text\":\"u\"},{\"boundingBox\":{\"vertices\":[{\"x\":301,\"y\":312},{\"x\":307,\"y\":312},{\"x\":307,\"y\":327},{\"x\":301,\"y\":327}]},\"confidence\":0.8942667,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"o\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":312,\"y\":312},{\"x\":315,\"y\":312},{\"x\":315,\"y\":327},{\"x\":312,\"y\":327}]},\"confidence\":0.9448823,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":312,\"y\":312},{\"x\":315,\"y\":312},{\"x\":315,\"y\":327},{\"x\":312,\"y\":327}]},\"confidence\":0.9448823,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":321,\"y\":312},{\"x\":348,\"y\":312},{\"x\":348,\"y\":327},{\"x\":321,\"y\":327}]},\"confidence\":0.9854583,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":321,\"y\":312},{\"x\":326,\"y\":312},{\"x\":326,\"y\":327},{\"x\":321,\"y\":327}]},\"confidence\":0.9866581,\"text\":\"L\"},{\"boundingBox\":{\"vertices\":[{\"x\":328,\"y\":312},{\"x\":331,\"y\":312},{\"x\":331,\"y\":327},{\"x\":328,\"y\":327}]},\"confidence\":0.9937914,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":330,\"y\":312},{\"x\":336,\"y\":312},{\"x\":336,\"y\":327},{\"x\":330,\"y\":327}]},\"confidence\":0.99423474,\"text\":\"v\"},{\"boundingBox\":{\"vertices\":[{\"x\":338,\"y\":312},{\"x\":341,\"y\":312},{\"x\":341,\"y\":327},{\"x\":338,\"y\":327}]},\"confidence\":0.99234974,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":340,\"y\":312},{\"x\":348,\"y\":312},{\"x\":348,\"y\":327},{\"x\":340,\"y\":327}]},\"confidence\":0.9602575,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"k\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":103,\"y\":465},{\"x\":158,\"y\":465},{\"x\":158,\"y\":520},{\"x\":103,\"y\":520}]},\"confidence\":0.9604392,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":103,\"y\":465},{\"x\":154,\"y\":465},{\"x\":154,\"y\":494},{\"x\":103,\"y\":494}]},\"confidence\":0.9219247,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":103,\"y\":465},{\"x\":125,\"y\":465},{\"x\":125,\"y\":494},{\"x\":103,\"y\":494}]},\"confidence\":0.81250936,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":103,\"y\":465},{\"x\":125,\"y\":465},{\"x\":125,\"y\":494},{\"x\":103,\"y\":494}]},\"confidence\":0.81250936,\"text\":\"/\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":123,\"y\":465},{\"x\":154,\"y\":465},{\"x\":154,\"y\":494},{\"x\":123,\"y\":494}]},\"confidence\":0.97663236,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":123,\"y\":465},{\"x\":142,\"y\":465},{\"x\":142,\"y\":494},{\"x\":123,\"y\":494}]},\"confidence\":0.9729718,\"text\":\"5\"},{\"boundingBox\":{\"vertices\":[{\"x\":142,\"y\":465},{\"x\":154,\"y\":465},{\"x\":154,\"y\":494},{\"x\":142,\"y\":494}]},\"confidence\":0.9802929,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"1\"}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":104,\"y\":507},{\"x\":158,\"y\":506},{\"x\":158,\"y\":519},{\"x\":104,\"y\":520}]},\"confidence\":0.97694546,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":104,\"y\":507},{\"x\":147,\"y\":506},{\"x\":147,\"y\":519},{\"x\":104,\"y\":520}]},\"confidence\":0.9854627,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":104,\"y\":508},{\"x\":112,\"y\":508},{\"x\":112,\"y\":520},{\"x\":104,\"y\":520}]},\"confidence\":0.9870934,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":111,\"y\":507},{\"x\":118,\"y\":507},{\"x\":118,\"y\":519},{\"x\":111,\"y\":519}]},\"confidence\":0.988211,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":118,\"y\":507},{\"x\":124,\"y\":507},{\"x\":124,\"y\":519},{\"x\":118,\"y\":519}]},\"confidence\":0.9951665,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":125,\"y\":507},{\"x\":132,\"y\":507},{\"x\":132,\"y\":519},{\"x\":125,\"y\":519}]},\"confidence\":0.99528486,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":132,\"y\":507},{\"x\":139,\"y\":507},{\"x\":139,\"y\":519},{\"x\":132,\"y\":519}]},\"confidence\":0.98529696,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":139,\"y\":507},{\"x\":147,\"y\":507},{\"x\":147,\"y\":519},{\"x\":139,\"y\":519}]},\"confidence\":0.9617238,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":150,\"y\":507},{\"x\":158,\"y\":507},{\"x\":158,\"y\":519},{\"x\":150,\"y\":519}]},\"confidence\":0.92584157,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":150,\"y\":507},{\"x\":158,\"y\":507},{\"x\":158,\"y\":519},{\"x\":150,\"y\":519}]},\"confidence\":0.92584157,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"C\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":14,\"y\":572},{\"x\":246,\"y\":572},{\"x\":246,\"y\":587},{\"x\":14,\"y\":587}]},\"confidence\":0.96966076,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":14,\"y\":572},{\"x\":246,\"y\":572},{\"x\":246,\"y\":587},{\"x\":14,\"y\":587}]},\"confidence\":0.96966076,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":14,\"y\":572},{\"x\":58,\"y\":572},{\"x\":58,\"y\":587},{\"x\":14,\"y\":587}]},\"confidence\":0.98619133,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":14,\"y\":572},{\"x\":22,\"y\":572},{\"x\":22,\"y\":587},{\"x\":14,\"y\":587}]},\"confidence\":0.98197585,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":22,\"y\":572},{\"x\":28,\"y\":572},{\"x\":28,\"y\":587},{\"x\":22,\"y\":587}]},\"confidence\":0.98810434,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":29,\"y\":572},{\"x\":35,\"y\":572},{\"x\":35,\"y\":587},{\"x\":29,\"y\":587}]},\"confidence\":0.9943929,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":37,\"y\":572},{\"x\":43,\"y\":572},{\"x\":43,\"y\":587},{\"x\":37,\"y\":587}]},\"confidence\":0.9940016,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":43,\"y\":572},{\"x\":50,\"y\":572},{\"x\":50,\"y\":587},{\"x\":43,\"y\":587}]},\"confidence\":0.98508614,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":50,\"y\":572},{\"x\":58,\"y\":572},{\"x\":58,\"y\":587},{\"x\":50,\"y\":587}]},\"confidence\":0.97358704,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":61,\"y\":572},{\"x\":101,\"y\":572},{\"x\":101,\"y\":587},{\"x\":61,\"y\":587}]},\"confidence\":0.98563015,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":61,\"y\":572},{\"x\":68,\"y\":572},{\"x\":68,\"y\":587},{\"x\":61,\"y\":587}]},\"confidence\":0.9525493,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":69,\"y\":572},{\"x\":72,\"y\":572},{\"x\":72,\"y\":587},{\"x\":69,\"y\":587}]},\"confidence\":0.9917375,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":71,\"y\":572},{\"x\":77,\"y\":572},{\"x\":77,\"y\":587},{\"x\":71,\"y\":587}]},\"confidence\":0.9875339,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":79,\"y\":572},{\"x\":85,\"y\":572},{\"x\":85,\"y\":587},{\"x\":79,\"y\":587}]},\"confidence\":0.99113315,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":86,\"y\":572},{\"x\":92,\"y\":572},{\"x\":92,\"y\":587},{\"x\":86,\"y\":587}]},\"confidence\":0.992745,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":92,\"y\":572},{\"x\":95,\"y\":572},{\"x\":95,\"y\":587},{\"x\":92,\"y\":587}]},\"confidence\":0.99288446,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":96,\"y\":572},{\"x\":101,\"y\":572},{\"x\":101,\"y\":587},{\"x\":96,\"y\":587}]},\"confidence\":0.99082786,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"c\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":106,\"y\":572},{\"x\":134,\"y\":572},{\"x\":134,\"y\":587},{\"x\":106,\"y\":587}]},\"confidence\":0.9701138,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":106,\"y\":572},{\"x\":114,\"y\":572},{\"x\":114,\"y\":587},{\"x\":106,\"y\":587}]},\"confidence\":0.9760545,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":115,\"y\":572},{\"x\":120,\"y\":572},{\"x\":120,\"y\":587},{\"x\":115,\"y\":587}]},\"confidence\":0.94939464,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":121,\"y\":572},{\"x\":127,\"y\":572},{\"x\":127,\"y\":587},{\"x\":121,\"y\":587}]},\"confidence\":0.9830445,\"text\":\"d\"},{\"boundingBox\":{\"vertices\":[{\"x\":129,\"y\":572},{\"x\":134,\"y\":572},{\"x\":134,\"y\":587},{\"x\":129,\"y\":587}]},\"confidence\":0.97196156,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"e\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":138,\"y\":572},{\"x\":142,\"y\":572},{\"x\":142,\"y\":587},{\"x\":138,\"y\":587}]},\"confidence\":0.978722,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":138,\"y\":572},{\"x\":142,\"y\":572},{\"x\":142,\"y\":587},{\"x\":138,\"y\":587}]},\"confidence\":0.978722,\"text\":\"(\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":144,\"y\":572},{\"x\":166,\"y\":572},{\"x\":166,\"y\":587},{\"x\":144,\"y\":587}]},\"confidence\":0.9803808,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":144,\"y\":572},{\"x\":150,\"y\":572},{\"x\":150,\"y\":587},{\"x\":144,\"y\":587}]},\"confidence\":0.9929802,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":152,\"y\":572},{\"x\":158,\"y\":572},{\"x\":158,\"y\":587},{\"x\":152,\"y\":587}]},\"confidence\":0.9927781,\"text\":\"P\"},{\"boundingBox\":{\"vertices\":[{\"x\":160,\"y\":572},{\"x\":166,\"y\":572},{\"x\":166,\"y\":587},{\"x\":160,\"y\":587}]},\"confidence\":0.9553841,\"text\":\"P\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":168,\"y\":572},{\"x\":172,\"y\":572},{\"x\":172,\"y\":587},{\"x\":168,\"y\":587}]},\"confidence\":0.9786537,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":168,\"y\":572},{\"x\":172,\"y\":572},{\"x\":172,\"y\":587},{\"x\":168,\"y\":587}]},\"confidence\":0.9786537,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\")\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":177,\"y\":572},{\"x\":181,\"y\":572},{\"x\":181,\"y\":587},{\"x\":177,\"y\":587}]},\"confidence\":0.9422689,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":177,\"y\":572},{\"x\":181,\"y\":572},{\"x\":181,\"y\":587},{\"x\":177,\"y\":587}]},\"confidence\":0.9422689,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":186,\"y\":572},{\"x\":206,\"y\":572},{\"x\":206,\"y\":587},{\"x\":186,\"y\":587}]},\"confidence\":0.8721534,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":186,\"y\":572},{\"x\":192,\"y\":572},{\"x\":192,\"y\":587},{\"x\":186,\"y\":587}]},\"confidence\":0.96509224,\"text\":\"D\"},{\"boundingBox\":{\"vertices\":[{\"x\":194,\"y\":572},{\"x\":199,\"y\":572},{\"x\":199,\"y\":587},{\"x\":194,\"y\":587}]},\"confidence\":0.94359314,\"text\":\"u\"},{\"boundingBox\":{\"vertices\":[{\"x\":201,\"y\":572},{\"x\":206,\"y\":572},{\"x\":206,\"y\":587},{\"x\":201,\"y\":587}]},\"confidence\":0.7077748,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"o\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":211,\"y\":572},{\"x\":215,\"y\":572},{\"x\":215,\"y\":587},{\"x\":211,\"y\":587}]},\"confidence\":0.92063427,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":211,\"y\":572},{\"x\":215,\"y\":572},{\"x\":215,\"y\":587},{\"x\":211,\"y\":587}]},\"confidence\":0.92063427,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":220,\"y\":572},{\"x\":246,\"y\":572},{\"x\":246,\"y\":587},{\"x\":220,\"y\":587}]},\"confidence\":0.99084973,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":220,\"y\":572},{\"x\":224,\"y\":572},{\"x\":224,\"y\":587},{\"x\":220,\"y\":587}]},\"confidence\":0.9837325,\"text\":\"L\"},{\"boundingBox\":{\"vertices\":[{\"x\":226,\"y\":572},{\"x\":228,\"y\":572},{\"x\":228,\"y\":587},{\"x\":226,\"y\":587}]},\"confidence\":0.99232864,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":230,\"y\":572},{\"x\":235,\"y\":572},{\"x\":235,\"y\":587},{\"x\":230,\"y\":587}]},\"confidence\":0.99431723,\"text\":\"v\"},{\"boundingBox\":{\"vertices\":[{\"x\":237,\"y\":572},{\"x\":239,\"y\":572},{\"x\":239,\"y\":587},{\"x\":237,\"y\":587}]},\"confidence\":0.9941666,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":240,\"y\":572},{\"x\":246,\"y\":572},{\"x\":246,\"y\":587},{\"x\":240,\"y\":587}]},\"confidence\":0.9897036,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"k\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":164,\"y\":634},{\"x\":492,\"y\":627},{\"x\":493,\"y\":677},{\"x\":165,\"y\":684}]},\"confidence\":0.963619,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":164,\"y\":634},{\"x\":492,\"y\":627},{\"x\":493,\"y\":677},{\"x\":165,\"y\":684}]},\"confidence\":0.963619,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":164,\"y\":634},{\"x\":262,\"y\":632},{\"x\":263,\"y\":682},{\"x\":165,\"y\":684}]},\"confidence\":0.95495605,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":164,\"y\":635},{\"x\":181,\"y\":635},{\"x\":182,\"y\":684},{\"x\":165,\"y\":684}]},\"confidence\":0.96277833,\"text\":\"B\"},{\"boundingBox\":{\"vertices\":[{\"x\":180,\"y\":634},{\"x\":195,\"y\":634},{\"x\":196,\"y\":683},{\"x\":181,\"y\":683}]},\"confidence\":0.9400032,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":195,\"y\":634},{\"x\":214,\"y\":634},{\"x\":215,\"y\":683},{\"x\":196,\"y\":683}]},\"confidence\":0.9345889,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":210,\"y\":633},{\"x\":228,\"y\":633},{\"x\":229,\"y\":682},{\"x\":211,\"y\":682}]},\"confidence\":0.9503635,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":225,\"y\":633},{\"x\":243,\"y\":633},{\"x\":244,\"y\":682},{\"x\":226,\"y\":682}]},\"confidence\":0.97008073,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":245,\"y\":633},{\"x\":262,\"y\":633},{\"x\":263,\"y\":682},{\"x\":246,\"y\":682}]},\"confidence\":0.97192156,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"R\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":271,\"y\":632},{\"x\":341,\"y\":630},{\"x\":342,\"y\":679},{\"x\":272,\"y\":681}]},\"confidence\":0.9823402,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":271,\"y\":632},{\"x\":285,\"y\":632},{\"x\":286,\"y\":681},{\"x\":272,\"y\":681}]},\"confidence\":0.9851323,\"text\":\"L\"},{\"boundingBox\":{\"vertices\":[{\"x\":284,\"y\":632},{\"x\":302,\"y\":632},{\"x\":303,\"y\":681},{\"x\":285,\"y\":681}]},\"confidence\":0.9772747,\"text\":\"U\"},{\"boundingBox\":{\"vertices\":[{\"x\":303,\"y\":631},{\"x\":320,\"y\":631},{\"x\":321,\"y\":680},{\"x\":304,\"y\":680}]},\"confidence\":0.9786599,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":323,\"y\":631},{\"x\":341,\"y\":631},{\"x\":342,\"y\":680},{\"x\":324,\"y\":680}]},\"confidence\":0.9882939,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"K\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":348,\"y\":630},{\"x\":415,\"y\":628},{\"x\":416,\"y\":678},{\"x\":349,\"y\":680}]},\"confidence\":0.98128915,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":348,\"y\":630},{\"x\":365,\"y\":630},{\"x\":366,\"y\":679},{\"x\":349,\"y\":679}]},\"confidence\":0.9919491,\"text\":\"N\"},{\"boundingBox\":{\"vertices\":[{\"x\":365,\"y\":630},{\"x\":380,\"y\":630},{\"x\":381,\"y\":679},{\"x\":366,\"y\":679}]},\"confidence\":0.98421603,\"text\":\"E\"},{\"boundingBox\":{\"vertices\":[{\"x\":379,\"y\":630},{\"x\":398,\"y\":630},{\"x\":399,\"y\":679},{\"x\":380,\"y\":679}]},\"confidence\":0.9732157,\"text\":\"X\"},{\"boundingBox\":{\"vertices\":[{\"x\":400,\"y\":629},{\"x\":415,\"y\":629},{\"x\":416,\"y\":678},{\"x\":401,\"y\":678}]},\"confidence\":0.9757757,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"T\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":423,\"y\":628},{\"x\":481,\"y\":627},{\"x\":482,\"y\":677},{\"x\":424,\"y\":678}]},\"confidence\":0.93835,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":423,\"y\":629},{\"x\":437,\"y\":629},{\"x\":438,\"y\":678},{\"x\":424,\"y\":678}]},\"confidence\":0.91479623,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":439,\"y\":628},{\"x\":445,\"y\":628},{\"x\":446,\"y\":677},{\"x\":440,\"y\":677}]},\"confidence\":0.93619496,\"text\":\"I\"},{\"boundingBox\":{\"vertices\":[{\"x\":448,\"y\":628},{\"x\":466,\"y\":628},{\"x\":467,\"y\":677},{\"x\":449,\"y\":677}]},\"confidence\":0.92268807,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":469,\"y\":628},{\"x\":481,\"y\":628},{\"x\":482,\"y\":677},{\"x\":470,\"y\":677}]},\"confidence\":0.9797209,\"text\":\"E\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":484,\"y\":627},{\"x\":492,\"y\":627},{\"x\":493,\"y\":676},{\"x\":485,\"y\":676}]},\"confidence\":0.97110754,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":484,\"y\":627},{\"x\":492,\"y\":627},{\"x\":493,\"y\":676},{\"x\":485,\"y\":676}]},\"confidence\":0.97110754,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"!\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":108,\"y\":682},{\"x\":339,\"y\":682},{\"x\":339,\"y\":696},{\"x\":108,\"y\":696}]},\"confidence\":0.96343863,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":108,\"y\":682},{\"x\":339,\"y\":682},{\"x\":339,\"y\":696},{\"x\":108,\"y\":696}]},\"confidence\":0.96343863,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":108,\"y\":682},{\"x\":150,\"y\":682},{\"x\":150,\"y\":696},{\"x\":108,\"y\":696}]},\"confidence\":0.98714995,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":108,\"y\":682},{\"x\":115,\"y\":682},{\"x\":115,\"y\":696},{\"x\":108,\"y\":696}]},\"confidence\":0.9832542,\"text\":\"R\"},{\"boundingBox\":{\"vertices\":[{\"x\":116,\"y\":682},{\"x\":122,\"y\":682},{\"x\":122,\"y\":696},{\"x\":116,\"y\":696}]},\"confidence\":0.9873148,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":122,\"y\":682},{\"x\":128,\"y\":682},{\"x\":128,\"y\":696},{\"x\":122,\"y\":696}]},\"confidence\":0.9937039,\"text\":\"n\"},{\"boundingBox\":{\"vertices\":[{\"x\":129,\"y\":682},{\"x\":135,\"y\":682},{\"x\":135,\"y\":696},{\"x\":129,\"y\":696}]},\"confidence\":0.9926227,\"text\":\"k\"},{\"boundingBox\":{\"vertices\":[{\"x\":137,\"y\":682},{\"x\":143,\"y\":682},{\"x\":143,\"y\":696},{\"x\":137,\"y\":696}]},\"confidence\":0.9875502,\"text\":\"e\"},{\"boundingBox\":{\"vertices\":[{\"x\":143,\"y\":682},{\"x\":150,\"y\":682},{\"x\":150,\"y\":696},{\"x\":143,\"y\":696}]},\"confidence\":0.97845393,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":154,\"y\":682},{\"x\":194,\"y\":682},{\"x\":194,\"y\":696},{\"x\":154,\"y\":696}]},\"confidence\":0.9880915,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":154,\"y\":682},{\"x\":160,\"y\":682},{\"x\":160,\"y\":696},{\"x\":154,\"y\":696}]},\"confidence\":0.9613659,\"text\":\"C\"},{\"boundingBox\":{\"vertices\":[{\"x\":162,\"y\":682},{\"x\":165,\"y\":682},{\"x\":165,\"y\":696},{\"x\":162,\"y\":696}]},\"confidence\":0.99395055,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":165,\"y\":682},{\"x\":171,\"y\":682},{\"x\":171,\"y\":696},{\"x\":165,\"y\":696}]},\"confidence\":0.99145186,\"text\":\"a\"},{\"boundingBox\":{\"vertices\":[{\"x\":171,\"y\":682},{\"x\":177,\"y\":682},{\"x\":177,\"y\":696},{\"x\":171,\"y\":696}]},\"confidence\":0.9933803,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":179,\"y\":682},{\"x\":185,\"y\":682},{\"x\":185,\"y\":696},{\"x\":179,\"y\":696}]},\"confidence\":0.9943334,\"text\":\"s\"},{\"boundingBox\":{\"vertices\":[{\"x\":185,\"y\":682},{\"x\":188,\"y\":682},{\"x\":188,\"y\":696},{\"x\":185,\"y\":696}]},\"confidence\":0.9940282,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":189,\"y\":682},{\"x\":194,\"y\":682},{\"x\":194,\"y\":696},{\"x\":189,\"y\":696}]},\"confidence\":0.9881306,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"c\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":198,\"y\":682},{\"x\":227,\"y\":682},{\"x\":227,\"y\":696},{\"x\":198,\"y\":696}]},\"confidence\":0.9722866,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":198,\"y\":682},{\"x\":206,\"y\":682},{\"x\":206,\"y\":696},{\"x\":198,\"y\":696}]},\"confidence\":0.9773086,\"text\":\"M\"},{\"boundingBox\":{\"vertices\":[{\"x\":208,\"y\":682},{\"x\":213,\"y\":682},{\"x\":213,\"y\":696},{\"x\":208,\"y\":696}]},\"confidence\":0.9295172,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":215,\"y\":682},{\"x\":220,\"y\":682},{\"x\":220,\"y\":696},{\"x\":215,\"y\":696}]},\"confidence\":0.9916238,\"text\":\"d\"},{\"boundingBox\":{\"vertices\":[{\"x\":222,\"y\":682},{\"x\":227,\"y\":682},{\"x\":227,\"y\":696},{\"x\":222,\"y\":696}]},\"confidence\":0.99069667,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"e\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":232,\"y\":682},{\"x\":235,\"y\":682},{\"x\":235,\"y\":696},{\"x\":232,\"y\":696}]},\"confidence\":0.9887712,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":232,\"y\":682},{\"x\":235,\"y\":682},{\"x\":235,\"y\":696},{\"x\":232,\"y\":696}]},\"confidence\":0.9887712,\"text\":\"(\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":237,\"y\":682},{\"x\":259,\"y\":682},{\"x\":259,\"y\":696},{\"x\":237,\"y\":696}]},\"confidence\":0.98082876,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":237,\"y\":682},{\"x\":243,\"y\":682},{\"x\":243,\"y\":696},{\"x\":237,\"y\":696}]},\"confidence\":0.98758286,\"text\":\"T\"},{\"boundingBox\":{\"vertices\":[{\"x\":245,\"y\":682},{\"x\":250,\"y\":682},{\"x\":250,\"y\":696},{\"x\":245,\"y\":696}]},\"confidence\":0.9919144,\"text\":\"P\"},{\"boundingBox\":{\"vertices\":[{\"x\":253,\"y\":682},{\"x\":259,\"y\":682},{\"x\":259,\"y\":696},{\"x\":253,\"y\":696}]},\"confidence\":0.962989,\"text\":\"P\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":261,\"y\":682},{\"x\":265,\"y\":682},{\"x\":265,\"y\":696},{\"x\":261,\"y\":696}]},\"confidence\":0.97846967,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":261,\"y\":682},{\"x\":265,\"y\":682},{\"x\":265,\"y\":696},{\"x\":261,\"y\":696}]},\"confidence\":0.97846967,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\")\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":270,\"y\":682},{\"x\":273,\"y\":682},{\"x\":273,\"y\":696},{\"x\":270,\"y\":696}]},\"confidence\":0.9105579,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":270,\"y\":682},{\"x\":273,\"y\":682},{\"x\":273,\"y\":696},{\"x\":270,\"y\":696}]},\"confidence\":0.9105579,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":279,\"y\":682},{\"x\":299,\"y\":682},{\"x\":299,\"y\":696},{\"x\":279,\"y\":696}]},\"confidence\":0.82229906,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":279,\"y\":682},{\"x\":285,\"y\":682},{\"x\":285,\"y\":696},{\"x\":279,\"y\":696}]},\"confidence\":0.95483243,\"text\":\"D\"},{\"boundingBox\":{\"vertices\":[{\"x\":287,\"y\":682},{\"x\":292,\"y\":682},{\"x\":292,\"y\":696},{\"x\":287,\"y\":696}]},\"confidence\":0.94034636,\"text\":\"u\"},{\"boundingBox\":{\"vertices\":[{\"x\":294,\"y\":682},{\"x\":299,\"y\":682},{\"x\":299,\"y\":696},{\"x\":294,\"y\":696}]},\"confidence\":0.5717184,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"o\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":304,\"y\":682},{\"x\":308,\"y\":682},{\"x\":308,\"y\":696},{\"x\":304,\"y\":696}]},\"confidence\":0.85793614,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":304,\"y\":682},{\"x\":308,\"y\":682},{\"x\":308,\"y\":696},{\"x\":304,\"y\":696}]},\"confidence\":0.85793614,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"-\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":313,\"y\":682},{\"x\":339,\"y\":682},{\"x\":339,\"y\":696},{\"x\":313,\"y\":696}]},\"confidence\":0.99124604,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":313,\"y\":682},{\"x\":318,\"y\":682},{\"x\":318,\"y\":696},{\"x\":313,\"y\":696}]},\"confidence\":0.97983044,\"text\":\"L\"},{\"boundingBox\":{\"vertices\":[{\"x\":320,\"y\":682},{\"x\":323,\"y\":682},{\"x\":323,\"y\":696},{\"x\":320,\"y\":696}]},\"confidence\":0.99259406,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":323,\"y\":682},{\"x\":328,\"y\":682},{\"x\":328,\"y\":696},{\"x\":323,\"y\":696}]},\"confidence\":0.9955703,\"text\":\"v\"},{\"boundingBox\":{\"vertices\":[{\"x\":329,\"y\":682},{\"x\":333,\"y\":682},{\"x\":333,\"y\":696},{\"x\":329,\"y\":696}]},\"confidence\":0.9958575,\"text\":\"i\"},{\"boundingBox\":{\"vertices\":[{\"x\":332,\"y\":682},{\"x\":339,\"y\":682},{\"x\":339,\"y\":696},{\"x\":332,\"y\":696}]},\"confidence\":0.9923778,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"k\"}]}]}]},{\"blockType\":\"TEXT\",\"boundingBox\":{\"vertices\":[{\"x\":246,\"y\":1047},{\"x\":317,\"y\":1047},{\"x\":317,\"y\":1173},{\"x\":246,\"y\":1173}]},\"confidence\":0.94972706,\"paragraphs\":[{\"boundingBox\":{\"vertices\":[{\"x\":261,\"y\":1047},{\"x\":297,\"y\":1047},{\"x\":297,\"y\":1072},{\"x\":261,\"y\":1072}]},\"confidence\":0.8568949,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":261,\"y\":1047},{\"x\":297,\"y\":1047},{\"x\":297,\"y\":1072},{\"x\":261,\"y\":1072}]},\"confidence\":0.8568949,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":261,\"y\":1047},{\"x\":276,\"y\":1047},{\"x\":276,\"y\":1072},{\"x\":261,\"y\":1072}]},\"confidence\":0.8463833,\"text\":\"I\"},{\"boundingBox\":{\"vertices\":[{\"x\":270,\"y\":1047},{\"x\":297,\"y\":1047},{\"x\":297,\"y\":1072},{\"x\":270,\"y\":1072}]},\"confidence\":0.86740655,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"V\"}]}]},{\"boundingBox\":{\"vertices\":[{\"x\":246,\"y\":1123},{\"x\":317,\"y\":1123},{\"x\":317,\"y\":1173},{\"x\":246,\"y\":1173}]},\"confidence\":0.9621047,\"words\":[{\"boundingBox\":{\"vertices\":[{\"x\":246,\"y\":1124},{\"x\":317,\"y\":1123},{\"x\":317,\"y\":1137},{\"x\":246,\"y\":1138}]},\"confidence\":0.9878545,\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":246,\"y\":1125},{\"x\":253,\"y\":1125},{\"x\":253,\"y\":1138},{\"x\":246,\"y\":1138}]},\"confidence\":0.9896374,\"text\":\"2\"},{\"boundingBox\":{\"vertices\":[{\"x\":254,\"y\":1124},{\"x\":261,\"y\":1124},{\"x\":261,\"y\":1137},{\"x\":254,\"y\":1137}]},\"confidence\":0.9918775,\"text\":\"3\"},{\"boundingBox\":{\"vertices\":[{\"x\":262,\"y\":1124},{\"x\":269,\"y\":1124},{\"x\":269,\"y\":1137},{\"x\":262,\"y\":1137}]},\"confidence\":0.99442434,\"text\":\"4\"},{\"boundingBox\":{\"vertices\":[{\"x\":270,\"y\":1124},{\"x\":277,\"y\":1124},{\"x\":277,\"y\":1137},{\"x\":270,\"y\":1137}]},\"confidence\":0.9858996,\"text\":\"6\"},{\"boundingBox\":{\"vertices\":[{\"x\":277,\"y\":1124},{\"x\":284,\"y\":1124},{\"x\":284,\"y\":1137},{\"x\":277,\"y\":1137}]},\"confidence\":0.98390555,\"text\":\"/\"},{\"boundingBox\":{\"vertices\":[{\"x\":286,\"y\":1124},{\"x\":293,\"y\":1124},{\"x\":293,\"y\":1137},{\"x\":286,\"y\":1137}]},\"confidence\":0.9873288,\"text\":\"2\"},{\"boundingBox\":{\"vertices\":[{\"x\":294,\"y\":1124},{\"x\":301,\"y\":1124},{\"x\":301,\"y\":1137},{\"x\":294,\"y\":1137}]},\"confidence\":0.9891205,\"text\":\"4\"},{\"boundingBox\":{\"vertices\":[{\"x\":302,\"y\":1124},{\"x\":309,\"y\":1124},{\"x\":309,\"y\":1137},{\"x\":302,\"y\":1137}]},\"confidence\":0.98795146,\"text\":\"0\"},{\"boundingBox\":{\"vertices\":[{\"x\":310,\"y\":1124},{\"x\":317,\"y\":1124},{\"x\":317,\"y\":1137},{\"x\":310,\"y\":1137}]},\"confidence\":0.9805451,\"property\":{\"detectedBreak\":{\"type\":\"EOL_SURE_SPACE\"}},\"text\":\"0\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":253,\"y\":1152},{\"x\":295,\"y\":1153},{\"x\":295,\"y\":1173},{\"x\":253,\"y\":1172}]},\"confidence\":0.9329197,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":253,\"y\":1152},{\"x\":265,\"y\":1152},{\"x\":265,\"y\":1172},{\"x\":253,\"y\":1172}]},\"confidence\":0.9524549,\"text\":\"G\"},{\"boundingBox\":{\"vertices\":[{\"x\":265,\"y\":1152},{\"x\":275,\"y\":1152},{\"x\":275,\"y\":1172},{\"x\":265,\"y\":1172}]},\"confidence\":0.9083988,\"text\":\"o\"},{\"boundingBox\":{\"vertices\":[{\"x\":275,\"y\":1152},{\"x\":282,\"y\":1152},{\"x\":282,\"y\":1172},{\"x\":275,\"y\":1172}]},\"confidence\":0.926637,\"text\":\"l\"},{\"boundingBox\":{\"vertices\":[{\"x\":282,\"y\":1152},{\"x\":295,\"y\":1152},{\"x\":295,\"y\":1172},{\"x\":282,\"y\":1172}]},\"confidence\":0.94418794,\"property\":{\"detectedBreak\":{\"type\":\"SPACE\"}},\"text\":\"d\"}]},{\"boundingBox\":{\"vertices\":[{\"x\":299,\"y\":1152},{\"x\":316,\"y\":1152},{\"x\":316,\"y\":1172},{\"x\":299,\"y\":1172}]},\"confidence\":0.9046007,\"property\":{\"detectedLanguages\":[{\"confidence\":1,\"languageCode\":\"en\"}]},\"symbols\":[{\"boundingBox\":{\"vertices\":[{\"x\":299,\"y\":1152},{\"x\":304,\"y\":1152},{\"x\":304,\"y\":1172},{\"x\":299,\"y\":1172}]},\"confidence\":0.8835425,\"text\":\"I\"},{\"boundingBox\":{\"vertices\":[{\"x\":303,\"y\":1152},{\"x\":316,\"y\":1152},{\"x\":316,\"y\":1172},{\"x\":303,\"y\":1172}]},\"confidence\":0.9256589,\"property\":{\"detectedBreak\":{\"type\":\"LINE_BREAK\"}},\"text\":\"V\"}]}]}]}],\"confidence\":0.90593064,\"height\":1215,\"property\":{\"detectedLanguages\":[{\"confidence\":0.89712167,\"languageCode\":\"en\"},{\"confidence\":0.0064938236,\"languageCode\":\"is\"}]},\"width\":586}],\"text\":\"#114\\nnmlsssss\\nFinishes 2\\nRanked Classic Mode (TPP) - Duo - Livik\\njarvis Friday\\nFinishes 4\\n/51\\nRanked\\nHAMLI\\n#11;\\nBETTER LUCK NEXT TIME!\\nRanked Classic Mode (TPP) - Duo - Livik\\n/51\\nRanked C\\nRanked Classic Mode (TPP) - Duo - Livik\\nBETTER LUCK NEXT TIME!\\nRanked Classic Mode (TPP) - Duo - Livik\\nIV\\n2346/2400\\nGold IV\"}"

val videoTests = arrayListOf(
    "/asingle_duo/", // passed
    "/complete_incomplete_inclomplete/", // passed
    "/exit_from_game/", // passed
    "/exit_from_lobby/", // passed
    "/game_with_a_long_time/", // passed
    "/less_kill_frames/", // passed
    "/less_overall_frames/", // passed
    "/less_rating_frames/", // passed
    "/single_solo/", // passed
    "/single_solo_no_spectate/", // passed, Failed over tier when call for automl.
    "/three_duo_match_spectate_in_first_two_no_spectate_in_last/", // Failed, wrong response from automl.  https://storage.cloud.google.com/gamerboard-dev.appspot.com/vcImage/9a63ab6f2b138823/2022-02-21/logs-08:32:20.jpeg
    "/three_solo_no_spectate/", // passed
    "/three_squad_matches_spectate_in_all/", // Fails, squad match fetched wrong kills.
    "/two_solo_matches/", //passed
)