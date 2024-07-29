import 'package:artemis/artemis.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/feature/team/team_invite/team_invite_states.dart';
import 'package:gamerboard/graphql/custom.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/utils/ui_utils.dart';

////Created by saurabh.lahoti on 14/03/22
class TeamInviteBloc extends Cubit<TeamInviteState> {
  final UserTournamentMixin userTournamentMixin;
  final SquadMixin squad;
  late ESports currentEsport;
  List<UserSearchResult> recentPlayers = [];

  TeamInviteBloc(this.userTournamentMixin, this.squad)
      : super(TeamInviteLoading());
  String lastSearchedText = "";

  void getRecentPlayers() async {
    emit(RecentLoading());
    if (recentPlayers.isEmpty) {
      var response = await QueryRepository.instance
          .recentPlayers(userTournamentMixin.tournament.id);
      if (response.hasErrors) {
        UiUtils.getInstance.showToast(response.errors!.first.message);
        emit(RecentLoaded([]));
      } else {
        recentPlayers.addAll(response.data!);
      }
    }
    emit(RecentLoaded(recentPlayers));
  }

  void searchPlayers(final SearchMode searchMode, {String? text}) async {
    if (text != null) {
      if (lastSearchedText != text && text.isNotEmpty) {
        emit(TeamInviteSearching());
        lastSearchedText = text;
        List<UserSearchResult> searchResults = [];
        GraphQLResponse<List<UserSearchResult>> response;
        switch(searchMode){
          case SearchMode.userName:
            response = await QueryRepository.instance
                .searchUser(currentEsport,username: lastSearchedText);
            break;
          case SearchMode.mobile:
            response = await QueryRepository.instance
                .searchUser(currentEsport,phone: lastSearchedText);
            break;
          case SearchMode.gameProfileId:
            response = await QueryRepository.instance
                .searchUser(currentEsport,gameProfileId: lastSearchedText);
            break;
        }
        if (response.hasErrors) {
          UiUtils.getInstance.showToast(response.errors!.first.message);
        } else {
          searchResults.addAll(response.data!);
        }
        if (searchResults.isEmpty && searchMode == SearchMode.mobile) {
          searchResults.add(UserSearchResult(
              false,
              SearchUser$Query$User()
                ..id = -1
                ..image = ImageConstants.DEFAULT_USER_PLACEHOLDER
                ..name = lastSearchedText));
        }
        emit(TeamInviteSearchResult(searchResults));
      }
    }
  }

  void inviteMember() {}

  void loadSearch() {
    emit(TeamInviteSearchStart());
  }
}

enum SearchMode { userName, mobile, gameProfileId }
