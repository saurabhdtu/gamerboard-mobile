import 'package:gamerboard/common/filter/list_filter.dart';
import 'package:gamerboard/common/services/user/user_service.dart';
import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
typedef UserTournament = GetMyActiveTournament$Query$ActiveTournamentList$UserTournament;
class TournamentFilterHelper {

  static List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament>
      filterTournamentByGroup(
          List<ActiveTournamentList> activeTournamentList, GameTeamGroup group,
          {bool includeCustom = false}) {
    List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament>
        tournaments = [];
    activeTournamentList.forEach((element) {
      if (element.type.toLowerCase() == group.name().toLowerCase()) {
        tournaments.addAll(element.tournaments);
      }
    });

    return tournaments;
  }

  static List<UserTournament> applyFilterOnList(
      List<UserTournament> data, List<ListFilter<UserTournament>> filters) {
    List<UserTournament> finalResult = data;
    filters.forEach((element) {
      finalResult = element.filter(finalResult) ;
    });
    return finalResult;
  }
}
