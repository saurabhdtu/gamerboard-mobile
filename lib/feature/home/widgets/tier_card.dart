
import 'package:flutter/material.dart';

import '../../../common/constants.dart';
import '../../../common/widgets/text.dart';
import '../../../graphql/query.graphql.dart';
import '../../../resources/colors.dart';
import '../../../resources/strings.dart';
import '../../../utils/graphql_ext.dart';
import '../home_bloc.dart';

List<Widget> getTierCards(double width, int index, int? selectedIndex,
    Function onItemSelection, bool isForDialog, HomeBloc homeBloc) {
  List<Widget> list = [];

  final selectionMapName =
  homeBloc.applicationBloc.userCurrentGame == ESports.bgmi
      ? GameLevels.BGMI_LEVEL
      : GameLevels.FFMAX_LEVEL;

  final selectionMap = homeBloc.applicationBloc.userCurrentGame == ESports.bgmi
      ? {
    BgmiLevels.bronzeFive: "Bronze (I-V)",
    BgmiLevels.silverFive: "Silver (I-V)",
    BgmiLevels.goldFive: "Gold (I-V)",
    BgmiLevels.platinumFive: "Platinum (I-V)",
    BgmiLevels.diamondFive: "Diamond (I-V)",
    BgmiLevels.crownFive: "Crown (I-V)",
    BgmiLevels.ace: "Ace (Ace, Master, Dominator)",
    BgmiLevels.conqueror: "Conqueror"
  }
      : {
    FfMaxLevels.bronzeThree: "Bronze (I-III)",
    FfMaxLevels.silverThree: "Silver (I-III)",
    FfMaxLevels.goldFour: "Gold (I-IV)",
    FfMaxLevels.platinumFour: "Platinum (I-IV)",
    FfMaxLevels.diamondFour: "Diamond (I-IV)",
    FfMaxLevels.heroic: "Heroic",
    FfMaxLevels.master: "Master",
    FfMaxLevels.grandmasterSix: "GrandMaster (I-VI)"
  };

  final selectionMapRank =
  homeBloc.applicationBloc.userCurrentGame == ESports.bgmi
      ? GameRanks.BGMI_RANK
      : GameRanks.FFMAX_RANK;

  int startIndex = index * 4;

  for (int i = startIndex; i < startIndex + 4; i++) {
    list.add(
        InkWell(
            onTap: () {
              onItemSelection(i);
            },
            child: Container(
                margin: EdgeInsets.symmetric(horizontal: isForDialog ? 4 : 12),
                decoration: BoxDecoration(
                    color: Color(0xff121212),
                    border: Border.all(
                      color: selectedIndex == i
                          ? AppColor.buttonSecondary
                          : AppColor.screenBackground,
                    )),
                width: isForDialog ? (width / 8) : (width / 5),
                child: Container(
                    padding: const EdgeInsets.symmetric(vertical: 12),
                    child: Column(children: [
                      Stack(children: [
                        Align(
                            alignment: Alignment.center,
                            child: Image.network(
                              getTierImage(selectionMap.entries.elementAt(i).key),
                              height: 82,
                            )),
                        Visibility(
                            visible: selectedIndex == i,
                            child: Padding(
                                padding: const EdgeInsets.only(right: 8),
                                child: Align(
                                    alignment: Alignment.topRight,
                                    child: Image.asset(
                                        "${imageAssets}ic_select.png",
                                        scale: 2.5))))
                      ]),
                      Column(
                          crossAxisAlignment: CrossAxisAlignment.center,
                          children: [
                            Text(selectionMapName[i],
                                style: SemiBoldTextStyle(fontSize: 16)),
                            Text(selectionMapRank[i],
                                textAlign: TextAlign.center,
                                style: RegularTextStyle(
                                    color: AppColor.grayText9E9E9E, fontSize: 10))
                          ])
                    ])))));
  }
  return list;
}