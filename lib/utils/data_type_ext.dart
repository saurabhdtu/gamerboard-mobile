import 'package:intl/intl.dart';

////Created by saurabh.lahoti on 17/10/21

extension StringUtils on String {
  String capitalizeFirstCharacter() {
    return this[0].toUpperCase() + this.substring(1, this.length);
  }
}

extension NumUtils on num {
  String formattedNumber() {
    try{
      var format = NumberFormat.currency(
          decimalDigits: this.toDouble() - this.toInt() > 0 ? 2 : 0,
          customPattern: "#,##,##0.00");
      return format.format(this);
    } catch(e){
      return '0.00';
    }
  }

  String getOrdinalNumber() {
    if (this >= 4 && this <= 19 || this.toString().endsWith("0")) {
      return "${this}th";
    } else if (this.toString().endsWith("1")) {
      return "${this}st";
    } else if (this.toString().endsWith("2")) {
      return "${this}nd";
    } else if (this.toString().endsWith("3")) {
      return "${this}rd";
    } else
      return "${this}th";
  }
}
