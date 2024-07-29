import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:gamerboard/common/services/payout/models/payment_request.dart';
import 'package:gamerboard/common/repository/mutation_repo.dart';
import 'package:gamerboard/common/services/payout/gateways/payment_gateway.dart';
import 'package:gamerboard/common/services/payout/models/payment_response.dart';

class ManualPaymentGateway extends PaymentGateway {
  final BuildContext context;
  final String upiId;

  ManualPaymentGateway(this.context, this.upiId);

  @override
  Future<PaymentResponse> process(PaymentRequest request) async {
    //call api
    var response = await MutationRepository.instance
        .depositUPIManual(upiId, request.amount);

    if (response.hasErrors) {
      return PaymentResponse(
          success: false,
          transactionId: null,
          error: PaymentError(response.errors!.first.message));
    } else if (response.data!.depositUPIManual.success) {
      return PaymentResponse(
          success: true,
          transactionId: response.data!.depositUPIManual.transactionId,
          error: null);
    }
    return PaymentResponse(
        success: false,
        transactionId: response.data!.depositUPIManual.transactionId,
        error: PaymentError("Transaction failed. Try again."));
  }
}
