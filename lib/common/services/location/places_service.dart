import 'package:gamerboard/common/services/location/model/location.dart';

import 'model/place.dart';

abstract class PlacesService {
  Future<List<Place>> query(String text , {List<String>? countries});
  Future<Place> getAddress(Location location);
}


