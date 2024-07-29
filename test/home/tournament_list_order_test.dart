import 'dart:convert';
import 'dart:io';

import 'package:gamerboard/common/filter/list_filter.dart';
import 'package:gamerboard/common/services/user/api_user_service.dart';
import 'package:gamerboard/common/services/user/filters/tournament_filter_helper.dart';
import 'package:gamerboard/common/services/user/filters/upcoming_custom_tournament_filter.dart';
import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:test/test.dart';

void main() {
  final userService = ApiUserService.instance;
  test('Solo tournaments sorted by next available custom tournament ', () async {
    var dataFile = 'test_resources/test/sample_tournament.json';
    var group = GameTeamGroup.solo;
    var filters = [UpcomingCustomTournamentFilter()];
    List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament>
        tournament =
        await _getFilteredData(dataFile, userService, filters, group);
    expect(tournament.first.tournament.name, 'NewSTest2');
  });

  test('Solo tournaments sorted by no filters', () async {
    var dataFile = 'test_resources/test/sample_tournament.json';
    var group = GameTeamGroup.solo;
    List<ListFilter<UserTournament>> filters = [];
    List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament>
    tournament =
    await _getFilteredData(dataFile, userService, filters, group);
    expect(tournament.first.tournament.name, 'NewSTest1');
  });

  test('Squad tournament sorted by next available custom tournament ', () async {
    var dataFile = 'test_resources/test/sample_tournament.json';
    var group = GameTeamGroup.squad;
    var filters = [UpcomingCustomTournamentFilter()];
    List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament>
    tournament =
    await _getFilteredData(dataFile, userService, filters, group);
    expect(tournament.first.tournament.name, 'NewTest1');
  });

  test('Squad tournament sorted by no filters', () async {
    var dataFile = 'test_resources/test/sample_tournament.json';
    var group = GameTeamGroup.squad;
    List<ListFilter<UserTournament>> filters = [];
    List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament>
    tournament =
    await _getFilteredData(dataFile, userService, filters, group);
    expect(tournament.first.tournament.name, 'NewTest4');
  });
}

Future<List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament>>
    _getFilteredData(
        String dataFile,
        ApiUserService userService,
        List<ListFilter<UserTournament>> filters,
        GameTeamGroup group) async {
  var file = File(dataFile);
  final json = jsonDecode(await file.readAsString());
  final activeTournamentList = GetMyActiveTournament$Query.fromJson(json);


  List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament>?
  tournaments = TournamentFilterHelper.filterTournamentByGroup(
      activeTournamentList.active ?? [], group,
      includeCustom: true);

  TournamentFilterHelper.applyFilterOnList(
      tournaments, filters);
  return tournaments;
}
