enum OnboardingVideoEvent {
  ON_USER_CLICK,
  ON_USER_REGISTRATION,
  ON_TIER_SUBMISSION
}

extension OnboardingVideoEventUtils on OnboardingVideoEvent {
  String getOnboardingVideoEventSource() {
    switch (this) {
      case OnboardingVideoEvent.ON_USER_CLICK:
        return "on_user_click";
      case OnboardingVideoEvent.ON_USER_REGISTRATION:
        return "on_user_register";
      case OnboardingVideoEvent.ON_TIER_SUBMISSION:
        return "on_tier_submitted";
    }
  }
}

OnboardingVideoEvent getOnboardingVideoEventFromKey(String key){
  switch (key) {
    case "on_user_click":
      return OnboardingVideoEvent.ON_USER_CLICK;
    case "on_user_register":
      return OnboardingVideoEvent.ON_USER_REGISTRATION;
    case "on_tier_submitted":
      return OnboardingVideoEvent.ON_TIER_SUBMISSION;
    default:
      return OnboardingVideoEvent.ON_USER_CLICK;
  }
}
