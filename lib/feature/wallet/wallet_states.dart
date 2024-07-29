import 'package:gamerboard/graphql/query.dart';
import 'package:gb_payment_plugin/upi_app.dart';

////Created by saurabh.lahoti on 28/01/22
abstract class WalletState {}

class WalletLoading extends WalletState {}

class TransactionsLoading extends WalletState {}

class TransactionsLoaded extends WalletState {
  List<GetTransactions$Query$LedgerTransaction> transactions;

  TransactionsLoaded(this.transactions);
}

class WalletLoaded extends WalletState {
  double deposit;
  double bonus;
  double winnings;

  WalletLoaded(this.deposit, this.bonus, this.winnings);
}

class WithdrawalLoaded extends WalletState {}

class WithdrawalAmountConfirmed extends WalletState {
  String? upiId;

  WithdrawalAmountConfirmed(this.upiId);
}

class AmountChanged extends WalletState {
  double amount;
  bool isCustom;

  AmountChanged(this.amount, {this.isCustom = false});
}

class WithdrawAmountChanged extends WalletState {
  double amount;

  WithdrawAmountChanged(this.amount);
}

class WithdrawTransactionCompleted extends WalletState {
  double amount;
  String? error;
  String? txnId;

  WithdrawTransactionCompleted(this.amount, {this.txnId, this.error});
}
class DepositTransactionCompleted extends WalletState {
  double amount;
  String? error;
  String? txnId;
  TransactionStatus? status;
  DepositTransactionCompleted(this.amount, {this.txnId, this.error, this.status});
}
class DepositTransactionPending extends WalletState {
  DepositTransactionPending();
}

class WalletEntriesLoaded extends WalletState {}

class WithdrawalProcessing extends WalletState{}

class DepositLoaded extends WalletState{}
class DepositConfirmation extends WalletState{
  String upiId;
  DepositConfirmation(this.upiId);
}
class DepositProcessing extends WalletState{
  DepositProcessing();
}

class DepositUpiAppSelected extends WalletState{
  double amount;
  UpiApp upiApp;

  DepositUpiAppSelected(this.amount, this.upiApp);
}
