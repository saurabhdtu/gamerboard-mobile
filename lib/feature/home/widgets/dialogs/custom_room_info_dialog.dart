import 'dart:ffi';

import 'package:firebase_remote_config/firebase_remote_config.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/share_utils.dart';
import 'package:gamerboard/utils/ui_utils.dart';
import 'package:gamerboard/utils/validators.dart';

class ShowCustomRoomInfoDialog extends StatefulWidget {
  final int? slotNumber;
  final String roomId;
  final String password;

  const ShowCustomRoomInfoDialog(this.roomId, this.password,
      {Key? key, this.slotNumber})
      : super(key: key);

  @override
  State<ShowCustomRoomInfoDialog> createState() =>
      _ShowCustomRoomInfoDialogState();
}

class _ShowCustomRoomInfoDialogState extends State<ShowCustomRoomInfoDialog> {
  final _slotTextController = TextEditingController();
  final _idController = TextEditingController();
  final _passwordController = TextEditingController();

  @override
  void initState() {
    super.initState();
    setState(() {
      _idController.text = widget.roomId;
      _passwordController.text = widget.password;
      if(widget.slotNumber != null){
        _slotTextController.text = widget.slotNumber.toString();
      }
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
                        AppStrings.customRoom,
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
                  const SizedBox(height: 8.0),
                  RegularText(
                    AppStrings.useCredentialsCustomRoom,
                    fontSize: 12.0,
                    color: AppColor.grayTextB3B3B3,
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 20.0),
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 16),
                    child: Row(
                      children: [
                        if (widget.slotNumber != null)
                          Flexible(
                            flex: 1,
                            child: _buildSlotField(),
                          ),
                        Flexible(
                          flex: 1,
                          child: _buildIdField(),
                        )
                      ],
                    ),
                  ),
                  const SizedBox(height: 4.0),
                  _buildPassword(),
                  const SizedBox(height: 12.0),
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
                  const SizedBox(height: 16.0),
                  Padding(
                    padding: EdgeInsets.symmetric(horizontal: 24),
                    child: Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        primaryBorderButton(AppStrings.gotIt, () {
                          Navigator.of(context).pop();
                        }, paddingHorizontal: 56),
                        const SizedBox(
                          width: 16,
                        ),
                        secondaryButton(AppStrings.joinDiscord, () {
                          Navigator.of(context).pop();
                          openUrlInExternalBrowser(FirebaseRemoteConfig.instance
                              .getString(
                                  RemoteConfigConstants.GB_DISCORD_LINK));
                        },
                            paddingHorizontal: 49,
                            textColor: AppColor.whiteTextColor)
                      ],
                    ),
                  ),
                  const SizedBox(height: 10.0),
                ])),
      ),
    );
  }

  Padding _buildPassword() {
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 24),
      child: InkWell(
        onTap: () {
          Clipboard.setData(ClipboardData(text: _passwordController.text));
        },
        child: TextFormField(
            validator: FieldValidators.validatePhone,
            focusNode: FocusNode(),
            enabled: false,
            controller: _passwordController,
            style: TextStyle(color: AppColor.whiteF6F6F6),
            decoration: GbEditTextDecor(
              AppStrings.roomPassword,
              suffix: InkWell(
                  onTap: () {
                    Clipboard.setData(
                        ClipboardData(text: _passwordController.text));
                  },
                  child: Icon(
                    Icons.copy,
                    color: Color(0xffB6B6B6),
                    size: 18,
                  )),
            )),
      ),
    );
  }

  Padding _buildIdField() {
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 8),
      child: InkWell(
        onTap: () {
          Clipboard.setData(ClipboardData(text: _idController.text));
        },
        child: TextFormField(
            validator: FieldValidators.validatePhone,
            focusNode: FocusNode(),
            enabled: false,
            controller: _idController,
            style: TextStyle(color: AppColor.whiteF6F6F6, fontSize: 16),
            decoration: GbEditTextDecor(
              AppStrings.roomId,
              suffix: InkWell(
                  onTap: () {
                    Clipboard.setData(ClipboardData(text: _idController.text));
                  },
                  child: Icon(
                    Icons.copy,
                    color: Color(0xffB6B6B6),
                    size: 18,
                  )),
            )),
      ),
    );
  }

  Padding _buildSlotField() {
    return Padding(
      padding: EdgeInsets.symmetric(horizontal: 8),
      child: TextFormField(
          validator: FieldValidators.validatePhone,
          focusNode: FocusNode(),
          enabled: false,
          controller: _slotTextController,
          style: TextStyle(color: AppColor.whiteF6F6F6, fontSize: 16),
          decoration: GbEditTextDecor(
            AppStrings.slotNumber,
          )),
    );
  }
}
