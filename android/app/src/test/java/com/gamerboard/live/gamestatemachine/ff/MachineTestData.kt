package com.gamerboard.live.gamestatemachine.ff.stateMachine

import androidx.annotation.Keep
import com.gamerboard.live.gamestatemachine.games.freefire.FreeFireConstants
import com.gamerboard.live.models.ImageResultJsonFlat
import com.gamerboard.live.models.Resolution
import com.gamerboard.live.models.TFResult
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
        resultJsonFlat.labels = listOf(TFResult(
            label = label.label.toInt(),
            confidence = label.confidence,
            ocr = ocrText,
            box = label.box,
            resolution = Resolution(1280, 720)
        ))
    }
    return resultJsonFlat
}

fun String.obj(): ImageResultJsonFlat {
    return flattenResultJsonOld(Json.decodeFromString(this) as ImageResultJsonOld)
}


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
          "label": ${FreeFireConstants.GameLabels.SELF_MENU.ordinal},
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
          "label": ${FreeFireConstants.GameLabels.PROFILE.ordinal},
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

val USER1_USERNAME_and_ID_RAW_JSON: String =
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
          "label": ${FreeFireConstants.GameLabels.SELF_MENU.ordinal},
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
          "label": ${FreeFireConstants.GameLabels.PROFILE.ordinal},
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
          "label": ${FreeFireConstants.GameLabels.LOGIN.ordinal},
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
            "label": ${FreeFireConstants.GameLabels.START.ordinal},
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

object TestUserff1 {
    const val id: String = "pratyushtiwari"
    const val level: String = "Player Lv. 22"
    const val idNumeric: String = "123456798"
    const val initialTier = "Gold VI"
    const val finalTier = "old III"

}

//TO DO: change gameInfo based on ff
val TestGame1 = Game(
    userId = TestUserff1.id,
    valid = true,
    rank = "74/99",
    gameInfo = "Classic (TPP) solo-Erangel",
    kills = "finishes 1",
    teamRank = "Team Rank # 23",
    startTimeStamp = "Un-Known",
    endTimestamp = "Un-Known",
    gameId = "Un-Known",
    initialTier = "BRONZE I",
    finalTier = "BRONZE II",
    metaInfoJson = "Un-Known"
)

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
          "label": ${FreeFireConstants.GameLabels.BR_RATING.ordinal},
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
          "label": ${FreeFireConstants.GameLabels.BR_GAME_INFO.ordinal},
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
          "label": ${FreeFireConstants.GameLabels.BR_RANK.ordinal},
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
          "label": ${FreeFireConstants.GameLabels.BR_GAME_INFO.ordinal},
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
          "label": ${FreeFireConstants.GameLabels.BR_KILL.ordinal},
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

val USER1_GAME1_PERFORMANCE_RAW_JSON: String =
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
          "label": ${FreeFireConstants.GameLabels.BR_RANK.ordinal},
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
          "label": ${FreeFireConstants.GameLabels.BR_RATING.ordinal},
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
          "label": ${FreeFireConstants.GameLabels.BR_GAME_INFO.ordinal},
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

