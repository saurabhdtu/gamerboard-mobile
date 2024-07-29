package com.gamerboard.live.service.screencapture.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.gamerboard.live.GamerboardApp
import com.gamerboard.live.GetGameScoringQuery
import com.gamerboard.live.R
import com.gamerboard.live.common.SharedPreferenceKeys
import com.gamerboard.live.gamestatemachine.stateMachine.MachineConstants
import com.gamerboard.live.models.LeaderBoardElement
import com.gamerboard.live.models.ServerTournamentElement
import com.gamerboard.live.repository.ApiClient
import com.gamerboard.live.repository.GameRepository
import com.gamerboard.live.type.ESports
import com.google.android.material.tabs.TabLayout
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by saurabh.lahoti on 16/11/21
 */
class AdapterLeaderboard(val ctx: Context,val apiClient: ApiClient) : RecyclerView.Adapter<ViewHolder>() {

    var userId: String? = null
    lateinit var leaderboardPagination: LeaderboardPagination

    init {
        userId =
            (ctx.applicationContext as GamerboardApp).prefsHelper.getString(SharedPreferenceKeys.USER_ID)
                ?: ""
    }

    var leaderboardEntities: ArrayList<LeaderBoardElement> = arrayListOf()
    var scoring: GetGameScoringQuery.Scoring? = null

    fun setScoringData(_scoring: GetGameScoringQuery.Scoring) {
        scoring = _scoring
    }

    fun addLeaderboardEntries(_leaderboardEntities: List<LeaderBoardElement>, refresh: Boolean) {
        if (refresh)
            leaderboardEntities.clear()
        leaderboardEntities.addAll(_leaderboardEntities)
        notifyDataSetChanged()
    }

    fun clearAllLeaderboardEntries() {
        leaderboardEntities.clear()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return Holder(
            inflater.inflate(R.layout.item_lb, parent, false),
            leaderboardEntities,
            scoring,
            ctx,
            userId!!
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is Holder) {
            holder.setData(position)
            if (position == itemCount - 1)
                leaderboardPagination.fetchMoreLeaderboardData()
        }
    }

    override fun getItemCount(): Int {
        return leaderboardEntities.size
    }

    class Holder(
        val v: View,
        private val leaderboardEntities: List<LeaderBoardElement>,
        val scoring: GetGameScoringQuery.Scoring?,
        val ctx: Context,
        val userId: String
    ) : ViewHolder(v) {
        fun setData(i: Int) {
            val leaderBoardEntry: LeaderBoardElement = leaderboardEntities[i]
            v.findViewById<AppCompatTextView>(R.id.tv_lb_rank).text = "${leaderBoardEntry.rank}"
            v.findViewById<AppCompatTextView>(R.id.tv_lb_username).text =
                leaderBoardEntry.name
            v.findViewById<AppCompatTextView>(R.id.tv_lb_games_played).text =
                "${leaderBoardEntry.matchesPlayed}"
            v.findViewById<AppCompatTextView>(R.id.tv_lb_total_pts).text =
                "${leaderBoardEntry.score}"
            v.findViewById<AppCompatTextView>(R.id.tv_lb_pts_behind).text =
                "${leaderBoardEntry.behindBy}"

            v.findViewById<AppCompatTextView>(R.id.tv_lb_rank_pts).text =
                "${getTopRankScore(leaderBoardEntry)}"
            v.findViewById<AppCompatTextView>(R.id.tv_lb_kill_pts).text =
                "${getTopKillScore(leaderBoardEntry)}"

            Glide.with(ctx).load(leaderBoardEntry.myPhoto)
                .into(v.findViewById(R.id.iv_lb_avatar))

            if ("${leaderBoardEntry.myId}" == userId) highlightView(
                v,
                R.color.verified,
                R.color.bg_dark_4
            )
            else highlightView(v, R.color.txt_color_white, R.color.bg_dark_3)
        }

        private fun highlightView(
            view: View,
            color: Int = R.color.txt_color,
            viewBackground: Int
        ) {
            view.setBackgroundColor(ctx.getColor(viewBackground))

            view.findViewById<AppCompatTextView>(R.id.tv_lb_rank).setTextColor(ctx.getColor(color))
            view.findViewById<AppCompatTextView>(R.id.tv_lb_username)
                .setTextColor(ctx.getColor(color))
            view.findViewById<AppCompatTextView>(R.id.tv_lb_games_played)
                .setTextColor(ctx.getColor(color))
            view.findViewById<AppCompatTextView>(R.id.tv_lb_total_pts)
                .setTextColor(ctx.getColor(color))
            view.findViewById<AppCompatTextView>(R.id.tv_lb_pts_behind)
                .setTextColor(ctx.getColor(color))
            view.findViewById<AppCompatTextView>(R.id.tv_lb_rank_pts)
                .setTextColor(ctx.getColor(color))
            view.findViewById<AppCompatTextView>(R.id.tv_lb_kill_pts)
                .setTextColor(ctx.getColor(color))
        }

        private fun getTopRankScore(leaderboardItem: LeaderBoardElement): Int {
            var rankScore = 0
            if (leaderboardItem.topGames?.gameResults?.isNotEmpty() == true) {
                leaderboardItem.topGames.gameResults.forEach { game ->
                    val score =
                        (scoring?.rankPoints?.find { it.rank == game.rank }?.points)
                            ?: 0
                    rankScore += score
                }
            }
            return rankScore
        }

        private fun getTopKillScore(leaderboardItem: LeaderBoardElement): Int {
//            var killScore = 0
            return leaderboardItem.score.toInt() - getTopRankScore(leaderboardItem)
            /*if (leaderboardItem.topGames?.gameResults?.isNotEmpty() == true) {
                leaderboardItem.topGames.gameResults.forEach { game ->
                    val score =
                        (scoring?.killPoints!! * game.kills)
                    killScore += score
                }
            }
            return killScore*/
        }
    }

}


class LeaderboardPagination(
    val ctx: Context,
    private val rvLeaderboard: RecyclerView,
    val tbLeaderboard: TabLayout,
    val progressBar: ProgressBar,
    val apiClient: ApiClient ,
    private val refreshLayout: SwipeRefreshLayout,
    private val lbPLaceHolderEmpty: TextView
) {

    var activeTournaments: ArrayList<ServerTournamentElement> = arrayListOf()
    var isMoreDataAvailable = true

    //var currentTournament: Long = -1
    var page: Long = 0
    val adapterLeaderboard: AdapterLeaderboard = rvLeaderboard.adapter as AdapterLeaderboard

    init {

        // refresh
        refreshLayout.setOnRefreshListener {
            if (activeTournaments.size != 0 && tbLeaderboard.selectedTabPosition != -1)
                refreshTournament(activeTournaments[tbLeaderboard.selectedTabPosition].tournament.id , apiClient = apiClient )
            refreshLayout.isRefreshing = false
        }

        //tabs
        tbLeaderboard.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.apply {
                    //showToast("Tab Switched!", force = true)
                    //currentTournament = activeTournaments[tab.position].tournament.id
                    if (tab.position != TabLayout.Tab.INVALID_POSITION)
                        refreshTournament(activeTournaments[tab.position].tournament.id,apiClient)
//                    activeTournaments[tab.position].tournament.rules.bgmiAllowedGroups
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }


    private fun refreshTournament(tournamentId: Long,apiClient: ApiClient) {
        if (tournamentId == -1L)
            return
        isMoreDataAvailable = true
        page = 0
        adapterLeaderboard.clearAllLeaderboardEntries()
        populateLeaderBoard(tournamentId, page + 1, isRefreshRequest = true, apiClient = apiClient)
    }

    fun fetchMoreLeaderboardData() {
        if (activeTournaments.isEmpty()) {
            //showToast("No entries to load!", force = true)
            progressBar.visibility = View.GONE
            return
        }
        populateLeaderBoard(
            activeTournaments[tbLeaderboard.selectedTabPosition].tournament.id,
            page + 1,
            apiClient
        )
    }

    fun getActiveTournaments(apiClient: ApiClient) {
        CoroutineScope(Dispatchers.Main).launch {
            if(adapterLeaderboard.leaderboardEntities.isEmpty())
            {
                var result = GameRepository.getMyJoinTournaments(MachineConstants.currentGame.eSport,apiClient)
                result?.let {
                    Log.d(
                        "get_active_tournaments",
                        "query Successful ${result}   \n\n\n ${result} \n\n\n ${
                            result.size
                        }"
                    )


                    if(!(activeTournaments.containsAll(result) && result.containsAll(activeTournaments)))
                    {
                        // clear entries from before
                        tbLeaderboard.removeAllTabs()
                        adapterLeaderboard.clearAllLeaderboardEntries()
                        activeTournaments.clear()

                        result.forEach{
                            activeTournaments.add(it)
                            tbLeaderboard.apply {
                                addTab(newTab().setText(it.tournament.name))
                            }
                        }
                        // if no active tournaments
                        if (activeTournaments.size == 0) {
                            progressBar.visibility = View.GONE
                            lbPLaceHolderEmpty.visibility = View.VISIBLE

                        }

                        lbPLaceHolderEmpty.visibility = View.GONE

                        // if just one entry, remove indicator
                        if (activeTournaments.size == 1) {
                            tbLeaderboard.setSelectedTabIndicator(null)
                        }
                        if (activeTournaments.isNotEmpty())
                            refreshTournament(activeTournaments[0].tournament.id,apiClient)
                    }


                }
            }

        }
    }


    private fun populateLeaderBoard(
        tournamentId: Long,
        pageNum: Long,
        apiClient: ApiClient,
        isRefreshRequest: Boolean = false
    ) {
        if (isMoreDataAvailable.not()) {
            progressBar.visibility = View.GONE
            //showToast("No more data to fetch, try refreshing!", force = true)
            return
        }
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Main).launch {
           var result = GameRepository.getLeaderboard(tournamentId, pageNum = pageNum.toInt(), apiClient = apiClient,activeTournaments[tbLeaderboard.selectedTabPosition].tournament.rules.allowedGroups)
            result?.let {
                if (activeTournaments[tbLeaderboard.selectedTabPosition].tournament.id.toInt() == result.tournamentId)
                {
                    page++
                    adapterLeaderboard.setScoringData(it.score)
                        adapterLeaderboard.addLeaderboardEntries(result.leaderBoard, isRefreshRequest)
                    isMoreDataAvailable = result.leaderBoard.isNotEmpty()
                    progressBar.visibility = View.GONE
                }

            }
        }

    }

//    private fun fetchTournaments(result: MethodChannel.Result) {
//        mBackgroundChannel?.invokeMethod("get_active_tournaments", null, result)
//    }

}