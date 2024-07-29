import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/bloc/application/application_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/feature/home/home_bloc.dart';
import 'package:gamerboard/feature/home/home_state.dart';
import 'package:gamerboard/feature/home/home_widgets.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/squad_invite_dialog.dart';
import 'package:gamerboard/feature/home/widgets/tournament_widget_helper.dart';
import 'package:gamerboard/feature/location/location_page_state.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/shared_preferences.dart';
import 'package:gamerboard/utils/ui_utils.dart';

import '../../common/widgets/containers.dart';
import '../../resources/colors.dart';
import 'widgets/dialogs/incomplete_team_dialog.dart';
import 'widgets/dialogs/invite_friends_dialog.dart';
import 'widgets/dialogs/low_balance_warning_popup.dart';
import 'widgets/dialogs/mobile_input_for_idp_dialog.dart';
import '../../common/widgets/qualification_criteria_not_found_dialog.dart';
import '../../common/widgets/qualifier_for_tournament_dialog.dart';
import 'widgets/dialogs/reward_dialog.dart';
import 'widgets/dialogs/select_game.dart';
import 'widgets/dialogs/user_prefrence_selection_dialog.dart';
import 'widgets/preference_selection_filter.dart';

////Created by saurabh.lahoti on 16/12/21

class HomePage extends StatefulWidget {
  final int initialIndex;

  HomePage(this.initialIndex);

  @override
  State<StatefulWidget> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage>
    with WidgetsBindingObserver, TickerProviderStateMixin {
  late HomeBloc _homeBloc;
  late HomeWidgets _homeWidgets;
  late TabController _tabController;
  static const int _numberOfTab = 6;
  bool _isLoading = false;
  final GlobalKey<ScaffoldState> scaffoldKey = new GlobalKey<ScaffoldState>();
  final FocusNode _buttonFocusNode = FocusNode(debugLabel: 'Menu Button');

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width;
    double height = MediaQuery.of(context).size.height;
    return appScaffold(
        scaffoldStateKey: scaffoldKey,
        endDrawer: PreferenceSelectionFilter(_homeBloc),
        body: BlocConsumer<HomeBloc, HomeState>(
            builder: (ctx, state) {
              if (state is UserDetailsLoaded) {
                return Padding(
                  padding: const EdgeInsets.all(5.0),
                  child: Row(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        cardContainer(_homeWidgets.getLeftPane(context, state),
                            width: width * 0.25, height: height - 10),
                        const SizedBox(width: 5.0),
                        cardContainer(
                            _homeWidgets.getRightSidePane(
                                context,
                                height - 10 - 2 - 50,
                                //height - topand bottom padding - dividers - tab bar
                                width - (width * 0.25 + 14 + 18 + 14),
                                _tabController, focusNode: _buttonFocusNode),
                            width: width - (width * 0.25 + 8 + 8 + 8))
                      ]),
                );
              } else if (state is HomePageLoaded)
                return appCircularProgressIndicator();
              return SizedBox.shrink();
            },
            listener: (ctx, state) async {
              await _handleStateChange(state, context);
            },
            bloc: _homeBloc,
            buildWhen: (previous, current) =>
                current is HomePageLoaded || current is UserDetailsLoaded));
  }

  Future<void> _handleStateChange(HomeState state, BuildContext context) async {
    switch (state.runtimeType) {
      case LowBalanceWarningState:
        await _handleLowBalanceState(context);
        break;

      case CustomTournamentQualifiedState:
        await _handleCustomQualifiedState(
            context, state as CustomTournamentQualifiedState);
        break;

      case NavigateToTabState:
        _tabController.animateTo((state as NavigateToTabState).page);
        break;

      case ShowEndDrawerForUserPreferenceState:
        scaffoldKey.currentState!.openEndDrawer();
        break;

      case ShowSquadInviteDialog:
        _handleSquadInviteDialogState(context, state as ShowSquadInviteDialog);
        break;

      case UserPreferenceSelectionDialogState:
        _handlePreferenceSelectionDialogState(context);
        break;

      case ShowRewardBonusDialog:
        await _handleRewardBonusDialogState(
            context, state as ShowRewardBonusDialog);
        break;

      case AlertTeamIsComplete:
        await _handleAlertTeamCompleteState(context);
        break;

      case QualifiedForTournament:
        await _handleQualifiedForTournamentState(
            state as QualifiedForTournament, context);
        break;

      case HomeLoading:
        _handleHomeLoading(state as HomeLoading, context);
        break;

      case ShowSoloJoinAlert:
        _handleSoloJoinAlertState(context, state as ShowSoloJoinAlert);
        break;

      case ShowInviteDialogAlert:
        TournamentWidgetHelper.showInviteOnlyDialog(
            context,
            (state as ShowInviteDialogAlert).tournament,
            _homeBloc,
            (state).phone,
            (state).pageType);
        break;

      case UserDetailsLoaded:
        // Handle UserDetailsLoaded state if needed
        break;

      case ShowInviteDialog:
        _handleShowInviteDialogState(context, state as ShowInviteDialog);
        break;

      case GameSelection:
        _loadPage(_tabController.index, false, true);
        break;

      case ShowGameSelectionDialog:
        await _handleShowGameSelectionDialog(context);
        break;

      case NavigateToLocationPrefs:
        await _handleNavigateToLocationPrefsState(
            context, state as NavigateToLocationPrefs);
        _homeBloc.willShowLocationPrefs = false;
        break;
    }
  }

  Future<void> _handleNavigateToLocationPrefsState(
      BuildContext context, NavigateToLocationPrefs state) async {
    await Navigator.pushNamed(context, Routes.LOCATION_PAGE);
    _homeBloc.checkAndShowPrefsDialog(state.identity);
  }

  Future<void> _handleShowGameSelectionDialog(BuildContext context) async {
    final width = MediaQuery.of(context).size.width / 1.5;
    final height = MediaQuery.of(context).size.height / 1.2;
    await UiUtils.getInstance.showCustomDialog(
        context,
        Container(
            width: width,
            height: height,
            child: SelectGame(() {
              Navigator.of(context).pop();
            }, _homeBloc)),
        dismissible: false);
  }

  void _handleShowInviteDialogState(
      BuildContext context, ShowInviteDialog state) {
    final width = MediaQuery.of(context).size.width / 1.5;
    final height = MediaQuery.of(context).size.height / 1.2;
    UiUtils.getInstance.showCustomDialog(
        context,
        Container(
            width: width,
            height: height,
            color: AppColor.dividerColor,
            child:
                InviteFriendDialog(_homeBloc.user!, state.inviteDialogEvent)),
        dismissible: true);
  }

  void _handleSoloJoinAlertState(
      BuildContext context, ShowSoloJoinAlert state) {
    UiUtils.getInstance.alertDialog(
        context,
        AppStrings.joiningConformation + "?",
        AppStrings.doYouWantToPay(state.tournament.tournament.fee),
        yesAction: () async {
      await _homeBloc.joinSoloTournament(
          context, state.tournament, state.pageType,
          phoneNumber: state.phone, joinCode: state.joinCode);
    });
  }

  void _handleHomeLoading(HomeLoading state, BuildContext context) {
    if (state.showLoader && !_isLoading) {
      _isLoading = true;
      UiUtils.getInstance.buildLoading(context);
    } else if (_isLoading) {
      _isLoading = false;
      Navigator.of(context).pop();
    }
  }

  Future<void> _handleQualifiedForTournamentState(
      QualifiedForTournament state, BuildContext context) async {
    if (state.qualificationResult.qualified) {
      await UiUtils.getInstance.showCustomDialog(
          context,
          QualifiedForTournamentDialog(
            state.tournament.tournament.qualifiers,
            state.ruleType,
            state.qualificationResult,
            tournamentName: state.tournamentName,
            onClickJoin: () {
              _homeBloc.joinTournament(
                  context, state.tournament, state.pageType);
            },
          ),
          dismissible: false);
    } else {
      await UiUtils.getInstance.showCustomDialog(
          context,
          QualificationCriteriaNotMatchDialog(
            state.tournament.tournament.qualifiers,
            state.ruleType,
            state.qualificationResult,
            tournamentName: state.tournamentName,
            onClickViewLeaderboard: () {
              _homeBloc.openTournament(
                  context, state.pageType, state.tournament);
            },
          ),
          dismissible: false);
    }
  }

  Future<void> _handleAlertTeamCompleteState(BuildContext context) async {
    await UiUtils.getInstance
        .showCustomDialog(context, InCompleteTeamDialog(), dismissible: false);
    _homeBloc.startService(context);
  }

  Future<void> _handleRewardBonusDialogState(
      BuildContext context, ShowRewardBonusDialog state) async {
    await UiUtils.getInstance.showCustomDialog(
        context, RewardDialog(state.rewardType),
        dismissible: false);
  }

  void _handlePreferenceSelectionDialogState(BuildContext context) {
    UiUtils.getInstance.showCustomDialog(
        context,
        Container(
            width: MediaQuery.of(context).size.width / 2.5,
            height: MediaQuery.of(context).size.height / 1,
            child: Center(
              child: UserPreferenceSelectionDialog(_homeBloc),
            )),
        dismissible: false);
  }

  void _handleSquadInviteDialogState(
      BuildContext context, ShowSquadInviteDialog state) {
    UiUtils.getInstance.showCustomDialog(
        context,
        SquadInviteDialog(state.userTournamentMixin, state.deeplinkData,
            _homeBloc, state.switchTeam));
  }

  Future<void> _handleCustomQualifiedState(
      BuildContext context, CustomTournamentQualifiedState state) async {
    await TournamentWidgetHelper.showMobileInputDialog(
        context, state.userTournamentMixin, _homeBloc, state.pageType);
  }

  Future<void> _handleLowBalanceState(BuildContext context) async {
    await UiUtils.getInstance.showCustomDialog(
        context,
        LowBalanceWarningPopup(
          onCloseClick: () {
            Navigator.of(context).pop();
          },
          onDepositClick: () async {
            Navigator.of(context).popAndPushNamed(Routes.WALLET);
            _homeBloc.deposit();
          },
        ));
    _homeBloc.closeLowBalanceWarning();
  }

  @override
  void dispose() {
    _homeBloc.stopTimer();
    _tabController.dispose();
    _buttonFocusNode.dispose();
    removeRoute(Routes.HOME_PAGE);
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    _homeBloc = context.read<HomeBloc>();
    _homeBloc.applicationBloc = context.read<ApplicationBloc>();
    _homeBloc.eSport = _homeBloc.applicationBloc.userCurrentGame;
    _homeWidgets = HomeWidgets(_homeBloc, context);
    _homeBloc.currentIndex = widget.initialIndex;
    _tabController = TabController(
        length: _numberOfTab, vsync: this, initialIndex: widget.initialIndex);
    _tabController.addListener(tabChangeListener);
    _homeBloc.currentIndex = widget.initialIndex;
    _homeBloc.getHistory();

    _loadPage(widget.initialIndex, true, false);
    _homeBloc.startTimer();
    _setUpGameState();
  }

  _setUpGameState() async {
    var currentGame = await SharedPreferenceHelper.getInstance
        .getStringPref(PrefKeys.SELECTED_GAMES);
    if (currentGame == ESports.freefiremax.getPackageName()) {
      Constants.PLATFORM_CHANNEL.invokeMethod("setup_current_game",
          {"game_name": ESports.freefiremax.getShortName()});

      _homeBloc.applicationBloc.userCurrentGame = ESports.freefiremax;
    } else {
      Constants.PLATFORM_CHANNEL.invokeMethod(
          "setup_current_game", {"game_name": ESports.bgmi.getShortName()});
      _homeBloc.applicationBloc.userCurrentGame = ESports.bgmi;
    }
  }

  void tabChangeListener() {
    if (!_tabController.indexIsChanging &&
        _tabController.previousIndex != _tabController.index) {
      _homeBloc.currentIndex = _tabController.index;
      // _homeBloc.emit(BgmiTierInputState(_homeBloc.bgmiGroupFromIndex));
      if (_tabController.index >= 2 && _tabController.index <= 4) {
        SharedPreferenceHelper.getInstance
            .setIntPref(PrefKeys.LAST_VISITED_GROUP_PAGE, _tabController.index);
      }
      _loadPage(_tabController.index, false, false);
      _homeBloc.loadGames();
      _homeBloc.getHistory();
    }
  }

  void _loadPage(
      int initialIndex, bool initialize, bool isComingFromGameChange) async {
    if (initialize) await _homeBloc.loadData(false);
    switch (initialIndex) {
      case HomeScreenIndex.HISTORY:
        return _homeBloc.getHistory();
      case HomeScreenIndex.TOP:
        return _homeBloc.getTopTournaments();
      case HomeScreenIndex.SOLO:
        return _homeBloc.getMyTournaments(
            initialIndex, GameTeamGroup.solo, isComingFromGameChange);
      case HomeScreenIndex.DUO:
        return _homeBloc.getMyTournaments(
            initialIndex, GameTeamGroup.duo, isComingFromGameChange);
      case HomeScreenIndex.SQUAD:
        return _homeBloc.getMyTournaments(
            initialIndex, GameTeamGroup.squad, isComingFromGameChange);
      case HomeScreenIndex.NEWS:
        return;
      case HomeScreenIndex.TOP_GAMERS:
        return _homeBloc.getAchievementSearchUsers(false);
    }
  }
}

class HomeScreenIndex {
  static const HISTORY = 0, TOP = 1, SQUAD = 2, SOLO = 3, DUO = 4, NEWS = 5,TOP_GAMERS = 6;
}

