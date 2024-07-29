import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/feature/wallet/wallet_bloc.dart';
import 'package:gamerboard/feature/wallet/wallet_states.dart';
import 'package:gamerboard/feature/wallet/wallet_widgets.dart';

import '../../common/widgets/containers.dart';

////Created by saurabh.lahoti on 28/01/22

class WalletPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _WalletPageState();
}

class _WalletPageState extends State<WalletPage> {
  late WalletBloc _walletBloc;
  late WalletWidgets _walletWidgets;

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: onBackPressed,
      child: appScaffold(
          appBar: appBar(context, "Wallet", backAction: onBackPressed),
          body: Column(children: [
            Expanded(
                child: BlocBuilder<WalletBloc, WalletState>(
                    builder: (context, state) {
                      if (state is WalletLoading) {
                        return appCircularProgressIndicator();
                      } else if (state is WalletLoaded) {
                        return Row(
                            crossAxisAlignment: CrossAxisAlignment.stretch,
                            children: [
                              Expanded(
                                  child: cardContainer(TransactionList(),
                                      padding: const EdgeInsets.symmetric(
                                          horizontal: 20.0))),
                              _walletWidgets.getWalletHomeRightPane(
                                  context,
                                  state,
                                  MediaQuery.of(context).size.width * .35)
                            ]);
                      }
                      return SizedBox.shrink();
                    },
                    buildWhen: (previous, current) =>
                        current is WalletLoading || current is WalletLoaded,
                    bloc: _walletBloc))
          ])),
    );
  }

  @override
  void dispose() {
    super.dispose();
  }

  Future<bool> onBackPressed() {
    if (checkForRoute(Routes.HOME_PAGE)) {
      Navigator.of(context).pop();
    } else {
      Navigator.popAndPushNamed(context, Routes.HOME_PAGE);
    }
    return Future.value(false);
  }

  @override
  void initState() {
    super.initState();
    _walletBloc = context.read<WalletBloc>();
    _walletWidgets = WalletWidgets(_walletBloc);
    _walletBloc.loadData();
  }
}
