import 'package:dotted_border/dotted_border.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/feature/home/model/game_info_model.dart';
import 'package:gamerboard/feature/home/widgets/selectable_game_widget.dart';
import 'package:gamerboard/utils/graphql_ext.dart';

import '../../../../common/constants.dart';
import '../../../../common/widgets/buttons.dart';
import '../../../../common/widgets/text.dart';
import '../../../../graphql/query.graphql.dart';
import '../../../../resources/colors.dart';
import '../../../../resources/strings.dart';
import '../../../../common/services/analytics/analytic_utils.dart';
import '../../home_bloc.dart';
import '../../home_state.dart';

class SelectGame extends StatefulWidget {
  final Function onButtonClick;
  final HomeBloc homeBloc;

  SelectGame (this.onButtonClick,this.homeBloc);

  @override
  State<SelectGame> createState() => _SelectGameState();
}

class _SelectGameState extends State<SelectGame> {
  ESports? selectedGame;

  @override
  void initState() {
    super.initState();
    AnalyticService.getInstance().trackEvents(Events.ESPORTS_SWITCH_CLICKED);
    selectedGame = widget.homeBloc.applicationBloc.userCurrentGame;

  }

  @override
  Widget build(BuildContext context) {
    return BlocListener<HomeBloc, HomeState>(
        listener: (context, state) {
          if (state is GameSelection) {
            setState(() {
              selectedGame = state.selectedGame;
            });
          }
        },
        listenWhen: (p, c) => c is GameSelection,
        child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 52.0, vertical: 12),
            decoration: BoxDecoration(color: Colors.black),
            child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      BoldText(AppStrings.selectGame,
                          fontSize: 22.0, color: AppColor.textSubTitle),
                      InkWell(
                        onTap: (){
                          Navigator.of(context).pop();
                        },
                        child: Padding(
                            padding: EdgeInsets.all(12),
                            child: Icon(Icons.close,color: AppColor.textSubTitle,)),
                      )
                    ],
                  ),

                  const SizedBox(height: 32),
                  Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: GameInfoModel.allTypes.map((gameInfo){
                        final isSelected = selectedGame == gameInfo.gameType;
                        final color = isSelected ? AppColor.colorAccent : AppColor.dividerColor;
                        final dashPattern = isSelected ? [3.0, 1.0] : [4.0, 3.0];
                        return InkWell(
                            onTap: () {
                              setState(() {
                                selectedGame = gameInfo.gameType;
                              });
                            },
                            child: Column(
                                crossAxisAlignment: CrossAxisAlignment.center,
                                children: [
                                  GameSelectionIcon(
                                    color : color,
                                    size : 82,
                                    dashPattern : dashPattern,
                                    isSelected : isSelected, iconPath: gameInfo.iconPath,
                                  ),
                                  const SizedBox(height: 12),
                                  RegularText(gameInfo.gameType.getGameName())
                                ]));
                      }).toList()),
                  const SizedBox(height: 18),
                  secondaryButton(AppStrings.confirm, () async {
                    Constants.PLATFORM_CHANNEL.invokeMethod("setup_current_game", {"game_name": selectedGame!.getShortName()});
                    AnalyticService.getInstance().trackEvents(Events.ESPORTS_SELECTED,properties: {
                      "selected_game":selectedGame!.getShortName()
                    });
                    widget.homeBloc.changeSelectedGames(selectedGame!);
                    widget.onButtonClick();
                  }, active: selectedGame != null)
                ])),
        bloc: widget.homeBloc);
  }
}