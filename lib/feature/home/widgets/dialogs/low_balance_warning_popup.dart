
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

import '../../../../common/widgets/buttons.dart';
import '../../../../common/widgets/text.dart';
import '../../../../resources/colors.dart';
import '../../../../resources/strings.dart';
import '../../home_bloc.dart';

class LowBalanceWarningPopup extends StatefulWidget {
  final Function onCloseClick;
  final Function onDepositClick;

  LowBalanceWarningPopup(
      {required this.onCloseClick, required this.onDepositClick});

  @override
  State<StatefulWidget> createState() => _LowBalanceWarningPopupState();
}

class _LowBalanceWarningPopupState extends State<LowBalanceWarningPopup> {
  @override
  Widget build(BuildContext context) {
    var width = MediaQuery.of(context).size.width / 2;
    var height = MediaQuery.of(context).size.height / 2;

    return Container(
      width: width,
      height: height,
      child: Stack(
        children: [
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 0),
            color: AppColor.darkBackground,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Padding(
                  padding:
                  const EdgeInsets.symmetric(vertical: 8, horizontal: 8),
                  child: SvgPicture.asset("${imageAssets}ic_add_to_wallet.svg",
                      color: AppColor.buttonSecondary.withAlpha(150),
                      height: 32.0,
                      width: 32.0),
                ),
                const SizedBox(
                  height: 16,
                ),
                Column(
                  children: [
                    BoldText(AppStrings.runningLowBalance),
                    RegularText(AppStrings.addMoneyToWallet),
                  ],
                ),
                const SizedBox(
                  height: 24,
                ),
                secondaryButton(AppStrings.addMoney, () {
                  widget.onDepositClick();
                }, active: true, paddingVertical: 8, paddingHorizontal: 32),
              ],
            ),
          ),
          Positioned(
            top: 0,
            right: 0,
            child: IconButton(
                onPressed: () {
                  widget.onCloseClick();
                },
                icon: Icon(Icons.clear, color: AppColor.whiteTextColor),
                visualDensity: VisualDensity.compact,
                padding: EdgeInsets.zero,
                iconSize: 24),
          )
        ],
      ),
    );
  }
}