////Created by saurabh.lahoti on 17/03/22
import 'package:flutter/material.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/utils/graphql_ext.dart';

Widget cardContainer(Widget child,
        {double? width, double? height, EdgeInsetsGeometry? padding}) =>
    Container(
        child: child,
        width: width,
        height: height,
        padding: padding,
        decoration: BoxDecoration(
            border: Border.all(color: AppColor.dividerColor, width: 1.0),
            borderRadius: BorderRadius.circular(2.0),
            color: AppColor.screenBackground));

Widget themeContainer(Widget child,
        {double? width,
        double? height,
        EdgeInsetsGeometry? padding,
        Color? backgroundColor}) =>
    Container(
        child: child,
        width: width,
        height: height,
        padding: padding,
        color: backgroundColor ?? AppColor.titleBarBg);

Widget teamPlayerStatusContainer(int? userId,
    SquadMixin squadMixin, GameTeamGroup teamGroup, Function? onClick,
    {Color? backgroundColor, double scale = 1.0, bool ellipsisStatus = true}) {
  final statusResult = squadMixin.squadStatusAndColor(userId, teamGroup);
  return InkWell(
      onTap: () => onClick?.call(),
      child: themeContainer(
          Row(children: [
            SizedBox(
                height: 20.0 * scale,
                width: 20.0 * scale,
                child: GridView.builder(
                    gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: 2,
                        mainAxisSpacing: 2.0 * scale,
                        crossAxisSpacing: 2.0 * scale,
                        childAspectRatio: 1),
                    itemBuilder: (context, index) =>
                        index < squadMixin.members.length
                            ? Image.network(
                                squadMixin.members[index].user.image ??
                                    ImageConstants.DEFAULT_USER_PLACEHOLDER,
                                width: 9.0 * scale,
                                height: 9.0 * scale)
                            : ColoredBox(
                                color: AppColor.buttonGrayBg,
                                child: SizedBox(
                                    width: 9.0 * scale, height: 9.0 * scale)),
                    itemCount: teamGroup.teamSize())),
            SizedBox(width: 7.0 * scale),
            Expanded(
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                  SemiBoldText(squadMixin.name,
                      maxLines: ellipsisStatus ? 1 : null,
                      overflow: ellipsisStatus ? TextOverflow.ellipsis : null,
                      fontSize: 12.0 * scale),
                  SizedBox(height: 2.0 * scale),
                  RegularText(statusResult.key,
                      color: statusResult.value,
                      fontSize: 10.0 * scale,
                      maxLines: ellipsisStatus ? 1 : null,
                      overflow: ellipsisStatus ? TextOverflow.ellipsis : null)
                ])),
            Padding(
                padding: EdgeInsets.symmetric(horizontal: 5.0 * scale),
                child: Icon(Icons.keyboard_arrow_right_rounded,
                    size: 20.0, color: AppColor.whiteF6F6F6))
          ]),
          padding: EdgeInsets.all(5.0 * scale),
          backgroundColor: backgroundColor));
}
