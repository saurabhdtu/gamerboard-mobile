import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/colors.dart';

class DialogContainer extends StatelessWidget {
  final double width;
  final double horizontalPadding;
  final Widget child;
  final String? title;
  final String? subtitle;
  final Function()? onClose;

  DialogContainer(
      {required this.width,
      required this.child,
      this.title,
      this.subtitle,
      this.horizontalPadding = 24.0,
      this.onClose});

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Container(
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(boxShadow: [], color: AppColor.dividerColor),
        width: width,
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const SizedBox(
                  width: 24,
                ),
                if (title != null)
                  SemiBoldText(
                    title!,
                  ),
                InkWell(
                    onTap: () {
                      Navigator.of(context).pop();
                      onClose?.call();
                    },
                    child: const Icon(
                      Icons.close,
                      color: AppColor.grayIconCCCCCC,
                      size: 24,
                    ))
              ],
            ),
            const SizedBox(height: 8.0),
            if (subtitle != null)
              Padding(
                padding: const EdgeInsets.only(top: 8.0),
                child: RegularText(
                  subtitle!,
                  fontSize: 12.0,
                  color: AppColor.grayTextB3B3B3,
                  textAlign: TextAlign.center,
                ),
              ),
            const SizedBox(height: 20.0),
            Padding(
                padding:
                    EdgeInsets.symmetric(horizontal: horizontalPadding),
                child: child)
          ],
        ),
      ),
    );
  }
}
