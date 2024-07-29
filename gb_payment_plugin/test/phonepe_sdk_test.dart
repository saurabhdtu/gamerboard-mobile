import 'dart:async';
import 'dart:typed_data';

import 'package:flutter_test/flutter_test.dart';
import 'package:gb_payment_plugin/phonepe_payment_response.dart';
import 'package:gb_payment_plugin/upi_app.dart';
import 'package:gb_payment_plugin/phonepe_sdk_platform_interface.dart';
import 'package:gb_payment_plugin/phonepe_sdk_method_channel.dart';
import 'package:gb_payment_plugin/upi_payment_request.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockPhonepeSdkPlatform
    with MockPlatformInterfaceMixin
    implements PhonepeSdkPlatform {

  @override
  Future<List<UpiApp>?> getUpiApps() => Future.value([UpiApp("com.phonepe", "phonepe", 1, Uint8List(0))]);

  @override
  Future<PhonePePaymentResponse> startPayment(UpiPaymentRequest upiPaymentRequest) {
    // TODO: implement startPayment
    throw UnimplementedError();
  }
}

void main() {
  final PhonepeSdkPlatform initialPlatform = PhonepeSdkPlatform.instance;

  test('$MethodChannelPhonepeSdk is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelPhonepeSdk>());
  });

}
