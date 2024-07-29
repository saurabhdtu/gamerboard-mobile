import 'dart:convert';

import 'package:firebase_remote_config/firebase_remote_config.dart';
import 'package:gamerboard/common/constants.dart';

class OnboardingHelp {
  static OnboardingHelp? _instance;

  bool? showHelp;
  Video? video;
  List<Steps>? steps;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['show_help'] = showHelp;
    if (video != null) {
      map['video'] = video?.toJson();
    }
    if (steps != null) {
      map['steps'] = steps?.map((v) => v.toJson()).toList();
    }
    return map;
  }

  static OnboardingHelp get instance => _instance ??=
      OnboardingHelp._fromJson(jsonDecode(FirebaseRemoteConfig.instance
          .getString(RemoteConfigConstants.ONBOARDING_DATA)));

  OnboardingHelp._fromJson(dynamic json) {
    showHelp = json['showHelp'];
    video = json['video'] != null ? Video.fromJson(json['video']) : null;
    if (json['steps'] != null) {
      steps = [];
      json['steps'].forEach((v) {
        steps?.add(Steps.fromJson(v));
      });
    }
  }
}

class Steps {
  Steps({
    this.title,
    this.description,
  });

  Steps.fromJson(dynamic json) {
    title = json['title'];
    description = json['description'];
  }

  String? title;
  String? description;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['title'] = title;
    map['description'] = description;
    return map;
  }
}

class Video {
  Video({
    this.url,
    this.title,
    this.cta,
  });

  Video.fromJson(dynamic json) {
    url = json['url'];
    title = json['title'];
    cta = json['cta'];
  }

  String? url;
  String? title;
  String? cta;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['url'] = url;
    map['title'] = title;
    map['cta'] = cta;
    return map;
  }
}
