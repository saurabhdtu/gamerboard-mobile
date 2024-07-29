import 'package:gamerboard/common/repository/mutation_repo.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/common/services/payout/gateways/payment_gateway.dart';
import 'package:gamerboard/common/services/payout/models/payment_request.dart';
import 'package:gamerboard/common/services/payout/models/payment_response.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gb_payment_plugin/phonepe_payment_response.dart';
import 'package:gb_payment_plugin/phonepe_sdk.dart';
import 'package:gb_payment_plugin/upi_payment_request.dart';

class PhonePePaymentGateway extends PaymentGateway {
  final String _packageName;
  final PhonepeSdk _phonepeSdk = PhonepeSdk();
  PhonePePaymentGateway(this._packageName);

  @override
  Future<PaymentResponse> process(PaymentRequest request) async {
    final response = await MutationRepository.instance
        .paymentCreation((request.amount).toInt(), _packageName);
    if (response.data?.paymentCreation.ledgerId == null) {
      return PaymentResponse(
          success: false,
          transactionId: null,
          status: TransactionStatus.failed,
          error: PaymentError(
             AppStrings.unableToProcessPayment));
    }
    var ledgerId = response.data?.paymentCreation.ledgerId;
    if (response.hasErrors) {
      return PaymentResponse(
          success: false,
          transactionId: null,
          status: TransactionStatus.failed,
          error: PaymentError(response.errors!.first.message));
    }

    final redirectUrl = response.data!.paymentCreation.intentUrl;

    await _phonepeSdk
        .startPayment(UpiPaymentRequest(redirectUrl, _packageName));

    //We may need this in future so keeping it commented for now.
    /*if (_checkForCancelledPayment(phonePePaymentResponse.status)) {
      AnalyticUtils.getInstance().trackEvents(Events.DEPOSIT_CANCELLED, properties: {
        "reason" : "The upi app was closed before completing transaction"
      });
      return PaymentResponse(
          success: false,
          transactionId: null,
          status: TransactionStatus.cancelled,
          error: PaymentError(AppStrings.transactionCancelled));
    }*/
    return PaymentResponse(
        success: false,
        ledgerId: ledgerId!,
        status: TransactionStatus.processing,);
  }


  bool _checkForCancelledPayment(PhonePeStatus status) {
    return status == PhonePeStatus.cancelled;
  }

  @override
  Future<PaymentResponse> checkStatus(int transactionId) async {
    final response = await QueryRepository.instance.transaction(transactionId);
    if (response.data == null) {
      return PaymentResponse(
          success: false,
          transactionId: null,
          error: PaymentError(AppStrings.paymentMessageDesc));
    }
    return PaymentResponse(
        success: true,
        transactionId:
            response.data?.transaction?.transactionId.toString() ?? "",
        ledgerId: transactionId,
        status: response.data?.transaction?.status ?? TransactionStatus.processing);
  }
}