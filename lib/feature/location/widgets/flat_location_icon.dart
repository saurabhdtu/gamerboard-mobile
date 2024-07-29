import 'package:flutter/material.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';

class FlatRoundLocationIcon extends StatelessWidget {
  const FlatRoundLocationIcon({
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: 60.0,
      height: 60.0,
      padding: const EdgeInsets.all(16.0),
      decoration: BoxDecoration(
          color: AppColor.colorGrayBg2,
          borderRadius: BorderRadius.circular(50.0)),
      child: SvgPicture.asset('${imageAssets}ic_location.svg'),
    );
  }
}
