import 'dart:async';
import 'dart:io';
import 'dart:isolate';

import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_crashlytics/firebase_crashlytics.dart';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/bloc/application/application_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/services/feature_flag/flagsmit_feature_manager.dart';
import 'package:gamerboard/feature/bgservice/capture_service.dart';
import 'package:gamerboard/feature/login/login_bloc.dart';
import 'package:gamerboard/feature/wallet/wallet_bloc.dart';
import 'package:gamerboard/resources/colors.dart';

class _MyHttpOverrides extends HttpOverrides {
  @override
  HttpClient createHttpClient(SecurityContext? context) {
    return super.createHttpClient(context)
      ..badCertificateCallback =
          (X509Certificate cert, String host, int port) => true;
  }
}

void main() async {
  HttpOverrides.global = new _MyHttpOverrides();
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  await FirebaseCrashlytics.instance.setCrashlyticsCollectionEnabled(true);
  Isolate.current.addErrorListener(RawReceivePort((pair) async {
    final List<dynamic> errorAndStacktrace = pair;
    await FirebaseCrashlytics.instance.recordError(
      errorAndStacktrace.first,
      errorAndStacktrace.last,
    );
  }).sendPort);

  FlutterError.onError = (errorDetails) {
    try{
      FirebaseCrashlytics.instance.recordFlutterFatalError(errorDetails);
    }catch(e){

    }
  };

  // Pass all uncaught asynchronous errors that aren't handled by the Flutter framework to Crashlytics
  PlatformDispatcher.instance.onError = (error, stack) {
    FirebaseCrashlytics.instance.recordError(error, stack, fatal: true);
    return true;
  };
  runApp(MyApp(AppRouter()));
}

final RouteObserver<ModalRoute<void>> routeObserver =
    RouteObserver<ModalRoute<void>>();

class MyApp extends StatefulWidget {
  final AppRouter _appRouter;

  MyApp(this._appRouter);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  Widget build(BuildContext context) {
    return MultiBlocProvider(
        providers: [
          BlocProvider<ApplicationBloc>(create: (ctx) => ApplicationBloc()),
          BlocProvider<LoginBloc>(
              create: (ctx) => LoginBloc(FlagsmitFeatureManager(
                  ctx.read<ApplicationBloc>().flagsmithClient))),
          BlocProvider<WalletBloc>(create: (ctx) => WalletBloc())
        ],
        child: MaterialApp(
            title: 'Gamerboard-LIVE',
            initialRoute: Routes.SPLASH,
            navigatorKey: NavigatorService.navigatorKey,
            onGenerateRoute: widget._appRouter.onGenerateRoute,
            theme: ThemeData(
                primaryColor: AppColor.colorPrimary,
                scaffoldBackgroundColor: AppColor.colorAppTheme,
                focusColor: AppColor.colorAccent,
                dividerColor: AppColor.dividerColor,
                inputDecorationTheme: InputDecorationTheme(
                    focusedBorder: UnderlineInputBorder(
                        borderSide: BorderSide(color: AppColor.colorAccent)),
                    enabledBorder: UnderlineInputBorder(
                        borderSide: BorderSide(color: AppColor.dividerColor))),
                textSelectionTheme:
                    TextSelectionThemeData(cursorColor: AppColor.colorAccent),
                checkboxTheme: CheckboxTheme.of(context).copyWith(
                    fillColor: MaterialStateProperty.all(AppColor.colorAccent),
                    shape: RoundedRectangleBorder(
                        side: BorderSide(color: AppColor.textSubTitle),
                        borderRadius: BorderRadius.circular(1.0))),
                progressIndicatorTheme: ProgressIndicatorTheme.of(context)
                    .copyWith(color: AppColor.colorAccent),
                primaryColorDark: AppColor.colorPrimaryDark,
                canvasColor: AppColor.colorAppTheme,
                fontFamily: 'regular',
                radioTheme: RadioTheme.of(context).copyWith(
                    splashRadius: 10.0,
                    fillColor: MaterialStateProperty.all(AppColor.colorAccent)),
                splashColor: AppColor.colorAppTheme,
                backgroundColor: AppColor.colorAppTheme,
                visualDensity: VisualDensity.adaptivePlatformDensity)));
  }

  @override
  void initState() {
    super.initState();
    SystemChrome.setEnabledSystemUIMode(SystemUiMode.immersiveSticky);
    BackgroundServicePlugin.initialize();
  }
}

bool isDarkTheme() => true;

void writeLogToFile(String log) {
  Constants.PLATFORM_CHANNEL.invokeMethod("log", {"message": log});
}

logException(ex, stack) {
  FirebaseCrashlytics.instance.recordError(ex, stack);
  debugPrint(ex?.toString());
}

class NavigatorService {
  static GlobalKey<NavigatorState> navigatorKey = GlobalKey<NavigatorState>();
}

/// dart run build_runner build --delete-conflicting-outputs
