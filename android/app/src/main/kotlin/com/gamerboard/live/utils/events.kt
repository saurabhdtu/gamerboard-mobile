package com.gamerboard.live.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.amplitude.api.Amplitude
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.logger.gson
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.onesignal.OneSignal
import io.branch.referral.Branch
import io.branch.referral.util.BranchEvent
import org.json.JSONObject
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by saurabh.lahoti on 20/01/22
 */
class EventUtils : KoinComponent{
    private val context : Context by inject()

    private fun logAmplitudeEvent(eventName: String, jsonObject: JSONObject) =
        Amplitude.getInstance().logEvent(eventName, jsonObject)

    private fun logOneSignalEvent(eventName: String, jsonObject: JSONObject) {
        if (Events.ONE_SIGNAL_EVENTS.contains(eventName)) {
            val value =
                if (jsonObject.length() == 0) "0" else jsonObject.get(jsonObject.keys().next())
                    .toString()
            if (eventName == Events.APP_LAUNCHED && jsonObject.get("type")
                    .equals("fresh_install")
            ) {
                OneSignal.sendTag("app_install", value)
            } else {
                OneSignal.sendTag(eventName, value)
            }
        }
    }

    private fun logGAEvent(eventName: String, jsonObject: JSONObject) = Bundle().apply {
        jsonObject.keys().forEach {
            this.putString(it, jsonObject.get(it).toString())
        }
        FirebaseAnalytics.getInstance(context).logEvent(eventName, this)
    }


    private fun logBranchEvent(eventName: String, jsonObject: JSONObject) =
        BranchEvent(eventName).apply {
            setCustomerEventAlias(eventName)
            jsonObject.keys().forEach {
                this.addCustomDataProperty(it, jsonObject.get(it).toString())
            }
            logEvent(context)
        }


    private fun logUserPropertiesAmplitude(jsonObject: JSONObject) =
        Amplitude.getInstance().setUserProperties(jsonObject)

    private fun logUserPropertiesGA(jsonObject: JSONObject) = jsonObject.keys().forEach {
        FirebaseAnalytics.getInstance(context)
            .setUserProperty(it, jsonObject.get(it).toString())
    }


    companion object {
        private var inst: EventUtils? = null
        fun instance(): EventUtils {
            if (inst == null) inst = EventUtils()
            return inst!!
        }
    }

    fun logAnalyticsEvent(eventName: String, properties: Map<String, Any>) {
        val jsonObject = JSONObject(gson.toJson(properties))
        logAnalyticsEvent(eventName, jsonObject)
    }

    fun logAnalyticsEvent(eventName: String, jsonObject: JSONObject) {
        Log.d("logging_analytics_event", "event:$eventName : $jsonObject")
        val keysByPrefix = FirebaseRemoteConfig.getInstance().getKeysByPrefix("exp_")
        var currentGame = GamerboardApp.instance.prefsHelper.getString(SharedPreferenceKeys.CURRENT_GAME_NAME);
        jsonObject.put("game",currentGame)
        keysByPrefix.forEach { jsonObject.put(it, FirebaseRemoteConfig.getInstance().getString(it)) }
        logAmplitudeEvent(eventName, jsonObject)
        logGAEvent(eventName, jsonObject)
        logBranchEvent(eventName, jsonObject)
        logOneSignalEvent(eventName, jsonObject)
    }

    fun pushUserIdentity(id: String) {
        Amplitude.getInstance().userId = id
        OneSignal.setExternalUserId(id)
        Branch.getInstance().setIdentity(id)
        FirebaseAnalytics.getInstance(context).setUserId(id)
    }

    fun logUserProperties(properties: Map<String, Any?>) {
        val jsonObject = JSONObject(gson.toJson(properties))
        Log.d("logging_user_properties", "props: $jsonObject")
        logUserPropertiesAmplitude(jsonObject)
        logUserPropertiesGA(jsonObject)
    }

}

object Events {
    const val APP_LAUNCHED = "app_launched"
    const val GAME_LAUNCHED = "game_launched"
    const val PERMISSION_CLICKED = "permission_clicked"
    const val OVERLAY_SHOWN = "overlay_shown"
    const val OVERLAY_DISMISSED = "overlay_dismissed"
    const val GAME_PROFILE_ID_POPUP = "game_profile_id_popup_shown"
    const val GAME_PROFILE_ID_VERIFIED = "game_profile_id_verified"
    const val GAME_PROFILE_ID_ERROR = "game_profile_id_error"
    const val MINI_MENU_INTERACTED = "mini_menu_interacted"
    const val ALERT_BOX_INTERACTED = "alert_box_interacted"
    const val TUTORIAL_MATCH_COMPLETED = "tutorial_match_completed"
    const val TUTORIAL_MATCH_SUBMITTED = "tutorial_match_submitted_successfully"
    const val TUTORIAL_MATCH_ERROR = "tutorial_match_error"
    const val GAME_STARTED = "game_started"
    const val GAME_COMPLETED = "game_completed"
    const val GAME_SUBMITTED = "game_submitted"
    const val GAME_ERROR = "game_error"
    const val NOTIFICATION_CLICKED = "notification_clicked"
    const val FEEDBACK_POPUP_DISMISSED = "feedback_popup_dismissed"
    const val FEEDBACK_POPUP_SHOWN = "feedback_popup_shown"
    const val FEEDBACK_POPUP_SUBMITTED = "feedback_popup_submitted"
    const val NULL_TOKEN = "null_token"

    val ONE_SIGNAL_EVENTS = listOf(
        APP_LAUNCHED,
        "create_account_form_submitted",
        "game_tier_submitted",
        PERMISSION_CLICKED,
        GAME_PROFILE_ID_VERIFIED,
        GAME_SUBMITTED,
        "lb_joined",
        GAME_LAUNCHED,
        GAME_COMPLETED,
        "lb_clicked"
    )
}