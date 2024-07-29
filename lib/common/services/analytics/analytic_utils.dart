import 'package:firebase_remote_config/firebase_remote_config.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/services/analytics/abstract_analytics_service.dart';

////Created by saurabh.lahoti on 02/01/22

class AnalyticService extends AbstractAnalyticsService{
  static AnalyticService? _instance;

  AnalyticService._();

  static AnalyticService getInstance() => _instance ??= AnalyticService._();

  void trackEvents(String eventName, {Map<String, dynamic>? properties}) {
    Constants.PLATFORM_CHANNEL.invokeMethod("analytics", {
      "analytics_mode": "event",
      "data": {"event_name": eventName, "properties": properties ?? {}}
    });
  }

  void pushUserProfile(String userId) {
    Constants.PLATFORM_CHANNEL.invokeMethod("user_profile", {"user_id": userId});
  }

  void pushUserProperties(Map<String, dynamic> properties) {
    Constants.PLATFORM_CHANNEL
        .invokeMethod("analytics", {"analytics_mode": "user_properties", "data": properties});
  }
}

class Events {
  static const SIGN_IN_CLICKED = "sign_in_clicked";
  static const CREATE_ACCOUNT_CLICKED = "create_account_clicked";
  static const FORM_SUBMITTED = "create_account_form_submitted";
  static const CREATE_ACCOUNT_ERROR = "create_account_error";
  static const OTP_SUBMITTED = "otp_submitted";
  static const USER_SIGNED_IN = "user_signed_in";
  static const SIGN_IN_STARTED = "sign_in_started";
  static const SIGN_OUT_CLICKED = "sign_out_clicked";
  static const USER_SIGNED_OUT = "user_signed_out";
  static const INTRO_VIDEO_PLAY = "intro_video_play";
  static const GAME_TIER_SUBMITTED ="game_tier_submitted";
  static const GAME_TIER_ERROR ="game_tier_error";
  static const CUSTOM_ROOM_PASSWORD_VIEW = "custom_room_password_click";
  static const VIEW_CUSTOM_ROOM_RESULT= "view_custom_room_result";
  static const SHOW_HELPER_TEXT_SQUAD= "show_helper_text_squad";
  static const VIEW_CUSTOM_ROOM_STREAM = "view_custom_room_stream";
  static const LB_CLICKED = "lb_clicked";
  static const LB_JOINED = "lb_joined";
  static const LB_TAB_LOADED = "lb_tab_loaded";
  static const REWARD_POPUP_DISMISSED = "reward_popup_dismissed";
  static const SMARTLOOK_TRACKED = "smartlook_tracked";
  static const INVITE_SHARED = "invite_shared";
  static const INTRO_SHOWN = "intro_shown";
  static const INTRO_DISMISSED = "intro_dismissed";
  static const VIDEO_CLICKED = "video_clicked";
  static const SHOW_INVITE = "show_invite";
  static const REWARD_POPUP_SHOWN = "reward_popup_shown";
  static const LOW_BALANCE_POPUP_DISMISSED = "low_balance_popup_dismissed";
  static const LOW_BALANCE_POPUP_DEPOSIT = "low_balance_popup_deposit_clicked";
  static const WALLET_DEPOSIT_BUTTON_CLICKED = "wallet_deposit_button_clicked";
  static const WALLET_DEPOSIT_POPUP_CONFIRMED = "wallet_deposit_popup_confirmed";
  static const WALLET_DEPOSIT_POPUP_DISMISSED = "wallet_deposit_popup_dismissed";
  static const UPI_APP_SELECTED = "wallet_deposit_upi_app_selected";
  static const NO_UPI_INSTALLED = "wallet_deposit_no_upi_app_installed";
  static const DEPOSIT_COMPLETED = "deposit_completed";
  static const DEPOSIT_FAILED = "deposit_failed";
  static const DEPOSIT_REQUEST_TIMEOUT = "deposit_timeout";
  static const DEPOSIT_CANCELLED = "deposit_cancelled";
  static const ESPORTS_SWITCH_CLICKED = "esports_switch_clicked";
  static const ESPORTS_SELECTED = "esports_selected";
  static const OTPLESS_LOGIN_CLICKED = "otpless_login_clicked";
  static const OTPLESS_SIGNUP_CLICKED = "otpless_signup_clicked";
  static const VIEW_TOP_GAMER_TAB = "view_top_gamer_tab";
  static const TOP_USER_SEARCH_TAP = "top_user_search_tap";
  static const TOP_USER_SEARCH_APPLY = "top_user_search_apply";
  static const TOP_USER_WHATSAPP_CLICK = "top_user_whatsapp_click";
  static const OTPLESS_SUCCESSFULL = "otpless_successful";
  static const OTPLESS_FAILED = "otpless_failed";
  static const USER_PREFERENCE_DIALOG_OPEN = "user_preference_dialog_open";
  static const USER_PREFERENCE_SUBMITTED = "user_preference_submitted";
  static const USER_PREFERENCE_DIALOG_CANCEL = "user_preference_dialog_cancel";
  static const PREFERENCE_SUBMITTED = "preference_submitted";
  static const LOCATION_PREFERENCE_SHOWN = "location_prefs_shown";
  static const MANUAL_LOCATION_ENTERED = "manual_location_entered";
  static const AUTOMATIC_LOCATION_ENTERED = "automatic_location_entered";
  static const LOCATION_SUBMITTED = "location_prefs_submitted";
  static const LOCATION_SUBMISSION_FAILED = "location_submission_failed";
  static const LOCATION_PREFS_SKIPPED = "location_prefs_screen_skipped";

}

extension FilterKeys on FirebaseRemoteConfig {
  Map<String, RemoteConfigValue> getKeysByPrefix(String prefix) {
    final map = <String, RemoteConfigValue>{};
    this.getAll().forEach((key, value) {
      if (key.startsWith(prefix)) {
        map[key] = value;
      }
    });
    return map;
  }
}

