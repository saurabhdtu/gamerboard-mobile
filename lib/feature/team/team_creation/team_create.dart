import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/mobile_input_for_idp_dialog.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_bloc.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_states.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_widgets.dart';
import 'package:gamerboard/utils/ui_utils.dart';

////Created by saurabh.lahoti on 16/03/22
class TeamCreate extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _TeamCreateState();
}

class _TeamCreateState extends State<TeamCreate> {
  late TeamCreateBloc _teamCreateBloc;
  late TeamCreateWidgets _teamCreateWidgets;

  @override
  Widget build(BuildContext context) => WillPopScope(
      onWillPop: () => Future.value(_teamCreateBloc.onlyPayment),
      child: appScaffold(
          appBar: appBar(context, "",
              titleWidget: _teamCreateBloc.onlyPayment
                  ? const SizedBox.shrink()
                  : const TopProgressBar(),
              centerTitle: true,
              showBack: _teamCreateBloc.onlyPayment),
          body: Padding(
              padding: const EdgeInsets.symmetric(vertical: 10, horizontal: 20),
              child: SingleChildScrollView(
                  child: BlocConsumer<TeamCreateBloc, TeamCreateState>(
                      builder: (c, state) {
                        int currentPage = -1;
                        if (state is TeamCreateLoaded)
                          currentPage = state.currentPage;
                        else if (state is TeamCreationPageChange)
                          currentPage = state.currentPage;
                        if (currentPage != -1)
                          return Container(
                              child: Row(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                _teamCreateWidgets.getLeftCTA(currentPage),
                                const SizedBox(width: 15.0),
                                Expanded(
                                    child: _teamCreateWidgets
                                        .getContentCard(currentPage)),
                                const SizedBox(width: 15.0),
                                _teamCreateWidgets.getRightCTA(currentPage)
                              ]));
                        else
                          return appCircularProgressIndicator();
                      },
                      bloc: _teamCreateBloc,
                      buildWhen: (p, c) => (c is TeamCreationPageChange ||
                          c is TeamCreateLoaded),
                      listener: (BuildContext context, TeamCreateState state) {
                        if (state is ShowTournamentLoader) {
                          if (!showLoader && state.showLoader) {
                            showLoader = true;
                            UiUtils.getInstance.buildLoading(context);
                          } else if (showLoader && !state.showLoader) {
                            showLoader = false;
                            Navigator.of(context).pop();
                          }
                        }else if (state is MobileInputForIDP) {
                          UiUtils.getInstance.showCustomDialog(
                              context,
                              MobileInputForIDPDialog(
                                  _teamCreateBloc.tournamentMixin,
                                  _teamCreateBloc.user.phone,
                                      (tournament, pageType, text) {
                                    _teamCreateBloc.joinTournament(context, "team_mobile_input_idp",
                                        phoneNumber: text);
                                  }, 0),
                              dismissible: false);
                        }
                      })))));

  bool showLoader = false;

  @override
  void initState() {
    super.initState();
    _teamCreateBloc = context.read<TeamCreateBloc>();
    _teamCreateWidgets = TeamCreateWidgets(_teamCreateBloc, context);
    _teamCreateBloc.init();
  }
}
