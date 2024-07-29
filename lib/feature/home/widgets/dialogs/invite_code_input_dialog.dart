import 'package:flutter/material.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/dialog_container.dart';
import 'package:gamerboard/common/widgets/input.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/ui_utils.dart';
import 'package:gamerboard/utils/validators.dart';

class InviteCodeInputDialog extends StatefulWidget {

  final Function(String) onSubmitInviteCode;

  InviteCodeInputDialog({required this.onSubmitInviteCode});

  @override
  State<StatefulWidget> createState() => _InviteCodeInputDialogState();

  static show(BuildContext context, Function(String) onSubmitInviteCode) =>
      UiUtils.getInstance.showCustomDialog(context,
          InviteCodeInputDialog(onSubmitInviteCode: onSubmitInviteCode,));
}

class _InviteCodeInputDialogState extends State<InviteCodeInputDialog> {
  final TextEditingController inviteCodeController = TextEditingController();
  final _formKey = GlobalKey<FormState>();
  var _isValidCode = false;

  @override
  Widget build(BuildContext context) {
    double width = MediaQuery
        .of(context)
        .size
        .width * 0.50;
    return DialogContainer(
      width: width,
      child: Column(
        children: [
          _buildInviteInput(),
          const SizedBox(height: 16.0),
          _actions(context),
          const SizedBox(height: 10.0),
        ],
      ),
      title: AppStrings.titleInviteOnly,
      subtitle: AppStrings.descriptionInviteOnly,
    );
  }

  Widget _actions(BuildContext context) {
    return Row(
      mainAxisAlignment: MainAxisAlignment.spaceBetween,
      children: [

        Expanded(
          child: primaryBorderButton(AppStrings.close, () {
            Navigator.of(context).pop();
          }),
        ),

        const SizedBox(
          width: 16,
        ),

        Expanded(
          child: primaryButton(AppStrings.join, () {
            Navigator.of(context).pop();
            if (_formKey.currentState?.validate() == true) {
              widget.onSubmitInviteCode.call(inviteCodeController.text);
            }
          }, active: _isValidCode),
        )
      ],
    );
  }

  Widget _buildInviteInput() {
    return Form(
      key: _formKey,
      child: TextField(
          maxLines: 1,
          maxLength: Constants.INVITE_CODE,
          keyboardType: TextInputType.text,
          textInputAction: TextInputAction.done,
          controller: inviteCodeController,
          inputFormatters: [AppInputFormatters.spaceFormatter],
          onChanged: (val) => setState(() {
            _isValidCode = FieldValidators.validateJoinCode(val);
          }),
          style: RegularTextStyle(),
          decoration: darkTextFieldWithBorderDecoration(
              hintLabel: AppStrings.hintInviteOnlyCode)),
    );
  }
}
