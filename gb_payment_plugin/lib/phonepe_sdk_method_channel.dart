import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:gb_payment_plugin/phonepe_payment_response.dart';
import 'package:gb_payment_plugin/upi_app.dart';
import 'package:gb_payment_plugin/upi_payment_request.dart';

import 'phonepe_sdk_platform_interface.dart';

/// An implementation of [PhonepeSdkPlatform] that uses method channels.
class MethodChannelPhonepeSdk extends PhonepeSdkPlatform {
  static const String methodGetUpiApps = "getUpiApps";
  static const String methodStartPayment = "startPayment";

  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('gb_payment_plugin');

  @override
  Future<List<UpiApp>?> getUpiApps() async {
    final version =
        await methodChannel.invokeMethod<List<dynamic>>(methodGetUpiApps);
    return version?.map<UpiApp>((e) => UpiApp.fromMap(e)).toList();
  }

  @override
  Future<PhonePePaymentResponse> startPayment(
      UpiPaymentRequest upiPaymentRequest) async {
    final status = await methodChannel.invokeMethod(
        methodStartPayment, upiPaymentRequest.toMap());
    print("Payment response ==========> ${status}");
    return Future.value(
        PhonePePaymentResponse.fromMap(status));
  }
}
