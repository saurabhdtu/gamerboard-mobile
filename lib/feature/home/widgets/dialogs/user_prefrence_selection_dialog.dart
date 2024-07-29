import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_svg/svg.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/home/home_bloc.dart';
import 'package:gamerboard/feature/home/home_state.dart';
import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/ui_utils.dart';

import '../../../../utils/common_widgets.dart';



class UserPreferenceSelectionDialog extends StatefulWidget {
  final HomeBloc homeBloc;

  UserPreferenceSelectionDialog(this.homeBloc);

  @override
  State<UserPreferenceSelectionDialog> createState() =>
      _UserPreferenceSelectionDialogState();
}

class _UserPreferenceSelectionDialogState
    extends State<UserPreferenceSelectionDialog> {
  ESports? selectedGame;
  List ansList = [
    TimeOfDayPreference.morning,
    TimeOfWeekPreference.weekdays,
    [RolePreference.assaulter],
    PlayingReasonPreference.fun
  ];
  var currentUserPosition = 0;

  @override
  void initState() {
    super.initState();
    AnalyticService.getInstance().trackEvents(Events.USER_PREFERENCE_DIALOG_OPEN);
  }

  Widget timeOfDayStep(String title, String description, [ansList]) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _title(title),
        const SizedBox(height: 0),
        _description(description),
        ...TimeOfDayPreference.values
            .where((element) => element != TimeOfDayPreference.artemisUnknown)
            .map((e) {
          return Padding(
              padding: const EdgeInsets.only(top: 12),
              child: getRadioTile(
                ansList,
                e,
                e.getDialogText(),
              ));
        }).toList(),
      ],
    );
  }

  Widget weekOfDayStep(String title, String description, [ansList]) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _title(title),
        const SizedBox(height: 0),
        _description(description),
        ...TimeOfWeekPreference.values
            .where((element) => element != TimeOfWeekPreference.artemisUnknown)
            .map((e) {
          return Padding(
              padding: const EdgeInsets.only(top: 12),
              child: getRadioTile(
                ansList,
                e,
                e.getDialogText(),
              ));
        }).toList(),
      ],
    );
  }

  Widget step3(String title, String description) {
    var roleList = RolePreference.values
        .where((element) => element != RolePreference.artemisUnknown)
        .toList();
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _title(title),
        const SizedBox(height: 0),
        _description(description),
        const SizedBox(
          height: 8,
        ),
        GridView.builder(
            shrinkWrap: true,
            physics: const NeverScrollableScrollPhysics(),
            gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                crossAxisCount: 2, childAspectRatio: 5),
            padding: const EdgeInsets.only(top: 8),
            itemBuilder: (context, index) {
              return onUserRoleCheckbox(roleList[index]);
            },
            itemCount: roleList.length),
      ],
    );
  }

  Text _title(String title) {
    return Text(
      title,
      style:
          SemiBoldTextStyle(color: Colors.white.withOpacity(0.8), fontSize: 14),
    );
  }

  Widget playingReasonStep(String title, String description, [ansList]) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        _title(title),
        const SizedBox(height: 0),
        _description(description),
        ...PlayingReasonPreference.values
            .where(
                (element) => element != PlayingReasonPreference.artemisUnknown)
            .map((e) {
          return Padding(
              padding: const EdgeInsets.only(top: 12),
              child: getRadioTile(
                ansList,
                e,
                e.getDialogText(),
              ));
        }).toList(),
      ],
    );
  }

  Text _description(String description) {
    return Text(
      description,
      style:
          RegularTextStyle(color: Colors.white.withOpacity(0.8), fontSize: 8),
    );
  }

  @override
  Widget build(BuildContext context) {
    return BlocListener<HomeBloc, HomeState>(
        listener: (context, state) {
          if (state is UserPreferenceDialogState) {
            setState(() {
              currentUserPosition= state.currentUserPosition;
            });
          }
        },
        listenWhen: (p, c) => c is UserPreferenceDialogState,
        child: SingleChildScrollView(
          child: Container(
              padding:
                  const EdgeInsets.symmetric(horizontal: 16.0, vertical: 8),
              decoration: BoxDecoration(color: AppColor.dividerColor),
              child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      mainAxisAlignment: currentUserPosition == 4
                          ? MainAxisAlignment.end
                          : MainAxisAlignment.spaceBetween,
                      children: [
                        const SizedBox(
                          width: 28,
                        ),
                        currentUserPosition == 4
                            ? const SizedBox.shrink()
                            : Row(
                                children: List.generate(4, (index) {
                                  return Container(
                                    height: 8,
                                    width: 8,
                                    margin: const EdgeInsets.only(right: 4),
                                    decoration: BoxDecoration(
                                      borderRadius: BorderRadius.circular(360),
                                      color: currentUserPosition == index
                                          ? AppColor.buttonActive
                                          : AppColor.gray767677,
                                    ),
                                  );
                                }),
                              ),
                        InkWell(
                          onTap: () {
                            Navigator.of(context).pop();
                          },
                          child: Container(
                            padding: EdgeInsets.all(4),
                            child: SvgPicture.asset(
                                "${imageAssets}ic_close.svg",
                                height: 20,
                                width: 20),
                          ),
                        )
                      ],
                    ),
                    _getActionWidget(currentUserPosition),
                  ])),
        ),
        bloc: widget.homeBloc);
  }

  getUserRoleRow(RolePreference rolePreference) {
    return Padding(
      padding: EdgeInsets.only(right: 62),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          InkWell(
            onTap: () {
              onRolesSelected(rolePreference);
            },
            child: Padding(
              padding: EdgeInsets.symmetric(vertical: 4),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.start,
                children: [
                  gbCheckBoxButton(
                      ansList[2].contains(rolePreference), (value) {}),
                  SizedBox(
                    width: 8,
                  ),
                  RegularText(
                    rolePreference.getDialogText(),
                    color: Colors.white,
                    fontSize: 12,
                  )
                ],
              ),
            ),
          ),
          SizedBox(
            width: 118,
            child: InkWell(
              onTap: () {
                onRolesSelected(RolePreference.coach);
              },
              child: Padding(
                padding: EdgeInsets.symmetric(vertical: 4),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                  gbCheckBoxButton(
                        ansList[2].contains(RolePreference.coach), (value) {}),
                    SizedBox(
                      width: 8,
                    ),
                    RegularText(
                      RolePreference.coach.getDialogText(),
                      color: Colors.white,
                      fontSize: 12,
                    )
                  ],
                ),
              ),
            ),
          )
        ],
      ),
    );
  }

  onUserRoleCheckbox(RolePreference rolePreference) {
    return InkWell(
      onTap: () {
        onRolesSelected(rolePreference);
      },
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 4),
        child: Row(
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            gbCheckBoxButton(ansList[2].contains(rolePreference), (value) {
              onRolesSelected(rolePreference);
            }),
            const SizedBox(
              width: 8,
            ),
            RegularText(
              rolePreference.getDialogText(),
              color: Colors.white,
              fontSize: 12,
            )
          ],
        ),
      ),
    );
  }

  _getActionWidget(int currentPosition) {
    switch (currentPosition) {
      case 4:
        return Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                SvgPicture.asset("${imageAssets}ic_save_user_prefrence.svg",
                    height: 60, width: 60),
              ],
            ),
            const SizedBox(
              height: 24,
            ),
            BoldText(
              AppStrings.yourProfileIsReady,
              fontSize: 18,
              color: AppColor.whiteTextColor,
            ),
            const SizedBox(
              height: 8,
            ),
            RegularText(
                AppStrings.youHaveCompletedYourProfile,
                textAlign: TextAlign.center,
                color: AppColor.whiteTextColor.withOpacity(0.8),
                fontSize: 14
            ),
            const SizedBox(
              height: 16,
            ),
            InkWell(
                onTap: () {
                  Navigator.pop(context);
                },
                child: Container(
                  width: 156,
                  height: 32,
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(8),
                    color: AppColor.buttonActive,
                  ),
                  child: Center(
                      child: Text(AppStrings.done,
                          style: SemiBoldTextStyle(
                              color: AppColor.whiteTextColor, fontSize: 14))),
                )),
            const SizedBox(
              height: 8,
            ),
          ],
        );
      case 1:
        return Column(
          children: [
            _completeYourProfileTitle(),
            const SizedBox(height: 16),
            weekOfDayStep(AppStrings.whatTimeYouPlayInWeek,
                AppStrings.youCanChooseOneOption, ansList[1]),
            const SizedBox(
              height: 12,
            ),
            _actionButtons(() => Future.value(true)),
          ],
        );
      case 2:
        return Column(
          children: [
            _completeYourProfileTitle(),
            const SizedBox(height: 16),
            step3(AppStrings.whatIsPreferredRole, AppStrings.notMoreThanTwo),
            const SizedBox(
              height: 12,
            ),
            _actionButtons(() async{
              if (ansList[2].length == 0) {
                UiUtils.getInstance.showToast(
                    AppStrings.selectAtLeastOnRole);
                return false;
              }
              return true;
            }),
          ],
        );
      case 3:
        return Column(
          children: [
            _completeYourProfileTitle(),
            const SizedBox(height: 16),
            playingReasonStep(AppStrings.whyDoYouPlay, AppStrings.youCanChooseOneOption,
                ansList[3]),
            const SizedBox(
              height: 12,
            ),
            _actionButtons(() async {
              bool result = await _submitPreference();
              return result;
            }),
          ],
        );
      case 0:
        return Column(
          children: [
            _completeYourProfileTitle(),
            const SizedBox(height: 16),
            timeOfDayStep(AppStrings.whenDoYouPlay, AppStrings.youCanChooseOneOption,
                ansList[0]),
            const SizedBox(
              height: 12,
            ),
            _actionButtons(() => Future.value(true))
          ],
        );
    }
  }

  Future<bool> _submitPreference() async {
    PreferencesInput preferenceInput = PreferencesInput(
      timeOfDay: [ansList[0]],
      timeOfWeek: ansList[1],
      roles: ansList[2],
      playingReason: [ansList[3]],
    );
    var result = await widget.homeBloc
        .submitUserPreference(context, preferenceInput);
    return result;
  }

  Container _actionButtons(Future<bool> Function() onNext) {
    return Container(
        child: Row(mainAxisAlignment: MainAxisAlignment.end, children: [
      InkWell(
          onTap: () {
              if (currentUserPosition > 0) {
                var index = currentUserPosition-1;

                widget.homeBloc.emit(UserPreferenceDialogState(index));
              } else {
                AnalyticService.getInstance()
                    .trackEvents(Events.USER_PREFERENCE_DIALOG_CANCEL);
                Navigator.pop(context);
              }
          },
          child: Container(
              child: Center(
                  child: Text(currentUserPosition > 0 ? AppStrings.back : AppStrings.cancel,
                      style: SemiBoldTextStyle(
                          color: AppColor.whiteTextColor, fontSize: 14))),
              padding:
                  const EdgeInsets.symmetric(vertical: 6, horizontal: 38))),
      InkWell(
          onTap: () async {
            var success = await onNext();
            var index = currentUserPosition+1;
            if (success)  widget.homeBloc.emit(UserPreferenceDialogState(index));

          },
          child: Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(8),
                color: AppColor.buttonActive,
              ),
              child: Center(
                  child: Text(AppStrings.next,
                      style: SemiBoldTextStyle(
                          color: AppColor.whiteTextColor, fontSize: 14))),
              padding: const EdgeInsets.symmetric(vertical: 6, horizontal: 38)))
    ]));
  }

  Row _completeYourProfileTitle() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Text.rich(
          TextSpan(
              text: AppStrings.completeYourProfile,
              style: SemiBoldTextStyle(fontSize: 14, color: Colors.white),
              children: <InlineSpan>[
                TextSpan(
                  text: AppStrings.newTeam,
                  style: SemiBoldTextStyle(
                      fontSize: 14, color: AppColor.purplleA78DF2),
                )
              ]),
          textAlign: TextAlign.center,
        ),
      ],
    );
  }

  onRolesSelected(RolePreference rolePreference) {
    if (!ansList[2].contains(rolePreference)) {
      if (ansList[2].length <= 1) {
        ansList[2].add(rolePreference);
      } else {
        UiUtils.getInstance.showToast(AppStrings.youCannotSelectMoreThanTwo);
      }
    } else {
      ansList[2].remove(rolePreference);
    }
    setState(() {});
  }

  getRadioTile(dynamic groupValue, dynamic optionValue, String radioValue) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 4),
      child: InkWell(
        onTap: () {
          setState(() {
            ansList[currentUserPosition] = optionValue;
          });
        },
        child: Row(
          children: [
           gbRadioButton(groupValue, optionValue, (value) {
              setState(() {
                ansList[currentUserPosition] = optionValue;
              });
            }),
            const SizedBox(
              width: 8,
            ),
            RegularText(
              radioValue,
              color: Colors.white,
              fontSize: 12,
            )
          ],
        ),
      ),
    );
  }
}
