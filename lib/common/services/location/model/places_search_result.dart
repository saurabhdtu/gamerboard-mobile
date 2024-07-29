import 'dart:convert';

PlacesSearchResult placesSearchResultFromJson(String str) =>
    PlacesSearchResult.fromJson(json.decode(str));

String placesSearchResultToJson(PlacesSearchResult data) =>
    json.encode(data.toJson());

class PlacesSearchResult {
  PlacesSearchResult({
    List<Places>? places,
  }) {
    _places = places;
  }

  PlacesSearchResult.fromJson(dynamic json) {
    if (json['places'] != null) {
      _places = [];
      json['places'].forEach((v) {
        _places?.add(Places.fromJson(v));
      });
    }
  }

  List<Places>? _places;

  List<Places>? get places => _places;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    if (_places != null) {
      map['places'] = _places?.map((v) => v.toJson()).toList();
    }
    return map;
  }
}

Places placesFromJson(String str) => Places.fromJson(json.decode(str));

String placesToJson(Places data) => json.encode(data.toJson());

class Places {
  Places({
    String? formattedAddress,
    List<AddressComponentsV2>? addressComponents,
    PlaceLocation? location,
    DisplayName? displayName,
  }) {
    _formattedAddress = formattedAddress;
    _addressComponents = addressComponents;
    _location = location;
    _displayName = displayName;
  }

  Places.fromJson(dynamic json) {
    _formattedAddress = json['formattedAddress'];
    if (json['addressComponents'] != null) {
      _addressComponents = [];
      json['addressComponents'].forEach((v) {
        _addressComponents?.add(AddressComponentsV2.fromJson(v));
      });
    }
    _location =
        json['location'] != null ? PlaceLocation.fromJson(json['location']) : null;
    _displayName = json['displayName'] != null
        ? DisplayName.fromJson(json['displayName'])
        : null;
  }

  String? _formattedAddress;
  List<AddressComponentsV2>? _addressComponents;
  PlaceLocation? _location;
  DisplayName? _displayName;

  String? get formattedAddress => _formattedAddress;

  List<AddressComponentsV2>? get addressComponents => _addressComponents;

  PlaceLocation? get location => _location;

  DisplayName? get displayName => _displayName;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['formattedAddress'] = _formattedAddress;
    if (_addressComponents != null) {
      map['addressComponents'] =
          _addressComponents?.map((v) => v.toJson()).toList();
    }
    if (_location != null) {
      map['location'] = _location?.toJson();
    }
    if (_displayName != null) {
      map['displayName'] = _displayName?.toJson();
    }
    return map;
  }

  AddressComponentsV2? getComponent( String key) {
    try {
      return addressComponents
          ?.firstWhere((element) => element.types?.contains(key) == true);
    } catch (e) {}
    return null;
  }
}

DisplayName displayNameFromJson(String str) =>
    DisplayName.fromJson(json.decode(str));

String displayNameToJson(DisplayName data) => json.encode(data.toJson());

class DisplayName {
  DisplayName({
    String? text,
    String? languageCode,
  }) {
    _text = text;
    _languageCode = languageCode;
  }

  DisplayName.fromJson(dynamic json) {
    _text = json['text'];
    _languageCode = json['languageCode'];
  }

  String? _text;
  String? _languageCode;

  String? get text => _text;

  String? get languageCode => _languageCode;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['text'] = _text;
    map['languageCode'] = _languageCode;
    return map;
  }
}

PlaceLocation locationFromJson(String str) => PlaceLocation.fromJson(json.decode(str));

String locationToJson(PlaceLocation data) => json.encode(data.toJson());

class PlaceLocation {
  PlaceLocation({
    double? latitude,
    double? longitude,
  }) {
    _latitude = latitude;
    _longitude = longitude;
  }

  PlaceLocation.fromJson(dynamic json) {
    _latitude = json['latitude'];
    _longitude = json['longitude'];
  }

  double? _latitude;
  double? _longitude;

  double? get latitude => _latitude;

  double? get longitude => _longitude;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['latitude'] = _latitude;
    map['longitude'] = _longitude;
    return map;
  }
}

AddressComponentsV2 addressComponentsFromJson(String str) =>
    AddressComponentsV2.fromJson(json.decode(str));

String addressComponentsToJson(AddressComponentsV2 data) =>
    json.encode(data.toJson());

class AddressComponentsV2 {
  AddressComponentsV2({
    String? longText,
    String? shortText,
    List<String>? types,
    String? languageCode,
  }) {
    _longText = longText;
    _shortText = shortText;
    _types = types;
    _languageCode = languageCode;
  }

  AddressComponentsV2.fromJson(dynamic json) {
    _longText = json['longText'];
    _shortText = json['shortText'];
    _types = json['types'] != null ? json['types'].cast<String>() : [];
    _languageCode = json['languageCode'];
  }

  String? _longText;
  String? _shortText;
  List<String>? _types;
  String? _languageCode;

  String? get longText => _longText;

  String? get shortText => _shortText;

  List<String>? get types => _types;

  String? get languageCode => _languageCode;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['longText'] = _longText;
    map['shortText'] = _shortText;
    map['types'] = _types;
    map['languageCode'] = _languageCode;
    return map;
  }


}
