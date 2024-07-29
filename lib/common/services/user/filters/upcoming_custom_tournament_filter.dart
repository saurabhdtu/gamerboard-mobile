import 'package:gamerboard/common/filter/list_filter.dart';
import 'package:gamerboard/common/services/user/user_service.dart';
import 'package:gamerboard/graphql/query.dart';

class UpcomingCustomTournamentFilter
    extends ListFilter<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament> {
  @override
  List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament> filter(List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament> list) {
    list.sort((a, b) {
      // First, sort by matchType (headToHead comes first)
      int matchTypeComparison = b.tournament.matchType.index.compareTo(a.tournament.matchType.index);
      if (matchTypeComparison != 0) {
        return matchTypeComparison;
      }

      // If matchType is the same, sort by joinBy
      DateTime? aJoinBy = a.tournament.joinBy;
      DateTime? bJoinBy = b.tournament.joinBy;

      if (aJoinBy != null && bJoinBy != null) {
        return aJoinBy.compareTo(bJoinBy);
      } else {
        // If one of them doesn't have a joinBy, consider it greater
        return aJoinBy != null ? 1 : -1;
      }
    });
    return list;
  }
}
