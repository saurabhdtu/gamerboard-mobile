package com.gamerboard.live.service.screencapture

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.common.PlatformChannels
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.gamestatemachine.stateMachine.Event
import com.gamerboard.live.gamestatemachine.stateMachine.StateMachine
import com.gamerboard.live.repository.SessionManager
import com.gamerboard.live.service.screencapture.ui.ServiceManager
import com.gamerboard.logger.Logger
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.PluginRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


/**
 * Created by saurabh.lahoti on 24/08/21
 */
class ScreenCapturePlugin : ActivityAware, FlutterPlugin, MethodChannel.MethodCallHandler,
    KoinComponent,
    PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionsResultListener {

    private var mContext: Context? = null
    private var mActivity: Activity? = null
    private var serviceManager: ServiceManager? = null
    val logger: Logger by inject()
    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        mActivity = binding.activity
        binding.addActivityResultListener(this)
        binding.addRequestPermissionsResultListener(this)
        serviceManager?.activity = mActivity
        serviceManager?.let { (mActivity as FlutterActivity).lifecycle.addObserver(it) }
    }

    override fun onDetachedFromActivityForConfigChanges() {
        serviceManager?.let { (mActivity as FlutterActivity).lifecycle.removeObserver(it) }
        mActivity = null
        serviceManager?.activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        mActivity = binding.activity
        serviceManager?.activity = mActivity
    }

    override fun onDetachedFromActivity() {
        mActivity = null
        serviceManager?.activity = null
    }

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        mContext = binding.applicationContext
        val channel = MethodChannel(binding.binaryMessenger, PlatformChannels.BG_PLUGIN)
        channel.setMethodCallHandler(this)
        serviceManager = ServiceManager(mActivity, mContext, channel)
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        mContext = null
        serviceManager?.ctx = null
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "initialize_service" -> {
                val args = call.arguments<ArrayList<*>>()
                if (args != null) {
                    val callBackHandle = args[0] as Long
                    (mContext as GamerboardApp).prefsHelper.putLong(
                        SharedPreferenceKeys.KEY_CALLBACK_HANDLE,
                        callBackHandle
                    )
                }
                result.success("success")
            }

            "capture_service" -> {
                val gameProfileId = call.argument<String>("game_profile_id")
                val gameProfileName = call.argument<String>("game_profile_name")
                val gamePackage = call.argument<String>("game_package")
                val auth = call.argument<String>("auth_token")
                val appRestarted = call.argument<Boolean>("app_restarted") ?: false
                if ((mContext?.applicationContext is GamerboardApp)) {
                    auth?.let {
                        auth
                        (mContext as GamerboardApp).prefsHelper.also {
                            it.putString(SharedPreferenceKeys.AUTH_TOKEN, auth)
                            it.putString(
                                SharedPreferenceKeys.LAST_LAUNCHED_GAME,
                                gamePackage
                            )
                        }
                        val askAdditionalPermission =
                            call.argument<Boolean>("ask_additional_permission")
                        val onBoarding = gameProfileId == null
                        if (gamePackage != null) {
                            Log.i("ScreenCapturePlugin", "game profile: $gameProfileId; $gameProfileName")
                            serviceManager?.checkPermissionsForService(
                                gameProfileId = gameProfileId,
                                gameProfileName = gameProfileName,
                                askAdditionalPermission = askAdditionalPermission,
                                packageName = gamePackage,
                                appRestarted = appRestarted
                            )

                            // from here we initialize the user
                            StateMachine.machine.transition(
                                Event.SetOriginalGameProfile(
                                    gameProfileId,
                                    gameProfileName
                                )
                            )
                            StateMachine.machine.transition(Event.SetOnBoarding(onBoarding))
                        }
                    }
                }
                result.success("success")

            }

            "stop_service" -> {
                CoroutineScope(Dispatchers.IO).launch {
                    SessionManager.clearSession()
                }
                serviceManager?.stopService("Stopped service on logout")
            }

            "check_service_status" -> {
                serviceManager?.checkServiceStatus {
                    result.success(it)
                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        serviceManager?.onActivityResult(requestCode, resultCode, data)
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        serviceManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        return false
    }

}