import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/feature/wallet/wallet_bloc.dart';
import 'package:gamerboard/feature/wallet/wallet_states.dart';
import 'package:gamerboard/feature/wallet/wallet_widgets.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
////Created by saurabh.lahoti on 07/02/22

class WalletDepositPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _WalletDepositPageState();
}

class _WalletDepositPageState extends State<WalletDepositPage> {
  late WalletBloc _walletBloc;
  int page = 0;

  @override
  Widget build(BuildContext context) => WillPopScope(
    onWillPop: onBackPressed,
    child: appScaffold(
        appBar: appBar(context, AppStrings.deposit, backAction: onBackPressed),
        body: Column(
          children: [
            Expanded(
              child: BlocBuilder<WalletBloc, WalletState>(
                  builder: (context, state) {
                  return GestureDetector(
                      onTap: () => FocusScope.of(context).unfocus(),
                      child: Row(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Expanded(child: AmountPickerDeposit()),
                            Container(
                                width: MediaQuery.of(context).size.width * .4,
                                height: MediaQuery.of(context).size.height,
                                decoration: BoxDecoration(
                                    color: AppColor.colorGrayBg2),
                                padding: const EdgeInsets.all(20.0),
                                child: AddDeposit())
                          ]));
                },
                buildWhen: (previous, current) =>
                current is DepositLoaded ,
                bloc: _walletBloc,
              ),
            ),
          ],
        )),
  );

  Future<bool> onBackPressed() {
    _walletBloc.emit(DepositLoaded());
    _walletBloc.loadData();
    Navigator.of(context).pop();
    return Future.value(false);
  }

  @override
  void initState() {
    super.initState();
    _walletBloc = context.read<WalletBloc>();
    _walletBloc.amountForDeposit = 0;
    _walletBloc.walletChanged = false;
    _walletBloc.emit(DepositLoaded());
    _walletBloc.loadData();
  }
}
