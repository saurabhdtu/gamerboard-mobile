import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../common/bloc/application/application_bloc.dart';
import '../../common/router.dart';
import '../../resources/colors.dart';
import '../../common/services/analytics/analytic_utils.dart';

class OnboardingGuide extends StatefulWidget {
  int step;

  OnboardingGuide({super.key, this.step = 1});

  @override
  State<OnboardingGuide> createState() => _OnboardingGuideState();
}

class _OnboardingGuideState extends State<OnboardingGuide> {
  var applicationBloc;
  Timer? timer;

  @override
  void initState() {
    super.initState();
    applicationBloc = context.read<ApplicationBloc>();
    AnalyticService.getInstance().pushUserProperties({
      "flagsmith_enable_onboarding_steps":true
    });
    startTime();
  }

  startTime() {
    timer = Timer.periodic(Duration(seconds: 3), (Timer t) {
      if (widget.step < 3) {
        setState(() {
          widget.step = widget.step + 1;
        });
      } else {
        timer!.cancel();
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColor.black1E1E1E,
      body: _getWalkthroughStep(),
    );
  }

  Widget _getWalkthroughStep() => SafeArea(
      bottom: false,
      child: Stack(
        children: [
          Center(
            child: _stepImage(),
          ),
          widget.step > 1
              ? Align(
                  alignment: Alignment.centerLeft,
                  child: IconButton(
                    onPressed: () {
                      timer!.cancel();
                      startTime();
                      setState(() {
                        widget.step = widget.step - 1;
                      });
                    },
                    icon: const Icon(
                      Icons.chevron_left,
                      color: AppColor.buttonActive,
                      size: 50,
                    ),
                  ),
                )
              : const SizedBox(),
          Align(
            alignment:
                widget.step < 3 ? Alignment.centerRight : Alignment.bottomRight,
            child: widget.step < 3
                ? Padding(
                    padding: EdgeInsets.only(right: 12),
                    child: IconButton(
                      onPressed: () {
                        setState(() {
                          timer!.cancel();
                          startTime();
                          widget.step = widget.step + 1;
                        });
                      },
                      icon: const Icon(
                        Icons.chevron_right,
                        color: AppColor.buttonActive,
                        size: 50,
                      ),
                    ),
                  )
                : SafeArea(
                    child: Padding(
                      padding: EdgeInsets.all(16.0),
                      child: ElevatedButton(
                        onPressed: () => {
                          Navigator.of(context).popAndPushNamed(Routes.SPLASH)
                        },
                        style: ButtonStyle(
                            backgroundColor: MaterialStateProperty.all(
                                AppColor.buttonActive)),
                        child: const Text("LET'S PLAY"),
                      ),
                    ),
                  ),
          ),
        ],
      ));

  Image _stepImage() {
    var stepImage =
        context.read<ApplicationBloc>().onboardingStep["step${widget.step}"];
    if (stepImage is File)
      return Image.file(
        context.read<ApplicationBloc>().onboardingStep["step${widget.step}"],
        width: MediaQuery.of(context).size.width,
        height: MediaQuery.of(context).size.height,
      );
    return Image.network(
      stepImage,
      width: MediaQuery.of(context).size.width,
      height: MediaQuery.of(context).size.height,
    );
  }
}
