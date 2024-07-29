abstract class FeatureManager {
  Future<bool> isEnabled(String flag, String? userId);

  Future<void> setTrait(String flag, String? userId, bool value);

  Future<bool> getTrait(String flag, String? userId);

  Future<void> fetchAllFlags(String? userId);
}
