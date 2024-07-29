////Created by saurabh.lahoti on 06/03/22
import 'package:flutter/material.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/feature/home/home_page.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/data_type_ext.dart';
import 'package:intl/intl.dart';
import 'package:sprintf/sprintf.dart';

import '../common/widgets/text.dart';
import '../resources/colors.dart';

extension TournamentDataHelper on UserTournamentMixin {
  String getAllowedGameMode() {
    var rules = this.tournament.rules;
    if(tournament.matchType == MatchType.headToHead){
      return AppStrings.gbCustom;
    }
    if (rules is TournamentMixin$TournamentRules$BGMIRules) {
      return ("${rules.bgmiAllowedModes.first.name} ${rules.bgmiAllowedGroups.name.toString()}")
          .capitalizeFirstCharacter();
    } else if (rules is TournamentMixin$TournamentRules$FFMaxRules) {
      return ("${rules.ffAllowedModes.first.name} ${rules.ffAllowedGroups.name.toString()}")
          .capitalizeFirstCharacter();
    }
    return "Classic solo";
  }

  GameTeamGroup tournamentGroup() {
    var rules = tournament.rules;
    if (rules is TournamentMixin$TournamentRules$BGMIRules) {
      return rules.bgmiAllowedGroups.group();
    } else if (rules is TournamentMixin$TournamentRules$FFMaxRules) {
      return rules.ffAllowedGroups.group();
    }
    return GameTeamGroup.solo;
  }

  bool isSolo() {
    return !tournamentGroup().isTeamGame();
  }

  String allowedTierString() {
    String tierString = getAllowedTiers().fold("",
        (previousValue, element) => "$previousValue${getTierName(element)}, ");
    if (tierString.length > 2)
      tierString = tierString.substring(0, tierString.length - 2);
    return tierString;
  }

  String getTournamentLevelImageUrl() {
    var rules = this.tournament.rules;
    if (rules is TournamentMixin$TournamentRules$BGMIRules) {
      return rules.bgmiMaxLevel.getTierImage();
    } else if (rules is TournamentMixin$TournamentRules$FFMaxRules) {
      return rules.ffMaxLevel.getTierImage();
    }
    return BgmiLevels.bronzeFive.getTierImage();
  }

  List<Enum> getAllowedTiers() {
    var rules = this.tournament.rules;
    List<Enum> levels = [];
    if (rules is TournamentMixin$TournamentRules$BGMIRules) {
      for (int i = rules.bgmiMinLevel.index;
          i <= rules.bgmiMaxLevel.index;
          i++) {
        if (i == BgmiLevels.bronzeFive.index ||
            i == BgmiLevels.silverFive.index ||
            i == BgmiLevels.goldFive.index ||
            i == BgmiLevels.platinumFive.index ||
            i == BgmiLevels.diamondFive.index ||
            i == BgmiLevels.crownFive.index ||
            i == BgmiLevels.ace.index ||
            i == BgmiLevels.conqueror.index) levels.add(BgmiLevels.values[i]);
      }
    } else if (rules is TournamentMixin$TournamentRules$FFMaxRules) {
      for (int i = rules.ffMinLevel.index; i <= rules.ffMaxLevel.index; i++) {
        if (i == FfMaxLevels.bronzeOne.index ||
            i == FfMaxLevels.silverOne.index ||
            i == FfMaxLevels.goldOne.index ||
            i == FfMaxLevels.platinumOne.index ||
            i == FfMaxLevels.diamondOne.index ||
            i == FfMaxLevels.heroic.index ||
            i == FfMaxLevels.master.index ||
            i == FfMaxLevels.grandmasterOne.index)
          levels.add(FfMaxLevels.values[i]);
      }
    }
    return levels;
  }

  String getAllowedMaps() {
    var rules = this.tournament.rules;
    if (rules is TournamentMixin$TournamentRules$BGMIRules) {
      if (rules.bgmiAllowedMaps.length == BgmiMaps.values.length - 1)
        return "Any map";
      else {
        String map = "";
        rules.bgmiAllowedMaps.forEach((element) {
          map += element.name.toLowerCase().capitalizeFirstCharacter() + ", ";
        });
        map = map.substring(0, map.lastIndexOf(","));
        return map;
      }
    } else if (rules is TournamentMixin$TournamentRules$FFMaxRules) {
      if (rules.ffAllowedMaps.length == FfMaxMaps.values.length - 1)
        return "Any map";
      else {
        String map = "";
        rules.ffAllowedMaps.forEach((element) {
          map += element.name.toLowerCase().capitalizeFirstCharacter() + ", ";
        });
        map = map.substring(0, map.lastIndexOf(","));
        return map;
      }
    }
    return "";
  }

  TournamentUIState getTournamentState() {
    DateTime now = DateTime.now();
    GameTeamGroup? grp;
    if (this.tournament.rules is TournamentMixin$TournamentRules$BGMIRules) {
      grp = (this.tournament.rules as TournamentMixin$TournamentRules$BGMIRules)
          .bgmiAllowedGroups
          .group();
    } else if (this.tournament.rules
        is TournamentMixin$TournamentRules$FFMaxRules) {
      grp =
          (this.tournament.rules as TournamentMixin$TournamentRules$FFMaxRules)
              .ffAllowedGroups
              .group();
    }
    bool isTeamGame = grp == GameTeamGroup.duo || grp == GameTeamGroup.squad;
    if (this.joinedAt == null) {
      if (this.tournament.startTime.isAfter(now))
        return isTeamGame
            ? TournamentUIState.TEAM_UPCOMING_PRE_JOINED
            : TournamentUIState.UPCOMING_PRE_JOINED;
      else if (this.tournament.startTime.isBefore(now) &&
          this.tournament.endTime.isAfter(now)) {
        return isTeamGame
            ? TournamentUIState.TEAM_LIVE_PRE_JOINED
            : TournamentUIState.LIVE_PRE_JOINED;
      } else {
        return isTeamGame
            ? TournamentUIState.TEAM_ENDED
            : TournamentUIState.ENDED;
      }
    } else {
      if (this.tournament.startTime.isAfter(now))
        return isTeamGame
            ? TournamentUIState.TEAM_UPCOMING_JOINED
            : TournamentUIState.UPCOMING_JOINED;
      else if (this.tournament.startTime.isBefore(now) &&
          this.tournament.endTime.isAfter(now)) {
        return isTeamGame
            ? TournamentUIState.TEAM_LIVE_JOINED
            : TournamentUIState.LIVE_JOINED;
      } else {
        return isTeamGame
            ? TournamentUIState.TEAM_HISTORY
            : TournamentUIState.HISTORY;
      }
    }
  }

  TournamentUIState getCustomTournamentState() {
    DateTime now = DateTime.now();
    GameTeamGroup? grp;
    if (this.tournament.rules is TournamentMixin$TournamentRules$BGMIRules) {
      grp = (this.tournament.rules as TournamentMixin$TournamentRules$BGMIRules)
          .bgmiAllowedGroups
          .group();
    } else if (this.tournament.rules
        is TournamentMixin$TournamentRules$FFMaxRules) {
      grp =
          (this.tournament.rules as TournamentMixin$TournamentRules$FFMaxRules)
              .ffAllowedGroups
              .group();
    }
    bool isTeamGame = grp?.isTeamGame() == true;
    if (this.joinedAt == null) {
      if(this.tournament.joinBy == null){
        return isTeamGame
            ? TournamentUIState.TEAM_ENDED
            : TournamentUIState.ENDED;
      }
      if (this.tournament.joinBy!.isAfter(now))
        return isTeamGame
            ? TournamentUIState.TEAM_UPCOMING_PRE_JOINED
            : TournamentUIState.UPCOMING_PRE_JOINED;
      else if (this.tournament.startTime!.isBefore(now) &&
          this.tournament.joinBy!.isAfter(now)) {
        return isTeamGame
            ? TournamentUIState.TEAM_LIVE_PRE_JOINED
            : TournamentUIState.LIVE_PRE_JOINED;
      } else {
        return isTeamGame
            ? TournamentUIState.TEAM_ENDED
            : TournamentUIState.ENDED;
      }
    } else {
      if (this.tournament.startTime.isAfter(now))
        return isTeamGame
            ? TournamentUIState.TEAM_UPCOMING_JOINED
            : TournamentUIState.UPCOMING_JOINED;
      else if (this.tournament.startTime.isBefore(now) &&
          this.tournament.endTime.isAfter(now)) {
        return isTeamGame
            ? TournamentUIState.TEAM_LIVE_JOINED
            : TournamentUIState.LIVE_JOINED;
      } else {
        return isTeamGame
            ? TournamentUIState.TEAM_HISTORY
            : TournamentUIState.HISTORY;
      }
    }
  }

  int homeScreenIndex() {
    switch (tournamentGroup()) {
      case GameTeamGroup.duo:
        return HomeScreenIndex.DUO;
      case GameTeamGroup.squad:
        return HomeScreenIndex.SQUAD;
      case GameTeamGroup.solo:
        return HomeScreenIndex.SOLO;
    }
  }
}

extension TournamentHelper on TournamentMixin {
  GameTeamGroup tournamentGroup() {
    if (rules is TournamentMixin$TournamentRules$BGMIRules) {
      return (rules as TournamentMixin$TournamentRules$BGMIRules)
          .bgmiAllowedGroups
          .group();
    } else if (rules is TournamentMixin$TournamentRules$FFMaxRules) {
      return (rules as TournamentMixin$TournamentRules$FFMaxRules)
          .ffAllowedGroups
          .group();
    }
    return GameTeamGroup.solo;
  }
}

String getTierImage<T extends Enum>(T? t) {
  if (t is BgmiLevels)
    return t.getTierImage();
  else if (t is FfMaxLevels) return t.getTierImage();
  return ImageConstants.DEFAULT_USER_PLACEHOLDER;
}

BgmiLevels getStringToTier(String tier)
{
  switch(tier)
  {
    case "Ace":
      return BgmiLevels.ace;
    case "AceDominator":
      return BgmiLevels.aceDominator;
    case "AceMaster":
      return BgmiLevels.aceMaster;
    case "BronzeFive":
      return BgmiLevels.bronzeFive;
    case "BronzeFour":
      return BgmiLevels.bronzeFour;
    case "BronzeOne":
      return BgmiLevels.bronzeOne;
    case "BronzeThree":
      return BgmiLevels.bronzeThree;
    case "BronzeTwo":
      return BgmiLevels.bronzeTwo;
    case "Conqueror":
      return BgmiLevels.conqueror;
    case "Conqueror":
      return BgmiLevels.conqueror;
    case "CrownTwo":
      return BgmiLevels.crownTwo;
    case "CrownOne":
      return BgmiLevels.crownOne;
    case "CrownFour":
      return BgmiLevels.crownFour;
    case "CrownFive":
      return BgmiLevels.crownFive;
    case "DiamondOne":
      return BgmiLevels.diamondOne;
    case "DiamondTwo":
      return BgmiLevels.diamondTwo;
    case "DiamondThree":
      return BgmiLevels.diamondThree;
    case "DiamondFour":
      return BgmiLevels.diamondFour;
    case "DiamondFive":
      return BgmiLevels.diamondFive;
    case "GoldOne":
      return BgmiLevels.goldOne;
    case "GoldTwo":
      return BgmiLevels.goldTwo;
    case "GoldThree":
      return BgmiLevels.goldThree;
    case "GoldFour":
      return BgmiLevels.goldFour;
    case "GoldFour":
      return BgmiLevels.goldFive;
    case "GoldFive":
      return BgmiLevels.diamondFour;
    case "PlatinumOne":
      return BgmiLevels.platinumOne;
    case "PlatinumTwo":
      return BgmiLevels.platinumTwo;
    case "PlatinumThree":
      return BgmiLevels.platinumThree;
    case "PlatinumFour":
      return BgmiLevels.platinumFour;
    case "PlatinumFive":
      return BgmiLevels.platinumFive;
    case "SilverOne":
      return BgmiLevels.silverOne;
    case "SilverTwo":
      return BgmiLevels.silverTwo;
    case "SilverThree":
      return BgmiLevels.silverThree;
    case "SilverFour":
      return BgmiLevels.silverFour;
    case "SilverFive":
      return BgmiLevels.silverFive;
    case "ArtemisUnknown":
      return BgmiLevels.artemisUnknown;
    default:
      return BgmiLevels.artemisUnknown;
  }
}


String getTierName<T extends Enum>(T? t) {
  if (t is BgmiLevels)
    return t.getTierName();
  else if (t is FfMaxLevels) return t.getTierName();
  return "";
}

Enum? getBaseLevel<T extends Enum>(T? t) {
  if (t is BgmiLevels)
    return t.getBaseLevel();
  else if (t is FfMaxLevels) return t.getBaseLevel();
  return t;
}

extension _FFMaxLevelImage on FfMaxLevels {
  String getTierImage() => this.name.getTierImage("freefire");

  String getTierName() => this.name.getTierName();

  FfMaxLevels getBaseLevel() {
    FfMaxLevels baseLevel = FfMaxLevels.bronzeOne;
    final baseLevels = [
      FfMaxLevels.bronzeOne,
      FfMaxLevels.silverOne,
      FfMaxLevels.goldOne,
      FfMaxLevels.platinumOne,
      FfMaxLevels.diamondOne,
      FfMaxLevels.heroic,
      FfMaxLevels.master,
      FfMaxLevels.grandmasterOne
    ];
    int i = 0;
    while (i < baseLevels.length && this.index >= baseLevels[i].index) i++;
    baseLevel = baseLevels[--i];
    return baseLevel;
  }
}

extension MatchTypeExtenstion on MatchType {
  getMatchTypeName() {
    switch (this) {
      case MatchType.classic:
        return 'classic';
      case MatchType.headToHead:
        return 'custom';
      case MatchType.artemisUnknown:
        return 'Unknown';
    }
  }
}

extension _BgmiLevelImage on BgmiLevels {
  String getTierImage() => this.name.getTierImage("bgmi");

  String getTierName() => this.name.getTierName();

  BgmiLevels getBaseLevel() {
    BgmiLevels baseLevel = BgmiLevels.bronzeFive;
    final baseLevels = [
      BgmiLevels.bronzeFive,
      BgmiLevels.silverFive,
      BgmiLevels.goldFive,
      BgmiLevels.platinumFive,
      BgmiLevels.diamondFive,
      BgmiLevels.crownFive,
      BgmiLevels.ace,
      BgmiLevels.conqueror
    ];
    int i = 0;
    while (i < baseLevels.length && this.index >= baseLevels[i].index) i++;
    baseLevel = baseLevels[--i];
    return baseLevel;
  }
}

extension _TierDetails on String {
  String getTierImage(String game) {
    var level = this;
    int i = level.indexOf(RegExp(r'[A-Z]'));
    level = level.substring(0, i == -1 ? level.length : i);
    return sprintf(ImageConstants.TIER_PREFIX_URL, [game, level.toLowerCase()]);
  }

  String getTierName() {
    var level = this;
    int i = level.indexOf(RegExp(r'[A-Z]'));
    level = level.substring(0, i == -1 ? level.length : i);
    return level.capitalizeFirstCharacter();
  }
}

extension UserData on UserMixin {
  Enum? getGameLevelFromMetaData(
      GameTeamGroup group, UserMixin$Profile? profile) {
    if (profile != null) {
      final metaData = profile.metadata;
      if (metaData is ProfileMixin$ProfileMetadata$BgmiProfileMetadata) {
        ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevel?
            level;
        metaData.levels?.forEach((element) {
          if (element.bgmiGroup.group() == group) level = element;
        });
        return level?.bgmiLevel;
      } else if (metaData
          is ProfileMixin$ProfileMetadata$FFMaxProfileMetadata) {
        ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevel?
            level;
        metaData.levels?.forEach((element) {
          if (element.ffMaxGroup.group() == group) level = element;
        });
        return level?.ffMaxLevel;
      }
    }
    return null;
  }

  UserMixin$Profile? getCurrentGameProfile(ESports eSports) {
    final list =
        profiles?.where((element) => element.eSport == eSports).toList();
    if (list?.isNotEmpty ?? false) return list?.first;
    return null;
  }

  bool hasPlayedAnyGame(ESports currentGame) {
    var currentGameProfile = profiles
        ?.where((element) => element.eSport == currentGame)
        .toList()
        .first;
    return currentGameProfile != null &&
        currentGameProfile.profileId != null &&
        currentGameProfile.username != null;
  }
}

extension UserSummaryData on UserSummaryMixin {
  Enum? getLevelFromMetaData(GameTeamGroup group, ESports currentGame) {
    final gameProfiles =
        profiles?.where((element) => element.eSport == currentGame).toList();
    var currentGameProfile =
        gameProfiles?.isNotEmpty == true ? gameProfiles?.first : null;
    final metaData = currentGameProfile?.metadata;
    if (metaData is ProfileMixin$ProfileMetadata$BgmiProfileMetadata) {
      ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevel?
          level;
      metaData.levels?.forEach((element) {
        if (element.bgmiGroup.group() == group) level = element;
      });
      return level?.bgmiLevel;
    }

    if (metaData is ProfileMixin$ProfileMetadata$FFMaxProfileMetadata) {
      ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevel?
          level;
      metaData.levels?.forEach((element) {
        if (element.ffMaxGroup.group() == group) level = element;
      });
      return level?.ffMaxLevel;
    }
    return null;
  }
}

extension BgmiGroupUtil on BgmiGroups {
  GameTeamGroup group() {
    switch (this) {
      case BgmiGroups.duo:
        return GameTeamGroup.duo;
      case BgmiGroups.solo:
        return GameTeamGroup.solo;
      case BgmiGroups.squad:
        return GameTeamGroup.squad;
      case BgmiGroups.artemisUnknown:
        return GameTeamGroup.solo;
    }
  }
}

extension QualificationRuleUtils on CustomQualificationRuleTypes {
  String getQualifierTitle(
      String qualificationNumber, Map rules, String? tournamentName) {
    switch (this) {
      case CustomQualificationRuleTypes.numGamesSince:
        var since =
            DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'").parse(rules["since"]);
        final difference = DateTime.now().difference(since).inDays;
        return AppStrings.getNumGameQualifyTitle(
            qualificationNumber, difference.toString());
      case CustomQualificationRuleTypes.numTournamentsSince:
        var since =
            DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'").parse(rules["since"]);
        final difference = DateTime.now().difference(since).inDays;
        return AppStrings.getNumTournamentQualifyTitle(
            qualificationNumber, difference.toString());
      case CustomQualificationRuleTypes.rankByTournament:
        return AppStrings.getRankByTournamentQualifyTitle(
            qualificationNumber, tournamentName!);
      case CustomQualificationRuleTypes.rankSince:
        var since =
            DateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'").parse(rules["since"]);
        final difference = DateTime.now().difference(since).inDays;
        return AppStrings.getOnlyWinnersWhoQualifyTitle(
            rules["maxRank"].toString(), difference.toString());
      case CustomQualificationRuleTypes.signupAge:
        return AppStrings.getOnlyVeteransQualifyTitle(qualificationNumber);
      default:
        return "";
    }
  }

  String getQualifierSubTitle(int? current, int? required) {
    switch (this) {
      case CustomQualificationRuleTypes.numGamesSince:
        return "Play ${(required! - current!)} games to qualify";
      case CustomQualificationRuleTypes.numTournamentsSince:
        return "Play ${(required! - current!)} Tournaments to qualify";
      case CustomQualificationRuleTypes.rankByTournament:
        return "Keep improving to qualify";
      case CustomQualificationRuleTypes.rankSince:
        return "Keep improving to qualify";
      case CustomQualificationRuleTypes.signupAge:
        return "keep playing for ${(required! - current!)} days to qualify";
      default:
        return "";
    }
  }

  Widget getQualifierResultTitle(int current, int? required) {
    switch (this) {
      case CustomQualificationRuleTypes.numGamesSince:
        return Column(
          children: [
            RichText(
                text: TextSpan(children: [
              TextSpan(
                text: "$current",
                style:
                    BoldTextStyle(fontSize: 30.0, color: AppColor.successGreen),
              ),
              TextSpan(
                  text: "/${required!}",
                  style: BoldTextStyle(
                      fontSize: 26.0, color: AppColor.successGreen))
            ])),
            Text(
              AppStrings.gamesPlayedRecently,
              style:
                  BoldTextStyle(color: AppColor.grayText747474, fontSize: 16),
            )
          ],
        );
      case CustomQualificationRuleTypes.numTournamentsSince:
        return Column(
          children: [
            RichText(
                text: TextSpan(children: [
              TextSpan(
                text: "$current",
                style:
                    BoldTextStyle(fontSize: 30.0, color: AppColor.successGreen),
              ),
              TextSpan(
                  text: "/${required!}",
                  style: BoldTextStyle(
                      fontSize: 26.0, color: AppColor.successGreen))
            ])),
            Text(
              AppStrings.tournamentsPlayedRecently,
              style:
                  BoldTextStyle(color: AppColor.grayText747474, fontSize: 16),
            )
          ],
        );
      case CustomQualificationRuleTypes.rankByTournament:
        return Text(AppStrings.youDontQualify,
            style: BoldTextStyle(fontSize: 26.0, color: AppColor.successGreen));
      case CustomQualificationRuleTypes.rankSince:
        return Text(AppStrings.youDontQualify,
            style: BoldTextStyle(fontSize: 26.0, color: AppColor.successGreen));
      case CustomQualificationRuleTypes.signupAge:
        return Column(
          children: [
            RichText(
                text: TextSpan(children: [
              TextSpan(
                text: "$current",
                style:
                    BoldTextStyle(fontSize: 30.0, color: AppColor.successGreen),
              ),
              TextSpan(
                  text: "/${required!}",
                  style: BoldTextStyle(
                      fontSize: 26.0, color: AppColor.successGreen))
            ])),
            Text(
              AppStrings.daysSinceSignup,
              style:
                  BoldTextStyle(color: AppColor.grayText747474, fontSize: 16),
            )
          ],
        );
      default:
        return Column(
          children: [
            RichText(
                text: TextSpan(children: [
              TextSpan(
                text: "$current",
                style:
                    BoldTextStyle(fontSize: 30.0, color: AppColor.successGreen),
              ),
              TextSpan(
                  text: "/${required!}",
                  style: BoldTextStyle(
                      fontSize: 26.0, color: AppColor.successGreen))
            ])),
            Text(
              AppStrings.daysSinceSignup,
              style:
                  BoldTextStyle(color: AppColor.grayText747474, fontSize: 16),
            )
          ],
        );
    }
  }

  bool getProgressByIndicatorStatus() {
    switch (this) {
      case CustomQualificationRuleTypes.numGamesSince:
        return true;
      case CustomQualificationRuleTypes.numTournamentsSince:
        return true;
      case CustomQualificationRuleTypes.rankByTournament:
        return false;
      case CustomQualificationRuleTypes.rankSince:
        return false;
      case CustomQualificationRuleTypes.signupAge:
        return true;
      default:
        return false;
    }
  }
}

CustomQualificationRuleTypes getQualificationRule(String rule) {
  switch (rule) {
    case "NUM_GAMES_SINCE":
      return CustomQualificationRuleTypes.numGamesSince;
    case "NUM_TOURNAMENTS_SINCE":
      return CustomQualificationRuleTypes.numTournamentsSince;
    case "RANK_BY_TOURNAMENT":
      return CustomQualificationRuleTypes.rankByTournament;
    case "RANK_SINCE":
      return CustomQualificationRuleTypes.rankSince;
    case "SIGNUP_AGE":
      return CustomQualificationRuleTypes.signupAge;
    default:
      return CustomQualificationRuleTypes.signupAge;
  }
}

extension FFGroupUtil on FfMaxGroups {
  GameTeamGroup group() {
    switch (this) {
      case FfMaxGroups.duo:
        return GameTeamGroup.duo;
      case FfMaxGroups.solo:
        return GameTeamGroup.solo;
      case FfMaxGroups.squad:
        return GameTeamGroup.squad;
      case FfMaxGroups.artemisUnknown:
        return GameTeamGroup.solo;
    }
  }
}

extension BgmiSquadUtil on SquadMixin {
  MapEntry<String, Color> squadStatusAndColor(
      int? myId, GameTeamGroup teamGroup) {
    String? notReady;
    String status = "";
    Color color = AppColor.successGreen;
    members.forEach((element) {
      if (element.user.id == myId && !element.isReady) {
        status = AppStrings.paymentPending;
        color = AppColor.errorRed;
      }
      if (element.isReady) notReady = element.status;
    });
    if (status.isEmpty) {
      if (notReady != null || members.length != teamGroup.teamSize()) {
        status =
            "${members.length}/${teamGroup.teamSize()} ${AppStrings.players}";
        color = AppColor.errorRed;
      } else {
        status = AppStrings.ready;
        color = AppColor.successGreen;
      }
    }
    return MapEntry(status, color);
  }

  bool userIsInSquad(int? userId) {
    bool found = members.fold(false,
        (previousValue, element) => previousValue || element.user.id == userId);
    return found;
  }
}

enum TournamentUIState {
  TEAM_UPCOMING_PRE_JOINED,
  TEAM_LIVE_PRE_JOINED,
  TEAM_UPCOMING_JOINED,
  TEAM_LIVE_JOINED,
  TEAM_HISTORY,
  TEAM_ENDED,
  UPCOMING_PRE_JOINED,
  LIVE_PRE_JOINED,
  UPCOMING_JOINED,
  LIVE_JOINED,
  HISTORY,
  ENDED
}

enum GameTeamGroup { solo, duo, squad }

extension GameTeamGroupExt on GameTeamGroup {
  int teamSize() =>
      this == GameTeamGroup.solo ? 1 : (this == GameTeamGroup.duo ? 2 : 4);

  BgmiGroups toBGMIGroup() {
    switch (this) {
      case GameTeamGroup.solo:
        return BgmiGroups.solo;
      case GameTeamGroup.duo:
        return BgmiGroups.duo;
      case GameTeamGroup.squad:
        return BgmiGroups.squad;
    }
  }

  String name() {
    switch (this) {
      case GameTeamGroup.solo:
        return AppStrings.solo;
      case GameTeamGroup.duo:
        return AppStrings.duo;
      case GameTeamGroup.squad:
        return AppStrings.squad;
    }
  }

  FfMaxGroups toFreeFireGroup() {
    switch (this) {
      case GameTeamGroup.solo:
        return FfMaxGroups.solo;
      case GameTeamGroup.duo:
        return FfMaxGroups.duo;
      case GameTeamGroup.squad:
        return FfMaxGroups.squad;
    }
  }

  String getSuffix() {
    //TODO: Once fixed in backend we can change back to appropriate
    //Suffix
    switch (this) {
      case GameTeamGroup.solo:
        return AppStrings.players;
      case GameTeamGroup.duo:
      case GameTeamGroup.squad:
        return AppStrings.teams;
    }
  }

  bool isTeamGame() => this == GameTeamGroup.duo || this == GameTeamGroup.squad;
}

extension ESportExt on ESports {
  String getShortName() {
    switch (this) {
      case ESports.bgmi:
        return "BGMI";
      case ESports.freefiremax:
        return "FF Max";
      case ESports.artemisUnknown:
        return "";
    }
  }

  String getPackageName() {
    switch (this) {
      case ESports.bgmi:
        return "com.pubg.imobile";
      case ESports.freefiremax:
        return "com.dts.freefiremax";
      default:
        return "";
    }
  }

  String getTierFinder() {
    switch (this) {
      case ESports.bgmi:
        return URLConstants.HELP_BGMI_TIER_FINDER;
      case ESports.freefiremax:
        return URLConstants.HELP_FFMAX_TIER_FINDER;
      default:
        return "";
    }
  }

  String getGameName() {
    switch (this) {
      case ESports.bgmi:
        return "Battles ground mobile india";
      case ESports.freefiremax:
        return "Garena free fire max";
      default:
        return "";
    }
  }

  String getGameLogo() {
    switch (this) {
      case ESports.bgmi:
        return "ic_bgmi_logo.png";
      case ESports.freefiremax:
        return "ic_freefire_logo.png";
      default:
        return "";
    }
  }

  String getFindTierImage() {
    switch (this) {
      case ESports.bgmi:
        return "ic_find_tier.gif";
      case ESports.freefiremax:
        return "ic_find_ffmax_tier.gif";
      default:
        return "";
    }
  }

  Map<Enum, String> getTierMap() {
    switch (this) {
      case ESports.bgmi:
        return {
          BgmiLevels.bronzeFive: "Bronze (I-V)",
          BgmiLevels.silverFive: "Silver (I-V)",
          BgmiLevels.goldFive: "Gold (I-V)",
          BgmiLevels.platinumFive: "Platinum (I-V)",
          BgmiLevels.diamondFive: "Diamond (I-V)",
          BgmiLevels.crownFive: "Crown (I-V)",
          BgmiLevels.ace: "Ace (Ace, Master, Dominator)",
          BgmiLevels.conqueror: "Conqueror"
        };
      case ESports.freefiremax:
        return {
          FfMaxLevels.bronzeOne: "Bronze (I-III)",
          FfMaxLevels.silverOne: "Silver (I-III)",
          FfMaxLevels.goldOne: "Gold (I-IV)",
          FfMaxLevels.platinumOne: "Platinum (I-IV)",
          FfMaxLevels.diamondOne: "Diamond (I-IV)",
          FfMaxLevels.heroic: "Heroic",
          FfMaxLevels.master: "Master",
          FfMaxLevels.grandmasterOne: "Grandmaster (I-VI)"
        };
      case ESports.artemisUnknown:
        return {};
    }
  }
}


extension playingReasonPreferenceExtenstion on PlayingReasonPreference
{
  getDialogText() {
    switch (this) {
      case PlayingReasonPreference.competitive:
        return "I am a professional";
      case PlayingReasonPreference.fun:
        return "For fun";
      case PlayingReasonPreference.learning:
        return "I am learning";
      case PlayingReasonPreference.rewards:
        return "For rewards";
      case PlayingReasonPreference.artemisUnknown:
        return "artemisUnknown";
    }
  }
}


extension timeOfDayPreferenceExtenstion on TimeOfDayPreference
{
  getDialogText() {
    switch (this) {
      case TimeOfDayPreference.morning:
        return "Morning";
      case TimeOfDayPreference.afternoon:
        return "Afternoon";
      case TimeOfDayPreference.evening:
        return "Evening";
      case TimeOfDayPreference.artemisUnknown:
        return "artemisUnknown";
    }
  }
}

extension rolePreferenceExtenstion on RolePreference
{
  getDialogText() {
    switch (this) {
      case RolePreference.assaulter:
        return "Assaulter";
      case RolePreference.coach:
        return "Coach";
      case RolePreference.commander:
        return "Commander";
      case RolePreference.fragger:
        return "Fragger";
      case RolePreference.healer:
        return "Healer";
      case RolePreference.igl:
        return "In-Game Leader";
      case RolePreference.scout:
        return "Scout";
      case RolePreference.sniper:
        return "Sniper";
      case RolePreference.support:
        return "Support";
      case RolePreference.vehicleSpecialist:
        return "Vehicle Specialist";
      case RolePreference.artemisUnknown:
        return "artemisUnknown";
    }
  }
}

extension timeOfWeekPreferenceExtenstion on TimeOfWeekPreference
{
  getDialogText() {
    switch (this) {
      case TimeOfWeekPreference.weekdays:
        return "Weekdays";
      case TimeOfWeekPreference.weekends:
        return "Weekends";
      case TimeOfWeekPreference.both:
        return "Both";
      case TimeOfWeekPreference.artemisUnknown:
        return "artemisUnknown";
    }
  }
}

