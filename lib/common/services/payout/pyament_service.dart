import 'package:gamerboard/common/services/payout/gateways/payment_gateway.dart';
import 'package:gamerboard/common/services/payout/models/payment_request.dart';
import 'package:gamerboard/common/services/payout/models/payment_response.dart';

class PaymentService {
  Future<PaymentResponse> startPayment(PaymentGateway gateway, PaymentRequest request){
    return gateway.process(request);
  }
}