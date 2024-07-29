import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/feature/home/widgets/tournament_widget_helper.dart';
import 'package:gamerboard/feature/home/widgets/onboarding_progress_helper.dart';

import '../../../common/widgets/skeleton.dart';
import '../../../common/widgets/text.dart';
import '../../../resources/colors.dart';
import '../home_bloc.dart';
import '../home_page.dart';
import '../home_state.dart';

class HomePageTopBoards extends StatefulWidget {
  final double width, height;
  final bool isHistory;

  HomePageTopBoards(this.width, this.height, {this.isHistory = false});

  @override
  State<StatefulWidget> createState() => _TopBoardState();
}

class _TopBoardState extends State<HomePageTopBoards> {
  late HomeBloc _homeBloc2;

  @override
  Widget build(BuildContext context) {
    return BlocBuilder(
        builder: (context, state) {
          if (state is TopTournamentsLoaded)
            return state.topTournaments.isEmpty
                ? Center(
                    child: RegularText("No tournaments",
                        fontSize: 30.0, color: AppColor.dividerColor))
                : Padding(
                    padding: const EdgeInsets.only(top: 5.0),
                    child: Column(
                      children: [
                        onboardingHelperProgress(_homeBloc2),
                        Flexible(
                          child: TournamentWidgetHelper.getLeaderboardGrid(
                            context,
                              _homeBloc2,
                              widget.isHistory
                                  ? HomeScreenIndex.HISTORY
                                  : HomeScreenIndex.TOP,
                              widget.width,
                              widget.height,
                              state.topTournaments),
                        ),
                      ],
                    ));
          else if (state is TournamentLoading) if (state.showProgress)
            return appCircularProgressIndicator();
          return SizedBox.shrink();
        },
        bloc: _homeBloc2,
        buildWhen: (previous, current) =>
            current is TopTournamentsLoaded || current is TournamentLoading);
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    _homeBloc2 = context.read<HomeBloc>();
    widget.isHistory
        ? _homeBloc2.loadDataOrShowLoader(HomeScreenIndex.HISTORY)
        : _homeBloc2.loadDataOrShowLoader(HomeScreenIndex.TOP);
  }
}
