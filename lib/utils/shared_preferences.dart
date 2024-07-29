import 'package:shared_preferences/shared_preferences.dart';

////Created by saurabh.lahoti on 18/08/21

class SharedPreferenceHelper {
  static SharedPreferenceHelper? _instance;

  SharedPreferenceHelper._();

  static SharedPreferenceHelper get getInstance =>
      _instance ??= SharedPreferenceHelper._();

  Future<SharedPreferences> _getSharedPreferenceInstance() async {
    return await SharedPreferences.getInstance();
  }

  Future<String?> getStringPref(String key) async {
    SharedPreferences preferences = await _getSharedPreferenceInstance();
    return Future.value(preferences.getString(key));
  }

  setStringPref(String key, String value) async {
    var instance = await _getSharedPreferenceInstance();
    instance.setString(key, value);
  }

  removeKey(String key) async {
    var instance = await _getSharedPreferenceInstance();
    instance.remove(key);
  }

  Future<bool?> getBoolPref(String key) async {
    SharedPreferences preferences = await _getSharedPreferenceInstance();
    return Future.value(preferences.getBool(key));
  }

  setBoolPref(String key, bool value) async {
    var instance = await _getSharedPreferenceInstance();
    instance.setBool(key, value);
  }

  setDoublePref(String key, double value) async {
    var instance = await _getSharedPreferenceInstance();
    instance.setDouble(key, value);
  }

  setIntPref(String key, int value) async {
    var instance = await _getSharedPreferenceInstance();
    instance.setInt(key, value);
  }

  Future<int?> getIntPref(String key) async {
    SharedPreferences preferences = await _getSharedPreferenceInstance();
    return Future.value(preferences.getInt(key));
  }


  Future<double?> getDoublePref(String key) async {
    SharedPreferences preferences = await _getSharedPreferenceInstance();
    return Future.value(preferences.getDouble(key));
  }


  Future<bool> isLoggedIn() async {
    var instance = await _getSharedPreferenceInstance();
    String? userData = instance.getString(PrefKeys.USER_DATA);
    String? auth = instance.getString(PrefKeys.USER_AUTH);
    return Future.value(userData != null && auth != null);
  }
}

class PrefKeys {
  static const KEY_DEVICE_INFO = "device_info";
  static const INVITE_CODE = "invite_code";

  static const BOOL_UPLOAD_IMAGES = "upload_images";
  static const BOOL_CAPTURE_IMAGES = "capture_images";
  static const USER_DATA = "user_data";
  static const USER_AUTH = "user_auth";
  static const IS_NEW_USER_FIRST_SESSION = "is_new_user_first_session";

  static const UPI_ID = "upi_id";
  // static const IS_ANY_TOURNAMENT_JOIN = "is_any_tournament_join";
  static const BOOL_NEW_REGISTERED_CHECK= "bool_new_registered_check";
  static const BOOL_TIER_SUBMISSION_VIDEO_SHOW = "bool_show_video_tier_submission";
  static const ATTRIBUTION_RECORDED = "attribution_recorded";
  static const REFERRED_ID = "referrer_id";
  static const USER_SHARE_LINK = "branch_user_link";
  static const ADDITIONAL_PERMISSIONS = "additional_permissions";
  static const LAST_VISITED_GROUP_PAGE = "last_visited_group";
  static const SESSION_DATA = "session_data";
  static const FIRST_TS = "first_ts";
  static const REWARD_DIALOG_SHOW = "reward_dialog_show";

  static const INVITE_DIALOG_ON_WALLET_BALANCE_COUNT = "invite_dialog_on_wallet_balance_count";
  static const INVITE_DIALOG_ON_GAME_SUBMISSION_COUNT = "invite_dialog_on_game_submission_count";
  static const SELECTED_GAMES = "selected_game";
  static const IS_FFMAX_ENABLE = "is_ffmax_enable";
  static const SHOW_ONBOARDING_FLOW = "SHOW_ONBOARDING_FLOW";


}
