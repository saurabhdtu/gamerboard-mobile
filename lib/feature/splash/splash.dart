import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:gamerboard/common/bloc/application/application_bloc.dart';
import 'package:gamerboard/common/bloc/application/streams.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';

import '../../utils/shared_preferences.dart';
////Created by saurabh.lahoti on 26/10/21

class SplashPage extends StatefulWidget {
  final String? message;

  SplashPage({this.message});

  @override
  State<StatefulWidget> createState() => _SplashState();
}

class _SplashState extends State<SplashPage> {
  ApplicationBloc? applicationBloc;

  @override
  Widget build(BuildContext context) {
    FocusScope.of(context).unfocus();
    return appScaffold(
        body: BlocConsumer<ApplicationBloc, BaseState>(
            listener: (ctx, state) {
              if (state is NavigateFromSplash) {
                Navigator.of(context)
                    .pushReplacementNamed(state.route, arguments: state.data);
              }
            },
            builder: (context, state) {
              if (state is Loading ||
                  (state is RemoteConfigLoaded && state.error != null)) {
                if (state is Loading)
                  return Stack(alignment: Alignment.center, children: [
                    Center(
                        child:
                            SvgPicture.asset("${imageAssets}ic_gb_logo.svg")),
                    Center(
                        child: SizedBox(
                            width: 150.0,
                            height: 150.0,
                            child:
                                CircularProgressIndicator(strokeWidth: 1.5))),
                  ]);
                else {
                  return Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        Center(
                            child: SvgPicture.asset(
                                "${imageAssets}ic_gb_logo.svg")),
                        const SizedBox(height: 20.0),
                        RegularText(
                            (state as RemoteConfigLoaded).error!.message,
                            fontSize: 16.0),
                        const SizedBox(height: 20.0),
                        SizedBox(
                            height: 90,
                            width: 200,
                            child: Padding(
                                padding: const EdgeInsets.all(20.0),
                                child: secondaryButton(
                                    state.error!.message ==
                                            ErrorMessages.ERROR_SESSION_EXPIRED
                                        ? AppStrings.signIn
                                        : AppStrings.retry, () {
                                  applicationBloc?.loadRemoteConfig(context);
                                })))
                      ]);
                }
              } else if (state is RemoteConfigLoaded) {
                return Padding(
                    padding: EdgeInsets.all(10.0),
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: RegularText(AppStrings.welcomeGamerboard,
                                  fontSize: 27.0)),
                          Image.asset("${imageAssets}img_splash.png"),
                          Expanded(
                              child: Center(
                                  child: Row(
                                      mainAxisSize: MainAxisSize.max,
                                      mainAxisAlignment:
                                          MainAxisAlignment.center,
                                      children: [
                                // SizedBox(width: 150.0),
                                darkBorderButton(AppStrings.createAccount, () {
                                  AnalyticService.getInstance().trackEvents(
                                      Events.CREATE_ACCOUNT_CLICKED,
                                      properties: {"from": "splash_page"});
                                  Navigator.of(context)
                                      .pushNamed(Routes.SIGN_UP);
                                }, paddingHorizontal: 24.0),

                                const SizedBox(width: 30.0),

                                darkBorderButton(AppStrings.signIn, () {
                                  AnalyticService.getInstance().trackEvents(
                                      Events.SIGN_IN_CLICKED,
                                      properties: {"from": "splash_page"});
                                  Navigator.of(context)
                                      .pushNamed(Routes.LOG_IN);
                                }, paddingHorizontal: 24.0)
                                /*  Icon(Icons.arrow_left_sharp, color: Colors.white),
                                DecoratedBox(
                                    decoration:
                                        BoxDecoration(gradient: AppColor.popupBackgroundGradient),
                                    child: Padding(
                                        padding: EdgeInsets.symmetric(horizontal: 16.0, vertical: 8.0),
                                        child: RegularText(
                                            "Get ${AppStrings.rupeeSymbol}${RemoteConfig.instance.getInt(RemoteConfigConstants.NEW_USER_REWARD)} (Free)",
                                            fontSize: 13.0)))*/
                              ])))
                        ]));
              }
              return SizedBox.shrink();
            },
            bloc: applicationBloc));
  }

  @override
  void initState() {
    super.initState();
    applicationBloc = context.read<ApplicationBloc>();

    applicationBloc?.loadRemoteConfig(context);
  }
}
