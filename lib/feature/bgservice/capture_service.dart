import 'dart:ui';

import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/feature/bgservice/dispatcher.dart';
import 'package:gamerboard/utils/shared_preferences.dart';

////Created by saurabh.lahoti on 18/08/21

class FileAnalysis {
  String filePath;
  int sessionId;
  String analysisJson;
  int uploaded;
  String? uploadedUrl;

  FileAnalysis(this.sessionId, this.filePath, this.analysisJson, this.uploaded,
      {this.uploadedUrl});

  Map<String, dynamic> toMap() {
    return {
      'id': sessionId,
      'path': filePath,
      'json': analysisJson,
      'url': uploadedUrl,
      'uploaded': uploaded
    };
  }

  factory FileAnalysis.fromMap(Map<String, dynamic> map) {
    return new FileAnalysis(map['id'] as int, map['path'] as String,
        map['json'] as String, map['uploaded'] as int,
        uploadedUrl: map['url'] as String);
  }

  @override
  String toString() {
    return 'FileAnalysis{path: $filePath, id: $sessionId, json: $analysisJson, uploaded: $uploaded, url: $uploadedUrl}';
  }
}

class BackgroundServicePlugin {
  static initialize() async {
    final callback = PluginUtilities.getCallbackHandle(callbackDispatcher);
    await Constants.BG_PLUGIN
        .invokeMethod('initialize_service', <dynamic>[callback!.toRawHandle()]);
  }

  static Future<dynamic> startService( String auth, bool additionalPermissions,
  {String? profileId, String? profileName,String? gamePackage, bool appRestarted = false}) async {
    return Constants.BG_PLUGIN.invokeMethod('capture_service', {
      "game_profile_id": profileId,
      "game_profile_name": profileName,
      "game_package":gamePackage,
      "auth_token":auth,
      "app_restarted":appRestarted,
      "ask_additional_permission": additionalPermissions
    });
  }

  static Future<dynamic> deleteSession() {
    return Constants.BG_PLUGIN.invokeMethod('delete_local_data');
  }

  static Future<dynamic> sendLogs() {
    return Constants.BG_PLUGIN.invokeMethod('upload_log');
  }

  static Future<dynamic> stopService() {
    return Constants.BG_PLUGIN.invokeMethod('stop_service');
  }

  static Future<dynamic> syncWithServer() async {
    return Constants.BG_PLUGIN.invokeMethod('sync_with_server', {
      'upload_images': await SharedPreferenceHelper.getInstance
          .getBoolPref(PrefKeys.BOOL_UPLOAD_IMAGES)
    });
  }

  static Future<dynamic> checkServiceStatus() {
    return Constants.BG_PLUGIN.invokeMethod("check_service_status");
  }
}
