import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/feature/home/widgets/onboarding_progress_helper.dart';

import '../../../common/widgets/blank.dart';
import '../../../common/widgets/skeleton.dart';
import '../../../utils/graphql_ext.dart';
import '../home_bloc.dart';
import '../home_page.dart';
import '../home_state.dart';
import 'initial_tier_selection.dart';
import 'tournament_widget_helper.dart';

class MyBoards extends StatefulWidget {
  final double width, height;
  final GameTeamGroup group;
  final int screen;

  MyBoards(this.width, this.height, this.group, this.screen);

  @override
  State<StatefulWidget> createState() => _MyBoardState();
}

class _MyBoardState extends State<MyBoards> {
  late HomeBloc _homeBloc;

  @override
  Widget build(BuildContext context) {
    return BlocBuilder(
        builder: (ctx, state) {
          if (state is MyTournamentLoaded && state.group == widget.group) {
            return state.activeTournaments.isNotEmpty
                ? Padding(
                    padding: const EdgeInsets.only(top: 5.0),
                    child: Column(children: [
                      onboardingHelperProgress(_homeBloc),
                      Flexible(
                          child: TournamentWidgetHelper.getLeaderboardGrid(
                              context,
                              _homeBloc,
                              widget.screen,
                              widget.width,
                              widget.height,
                              state.activeTournaments))
                    ]))
                : emptyState("No tournaments available");
          } else if (state is TournamentLoading &&
              state.group == widget.group) {
            if (state.showProgress) return appCircularProgressIndicator();
          } else if (state is GameTierInputState &&
              state.group == widget.group) {
            return InitialTierSelection(_homeBloc.teamGroupForIndex(), null,
                widget.width, widget.height, _homeBloc);
          } else if (state is UpdatingBgmiLevel) {
            if (state.showProgress) {
              return Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [Center(child: appCircularProgressIndicator())]);
            }
          }
          return const SizedBox.shrink();
        },
        buildWhen: (previous, current) =>
            (current is MyTournamentLoaded && current.group == widget.group) ||
            (current is GameTierInputState && current.group == widget.group) ||
            (current is TournamentLoading && current.group == widget.group) ||
            (current is GameSelection) ||
            (current is UpdatingBgmiLevel),
        bloc: _homeBloc);
  }

  @override
  void initState() {
    super.initState();
    _homeBloc = context.read<HomeBloc>();
    switch (widget.group) {
      case GameTeamGroup.solo:
        _homeBloc.loadDataOrShowLoader(HomeScreenIndex.SOLO);
        break;
      case GameTeamGroup.duo:
        _homeBloc.loadDataOrShowLoader(HomeScreenIndex.DUO);
        break;
      case GameTeamGroup.squad:
        _homeBloc.loadDataOrShowLoader(HomeScreenIndex.SQUAD);
        break;
    }
  }
}
