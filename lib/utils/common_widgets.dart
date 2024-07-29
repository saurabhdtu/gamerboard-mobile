import 'package:flutter/material.dart';

import '../resources/colors.dart';

Widget gbRadioButton(dynamic groupValue,dynamic buttonValue,Function onSelection){
  return SizedBox(
    height: 12,
    width: 12,
    child: Theme(
      data: ThemeData(
          unselectedWidgetColor: Color(0xff7D7D7D)),
      child: Radio(
        value: buttonValue,
        activeColor: AppColor.buttonActive,
        groupValue: groupValue,
        onChanged: (dynamic? value) {
          onSelection(value);
        },
      ),
    ),
  );
}
Widget gbCheckBoxButton(bool isChecked,Function onSelection){
  return   SizedBox(
    height: 12,
    width: 12,
    child: Theme(
      data: ThemeData(
          unselectedWidgetColor: Color(0xff7D7D7D)),
      child: Checkbox(
        value: isChecked,
        checkColor:Color(0xff1B1B1C),
        activeColor: AppColor.buttonActive,
        onChanged: (value){
          onSelection(value);
        },

      ),
    ),
  );
}
