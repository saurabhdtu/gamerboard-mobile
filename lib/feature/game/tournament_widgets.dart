import 'dart:async';

import 'package:firebase_remote_config/firebase_remote_config.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/widgets/blank.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/containers.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/game/game_entities.dart';
import 'package:gamerboard/feature/game/tournament_bloc.dart';
import 'package:gamerboard/feature/game/tournament_states.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/custom_room_info_dialog.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/mobile_input_for_idp_dialog.dart';
import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/share_utils.dart';
import 'package:gamerboard/utils/time_utils.dart';
import 'package:gamerboard/utils/validators.dart';
import 'package:lottie/lottie.dart';
import 'package:marquee/marquee.dart';
import 'package:sprintf/sprintf.dart';

import '../../common/bloc/application/application_bloc.dart';
import '../../utils/ui_utils.dart';

////Created by saurabh.lahoti on 27/12/21

class TournamentWidgets {
  final TournamentBloc _tournamentBloc;

  TournamentWidgets(this._tournamentBloc);

  Widget getLeftPane(double width, LeaderboardState state) {
    List<Widget> pages = [];
    for (int i = 0; i < state.tournaments.length; i++)
      pages.add(_LeaderboardWidget(state.tournaments[i]));

    return Container(
        width: width,
        child: TabBarView(
            physics: NeverScrollableScrollPhysics(), children: pages));
  }

  Widget getTabBar(List<UserTournamentMixin> tournaments) {
    List<Widget> tabs = [];
    tournaments.forEach((element) {
      tabs.add(Tab(
          child: Padding(
              padding:
                  const EdgeInsets.symmetric(horizontal: 5.0, vertical: 6.0),
              child: Text(element.tournament.name))));
    });
    return TabBar(
        isScrollable: true,
        indicatorColor: Colors.white,
        indicatorWeight: 4.0,
        unselectedLabelStyle: RegularTextStyle(color: AppColor.textSubTitle),
        labelStyle: BoldTextStyle(color: Colors.white),
        tabs: tabs);
  }

  Widget getRightPane(
      double width, LeaderboardState state, BuildContext context) {
    return Container(
        width: width,
        height: MediaQuery.of(context).size.height - 45,
        child: Column(children: [
          Expanded(
              child: SingleChildScrollView(
                  child: Column(children: [
            SizedBox(width: width, child: _allowedTiers()),
            _tournamentRulesSection()
          ]))),
          // _tournamentRulesSection(),
          Column(children: [_userGameAndSquad(), _cta(context)])
        ]));
  }

  Widget _allowedTiers() => Container(
      color: AppColor.darkBackground,
      padding: const EdgeInsets.all(14.0),
      child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        RegularText(AppStrings.allowedTiers, fontSize: 11.0),
        SizedBox(
            height: 55.0,
            child: BlocBuilder<TournamentBloc, TournamentState>(
                builder: (context, state) {
                  if (state is TournamentChanged) {
                    List<Enum> allowedLevels =
                        _tournamentBloc.currentTournament.getAllowedTiers();
                    return ListView.builder(
                        shrinkWrap: true,
                        scrollDirection: Axis.horizontal,
                        itemCount: allowedLevels.length,
                        itemBuilder: (context, index) => Padding(
                            padding:
                                const EdgeInsets.only(top: 8.0, right: 10.0),
                            child: Column(children: [
                              Image.network(getTierImage(allowedLevels[index]),
                                  width: 24.0, height: 24.0),
                              const SizedBox(height: 5.0),
                              RegularText(getTierName(allowedLevels[index]),
                                  fontSize: 12.0)
                            ])));
                  }
                  return SizedBox.shrink();
                },
                bloc: _tournamentBloc,
                buildWhen: (p, c) => c is TournamentChanged))
      ]));

  Widget _tournamentRulesSection() => themeContainer(
      BlocBuilder<TournamentBloc, TournamentState>(
          builder: (context, state) {
            if (state is TournamentChanged)
              return Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    if (_tournamentBloc.currentTournament.squad != null)
                      Padding(
                          padding: const EdgeInsets.only(bottom: 15.0),
                          child: teamPlayerStatusContainer(
                              _tournamentBloc.user?.id,
                              _tournamentBloc.currentTournament.squad!,
                              _tournamentBloc.currentTournament
                                  .tournamentGroup(),
                              () => _tournamentBloc.loadSquadDetails(true),
                              backgroundColor: AppColor.darkBackground,
                              scale: 1.2,
                              ellipsisStatus: false)),
                    BoldText(AppStrings.rules, fontSize: 15.0),
                    const SizedBox(height: 10.0),
                    if (_tournamentBloc
                            .currentTournament.tournament.matchType ==
                        MatchType.headToHead)
                      InkWell(
                        onTap: () {
                          openUrlInExternalBrowser(
                              URLConstants.HELP_CUSTOM_ROOM);
                        },
                        child: Text(
                          AppStrings.customRoomGameRules,
                          style: RegularTextStyle(
                              color: AppColor.textHighlighted,
                              height: 1.2,
                              decoration: TextDecoration.underline,
                              decorationColor: AppColor.textHighlighted,
                              fontSize: 12.0),
                        ),
                      ),
                    const SizedBox(height: 10.0),
                    Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          SvgPicture.asset("${imageAssets}ic_maps.svg",
                              width: 14.0,
                              height: 14.0,
                              color: AppColor.textSubTitle),
                          const SizedBox(width: 10.0),
                          Expanded(
                              child: RegularText(
                                  "Map(s)\n${_tournamentBloc.currentTournament.getAllowedMaps()}",
                                  color: AppColor.textSubTitle,
                                  height: 1.3,
                                  fontSize: 12.0))
                        ]),
                    const SizedBox(height: 10.0),
                    Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          SvgPicture.asset("${imageAssets}ic_game.svg",
                              width: 14.0,
                              height: 14.0,
                              color: AppColor.textSubTitle),
                          const SizedBox(width: 10.0),
                          Expanded(
                              child: RegularText(
                                  "Game type\n${_tournamentBloc.currentTournament.getAllowedGameMode()}",
                                  color: AppColor.textSubTitle,
                                  height: 1.3,
                                  fontSize: 12.0))
                        ]),
                    const SizedBox(height: 10.0),
                    if (_tournamentBloc
                            .currentTournament.tournament.matchType ==
                        MatchType.classic)
                      Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            SvgPicture.asset("${imageAssets}ic_lb.svg",
                                width: 14.0,
                                height: 14.0,
                                color: AppColor.textSubTitle),
                            const SizedBox(width: 10.0),
                            Expanded(
                                child: Column(
                                    crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                    children: [
                                  RegularText(AppStrings.scoring,
                                      color: AppColor.textSubTitle,
                                      height: 1.1,
                                      fontSize: 12.0),
                                  Padding(
                                      padding: const EdgeInsets.symmetric(
                                          vertical: 1.0),
                                      child: InkWell(
                                          onTap: () => openUrlInExternalBrowser(
                                              URLConstants.HELP_TOP_GAME_COUNT),
                                          child: RichText(
                                              text: TextSpan(children: [
                                            TextSpan(
                                                text: sprintf(
                                                    AppStrings.bestGames, [
                                                  _tournamentBloc
                                                      .currentTournament
                                                      .tournament
                                                      .gameCount
                                                      .toString()
                                                ]),
                                                style: RegularTextStyle(
                                                    color: AppColor
                                                        .textHighlighted,
                                                    height: 1.2,
                                                    decoration: TextDecoration
                                                        .underline,
                                                    decorationColor: AppColor
                                                        .textHighlighted,
                                                    fontSize: 12.0)),
                                            TextSpan(
                                                text: sprintf(
                                                    " ${AppStrings.tournamentRule1}",
                                                    [
                                                      _tournamentBloc
                                                                  .currentTournament
                                                                  .tournament
                                                                  .tournamentGroup() ==
                                                              GameTeamGroup.solo
                                                          ? "10 players"
                                                          : "8 teams"
                                                    ]),
                                                style: RegularTextStyle(
                                                    fontSize: 12.0))
                                          ]))))
                                ]))
                          ]),
                    if (_tournamentBloc
                            .currentTournament.tournament.matchType ==
                        MatchType.headToHead)
                      Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const SizedBox(height: 12.0),
                          BoldText(AppStrings.support, fontSize: 15.0),
                          const SizedBox(height: 12.0),
                          Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                SvgPicture.asset("${imageAssets}ic_help.svg",
                                    width: 14.0,
                                    height: 14.0,
                                    color: AppColor.textSubTitle),
                                const SizedBox(width: 10.0),
                                Expanded(
                                    child: Column(
                                        crossAxisAlignment:
                                            CrossAxisAlignment.start,
                                        children: [
                                      RegularText(AppStrings.needHelp,
                                          color: AppColor.textSubTitle,
                                          height: 1.1,
                                          fontSize: 12.0),
                                      const SizedBox(
                                        height: 4,
                                      ),
                                      InkWell(
                                        onTap: () {
                                          openUrlInExternalBrowser(
                                              FirebaseRemoteConfig.instance
                                                  .getString(
                                                      RemoteConfigConstants
                                                          .GB_DISCORD_LINK));
                                        },
                                        child: Padding(
                                            padding: const EdgeInsets.symmetric(
                                                vertical: 1.0),
                                            child: Text(
                                              AppStrings.joinDiscordServer,
                                              style: RegularTextStyle(
                                                  color: AppColor.successGreen,
                                                  height: 1.2,
                                                  fontSize: 12.0),
                                            )),
                                      )
                                    ]))
                              ]),
                        ],
                      )
                  ]);
            return SizedBox.shrink();
          },
          buildWhen: (p, c) => c is TournamentChanged),
      padding: const EdgeInsets.all(15.0));

  Widget _userGameAndSquad() {
    return _tournamentBloc.isTournamentJoined
        ? BlocBuilder<TournamentBloc, TournamentState>(
            builder: (context, state) {
              if (state is MyRankLoaded && state.myLeaderboard != null)
                return Container(
                    color: AppColor.titleBarBg,
                    padding: const EdgeInsets.symmetric(horizontal: 16.0),
                    child: Column(children: [
                      const Divider(
                          color: AppColor.dividerColor,
                          height: 2.0,
                          thickness: 2.0),
                      const SizedBox(height: 10.0),
                      Row(children: [
                        Expanded(
                            child: BoldText(sprintf(AppStrings.topXGames, [
                          _tournamentBloc.currentTournament.tournament.gameCount
                        ]))),
                        const SizedBox(width: 10.0),
                        BoldText(
                            (state.myLeaderboard!.topGames?.gameResults
                                        ?.length ??
                                    0)
                                .toString(),
                            fontSize: 17.0,
                            color: AppColor.textHighlighted)
                      ]),
                      const SizedBox(height: 10.0),
                      Row(children: [
                        Expanded(child: BoldText(AppStrings.totalPlayed)),
                        const SizedBox(width: 10.0),
                        BoldText(
                            (state.myLeaderboard!.matchesPlayed ?? 0)
                                .toString(),
                            fontSize: 17.0,
                            color: AppColor.textHighlighted)
                      ]),
                      const SizedBox(height: 20.0)
                    ]));
              else if (state is LeaderboardLoading &&
                  state.stateLoading == true)
                return appCircularProgressIndicator();
              return SizedBox.shrink();
            },
            buildWhen: (previous, current) =>
                current is MyRankLoaded || current is LeaderboardLoading,
            bloc: _tournamentBloc)
        : SizedBox.shrink();
  }

  Widget _cta(BuildContext context) {

    var tournament = _tournamentBloc.currentTournament.tournament;

    var isTournamentFull =
        tournament.userCount == (
    tournament.tournamentGroup() == GameTeamGroup.squad ||
    tournament.tournamentGroup() == GameTeamGroup.duo
    ? tournament.rules.maxTeams
        : tournament.rules.maxUsers
    );
    return (!_tournamentBloc.isTournamentJoined &&
            canJoinTournament(
                    _tournamentBloc.currentTournament,
                    _tournamentBloc.user,
                    context.read<ApplicationBloc>().userCurrentGame,
                    false) ==
                null)
        ? Container(
            color: AppColor.titleBarBg,
            padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 10),
            child: Column(children: [
              secondaryButton(
                  isTournamentFull
                      ? AppStrings.tournamentFull
                      : _tournamentBloc.currentTournament.tournamentGroup() ==
                              GameTeamGroup.solo
                          ? AppStrings.joinString(
                              _tournamentBloc.currentTournament.tournament.fee)
                          : _tournamentBloc.currentTournament.squad == null
                              ? AppStrings.createJoin
                              : AppStrings.joinString(_tournamentBloc
                                  .currentTournament.tournament.fee), () async {
                if (!isTournamentFull) {
                  if (_tournamentBloc.currentTournament.tournament.qualifiers !=
                      null) {
                    _tournamentBloc.checkUserQualification(
                        context, _tournamentBloc.currentTournament.tournament);
                  } else {
                    if (_tournamentBloc
                            .currentTournament.tournament.matchType ==
                        MatchType.headToHead) {
                      await UiUtils.getInstance.showCustomDialog(
                          context,
                          MobileInputForIDPDialog(
                              _tournamentBloc.currentTournament,
                              _tournamentBloc.user!.phone!,
                              (tournament, pageType, phone) {
                            _tournamentBloc.joinTournament(
                                context, tournament.tournament,
                                phoneNumber: phone);
                          }, 0),
                          dismissible: false);
                    } else {
                      _tournamentBloc.joinTournament(context,
                          _tournamentBloc.currentTournament.tournament);
                    }
                  }
                }
              },
                  isQualified:
                      _tournamentBloc.currentTournament.tournament.matchType ==
                              MatchType.headToHead
                          ? false
                          : _tournamentBloc
                                  .currentTournament.tournament.qualifiers !=
                              null)
            ]))
        : SizedBox.shrink();
  }
}

class _LeaderboardWidget extends StatefulWidget {
  final UserTournamentMixin userTournament;

  _LeaderboardWidget(this.userTournament);

  @override
  State<StatefulWidget> createState() => _LeaderboardState();
}

class _LeaderboardState extends State<_LeaderboardWidget> {
  late TournamentBloc _tournamentBloc;
  bool isLocked = false;
  int pageNum = 0;
  ScrollController _scrollController = ScrollController();
  bool? myPositionVisible;
  int myPosition = -1;
  double listHeight = 0;
  LeaderboardItemMapper? myLeaderboard;

  @override
  Widget build(BuildContext context) {
    return DecoratedBox(
        decoration: BoxDecoration(color: AppColor.colorGrayBg1),
        child: Column(children: [
          widget.userTournament.tournament.matchType == MatchType.headToHead
              ? _CustomTournamentPointBreakDown(widget.userTournament)
              : _PointBreakDown(widget.userTournament),
          const Divider(
              height: 1.0, thickness: 1.0, color: AppColor.dividerColor),
          _leaderboardHeader(),
          Expanded(
              child: Stack(children: [
            BlocBuilder<TournamentBloc, TournamentState>(
                builder: (ctx, state) {
                  if (state is LeaderboardLoaded &&
                      widget.userTournament.tournament.id ==
                          state.tournamentId) {
                    myPosition = state.myIndex;
                    if (state.leaderboard.isEmpty) {
                      if (canJoinTournament(
                              _tournamentBloc.currentTournament,
                              _tournamentBloc.user,
                              context.read<ApplicationBloc>().userCurrentGame,
                              false) ==
                          null)
                        return emptyState("Be the first one to score.");
                      else if (_tournamentBloc
                              .currentTournament.tournament.userCount ==
                          _tournamentBloc
                              .currentTournament.tournament.rules.maxUsers)
                        return emptyState(AppStrings.tournamentFull);
                      else
                        return emptyState(
                            "You have entered a dark empty room.\nPlease go back.",
                            lottieJson: "empty_ghost_state.json");
                    } else {
                      return LayoutBuilder(builder: (context, constraints) {
                        listHeight = constraints.maxHeight;
                        return RefreshIndicator(
                            child: ListView.separated(
                                padding: EdgeInsets.zero,
                                physics: const AlwaysScrollableScrollPhysics(),
                                itemBuilder: (_, index) {
                                  if (index <= state.leaderboard.length - 1)
                                    return _leaderboardRow(
                                        state.leaderboard[index]);
                                  return const SizedBox(height: 46.0);
                                },
                                controller: _scrollController,
                                separatorBuilder: (_, index) => Divider(
                                    height: 2.0,
                                    thickness: 2.0,
                                    color: AppColor.titleBarBg),
                                itemCount: state.leaderboard.length + 1),
                            onRefresh: () async {
                              isLocked = true;
                              await _tournamentBloc.refreshPage(context);
                              await _tournamentBloc.getLeaderboard(
                                  context, widget.userTournament.tournament.id);
                              isLocked = false;
                              return Future.value();
                            });
                      });
                    }
                  } else if (state is LeaderboardLoading &&
                      state.stateLoading == true) {
                    return appCircularProgressIndicator();
                  } else
                    return SizedBox.shrink();
                },
                bloc: _tournamentBloc,
                buildWhen: (previous, current) =>
                    current is LeaderboardLoaded ||
                    current is LeaderboardLoading),
            Align(
                alignment: Alignment.bottomCenter,
                child: BlocListener<TournamentBloc, TournamentState>(
                    listener: (context, state) {
                      if (state is MyRankLoaded) {
                        myLeaderboard = state.myLeaderboard;
                        decideFloaterVisibility(true);
                      } else if (state is LeaderboardLoaded) {
                        myPosition = state.myIndex;
                      }
                    },
                    bloc: _tournamentBloc,
                    listenWhen: (previous, current) => current is MyRankLoaded,
                    child: Visibility(
                        visible: myPositionVisible == true,
                        child: myLeaderboard == null
                            ? SizedBox.shrink()
                            : _leaderboardRow(myLeaderboard!,
                                isFloater: true))))
          ]))
        ]));
  }

  void _pagination() {
    decideFloaterVisibility(false);
    if ((_scrollController.position.pixels ==
            _scrollController.position.maxScrollExtent &&
        !isLocked)) {
      _tournamentBloc.getLeaderboard(
          context, widget.userTournament.tournament.id);
    }
  }

  String _getHeaderValueForName(){
    if (widget.userTournament.isSolo())
      return AppStrings.playerName;
    return AppStrings.teamName;
  }

  Widget _leaderboardHeader() => Container(
      color: AppColor.titleBarBg,
      padding: const EdgeInsets.symmetric(
          vertical: 8.0, horizontal: _CONTENT_PADDING_HORIZONTAL),
      child: Row(children: [
        const SizedBox(width: 24.0),
        SvgPicture.asset("${imageAssets}ic_rank_arrow.svg"),
        const SizedBox(width: 10.0),
        Expanded(
            child: RegularText(_getHeaderValueForName(), fontSize: 12.0),
            flex: 2),
        const SizedBox(width: 10.0),
        Expanded(
            child: RegularText(AppStrings.gamesPlayed,
                fontSize: 10.0, textAlign: TextAlign.center),
            flex: 1),
        const SizedBox(width: 10.0),
        Expanded(
            child: RegularText(AppStrings.rankPoints,
                fontSize: 10.0, textAlign: TextAlign.center),
            flex: 1),
        const SizedBox(width: 10.0),
        Expanded(
            child: RegularText(AppStrings.killPoints,
                fontSize: 10.0, textAlign: TextAlign.center),
            flex: 1),
        const SizedBox(width: 10.0),
        Expanded(
            child: RegularText(AppStrings.totalPoints,
                fontSize: 10.0, textAlign: TextAlign.center),
            flex: 1),
        const SizedBox(width: 10.0),
        Expanded(
            child: RegularText(AppStrings.pointsBehind,
                fontSize: 10.0, textAlign: TextAlign.center),
            flex: 1)
      ]));

  Widget _leaderboardRow(LeaderboardItemMapper leaderboard,
      {bool isFloater = false}) {
    var textColor = leaderboard.myId == _tournamentBloc.userOrTeamId
        ? AppColor.highlightCardTextColor
        : Colors.white;
    var decoration = TextDecoration.none;

    if(leaderboard.isDisqualified == true){
      textColor = AppColor.grayTextB3B3B3;
      decoration = TextDecoration.lineThrough;
    }

    return Container(
        color: isFloater ? null : AppColor.leaderboardRow,
        decoration: isFloater
            ? BoxDecoration(
                gradient: AppColor.cardGradient,
                border: Border.all(color: AppColor.colorAccent, width: 1.0))
            : null,
        height: 45.0,
        child: Row(children: [
          const SizedBox(width: 2.0),
          _avatar(leaderboard),
          SizedBox(
              width: 24.0,
              child: RegularText(leaderboard.rank.toString(),
                  fontSize: 12.0,
                  textAlign: TextAlign.center,
                  decoration: decoration,
                  color: textColor)),
          const SizedBox(width: 3.0),
          Expanded(
              child: RegularText( "${leaderboard.name ?? "-" } ${leaderboard.isDisqualified == true ? AppStrings.disqualified : ""}",
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  color: textColor,
                  decoration: decoration,
                  fontSize: 12.0),
              flex: 2),
          const SizedBox(width: 10.0),
          Expanded(
              child: RegularText(leaderboard.matchesPlayed?.toString() ?? "",
                  fontSize: 10.0,
                  textAlign: TextAlign.center,
                  decoration: decoration,
                  color: textColor),
              flex: 1),
          const SizedBox(width: 10.0),
          Expanded(
              child: RegularText(
                  _tournamentBloc.getTopRankScore(leaderboard).toString(),
                  fontSize: 10.0,
                  textAlign: TextAlign.center,
                  decoration: decoration,
                  color: textColor),
              flex: 1),
          const SizedBox(width: 10.0),
          Expanded(
              child: RegularText(
                  _tournamentBloc.getTopKillScore(leaderboard).toString(),
                  fontSize: 10.0,
                  textAlign: TextAlign.center,
                  decoration: decoration,
                  color: textColor),
              flex: 1),
          const SizedBox(width: 10.0),
          Expanded(
              child: RegularText(leaderboard.score.toString(),
                  fontSize: 10.0,
                  textAlign: TextAlign.center,
                  decoration: decoration,
                  color: textColor),
              flex: 1),
          const SizedBox(width: 10.0),
          Expanded(
              child: RegularText(leaderboard.behindBy.toString(),
                  fontSize: 10.0,
                  textAlign: TextAlign.center,
                  decoration: decoration,
                  color: textColor),
              flex: 1)
        ]));
  }

  Stack _avatar(LeaderboardItemMapper leaderboard) {
    return Stack(
          children: [
            ColorFiltered(
                colorFilter: ColorFilter.mode(
                  Colors.black.withOpacity(
                      leaderboard.isDisqualified != true ? 0.0 : 0.6),
                  BlendMode.srcATop,
                ),
                child: Image.network(
                    leaderboard.myPhoto ??
                        ImageConstants.DEFAULT_USER_PLACEHOLDER,
                    width: 16.0,
                    height: 16.0)),
            if (leaderboard.isDisqualified == true)
              Icon(
                Icons.block,
                color: Colors.red,
                size: 16,
              ),
          ],
        );
  }

  @override
  void initState() {
    super.initState();
    _scrollController.addListener(_pagination);
    _tournamentBloc = context.read<TournamentBloc>();
    _tournamentBloc.getLeaderboard(context, widget.userTournament.tournament.id,
        showLoader: true);
    AnalyticService.getInstance().trackEvents(Events.LB_TAB_LOADED,
        properties: {"id": widget.userTournament.tournament.id});
  }

  void decideFloaterVisibility(bool forceUpdate) {
    double itemHeight =
        45.0 + 2; // including padding above and below the list item
    if (_scrollController.positions.isNotEmpty) {
      double scrollOffset = _scrollController.offset;
      int firstVisibleItemIndex = (scrollOffset / itemHeight).floor();
      // debugPrint("first_visible_item--->" + firstVisibleItemIndex.toString());

      /*int firstCompletelyVisibleItemIndex =
        (scrollOffset / itemHeight).ceil(); //convert
    debugPrint("first_completely_visible_item--->" +
        firstCompletelyVisibleItemIndex.toString());*/
      int lastVisibleItemIndex =
          ((scrollOffset + listHeight) / itemHeight).floor();
      // debugPrint("last_visible_item--->" + lastVisibleItemIndex.toString());
      if (_tournamentBloc.isTournamentJoined) {
        bool flag;
        if (myPosition == -1) {
          flag = true;
        } else {
          if (myPosition >= firstVisibleItemIndex &&
              myPosition <= lastVisibleItemIndex) {
            flag = false;
          } else {
            flag = true;
          }
        }
        if (flag != myPositionVisible || forceUpdate)
          setState(() {
            myPositionVisible = flag;
          });
      }
    }

    /*int lastCompletelyVisibleItemIndex = firstCompletelyVisibleItemIndex +
        (scrollOffset % listHeight) -
        1;
    debugPrint("last_completely_visible_item--->" +
        lastCompletelyVisibleItemIndex.toString());*/
  }
}

class _PointBreakDown extends StatefulWidget {
  final UserTournamentMixin userTournament;

  _PointBreakDown(this.userTournament);

  @override
  State<StatefulWidget> createState() => _PointState();
}

class _PointState extends State<_PointBreakDown> {
  bool pointsBreakdownExpanded = false;
  bool rewardsExpanded = false;
  Duration? remainingTime;
  Timer? _timer;

  late TournamentBloc _tournamentBloc;

  @override
  Widget build(BuildContext context) => Column(children: [
        Padding(
            padding: const EdgeInsets.symmetric(
                horizontal: _CONTENT_PADDING_HORIZONTAL),
            child: Row(children: [
              InkWell(
                  onTap: () {
                    setState(() {
                      pointsBreakdownExpanded = !pointsBreakdownExpanded;
                      rewardsExpanded = false;
                    });
                  },
                  child: Container(
                      margin: const EdgeInsets.symmetric(vertical: 10.0),
                      padding: const EdgeInsets.symmetric(
                          vertical: 5.0, horizontal: 8.0),
                      color: AppColor.colorGrayBg2,
                      child: Row(children: [
                        RegularText(AppStrings.pointsBreakDown,
                            color: AppColor.textSubTitle),
                        const SizedBox(width: 7.0),
                        RotatedBox(
                            quarterTurns: pointsBreakdownExpanded ? 2 : 0,
                            child: const Icon(Icons.arrow_drop_down,
                                color: AppColor.textSubTitle)),
                      ]))),
              SizedBox(width: 10.0),
              InkWell(
                  onTap: () {
                    setState(() {
                      rewardsExpanded = !rewardsExpanded;
                      pointsBreakdownExpanded = false;
                    });
                  },
                  child: Container(
                      margin: const EdgeInsets.symmetric(vertical: 10.0),
                      padding: const EdgeInsets.symmetric(
                          vertical: 5.0, horizontal: 8.0),
                      color: AppColor.colorGrayBg2,
                      child: Row(children: [
                        RegularText("${AppStrings.reward}s",
                            color: AppColor.textSubTitle),
                        const SizedBox(width: 7.0),
                        RotatedBox(
                            quarterTurns: rewardsExpanded ? 2 : 0,
                            child: const Icon(Icons.arrow_drop_down,
                                color: AppColor.textSubTitle)),
                      ]))),
              Spacer(),
              RegularText(remainingTime != null
                  ? TimeUtils.instance.getDurationString(remainingTime!,
                      enforceDHMPattern: false)
                  : AppStrings.tournamentEnded)
            ])),
        const Divider(
            height: 1.0, thickness: 1.0, color: AppColor.dividerColor),
        if (pointsBreakdownExpanded) _breakdownWidget(),
        if (rewardsExpanded) _rewardsWidget()
      ]);

  Widget _breakdownWidget() {
    final scoring = _tournamentBloc.getRankScoring();

    return Container(
        padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
        color: AppColor.colorGrayBg2,
        child: Column(children: [
          Row(children: [
            _pointDistributionCard(
                ESports.bgmi.name.toUpperCase() +
                    " " +
                    AppStrings.rankPoints.toLowerCase(),
                sprintf(
                    sprintf(AppStrings.top15text, [
                      _tournamentBloc
                          .scoring?.data?.scoring.rankPoints.last.rank
                          .toString()
                    ]),
                    [ESports.bgmi.name.toUpperCase()])),
            Padding(
                padding: EdgeInsets.all(14.0),
                child: BoldText("+", fontSize: 16.0)),
            _pointDistributionCard(AppStrings.killPoints, AppStrings.killText),
            Padding(
                padding: EdgeInsets.all(14.0),
                child: BoldText("=", fontSize: 16.0)),
            _pointDistributionCard(
                AppStrings.totalPoints,
                sprintf(AppStrings.totalText, [
                  _tournamentBloc.currentTournament.tournament.gameCount
                      .toString()
                ]))
          ]),
          SizedBox(height: 5.0),
          scoring.isNotEmpty
              ? SizedBox(
                  height: 20.0,
                  child: Marquee(
                      text: scoring,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      blankSpace: 20.0,
                      velocity: 50.0,
                      startPadding: 10.0,
                      accelerationDuration: Duration(seconds: 1),
                      accelerationCurve: Curves.linear,
                      decelerationDuration: Duration(seconds: 1),
                      decelerationCurve: Curves.easeOut,
                      style: RegularTextStyle(
                          color: AppColor.textSubTitle, fontSize: 12.0),
                      pauseAfterRound: Duration(seconds: 2)))
              : const SizedBox(height: 20.0)
        ]));
  }

  Widget _pointDistributionCard(String title, String desc) => Expanded(
      child: Container(
          color: AppColor.tournamentCardBG,
          padding: const EdgeInsets.all(4.0),
          child:
              Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
            BoldText(title, fontSize: 12.0, textAlign: TextAlign.center),
            const SizedBox(height: 4.0),
            RegularText(desc, fontSize: 10.0, textAlign: TextAlign.center)
          ])));

  @override
  void initState() {
    super.initState();
    _tournamentBloc = context.read<TournamentBloc>();
    var now = DateTime.now();
    if (widget.userTournament.tournament.endTime.isAfter(now)) {
      remainingTime = widget.userTournament.tournament.endTime.difference(now);
    } else {
      remainingTime = null;
    }

    _timer = Timer.periodic(Duration(seconds: 1), (timer) {
      var now = DateTime.now();
      if (widget.userTournament.tournament.endTime.isAfter(now)) {
        setState(() {
          remainingTime =
              widget.userTournament.tournament.endTime.difference(now);
        });
      } else {
        timer.cancel();
        setState(() {
          remainingTime = null;
        });
      }
    });
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  Widget _rewardsWidget() => Container(
      color: AppColor.colorGrayBg2,
      height: 60.0,
      child: ListView.builder(
          scrollDirection: Axis.horizontal,
          itemBuilder: (context, index) {
            TournamentMixin$WinningDistribution distribution =
                widget.userTournament.tournament.winningDistribution[index];
            String s = distribution.startRank == distribution.endRank
                ? "Rank ${distribution.startRank}"
                : "Rank ${distribution.startRank} - ${distribution.endRank}";
            return Padding(
                padding: const EdgeInsets.symmetric(
                    vertical: 12.0, horizontal: 20.0),
                child: Column(children: [
                  RegularText(s, fontSize: 12),
                  RegularText(
                      "${AppStrings.rupeeSymbol}${(0.01 * distribution.value * widget.userTournament.tournament.maxPrize).toInt()}",
                      fontSize: 16.0,
                      color: AppColor.highlightCardTextColor)
                ]));
          },
          itemCount:
              widget.userTournament.tournament.winningDistribution.length));
}

class _CustomTournamentPointBreakDown extends StatefulWidget {
  final UserTournamentMixin userTournament;

  _CustomTournamentPointBreakDown(this.userTournament);

  @override
  State<_CustomTournamentPointBreakDown> createState() =>
      _CustomTournamentPointBreakDownState();
}

class _CustomTournamentPointBreakDownState
    extends State<_CustomTournamentPointBreakDown> {
  var showUserRegistrationTime = false;
  var showPasswordVisibleTime = false;
  var passwordAvailable = false;
  var gameLive = false;
  var gameEnded = false;
  var gameStartPasswordNotAvailable = false;
  var registrationClose = false;

  bool pointsBreakdownExpanded = false;
  bool rewardsExpanded = false;
  Duration? remainingTime;
  Timer? _timer;

  late TournamentBloc _tournamentBloc;

  @override
  Widget build(BuildContext context) => Column(children: [
        Padding(
            padding: const EdgeInsets.symmetric(
                horizontal: _CONTENT_PADDING_HORIZONTAL),
            child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      InkWell(
                          onTap: () {
                            setState(() {
                              pointsBreakdownExpanded =
                                  !pointsBreakdownExpanded;
                              rewardsExpanded = false;
                            });
                          },
                          child: Container(
                              margin:
                                  const EdgeInsets.symmetric(vertical: 10.0),
                              padding: const EdgeInsets.symmetric(
                                  vertical: 5.0, horizontal: 8.0),
                              color: AppColor.colorGrayBg2,
                              child: Row(children: [
                                RegularText(AppStrings.pointsBreakDown,
                                    color: AppColor.textSubTitle),
                                const SizedBox(width: 7.0),
                                RotatedBox(
                                    quarterTurns:
                                        pointsBreakdownExpanded ? 2 : 0,
                                    child: const Icon(Icons.arrow_drop_down,
                                        color: AppColor.textSubTitle)),
                              ]))),
                      SizedBox(width: 10.0),
                      InkWell(
                          onTap: () {
                            setState(() {
                              rewardsExpanded = !rewardsExpanded;
                              pointsBreakdownExpanded = false;
                            });
                          },
                          child: Container(
                              margin:
                                  const EdgeInsets.symmetric(vertical: 10.0),
                              padding: const EdgeInsets.symmetric(
                                  vertical: 5.0, horizontal: 8.0),
                              color: AppColor.colorGrayBg2,
                              child: Row(children: [
                                RegularText("${AppStrings.reward}s",
                                    color: AppColor.textSubTitle),
                                const SizedBox(width: 7.0),
                                RotatedBox(
                                    quarterTurns: rewardsExpanded ? 2 : 0,
                                    child: const Icon(Icons.arrow_drop_down,
                                        color: AppColor.textSubTitle)),
                              ]))),
                    ],
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      getCustomRoomCenterStateWidget(),
                      SizedBox(
                        width: gameLive ? 62 : 36,
                      ),
                      getCustomRoomEndStateWidget()
                    ],
                  )
                ])),
        const Divider(
            height: 1.0, thickness: 1.0, color: AppColor.dividerColor),
        if (pointsBreakdownExpanded) _breakdownWidget(),
        if (rewardsExpanded) _rewardsWidget()
      ]);

  Widget _breakdownWidget() {
    final scoring = _tournamentBloc.getRankScoring();

    return Container(
        padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
        color: AppColor.colorGrayBg2,
        child: Column(children: [
          Row(children: [
            _pointDistributionCard(
                ESports.bgmi.name.toUpperCase() +
                    " " +
                    AppStrings.rankPoints.toLowerCase(),
                sprintf(
                    sprintf(AppStrings.top15text, [
                      _tournamentBloc
                          .scoring?.data?.scoring.rankPoints.last.rank
                          .toString()
                    ]),
                    [ESports.bgmi.name.toUpperCase()])),
            Padding(
                padding: EdgeInsets.all(14.0),
                child: BoldText("+", fontSize: 16.0)),
            _pointDistributionCard(AppStrings.killPoints, AppStrings.killText),
            Padding(
                padding: EdgeInsets.all(14.0),
                child: BoldText("=", fontSize: 16.0)),
            _pointDistributionCard(
                AppStrings.totalPoints,
                sprintf(AppStrings.totalText, [
                  _tournamentBloc.currentTournament.tournament.gameCount
                      .toString()
                ]))
          ]),
          SizedBox(height: 5.0),
          scoring.isNotEmpty
              ? SizedBox(
                  height: 20.0,
                  child: Marquee(
                      text: scoring,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      blankSpace: 20.0,
                      velocity: 50.0,
                      startPadding: 10.0,
                      accelerationDuration: Duration(seconds: 1),
                      accelerationCurve: Curves.linear,
                      decelerationDuration: Duration(seconds: 1),
                      decelerationCurve: Curves.easeOut,
                      style: RegularTextStyle(
                          color: AppColor.textSubTitle, fontSize: 12.0),
                      pauseAfterRound: Duration(seconds: 2)))
              : const SizedBox(height: 20.0)
        ]));
  }

  Widget _pointDistributionCard(String title, String desc) => Expanded(
      child: Container(
          color: AppColor.tournamentCardBG,
          padding: const EdgeInsets.all(4.0),
          child:
              Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
            BoldText(title, fontSize: 12.0, textAlign: TextAlign.center),
            const SizedBox(height: 4.0),
            RegularText(desc, fontSize: 10.0, textAlign: TextAlign.center)
          ])));

  getCustomRoomCenterStateWidget() {
    //this will check if user join and password is available and if game is live than this show live game
    // or join time to remain for game live

    if ((showPasswordVisibleTime || passwordAvailable) && gameLive) {
      return Row(
        children: [
          LottieBuilder.asset("${lottieAssets}tournament_live.json",
              height: 36, width: 36),
          SizedBox(
            width: 4,
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                AppStrings.gameIsLive,
                style: SemiBoldTextStyle(
                    color: AppColor.whiteTextColor,
                    fontWeight: FontWeight.w700,
                    fontSize: 14),
              ),
              SizedBox(
                height: 4,
              ),
              InkWell(
                onTap: () {
                  AnalyticService.getInstance()
                      .trackEvents(Events.VIEW_CUSTOM_ROOM_STREAM, properties: {
                    "tournament":
                        _tournamentBloc.currentTournament.tournament.id
                  });
                  openUrlInExternalBrowser(FirebaseRemoteConfig.instance
                      .getString(
                          RemoteConfigConstants.GB_CUSTOM_ROOM_PLAYLIST));
                },
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Icon(
                      Icons.play_arrow,
                      color: Colors.white,
                      size: 14,
                    ),
                    SizedBox(
                      width: 4,
                    ),
                    Padding(
                      padding: EdgeInsets.only(top: 2),
                      child: RegularText(
                        AppStrings.clickToWatch,
                        color: AppColor.blueD5C6F7,
                        fontSize: 10,
                        fontWeight: FontWeight.w400,
                      ),
                    )
                  ],
                ),
              )
            ],
          )
        ],
      );
    } else if ((showPasswordVisibleTime || passwordAvailable) && !gameLive) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        children: [
          Visibility(
            visible: remainingTime != null,
            child: Text(
              AppStrings.passwordWillBeSharedIn,
              style: SemiBoldTextStyle(
                  color: AppColor.grayB8B8B9,
                  fontWeight: FontWeight.w400,
                  fontSize: 12),
            ),
          ),
          SizedBox(
            height: 4,
          ),
          remainingTime != null
              ? RegularText(
                  TimeUtils.instance.getDurationString(remainingTime!,
                      enforceDHMPattern: true),
                  color: AppColor.whiteF6F6F6,
                  fontSize: 14,
                  fontWeight: FontWeight.w700,
                )
              : const SizedBox()
        ],
      );
    } else {
      return SizedBox();
    }
  }

  getCustomRoomEndStateWidget() {
    if (gameStartPasswordNotAvailable) {
      return Row(
        children: [
          Icon(Icons.watch_later_outlined,
              size: 24, color: AppColor.whiteTextColor),
          const SizedBox(
            width: 4,
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                AppStrings.passwordShareInMovement,
                style: SemiBoldTextStyle(
                    color: AppColor.whiteTextColor,
                    fontWeight: FontWeight.w700,
                    fontSize: 14),
              ),
              const SizedBox(
                height: 4,
              ),
            ],
          )
        ],
      );
    } else if (gameEnded) {
      //when game end will show that url
      return Column(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Visibility(
            visible: remainingTime != null,
            child: Text(
              showUserRegistrationTime
                  ? AppStrings.registrationCloseIn
                  : showPasswordVisibleTime
                      ? AppStrings.passwordWillBeSharedIn
                      : "",
              style: SemiBoldTextStyle(
                  color: AppColor.grayB8B8B9,
                  fontWeight: FontWeight.w400,
                  fontSize: 12),
            ),
          ),
          SizedBox(
            height: 4,
          ),
          RegularText(
            remainingTime != null
                ? TimeUtils.instance
                    .getDurationString(remainingTime!, enforceDHMPattern: true)
                : registrationClose
                    ? AppStrings.registrationClose
                    : AppStrings.tournamentEnded,
            color: AppColor.whiteF6F6F6,
            fontSize: 14,
            fontWeight: FontWeight.w700,
          )
        ],
      );
    } else if (gameLive && widget.userTournament.joinedAt != null) {
      //game is live and user join that tournament
      return InkWell(
        onTap: () async {
          AnalyticService.getInstance()
              .trackEvents(Events.CUSTOM_ROOM_PASSWORD_VIEW, properties: {
            "tournament": _tournamentBloc.currentTournament.tournament.id
          });
          var metaData =
              _tournamentBloc.customMatches.getTournamentMatches.first.metadata;
          await UiUtils.getInstance.showCustomDialog(
              context,
              ShowCustomRoomInfoDialog(
                metaData?.roomId ?? "",
                metaData?.roomPassword ?? "",
                slotNumber: _tournamentBloc.currentTournament
                    .tournamentMatchUser?.slotInfo?.teamNumber,
              ),
              dismissible: false);
        },
        child: Container(
            height: 38.0,
            margin: const EdgeInsets.all(12),
            decoration: BoxDecoration(
                border: Border.all(color: AppColor.whiteTextColor)),
            child: Padding(
                padding:
                    const EdgeInsets.symmetric(vertical: 4.0, horizontal: 18),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                      RegularText(AppStrings.showPassword,
                          fontWeight: FontWeight.w700,
                          fontSize: 12,
                          color: AppColor.whiteEEE9FC)
                    ]),
                  ],
                ))),
      );
    } else if (gameLive) {
      // is user not join and tournament live than will show live tounrmanet state
      return Row(
        children: [
          LottieBuilder.asset("${lottieAssets}tournament_live.json",
              height: 36, width: 36),
          SizedBox(
            width: 4,
          ),
          Column(
            crossAxisAlignment: CrossAxisAlignment.end,
            children: [
              Text(
                AppStrings.gameIsLive,
                style: SemiBoldTextStyle(
                    color: AppColor.whiteTextColor,
                    fontWeight: FontWeight.w700,
                    fontSize: 14),
              ),
              SizedBox(
                height: 4,
              ),
              InkWell(
                onTap: () {
                  AnalyticService.getInstance()
                      .trackEvents(Events.VIEW_CUSTOM_ROOM_STREAM, properties: {
                    "tournament":
                        _tournamentBloc.currentTournament.tournament.id
                  });
                  openUrlInExternalBrowser(FirebaseRemoteConfig.instance
                      .getString(
                          RemoteConfigConstants.GB_CUSTOM_ROOM_PLAYLIST));
                },
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Icon(
                      Icons.play_arrow,
                      color: Colors.white,
                      size: 14,
                    ),
                    SizedBox(
                      width: 4,
                    ),
                    Padding(
                      padding: EdgeInsets.only(top: 2),
                      child: RegularText(
                        AppStrings.clickToWatch,
                        color: AppColor.blueD5C6F7,
                        fontSize: 10,
                        fontWeight: FontWeight.w400,
                      ),
                    )
                  ],
                ),
              )
            ],
          )
        ],
      );
    } else if (passwordAvailable) {
      return InkWell(
        onTap: () async {
          AnalyticService.getInstance()
              .trackEvents(Events.CUSTOM_ROOM_PASSWORD_VIEW, properties: {
            "tournament": _tournamentBloc.currentTournament.tournament.id
          });
          var metaData =
              _tournamentBloc.customMatches.getTournamentMatches.first.metadata;
          await UiUtils.getInstance.showCustomDialog(
              context,
              ShowCustomRoomInfoDialog(
                metaData?.roomId ?? "",
                metaData?.roomPassword ?? "",
                slotNumber: _tournamentBloc.currentTournament
                    .tournamentMatchUser?.slotInfo?.teamNumber,
              ),
              dismissible: false);
        },
        child: Container(
            height: 38.0,
            margin: const EdgeInsets.all(12),
            decoration: BoxDecoration(
                border: Border.all(color: AppColor.whiteTextColor)),
            child: Padding(
                padding:
                    const EdgeInsets.symmetric(vertical: 4.0, horizontal: 18),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                      RegularText(AppStrings.showPassword,
                          fontWeight: FontWeight.w700,
                          fontSize: 12,
                          color: AppColor.whiteEEE9FC)
                    ]),
                  ],
                ))),
      );
    } else if (widget.userTournament.joinedAt == null) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          Visibility(
            visible: remainingTime != null,
            child: Text(
              showUserRegistrationTime
                  ? AppStrings.registrationCloseIn
                  : showPasswordVisibleTime
                      ? AppStrings.passwordWillBeSharedIn
                      : "",
              style: SemiBoldTextStyle(
                  color: AppColor.grayB8B8B9,
                  fontWeight: FontWeight.w400,
                  fontSize: 12),
            ),
          ),
          SizedBox(
            height: 4,
          ),
          RegularText(
            remainingTime != null
                ? TimeUtils.instance
                    .getDurationString(remainingTime!, enforceDHMPattern: true)
                : registrationClose
                    ? AppStrings.registrationClose
                    : AppStrings.tournamentEnded,
            color: AppColor.whiteF6F6F6,
            fontSize: 14,
            fontWeight: FontWeight.w700,
          )
        ],
      );
    } else {
      return SizedBox();
    }
  }

  @override
  void initState() {
    super.initState();
    _tournamentBloc = context.read<TournamentBloc>();

    calculateTime();
    _timer = Timer.periodic(Duration(seconds: 1), (timer) {
      calculateTime();
    });
  }

  calculateTime() {
    var now = DateTime.now();

    if (widget.userTournament.joinedAt != null) {
      if (_tournamentBloc.customMatches.getTournamentMatches.length > 0 &&
          _tournamentBloc
                  .customMatches.getTournamentMatches.first.metadata!.roomId !=
              null) {
        passwordAvailable = true;
        if (widget.userTournament.tournament.startTime.isAfter(now)) {
          remainingTime = _tournamentBloc
              .customMatches.getTournamentMatches.first.startTime
              .difference(now);
        } else if (widget.userTournament.tournament.startTime.isBefore(now) &&
            widget.userTournament.tournament.endTime.isAfter(now)) {
          gameLive = true;
        } else {
          gameEnded = true;
          remainingTime = null;
        }
      } else if (widget.userTournament.tournament.startTime.isAfter(now)) {
        showPasswordVisibleTime = true;
        remainingTime =
            widget.userTournament.tournament.startTime!.difference(now);
      } else if (widget.userTournament.tournament.startTime.isBefore(now)) {
        gameStartPasswordNotAvailable = true;
      } else {
        gameEnded = true;
        remainingTime = null;
      }
    } else {
      if (widget.userTournament.tournament.joinBy!.isAfter(now)) {
        showUserRegistrationTime = true;
        remainingTime =
            widget.userTournament.tournament.joinBy!.difference(now);
      } else if (widget.userTournament.tournament.startTime.isBefore(now) &&
          widget.userTournament.tournament.endTime.isAfter(now)) {
        gameLive = true;
      } else if (widget.userTournament.tournament.endTime.isAfter(now)) {
        gameEnded = true;
        remainingTime = null;
      } else {
        gameEnded = true;
        remainingTime = null;
      }
    }
    setState(() {});
  }

  @override
  void dispose() {
    _timer?.cancel();
    super.dispose();
  }

  Widget _rewardsWidget() => Container(
      color: AppColor.colorGrayBg2,
      height: 60.0,
      child: ListView.builder(
          scrollDirection: Axis.horizontal,
          itemBuilder: (context, index) {
            TournamentMixin$WinningDistribution distribution =
                widget.userTournament.tournament.winningDistribution[index];
            String s = distribution.startRank == distribution.endRank
                ? "Rank ${distribution.startRank}"
                : "Rank ${distribution.startRank} - ${distribution.endRank}";
            return Padding(
                padding: const EdgeInsets.symmetric(
                    vertical: 12.0, horizontal: 20.0),
                child: Column(children: [
                  RegularText(s, fontSize: 12),
                  RegularText(
                      "${AppStrings.rupeeSymbol}${(0.01 * distribution.value * widget.userTournament.tournament.maxPrize).toInt()}",
                      fontSize: 16.0,
                      color: AppColor.highlightCardTextColor)
                ]));
          },
          itemCount:
              widget.userTournament.tournament.winningDistribution.length));
}

class SquadDetail extends StatelessWidget {
  final TournamentBloc _tournamentBloc;
  final BuildContext _parentContext;

  SquadDetail(this._tournamentBloc, this._parentContext);

  @override
  Widget build(BuildContext context) {
    List<Widget> items = [
      RegularText(AppStrings.teamName),
      const SizedBox(height: 5.0),
      BoldText(_tournamentBloc.currentTournament.squad?.name ?? "",
          fontSize: 24.0),
      const SizedBox(height: 10.0),
      InkWell(
          onTap: () => ShareUtils.getInstance().teamInviteShare(
              _tournamentBloc.currentTournament.squad!,
              _tournamentBloc.currentTournament,
              medium: ShareMedium.CLIPBOARD),
          child: Row(children: [
            RegularText(
                "Invite code: ${_tournamentBloc.currentTournament.squad!.inviteCode}"),
            const SizedBox(width: 5.0),
            Icon(Icons.copy, color: AppColor.textSubTitle)
          ])),
      const SizedBox(height: 15.0),
      Container(
          height: 100.0,
          child: ListView.separated(
              shrinkWrap: true,
              padding: const EdgeInsets.all(0),
              scrollDirection: Axis.horizontal,
              itemBuilder: (ctx, index) => index <
                      _tournamentBloc.currentTournament.squad!.members.length
                  ? _teamMemberContainer(ctx,
                      _tournamentBloc.currentTournament.squad!.members[index])
                  : _teamMemberContainer(ctx, null),
              separatorBuilder: (ctx, index) => const SizedBox(width: 20.0),
              itemCount: _tournamentBloc.currentTournament
                  .tournamentGroup()
                  .teamSize()))
    ];
    if (_ifIHaveNotPaid() || _ifOthersHaveNotPaid())
      items.addAll([
        BoldText(AppStrings.paymentIncomplete, fontSize: 16.0),
        const SizedBox(height: 5.0),
        RegularText(AppStrings.paymentShare, color: AppColor.textDarkGray),
        const SizedBox(height: 15.0)
      ]);

    if (_ifIHaveNotPaid())
      items.addAll([
        secondaryButton(
            "Pay ${AppStrings.rupeeSymbol}${_tournamentBloc.currentTournament.tournament.fee}",
            () {
          _tournamentBloc.joinTournament(
              _parentContext, _tournamentBloc.currentTournament.tournament);
        }),
        const SizedBox(height: 15.0)
      ]);
    else if (_tournamentBloc.currentTournament.squad!.members.length <
        _tournamentBloc.currentTournament.tournamentGroup().teamSize()) {
      items.addAll([
        secondaryButton(AppStrings.inviteTeammates, () {
          navigateToInvitePage(context, _tournamentBloc.currentTournament,
              _tournamentBloc.currentTournament.squad!);
        }),
        const SizedBox(height: 15.0),
        BoldText(
            "${_tournamentBloc.currentTournament.squad!.members.length}/${_tournamentBloc.currentTournament.tournamentGroup().teamSize()} players in team"),
        const SizedBox(height: 10.0),
        RichText(
            text: TextSpan(text: "Do ", style: RegularTextStyle(), children: [
          TextSpan(text: "NOT ", style: BoldTextStyle()),
          TextSpan(
              text: "turn on auto-matching in ${_tournamentBloc.eSports.name}"),
        ])),
        const SizedBox(height: 10.0),
        RegularText(sprintf(AppStrings.youCanPlayWithIncompleteTeam, [
          _tournamentBloc.currentTournament
              .tournamentGroup()
              .teamSize()
              .toString(),
          _tournamentBloc.eSports.name
        ]))
      ]);
    } else if (!_ifOthersHaveNotPaid()) {
      items.addAll([
        RegularText("Payment", fontSize: 16.0),
        BoldText("Complete", fontSize: 20.0),
        const SizedBox(height: 15.0),
        RegularText("Ready to play",
            fontSize: 16.0, color: AppColor.successGreen)
      ]);
    }
    return Material(
        color: Colors.transparent,
        child: Row(children: [
          Expanded(
              child: InkWell(
                  highlightColor: Colors.transparent,
                  focusColor: Colors.transparent,
                  onTap: () => _tournamentBloc.loadSquadDetails(false),
                  child:
                      Container(height: MediaQuery.of(context).size.height))),
          Card(
              elevation: 8.0,
              child: themeContainer(
                  SingleChildScrollView(
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: items),
                  ),
                  padding: const EdgeInsets.symmetric(
                      horizontal: 20.0, vertical: 15.0),
                  width: 350,
                  height: MediaQuery.of(context).size.height))
        ]));
  }

  bool _ifIHaveNotPaid() {
    return !_tournamentBloc.currentTournament.squad!.members
        .firstWhere((element) => element.user.id == _tournamentBloc.user!.id)
        .isReady;
  }

  bool _ifOthersHaveNotPaid() {
    SquadMemberMixin? member = _tournamentBloc.currentTournament.squad!.members
        .firstWhere(
            (element) =>
                element.user.id != _tournamentBloc.user!.id && !element.isReady,
            orElse: () => SquadMixin$SquadMember()..isReady = true);
    return !member.isReady;
  }

  _teamMemberContainer(BuildContext context, SquadMemberMixin? memberMixin) =>
      Column(children: [
        memberMixin != null
            ? Image.network(
                memberMixin.user.image ??
                    ImageConstants.DEFAULT_USER_PLACEHOLDER,
                height: 40.0,
                width: 40.0)
            : InkWell(
                onTap: () {
                  navigateToInvitePage(
                      context,
                      _tournamentBloc.currentTournament,
                      _tournamentBloc.currentTournament.squad!);
                },
                child: ColoredBox(
                    color: AppColor.darkBackground,
                    child: Padding(
                        padding: const EdgeInsets.all(3.0),
                        child: DecoratedBox(
                            decoration: BoxDecoration(
                                border: Border.all(
                                    color: AppColor.dividerColor, width: 1.0)),
                            child: Padding(
                                padding: const EdgeInsets.all(3.0),
                                child: Icon(Icons.add,
                                    color: AppColor.dividerColor,
                                    size: 30.0)))))),
        const SizedBox(height: 7.0),
        RegularText(
            _tournamentBloc.user?.id == memberMixin?.user.id
                ? "You"
                : (memberMixin?.user.username ?? "Add"),
            color: memberMixin != null
                ? AppColor.textSubTitle
                : AppColor.colorAccent),
        const SizedBox(height: 4.0),
        memberMixin != null
            ? RegularText(
                memberMixin.isReady ? AppStrings.ready : AppStrings.notPaid,
                fontSize: 11.0,
                color: memberMixin.isReady
                    ? AppColor.successGreen
                    : AppColor.errorRed)
            : const SizedBox(height: 10.0)
      ]);
}

const _CONTENT_PADDING_HORIZONTAL = 13.0;

class _Mydecor extends InputDecoration {
  _Mydecor(
    String labelText, {
    Widget? prefix,
    Widget? suffix,
  }) : super(
            filled: true,
            labelText: labelText,
            counterText: "",
            border: InputBorder.none,
            contentPadding: EdgeInsets.all(12),
            prefix: prefix,
            suffix: suffix,
            labelStyle: TextStyle(color: AppColor.grayText9E9E9E, fontSize: 12),
            fillColor: AppColor.inputBackground,
            errorBorder: OutlineInputBorder(
                borderSide: BorderSide(color: AppColor.errorRed)),
            errorStyle: TextStyle(color: AppColor.errorRed));
}
