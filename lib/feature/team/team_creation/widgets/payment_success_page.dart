import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/containers.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_bloc.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_widgets.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:lottie/lottie.dart';

class PaymentSuccessPage extends StatefulWidget {
  const PaymentSuccessPage();

  @override
  State<StatefulWidget> createState() => _PaymentSuccessPageState();
}

class _PaymentSuccessPageState extends State<PaymentSuccessPage> {
  late TeamCreateBloc _teamCreateBloc;
  bool isJoinTournamentEnable = true;

  @override
  Widget build(BuildContext context) {
    final items = _buildPaymentSuccessPage(context);

    final List<Widget> columnItems = [
      RegularText(AppStrings.payForYourself, fontSize: 16.0),
      const SizedBox(height: 10.0),
      BoldText(
          "${AppStrings.rupeeSymbol}${_teamCreateBloc.tournamentMixin.tournament.fee}",
          fontSize: 55.0),
      const SizedBox(height: 5.0),
      const Divider(height: 1.0, thickness: 1, color: AppColor.dividerColor),
      const Spacer()
    ];
    columnItems.addAll(items);
    columnItems.add(const Spacer());
    return themeContainer(
        Column(mainAxisSize: MainAxisSize.min, children: columnItems),
        padding: const EdgeInsets.symmetric(vertical: 20.0, horizontal: 40.0),
        height: MediaQuery.of(context).size.height - 50);
  }

  List<Widget> _buildPaymentSuccessPage(BuildContext context) {
    return [
      LottieBuilder.asset("${lottieAssets}tick.json",
          height: 80.0, width: 80.0, repeat: true),
      const SizedBox(height: 5.0),
      RegularText(AppStrings.successfullyPaid, fontSize: 16.0),
      const Spacer(),
      if (_teamCreateBloc.onlyPayment)
        secondaryButton(AppStrings.backToTournament,
            () => _teamCreateBloc.openTournament(context)),
      const Spacer()
    ];
  }

  @override
  void initState() {
    super.initState();
    _teamCreateBloc = context.read<TeamCreateBloc>();
  }
}
