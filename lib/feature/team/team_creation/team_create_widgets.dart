import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/containers.dart';
import 'package:gamerboard/common/widgets/input.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/team/team_creation/page_state.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_bloc.dart';
import 'package:gamerboard/feature/team/team_creation/team_create_states.dart';
import 'package:gamerboard/feature/team/team_creation/widgets/join_code_screen.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/validators.dart';
import 'package:lottie/lottie.dart';

////Created by saurabh.lahoti on 19/03/22
class TeamCreateWidgets {
  final TeamCreateBloc _teamCreateBloc;
  final BuildContext _buildContext;

  TeamCreateWidgets(this._teamCreateBloc, this._buildContext);

  _getCTACards(IconData icon, String cta, bool isLeft) {
    return InkWell(
        onTap: () => isLeft
            ? _teamCreateBloc.back(_buildContext)
            : _teamCreateBloc.forward(_buildContext),
        child: Container(
            width: 100,
            height: 100,
            child: Center(
                child: Column(children: [
              Spacer(),
              Icon(icon, color: AppColor.textSubTitle, size: 30.0),
              const SizedBox(height: 4.0),
              RegularText(cta, textAlign: TextAlign.center),
              Spacer()
            ])),
            decoration: BoxDecoration(
                borderRadius: isLeft
                    ? BorderRadius.only(
                        topLeft: Radius.circular(6.0),
                        bottomLeft: Radius.circular(6.0))
                    : BorderRadius.only(
                        topRight: Radius.circular(6.0),
                        bottomRight: Radius.circular(6.0)),
                color: AppColor.titleBarBg,
                boxShadow: [
                  BoxShadow(
                      color: AppColor.tournamentCardBG.withAlpha(100),
                      spreadRadius: 4.0)
                ])));
  }

  Widget getLeftCTA(int currentPage) {
    final pageState = _teamCreateBloc.getPageState( currentPage);
   if(!_teamCreateBloc.onlyPayment){
     switch (pageState) {
       case TeamCreatePageState.inviteOnly:
         return _getCTACards(Icons.close, AppStrings.cancel, true);
       case TeamCreatePageState.teamName:
         if (_teamCreateBloc.tournamentMixin.tournament.joinCode != null) {
           return _getCTACards(Icons.arrow_back, AppStrings.inviteOnly, true);
         }
         return _getCTACards(Icons.close, AppStrings.cancel, true);
       case TeamCreatePageState.paymentSuccess:
         return const SizedBox(width: 100);
       case TeamCreatePageState.payment:
         return _teamCreateBloc.onlyPayment
             ? _getCTACards(Icons.close, AppStrings.cancel, true)
             : _getCTACards(Icons.arrow_back, AppStrings.teamName, true);
       default:
         return const SizedBox(width: 100);
     }
   }
    return const SizedBox(width: 100);
  }

  Widget getRightCTA(int currentPage) {
    if (!_teamCreateBloc.onlyPayment) {
      final pageState = _teamCreateBloc.getPageState( currentPage);
      switch (pageState) {
        case TeamCreatePageState.inviteOnly:
        case TeamCreatePageState.teamName:
        case TeamCreatePageState.payment:
          return const SizedBox(width: 100.0);
        case TeamCreatePageState.paymentSuccess:
          return _getCTACards(Icons.arrow_forward, AppStrings.next, false);
        default:
          return _getCTACards(Icons.check, AppStrings.done, false);
      }
    }
    return const SizedBox(width: 100.0);
  }

  Widget getContentCard(int currentPage) {
    final pageState = _teamCreateBloc.getPageState(currentPage);
    switch (pageState) {
      case TeamCreatePageState.inviteOnly:
        return const JoinCodeScreen();
      case TeamCreatePageState.teamName:
        return const TeamNameScreen();
      case TeamCreatePageState.paymentSuccess:
      case TeamCreatePageState.payment:
        return const PaymentPage();
      default:
        return _teamInfo();
    }
  }

  Widget _teamInfo() {
    final members = _teamCreateBloc.squad?.members ?? [];
    List<Widget> memberImages = List.generate(
        _teamCreateBloc.tournamentMixin.tournamentGroup() == GameTeamGroup.duo
            ? 2
            : 4, (index) {
      if (_teamCreateBloc.squad == null && index == 0)
        return Padding(
          padding: const EdgeInsets.all(4.0),
          child: Image.network(
              _teamCreateBloc.user.image ??
                  ImageConstants.DEFAULT_USER_PLACEHOLDER,
              width: 48.0,
              height: 48.0),
        );

      if (index < members.length) {
        return Padding(
          padding: const EdgeInsets.all(4.0),
          child: Image.network(
              members[index].user.image ??
                  ImageConstants.DEFAULT_USER_PLACEHOLDER,
              width: 48.0,
              height: 48.0),
        );
      }
      return Padding(
        padding: const EdgeInsets.all(4.0),
        child: ColoredBox(
            color: AppColor.darkBackground,
            child: Padding(
                padding: const EdgeInsets.all(8.0),
                child: Icon(Icons.person,
                    color: AppColor.buttonGrayBg, size: 40.0))),
      );
    });

    return themeContainer(
        Column(children: [
          RegularText(AppStrings.inviteTeammates, fontSize: 18.0),
          const Spacer(flex: 2),
          Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: memberImages),
          const Spacer(flex: 2),
          RegularText(AppStrings.sendInvite, textAlign: TextAlign.center),
          const Spacer(flex: 2),
          Padding(
              padding: const EdgeInsets.symmetric(horizontal: 20.0),
              child: secondaryButton(AppStrings.invitePlayersNow, () async {
                final result = await navigateToInvitePage(_buildContext,
                    _teamCreateBloc.tournamentMixin, _teamCreateBloc.squad!);
                if (result == true) _teamCreateBloc.forward(_buildContext);
              })),
          const Spacer(flex: 3),
          RegularText(AppStrings.sendInvite, textAlign: TextAlign.center),
          const Spacer(flex: 2)
        ]),
        padding: const EdgeInsets.all(20.0),
        height: MediaQuery.of(_buildContext).size.height - 70);
  }
}

class TeamNameScreen extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _TeamNameState();

  const TeamNameScreen();
}

class _TeamNameState extends State<TeamNameScreen> {
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
            BoldText(AppStrings.registerTeam, fontSize: 18.0),
            const Spacer(flex: 2),
            RegularText(AppStrings.chooseSquadName,
                color: AppColor.textSubTitle, textAlign: TextAlign.center),
            const Spacer(flex: 2),
            TextField(
                maxLines: 1,
                maxLength: 15,
                keyboardType: TextInputType.text,
                textInputAction: TextInputAction.done,
                controller: _controller,
                inputFormatters: [AppInputFormatters.spaceFormatter],
                onChanged: (val) => setState(() {
                      isValid = FieldValidators.validateTeamName(val);
                    }),
                style: RegularTextStyle(),
                decoration: darkTextFieldWithBorderDecoration(
                    hintLabel: AppStrings.enterTeamName, error: error)),
            const Spacer(flex: 3),
            secondaryButton(
                AppStrings.submit,
                () => isValid
                    ? _teamCreateBloc.submitTeamName(context, _controller!.text)
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
    _controller?.text = _teamCreateBloc.teamName ?? '';
    isValid = FieldValidators.validateTeamName(_controller?.text);
  }
}

class TopProgressBar extends StatefulWidget {
  const TopProgressBar();

  @override
  State<StatefulWidget> createState() => _TopProgressBarState();
}

class _TopProgressBarState extends State<TopProgressBar> {
  double progressValue = 0.0;
  int currentPage = 0;
  late TeamCreateBloc _teamCreateBloc;

  @override
  Widget build(BuildContext context) =>
      BlocListener<TeamCreateBloc, TeamCreateState>(
          listener: (context, state) {
            if (state is TeamCreationPageChange) {
              setState(() {
                currentPage = state.currentPage;
                var mappedCurrentPage = currentPage;
                var pageState = _teamCreateBloc.getPageState(currentPage);
                if (pageState == TeamCreatePageState.paymentSuccess) {
                  mappedCurrentPage = currentPage - 1;
                }
                progressValue = mappedCurrentPage / (_teamCreateBloc.totalPages - 1);
              });
            }
          },
          child: SizedBox(
              width: MediaQuery.of(context).size.width / 4,
              child: Stack(children: [
                Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 5.0, vertical: 14.0),
                    child: SizedBox(
                        height: 3.0,
                        child: LinearProgressIndicator(
                            color: AppColor.colorAccent,
                            backgroundColor: Colors.white,
                            value: progressValue))),
                Center(
                    child: Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children:
                            List.generate(_teamCreateBloc.totalPages, (index) {
                          var mappedCurrentPage = currentPage;
                          var state = _teamCreateBloc.getPageState(currentPage);
                          if (state == TeamCreatePageState.paymentSuccess) {
                            mappedCurrentPage = currentPage - 1;
                          }
                          if (mappedCurrentPage > index) {
                            return _completedCircle("${index + 1}");
                          }
                          if (mappedCurrentPage == index) {
                            return _activeCircle("${index + 1}");
                          }
                          return _inactiveCircle("${index + 1}");
                        })))
              ])),
          bloc: _teamCreateBloc,
          listenWhen: (p, c) => c is TeamCreationPageChange);

  Widget _inactiveCircle(String num) => SizedBox(
      width: 24.0,
      height: 24.0,
      child: CircleAvatar(
          backgroundColor: Colors.white,
          child: Center(
              child: Padding(
                  padding: const EdgeInsets.all(1.5),
                  child: RegularText(num, color: Colors.black)))));

  Widget _completedCircle(String num) => SizedBox(
      width: 28.0,
      height: 28.0,
      child: CircleAvatar(
          backgroundColor: AppColor.colorAccent,
          child: Center(
              child: Padding(
                  padding: const EdgeInsets.all(1.5),
                  child: RegularText(num)))));

  Widget _activeCircle(String num) => Container(
      width: 28.0,
      height: 28.0,
      padding: const EdgeInsets.all(1.5),
      decoration: BoxDecoration(
          shape: BoxShape.circle,
          color: AppColor.darkBackground,
          border: Border.all(color: AppColor.colorAccent, width: 1.0)),
      child: CircleAvatar(
          backgroundColor: AppColor.colorAccent,
          child: Center(child: RegularText(num, color: Colors.white))));

  @override
  void initState() {
    super.initState();
    _teamCreateBloc = context.read<TeamCreateBloc>();
  }
}

class PaymentPage extends StatefulWidget {
  const PaymentPage();

  @override
  State<StatefulWidget> createState() => _PaymentPageState();
}

class _PaymentPageState extends State<PaymentPage> {
  late TeamCreateBloc _teamCreateBloc;
  bool isJoinTournamentEnable = true;

  @override
  Widget build(BuildContext context) {
    final totalBal = _teamCreateBloc.user.wallet.bonus +
        _teamCreateBloc.user.wallet.winning +
        _teamCreateBloc.user.wallet.deposit;

    final List<Widget> columnItems = [
      RegularText(AppStrings.payForYourself, fontSize: 16.0),
      const SizedBox(height: 10.0),
      BoldText(
          "${AppStrings.rupeeSymbol}${_teamCreateBloc.tournamentMixin.tournament.fee}",
          fontSize: 55.0),
      const SizedBox(height: 5.0),
      const Divider(height: 1.0, thickness: 1, color: AppColor.dividerColor),
      const Spacer()
    ];
    List<Widget> items = [];

    var indexToCheck = 1;
    if (_teamCreateBloc.showUseJoinCode()) {
      indexToCheck = 2;
    }

    if (_teamCreateBloc.currentPage <= indexToCheck) {
      items = _buildPaymentPage(totalBal, context);
    } else {
      items = _buildPaymentSuccessPage(context);
    }

    columnItems.addAll(items);
    columnItems.add(const Spacer());
    return themeContainer(
        Column(mainAxisSize: MainAxisSize.min, children: columnItems),
        padding: const EdgeInsets.symmetric(vertical: 20.0, horizontal: 40.0),
        height: MediaQuery.of(context).size.height - 50);
  }

  List<Widget> _buildPaymentPage(double totalBal, BuildContext context) {
    return [
      const SizedBox(height: 20.0),
      RegularText(AppStrings.payYourShare,
          fontSize: 16.0, color: AppColor.textDarkGray),
      const SizedBox(height: 10.0),
      _haveEnoughBalance(totalBal)
          ? _buildPayButton(context)
          : _buildAddFundsButton(context),
      const SizedBox(height: 10.0),
      RegularText(
          "${AppStrings.currentBalance}: ${AppStrings.rupeeSymbol}$totalBal",
          fontSize: 16.0,
          color: _haveEnoughBalance(totalBal)
              ? AppColor.textDarkGray
              : AppColor.errorRed)
    ];
  }

  List<Widget> _buildPaymentSuccessPage(BuildContext context) {
    return [
      LottieBuilder.asset("${lottieAssets}tick.json",
          height: 80.0, width: 80.0, repeat: true),
      const SizedBox(height: 5.0),
      RegularText(AppStrings.successfullyPaid, fontSize: 16.0),
      const Spacer(),
      if (_teamCreateBloc.onlyPayment)
        secondaryButton(AppStrings.backToTournament,
            () => _teamCreateBloc.openTournament(context)),
      const Spacer()
    ];
  }

  bool _haveEnoughBalance(double totalBal) =>
      totalBal >= _teamCreateBloc.tournamentMixin.tournament.fee;

  Widget _buildAddFundsButton(BuildContext context) {
    return secondaryButton(
        AppStrings.addFunds, () => _teamCreateBloc.addFunds(context));
  }

  BlocListener<TeamCreateBloc, TeamCreateState> _buildPayButton(
      BuildContext context) {
    return BlocListener<TeamCreateBloc, TeamCreateState>(
      listener: (context, state) {
        if (state is JoinTournamentStateChange) {
          setState(() {
            isJoinTournamentEnable = !state.isApiCalling;
          });
        }
      },
      listenWhen: (p, c) => c is JoinTournamentStateChange,
      child: secondaryButton(AppStrings.payWithGB, () {
        if (isJoinTournamentEnable) {
          _teamCreateBloc.joinTournament(context, "payment_page");
        }
      }, active: isJoinTournamentEnable),
    );
  }

  @override
  void initState() {
    super.initState();
    _teamCreateBloc = context.read<TeamCreateBloc>();
  }
}
