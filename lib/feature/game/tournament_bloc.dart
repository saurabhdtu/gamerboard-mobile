import 'dart:async';

import 'package:artemis/artemis.dart';
import 'package:async/async.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/repository/mutation_repo.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/feature/game/game_entities.dart';
import 'package:gamerboard/feature/game/tournament_states.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/time_utils.dart';
import 'package:gamerboard/utils/ui_utils.dart';

import '../../resources/strings.dart';

////Created by saurabh.lahoti on 25/12/21
class TournamentBloc extends Cubit<TournamentState> {
  TournamentPayload tournamentPayload;
  int pageNum = 0;
  bool tournamentJoinedDelta = false;
  int tournamentId = 0;

  bool moreDataAvailable = true;
  UserMixin? user;
  bool isTournamentJoined = false;
  GraphQLResponse<GetGameScoring$Query>? scoring;
  List<LeaderboardItemMapper> leaderboard = [];
  Map<int, UserTournamentMixin> tournamentMap = {};
  CancelableOperation? leaderboardAPICall;
  CancelableOperation? userRankAPICall;
  late ESports eSports;
  late GetTournamentMatches$Query customMatches;

  TournamentBloc(this.tournamentPayload) : super(TournamentLoading());

  void loadData({bool forceLoad = false, ESports? currentGame}) async {
    if (currentGame != null) this.eSports = currentGame;
    try {
      final tournamentRes = await QueryRepository.instance.getTournament(
          tournamentId != 0 ? tournamentId : tournamentPayload.tournamentId);
      if (tournamentId == 0) tournamentId = tournamentPayload.tournamentId;
      if (tournamentRes.hasErrors) return;
      tournamentMap[tournamentId] = tournamentRes.data!.tournament!;
      isTournamentJoined = tournamentRes.data!.tournament!.joinedAt != null;

      var match =
          await QueryRepository.instance.getTournamentMatchQuery(tournamentId);
      customMatches = match.data!;

      var res = await QueryRepository.instance.getMyProfile(getCached: true);
      user = res.data?.me;
      if (forceLoad) {
        _emitState(TournamentLoading());
        List<UserTournamentMixin> list = [];
        list.add(tournamentRes.data!.tournament!);
        if (isTournamentJoined &&
            tournamentRes.data!.tournament!.tournament.endTime
                .isAfter(DateTime.now())) {
          var response =
              await QueryRepository.instance.getMyJoinedTournaments(eSports);
          if (response.hasErrors)
            throw (response.errors!.first.message);
          else {
            response.data!.forEach((element) {
              if (!tournamentMap.containsKey(element.tournament.id))
                list.add(element);
              tournamentMap[element.tournament.id] = element;
            });
          }
        }
        list.forEach((tournamentItem) {
          tournamentItem.tournament.winningDistribution
              .forEach((winningDistribution) {
            var tournamentIndex = tournamentItem.tournament.winningDistribution
                .indexOf(winningDistribution);
            tournamentItem.tournament.winningDistribution[tournamentIndex];
          });
        });
        _emitState(LeaderboardState(list));
        _emitState(TournamentChanged());
        Future.delayed(Duration(milliseconds: 100), () {
          if (tournamentPayload.showTeamDetail) {
            tournamentPayload.showTeamDetail = false;
            loadSquadDetails(true);
          }
        });
      } else {
        _emitState(ScoreRefreshed());
        _emitState(TournamentChanged());
      }
    } catch (ex, stacktrace) {
      debugPrintStack(stackTrace: stacktrace);
      UiUtils.getInstance.showToast(ex.toString());
      _emitState(TournamentError());
    }
  }

  Future<void> getTournamentMatchDetail(int tournamentId) async {
    var call =
        await QueryRepository.instance.getTournamentMatchQuery(tournamentId);
    print(call.data!);
  }

  void resetLeaderboard() {
    pageNum = 0;
    leaderboard.clear();
    moreDataAvailable = true;
  }

  Future<void> getLeaderboard(BuildContext context, int tournamentId,
      {bool showLoader = false}) async {
    if (this.tournamentId != tournamentId) {
      showLoader = true;
      this.tournamentId = tournamentId;
      refreshPage(context);
      _emitState(TournamentChanged());
    }
    if (moreDataAvailable) {
      leaderboardAPICall?.cancel();
      if (showLoader) _emitState(LeaderboardLoading());
      var apiCall = QueryRepository.instance.getLeaderboard(
          this.tournamentId,
          LeaderboardDirection.next,
          tournamentMap[tournamentId]!.tournamentGroup(),
          pageNum: pageNum + 1);
      leaderboardAPICall = CancelableOperation.fromFuture(apiCall);
      var response = await leaderboardAPICall?.valueOrCancellation();
      if (response != null) {
        if (isTournamentJoined) getUserInLeaderboard();
        if (scoring == null)
          scoring = await QueryRepository.instance.getScoring(eSports);

        if (response.hasErrors) {
          UiUtils.getInstance.showToast(response.errors!.first.message);
          _emitState(LeaderboardLoading(stateLoading: false));
        } else {
          debugPrint("---------> api response   ");
          if (response.data!.isNotEmpty) {
            pageNum++;
            leaderboard.addAll(response.data!);
            if (tournamentMap[tournamentId]!.tournament.matchType ==
                MatchType.headToHead) {
              if (leaderboard
                  .where((element) => element.matchesPlayed! > 0)
                  .toList()
                  .isNotEmpty) {
                AnalyticService.getInstance()
                    .trackEvents(Events.VIEW_CUSTOM_ROOM_RESULT, properties: {
                  "tournament": tournamentId,
                });
              }
            }
          } else
            moreDataAvailable = false;
          _emitState(
              LeaderboardLoaded(leaderboard, tournamentId, userOrTeamId));
        }
      }
    }
    return Future.value();
  }

  int get userOrTeamId {
    UserTournamentMixin? tour = tournamentMap[tournamentId];
    if (tour?.squad != null) {
      return tour?.squad?.id ?? -1;
    } else if (tour?.tournamentGroup() == GameTeamGroup.solo) {
      return user?.id ?? -1;
    } else
      return -1;
  }

  void getUserInLeaderboard() async {
    userRankAPICall?.cancel();
    var apiCall = QueryRepository.instance.getLeaderboard(
        tournamentId,
        LeaderboardDirection.next,
        tournamentMap[tournamentId]!.tournamentGroup(),
        userOrTeamId: userOrTeamId,
        pageSize: 1);
    userRankAPICall = CancelableOperation.fromFuture(apiCall);
    var response = await userRankAPICall?.valueOrCancellation();
    if (response != null && !response.hasErrors) {
      _emitState(MyRankLoaded(
          response.data != null && response.data!.isNotEmpty == true
              ? response.data!.first
              : null));
    } else {
      _emitState(LeaderboardLoading(stateLoading: false));
    }
  }

  int getTopRankScore(LeaderboardItemMapper leaderboardItem) {
    var rankScore = 0;
    if (leaderboardItem.topGames != null &&
        leaderboardItem.topGames?.gameResults?.isNotEmpty == true) {
      leaderboardItem.topGames?.gameResults?.forEach((element) {
        var score = scoring?.data?.scoring.rankPoints
            .firstWhere((e) => element.rank == e.rank, orElse: () {
          var x = GetGameScoring$Query$Scoring$RankPoint();
          x.rank = 0;
          x.points = 0;
          return x;
        });
        rankScore += (score?.points ?? 0);
      });
    }

    return rankScore;
  }

  int getTopKillScore(LeaderboardItemMapper leaderboardItem) {
    return (leaderboardItem.score ?? 0) - getTopRankScore(leaderboardItem);
  }

  UserTournamentMixin get currentTournament => tournamentMap[tournamentId]!;

  void joinTournament(BuildContext context, TournamentMixin tournament,
      {String? phoneNumber, String? joinCode}) async {

    if (tournament.matchType == MatchType.headToHead && phoneNumber == null) {
      _emitState(
          CustomTournamentQualifiedState(tournament));
      return;
    }
    if(tournament.joinCode != null && joinCode == null
        && tournament.tournamentGroup() == GameTeamGroup.solo){
      _emitState(ShowInviteDialogAlert(tournament,
          phone: phoneNumber));
      return;
    }
    if (currentTournament.tournamentGroup() != GameTeamGroup.solo) {
      if (currentTournament.squad != null) {
        await navigateToCreateTeam(context, currentTournament, true, true,
            phoneNumber: phoneNumber,
            inviteCode: currentTournament.squad?.inviteCode);
      } else {
        var result = await Navigator.of(context)
            .pushNamed(Routes.TEAM_JOIN_CREATE, arguments: {
          "tournament": currentTournament,
          "fromTournament": true,
          "phoneNumber": phoneNumber
        });
        if (result == true) tournamentJoinedDelta = true;
      }
      loadSquadDetails(false);
      refreshPage(context, showLoader: true);
    } else {
      if (tournament.fee > 0) {
        UiUtils.getInstance.alertDialog(
            context,
            AppStrings.joiningConformation + "?",
            AppStrings.doYouWantToPay(tournament.fee), yesAction: () async {
          joinSoloTournament(context, tournament, phoneNumber: phoneNumber);
        });
      } else {
        joinSoloTournament(context, tournament, phoneNumber: phoneNumber);
      }
    }
  }

  joinSoloTournament(BuildContext context, TournamentMixin tournament,
      {String? phoneNumber}) async {
    var tournamentResponse;
    UiUtils.getInstance.buildLoading(context);

    if (phoneNumber != null) {
      tournamentResponse = await MutationRepository.instance
          .enterTournament(tournament.id, phoneNumber: phoneNumber);
    } else {
      tournamentResponse =
          await MutationRepository.instance.enterTournament(tournament.id);
    }
    if (tournamentResponse.hasErrors) {
      UiUtils.getInstance.showToast(tournamentResponse.errors!.first.message);
    } else {
      UiUtils.getInstance.showToast("Joined tournament");
      AnalyticService.getInstance().trackEvents(Events.LB_JOINED, properties: {
        "id": tournament.id,
        "from": "leaderboard_page",
        "tournament_type": tournament.matchType.getMatchTypeName(),
        "fee": tournament.fee,
        "group": tournament.tournamentGroup().name(),
        "duration": TimeUtils.instance
            .durationInDays(tournament.startTime, tournament.endTime)
      });
      tournamentJoinedDelta = true;
      refreshPage(context, showLoader: true);
    }
    Navigator.pop(context);
  }

  void checkUserQualification(
      BuildContext context, TournamentMixin tournament) async {
    UiUtils.getInstance.buildLoading(context);
    var checkQualificationResponse = await QueryRepository.instance
        .checkUserTournamentQualification(tournament.id);
    if (checkQualificationResponse.hasErrors) {
      Navigator.of(context).pop();
      UiUtils.getInstance
          .showToast(checkQualificationResponse.errors!.first.message);
    } else {
      Navigator.pop(context);
      if (tournament.qualifiers!.first.rule ==
          CustomQualificationRuleTypes.rankByTournament) {
        var targetTournament = await getTournamentId(
            tournament.qualifiers!.first.value["tournamentId"]);
        _emitState(QualifiedForTournament(
            getQualificationRule(checkQualificationResponse
                .data!.getTournamentQualification!.rules.first.rule),
            checkQualificationResponse.data!.getTournamentQualification!,
            tournament,
            tournamentName: targetTournament!.tournament.name));
      } else {
        _emitState(QualifiedForTournament(
          getQualificationRule(checkQualificationResponse
              .data!.getTournamentQualification!.rules.first.rule),
          checkQualificationResponse.data!.getTournamentQualification!,
          tournament,
        ));
      }
    }
  }

  FutureOr<UserTournamentMixin?> getTournamentId(int tournamentId) async {
    var getTournament =
        await QueryRepository.instance.getTournament(tournamentId);
    if (getTournament.hasErrors) {
      UiUtils.getInstance.showToast(getTournament.errors!.first.message);
    } else {
      return getTournament.data!.tournament!;
    }
    return null;
  }

  Future<void> refreshPage(BuildContext context,
      {bool showLoader = false}) async {
    if (showLoader) UiUtils.getInstance.buildLoading(context);
    resetLeaderboard();
    if (showLoader || !tournamentMap.containsKey(tournamentId)) {
      var res = await QueryRepository.instance.getTournament(tournamentId);
      if(res.data != null && res.data?.tournament != null)
        tournamentMap[tournamentId] = res.data!.tournament!;
    }
    if (showLoader) Navigator.pop(context);
    loadData(forceLoad: showLoader);
    return Future.value();
  }

  String getRankScoring() {
    String s = "";
    if (scoring != null && scoring?.data != null) {
      scoring?.data?.scoring.rankPoints.forEach((element) {
        s += "   Rank ${element.rank}: ${element.points}pts   ";
      });
    }
    return s;
  }

  @override
  Future<Function> close() {
    super.close();
    return Future.value(() {
      leaderboardAPICall?.cancel();
      userRankAPICall?.cancel();
    });
  }

  void loadSquadDetails(bool show) {
    _emitState(LoadSquadDetails(show));
  }

  void _emitState(TournamentState state) {
    if (!isClosed) emit(state);
  }
}
