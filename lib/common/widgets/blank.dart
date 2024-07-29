import 'package:flutter/cupertino.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:lottie/lottie.dart';

////Created by saurabh.lahoti on 20/02/22
Widget emptyState(String message, {String? lottieJson}) => Padding(
      padding: const EdgeInsets.all(20.0),
      child: Center(
          child: Column(children: [
        if (lottieJson != null)
          Padding(
              padding: const EdgeInsets.only(bottom: 20.0),
              child: LottieBuilder.asset("$lottieAssets$lottieJson",
                  height: 100.0, width: 100.0, repeat: true)),
        BoldText(message,
            fontSize: 22.0,
            color: AppColor.textDarkGray,
            textAlign: TextAlign.center)
      ])),
    );
