import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_svg/svg.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/feature/home/home_state.dart';
import 'package:url_launcher/url_launcher.dart';

import '../../../common/widgets/skeleton.dart';
import '../../../common/widgets/text.dart';
import '../../../graphql/query.graphql.dart';
import '../../../resources/colors.dart';
import '../../../resources/strings.dart';
import '../../../utils/graphql_ext.dart';
import '../home_bloc.dart';

class UserSearchByPreference extends StatefulWidget {
  HomeBloc homeBloc;

  UserSearchByPreference(this.homeBloc, {Key? key}) : super(key: key);

  @override
  State<UserSearchByPreference> createState() => _UserSearchByPreferenceState();
}

class _UserSearchByPreferenceState extends State<UserSearchByPreference> {
  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    SchedulerBinding.instance.addPostFrameCallback((_) {
      AnalyticService.getInstance().trackEvents(Events.VIEW_TOP_GAMER_TAB);
      widget.homeBloc.getAchievementSearchUsers(false);
    });
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 14),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  AppStrings.findPlayerToPlayNextMatch,
                  style: SemiBoldTextStyle(
                      fontSize: 16, color: AppColor.whiteTextColor),
                ),
                Row(
                  children: [
                    InkWell(
                      onTap: () {
                        AnalyticService.getInstance()
                            .trackEvents(Events.TOP_USER_SEARCH_APPLY);

                        widget.homeBloc
                            .emit(ShowEndDrawerForUserPreferenceState(true));
                      },
                      child: Container(
                        color: AppColor.buttonGrayBg,
                        padding:
                            EdgeInsets.symmetric(horizontal: 8, vertical: 8),
                        child: SvgPicture.asset(
                          "${imageAssets}ic_search.svg",
                          height: 14,
                          width: 14,
                        ),
                      ),
                    ),
                  ],
                )
              ],
            ),
            BlocBuilder(
                builder: (context, state) {
                  if (state is UserPreferenceListState)
                    return state.userList.isEmpty
                        ? Center(
                            child: RegularText(AppStrings.noUserFound,
                                fontSize: 30.0, color: AppColor.dividerColor))
                        : Expanded(
                            child: ListView.builder(
                                itemCount: state.userList.length,
                                itemBuilder: (context, index) {
                                  var user = state.userList[index];
                                  user!.achievements.tournamentSummary.sort((a,
                                          b) =>
                                      a!.topTenCount.compareTo(b!.topTenCount));
                                  var kdForClassic = user!
                                              .achievements.tournamentSummary
                                              .where((element) =>
                                                  element!.matchType ==
                                                  MatchType.classic) ==
                                          null
                                      ? user.achievements.tournamentSummary
                                          .where((element) =>
                                              element!.matchType ==
                                              MatchType.classic)!
                                          .first!
                                          .kDRatio
                                          .toString()
                                      : "0";
                                  var kdForCustom = user!
                                              .achievements.tournamentSummary
                                              .where((element) =>
                                                  element!.matchType ==
                                                  MatchType.classic) ==
                                          null
                                      ? user.achievements.tournamentSummary
                                          .where((element) =>
                                              element!.matchType ==
                                              MatchType.classic)!
                                          .first!
                                          .kDRatio
                                          .toString()
                                      : "0";
                                  var userRank =
                                      user.achievements.bestPerformance == null
                                          ? 0
                                          : user.achievements.bestPerformance!
                                              .rank;
                                  var tournamentName =
                                      user.achievements.bestPerformance == null
                                          ? "NA"
                                          : user.achievements.bestPerformance!
                                              .tournament.name;
                                  ;
                                  var top10CountForClassic = user!.achievements
                                          .tournamentSummary.isEmpty
                                      ? 0
                                      : user!.achievements.tournamentSummary
                                          .last!.topTenCount;
                                  var totalPlayForClassic = user!.achievements
                                          .tournamentSummary.isEmpty
                                      ? 0
                                      : user!.achievements.tournamentSummary
                                          .last!.played;
                                  var maxTier = user!.achievements
                                              .tournamentSummary.isEmpty ||
                                          user!.achievements.tournamentSummary
                                                  .last!.maxTier ==
                                              null
                                      ? null
                                      : getStringToTier(user!.achievements
                                          .tournamentSummary.last!.maxTier!);
                                  var group = user!.achievements
                                              .tournamentSummary.isEmpty ||
                                          user!.achievements.tournamentSummary
                                                  .last!.maxTier ==
                                              null
                                      ? null
                                      : user!.achievements.tournamentSummary
                                          .last!.group;

                                  return Container(
                                    margin: EdgeInsets.symmetric(vertical: 6),
                                    padding: EdgeInsets.all(12),
                                    color: AppColor.enableTournamentCard,
                                    child: Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      crossAxisAlignment:
                                          CrossAxisAlignment.start,
                                      children: [
                                        Expanded(
                                            child: Column(
                                          mainAxisAlignment:
                                              MainAxisAlignment.spaceBetween,
                                          children: [
                                            Column(
                                              crossAxisAlignment:
                                                  CrossAxisAlignment.start,
                                              children: [
                                                Row(
                                                  children: [
                                                    Container(
                                                      height: 26,
                                                      width: 26,
                                                      decoration: BoxDecoration(
                                                          image:
                                                              DecorationImage(
                                                                  image:
                                                                      NetworkImage(
                                                                    user!.image ??
                                                                        "",
                                                                  ),
                                                                  fit: BoxFit
                                                                      .fill)),
                                                    ),
                                                    const SizedBox(
                                                      width: 12,
                                                    ),
                                                    Column(
                                                      crossAxisAlignment:
                                                          CrossAxisAlignment
                                                              .start,
                                                      children: [
                                                        Text(user!.name ?? "",
                                                            style: BoldTextStyle(
                                                                color: AppColor
                                                                    .whiteTextColor,
                                                                fontSize: 12)),
                                                        const SizedBox(
                                                          height: 4,
                                                        ),
                                                        Text.rich(
                                                          TextSpan(
                                                            children: [
                                                              TextSpan(
                                                                text: AppStrings
                                                                    .bgmiUid,
                                                                style: TextStyle(
                                                                    color: AppColor
                                                                        .whiteTextColor,
                                                                    fontSize:
                                                                        8),
                                                              ),
                                                              TextSpan(
                                                                text: user!
                                                                    .profiles!
                                                                    .where((element) =>
                                                                element.eSport ==
                                                                    ESports.bgmi)
                                                                    .isEmpty
                                                                    ? ""
                                                                    :user!
                                                                    .profiles!
                                                                    .where((element) =>
                                                                element.eSport ==
                                                                    ESports.bgmi)
                                                                        .first
                                                                        .profileId,
                                                                style: SemiBoldTextStyle(
                                                                    color: AppColor
                                                                        .whiteTextColor,
                                                                    fontSize:
                                                                        8),
                                                              ),
                                                            ],
                                                          ),
                                                        ),
                                                      ],
                                                    )
                                                  ],
                                                ),
                                                const SizedBox(
                                                  height: 12,
                                                ),
                                                Row(
                                                  mainAxisAlignment:
                                                      MainAxisAlignment.start,
                                                  children: [
                                                    SvgPicture.asset(
                                                      "${imageAssets}ic_star.svg",
                                                      height: 14,
                                                      width: 14,
                                                    ),
                                                    const SizedBox(
                                                      width: 8,
                                                    ),
                                                    Text.rich(
                                                      TextSpan(
                                                        children: [
                                                          TextSpan(
                                                            text:
                                                                AppStrings.role,
                                                            style: TextStyle(
                                                                color: AppColor
                                                                    .whiteTextColor,
                                                                fontSize: 12),
                                                          ),
                                                          TextSpan(
                                                            text: user!.preferences!
                                                                        .roles ==
                                                                    null
                                                                ? ""
                                                                : user!
                                                                    .preferences!
                                                                    .roles!
                                                                    .map((e) =>
                                                                        e.getDialogText())
                                                                    .toList()
                                                                    .join(","),
                                                            style: BoldTextStyle(
                                                                color: AppColor
                                                                    .whiteTextColor,
                                                                fontSize: 12),
                                                          ),
                                                        ],
                                                      ),
                                                    ),
                                                  ],
                                                ),
                                                const SizedBox(
                                                  height: 76,
                                                ),
                                              ],
                                            ),
                                            Visibility(
                                              visible:true,
                                              child: InkWell(
                                                onTap: () async {


                                                  if (user.phone != null) {
                                                    AnalyticService.getInstance()
                                                        .trackEvents(Events
                                                        .TOP_USER_WHATSAPP_CLICK,properties: {
                                                          "isNumberAvailable":true
                                                    });
                                                    var whatsapp = user.phone;
                                                    var whatsappAndroid = Uri.parse(
                                                        "whatsapp://send?phone=$whatsapp&text=hello");
                                                    if (await canLaunchUrl(
                                                        whatsappAndroid)) {
                                                      await launchUrl(
                                                          whatsappAndroid);
                                                    } else {
                                                      ScaffoldMessenger.of(
                                                              context)
                                                          .showSnackBar(
                                                        const SnackBar(
                                                          content: Text(
                                                              AppStrings.whatsappNotAvailable),
                                                        ),
                                                      );
                                                    }
                                                  }
                                                  else{
                                                    AnalyticService.getInstance()
                                                        .trackEvents(Events
                                                        .TOP_USER_WHATSAPP_CLICK,properties: {
                                                      "isNumberAvailable":false
                                                    });
                                                  }
                                                },
                                                child: Row(
                                                  children: [
                                                    SvgPicture.asset(
                                                      "${imageAssets}ic_whatsapp_vector.svg",
                                                      height: 14,
                                                      width: 14,
                                                    ),
                                                    SizedBox(
                                                      width: 4,
                                                    ),
                                                    BoldText(
                                                      AppStrings
                                                          .sayHiToOnWhatsapp,
                                                      color: AppColor
                                                          .whatsAppGreenColor,
                                                      fontSize: 12,
                                                    )
                                                  ],
                                                ),
                                              ),
                                            )
                                          ],
                                        )),
                                        Expanded(
                                            child: Column(
                                          crossAxisAlignment:
                                              CrossAxisAlignment.start,
                                          children: [
                                            Container(
                                              padding:
                                                  EdgeInsets.only(right: 24),
                                              child: Row(
                                                mainAxisAlignment:
                                                    MainAxisAlignment
                                                        .spaceBetween,
                                                children: [
                                                  Column(
                                                    children: [
                                                      Row(
                                                        mainAxisAlignment:
                                                            MainAxisAlignment
                                                                .start,
                                                        children: [
                                                          Column(
                                                            crossAxisAlignment:
                                                                CrossAxisAlignment
                                                                    .start,
                                                            children: [
                                                              Row(
                                                                children: [
                                                                  Column(
                                                                    crossAxisAlignment:
                                                                        CrossAxisAlignment
                                                                            .start,
                                                                    children: [
                                                                      BoldText(
                                                                        AppStrings
                                                                            .kdRatio,
                                                                        fontSize:
                                                                            12,
                                                                        color: AppColor
                                                                            .whiteF5F5F5,
                                                                      ),
                                                                      const SizedBox(
                                                                        height:
                                                                            2,
                                                                      ),
                                                                      Text(
                                                                        AppStrings
                                                                            .last50Games,
                                                                        style: TextStyle(
                                                                            color:
                                                                                AppColor.grayA0A0A0,
                                                                            fontSize: 8),
                                                                      ),
                                                                    ],
                                                                  ),
                                                                  const SizedBox(
                                                                    width: 4,
                                                                  ),
                                                                  BoldText(
                                                                    kdForClassic,
                                                                    fontSize:
                                                                        22,
                                                                    color: AppColor
                                                                        .whiteTextColor,
                                                                  )
                                                                ],
                                                              ),
                                                              const SizedBox(
                                                                height: 5,
                                                              ),
                                                              Container(
                                                                decoration: BoxDecoration(
                                                                    borderRadius:
                                                                        BorderRadius
                                                                            .circular(
                                                                                4),
                                                                    color: AppColor
                                                                        .buttonGrayBg),
                                                                padding: EdgeInsets
                                                                    .symmetric(
                                                                        horizontal:
                                                                            4,
                                                                        vertical:
                                                                            4),
                                                                child: Text(
                                                                  AppStrings
                                                                      .classic,
                                                                  style: TextStyle(
                                                                      color: AppColor
                                                                          .gray9f9f9f,
                                                                      fontSize:
                                                                          6),
                                                                ),
                                                              )
                                                            ],
                                                          )
                                                        ],
                                                      ),
                                                      const SizedBox(
                                                        height: 8,
                                                      ),
                                                      Row(
                                                        mainAxisAlignment:
                                                            MainAxisAlignment
                                                                .start,
                                                        children: [
                                                          Column(
                                                            crossAxisAlignment:
                                                                CrossAxisAlignment
                                                                    .start,
                                                            children: [
                                                              Row(
                                                                children: [
                                                                  Column(
                                                                    crossAxisAlignment:
                                                                        CrossAxisAlignment
                                                                            .start,
                                                                    children: [
                                                                      BoldText(
                                                                        AppStrings
                                                                            .kdRatio,
                                                                        fontSize:
                                                                            12,
                                                                        color: AppColor
                                                                            .whiteF5F5F5,
                                                                      ),
                                                                      const SizedBox(
                                                                        height:
                                                                            2,
                                                                      ),
                                                                      Text(
                                                                        AppStrings
                                                                            .last50Games,
                                                                        style: TextStyle(
                                                                            color:
                                                                                AppColor.grayA0A0A0,
                                                                            fontSize: 8),
                                                                      ),
                                                                    ],
                                                                  ),
                                                                  const SizedBox(
                                                                    width: 4,
                                                                  ),
                                                                  BoldText(
                                                                    kdForCustom,
                                                                    fontSize:
                                                                        22,
                                                                    color: AppColor
                                                                        .whiteTextColor,
                                                                  )
                                                                ],
                                                              ),
                                                              const SizedBox(
                                                                height: 5,
                                                              ),
                                                              Container(
                                                                decoration: BoxDecoration(
                                                                    borderRadius:
                                                                        BorderRadius
                                                                            .circular(
                                                                                4),
                                                                    color: AppColor
                                                                        .buttonGrayBg),
                                                                padding: EdgeInsets
                                                                    .symmetric(
                                                                        horizontal:
                                                                            4,
                                                                        vertical:
                                                                            4),
                                                                child: Text(
                                                                  AppStrings
                                                                      .custom,
                                                                  style: TextStyle(
                                                                      color: AppColor
                                                                          .gray9f9f9f,
                                                                      fontSize:
                                                                          6),
                                                                ),
                                                              )
                                                            ],
                                                          )
                                                        ],
                                                      ),
                                                    ],
                                                  ),
                                                  maxTier == null
                                                      ? SizedBox.shrink()
                                                      : DecoratedBox(
                                                          decoration: BoxDecoration(
                                                              color: AppColor
                                                                  .buttonGrayBg,
                                                              border: Border.all(
                                                                  color: AppColor
                                                                      .buttonGrayBg,
                                                                  width: 1.0),
                                                              borderRadius:
                                                                  BorderRadius
                                                                      .circular(
                                                                          3.0)),
                                                          child: Container(
                                                              padding: EdgeInsets
                                                                  .symmetric(
                                                                      horizontal:
                                                                          26),
                                                              height: 96,
                                                              child: Column(
                                                                  mainAxisAlignment:
                                                                      MainAxisAlignment
                                                                          .spaceEvenly,
                                                                  children: [
                                                                    RegularText(
                                                                      group ??
                                                                          "",
                                                                      fontSize:
                                                                          12.0,
                                                                      color: Colors
                                                                          .white,
                                                                    ),
                                                                    const SizedBox(
                                                                        height:
                                                                            4.0),
                                                                    Image.network(
                                                                        getTierImage(
                                                                            maxTier),
                                                                        width:
                                                                            36.0,
                                                                        height:
                                                                            36.0),
                                                                    const SizedBox(
                                                                        height:
                                                                            4.0),
                                                                    RegularText(
                                                                        getTierName(
                                                                            maxTier),
                                                                        maxLines:
                                                                            1,
                                                                        color: AppColor
                                                                            .lightWhiteColor,
                                                                        overflow:
                                                                            TextOverflow
                                                                                .ellipsis,
                                                                        fontSize:
                                                                            12.0)
                                                                  ])))
                                                ],
                                              ),
                                            ),
                                            const SizedBox(
                                              height: 16,
                                            ),
                                            Row(
                                              mainAxisAlignment:
                                                  MainAxisAlignment.start,
                                              children: [
                                                SvgPicture.asset(
                                                  "${imageAssets}ic_rank.svg",
                                                  height: 14,
                                                  width: 14,
                                                ),
                                                const SizedBox(
                                                  width: 4,
                                                ),
                                                Text(
                                                  "#${userRank} in ${tournamentName}",
                                                  style: TextStyle(
                                                      color: AppColor
                                                          .whiteTextColor,
                                                      fontSize: 12),
                                                ),
                                              ],
                                            ),
                                            const SizedBox(
                                              height: 4,
                                            ),
                                            Row(
                                              mainAxisAlignment:
                                                  MainAxisAlignment.start,
                                              children: [
                                                SvgPicture.asset(
                                                  "${imageAssets}ic_last_played.svg",
                                                  height: 14,
                                                  width: 14,
                                                ),
                                                const SizedBox(
                                                  width: 4,
                                                ),
                                                Text(
                                                  "Played $totalPlayForClassic, placed in top 10 - ${top10CountForClassic} ",
                                                  style: TextStyle(
                                                      color: AppColor
                                                          .whiteTextColor,
                                                      fontSize: 12),
                                                ),
                                              ],
                                            )
                                          ],
                                        ))
                                      ],
                                    ),
                                  );
                                }),
                          );
                  else if (state is TournamentLoading) if (state.showProgress)
                    return appCircularProgressIndicator();
                  return SizedBox.shrink();
                },
                bloc: widget.homeBloc,
                buildWhen: (previous, current) =>
                    current is UserPreferenceListState ||
                    current is TournamentLoading)
          ],
        ),
      ),
    );
  }
}
