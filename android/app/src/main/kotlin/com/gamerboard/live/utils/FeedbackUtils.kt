package com.gamerboard.live.utils

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.LayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.gamerboard.live.BuildConfig
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.R
import com.gamerboard.live.common.BroadcastFilters
import com.gamerboard.live.common.RemoteConfigConstants
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.databinding.DialogFeedbackInputBinding
import com.gamerboard.live.databinding.DialogFeedbackRatingBinding
import com.gamerboard.live.databinding.DialogFeedbackReasonBinding
import com.gamerboard.live.databinding.ItemFeedbackReasonBinding
import com.gamerboard.live.models.FeedBackFrom
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.repository.FeedbackRepository
import com.gamerboard.logger.gson
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedbackUtils(val context: Context, val apiClient: ApiClient) {
    private val feedbackRepository = FeedbackRepository(apiClient)
    private val prefHelper = ((context.applicationContext) as GamerboardApp).prefsHelper
    private var rating: Int? = null
    private var reason: String? = null
    private var feedback: String? = null
    private var feedbackFrom: FeedBackFrom? = null
    private var isFloating = false
    private val SESSION_COUNT_KEY = -1000
    private var currentSesion = 1
    private lateinit var feedbackData: HashMap<Int, Int>

    companion object {
        fun requestFeedback(
            ctx: Context, feedbackFrom: FeedBackFrom, intentFilter: String? = null
        ) {
            ctx.sendBroadcast(Intent(intentFilter ?: BroadcastFilters.SERVICE_COM).apply {
                putExtra("action", "feedback")
                putExtra("feedback_from", feedbackFrom.ordinal)
            })
        }
    }

    fun getFeedback(feedbackFrom: FeedBackFrom) {
        this.feedbackFrom = feedbackFrom
        val jsonData = prefHelper.getString(SharedPreferenceKeys.FEEDBACK_DATA)
        currentSesion = prefHelper.getInt(SharedPreferenceKeys.SESSION_COUNT)
        if (jsonData == null) feedbackData = hashMapOf(feedbackFrom.ordinal to 0)
        else {
            val typeToken = object : TypeToken<HashMap<Int?, Int?>?>() {}.type
            feedbackData = try {
                Log.d("feedback-data", jsonData.toString())
                gson.fromJson(jsonData, typeToken)
            } catch (ex: Exception) {
                hashMapOf(
                    feedbackFrom.ordinal to 0, SESSION_COUNT_KEY to 0
                )
            }
        }
        if (feedbackData.containsKey(feedbackFrom.ordinal)
                .not()
        ) feedbackData[feedbackFrom.ordinal] = 0
        if (feedbackData.containsKey(SESSION_COUNT_KEY).not()) feedbackData[SESSION_COUNT_KEY] = 0
        if (feedbackData[feedbackFrom.ordinal]!! < (if (BuildConfig.DEBUG) 5 else 2) && feedbackData[SESSION_COUNT_KEY]!! < currentSesion) {
            isFloating = when (feedbackFrom) {
                FeedBackFrom.GAME_COMPLETION -> {
                    true
                }
                FeedBackFrom.BACK_TO_GB_FROM_GAME -> {
                    false
                }
            }
            showRatingDialog()
        } else {
            //TODO implement case when the feedback doesn't meet the requirement
        }
    }

    private fun setupDialog(view: View): Dialog {
        val dialog = Dialog(context)
        dialog.setCancelable(false)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(view)
        if (isFloating) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dialog.window?.setType(LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else {
                dialog.window?.setType(LayoutParams.TYPE_SYSTEM_OVERLAY)
            }
        }
        return dialog
    }


    private fun showRatingDialog() {
        val layout = DialogFeedbackRatingBinding.inflate(LayoutInflater.from(context))
        val dialog = setupDialog(layout.root)
        layout.ivClose.setOnClickListener {
            EventUtils.instance().logAnalyticsEvent(
                Events.FEEDBACK_POPUP_DISMISSED, mapOf(
                    "screen" to "star_rating", "from" to "close_icon"
                )
            )
            dialog.dismiss()
        }
        layout.tvJoinDiscord.setOnClickListener {
            openDiscordServer(context);
        }
        layout.btnSubmit.setOnClickListener {
            rating = layout.ratingBar.rating.toInt()
            if (rating!! <= 3) {
                layout.layoutProgress.visibility = View.VISIBLE
                CoroutineScope(Dispatchers.IO).launch {
                    val apiResponse = feedbackRepository.getReasons()
                    if (apiResponse.response != null) {
                        withContext(Dispatchers.Main) {
                            dialog.dismiss()
                            EventUtils.instance().logAnalyticsEvent(
                                Events.FEEDBACK_POPUP_SUBMITTED, mapOf(
                                    "screen" to "star_rating", "rating" to rating!!
                                )
                            )
                            showFeedbackReasonDialog(apiResponse.response)
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            UiUtils.showToast(context, apiResponse.error ?: "", null)
                            layout.layoutProgress.visibility = View.GONE
                        }
                    }
                }
            } else {
                showFeedbackInputDialog()
                dialog.dismiss()
            }

        }
        layout.btnCancel.setOnClickListener {
            EventUtils.instance().logAnalyticsEvent(
                Events.FEEDBACK_POPUP_DISMISSED, mapOf(
                    "screen" to "star_rating", "from" to "cancel"
                )
            )
            dialog.dismiss()
        }
        EventUtils.instance()
            .logAnalyticsEvent(Events.FEEDBACK_POPUP_SHOWN, mapOf("screen" to "star_rating"))
        dialog.show()
    }


    private fun openDiscordServer(ctx: Context) {
        UiUtils.showToast(ctx, "Launching discord server...", null)
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(  FirebaseRemoteConfig.getInstance().getString(
                RemoteConfigConstants.GB_DISCORD),
            ))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            ctx.startActivity(
                Intent.createChooser(intent, "Gamerboard").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        } catch (e: java.lang.Exception) {
            logException(e)
        }
    }

    private fun showFeedbackInputDialog() {
        val layout = DialogFeedbackInputBinding.inflate(LayoutInflater.from(context))
        UiUtils.setFullUiParams(layout.root)
        val dialog = setupDialog(layout.root)
        dialog.window?.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.6).toInt(), LayoutParams.WRAP_CONTENT
        )
        if ((rating ?: 0) <= 3) {
            layout.tvSubTitle.visibility = View.GONE
            layout.tvTitle.text = context.getString(R.string.tell_experience)
        } else {
            layout.tvSubTitle.visibility = View.VISIBLE
            layout.tvTitle.text = context.getString(R.string.share_feedback)
        }
        layout.etFeedback.minLines = 3
        layout.btnSubmit.setOnClickListener {
            feedback = layout.etFeedback.text.toString().trim()
            EventUtils.instance().logAnalyticsEvent(
                Events.FEEDBACK_POPUP_SUBMITTED, mapOf(
                    "screen" to "feedback_input", "rating" to rating!!
                )
            )
            submitFeedBack()
            dialog.dismiss()
        }
        layout.btnCancel.setOnClickListener {
            EventUtils.instance().logAnalyticsEvent(
                Events.FEEDBACK_POPUP_DISMISSED, mapOf(
                    "screen" to "feedback_input", "from" to "cancel"
                )
            )
            submitFeedBack()
            dialog.dismiss()
        }
        layout.ivClose.setOnClickListener {
            EventUtils.instance().logAnalyticsEvent(
                Events.FEEDBACK_POPUP_DISMISSED, mapOf(
                    "screen" to "feedback_input", "from" to "close_icon", "rating" to (rating ?: -1)
                )
            )
            submitFeedBack()
            dialog.dismiss()
        }
        EventUtils.instance().logAnalyticsEvent(
            Events.FEEDBACK_POPUP_SHOWN, mapOf(
                "screen" to "feedback_input", "rating" to (rating ?: -1)
            )
        )
        dialog.show()
    }

    private fun showFeedbackReasonDialog(reasons: List<String>) {
        val layout = DialogFeedbackReasonBinding.inflate(LayoutInflater.from(context))
        val dialog = setupDialog(layout.root)
        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.8).toInt(), LayoutParams.WRAP_CONTENT
        )
        layout.btnCancel.setOnClickListener {
            EventUtils.instance().logAnalyticsEvent(
                Events.FEEDBACK_POPUP_DISMISSED, mapOf(
                    "screen" to "reasons_selection", "from" to "cancel", "rating" to (rating ?: -1)
                )
            )
            submitFeedBack()
            dialog.dismiss()
        }
        val linkedHashMap = LinkedHashMap<String, Boolean>()
        reasons.forEach { linkedHashMap[it] = false }
        val feedbackAdapter = FeedbackReasonAdapter(linkedHashMap)
        layout.btnSubmit.setOnClickListener {
            var reasonStr = ""
            feedbackAdapter.reasons.entries.forEach {
                if (it.value) {
                    reasonStr += it.key + "|"
                }
            }
            if (reasonStr.contains("|")) reasonStr = reasonStr.substring(0, reasonStr.length - 1)
            reason = reasonStr
            showFeedbackInputDialog()
            EventUtils.instance().logAnalyticsEvent(
                Events.FEEDBACK_POPUP_SUBMITTED, mapOf(
                    "screen" to "reasons_selection",
                    "rating" to (rating ?: -1),
                    "#reasons" to feedbackAdapter.reasons.filter { it.value }.size
                )
            )
            dialog.dismiss()
        }
        layout.ivClose.setOnClickListener {
            EventUtils.instance().logAnalyticsEvent(
                Events.FEEDBACK_POPUP_DISMISSED, mapOf(
                    "screen" to "reasons_selection",
                    "from" to "close_icon",
                    "rating" to (rating ?: -1)
                )
            )
            submitFeedBack()
            dialog.dismiss()
        }
        layout.rvReasons.adapter = feedbackAdapter
        EventUtils.instance().logAnalyticsEvent(
            Events.FEEDBACK_POPUP_SHOWN, mapOf(
                "screen" to "reasons_selection"
            )
        )
        dialog.show()
    }

    private inner class FeedbackReasonAdapter(val reasons: LinkedHashMap<String, Boolean>) :

        RecyclerView.Adapter<FeedbackReasonAdapter.MyHolder>() {
        val items = reasons.keys.toList()

        inner class MyHolder(val binding: ItemFeedbackReasonBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun setData(text: String, isChecked: Boolean) {
                binding.tvReason.text = text
                binding.checkbox.isClickable = true

                binding.checkbox.isChecked = isChecked
                binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                    reasons[text] = isChecked
                }
            }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            return MyHolder(ItemFeedbackReasonBinding.inflate(LayoutInflater.from(parent.context)))
        }

        override fun onBindViewHolder(holder: MyHolder, position: Int) {
            holder.setData(items[position], reasons[items[position]]!!)
        }

        override fun getItemCount(): Int = items.size
    }

    private fun submitFeedBack() {
        rating?.let { rat ->
            CoroutineScope(Dispatchers.IO).launch {
                val submitFeedback = feedbackRepository.submitFeedback(
                    feedbackFrom.toString(), rat, feedback, reason
                )
                if (submitFeedback.response is Int) {
                    withContext(Dispatchers.Main) {
                        UiUtils.showToast(context, "Feedback submitted", null)
                    }
                    feedbackData[feedbackFrom!!.ordinal] =
                        feedbackData[feedbackFrom!!.ordinal]!! + 1
                    feedbackData[SESSION_COUNT_KEY] = currentSesion
                    val s = gson.toJson(feedbackData)
                    Log.d("feedback-data", s)
                    prefHelper.putString(
                        SharedPreferenceKeys.FEEDBACK_DATA, s
                    )
                }
            }
        }
    }
}