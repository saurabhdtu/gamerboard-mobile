import 'dart:async';
import 'dart:convert';

import 'package:artemis/artemis.dart';
import 'package:flagsmith/flagsmith.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/exception/api_exception.dart';
import 'package:gamerboard/common/filter/list_filter.dart';
import 'package:gamerboard/common/models/device.dart';
import 'package:gamerboard/common/repository/mutation_repo.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/common/services/user/filters/tournament_filter_helper.dart';
import 'package:gamerboard/common/services/user/filters/upcoming_custom_tournament_filter.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/common/services/user/user_service.dart';
import 'package:gamerboard/feature/bgservice/capture_service.dart';
import 'package:gamerboard/feature/home/home_page.dart';
import 'package:gamerboard/feature/home/home_state.dart';
import 'package:gamerboard/feature/home/model/tournament_sort_order.dart';
import 'package:gamerboard/feature/home/model/tournament_sort_order_feature.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/low_balance_popup_scheduler.dart';
import 'package:gamerboard/utils/network_utils.dart';
import 'package:gamerboard/utils/share_utils.dart';
import 'package:gamerboard/utils/shared_preferences.dart';
import 'package:gamerboard/utils/time_utils.dart';
import 'package:gamerboard/utils/ui_utils.dart';
import 'package:gamerboard/utils/validators.dart';
import 'package:gamerboard/utils/wallet_extension.dart';

import '../../common/bloc/application/application_bloc.dart';
import '../../common/models/deeplink_data.dart';
import '../../resources/colors.dart';
import '../../utils/flagsmith_utils.dart';
import '../../utils/invite_utils.dart';
import '../../utils/reward_utils.dart';
import '../history/history_entities.dart';
import 'widgets/dialogs/squad_helper_guide_dialog.dart';

////Created by saurabh.lahoti on 06/08/21

class HomeBloc extends Cubit<HomeState> {
  UserService userService;

  late ESports eSport;
  Future<GraphQLResponse<dynamic>>? _apiCall;
  UserMixin? user;
  late int currentIndex;
  static bool isOnboardingTutorialDismissed = false;
  GetTournamentsHistory$Query? historyResponse;
  GetTopTournaments$Query? topTournamentResponse;
  bool inviteStatusChecked = false;
  var gameBonusEnable = false;
  var showPlayButtonOnCard = false;
  var customRoomEnable = false;
  var ffmaxEnable = false;
  var isPlayedInSquad = false;
  RolePreference searchUserRole = RolePreference.artemisUnknown;
  TimeOfDayPreference searchTimeOfDay = TimeOfDayPreference.artemisUnknown;
  TimeOfWeekPreference searchDayOfWeek = TimeOfWeekPreference.artemisUnknown;
  bool isWhatsappConnectInAchievementEnable = false;
  String? searchPreferenceKdValueCustom = "1.0+";

  String? searchPreferenceKdValueClassic = "1.0+";
  var enableQualifierTournaments = false;
  List<GetUserListByPreference$Query$User?> preferenceByUser = [];
  var _enableQualifierTournaments = false;
  List<ListFilter<UserTournament>> _tournamentFilter = [];

  bool isFlagSmithChecked = false;
  String isRewardDoubleEnabled = "true";
  List<ActiveTournamentList>? activeTournamentList;

  Timer? _timer;
  DeeplinkData? deeplinkData;
  MapEntry<Enum, String>? selectedTier;
  var isAnyGamePlayed = false;
  var isAnyTournamentJoin = false;
  var noGamesPlayed = 0;

  var isOnboardingSteeperCompleted = false;
  var isNewUser = false;
  var willShowLocationPrefs = false;
  late ApplicationBloc applicationBloc;
  String tournamentSortOrderTypeFlag = TournamentSortOrderFeature.classic;

  HomeBloc({required this.userService, this.deeplinkData})
      : super(HomePageLoaded()) {
    _getDeviceInfo();
    Constants.LOCAL_CHANNEL.setMethodCallHandler((call) {
      _handleLocalChannelMethodCall(call);
      return Future.value(1);
    });
  }

  void _handleLocalChannelMethodCall(MethodCall call) {
    switch (call.method) {
      case "launch_game":
        final package = call.arguments['package'];
        final appRestarted = call.arguments['app_restarted'] ?? false;
        startService(null, packageName: package, appRestarted: appRestarted);
        break;
      case "refresh_page":
        debugPrint("reached home bloc");
        refreshPage();
        break;
    }
  }

  launchGame(
      context, GameTeamGroup group, UserTournamentMixin tournamentMixin) {
    if (group == teamGroupForIndex()) {
      if (group == GameTeamGroup.squad && !isPlayedInSquad) {
        final width = MediaQuery.of(context).size.width / 1.5;
        final height = MediaQuery.of(context).size.height / 1.2;
        UiUtils.getInstance.showCustomDialog(
            context,
            Container(
                width: width,
                height: height,
                color: AppColor.dividerColor,
                child: SquadHelperGuide(width, this, tournamentMixin, () {
                  if (group == teamGroupForIndex()) {
                    startService(context);
                  } else {
                    showTeamAlert();
                  }
                })),
            dismissible: true);
      } else {
        startService(context);
      }
    } else
      showTeamAlert();
  }

  void _getDeviceInfo() async {
    final String? info = await SharedPreferenceHelper.getInstance
        .getStringPref(PrefKeys.KEY_DEVICE_INFO);
    if (buildConfig == null && info != null) {
      buildConfig = BuildConfig.fromMap(jsonDecode(info));
    }
  }

  Future<bool> submitUserPreference(
      BuildContext context, PreferencesInput preferencesInput) async {
    _emitState(HomeLoading(true));
    var postSubmitUserPreference;

    postSubmitUserPreference =
    await userService.savePreferences(preferencesInput);
    _emitState(HomeLoading(false));
    if (postSubmitUserPreference.hasErrors) {
      UiUtils.getInstance
          .showToast(postSubmitUserPreference.errors!.first.message);
      return false;
    } else {
      UiUtils.getInstance.showToast("Preference submitted");
      AnalyticService.getInstance()
          .trackEvents(Events.PREFERENCE_SUBMITTED, properties: {
        "id": user!.id,
      });
      return true;
    }
  }

  getAchievementSearchUsers(bool isComingFromSearch) async {
    if (preferenceByUser != null) _emitState(TournamentLoading(true, null));

    PreferencesInput preferencesInput = PreferencesInput(
      playingReason: null,
      roles:( isComingFromSearch  && searchUserRole != RolePreference.artemisUnknown)?  [searchUserRole] : null,
      timeOfDay: ( isComingFromSearch  && searchTimeOfDay != TimeOfDayPreference.artemisUnknown) ? [searchTimeOfDay] : null,
      timeOfWeek: ( isComingFromSearch  && searchDayOfWeek != TimeOfWeekPreference.artemisUnknown)  ? searchDayOfWeek : null,
    );
    var isClearFilter = (searchUserRole == RolePreference.artemisUnknown &&
        searchDayOfWeek == TimeOfWeekPreference.artemisUnknown &&
        searchTimeOfDay == TimeOfDayPreference.artemisUnknown
    );
    if (user?.preferences != null &&  (!isComingFromSearch || isClearFilter)) {
      preferencesInput = PreferencesInput(
        playingReason: user?.preferences?.playingReason,
        roles: user?.preferences?.roles,
        timeOfDay: user?.preferences?.timeOfDay,
        timeOfWeek: user?.preferences?.timeOfWeek,
      );
    }
    var userPreference =
    await userService.getAchievementSearchUsers(preferencesInput);
    if (userPreference.hasErrors) {
      _emitState(TournamentLoading(false, null));
      UiUtils.getInstance.showToast(userPreference.errors!.first.message);
    } else {
      if (userPreference.data?.searchByPreferences != null) {
        preferenceByUser = userPreference.data!.searchByPreferences;
        _emitState(UserPreferenceListState(preferenceByUser));
      }
    }
  }

  Future<void> loadData(bool force) async {
    var response = await userService.getMyProfile(getCached: !force);
    if (!response.hasErrors) {
      user = response.data!.me;
      if (!isFlagSmithChecked) {
        await setAllFlagSmithVariant();
        isFlagSmithChecked = true;
      }
      await _checkUserPreference();
      _emitState(UserDetailsLoaded(user!));
      _checkForLowBalance(user?.wallet);
    } else {
      UiUtils.getInstance.showToast(response.errors!.first.message);
    }
    return Future.value();
  }

  _checkUserPreference() async {
    if (user?.id == null) return;
    var identity = Identity(identifier: user!.id.toString());
    checkAndShowPrefsDialog(identity);
  }

  checkAndShowPrefsDialog(Identity identity) async {
    var isNewUserFirstSession = await SharedPreferenceHelper.getInstance
        .getBoolPref(PrefKeys.IS_NEW_USER_FIRST_SESSION);
    if ((isNewUserFirstSession ?? false)) return;

    var isPreferenceDialogEnable = await getFlagSmithFlag(
        FlagKeys.ENABLE_USER_PREFERENCE_DIALOG,
        identity,
        applicationBloc.flagsmithClient);
    if (!isPreferenceDialogEnable) return;

    if (user?.preferences?.roles != null) return;

    var alreadyShowed = await getFlagSmithTraitBoolValue(
        TraitKeys.SHOWED_USER_PREFERENCE_DIALOG,
        identity,
        applicationBloc.flagsmithClient);
    if (alreadyShowed) return;

    _emitState(UserPreferenceSelectionDialogState(true));
    setFlagSmithTraitBoolValue(TraitKeys.SHOWED_USER_PREFERENCE_DIALOG,
        identity, applicationBloc.flagsmithClient, "true");
  }

  setAllFlagSmithVariant() async {
    var identity = Identity(identifier: user!.id.toString());
    var isConnectedToInternet = await NetworkUtils.isInternetConnected();
    if (!isConnectedToInternet) {
      UiUtils.getInstance
          .showDebugToast(ErrorMessages.ERROR_INTERNET_CONNECTIVITY);
      return;
    }
    try {
      await setFeatureFlag(identity, applicationBloc.flagsmithClient);
    } on FlagsmithApiException catch (ex) {
      UiUtils.getInstance.showDebugToast(
          ex.message ?? ErrorMessages.ERROR_INTERNET_CONNECTIVITY);
      return;
    }

    noGamesPlayed = await loadGames();
    if (noGamesPlayed == 0) {
      noGamesPlayed = await getFlagSmithTraitIntValue(
          TraitKeys.GAMES, identity, applicationBloc.flagsmithClient);
    }

    isAnyGamePlayed = noGamesPlayed > 0;
    if (isAnyGamePlayed) {
      isAnyTournamentJoin = true;
    }

    var isOnboardingStepperEnable = await getFlagSmithFlag(
        FlagKeys.ONBOARDING_STEPPER_VISIBLE,
        identity,
        applicationBloc.flagsmithClient);

    if (isOnboardingStepperEnable) {
      isOnboardingSteeperCompleted = (await getFlagSmithTraitBoolValue(
          TraitKeys.ONBOARDING_STEPS_VISIBLE, identity, applicationBloc.flagsmithClient));
    } else {
      isOnboardingSteeperCompleted = !isOnboardingStepperEnable;
    }

    isNewUser = (await getFlagSmithTraitBoolValue(
        TraitKeys.IS_NEW_USER, identity, applicationBloc.flagsmithClient));

    customRoomEnable = await getFlagSmithFlag(
        FlagKeys.CUSTOM_ROOM_ENABLE, identity, applicationBloc.flagsmithClient);

    showPlayButtonOnCard = await getFlagSmithFlag(
        FlagKeys.SHOW_PLAY_BUTTON_ON_CARD,
        identity,
        applicationBloc.flagsmithClient);
    _enableQualifierTournaments = await getFlagSmithFlag(
        FlagKeys.ENABLE_QUALIFIER_TOURNAMENT,
        identity,
        applicationBloc.flagsmithClient);
    ffmaxEnable = await getFlagSmithFlag(
        FlagKeys.FFMAX_ENABLE, identity, applicationBloc.flagsmithClient);
    var showOnlyUserVerification = await getFlagSmithFlag(
        FlagKeys.SHOW_ONLY_PROFILE_VERIFICATION,
        identity,
        applicationBloc.flagsmithClient);
    var gameIdVerification = await getFlagSmithFlag(
        FlagKeys.GAME_ID_VERIFICATION,
        identity,
        applicationBloc.flagsmithClient);

    var newLogingFlag = await getFlagSmithFlag(
        FlagKeys.NEW_LOGING_MODE, identity, applicationBloc.flagsmithClient);

    var profileNudge = await getFlagSmithFlag(
        FlagKeys.PROFILE_NUDGE, identity, applicationBloc.flagsmithClient);

    String? killAlgoFlag = await getFlagSmithFlagString(
        FlagKeys.KILL_ALGO, identity, applicationBloc.flagsmithClient);

    await _setTournamentOrderFeatureFlag(identity);

    Constants.PLATFORM_CHANNEL.invokeMethod("feature_flags", {
      "ffmax_flag": ffmaxEnable,
      "show_verification_only": showOnlyUserVerification,
      "profile_nudge": profileNudge,
      "new_loging_mode": newLogingFlag,
      "game_id_verification": gameIdVerification,
      "kill_algo": killAlgoFlag ?? "baseline"
    });

    SharedPreferenceHelper.getInstance
        .setBoolPref(PrefKeys.IS_FFMAX_ENABLE, ffmaxEnable);
    var showNewUserDialog = await SharedPreferenceHelper.getInstance
        .getBoolPref(PrefKeys.BOOL_NEW_REGISTERED_CHECK) ??
        false;
    if (ffmaxEnable & showNewUserDialog) {
      SharedPreferenceHelper.getInstance
          .setBoolPref(PrefKeys.BOOL_NEW_REGISTERED_CHECK, false);
      _emitState(ShowGameSelectionDialog(true));
    }
  }

  Future<int> loadGames() async {
    var data =
    await Constants.PLATFORM_CHANNEL.invokeMethod("get_game_history");
    Iterable l = jsonDecode(data);
    return List<GameHistory>.from(l.map((e) => GameHistory.fromMap(e))).length;
  }

  get isFlaggedDevice {
    return buildConfig?.deviceManufacturer == 'xiaomi' ||
        buildConfig?.deviceManufacturer == 'vivo' ||
        buildConfig?.deviceManufacturer == 'realme' ||
        buildConfig?.deviceManufacturer == 'oneplus' ||
        buildConfig?.deviceManufacturer == 'oppo';
  }

  void startService(BuildContext? context, {String? packageName,  bool appRestarted = false}) async {
    if (user == null) {
      var response =
      await QueryRepository.instance.getMyProfile(getCached: true);
      if (!response.hasErrors) {
        user = response.data!.me;
      }
    }
    String? profileId;
    String? profileUserName;
    String? gamePackage;
    final String? auth = await SharedPreferenceHelper.getInstance
        .getStringPref(PrefKeys.USER_AUTH);
    if (auth != null) {
      if (user?.profiles?.isNotEmpty ?? false) {
        profileId = user!.getCurrentGameProfile(eSport)!.profileId;
        profileUserName = user!.getCurrentGameProfile(eSport)!.username;
        gamePackage = eSport.getPackageName();
      }
      if (context != null) UiUtils.getInstance.buildLoading(context);
      bool additionalPermissions = user?.hasPlayedAnyGame(eSport) ?? false;
      if (context != null) Navigator.of(context).pop();
      debugPrint(
          "uid:${user?.id} ;profileName: $profileUserName; profileId: $profileId");
      UiUtils.getInstance.showDebugToast(
          "uid:${user?.id} ;profileName: $profileUserName; profileId: $profileId");
      BackgroundServicePlugin.startService(auth, additionalPermissions,
          profileId: profileId,
          profileName: profileUserName,
          appRestarted: appRestarted,
          gamePackage: gamePackage);
    }
  }

  void showFeedback() {
    if (kDebugMode) Constants.PLATFORM_CHANNEL.invokeMethod("feedback");
  }

  void showTeamAlert() {
    _emitState(AlertTeamIsComplete());
  }

  Future<dynamic> launchVideoTutorial(
      BuildContext context, String videoUrl, String title) {
    return Navigator.of(context).pushNamed(Routes.VIDEO_PLAYER,
        arguments: {"video_url": videoUrl, 'title': title});
  }

  void _setOrderByFlagAndGroup(GameTeamGroup group, bool isNewUser) {
    if (!isNewUser) return;

    var order = TournamentSortOrderFeature.getSortOrderByFeature(
        tournamentSortOrderTypeFlag, group);
    setSortOrder(order);
    return;
  }

  Future<void> _setTournamentOrderFeatureFlag(Identity identity) async {
    tournamentSortOrderTypeFlag = (await getFlagSmithFlagString(
        FlagKeys.TOURNAMENT_ORDER,
        identity,
        applicationBloc.flagsmithClient)) ??
        TournamentSortOrderFeature.classic;
  }

  void getMyTournaments(int initialIndex, GameTeamGroup group,
      bool isComingFromGameSelection) async {
    {
      if (isComingFromGameSelection) _emitState(TournamentLoading(true, group));

      _setOrderByFlagAndGroup(group, isNewUser);
      _emitActiveTournamentDataEvent(group);

      if (user?.profiles != null &&
          user?.getGameLevelFromMetaData(
              group,
              user!.getCurrentGameProfile(
                  applicationBloc.userCurrentGame)) !=
              null) {
        switch (group) {
          case GameTeamGroup.solo:
          case GameTeamGroup.duo:
          case GameTeamGroup.squad:
            if (activeTournamentList?.isEmpty != false)
              _emitState(TournamentLoading(true, group));
            break;
          default:
            break;
        }

        try {

          activeTournamentList =
          await userService.getActiveTournaments(eSport, _tournamentFilter);
          _emitActiveTournamentDataEvent(group);
        } on ApiException catch (e) {
          _emitState(TournamentLoading(false, group));
          UiUtils.getInstance.showToast(e.message);
        }
      } else {
        _emitActiveTournamentDataEvent(group);
      }
    }
    handleDeeplink();
  }

  void getTopTournaments() async {
    var identity = Identity(identifier: user!.id.toString());
    _enableQualifierTournaments = await getFlagSmithFlag(
        FlagKeys.ENABLE_QUALIFIER_TOURNAMENT,
        identity,
        applicationBloc.flagsmithClient);
    if (topTournamentResponse == null)
      _emitState(TournamentLoading(true, null));
    _apiCall?.ignore();
    var call = QueryRepository.instance.getTopTournaments(eSport);
    _apiCall = call;
    var topTournaments = await call;
    if (topTournaments.hasErrors) {
      _emitState(TournamentLoading(false, null));
      UiUtils.getInstance.showToast(topTournaments.errors!.first.message);
    } else {
      topTournamentResponse = topTournaments.data;

      if (_enableQualifierTournaments) {
        _emitState(TopTournamentsLoaded(topTournaments.data!.top));
      } else {
        topTournamentResponse!.top = topTournamentResponse!.top
            .where((element) => element.tournament.qualifiers == null)
            .toList();
        _emitState(TopTournamentsLoaded(topTournaments.data!.top));
      } //add leaderboards
    }
  }

  Future<void> joinTournament(BuildContext context,
      UserTournamentMixin userTournamentMixin, int pageType,
      {String? phoneNumber, String? inviteCode, String? joinCode}) async {
    if (userTournamentMixin.tournament.matchType == MatchType.headToHead &&
        phoneNumber == null) {
      _emitState(CustomTournamentQualifiedState(userTournamentMixin, pageType));
      return;
    }
    if (userTournamentMixin.tournament.joinCode != null &&
        joinCode == null &&
        userTournamentMixin.tournamentGroup() == GameTeamGroup.solo) {
      _emitState(ShowInviteDialogAlert(userTournamentMixin, pageType,
          phone: phoneNumber));
      return;
    }

    if (userTournamentMixin.tournamentGroup() != GameTeamGroup.solo) {
      var result = await Navigator.of(context)
          .pushNamed(Routes.TEAM_JOIN_CREATE, arguments: {
        "tournament": userTournamentMixin,
        "phoneNumber": phoneNumber,
        "joinCode": joinCode,
        "fromTournament": false
      });
      if (result == true) {
        refreshPage();
        openTournament(context, pageType, userTournamentMixin);
      }
    } else {
      if (userTournamentMixin.tournament.fee > 0) {
        if (userTournamentMixin.tournament.matchType == MatchType.headToHead) {
          if (phoneNumber != null)
            _emitState(ShowSoloJoinAlert(userTournamentMixin, pageType,
                phone: phoneNumber, joinCode: joinCode));
          else
            _emitState(
                CustomTournamentQualifiedState(userTournamentMixin, pageType));
        } else {
          _emitState(ShowSoloJoinAlert(userTournamentMixin, pageType,
              phone: phoneNumber, joinCode: joinCode));
        }
      } else {
        await joinSoloTournament(context, userTournamentMixin, pageType,
            phoneNumber: phoneNumber, joinCode: joinCode);
      }
    }
  }

  void checkUserQualification(BuildContext context,
      UserTournamentMixin tournament, int pageType) async {
    _emitState(HomeLoading(true));
    var checkQualificationResponse = await QueryRepository.instance
        .checkUserTournamentQualification(tournament.tournament.id);
    _emitState(HomeLoading(false));
    if (checkQualificationResponse.hasErrors) {
      UiUtils.getInstance
          .showToast(checkQualificationResponse.errors!.first.message);
    } else {
      if (tournament.tournament.qualifiers!.first.rule ==
          CustomQualificationRuleTypes.rankByTournament) {
        var targetTournament = await getTournamentId(
            tournament.tournament.qualifiers!.first.value["tournamentId"]);
        _emitState(QualifiedForTournament(
            getQualificationRule(checkQualificationResponse
                .data!.getTournamentQualification!.rules.first.rule),
            checkQualificationResponse.data!.getTournamentQualification!,
            tournament,
            pageType,
            tournamentName: targetTournament!.tournament.name));
      } else {
        _emitState(QualifiedForTournament(
          getQualificationRule(checkQualificationResponse
              .data!.getTournamentQualification!.rules.first.rule),
          checkQualificationResponse.data!.getTournamentQualification!,
          tournament,
          pageType,
        ));
      }
    }
  }

  FutureOr<UserTournamentMixin?> getTournamentId(int tournamentId) async {
    _emitState(HomeLoading(true));
    var getTournament =
    await QueryRepository.instance.getTournament(tournamentId);
    _emitState(HomeLoading(false));
    if (getTournament.hasErrors) {
      UiUtils.getInstance.showToast(getTournament.errors!.first.message);
    } else {
      return getTournament.data!.tournament!;
    }
    return null;
  }

  Future<void> joinSoloTournament(BuildContext context,
      UserTournamentMixin userTournamentMixin, int pageType,
      {String? phoneNumber, String? joinCode}) async {
    _emitState(HomeLoading(true));
    var tournamentResponse;

    if (phoneNumber != null) {
      tournamentResponse = await MutationRepository.instance.enterTournament(
          userTournamentMixin.tournament.id,
          phoneNumber: phoneNumber,
          joinCode: joinCode);
    } else {
      tournamentResponse = await MutationRepository.instance.enterTournament(
          userTournamentMixin.tournament.id,
          joinCode: joinCode);
    }

    _emitState(HomeLoading(false));
    if (tournamentResponse.hasErrors) {
      UiUtils.getInstance.showToast(tournamentResponse.errors!.first.message);
    } else {
      UiUtils.getInstance.showToast("Joined tournament");
      AnalyticService.getInstance().trackEvents(Events.LB_JOINED, properties: {
        "id": userTournamentMixin.tournament.id,
        "from": pageType == HomeScreenIndex.TOP
            ? "home_page_top"
            : "home_page_active",
        "tournament_type":
        userTournamentMixin.tournament.matchType.getMatchTypeName(),
        "fee": userTournamentMixin.tournament.fee,
        "group": userTournamentMixin.tournamentGroup().name(),
        "duration": TimeUtils.instance.durationInDays(
            userTournamentMixin.tournament.startTime,
            userTournamentMixin.tournament.endTime)
      });
      if (pageType == HomeScreenIndex.TOP) {
        _apiCall?.ignore();
        var call = QueryRepository.instance.getTopTournaments(eSport);
        _apiCall = call;
        var topTournaments = await call;
        if (!topTournaments.hasErrors) {
          topTournamentResponse = topTournaments.data;
          _emitState(TopTournamentsLoaded(
              topTournaments.data!.top)); //add leaderboards
        }
      } else {
        try {
          activeTournamentList =
          await userService.getActiveTournaments(eSport, []);
          loadDataOrShowLoader(currentIndex);
        } on ApiException catch (_) {}
      }
    }
  }

  void _emitActiveTournamentDataEvent(GameTeamGroup group) async {
    if (user?.getGameLevelFromMetaData(group,
        user!.getCurrentGameProfile(applicationBloc.userCurrentGame)) !=
        null) {
      if (activeTournamentList != null) {
        _setOrderByFlagAndGroup(group, isNewUser);

        List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament>?
        tournaments = TournamentFilterHelper.filterTournamentByGroup(
            activeTournamentList ?? [], group,
            includeCustom: customRoomEnable);

        TournamentFilterHelper.applyFilterOnList(
            tournaments, _tournamentFilter);

        if (!(user?.hasPlayedAnyGame(applicationBloc.userCurrentGame) ??
            false) &&
            !isOnboardingTutorialDismissed) {
          if (tournaments.isNotEmpty && tournaments.length > 1) {
            final list = tournaments.where((element) {
              if (element.tournament.rules
              is TournamentMixin$TournamentRules$BGMIRules) {
                return element.tournament.userCount <
                    element.tournament.rules.maxUsers;
              } else if (element.tournament.rules
              is TournamentMixin$TournamentRules$FFMaxRules) {
                return element.tournament.userCount <
                    element.tournament.rules.maxUsers;
              }
              return false;
            });
            if (list.isNotEmpty) tournaments = [list.first];
          }
          tournaments.add(OnboardingEducationCard());
        }

        _emitState(MyTournamentLoaded(tournaments, group));
      }
    } else {
      _emitState(GameTierInputState(group));
    }
  }

  void openTournament(BuildContext context, int pageType,
      UserTournamentMixin userTournamentMixin,
      {bool showTeamDetail = false}) async {
    AnalyticService.getInstance().trackEvents(Events.LB_CLICKED, properties: {
      "group": userTournamentMixin.tournament.tournamentGroup().toString(),
      "fee": userTournamentMixin.tournament.fee,
      "tournament_type":
      userTournamentMixin.tournament.matchType.getMatchTypeName(),
      "from": pageType == HomeScreenIndex.HISTORY
          ? "history"
          : (pageType == HomeScreenIndex.TOP ? "top" : "my_eactive")
    });
    await Navigator.of(context).pushNamed(Routes.TOURNAMENT_PAGE,
        arguments: TournamentPayload(userTournamentMixin.tournament.id,
            showTeamDetail: showTeamDetail));
    /*if (result == true) */
    refreshPage();
  }

  void refreshPage() async {
    await loadData(true);
    switch (currentIndex) {
      case HomeScreenIndex.TOP:
        getTopTournaments();
        break;
      case HomeScreenIndex.SOLO:
        getMyTournaments(currentIndex, GameTeamGroup.solo, false);
        break;
      case HomeScreenIndex.DUO:
        getMyTournaments(currentIndex, GameTeamGroup.duo, false);
        break;
      case HomeScreenIndex.SQUAD:
        getMyTournaments(currentIndex, GameTeamGroup.squad, false);
        break;
    }
  }

  void getHistory() async {
    if (historyResponse == null) _emitState(TournamentLoading(true, null));
    _apiCall?.ignore();
    var call = QueryRepository.instance.getTournamentHistory(eSport);
    _apiCall = call;
    var history = await call;
    if (history.hasErrors) {
      _emitState(TournamentLoading(false, null));
      UiUtils.getInstance.showToast(history.errors!.first.message);
    } else {
      historyResponse = history.data;
      if (historyResponse != null && historyResponse!.history.isNotEmpty) {
        isAnyTournamentJoin = true;
        if (noGamesPlayed == 0) {
          var gamePlayed = historyResponse!.history
              .where((element) => element.score! > 0)
              .toList();
          isAnyGamePlayed = gamePlayed.isNotEmpty;
          noGamesPlayed = gamePlayed.length;
        }
        if (!isPlayedInSquad) {
          for (var tournament in historyResponse!.history) {
            if (tournament.tournamentGroup() == GameTeamGroup.squad &&
                tournament.squad != null) {
              var isPlayAnyTournament = await checkSquadGamePlayStatus(
                  tournament.tournament, tournament.squad!);
              if (isPlayAnyTournament) break;
            }
          }
        }
      }

      await _validateAndShowInviteDialog();

      _emitState(
          TopTournamentsLoaded(history.data!.history)); //add leaderboards
    }
  }

  Future<void> _validateAndShowInviteDialog() async {
    if (willShowLocationPrefs) return;

    var inviteStateEvent = await InviteUtils.checkInviteDialogStatus(
        applicationBloc.flagsmithClient,
        user: user,
        noGamesPlayed: noGamesPlayed);

    if (inviteStateEvent != null && !inviteStatusChecked) {
      inviteStatusChecked = true;
      _emitState(ShowInviteDialog(inviteStateEvent));
    }
  }

  Future<bool> checkSquadGamePlayStatus(
      UserTournamentMixin$Tournament tournament,
      UserTournamentMixin$Squad squad) async {
    var leaderBoard = await QueryRepository.instance.getLeaderboard(
        tournament.id, LeaderboardDirection.next, tournament.tournamentGroup(),
        userOrTeamId: squad!.id, pageSize: 1);

    if (leaderBoard.data == null) return false;
    if (leaderBoard.data!.length > 0 &&
        leaderBoard.data!.first!.matchesPlayed! > 0) {
      isPlayedInSquad = true;
      return true;
    }
    return false;
  }

  void loadDataOrShowLoader(int screen) {
    selectedTier = null;
    switch (screen) {
      case HomeScreenIndex.SOLO:
        return _emitActiveTournamentDataEvent(GameTeamGroup.solo);

      case HomeScreenIndex.DUO:
        return _emitActiveTournamentDataEvent(GameTeamGroup.duo);

      case HomeScreenIndex.SQUAD:
        return _emitActiveTournamentDataEvent(GameTeamGroup.squad);

      case HomeScreenIndex.HISTORY:
        historyResponse == null
            ? _emitState(TournamentLoading(true, null))
            : _emitState(TopTournamentsLoaded(historyResponse!.history));
        break;

      case HomeScreenIndex.TOP:
        topTournamentResponse == null
            ? _emitState(TournamentLoading(true, null))
            : _emitState(TopTournamentsLoaded(topTournamentResponse!.top));
        break;
    }
  }

  void startTimer() {
    _timer = Timer.periodic(Duration(seconds: 1), (timer) {
      _emitState(Tick());
    });
  }

  void stopTimer() {
    _timer?.cancel();
  }

  void navigateToTab(GameTeamGroup mode) {
    int page = -1;
    switch (mode) {
      case GameTeamGroup.duo:
        page = 4;
        break;
      case GameTeamGroup.solo:
        page = 3;
        break;
      case GameTeamGroup.squad:
        page = 2;
        break;
      default:
        break;
    }
    if (page != -1) _emitState(NavigateToTabState(page));
  }

  GameTeamGroup teamGroupForIndex() {
    switch (currentIndex) {
      case 2:
        return GameTeamGroup.squad;
      case 3:
        return GameTeamGroup.solo;
      case 4:
        return GameTeamGroup.duo;
    }
    return GameTeamGroup.squad;
  }

  void addGameLevel(Map<String, dynamic> levelGroupMap) async {
    _emitState(UpdatingBgmiLevel(true));
    var res;
    bool editMode;
    try {
      if (user!.getCurrentGameProfile(applicationBloc.userCurrentGame) !=
          null) {
        editMode = true;
        res = await MutationRepository.instance
            .updateGameProfile(levelGroupMap, applicationBloc.userCurrentGame);
      } else {
        editMode = false;
        res = await MutationRepository.instance
            .createGameProfile(levelGroupMap, applicationBloc.userCurrentGame);
      }
      if (res.hasErrors) {
        UiUtils.getInstance.showToast(res.errors!.first.message);
        AnalyticService.getInstance()
            .trackEvents(Events.GAME_TIER_ERROR, properties: {
          "edit_mode": editMode,
          "group": levelGroupMap["group"].toString(),
          "tier": levelGroupMap["level"].toString(),
          "error": res.errors!.first.message
        });
      } else {
        refreshPage();
        AnalyticService.getInstance()
            .trackEvents(Events.GAME_TIER_SUBMITTED, properties: {
          "edit_mode": editMode,
          "group": levelGroupMap["group"].toString(),
          "tier": levelGroupMap["level"].toString(),
        });
      }
      _emitState(UpdatingBgmiLevel(false));
    } catch (ex) {
      _emitState(UpdatingBgmiLevel(false));
    }
  }

  void handleDeeplink() async {
    if (deeplinkData != null &&
        deeplinkData?.deeplinkMode == DeeplinkMode.SQUAD_INVITE &&
        deeplinkData?.tournamentId != null &&
        deeplinkData?.inviteCode != null) {
      final tournament = await QueryRepository.instance
          .getTournament(deeplinkData!.tournamentId!);
      if (tournament.data != null && tournament.data?.tournament != null) {
        if (tournament.data?.tournament?.squad == null) {
          if (user?.getGameLevelFromMetaData(
              tournament.data!.tournament!.tournamentGroup(),
              user?.getCurrentGameProfile(
                  applicationBloc.userCurrentGame)) ==
              null) {
            UiUtils.getInstance.showToast(AppStrings.errorTier,
                toastLength: Toast.LENGTH_LONG);
          } else {
            final error = canJoinTournament(tournament.data!.tournament!, user,
                applicationBloc.userCurrentGame, true);
            if (error == null) {
              _emitState(ShowSquadInviteDialog(
                  tournament.data!.tournament!, deeplinkData!, false));
            } else if (tournament.data?.tournament?.tournament.userCount ==
                tournament.data?.tournament?.tournament.rules.maxUsers) {
              UiUtils.getInstance.showToast("Max players joined");
            } else {
              UiUtils.getInstance.showToast(error);
            }
          }
        } else {
          if (tournament.data?.tournament?.squad?.id == deeplinkData?.teamId) {
            UiUtils.getInstance.showToast(AppStrings.errorTeamThis);
            deeplinkData = null;
          } else {
            _emitState(ShowSquadInviteDialog(
                tournament.data!.tournament!, deeplinkData!, true));
          }
        }
      } else {
        UiUtils.getInstance.showToast(AppStrings.tournamentNotAvailable);
      }
    }
  }

  Future<bool> joinSquad(
      BuildContext context, UserTournamentMixin tournament, bool teamSwitch,
      {String? inviteCode, int? teamId}) async {
    _emitState(HomeLoading(true));
    final response;

    if (teamSwitch) {
      response = await MutationRepository.instance
          .changeSquad(teamId!, tournament.tournament.id);
      if (tournament.tournament.fee == 0)
        await MutationRepository.instance
            .enterTournament(tournament.tournament.id);
    } else if (tournament.tournament.fee == 0) {
      response = await MutationRepository.instance.enterTournament(
          tournament.tournament.id,
          squadInfo: TournamentJoiningSquadInfo(inviteCode: inviteCode!));
    } else {
      _emitState(HomeLoading(false));
      await navigateToCreateTeam(context, tournament, false, true,
          inviteCode: inviteCode);
      response = null;
    }

    AnalyticService.getInstance().trackEvents(Events.LB_JOINED, properties: {
      "id": tournament.tournament.id,
      "from": currentIndex == HomeScreenIndex.TOP
          ? "home_page_top"
          : "home_page_active",
      "tournament_type": tournament.tournament.matchType.getMatchTypeName(),
      "fee": tournament.tournament.fee,
      "group": tournament.tournamentGroup().name(),
      "duration": TimeUtils.instance.durationInDays(
          tournament.tournament.startTime, tournament.tournament.endTime)
    });
    if (response?.hasErrors == true)
      UiUtils.getInstance.showToast(response?.errors?.first.message);
    deeplinkData = null;
    _emitState(HomeLoading(false));
    return Future.value(response?.hasErrors == false || response == null);
  }

  void _emitState(HomeState state) {
    if (!isClosed) emit(state);
  }

  void changeLoadingStatues(bool value) {
    HomePageLoaded();
  }

  changeSelectedGames(ESports selectedGame) {
    SharedPreferenceHelper.getInstance
        .setStringPref(PrefKeys.SELECTED_GAMES, selectedGame.getPackageName());
    applicationBloc.userCurrentGame = selectedGame;
    emit(GameSelection(selectedGame));
    eSport = selectedGame;
  }

  void dismissTutorial(int homeScreenIndex) {
    isOnboardingTutorialDismissed = true;
    loadDataOrShowLoader(homeScreenIndex);
  }

  void loadTicker() async {
    _emitState(TickerLoading());
    final response = await QueryRepository.instance.getTicker(7);
    if (!response.hasErrors) {
      final rewardEntries = response.data?.topWinners
          .map((e) => MapEntry(
          e.user.username, "${AppStrings.rupeeSymbol}${e.amount.toInt()}"))
          .toList();
      _emitState(TickerLoaded(rewardEntries != null
          ? (<String, String>{}..addEntries(rewardEntries))
          : {}));
    }
  }

  Future<int> getGameCounts() async {
    var data =
    await Constants.PLATFORM_CHANNEL.invokeMethod("get_game_history");
    Iterable l = jsonDecode(data);
    List<GameHistory> games =
    List<GameHistory>.from(l.map((e) => GameHistory.fromMap(e)));
    return games.length;
  }

  void showRewardDialog(RewardType rewardType) {
    _emitState(ShowRewardBonusDialog(rewardType));
  }

  void showInviteDialog(InviteDialogEvent inviteDialogEvent) {
    _emitState(ShowInviteDialog(inviteDialogEvent));
  }

  void selectTier(MapEntry<Enum, String> mapEntry) {
    selectedTier = mapEntry;
    _emitState(GameTierInputState(teamGroupForIndex()));
  }

  void _checkForLowBalance(UserMixin$Wallet? wallet) {
    if (LowBalancePopupScheduler.shouldShow() == false) return;
    if (wallet == null || (wallet.total()) > 5) return;
    _emitState(LowBalanceWarningState());
  }

  void closeLowBalanceWarning() {
    _emitState(LowBalanceWarningClosedState());
    LowBalancePopupScheduler.setHandled();
    AnalyticService.getInstance()
        .trackEvents(Events.LOW_BALANCE_POPUP_DISMISSED);
  }

  void deposit() {
    _emitState(LowBalanceWarningClosedState());
    AnalyticService.getInstance().trackEvents(Events.LOW_BALANCE_POPUP_DEPOSIT);
  }

  void setSortOrder(TournamentSortOrder sortOrder) {
    _tournamentFilter.clear();
    switch (sortOrder) {
      case TournamentSortOrder.classic:
        {
          break;
        }
      case TournamentSortOrder.head2Head:
        _tournamentFilter.add(UpcomingCustomTournamentFilter());
    }
    //getMyTournaments(currentIndex, teamGroupForIndex(), false);
  }
}

class OnboardingEducationCard
    extends GetMyActiveTournament$Query$ActiveTournamentList$UserTournament {}
