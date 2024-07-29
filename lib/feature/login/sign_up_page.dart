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
import 'package:gamerboard/utils/validators.dart';

import '../../utils/shared_preferences.dart';
////Created by saurabh.lahoti on 13/12/21

class SignUpPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _SignUpState();
}

class _SignUpState extends State<SignUpPage> {
  late LoginBloc _loginBloc;
  final GlobalKey<FormState> _formKey = GlobalKey<FormState>();
  final _controllerFName = TextEditingController();
  final _controllerLName = TextEditingController();
  final _controllerGBUname = TextEditingController();
  final _controllerMobile = TextEditingController();
  final _controllerDOB = TextEditingController();
  final _controllerInvite = TextEditingController();

  final DateTime today = DateTime.now();
  bool verifiedOnce = false;
  late DateTime lastDate;
  final focusFirstName = FocusNode();
  final focusLastName = FocusNode();
  final focusMobile = FocusNode();

  LoginState? _currentState;

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _onBackPressed,
      child: appScaffold(
          body: SingleChildScrollView(
        child: Form(
            key: _formKey,
            child: BlocListener<LoginBloc, LoginState>(
                bloc: _loginBloc,
                listener: (ctx, state) async {
                  if (state is NavigateToLocationPrefs) {
                    await context.read<ApplicationBloc>().loadAppConfig();
                    Navigator.of(context).pushNamedAndRemoveUntil(
                        Routes.LOCATION_PAGE, (Route<dynamic> route) => false);
                  }
                  if (state is AuthAction) {
                    if (state.error != null) {
                      AnalyticService.getInstance().trackEvents(
                          Events.CREATE_ACCOUNT_ERROR,
                          properties: {"message": state.error});
                      UiUtils.getInstance.showToast(state.error!);
                    }

                    if (state.moveToHome) {
                      await context.read<ApplicationBloc>().loadAppConfig();
                      Navigator.of(context).pushNamedAndRemoveUntil(
                          Routes.HOME_PAGE, (Route<dynamic> route) => false);
                    }
                  }
                  if (state is ShowSignUpForm) {
                    if (state.otpLessToken != null && state.mobile != null) {
                      _controllerMobile.text = state.mobile!;
                    } else {
                      _controllerMobile.text = "";
                    }
                  }
                },
                listenWhen: (previous, current) =>
                    current is ShowSignUpForm || current is AuthAction || current is NavigateToLocationPrefs,
                child: Align(
                    alignment: Alignment.center,
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          const SizedBox(height: 20.0),
                          SvgPicture.asset("${imageAssets}ic_gb_logo.svg"),
                          const SizedBox(height: 20.0),
                          RegularText(AppStrings.createGBAccount,
                              fontSize: 20.0),
                          const SizedBox(height: 20.0),
                          Container(
                              width: MediaQuery.of(context).size.width * 0.4,
                              child: BlocConsumer<LoginBloc, LoginState>(
                                listener: (ctx, state) {},
                                builder: (ctx, state) {
                                  _currentState = state;
                                  if (state is LoginOptionState) {
                                    return LoginOption(
                                      otpLessButtonText:
                                          AppStrings.signupUsingWhatsapp,
                                      otpButtonText: AppStrings.signupUsingOtp,
                                      onOtpLessOptionClicked: () {
                                        AnalyticService.getInstance()
                                            .trackEvents(
                                                Events.OTPLESS_SIGNUP_CLICKED);
                                        _loginBloc.startOtpLessAuthentication(
                                            context, (mobile, token) {
                                          Navigator.of(context).pop();
                                          _loginBloc.showSignupForm(
                                              otpLessToken: token,
                                              mobile: mobile);
                                        });
                                      },
                                      onOtpOptionClicked:
                                          _loginBloc.showSignupForm,
                                      additionalActions: [
                                        _loginOption(context)
                                      ],
                                    );
                                  }
                                  if (state is ShowSignUpForm) {
                                    return _signupForm(context, state);
                                  }
                                  return SizedBox.shrink();
                                },
                                buildWhen: (previous, current) =>
                                    current is LoginOptionState ||
                                    current is ShowSignUpForm,
                              ))
                        ])))),
      )),
    );
  }

  Column _signupForm(BuildContext context, ShowSignUpForm state) {
    return Column(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
      TextFormField(
          maxLength: 30,
          validator: FieldValidators.validatorGamertag,
          controller: _controllerGBUname,
          enableSuggestions: false,
          autocorrect: false,
          inputFormatters: [
            TextInputFormatter.withFunction((oldValue, newValue) =>
                TextEditingValue(
                    text: newValue.text.toLowerCase(),
                    selection: newValue.selection)),
            AppInputFormatters.spaceFormatter
          ],
          textInputAction: TextInputAction.next,
          onEditingComplete: () => _validateForm(),
          style: TextStyle(color: Colors.white),
          onFieldSubmitted: (value) =>
              FocusScope.of(context).requestFocus(focusFirstName),
          decoration: _Mydecor(AppStrings.username)),
      const SizedBox(height: 10.0),
      Row(children: [
        Expanded(
            child: TextFormField(
                maxLength: 50,
                onEditingComplete: () => _validateForm(),
                validator: FieldValidators.validatorFName,
                textInputAction: TextInputAction.next,
                enableSuggestions: false,
                autocorrect: false,
                textCapitalization: TextCapitalization.words,
                keyboardType: TextInputType.name,
                onFieldSubmitted: (value) =>
                    FocusScope.of(context).requestFocus(focusLastName),
                style: TextStyle(color: Colors.white),
                controller: _controllerFName,
                focusNode: focusFirstName,
                decoration: _Mydecor(AppStrings.firstName))),
        const SizedBox(width: 10.0),
        Expanded(
            child: TextFormField(
                maxLength: 50,
                onEditingComplete: () => _validateForm(),
                validator: FieldValidators.validatorLName,
                textInputAction: TextInputAction.next,
                autocorrect: false,
                focusNode: focusLastName,
                textCapitalization: TextCapitalization.words,
                keyboardType: TextInputType.name,
                onFieldSubmitted: (value) =>
                    FocusScope.of(context).requestFocus(focusMobile),
                enableSuggestions: false,
                style: TextStyle(color: Colors.white),
                controller: _controllerLName,
                decoration: _Mydecor(AppStrings.lastName)))
      ]),
      const SizedBox(height: 10.0),
      TextFormField(
          maxLength: 10,
          validator: FieldValidators.validatePhone,
          focusNode: focusMobile,
          onEditingComplete: () => _validateForm(),
          onFieldSubmitted: (value) {
            FocusScope.of(context).unfocus();
            if (_controllerDOB.text.isEmpty) _openCalendar();
          },
          enabled: state.otpLessToken == null,
          controller: _controllerMobile,
          keyboardType: TextInputType.phone,
          inputFormatters: <TextInputFormatter>[
            FilteringTextInputFormatter.digitsOnly
          ],
          style: TextStyle(color: Colors.white),
          decoration: _Mydecor(AppStrings.enterMobileNumber,
              prefix: Padding(
                  padding: EdgeInsets.only(right: 6.0),
                  child: RegularText("+91")))),
      const SizedBox(height: 15.0),
      RegularText(AppStrings.dateOfBirth, color: AppColor.grayText9E9E9E),
      const SizedBox(height: 5.0),
      InkWell(
          onTap: () => _openCalendar(),
          child: TextFormField(
              controller: _controllerDOB,
              enabled: false,
              validator: FieldValidators.validateDOB,
              textAlignVertical: TextAlignVertical.center,
              style: TextStyle(color: Colors.white),
              decoration: InputDecoration(
                  border: InputBorder.none,
                  hintText: "DD / MM / YYYY",
                  hintStyle: TextStyle(color: AppColor.grayText9E9E9E),
                  errorBorder: OutlineInputBorder(
                      borderSide: BorderSide(color: AppColor.errorRed)),
                  errorStyle: TextStyle(color: AppColor.errorRed),
                  prefixIcon: const Icon(Icons.calendar_today_rounded,
                      size: 15.0, color: AppColor.textSubTitle),
                  filled: true,
                  fillColor: AppColor.inputBackground))),
      const SizedBox(height: 10.0),
      TextFormField(
          maxLength: 30,
          controller: _controllerInvite,
          enableSuggestions: false,
          autocorrect: false,
          textCapitalization: TextCapitalization.characters,
          inputFormatters: [
            TextInputFormatter.withFunction((oldValue, newValue) =>
                TextEditingValue(
                    text: newValue.text.toUpperCase(),
                    selection: newValue.selection)),
            AppInputFormatters.spaceFormatter,
            LengthLimitingTextInputFormatter(
              10,
            ),
            //n is maximum number of characters you want in textfield
          ],
          textInputAction: TextInputAction.done,
          onEditingComplete: () => _validateForm(),
          style: TextStyle(color: Colors.white),
          decoration: _Mydecor(AppStrings.inviteCodeOptionalField,
              prefix: Text(
                AppStrings.code,
                style: TextStyle(color: Colors.white),
              ))),
      const SizedBox(height: 10.0),
      Row(children: [
        Checkbox(
            value: _loginBloc.agreedToTOS,
            onChanged: (value) {
              setState(() {
                _loginBloc.agreedToTOS = value;
              });
            }),
        Expanded(
            child: RichText(
                text: TextSpan(
                    text: "I agree to gamerboard's ",
                    style: TextStyle(fontSize: 14.0, color: Colors.white),
                    children: [
              WidgetSpan(
                  child: InkWell(
                      child: RegularText("Terms of Service",
                          color: AppColor.textHighlighted),
                      onTap: () {}))
            ])))
      ]),
      const SizedBox(height: 15.0),
      primaryButton(AppStrings.continueText,
          () => _validateForm(submit: true, otplessToken: state.otpLessToken),
          active: true),
      _loginOption(context)
    ]);
  }

  Column _loginOption(BuildContext context) {
    return Column(children: [
      const SizedBox(height: 15.0),
      RegularText(AppStrings.alreadyHaveAccount),
      const SizedBox(height: 5.0),
      InkWell(
          onTap: () {
            AnalyticService.getInstance().trackEvents(Events.SIGN_IN_CLICKED,
                properties: {"from": "create_account_page"});
            Navigator.of(context).popAndPushNamed(Routes.LOG_IN);
          },
          child: BoldText(AppStrings.signIn, color: AppColor.textHighlighted)),
      const SizedBox(height: 30.0)
    ]);
  }

  void _validateForm({bool submit = false, String? otplessToken}) {
    if (verifiedOnce || submit) {
      bool? result = _formKey.currentState?.validate();
      if (submit && result == true && _loginBloc.agreedToTOS == true) {
        _loginBloc.saveProfileDetails(
            _controllerGBUname.text,
            _controllerFName.text,
            _controllerLName.text,
            _controllerDOB.text,
            _controllerInvite.text);
        UiUtils.getInstance.buildLoading(context, dismissible: false);
        _loginBloc
            .checkUserValidity(_controllerMobile.text, _controllerGBUname.text)
            .then((value) {
          Navigator.of(context).pop();
          if (value?.phone == false && value?.username == false) {
            AnalyticService.getInstance().trackEvents(Events.FORM_SUBMITTED);
            if (otplessToken != null) {
              _loginBloc.verifyOTPLess(context, otplessToken);
            } else {
              _loginBloc.requestOTP(context, _controllerMobile.text);
            }
          } else {
            late String message;
            if (value == null) {
              message = "Some error occurred";
            } else if (value.phone == true && value.username == true) {
              message = "Username and mobile number already exists";
            } else if (value.phone == true) {
              message = "Mobile number already exists";
            } else {
              message = "Username already exists";
            }
            AnalyticService.getInstance().trackEvents(
                Events.CREATE_ACCOUNT_ERROR,
                properties: {"message": message});
            UiUtils.getInstance.showToast(message);
          }
        });
      }
    }
    if (submit) verifiedOnce = true;
  }

  @override
  void initState() {
    super.initState();
    _loginBloc = context.read<LoginBloc>();
    _checkInviteCode();
    lastDate = DateTime(today.year - 18, today.month, today.day, today.hour);
    _loginBloc.isWhatsappInstalled().then((isWhatsappInstalled) {
      if (isWhatsappInstalled) {
        _loginBloc.showLoginOptions();
      } else {
        _loginBloc.showSignupForm();
      }
    });
  }

  _checkInviteCode() async {
    String? inviteCode = await SharedPreferenceHelper.getInstance
        .getStringPref(PrefKeys.INVITE_CODE);
    if (inviteCode != null) {
      setState(() {
        _controllerInvite.text = inviteCode;
      });
    }
  }

  _openCalendar() async {
    final DateTime? picked = await showDatePicker(
        context: context,
        firstDate: today.subtract(Duration(days: 365 * 80)),
        initialDate: lastDate,
        lastDate: lastDate);
    lastDate = picked ?? lastDate;
    _controllerDOB.text = AppDateFormats.dobFormat.format(lastDate);
  }

  Future<bool> _onBackPressed() async {
    var isWhatsappInstalled = await _loginBloc.isWhatsappInstalled();
    if (_currentState is ShowSignUpForm && isWhatsappInstalled) {
      _loginBloc.showLoginOptions();
      return Future.value(false);
    }
    return Future.value(true);
  }
}

class _Mydecor extends InputDecoration {
  _Mydecor(
    String labelText, {
    Widget? prefix,
  }) : super(
            filled: true,
            labelText: labelText,
            counterText: "",
            border: InputBorder.none,
            prefix: prefix,
            labelStyle: TextStyle(color: AppColor.grayText9E9E9E),
            fillColor: AppColor.inputBackground,
            errorBorder: OutlineInputBorder(
                borderSide: BorderSide(color: AppColor.errorRed)),
            errorStyle: TextStyle(color: AppColor.errorRed));
}
