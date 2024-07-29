package com.gamerboard.live.common

import com.gamerboard.live.BuildConfig
import com.gamerboard.live.R
import com.gamerboard.live.type.ESports
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.json.JSONObject

/**
 * Created by saurabh.lahoti on 04/08/21
 */

val GAME_PACKAGES = if (BuildConfig.IS_TEST) arrayOf(
    "com.google.android.apps.photos",
    "org.videolan.vlc"
) else SupportedGames.values().map { it.packageName }.toTypedArray()


val SYSTEM_PACKAGES = arrayOf(
    "com.android.systemui",
    "android.incallui",
    "launcher",
    "systemui",
    "com.miui.home",
    "com.android.settings",
    "com.google.android.inputmethod"
)

fun String.toSupportedGame(): SupportedGames = when (this) {
    SupportedGames.BGMI.packageName -> SupportedGames.BGMI

    SupportedGames.FREEFIRE.packageName -> SupportedGames.FREEFIRE

    else -> SupportedGames.BGMI
}

enum class SupportedGames {
    BGMI,
    FREEFIRE;

    val packageName: String
        get() = when (this) {
            BGMI -> "com.pubg.imobile"
            FREEFIRE -> "com.dts.freefiremax"
        }
    val gameName: String
        get() = when (this) {
            BGMI -> "BGMI"
            FREEFIRE -> "Freefire"
        }

    val findProfileId: Int
        get() = when (this) {
            BGMI -> R.string.bgmi_id_number_can_be_found
            FREEFIRE -> R.string.freefire_id_number_can_be_found
        }

    val verifyProfileImage: Int
        get() = when (this) {
            BGMI -> R.drawable.iv_bgmi_verify_profile
            FREEFIRE -> R.drawable.iv_freefire_verify_profile
        }

    val verifyGameProfile: Int
        get() = when (this) {
            BGMI -> R.string.go_to_your_bgmi_profile_page_to_verify_your_session
            FREEFIRE -> R.string.go_to_your_freefire_profile_page_to_verify_your_session
        }

    val pleaseVerifyGameProfile: Int
        get() = when (this) {
            BGMI -> R.string.please_go_to_your_bgmi_profile_page_to_verify_your_session
            FREEFIRE -> R.string.please_go_to_your_freefire_profile_page_to_verify_your_session
        }


    val verifyGameIdImage: Int
        get() = when (this) {
            BGMI -> R.drawable.iv_bgmi_verify_id
            FREEFIRE -> R.drawable.iv_freefire_verify_id
        }

    val overlayProfileVerification: Int
        get() = when (this) {
            BGMI -> R.drawable.bgmi_profile_verification_overlay
            FREEFIRE -> R.drawable.ff_profile_verification_overlay
        }
    val verifyStartImage: Int
        get() = when (this) {
            BGMI -> R.drawable.iv_bgmi_start
            FREEFIRE -> R.drawable.iv_freefire_start
        }
    val verifyStartText: Int
        get() = when (this) {
            BGMI -> R.string.ready_to_play_content_bgmi
            FREEFIRE -> R.string.ready_to_play_content_freefire
        }

    val modelURL: String
        get() = when (this) {
            BGMI -> "${BuildConfig.GS_BUCKET_URL}/game-models/bgmi_model_v5.tflite"
            FREEFIRE -> "${BuildConfig.GS_BUCKET_URL}/game-models/ff_model_v4.tflite"
        }
    val minKillScreenLabelCount: Int
        get() = when (this) {
            BGMI -> 3
            FREEFIRE -> 3
        }
    val minRatingScreenLabelCount: Int
        get() = when (this) {
            BGMI -> 3
            FREEFIRE -> 1
        }

    //    val minPerformanceScreenLabelCount: Int
//        get() = when (this) {
//            BGMI -> Int.MAX_VALUE
//            FREEFIRE -> 3//only considering labels which are required for ocr
//        }
    val eSport: ESports
        get() = when (this) {
            BGMI -> ESports.BGMI
            FREEFIRE -> ESports.FREEFIREMAX
        }
    val smallestCharPixelPercentage: Float
        get() = textSizeBoundRemoteConfig().getDouble("min").toFloat()
    val largestCharPixelPercentage: Float
        get() = textSizeBoundRemoteConfig().getDouble("max").toFloat()

    private fun textSizeBoundRemoteConfig(): JSONObject =
        JSONObject(
            FirebaseRemoteConfig.getInstance().getString("ocr_text_size_bound")
        ).getJSONObject(this.packageName)
}

object AppNotificationChannel {
    const val FOREGROUND_CAPTURE_SERVICE = "foreground_capture_service"
}

object AppNotificationID {
    const val FOREGROUND_CAPTURE_SERVICE = 1009
}

object BroadcastFilters {
    const val RESPONSE_CAPTURE_SERVICE = "response_capture_service_status"
    const val SERVICE_COM = "service_com"
    const val NATIVE_TO_FLUTTER = "native_to_flutter"
}

object IntentRequestCode {
    const val MEDIA_PROJECTION_RC = 100
    const val OPEN_FILE_RC = 601
    const val TEST_VDO_COMPLETION = 500
}

object IntentKeys {
    const val LAUNCH_GAME = "launch_game"
    const val APP_RESTARTED = "app_restarted"
    const val RESTART_CLICKED = "restart_clicked"
    const val NOTIFICATION_CLICKED = "notification_clicked"
}

object PermissionRequestCode {
    const val FOREGROUND_SERVICE = 200
    const val INSTALL_APP_UNKNOWN_SOURCE = 602

}

object PlatformChannels {
    const val APPLICATION_CHANNEL = "com.gamerboard.live/platform"
    const val LOCAL_CHANNEL = "com.gamerboard.live/local"
    const val BG_PLUGIN = "com.gamerboard.live/bg_plugin"
    const val BG_PLUGIN_SERVICE = "com.gamerboard.live/bg_service"
}


object VideoTestConstants {
    const val CURRENT_VIDEO_TEST = "currentVideoTest"
    const val VIDEO_TESTS = "videoTests"
}