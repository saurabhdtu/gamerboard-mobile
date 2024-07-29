package com.gamerboard.live.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import com.gamerboard.live.R

/**
 * Created by saurabh.lahoti on 13/08/21
 */
object DialogHelper {
    fun showSettingsDialog(context: Context, message: String?): Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.confirm_show_dialog)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        (dialog.findViewById<View>(R.id.title) as TextView).text = "Permission required"
        (dialog.findViewById<View>(R.id.message) as TextView).text =
            message
        (dialog.findViewById<View>(R.id.confirm) as TextView).text = "Settings"
        dialog.findViewById<View>(R.id.cancel).setOnClickListener { v: View? -> dialog.dismiss() }
        dialog.findViewById<View>(R.id.confirm).setOnClickListener { v: View? ->
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri =
                Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
        return dialog
    }

    fun showDownloadProgress(context:Context, dismissible:Boolean=true):Dialog{
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_download_progress)
        dialog.setCancelable(dismissible)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        (dialog.findViewById<View>(R.id.progressbar) as ProgressBar).progress = 0
        (dialog.findViewById<View>(R.id.tv_progress) as TextView).text = "0"
        dialog.show()
        return dialog
    }
}