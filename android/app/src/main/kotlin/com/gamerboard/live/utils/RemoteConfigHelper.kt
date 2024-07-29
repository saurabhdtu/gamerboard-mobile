package com.gamerboard.live.utils

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import kotlinx.coroutines.tasks.await

object RemoteConfigHelper {

   suspend fun fetchAndActivate(){
        try {
            val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig
            remoteConfig.fetchAndActivate().await()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }
}