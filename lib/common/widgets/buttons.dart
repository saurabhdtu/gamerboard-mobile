import 'package:flutter/material.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/colors.dart';

////Created by saurabh.lahoti on 13/12/21
Widget darkBorderButton(String text, GestureTapCallback onPressed,
        {Color textColor = Colors.white,
        double? paddingVertical,
        double? paddingHorizontal,
        Icon? icon,
        Color borderColor = AppColor.buttonBorderColor}) =>
    InkWell(
        onTap: onPressed,
        child: DecoratedBox(
            child: Padding(
                padding: EdgeInsets.symmetric(
                    horizontal: paddingHorizontal ?? 16.0,
                    vertical: paddingVertical ?? 12.0),
                child:
                    Row(mainAxisAlignment: MainAxisAlignment.center, children: [
                  if (icon != null)
                    Padding(padding: EdgeInsets.only(right: 5.0), child: icon),
                  RegularText(text, color: textColor)
                ])),
            decoration: BoxDecoration(
                color: AppColor.dividerColor,
                shape: BoxShape.rectangle,
                border: Border.all(color: AppColor.dividerColor, width: 1.0))));

Widget outlineButton(String text, Function() onClick,
    { Widget? prefix, Widget? suffix}) {
  return InkWell(
    onTap: () {
      onClick.call();
    },
    child: Container(
        padding: EdgeInsets.symmetric(
            horizontal:  16.0,
            vertical: 12.0),
        decoration:
            BoxDecoration(border: Border.all(color: AppColor.buttonActive)),
        child: Padding(
            padding: const EdgeInsets.symmetric(vertical: 1.0),
            child: Row(mainAxisAlignment: MainAxisAlignment.center, children: [
              if (prefix != null) prefix,
              const SizedBox(width: 4.0),
              Expanded(child: RegularText(text, color: AppColor.buttonActive, textAlign: TextAlign.center, )),
              const SizedBox(width: 4.0),
              if (suffix != null) suffix,
            ]))),
  );
}

Widget primaryButton(String text, GestureTapCallback onPressed,
        {Color textColor = Colors.white,
        bool isQualified = false,
        double? paddingVertical,
        double? paddingHorizontal,
        bool active = false}) =>
    InkWell(
        onTap: active ? onPressed : null,
        child: DecoratedBox(
            child: Padding(
                padding: EdgeInsets.symmetric(
                    horizontal: paddingHorizontal ?? 16.0,
                    vertical: paddingVertical ?? 12.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Visibility(
                        visible: isQualified,
                        child: Icon(
                          Icons.star,
                          color: Color(0xfff0e8fd),
                        )),
                    BoldText(text,
                        fontSize: 14.0,
                        color: AppColor.whiteTextColor,
                        textAlign: TextAlign.center),
                    Visibility(
                        visible: isQualified,
                        child: Icon(
                          Icons.star,
                          color: Color(0xfff0e8fd),
                        )),
                  ],
                )),
            decoration: BoxDecoration(
                shape: BoxShape.rectangle,
                color:
                    active ? AppColor.buttonActive : AppColor.buttonGrayBg)));

Widget secondaryButton(String text, GestureTapCallback onPressed,
        {Color textColor = Colors.black,
        bool isQualified = false,
        double? paddingVertical,
        double? paddingHorizontal,
        double? fontSize,
        bool inverted = false,
        bool active = true}) =>
    InkWell(
        onTap: active ? onPressed : null,
        child: DecoratedBox(
            child: Padding(
                padding: EdgeInsets.symmetric(
                    horizontal: paddingHorizontal ?? 16.0,
                    vertical: paddingVertical ?? 12.0),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Visibility(
                        visible: isQualified,
                        child: Icon(
                          Icons.star,
                          color: Color(0xfff0e8fd),
                        )),
                    BoldText(text,
                        fontSize: 14.0,
                        color: AppColor.whiteTextColor,
                        textAlign: TextAlign.center),
                    Visibility(
                        visible: isQualified,
                        child: Icon(
                          Icons.star,
                          color: Color(0xfff0e8fd),
                        )),
                  ],
                )),
            decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(2.0),
                border: inverted
                    ? Border.all(color: AppColor.buttonActive, width: 1.0)
                    : null,
                shape: BoxShape.rectangle,
                color: active
                    ? (inverted ? Colors.transparent : AppColor.buttonActive)
                    : AppColor.buttonGrayBg)));

Widget iconTextButton(String text, GestureTapCallback onPressed,
        {Widget? icon,
        EdgeInsets? padding,
        Color backgroundColor = Colors.white,
        Color textColor = AppColor.textColorDark}) =>
    InkWell(
        onTap: onPressed,
        child: Container(
            color: backgroundColor,
            padding: const EdgeInsets.all(9.0),
            child: Row(children: [
              if (icon != null) icon,
              if (icon != null) const SizedBox(width: 17.0),
              RegularText(text, color: textColor, fontWeight: FontWeight.w700)
            ])));

Widget primaryBorderButton(String text, GestureTapCallback onPressed,
        {Color textColor = Colors.black,
        double? paddingVertical,
        double? paddingHorizontal,
        double? fontSize,
        bool inverted = false,
        bool active = true}) =>
    InkWell(
        onTap: active ? onPressed : null,
        child: DecoratedBox(
            child: Padding(
                padding: EdgeInsets.symmetric(
                    horizontal: paddingHorizontal ?? 16.0,
                    vertical: paddingVertical ?? 12.0),
                child: Center(
                    child: BoldText(text,
                        fontSize: fontSize ?? 14.0,
                        color: AppColor.buttonSecondary,
                        textAlign: TextAlign.center))),
            decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(2.0),
                border: Border.all(color: AppColor.buttonSecondary, width: 1.0),
                shape: BoxShape.rectangle,
                color: active ? (Colors.transparent) : AppColor.buttonGrayBg)));
