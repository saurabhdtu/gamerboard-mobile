import 'package:flagsmith/flagsmith.dart';

import '../common/services/analytics/analytic_utils.dart';

class FlagKeys {
  static const FIRST_GAME_BONUS_FLAG = "first_game_bonus";
  static const THREE_KILL_BONUS = "three_kill_bonus";
  static const POP_INTRO_VIDEO = "show_intro_video";
  static const SHOW_PLAY_BUTTON_ON_CARD = "show_play_button_on_card";
  static const FFMAX_ENABLE = "ffmax_enable";
  static const NEW_LOGING_MODE = "new_loging_mode";
  static const PROFILE_NUDGE = "profile_nudge";
  static const KILL_ALGO = "kill_algo";
  static const TOURNAMENT_ORDER = "tournament_order";
  static const ONBOARDING_STEPPER_VISIBLE = "onboarding_stepper_visible";
  static const ENABLE_QUALIFIER_TOURNAMENT = "enable_qualifier_tournament";
  static const SHOW_ONLY_PROFILE_VERIFICATION = "show_only_profile_verification";
  static const GAME_ID_VERIFICATION = "game_id_verification";
  static const CUSTOM_ROOM_ENABLE ="custom_room_enable";
  static const ENABLE_USER_PREFERENCE_DIALOG="enable_user_preference_dialog";
 static const ENABLE_LOCATION_PREFS = "location_prefs";
}

class TraitKeys {
  static const GAMES = "games";
  static const SHOWED_USER_PREFERENCE_DIALOG = "showed_user_preference_dialog";
  static const LOCATION_PREFS_SHOWN = "location_prefs_shown";
  static const SHOW_FIRST_GAME_DIALOG = "show_first_game_bonus_dialog";
  static const KILLS = "kills";
  static const ONBOARDING_STEPS_VISIBLE = "onboarding_steps_visible";
  static const IS_NEW_USER = "is_new_user";
  static const SHOW_THREE_KILL_DIALOG = "show_three_kill_bonus_dialog";
  static const INTRO_VIDEO_SHOWED = "intro_video_showed";
}

class FlagSmithEngine {
  static var ALL_USER_FLAG = <Flag>[];
  static var ALL_USER_TRAITS = <Trait>[];
  static Identity? identity = null;
}

Future<bool> getFlagSmithFlag(
    String flag, Identity identity, FlagsmithClient flagSmithClient) async {
  var flagList = FlagSmithEngine.ALL_USER_FLAG
      .where((element) => element.feature.name == flag)
      .toList();
  var isEnable =
      flagList.isNotEmpty ? (flagList.first.stateValue == "true") : false;

  return isEnable;
}

Future<String?> getFlagSmithFlagString(
    String flag, Identity identity, FlagsmithClient flagSmithClient) async {
  var flagList = FlagSmithEngine.ALL_USER_FLAG
      .where((element) => element.feature.name == flag)
      .toList();

  return flagList.isNotEmpty ? flagList.first.stateValue : null;
}

setFeatureFlag(Identity identity, FlagsmithClient flagSmithClient) async {
  if (FlagSmithEngine.identity == null ||
      identity != FlagSmithEngine.identity ||
      FlagSmithEngine.ALL_USER_FLAG.isEmpty) {

    try {
      FlagSmithEngine.identity = identity;
      final flagSmithAttribute = <String, dynamic>{};
      FlagSmithEngine.ALL_USER_FLAG =
      await flagSmithClient.getFeatureFlags(user: identity);
      FlagSmithEngine.ALL_USER_TRAITS = await flagSmithClient.getTraits(identity);
      FlagSmithEngine.ALL_USER_FLAG.forEach((element) {
        flagSmithAttribute["flagsmith_" + element.key!.toString()] =
            element.stateValue ?? element.enabled.toString();
      });
      AnalyticService.getInstance().pushUserProperties(flagSmithAttribute);
    } catch (e) {
      print(e);
    }

  }

}

Future<String> getFlagValue(
    String flag, Identity identity, FlagsmithClient flagSmithClient) async {
  var flagList = FlagSmithEngine.ALL_USER_FLAG
      .where((element) => element.feature.name == flag)
      .toList();
  return flagList.first.stateValue!;
}

Future<int> getFlagSmithTraitIntValue(
    String trait, Identity identity, FlagsmithClient flagSmithClient) async {
  var traitList = FlagSmithEngine.ALL_USER_TRAITS
      .where((element) => element.key == trait)
      .toList();
  return traitList.isNotEmpty
      ? int.tryParse(traitList.first.value.toString()) ?? 0
      : 0;
}

setFlagSmithTraitBoolValue(String trait, Identity identity, FlagsmithClient flagSmithClient,String value) async {
  try{
    await flagSmithClient.createTrait(value: TraitWithIdentity(identity: identity, key: trait, value: value));
  } on FlagsmithApiException catch(e){
    //Network error
  }
}

Future<bool> getFlagSmithTraitBoolValue(
    String trait, Identity identity, FlagsmithClient flagSmithClient) async {
  var traitList = FlagSmithEngine.ALL_USER_TRAITS
      .where((element) => element.key == trait)
      .toList();
  return traitList.isNotEmpty ? (traitList.first.value == "true") : false;
}
