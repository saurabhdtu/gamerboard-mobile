////Created by saurabh.lahoti on 19/09/21
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/utils/api_client.dart';

void callbackDispatcher() {
  // 1. Initialize MethodChannel used to communicate with the platform portion of the plugin.

  // 2. Setup internal state needed for MethodChannels.
  WidgetsFlutterBinding.ensureInitialized();
  // 3. Listen for background events from the platform portion of the plugin.
  Constants.BG_PLUGIN_SERVICE.setMethodCallHandler((MethodCall call) async {
    debugPrint("method ${call.method}");
    return handle(call);
    // 3.1. Retrieve callback instance for handle.
    /*final Function? callback =
        PluginUtilities.getCallbackFromHandle(CallbackHandle.fromRawHandle(args[0]));
    assert(callback != null);

    // 3.2. Preprocess arguments.
    */ /* final triggeringGeofences = args[1].cast<String>();
    final locationList = args[2].cast<double>();
    final triggeringLocation = locationFromList(locationList);
    final GeofenceEvent event = intToGeofenceEvent(args[3]);
*/ /*
    // 3.3. Invoke callback.
    if (callback != null) callback("call");*/
  });
  Constants.BG_PLUGIN_SERVICE.invokeMethod('initialize');
}

Future<dynamic> handle(MethodCall call) async {
  if (Firebase.apps.isEmpty) {
    var values = await Constants.BG_PLUGIN_SERVICE.invokeMethod('init_isolate');
    await initArgs(values);
  }
}

Future<void> initArgs(dynamic args) async {
  DioClient.baseUrl = args['api_endpoint'];
  DioClient.authToken = args['auth_token'];
  DioClient.VC = args['build_version_code']?.toString();
  await Firebase.initializeApp();
  return Future.value();
}
