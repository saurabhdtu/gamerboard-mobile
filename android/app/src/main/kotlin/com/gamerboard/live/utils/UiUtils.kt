package com.gamerboard.live.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.graphics.PixelFormat
import android.graphics.Point
import android.os.Build
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import com.gamerboard.live.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by saurabh.lahoti on 06/08/21
 */
object UiUtils {
    fun showToast(context: Context, message: String, duration: Int?) {
        Toast.makeText(context, message, duration ?: Toast.LENGTH_SHORT).show()
    }

    fun showAlertDialog(
        context: Activity,
        message: String,
        posMsg: String,
        negMsg: String,
        dialogClickHandler: DialogClickHandler
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.confirm_show_dialog)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog.findViewById<TextView>(R.id.tv_message).text = message
        dialog.findViewById<TextView>(R.id.title).text = ""
        dialog.setOnDismissListener { dialogClickHandler.dismiss(dialog) }
        dialog.findViewById<TextView>(R.id.confirm).apply {
            text = posMsg
            setOnClickListener {
                dialogClickHandler.positiveClick(dialog)
            }
        }
        dialog.findViewById<TextView>(R.id.cancel).apply {
            text = negMsg
            setOnClickListener {
                dialogClickHandler.negativeClick(dialog)
            }
        }

        dialog.show()
    }

    fun convertDpToPixel(dp: Int, ctx: Context): Int {
        val resources: Resources = ctx.resources
        val metrics: DisplayMetrics = resources.displayMetrics
        return (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT.toFloat())).toInt()
    }

    fun convertPixelsToDp(px: Int, context: Context): Int {
        val resources: Resources = context.resources
        val metrics: DisplayMetrics = resources.displayMetrics
        return (px / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT.toFloat())).toInt()
    }

    fun getOverlayParams(
        width: Int,
        height: Int,
        canFocus: Boolean? = false,
        canBePortrait: Boolean = false
    ): WindowManager.LayoutParams {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return WindowManager.LayoutParams(
                width,
                height,
                WindowManager.LayoutParams.TYPE_PHONE,
                (if (canFocus == null || !canFocus) WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE else WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                        or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT,
            ).apply {
                if (!canBePortrait)
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        } else {
            return WindowManager.LayoutParams(
                width,
                height,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                (if (canFocus == null || !canFocus) WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE else WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
                        or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT
            ).apply {
                if (!canBePortrait)
                    screenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }

    fun setFullUiParams(layout: View?) {
        val uiOptions = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LOW_PROFILE)
        layout?.systemUiVisibility = uiOptions
    }


    fun getAlignmentToGravity(alignOverlayFrom: AlignOverlayFrom?): Int {
        return when (alignOverlayFrom) {
            AlignOverlayFrom.TopRight -> {
                (Gravity.TOP or Gravity.END)
            }

            AlignOverlayFrom.TopLeft -> {
                (Gravity.TOP or Gravity.START)
            }
            AlignOverlayFrom.BottomRight -> {
                (Gravity.BOTTOM or Gravity.END)
            }
            AlignOverlayFrom.BottomLeft -> {
                (Gravity.BOTTOM or Gravity.START)
            }
            AlignOverlayFrom.Center -> {
                (Gravity.CENTER)
            }
            AlignOverlayFrom.CenterHorizontalTop -> {
                (Gravity.CENTER_HORIZONTAL or Gravity.TOP)
            }
            AlignOverlayFrom.CenterVerticalRight -> {
                (Gravity.CENTER_VERTICAL or Gravity.END)
            }
            else -> {
                (Gravity.TOP or Gravity.END)
            }
        }
    }

    fun getLocationOnScreen(view: View): Point {
        val point = IntArray(2)
        view.getLocationOnScreen(point)
        return Point(point[0], point[1])
    }

    enum class AlignOverlayFrom { TopLeft, TopRight, BottomLeft, BottomRight, Center, CenterVerticalRight, CenterHorizontalTop }


}

object DateUtils {
    val UTC_FORMAT = "yyyy-MM-dd hh:mm:ss"

    fun formatTime(time: Long): String {
        return SimpleDateFormat(UTC_FORMAT).format(Date(time))
    }
}