import 'package:artemis/schema/graphql_response.dart';
import 'package:gamerboard/common/exception/api_exception.dart';
import 'package:gamerboard/common/filter/list_filter.dart';
import 'package:gamerboard/common/repository/mutation_repo.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/common/services/user/user_service.dart';
import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/file_manager.dart';

class ApiUserService extends UserService{

  static ApiUserService? _instance;

  Future<GraphQLResponse<GetMyActiveTournament$Query>>? _activeTournamentCall = null;

  ApiUserService._();

  static ApiUserService get instance => _instance ??= ApiUserService._();


  @override
  Future<GraphQLResponse<GetMyProfile$Query>> getMyProfile({bool getCached = false}) {
    return  QueryRepository.instance.getMyProfile(getCached: getCached);
  }

  @override
  Future<GraphQLResponse<SubmitPreferences$Mutation>> savePreferences(PreferencesInput preferencesInput) {
    return  MutationRepository.instance.submitUserPreference(preferencesInput);
  }

  @override
  Future<List<ActiveTournamentList>> getActiveTournaments(ESports eSports, List<ListFilter> filters) async{
    _activeTournamentCall?.ignore();
    _activeTournamentCall = QueryRepository.instance.getMyActiveTournaments(eSports);
    GraphQLResponse<GetMyActiveTournament$Query>? response = await _activeTournamentCall;
    if(response?.hasErrors == true){
      throw ApiException(response?.errors?.first.message ?? AppStrings.someErrorOccurred);
    }
    return response?.data?.active ?? [];
  }


  @override
  Future<GraphQLResponse<GetUserListByPreference$Query>> getAchievementSearchUsers(PreferencesInput preferencesInput) {
    return QueryRepository.instance.getAchievementSearchUsers(preferencesInput);
  }



}