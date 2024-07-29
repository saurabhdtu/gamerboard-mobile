import 'dart:convert';
import 'dart:io';

import 'package:firebase_remote_config/firebase_remote_config.dart';
import 'package:flutter/material.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/models/device.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/shared_preferences.dart';
import 'package:gamerboard/utils/ui_utils.dart';
import 'package:permission_handler/permission_handler.dart';

////Created by saurabh.lahoti on 21/10/21

class AppUpdateUtil {
  static Future<int> initiateAppUpdate(
      BuildContext context, int currentAppVersion) async {
    try {
      String updateData = "{}";
      updateData = FirebaseRemoteConfig.instance
          .getString(RemoteConfigConstants.APP_UPDATE_ANDROID);
      AppUpdate appUpdate = AppUpdate.fromMap(jsonDecode(updateData));
      int appState = AppUpdate.NO_UPDATE;
      final user = await QueryRepository.instance.getMyProfile();
      int value = 0;
      if (user.data != null) {
        value = user.data!.me.id;
      } else {
        value = await SharedPreferenceHelper.getInstance
            .getIntPref(PrefKeys.FIRST_TS) ??
            0;
      }
      debugPrint(
          "value: $value; perc: ${appUpdate.rolloutPercentage}; eligible: ${eligibleForUpdate(value, appUpdate.rolloutPercentage)}");

      if (eligibleForUpdate(value, appUpdate.rolloutPercentage)) {
        if (appUpdate.forceUpdateVersion > currentAppVersion) {
          appState = AppUpdate.FORCE_UPDATE;
        } else if (appUpdate.latestVersion > currentAppVersion) {
          appState = AppUpdate.SOFT_UPDATE;
        }
        if (Platform.isAndroid) {
          switch (appState) {
            case AppUpdate.FORCE_UPDATE:
              var status = await Permission.requestInstallPackages.status;
              if (status.isPermanentlyDenied) openAppSettings();
              bool? result = await showAppUpdateUI(
                  context,
                  appUpdate.forceTitle,
                  appUpdate.forceDescription,
                  appUpdate,
                  AppStrings.installNow, () async {
                /*DownloadHandler downloadHandler = DownloadHandler(context);
            UiUtils.getInstance
                .downloadProgressDialog(context, downloadHandler);
            int result = await DownloadUtils.getInstance
                .downloadFileToDownloads(appUpdate.downloadUrl,
                    downloadListener: downloadHandler);*/
                UiUtils.getInstance
                    .showToast("Check notification for download progress.");
                Constants.PLATFORM_CHANNEL
                    .invokeMethod("download_apk", {"url": appUpdate.downloadUrl});
                // if (result >= -1)
                //   Constants.PLATFORM_CHANNEL.invokeMethod("close_app");
              }, isDismissible: false);
              if (result == null || result == false)
                Constants.PLATFORM_CHANNEL.invokeMethod("close_app");
              return Future.value(appState);

            case AppUpdate.SOFT_UPDATE:
              var status = await Permission.requestInstallPackages.status;
              if (status.isPermanentlyDenied) openAppSettings();
              bool? result = await showAppUpdateUI(
                  context,
                  appUpdate.title,
                  appUpdate.description,
                  appUpdate,
                  AppStrings.installNow, () async {
                /* DownloadHandler downloadHandler = DownloadHandler(context);
            UiUtils.getInstance
                .downloadProgressDialog(context, downloadHandler);
            await DownloadUtils.getInstance.downloadFileToDownloads(
                appUpdate.downloadUrl,
                downloadListener: downloadHandler);*/
                UiUtils.getInstance
                    .showToast("Check notification for download progress.");
                Constants.PLATFORM_CHANNEL
                    .invokeMethod("download_apk", {"url": appUpdate.downloadUrl});
                Navigator.of(context).popAndPushNamed(Routes.HOME_PAGE);
              }, negMsg: AppStrings.later);
              if (result == null || result == false)
                return Future.value(appState);
              else
                return Future.value(AppUpdate.FORCE_UPDATE);

            case AppUpdate.NO_UPDATE:
            // commented for now : getting plugin exception in getting download dir
            // DownloadUtils.getInstance.deleteUnusedApks();
              return Future.value(appState);
          }
        }
      }
      return Future.value(AppUpdate.NO_UPDATE);
    } catch (e) {
      return Future.value(AppUpdate.NO_UPDATE);
    }
  }

  static bool eligibleForUpdate(int value, int rolloutPercentage) =>
      (value % 10) <= (rolloutPercentage / 10) ||
      (rolloutPercentage >= 100 || rolloutPercentage <= 0);

  static Future<dynamic> showAppUpdateUI(BuildContext context, String title,
      String message, AppUpdate appUpdate, String posMsg, Function posAction,
      {String? negMsg, Function? negAction, bool isDismissible = true}) {
    Function pos = () {
      Navigator.of(context).pop(true);
      posAction.call();
    };
    Function neg = () {
      Navigator.of(context).pop(false);
    };
    if (negAction != null) {
      neg = () {
        Navigator.of(context).pop(false);
        negAction.call();
      };
    }
    return showModalBottomSheet(
        context: context,
        isScrollControlled: true,
        isDismissible: isDismissible,
        builder: (context) {
          return Container(
              margin: EdgeInsets.symmetric(horizontal: 60.0),
              decoration: BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.circular(5.0)),
              child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  mainAxisSize: MainAxisSize.min,
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    Padding(
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              TitleText(
                                  "$title : (${appUpdate.latestVersionCode})",
                                  color: Colors.black),
                              SizedBox(height: 8.0),
                              RegularText(message, color: Colors.black),
                              SizedBox(height: 10.0),
                              BoldText(AppStrings.whatsNew, color: Colors.blue),
                              ListView.builder(
                                  shrinkWrap: true,
                                  physics: NeverScrollableScrollPhysics(),
                                  itemBuilder: (context, index) {
                                    return Padding(
                                        padding: EdgeInsets.only(top: 3.0),
                                        child: RegularText(
                                          " -> ${appUpdate.whatsNew[index]}",
                                          color: Colors.black,
                                        ));
                                  },
                                  itemCount: appUpdate.whatsNew.length)
                            ]),
                        padding: EdgeInsets.all(20.0)),
                    Row(children: [
                      if (negMsg != null)
                        Expanded(
                            child: SizedBox(
                                height: 40.0,
                                child: DecoratedBox(
                                    decoration: BoxDecoration(
                                        color: Colors.black12,
                                        borderRadius: BorderRadius.only(
                                            bottomLeft: Radius.circular(5.0))),
                                    child: TextButton(
                                        onPressed: () => {neg()},
                                        child: BoldText(negMsg,
                                            color: Colors.black,
                                            fontSize: 15.0))))),
                      Expanded(
                          child: SizedBox(
                              height: 40.0,
                              child: DecoratedBox(
                                  decoration: BoxDecoration(
                                      color: Colors.lightBlue,
                                      borderRadius: BorderRadius.only(
                                          bottomRight: Radius.circular(5.0),
                                          bottomLeft: Radius.circular(
                                              negMsg == null ? 5.0 : 0))),
                                  child: TextButton(
                                      onPressed: () => {pos()},
                                      child:
                                          BoldText(posMsg, fontSize: 15.0)))))
                    ])
                  ]));
        },
        backgroundColor: Colors.transparent);
  }
}
