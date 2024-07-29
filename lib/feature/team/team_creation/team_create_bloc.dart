import 'package:flutter/cupertino.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/repository/mutation_repo.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/feature/team/team_creation/page_state.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_states.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/time_utils.dart';
import 'package:gamerboard/utils/ui_utils.dart';

////Created by saurabh.lahoti on 17/03/22
class TeamCreateBloc extends Cubit<TeamCreateState> {
  late UserMixin user;
  int currentPage = 0;
  int totalPages = 3;
  bool onlyPayment, fromTournament;
  UserTournamentMixin tournamentMixin;
  SquadMixin? squad;
  String? teamName;
  String? inviteCode;
  String? phoneNumber;
  String? joinCode;

  TeamCreateBloc(this.tournamentMixin,
      {this.inviteCode,
      this.onlyPayment = false,
      this.fromTournament = false,
      this.joinCode,
      this.phoneNumber})
      : super(TeamCreateLoading()) {
    if (onlyPayment) {
      currentPage = 1;
    }
    totalPages = showUseJoinCode()  ? 4 : 3;
  }

  bool showUseJoinCode(){
    return tournamentMixin.tournament.joinCode != null &&  !onlyPayment;
  }

  init() async {
    var res = await QueryRepository.instance.getMyProfile(getCached: true);
    user = res.data!.me;
    _emitState(TeamCreateLoaded(currentPage));
    _emitState(JoinTournamentStateChange(false));
    _emitState(ShowTournamentLoader(false));
  }

  void submitTeamName(BuildContext buildContext, String teamName) async {
    this.teamName = teamName;
    forward(buildContext);
    if (squad != null)
      MutationRepository.instance.updateSquad(squad!.id, teamName);

    /*UiUtils.getInstance.buildLoading(buildContext, dismissible: false);
    try {
        final res = await MutationRepository.instance.enterTournament(
            tournamentMixin.tournament.id,
            squadInfo: TournamentJoiningSquadInfo(name: teamName));
        if (res.hasErrors) throw res.errors!.first.message;
        // squad = res.data!.;
      }
      forward(buildContext);
    } catch (ex) {
      UiUtils.getInstance.showToast(ex.toString());
    }
    Navigator.of(buildContext).pop();*/
  }

  back(BuildContext buildContext) {
    if (onlyPayment || currentPage == 0) {
      // if (squad != null) MutationRepository.instance.deleteSquad(squad!.id);
      Navigator.of(buildContext).pop(currentPage > 1);
    } else {
      currentPage--;
      _emitState(TeamCreationPageChange(currentPage));
    }
  }

  void forward(BuildContext buildContext) async {
    var lastPage = currentPage;
    currentPage++;
    var lastPageState = getPageState(lastPage);
    if (lastPageState == TeamCreatePageState.teamName) {
      if (tournamentMixin.tournament.fee == 0) {
        await joinTournament(buildContext, "zero_fee_auto_join");
      }
    } else if (lastPageState == TeamCreatePageState.teamInfo) {
      openTournament(buildContext);
    }
    _emitState(TeamCreationPageChange(currentPage));
  }

  void openTournament(BuildContext buildContext) {
    Navigator.of(buildContext).pop(true);
  }

  Future<void> joinTournament(BuildContext context, String from,
      {String? phoneNumber}) async {
    if (phoneNumber == null) {
      phoneNumber = this.phoneNumber;
    }
    if (phoneNumber == null &&
        tournamentMixin.tournament.matchType == MatchType.headToHead) {
      _emitState(MobileInputForIDP());
    } else {
      _emitState(JoinTournamentStateChange(true));
      _emitState(ShowTournamentLoader(true));

      var tournamentResponse = await MutationRepository.instance
          .enterTournament(tournamentMixin.tournament.id,
              squadInfo: TournamentJoiningSquadInfo(
                  inviteCode: inviteCode, name: teamName),
              phoneNumber: phoneNumber, joinCode: joinCode);
      _emitState(ShowTournamentLoader(false));
      if (tournamentResponse.hasErrors) {
        UiUtils.getInstance.showToast(tournamentResponse.errors!.first.message);
        //currentPage = 1;
        _emitState(JoinTournamentStateChange(false));
      } else {
        squad = tournamentResponse.data?.enterTournament?.squad;
        AnalyticService.getInstance().trackEvents(Events.LB_JOINED, properties: {
          "id": tournamentMixin.tournament.id,
          "from": from,
          "fee": tournamentMixin.tournament.fee,
          "tournament_type":
              tournamentMixin.tournament.matchType.getMatchTypeName(),
          "group": tournamentMixin.tournamentGroup().name(),
          "duration": TimeUtils.instance.durationInDays(
              tournamentMixin.tournament.startTime,
              tournamentMixin.tournament.endTime)
        });

        currentPage++;
        _emitState(JoinTournamentStateChange(false));
        _emitState(TeamCreationPageChange(currentPage));
        tournamentMixin.joinedAt = DateTime.now();
      }
    }
  }

  _emitState(TeamCreateState state) {
    if (!isClosed) emit(state);
  }

  void addFunds(BuildContext context) async {
    await Navigator.of(context).pushNamed(Routes.WALLET);
    init();
  }

  submitJoinCode(BuildContext context, String joinCode) {
    this.joinCode = joinCode;
    forward(context);
  }


  TeamCreatePageState getPageState(int currentPage){
    final isJoinCodeAvailable = showUseJoinCode();
    if(currentPage == 0){
      if(isJoinCodeAvailable){
        return TeamCreatePageState.inviteOnly;
      }
      return TeamCreatePageState.teamName;
    }
    if(currentPage == 1 && isJoinCodeAvailable){
      return TeamCreatePageState.teamName;
    }

    var paymentPageIndex = isJoinCodeAvailable ? 2 : 1;
    var paymentSuccessPageIndex = isJoinCodeAvailable ? 3 : 2;

    if(paymentPageIndex == currentPage){
      return TeamCreatePageState.payment;
    }
    if(paymentSuccessPageIndex == currentPage){
      return TeamCreatePageState.paymentSuccess;
    }
    return TeamCreatePageState.teamInfo;
  }

}
