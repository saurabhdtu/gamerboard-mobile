package com.gamerboard.live.common

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.Keep
import org.json.JSONObject

/**
 * Created by saurabh.lahoti on 13/08/21
 */
@Keep
class PrefsHelper constructor(val context: Context) {
    private val KEY_PERMS_FLAG = "permission_data"
    private val pref: SharedPreferences = context.getSharedPreferences(
        "${context.packageName}_my_preferences",
        0
    )
    var editor: SharedPreferences.Editor = pref.edit()


    fun getPermissionFlags(): JSONObject {
        return JSONObject(
            pref.getString(
                KEY_PERMS_FLAG,
                "{}"
            )
        )
    }

    fun setPermissionFlags(permission: String, value: Boolean) {
        val map = getPermissionFlags()
        map.put(permission, value)
        editor.putString(
            KEY_PERMS_FLAG,
            map.toString()
        ).apply()
    }

    fun putLong(key: String, value: Long) {
        editor.putLong(
            key,
            value
        ).apply()
    }

    fun getLong(key: String): Long {
        return pref.getLong(key, -1)
    }

    fun putString(key: String, value: String?) {
        editor.putString(
            key,
            value
        ).apply()
    }

    fun getString(key: String): String? {
        return pref.getString(key, null)
    }

    fun clearData() {
        editor.clear()
        editor.apply()
    }

    fun putBoolean(key: String, value: Boolean?) {
        value?.let {
            editor.putBoolean(
                key,
                it
            ).apply()
        }
    }

    fun getInt(key: String): Int {
        return pref.getInt(key, 0)
    }

    fun putInt(key: String, value: Int) {
        editor.putInt(
            key,
            value
        ).apply()
    }

    fun getBoolean(key: String): Boolean {
        return pref.getBoolean(key, false)
    }
}

object SharedPreferenceKeys {
    const val LATEST_DOWNLOADED_VERSION="latest_downloaded_version"
    const val USER_ID="user_id"
    const val UTM_PARAMS = "utm_params"
    const val AUTO_START = "auto_start_permission"
    const val LAST_VERSION = "last_version"
    const val SESSION_COUNT = "session_count"
    const val LAST_LAUNCHED_GAME = "last_launched_game"
    const val KEY_CALLBACK_HANDLE = "callback_handle_key"
    const val UUID = "unique_identifier"
    const val APP_STATE = "app_state"
    const val RUN_TUTORIAL = "run_tutorial"
    /*const val RUN_TUTORIAL_POST_GAME = "run_tutorial_post_game"*/
    const val RUN_TUTORIAL_IN_GAME = "run_tutorial_in_game"
    const val AUTH_TOKEN = "auth_token"
    const val FEEDBACK_DATA = "feedback_data"
    const val FFMAX_FLAG = "ffmax_flag"
    const val KILL_ALGO_FLAG = "kill_algo_flag"
    const val GAME_ID_VERIFICATION = "game_id_verification"
    const val NEW_LOGING_MODE = "new_loging_mode"
    const val NEW_PROFILE_VERIFY_UI = "profile_nudge"

    const val SHOW_VERIFICATION_ONLY = "show_verification_only"

    const val CURRENT_GAME_NAME = "current_game_name"

}