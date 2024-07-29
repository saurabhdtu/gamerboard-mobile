import 'package:gamerboard/graphql/query.dart';

////Created by saurabh.lahoti on 02/04/22
class UserSearchResult {
  bool invited = false;
  UserSummaryMixin? userSummaryMixin;
  UserSearchResult(this.invited, this.userSummaryMixin);
}
