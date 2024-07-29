import 'package:intl/intl.dart';

////Created by saurabh.lahoti on 20/12/21

class TimeUtils {
  static TimeUtils? _instance;

  TimeUtils._();

  static TimeUtils get instance => _instance ??= TimeUtils._();

  final _cardDateTimeFormat = DateFormat('dd MMM yy, hh:mm aa');

  String formatCardDateTime(DateTime dateTime) {
    return _cardDateTimeFormat.format(dateTime);
  }

  String duration(DateTime startTime, DateTime endTime) {
    Duration d = endTime.difference(startTime);
    return getDurationString(d, enforceDHMPattern: true);
  }

  String durationInDays(DateTime startTime, DateTime endTime) {
    Duration d = endTime.difference(startTime);
    return d.inDays != 0 ? "${d.inDays} days" : "${d.inHours} hours";
  }

  String getDurationString(Duration d, {bool enforceDHMPattern = false, bool shortify = false}) {
    String duration = "";
    bool lessThanADay = false;
    if (d.inDays > 0) {
      duration += "${d.inDays}${shortify ? "D" : " days"} ";
    } else if (!enforceDHMPattern) {
      lessThanADay = true;
    }
    if (d.inHours > 0) {
      int hours = d.inHours - d.inDays * 24;
      duration += "${_appendZero(hours)}$hours${lessThanADay ? ":" : (shortify ? "H " : " hrs ")}";
    }
    if (lessThanADay || d.inMinutes > 0) {
      int mins = d.inMinutes - d.inHours * 60;
      duration +=
          "${_appendZero(d.inMinutes)}$mins${lessThanADay ? ":" : (shortify ? "M " : " mins ")}";
    }
    if (lessThanADay || d.inHours < 4) {
      int secs = d.inSeconds - d.inMinutes * 60;
      duration += "${_appendZero(secs)}$secs ${enforceDHMPattern? (shortify ? "S" : " secs"):""}";
    }
    int lastColon = duration.lastIndexOf(" ");
    if (lastColon != -1) duration = duration.replaceRange(lastColon, lastColon + 1, "");
    if (duration.length > 25) {
      duration = duration.replaceAll("days ", "D ");
      duration = duration.replaceAll("hrs ", "H ");
      duration = duration.replaceAll("mins ", "M ");
    }
    return duration;
  }

  String _appendZero(int val) {
    if (val <= 9) return "0";
    return "";
  }
}
