import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

import '../../../../common/constants.dart';
import '../../../../common/widgets/text.dart';
import '../../../../graphql/query.graphql.dart';
import '../../../../resources/colors.dart';
import '../../../../resources/strings.dart';
import '../../../../common/services/analytics/analytic_utils.dart';
import '../../../../utils/share_utils.dart';

class InviteFriendDialog extends StatefulWidget {
  final UserMixin user;
  final InviteDialogEvent inviteDialogEvent;

  InviteFriendDialog(this.user, this.inviteDialogEvent, {Key? key})
      : super(key: key);

  @override
  State<InviteFriendDialog> createState() => _InviteFriendDialogState();
}

class _InviteFriendDialogState extends State<InviteFriendDialog> {
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    AnalyticService.getInstance().trackEvents(Events.SHOW_INVITE, properties: {
      "from": widget.inviteDialogEvent.getInviteEventSource(),
    });
  }

  @override
  Widget build(BuildContext context) {
    return SingleChildScrollView(
      child: Container(
          padding: const EdgeInsets.symmetric(vertical: 6, horizontal: 18),
          child: Column(children: [
            Row(mainAxisAlignment: MainAxisAlignment.end, children: [
              InkWell(
                  onTap: () => Navigator.of(context).pop(),
                  child: Padding(
                      padding: const EdgeInsets.symmetric(horizontal: 8),
                      child: Icon(Icons.close, color: AppColor.whiteTextColor)))
            ]),
            Container(
                padding: const EdgeInsets.only(left: 16),
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(children: [
                        SvgPicture.asset("${imageAssets}ic_invite_friend.svg",
                            height: 28, width: 28),
                        const SizedBox(width: 12),
                        Text(AppStrings.inviteYourFriendToGetRewards,
                            style: SemiBoldTextStyle(fontSize: 16))
                      ]),
                      const SizedBox(height: 12),
                      RichText(
                          text: TextSpan(
                              text: AppStrings.youWillReceive,
                              style: RegularTextStyle(
                                  color: AppColor.grayTextB3B3B3, fontSize: 12),
                              children: [
                                TextSpan(
                                  text: AppStrings.rupeeSymbol +
                                      Constants.REFERRAL_AMOUNT.toString(),
                                  style: RegularTextStyle(
                                      color: AppColor.successGreen, fontSize: 12),
                                ),
                                TextSpan(
                                    text: AppStrings.bonusCaseByFollowing,
                                    style: RegularTextStyle(
                                        color: AppColor.grayTextB3B3B3,
                                        fontSize: 12))
                              ])),
                      Text(AppStrings.youInviteYourFriend,
                          style: RegularTextStyle(
                              color: AppColor.grayTextB3B3B3, fontSize: 12)),
                      Text(AppStrings.yourFriendInstalls,
                          style: RegularTextStyle(
                              color: AppColor.grayTextB3B3B3, fontSize: 12)),
                      Text(AppStrings.theyJoinTournament,
                          style: RegularTextStyle(
                              color: AppColor.grayTextB3B3B3, fontSize: 12)),
                      Container(
                          color: AppColor.titleBarBg,
                          height: 64,
                          padding: const EdgeInsets.only(left: 16),
                          margin: const EdgeInsets.only(top: 18),
                          child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisAlignment: MainAxisAlignment.center,
                              children: [
                                Row(children: [
                                  Text(AppStrings.useTheCodeBelow,
                                      style: RegularTextStyle(
                                          color: AppColor.grayText696969))
                                ]),
                                Text(AppStrings.code + widget.user!.inviteCode,
                                    style: SemiBoldTextStyle(
                                        color: AppColor.whiteTextColor))
                              ])),
                      const SizedBox(height: 16),
                      Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            InkWell(
                                onTap: () => ShareUtils.getInstance().inviteApp(
                                    medium: ShareMedium.CLIPBOARD,
                                    inviteDialogEvent:
                                    widget.inviteDialogEvent),
                                child: Container(
                                    width: 142,
                                    padding: const EdgeInsets.symmetric(
                                        vertical: 10),
                                    decoration: BoxDecoration(
                                        border: Border.all(
                                            color: AppColor.whiteTextColor)),
                                    child: Row(
                                        mainAxisAlignment:
                                        MainAxisAlignment.center,
                                        children: [
                                          Icon(Icons.copy,
                                              color: Colors.white, size: 18),
                                          const SizedBox(width: 8),
                                          Text(AppStrings.copy,
                                              style: RegularTextStyle(
                                                  color:
                                                  AppColor.whiteTextColor,
                                                  fontSize: 12))
                                        ]))),
                            InkWell(
                                onTap: () => ShareUtils.getInstance().inviteApp(
                                    inviteDialogEvent:
                                    widget.inviteDialogEvent),
                                child: Container(
                                    width: 142,
                                    padding: const EdgeInsets.symmetric(
                                        vertical: 10),
                                    decoration: BoxDecoration(
                                        border: Border.all(
                                            color: AppColor.whiteTextColor)),
                                    child: Row(
                                        mainAxisAlignment:
                                        MainAxisAlignment.center,
                                        children: [
                                          SvgPicture.asset(
                                              "${imageAssets}ic_share.svg",
                                              height: 20.0,
                                              width: 20.0),
                                          const SizedBox(width: 8),
                                          Text(AppStrings.share,
                                              style: RegularTextStyle(
                                                  color:
                                                  AppColor.whiteTextColor,
                                                  fontSize: 12))
                                        ]))),
                            InkWell(
                                onTap: () => ShareUtils.getInstance().inviteApp(
                                    medium: ShareMedium.WHATSAPP,
                                    inviteDialogEvent:
                                    widget.inviteDialogEvent),
                                child: Container(
                                    width: 142,
                                    padding: const EdgeInsets.symmetric(
                                        vertical: 10),
                                    decoration: BoxDecoration(
                                        border: Border.all(
                                            color: AppColor.whatsappColor),
                                        color: AppColor.whatsappColor),
                                    child: Row(
                                        mainAxisAlignment:
                                        MainAxisAlignment.center,
                                        children: [
                                          Image.asset(
                                              "${imageAssets}ic_whatsapp.png",
                                              height: 20.0,
                                              width: 20.0),
                                          const SizedBox(width: 8),
                                          Text(AppStrings.whatsapp,
                                              style: RegularTextStyle(
                                                  color:
                                                  AppColor.whiteTextColor,
                                                  fontSize: 12))
                                        ])))
                          ])
                    ]))
          ])),
    );
  }
}