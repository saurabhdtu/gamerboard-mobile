package com.gamerboard.live.gb_payment_plugin

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import java.io.ByteArrayOutputStream


object AppIconHelper {
    private fun getAppIcon(mPackageManager: PackageManager, packageName: String?): Bitmap? {
        if (Build.VERSION.SDK_INT >= 26) {
            return AppIconHelperV26.getAppIcon(mPackageManager, packageName)
        }
        try {
            val drawable = mPackageManager.getApplicationIcon(packageName!!)
            return (drawable as BitmapDrawable).bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun getAppIconRaw(packageManager : PackageManager?, packageName: String?) : ByteArray{
        val stream = ByteArrayOutputStream()
        packageManager?.let { getAppIcon(it, packageName)?.compress(Bitmap.CompressFormat.PNG, 100, stream) }
        return stream.toByteArray()
    }
}