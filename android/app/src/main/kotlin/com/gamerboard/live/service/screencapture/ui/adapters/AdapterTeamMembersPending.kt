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

class AdapterTeamMembersPending(
    private val squadScores: List<GameResponse.SubmissionState>,
) :
    RecyclerView.Adapter<AdapterTeamMembersPending.MemberHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MemberHolder(inflater.inflate(R.layout.item_team_players, parent, false))
    }

    override fun onBindViewHolder(holder: MemberHolder, position: Int) {
        holder.setData(squadScores[position])
    }

    override fun getItemCount(): Int {
        return squadScores.size
    }

    inner class MemberHolder(val v: View) : RecyclerView.ViewHolder(v) {
        fun setData(score: GameResponse.SubmissionState) {

            Glide.with(v.context).load(score.user.leaderboardUser.image)
                .into(v.findViewById(R.id.iv_player))
            v.findViewById<TextView>(R.id.tv_user_name).text = score.user.leaderboardUser.username
            if(score.hasSubmitted)
            {
                v.findViewById<TextView>(R.id.tv_status).text = "Complete"
                v.findViewById<TextView>(R.id.tv_status).setTextColor(v.context.getColor(R.color.verified))
            }
            else{
                v.findViewById<TextView>(R.id.tv_status).text = "Pending"
            }

        }
    }
}