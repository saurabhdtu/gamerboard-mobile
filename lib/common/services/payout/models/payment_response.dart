import 'package:gamerboard/graphql/query.dart';

class PaymentResponse{
  bool success;
  String? transactionId;
  int? ledgerId;
  PaymentError?  error;
  TransactionStatus?  status = TransactionStatus.processing;
  PaymentResponse({required this.success, this.transactionId, this.ledgerId, this.error, this.status});
}

class PaymentError{
  String? errorMsg;
  PaymentError(this.errorMsg);
}