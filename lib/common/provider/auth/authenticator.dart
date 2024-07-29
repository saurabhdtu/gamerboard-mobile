import 'package:gamerboard/feature/login/login_entities.dart';

typedef void OtplessRequestCallback(String mobile, String token);
typedef void OtplessError(String error);

abstract class Authenticator {
  signInWithPhoneNumber(String mobileNumber);
  signInWithWhatsApp({required OtplessRequestCallback callback,required OtplessError onError});
  Future<bool> canAuthenticateWithWA();
  Future<AuthResult> verifyOtp(String mobile, int otp,
      {UserProfileRequest? userProfileRequest});
  Future<AuthResult> verifyOtplessToken(String token,String mobile,
      {UserProfileRequest? userProfileRequest});
}


class AuthResult{
  int userId;
  bool isNewUser;

  AuthResult(this.userId, this.isNewUser);
}