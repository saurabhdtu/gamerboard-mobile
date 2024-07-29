import 'package:gamerboard/common/services/location/model/location.dart';
import 'package:geolocator/geolocator.dart';

abstract class LocationProvider {
  Future<Location> getCurrentPosition();

  Future<bool> isServiceAvailable();
}

class LocationServiceError extends Error {
  final String error;

  LocationServiceError(this.error);
}

class PermissionDeniedError extends LocationServiceError {
  final String error;

  PermissionDeniedError(this.error) : super(error);
}

class PermissionDeniedPermanentlyError extends LocationServiceError {
  final String error;

  PermissionDeniedPermanentlyError(this.error) : super(error);
}

class LocationServiceDisabledError extends LocationServiceError {
  final String error;

  LocationServiceDisabledError(this.error) : super(error);
}

class DeviceLocationProvider extends LocationProvider {
  @override
  Future<bool> isServiceAvailable() {
    return Geolocator.isLocationServiceEnabled();
  }

  @override
  Future<Location> getCurrentPosition() async {
    var currentPosition = await _determinePosition();
    return Location(currentPosition.latitude, currentPosition.longitude);
  }

  /// Determine the current position of the device.
  ///
  /// When the location services are not enabled or permissions
  /// are denied the `Future` will return an error.
  Future<Position> _determinePosition() async {
    bool serviceEnabled;
    LocationPermission permission;

    // Test if location services are enabled.
    serviceEnabled = await isServiceAvailable();
    if (!serviceEnabled) {
      // Location services are not enabled don't continue
      // accessing the position and request users of the
      // App to enable the location services.
      return Future.error(
          LocationServiceDisabledError('Location services are disabled.'));
    }

    permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        // Permissions are denied, next time you could try
        // requesting permissions again (this is also where
        // Android's shouldShowRequestPermissionRationale
        // returned true. According to Android guidelines
        // your App should show an explanatory UI now.
        return Future.error(
            PermissionDeniedError('Location permissions are denied'));
      }
    }

    if (permission == LocationPermission.deniedForever) {
      // Permissions are denied forever, handle appropriately.
      return Future.error(PermissionDeniedPermanentlyError(
          'Location permissions are permanently denied, we cannot request permissions.'));
    }

    // When we reach here, permissions are granted and we can
    // continue accessing the position of the device.
    return await Geolocator.getCurrentPosition();
  }
}
