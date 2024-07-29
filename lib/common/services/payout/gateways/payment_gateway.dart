import 'package:gamerboard/common/services/payout/models/payment_request.dart';
import 'package:gamerboard/common/services/payout/models/payment_response.dart';

abstract class PaymentGateway{
  Future<PaymentResponse> process(PaymentRequest request);
  Future<PaymentResponse> checkStatus(int transactionId){
    throw UnimplementedError("Unsupported check status call");
  }
}