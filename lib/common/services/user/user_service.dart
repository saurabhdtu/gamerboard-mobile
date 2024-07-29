import 'package:artemis/schema/graphql_response.dart';
import 'package:gamerboard/common/filter/list_filter.dart';
import 'package:gamerboard/graphql/query.dart';
typedef ActiveTournamentList = GetMyActiveTournament$Query$ActiveTournamentList;

abstract class UserService{
  Future<GraphQLResponse<SubmitPreferences$Mutation>> savePreferences(PreferencesInput preferencesInput);
  Future<GraphQLResponse<GetMyProfile$Query>> getMyProfile(
      {bool getCached = false});
  Future<GraphQLResponse<GetUserListByPreference$Query>> getAchievementSearchUsers(PreferencesInput preferencesInput);
  Future<List<ActiveTournamentList>> getActiveTournaments(ESports eSports, List<ListFilter> filters);
}

