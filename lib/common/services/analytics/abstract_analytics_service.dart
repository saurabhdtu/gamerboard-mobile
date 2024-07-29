abstract class AbstractAnalyticsService{
  void trackEvents(String eventName, {Map<String, dynamic>? properties});
  void pushUserProfile(String userId);
  void pushUserProperties(Map<String, dynamic> properties);
}