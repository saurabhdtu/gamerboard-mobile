package com.gamerboard.live.utils

import android.content.Context
import com.gamerboard.live.common.PrefsHelper
import com.gamerboard.live.common.SharedPreferenceKeys

object FeatureHelper {

    fun getLoggingFlag(context : Context) : FeatureFlag{
       val isNewLogging =  PrefsHelper(context).getBoolean(SharedPreferenceKeys.NEW_LOGING_MODE)
        return if(isNewLogging) FeatureFlag.NEW_LOGGING else FeatureFlag.OLD
    }

    fun enableNewLoggingFlag(context: Context){
        PrefsHelper(context).putBoolean(SharedPreferenceKeys.NEW_LOGING_MODE, true)
    }

    fun disableNewLoggingFlag(context: Context) {
        PrefsHelper(context).putBoolean(SharedPreferenceKeys.NEW_LOGING_MODE, false)
    }
}