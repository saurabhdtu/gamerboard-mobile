package com.gamerboard.live.service.screencapture.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gamerboard.live.R
import com.gamerboard.live.SubmitBGMIGameMutation
import com.gamerboard.live.fragment.GameResponse

class AdapterTeamMembers(
    private val squadScores: List<GameResponse.SquadScore>,
    private val pointsPerKill: Int
) :
    RecyclerView.Adapter<AdapterTeamMembers.MemberHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MemberHolder(inflater.inflate(R.layout.item_view_post_match_squad, parent, false))
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        holder.setData(squadScores[position])
    }

    override fun getItemCount(): Int {
        return squadScores.size
    }

    inner class MemberHolder(val v: View) : RecyclerView.ViewHolder(v) {
        fun setData(score: GameResponse.SquadScore) {

            Glide.with(v.context).load(score.user.image)
                .into(v.findViewById(R.id.iv_lb_avatar))
            v.findViewById<TextView>(R.id.tv_player_name).text = score.user.username
            v.findViewById<TextView>(R.id.tv_player_kills).text = "Match kills: ${score.kills}"
            v.findViewById<TextView>(R.id.tv_player_points).text =
                (score.kills * pointsPerKill).toString()
        }
    }
}