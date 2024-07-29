import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gamerboard/common/services/user/api_user_service.dart';
import 'package:gamerboard/feature/home/home_bloc.dart';
import 'package:gamerboard/feature/home/widgets/tournament_card.dart';
import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:network_image_mock/network_image_mock.dart';

import 'common/widget_container.dart';

final qualifiersJson =
    "{\"rule\":\"NUM_GAMES_SINCE\",\"value\":{\"since\":\"2023-11-03T13:03:37.928+0530\",\"numGames\":10}}";

Widget _widget(Widget child) => singleBlocProviderWidget<HomeBloc>(child, () => HomeBloc(
  userService: ApiUserService.instance
));

final model3 = TournamentCardModel(
  name: "Test",
  rank: 1,
  userCount: 10,
  maxUsers: 100,
  maxTeams: 10,
  allowedGameMode: "GB Custom",
  joinedAt: DateTime(2023, 11, 02),
  startTime: DateTime(2023, 11, 02),
  endTime: DateTime(2023, 11, 02),
  qualifiers: [
    TournamentMixin$CustomQualificationRules.fromJson(
        jsonDecode(qualifiersJson))
  ],
  allowedMaps: "Nusa",
  fee: 10,
  joinBy: DateTime(2023, 11, 02),
  allowedTiers: [BgmiLevels.bronzeFive],
  maxPrize: 100,
  levelImageUrl:
      "https://t3.ftcdn.net/jpg/03/45/05/92/360_F_345059232_CPieT8RIWOUk4JqBkkWkIETYAkmz2b75.jpg",
  roomId: null,
  roomPassword: null,
  slot: null,
);


void main() {


  testWidgets('Shows start icon on action button for qualifier matches', (tester) async {
    model3.joinedAt = null;
    model3.maxUsers = 2;
    model3.userCount = 2;
    model3.roomId = "123";
    
    model3.joinBy = DateTime.now().add(Duration(days:  2));

    await mockNetworkImagesFor(
            () async => await tester.pumpWidget(_widget(_tournamentCard(state: TournamentUIState.TEAM_LIVE_PRE_JOINED, type: TournamentCardType.classic))));
    final registrationClosedButton = find.byIcon(Icons.star);
    expect(registrationClosedButton, findsAtLeastNWidgets(2));
  });
  testWidgets('Shows start icon on play action button for qualifier matches', (tester) async {
    model3.maxUsers = 2;
    model3.userCount = 2;
    model3.roomId = "123";

    model3.joinedAt = DateTime.now();
    model3.joinBy = DateTime.now().add(Duration(days:  -2));

    await mockNetworkImagesFor(
            () async => await tester.pumpWidget(_widget(_tournamentCard(state: TournamentUIState.TEAM_LIVE_PRE_JOINED, type: TournamentCardType.classic))));
    final playButton = find.text(AppStrings.play);
    expect(playButton, findsOneWidget);

    final registrationClosedButton = find.byIcon(Icons.star);
    expect(registrationClosedButton, findsAtLeastNWidgets(2));
  });
}

TournamentCard _tournamentCard({TournamentUIState state = TournamentUIState.LIVE_JOINED, TournamentCardType type = TournamentCardType.custom}) {
  return TournamentCard(
        0,
        1,
        type: type,
        state: state,
        tagText: AppStrings.exclusive,
        showTag: true,
        squadAvailable: false,
        profileAvailable: true,
        showPlayButtonOnCard: true,
        userEligible: true,
        width: 200,
        onOpenTournament: (int) {},
        onJoinTournament: (int) {},
        onNavigateToTab: (GameTeamGroup group) {},
        onLaunchGame: (GameTeamGroup group) {},
        onShowQualified: () {},
        onShowMobileInput: (page) async {},
        group: GameTeamGroup.solo,
        model: model3,
        teamPlayerWidget: SizedBox.shrink(),
        matchType: MatchType.classic,
      );
}
