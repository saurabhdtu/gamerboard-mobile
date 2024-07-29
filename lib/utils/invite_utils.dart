import 'package:flagsmith/flagsmith.dart';
import 'package:flutter/cupertino.dart';
import 'package:gamerboard/feature/home/home_bloc.dart';
import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/utils/reward_utils.dart';
import 'package:gamerboard/utils/share_utils.dart';
import 'package:gamerboard/utils/shared_preferences.dart';

import 'flagsmith_utils.dart';

class InviteUtils {
  static  Future<InviteDialogEvent?> checkInviteDialogStatus(
      FlagsmithClient flagsmithClient, {UserMixin? user, int noGamesPlayed = 0}
      ) async {
    if(user == null) return  null;

    var previousGame =  noGamesPlayed;
    var isRewardDialogAlreadyShowed = await getFlagSmithTraitBoolValue(
        TraitKeys.SHOW_FIRST_GAME_DIALOG,
        Identity(identifier: user.id.toString()),
        flagsmithClient);
    var isStepperVisible = await getFlagSmithTraitBoolValue(
        TraitKeys.ONBOARDING_STEPS_VISIBLE,
        Identity(identifier: user.id.toString()),
        flagsmithClient);

    if (previousGame > 0 && !isStepperVisible) {
      setFlagSmithTraitBoolValue(
          TraitKeys.ONBOARDING_STEPS_VISIBLE,
          Identity(identifier: user.id.toString()),
          flagsmithClient, "true");
    }

    // if (previousGame > 0) {
    //   if (!isRewardDialogAlreadyShowed) {
    //     var isAlreadyShow =  await SharedPreferenceHelper.getInstance.getBoolPref(PrefKeys.REWARD_DIALOG_SHOW);
    //     if(!(isAlreadyShow ?? false))
    //     {
    //       setFlagSmithTraitBoolValue(
    //           TraitKeys.SHOW_FIRST_GAME_DIALOG,
    //           Identity(identifier: homeBloc.user!.id.toString()),
    //           homeBloc.applicationBloc.flagsmithClient,"true");
    //       SharedPreferenceHelper.getInstance.setBoolPref(PrefKeys.REWARD_DIALOG_SHOW, true);
    //       // homeBloc.showRewardDialog(RewardType.ONE_GAME_SUBMISSION);
    //     }
    //   }
    // }

    var userWalletBalance = user.wallet.bonus;
    var numberOfTimesDialogShowOnWalletBalance = await SharedPreferenceHelper
        .getInstance
        .getIntPref(PrefKeys.INVITE_DIALOG_ON_WALLET_BALANCE_COUNT) ??
        0;
    var numberOfTimesDialogShowOnGameSubmission = await SharedPreferenceHelper
        .getInstance
        .getIntPref(PrefKeys.INVITE_DIALOG_ON_GAME_SUBMISSION_COUNT) ??
        0;

    if (numberOfTimesDialogShowOnGameSubmission < 2 && previousGame >= 1)
    {
      SharedPreferenceHelper.getInstance.setIntPref(
          PrefKeys.INVITE_DIALOG_ON_GAME_SUBMISSION_COUNT,
          numberOfTimesDialogShowOnGameSubmission + 1);
      return InviteDialogEvent.ON_MORE_THAN_ONE_GAME_SUBMIT;
    }
    else if (numberOfTimesDialogShowOnWalletBalance < 2 &&
        userWalletBalance == 0) {
      SharedPreferenceHelper.getInstance.setIntPref(
          PrefKeys.INVITE_DIALOG_ON_WALLET_BALANCE_COUNT,
          numberOfTimesDialogShowOnWalletBalance + 1);
      return InviteDialogEvent.ON_WALLET_BALANCE_ZERO;
    }
    return null;
  }
}