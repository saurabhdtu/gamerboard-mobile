import 'package:gamerboard/common/services/location/model/location.dart';

class Place {
  String? address;
  String? city;
  String? state;
  String? country;
  Location location;

  Place(
      {this.address,
      required this.city,
      required this.state,
      required this.location,
      required this.country});

  @override
  String toString() {
    return '${address ?? ''}, $city, $state, ${country}';
  }

  String getFormattedAddress() => address ?? '';
}
