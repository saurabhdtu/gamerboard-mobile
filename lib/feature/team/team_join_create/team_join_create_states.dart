import 'package:gamerboard/graphql/query.dart';

////Created by saurabh.lahoti on 14/03/22
abstract class TeamJoinCreateState {}

class LoadProfile extends TeamJoinCreateState {
  bool loading;

  LoadProfile(this.loading);
}
class ProfileLoaded extends TeamJoinCreateState {
  UserMixin userMixin;

  ProfileLoaded(this.userMixin);
}
