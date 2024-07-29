import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_svg/svg.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/feature/home/home_bloc.dart';
import 'package:gamerboard/utils/graphql_ext.dart';

import '../../../common/widgets/text.dart';
import '../../../graphql/query.graphql.dart';
import '../../../resources/colors.dart';
import '../../../resources/strings.dart';
import '../../../utils/common_widgets.dart';
import '../../../utils/ui_utils.dart';
import '../home_state.dart';
class PreferenceSelectionFilter extends StatefulWidget {
  HomeBloc homeBloc;
  PreferenceSelectionFilter(this.homeBloc,{Key? key}) : super(key: key);

  @override
  State<PreferenceSelectionFilter> createState() => _PreferenceSelectionFilterState();
}


class _PreferenceSelectionFilterState extends State<PreferenceSelectionFilter> {


  @override
  Widget build(BuildContext context) {
    return    BlocBuilder<HomeBloc, HomeState>(
        builder: (ctx, state) {
          return Container(
            width: MediaQuery.of(context).size.width/2.5,
            child: Drawer(
              child: Container(
                  height: MediaQuery.of(context).size.height,
                  color: AppColor.dividerColor,
                  padding: EdgeInsets.symmetric(horizontal: 12),
                  child: Stack(
                    children: [
                      Padding(
                        padding: EdgeInsets.only(top: 12,right: 4),
                        child: Align(
                          alignment: Alignment.topRight,
                          child:  InkWell(
                            onTap: () {
                              Navigator.of(context).pop();
                            },
                            child: Padding(
                              padding: EdgeInsets.all(4),
                              child: SvgPicture.asset(
                                  "${imageAssets}ic_close.svg",
                                  height: 16,
                                  width: 16),
                            ),
                          ),
                        ),
                      ),
                      Container(
                        margin: EdgeInsets.only(bottom: 46),
                        child: SingleChildScrollView(
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.start,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment: MainAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: 16,
                                    ),


                                    BoldText(AppStrings.playingTimeOfDay,color: AppColor.whiteTextColor,fontSize: 12,),
                                    const SizedBox(height: 8,),
                                    ...TimeOfDayPreference.values.where((element) => element !=TimeOfDayPreference.artemisUnknown).map<Widget>((timeOfDay) => getTimeOfDayRadioTile( widget.homeBloc.searchTimeOfDay!, timeOfDay,
                                        timeOfDay.getDialogText())),
                                    const SizedBox(height: 16,),
                                    BoldText(AppStrings.playingTimeOfWeek,color: AppColor.whiteTextColor,fontSize: 12,),
                                    const SizedBox(height: 8,),
                                    ...TimeOfWeekPreference.values.where((element) => element !=TimeOfWeekPreference.artemisUnknown).map<Widget>((timeOfWeek) =>  getDayOfWeekRadioTile(widget.homeBloc.searchDayOfWeek!,timeOfWeek,
                                        timeOfWeek.getDialogText()))
                                  ],
                                ),
                                const SizedBox(
                                  width: 24,
                                ),
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    const SizedBox(
                                      height: 16,
                                    ),
                                    Row(
                                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                      children: [
                                        BoldText(AppStrings.roles,color: AppColor.whiteTextColor,fontSize: 12,),

                                      ],
                                    ),
                                    const SizedBox(height: 8,),

                                    ...RolePreference.values.where((element) => element !=RolePreference.artemisUnknown).map<Widget>((roles) =>   getRoleRadioTile(roles)).toList(),
                                    const SizedBox(height: 16,),

                                  ],
                                ),
                              ],
                            )

                        ),
                      ),
                      Align(
                          alignment: Alignment.bottomCenter,
                          child: Container(
                            height: 32,
                            margin: const EdgeInsets.only(bottom: 8),
                            child: Center(
                              child: Row(
                                mainAxisAlignment: MainAxisAlignment.end,
                                children: [
                                  InkWell(
                                    onTap: (){
                                      Navigator.of(context).pop();

                                    },
                                    child: BoldText(
                                      AppStrings.cancel,
                                      fontSize: 12,
                                      color: AppColor.whiteTextColor,
                                    ),
                                  ),
                                  const SizedBox(
                                    width: 72,
                                  ),
                                  InkWell(
                                      onTap: () async {
                                        AnalyticService.getInstance().trackEvents(Events.TOP_USER_SEARCH_TAP);
                                        widget.homeBloc.getAchievementSearchUsers(true);
                                        Navigator.of(context).pop();

                                      },
                                      child: Container(
                                          decoration: BoxDecoration(
                                            borderRadius:
                                            BorderRadius.circular(8),
                                            color: AppColor.buttonActive,
                                          ),
                                          child: Center(
                                              child: Text(AppStrings.apply,
                                                  style: SemiBoldTextStyle(
                                                      color: AppColor
                                                          .whiteTextColor,
                                                      fontSize: 14))),
                                          padding: const EdgeInsets.symmetric(
                                              vertical: 2, horizontal: 24))),
                                  const SizedBox(
                                    width: 48,
                                  ),

                                ],
                              ),
                            ),
                          )
                      )
                    ],
                  )


              ),
            ),
          );
        },
        buildWhen: (previous, current) =>
        current is ChangeUserFilterState,
        bloc: widget.homeBloc);



  }

  getTimeOfDayRadioTile(dynamic groupValue, dynamic optionValue, String radioValue) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 4),
      child: InkWell(
        onTap: (){
          if(  widget.homeBloc.searchTimeOfDay == optionValue)
            {
              widget.homeBloc.searchTimeOfDay = TimeOfDayPreference.artemisUnknown;
            }
          else{
            widget.homeBloc.searchTimeOfDay = optionValue;
          }
         widget.homeBloc.emit(ChangeUserFilterState(true));
        },
        child: Row(
          children: [
            gbCheckBoxButton( widget.homeBloc.searchTimeOfDay == optionValue, (value) {
              if(  widget.homeBloc.searchTimeOfDay == optionValue)
              {
                widget.homeBloc.searchTimeOfDay = TimeOfDayPreference.artemisUnknown;
              }
              else{
                widget.homeBloc.searchTimeOfDay = optionValue;
              }
              widget.homeBloc.emit(ChangeUserFilterState(true));
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

  getDayOfWeekRadioTile(dynamic groupValue, dynamic optionValue, String radioValue) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 4),
      child: InkWell(
        onTap: (){
          if(  widget.homeBloc.searchDayOfWeek == optionValue)
          {
            widget.homeBloc.searchDayOfWeek = TimeOfWeekPreference.artemisUnknown;
          }
          else{
            widget.homeBloc.searchDayOfWeek = optionValue;
          }
          widget.homeBloc.emit(ChangeUserFilterState(true));

        },
        child: Row(
          children: [
            gbCheckBoxButton( widget.homeBloc.searchDayOfWeek == optionValue, (value) {
              if(  widget.homeBloc.searchDayOfWeek == optionValue)
              {
                widget.homeBloc.searchDayOfWeek = TimeOfWeekPreference.artemisUnknown;
              }
              else{
                widget.homeBloc.searchDayOfWeek = optionValue;
              }
              widget.homeBloc.emit(ChangeUserFilterState(true));

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


  getRoleRadioTile(RolePreference value) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 4),
      child: InkWell(
        onTap: (){
          if(widget.homeBloc.searchUserRole == value)
          {
            widget.homeBloc.searchUserRole = RolePreference.artemisUnknown;
          }
          else{
            widget.homeBloc.searchUserRole = value;
          }
          widget.homeBloc.emit(ChangeUserFilterState(true));

        },
        child: Row(
          children: [
            gbCheckBoxButton( widget.homeBloc.searchUserRole == value, (ans) {
              if(widget.homeBloc.searchUserRole == value)
              {
                widget.homeBloc.searchUserRole = RolePreference.artemisUnknown;
              }
              else{
                widget.homeBloc.searchUserRole = value;
              }
              widget.homeBloc.emit(ChangeUserFilterState(true));
            }),
            const SizedBox(
              width: 8,
            ),
            RegularText(
              value.getDialogText(),
              color: Colors.white,
              fontSize: 12,
            )
          ],
        ),
      ),
    );
  }
}