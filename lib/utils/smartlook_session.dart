import 'dart:math';
// import 'package:flutter_smartlook/flutter_smartlook.dart';
import 'package:gamerboard/common/models/device.dart';

////Created by saurabh.lahoti on 03/08/22

class SmartLookSessionHelper {
  // static final Smartlook smartlook = Smartlook.instance;

  static void init(BuildConfig buildConfig) {
    // smartlook.preferences.setProjectKey(buildConfig.smartlookKey);
  }

  //to track 10% of new signups
  static void recordNewUserSession(int userId) {
  /*  smartlook.user.setIdentifier(userId.toString());
    if (userId % 10 == 0 && userId > 10) {
      smartlook.start();
      AnalyticUtils.getInstance().trackEvents(Events.SMARTLOOK_TRACKED,
          properties: {"session_type": "new_user"});
      if (kDebugMode) {
        UiUtils.getInstance.showToast("Smartlook session started");
      }
    }*/
  }

  //to track 2% of regular logged in sessions
  static void recordSessionOnAppLaunch(int userId) async {
   /* smartlook.user.setIdentifier(userId.toString());
    final String? data = await SharedPreferenceHelper.getInstance
        .getStringPref(PrefKeys.SESSION_DATA);
    final SessionData sessionData;
    if (data == null) {
      sessionData = SessionData.generate();
    } else {
      sessionData = SessionData.fromMap(jsonDecode(data));
    }
    if (sessionData.currentSession >= sessionData.nextSessionTrigger) {
      sessionData.nextSessionTrigger += 49;
      smartlook.start();
      AnalyticUtils.getInstance().trackEvents(Events.SMARTLOOK_TRACKED,
          properties: {"session_type": "regular"});
      if (kDebugMode) {
        UiUtils.getInstance.showToast("Smartlook session started");
      }
    }
    if (kDebugMode) {
      UiUtils.getInstance.showToast(
          "Current session ${sessionData.currentSession}. Tracking session: ${sessionData.nextSessionTrigger}");
    }
    sessionData.currentSession++;
    SharedPreferenceHelper.getInstance
        .setStringPref(PrefKeys.SESSION_DATA, jsonEncode(sessionData.toMap()));*/
  }
}

class SessionData {
  int currentSession;
  int nextSessionTrigger;

  SessionData(this.currentSession, this.nextSessionTrigger);

  factory SessionData.generate() {
    return SessionData(1, Random().nextInt(50));
  }

  Map<String, dynamic> toMap() {
    return {
      'currentSession': currentSession,
      'nextSessionTrigger': nextSessionTrigger,
    };
  }

  factory SessionData.fromMap(dynamic map) {
    return SessionData(
        map['currentSession'].toInt(), map['nextSessionTrigger'].toInt());
  }
}
