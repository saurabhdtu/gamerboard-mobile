
import 'package:intl/intl.dart';

////Created by saurabh.lahoti on 14/12/21
final dateFormatter = DateFormat('MM-dd-yyyy');
final timeFormatter = DateFormat('HH:mm:ss');

final dateTimeFormatter = DateFormat('yyyy-MM-dd HH:mm:ss');

DateTime fromGraphQLDateToDartDateTime(String date) => DateTime.parse(date).toLocal();

String fromDartDateTimeToGraphQLDate(DateTime date) => dateFormatter.format(date);

DateTime fromGraphQLDateTimeToDartDateTime(String dateTime) => DateTime.parse(dateTime).toLocal();

String fromDartDateTimeToGraphQLDateTime(DateTime dateTime) => dateTimeFormatter.format(dateTime);

DateTime? fromGraphQLDateNullableToDartDateTimeNullable(String? dateTime) =>
    dateTime != null ? DateTime.tryParse(dateTime)?.toLocal() : null;

DateTime? fromGraphQLDateTimeNullableToDartDateTimeNullable(String? dateTime) =>
    dateTime != null ? DateTime.tryParse(dateTime)?.toLocal() : null;

String fromDartDateTimeNullableToGraphQLDateNullable(DateTime? time) =>
    dateFormatter.format(time ?? DateTime.now());

String fromDartDateTimeNullableToGraphQLDateTimeNullable(DateTime? time) =>
    dateTimeFormatter.format(time ?? DateTime.now());

Map<dynamic,dynamic> fromGraphQLJsonToDartMap(Map<dynamic,dynamic>? json) {
  return json!;
}

Map<dynamic, dynamic> fromDartMapToGraphQLJson(Map<dynamic,dynamic>? json) {
  return json!;
}



