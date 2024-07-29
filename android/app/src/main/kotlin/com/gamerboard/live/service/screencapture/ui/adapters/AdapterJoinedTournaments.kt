package com.gamerboard.live.service.screencapture.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gamerboard.live.R
import com.gamerboard.live.SubmitBGMIGameMutation
import com.gamerboard.live.fragment.GameResponse

class AdapterJoinedTournaments(private val tournamentResponses: List<GameResponse.Tournament?>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return Holder(inflater.inflate(R.layout.item_post_match_include_tourament, parent, false), tournamentResponses)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is Holder) {
            holder.setData(position)
        }
    }

    override fun getItemCount(): Int {
        return tournamentResponses.size
    }

    class Holder(val v: View, private val items: List<GameResponse.Tournament?>) : RecyclerView.ViewHolder(v) {
        fun setData(position: Int) {
            val serverResponse: GameResponse.Tournament? = items[position]
            if(serverResponse!=null) {
                if (serverResponse.isAdded) {
                    v.findViewById<ImageView>(R.id.iv_included_check)
                        .setImageResource(R.drawable.ic_right_ok)
                    v.findViewById<TextView>(R.id.tv_head).text = serverResponse.tournament.name
                    v.findViewById<TextView>(R.id.tv_content).text =
                        if (serverResponse.isTop) "Top 3 match logged!" else "Match logged for this leaderboard"
                } else {
                    v.findViewById<ImageView>(R.id.iv_included_check)
                        .setImageResource(R.drawable.ic_wrong_cross)
                    v.findViewById<TextView>(R.id.tv_head).text = serverResponse.tournament.name
                    v.findViewById<TextView>(R.id.tv_content).text = serverResponse.exclusionReason
                }
            }else{
                v.findViewById<ImageView>(R.id.iv_included_check)
                    .setImageResource(R.drawable.ic_wrong_cross)
                v.findViewById<TextView>(R.id.tv_head).text = "Tournament error"
                v.findViewById<TextView>(R.id.tv_content).text = "Not found"
            }
        }
    }
}