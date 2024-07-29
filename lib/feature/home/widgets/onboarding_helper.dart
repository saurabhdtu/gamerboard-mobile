import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../common/models/onboarding.dart';
import '../../../common/router.dart';
import '../../../common/widgets/GBMarquee.dart';
import '../../../common/widgets/text.dart';
import '../../../resources/colors.dart';
import '../../../common/services/analytics/analytic_utils.dart';
import '../../../utils/onboarding_utils.dart';
import '../home_bloc.dart';
import '../home_state.dart';

class OnboardingHelper extends StatelessWidget {
  late OnboardingHelp _onboardingHelp;
  late HomeBloc _homeBloc;
  final double width, height;
  final int homeScreenIndex;

  OnboardingHelper(this.width, this.height, this.homeScreenIndex) {
    _onboardingHelp = OnboardingHelp.instance;
  }

  @override
  Widget build(BuildContext context) {
    AnalyticService.getInstance().trackEvents(Events.INTRO_SHOWN);
    _homeBloc = context.read<HomeBloc>();
    _homeBloc.loadTicker();
    return _onboardingHelp.showHelp ?? false
        ? Container(
        width: this.width,
        height: this.height,
        decoration:
        BoxDecoration(border: Border.all(color: AppColor.colorGrayBg1)),
        padding: const EdgeInsets.symmetric(horizontal: 5.0),
        child: Column(mainAxisSize: MainAxisSize.min, children: [
          Row(children: [
            Expanded(
                child: BoldText(_onboardingHelp.video?.title ?? "",
                    color: AppColor.lightWhiteColor)),
            Padding(
                padding: const EdgeInsets.all(5.0),
                child: InkWell(
                    onTap: () {
                      AnalyticService.getInstance()
                          .trackEvents(Events.INTRO_DISMISSED);
                      _homeBloc.dismissTutorial(homeScreenIndex);
                    },
                    child: Icon(Icons.close, color: Colors.white)))
          ]),
          InkWell(
              onTap: () {
                AnalyticService.getInstance().trackEvents(
                    Events.VIDEO_CLICKED,
                    properties: {"type": "intro"});
                Routes.launchVideoTutorial(
                    context,
                    _onboardingHelp.video?.url ?? "",
                    _onboardingHelp.video?.title ?? "",
                    OnboardingVideoEvent.ON_USER_CLICK
                        .getOnboardingVideoEventSource());
              },
              child: Container(
                  margin: const EdgeInsets.symmetric(vertical: 10.0),
                  decoration: BoxDecoration(
                      border: Border.all(color: Colors.white, width: 1.0)),
                  padding: const EdgeInsets.symmetric(vertical: 5.0),
                  child: Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.play_arrow, color: Colors.white),
                        const SizedBox(width: 11.0),
                        BoldText(_onboardingHelp.video?.cta ?? "",
                            color: Colors.white, fontSize: 12.0)
                      ]))),
          Expanded(
              child: ListView.builder(
                  shrinkWrap: true,
                  physics: NeverScrollableScrollPhysics(),
                  itemBuilder: (ctx, i) {
                    return Stack(children: [
                      /*      Positioned(
                            left:5.0,
                              child: ColoredBox(
                                  child:SizedBox(width: 2.0),
                                  color: AppColor.whatsappColor)),*/
                      Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            DecoratedBox(
                                decoration: BoxDecoration(
                                    borderRadius:
                                    BorderRadius.circular(16.0),
                                    color: AppColor.successGreen),
                                child: const SizedBox(
                                    height: 10.0, width: 10.0)),
                            const SizedBox(width: 10.0),
                            Expanded(
                                child: Column(
                                    crossAxisAlignment:
                                    CrossAxisAlignment.start,
                                    children: [
                                      BoldText(
                                          _onboardingHelp.steps?[i].title ?? "",
                                          color: Colors.white,
                                          fontSize: 12.0),
                                      RegularText(
                                          _onboardingHelp
                                              .steps?[i].description ??
                                              "",
                                          color: AppColor.lightWhiteColor),
                                      const SizedBox(height: 15.0)
                                    ]))
                          ])
                    ]);
                  },
                  itemCount: _onboardingHelp.steps?.length ?? 0)),
          Divider(
              height: 1.5, thickness: 1.5, color: AppColor.lightWhiteColor),
          BlocBuilder<HomeBloc, HomeState>(
              builder: (context, state) {
                if (state is TickerLoaded) {
                  final entries = state.map.entries.toList();
                  return SizedBox(
                      height: 30.0,
                      child: GBMarquee(
                          itemCount: entries.length,
                          pace: 4,
                          builder: (ctx, i) => RichText(
                              text: TextSpan(
                                  style: BoldTextStyle(
                                      color: AppColor.grayTextB3B3B3,
                                      fontSize: 10.0),
                                  children: [
                                    TextSpan(text: entries[i].key),
                                    TextSpan(
                                        text: " just won ",
                                        style: RegularTextStyle(
                                            color: AppColor.grayText696969,
                                            fontSize: 11.0)),
                                    TextSpan(
                                        text: entries[i].value,
                                        style: BoldTextStyle(
                                            color: AppColor.successGreen,
                                            fontSize: 11.0)),
                                    TextSpan(text: "  ●  ")
                                  ]))));
                }
                return SizedBox.shrink();
              },
              buildWhen: (p, c) => c is TickerLoading || c is TickerLoaded,
              bloc: _homeBloc)
        ]))
        : SizedBox.shrink();
  }
}
