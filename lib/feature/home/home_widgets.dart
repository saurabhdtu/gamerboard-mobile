import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/bloc/application/application_bloc.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/home/home_bloc.dart';
import 'package:gamerboard/feature/home/home_page.dart';
import 'package:gamerboard/feature/home/home_state.dart';
import 'package:gamerboard/feature/home/model/tournament_sort_order.dart';
import 'package:gamerboard/feature/home/model/game_info_model.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/invite_friends_dialog.dart';
import 'package:gamerboard/feature/home/widgets/dialogs/tier_selection_dialog.dart';
import 'package:gamerboard/feature/home/widgets/home_top_board.dart';
import 'package:gamerboard/feature/home/widgets/my_boards.dart';
import 'package:gamerboard/feature/home/widgets/selectable_game_widget.dart';
import 'package:gamerboard/feature/home/widgets/user_search_by_preference.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/data_type_ext.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/ui_utils.dart';

import '../../utils/share_utils.dart';
////Created by saurabh.lahoti on 27/09/21

class HomeWidgets {
  final HomeBloc _homeBloc;
  final BuildContext context;

  HomeWidgets(this._homeBloc, this.context);

  Widget _getTabBar(
      TabController tabController, HomeBloc homeBloc, double width,
      {required FocusNode buttonFocusNode}) {
    return Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
      Flexible(
        child: TabBar(
            isScrollable: true,
            indicatorColor: Colors.white,
            indicatorWeight: 2.0,
            controller: tabController,
            unselectedLabelStyle:
                RegularTextStyle(color: AppColor.textSubTitle),
            labelStyle: BoldTextStyle(color: Colors.white),
            tabs: [
              AppStrings.history,
              AppStrings.topLeaderboards,
              AppStrings.squad,
              AppStrings.solo,
              AppStrings.duo,
              AppStrings.topGamer
            ]
                .map((e) => Tab(
                    height: 35.0,
                    child: Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 20.0),
                        child: Text(e))))
                .toList()),
      ),
      BlocBuilder<HomeBloc, HomeState>(
          builder: (ctx, state) {
            if (state is TournamentLoading &&
                state.group == _homeBloc.teamGroupForIndex()) {
              if (state.showProgress) return appCircularProgressIndicator();
            } else if (state is GameTierInputState) {
              return Row(children: [
                const SizedBox(width: 24),
                primaryButton(AppStrings.submit, () {
                  if (homeBloc.selectedTier != null)
                    homeBloc.addGameLevel({
                      "level": homeBloc.selectedTier!.key,
                      "group": _homeBloc.teamGroupForIndex()
                    });
                },
                    active: homeBloc.selectedTier != null,
                    paddingVertical: 8,
                    paddingHorizontal: 32),
                const SizedBox(width: 12)
              ]);
            } else if (state is UpdatingBgmiLevel && state.showProgress) {
              return appCircularProgressIndicator();
            }
            return _filterMenu(buttonFocusNode);
          },
          buildWhen: (previous, current) =>
              (current is MyTournamentLoaded) ||
              (current is GameTierInputState &&
                  current.group == _homeBloc.teamGroupForIndex()) ||
              (current is TournamentLoading &&
                  current.group == _homeBloc.teamGroupForIndex()) ||
              (current is UpdatingBgmiLevel),
          bloc: _homeBloc)
    ]);
  }

  Widget getLeftPane(BuildContext context, UserDetailsLoaded state) {
    return SingleChildScrollView(
        child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
          Padding(
              padding: const EdgeInsets.symmetric(horizontal: 8.0),
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const SizedBox(height: 10.0),
                    Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          Row(children: [
                            if (state.user.image != null)
                              Image.network(state.user.image!,
                                  width: 24.0, height: 24.0)
                            else
                              const Icon(Icons.person,
                                  size: 24.0, color: Colors.white30),
                            const SizedBox(width: 8.0),
                            RegularText(state.user.username, fontSize: 13.0)
                          ]),
                        ]),
                    const SizedBox(height: 8.0),
                    _homeBloc.ffmaxEnable
                        ? Container(
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                InkWell(
                                    onTap: () {
                                      _homeBloc
                                          .emit(ShowGameSelectionDialog(true));
                                    },
                                    child: BlocConsumer<HomeBloc, HomeState>(
                                        listener: (context, state) {},
                                        builder: (context, gameState) {
                                          var selectedGame = _homeBloc
                                              .applicationBloc.userCurrentGame;
                                          if (gameState is GameSelection) {
                                            return Row(
                                              children: GameInfoModel.allTypes
                                                  .map(
                                                    (e) => _gameSelectionItem(
                                                        e, selectedGame),
                                                  )
                                                  .toList(),
                                            );
                                          }
                                          return Column(
                                            children: [
                                              Row(
                                                children: GameInfoModel.allTypes
                                                    .map(
                                                      (e) => _gameSelectionItem(
                                                          e, selectedGame),
                                                    )
                                                    .toList(),
                                              ),
                                              Text(
                                                selectedGame.getGameName(),
                                                style: SemiBoldTextStyle(
                                                    fontSize: 12),
                                              )
                                            ],
                                          );
                                        }))
                              ],
                            ),
                          )
                        : SizedBox.shrink(),
                    const SizedBox(height: 8.0),
                    _tierSection(),
                    const SizedBox(height: 10.0)
                  ])),
          Divider(height: 1.5, color: AppColor.dividerColor, thickness: 1.5),
          Padding(
              padding:
                  const EdgeInsets.symmetric(horizontal: 8.0, vertical: 10),
              child: Column(
                  crossAxisAlignment: CrossAxisAlignment.stretch,
                  children: [
                    InkWell(
                        onTap: () async {
                          await Navigator.of(context).pushNamed(Routes.WALLET);
                          _homeBloc.loadData(true);
                        },
                        child: Column(
                            crossAxisAlignment: CrossAxisAlignment.stretch,
                            children: [
                              Text(AppStrings.myWallet,
                                  textAlign: TextAlign.left,
                                  style: SemiBoldTextStyle(fontSize: 16)),
                              const SizedBox(height: 4.0),
                              RegularText(AppStrings.depositWinningBonus,
                                  fontSize: 10,
                                  color: AppColor.lightWhiteColor),
                              const SizedBox(height: 8.0),
                              BoldText(
                                  "${AppStrings.rupeeSymbol}${(state.user.wallet.deposit + state.user.wallet.winning + state.user.wallet.bonus).formattedNumber()}",
                                  fontSize: 26.0,
                                  textAlign: TextAlign.start,
                                  color: AppColor.successGreen),
                              const SizedBox(height: 4.0),
                              Row(children: [
                                Text(AppStrings.manageWallet,
                                    style: SemiBoldTextStyle(
                                        fontSize: 12,
                                        color: Color(0xffA375F3))),
                                const SizedBox(width: 4),
                                Image.asset("${imageAssets}ic_right_arrow.png",
                                    height: 12, width: 12)
                              ])
                            ])),
                    const SizedBox(height: 10.0),
                    darkBorderButton(AppStrings.inviteFriend, () {
                      final width = MediaQuery.of(context).size.width / 1.5;
                      final height = MediaQuery.of(context).size.height / 1.2;
                      UiUtils.getInstance.showCustomDialog(
                          context,
                          Container(
                              width: width,
                              height: height,
                              color: AppColor.dividerColor,
                              child: InviteFriendDialog(_homeBloc.user!,
                                  InviteDialogEvent.ON_INVITE_CLICK)),
                          dismissible: true);
                    }, paddingHorizontal: 10.0),
                    const SizedBox(height: 10.0),
                    darkBorderButton(
                        AppStrings.gameHistory,
                        () => /*Smartlook.startRecording()*/
                            Navigator.of(context)
                                .pushNamed(Routes.GAME_HISTORY),
                        paddingHorizontal: 10.0),
                    const SizedBox(height: 10.0),
                    darkBorderButton(AppStrings.signOut, () {
                      AnalyticService.getInstance()
                          .trackEvents(Events.SIGN_OUT_CLICKED);
                      UiUtils.getInstance.alertDialog(
                          context,
                          AppStrings.signOut + "?",
                          AppStrings.signOutMessage, yesAction: () {
                        AnalyticService.getInstance()
                            .trackEvents(Events.USER_SIGNED_OUT);
                        signOut(context);
                      });
                    }, paddingHorizontal: 10.0),
                    SizedBox(height: 16.0)
                  ]))
        ]));
  }

  Widget _gameSelectionItem(GameInfoModel gameInfo, ESports game) {
    final isSelected = game == gameInfo.gameType;
    final size = isSelected ? 62.0 : 52.0;
    final color = isSelected ? AppColor.colorAccent : AppColor.dividerColor;
    final dashPattern = isSelected ? [3.0, 1.0] : [4.0, 3.0];
    return SelectableGameWidget(
      size: size,
      color: color,
      dashPattern: dashPattern,
      isSelected: isSelected,
      iconPath: gameInfo.iconPath,
    );
  }

  Widget _tierSection() => Column(children: [
        Row(children: [
          _getTierCard(
              _homeBloc.user?.getGameLevelFromMetaData(
                  GameTeamGroup.solo,
                  _homeBloc.user!.getCurrentGameProfile(
                      _homeBloc.applicationBloc.userCurrentGame)),
              GameTeamGroup.solo),
          const SizedBox(width: 4.0),
          _getTierCard(
              _homeBloc.user?.getGameLevelFromMetaData(
                  GameTeamGroup.duo,
                  _homeBloc.user!.getCurrentGameProfile(
                      _homeBloc.applicationBloc.userCurrentGame)),
              GameTeamGroup.duo),
          const SizedBox(width: 4.0),
          _getTierCard(
              _homeBloc.user?.getGameLevelFromMetaData(
                  GameTeamGroup.squad,
                  _homeBloc.user!.getCurrentGameProfile(
                      _homeBloc.applicationBloc.userCurrentGame)),
              GameTeamGroup.squad),
        ]),
        const SizedBox(height: 10.0),
        Padding(
            child: RegularText(AppStrings.tapToEditTier,
                fontSize: 10.0, fontStyle: FontStyle.italic),
            padding: const EdgeInsets.symmetric(vertical: 5.0))
      ]);

  Widget _getTierCard<T extends Enum>(T? level, GameTeamGroup group) =>
      Expanded(
          child: InkWell(
              onTap: () {
                if (level == null) {
                  _homeBloc.navigateToTab(group);
                } else {
                  final width = MediaQuery.of(context).size.width;
                  final height = MediaQuery.of(context).size.height;
                  UiUtils.getInstance.showCustomDialog(
                      context,
                      Container(
                          width: width,
                          height: height,
                          child: HomeTierSelectorDialog(
                              true, group, level, width, height, _homeBloc)),
                      dismissible: true);
                }
              },
              child: DecoratedBox(
                  decoration: BoxDecoration(
                      border:
                          Border.all(color: AppColor.dividerColor, width: 1.0),
                      borderRadius: BorderRadius.circular(3.0)),
                  child: Padding(
                      padding: const EdgeInsets.all(4.0),
                      child: Column(children: [
                        RegularText(group.name().capitalizeFirstCharacter(),
                            fontSize: 11.0),
                        const SizedBox(height: 4.0),
                        level != null
                            ? Image.network(getTierImage(level),
                                width: 28.0, height: 28.0)
                            : CircleAvatar(
                                radius: 14.0,
                                backgroundColor: AppColor.darkBackground),
                        const SizedBox(height: 4.0),
                        level != null
                            ? RegularText(getTierName(level),
                                maxLines: 1,
                                color: AppColor.lightWhiteColor,
                                overflow: TextOverflow.ellipsis,
                                fontSize: 11.0)
                            : RegularText(
                                "Unranked",
                                fontSize: 11.0,
                                color: AppColor.lightWhiteColor,
                              )
                      ])))));

  Widget getRightSidePane(BuildContext context, double height, double width,
          TabController tabController,
          {required FocusNode focusNode}) =>
      Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
        BlocBuilder<HomeBloc, HomeState>(
            builder: (ctx, state) {
              return _getTabBar(tabController, _homeBloc, width,
                  buttonFocusNode: focusNode);
            },
            buildWhen: (previous, current) =>
                current is MyTournamentLoaded ||
                current is GameTierInputState ||
                current is TournamentLoading,
            bloc: _homeBloc),
        const Divider(
            height: 1.0, thickness: 1.0, color: AppColor.dividerColor),
        Container(
            height: height,
            padding: const EdgeInsets.all(3.0),
            child: TabBarView(controller: tabController, children: [
              HomePageTopBoards(width, height, isHistory: true),
              HomePageTopBoards(width, height),
              MyBoards(
                  width, height, GameTeamGroup.squad, HomeScreenIndex.SQUAD),
              MyBoards(width, height, GameTeamGroup.solo, HomeScreenIndex.SOLO),
              MyBoards(width, height, GameTeamGroup.duo, HomeScreenIndex.DUO),
              UserSearchByPreference(_homeBloc)
            ]))
      ]);

  Widget _filterMenu(FocusNode focusNode) {
    return SizedBox.shrink();
    return MenuAnchor(
      menuChildren: [
        MenuItemButton(onPressed: () {
          _homeBloc.setSortOrder(TournamentSortOrder.head2Head);
        }, child: Text("Custom")),
        MenuItemButton(onPressed: () {
          _homeBloc.setSortOrder(TournamentSortOrder.classic);
        }, child: Text("Classic")),
      ],
      builder:
          (BuildContext context, MenuController controller, Widget? child) {
        return TextButton(
          focusNode: focusNode,
          onPressed: () {
            if (controller.isOpen) {
              controller.close();
            } else {
              controller.open();
            }
          },
          child: const Icon(Icons.sort),
        );
      },
    );
  }
}
