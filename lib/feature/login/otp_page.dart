import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_svg/flutter_svg.dart';
import 'package:gamerboard/common/bloc/application/application_bloc.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/login/login_bloc.dart';
import 'package:gamerboard/feature/login/login_states.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';

////Created by saurabh.lahoti on 13/12/21

class OTPPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _OTPState();
}

class _OTPState extends State<OTPPage> {
  late LoginBloc _loginBloc;
  TextEditingController _otpController = TextEditingController();
  Timer? _timer;
  int _start = 60;

  @override
  Widget build(BuildContext context) {
    return appScaffold(
        body: SingleChildScrollView(
            child: Align(
                alignment: Alignment.center,
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      const SizedBox(height: 20.0),
                      SvgPicture.asset("${imageAssets}ic_gb_logo.svg"),
                      const SizedBox(height: 20.0),
                      RegularText(AppStrings.enterOTP, fontSize: 20.0),
                      const SizedBox(height: 5.0),
                      RegularText(
                        "${AppStrings.codeIsSentTo}${_loginBloc.mobile}",
                        fontSize: 12.0,
                        color: AppColor.grayA9A9A9,
                        textAlign: TextAlign.center,
                      ),
                      const SizedBox(height: 20.0),
                      SizedBox(
                          width: 250.0,
                          child: BlocConsumer<LoginBloc, LoginState>(
                              listener: (ctx, state) async {
                                if (state is AuthAction) {
                                  if (state.moveToHome) {
                                    await context
                                        .read<ApplicationBloc>()
                                        .loadAppConfig();
                                    Navigator.of(context)
                                        .pushNamedAndRemoveUntil(
                                            Routes.HOME_PAGE,
                                            (Route<dynamic> route) => false);
                                  }
                                }
                              },
                              listenWhen: (previous, current) =>
                                  current is AuthAction,
                              builder: (ctx, state) {
                                bool verified = false;
                                if (state is LoadOTPPage) {
                                  verified = state.isOTPValid;
                                }
                                return _otpPage(verified);
                              },
                              buildWhen: (previous, current) =>
                                  (current is LoadOTPPage),
                              bloc: _loginBloc)),
                      const SizedBox(height: 16.0),
                      BlocBuilder<LoginBloc, LoginState>(
                          builder: (ctx, state) {
                            if (state is TimerState) {
                              if (state.time > 0)
                                return RegularText(
                                    "${AppStrings.receiveOTP} (wait ${_start}s)");
                              else
                                return InkWell(
                                    onTap: () {
                                      if (_loginBloc.mobile != null) {
                                        _loginBloc.requestOTP(
                                            context, _loginBloc.mobile!,
                                            otpPage: false);
                                        startTimer();
                                      }
                                    },
                                    child: Padding(
                                        padding: EdgeInsets.all(5.0),
                                        child: BoldText(AppStrings.sendAgain,
                                            color: AppColor.textHighlighted)));
                            }
                            return SizedBox.shrink();
                          },
                          bloc: _loginBloc,
                          buildWhen: (previous, current) =>
                              current is TimerState)
                    ]))));
  }

  @override
  void dispose() {
    _otpController.dispose();
    _timer?.cancel();
    super.dispose();
  }

  Widget _otpPage(bool verified) =>
      Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [

        TextFormField(
            onChanged: (newText) {
              _loginBloc.validOTP(newText);
            },
            textInputAction: TextInputAction.done,
            keyboardType: TextInputType.number,
            maxLength: 4,
            inputFormatters: <TextInputFormatter>[
              FilteringTextInputFormatter.digitsOnly
            ],
            controller: _otpController,
            textAlign: TextAlign.center,
            textAlignVertical: TextAlignVertical.center,
            style: TextStyle(color: Colors.white, letterSpacing: 10.0),
            decoration: InputDecoration(
                filled: true,
                hintText: "- - - -",
                counterText: "",
                hintStyle: TextStyle(color: AppColor.grayText9E9E9E),
                border: InputBorder.none,
                fillColor: AppColor.inputBackground)),
        const SizedBox(height: 5.0),

        BlocBuilder<LoginBloc, LoginState>(
            builder: (context, state) {
              if (state is AuthAction) {
                if (state.loading) {
                  return Center(
                      child: SizedBox(
                          width: 20.0,
                          height: 20.0,
                          child: CircularProgressIndicator(strokeWidth: 1.0)));
                } else if (state.error != null) {
                  return RegularText(state.error!,
                      fontSize: 12.0,
                      color: AppColor.errorRed,
                      textAlign: TextAlign.center);
                }
              }
              return const SizedBox(height: 5.0);
            },
            bloc: _loginBloc,
            buildWhen: (previous, current) => current is AuthAction),
        const SizedBox(height: 16.0),
        primaryButton(AppStrings.verify, () {
          if (verified) {
            _loginBloc.verifyOTP(_otpController.text);
            FocusScope.of(context).unfocus();
          }
        }, active: verified)
      ]);

  void startTimer() {
    const oneSec = const Duration(seconds: 1);
    _start = 60;
    _timer = new Timer.periodic(oneSec, (Timer timer) {
      if (_start == 0) {
        timer.cancel();
      }
      _loginBloc.emitTimerState(_start--);
    });
  }

  @override
  void initState() {
    super.initState();
    _loginBloc = context.read<LoginBloc>();
    _loginBloc.loadOTPPage();
    startTimer();
  }
}
