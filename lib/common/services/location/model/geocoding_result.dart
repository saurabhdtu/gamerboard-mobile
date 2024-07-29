import 'dart:convert';

import 'package:gamerboard/common/services/location/model/places_search_result.dart';
GeocodingResult geocodingResultFromJson(String str) => GeocodingResult.fromJson(json.decode(str));
String geocodingResultToJson(GeocodingResult data) => json.encode(data.toJson());
class GeocodingResult {
  GeocodingResult({
      PlusCode? plusCode, 
      List<Results>? results, 
      String? status,}){
    _plusCode = plusCode;
    _results = results;
    _status = status;
}

  GeocodingResult.fromJson(dynamic json) {
    _plusCode = json['plus_code'] != null ? PlusCode.fromJson(json['plus_code']) : null;
    if (json['results'] != null) {
      _results = [];
      json['results'].forEach((v) {
        _results?.add(Results.fromJson(v));
      });
    }
    _status = json['status'];
  }
  PlusCode? _plusCode;
  List<Results>? _results;
  String? _status;

  PlusCode? get plusCode => _plusCode;
  List<Results>? get results => _results;
  String? get status => _status;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    if (_plusCode != null) {
      map['plus_code'] = _plusCode?.toJson();
    }
    if (_results != null) {
      map['results'] = _results?.map((v) => v.toJson()).toList();
    }
    map['status'] = _status;
    return map;
  }

}

Results resultsFromJson(String str) => Results.fromJson(json.decode(str));
String resultsToJson(Results data) => json.encode(data.toJson());
class Results {
  Results({
      List<AddressComponent>? addressComponents,
      String? formattedAddress, 
      Geometry? geometry, 
      String? placeId, 
      PlusCode? plusCode, 
      List<String>? types,}){
    _addressComponents = addressComponents;
    _formattedAddress = formattedAddress;
    _geometry = geometry;
    _placeId = placeId;
    _plusCode = plusCode;
    _types = types;
}

  Results.fromJson(dynamic json) {
    if (json['address_components'] != null) {
      _addressComponents = [];
      json['address_components'].forEach((v) {
        _addressComponents?.add(AddressComponent.fromJson(v));
      });
    }
    _formattedAddress = json['formatted_address'];
    _geometry = json['geometry'] != null ? Geometry.fromJson(json['geometry']) : null;
    _placeId = json['place_id'];
    _plusCode = json['plus_code'] != null ? PlusCode.fromJson(json['plus_code']) : null;
    _types = json['types'] != null ? json['types'].cast<String>() : [];
  }
  List<AddressComponent>? _addressComponents;
  String? _formattedAddress;
  Geometry? _geometry;
  String? _placeId;
  PlusCode? _plusCode;
  List<String>? _types;

  List<AddressComponent>? get addressComponents => _addressComponents;
  String? get formattedAddress => _formattedAddress;
  Geometry? get geometry => _geometry;
  String? get placeId => _placeId;
  PlusCode? get plusCode => _plusCode;
  List<String>? get types => _types;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    if (_addressComponents != null) {
      map['address_components'] = _addressComponents?.map((v) => v.toJson()).toList();
    }
    map['formatted_address'] = _formattedAddress;
    if (_geometry != null) {
      map['geometry'] = _geometry?.toJson();
    }
    map['place_id'] = _placeId;
    if (_plusCode != null) {
      map['plus_code'] = _plusCode?.toJson();
    }
    map['types'] = _types;
    return map;
  }

  AddressComponent? getComponent(String key) {
    try {
      return addressComponents
          ?.firstWhere((element) => element.types?.contains(key) == true);
    } catch (e) {}
    return null;
  }
}

PlusCode plusCodeFromJson(String str) => PlusCode.fromJson(json.decode(str));
String plusCodeToJson(PlusCode data) => json.encode(data.toJson());
class PlusCode {
  PlusCode({
      String? compoundCode, 
      String? globalCode,}){
    _compoundCode = compoundCode;
    _globalCode = globalCode;
}

  PlusCode.fromJson(dynamic json) {
    _compoundCode = json['compound_code'];
    _globalCode = json['global_code'];
  }
  String? _compoundCode;
  String? _globalCode;

  String? get compoundCode => _compoundCode;
  String? get globalCode => _globalCode;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['compound_code'] = _compoundCode;
    map['global_code'] = _globalCode;
    return map;
  }

}

Geometry geometryFromJson(String str) => Geometry.fromJson(json.decode(str));
String geometryToJson(Geometry data) => json.encode(data.toJson());
class Geometry {
  Geometry({
      PlaceLocation? location,
      String? locationType, 
      Viewport? viewport,}){
    _location = location;
    _locationType = locationType;
    _viewport = viewport;
}

  Geometry.fromJson(dynamic json) {
    _location = (json['location'] != null ? PlaceLocation.fromJson(json['location']) : null);
    _locationType = json['location_type'];
    _viewport = json['viewport'] != null ? Viewport.fromJson(json['viewport']) : null;
  }
  PlaceLocation? _location;
  String? _locationType;
  Viewport? _viewport;

  PlaceLocation? get location => _location;
  String? get locationType => _locationType;
  Viewport? get viewport => _viewport;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    if (_location != null) {
      map['location'] = _location?.toJson();
    }
    map['location_type'] = _locationType;
    if (_viewport != null) {
      map['viewport'] = _viewport?.toJson();
    }
    return map;
  }

}

Viewport viewportFromJson(String str) => Viewport.fromJson(json.decode(str));
String viewportToJson(Viewport data) => json.encode(data.toJson());
class Viewport {
  Viewport({
      Northeast? northeast, 
      Southwest? southwest,}){
    _northeast = northeast;
    _southwest = southwest;
}

  Viewport.fromJson(dynamic json) {
    _northeast = json['northeast'] != null ? Northeast.fromJson(json['northeast']) : null;
    _southwest = json['southwest'] != null ? Southwest.fromJson(json['southwest']) : null;
  }
  Northeast? _northeast;
  Southwest? _southwest;

  Northeast? get northeast => _northeast;
  Southwest? get southwest => _southwest;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    if (_northeast != null) {
      map['northeast'] = _northeast?.toJson();
    }
    if (_southwest != null) {
      map['southwest'] = _southwest?.toJson();
    }
    return map;
  }

}

Southwest southwestFromJson(String str) => Southwest.fromJson(json.decode(str));
String southwestToJson(Southwest data) => json.encode(data.toJson());
class Southwest {
  Southwest({
      double? lat, 
      double? lng,}){
    _lat = lat;
    _lng = lng;
}

  Southwest.fromJson(dynamic json) {
    _lat = json['lat'];
    _lng = json['lng'];
  }
  double? _lat;
  double? _lng;

  double? get lat => _lat;
  double? get lng => _lng;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['lat'] = _lat;
    map['lng'] = _lng;
    return map;
  }

}

Northeast northeastFromJson(String str) => Northeast.fromJson(json.decode(str));
String northeastToJson(Northeast data) => json.encode(data.toJson());
class Northeast {
  Northeast({
      double? lat, 
      double? lng,}){
    _lat = lat;
    _lng = lng;
}

  Northeast.fromJson(dynamic json) {
    _lat = json['lat'];
    _lng = json['lng'];
  }
  double? _lat;
  double? _lng;

  double? get lat => _lat;
  double? get lng => _lng;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['lat'] = _lat;
    map['lng'] = _lng;
    return map;
  }

}


AddressComponent addressComponentFromJson(String str) => AddressComponent.fromJson(json.decode(str));
String addressComponentToJson(AddressComponent data) => json.encode(data.toJson());
class AddressComponent {
  AddressComponent({
    String? longName,
    String? shortName,
    List<String>? types,}){
    _longName = longName;
    _shortName = shortName;
    _types = types;
  }

  AddressComponent.fromJson(dynamic json) {
    _longName = json['long_name'];
    _shortName = json['short_name'];
    _types = json['types'] != null ? json['types'].cast<String>() : [];
  }
  String? _longName;
  String? _shortName;
  List<String>? _types;

  String? get longName => _longName;
  String? get shortName => _shortName;
  List<String>? get types => _types;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['long_name'] = _longName;
    map['short_name'] = _shortName;
    map['types'] = _types;
    return map;
  }

}
