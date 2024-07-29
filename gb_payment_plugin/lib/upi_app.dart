import 'dart:typed_data';

class UpiApp{
  String packageName;
  String applicationName;
  int version;
  Uint8List icon;

  UpiApp(this.packageName, this.applicationName, this.version, this.icon);

  static UpiApp fromMap(Map<dynamic, dynamic> map){
    return UpiApp(map["packageName"] as String, map["applicationName"] as String, map["version"] as int, map["icon"] as Uint8List);
  }
}

