import 'package:flutter/cupertino.dart';
import 'package:gamerboard/main.dart';

class AppColor {
  static Color colorAppBackground = isDarkTheme() ? Color(0xff000000) : Color(
      0xffffffff);
  static Color colorAppTheme = isDarkTheme() ? Color(0xff000000) : Color(
      0xffffffff);
  static Color colorPrimary = isDarkTheme() ? Color(0xff3c3c3c) : Color(
      0xff1c1c1c);
  static Color colorPrimaryDark = isDarkTheme() ? Color(0xff565656) : Color(
      0xff292928);
  static const Color colorAccent = Color(0xff1321C9);
  static const Color gray9f9f9f=Color(0xff9f9f9f);
  static const Color titleBarBg = Color(0xff00D0D0D);
  static const Color buttonBorderColor = Color(0xff3B454D);
  static const Color buttonGrayBg = Color(0xff222224);
  static const Color textDarkGray = Color(0xff797B7D);
  static const Color leaderboardRow = Color(0xff474C56);
  static const Color buttonActive = Color(0xff6C43E5);
  static const Color buttonSecondary = Color(0xffA375F3);
  static const Color highlightOrange = Color(0xffFF9900);
  static const Color transactionCredit = Color(0xffA6FFEA);
  static const Color transactionDebit = Color(0xffED95A5);
  static const Color grayText9E9E9E = Color(0xff898989);
  static const Color textSubTitle = Color(0xffDADEDF);
  static const Color textWhiteDADEDF = Color(0xffdadedf);
  static const Color errorRed = Color(0xffD32645);
  static const Color successGreen = Color(0xff2FD8B0);
  static const Color cardTextColor = Color(0xffD8E8F8);
  static const Color highlightCardTextColor = Color(0xffFFE144);
  static const Color disabledLBCardBG = Color(0x52881f32);
  static const Color tournamentCardBG = Color(0xff0D0D0D);
  static const Color textHighlighted = Color(0xff7a76f7);
  static const Color dividerColor = Color(0xff1B1B1C);
  static const Color gray767677 = Color(0xff767677);
  static const Color purplleA78DF2=Color(0xffA78DF2);
  static const Color inputBackground = Color(0xff111315);
  static const Color colorGrayBg1 = Color(0xff383A3F);
  static const Color colorGrayBg2 = Color(0xff323438);
  static const Color textColorDark = Color(0xff353942);
  static const Color darkBackground = Color(0xff212329);
  static const Color whiteTextColor = Color(0xffffffff);
  static const Color whatsAppGreenColor=Color(0xff0BC33F);
  static const Color screenBackground = Color(0xff0D0D0D);
  static const Color whiteF5F5F5= Color(0xffF5F5F5);
  static const Color grayA0A0A0=Color(0xffA0A0A0);
  static const Color lightWhiteColor = Color(0xff727272);
  static const Color grayTextB3B3B3 = Color(0xffB3B3B3);
  static const Color enableTournamentCard = Color(0xff121212);
  static const Color cardTextHighlightColor = Color(0xffB9A3F2);
  static const Color whatsappColor= Color(0xff4E9947);
  static const Color grayText696969 = Color(0xff696969);
  static const Color grayText8F8F90 = Color(0xff8F8F90);
  static const Color grayText747474 = Color(0xff747474);
  static const Color grayTextC6C6C6 = Color(0xffC6C6C6);
  static const Color grayIconCCCCCC = Color(0xffCCCCCC);
  static const Color blackColor3E3E3E=Color(0xff3E3E3E);
  static const Color grayColorf0e8fd=Color(0xfff0e8fd);
  static const Color whiteF6F6F6 =Color(0xffF6F6F6);
  static const Color blueD5C6F7 = Color(0xffD5C6F7);
  static const Color whiteEEE9FC = Color(0xffEEE9FC);
  static const Color grayB8B8B9 = Color(0xffB8B8B9);
  static const Color grayA9A9A9=Color(0xffA9A9A9);
  static const Color black1E1E1E = Color(0xff1E1E1E);

  static LinearGradient popupBackgroundGradient = LinearGradient(
      colors: [Color(0xff222224), Color(0xff222224)],
      begin: Alignment.centerLeft,
      end: Alignment.centerRight);
  static LinearGradient cardGradient = LinearGradient(
      colors: [Color(0xff04141C), Color(0xff111317)],
      begin: Alignment.topCenter,
      end: Alignment.bottomCenter);
}
