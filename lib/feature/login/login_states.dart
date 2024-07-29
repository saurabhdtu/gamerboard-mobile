import 'package:flagsmith/flagsmith.dart';

////Created by saurabh.lahoti on 13/12/21

abstract class LoginState {}

class Loading extends LoginState{}

class LoginOptionState extends LoginState {
  LoginOptionState();
}
class ShowSignUpForm extends LoginState {
  String? otpLessToken;
  String? mobile;
  ShowSignUpForm({this.otpLessToken, this.mobile});
}
class OtpLoginPage extends LoginState {
  bool isMobileVerified;
  OtpLoginPage(this.isMobileVerified);
}


class LoadOTPPage extends LoginState {
  bool isOTPValid;

  LoadOTPPage(this.isOTPValid);
}

class Error extends LoginState {
  String message;

  Error(this.message);
}

/*class SignUpAction extends LoginState {
  bool loading;
  String? error;

  SignUpAction({this.loading=false, this.error});
}*/

class AuthAction extends LoginState {
  bool loading;
  String? error;
  bool moveToHome;

  AuthAction({this.loading = false, this.moveToHome = false, this.error});
}

class TimerState extends LoginState{
  int time;
  TimerState(this.time);
}

class NavigateToLocationPrefs extends LoginState {
  Identity identity;
  NavigateToLocationPrefs(this.identity);
}