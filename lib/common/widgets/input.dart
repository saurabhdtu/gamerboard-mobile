import 'package:flutter/material.dart';
import 'package:gamerboard/common/widgets/containers.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/colors.dart';

////Created by saurabh.lahoti on 20/02/22

class AppDropdown extends StatefulWidget {
  final String defaultText;
  final List<String> items;
  final double? width;
  final double? height;
  final EdgeInsets? padding;
  final Function(int)? onItemSelected;

  AppDropdown(
      {this.defaultText = "Select",
      this.onItemSelected,
      this.items = const [],
      this.width,
      this.height,
      this.padding});

  @override
  State<StatefulWidget> createState() => _DropdownState(defaultText);
}

class _DropdownState extends State<AppDropdown> {
  String defaultText;

  _DropdownState(this.defaultText);

  @override
  Widget build(BuildContext context) => cardContainer(
        DropdownButton(
            hint: RegularText(defaultText, fontSize: 16.0),
            isExpanded: true,
            enableFeedback: true,
            menuMaxHeight: 200.0,
            underline: SizedBox.shrink(),
            icon: const Icon(Icons.keyboard_arrow_down,
                color: Colors.white, size: 20.0),
            items: widget.items
                .map((e) => DropdownMenuItem(
                    value: e,
                    child: Padding(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 10.0, vertical: 12.0),
                        child: RegularText(e, fontSize: 16.0))))
                .toList(),
            onChanged: (val) {
              defaultText = val.toString();
              widget.onItemSelected?.call(widget.items.indexOf(val.toString()));
            }),
        width: widget.width,
        height: widget.height,
        padding: widget.padding,
      );
}

InputDecoration darkTextFieldWithBorderDecoration(
        {String? hintLabel,
        Widget? suffixIcon,
        Widget? suffix,
        Widget? prefix,
        String? error,
        OutlineInputBorder? errorBorder}) =>
    InputDecoration(
        counterText: "",
        hintText: hintLabel,
        suffixIcon: suffixIcon,
        suffix: suffix,
        prefix: prefix,
        hintStyle: TextStyle(color: AppColor.grayText9E9E9E),
        contentPadding:
            const EdgeInsets.symmetric(horizontal: 10.0, vertical: 0),
        enabledBorder: OutlineInputBorder(
            borderSide: BorderSide(width: 1.0, color: AppColor.dividerColor)),
        focusedBorder: OutlineInputBorder(
            borderSide: BorderSide(width: 1.0, color: AppColor.colorAccent)),
        errorBorder: errorBorder ??
            OutlineInputBorder(
                borderSide:
                    BorderSide(width: 1.0, color: AppColor.errorRed)),
        errorStyle: TextStyle(color: AppColor.errorRed),
        errorText: error,
        floatingLabelBehavior: FloatingLabelBehavior.never,
        filled: true,
        fillColor: AppColor.inputBackground);
