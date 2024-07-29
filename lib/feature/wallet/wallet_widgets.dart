import 'dart:convert';

import 'package:firebase_remote_config/firebase_remote_config.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/wallet/wallet_bloc.dart';
import 'package:gamerboard/feature/wallet/wallet_deposit.dart';
import 'package:gamerboard/feature/wallet/wallet_states.dart';
import 'package:gamerboard/feature/wallet/wallet_withdraw.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/data_type_ext.dart';
import 'package:gamerboard/utils/time_utils.dart';
import 'package:gamerboard/utils/ui_utils.dart';
import 'package:gamerboard/utils/validators.dart';
import 'package:gb_payment_plugin/upi_app.dart';
import 'package:gb_payment_plugin/upi_app_chooser.dart';
import 'package:lottie/lottie.dart';

import '../../common/widgets/containers.dart';
import '../../utils/share_utils.dart';
////Created by saurabh.lahoti on 28/01/22

class WalletWidgets {
  final WalletBloc _walletBloc;

  WalletWidgets(this._walletBloc);

  Widget getWalletHomeRightPane(
      BuildContext context, WalletLoaded state, double width) {
    return Container(
        width: width,
        child: DecoratedBox(
            decoration: BoxDecoration(color: AppColor.colorGrayBg2),
            child: SingleChildScrollView(
                child: Padding(
                    padding: EdgeInsets.all(20.0),
                    child: Column(
                        mainAxisSize: MainAxisSize.max,
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          RegularText(AppStrings.total,
                              color: AppColor.textSubTitle),
                          const SizedBox(height: 3.0),
                          BoldText(
                              "${AppStrings.rupeeSymbol}${(state.bonus + state.winnings + state.deposit).formattedNumber()}",
                              fontSize: 36.0),
                          const SizedBox(height: 8.0),
                          Row(
                              crossAxisAlignment:
                              CrossAxisAlignment.start,
                              children: [
                                RegularText(AppStrings.needHelp,
                                    color: AppColor.textSubTitle,
                                    height: 1.1,
                                    fontSize: 12.0),
                                const SizedBox(
                                  width: 2,
                                ),
                                InkWell(
                                  onTap: () {
                                    openUrlInExternalBrowser(
                                        FirebaseRemoteConfig.instance
                                            .getString(
                                            RemoteConfigConstants
                                                .GB_DISCORD_LINK));
                                  },
                                  child: Text(
                                    AppStrings.joinDiscordServer,
                                    style: RegularTextStyle(
                                        color: AppColor.successGreen,
                                        height: 1.2,
                                        decoration: TextDecoration.underline,
                                        fontSize: 12.0),
                                  ),
                                )
                              ]),
                          const SizedBox(height: 8.0),
                          Row(children: [
                            cardContainer(
                                Column(children: [
                                  RegularText(AppStrings.deposit,
                                      fontSize: 10.0,
                                      color: AppColor.textSubTitle),
                                  const SizedBox(width: 3.0),
                                  RegularText(
                                      "${AppStrings.rupeeSymbol}${(state.deposit).formattedNumber()}",
                                      fontSize: 14.0)
                                ]),
                                padding: const EdgeInsets.all(8.0),
                                width: 70.0),
                            const SizedBox(width: 7.0),
                            Expanded(
                                child: secondaryButton(AppStrings.deposit,
                                    () async {
                              var map = jsonDecode(FirebaseRemoteConfig.instance
                                  .getString(
                                      RemoteConfigConstants.WALLET_FLAGS));
                              if (map['deposit_enabled'] == true) {
                                if (_walletBloc.user?.wallet != null) {
                                  await Navigator.of(context).push(
                                      MaterialPageRoute(
                                          builder: (context) =>
                                              WalletDepositPage()));
                                  if (_walletBloc.walletChanged) {
                                    _walletBloc.loadData();
                                  }
                                }
                              } else {
                                UiUtils.getInstance.showToast(
                                    map['deposit_message'].toString());
                              }
                            }, paddingHorizontal: 8.0))
                          ]),
                          const SizedBox(height: 10.0),
                          Row(children: [
                            cardContainer(
                                Column(children: [
                                  RegularText(AppStrings.winnings,
                                      fontSize: 10.0,
                                      color: AppColor.textSubTitle),
                                  const SizedBox(height: 3.0),
                                  RegularText(
                                      "${AppStrings.rupeeSymbol}${(state.winnings).formattedNumber()}",
                                      fontSize: 14.0)
                                ]),
                                padding: const EdgeInsets.all(8.0),
                                width: 70.0),
                            const SizedBox(width: 7.0),
                            Expanded(
                                child: secondaryButton(AppStrings.withdraw,
                                    () async {
                              var map = jsonDecode(FirebaseRemoteConfig.instance
                                  .getString(
                                      RemoteConfigConstants.WALLET_FLAGS));
                              if (map['withdrawal_enabled'] == true) {
                                if (_walletBloc.user?.wallet != null) {
                                  await Navigator.of(context).push(
                                      MaterialPageRoute(
                                          builder: (context) =>
                                              WalletWithdrawPage()));
                                  if (_walletBloc.walletChanged) {
                                    _walletBloc.loadData( );
                                  }
                                }
                              } else {
                                UiUtils.getInstance.showToast(
                                    map['withdrawal_message'].toString());
                              }
                            }, inverted: true, paddingHorizontal: 8.0))
                          ]),
                          const SizedBox(height: 7.0),
                          Row(children: [
                            InkWell(
                              onTap: () => kDebugMode
                                  ? _walletBloc.testAppRestart()
                                  : {},
                              child: cardContainer(
                                  Column(children: [
                                    RegularText(AppStrings.bonus,
                                        fontSize: 10.0,
                                        color: AppColor.textSubTitle),
                                    const SizedBox(height: 3.0),
                                    RegularText(
                                        "${AppStrings.rupeeSymbol}${(state.bonus).formattedNumber()}",
                                        fontSize: 14.0)
                                  ]),
                                  padding: const EdgeInsets.all(8.0),
                                  width: 70.0),
                            ),
                            const SizedBox(width: 7.0),
                            Expanded(
                                child: RegularText(AppStrings.bonusDesc,
                                    fontSize: 10.0,
                                    color: AppColor.textSubTitle))
                          ])
                        ])))));
  }

  Widget getTransactionItem(
          GetTransactions$Query$LedgerTransaction transaction) =>
      Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        const SizedBox(height: 10.0),
        Row(children: [
          Expanded(child: BoldText(transaction.description, fontSize: 14.0)),
          const SizedBox(width: 8.0),
          BoldText(
              "${AppStrings.rupeeSymbol}${(transaction.amount.abs()).formattedNumber()}",
              fontSize: 14.0,
              textAlign: TextAlign.end,
              color: transaction.amount > 0
                  ? AppColor.transactionCredit
                  : AppColor.transactionDebit),
        ]),
        const SizedBox(height: 4.0),
        Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Expanded(
              child: RegularText(
                  "${AppStrings.from} ${transaction.toJson()['subWallet'].toString()} ${AppStrings.wallet.toLowerCase()}",
                  fontSize: 12.0)),
          const SizedBox(width: 8.0),
          RegularText(
              TimeUtils.instance.formatCardDateTime(transaction.createdAt),
              fontSize: 12.0,
              textAlign: TextAlign.end)
        ]),
        const SizedBox(height: 4.0),
        Row(crossAxisAlignment: CrossAxisAlignment.start, children: [
          Expanded(
              child: InkWell(
            onTap: () =>
                UiUtils.getInstance.copyToClipboard(transaction.transactionId),
            child: RegularText("Txn id: ${transaction.transactionId}",
                fontSize: 12.0,
                textAlign: TextAlign.start,
                color: AppColor.textDarkGray),
          )),
          const SizedBox(width: 8.0),
          if ([
            TransactionStatus.processing,
            TransactionStatus.cancelled,
            TransactionStatus.failed
          ].contains(transaction.status))
            Row(mainAxisSize: MainAxisSize.min, children: [
              Icon(
                  transaction.status == TransactionStatus.failed
                      ? Icons.error
                      : (transaction.status == TransactionStatus.processing
                          ? Icons.change_circle
                          : Icons.cancel),
                  color: _getTransactionStatusTextColor(transaction),
                  size: 12.0),
              const SizedBox(width: 3.0),
              RegularText(transaction.status.name.capitalizeFirstCharacter(),
                  fontSize: 12.0,
                  color: _getTransactionStatusTextColor(transaction),
                  textAlign: TextAlign.end)
            ])
        ]),
        const SizedBox(height: 10.0)
      ]);

  Color _getTransactionStatusTextColor(
      GetTransactions$Query$LedgerTransaction transaction) {
    return transaction.status == TransactionStatus.failed
        ? AppColor.errorRed.withAlpha(150)
        : (transaction.status == TransactionStatus.processing
            ? AppColor.highlightOrange.withAlpha(150)
            : AppColor.textDarkGray);
  }

  Widget depositCompletedPopup(
      BuildContext context, double amount, String? txnId) {
    List<Widget> widgets = [];
    widgets.addAll([
      TitleText(AppStrings.paymentMessageTitle,
          fontSize: 16.0, color: AppColor.textSubTitle),
      const SizedBox(
        height: 4,
      ),
      RegularText(AppStrings.paymentMessageDesc,
          fontSize: 12.0, color: AppColor.textDarkGray),
    ]);
    if (txnId != null) {
      widgets.addAll([
        const SizedBox(
          height: 8,
        ),
        RegularText("Transaction Id:",
            color: AppColor.textDarkGray, fontSize: 11.0),
        const SizedBox(height: 4.0),
        RegularText(txnId, fontSize: 13.0, color: AppColor.textSubTitle),
      ]);
    }

    widgets.addAll([
      const SizedBox(height: 20.0),
      secondaryButton(AppStrings.close, () {
        Navigator.of(context)
            .popUntil((route) => route.settings.name == Routes.WALLET);
      })
    ]);
    return Container(
        padding: const EdgeInsets.all(10.0),
        width: MediaQuery.of(context).size.width / 2,
        height: MediaQuery.of(context).size.height / (txnId != null ? 2 : 2.2),
        decoration: BoxDecoration(gradient: AppColor.popupBackgroundGradient),
        child: Center(
            child: Column(mainAxisSize: MainAxisSize.min, children: widgets)));
  }

  Widget depositProcessingPopup(BuildContext context, {Function()? onClose}) {
    List<Widget> widgets = [
      Lottie.asset("$lottieAssets${"lottie_pending.json"}",
          width: 60.0, height: 60.0),
      const SizedBox(height: 15.0)
    ];
    widgets.addAll([
      TitleText(AppStrings.processingPayment,
          fontSize: 16.0, color: AppColor.textSubTitle),
      const SizedBox(
        height: 4,
      ),
    ]);

    widgets.addAll([
      const SizedBox(height: 20.0),
      secondaryButton(AppStrings.close, () {
        onClose?.call();
        Navigator.of(context)
            .popUntil((route) => route.settings.name == Routes.WALLET);
      })
    ]);
    return Container(
        padding: const EdgeInsets.all(10.0),
        width: MediaQuery.of(context).size.width / 2,
        height: MediaQuery.of(context).size.height / 2,
        decoration: BoxDecoration(gradient: AppColor.popupBackgroundGradient),
        child: Center(
            child: Column(mainAxisSize: MainAxisSize.min, children: widgets)));
  }

  Widget transactionPopup(BuildContext context, bool isWithdraw, double amount,
      {String? txnId, String? error, TransactionStatus? status}) {
    List<Widget> widgets = [
      Lottie.asset(
          "$lottieAssets${error == null ? "tick.json" : "lottie_error.json"}",
          width: 60.0,
          height: 60.0),
      const SizedBox(height: 15.0),
      BoldText("${AppStrings.rupeeSymbol}$amount", fontSize: 30.0),
      const SizedBox(height: 15.0)
    ];
    if (status == TransactionStatus.processing) {
      widgets.addAll([
        RegularText(AppStrings.msgPendingTransaction,
            fontSize: 12.0, color: AppColor.textDarkGray),
        const SizedBox(height: 15.0)
      ]);
    }
    if (error == null) {
      widgets.addAll([
        RegularText("Transaction Id:",
            color: AppColor.textDarkGray, fontSize: 11.0),
        const SizedBox(height: 4.0),
        RegularText(txnId!, fontSize: 13.0, color: AppColor.textSubTitle),
        const SizedBox(height: 6.0),
        RegularText(
            "${isWithdraw ? AppStrings.withdrawalSuccess : AppStrings.depositSuccess}",
            fontSize: 18.0)
      ]);
    } else {
      widgets.addAll([
        RegularText("Transaction failed",
            color: AppColor.textDarkGray, fontSize: 11.0),
        const SizedBox(height: 6.0),
        RegularText("Error occurred", fontSize: 13.0),
        const SizedBox(height: 4.0),
        RegularText(error, fontSize: 15.0, textAlign: TextAlign.center),
        const SizedBox(height: 16.0),

        InkWell(
          onTap: () {
            openUrlInExternalBrowser(
                FirebaseRemoteConfig.instance
                    .getString(
                    RemoteConfigConstants
                        .GB_DISCORD_LINK));
          },
          child: Padding(
              padding: const EdgeInsets.symmetric(
                  vertical: 1.0),
              child: Text(
                AppStrings.joinDiscordServer,
                textAlign: TextAlign.center,
                style: RegularTextStyle(
                    color: AppColor.successGreen,
                    decoration: TextDecoration.underline,
                    height: 1.2,
                    fontSize: 12.0),
              )),
        )
      ]);
    }
    widgets.addAll([
      const SizedBox(height: 20.0),
      secondaryButton(AppStrings.finish, () {
        Navigator.of(context)
            .popUntil((route) => route.settings.name == Routes.WALLET);
      })
    ]);
    return Container(
        padding: EdgeInsets.all(10.0),
        width: MediaQuery.of(context).size.width *
            (status == TransactionStatus.processing ? .6 : .5),
        decoration: BoxDecoration(gradient: AppColor.popupBackgroundGradient),
        child: Center(
            child: Column(mainAxisSize: MainAxisSize.min, children: widgets)));
  }

  Widget depositSuccessPopup(
      BuildContext context, bool isWithdraw, double amount,
      {String? txnId, String? error}) {
    List<Widget> widgets = [
      Lottie.asset(
          "$lottieAssets${error == null ? "tick.json" : "lottie_error.json"}",
          width: 60.0,
          height: 60.0),
      const SizedBox(height: 15.0),
      BoldText("${AppStrings.rupeeSymbol}$amount", fontSize: 30.0),
      const SizedBox(height: 15.0)
    ];
    if (error == null) {
      widgets.addAll([
        RegularText("Transaction Id:",
            color: AppColor.textDarkGray, fontSize: 11.0),
        const SizedBox(height: 4.0),
        RegularText(txnId!, fontSize: 13.0, color: AppColor.textSubTitle),
        const SizedBox(height: 6.0),
        RegularText(
            "${isWithdraw ? AppStrings.withdrawalSuccess : AppStrings.depositSuccess}",
            fontSize: 18.0)
      ]);
    } else {
      widgets.addAll([
        RegularText("Transaction failed",
            color: AppColor.textDarkGray, fontSize: 11.0),
        const SizedBox(height: 6.0),
        RegularText("Error occurred", fontSize: 13.0),
        const SizedBox(height: 4.0),
        RegularText(error, fontSize: 15.0, textAlign: TextAlign.center)
      ]);
    }
    widgets.addAll([
      const SizedBox(height: 20.0),
      secondaryButton(AppStrings.finish, () {
        Navigator.of(context)
            .popUntil((route) => route.settings.name == Routes.WALLET);
      })
    ]);
    return Container(
        padding: const EdgeInsets.all(10.0),
        width: MediaQuery.of(context).size.width * .5,
        decoration: BoxDecoration(gradient: AppColor.popupBackgroundGradient),
        child: Center(
            child: Column(mainAxisSize: MainAxisSize.min, children: widgets)));
  }
}

class AmountPickerDeposit extends StatefulWidget {
  final bool isWithdraw;

  AmountPickerDeposit({this.isWithdraw = false});

  @override
  State<StatefulWidget> createState() => _AmountPickerDepositState();
}

class _AmountPickerDepositState extends State<AmountPickerDeposit> {
  late WalletBloc _walletBloc;
  var amounts = [50.0, 100.0, 200.0, 300.0, 500.0];

  @override
  Widget build(BuildContext context) {
    if (widget.isWithdraw) {
      if (_walletBloc.user?.wallet.winning != null)
        amounts = [_walletBloc.user!.wallet.winning];
    }
    return BlocListener<WalletBloc, WalletState>(
        listener: (context, state) {
          if (state is AmountChanged) setState(() {});
        },
        child: Padding(
            padding: const EdgeInsets.fromLTRB(30.0, 20.0, 30.0, 0.0),
            child: Center(
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: [
                  RegularText(widget.isWithdraw
                      ? AppStrings.amountWithdraw
                      : AppStrings.fundsToDeposit),
                  const SizedBox(height: 15.0),
                  Expanded(
                      child: GridView.builder(
                          gridDelegate:
                              SliverGridDelegateWithFixedCrossAxisCount(
                                  crossAxisCount: 3,
                                  childAspectRatio: 2,
                                  crossAxisSpacing: 20.0,
                                  mainAxisSpacing: 20.0),
                          itemBuilder: (context, index) {
                            int containIndex = amounts.indexOf(widget.isWithdraw
                                ? _walletBloc.amountToWithdraw
                                : _walletBloc.amountForDeposit);
                            return secondaryButton(
                                index == amounts.length
                                    ? "Custom"
                                    : "${AppStrings.rupeeSymbol}${widget.isWithdraw ? amounts[index] : amounts[index].toInt()}",
                                () {
                              widget.isWithdraw
                                  ? _walletBloc.selectAmountForWithdrawal(
                                      index == amounts.length
                                          ? null
                                          : amounts[index])
                                  : _walletBloc.selectAmountForDeposit(
                                      index == amounts.length
                                          ? null
                                          : amounts[index]);
                            },
                                fontSize: 20.0,
                                inverted: !(containIndex == index ||
                                    (index == amounts.length &&
                                        containIndex == -1 &&
                                        (widget.isWithdraw
                                            ? _walletBloc.amountToWithdraw > 0
                                            : _walletBloc.amountForDeposit >
                                                0))),
                                paddingVertical: 10.0);
                          },
                          itemCount: amounts.length + 1))
                ]))),
        bloc: _walletBloc,
        listenWhen: (previous, current) => current is AmountChanged);
  }

  @override
  void initState() {
    super.initState();
    _walletBloc = context.read<WalletBloc>();
  }
}

class AddDeposit extends StatefulWidget {
  final bool isWithdraw;

  @override
  State<StatefulWidget> createState() => _AddDepositState();

  AddDeposit({this.isWithdraw = false});
}

class _AddDepositState extends State<AddDeposit> {
  late WalletBloc _walletBloc;
  late TextEditingController _controller;
  String? isAmountValid = "";
  UpiApp? _selectedUpiApp;
  bool _popDismissed = false;
  FocusNode _node = FocusNode();

  @override
  Widget build(BuildContext context) => BlocListener<WalletBloc, WalletState>(
      listener: (context, state) {
        if (state is AmountChanged) {
          if (state.isCustom) {
            FocusScope.of(context).requestFocus(_node);
          }
          if (state.amount != double.tryParse(_controller.text)) {
            setState(() {
              if (state.amount == 0)
                _controller.text = "";
              else {
                isAmountValid = null;
                _controller.text = state.amount.toString();
              }
            });
          }
        }
        if (state is DepositUpiAppSelected) {
          _selectedUpiApp = state.upiApp;
        }
        if (state is DepositTransactionPending) {
          UiUtils.getInstance.showCustomDialog(
              context,
              WalletWidgets(_walletBloc).depositProcessingPopup(context,
                  onClose: () {
                _popDismissed = true;
              }),
              dismissible: false);
        }
        if (state is DepositTransactionCompleted) {
          if (!_popDismissed) {
            UiUtils.getInstance.showCustomDialog(
                context,
                WalletWidgets(_walletBloc).transactionPopup(
                    context, false, state.amount.round().toDouble(),
                    status: state.status,
                    error: state.error,
                    txnId: state.txnId),
                dismissible: false);
          }
        }
      },
      child: SingleChildScrollView(
          child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: [
            RegularText(
                widget.isWithdraw
                    ? AppStrings.amountWithdraw
                    : AppStrings.amountToAdd,
                color: AppColor.textSubTitle),
            TextField(
                autofocus: false,
                focusNode: _node,
                decoration: InputDecoration(
                    contentPadding: const EdgeInsets.all(0),
                    focusColor: AppColor.colorAccent,
                    prefix: RegularText(AppStrings.rupeeSymbol, fontSize: 40.0),
                    label: null),
                keyboardType: TextInputType.numberWithOptions(
                    signed: false, decimal: true),
                inputFormatters: [
                  FilteringTextInputFormatter.allow(RegExp(r'^\d*\.?\d{0,2}'))
                ],
                onChanged: (value) {
                  var amount = value.isEmpty ? null : double.tryParse(value);
                  setState(() {
                    _validateAmount(amount);
                  });
                },
                style: RegularTextStyle(fontSize: 40.0),
                controller: _controller),
            const SizedBox(height: 5.0),
            if (isAmountValid != null && isAmountValid!.isNotEmpty)
              RegularText(isAmountValid!,
                  color: AppColor.errorRed, fontSize: 10.0),
            const SizedBox(height: 15.0),
            if (!widget.isWithdraw) _buildUpiAppsChooser(),
            Row(children: [
              Expanded(
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                    RegularText(
                        widget.isWithdraw
                            ? AppStrings.availableBalance
                            : AppStrings.currentTotal,
                        fontSize: 12.0,
                        color: AppColor.textDarkGray),
                    const SizedBox(height: 3.0),
                    BoldText(widget.isWithdraw
                        ? "${AppStrings.rupeeSymbol} ${(_walletBloc.user!.wallet.winning).formattedNumber()}"
                        : "${AppStrings.rupeeSymbol} ${(_walletBloc.user!.wallet.winning + _walletBloc.user!.wallet.deposit + _walletBloc.user!.wallet.bonus).formattedNumber()}")
                  ])),
              Expanded(
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                    RegularText(
                        widget.isWithdraw
                            ? AppStrings.afterWithdrawal
                            : AppStrings.totalAfterDeposit,
                        fontSize: 12.0,
                        color: AppColor.textDarkGray),
                    const SizedBox(height: 3.0),
                    BoldText(widget.isWithdraw
                        ? "${AppStrings.rupeeSymbol} ${((_walletBloc.user?.wallet.winning ?? 0) - (_controller.text.isNotEmpty ? (double.tryParse(_controller.text) ?? 0) : 0)).formattedNumber()}"
                        : "${AppStrings.rupeeSymbol} ${(_walletBloc.user!.wallet.winning + _walletBloc.user!.wallet.deposit + _walletBloc.user!.wallet.bonus + (_controller.text.isNotEmpty ? (double.tryParse(_controller.text) ?? 0) : 0)).formattedNumber()}")
                  ]))
            ]),
            const SizedBox(height: 20.0),
            BlocBuilder(
              builder: (context, state) {
                return secondaryButton(
                    widget.isWithdraw ? AppStrings.next : AppStrings.deposit,
                    () {
                  if (isAmountValid == null) if (widget.isWithdraw) {
                    _walletBloc.withDrawAmountDecided();
                  } else if (_selectedUpiApp != null) {
                    _walletBloc.depositAmountDecided(context, _selectedUpiApp!);
                  }
                },
                    active: isAmountValid == null &&
                        ((widget.isWithdraw == false &&
                                _selectedUpiApp != null) ||
                            widget.isWithdraw) &&
                        state is! DepositProcessing);
              },
              bloc: _walletBloc,
              buildWhen: (previous, current) =>
                  current is DepositProcessing || current is AmountChanged,
            )
          ])),
      listenWhen: (previous, current) =>
          current is AmountChanged ||
          current is DepositUpiAppSelected ||
          current is DepositTransactionCompleted ||
          current is DepositTransactionPending,
      bloc: _walletBloc);

  Widget _buildUpiAppsChooser() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        RegularText(AppStrings.choosePaymentMethod,
            fontSize: 12.0, color: AppColor.textSubTitle),
        const SizedBox(
          height: 8.0,
        ),
        UpiAppsChooser(
            selectorColor: AppColor.colorPrimaryDark,
            upiAppsLoaded: _walletBloc.onUpiAppsLoaded,
            noUpiAppsWidget: _buildNoUpiAppAvailableView(),
            onSelectUpiApp: (UpiApp upiApp) {
              _walletBloc.upiAppSelected(upiApp);
            }),
        const SizedBox(
          height: 15.0,
        )
      ],
    );
  }

  Widget _buildNoUpiAppAvailableView() {
    return RegularText(AppStrings.noUpiAppsAvailable,
        fontSize: 12.0, color: AppColor.highlightOrange);
  }

  void _validateAmount(double? amount) {
    if (widget.isWithdraw) {
      if (amount == null || amount == 0)
        isAmountValid = "Invalid amount";
      else if (amount > (_walletBloc.user?.wallet.winning ?? 0))
        isAmountValid =
            "Withdraw amount exceeding ${AppStrings.rupeeSymbol}${_walletBloc.user?.wallet.winning}";
      else {
        isAmountValid = null;
        _walletBloc.selectAmountForWithdrawal(amount);
      }
    } else {
      if (amount == null || amount == 0) {
        isAmountValid = "Invalid amount";
      } else {
        isAmountValid = null;
        _walletBloc.selectAmountForDeposit(amount);
      }
    }
  }

  @override
  void initState() {
    super.initState();
    _walletBloc = context.read<WalletBloc>();
    _controller = TextEditingController();
    _controller.text = widget.isWithdraw
        ? _walletBloc.amountToWithdraw > 0
            ? _walletBloc.amountToWithdraw.toString()
            : ""
        : _walletBloc.amountForDeposit > 0
            ? _walletBloc.amountForDeposit.toString()
            : "";
    var amount = widget.isWithdraw
        ? _walletBloc.amountToWithdraw
        : _walletBloc.amountForDeposit;
    if (amount != 0) {
      _validateAmount(amount);
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}

class TransactionList extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _TransactionListState();
}

class _TransactionListState extends State<TransactionList> {
  late WalletBloc _walletBloc;
  late WalletWidgets walletHomeWidgets;

  @override
  Widget build(BuildContext context) {
    return BlocBuilder<WalletBloc, WalletState>(
      builder: (context, state) {
        if (state is TransactionsLoading) {
          return appCircularProgressIndicator();
        } else if (state is TransactionsLoaded) {
          return RefreshIndicator(
              child: ListView.separated(
                  itemBuilder: (context, index) => walletHomeWidgets
                      .getTransactionItem(state.transactions[index]),
                  separatorBuilder: (context, index) => Divider(
                      height: 1.0,
                      thickness: 1.0,
                      color: AppColor.dividerColor),
                  itemCount: state.transactions.length),
              onRefresh: () => _walletBloc.getTransactions());
        }
        return SizedBox.shrink();
      },
      buildWhen: (previous, current) =>
          current is TransactionsLoaded || current is TransactionsLoading,
      bloc: _walletBloc,
    );
  }

  @override
  void initState() {
    super.initState();
    _walletBloc = context.read<WalletBloc>();
    walletHomeWidgets = WalletWidgets(_walletBloc);
  }
}

class WithdrawalMethodInputWidget extends StatefulWidget {
  final String? upiId;
  final bool isWithdraw;

  WithdrawalMethodInputWidget(this.upiId, {this.isWithdraw = false});

  @override
  State<StatefulWidget> createState() => _WithdrawalMethodInputWidgetState();
}

class _WithdrawalMethodInputWidgetState
    extends State<WithdrawalMethodInputWidget> {
  late WalletBloc _walletBloc;
  late TextEditingController _controller;
  String? isValidUPI = "";

  @override
  Widget build(BuildContext context) => BlocListener<WalletBloc, WalletState>(
      listener: (ctx, state) {
        if (state is WithdrawTransactionCompleted) {
          UiUtils.getInstance.showCustomDialog(
              context,
              WalletWidgets(_walletBloc).transactionPopup(
                  context, true, state.amount,
                  error: state.error, txnId: state.txnId),
              dismissible: false);
        }
      },
      child: GestureDetector(
          onTap: () => FocusScope.of(context).unfocus(),
          child: Container(
              padding: const EdgeInsets.all(20.0),
              height: MediaQuery.of(context).size.height,
              child: SingleChildScrollView(
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                    RegularText(
                        widget.isWithdraw
                            ? AppStrings.amountWithdraw
                            : AppStrings.amountDeposit,
                        color: AppColor.textSubTitle),
                    const SizedBox(height: 4.0),
                    BoldText(
                        "${AppStrings.rupeeSymbol}${(widget.isWithdraw ? _walletBloc.amountToWithdraw : _walletBloc.amountForDeposit).formattedNumber()}",
                        fontSize: 35.0),
                    const SizedBox(height: 10.0),
                    RegularText(AppStrings.enterUpiId),
                    const SizedBox(height: 10.0),
                    TextField(
                        onChanged: (newText) {
                          setState(() {
                            isValidUPI = FieldValidators.validateUPI(newText);
                          });
                        },
                        textInputAction: TextInputAction.done,
                        keyboardType: TextInputType.emailAddress,
                        controller: _controller,
                        style: TextStyle(color: Colors.white),
                        decoration: InputDecoration(
                            prefixIcon: Padding(
                                padding: const EdgeInsets.all(8.0),
                                child: Image.asset(
                                    "${imageAssets}img_upi_logo.png",
                                    width: 24.0)),
                            counterText: "",
                            hintText: "eg: johnDoe@okicici",
                            hintStyle:
                                TextStyle(color: AppColor.grayText9E9E9E),
                            border: InputBorder.none,
                            filled: true,
                            fillColor: AppColor.inputBackground)),
                    const SizedBox(height: 20.0),
                    BlocBuilder<WalletBloc, WalletState>(
                      builder: (context, state) {
                        return secondaryButton(
                            widget.isWithdraw
                                ? AppStrings.withdraw
                                : AppStrings.deposit, () {
                          if (widget.isWithdraw) {
                            _walletBloc.withDraw(context, _controller.text);
                          } /*else {
                            _walletBloc.deposit(context, _controller.text);
                          }*/
                        },
                            active: isValidUPI == null &&
                                state is! WithdrawalProcessing);
                      },
                      bloc: _walletBloc,
                      buildWhen: (previous, current) =>
                          current is WithdrawalProcessing ||
                          current is WithdrawalAmountConfirmed ||
                          current is WithdrawTransactionCompleted,
                    )
                  ])))),
      bloc: _walletBloc);

  @override
  void initState() {
    super.initState();
    _walletBloc = context.read<WalletBloc>();
    _controller = TextEditingController();
    if (widget.upiId != null) {
      _controller.text = widget.upiId!;
      isValidUPI = null;
    }
  }
}

class UpiPaymentConfirmation extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _UpiPaymentConfirmationState();
}

class _UpiPaymentConfirmationState extends State<UpiPaymentConfirmation> {
  late WalletBloc _walletBloc;

  @override
  Widget build(BuildContext context) {
    return BlocListener<WalletBloc, WalletState>(
      listener: (ctx, state) {
        if (state is DepositTransactionCompleted) {
          UiUtils.getInstance.showCustomDialog(
              context,
              state.error == null
                  ? WalletWidgets(_walletBloc)
                      .depositCompletedPopup(context, state.amount, state.txnId)
                  : WalletWidgets(_walletBloc).transactionPopup(
                      context, true, state.amount,
                      error: state.error, txnId: state.txnId),
              dismissible: false);
        }
      },
      child: BlocBuilder<WalletBloc, WalletState>(
        builder: (context, state) {
          if (state is DepositConfirmation) {
            var upiId = state.upiId;
            return Container(
              padding:
                  const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12),
              decoration: BoxDecoration(color: Colors.black),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Row(
                    children: [
                      Expanded(
                        flex: 1,
                        child: TitleText(AppStrings.paymentConfirmDialogTitle,
                            fontSize: 16.0, color: AppColor.textSubTitle),
                      ),
                      const SizedBox(width: 32),
                      BlocBuilder<WalletBloc, WalletState>(
                        builder: (context, state) {
                          return secondaryButton(AppStrings.confirm, () {
                            _walletBloc.depositConfirm(context, upiId);
                          }, active: !(state is DepositProcessing));
                        },
                        buildWhen: (previous, current) =>
                            current is DepositConfirmation ||
                            current is DepositProcessing ||
                            current is DepositTransactionCompleted,
                        bloc: _walletBloc,
                      )
                    ],
                  ),
                  const SizedBox(height: 24),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Column(
                        children: [
                          Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(AppStrings.notAbleToMakePayment,
                                  style: RegularTextStyle(
                                      color: AppColor.grayText696969)),
                              Text(
                                AppStrings.useTheQrCode,
                                style: SemiBoldTextStyle(
                                    color: AppColor.whiteTextColor),
                              ),
                            ],
                          ),
                        ],
                      ),
                      const SizedBox(width: 24),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Container(
                            child: _vpaAddress(),
                          ),
                          const SizedBox(width: 24),
                          Text(
                            AppStrings.or,
                            style: RegularTextStyle(
                                color: AppColor.grayText9E9E9E),
                          ),
                          const SizedBox(width: 24),
                          Image.asset("${imageAssets}gamerboard_qr.png",
                              width: MediaQuery.of(context).size.height * 0.6),
                          IconButton(
                              onPressed: () {
                                _walletBloc.downloadUpiQr();
                              },
                              icon: Icon(
                                Icons.download,
                                color: AppColor.buttonActive,
                              )),
                        ],
                      )
                    ],
                  ),
                  const SizedBox(height: 24),
                ],
              ),
            );
          }
          return SizedBox.shrink();
        },
        buildWhen: (previous, current) => current is DepositConfirmation,
        bloc: _walletBloc,
      ),
      listenWhen: (previous, current) =>
          current is DepositTransactionCompleted ||
          current is DepositProcessing,
    );
  }

  InkWell _vpaAddress() {
    return InkWell(
      onTap: () {
        _walletBloc.copyUpiToClipboard(AppStrings.vpaAddress);
      },
      child: Container(
          color: AppColor.textColorDark.withAlpha(100),
          margin: const EdgeInsets.only(top: 18),
          padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 18),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.center,
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                AppStrings.vpaAddress,
                style: RegularTextStyle(color: AppColor.whiteTextColor),
              ),
              const SizedBox(width: 24),
              Icon(Icons.file_copy, color: AppColor.whiteTextColor)
            ],
          )),
    );
  }

  @override
  void initState() {
    super.initState();
    _walletBloc = context.read<WalletBloc>();
  }
}
