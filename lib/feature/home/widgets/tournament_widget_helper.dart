import 'package:flutter/material.dart';
import 'package:gamerboard/common/widgets/containers.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/home/home_bloc.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/invite_code_input_dialog.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/mobile_input_for_idp_dialog.dart';
import 'package:gamerboard/feature/home/widgets/onboarding_helper.dart';
import 'package:gamerboard/feature/home/widgets/tournament_card.dart';
import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/ui_utils.dart';
import 'package:gamerboard/utils/validators.dart';

class TournamentWidgetHelper{
  static Widget getLeaderboardGrid(
      BuildContext context,
      HomeBloc bloc,
      int homeScreenIndex,
      double width,
      double height,
      List<UserTournamentMixin> topTournaments) {
    return ListView.separated(
        itemBuilder: (context, index) => Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: _getCards(context, bloc, width, height, homeScreenIndex,
                index, topTournaments)),
        separatorBuilder: (context, index) => const SizedBox(height: 8.0),
        itemCount: (topTournaments.length / 3).ceil());
  }

  static  List<Widget> _getCards(
      BuildContext context,
      HomeBloc bloc,
      double width,
      double height,
      int homeScreenIndex,
      int index,
      List<UserTournamentMixin> tournaments) {
    List<Widget> list = [];
    double w = (width - 16) / 3;
    int startIndex = index * 3;
    for (int i = startIndex; i < startIndex + 3; i++) {
      if (i < tournaments.length) {
        if (tournaments[i] is OnboardingEducationCard) {
          list.add(OnboardingHelper(w * 2, height, homeScreenIndex));
          break;
        } else {
          final userTournament = tournaments[i];
          var state = userTournament.getTournamentState();
          var type = TournamentCardType.classic;
          if (userTournament.tournament.matchType == MatchType.headToHead) {
            state = userTournament.getCustomTournamentState();
            type = TournamentCardType.custom;
          }
          list.add(_customRoomCard(context, bloc, type,
              state: state,
              homeScreenIndex: homeScreenIndex,
              width: w,
              userTournament: tournaments[i]));
        }
      } else
        list.add(SizedBox(width: w));
    }
    return list;
  }

  static  _customRoomCard(BuildContext context, HomeBloc bloc, TournamentCardType type,
      {required int homeScreenIndex,
        required double width,
        required TournamentUIState state,
        required UserTournamentMixin userTournament}) {
    final tournament = userTournament.tournament;
    final isProfileAvailable = bloc.user?.getGameLevelFromMetaData(
        userTournament.tournamentGroup(),
        bloc.user!
            .getCurrentGameProfile(bloc.applicationBloc.userCurrentGame)) !=
        null;
    final    isUserEligible = canJoinTournament(userTournament, bloc.user,
        bloc.applicationBloc.userCurrentGame, true) ==
        null;
    TournamentMatchMixin$TournamentMatchMetadata? metadata;
    if (tournament.matches?.isNotEmpty == true) {
      metadata = tournament.matches?.first.metadata;
    }
    final model = TournamentCardModel(
      name: tournament.name,
      rank: userTournament.rank,
      maxTeams: tournament.rules.maxTeams,
      userCount: tournament.userCount,
      maxUsers: tournament.rules.maxUsers,
      joinedAt: userTournament.joinedAt,
      startTime: tournament.startTime,
      endTime: tournament.endTime,
      qualifiers: tournament.qualifiers,
      allowedMaps: userTournament.getAllowedMaps(),
      fee: tournament.fee,
      joinBy: tournament.joinBy,
      allowedGameMode: userTournament.getAllowedGameMode(),
      allowedTiers: userTournament.getAllowedTiers(),
      maxPrize: tournament.maxPrize,
      levelImageUrl: userTournament.getTournamentLevelImageUrl(),
      roomId: metadata?.roomId,
      roomPassword: metadata?.roomPassword,
      slot: userTournament.tournamentMatchUser?.slotInfo?.teamNumber,
    );

    var tagText = AppStrings.exclusive;

    if (tournament.joinCode != null) {
      tagText = AppStrings.inviteOnly;
    }

    return TournamentCard(
      tournament.id,
      homeScreenIndex,
      type: type,
      state: state,
      tagText: tagText,
      showTag: type == TournamentCardType.custom || tournament.joinCode != null,
      squadAvailable: userTournament.squad != null,
      profileAvailable: isProfileAvailable,
      showPlayButtonOnCard: bloc.showPlayButtonOnCard,
      userEligible: isUserEligible,
      width: width,
      onOpenTournament: (pageType) {
        bloc.openTournament(context, pageType, userTournament, );
      },
      onJoinTournament: (pageType){
        bloc.joinTournament(context, userTournament, pageType);
      },
      onNavigateToTab: (GameTeamGroup group) {
        bloc.navigateToTab(group);
      },
      onLaunchGame: (GameTeamGroup group) {
        bloc.launchGame(context, group, userTournament);
      },
      onShowQualified: () {
        bloc.checkUserQualification(context, userTournament, homeScreenIndex);
      },
      onShowMobileInput: (pageType) async {
        await showMobileInputDialog(
            context, userTournament, bloc, homeScreenIndex);
      },
      group: tournament.tournamentGroup(),
      model: model,
      teamPlayerWidget:
      _teamPlayerWidget(userTournament, bloc, context, homeScreenIndex),
      matchType: tournament.matchType,
    );
  }

  static  Widget _teamPlayerWidget(UserTournamentMixin userTournament, HomeBloc bloc,
      BuildContext context, int homeScreenIndex) {
    return userTournament.squad == null
        ? Padding(
        padding: const EdgeInsets.only(bottom: 8.0),
        child: Align(
            alignment: Alignment.center,
            child: BoldText(AppStrings.seeDetails)))
        : teamPlayerStatusContainer(
        bloc.user?.id,
        userTournament.squad!,
        userTournament.tournamentGroup(),
            () => bloc.openTournament(context, homeScreenIndex, userTournament,
            showTeamDetail: true));
  }

  static  Future<dynamic> showMobileInputDialog(
      BuildContext context,
      UserTournamentMixin userTournament,
      HomeBloc bloc,
      int homeScreenIndex) async {

    return await UiUtils.getInstance.showCustomDialog(
        context,
        MobileInputForIDPDialog(
          userTournament,
          bloc.user!.phone!,
              (tournament, pageType, phone) {
            bloc.joinTournament(context, tournament, pageType,
                phoneNumber: phone);
          },
          homeScreenIndex,
        ),
        dismissible: false);
  }

  static  void showInviteOnlyDialog(
      BuildContext context,
      UserTournamentMixin userTournament,
      HomeBloc bloc,
      String? phoneNumber,
      int homeScreenIndex) async {
    return await UiUtils.getInstance.showCustomDialog(
        context,
        InviteCodeInputDialog(onSubmitInviteCode :
            (joinCode) {
          bloc.joinTournament(context, userTournament, homeScreenIndex,
              phoneNumber: phoneNumber, joinCode: joinCode);
        }
        ),
        dismissible: false);
  }

}