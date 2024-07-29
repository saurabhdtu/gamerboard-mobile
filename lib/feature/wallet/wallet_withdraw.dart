import 'package:flutter/cupertino.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/feature/wallet/wallet_bloc.dart';
import 'package:gamerboard/feature/wallet/wallet_states.dart';
import 'package:gamerboard/feature/wallet/wallet_widgets.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
////Created by saurabh.lahoti on 07/02/22

class WalletWithdrawPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _WalletWithdrawPageState();
}

class _WalletWithdrawPageState extends State<WalletWithdrawPage> {
  late WalletBloc _walletBloc;
  int page = 0;

  @override
  Widget build(BuildContext context) => WillPopScope(
      child: appScaffold(
          appBar:
              appBar(context, AppStrings.withdraw, backAction: onBackPressed),
          body: Column(children: [
            Expanded(
                child: BlocBuilder<WalletBloc, WalletState>(
                    builder: (context, state) {
                      if (state is WithdrawalLoaded) {
                        return GestureDetector(
                            onTap: () => FocusScope.of(context).unfocus(),
                            child: Row(
                                crossAxisAlignment: CrossAxisAlignment.stretch,
                                children: [
                                  Expanded(
                                      child: AmountPickerDeposit(
                                          isWithdraw: true)),
                                  Container(
                                      width: MediaQuery.of(context).size.width *
                                          .4,
                                      height:
                                          MediaQuery.of(context).size.height,
                                      decoration: BoxDecoration(
                                          color: AppColor.colorGrayBg2),
                                      padding: const EdgeInsets.all(20.0),
                                      child: AddDeposit(isWithdraw: true))
                                ]));
                      } else if (state is WithdrawalAmountConfirmed) {
                        page = 1;
                        return WithdrawalMethodInputWidget(state.upiId, isWithdraw: true,);
                      }
                      return SizedBox.shrink();
                    },
                    buildWhen: (previous, current) =>
                        current is WithdrawalLoaded ||
                        current is WithdrawalAmountConfirmed,
                    bloc: _walletBloc))
          ])),
      onWillPop: () => onBackPressed());

  Future<bool> onBackPressed() {
    if (page == 1) {
      page = 0;
      _walletBloc.emit(WithdrawalLoaded());
    } else {
      Navigator.of(context).pop();
    }
    return Future.value(false);
  }

  @override
  void initState() {
    super.initState();
    _walletBloc = context.read<WalletBloc>();
    _walletBloc.amountToWithdraw = 0;
    _walletBloc.walletChanged = false;
    _walletBloc.emit(WithdrawalLoaded());
    page = 0;
  }
}
