import 'package:flutter/material.dart';

import '../../../../common/widgets/text.dart';
import '../../../../graphql/query.graphql.dart';
import '../../../../resources/colors.dart';
import '../../../../resources/strings.dart';
import '../../../../common/services/analytics/analytic_utils.dart';
import '../../home_bloc.dart';
import '../../home_widgets.dart';

class SquadHelperGuide extends StatefulWidget {
  double width;
  HomeBloc homeBloc;
  UserTournamentMixin tournament;
  Function onPlayClick;


  SquadHelperGuide(this.width, this.homeBloc, this.tournament,this.onPlayClick);

  @override
  State<SquadHelperGuide> createState() => _SquadHelperGuideState();
}

class _SquadHelperGuideState extends State<SquadHelperGuide> {
  @override
  void initState() {
    // TODO: implement initState
    AnalyticService.getInstance().trackEvents(Events.SHOW_HELPER_TEXT_SQUAD, properties: {
      "tournament": widget.tournament.tournament.id,
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    var members = widget.tournament.squad?.members ?? [];
    return Stack(children: [
      Container(
          padding: const EdgeInsets.symmetric(horizontal: 0.0, vertical: 8),
          height: 316,
          width: widget.width,
          decoration: BoxDecoration(gradient: AppColor.popupBackgroundGradient),
          child: Column(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Column(
                  children: [
                    Padding(
                      padding: EdgeInsets.only(right: 12, top: 12),
                      child: InkWell(
                          onTap: () {
                            Navigator.of(context).pop();
                          },
                          child: Align(
                              alignment: Alignment.topRight,
                              child: Icon(Icons.close, color: Colors.white))),
                    ),
                    Container(
                        padding: const EdgeInsets.symmetric(horizontal: 38.0),
                        child: Column(children: [
                          BoldText(AppStrings.squadTeamGuidelines,
                              fontSize: 24.0, color: AppColor.textSubTitle),
                          const SizedBox(height: 8.0),
                          Text(AppStrings.squadGuidelines,
                              textAlign: TextAlign.center,
                              style: RegularTextStyle(
                                  color: AppColor.textSubTitle)),
                          const SizedBox(height: 12.0),
                          Padding(
                            padding: EdgeInsets.symmetric(horizontal: 18),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.start,
                              children: [
                                Column(
                                    mainAxisAlignment: MainAxisAlignment.start,
                                    crossAxisAlignment:
                                    CrossAxisAlignment.start,
                                    children: [
                                      SizedBox(
                                        height: 4,
                                      ),
                                      SizedBox(
                                        width: widget.width - 112,
                                        child: memberRow(context, members.elementAtOrNull(0)?.user, members.elementAtOrNull(1)?.user),
                                      ),
                                      SizedBox(
                                        height: 8,
                                      ),
                                      members.length >=
                                          3
                                          ? SizedBox(
                                        width: widget.width - 112,
                                        child: memberRow(context, members.elementAtOrNull(2)?.user, members.elementAtOrNull(3)?.user),
                                      )
                                          : SizedBox.shrink(),
                                    ])
                              ],
                            ),
                          )
                        ])),
                  ],
                ),
                Container(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 38.0, vertical: 8),
                    margin: EdgeInsets.only(top: 22),
                    child: Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          InkWell(
                              onTap: () {
                                widget.homeBloc.startService(context);
                                Navigator.of(context).pop();
                              },
                              child: Container(
                                  width: widget.width / 2,
                                  child: Center(
                                      child: Text(AppStrings.play,
                                          style: TextStyle(
                                              color: AppColor.whiteTextColor,
                                              fontSize: 16))),
                                  color: AppColor.buttonActive,
                                  padding:
                                  const EdgeInsets.symmetric(vertical: 8)))
                        ])),
                const Spacer()
              ]))
    ]);
  }
}

Widget memberRow(BuildContext context, SquadMemberMixin$User? user1, SquadMemberMixin$User? user2){
  return Row(
    mainAxisAlignment: user2 != null ? MainAxisAlignment.spaceEvenly :MainAxisAlignment.center ,
    crossAxisAlignment:
    CrossAxisAlignment.start,
    children: [
      squadUserWidget(context,user1),
      squadUserWidget(context,user2)
    ],
  );
}

squadUserWidget(BuildContext context,SquadMemberMixin$User? user) {
  if(user == null ){
    return SizedBox.shrink();
  }
  return Container(
    child: Row(
      children: [
        if(user.image != null)
          Image.network(user.image!, width: 36.0, height: 36.0),
        SizedBox(
          width: 6,
        ),
        RegularText(
          user.name,
          color: AppColor.whiteTextColor,
          fontSize: 14,
        )
      ],
    ),
  );
}