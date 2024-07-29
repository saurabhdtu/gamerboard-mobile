////Created by saurabh.lahoti on 17/03/22
abstract class TeamCreateState {}

class TeamCreateLoaded extends TeamCreateState {
  int currentPage;
  TeamCreateLoaded(this.currentPage);
}

class TeamCreateLoading extends TeamCreateState {}

class TeamCreationPageChange extends TeamCreateState {
  int currentPage;
  TeamCreationPageChange(this.currentPage);
}

class MobileInputForIDP extends TeamCreateState{

}
class JoinTournamentStateChange  extends TeamCreateState {
  bool isApiCalling;
  JoinTournamentStateChange(this.isApiCalling);
}

class ShowTournamentLoader extends TeamCreateState {
  final bool showLoader;
  ShowTournamentLoader(this.showLoader);
}
class TeamNameSubmitted extends TeamCreateState{
  String? error;
  TeamNameSubmitted(this.error);
}
class JoinCodeSubmitted extends TeamCreateState{
  String? error;
  JoinCodeSubmitted(this.error);
}
class TeamJoining extends TeamCreateState {}

class TeamJoined extends TeamCreateState {}

class TeamCreated extends TeamCreateState {}
