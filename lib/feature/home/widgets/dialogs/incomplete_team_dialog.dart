import 'package:flutter/material.dart';

import '../../../../common/widgets/buttons.dart';
import '../../../../common/widgets/text.dart';
import '../../../../resources/colors.dart';
import '../../../../resources/strings.dart';
import '../../home_bloc.dart';

class InCompleteTeamDialog extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return ColoredBox(
        color: Colors.transparent,
        child: Row(children: [
          const Spacer(),
          Container(
              decoration: BoxDecoration(boxShadow: [
                BoxShadow(
                    color: AppColor.buttonSecondary.withAlpha(40),
                    blurRadius: 5.0,
                    blurStyle: BlurStyle.outer,
                    spreadRadius: 5.0)
              ], gradient: AppColor.popupBackgroundGradient),
              padding: const EdgeInsets.all(16.0),
              child: Column(
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.center,
                  children: [
                    RegularText(AppStrings.gbTeamRules, fontSize: 16.0),
                    const SizedBox(height: 16.0),
                    DecoratedBox(
                        decoration:
                        BoxDecoration(color: AppColor.darkBackground),
                        child: Padding(
                            padding: const EdgeInsets.all(20.0),
                            child:
                            RegularText(AppStrings.playWithCompleteTeam))),
                    const SizedBox(height: 15.0),
                    secondaryButton(AppStrings.play, () {
                      Navigator.of(context).pop();
                    }, paddingHorizontal: 70.0)
                  ])),
          const Spacer()
        ]));
  }
}