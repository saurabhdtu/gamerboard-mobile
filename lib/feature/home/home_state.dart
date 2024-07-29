import 'package:flagsmith/flagsmith.dart';
import 'package:gamerboard/common/models/deeplink_data.dart';
import 'package:gamerboard/graphql/query.graphql.dart';

import '../../utils/reward_utils.dart';
import '../../utils/share_utils.dart';
import '../../utils/graphql_ext.dart';

////Created by saurabh.lahoti on 06/08/21

abstract class HomeState {}

class HomeLoading extends HomeState {
  bool showLoader;

  HomeLoading(this.showLoader);
}

class ShowSoloJoinAlert extends HomeState {
  UserTournamentMixin tournament;
  int pageType;
  String? phone;
  String? joinCode;

  ShowSoloJoinAlert(this.tournament, this.pageType , {this.phone, this.joinCode});
}
class ShowInviteDialogAlert extends HomeState {
  UserTournamentMixin tournament;
  int pageType;
  String? phone;

  ShowInviteDialogAlert(this.tournament, this.pageType , {this.phone});
}

class HomeLoaded extends HomeState {
  String deviceId;

  bool? uploadImages;
  bool? captureImages;
  bool? serviceRunning;

  HomeLoaded(this.deviceId, this.captureImages, this.uploadImages,
      this.serviceRunning);
}

class Tick extends HomeState {}

class UserDetailsLoaded extends HomeState {
  UserMixin user;
  UserDetailsLoaded(this.user);
}


class ShowGameSelectionDialog extends HomeState {
  bool showDialog;
  ShowGameSelectionDialog(this.showDialog);
}


class ChangeUserFilterState extends HomeState {
  bool filterUpdated;
  ChangeUserFilterState(this.filterUpdated);
}


class TournamentLoading extends HomeState {
  bool showProgress;
  GameTeamGroup? group;

  TournamentLoading(this.showProgress, this.group);
}

class MyTournamentLoaded extends HomeState {
  List<UserTournamentMixin> activeTournaments;
  GameTeamGroup group;

  MyTournamentLoaded(this.activeTournaments, this.group);
}

class GameSelection extends HomeState {
  ESports selectedGame;

  GameSelection(this.selectedGame);
}

class QualifiedForTournament extends HomeState {
  CustomQualificationRuleTypes ruleType;
  CheckUserTournamentQualification$Query$TournamentQualificationResult qualificationResult;
  UserTournamentMixin tournament;
  int pageType;
  String? tournamentName;


  QualifiedForTournament(this.ruleType,this.qualificationResult,
      this.tournament,this.pageType,{this.tournamentName});
}

class GameTierInputState extends HomeState {
  GameTeamGroup group;

  GameTierInputState(this.group);
}

class TopTournamentsLoaded extends HomeState {
  List<UserTournamentMixin> topTournaments;

  TopTournamentsLoaded(this.topTournaments);
}

class HomePageLoaded extends HomeState {}

class NavigateToTabState extends HomeState {
  int page;

  NavigateToTabState(this.page);
}
class LowBalanceWarningState extends HomeState {}
class LowBalanceWarningClosedState extends HomeState {}
class LoadTutorialVideo extends HomeState {
  LoadTutorialVideo();
}
class CustomTournamentQualifiedState extends HomeState{
  UserTournamentMixin userTournamentMixin;
  int pageType;

  CustomTournamentQualifiedState(this.userTournamentMixin,this.pageType);
}


class UserPreferenceDialogState extends HomeState{
  int currentUserPosition;

  UserPreferenceDialogState(this.currentUserPosition);
}

class UpdatingBgmiLevel extends HomeState {
  final bool showProgress;

  UpdatingBgmiLevel(this.showProgress);
}

class UserPreferenceSelectionDialogState extends HomeState {
  final bool showPreferenceSelectionDialog;

  UserPreferenceSelectionDialogState(this.showPreferenceSelectionDialog);
}

class ShowSquadInviteDialog extends HomeState {
  UserTournamentMixin userTournamentMixin;
  DeeplinkData deeplinkData;
  bool switchTeam;

  ShowSquadInviteDialog(
      this.userTournamentMixin, this.deeplinkData, this.switchTeam);
}

class UserPreferenceListState extends HomeState {
  List<GetUserListByPreference$Query$User?> userList;
  UserPreferenceListState(this.userList);
}
class ShowEndDrawerForUserPreferenceState extends HomeState {
  final bool openDrawer;

  ShowEndDrawerForUserPreferenceState(this.openDrawer);
}


class ShowRewardBonusDialog extends HomeState {
  final RewardType rewardType;
  ShowRewardBonusDialog(this.rewardType);
}

class AlertTeamIsComplete extends HomeState {}


class ShowInviteDialog extends HomeState {
  InviteDialogEvent inviteDialogEvent;
  ShowInviteDialog(this.inviteDialogEvent);
}
class NavigateToLocationPrefs extends HomeState {
  Identity identity;
  NavigateToLocationPrefs(this.identity);
}

class TickerLoading extends HomeState {}

class TickerLoaded extends HomeState {
  Map<String, String> map;

  TickerLoaded(this.map);
}
