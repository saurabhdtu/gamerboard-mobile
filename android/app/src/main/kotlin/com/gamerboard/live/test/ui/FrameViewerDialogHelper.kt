package com.gamerboard.live.test.ui

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.gamerboard.live.R
import com.google.android.material.button.MaterialButton

class FrameViewerDialogHelper {
    private var listener: Listener? = null
    private var dialog: AlertDialog? = null
    private var progressTextView: TextView? = null
    private var frameTextView: TextView? = null
    private var passedTextView: TextView? = null
    private var pauseButton: MaterialButton? = null
    private var imageView: ImageView? = null
    private var context: Context? = null

    fun setFrameText(text: String) {

        try {
            frameTextView?.text = text
        } catch (_: java.lang.Exception) { }
    }

    fun setTitleText(text: String) {
        try {
            progressTextView?.text = text
        } catch (_: java.lang.Exception) { }
    }

    fun setTestPassed(passed: Boolean) {
        try{
            passedTextView?.text = if (passed) "Passed" else "Failed"
            passedTextView?.setTextColor(if (passed) Color.GREEN else Color.RED)
        }catch (_ : Exception){ }
//        Toast.makeText(context, passedTextView?.text, Toast.LENGTH_SHORT).show()
    }

    fun show(context: Context) {
        val builder = AlertDialog.Builder(context)
        imageView = ImageView(context)
        progressTextView = TextView(context)
        frameTextView = TextView(context)
        passedTextView = TextView(context)
        pauseButton = MaterialButton(context)


        progressTextView?.setTextAppearance(R.style.TextAppearance_AppCompat_Body1)
        frameTextView?.setTextAppearance(R.style.TextAppearance_AppCompat_Caption)
        passedTextView?.setTextAppearance(R.style.TextAppearance_MaterialComponents_Overline)
        pauseButton?.text = "Pause"

        val layoutParams =
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        val frameView = LinearLayout(context)
        frameView.orientation = LinearLayout.VERTICAL
        frameView.gravity = Gravity.CENTER
        val wrapContent = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.weight = 1f
        frameView.addView(progressTextView, wrapContent)
        frameView.addView(frameTextView, wrapContent)
        frameView.addView(passedTextView, wrapContent)
        frameView.addView(imageView, layoutParams)
        frameView.addView(pauseButton, wrapContent)

        imageView?.setOnClickListener {
            listener?.onSeek()
        }
        pauseButton?.setOnClickListener {
            listener?.onPause()
        }


        builder.setView(frameView)
        builder.setCancelable(false)

        dialog = builder.create()
        dialog?.show()
    }

    fun close() {
        dialog?.dismiss()
        dialog = null
        context = null
    }

    fun setFrame(frame: Bitmap) {
        try {
            if (frame.isRecycled) return
            imageView?.setImageBitmap(frame)
            imageView?.requestLayout()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun setListener(listener: Listener) {
        this.listener = listener
    }


    interface Listener {
        fun onPause()
        fun onSeek()
    }
}
