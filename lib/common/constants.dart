import 'package:flutter/services.dart';
import 'package:gamerboard/common/models/device.dart';

////Created by saurabh.lahoti on 02/08/21
class ImageConstants {
  static const String DEFAULT_USER_PLACEHOLDER =
      "https://storage.googleapis.com/gb-app/gamer.png";
  static const String TIER_PREFIX_URL =
      "https://storage.googleapis.com/gb-app/assets/%s/%s.png";
  static const String APP_LOGO =
      "https://storage.googleapis.com/gb-app/app_logo.png";
}

class VideoConstants {
  static const String APP_LOCK_VIDEO =
      "https://storage.googleapis.com/gb-app/tutorial-app-lock.mp4";
  static const String TUTORIAL_MATCH_VIDEO =
      "https://storage.googleapis.com/gb-app/tutorial-match-explanation.mp4";
}

class GameLevels {
  static const BGMI_LEVEL = [
    "Bronze",
    "Silver",
    "Gold",
    "Platinum",
    "Diamond",
    "Crown",
    "Ace",
    "Conqueror"
  ];

  static const FFMAX_LEVEL = [
    "Bronze",
    "Silver",
    "Gold",
    "Platinum",
    "Diamond",
    'Heroic',
    'Master',
    'GRANDMASTER'
  ];
}

class GameRanks {
  static const BGMI_RANK = [
    "(I - V)",
    "(I - V)",
    "(I - V)",
    "(I - V)",
    "(I - V)",
    "(I - V)",
    "(Ace, Master, Dominator)",
    "(I - V)"
  ];

  static const FFMAX_RANK = [
    "(I - III)",
    "(I - III)",
    "(I - IV)",
    "(I - IV)",
    "(I - IV)",
    "Heroic",
    "Master",
    "(I - VI)"
  ];
}

class URLConstants {
  static const String HELP_BGMI_TIER_FINDER =
      "https://gamerboard.notion.site/Tiers-03bb4a70814b4b838fcb4bf3fcbfa3ac#5fb1ac22c62840a9af3bb2a0ba814f52";
  static const String HELP_FFMAX_TIER_FINDER = "https://gamerboard.notion.site/FFMAX-Tiers-7de3565588194aac96defbbecfa3e977";
  static const String HELP_TOP_GAME_COUNT =
      "https://gamerboard.notion.site/Scoring-b460ee791c4d48b2b3969e15ea1e6583#19657604f46d4e228b7cd8d650efc9cd";
  static const String HELP_CUSTOM_ROOM =
      "https://www.notion.so/gamerboard/Custom-Tournament-Rules-7b67d6ac38624b6f80d741237fec8fae";
  static const String NEWS =
      "https://gamerboard.notion.site/News-f2214a71b7d6495b9ed81eed1a858da5";
}

BuildConfig? buildConfig;

class Constants {
  static const PLATFORM_CHANNEL =
      const MethodChannel("com.gamerboard.live/platform");
  static const LOCAL_CHANNEL = const MethodChannel("com.gamerboard.live/local");
  static const BG_PLUGIN = const MethodChannel("com.gamerboard.live/bg_plugin");
  static const BG_PLUGIN_SERVICE =
      const MethodChannel("com.gamerboard.live/bg_service");

  static const int INVITE_CODE = 6;
  static const int SQUAD_CREATION_CODE = 7;
  static const int REFERRAL_AMOUNT = 30;

}

class DB {
  static const DATABASE_NAME = "app_database.db";
  static const DATABASE_VERSION = 1;
  static const TABLE_SESSION = "game_session";
  static const CREATE_TABLE_SESSION =
      'CREATE TABLE $TABLE_SESSION (id INTEGER PRIMARY KEY, path TEXT, json TEXT, url TEXT, uploaded INTEGER)';
}

class RemoteConfigConstants {
  static const APP_UPDATE_ANDROID = "app_update_android";
  static const NEW_USER_REWARD = "new_user_reward";
  static const ONBOARDING_DATA = "onboarding_data";
  static const WALLET_FLAGS = "wallet_flags";
  static const GB_DISCORD_LINK = "gb_discord_link";
  static const GB_CUSTOM_ROOM_PLAYLIST = "gb_custom_room_playlist";

  static const GB_RELAUNCH_VIDEO_ID = "gb_relaunch_video_id";
  static const EXP_SHOW_PLAY_BUTTON_ON_CARD = "exp_show_play_button_on_card";
  static const EXP_SHOW_ONBOARDING_VIDEO = "exp_show_onboarding_video";
  static const ONBOARDING_STEPS = "onboarding_steps";


}

