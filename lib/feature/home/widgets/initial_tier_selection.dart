import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/find_game_tier_dialog.dart';
import 'package:gamerboard/feature/home/widgets/tier_card.dart';

import '../../../common/widgets/text.dart';
import '../../../resources/colors.dart';
import '../../../resources/strings.dart';
import '../../../utils/graphql_ext.dart';
import '../../../utils/ui_utils.dart';
import '../home_bloc.dart';

class InitialTierSelection extends StatefulWidget {
  final GameTeamGroup teamGroup;
  final Enum? selectedLevel;
  final double width;
  final double height;
  final HomeBloc homeBloc;
  InitialTierSelection( this.teamGroup, this.selectedLevel,
      this.width, this.height,this.homeBloc);

  @override
  State<InitialTierSelection> createState() => _InitialTierSelectionState();
}

class _InitialTierSelectionState extends State<InitialTierSelection> {
  late Map<Enum, String> selectionMapGame;
  int? indexSelected;
  onItemSelected(int index) {
    setState(() {
      indexSelected = index;
      widget.homeBloc.selectTier(MapEntry(
          selectionMapGame.keys.elementAt(index),
          selectionMapGame.values.elementAt(index)));
    });
  }

  @override
  void initState() {
    super.initState();
    SchedulerBinding.instance.addPostFrameCallback((_) {
      selectionMapGame =
          widget. homeBloc.applicationBloc.userCurrentGame.getTierMap();
    });

  }

  @override
  Widget build(BuildContext context) {
    return Container(
        padding: const EdgeInsets.all(12),
        child: Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
            RichText(
                text: TextSpan(
                    text: AppStrings.firstSelectYourCurrent,
                    style: SemiBoldTextStyle(color: AppColor.whiteTextColor),
                    children: [
                      TextSpan(
                          text:
                          " ${widget.homeBloc.applicationBloc.userCurrentGame.getShortName()} ${widget.homeBloc.teamGroupForIndex().name()} Tier",
                          style: SemiBoldTextStyle(
                            color: AppColor.successGreen,
                          ))
                    ])),
            InkWell(
                onTap: () {
                  final width = MediaQuery.of(context).size.width / 1.8;
                  final height = MediaQuery.of(context).size.height;
                  UiUtils.getInstance.showCustomDialog(
                      context,
                      Container(
                          width: width,
                          height: height,
                          child: FindYourGameTier(width,widget.homeBloc)),
                      dismissible: true);
                },
                child: Row(children: [
                  Image.asset("${imageAssets}ic_gb_help.png",
                      height: 14, width: 14),
                  Text(
                    AppStrings.howToFindMyTier,
                    style: RegularTextStyle(
                        color: AppColor.whiteTextColor, fontSize: 12),
                  )
                ]))
          ]),
          const SizedBox(height: 8.0),
          Text(AppStrings.selectedTierDoesNotMatch,
              style: RegularTextStyle(
                  color: AppColor.grayText9E9E9E, fontSize: 11)),
          const SizedBox(height: 8.0),
          Expanded(
              child: ListView.separated(
                  shrinkWrap: true,
                  itemBuilder: (context, index) => Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: getTierCards(
                          widget.width,
                          index,
                          indexSelected,
                          onItemSelected,
                          false,
                          widget.homeBloc)),
                  separatorBuilder: (context, index) =>
                  const SizedBox(height: 8.0),
                  itemCount: 2))
        ]));
  }
}