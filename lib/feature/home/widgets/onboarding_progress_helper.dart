import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../common/widgets/text.dart';
import '../../../resources/colors.dart';
import '../../../resources/strings.dart';
import '../../../utils/ui_utils.dart';
import '../home_bloc.dart';
import '../home_state.dart';

Widget onboardingHelperProgress(HomeBloc homeBloc) {
  return !homeBloc.isOnboardingSteeperCompleted
      ? BlocBuilder(
      builder: (context, state) {
        if (state is MyTournamentLoaded) {
          bool isJoinedInAny = false;
          if (homeBloc.isAnyTournamentJoin) {
            isJoinedInAny = true;
          } else {
            for(var  tournament in homeBloc.activeTournamentList!)
              for (var element in tournament.tournaments) {
                if (!isJoinedInAny && element.joinedAt != null)
                {
                  isJoinedInAny = true;
                  homeBloc.isAnyTournamentJoin = true;
                }
              }
          }
          return Container(
              height: 80,
              margin: EdgeInsets.fromLTRB(12,5,12,10),
              color: Color(0xff1B1B1C),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      UserOnboardingStep.ON_COMPLETED.getStepWidget1(),
                      Container(
                        width: 136,
                        height: 2,
                        color: isJoinedInAny ? AppColor.successGreen:Colors.white,
                        margin: EdgeInsets.symmetric(horizontal: 12),
                      ),
                      isJoinedInAny
                          ? UserOnboardingStep.ON_COMPLETED.getStepWidget1()
                          : UserOnboardingStep.CURRENT_STEP
                          .getStepWidget1(),
                      Container(
                        width: 136,
                        height: 2,
                        color:  homeBloc.isAnyGamePlayed ? AppColor.successGreen:Colors.white,
                        margin: EdgeInsets.symmetric(horizontal: 12),
                      ),
                      homeBloc.isAnyGamePlayed
                          ? UserOnboardingStep.ON_COMPLETED.getStepWidget1()
                          : isJoinedInAny
                          ? UserOnboardingStep.CURRENT_STEP
                          .getStepWidget1()
                          : UserOnboardingStep.NEXT_STEP
                          .getStepWidget1()
                    ],
                  ),
                  SizedBox(
                    height: 8,
                  ),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                    children: [
                      Container(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            BoldText(AppStrings.welcomeAboard,
                                fontSize: 12,
                                color: AppColor.whiteTextColor),
                            SizedBox(
                              height: 4,
                            ),
                            RegularText(
                              AppStrings.submitYourGameTier,
                              textAlign: TextAlign.center,
                              fontSize: 10,
                              color: Color(0xffB6B6B6),
                            )
                          ],
                        ),
                      ),
                      Container(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            BoldText(AppStrings.joinTournament,
                                fontSize: 12,
                                color: AppColor.whiteTextColor),
                            SizedBox(
                              height: 4,
                            ),
                            RegularText(
                              AppStrings.findAndJoinTournament,
                              textAlign: TextAlign.center,
                              fontSize: 10,
                              color: Color(0xffB6B6B6),
                            )
                          ],
                        ),
                      ),
                      Container(
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            BoldText(AppStrings.playTournament,
                                fontSize: 12,
                                color: AppColor.whiteTextColor),
                            SizedBox(
                              height: 4,
                            ),
                            RegularText(
                              AppStrings.playTournamentAndEarn,
                              textAlign: TextAlign.center,
                              fontSize: 10,
                              color: Color(0xffB6B6B6),
                            )
                          ],
                        ),
                      )
                    ],
                  ),
                ],
              ));
        }
        return SizedBox.shrink();
      },
      bloc: homeBloc,
      buildWhen: (previous, current) => ((current is MyTournamentLoaded ||
          current is TournamentLoading) &&
          !homeBloc.isAnyTournamentJoin))
      : SizedBox();
}
