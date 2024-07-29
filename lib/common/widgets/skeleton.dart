import 'package:flutter/material.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/colors.dart';

////Created by saurabh.lahoti on 20/09/21
Widget appCircularProgressIndicator() {
  return Center(
      child: SizedBox(
          width: 24.0,
          height: 24.0,
          child: CircularProgressIndicator(strokeWidth: 1.5)));
}

Widget appScaffold(
    {AppBar? appBar,
      Widget? body,
      Widget? drawer,
      Widget? endDrawer,
      GlobalKey<ScaffoldState>? scaffoldStateKey,
      bool resizeToAvoidBottomInset = true}) =>
    Container(
        decoration: BoxDecoration(color: AppColor.screenBackground),
        child: Scaffold(
            key: scaffoldStateKey ?? GlobalKey<ScaffoldState>(),
            appBar: appBar,
            body: body,
            drawer: drawer,
            endDrawer: endDrawer,
            resizeToAvoidBottomInset: resizeToAvoidBottomInset,
            backgroundColor: Colors.transparent));

/*class AppScaffold extends StatelessWidget {
  final PreferredSizeWidget? appBar;
  final Widget? body;
  final Widget? drawer;
  final bool resizeToAvoidBottomInset;

Widget cardContainer(Widget child,
    {double? width, double? height, EdgeInsetsGeometry? padding}) =>
    Container(
        child: child,
        width: width,
        height: height,
        padding: padding,
        decoration: BoxDecoration(
            border: Border.all(color: AppColor.buttonBorderColor, width: 1.0),
            borderRadius: BorderRadius.circular(2.0),
            gradient: AppColor.cardGradient));
class AppScaffold extends StatelessWidget {
  final PreferredSizeWidget? appBar;
  final Widget? body;
  final Widget? drawer;
  final bool resizeToAvoidBottomInset;

  @override
  Widget build(BuildContext context) => Container(
      decoration: BoxDecoration(gradient: AppColor.screenBackgroundGradient),
      child: Scaffold(
          appBar: appBar,
          body: body,
          drawer: drawer,
          resizeToAvoidBottomInset: resizeToAvoidBottomInset,
          backgroundColor: Colors.transparent));
}*/

AppBar appBar(BuildContext context, String title,
    {Widget? titleWidget,
          VoidCallback? backAction,
          bool centerTitle = false,
          bool showBack = true,
          List<Widget>? actions}) =>
    AppBar(
        toolbarHeight: 45.0,
        backgroundColor: AppColor.titleBarBg,
        centerTitle: centerTitle,
        automaticallyImplyLeading: false,
        title: titleWidget ?? BoldText(title, fontSize: 16.0),
        actions: actions ?? [],
        leading: showBack
            ? IconButton(
            icon: const Icon(Icons.arrow_back),
            color: Colors.white,
            onPressed: backAction ?? () => Navigator.pop(context))
            : null);
