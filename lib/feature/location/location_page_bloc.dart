import 'dart:async';

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/services/analytics/abstract_analytics_service.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/common/services/location/location_provider.dart';
import 'package:gamerboard/common/services/location/model/location.dart';
import 'package:gamerboard/common/services/location/model/place.dart';
import 'package:gamerboard/common/services/location/places_service.dart';
import 'package:gamerboard/common/services/user/user_service.dart';
import 'package:gamerboard/feature/location/location_page_state.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/resources/strings.dart';

class LocationPageBloc extends Cubit<LocationPageState> {
  final AbstractAnalyticsService _analyticsService;
  final UserService _userService;
  final PlacesService _placesService;
  final LocationProvider _locationProvider;
  List<LocationPageState> states = [];
  List<Place> _suggestedPlaces = [];
  Timer? _searchLocationTimer = null;

  LocationPageBloc(
      {required UserService userService,
      required AbstractAnalyticsService analyticsService,
      required PlacesService placesService,
      required LocationProvider locationProvider})
      : _locationProvider = locationProvider,
        _placesService = placesService,
        _userService = userService,
        _analyticsService = analyticsService,
        super(LocationOptionState()) {
    states.add(state);
  }

  push(LocationPageState state) {
    var lastState = states.isNotEmpty ? states.last : null;
    if (lastState.runtimeType == state.runtimeType) {
      states[states.length - 1] = state;
      _safeEmit(states.last);
      return;
    }
    states.add(state);
    _safeEmit(states.last);
  }

  pop() {
    if (states.length > 1) {
      states.removeLast();
      _safeEmit(states.last);
    }
  }

  _safeEmit(LocationPageState state) {
    if (isClosed) return;
    emit(state);
  }

  void navigateBack(dynamic result) {
    _analyticsService.trackEvents(Events.LOCATION_PREFS_SKIPPED);
    _safeEmit(NavigateBackState(result));
  }

  onQueryPlace(String query) {
    if (query.isEmpty) {
      _suggestedPlaces.clear();
    }
    onSelectPlace(null);
    _searchLocationTimer?.cancel();
    _safeEmit(
        QueryPlaceState(isLoading: true, suggestedPlaces: _suggestedPlaces));
    _searchLocationTimer = Timer(
        Duration(seconds: _searchLocationTimer == null ? 0 : 2), () async {
      try {
        _suggestedPlaces = await _placesService.query(query);
        _safeEmit(QueryPlaceState(
            isLoading: false, suggestedPlaces: _suggestedPlaces));
      } catch (e) {}
    });
  }

  void onGetCurrentLocationClicked() async {
    var isServiceAvailable = await _locationProvider.isServiceAvailable();
    push(LocationOptionState(isLoading: true));
    if (!isServiceAvailable) {
      push(LocationOptionState(isLoading: false));
      _safeEmit(ShowEnableLocationServiceDialogState());
      return;
    }
    try {
      Location location = await _locationProvider.getCurrentPosition();
      var place = await _placesService.getAddress(location);

      _submitLocation(place);

      _analyticsService.trackEvents(Events.AUTOMATIC_LOCATION_ENTERED,
          properties: _analyticsPlaceProperties(place));
    } on PermissionDeniedPermanentlyError catch (e) {
      push(LocationOptionState(isLoading: false));
      _safeEmit(ShowGrantPermissionDialogState());
    } on LocationServiceError catch (e) {
      push(LocationOptionState(isLoading: false));
      handleError(e.error);
    } finally {
      push(LocationOptionState(isLoading: false));
    }
    pop();
  }

  _submitLocation(Place place) async {
    var response = await _userService.savePreferences(PreferencesInput(
        location: LocationInput(
            lat: place.location.lat,
            lng: place.location.lng,
            city: place.city,
            region: place.state,
            country: place.country)));
    if (response.hasErrors) {
      handleError(
          response.errors?.first.message ?? AppStrings.someErrorOccurred);
      _analyticsService.trackEvents(Events.LOCATION_SUBMISSION_FAILED,
          properties: {'error': response.errors?.first});
      return;
    }
    _analyticsService.trackEvents(Events.LOCATION_SUBMITTED,
        properties: _analyticsPlaceProperties(place));
    navigateBack(true);
  }

  Map<String, dynamic> _analyticsPlaceProperties(Place place) {
    return {
      'lat': place.location.lat,
      'lng': place.location.lng,
      'city': place.city,
      'region': place.state,
      'country': place.country
    };
  }

  handleError(String e) {
    _safeEmit(ShowErrorState(e));
  }

  onSelectPlace(Place? place) {
    _suggestedPlaces.clear();
    push(ManualLocationState(isLoading: false, selectedPlace: place));
  }

  onConfirmManualLocation(Place? place) async {
    /*pop();
    push(LocationOptionState(isLoading: false, selectedPlace: place));*/
    push(ManualLocationState(isLoading: true, selectedPlace: place));
    if (place != null) {
      await _submitLocation(place);
    }
    if (place != null) {
      _analyticsService.trackEvents(Events.MANUAL_LOCATION_ENTERED,
          properties: _analyticsPlaceProperties(place));
    }
  }
}
