import 'package:gamerboard/feature/home/model/tournament_sort_order.dart';
import 'package:gamerboard/utils/graphql_ext.dart';

class TournamentSortOrderFeature{
  static const String classic = "classic";
  static const String allCustom = "all_custom";
  static const String customSquad = "custom_squad";
  static const String customDuo = "custom_duo";
  static const String customSolo = "custom_solo";

  static TournamentSortOrder getSortOrderByFeature(String feature, GameTeamGroup group) {
    switch (feature) {
      case TournamentSortOrderFeature.classic:
        {
          return TournamentSortOrder.classic;
        }
      case TournamentSortOrderFeature.customSolo:
        {
          if (group == GameTeamGroup.solo) {
            return TournamentSortOrder.head2Head;
          }
        }
      case TournamentSortOrderFeature.customSquad:
        {
          if (group == GameTeamGroup.squad) {
            return TournamentSortOrder.head2Head;
          }
        }
      case TournamentSortOrderFeature.customDuo:
        {
          if (group == GameTeamGroup.duo) {
            return TournamentSortOrder.head2Head;
          }
        }
      case TournamentSortOrderFeature.allCustom:
        {
          return TournamentSortOrder.head2Head;
        }
    }
    return TournamentSortOrder.classic;
  }
}