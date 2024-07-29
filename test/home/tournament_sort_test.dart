import 'package:flutter_test/flutter_test.dart';
import 'package:gamerboard/feature/home/home_bloc.dart';
import 'package:gamerboard/feature/home/model/tournament_sort_order.dart';
import 'package:gamerboard/feature/home/model/tournament_sort_order_feature.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:mockito/mockito.dart';
import 'package:shared_preferences/shared_preferences.dart';

void main() {
  group('Tournament Order', () {

    test('Flag custom_solo, Team group solo returns head2Head order type', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.customSolo, GameTeamGroup.solo);
      expect(tournamentOrder, TournamentSortOrder.head2Head);
    });

    test('Flag custom_solo, Team group squad returns classic order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.customSolo, GameTeamGroup.squad);
      expect(tournamentOrder, TournamentSortOrder.classic);
    });

    test('Flag custom_solo, Team group duo returns classic order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.customSolo, GameTeamGroup.duo);
      expect(tournamentOrder, TournamentSortOrder.classic);
    });

    test('Custom duo tournament order, duo team group returns head2Head order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.customDuo, GameTeamGroup.duo);
      expect(tournamentOrder, TournamentSortOrder.head2Head);
    });

    test('Flag custom_duo, Team group solo returns classic order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.customDuo, GameTeamGroup.solo);
      expect(tournamentOrder, TournamentSortOrder.classic);
    });

    test('Flag custom_duo, Team group squad returns classic order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.customDuo, GameTeamGroup.squad);
      expect(tournamentOrder, TournamentSortOrder.classic);
    });

    test('Custom squad tournament order, squad team group returns head2Head order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.customSquad, GameTeamGroup.squad);
      expect(tournamentOrder, TournamentSortOrder.head2Head);
    });

    test('Flag custom_squad, Team group duo returns classic order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.customSquad, GameTeamGroup.duo);
      expect(tournamentOrder, TournamentSortOrder.classic);
    });

    test('Flag custom_squad, Team group solo returns classic order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.customSquad, GameTeamGroup.solo);
      expect(tournamentOrder, TournamentSortOrder.classic);
    });

    test('Flag all custom, Team group solo returns custom order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.allCustom, GameTeamGroup.solo);
      expect(tournamentOrder, TournamentSortOrder.head2Head);
    });

    test('Flag all custom, Team group duo returns custom order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.allCustom, GameTeamGroup.solo);
      expect(tournamentOrder, TournamentSortOrder.head2Head);
    });

    test('Flag all custom, Team group squad returns custom order', () async {
      var tournamentOrder = TournamentSortOrderFeature.getSortOrderByFeature(
          TournamentSortOrderFeature.allCustom, GameTeamGroup.squad);
      expect(tournamentOrder, TournamentSortOrder.head2Head);
    });
  });
}

class MockHomeBloc extends Mock implements HomeBloc {}

class MockSharedPreference extends Mock implements SharedPreferences {
}
