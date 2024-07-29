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
import 'package:gamerboard/feature/login/login_widgets.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/ui_utils.dart';

////Created by saurabh.lahoti on 13/12/21

class LoginPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _LoginInState();
}

class _LoginInState extends State<LoginPage> {
  late LoginBloc _loginBloc;
  TextEditingController _mobController = TextEditingController();
  bool isMobileVerified = false;

  LoginState? _currentState;

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onBackPressed,
      child: appScaffold(
          body: SingleChildScrollView(
              child: Align(
                  alignment: Alignment.center,
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      children: [
                        const SizedBox(height: 20.0),
                        SvgPicture.asset("${imageAssets}ic_gb_logo.svg"),
                        const SizedBox(height: 20.0),
                        RegularText(AppStrings.signIn, fontSize: 20.0),
                        const SizedBox(height: 20.0),
                        SizedBox(
                            width: 250.0,
                            child: BlocConsumer<LoginBloc, LoginState>(
                                listener: (ctx, state) async {
                                  if (state is AuthAction && !(_currentState is OtpLoginPage)) {
                                    if (state.error != null) {
                                      UiUtils.getInstance
                                          .showToast(state.error!);
                                    }

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
                                builder: (ctx, state) {
                                  _currentState = state;
                                  if (state is OtpLoginPage) {
                                    return _mobileNumberPage(state);
                                  }
                                  if (state is LoginOptionState) {
                                    return LoginOption(
                                      otpLessButtonText:
                                          AppStrings.loginUsingWhatsapp,
                                      otpButtonText: AppStrings.loginUsingOtp,
                                      onOtpLessOptionClicked: () {
                                        AnalyticService.getInstance().trackEvents(Events.OTPLESS_LOGIN_CLICKED);
                                        _loginBloc.startOtpLessAuthentication(
                                            context,
                                            (mobile, token) => _loginBloc
                                                .verifyOTPLess(context, token));
                                      },
                                      onOtpOptionClicked:
                                          _loginBloc.showOtpLoginOption,
                                      additionalActions: _signupAction(),
                                    );
                                  }
                                  return SizedBox.shrink();
                                },
                                buildWhen: (previous, current) =>
                                    current is LoginOptionState ||
                                    current is OtpLoginPage,
                                bloc: _loginBloc))
                      ])))),
    );
  }

  @override
  void dispose() {
    _mobController.dispose();
    super.dispose();
  }

  List<Widget> _signupAction() => [
        RegularText(AppStrings.dontHaveAccount),
        const SizedBox(height: 8.0),
        InkWell(
            onTap: () {
              AnalyticService.getInstance().trackEvents(
                  Events.CREATE_ACCOUNT_CLICKED,
                  properties: {"from": "sign_in_page"});
              Navigator.of(context).popAndPushNamed(Routes.SIGN_UP);
            },
            child: BoldText(AppStrings.createAccount,
                color: AppColor.textHighlighted))
      ];

  Widget _mobileNumberPage(OtpLoginPage state) =>
      Column(crossAxisAlignment: CrossAxisAlignment.center, children: [
        TextFormField(
            onChanged: (newText) {
              _loginBloc.validateMobile(newText);
            },
            textInputAction: TextInputAction.done,
            keyboardType: TextInputType.number,
            maxLength: 10,
            inputFormatters: <TextInputFormatter>[
              FilteringTextInputFormatter.digitsOnly
            ],
            controller: _mobController,
            style: TextStyle(color: Colors.white),
            decoration: InputDecoration(
                prefixStyle: TextStyle(color: Colors.white),
                prefix: Padding(
                    padding: EdgeInsets.only(right: 6.0),
                    child: RegularText("+91")),
                counterText: "",
                labelText: AppStrings.enterMobileNumber,
                labelStyle: TextStyle(color: AppColor.grayText9E9E9E),
                border: InputBorder.none,
                filled: true,
                fillColor: AppColor.inputBackground)),
        const SizedBox(height: 25.0),
        primaryButton(AppStrings.getOtp, () {
          if (state.isMobileVerified) {
            AnalyticService.getInstance().trackEvents(Events.SIGN_IN_STARTED,
                properties: {"mobile": _mobController.text});
            _loginBloc.requestOTP(context, _mobController.text);
          }
        }, active: state.isMobileVerified),
        const SizedBox(height: 20.0),
        ..._signupAction()
      ]);

  @override
  void initState() {
    super.initState();
    _loginBloc = context.read<LoginBloc>();
    _loginBloc.isWhatsappInstalled().then((isWhatsappInstalled) {
      if (isWhatsappInstalled) {
        _loginBloc.showLoginOptions();
      } else {
        _loginBloc.showOtpLoginOption();
      }
    });
  }

  Future<bool> _onBackPressed() async {
    var isWhatsappInstalled = await _loginBloc.isWhatsappInstalled();
    isMobileVerified = false;
    _mobController.text = "";
    if (_currentState is OtpLoginPage && isWhatsappInstalled) {
      _loginBloc.showLoginOptions();
      return Future.value(false);
    }
    return Future.value(true);
  }
}
