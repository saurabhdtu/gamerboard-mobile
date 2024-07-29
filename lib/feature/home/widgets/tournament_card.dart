import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/home/home_page.dart';
import 'package:gamerboard/feature/home/widgets/tournament_card_timer.dart';
import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/data_type_ext.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/time_utils.dart';
import 'package:gamerboard/utils/ui_utils.dart';

import 'dialogs/custom_room_info_dialog.dart';

class TournamentCard extends StatefulWidget {
  final int tournamentId;
  final TournamentUIState state;
  final TournamentCardType type;
  final double? width;
  final TournamentCardModel model;
  final int pageType;
  final bool profileAvailable;
  final bool userEligible;
  final bool showPlayButtonOnCard;
  final bool squadAvailable;
  final bool showTag;
  final String? tagText;
  final GameTeamGroup group;
  final Function(int) onOpenTournament;
  final Function(int) onJoinTournament;
  final Function(GameTeamGroup) onLaunchGame;
  final Function onShowQualified;
  final Function(int) onShowMobileInput;
  final Widget? teamPlayerWidget;
  final Function(GameTeamGroup) onNavigateToTab;
  final MatchType matchType;

  TournamentCard(
    this.tournamentId,
    this.pageType, {
    required this.state,
    required this.type,
    required this.onOpenTournament,
    required this.onJoinTournament,
    required this.onNavigateToTab,
    required this.onLaunchGame,
    required this.onShowQualified,
    required this.onShowMobileInput,
    required this.group,
    required this.model,
    required this.matchType,
    this.teamPlayerWidget,
    this.tagText,
    this.showTag = false,
    this.width,
    this.squadAvailable = false,
    this.profileAvailable = false,
    this.showPlayButtonOnCard = false,
    this.userEligible = false,
  });

  @override
  State<TournamentCard> createState() => _TournamentCardState();
}

class _TournamentCardState extends State<TournamentCard> {
  final _joinedUpcomingStates = [
    TournamentUIState.LIVE_JOINED,
    TournamentUIState.TEAM_LIVE_PRE_JOINED,
    TournamentUIState.UPCOMING_JOINED,
    TournamentUIState.TEAM_UPCOMING_JOINED,
  ];

/*  final preJoinedStates = [
    TournamentUIState.LIVE_PRE_JOINED,
    TournamentUIState.TEAM_LIVE_PRE_JOINED,
    TournamentUIState.UPCOMING_PRE_JOINED,
  ];*/

  final _userPlayedGameStates = [
    TournamentUIState.TEAM_UPCOMING_PRE_JOINED,
    TournamentUIState.TEAM_LIVE_PRE_JOINED,
    TournamentUIState.TEAM_UPCOMING_JOINED,
    TournamentUIState.TEAM_ENDED,
    TournamentUIState.TEAM_LIVE_JOINED,
    TournamentUIState.TEAM_HISTORY
  ];

  final _userPreJoinedStates = [
    TournamentUIState.UPCOMING_PRE_JOINED,
    TournamentUIState.LIVE_PRE_JOINED,
    TournamentUIState.UPCOMING_JOINED,
    TournamentUIState.TEAM_UPCOMING_JOINED
  ];

  final _livePreJoinStates = [
    TournamentUIState.LIVE_JOINED,
    TournamentUIState.LIVE_PRE_JOINED,
    TournamentUIState.TEAM_LIVE_JOINED,
    TournamentUIState.TEAM_LIVE_PRE_JOINED
  ];

  final _preJoinedStates = [
    TournamentUIState.LIVE_PRE_JOINED,
    TournamentUIState.TEAM_LIVE_PRE_JOINED,
    TournamentUIState.UPCOMING_PRE_JOINED,
    TournamentUIState.TEAM_UPCOMING_PRE_JOINED
  ];

  final _upcomingLiveJoinedStates = [
    TournamentUIState.LIVE_JOINED,
    TournamentUIState.TEAM_LIVE_JOINED,
    TournamentUIState.UPCOMING_JOINED,
    TournamentUIState.TEAM_UPCOMING_JOINED
  ];

  @override
  Widget build(BuildContext context) {
    GameTeamGroup group = widget.group;

    final profileAvailable = widget.profileAvailable;
    final isUserEligible = widget.userEligible;

    final userCount = widget.model.userCount;
    final maxUsers = group == GameTeamGroup.squad || group == GameTeamGroup.duo ?widget.model.maxTeams :widget.model.maxUsers;
    final joinedAt = widget.model.joinedAt;
    final qualifiers = widget.model.qualifiers;
    final pageType = widget.pageType;

    return Container(
        width: widget.width,
        padding: const EdgeInsets.all(2.0),
        margin: const EdgeInsets.all(1.0),
        decoration: BoxDecoration(
            border: Border.all(color: AppColor.dividerColor, width: 1.0),
            color: profileAvailable
                ? AppColor.enableTournamentCard
                : AppColor.disabledLBCardBG,
            borderRadius: BorderRadius.circular(2.0)),
        child: InkWell(
            onTap: () => profileAvailable
                ? widget.onOpenTournament(widget.pageType)
                : widget.onNavigateToTab(group),
            child: Column(
                mainAxisSize: MainAxisSize.max,
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  const SizedBox(width: 4.0),
                  _cardHeader(),
                  const SizedBox(height: 8.0),
                  _cardContent(context),
                  const SizedBox(height: 10.0),
                  _actions(isUserEligible, userCount, maxUsers,
                      profileAvailable, group, joinedAt, qualifiers, pageType),
                  widget.teamPlayerWidget != null
                      ? widget.teamPlayerWidget!
                      : SizedBox.shrink(),
                ])));
  }

  Widget _actions(
      bool isUserEligible,
      int userCount,
      int maxUsers,
      bool profileAvailable,
      GameTeamGroup group,
      DateTime? joinedAt,
      List<dynamic>? qualifiers,
      int pageType) {
    if (isUserEligible) {
      return InkWell(
          onTap: () async {
            _onClickActionButton(userCount, maxUsers, profileAvailable, group,
                joinedAt, qualifiers, pageType);
          },
          child:
              _buildActionButton(userCount, maxUsers, joinedAt, widget.group));
    }
    return SizedBox(
        height: (widget.pageType == HomeScreenIndex.HISTORY) ? 5 : 15.0);
  }

  void _onClickActionButton(
      int userCount,
      int maxUsers,
      bool profileAvailable,
      GameTeamGroup group,
      DateTime? joinedAt,
      List<dynamic>? qualifiers,
      int pageType) {
    _handleNormalTournamentAction(profileAvailable, group);

    _handleCustomTournamentAction(userCount, maxUsers, profileAvailable, group,
        joinedAt, qualifiers, pageType);
  }

  void _handleCustomTournamentAction(
      int userCount,
      int maxUsers,
      bool profileAvailable,
      GameTeamGroup group,
      DateTime? joinedAt,
      List<dynamic>? qualifiers,
      int pageType) {
    if (widget.type != TournamentCardType.custom) return;

    if (widget.model.joinBy?.isAfter(DateTime.now()) != true) {
      return;
    }

    if (userCount >= maxUsers || !profileAvailable) {
      widget.onNavigateToTab(group);
      return;
    }

    if (joinedAt == null) {
      if (qualifiers != null) {
        widget.onShowQualified();
        return;
      }

      if (widget.group == GameTeamGroup.solo) {
        widget.onShowMobileInput(pageType);
      } else {
        widget.onJoinTournament(pageType);
      }
      return;
    }
    return;
  }

  void _handleNormalTournamentAction(
      bool profileAvailable, GameTeamGroup group) {
    if (widget.type != TournamentCardType.classic) return;
    if (widget.model.userCount >= widget.model.maxUsers || !profileAvailable) {
      widget.onNavigateToTab(group);
      return;
    }

    if (widget.model.joinedAt == null) {
      if (widget.model.qualifiers != null) {
        widget.onShowQualified();
        return;
      }
      widget.onJoinTournament(widget.pageType);
      return;
    }
    return;
  }

  Padding _cardContent(BuildContext context) {
    return Padding(
        padding: const EdgeInsets.all(1.0),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          _getPlayerInfo(widget.state,widget.group),
          const SizedBox(height: 6.0),
          RegularText(widget.model.allowedMaps,
              color: AppColor.grayTextB3B3B3,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              fontSize: 12.0),
          const SizedBox(height: 6.0),
          RegularText(
              "Start: ${TimeUtils.instance.formatCardDateTime(widget.model.startTime)}",
              color: AppColor.grayTextB3B3B3,
              fontSize: 12.0,
              textAlign: TextAlign.start),
          const SizedBox(height: 6.0),
          ..._buildDuration(),
          _buildPasswordInfo(context),
          _buildCustomTournamentDuration(),
        ]));
  }

  Padding _cardHeader() {
    return Padding(
        padding: const EdgeInsets.fromLTRB(1.0, 2.0, 2.0, 1.0),
        child: Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          const SizedBox(width: 2.0),
          Expanded(
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                RegularText(widget.model.name,
                    maxLines: 1,
                    fontSize: 12.0,
                    fontWeight: FontWeight.w700,
                    overflow: TextOverflow.ellipsis,
                    color: AppColor.whiteTextColor),
                const SizedBox(height: 4.0),
                    BoldText(
                        widget.model.allowedGameMode,
                        fontSize: 14,
                        color: AppColor.grayTextB3B3B3),
                const SizedBox(height: 6.0),
                _tournamentPrice()
              ])),
          Column(mainAxisAlignment: MainAxisAlignment.start, children: [
            Image.network(widget.model.levelImageUrl,
                width: 24.0, height: 24.0),
            const SizedBox(height: 4.0),
            widget.model.allowedTiers.length == 1
                ? const SizedBox.shrink()
                : RegularText("+${widget.model.allowedTiers.length - 1} more",
                    fontSize: 9.0,
                    color: AppColor.textSubTitle,
                    fontStyle: FontStyle.italic)
          ])
        ]));
  }

  Widget _tournamentPrice() {
    return Row(children: [
      RegularText("${AppStrings.rupeeSymbol}${widget.model.maxPrize}",
          fontSize: 28.0,
          fontWeight: FontWeight.w700,
          color: AppColor.successGreen,
          textAlign: TextAlign.center),
      const SizedBox(width: 8),
      if (widget.tagText != null && widget.showTag) _tournamentTag()
    ]);
  }

  Container _tournamentTag() {
    return Container(
        decoration: BoxDecoration(color: AppColor.errorRed),
        padding: const EdgeInsets.all(4),
        child: RegularText(widget.tagText ?? "",
            fontSize: 10.0,
            color: AppColor.whiteTextColor,
            fontWeight: FontWeight.w700));
  }

  Widget _buildPasswordInfo(BuildContext context) {
    if (widget.state == TournamentUIState.ENDED ||
        widget.type != TournamentCardType.custom) {
      return SizedBox.shrink();
    }
    if (_upcomingLiveJoinedStates.contains(widget.state) &&
        (widget.model.roomId != null)) {
      return InkWell(
          onTap: () async {
            AnalyticService.getInstance().trackEvents(
                Events.CUSTOM_ROOM_PASSWORD_VIEW,
                properties: {"tournament": widget.tournamentId});

            await UiUtils.getInstance.showCustomDialog(
                context,
                ShowCustomRoomInfoDialog(
                  widget.model.roomId ?? "",
                  widget.model.roomPassword ?? "",
                  slotNumber: widget.model.slot,
                ),
                dismissible: false);
          },
          child: RegularText("${AppStrings.showPassword} >",
              color: AppColor.successGreen,
              fontSize: 14.0,
              textAlign: TextAlign.start));
    }
    if (TournamentUIState.LIVE_JOINED == widget.state ||
        TournamentUIState.TEAM_LIVE_JOINED == widget.state) {
      return RegularText("${AppStrings.passwordShareInMovement}",
          color: AppColor.grayTextB3B3B3,
          fontSize: 14.0,
          textAlign: TextAlign.start);
    }

    return _buildCustomTournamentTimerLabel();
  }

  Widget _buildCustomTournamentTimerLabel() {
    if (widget.type == TournamentCardType.classic) return SizedBox.shrink();

    final state = widget.state;
    final model = widget.model;
    final currentTime = DateTime.now();

    String text = "";

    if (_preJoinedStates.contains(state)) {
      text = AppStrings.registrationCloseIn;
    } else if (_joinedUpcomingStates.contains(state) &&
        model.startTime.isAfter(currentTime)) {
      text = AppStrings.passwordWillBeSharedIn;
    }

    return RegularText(
      text,
      color: AppColor.grayTextB3B3B3,
      fontSize: 12.0,
      textAlign: TextAlign.start,
    );
  }

  Padding _playButton(BuildContext context, GameTeamGroup group) {
    return Padding(
        padding: const EdgeInsets.symmetric(vertical: 18),
        child: primaryButton(AppStrings.play, () {
          widget.onLaunchGame(group);
        },
            active: true,
            paddingVertical: 8,
            isQualified: widget.model.qualifiers != null &&
                widget.type != TournamentCardType.custom,
            paddingHorizontal: 32));
  }

  Container _outlineButton(String text, {IconData? iconData, double height = 38}) {
    return Container(
        height: height,
        margin: const EdgeInsets.all(12),
        decoration:
            BoxDecoration(border: Border.all(color: AppColor.buttonActive)),
        child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 1.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                _qualifierIcon(),
                Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                  Visibility(
                      visible: widget.model.qualifiers == null,
                      child: Icon(iconData ?? Icons.error_outline,
                          size: 16.0,
                          color: AppColor.buttonActive)),
                  const SizedBox(width: 4.0),
                  RegularText(text, color: AppColor.buttonActive),
                  const SizedBox(width: 4.0),
                ]),
                _qualifierIcon()
              ],
            )));
  }

  Visibility _qualifierIcon() {
    return Visibility(
        visible: widget.model.qualifiers != null,
        child: const Icon(Icons.star, color: AppColor.grayColorf0e8fd));
  }

  Widget _getPlayerInfo(TournamentUIState state, GameTeamGroup gameTeamGroup) {



    String suffix = widget.group.getSuffix() +
        (widget.group.isTeamGame() && widget.model.slot != null
            ? " ${AppStrings.dot} #${widget.model.slot}"
            : "");

    if (_userPlayedGameStates.contains(state)) {
      return RegularText(
          _getGameCount(gameTeamGroup,suffix),
          color: AppColor.whiteTextColor,
          fontWeight: FontWeight.w700,
          fontSize: 12.0);
    }

    if (_userPreJoinedStates.contains(state)) {
      return RegularText(
          _getGameCount(gameTeamGroup,suffix),
          color: AppColor.whiteTextColor,
          fontWeight: FontWeight.w700,
          fontSize: 12.0);
    }

    return RegularText(
        widget.model.rank == null
            ?  _getGameCount(gameTeamGroup, suffix)
            : "${widget.model.rank!.getOrdinalNumber()} place / ${widget.model.userCount}",
        color: AppColor.highlightOrange,
        fontWeight: FontWeight.w700,
        fontSize: 12.0);
  }

  String _getGameCount( GameTeamGroup gameTeamGroup,String suffix){
    return  gameTeamGroup == GameTeamGroup.squad ||  gameTeamGroup == GameTeamGroup.duo ?
    "${widget.model.userCount}/${widget.model.maxTeams} $suffix" :
    "${widget.model.userCount}/${widget.model.maxUsers} $suffix";
  }

  Widget _buildActionButton(
      int userCount, int maxUsers, DateTime? joinedAt, GameTeamGroup group) {

    if (_canPlayGame(joinedAt))
      return _playButton(context, group);

    if (widget.model.joinedAt != null)
      return _outlineButton(" " + AppStrings.joined, iconData: Icons.check_circle);


    final isJoinedByBefore =
        widget.model.joinBy?.isBefore(DateTime.now()) == true;
    final height  =  (group == GameTeamGroup.solo ? 38.0 : 45.0);

    if(isJoinedByBefore){
      return _outlineButton(AppStrings.registrationClosed, iconData:  Icons.report_gmailerrorred_rounded, height: height);
    }
    if(userCount == maxUsers){
      return _outlineButton(AppStrings.tournamentFull);
    }
    return _outlineButton(AppStrings.joinString(widget.model.fee), iconData:  Icons.add, height: height);
  }

  bool _canPlayGame(DateTime? joinedAt) {
    return joinedAt != null && widget.showPlayButtonOnCard;
  }

  _buildDuration() {
    if (widget.type == TournamentCardType.custom) {
      return [];
    }
    return [
      RegularText(
          "Duration: ${TimeUtils.instance.durationInDays(widget.model.startTime, widget.model.endTime)}",
          color: AppColor.grayTextB3B3B3,
          fontSize: 12.0,
          textAlign: TextAlign.start),
      const SizedBox(height: 4.0),
      if (_livePreJoinStates.contains(widget.state))
        TournamentCardTimer(widget.model.endTime, false)
      else
        const SizedBox(height: 0.0)
    ];
  }

  _buildCustomTournamentDuration() {
    if (widget.type == TournamentCardType.classic) return SizedBox.shrink();

    if (_preJoinedStates.contains(widget.state) &&
        DateTime.now().isBefore(widget.model.joinBy!))
      return TournamentCardTimer(widget.model.joinBy!, true);

    if (_upcomingLiveJoinedStates.contains(widget
        .state)) if (_isStartTimeInFuture() && widget.model.roomId == null) {
      return TournamentCardTimer(widget.model.startTime, true);
    }
    return const SizedBox(height: 0.0);
  }

  bool _isStartTimeInFuture() => widget.model.startTime.isAfter(DateTime.now());
}

enum TournamentCardType { custom, classic }

class TournamentCardModel {
  int userCount;
  int maxUsers;
  int maxTeams;
  DateTime? joinedAt;
  DateTime startTime;
  List<dynamic>? qualifiers;

  String? roomId;
  String? roomPassword;
  int? slot;
  int? rank;

  String allowedMaps;

  int fee;

  String name;

  DateTime? joinBy;

  List<Enum> allowedTiers;

  int maxPrize;

  String levelImageUrl;

  DateTime endTime;

  String allowedGameMode;

  TournamentCardModel({
    required this.name,
    required this.rank,
    required this.userCount,
    required this.maxTeams,
    required this.maxUsers,
    required this.joinedAt,
    required this.startTime,
    required this.endTime,
    required this.qualifiers,
    required this.allowedMaps,
    required this.allowedGameMode,
    required this.fee,
    required this.joinBy,
    required this.allowedTiers,
    required this.maxPrize,
    required this.levelImageUrl,
    required this.roomId,
    required this.roomPassword,
    required this.slot,
  });
}
