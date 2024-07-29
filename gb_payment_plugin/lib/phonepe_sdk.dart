
import 'package:gb_payment_plugin/phonepe_payment_response.dart';
import 'package:gb_payment_plugin/upi_app.dart';
import 'package:gb_payment_plugin/upi_payment_request.dart';

import 'phonepe_sdk_platform_interface.dart';

class PhonepeSdk {
  Future<List<UpiApp>?> getUpiApps() {
    return PhonepeSdkPlatform.instance.getUpiApps();
  }
  Future<PhonePePaymentResponse> startPayment(UpiPaymentRequest paymentRequest){
    return PhonepeSdkPlatform.instance.startPayment(paymentRequest);
  }
}
