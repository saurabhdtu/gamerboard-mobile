////Created by saurabh.lahoti on 03/08/21

class BaseState {}

class AppLoadState extends BaseState {}

class Loading extends BaseState{}

class NavigateFromSplash extends BaseState{
  Map<String,dynamic>? data;
  String route;
  NavigateFromSplash(this.data, this.route);
}

class RemoteConfigLoaded extends BaseState {
  Error? error;
  RemoteConfigLoaded({this.error});
}

class Error {
  String message;
  int code;

  Error(this.message, this.code);
}
