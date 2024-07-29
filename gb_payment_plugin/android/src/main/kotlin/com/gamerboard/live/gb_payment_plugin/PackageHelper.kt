package com.gamerboard.live.gb_payment_plugin

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import com.phonepe.intent.sdk.api.UPIApplicationInfo
import java.lang.Exception


object PackageHelper {
    fun getDebugUpiApps(context: Context): ArrayList<UPIApplicationInfo> {
        val upiList = ArrayList<UPIApplicationInfo>()
        val uri = Uri.parse(String.format("%s://%s", "ppesim", "pay"))
        val upiUriIntent = Intent()
        upiUriIntent.data = uri
        val packageManager: PackageManager = context.packageManager
        val resolveInfoList =
            packageManager.queryIntentActivities(upiUriIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resolveInfoList) {
            val packageInfo =
                context.packageManager.getPackageInfo(resolveInfo.activityInfo.packageName, 0)
            upiList.add(
                UPIApplicationInfo(
                    applicationName = packageInfo.applicationInfo.name,
                    packageName = resolveInfo.activityInfo.packageName,
                    version = packageInfo.versionCode.toLong()
                )
            )
        }
        try{
            val packageInfo = context.packageManager.getPackageInfo("com.phonepe.app.preprod", 0)
            upiList.add(
                UPIApplicationInfo(
                    applicationName = packageInfo.applicationInfo.name,
                    packageName = packageInfo.packageName,
                    version = packageInfo.versionCode.toLong()
                )
            )
        }catch (ex : Exception){

        }
        return upiList
    }
}