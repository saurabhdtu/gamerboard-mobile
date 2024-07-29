import 'package:flutter/cupertino.dart';
import 'package:flutter_branch_sdk/flutter_branch_sdk.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/time_utils.dart';
import 'package:gamerboard/utils/ui_utils.dart';
import 'package:sprintf/sprintf.dart';
////Created by saurabh.lahoti on 10/03/22

class ShareUtils {
  static ShareUtils? _instance;

  ShareUtils._();

  static ShareUtils getInstance() => _instance ??= ShareUtils._();
  BranchLinkProperties _lp = BranchLinkProperties(
      channel: 'android-app',
      feature: 'sharing',
      campaign: 'app-share',
      stage: 'app-share');

  void teamInviteShare(
      SquadMixin squadMixin, UserTournamentMixin userTournamentMixin,
      {ShareMedium? medium}) async {
    var user =
        (await QueryRepository.instance.getMyProfile(getCached: true)).data!.me;
    AnalyticService.getInstance().trackEvents(Events.INVITE_SHARED, properties: {
      "from": "team_invite_${userTournamentMixin.tournament.tournamentGroup()}",
      "medium": medium?.toString() ?? "external_app"
    });
    final branchContentMetaData = BranchContentMetaData()
      ..addCustomMetadata('deeplinkMode', DeeplinkMode.SQUAD_INVITE)
      ..addCustomMetadata('referrerId', user.id)
      ..addCustomMetadata('name', user.name)
      ..addCustomMetadata('index', userTournamentMixin.homeScreenIndex())
      ..addCustomMetadata('userName', user.username)
      ..addCustomMetadata("referrerCode", user.inviteCode)
      ..addCustomMetadata('inviteCode', squadMixin.inviteCode)
      ..addCustomMetadata('teamName', squadMixin.name)
      ..addCustomMetadata('teamId', squadMixin.id)
      ..addCustomMetadata('tournamentId', userTournamentMixin.tournament.id);
    BranchUniversalObject buo = BranchUniversalObject(
        canonicalIdentifier: 'android/app',
        //canonicalUrl: '',
        title: 'Gamerboard app',
        imageUrl: ImageConstants.APP_LOGO,
        contentDescription: AppStrings.linkShareText,
        keywords: ['Gamerboard', 'User', 'Share'],
        contentMetadata: branchContentMetaData);
    BranchResponse response =
        await FlutterBranchSdk.getShortUrl(buo: buo, linkProperties: _lp);
    if (response.success) {
      debugPrint('link created: ${response.result}');
      _shareText(
          sprintf(AppStrings.squadShareText, [
            user.username,
            userTournamentMixin.tournament.name,
            userTournamentMixin.getAllowedMaps(),
            userTournamentMixin.getAllowedGameMode(),
            userTournamentMixin.allowedTierString(),
            userTournamentMixin.tournament.fee.toString(),
            TimeUtils.instance
                .formatCardDateTime(userTournamentMixin.tournament.startTime),
            TimeUtils.instance.duration(
                userTournamentMixin.tournament.startTime,
                userTournamentMixin.tournament.endTime),
            squadMixin.inviteCode,
            response.result,
          ]),
          medium: medium);
    } else {
      UiUtils.getInstance.showToast(response.errorMessage);
      debugPrint('Error : ${response.errorCode} - ${response.errorMessage}');
    }
  }

  void inviteApp({ShareMedium? medium,InviteDialogEvent? inviteDialogEvent}) async {
    var user =
        (await QueryRepository.instance.getMyProfile(getCached: true)).data!.me;
    AnalyticService.getInstance().trackEvents(Events.INVITE_SHARED, properties: {
      "from": inviteDialogEvent?.getInviteEventSource(),
      "medium": medium?.toString() ?? "external_app"
    });
    final branchContentMetaData = BranchContentMetaData()
      ..addCustomMetadata('deeplinkMode', DeeplinkMode.APP_INVITE)
      ..addCustomMetadata('referrerId', user.id)
      ..addCustomMetadata("referrerCode", user.inviteCode)
      ..addCustomMetadata('name', user.name);
    BranchUniversalObject buo = BranchUniversalObject(
        canonicalIdentifier: 'android/app',
        //canonicalUrl: '',
        title: 'Gamerboard app',
        imageUrl: ImageConstants.APP_LOGO,
        contentDescription: AppStrings.linkShareText,
        keywords: ['Gamerboard', 'User', 'Share'],
        contentMetadata: branchContentMetaData);

    try {
      BranchResponse response =
          await FlutterBranchSdk.getShortUrl(buo: buo, linkProperties: _lp);
      if (response.success) {
        debugPrint('link created: ${response.result}');
        _shareText(
            sprintf(
                AppStrings.inviteVariant, [user.inviteCode, response.result]),
            medium: medium);
      } else {
        UiUtils.getInstance.showToast(response.errorMessage);
        debugPrint('Error : ${response.errorCode} - ${response.errorMessage}');
      }
    } catch (ex) {
      debugPrint(ex.toString());
    }
  }

  void _shareText(String content, {ShareMedium? medium}) {
    switch (medium) {
      case ShareMedium.CLIPBOARD:
        UiUtils.getInstance.copyToClipboard(content);
        break;
      case ShareMedium.WHATSAPP:
        Constants.PLATFORM_CHANNEL.invokeMethod(
            "share", {"content": content, "package": "com.whatsapp"});
        break;
      default:
        Constants.PLATFORM_CHANNEL.invokeMethod("share", {"content": content});
    }
  }
}

enum ShareMedium { CLIPBOARD, WHATSAPP }

enum InviteDialogEvent {
  ON_INVITE_CLICK,
  ON_WALLET_BALANCE_ZERO,
  ON_MORE_THAN_ONE_GAME_SUBMIT
}

extension inviteDialogEventUtils on InviteDialogEvent {
  String getInviteEventSource() {
    switch (this) {
      case InviteDialogEvent.ON_INVITE_CLICK:
        return "app_invite_user";
      case InviteDialogEvent.ON_WALLET_BALANCE_ZERO:
        return "app_invite_on_zero_wallet_balance";
      case InviteDialogEvent.ON_MORE_THAN_ONE_GAME_SUBMIT:
        return "app_invite_on_more_than_one_game_submit";
    }
  }
}

class DeeplinkMode {
  static const APP_SHARE = "app_share";
  static const APP_INVITE = "app_invite";
  static const SQUAD_INVITE = "squad_invite";
}

void openUrlInExternalBrowser(String url) {
  Constants.PLATFORM_CHANNEL.invokeMethod("open_url", {"url": url});
}
