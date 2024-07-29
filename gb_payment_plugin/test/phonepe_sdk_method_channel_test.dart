import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gb_payment_plugin/phonepe_sdk_method_channel.dart';

void main() {
  MethodChannelPhonepeSdk platform = MethodChannelPhonepeSdk();
  const MethodChannel channel = MethodChannel('gb_payment_plugin');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getUpiApps(), '42');
  });
}
