import 'package:flutter/material.dart';
import 'package:gamerboard/utils/graphql_ext.dart';

import '../../../../common/widgets/text.dart';
import '../../../../resources/colors.dart';
import '../../../../resources/strings.dart';
import '../../../../utils/share_utils.dart';
import '../../home_bloc.dart';

class FindYourGameTier extends StatefulWidget {
  double width;
  HomeBloc homeBloc;

  FindYourGameTier(this.width, this.homeBloc);

  @override
  State<FindYourGameTier> createState() => _FindYourBGMITierState();
}

class _FindYourBGMITierState extends State<FindYourGameTier> {
  @override
  Widget build(BuildContext context) {
    return Stack(children: [
      Container(
          padding: const EdgeInsets.symmetric(horizontal: 12.0, vertical: 8),
          decoration: BoxDecoration(gradient: AppColor.popupBackgroundGradient),
          child: Column(children: [
            InkWell(
                onTap: () {
                  Navigator.of(context).pop();
                },
                child: Align(
                    alignment: Alignment.topRight,
                    child: Icon(Icons.close, color: Colors.white))),
            Container(
                padding: const EdgeInsets.symmetric(horizontal: 38.0),
                child: Column(children: [
                  BoldText(
                      AppStrings.findYourGameTier(widget
                          .homeBloc.applicationBloc.userCurrentGame
                          .getShortName()),
                      fontSize: 24.0,
                      color: AppColor.textSubTitle),
                  const SizedBox(height: 8.0),
                  Text(
                      AppStrings.submitYourTier(widget
                          .homeBloc.applicationBloc.userCurrentGame
                          .getShortName()),
                      textAlign: TextAlign.center,
                      style: RegularTextStyle(color: AppColor.textSubTitle)),
                  const SizedBox(height: 12.0),
                  Image.asset(
                    "${imageAssets}${widget.homeBloc.applicationBloc.userCurrentGame.getFindTierImage()}",
                    height: 128,
                    width: widget.width,
                  )
                ])),
            Container(
                padding:
                const EdgeInsets.symmetric(horizontal: 38.0, vertical: 8),
                child: Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      InkWell(
                          onTap: () {
                            openUrlInExternalBrowser(
                                widget.homeBloc.applicationBloc.userCurrentGame
                                    .getTierFinder());
                          },
                          child: Container(
                              width: widget.width / 2.8,
                              child: Center(
                                  child: Text(AppStrings.moreInstruction,
                                      style: TextStyle(
                                          color: AppColor.buttonSecondary,
                                          fontSize: 16))),
                              decoration: BoxDecoration(
                                  border: Border.all(
                                      color: AppColor.buttonSecondary)),
                              padding:
                              const EdgeInsets.symmetric(vertical: 8))),
                      InkWell(
                          onTap: () {
                            Navigator.of(context).pop();
                          },
                          child: Container(
                              width: widget.width / 2.8,
                              child: Center(
                                  child: Text("I found it",
                                      style: TextStyle(
                                          color: AppColor.whiteTextColor,
                                          fontSize: 16))),
                              color: AppColor.buttonActive,
                              padding: const EdgeInsets.symmetric(vertical: 8)))
                    ])),
            const Spacer()
          ]))
    ]);
  }
}