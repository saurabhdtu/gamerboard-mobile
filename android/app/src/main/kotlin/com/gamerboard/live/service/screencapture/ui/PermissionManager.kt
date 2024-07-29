package com.gamerboard.live.service.screencapture.ui

import android.app.Activity
import android.app.AppOpsManager
import android.app.Dialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.R
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.service.MyAccessibilityService
import com.gamerboard.live.utils.EventUtils
import com.gamerboard.live.utils.Events
import com.gamerboard.live.utils.logException
import com.gamerboard.logger.log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent


/**
 * Created by saurabh.lahoti on 03/09/21
 */
class PermissionManager(
    private val activity: Activity,
    private val serviceManager: ServiceManager?,
    var askAdditionalPermission: Boolean = false
) : KoinComponent {
    val BATTERY_OPTIMIZATION = 101
    val DRAW_OVER_OTHER_APPS = 102
    val APP_USAGE = 103
    val ACCESSIBILITY = 104
    val AUTO_START = 105
    var lastRequestCode = 0
    var dialog: Dialog? = null
    val prefsHelper = (activity.applicationContext as GamerboardApp).prefsHelper

    private fun verifyBrandForAutoStart(): Boolean {
        val intent = Intent()
        val manufacturer = Build.MANUFACTURER
        when {
            "xiaomi".equals(manufacturer, ignoreCase = true) -> {
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }

            "oppo".equals(manufacturer, ignoreCase = true) -> {
                intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            }

            "vivo".equals(manufacturer, ignoreCase = true) -> {
                intent.component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            }

            "Letv".equals(manufacturer, ignoreCase = true) -> {
                intent.component = ComponentName(
                    "com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"
                )
            }

            "Honor".equals(manufacturer, ignoreCase = true) -> {
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            }
        }
        val list: List<ResolveInfo> = activity.packageManager.queryIntentActivities(
            intent, PackageManager.MATCH_DEFAULT_ONLY
        )
        return list.isNotEmpty()
    }

    private fun appUsageAccessGranted(launchPermission: Boolean): Boolean {
        log("checking app usage permission $launchPermission")

        val result = try {
            val packageManager = activity.packageManager
            val applicationInfo = packageManager.getApplicationInfo(activity.packageName, 0)
            val appOpsManager: AppOpsManager? =
                activity.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager?
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOpsManager?.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
            } else {
                appOpsManager?.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid,
                    applicationInfo.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
        log("App usage Permission result $result")
        if (!result && launchPermission) {
            activity.startActivityForResult(
                Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), APP_USAGE
            )
        }
        return result
    }

    private fun checkBatteryPermission(launchPermission: Boolean): Boolean {
        log("battery optimization Permission  $launchPermission")

        val packageName = activity.packageName
        val pm: PowerManager? = activity.getSystemService(Context.POWER_SERVICE) as PowerManager?
        val result = if (pm != null) {
            val b = if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                if (launchPermission) {
                    val intent = Intent()
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:$packageName")
                    activity.startActivityForResult(
                        intent, BATTERY_OPTIMIZATION
                    )
                }
                false
            } else {
                true
            }
            b
        } else {
            false
        }
        log("Battery optimization Permission result $result")
        return result
    }

    private fun isAccessibilityEnabled(launchPermission: Boolean): Boolean {
        var accessibilityEnabled = 0
        val ACCESSIBILITY_SERVICE =
            "${activity.packageName}/${MyAccessibilityService::class.java.name}"
        var accessibilityFound = false
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                activity.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        val mStringColonSplitter: TextUtils.SimpleStringSplitter =
            TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                activity.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )

            if (settingValue != null) {
                val splitter: TextUtils.SimpleStringSplitter = mStringColonSplitter
                splitter.setString(settingValue)
                while (splitter.hasNext()) {
                    val accessibilityService: String = splitter.next()
                    accessibilityFound = accessibilityService.equals(
                        MyAccessibilityService::class.java.name, ignoreCase = true
                    ) || accessibilityService == ACCESSIBILITY_SERVICE
                    if (accessibilityFound) break
                }
            }
        }
        if (!accessibilityFound && launchPermission) activity.startActivityForResult(
            Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS), ACCESSIBILITY
        )

        return accessibilityFound
    }

    private fun drawOverOtherAppsGranted(launchPermission: Boolean): Boolean {
        log("Check Draw over other apps Permission  $launchPermission")
        val result = if (!Settings.canDrawOverlays(activity.applicationContext)) {
            if (launchPermission) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.packageName)
                )
                activity.startActivityForResult(
                    intent, DRAW_OVER_OTHER_APPS
                )
            }
            false
        } else true
        log("Draw over other apps Permission result $result")
        return result
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            AUTO_START, ACCESSIBILITY, APP_USAGE, BATTERY_OPTIMIZATION, DRAW_OVER_OTHER_APPS -> {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(600)
                    if (requestCode == BATTERY_OPTIMIZATION) checkBatteryPermission(false)
                    delay(600)
                    checkPermissions()
                }
                // to avoid endless openings of permissions
            }
//
            /*DRAW_OVER_OTHER_APPS -> {
                if (drawOverOtherAppsGranted(false))
                    serviceManager?.checkPermissionsForService(null, null)
            }*/
        }
    }

    private fun addAutoStartup(context: Context) {
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER
            when {
                "xiaomi".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.autostart.AutoStartManagementActivity"
                    )
                }

                "oppo".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.coloros.safecenter",
                        "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                    )
                }

                "vivo".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.vivo.permissionmanager",
                        "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                    )
                }

                "Letv".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.letv.android.letvsafe",
                        "com.letv.android.letvsafe.AutobootManageActivity"
                    )
                }

                "Honor".equals(manufacturer, ignoreCase = true) -> {
                    intent.component = ComponentName(
                        "com.huawei.systemmanager",
                        "com.huawei.systemmanager.optimize.process.ProtectActivity"
                    )
                }
            }
            val list: List<ResolveInfo> = context.packageManager.queryIntentActivities(
                intent, PackageManager.MATCH_DEFAULT_ONLY
            )
            if (list.isNotEmpty()) {
                activity.startActivityForResult(intent, AUTO_START)
                prefsHelper.putBoolean(SharedPreferenceKeys.AUTO_START, true)
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    fun checkPermissions(): Boolean {
        try {
            val autoRun = verifyBrandForAutoStart() && !prefsHelper.getBoolean(
                SharedPreferenceKeys.AUTO_START
            )
            val accessibility =
                true//if (askAdditionalPermission) isAccessibilityEnabled(false) else true
            val usageAccess = if (askAdditionalPermission) appUsageAccessGranted(false) else true
            val batteryOptimisation = checkBatteryPermission(false)
            val drawOverOtherApps = drawOverOtherAppsGranted(false)
            var launchDialog = false
            if (dialog?.isShowing == false) dialog = null
            if (dialog == null || dialog!!.isShowing.not()) {
                launchDialog = true
                dialog = Dialog(activity)
                dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog!!.setContentView(R.layout.layout_permission)
            }
            val view: View = dialog!!.findViewById(R.id.layout_permission_dialog)

            val p = Point()
            activity.windowManager.defaultDisplay.getSize(p)
            dialog!!.window?.setLayout(
                (p.x * 0.7f).toInt(),
                (p.y * 0.95f).toInt(),
            )
            dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val button = view.findViewById<Button>(R.id.btn_continue)
            view.findViewById<View>(R.id.layout_autorun).visibility =
                if (verifyBrandForAutoStart()) View.VISIBLE else View.GONE
            if (autoRun.not()) view.findViewById<ImageView>(R.id.iv_autostart)
                .setImageResource(R.drawable.ic_check_circle)
            else {
                view.findViewById<ImageView>(R.id.iv_autostart)
                    .setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                view.findViewById<View>(R.id.layout_autorun).setOnClickListener {
                    EventUtils.instance().logAnalyticsEvent(
                        Events.PERMISSION_CLICKED, mapOf("permission" to "auto_start")
                    )
                    addAutoStartup(activity)
                }
            }
            view.findViewById<View>(R.id.layout_accessibility).visibility = View.GONE
            if (!askAdditionalPermission) {
                view.findViewById<View>(R.id.layout_app_usage).visibility = View.GONE
            } else {
                /* if (accessibility)
                view.findViewById<ImageView>(R.id.iv_accessibility)
                    .setImageResource(R.drawable.ic_check_circle)
            else {
                view.findViewById<ImageView>(R.id.iv_accessibility)
                    .setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                view.findViewById<View>(R.id.layout_accessibility).setOnClickListener {
                    EventUtils.instance().logAnalyticsEvent(
                        Events.PERMISSION_CLICKED,
                        mapOf("permission" to "accessibility")
                    )
                    isAccessibilityEnabled(true)
                }
            }*/
                if (usageAccess) view.findViewById<ImageView>(R.id.iv_app_usage)
                    .setImageResource(R.drawable.ic_check_circle)
                else {
                    view.findViewById<ImageView>(R.id.iv_app_usage)
                        .setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                    view.findViewById<View>(R.id.layout_app_usage).setOnClickListener {
                        EventUtils.instance().logAnalyticsEvent(
                            Events.PERMISSION_CLICKED, mapOf("permission" to "app_usage")
                        )
                        appUsageAccessGranted(true)
                    }
                }
            }

            if (batteryOptimisation) view.findViewById<ImageView>(R.id.iv_battery)
                .setImageResource(R.drawable.ic_check_circle)
            else {
                view.findViewById<ImageView>(R.id.iv_battery)
                    .setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                view.findViewById<View>(R.id.layout_battery).setOnClickListener {
                    EventUtils.instance().logAnalyticsEvent(
                        Events.PERMISSION_CLICKED, mapOf("permission" to "battery_optimization")
                    )
                    checkBatteryPermission(true)
                }
            }

            if (drawOverOtherApps) view.findViewById<ImageView>(R.id.iv_draw_over)
                .setImageResource(R.drawable.ic_check_circle)
            else {
                view.findViewById<ImageView>(R.id.iv_draw_over)
                    .setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                view.findViewById<View>(R.id.layout_draw_over).setOnClickListener {
                    EventUtils.instance().logAnalyticsEvent(
                        Events.PERMISSION_CLICKED, mapOf("permission" to "draw_over_other_apps")
                    )
                    drawOverOtherAppsGranted(true)
                }
            }
            val permission =
                autoRun.not() && accessibility && usageAccess && batteryOptimisation && drawOverOtherApps
            button.isEnabled = permission
            button.setOnClickListener {
                dialog!!.dismiss()
                serviceManager?.checkPermissionsForService(null, null, null, null)
            }
            if (launchDialog && !permission) dialog!!.show()
            dialog?.setOnDismissListener {
                dialog = null
            }
            return permission
        } catch (ex: Exception) {
            logException(ex)
        }
        return false
    }

}