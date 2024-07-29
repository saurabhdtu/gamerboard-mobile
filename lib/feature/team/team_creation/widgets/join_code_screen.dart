
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/containers.dart';
import 'package:gamerboard/common/widgets/input.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_bloc.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_states.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/validators.dart';

class JoinCodeScreen extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _JoinCodeState();

  const JoinCodeScreen();
}

class _JoinCodeState extends State<JoinCodeScreen> {
  late TeamCreateBloc _teamCreateBloc;
  late TextEditingController? _controller;

  String? error;
  bool isValid = false;

  @override
  void dispose() {
    _controller?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) => BlocListener<TeamCreateBloc,
      TeamCreateState>(
      listener: (context, state) {
        if (state is TeamNameSubmitted) {
          setState(() {
            error = state.error;
          });
        }
      },
      listenWhen: (p, c) => c is TeamNameSubmitted,
      child: themeContainer(
          Column(children: [
            BoldText(AppStrings.inviteOnly, fontSize: 18.0),
            const Spacer(flex: 2),
            RegularText(AppStrings.descriptionInviteOnly,
                color: AppColor.textSubTitle, textAlign: TextAlign.center),
            const Spacer(flex: 2),
            TextField(
                maxLines: 1,
                maxLength: Constants.INVITE_CODE,
                keyboardType: TextInputType.text,
                textInputAction: TextInputAction.done,
                controller: _controller,
                inputFormatters: [AppInputFormatters.spaceFormatter],
                onChanged: (val) => setState(() {
                  isValid = FieldValidators.validateJoinCode(val);
                }),

                style: RegularTextStyle(),
                decoration: darkTextFieldWithBorderDecoration(
                    hintLabel: AppStrings.hintInviteOnlyCode, error: error)),
            const Spacer(flex: 3),
            secondaryButton(
                AppStrings.submit,
                    () => isValid
                    ? _teamCreateBloc.submitJoinCode(context, _controller!.text)
                    : {},
                active: isValid),
            const Spacer(flex: 3)
          ]),
          padding: const EdgeInsets.symmetric(horizontal: 45.0, vertical: 20.0),
          height: MediaQuery.of(context).size.height - 70));

  @override
  void initState() {
    super.initState();
    _teamCreateBloc = context.read<TeamCreateBloc>();
    _controller = TextEditingController();
    _controller?.text = _teamCreateBloc.joinCode ?? '';
    isValid = FieldValidators.validateTeamName(_controller?.text);
  }
}