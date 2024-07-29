import 'package:gamerboard/common/services/location/model/place.dart';

abstract class LocationPageState {}

class ShowEnableLocationServiceDialogState extends LocationPageState {
  ShowEnableLocationServiceDialogState();
}

class ShowGrantPermissionDialogState extends LocationPageState {
  ShowGrantPermissionDialogState();
}

class ShowErrorState extends LocationPageState {
  final String message;

  ShowErrorState(this.message);
}
class ShowLoadingState extends LocationPageState {
  final String message;
  ShowLoadingState(this.message);
}

class NavigateBackState extends LocationPageState {
  dynamic result;
  NavigateBackState(this.result);
}
class ManualLocationState extends LocationPageState {
  bool isLoading = false;
  Place? selectedPlace = null;

  ManualLocationState({this.isLoading = false, this.selectedPlace});
}

class LocationOptionState extends LocationPageState {
  bool isLoading = false;
  Place? selectedPlace = null;

  LocationOptionState({this.isLoading = false, this.selectedPlace});
}

class QueryPlaceState extends LocationPageState {
  List<Place>? suggestedPlaces = [];
  bool isLoading = false;

  QueryPlaceState({this.isLoading = false, this.suggestedPlaces});
}
