import 'package:flutter/material.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:sprintf/sprintf.dart';

import '../../../../common/models/deeplink_data.dart';
import '../../../../common/router.dart';
import '../../../../common/widgets/buttons.dart';
import '../../../../common/widgets/containers.dart';
import '../../../../common/widgets/text.dart';
import '../../../../graphql/query.graphql.dart';
import '../../../../resources/colors.dart';
import '../../../../resources/strings.dart';
import '../../../../utils/time_utils.dart';
import '../../home_bloc.dart';

class SquadInviteDialog extends StatefulWidget {
  final UserTournamentMixin userTournamentMixin;
  final DeeplinkData deeplinkData;
  final HomeBloc homeBloc;
  final bool teamSwitch;

  SquadInviteDialog(this.userTournamentMixin, this.deeplinkData, this.homeBloc,
      this.teamSwitch);

  @override
  State<StatefulWidget> createState() => _SquadInviteState();
}

class _SquadInviteState extends State<SquadInviteDialog> {
  bool isJoined = false;

  @override
  Widget build(BuildContext context) {
    return themeContainer(
        !isJoined
            ? Column(children: [
                BoldText(
                    widget.teamSwitch
                        ? sprintf(AppStrings.errorTeamOther, [
                            widget.userTournamentMixin.squad!.name,
                            widget.deeplinkData.teamName
                          ])
                        : sprintf(AppStrings.inviteTitle, [
                            widget.deeplinkData.userName,
                            widget.userTournamentMixin.tournament.name
                          ]),
                    fontSize: 16.0,
                    textAlign: TextAlign.center),
                const Spacer(),
                ListView(shrinkWrap: true, children: _items()),
                const Spacer(),
                secondaryButton(AppStrings.acceptInvite, () async {
                  final res = await widget.homeBloc.joinSquad(
                      context, widget.userTournamentMixin, widget.teamSwitch,
                      inviteCode: widget.deeplinkData.inviteCode,
                      teamId: widget.deeplinkData.teamId);
                  if (res) {
                    widget.homeBloc.refreshPage();
                   Navigator.of(context).pop();
                  }
                })
              ])
            : Column(crossAxisAlignment: CrossAxisAlignment.center, children: [
                iconText(
                    Icon(Icons.check_circle_outline,
                        color: AppColor.successGreen, size: 28.0),
                    RegularText(AppStrings.joinedTeam)),
                const SizedBox(height: 10.0),
                RegularText(AppStrings.payForYourself, fontSize: 16.0),
                const SizedBox(height: 8.0),
                BoldText(
                    "${AppStrings.rupeeSymbol}${widget.userTournamentMixin.tournament.fee}",
                    fontSize: 40.0),
                const SizedBox(height: 5.0),
                Divider(
                    height: 1.0, thickness: 1, color: AppColor.dividerColor),
                const SizedBox(height: 10.0),
                RegularText(AppStrings.payYourShare,
                    color: AppColor.textDarkGray, fontSize: 16.0),
                const Spacer(),
                secondaryButton(AppStrings.payNow, () async {
                  var result = await navigateToCreateTeam(
                      context, widget.userTournamentMixin, false, true,
                      inviteCode: widget.deeplinkData.inviteCode);
                  if (result == true) {
                    widget.homeBloc.refreshPage();
                    Navigator.of(context).pop();
                  }
                }),
                const SizedBox(height: 10.0),
                const Spacer(),
                InkWell(
                    onTap: () => Navigator.of(context).pop(),
                    child: Padding(
                        padding: const EdgeInsets.all(8.0),
                        child: BoldText(AppStrings.later,
                            color: AppColor.textHighlighted, fontSize: 16.0))),
                const SizedBox(height: 10.0)
              ]),
        padding: const EdgeInsets.symmetric(horizontal: 30.0, vertical: 20.0),
        width: MediaQuery.of(context).size.width * 0.6,
        height: MediaQuery.of(context).size.width * 0.8);
  }

  Column _listItem(String title1, String description1, String title2,
          String description2) =>
      Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        Row(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                  child: RichText(
                      text: TextSpan(style: RegularTextStyle(), children: [
                TextSpan(text: title1, style: BoldTextStyle()),
                TextSpan(text: description1)
              ]))),
              const SizedBox(width: 10.0),
              Expanded(
                  child: RichText(
                      text: TextSpan(style: RegularTextStyle(), children: [
                TextSpan(text: title2, style: BoldTextStyle()),
                TextSpan(text: description2)
              ])))
            ]),
        const SizedBox(height: 10.0)
      ]);

  List<Widget> _items() => [
        _listItem(
            "Maps: ",
            widget.userTournamentMixin.getAllowedMaps(),
            "Fee: ",
            AppStrings.rupeeSymbol +
                widget.userTournamentMixin.tournament.fee.toString()),
        _listItem(
            "Mode: ",
            widget.userTournamentMixin.getAllowedGameMode(),
            "Starts: ",
            TimeUtils.instance.formatCardDateTime(
                widget.userTournamentMixin.tournament.startTime)),
        _listItem(
            "Tier: ",
            widget.userTournamentMixin.allowedTierString(),
            "Duration: ",
            TimeUtils.instance.duration(
                widget.userTournamentMixin.tournament.startTime,
                widget.userTournamentMixin.tournament.endTime))
      ];
}
