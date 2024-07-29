import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/widgets/input.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/feature/team/team_join_create/team_join_create_bloc.dart';
import 'package:gamerboard/feature/team/team_join_create/team_join_create_states.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/validators.dart';
import 'package:sprintf/sprintf.dart';

import '../../../common/constants.dart';
import '../../../common/widgets/buttons.dart';
import '../../../common/widgets/containers.dart';
import '../../../common/widgets/text.dart';
import '../../../resources/colors.dart';
import '../../../resources/strings.dart';

////Created by saurabh.lahoti on 27/03/22

class TeamJoinCreateOption extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _TeamJoinCreateState();
}

class _TeamJoinCreateState extends State<TeamJoinCreateOption> {
  late final TeamJoinCreateBloc _teamJoinCreateBloc;
  late final TextEditingController _groupCodeController;
  bool isValidInviteCode = false;
  bool isValidSquadCode = false;

  @override
  Widget build(BuildContext context) =>
      appScaffold(
          appBar: appBar(context, AppStrings.createOrJoinTitle),
          body: SafeArea(
            child: BlocBuilder<TeamJoinCreateBloc, TeamJoinCreateState>(
                builder: (ctx, state) {
                  if (state is LoadProfile) {
                    if (state.loading) return appCircularProgressIndicator();
                    return SizedBox.shrink();
                  }
                  return SingleChildScrollView(
                      child: SizedBox(
                          height: MediaQuery
                              .of(ctx)
                              .size
                              .height - 45,
                          child: Row(children: [
                            Expanded(
                                child: themeContainer(
                                    Column(
                                        crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                        children: [
                                          Row(children: [
                                            Icon(Icons
                                                .supervisor_account_rounded,
                                                color: AppColor.textSubTitle,
                                                size: 24.0),
                                            const SizedBox(width: 20.0),
                                            BoldText(AppStrings.createATeam,
                                                fontSize: 18.0)
                                          ]),
                                          const SizedBox(height: 13.0),
                                          const SizedBox(height: 25.0),
                                          RegularText(
                                              sprintf(AppStrings.createStep1, [
                                                (_teamJoinCreateBloc
                                                    .tournamentMixin
                                                    .tournamentGroup()
                                                    .teamSize() -
                                                    1)
                                                    .toString()
                                              ]),
                                              color: AppColor.textDarkGray),
                                          const SizedBox(height: 15.0),
                                          RegularText(
                                              _teamJoinCreateBloc
                                                  .tournamentMixin
                                                  .tournament
                                                  .fee > 0 ?
                                              sprintf(AppStrings.createStep2a, [
                                                AppStrings.rupeeSymbol +
                                                    _teamJoinCreateBloc
                                                        .tournamentMixin
                                                        .tournament
                                                        .fee
                                                        .toString()
                                              ]): AppStrings
                                                  .createStep2b,
                                              color: AppColor.textDarkGray),
                                          const SizedBox(height: 15.0),
                                          const SizedBox(height: 30.0),
                                          Row(children: [
                                            const Spacer(),
                                            secondaryButton(
                                                AppStrings.createTeam,
                                                    () async {
                                                  var result =
                                                  await navigateToCreateTeam(
                                                      ctx,
                                                      _teamJoinCreateBloc
                                                          .tournamentMixin,
                                                      _teamJoinCreateBloc
                                                          .fromTournamentDetail,
                                                      false, inviteCode: null, phoneNumber: _teamJoinCreateBloc.phoneNumber);
                                                  if (result == true)
                                                    Navigator.of(ctx).pop(
                                                        true);
                                                })
                                          ])
                                        ]),
                                    padding: const EdgeInsets.all(15.0))),

                            RotatedBox(
                                quarterTurns: 1,
                                child: Divider(
                                    thickness: 8.0,
                                    height: 8.0,
                                    color: AppColor.darkBackground)),
                            Expanded(
                                child: themeContainer(
                                    Column(
                                        crossAxisAlignment:
                                        CrossAxisAlignment.start,
                                        children: [
                                          Row(children: [
                                            Icon(Icons.input,
                                                color: AppColor.textSubTitle,
                                                size: 24.0),
                                            const SizedBox(width: 20.0),
                                            BoldText(
                                                AppStrings.joinExistingSquad,
                                                fontSize: 18.0)
                                          ]),
                                          const SizedBox(height: 13.0),
                                          RegularText(
                                              AppStrings.enterTeamInviteCode,
                                              color: AppColor.textDarkGray),
                                          const SizedBox(height: 8.0),
                                          _getTheTextField(
                                              _groupCodeController,
                                              Constants.INVITE_CODE,
                                                  (val) =>
                                              {
                                                setState(() {
                                                  isValidInviteCode =
                                                      FieldValidators
                                                          .validateCode(
                                                          val,
                                                          codeLength: Constants
                                                              .INVITE_CODE) ==
                                                          null;
                                                })
                                              },
                                              [
                                                AppInputFormatters
                                                    .spaceFormatter,
                                                AppInputFormatters
                                                    .alphaNumericFormatter
                                              ],
                                              AppStrings.enterGroupCode),
                                          const SizedBox(height: 10.0),
                                          RegularText(AppStrings.gamerboardCode,
                                              color: AppColor.textDarkGray),
                                          const SizedBox(height: 40.0),
                                          Row(children: [
                                            const Spacer(),
                                            secondaryButton(
                                                AppStrings.joinTeam,
                                                    () =>
                                                    _teamJoinCreateBloc
                                                        .joinSquad(
                                                        context,
                                                        _groupCodeController
                                                            .text),
                                                active: isValidInviteCode)
                                          ])
                                        ]),
                                    padding: const EdgeInsets.all(15.0)))
                          ])));
                },
                bloc: _teamJoinCreateBloc),
          ));

  @override
  void dispose() {
    _groupCodeController.dispose();
    super.dispose();
  }

  @override
  void initState() {
    super.initState();
    _teamJoinCreateBloc = context.read<TeamJoinCreateBloc>();
    _groupCodeController = TextEditingController();
    _teamJoinCreateBloc.init(true);
  }
}

Widget _getTheTextField(TextEditingController controller,
    int maxLength,
    Function(String) onChanged,
    List<TextInputFormatter> formatters,
    String hintLabel) =>
    TextField(
        maxLines: 1,
        controller: controller,
        textCapitalization: TextCapitalization.characters,
        maxLength: maxLength,
        onChanged: onChanged,
        textInputAction: TextInputAction.done,
        style: RegularTextStyle(),
        inputFormatters: formatters,
        decoration: darkTextFieldWithBorderDecoration(hintLabel: hintLabel));
