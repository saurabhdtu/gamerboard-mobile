import '../resources/strings.dart';

enum RewardType { ONBOARDING_REWARD, ONE_GAME_SUBMISSION, THREE_KILLS }

extension rewardExt on RewardType{
  String getDialogTitle(){
    switch (this) {
      case RewardType.ONBOARDING_REWARD:
        return AppStrings.accountCreationCompleted;
      case RewardType.ONE_GAME_SUBMISSION:
        return AppStrings.oneGameSubmissionReward;
      case RewardType.THREE_KILLS:
        return AppStrings.threeKillReward;
    }
   }

  double rewardAmount(){
    switch (this) {
      case RewardType.ONBOARDING_REWARD:
        return 20.0;
      case RewardType.ONE_GAME_SUBMISSION:
        return 10.0;
      case RewardType.THREE_KILLS:
        return 10.0;
    }
  }


  String rewardSource(){
    switch (this) {
      case RewardType.ONBOARDING_REWARD:
        return "ONBOARDING_REWARD";
      case RewardType.ONE_GAME_SUBMISSION:
        return "ONE_GAME_SUBMISSION";
      case RewardType.THREE_KILLS:
        return "THREE_KILLS";
    }
  }
}