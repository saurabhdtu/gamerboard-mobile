import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_svg/svg.dart';

import '../../../../common/widgets/buttons.dart';
import '../../../../common/widgets/skeleton.dart';
import '../../../../common/widgets/text.dart';
import '../../../../resources/colors.dart';
import '../../../../resources/strings.dart';
import '../../../../utils/graphql_ext.dart';
import '../../../../utils/ui_utils.dart';
import '../../home_bloc.dart';
import '../../home_state.dart';
import '../tier_card.dart';
import 'find_game_tier_dialog.dart';

class HomeTierSelectorDialog extends StatefulWidget {
  final bool isEditMode;
  final GameTeamGroup group;
  final Enum? selectedLevel;
  final double width;
  final double height;
  final HomeBloc homeBloc;

  HomeTierSelectorDialog( this.isEditMode, this.group,
      this.selectedLevel, this.width, this.height,this.homeBloc);

  @override
  State<StatefulWidget> createState() => _HomeTierSelectorDialogState();
}

class _HomeTierSelectorDialogState extends State<HomeTierSelectorDialog> {
  late Map<Enum, String> selectionMapGame;
  MapEntry<Enum, String>? selected;
  int? indexSelected;

  @override
  void initState() {
    super.initState();


    selectionMapGame = widget.homeBloc.applicationBloc.userCurrentGame.getTierMap();
    if (widget.selectedLevel != null) {
      setState(() {
        var baseLevel =getBaseLevel(widget.selectedLevel);
        if(baseLevel != null)
        {
          indexSelected = selectionMapGame.keys
              .toList()
              .indexOf(baseLevel);
        }

        if(indexSelected != null && indexSelected! >= 0)
        {
          selected = MapEntry<Enum, String>(
              selectionMapGame.keys.elementAt(indexSelected!),
              selectionMapGame.values.elementAt(indexSelected!));
          widget.homeBloc.selectedTier = MapEntry(
              selectionMapGame.keys.elementAt(indexSelected!),
              selectionMapGame.values.elementAt(indexSelected!));
        }

      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return StatefulBuilder(builder: (context, StateSetter setDialogState) {
      return Stack(children: [
        Container(
            padding: const EdgeInsets.symmetric(horizontal: 24.0),
            decoration: BoxDecoration(color: AppColor.enableTournamentCard),
            child: Column(children: [
              if (!widget.isEditMode)
                Padding(
                    padding: const EdgeInsets.only(top: 24.0),
                    child: SvgPicture.asset("${imageAssets}ic_gb_logo.svg")),
              const SizedBox(height: 20.0),
              Row(mainAxisAlignment: MainAxisAlignment.spaceBetween, children: [
                BoldText(
                    widget.isEditMode
                        ? AppStrings.editTier +
                        widget.homeBloc.applicationBloc.userCurrentGame
                            .getShortName() +
                        " Tier"
                        : AppStrings.welcomeGamerboard,
                    fontSize: 18.0,
                    color: AppColor.textSubTitle),
                InkWell(
                    onTap: () {
                      Navigator.of(context).pop();
                    },
                    child: Padding(
                        padding: const EdgeInsets.symmetric(horizontal: 8),
                        child:
                        Icon(Icons.close, color: AppColor.whiteTextColor)))
              ]),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  RichText(
                      text: TextSpan(
                          text: AppStrings.selectTier,
                          style: RegularTextStyle(color: AppColor.textSubTitle),
                          children: [
                            TextSpan(
                                text:
                                " ${widget.homeBloc.applicationBloc.userCurrentGame.getShortName()} ${widget.homeBloc.teamGroupForIndex().name()} Tier",
                                style: BoldTextStyle(color: AppColor.textSubTitle))
                          ])),
                  InkWell(
                      onTap: () {
                        final width = MediaQuery.of(context).size.width / 1.8;
                        final height = MediaQuery.of(context).size.height / 1;
                        UiUtils.getInstance.showCustomDialog(
                            context,
                            Container(
                                width: width,
                                height: height,
                                child:
                                FindYourGameTier(width,widget.homeBloc)),
                            dismissible: true);
                      },
                      child: Padding(
                          padding: const EdgeInsets.all(10.0),
                          child: RegularText("How do I check?",
                              color: AppColor.textHighlighted,
                              fontSize: 12.0))),
                ],
              ),
              const SizedBox(height: 10.0),
              SingleChildScrollView(
                child: Container(
                    height: widget.height - 232,
                    width: widget.width,
                    child: ListView.separated(
                        itemBuilder: (context, index) => Row(
                            mainAxisAlignment: MainAxisAlignment.spaceAround,
                            children: getTierCards(
                                widget.width, index, indexSelected, (postion) {
                              setDialogState(() {
                                indexSelected = postion;
                                widget.homeBloc.selectedTier = MapEntry(
                                    selectionMapGame.keys.elementAt(postion),
                                    selectionMapGame.values.elementAt(postion));
                              });
                            }, true, widget.homeBloc)),
                        separatorBuilder: (context, index) => const SizedBox(
                          height: 8.0,
                        ),
                        itemCount: 2)),
              ),
              const SizedBox(
                height: 12,
              ),
              RegularText(AppStrings.tierWarning,
                  color: AppColor.grayText9E9E9E,
                  textAlign: TextAlign.center,
                  fontSize: 12.0),
              const SizedBox(
                height: 8,
              ),
              Container(
                  width: widget.width / 1.5,
                  child: primaryButton(AppStrings.submit, () {
                    if(selected != null)
                    {
                      if (selected?.key != widget.homeBloc.selectedTier!.key) {
                        widget.homeBloc.addGameLevel({
                          "level": widget.homeBloc.selectedTier!.key,
                          "group": widget.group
                        });
                      }
                    }
                    else{
                      widget.homeBloc.addGameLevel({
                        "level": widget.homeBloc.selectedTier!.key,
                        "group": widget.group
                      });
                    }
                  },
                      active: selected != null ?
                      selected?.key != widget.homeBloc.selectedTier!.key:true,
                      paddingVertical: 12,
                      paddingHorizontal: 16)),
              const SizedBox(height: 8)
            ])),
        Align(
            alignment: Alignment.topRight,
            child: BlocBuilder<HomeBloc, HomeState>(
                builder: (ctx, state) {
                  if (state is UpdatingBgmiLevel) {
                    if (state.showProgress) {
                      return Column(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Center(
                              child: appCircularProgressIndicator(),
                            )
                          ]);
                    } else {
                      if (widget.isEditMode) Navigator.of(context).pop();
                    }
                  }
                  return SizedBox(height: 20);
                },
                buildWhen: (p, c) => c is UpdatingBgmiLevel,
                bloc: widget.homeBloc))
      ]);
    });
  }
}
