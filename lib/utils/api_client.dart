import 'dart:convert';

import 'package:dio/dio.dart';
import 'package:firebase_remote_config/firebase_remote_config.dart';
import 'package:flutter/cupertino.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/models/device.dart';
import 'package:gamerboard/utils/shared_preferences.dart';
import 'package:gql_dio_link/gql_dio_link.dart';
import "package:gql_link/gql_link.dart";

class DioClient {
  late Link _link;
  static DioClient? _dioClient;
  static String baseUrl = "";
  static String? VC = "";
  static String? authToken;

  static DioClient get instance => _dioClient ??= DioClient._();
  static bool trace = false;

  DioClient._() {
    debugPrint(baseUrl);
    final client = Dio(BaseOptions(
        baseUrl: baseUrl, receiveTimeout: Duration(seconds: 25), connectTimeout: Duration(seconds: 25)));
    client.interceptors.add(_CustomInterceptor());
    try {
      trace =
          jsonDecode(FirebaseRemoteConfig.instance.getString("logging_flags"))[
              'api_response'];
    } catch (e, stack) {
      debugPrintStack(stackTrace: stack);
    }
    _link = DioLink(baseUrl, client: client);
  }

  Link get link => _link;
}

class _CustomInterceptor extends Interceptor {
  @override
  void onRequest(
      RequestOptions options, RequestInterceptorHandler handler) async {
    if (buildConfig == null && DioClient.VC == null) {
      final String? info = await SharedPreferenceHelper.getInstance
          .getStringPref(PrefKeys.KEY_DEVICE_INFO);
      if (info != null) {
        buildConfig = BuildConfig.fromMap(jsonDecode(info));
      }
    }

    String? authToken = DioClient.authToken ??
        await SharedPreferenceHelper.getInstance
            .getStringPref(PrefKeys.USER_AUTH);
    if (authToken != null) {
      print("Token---> Bearer $authToken");
      options.headers.putIfAbsent('Authorization', () => "Bearer $authToken");
    }
    if (buildConfig != null) {
      options.headers.putIfAbsent('device', () => buildConfig!.deviceId);
      options.headers.putIfAbsent('trace', () => DioClient.trace ? 1 : 0);
      options.headers
          .putIfAbsent('release', () => buildConfig!.currentAppVersion);
    } else if (DioClient.VC != null) {
      options.headers.putIfAbsent('release', () => DioClient.VC!);
    }
    super.onRequest(options, handler);
  }
}

resetUserData() {
  SharedPreferenceHelper.getInstance.removeKey(PrefKeys.USER_DATA);
  SharedPreferenceHelper.getInstance.removeKey(PrefKeys.USER_AUTH);
  SharedPreferenceHelper.getInstance.removeKey(PrefKeys.UPI_ID);
  SharedPreferenceHelper.getInstance.removeKey(PrefKeys.USER_SHARE_LINK);
  SharedPreferenceHelper.getInstance.removeKey(PrefKeys.INVITE_CODE);
  SharedPreferenceHelper.getInstance.removeKey(PrefKeys.SELECTED_GAMES);
  SharedPreferenceHelper.getInstance.removeKey(PrefKeys.IS_FFMAX_ENABLE);
  SharedPreferenceHelper.getInstance.removeKey(PrefKeys.REWARD_DIALOG_SHOW);

  SharedPreferenceHelper.getInstance
      .removeKey(PrefKeys.INVITE_DIALOG_ON_GAME_SUBMISSION_COUNT);
  SharedPreferenceHelper.getInstance
      .removeKey(PrefKeys.INVITE_DIALOG_ON_WALLET_BALANCE_COUNT);
}
