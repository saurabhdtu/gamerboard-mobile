import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/bloc/application/application_bloc.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/team/team_invite/team_invite_bloc.dart';
import 'package:gamerboard/feature/team/team_invite/team_invite_widgets.dart';

import '../../../resources/strings.dart';

////Created by saurabh.lahoti on 09/03/22

class TeamInvite extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _TeamInviteState();
}

class _TeamInviteState extends State<TeamInvite> {
  late TeamInviteWidgets _teamInviteWidgets;
  late TeamInviteBloc _teamInviteBloc;

  @override
  Widget build(BuildContext context) => DefaultTabController(
      length: 3,
      child: appScaffold(
          appBar: appBar(context, '',
              titleWidget: _teamInviteWidgets.tabBar(),
              actions: [
                Center(
                    child: InkWell(
                        child: Padding(
                            padding: const EdgeInsets.symmetric(
                                horizontal: 35.0, vertical: 8.0),
                            child: RegularText(AppStrings.skip)),
                        onTap: () => Navigator.of(context).pop(true)))
              ]),
          body: TabBarView(children: [
            _teamInviteWidgets.inviteCode(),
            RecentPlayerView(_teamInviteWidgets),
            SearchPlayerView(_teamInviteWidgets)
          ])));

  @override
  void initState() {
    super.initState();
    _teamInviteBloc = context.read<TeamInviteBloc>();
    _teamInviteBloc.currentEsport = context.read<ApplicationBloc>().userCurrentGame;
    _teamInviteWidgets = TeamInviteWidgets(context, _teamInviteBloc);
  }
}
