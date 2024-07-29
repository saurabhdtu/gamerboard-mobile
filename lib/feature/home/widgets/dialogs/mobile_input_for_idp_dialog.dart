import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:gamerboard/utils/ui_utils.dart';

import '../../../../common/widgets/buttons.dart';
import '../../../../common/widgets/text.dart';
import '../../../../graphql/query.graphql.dart';
import '../../../../resources/colors.dart';
import '../../../../resources/strings.dart';
import '../../../../utils/share_utils.dart';
import '../../../../utils/validators.dart';

class MobileInputForIDPDialog extends StatefulWidget {
  final UserTournamentMixin tournament;
  final Function(UserTournamentMixin tournament,
      int pageType, String phone) submit;
  final int pageType;
  final String? phone;
  final String? tournamentName;

  MobileInputForIDPDialog(
      this.tournament, this.phone, this.submit, this.pageType,
      {this.tournamentName});

  @override
  State<MobileInputForIDPDialog> createState() =>
      _MobileInputForIDPDialogState();
}

class _MobileInputForIDPDialogState
    extends State<MobileInputForIDPDialog> {
  final _controllerMobile = TextEditingController();

  @override
  void initState() {
    super.initState();
    setState(() {
      _controllerMobile.text = widget.phone ?? '';
    });
  }

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery.of(context).size.width * 0.50;

    return Padding(
      padding:
          EdgeInsets.only(bottom: MediaQuery.of(context).viewInsets.bottom),
      child: SingleChildScrollView(
        child: Container(
            padding: EdgeInsets.all(12),
            decoration: BoxDecoration(boxShadow: [
              BoxShadow(
                  color: AppColor.blackColor3E3E3E,
                  blurRadius: 5.0,
                  blurStyle: BlurStyle.outer,
                  spreadRadius: 5.0)
            ], color: AppColor.dividerColor),
            width: width,
            child: Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      const SizedBox(
                        width: 24,
                      ),
                      Text(
                        AppStrings.congratulations,
                        style: SemiBoldTextStyle(
                            color: Colors.white, fontSize: 16),
                      ),
                      InkWell(
                          onTap: () {
                            Navigator.of(context).pop();
                          },
                          child: const Icon(
                            Icons.close,
                            color: AppColor.grayIconCCCCCC,
                            size: 24,
                          ))
                    ],
                  ),
                  const SizedBox(height: 16.0),
                  RegularText(
                    "You are eligible to join custom room",
                    fontSize: 12.0,
                    color: AppColor.successGreen,
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 8.0),
                  RegularText(
                    "Just enter your phone number on which you \nwant to receive the room's username and \npassword  ",
                    fontSize: 12.0,
                    color: AppColor.grayTextB3B3B3,
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 16.0),
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 24),
                    child: TextFormField(
                        maxLength: 10,
                        validator: FieldValidators.validatePhone,
                        focusNode: FocusNode(),
                        onFieldSubmitted: (value) {},
                        controller: _controllerMobile,
                        keyboardType: TextInputType.phone,
                        inputFormatters: <TextInputFormatter>[
                          FilteringTextInputFormatter.digitsOnly
                        ],
                        style: TextStyle(color: AppColor.whiteF6F6F6),
                        decoration: GbEditTextDecor(
                            AppStrings.enterMobileNumber,
                            suffix: Icon(
                              Icons.edit,
                              color: Color(0xffB6B6B6),
                            ),
                            prefix: Padding(
                                padding: EdgeInsets.only(right: 6.0),
                                child: RegularText("+91")))),
                  ),
                  const SizedBox(height: 8.0),
                  InkWell(
                    onTap: () {
                      openUrlInExternalBrowser(
                          "https://gamerboard.notion.site/How-To-Join-Custom-Room-5986cb677bba427bbc609d44c2e52e21?pvs=4");
                    },
                    child: Text(
                      AppStrings.howToJoinCustomRoom,
                      style: TextStyle(
                          fontWeight: FontWeight.w400,
                          fontSize: 12,
                          color: AppColor.successGreen),
                    ),
                  ),
                  const SizedBox(height: 24.0),
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 24),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        primaryBorderButton(AppStrings.cancel, () {
                          Navigator.of(context).pop();
                        }, paddingHorizontal: 56),
                        const SizedBox(
                          width: 16,
                        ),
                        secondaryButton(AppStrings.submit, () {
                          Navigator.of(context).pop();
                          widget.submit( widget.tournament,
                              widget.pageType, _controllerMobile.text);
                        },
                            paddingHorizontal: 56,
                            textColor: AppColor.whiteTextColor)
                      ],
                    ),
                  ),
                  const SizedBox(height: 10.0),
                ])),
      ),
    );
  }
}
