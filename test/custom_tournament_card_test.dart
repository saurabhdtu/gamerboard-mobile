import 'dart:convert';

import 'package:flutter/cupertino.dart';
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

final model1 = TournamentCardModel(
  name: "Test",
  rank: 1,
  allowedGameMode: "GB Custom",
  userCount: 10,
  maxUsers: 100,
  maxTeams: 10,
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

final model2 = TournamentCardModel(
  name: "Test",
  rank: 1,
  userCount: 10,
  allowedGameMode: "GB Custom",
  maxUsers: 100,
  maxTeams: 10,
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
  roomId: "asd",
  roomPassword: "asd",
  slot: 1,
);

final model3 = TournamentCardModel(
  name: "Test",
  rank: 1,
  userCount: 10,
  allowedGameMode: "GB Custom",
  maxUsers: 100,
  maxTeams: 10,
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
  roomPassword: "asd",
  slot: 1,
);

Widget _widget(Widget child) => singleBlocProviderWidget<HomeBloc>(child, () => HomeBloc(
    userService: ApiUserService.instance
));
void main() {
  testWidgets('Tournament Card Has Invite only tag', (tester) async {
    await mockNetworkImagesFor(
        () async => await tester.pumpWidget(_widget(_tournamentCard1())));
    final inviteOnlyTag = find.text(AppStrings.inviteOnly);
    expect(inviteOnlyTag, findsOneWidget);
  });

  testWidgets('Tournament Card Has Exclusive only tag', (tester) async {
    await mockNetworkImagesFor(
        () async => await tester.pumpWidget(_widget(_tournamentCard2())));
    final inviteOnlyTag = find.text(AppStrings.exclusive);
    expect(inviteOnlyTag, findsOneWidget);
  });


  testWidgets('Tournament Card Has Show Password', (tester) async {
    await mockNetworkImagesFor(
            () async => await tester.pumpWidget(_widget(_tournamentCard4())));
    final inviteOnlyTag = find.text("${AppStrings.showPassword} >");
    expect(inviteOnlyTag, findsOneWidget);
  });

  testWidgets('Tournament Card Has Password will be shared', (tester) async {
    await mockNetworkImagesFor(
            () async => await tester.pumpWidget(_widget(_tournamentCard())));
    final inviteOnlyTag = find.text(AppStrings.passwordShareInMovement);
    expect(inviteOnlyTag, findsOneWidget);
  });

  testWidgets('Tournament Card full tournament', (tester) async {
    model3.joinedAt = null;
    model3.maxUsers = 2;
    model3.userCount = 2;
    model3.roomId = null;
    model3.joinBy = DateTime.now().add(Duration(days: 2));
    await mockNetworkImagesFor(
            () async => await tester.pumpWidget(_widget(_tournamentCard())));
    final tournamentFullButton = find.text(AppStrings.tournamentFull);
    expect(tournamentFullButton, findsOneWidget);
  });

  testWidgets('Tournament Card shows joined button when password is not shared', (tester) async {
    model3.joinedAt = DateTime.now();
    model3.maxUsers = 2;
    model3.userCount = 2;
    model3.roomId = null;
    await mockNetworkImagesFor(
            () async => await tester.pumpWidget(_widget(_tournamentCard())));
    final joinedButton = find.text(AppStrings.play);
    expect(joinedButton, findsOneWidget);

    final passwordWillBeShared = find.text(AppStrings.passwordShareInMovement);
    expect(passwordWillBeShared, findsOneWidget);
  });

  testWidgets('Tournament Card shows Play button when password is shared', (tester) async {
    model3.joinedAt = DateTime.now();
    model3.maxUsers = 2;
    model3.userCount = 2;
    model3.roomId = "123";
    await mockNetworkImagesFor(
            () async => await tester.pumpWidget(_widget(_tournamentCard())));
    final playButton = find.text(AppStrings.play);
    expect(playButton, findsOneWidget);

  });

  testWidgets('Tournament Card shows registration closed', (tester) async {
    model3.joinedAt = null;
    model3.maxUsers = 2;
    model3.userCount = 2;
    model3.roomId = "123";
    model3.joinBy = DateTime.now().add(Duration(days:  -2));

    await mockNetworkImagesFor(
            () async => await tester.pumpWidget(_widget(_tournamentCard(state: TournamentUIState.TEAM_LIVE_PRE_JOINED))));
    final registrationClosedButton = find.text(AppStrings.registrationClosed);
    expect(registrationClosedButton, findsOneWidget);

  });
}

TournamentCard _tournamentCard4() {
  return TournamentCard(
        0,
        1,
        type: TournamentCardType.custom,
        state: TournamentUIState.LIVE_JOINED,
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
        model: model2,
        teamPlayerWidget: SizedBox.shrink(),
        matchType: MatchType.headToHead,
      );
}

TournamentCard _tournamentCard2() {
  return TournamentCard(
        0,
        1,
        type: TournamentCardType.custom,
        state: TournamentUIState.LIVE_JOINED,
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
        model: model1,
        teamPlayerWidget: SizedBox.shrink(),
        matchType: MatchType.headToHead,
      );
}

TournamentCard _tournamentCard1() {
  return TournamentCard(
        0,
        1,
        type: TournamentCardType.custom,
        state: TournamentUIState.LIVE_JOINED,
        tagText: AppStrings.inviteOnly,
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
        model: model1,
        teamPlayerWidget: SizedBox.shrink(),
        matchType: MatchType.headToHead,
      );
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
        onNavigateToTab: (GameTeamGroup group) {},
        onLaunchGame: (GameTeamGroup group) {},
        onShowQualified: () {},
        onShowMobileInput: (page) async {},
        group: GameTeamGroup.solo,
        model: model3,
        teamPlayerWidget: SizedBox.shrink(),
        matchType: MatchType.headToHead, onJoinTournament: (int ) {  },
      );
}
