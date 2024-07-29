import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/bloc/application/application_bloc.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/widgets/qualification_criteria_not_found_dialog.dart';
import 'package:gamerboard/common/widgets/qualifier_for_tournament_dialog.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/game/tournament_bloc.dart';
import 'package:gamerboard/feature/game/tournament_states.dart';
import 'package:gamerboard/feature/game/tournament_widgets.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/invite_code_input_dialog.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/mobile_input_for_idp_dialog.dart';

import '../../utils/ui_utils.dart';

////Created by saurabh.lahoti on 25/12/21

class TournamentPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _TournamentState();
}

class _TournamentState extends State<TournamentPage> {
  late TournamentBloc _tournamentBloc;
  late TournamentWidgets _tournamentWidgets;
  bool showSquadDetail = false;

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width;
    return WillPopScope(
        child: Stack(children: [
          BlocConsumer<TournamentBloc, TournamentState>(
              builder: (ctx, state) {
                if (state is TournamentLoading)
                  return appScaffold(body: appCircularProgressIndicator());
                else if (state is LeaderboardState) {
                  int index = 0;
                  for (int i = 0; i < state.tournaments.length; i++) {
                    var element = state.tournaments[i];
                    if (element.tournament.id ==
                        _tournamentBloc.currentTournament.tournament.id)
                      index = i;
                  }
                  return DefaultTabController(
                      length: state.tournaments.length,
                      initialIndex: index,
                      child: appScaffold(
                          appBar: appBar(context, "",
                              titleWidget: state.tournaments.length == 1
                                  ? BoldText(
                                      state.tournaments.first.tournament.name,
                                      fontSize: 22.0)
                                  : _tournamentWidgets
                                      .getTabBar(state.tournaments),
                              backAction: onBackPressed),
                          body: Row(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                _tournamentWidgets.getLeftPane(
                                    width * 0.7, state),
                                SizedBox(width: 10.0),
                                _tournamentWidgets.getRightPane(
                                    (width * 0.3) - 12, state, context)
                              ])));
                }
                return SizedBox.shrink();
              },
              listener: (ctx, state) async {
                if (state is CustomTournamentQualifiedState) {
                  await UiUtils.getInstance.showCustomDialog(
                      context,
                      MobileInputForIDPDialog(
                        _tournamentBloc.currentTournament,
                        _tournamentBloc.user!.phone!,
                        (tournament, pageType, phone) {
                          _tournamentBloc.joinTournament(
                              context, state.tournament,
                              phoneNumber: phone);
                        },
                        1,
                      ),
                      dismissible: false);
                } else if (state is ShowInviteDialogAlert) {
                  await UiUtils.getInstance.showCustomDialog(context,
                      InviteCodeInputDialog(onSubmitInviteCode: (joinCode) {
                    _tournamentBloc.joinTournament(context, state.tournament,
                        phoneNumber: state.phone, joinCode: joinCode);
                  }), dismissible: false);
                } else if (state is QualifiedForTournament) {
                  if (state.qualificationResult.qualified) {
                    await UiUtils.getInstance.showCustomDialog(
                        context,
                        QualifiedForTournamentDialog(
                          state.tournament.qualifiers,
                          state.ruleType,
                          state.qualificationResult,
                          tournamentName: state.tournamentName,
                          onClickJoin: () {
                            _tournamentBloc.joinTournament(
                                context, state.tournament);
                          },
                        ),
                        dismissible: false);
                  } else {
                    await UiUtils.getInstance.showCustomDialog(
                        context,
                        QualificationCriteriaNotMatchDialog(
                          state.tournament.qualifiers,
                          state.ruleType,
                          state.qualificationResult,
                          tournamentName: state.tournamentName,
                          showPrimaryAction: false,
                          onClickViewLeaderboard: () {},
                        ),
                        dismissible: false);
                  }
                }
              },
              listenWhen: (p, c) =>
                  c is QualifiedForTournament ||
                  c is CustomTournamentQualifiedState ||
                  c is ShowInviteDialogAlert,
              buildWhen: (previous, current) =>
                  current is TournamentLoading ||
                  current is LeaderboardState ||
                  current is TournamentError,
              bloc: _tournamentBloc),
          BlocBuilder<TournamentBloc, TournamentState>(
            builder: (context, state) {
              if (state is LoadSquadDetails) {
                showSquadDetail = state.show;
                if (showSquadDetail)
                  return Align(
                      child: SquadDetail(_tournamentBloc, context),
                      alignment: Alignment.centerRight);
              }
              return const SizedBox.shrink();
            },
            buildWhen: (p, c) => c is LoadSquadDetails,
            bloc: _tournamentBloc
          )
        ]),
        onWillPop: () => onBackPressed());
  }

  Future<bool> onBackPressed() {
    if (showSquadDetail) {
      _tournamentBloc.loadSquadDetails(false);
    } else if (checkForRoute(Routes.HOME_PAGE)) {
      Navigator.of(context).pop(_tournamentBloc.tournamentJoinedDelta);
    } else {
      Navigator.popAndPushNamed(context, Routes.HOME_PAGE);
    }
    return Future.value(false);
  }

  @override
  void initState() {
    super.initState();
    _tournamentBloc = context.read<TournamentBloc>();
    _tournamentBloc.loadData(
        forceLoad: true,
        currentGame: context.read<ApplicationBloc>().userCurrentGame);
    _tournamentWidgets = TournamentWidgets(_tournamentBloc);
  }
}
