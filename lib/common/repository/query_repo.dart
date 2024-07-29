import 'dart:convert';

import 'package:artemis/artemis.dart';
import 'package:flutter/foundation.dart';
import 'package:gamerboard/common/base_respository.dart';
import 'package:gamerboard/feature/game/game_entities.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/shared_preferences.dart';
import 'package:gamerboard/utils/validators.dart';

import '../../graphql/custom.dart';
// import 'package:onesignal_flutter/onesignal_flutter.dart';

////Created by saurabh.lahoti on 21/12/21

class QueryRepository extends BaseRepository {
  static QueryRepository? _instance;

  QueryRepository._();

  static QueryRepository get instance => _instance ??= QueryRepository._();

  Future<GraphQLResponse<VerifyOTP$Query>> verifyOTP(
      String mobile, int otp) async {
    var query = VerifyOTPQuery(
        variables: VerifyOTPArguments(otp: otp, phoneNum: mobile));
    return executeCall(query);
  }



  Future<GraphQLResponse<GetUserListByPreference$Query>> getAchievementSearchUsers(
      PreferencesInput userPreference) async {
    var query = GetUserListByPreferenceQuery(
        variables: GetUserListByPreferenceArguments(preference:userPreference ));
    return executeCall(query);
  }

  Future<GraphQLResponse<LoginOTPLess$Query>> verifyOtpLessToken(String token) async {
    var query = LoginOTPLessQuery(
        variables: LoginOTPLessArguments(token: token));
    return executeCall(query);
  }

  Future<GraphQLResponse<GetTournamentMatches$Query>>  getTournamentMatchQuery(int tournamentId){
    var query = GetTournamentMatchesQuery(variables: GetTournamentMatchesArguments(
      tournamentId: tournamentId
    ));
    return executeCall(query);
  }

  Future<GraphQLResponse<CheckUserTournamentQualification$Query>> checkUserTournamentQualification(int tournamentId) async {
    var query = CheckUserTournamentQualificationQuery(
        variables: CheckUserTournamentQualificationArguments(tournamentId: tournamentId));
    return executeCall(query);
  }

  Future<GraphQLResponse<GetMyProfile$Query>> getMyProfile(
      {bool getCached = false}) async {
    final String? localData = await SharedPreferenceHelper.getInstance
        .getStringPref(PrefKeys.USER_DATA);
    if (getCached && localData != null) {
      var data = GetMyProfile$Query.fromJson(jsonDecode(localData));
      var response = GraphQLResponse<GetMyProfile$Query>(data: data);
      return Future.value(response);
    } else {
      var query = GetMyProfileQuery();
      var response = await executeCall(query);
      if (!response.hasErrors) {
        String profile = jsonEncode(response.data!.toJson());
        AnalyticService.getInstance().pushUserProperties({
          "user_name": response.data!.me.username,
          "mobile_no": response.data!.me.phone,
          "dob": AppDateFormats.dobFormat.format(response.data!.me.birthdate!),
          "name": response.data!.me.name
        });
        SharedPreferenceHelper.getInstance
            .setStringPref(PrefKeys.USER_DATA, profile);
      }
      // OneSignal.shared.setExternalUserId(response.data!.me.id.toString());
      // OneSignal.shared.setSMSNumber(smsNumber: "+91${response.data!.me.phone}");

      return Future.value(response);
    }
  }

  Future<GraphQLResponse<GetMyActiveTournament$Query>> getMyActiveTournaments(ESports eSports) async {
    var query = GetMyActiveTournamentQuery(
        variables: GetMyActiveTournamentArguments(eSport: eSports));
    return executeCall(query);
  }

  Future<GraphQLResponse<List<UserTournamentMixin>>> getMyJoinedTournaments(
      ESports eSports) async {
    var query = GetMyActiveTournamentQuery(
        variables: GetMyActiveTournamentArguments(eSport: eSports));
    GraphQLResponse<GetMyActiveTournament$Query> response =
        await executeCall(query);
    GraphQLResponse<List<UserTournamentMixin>> result;
    if (response.data != null) {
      List<UserTournamentMixin> tournamentList = [];
      response.data!.active.forEach((element) {
        element.tournaments.forEach((element2) {
          if (element2.joinedAt != null) tournamentList.add(element2);
        });
      });
      result = GraphQLResponse<List<UserTournamentMixin>>(data: tournamentList);
    } else {
      result =
          GraphQLResponse<List<UserTournamentMixin>>(errors: response.errors);
    }
    return Future.value(result);
  }

  Future<GraphQLResponse<GetTopTournaments$Query>> getTopTournaments(
      ESports eSports) async {
    var query = GetTopTournamentsQuery(
        variables: GetTopTournamentsArguments(eSport: eSports));
    return executeCall(query);
  }

  Future<GraphQLResponse<GetTournamentsHistory$Query>> getTournamentHistory(
      ESports eSports) async {
    var query = GetTournamentsHistoryQuery(
        variables: GetTournamentsHistoryArguments(eSport: eSports));
    return executeCall(query);
  }

  Future<GraphQLResponse<GetGameScoring$Query>> getScoring(ESports eSport) {
    var query = GetGameScoringQuery(variables: GetGameScoringArguments(eSport: eSport));
    return executeCall(query);
  }

  Future<GraphQLResponse<List<LeaderboardItemMapper>>> getLeaderboard(
      int tournamentId, LeaderboardDirection direction, GameTeamGroup group,
      {int? pageNum, int? userOrTeamId, int? pageSize}) async {
    if (GameTeamGroup.solo == group) {
      var query = GetLeaderboardQuery(
          variables: GetLeaderboardArguments(
              tournamentId: tournamentId,
              direction: direction,
              page: pageNum,
              pageSize: pageSize,
              userId: userOrTeamId));

      final response = await executeCall(query);
          GraphQLResponse<List<LeaderboardItemMapper>> res =
          GraphQLResponse<List<LeaderboardItemMapper>>(
              errors: response.errors,
              context: response.context,
              data: response.data?.leaderboard?.map((e) => LeaderboardItemMapper.fromSoloLeaderboard(e)).toList() ?? []);
          return Future.value(res);

    } else {
      var query = GetSquadLeaderboardQuery(
          variables: GetSquadLeaderboardArguments(
              tournamentId: tournamentId,
              direction: direction,
              page: pageNum,
              pageSize: pageSize,
              squadId: userOrTeamId));
      final response = await executeCall(query);

      GraphQLResponse<List<LeaderboardItemMapper>> res =
          GraphQLResponse<List<LeaderboardItemMapper>>(
              errors: response.errors,
              context: response.context,
              data: response.data?.squadLeaderboard
                  .map((e) => LeaderboardItemMapper.fromTeamLeaderboard(e))
                  .toList());
      return Future.value(res);
    }
  }

  Future<GraphQLResponse<GetTournament$Query>> getTournament(int tournamentId) {
    var query = GetTournamentQuery(
        variables: GetTournamentArguments(tournamentId: tournamentId));
    return executeCall(query);
  }

  Future<GraphQLResponse<CheckUniqueUser$Query>> checkUniqueUser(
      String phoneNum, String userName) {
    var query = CheckUniqueUserQuery(
        variables:
            CheckUniqueUserArguments(phoneNum: phoneNum, userName: userName));
    return executeCall(query);
  }

  Future<GraphQLResponse<GetTransactions$Query>> getWalletLedger() {
    var query = GetTransactionsQuery();
    return executeCall(query);
  }

  Future<GraphQLResponse<Transaction$Query>> transaction(
      int transactionId) {
    var query = TransactionQuery(variables: TransactionArguments(transactionId: transactionId));
    return executeCall(query);
  }

  Future<GraphQLResponse<List<UserSearchResult>>> searchUser(ESports eSports,
      {String? username, String? phone, String? gameProfileId}) async {
    var query = SearchUserQuery(
        variables: SearchUserArguments(eSport: eSports, gameProfileId: gameProfileId,phoneNum: phone, userName: username));
    var response = await executeCall(query);
    GraphQLResponse<List<UserSearchResult>> myResponse =
        GraphQLResponse<List<UserSearchResult>>(
            errors: response.errors,
            data: response.data?.searchUserESports
                .map((e) => UserSearchResult(false, e))
                .toList());
    return Future.value(myResponse);
  }

  Future<GraphQLResponse<List<UserSearchResult>>> recentPlayers(
      int tournamentId) async {
    var query = RecentPlayersQuery(
        variables: RecentPlayersArguments(tournamentId: tournamentId));
    var response = await executeCall(query);
    GraphQLResponse<List<UserSearchResult>> myResponse =
        GraphQLResponse<List<UserSearchResult>>(
            errors: response.errors,
            data: response.data?.inviteList
                .map((e) => UserSearchResult(false, e))
                .toList());
    return Future.value(myResponse);
  }

  Future<GraphQLResponse<TopWinners$Query>> getTicker(int lastNDays) async {
    var query = TopWinnersQuery(
        variables: TopWinnersArguments(
            count: 15,
            from: DateTime.now()
                .subtract(Duration(days: kDebugMode ? 300 : lastNDays)),
            to: DateTime.now()));
    var response = await executeCall(query);
    return Future.value(response);
  }
}
