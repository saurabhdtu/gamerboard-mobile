////Created by saurabh.lahoti on 04/10/21
class BuildConfig {
  String deviceId;
  String deviceModel;
  String deviceBrand;
  String deviceManufacturer;
  String deviceProduct;
  String currentAppVersion;
  String versionName;
  String appPackage;
  String smartlookKey;

  BuildConfig(
      this.deviceId,
      this.deviceModel,
      this.deviceBrand,
      this.deviceManufacturer,
      this.deviceProduct,
      this.currentAppVersion,
      this.versionName,
      this.appPackage,
      this.smartlookKey);

  factory BuildConfig.fromMap(dynamic map) {
    return BuildConfig(
        map['deviceId'].toString(),
        map['deviceModel'].toString(),
        map['deviceBrand'].toString(),
        map['deviceManufacturer'].toString(),
        map['deviceProduct'].toString(),
        map['appVersion'].toString(),
        map['versionName'].toString(),
        map['appPackage'].toString(),
        map['smartlookKey'].toString());
  }
}

class AppUpdate {
  int latestVersion;
  String latestVersionCode;
  String downloadUrl;
  int forceUpdateVersion;
  String title;
  int rolloutPercentage;
  String description;
  String forceTitle;
  String forceDescription;
  List<String> whatsNew;

  static const int NO_UPDATE = 0;
  static const int FORCE_UPDATE = 1;
  static const int SOFT_UPDATE = 2;

  AppUpdate(
      this.latestVersion,
      this.latestVersionCode,
      this.downloadUrl,
      this.forceUpdateVersion,
      this.title,
      this.rolloutPercentage,
      this.description,
      this.forceTitle,
      this.forceDescription,
      this.whatsNew);

  factory AppUpdate.fromMap(dynamic map) {
    return AppUpdate(
        map['latestVersion'] as int,
        map['latestVersionCode'].toString(),
        map['downloadUrl'].toString(),
        map['forceUpdateVersion'] as int,
        map['title'].toString(),
        map['rolloutPercentage'] as int,
        map['description'].toString(),
        map['forceTitle'].toString(),
        map['forceDescription'].toString(),
        (map['whatsNew'] as List).map((e) => e.toString()).toList());
  }
}
