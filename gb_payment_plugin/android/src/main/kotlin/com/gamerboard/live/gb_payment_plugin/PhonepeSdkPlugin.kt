package com.gamerboard.live.gb_payment_plugin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import com.phonepe.intent.sdk.api.PhonePe
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry


/** PhonepeSdkPlugin */
class PhonepeSdkPlugin : FlutterPlugin, ActivityAware, MethodCallHandler,
    PluginRegistry.ActivityResultListener {

    companion object {
        private val TAG = PhonepeSdkPlugin::class.java.simpleName

        private const val METHOD_GET_UPI_APPS = "getUpiApps"
        private const val METHOD_START_PAYMENT = "startPayment"
        private const val B2B_PG_REQUEST_CODE = 33192


        private var flutterResult: MethodChannel.Result? = null
        private val allowedUpiApps = arrayOf(
            UpiApp.GPAY,
            UpiApp.PAYTM,
            UpiApp.PHONEPE,
            UpiApp.AMAZON,
            UpiApp.BHIM
        )
    }

    private lateinit var channel: MethodChannel
    private var activity: Activity? = null


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "gb_payment_plugin")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        flutterResult = result
        if (call.method == METHOD_GET_UPI_APPS) {
            result.success(
                PhonePe.getUpiApps().filter { upiApplicationInfo ->
                    allowedUpiApps.map { it.packageName }.contains(upiApplicationInfo.packageName)
                }.sortedBy { upiApplicationInfo ->
                    allowedUpiApps.map { it.packageName }.indexOf(upiApplicationInfo.packageName)
                }.toMutableList().apply {
                    if (BuildConfig.DEBUG) {
                        activity?.let {
                            addAll(PackageHelper.getDebugUpiApps(it))
                        }
                    }
                }.map {
                    hashMapOf(
                        Pair("applicationName", it.applicationName),
                        Pair("packageName", it.packageName),
                        Pair("version", it.version),
                        Pair(
                            "icon",
                            AppIconHelper.getAppIconRaw(
                                activity?.packageManager,
                                it.packageName
                            )
                        )
                    )
                })
        } else if (call.method == METHOD_START_PAYMENT) {
            val redirectUrl = call.argument<String>("redirectUrl")
            val packageName = call.argument<String>("packageName")
            checkNotNull(redirectUrl)
            checkNotNull(packageName)

            try {
                val intent = Intent()
                intent.action = Intent.ACTION_VIEW
                intent.data =
                    Uri.parse(redirectUrl)    //PhonePe Intent redirectUrl from the response.
                intent.setPackage(packageName)
                activity?.startActivityForResult(intent, B2B_PG_REQUEST_CODE)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    activity,
                    activity?.getString(R.string.application_not_found),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity
        PhonePe.init(activity)
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivity() {
        activity = null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode == B2B_PG_REQUEST_CODE) {
            data?.extras?.let { bundle ->
                bundle.keySet().forEach {
                    Log.e(
                        TAG,
                        it + " : " + if (bundle.get(it) != null) bundle.get(it) else "NULL"
                    )
                }
            }
            val response: MutableMap<String, Any?> =
                data?.extras?.keySet()?.associate { it to data.extras?.get(it) }?.toMutableMap()
                    ?: mutableMapOf()
            response["result"] = resultCode == Activity.RESULT_OK
            flutterResult?.success(response)
            flutterResult = null
        }
        return false
    }

}
