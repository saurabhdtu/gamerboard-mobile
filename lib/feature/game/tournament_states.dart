import 'package:gamerboard/feature/game/game_entities.dart';
import 'package:gamerboard/graphql/query.dart';

////Created by saurabh.lahoti on 25/12/21

abstract class TournamentState {}

class TournamentLoading extends TournamentState {
  bool showLoader;

  TournamentLoading({this.showLoader = false});
}

class TournamentError extends TournamentState {}

class LeaderboardState extends TournamentState {
  List<UserTournamentMixin> tournaments;

  LeaderboardState(this.tournaments);
}

class ScoreRefreshed extends TournamentState {}

class QualifiedForTournament extends TournamentState {
  CustomQualificationRuleTypes ruleType;
  CheckUserTournamentQualification$Query$TournamentQualificationResult qualificationResult;
  TournamentMixin tournament;
  String? tournamentName;


  QualifiedForTournament(this.ruleType,this.qualificationResult,
      this.tournament,{this.tournamentName});
}

class CustomTournamentQualifiedState extends TournamentState{
  TournamentMixin tournament;

  CustomTournamentQualifiedState(this.tournament);
}
class ShowInviteDialogAlert extends TournamentState {
  TournamentMixin tournament;
  String? phone;

  ShowInviteDialogAlert(this.tournament, {this.phone});
}


class LeaderboardLoaded extends TournamentState {
  late List<LeaderboardItemMapper> leaderboard;
  int tournamentId;
  late int myIndex;

  LeaderboardLoaded(
      List<LeaderboardItemMapper> leaderboard, this.tournamentId, int? myId) {
    this.leaderboard = []..addAll(leaderboard);
    myIndex = leaderboard.indexWhere((element) => element.myId == myId);
  }
}

class MyRankLoaded extends TournamentState {
  final LeaderboardItemMapper? myLeaderboard;

  MyRankLoaded(this.myLeaderboard);
}

class LeaderboardLoading extends TournamentState {
  bool stateLoading;

  LeaderboardLoading({this.stateLoading = true});
}

class LoadSquadDetails extends TournamentState {
  bool show;

  LoadSquadDetails(this.show);
}

class TournamentChanged extends TournamentState {}
