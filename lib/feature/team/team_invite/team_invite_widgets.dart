import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/bloc/application/application_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/common/widgets/blank.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/input.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/team/team_invite/team_invite_bloc.dart';
import 'package:gamerboard/feature/team/team_invite/team_invite_states.dart';
import 'package:gamerboard/graphql/custom.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/decorations.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:gamerboard/utils/share_utils.dart';

import '../../../common/widgets/containers.dart';
////Created by saurabh.lahoti on 14/03/22

class TeamInviteWidgets {
  BuildContext _context;
  TeamInviteBloc _teamInviteBloc;

  TeamInviteWidgets(this._context, this._teamInviteBloc);

  Widget tabBar() => TabBar(
          isScrollable: true,
          indicatorColor: Colors.white,
          indicatorWeight: 4.0,
          unselectedLabelStyle: RegularTextStyle(color: AppColor.textSubTitle),
          labelStyle: BoldTextStyle(color: Colors.white),
          tabs: [
            Tab(
                child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 5.0, vertical: 6.0),
                    child: Text(AppStrings.sendCode))),
            Tab(
                child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 5.0, vertical: 6.0),
                    child: Text(AppStrings.recentPlayers))),
            Tab(
                child: Padding(
                    padding: const EdgeInsets.symmetric(
                        horizontal: 5.0, vertical: 6.0),
                    child: Text(AppStrings.searchPlayers)))
          ]);

  Widget inviteCode() => themeContainer(
      Column(children: [
        Spacer(),
        RegularText(AppStrings.useCodeBelow, textAlign: TextAlign.center),
        const SizedBox(height: 15.0),
        Container(
            decoration: AppDecorations.decoration,
            width: 200,
            padding: const EdgeInsets.all(12.0),
            child: RegularText(_teamInviteBloc.squad.inviteCode,
                textAlign: TextAlign.center)),
        const SizedBox(height: 15.0),
        Row(children: [
          Expanded(
              child: iconTextButton(AppStrings.copy, () {
            ShareUtils.getInstance().teamInviteShare(
                _teamInviteBloc.squad, _teamInviteBloc.userTournamentMixin,
                medium: ShareMedium.CLIPBOARD);
          }, icon: Icon(Icons.file_copy, color: AppColor.textColorDark))),
          const SizedBox(width: 12.0),
          Expanded(
              child: iconTextButton(AppStrings.share, () {
            ShareUtils.getInstance().teamInviteShare(
                _teamInviteBloc.squad, _teamInviteBloc.userTournamentMixin);
          }, icon: Icon(Icons.share, color: AppColor.textColorDark))),
          const SizedBox(width: 12.0),
          Expanded(
              child: iconTextButton(AppStrings.whatsapp, () {
            ShareUtils.getInstance().teamInviteShare(
                _teamInviteBloc.squad, _teamInviteBloc.userTournamentMixin,
                medium: ShareMedium.WHATSAPP);
          },
                  icon: Image.asset("${imageAssets}ic_whatsapp.png",
                      height: 24.0, width: 24.0),
                  textColor: Colors.white,
                  backgroundColor: const Color(0xFF4E9847)))
        ]),
        const SizedBox(height: 15.0),
        RegularText(AppStrings.noticeInvite,
            color: AppColor.textDarkGray, textAlign: TextAlign.center),
        Spacer()
      ]),
      padding: EdgeInsets.symmetric(
          horizontal: MediaQuery.of(_context).size.width * .2));
}

class SearchPlayerView extends StatefulWidget {
  final TeamInviteWidgets teamInviteWidgets;

  SearchPlayerView(this.teamInviteWidgets);

  @override
  State<StatefulWidget> createState() => _SearchPlayerView();
}

class _SearchPlayerView extends State<SearchPlayerView> {
  late TeamInviteBloc _teamInviteBloc;
  late TextEditingController _searchController;
  SearchMode _currentSearchMode = SearchMode.userName;
  bool _searchEnabled = false;

  @override
  Widget build(BuildContext context) {
    final availableWidth = MediaQuery.of(context).size.width - 16 - 1;
    return Padding(
        padding: const EdgeInsets.all(8.0),
        child: Row(crossAxisAlignment: CrossAxisAlignment.stretch, children: [
          SizedBox(
              width: availableWidth * 0.55,
              child: BlocBuilder<TeamInviteBloc, TeamInviteState>(
                  builder: (context, state) {
                    if (state is TeamInviteSearchStart) {
                      return Center(
                        child: RegularText(AppStrings.startSearch,
                            color: AppColor.textDarkGray, fontSize: 18.0),
                      );
                    } else if (state is TeamInviteSearchResult) {
                      if (state.searchResult.isEmpty)
                        return emptyState("No results found.");
                      return Padding(
                          padding: const EdgeInsets.all(15.0),
                          child: ListView.separated(
                              itemBuilder: (context, index) =>
                                  SearchResult(state.searchResult[index]),
                              itemCount: state.searchResult.length,
                              separatorBuilder: (context, index) =>
                                  const SizedBox(height: 10.0)));
                    } else if (state is TeamInviteSearching)
                      return appCircularProgressIndicator();
                    return SizedBox.shrink();
                  },
                  bloc: _teamInviteBloc,
                  buildWhen: (p, c) =>
                      c is TeamInviteSearchStart ||
                      c is TeamInviteSearching ||
                      c is TeamInviteSearchResult)),
          RotatedBox(
              child: Divider(
                  thickness: 1.0, height: 1, color: AppColor.dividerColor),
              quarterTurns: 1),
          themeContainer(
              SingleChildScrollView(
                  child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                    const SizedBox(height: 10.0),
                    RegularText(AppStrings.searchBy,
                        fontSize: 12.0, textAlign: TextAlign.center),
                    const SizedBox(height: 12.0),
                    SizedBox(
                        height: 50.0,
                        child: TextField(
                            onChanged: (val) {
                              setState(() {
                                _searchEnabled = _isValid;
                              });
                            },
                            textInputAction: _searchEnabled
                                ? TextInputAction.search
                                : TextInputAction.done,
                            keyboardType: _keyboardType,
                            maxLength: _maxLength,
                            inputFormatters: _inputFormatters,
                            controller: _searchController,
                            onSubmitted: _search,
                            maxLines: 1,
                            style: TextStyle(color: Colors.white),
                            decoration: darkTextFieldWithBorderDecoration(
                                hintLabel: _label,
                                suffixIcon: _searchEnabled
                                    ? InkWell(
                                        onTap: () =>
                                            _search(_searchController.text),
                                        child: Padding(
                                          padding: const EdgeInsets.all(4.0),
                                          child: Icon(Icons.search,
                                              color: AppColor.colorAccent),
                                        ))
                                    : null))),
                    const SizedBox(height: 12.0),
                    _radioSearch('Gamerboard username', SearchMode.userName),
                    _radioSearch('Mobile number', SearchMode.mobile),
                    _radioSearch('Game profile ID', SearchMode.gameProfileId),
                    const SizedBox(height: 5.0)
                  ])),
              padding: const EdgeInsets.all(10.0),
              width: availableWidth * 0.45)
        ]));
  }

  _radioSearch(String radioText, SearchMode searchMode) => ListTile(
      title: RegularText(radioText),
      minLeadingWidth: 10,
      contentPadding: const EdgeInsets.all(0),
      leading: SizedBox(
          width: 24.0,
          height: 24.0,
          child: Radio<SearchMode>(
              value: searchMode,
              groupValue: _currentSearchMode,
              onChanged: (SearchMode? value) {
                setState(() {
                  FocusScope.of(context).unfocus();
                  _searchController.text = "";
                  _currentSearchMode = searchMode;
                });
              })));

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  void _search(String searchText) {
    FocusScope.of(context).unfocus();
    _teamInviteBloc.searchPlayers( _currentSearchMode, text: searchText);
  }

  get _maxLength {
    switch (_currentSearchMode) {
      case SearchMode.gameProfileId:
        return 15;
      case SearchMode.mobile:
        return 10;
      default:
        return 30;
    }
  }

  get _keyboardType {
    switch (_currentSearchMode) {
      case SearchMode.mobile:
      case SearchMode.gameProfileId:
        return TextInputType.number;
      default:
        return TextInputType.text;
    }
  }

  get _inputFormatters {
    switch (_currentSearchMode) {
      case SearchMode.gameProfileId:
      case SearchMode.mobile:
        return <TextInputFormatter>[FilteringTextInputFormatter.digitsOnly];
      default:
        return <TextInputFormatter>[
          FilteringTextInputFormatter.deny(' ', replacementString: '')
        ];
    }
  }

  get _label {
    switch (_currentSearchMode) {
      case SearchMode.gameProfileId:
        return AppStrings.searchByBGMID;
      case SearchMode.mobile:
        return AppStrings.searchByMob;
      default:
        return AppStrings.searchByUsername;
    }
  }

  get _isValid {
    switch (_currentSearchMode) {
      case SearchMode.mobile:
        return _searchController.text.length == 10;
      default:
        return _searchController.text.isNotEmpty;
    }
  }

  @override
  void initState() {
    super.initState();
    _teamInviteBloc = context.read<TeamInviteBloc>();
    _searchController = TextEditingController();
    _teamInviteBloc.loadSearch();
  }
}

class RecentPlayerView extends StatefulWidget {
  final TeamInviteWidgets teamInviteWidgets;

  @override
  State<StatefulWidget> createState() => _RecentPlayerState();

  RecentPlayerView(this.teamInviteWidgets);
}

class _RecentPlayerState extends State<RecentPlayerView> {
  late TeamInviteBloc _teamInviteBloc;

  @override
  Widget build(BuildContext context) => Padding(
        padding: const EdgeInsets.all(20.0),
        child: BlocBuilder<TeamInviteBloc, TeamInviteState>(
            builder: (ctx, state) {
              if (state is RecentLoaded) {
                if (state.results.isEmpty)
                  return emptyState("No recent players found");
                return GridView.builder(
                    gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                        crossAxisCount: 2,
                        mainAxisSpacing: 10.0,
                        crossAxisSpacing: 10.0,
                        childAspectRatio: 8),
                    itemBuilder: (context, index) {
                      return SearchResult(state.results[index]);
                    },
                    itemCount: state.results.length);
              } else
                return appCircularProgressIndicator();
            },
            bloc: _teamInviteBloc,
            buildWhen: (p, c) => c is RecentLoading || c is RecentLoaded),
      );

  @override
  void initState() {
    super.initState();

    _teamInviteBloc = context.read<TeamInviteBloc>();
    _teamInviteBloc.getRecentPlayers();
  }
}

class SearchResult extends StatefulWidget {
  final UserSearchResult result;

  SearchResult(this.result);

  @override
  State<StatefulWidget> createState() => _SearchResultState();
}

class _SearchResultState extends State<SearchResult> {
  late TeamInviteBloc _teamInviteBloc;



  @override
  Widget build(BuildContext context) => widget.result.userSummaryMixin?.id !=
              null &&
          widget.result.userSummaryMixin?.id != -1
      ? Container(
          padding: const EdgeInsets.all(8.0),
          decoration: BoxDecoration(color: AppColor.leaderboardRow, boxShadow: [
            BoxShadow(
                color: Colors.black,
                blurRadius: 2.0,
                spreadRadius: 0.0,
                offset: Offset(2.0, 2.0))
          ]),
          child: Row(children: [
            Image.network(
                widget.result.userSummaryMixin?.image ??
                    ImageConstants.DEFAULT_USER_PLACEHOLDER,
                width: 24.0,
                height: 24.0),
            const SizedBox(width: 10.0),
            Image.network(
                getTierImage(widget.result.userSummaryMixin!
                    .getLevelFromMetaData(
                        _teamInviteBloc.userTournamentMixin.tournamentGroup(),context.read<ApplicationBloc>().userCurrentGame)),
                width: 18.0,
                height: 18.0),
            const SizedBox(width: 10.0),
            Expanded(
                child: RegularText(
                    widget.result.userSummaryMixin?.username ?? "")),
            const SizedBox(width: 10.0),
            InkWell(
                child: widget.result.invited
                    ? iconText(
                        Icon(Icons.check, color: AppColor.successGreen),
                        RegularText(AppStrings.inviteSent,
                            color: AppColor.successGreen))
                    : RegularText(AppStrings.inviteToTeam,
                        color: AppColor.textHighlighted),
                onTap: () {
                  if (!widget.result.invited) {
                    setState(() {
                      widget.result.invited = true;
                    });
                    _teamInviteBloc.inviteMember();
                  }
                })
          ]))
      : Container(
          padding: const EdgeInsets.all(8.0),
          decoration: BoxDecoration(color: AppColor.leaderboardRow, boxShadow: [
            BoxShadow(
                color: Colors.black,
                blurRadius: 2.0,
                spreadRadius: 0.0,
                offset: Offset(2.0, 2.0))
          ]),
          child: Row(children: [
            Icon(Icons.call, color: AppColor.successGreen),
            const SizedBox(width: 8.0),
            Expanded(
                child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                  RegularText(widget.result.userSummaryMixin?.name ?? ""),
                  RegularText(AppStrings.notOnGamerboard, fontSize: 10.0)
                ])),
            const SizedBox(width: 10.0),
            InkWell(
                child: RegularText(AppStrings.inviteToGB,
                    color: AppColor.textHighlighted),
                onTap: () {
                  ShareUtils.getInstance().teamInviteShare(_teamInviteBloc.squad,
                      _teamInviteBloc.userTournamentMixin);
                })
          ]));

  @override
  void initState() {
    super.initState();
    _teamInviteBloc = context.read<TeamInviteBloc>();

  }
}
