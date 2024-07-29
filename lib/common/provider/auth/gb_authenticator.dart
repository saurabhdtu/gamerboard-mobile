import 'package:firebase_remote_config/firebase_remote_config.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/provider/auth/authentication_exception.dart';
import 'package:gamerboard/common/provider/auth/authenticator.dart';
import 'package:gamerboard/common/repository/mutation_repo.dart';
import 'package:gamerboard/common/repository/query_repo.dart';
import 'package:gamerboard/feature/login/login_entities.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/shared_preferences.dart';
import 'package:gamerboard/utils/smartlook_session.dart';
import 'package:gamerboard/utils/validators.dart';
import 'package:otpless_flutter/otpless_flutter.dart';

class GBAuthenticator extends Authenticator {
  final _otplessFlutterPlugin = Otpless();

  @override
  signInWithPhoneNumber(String mobileNumber) async {
    SharedPreferenceHelper.getInstance.removeKey(PrefKeys.USER_AUTH);
    var response = await MutationRepository.instance.requestOTP(mobileNumber);
    if (response.hasErrors) {
      throw new AuthenticationException(response.errors!.first.message);
    }
  }

  @override
  signInWithWhatsApp(
  {required OtplessRequestCallback callback,required OtplessError onError}) async {
    bool canAuthenticate = await canAuthenticateWithWA();
    if (!canAuthenticate) {
      throw new AuthenticationException("Whatsapp is not installed");
    }

    _otplessFlutterPlugin.start((result){
      _onOtplessCallback(result, callback : callback, onError : onError);
    });
  }

  void _onOtplessCallback(result, {required OtplessRequestCallback callback,required OtplessError onError}) async {
    _otplessFlutterPlugin.signInCompleted();

    if (result['errorMessage'] != null) {
      onError(result['errorMessage']);
      return;
    }

    if (result['data'] == null) {
      onError("Some error occurred");
      return;
    }

    if (result['data']['error'] != null) {
      onError(result['data']['error']);
      return;
    }

    if (result['data']['mobile'] == null || result['data']['mobile']['number'] == null) {
      onError("Couldn't fetch mobile number.");
      return;
    }

    final token = result['data']['token'];
    var mobile = result['data']['mobile']['number'] ?? "";

    if(mobile != null && mobile.length >= 11){
      mobile = mobile.replaceAll(RegExp(r"^(91|0)"), '');
    }

    var phoneValidation = FieldValidators.validatePhone(mobile);

    if (phoneValidation != null) {
      onError(phoneValidation);
      return;
    }
    callback( mobile, token);
  }

  @override
  Future<AuthResult> verifyOtp(String mobile, int value,
      {UserProfileRequest? userProfileRequest}) async {
    AnalyticService.getInstance().trackEvents(Events.OTP_SUBMITTED);

    var loginResponse = await QueryRepository.instance.verifyOTP(mobile, value);

    if (loginResponse.hasErrors) {
      throw new AuthenticationException(loginResponse.errors!.first.message);
    }

    if (loginResponse.data == null) {
      throw new AuthenticationException("Some error occurred");
    }

    return _login(
        loginResponse.data!.login.token, loginResponse.data!.login.user?.id,
        userProfileRequest: userProfileRequest, mobile: mobile);
  }

  Future<AuthResult> _login(String? token, int? userId,
      {UserProfileRequest? userProfileRequest, required String mobile}) async {
    if (token != null) {
      SharedPreferenceHelper.getInstance
          .setStringPref(PrefKeys.USER_AUTH, token);
      if (userId == null) {
        if (userProfileRequest != null) {
          var createResponse = await MutationRepository.instance
              .createUser(userProfileRequest, mobile!);
          if (createResponse.hasErrors) {
            throw new AuthenticationException(
                createResponse.errors!.first.message);
          } else {
            String? finalToken = createResponse.data?.addUser.token;
            SharedPreferenceHelper.getInstance
                .setBoolPref(PrefKeys.BOOL_NEW_REGISTERED_CHECK, true);
            // resetUserData();
            SharedPreferenceHelper.getInstance
                .setStringPref(PrefKeys.USER_AUTH, finalToken!);
            await QueryRepository.instance.getMyProfile(getCached: false);
            if (createResponse.data?.addUser.user != null) {
              //recording smartlook session for new signup
              SmartLookSessionHelper.recordNewUserSession(
                  createResponse.data!.addUser.user!.id);

              AnalyticService.getInstance().pushUserProfile(
                  createResponse.data!.addUser.user!.id.toString());
              Constants.PLATFORM_CHANNEL
                  .invokeMethod("user_login", {"auth_token": finalToken});
            }

            SharedPreferenceHelper.getInstance
                .setBoolPref(PrefKeys.ATTRIBUTION_RECORDED, true);
            SharedPreferenceHelper.getInstance.removeKey(PrefKeys.INVITE_CODE);
            return AuthResult(createResponse.data!.addUser.user!.id, true);
          }
        } else {
          throw new AuthenticationException(
              "Account does not exist. Create account first.");
        }
      } else {
        AnalyticService.getInstance().pushUserProfile(userId.toString());

        await QueryRepository.instance.getMyProfile(getCached: false);

        AnalyticService.getInstance().trackEvents(Events.USER_SIGNED_IN);

        SmartLookSessionHelper.recordSessionOnAppLaunch(userId);

        SharedPreferenceHelper.getInstance
            .setBoolPref(PrefKeys.ATTRIBUTION_RECORDED, true);

        Constants.PLATFORM_CHANNEL
            .invokeMethod("user_login", {"auth_token": token});

        return AuthResult(userId, false);
      }
    } else {
      throw new AuthenticationException("Error occurred. Open app again.");
    }
  }

  @override
  Future<bool> canAuthenticateWithWA() async {
    //Not releasing right now
    bool isEnabled = FirebaseRemoteConfig.instance.getBool("enable_otpless");
    return isEnabled && (await _otplessFlutterPlugin.isWhatsAppInstalled());
  }

  @override
  Future<AuthResult> verifyOtplessToken(String token, String mobile,
      {UserProfileRequest? userProfileRequest}) async {
    var loginResponse =
        await QueryRepository.instance.verifyOtpLessToken(token);

    if (loginResponse.errors != null) {
      throw new AuthenticationException(loginResponse.errors!.first.message);
    }

    if (loginResponse.data == null) {
      throw new AuthenticationException("Error occurred");
    }

    AuthResult authResult = await _login(loginResponse.data!.loginOTPLess.token,
        loginResponse.data!.loginOTPLess.user?.id,
        userProfileRequest: userProfileRequest, mobile: mobile);

    return authResult;
  }


}
