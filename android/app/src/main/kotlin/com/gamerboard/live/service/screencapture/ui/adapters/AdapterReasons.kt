package com.gamerboard.live.service.screencapture.ui.adapters

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.gamerboard.live.R
import com.gamerboard.live.SubmitBGMIGameMutation
import com.gamerboard.live.fragment.GameResponse

class AdapterReasons(
    private val tournamentResponses: List<GameResponse.Tournament?>,
    val scaleDown: Boolean = false
) :
    RecyclerView.Adapter<AdapterReasons.ReasonHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReasonHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ReasonHolder(inflater.inflate(R.layout.item_reason, parent, false))
    }

    override fun onBindViewHolder(holder: ReasonHolder, position: Int) {
        holder.setData(tournamentResponses[position])
    }

    override fun getItemCount(): Int {
        return tournamentResponses.size
    }

    inner class ReasonHolder(val v: View) : RecyclerView.ViewHolder(v) {
        fun setData(tournamentResponse: GameResponse.Tournament?) {
            val s1 = tournamentResponse?.tournament?.name + ": "
            val span =
                SpannableString(
                    s1 + (tournamentResponse?.exclusionReason ?: "Match added to tournament")
                )
            span.setSpan(StyleSpan(Typeface.BOLD), 0, s1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            span.setSpan(RelativeSizeSpan(1.2f), 0, s1.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val tv = v.findViewById<TextView>(R.id.tv_message)

            val icon = v.findViewById<AppCompatImageView>(R.id.iv_icon)
            tv.text = span
            if (scaleDown) {
                (tv.parent as LinearLayoutCompat).apply {
                    setPadding(
                        0,
                        v.context.resources.getDimension(R.dimen._2sdp).toInt(),
                        0, 0
                    )
                }
                icon.apply {
                    if (tournamentResponse?.exclusionReason != null)
                        setImageResource(R.drawable.ic_wrong_cross)
                    else
                        setImageResource(R.drawable.ic_right_ok)
                    scaleX = 0.7f
                    scaleY = 0.7f
                }
            } else {
                (tv.parent as LinearLayoutCompat).apply {
                    setPadding(
                        0,
                        v.context.resources.getDimension(R.dimen._3sdp).toInt(),
                        0, v.context.resources.getDimension(R.dimen._3sdp).toInt(),
                    )
                }
            }
        }
    }

}