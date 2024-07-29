package com.gamerboard.live.repository
import com.gamerboard.live.ModelParamQuery
import com.gamerboard.live.type.ESports
import com.gamerboard.logger.gson
import com.google.gson.Gson
import org.jetbrains.annotations.TestOnly

class ModelParamConst(val eSports: ESports) {

    val bgmiModelParam = """{
        "model_name": "String",
        "model_url": "game-models/bgmi_model_v5.tflite",
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 0,
            "name": "CLASSIC_RANK_GAME_INFO",
            "threshold": 0.3,
            "individualOCR": false,
            "sortOrder": "HORIZONTAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": true
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 1,
            "name": "CLASSIC_ALL_KILLS",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 2,
            "name": "CLASSIC_START",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 3,
            "name": "CLASSIC_ALL_WAITING",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 4,
            "name": "GAME_INFO",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 5,
            "name": "PROFILE_ID",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 6,
            "name": "GLOBAL_LOGIN",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 7,
            "name": "PROFILE_SELF",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 8,
            "name": "CLASSIC_RATING",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 9,
            "name": "CLASSIC_ALL_GAMEPLAY",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 10,
            "name": "RANK",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": true
        }
        }
        ],
        "bucket": {
        "resultRankRating": {
        "bucketFields": {
        "bufferSize": 1,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 8,
            "name": "CLASSIC_RATING",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 10,
            "name": "RANK",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    }
    },
        "resultRankKills": {
        "bufferSize": 1,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 1,
            "name": "CLASSIC_ALL_KILLS",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 10,
            "name": "RANK",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "resultRank": {
        "bufferSize": 1,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 10,
            "name": "RANK",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 4,
            "name": "GAME_INFO",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 0,
            "name": "CLASSIC_RANK_GAME_INFO",
            "threshold": 0.3,
            "individualOCR": false,
            "sortOrder": "HORIZONTAL",
            "mandatory": false,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "homeScreenBucket": {
        "bufferSize": 3,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 2,
            "name": "CLASSIC_START",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": false,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 5,
            "name": "PROFILE_ID",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": false,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 7,
            "name": "PROFILE_SELF",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "waitingScreenBucket": {
        "bufferSize": 4,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 3,
            "name": "CLASSIC_ALL_WAITING",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "gameScreenBucket": {
        "bufferSize": 4,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 9,
            "name": "CLASSIC_ALL_GAMEPLAY",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "loginScreenBucket": {
        "bufferSize": 2,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 6,
            "name": "GLOBAL_LOGIN",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "myProfileScreen": {
        "bufferSize": 1,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 2,
            "name": "CLASSIC_START",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 5,
            "name": "PROFILE_ID",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 7,
            "name": "PROFILE_SELF",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "playAgain": {
        "bufferSize": 3,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 100,
            "name": "PLAYAGAIN",
            "threshold": 1,
            "individualOCR": false,
            "sortOrder": "VERTICAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": false
        }
        }
        ]
    }
    }
    }"""




    val ffModelParam = """{
        "model_name": "String",
        "model_url": "game-models/ff_model_v5.tflite",
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 0,
            "name": "PLAYAGAIN",
            "threshold": 0.3,
            "individualOCR": false,
            "sortOrder": "VERTICAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 1,
            "name": "PROFILE_ID",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 2,
            "name": "CLASSIC_ALL_GAMEPLAY",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 3,
            "name": "PROFILE_SELF",
            "threshold": 0.15,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 4,
            "name": "RANK",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 5,
            "name": "CLASSIC_START",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 6,
            "name": "CLASSIC_ALL_KILLS",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "PERFORMANCE",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 7,
            "name": "CLASSIC_RATING",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 8,
            "name": "GAME_INFO",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 9,
            "name": "GLOBAL_LOGIN",
            "threshold": 0.15,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": null,
            "shouldPerformScaleAndStitching": false
        }
        }
        ],
        "bucket": {
        "resultRankRating": {
        "bufferSize": 1,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 7,
            "name": "CLASSIC_RATING",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "resultRankKills": {
        "bufferSize": 1,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 6,
            "name": "CLASSIC_ALL_KILLS",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "PERFORMANCE",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 4,
            "name": "RANK",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 8,
            "name": "GAME_INFO",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "resultRank": {
        "bufferSize": 0,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 4,
            "name": "RANK",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 8,
            "name": "GAME_INFO",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "homeScreenBucket": {
        "bufferSize": 3,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 5,
            "name": "CLASSIC_START",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": false,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 1,
            "name": "PROFILE_ID",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": false,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 3,
            "name": "PROFILE_SELF",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": false,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "waitingScreenBucket": {
        "bufferSize": 4,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 100,
            "name": "waiting",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "gameScreenBucket": {
        "bufferSize": 4,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 2,
            "name": "CLASSIC_ALL_GAMEPLAY",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "loginScreenBucket": {
        "bufferSize": 2,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 14,
            "name": "GLOBAL_LOGIN",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "HORIZONTAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "myProfileScreen": {
        "bufferSize": 1,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 1,
            "name": "PROFILE_ID",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 3,
            "name": "PROFILE_SELF",
            "threshold": 0.15,
            "individualOCR": true,
            "sortOrder": "VERTICAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    },
        "playAgain": {
        "bufferSize": 3,
        "labels": [
        {
            "__typename": "Label",
            "labelFields": {
            "index": 0,
            "name": "PLAYAGAIN",
            "threshold": 0.3,
            "individualOCR": false,
            "sortOrder": "VERTICAL",
            "mandatory": true,
            "shouldPerformScaleAndStitching": false
        }
        },
        {
            "__typename": "Label",
            "labelFields": {
            "index": 2,
            "name": "CLASSIC_ALL_GAMEPLAY",
            "threshold": 0.4,
            "individualOCR": true,
            "sortOrder": "SKIP",
            "mandatory": false,
            "shouldPerformScaleAndStitching": null
        }
        }
        ]
    }
    }
    }"""


    fun modelParamValues(): ModelParamQuery.ModelParam? {
        if (eSports == ESports.BGMI) {
            return gson.fromJson(bgmiModelParam, ModelParamQuery.ModelParam::class.java)
        } else {
            return gson.fromJson(ffModelParam, ModelParamQuery.ModelParam::class.java)
        }
    }
}

