import 'package:flutter/material.dart';

////Created by saurabh.lahoti on 31/08/21
class RegularText extends Text {
  RegularText(
    String text, {
    double? fontSize = 14.0,
    TextAlign textAlign = TextAlign.start,
    Color color = Colors.white,
    int? maxLines,
    TextOverflow? overflow,
    double? height,
    TextDecoration? decoration,
    Color? decorationColor,
    FontWeight fontWeight = FontWeight.w500,
    FontStyle fontStyle = FontStyle.normal,
  }) : super(text,
            textAlign: textAlign,
            maxLines: maxLines,
            overflow: overflow,
            textScaleFactor: 1.0,
            style: TextStyle(
                fontSize: fontSize,
                color: color,
                height: height,
                decoration: decoration,
                decorationColor: decorationColor,
                fontWeight: fontWeight,
                fontStyle: fontStyle));
}

class RegularTextStyle extends TextStyle {
  RegularTextStyle(
      {double fontSize = 14.0,
      Color color = Colors.white,
      double height = 1.5,
      TextDecoration? decoration,
      Color? decorationColor})
      : super(
            fontSize: fontSize,
            color: color,
            height: height,
            decoration: decoration,
            decorationColor: decorationColor);
}

class SemiBoldText extends Text {
  SemiBoldText(
      String text, {
        double? fontSize = 14.0,
        TextAlign textAlign = TextAlign.start,
        Color color = Colors.white,
        int? maxLines,
        TextOverflow? overflow,
        double? height,
        TextDecoration? decoration,
        Color? decorationColor,
        FontWeight fontWeight = FontWeight.w600,
        FontStyle fontStyle = FontStyle.normal,
      }) : super(text,
      textAlign: textAlign,
      maxLines: maxLines,
      overflow: overflow,
      textScaleFactor: 1.0,
      style: TextStyle(
          fontSize: fontSize,
          color: color,
          height: height,
          decoration: decoration,
          decorationColor: decorationColor,
          fontWeight: fontWeight,
          fontStyle: fontStyle));
}


class SemiBoldTextStyle extends TextStyle {
  SemiBoldTextStyle({double fontSize = 16.0, Color color = Colors.white,FontWeight fontWeight = FontWeight.w600})
      : super(
    fontSize: fontSize,
    color: color,
    fontWeight: fontWeight,
    height: 1.5,
  );
}

class BoldTextStyle extends TextStyle {
  BoldTextStyle({double fontSize = 14.0, Color color = Colors.white})
      : super(fontSize: fontSize, color: color, fontWeight: FontWeight.w700);
}

class TitleText extends Text {
  TitleText(
    String text, {
    double? fontSize = 22.0,
    TextAlign textAlign = TextAlign.start,
    Color color = Colors.white,
  }) : super(text,
            textAlign: textAlign,
            textScaleFactor: 1.0,
            style: TextStyle(fontSize: fontSize, color: color));
}

class BoldText extends Text {
  BoldText(
    String text, {
    double? fontSize = 14.0,
    double? height,
    TextAlign textAlign = TextAlign.start,
    Color color = Colors.white,
  }) : super(text,
            textScaleFactor: 1.0,
            textAlign: textAlign,
            style: TextStyle(
                fontSize: fontSize,
                color: color,
                height: height,
                fontWeight: FontWeight.w700));
}

Widget iconText(Icon icon, Text text, {double spacing = 7.0}) => Row(
    mainAxisAlignment: MainAxisAlignment.center,
    children: [icon, SizedBox(width: spacing), text]);
