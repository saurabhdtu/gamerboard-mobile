import 'package:flutter/cupertino.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/repository/mutation_repo.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/feature/team/team_join_create/team_join_create_states.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/time_utils.dart';
import 'package:gamerboard/utils/ui_utils.dart';

////Created by saurabh.lahoti on 28/03/22
class TeamJoinCreateBloc extends Cubit<TeamJoinCreateState> {
  final UserTournamentMixin tournamentMixin;
  final bool fromTournamentDetail;
  final String? phoneNumber;

  TeamJoinCreateBloc(this.tournamentMixin, this.fromTournamentDetail,
      {this.phoneNumber})
      : super(LoadProfile(true));

  void _emitState(TeamJoinCreateState state) {
    if (!isClosed) emit(state);
  }

  void init(bool cached) async {
    final profile =
        await QueryRepository.instance.getMyProfile(getCached: cached);
    if (profile.data != null && profile.data?.me != null)
      _emitState(ProfileLoaded(profile.data!.me));
    else
      _emitState(LoadProfile(false));
  }

  void joinSquad(BuildContext context, String groupCode) async {
/*
    UiUtils.getInstance.buildLoading(context, dismissible: true);
    var response = await MutationRepository.instance.enterTournament(
        tournamentMixin.tournament.id,
        squadInfo: TournamentJoiningSquadInfo(inviteCode: groupCode),
        phoneNumber: phoneNumber);
    bool? result;
    if (response.hasErrors) {
      result = false;
      UiUtils.getInstance.showToast(response.errors!.first.message);
    } else {

    }
*/

    if (tournamentMixin.tournament.fee == 0) {
      UiUtils.getInstance.buildLoading(context, dismissible: true);
      var response = await MutationRepository.instance
          .enterTournament(tournamentMixin.tournament.id, squadInfo: TournamentJoiningSquadInfo(inviteCode: groupCode));

      if (response.hasErrors) {
        UiUtils.getInstance.showToast(response.errors!.first.message);
      } else{
        Navigator.of(context).pop(true);
      }
      AnalyticService.getInstance().trackEvents(Events.LB_JOINED, properties: {
        "id": tournamentMixin.tournament.id,
        "from": "group_code_auto_join",
        "tournament_type":tournamentMixin.tournament.matchType.getMatchTypeName(),
        "fee": tournamentMixin.tournament.fee,
        "group": tournamentMixin.tournamentGroup().name(),
        "duration": TimeUtils.instance.durationInDays(
            tournamentMixin.tournament.startTime,
            tournamentMixin.tournament.endTime)
      });
    } else {
      Navigator.of(context).pop();
      await navigateToCreateTeam(
          context, tournamentMixin, fromTournamentDetail, true, phoneNumber: phoneNumber, inviteCode: groupCode);
    }
    Navigator.of(context).pop(false);
  }

  void validateSquadCreationInviteCode(String inviteCode) async {
    if (inviteCode.isNotEmpty) {
      _emitState(LoadProfile(true));
      final response = await MutationRepository.instance
          .validateSquadCreationPermission(inviteCode);
      if (response.hasErrors) {
        UiUtils.getInstance.showToast(response.errors!.first.message);
        init(true);
      } else {
        init(false);
      }
    }
  }
}
