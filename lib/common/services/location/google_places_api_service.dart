import 'dart:convert';
import 'dart:io';
import 'dart:math';

import 'package:dio/dio.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/services/location/model/geocoding_result.dart';
import 'package:gamerboard/common/services/location/model/location.dart';
import 'package:gamerboard/common/services/location/model/places_search_result.dart';
import 'package:gamerboard/common/services/location/places_service.dart';
import 'package:path_provider/path_provider.dart';

import 'model/place.dart';

class GooglePlacesApiService extends PlacesService {

  static const _API_PLACES_URL =
      'https://places.googleapis.com/v1/places:searchText';
  static const _API_GEOCOING_URL =
      'https://maps.googleapis.com/maps/api/geocode/json';

  GooglePlacesApiService();

  final _dio = Dio();

  // var _key = 'AIzaSyBHka40HHCR0-bBdJYIX1zMiZiLuHch5i8';
  final String _fieldMask =
      'places.displayName,places.formattedAddress,places.addressComponents,places.priceLevel,places.location';

  @override
  Future<List<Place>> query(String text, {List<String>? countries}) async {
    var response = await _postQuery(text);
    var searchPlacesResult = PlacesSearchResult.fromJson(response);
    var places = searchPlacesResult.places?.map((e) {
      var city = e.getComponent('locality');
      var country = e.getComponent( 'country');
      var state =
      e.getComponent('administrative_area_level_1');
      return Place(
          address: e.formattedAddress,
          city: city?.longText ?? '',
          state: state?.longText ?? '',
          location:
              Location(e.location?.latitude ?? 0, e.location?.longitude ?? 0),
          country: country?.longText ?? '');
    }).toList();
    return places ?? [];
  }


  Future<dynamic> _postQuery(String text) async {
    try {
      var googleApiKey = await _getMapApiKey();

      var response = await _dio.post(
        _API_PLACES_URL,
        data: {
          'textQuery': text,
        },
        options: Options(
          headers: {
            'Content-Type': 'application/json',
            'X-Goog-Api-Key': googleApiKey,
            'X-Goog-FieldMask': _fieldMask
          },
        ),
      );
      // Check if the response is successful (status code 200)
      if (response.statusCode == 200) {
        return response.data;
      }
    } catch (e) {
      // Handle DioError, which includes both DioError and DioErrorType
      _logError(e);
    }
    throw Exception('Couldn\'t query your request');
  }

  Future<dynamic> _getMapApiKey() async {
    var map = await Constants.PLATFORM_CHANNEL.invokeMethod("app_config");
    var googleApiKey =  map['MAP_API_KEY'];
    return googleApiKey;
  }

  void _logError(Object e) {
    // Handle DioError, which includes both DioError and DioErrorType
    if (e is DioException) {
      print('DioError: ${e.message}');
      if (e.response != null) {
        // Handle response-related errors
        print('Response data: ${e.response!.data}');
      }
    } else {
      // Handle other errors
      print('Error: $e');
    }
  }

  @override
  Future<Place> getAddress(Location location) async {
    var data = await _getGeocodingData(location);
    var geocodingResult = GeocodingResult.fromJson(data);
    final result = geocodingResult.results?.first;
    var city = result?.getComponent( 'locality');
    var country = result?.getComponent( 'country');
    var state = result?.getComponent( 'administrative_area_level_1');
    return Place(
        city: city?.longName,
        state: state?.longName,
        location: location,
        country: country?.longName);
  }

  Future<dynamic> _getGeocodingData(Location location) async {
    try {
      var googleApiKey = await _getMapApiKey();
      var response = await _dio.get(
        _API_GEOCOING_URL,
        queryParameters: {
          'latlng': '${location.lat},${location.lng}',
          'key': googleApiKey,
        },
      );
      // Check if the request was successful (status code 200)
      if (response.statusCode == 200) {
        return response.data;
      }
    } catch (e) {
      // Handle exception
      _logError(e);
    }
  }
}

