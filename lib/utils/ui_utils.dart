import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:fluttertoast/fluttertoast.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/dialog_container.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/file_helper.dart';
import 'package:gamerboard/utils/file_utils.dart';

////Created by saurabh.lahoti on 17/10/21

class UiUtils {
  static UiUtils? _instance;

  UiUtils._();

  static UiUtils get getInstance => _instance ??= UiUtils._();

  void showToast(String s, {Toast toastLength = Toast.LENGTH_SHORT}) {
    Fluttertoast.showToast(
        msg: s,
        toastLength: toastLength,
        gravity: ToastGravity.BOTTOM,
        backgroundColor: Colors.black,
        textColor: Colors.white,
        fontSize: 13.0);
  }

  void showDebugToast(String s, {Toast toastLength = Toast.LENGTH_SHORT}) {
    if (kDebugMode)
      Fluttertoast.showToast(
          msg: s,
          toastLength: toastLength,
          gravity: ToastGravity.BOTTOM,
          backgroundColor: Colors.black,
          textColor: Colors.white,
          fontSize: 13.0);
  }

  buildLoading(BuildContext context, {bool dismissible = true}) {
    return showDialog(
        context: context,
        barrierDismissible: dismissible,
        barrierColor: Colors.black12,
        builder: (BuildContext context) {
          return WillPopScope(
              onWillPop: () => Future.value(dismissible),
              child: Center(
                  child: CircularProgressIndicator(
                      valueColor: AlwaysStoppedAnimation<Color>(
                          Colors.deepPurpleAccent))));
        });
  }

  void downloadProgressDialog(
      BuildContext context, DownloadHandler downloadHandler,
      {bool isDismissible = false}) {
    showDialog(
        context: context,
        builder: (context) =>
            AlertDialog(content: StatefulBuilder(builder: (context, setState) {
              downloadHandler.setState = setState;
              return WillPopScope(
                  onWillPop: () => Future.value(false),
                  child: Container(
                      width: 150.0,
                      height: 50.0,
                      padding: EdgeInsets.all(10.0),
                      decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(6.0),
                          color: Colors.white),
                      child: Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          mainAxisSize: MainAxisSize.min,
                          crossAxisAlignment: CrossAxisAlignment.stretch,
                          children: [
                            RegularText("Downloading update...",
                                fontSize: 14.0, color: Colors.black),
                            SizedBox(height: 8.0),
                            LinearProgressIndicator(
                                value: downloadHandler.progress,
                                semanticsLabel: 'Download indicator')
                          ])));
            })),
        barrierDismissible: isDismissible);
  }

  void alertDialog(BuildContext context, String title, String message,
      {String yes = AppStrings.yes,
      String no = AppStrings.no,
      Function? yesAction,
      Function? noAction}) {
    showDialog(
        context: context,
        builder: (context) => AlertDialog(
            title: BoldText(title, fontSize: 15.0),
            content: RegularText(message),
            actions:
                _dialogActionButtons(context, noAction, no, yesAction, yes),
            titlePadding: EdgeInsets.all(20.0),
            backgroundColor: Colors.black87,
            contentPadding:
                EdgeInsets.only(left: 20.0, right: 20.0, bottom: 20.0),
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(2.0),
                side: BorderSide(color: AppColor.textSubTitle, width: 1.5))));
  }

  void alertDialogV2(BuildContext context, String title, String message,
      {String yes = AppStrings.yes,
      String no = AppStrings.no,
      double widthFactor = 0.3,
      Function? yesAction,
      Function? noAction}) {
    var width = MediaQuery.of(context).size.width * widthFactor;
    showCustomDialog(
        context,
        DialogContainer(
            width: width,
            child: Column(
              children: [
                BoldText(title, fontSize: 15.0),
                const SizedBox(
                  height: 8,
                ),
                RegularText(
                  message,
                  color: AppColor.grayText9E9E9E,
                  textAlign: TextAlign.center,
                ),
                const SizedBox(
                  height: 24,
                ),
                Row(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    ..._dialogActionButtonsV2(
                        context, noAction, no, yesAction, yes)
                  ],
                )
              ],
            )));
    return;
  }

  List<Widget> _dialogActionButtons(BuildContext context, Function? noAction,
      String no, Function? yesAction, String yes) {
    return [
      Padding(
          padding: EdgeInsets.symmetric(horizontal: 10.0, vertical: 5.0),
          child: InkWell(
              onTap: () {
                Navigator.of(context).pop();
                noAction?.call();
              },
              child: BoldText(no, color: AppColor.textSubTitle))),
      Padding(
          padding: EdgeInsets.symmetric(horizontal: 10.0, vertical: 5.0),
          child: InkWell(
              onTap: () {
                Navigator.of(context).pop();
                yesAction?.call();
              },
              child: BoldText(yes, color: AppColor.textHighlighted)))
    ];
  }

  List<Widget> _dialogActionButtonsV2(BuildContext context, Function? noAction,
      String no, Function? yesAction, String yes) {
    return [
      Expanded(
        child: Padding(
            padding: EdgeInsets.symmetric(horizontal: 10.0, vertical: 5.0),
            child: InkWell(
                onTap: () {
                  Navigator.of(context).pop();
                  noAction?.call();
                },
                child: Center(child: BoldText(no, color: AppColor.textSubTitle)))),
      ),
      Expanded(
        child: Padding(
            padding: EdgeInsets.symmetric(horizontal: 10.0, vertical: 5.0),
            child: secondaryButton(yes, () {
              Navigator.of(context).pop();
              yesAction?.call();
            })),
      )
    ];
  }

  void showMessageDialog(BuildContext context, String message, String btnText,
      {String? title, Function? btnAction}) {
    showDialog(
        context: context,
        builder: (context) => Dialog(
            child: Container(
                width: 0.45 * MediaQuery.of(context).size.width,
                padding: EdgeInsets.all(8.0),
                decoration: BoxDecoration(
                    border:
                        Border.all(color: AppColor.textSubTitle, width: 1.0),
                    borderRadius: BorderRadius.circular(1.0),
                    gradient: AppColor.popupBackgroundGradient),
                child: Column(mainAxisSize: MainAxisSize.min, children: [
                  if (title != null) BoldText(title),
                  SizedBox(height: 5.0),
                  RegularText(message),
                  SizedBox(height: 7.0),
                  Align(
                      alignment: Alignment.centerRight,
                      child: InkWell(
                          onTap: () {
                            Navigator.of(context).pop();
                            btnAction?.call();
                          },
                          child: BoldText(btnText)))
                ]))));
  }

  Future<dynamic> showCustomDialog(BuildContext context, Widget content,
      {bool dismissible = true}) {
    return showDialog(
        context: context,
        barrierDismissible: dismissible,
        builder: (context) => Dialog(
            backgroundColor: Colors.transparent,
            child: WillPopScope(
                onWillPop: () => Future.value(dismissible), child: content)));
  }

  Future<dynamic> showBottomDialog(BuildContext context, Widget content,
      {bool dismissible = true}) {
    return showGeneralDialog(
        context: context,
        barrierDismissible: true,
        barrierColor: Colors.transparent,
        pageBuilder: (BuildContext context, Animation<double> animation,
            Animation<double> secondaryAnimation) {
          return Align(
            alignment: Alignment.bottomCenter,
            child: content,
          );
        },
        transitionBuilder: (context, Animation<double> animation,
            Animation<double> secondaryAnimation, child) {
          return SlideTransition(
            position: Tween(begin: Offset(0, 1), end: Offset(0, 0))
                .animate(animation),
            child: child,
          );
        });
  }

  void copyToClipboard(String text) {
    showToast("Copied to clipboard");
    Clipboard.setData(ClipboardData(text: text));
  }

  void saveQrCode() async {
    try {
      await FileHelper.saveAssetFile("${imageAssets}gamerboard_qr.png");
      showToast("QR code save to saved to downloads");
    } catch (e) {
      showToast("Couldn't save the qr code.");
    }
  }
}

enum UserOnboardingStep { ON_COMPLETED, CURRENT_STEP, NEXT_STEP }

extension UserOnboardingStepUtils on UserOnboardingStep {
  Widget getStepWidget1() {
    switch (this) {
      case UserOnboardingStep.ON_COMPLETED:
        return Container(
          height: 12,
          width: 12,
          decoration: BoxDecoration(
              color: AppColor.successGreen,
              borderRadius: BorderRadius.circular(360)),
          child: Center(
              child: Icon(
            Icons.done,
            color: Colors.white,
            size: 8,
          )),
        );
      case UserOnboardingStep.CURRENT_STEP:
        return Container(
          height: 12,
          width: 12,
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(360),
              border: Border.all(color: AppColor.successGreen)),
        );
      case UserOnboardingStep.NEXT_STEP:
        return Container(
          height: 12,
          width: 12,
          decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(360),
              border: Border.all(color: AppColor.successGreen)),
        );
    }
  }
}

class GbEditTextDecor extends InputDecoration {
  GbEditTextDecor(
    String labelText, {
    Widget? prefix,
    Widget? suffix,
  }) : super(
            filled: true,
            labelText: labelText,
            counterText: "",
            border: InputBorder.none,
            prefix: prefix,
            suffix: suffix,
            labelStyle: TextStyle(
                color: AppColor.grayText9E9E9E, fontWeight: FontWeight.w700),
            fillColor: AppColor.inputBackground,
            errorBorder: OutlineInputBorder(
                borderSide: BorderSide(color: AppColor.errorRed)),
            errorStyle: TextStyle(color: AppColor.errorRed));
}
