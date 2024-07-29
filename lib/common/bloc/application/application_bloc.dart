import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:firebase_remote_config/firebase_remote_config.dart';
import 'package:flagsmith/flagsmith.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_branch_sdk/flutter_branch_sdk.dart';
import 'package:flutter_cache_manager/flutter_cache_manager.dart';
import 'package:gamerboard/common/bloc/application/streams.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/models/attribution.dart';
import 'package:gamerboard/common/models/deeplink_data.dart';
import 'package:gamerboard/common/models/device.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/feature/bgservice/capture_service.dart';
import 'package:gamerboard/main.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/api_client.dart';
import 'package:gamerboard/utils/app_update.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/shared_preferences.dart';
import 'package:gamerboard/utils/smartlook_session.dart';
import 'package:gamerboard/utils/ui_utils.dart';

import '../../../graphql/query.graphql.dart';

////Created by saurabh.lahoti on 03/08/21

class ApplicationBloc extends Cubit<BaseState> {
  var args;
  ESports userCurrentGame = ESports.bgmi;
  late FlagsmithClient flagsmithClient;
  bool ffMaxFeatureFlag = false;
  bool showOnboardingFlow = false;
  late Map<String, dynamic> onboardingStep;


  ApplicationBloc() : super(AppLoadState()) {
    loadAppConfig();
    Constants.PLATFORM_CHANNEL.setMethodCallHandler((methodCall) async {
      switch (methodCall.method) {
        case "notification_clicked":
          {
            args = jsonDecode(methodCall.arguments['metaData'].toString());
            bool freshLaunch = methodCall.arguments['fresh_launch'] ?? false;
            debugPrint("notification-metadata---> ${args.toString()}");
            if (!freshLaunch &&
                NavigatorService.navigatorKey.currentContext != null) {
              Navigator.of(NavigatorService.navigatorKey.currentContext!)
                  .popUntil((route) => route.isFirst);
              Navigator.of(NavigatorService.navigatorKey.currentContext!)
                  .pushReplacementNamed(Routes.SPLASH);
            }
          }
          break;
        case "normal_launch":
          {
            args = null;
          }
          break;
        case "branch_deep_link_clicked":
          await setArgsFromDeepLink();

          if (NavigatorService.navigatorKey.currentContext != null) {
            Navigator.of(NavigatorService.navigatorKey.currentContext!)
                .pushNamedAndRemoveUntil(Routes.SPLASH, (route) => false);
          }
          break;
      }
      return Future.value(1);
    });
  }

  String? deviceId;
  var lastUpdate = -1;

  Future<int> initiateAppUpdate(
      BuildContext context, int currentAppVersion) async {
    return AppUpdateUtil.initiateAppUpdate(context, currentAppVersion);
  }

  void loadRemoteConfig(BuildContext context) async {
    emit(Loading());
    attributeUser();
    var isNewUserFlagActive = await SharedPreferenceHelper.getInstance
        .getBoolPref(PrefKeys.IS_NEW_USER_FIRST_SESSION);
    if(isNewUserFlagActive ?? false)
      {
        SharedPreferenceHelper.getInstance
            .setBoolPref(PrefKeys.IS_NEW_USER_FIRST_SESSION,false);
      }
    FirebaseRemoteConfig.instance.setConfigSettings(RemoteConfigSettings(
        fetchTimeout: Duration(seconds: 5),
        minimumFetchInterval:
            kDebugMode ? Duration(seconds: 0) : Duration(seconds: 30)));
    if (await SharedPreferenceHelper.getInstance
            .getIntPref(PrefKeys.FIRST_TS) ==
        null)
      SharedPreferenceHelper.getInstance
          .setIntPref(PrefKeys.FIRST_TS, DateTime.now().millisecondsSinceEpoch);
    try {
      await FirebaseRemoteConfig.instance.fetchAndActivate();
    } catch (ex) {
      debugPrint(ex.toString());
    }
    var stepsJson = FirebaseRemoteConfig.instance
        .getString(RemoteConfigConstants.ONBOARDING_STEPS);

    if(stepsJson.isNotEmpty){
      onboardingStep = await jsonDecode(stepsJson);
    }else{
      onboardingStep = {};
    }

    for (var mapEntry in onboardingStep.entries) {
      await DefaultCacheManager()
          .getFileFromCache(mapEntry.value)
          .then((file) async {
        if (file == null) {
          var file = await DefaultCacheManager().downloadFile(mapEntry.value);
          onboardingStep[mapEntry.key] = file.file;
        } else {
          onboardingStep[mapEntry.key] = file.file;
        }
      }).catchError((error) {
        print(error);
      });
    }
    await _getDeviceInfo();
    try {
      if (buildConfig != null) {
        //Setup smartlook
        SmartLookSessionHelper.init(buildConfig!);
        //check for app update
        int result = await initiateAppUpdate(
            context, int.parse(buildConfig!.currentAppVersion));
        if (result != AppUpdate.FORCE_UPDATE) {
          bool isLoggedIn =
              await SharedPreferenceHelper.getInstance.isLoggedIn();
          if (isLoggedIn) {
            var profileResponse =
                await QueryRepository.instance.getMyProfile(getCached: false);
            if (profileResponse.hasErrors) {
              emit(RemoteConfigLoaded(
                  error: Error(profileResponse.errors!.first.message, 500)));
            } else {
              if (profileResponse.data != null) {
                AnalyticService.getInstance()
                    .pushUserProfile(profileResponse.data!.me.id.toString());
              }
              SmartLookSessionHelper.recordSessionOnAppLaunch(
                  profileResponse.data!.me.id);
            }
          } else {
            emit(RemoteConfigLoaded());
          }
          _conditionalNavigation();
        }
      } else {
        UiUtils.getInstance.showToast("Unable to identify device");
      }
    } catch (ex, trace) {
      emit(RemoteConfigLoaded(error: Error(ex.toString(), 500)));
      debugPrint(ex.toString());
      logException(ex, trace);
    }
  }

  _getDeviceInfo() async {
    var map = await Constants.PLATFORM_CHANNEL.invokeMethod("get_device_info");
    buildConfig = BuildConfig.fromMap(map);
    SharedPreferenceHelper.getInstance
        .setStringPref(PrefKeys.KEY_DEVICE_INFO, jsonEncode(map));
  }

  Future<void> loadAppConfig() async {
    ffMaxFeatureFlag = kDebugMode ||
        (await SharedPreferenceHelper.getInstance
                .getBoolPref(PrefKeys.IS_FFMAX_ENABLE) ??
            false);

    var isOnboardingShow = await SharedPreferenceHelper.getInstance
        .getBoolPref(PrefKeys.SHOW_ONBOARDING_FLOW) ??
        false;

    if (!isOnboardingShow) {
      int timestamp = DateTime.now().millisecondsSinceEpoch;
      showOnboardingFlow = (timestamp % 3) == 0;
    }

    var map = await Constants.PLATFORM_CHANNEL.invokeMethod("app_config");
    var currentGame = await SharedPreferenceHelper.getInstance
        .getStringPref(PrefKeys.SELECTED_GAMES) ??
        ESports.bgmi.getPackageName();
    if (currentGame == ESports.freefiremax.getPackageName()) {
      userCurrentGame = ESports.freefiremax;
    } else {
      userCurrentGame = ESports.bgmi;
    }
    DioClient.baseUrl = map['API_ENDPOINT'];
    flagsmithClient = FlagsmithClient(
      apiKey: map["FLAGSMITH_KEY"],
    );
    flagsmithClient.initialize();
    return Future.value();
  }

  void _conditionalNavigation() async {
    await setArgsFromDeepLink();
    final DeeplinkData deeplinkData = DeeplinkData.fromMap(args);

    Map data = await deeplinkNavigation(deeplinkData);
    bool isLoggedIn = await SharedPreferenceHelper.getInstance.isLoggedIn();
    if (deeplinkData.referrerCode != null) {
      SharedPreferenceHelper.getInstance
          .setStringPref(PrefKeys.INVITE_CODE, deeplinkData.referrerCode!);
    }
    if (isLoggedIn) {
      emit(NavigateFromSplash(data.values.first, data.keys.first));
    } else if (showOnboardingFlow) {
      SharedPreferenceHelper.getInstance
          .setBoolPref(PrefKeys.SHOW_ONBOARDING_FLOW, true);
      showOnboardingFlow = false;
      emit(NavigateFromSplash(null, Routes.ONBOARDING_FLOW));
    }
  }

  void attributeUser() {
    writeLogToFile("attributing user");
    FlutterBranchSdk.getFirstReferringParams().then((params) async {
      writeLogToFile(params.toString());
      if (kDebugMode) {
        UiUtils.getInstance.showToast(FirebaseRemoteConfig.instance
            .getKeysByPrefix("exp_")
            .entries
            .toList()
            .fold(
                "",
                (previousValue, element) =>
                    previousValue.toString() +
                    ";" +
                    element.key +
                    ":" +
                    element.value.asString()));
      }
      if (params.isNotEmpty) {
        try {
          final Attribution attribution = Attribution.fromJson(params);
          bool? recorded = await SharedPreferenceHelper.getInstance
              .getBoolPref(PrefKeys.ATTRIBUTION_RECORDED);
          if (attribution.referrerId != null)
            await SharedPreferenceHelper.getInstance
                .setIntPref(PrefKeys.REFERRED_ID, attribution.referrerId!);
          if (recorded != true) {
            final attributes = {
              "utm_source": attribution.utmSource,
              "utm_campaign": attribution.utmCampaign,
              "utm_medium": attribution.utmMedium,
              "advertiser": attribution.advertiserPartner
            };
            AnalyticService.getInstance().pushUserProperties(attributes);
          }
        } catch (ex, stack) {
          writeLogToFile(ex.toString() + stack.toString());
        }
      }
    });
  }

  Future<void> setArgsFromDeepLink() async {
    final params = await FlutterBranchSdk.getLatestReferringParams();
    final attribution = Attribution.fromJson(params);
    if (attribution.clickedBranchLink == true) args = params;
    return Future.value();
  }
}

signOut(BuildContext? context, {String? message}) async {
  BackgroundServicePlugin.stopService();
  Constants.PLATFORM_CHANNEL.invokeMethod(
      "logout");
  resetUserData();
  FlutterBranchSdk.logout();
  if (context != null)
    Navigator.of(context).pushNamedAndRemoveUntil(
        Routes.SPLASH, (route) => false,
        arguments: {"message": message});
}
