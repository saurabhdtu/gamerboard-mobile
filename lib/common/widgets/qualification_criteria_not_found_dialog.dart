import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/utils/graphql_ext.dart';

import 'buttons.dart';
import 'text.dart';
import '../../graphql/query.dart';
import '../../resources/colors.dart';
import '../../resources/strings.dart';
import '../../feature/home/home_bloc.dart';

class QualificationCriteriaNotMatchDialog extends StatelessWidget {
  final List<TournamentMixin$CustomQualificationRules>? qualifiers;
  final CheckUserTournamentQualification$Query$TournamentQualificationResult
      qualificationResult;
  final CustomQualificationRuleTypes ruleType;
  final String? tournamentName;
  final Function onClickViewLeaderboard;
  final bool showPrimaryAction;

  QualificationCriteriaNotMatchDialog(
      this.qualifiers, this.ruleType, this.qualificationResult,
      {this.tournamentName,
      required this.onClickViewLeaderboard,
      this.showPrimaryAction = true});

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
                      child: Icon(
                        Icons.close,
                        color: Color(0xffCCCCCC),
                        size: 24,
                      ))
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
              const SizedBox(height: 18.0),
              ruleType.getQualifierResultTitle(
                  qualificationResult.rules.first.current,
                  qualificationResult.rules.first.required),
              Visibility(
                  visible: ruleType.getProgressByIndicatorStatus(),
                  child: Column(children: [
                    const SizedBox(height: 22.0),
                    Container(
                        width: (width * 0.55),
                        child: Stack(children: [
                          Container(
                            width: (width * 0.55),
                            height: 5,
                            decoration: BoxDecoration(
                                color: Color(0xff353942),
                                borderRadius: BorderRadius.circular(0)),
                          ),
                          Container(
                              width: (width * 0.55) *
                                  (qualificationResult.rules.first.current /
                                      qualificationResult.rules.first.required),
                              height: 5,
                              decoration: BoxDecoration(
                                  color: AppColor.buttonActive,
                                  borderRadius: BorderRadius.circular(0)))
                        ]))
                  ])),
              const SizedBox(height: 8.0),
              RegularText(
                ruleType.getQualifierSubTitle(
                    qualificationResult.rules.first.current,
                    qualificationResult.rules.first.required),
                color: AppColor.grayTextC6C6C6,
                fontSize: 10,
              ),
              const SizedBox(height: 16.0),
              Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                Expanded(
                  child: primaryBorderButton(AppStrings.gotIt, () {
                    Navigator.of(context).pop();
                  }),
                ),
                if (showPrimaryAction)
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.only(left: 16),
                      child: secondaryButton(AppStrings.viewLeaderboard, () {
                        Navigator.of(context).pop();
                        onClickViewLeaderboard.call();
                      },
                          paddingHorizontal: 88,
                          textColor: AppColor.whiteTextColor),
                    ),
                  )
              ]),
              const SizedBox(height: 10.0),
            ]));
  }
}
