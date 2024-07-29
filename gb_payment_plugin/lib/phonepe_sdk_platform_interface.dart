import 'package:gb_payment_plugin/phonepe_payment_response.dart';
import 'package:gb_payment_plugin/upi_app.dart';
import 'package:gb_payment_plugin/upi_payment_request.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'phonepe_sdk_method_channel.dart';

abstract class PhonepeSdkPlatform extends PlatformInterface {
  /// Constructs a PhonepeSdkPlatform.
  PhonepeSdkPlatform() : super(token: _token);

  static final Object _token = Object();

  static PhonepeSdkPlatform _instance = MethodChannelPhonepeSdk();

  /// The default instance of [PhonepeSdkPlatform] to use.
  ///
  /// Defaults to [MethodChannelPhonepeSdk].
  static PhonepeSdkPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [PhonepeSdkPlatform] when
  /// they register themselves.
  static set instance(PhonepeSdkPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<List<UpiApp>?> getUpiApps() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<PhonePePaymentResponse> startPayment(UpiPaymentRequest upiPaymentRequest) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
