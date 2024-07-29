
import 'package:flutter/material.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';

class LoginOption extends StatefulWidget {
  final String otpLessButtonText;
  final String otpButtonText;
  final Function() onOtpLessOptionClicked;
  final Function() onOtpOptionClicked;
  final List<Widget>? additionalActions;
  const LoginOption({
    super.key,
    this.additionalActions,
    required this.otpLessButtonText,
    required this.otpButtonText,
    required this.onOtpLessOptionClicked,
    required this.onOtpOptionClicked
  });

  @override
  State<LoginOption> createState() => _LoginOptionState();
}

class _LoginOptionState extends State<LoginOption> {


  @override
  Widget build(BuildContext context) {
    return Column(crossAxisAlignment: CrossAxisAlignment.center, children: [
      const SizedBox(height: 25.0),
      primaryButton(widget.otpLessButtonText, widget.onOtpLessOptionClicked, active: true),
      const SizedBox(height: 16.0),
      RegularText(AppStrings.or, color: AppColor.grayTextC6C6C6,),
      const SizedBox(height: 16.0),
      primaryBorderButton(widget.otpButtonText, widget.onOtpOptionClicked, active: true),
      const SizedBox(height: 20.0),
      ...widget.additionalActions ?? []
    ]);
  }
}
