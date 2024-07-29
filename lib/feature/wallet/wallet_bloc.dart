import 'package:flutter/cupertino.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/repository/mutation_repo.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/common/services/payout/gateways/manual_payment_gateway.dart';
import 'package:gamerboard/common/services/payout/gateways/phone_pe_payment_gateway.dart';
import 'package:gamerboard/common/services/payout/models/payment_request.dart';
import 'package:gamerboard/common/services/payout/models/payment_response.dart';
import 'package:gamerboard/common/services/payout/pyament_service.dart';
import 'package:gamerboard/feature/wallet/wallet_states.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/shared_preferences.dart';
import 'package:gamerboard/utils/ui_utils.dart';
import 'package:gb_payment_plugin/upi_app.dart';
import 'package:sprintf/sprintf.dart';
////Created by saurabh.lahoti on 28/01/22

class WalletBloc extends Cubit<WalletState> {
  final _paymentService = PaymentService();
  late UserMixin? user;
  double amountForDeposit = 0;
  double amountToWithdraw = 0;
  bool walletChanged = false;
  final checkPaymentStatusTimeoutInterval = 15 * 60; // 15 minutes

  WalletBloc() : super(WalletLoading()) {}

  void loadData() async {
    var response = await QueryRepository.instance
        .getMyProfile(getCached: walletChanged ? false : true);
    user = response.data?.me;
    walletChanged = false;
    if (user != null) {
     _safeEmit(WalletLoaded(
          user!.wallet.deposit, user!.wallet.bonus, user!.wallet.winning));
      getTransactions();
    }
  }

  bool _handleDeposit(PaymentResponse response) {
    if (response.success && response.status == TransactionStatus.successful) {
     _safeEmit(DepositTransactionCompleted(amountForDeposit.round().toDouble(),
          txnId: response.transactionId));
      walletChanged = true;
      AnalyticService.getInstance()
          .trackEvents(Events.DEPOSIT_COMPLETED, properties: {
        "amount": amountForDeposit.round().toDouble(),
        "txnId": response.transactionId,
      });
      return true;
    } else if ([
      TransactionStatus.failed,
      TransactionStatus.cancelled,
      TransactionStatus.artemisUnknown
    ].contains(response.status)) {
      _onDepositTransactionFailed(response);
      return true;
    }
    return false;
  }

  void depositAmountDecided(BuildContext context, UpiApp upiApp) async {
    AnalyticService.getInstance().trackEvents(
        Events.WALLET_DEPOSIT_BUTTON_CLICKED,
        properties: {"amount": amountForDeposit.round()});
    UiUtils.getInstance.alertDialog(
        context,
        AppStrings.depositConfirmation,
        sprintf(AppStrings.withdrawalConfirmationDesc, [
          AppStrings.deposit,
          amountForDeposit.round().toString()
        ]), yesAction: () async {
      await _startPaymentFlow(context, upiApp);
    }, noAction: () {
     _safeEmit(DepositLoaded());
      AnalyticService.getInstance().trackEvents(
          Events.WALLET_DEPOSIT_POPUP_DISMISSED,
          properties: {"amount": amountForDeposit.round()});
    });
  }

  Future<void> _startPaymentFlow(BuildContext context, UpiApp upiApp) async {
   _safeEmit(DepositProcessing());
    UiUtils.getInstance.buildLoading(context, dismissible: false);
    var gateway = PhonePePaymentGateway(upiApp.packageName);
    PaymentResponse response = await _paymentService.startPayment(
        gateway, PaymentRequest(amountForDeposit.round().toDouble()));
    walletChanged = true;

    var checkDelay = 3;
    var timeout = 0;
    Navigator.pop(context);
   _safeEmit(DepositTransactionPending());

    bool status = _handleDeposit(response);
    while (!status && timeout < checkPaymentStatusTimeoutInterval) {
      if (response.status == TransactionStatus.processing &&
          response.ledgerId != null) {
        response = await gateway.checkStatus(response.ledgerId!);
        status = _handleDeposit(response);
        if (status) {
          return;
        }
        await Future.delayed(Duration(seconds: checkDelay));
        if (checkDelay < 60) {
          checkDelay *= 2;
        }
        timeout += checkDelay;
      } else {
        _onDepositTransactionFailed(response);
        return;
      }
    }
    if (timeout > 0) {
      _onDepositTransactionTimeout(response);
      return;
    }
    _onDepositTransactionFailed(response);
  }

  void _onDepositTransactionTimeout(PaymentResponse response) {
   _safeEmit(DepositTransactionCompleted(amountForDeposit.round().toDouble(),
        txnId: response.transactionId,
        error: AppStrings.paymentRequestTimeout));
    AnalyticService.getInstance().trackEvents(Events.DEPOSIT_FAILED, properties: {
      "amount": amountForDeposit.round().toDouble(),
      "txnId": response.transactionId,
      "reason": AppStrings.paymentRequestTimeout,
      "status": response.status?.name,
    });
  }

  void _safeEmit(WalletState state){
    if(isClosed) return;
   emit(state);
  }
  void _onDepositTransactionFailed(PaymentResponse response) {
   _safeEmit(DepositTransactionCompleted(amountForDeposit.round().toDouble(),
        txnId: response.transactionId,
        error: response.error?.errorMsg ?? "Transaction failed. Try again."));
    AnalyticService.getInstance().trackEvents(Events.DEPOSIT_FAILED, properties: {
      "amount": amountForDeposit.round().toDouble(),
      "txnId": response.transactionId,
      "reason": response.error?.errorMsg,
      "status": response.status?.name,
    });
  }

  void depositConfirm(BuildContext context, String upiId) async {
    UiUtils.getInstance.alertDialog(
        context,
        AppStrings.depositConfirmation,
        sprintf(AppStrings.withdrawalConfirmationDesc, [
          AppStrings.deposit,
          amountForDeposit.toString()
        ]), yesAction: () async {
     _safeEmit(DepositProcessing());
      UiUtils.getInstance.buildLoading(context, dismissible: false);
      var gateway = ManualPaymentGateway(context, upiId);
      PaymentResponse response = await _paymentService.startPayment(
          gateway, PaymentRequest(amountForDeposit));
      if (response.success) {
       _safeEmit(DepositTransactionCompleted(amountForDeposit,
            txnId: response.transactionId));
        walletChanged = true;
        SharedPreferenceHelper.getInstance
            .setStringPref(PrefKeys.UPI_ID, upiId);
        return;
      }
      _onDepositTransactionFailed(response);
    }, noAction: () {
     _safeEmit(DepositLoaded());
    });
  }

  void withDraw(BuildContext context, String upiId) async {
   _safeEmit(WithdrawalProcessing());
    UiUtils.getInstance.alertDialog(
        context,
        AppStrings.withdrawalConfirmation,
        sprintf(AppStrings.withdrawalConfirmationDesc, [
          AppStrings.withdraw,
          amountToWithdraw.toString()
        ]), yesAction: () async {
      UiUtils.getInstance.buildLoading(context, dismissible: false);
      var response = await MutationRepository.instance
          .withdrawAmount(upiId, amountToWithdraw);
      Navigator.of(context).pop();
      if (response.hasErrors) {
        ///////////api failure
       _safeEmit(WithdrawTransactionCompleted(amountToWithdraw,
            error: response.errors!.first.message));
      } else {
        if (response.data!.withdrawUPI.success) {
          ///////////success
         _safeEmit(WithdrawTransactionCompleted(amountToWithdraw,
              txnId: response.data!.withdrawUPI.transactionId));
          walletChanged = true;
          SharedPreferenceHelper.getInstance
              .setStringPref(PrefKeys.UPI_ID, upiId);
        } else
         _safeEmit(WithdrawTransactionCompleted(amountToWithdraw,
              txnId: response.data!.withdrawUPI.transactionId,
              error: "Transaction failed. Try again."));
      }
    }, noAction: () {
     _safeEmit(WithdrawalAmountConfirmed(upiId));
    });
  }

  Future<void> getTransactions() async {
   _safeEmit(TransactionsLoading());
    var response = await QueryRepository.instance.getWalletLedger();
    if (response.hasErrors) {
      UiUtils.getInstance.showToast(response.errors!.first.message);
    } else {
     _safeEmit(TransactionsLoaded(response.data!.transactions));
    }
    return Future.value();
  }

  void selectAmountForDeposit(double? amount) {
    if (amount != null && amount != amountForDeposit) {
      amountForDeposit = amount;
     _safeEmit(AmountChanged(amount));
    } else if (amount == null && amountForDeposit >= 0) {
     _safeEmit(AmountChanged(amountForDeposit, isCustom: true));
    }
  }

  void selectAmountForWithdrawal(double? amount) {
    if (amount != null && amount != amountToWithdraw) {
      amountToWithdraw = amount;
     _safeEmit(AmountChanged(amount));
    } else if (amount == null && amountToWithdraw >= 0) {
     _safeEmit(AmountChanged(amountToWithdraw, isCustom: true));
    }
  }

  void withDrawAmountDecided() async {
   _safeEmit(WithdrawalAmountConfirmed(await SharedPreferenceHelper.getInstance
        .getStringPref(PrefKeys.UPI_ID)));
  }

  void downloadUpiQr() {
    UiUtils.getInstance.saveQrCode();
  }

  void copyUpiToClipboard(String upiId) {
    UiUtils.getInstance.copyToClipboard(upiId);
  }

  void upiAppSelected(UpiApp upiApp) {
   _safeEmit(DepositUpiAppSelected(amountToWithdraw, upiApp));
    AnalyticService.getInstance()
        .trackEvents(Events.UPI_APP_SELECTED, properties: {
      "name": upiApp.applicationName,
      "packageName": upiApp.packageName,
      "version": upiApp.version
    });
  }

  onUpiAppsLoaded(List<UpiApp> upiApps) {
    if (upiApps.isEmpty) {
      AnalyticService.getInstance().trackEvents(Events.NO_UPI_INSTALLED);
    }
  }

  testAppRestart() {
    Future.delayed(Duration(seconds: 7),
        () => Constants.PLATFORM_CHANNEL.invokeMethod("test_app_restart"));
  }
}
