import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:lottie/lottie.dart';

import 'buttons.dart';
import 'text.dart';
import '../../graphql/query.graphql.dart';
import '../../resources/colors.dart';
import '../../resources/strings.dart';
import '../../feature/home/home_bloc.dart';

class QualifiedForTournamentDialog extends StatelessWidget {
  final List<TournamentMixin$CustomQualificationRules>? qualifiers;
  final CustomQualificationRuleTypes ruleType;
  final CheckUserTournamentQualification$Query$TournamentQualificationResult
      qualificationResult;
  final String? tournamentName;
  final Function onClickJoin;
  QualifiedForTournamentDialog(
      this.qualifiers,  this.ruleType, this.qualificationResult,
      {this.tournamentName, required this.onClickJoin});

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width * 0.65;
    return Container(
        decoration: BoxDecoration(boxShadow: [
          BoxShadow(
              color: AppColor.blackColor3E3E3E,
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
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const SizedBox(width: 24),
                  Text(AppStrings.exclusiveTournaments,
                      style:
                          SemiBoldTextStyle(color: Colors.white, fontSize: 16)),
                  InkWell(
                      onTap: () {
                        Navigator.of(context).pop();
                      },
                      child: const Icon(Icons.close,
                          color: AppColor.grayIconCCCCCC, size: 24))
                ],
              ),
              const SizedBox(height: 8.0),
              RegularText(
                  ruleType.getQualifierTitle(
                      qualificationResult.rules.first.required.toString(),
                      qualifiers!.first.value,
                      tournamentName),
                  color: AppColor.grayTextC6C6C6,
                  fontSize: 12),
              const SizedBox(height: 16.0),
              RegularText(AppStrings.congratulationsYouAreEligible,
                  fontSize: 20.0,
                  color: AppColor.successGreen,
                  textAlign: TextAlign.center),
              const SizedBox(height: 16.0),
              LottieBuilder.asset("${lottieAssets}tick.json",
                  height: 56, width: 56),
              const SizedBox(height: 16.0),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  primaryBorderButton(AppStrings.gotIt, () {
                    Navigator.of(context).pop();
                  }),
                  const SizedBox(width: 16),
                  secondaryButton(AppStrings.join, () {
                    Navigator.of(context).pop();
                    onClickJoin();
                  }, paddingHorizontal: 88, textColor: AppColor.whiteTextColor)
                ],
              ),
              const SizedBox(height: 10.0),
            ]));
  }
}
