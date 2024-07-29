import 'package:artemis/schema/graphql_response.dart';
import 'package:flagsmith/flagsmith.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/provider/auth/authentication_exception.dart';
import 'package:gamerboard/common/provider/auth/authenticator.dart';
import 'package:gamerboard/common/provider/auth/gb_authenticator.dart';
import 'package:gamerboard/common/repository/mutation_repo.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/services/feature_flag/feature_manager.dart';
import 'package:gamerboard/feature/login/login_entities.dart';
import 'package:gamerboard/feature/login/login_states.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/api_client.dart';
import 'package:gamerboard/utils/shared_preferences.dart';
import 'package:gamerboard/utils/smartlook_session.dart';
import 'package:gamerboard/utils/ui_utils.dart';
import 'package:gamerboard/utils/validators.dart';
import 'package:otpless_flutter/otpless_flutter.dart';

import '../../common/bloc/application/application_bloc.dart';
import '../../utils/flagsmith_utils.dart';
////Created by saurabh.lahoti on 13/12/21

class LoginBloc extends Cubit<LoginState> {
  bool isMobileVerified = false;
  bool isValidOTP = false;
  bool? agreedToTOS = false;
  String? mobile;
  UserProfileRequest? _userProfileRequest;
  late FeatureManager _featureManager;


  LoginBloc(this._featureManager) : super(Loading());

  final _authenticator = GBAuthenticator();


  validateMobile(String mobile) {
    _userProfileRequest = null;
    String? x = FieldValidators.validatePhone(mobile);
    bool result = x == null;
    if (isMobileVerified != result) {
      isMobileVerified = result;
      emit(OtpLoginPage(isMobileVerified));
    }
  }

  Future<bool> isWhatsappInstalled() {
    return _authenticator.canAuthenticateWithWA();
  }

  void requestOTP(BuildContext context, String mobile,
      {bool otpPage = true}) async {
    this.mobile = mobile;
    UiUtils.getInstance.buildLoading(context);
    try {
      await _authenticator.signInWithPhoneNumber(mobile);
      Navigator.of(context).pop();
      if (otpPage) Navigator.of(context).pushNamed(Routes.OTP_PAGE);
    } on AuthenticationException catch (e) {
      Navigator.of(context).pop();
      UiUtils.getInstance.showToast(e.message);
    }
  }

  void startOtpLessAuthentication(BuildContext context,
      OtplessRequestCallback onComplete) async {
    try {
      UiUtils.getInstance.buildLoading(context);
      await _authenticator.signInWithWhatsApp(callback: (mobile, token) {
        this.mobile = mobile;
        onComplete(mobile, token);
      }, onError: (errorMessage) {
        Navigator.of(context).pop();
        checkClosedOrEmit(AuthAction(error: errorMessage));
        AnalyticService.getInstance().trackEvents(
            Events.OTPLESS_FAILED, properties: {
          "mobile": mobile,
          "error": errorMessage
        });
      });
    } on AuthenticationException catch (e) {
      Navigator.of(context).pop();
      checkClosedOrEmit(AuthAction(error: e.message));
      AnalyticService.getInstance().trackEvents(
          Events.OTPLESS_FAILED, properties: {
        "mobile": mobile,
        "error": e.message
      });
    }
  }

  void validOTP(String newText) {
    bool temp = int.tryParse(newText) != null && newText.length == 4;
    if (temp != isValidOTP) {
      isValidOTP = temp;
      emit(LoadOTPPage(isValidOTP));
    }
  }

  Future<CheckUniqueUser$Query$UniqueUserResponse?> checkUserValidity(
      String phoneNum, String userName) async {
    var response =
    await QueryRepository.instance.checkUniqueUser(phoneNum, userName);
    if (response.hasErrors) {
      return Future.value(null);
    } else {
      return Future.value(response.data?.checkUniqueUser);
    }
  }

  void verifyOTP(String otp) async {
    final int? value = int.tryParse(otp);
    emit(AuthAction(loading: true));
    if (value != null && mobile != null) {
      try {
        AuthResult authResult = await _authenticator.verifyOtp(mobile!, value,
            userProfileRequest: _userProfileRequest);
        await _featureManager.fetchAllFlags(authResult.userId.toString());
        if (authResult.isNewUser) {
          SharedPreferenceHelper.getInstance
              .setBoolPref(PrefKeys.IS_NEW_USER_FIRST_SESSION, true);
          _featureManager.setTrait(TraitKeys.IS_NEW_USER,
              authResult.userId.toString(), true);
          _featureManager.setTrait(TraitKeys.ONBOARDING_STEPS_VISIBLE,
              authResult.userId.toString(), true);

          if ((await _checkUserPreference(authResult.userId))) {
            return;
          }
        }
        checkClosedOrEmit(AuthAction(moveToHome: true));
      } on AuthenticationException catch (e) {
        checkClosedOrEmit(AuthAction(error: e.message));
      }
    }
  }

  Future verifyOTPLess(BuildContext context, String token) async {
    emit(AuthAction(loading: true));
    try {
      AuthResult authResult = await _authenticator.verifyOtplessToken(
          token, mobile!,
          userProfileRequest: _userProfileRequest);
      if (authResult.isNewUser) {
        _featureManager.setTrait(TraitKeys.ONBOARDING_STEPS_VISIBLE,
            authResult.userId.toString(), true);
        _featureManager.setTrait(TraitKeys.IS_NEW_USER,
            authResult.userId.toString(), true);
      }
      AnalyticService.getInstance().trackEvents(
          Events.OTPLESS_SUCCESSFULL, properties: {
        "mobile": mobile
      });
      checkClosedOrEmit(AuthAction(moveToHome: true));
    } on AuthenticationException catch (e) {
      AnalyticService.getInstance().trackEvents(
          Events.OTPLESS_FAILED, properties: {
        "mobile": mobile,
        "error": e.message
      });
      Navigator.of(context).pop();
      checkClosedOrEmit(AuthAction(error: e.message));
    }
  }

  void saveProfileDetails(String userName, String firstName, String lastName,
      String dob, String referralCode) {
    _userProfileRequest = UserProfileRequest(
        userName,
        firstName,
        lastName,
        dob.isNotEmpty ? AppDateFormats.dobFormat.parse(dob) : null,
        referralCode);
  }

  void loadOTPPage() {
    isValidOTP = false;
    checkClosedOrEmit(LoadOTPPage(isValidOTP));
  }

  void emitTimerState(int i) {
    checkClosedOrEmit(TimerState(i));
  }

  void showOtpLoginOption() {
    checkClosedOrEmit(OtpLoginPage(false));
  }

  void showLoginOptions() {
    mobile = "";
    checkClosedOrEmit(LoginOptionState());
  }

  checkClosedOrEmit(LoginState state) {
    if (isClosed) return;
    emit(state);
  }

  void showSignupForm({String? otpLessToken, String? mobile}) {
    checkClosedOrEmit(
        ShowSignUpForm(otpLessToken: otpLessToken, mobile: mobile));
  }

  Future<bool> _checkUserPreference(int userId) async {
    var identity = Identity(identifier: userId.toString());

    var showLocationPrefs = await _featureManager.isEnabled( FlagKeys.ENABLE_LOCATION_PREFS, userId.toString());

    var locationPrefsInputAlreadyShown =  await _featureManager.getTrait(TraitKeys.LOCATION_PREFS_SHOWN,
        userId.toString());

    var show = showLocationPrefs && !locationPrefsInputAlreadyShown;
    if (show) {

      await _featureManager.setTrait(TraitKeys.LOCATION_PREFS_SHOWN,
          userId.toString(), true);

      AnalyticService.getInstance()
          .trackEvents(Events.LOCATION_PREFERENCE_SHOWN);
      checkClosedOrEmit(NavigateToLocationPrefs(identity));
      return true;
    }
    return false;
  }

}
