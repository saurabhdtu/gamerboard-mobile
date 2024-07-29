package com.gamerboard.live.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.R
import com.gamerboard.live.common.PrefsHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Created by saurabh.lahoti on 13/08/21
 */
object PermissionHelper : KoinComponent{

    const val GRANTED = 0
    const val DENIED = 1
    const val BLOCKED = 2

    private val prefsHelper : PrefsHelper by inject()
    fun getPermissionStatus(
        prefsHelper: PrefsHelper,
        activity: Activity,
        androidPermissionName: String
    ): Int {
        val map = prefsHelper.getPermissionFlags()
        val flag =
            if (map.has(androidPermissionName)) map[androidPermissionName] as Boolean else false
        return if (ContextCompat.checkSelfPermission(
                activity,
                androidPermissionName
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return try {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        activity,
                        androidPermissionName
                    ) && flag
                ) {
                    BLOCKED
                } else
                    DENIED
            } catch (ex: Exception) {
                return if (ex is IllegalStateException && ex.message?.contains("Unknown permission") == true) {
                    GRANTED
                } else {
                    DENIED
                }
            }
        } else GRANTED
    }

    fun requestPermissions(
        permissions: Array<String>,
        requestCode: Int,
        fragment: Fragment
    ): Boolean {
        val sessionManager = GamerboardApp.instance.prefsHelper
        val finalPermissions = ArrayList<String>()
        for (permission in permissions) {
            val result =
                getPermissionStatus(sessionManager, fragment.requireActivity(), permission)
            if (result == BLOCKED) {
                DialogHelper.showSettingsDialog(
                    fragment.requireContext(),
                    fragment.getString(R.string.permission_from_settings)
                )
                return false
            } else if (result == DENIED) {
                finalPermissions.add(permission)
            }
        }
        return if (finalPermissions.isEmpty()) {
            true
        } else {
            fragment.requestPermissions(
                finalPermissions.toTypedArray(),
                requestCode
            )
            false
        }
    }

    fun requestPermissions(
        permissions: Array<String>,
        requestCode: Int,
        activity: Activity
    ): Boolean {
        val sessionManager = prefsHelper
        val finalPermissions = ArrayList<String>()
        for (permission in permissions) {
            val result =
                getPermissionStatus(sessionManager, activity, permission)
            if (result == BLOCKED) {
                DialogHelper.showSettingsDialog(
                    activity,
                    activity.getString(R.string.permission_from_settings)
                )
                return false
            } else if (result == DENIED) {
                finalPermissions.add(permission)
            }
        }
        return if (finalPermissions.isEmpty()) {
            true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                finalPermissions.toArray() as Array<out String>,
                requestCode
            )
            false
        }
    }

    fun onRequestPermissionResult(
        permissions: Array<out String>, grantResults: IntArray, activity: Activity
    ): Int {
        val sessionManager = GamerboardApp.instance.prefsHelper
        val grantedPermissions = ArrayList<String>()
        var result = GRANTED
        for (index in permissions.indices) {
            val permission = permissions[index]
            val grantResult = grantResults[index]
            if (result == GRANTED && grantResult == PackageManager.PERMISSION_DENIED) {
                result = DENIED
            }
            result =
                getPermissionStatus(sessionManager, activity, permission)
            when (result) {
                DENIED -> {
                    sessionManager.setPermissionFlags(permission, true)
                }
                GRANTED -> {
                    grantedPermissions.add(permission)
                }
                else -> {

                }
            }
        }
        return result
    }

}