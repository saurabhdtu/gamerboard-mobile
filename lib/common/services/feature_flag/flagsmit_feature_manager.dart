import 'package:flagsmith/flagsmith.dart';
import 'package:gamerboard/common/services/feature_flag/feature_manager.dart';
import 'package:gamerboard/utils/flagsmith_utils.dart';

class FlagsmitFeatureManager extends FeatureManager {
  final FlagsmithClient flagsmithClient;

  FlagsmitFeatureManager(this.flagsmithClient);

  @override
  Future<bool> getTrait(String trait, String? userId) {
    var identity = _getUserIdentityByUserId(userId);
    return getFlagSmithTraitBoolValue(trait, identity, flagsmithClient);
  }

  Identity _getUserIdentityByUserId(String? userId) =>
      Identity(identifier: (userId ?? '').toString());

  @override
  Future<bool> isEnabled(String flag, String? userId) {
    var identity = Identity(identifier: userId.toString());
    return getFlagSmithFlag(flag, identity, flagsmithClient);
  }

  @override
  Future<void> setTrait(String trait, String? userId, bool value) async {
    await setFlagSmithTraitBoolValue(trait, _getUserIdentityByUserId(userId),
        flagsmithClient, value ? 'true' : 'false');
    return Future.value();
  }

  @override
  Future<void> fetchAllFlags(String? userId) async {
    await setFeatureFlag(_getUserIdentityByUserId(userId), flagsmithClient);
    return Future.value();
  }
}
