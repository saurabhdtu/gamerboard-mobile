import 'package:flutter/material.dart';

import '../../../../common/widgets/buttons.dart';
import '../../../../common/widgets/text.dart';
import '../../../../resources/colors.dart';
import '../../../../resources/strings.dart';
import '../../../../common/services/analytics/analytic_utils.dart';
import '../../../../utils/reward_utils.dart';
import '../../home_bloc.dart';

class RewardDialog extends StatelessWidget {
  final RewardType rewardType;

  RewardDialog(this.rewardType);

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width * 0.55;
    return Container(
        decoration: BoxDecoration(boxShadow: [
          BoxShadow(
              color: AppColor.dividerColor,
              blurRadius: 5.0,
              blurStyle: BlurStyle.outer,
              spreadRadius: 5.0)
        ], color: AppColor.dividerColor),
        width: width,
        padding: const EdgeInsets.all(10.0),
        child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  InkWell(
                      onTap: () {
                        AnalyticService.getInstance().trackEvents(
                            Events.REWARD_POPUP_DISMISSED,
                            properties: {
                              "source": rewardType.rewardSource(),
                            });
                        Navigator.of(context).pop();
                      },
                      child: Icon(
                        Icons.close,
                        color: AppColor.grayIconCCCCCC,
                      ))
                ],
              ),
              DecoratedBox(
                  decoration:
                  BoxDecoration(borderRadius: BorderRadius.circular(20.0)),
                  child: Padding(
                      padding: const EdgeInsets.symmetric(
                          vertical: 8.0, horizontal: 15.0),
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 16, vertical: 8),
                        decoration: BoxDecoration(
                            color: Colors.black,
                            borderRadius: BorderRadius.circular(12)),
                        child: Row(
                            mainAxisSize: MainAxisSize.min,
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(Icons.check_circle_sharp,
                                  color: AppColor.successGreen, size: 24.0),
                              const SizedBox(width: 8.0),
                              RegularText(rewardType.getDialogTitle())
                            ]),
                      ))),
              const SizedBox(height: 15.0),
              RegularText(
                "${AppStrings.rupeeSymbol}${rewardType.rewardAmount().toInt()}",
                fontSize: 48.0,
                color: AppColor.successGreen,
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 6.0),
              BoldText(
                AppStrings.rewardReceived,
                color: AppColor.textSubTitle,
                fontSize: 18,
              ),
              const SizedBox(height: 18.0),
              RegularText(
                AppStrings.bonusCash,
                color: AppColor.textSubTitle,
                fontSize: 14,
              ),
              const SizedBox(height: 6.0),
              RegularText(
                AppStrings.useIfNowToPlay,
                color: AppColor.grayText8F8F90,
                fontSize: 12,
              ),
              const SizedBox(height: 10.0),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  primaryBorderButton("Got It", () {
                    AnalyticService.getInstance().trackEvents(
                        Events.REWARD_POPUP_DISMISSED,
                        properties: {
                          "source": RewardType.THREE_KILLS.rewardSource(),
                        });
                    Navigator.of(context).pop();
                  }),
                  const SizedBox(
                    width: 16,
                  ),
                  secondaryButton("Explore Tournaments", () {
                    AnalyticService.getInstance().trackEvents(
                        Events.REWARD_POPUP_DISMISSED,
                        properties: {
                          "source": RewardType.THREE_KILLS.rewardSource(),
                        });
                    Navigator.of(context).pop();
                  })
                ],
              ),
              const SizedBox(height: 10.0),
            ]));
  }
}
