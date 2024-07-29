import 'package:gamerboard/graphql/custom.dart';

////Created by saurabh.lahoti on 14/03/22
abstract class TeamInviteState {}

class TeamInviteLoading extends TeamInviteState {}

class TeamInviteSearchStart extends TeamInviteState {}

class TeamInviteSearching extends TeamInviteState {}

class TeamInviteSearchResult extends TeamInviteState {
  List<UserSearchResult> searchResult;

  TeamInviteSearchResult(this.searchResult);
}

class RecentLoading extends TeamInviteState {}

class RecentLoaded extends TeamInviteState {
  List<UserSearchResult> results;

  RecentLoaded(this.results);
}
