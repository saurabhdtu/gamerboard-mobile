import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/models/deeplink_data.dart';
import 'package:gamerboard/common/services/analytics/abstract_analytics_service.dart';
import 'package:gamerboard/common/services/location/google_places_api_service.dart';
import 'package:gamerboard/common/services/location/location_provider.dart';
import 'package:gamerboard/common/services/user/api_user_service.dart';
import 'package:gamerboard/common/widgets/error.dart';
import 'package:gamerboard/feature/game/tournament_bloc.dart';
import 'package:gamerboard/feature/game/tournament_page.dart';
import 'package:gamerboard/feature/history/history.dart';
import 'package:gamerboard/feature/history/history_bloc.dart';
import 'package:gamerboard/feature/home/home_bloc.dart';
import 'package:gamerboard/feature/home/home_page.dart';
import 'package:gamerboard/feature/location/location_page.dart';
import 'package:gamerboard/feature/location/location_page_bloc.dart';
import 'package:gamerboard/feature/login/login_page.dart';
import 'package:gamerboard/feature/login/otp_page.dart';
import 'package:gamerboard/feature/login/sign_up_page.dart';
import 'package:gamerboard/feature/splash/splash.dart';
import 'package:gamerboard/feature/team/team_creation/team_create.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_bloc.dart';
import 'package:gamerboard/feature/team/team_invite/team_invite.dart';
import 'package:gamerboard/feature/team/team_invite/team_invite_bloc.dart';
import 'package:gamerboard/feature/team/team_join_create/team_join_create.dart';
import 'package:gamerboard/feature/team/team_join_create/team_join_create_bloc.dart';
import 'package:gamerboard/feature/videoplayer/video_player.dart';
import 'package:gamerboard/feature/wallet/wallet_page.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/main.dart';
import 'package:gamerboard/utils/shared_preferences.dart';
import 'package:path/path.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';

import '../feature/onboarding_guide/onboarding_guide.dart';

////Created by saurabh.lahoti on 02/08/21
List<String?> _routes = [];

void addRoute(String? route) {
  _routes.add(route);
}

void removeRoute(String? route) {
  _routes.remove(route);
}

bool checkForRoute(String route) {
  return _routes.contains(route);
}

class AppRouter {
  Route onGenerateRoute(RouteSettings routeSettings) {
    writeLogToFile("Route to: ${routeSettings.name}");
    FirebaseAnalytics.instance.logScreenView(
        screenName: routeSettings.name, screenClass: routeSettings.name);
    DeeplinkData? deeplinkData =
        routeSettings.arguments != null && routeSettings.arguments is Map
            ? (routeSettings.arguments as Map)['deeplinkData']
            : null;
    addRoute(routeSettings.name);
    Widget page;
    switch (routeSettings.name) {
      case Routes.SPLASH:
        _routes.clear();
        String? message;
        if (routeSettings.arguments != null)
          message = (routeSettings.arguments as Map)['message'];
        page = SplashPage(message: message);
        break;
      case Routes.HOME_PAGE:
        int? initialIndex;
        if (routeSettings.arguments != null && routeSettings.arguments is Map) {
          initialIndex = (routeSettings.arguments as Map)['index'];
        }
        page = BlocProvider<HomeBloc>(
            create: (context) => HomeBloc(
                userService: ApiUserService.instance,
                deeplinkData: deeplinkData),
            child: HomePage(initialIndex ?? HomeScreenIndex.SOLO));
        break;
      case Routes.SIGN_UP:
        page = SignUpPage();
        break;
      case Routes.ONBOARDING_FLOW:
        page = OnboardingGuide();
        break;
      case Routes.LOG_IN:
        page = LoginPage();
        break;
      case Routes.OTP_PAGE:
        page = OTPPage();
        break;
      case Routes.TOURNAMENT_PAGE:
        TournamentPayload? args;
        if (routeSettings.arguments is Map) {
          args = TournamentPayload(
              int.parse((routeSettings.arguments as Map)['id'].toString()));
        } else if (routeSettings.arguments is TournamentPayload) {
          args = routeSettings.arguments as TournamentPayload;
        }
        if (args != null)
          page = BlocProvider<TournamentBloc>(
              create: (context) => TournamentBloc(args!),
              child: TournamentPage());
        else
          page = ErrorPage();
        break;
      case Routes.VIDEO_PLAYER:
        page = VideoPlayerPage(
          (routeSettings.arguments as Map)['video_url'].toString(),
          (routeSettings.arguments as Map)['title'].toString(),
          (routeSettings.arguments as Map)['source'].toString(),
        );
        break;
      case Routes.WALLET:
        page = WalletPage();
        break;
      case Routes.GAME_HISTORY:
        page = BlocProvider<HistoryBloc>(
            create: (context) => HistoryBloc(), child: HistoryPage());
        break;
      case Routes.TEAM_JOIN_CREATE:
        page = BlocProvider<TeamJoinCreateBloc>(
            create: (context) => TeamJoinCreateBloc(
                  (routeSettings.arguments as Map)['tournament'],
                  (routeSettings.arguments as Map)['fromTournament'],
                  phoneNumber: (routeSettings.arguments as Map)['phoneNumber'],
                ),
            child: TeamJoinCreateOption());
        break;
      case Routes.TEAM_INVITE:
        page = BlocProvider<TeamInviteBloc>(
            create: (context) => TeamInviteBloc(
                (routeSettings.arguments as Map)['tournament'],
                (routeSettings.arguments as Map)['squad']),
            child: TeamInvite());
        break;
      case Routes.TEAM_CREATE:
        page = BlocProvider<TeamCreateBloc>(
            create: (context) => TeamCreateBloc(
                (routeSettings.arguments as Map)['tournament'],
                inviteCode: (routeSettings.arguments as Map)['inviteCode'],
                phoneNumber: (routeSettings.arguments as Map)['phoneNumber'],
                joinCode: (routeSettings.arguments as Map)['joinCode'],
                fromTournament:
                    (routeSettings.arguments as Map)['fromTournament'],
                onlyPayment: (routeSettings.arguments as Map)['onlyPayment']),
            child: TeamCreate());
        break;
      case Routes.LOCATION_PAGE:
        page = BlocProvider<LocationPageBloc>(
          create: (context) => LocationPageBloc(
            analyticsService: AnalyticService.getInstance(),
            userService: ApiUserService.instance,
            placesService: GooglePlacesApiService(),
            locationProvider: DeviceLocationProvider(),
          ),
          child: LocationPage(),
        );
        break;
      default:
        page = SplashPage();
    }
    return MaterialPageRoute(
        builder: (context) => page, settings: routeSettings);
  }
}

class Routes {
  static const SPLASH = "splash";
  static const SIGN_UP = "sign_up";
  static const LOG_IN = "log_in";
  static const OTP_PAGE = "otp";
  static const HOME_PAGE = "home";
  static const TOURNAMENT_PAGE = "tournament";
  static const GAME_HISTORY = "history";
  static const WALLET = "wallet";
  static const VIDEO_PLAYER = "video_player";
  static const TEAM_INVITE = "team_invite";
  static const ONBOARDING_FLOW = "onboarding_flow";
  static const TEAM_JOIN_CREATE = "team_join_create";
  static const TEAM_CREATE = "team_creation";
  static const LOCATION_PAGE = "location_page";
  static const WEB_VIEW = "webview";

  static Future<dynamic> launchVideoTutorial(
      BuildContext context, String videoUrl, String title, String source) {
    return Navigator.of(context).pushNamed(Routes.VIDEO_PLAYER,
        arguments: {"video_url": videoUrl, 'title': title, 'source': source});
  }
}

class TournamentPayload {
  int tournamentId;
  bool showTeamDetail;

  TournamentPayload(this.tournamentId, {this.showTeamDetail = false});
}

navigateToWebPage(BuildContext context, String webUrl, {String? title}) {
  Navigator.of(context).pushNamed(Routes.WEB_VIEW,
      arguments: {"title": title, "web_url": webUrl});
}

Future<dynamic> navigateToInvitePage(BuildContext context,
    UserTournamentMixin userTournamentMixin, SquadMixin squad) {
  return Navigator.of(context).pushNamed(Routes.TEAM_INVITE,
      arguments: {'tournament': userTournamentMixin, 'squad': squad});
}

Future<dynamic> navigateToCreateTeam(
    BuildContext context,
    UserTournamentMixin userTournamentMixin,
    bool fromTournament,
    bool onlyPayment,
    {String? phoneNumber,
    required String? inviteCode}) {
  return Navigator.of(context).pushNamed(Routes.TEAM_CREATE, arguments: {
    "tournament": userTournamentMixin,
    "fromTournament": fromTournament,
    "onlyPayment": onlyPayment,
    "phoneNumber": phoneNumber,
    "inviteCode": inviteCode
  });
}

Future<Map<String, Map<String, dynamic>>> deeplinkNavigation(
    DeeplinkData deeplinkData) async {
  Map<String, Map<String, dynamic>> map;
  if (await SharedPreferenceHelper.getInstance.isLoggedIn()) {
    try {
      switch (deeplinkData.route.toString().toLowerCase()) {
        case "tournament":
          map = {
            Routes.TOURNAMENT_PAGE: {"id": deeplinkData.id}
          };
          break;
        case "home":
          map = await _navigateToHome(index: deeplinkData.index);
          break;

        case "wallet":
          map = {Routes.WALLET: {}};
          break;
        case "sign_up":
          map = {Routes.SIGN_UP: {}};
          break;

        default:
          map = await _navigateToHome();
      }
    } catch (e) {
      map = await _navigateToHome();
    }
  } else {
    map = await _navigateToHome(
        index: deeplinkData.index ?? HomeScreenIndex.SOLO);
  }
  map.values.first.addAll({"deeplinkData": deeplinkData});
  return map;
}

Future<Map<String, Map<String, dynamic>>> _navigateToHome({int? index}) async {
  return {
    Routes.HOME_PAGE: {
      "index": index ??
          (await SharedPreferenceHelper.getInstance
              .getIntPref(PrefKeys.LAST_VISITED_GROUP_PAGE)) ??
          HomeScreenIndex.SOLO
    }
  };
}
