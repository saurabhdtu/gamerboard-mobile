// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'query.graphql.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

VerifyOTP$Query$LoggedInUser$User _$VerifyOTP$Query$LoggedInUser$UserFromJson(
        Map<String, dynamic> json) =>
    VerifyOTP$Query$LoggedInUser$User()..id = json['id'] as int;

Map<String, dynamic> _$VerifyOTP$Query$LoggedInUser$UserToJson(
        VerifyOTP$Query$LoggedInUser$User instance) =>
    <String, dynamic>{
      'id': instance.id,
    };

VerifyOTP$Query$LoggedInUser _$VerifyOTP$Query$LoggedInUserFromJson(
        Map<String, dynamic> json) =>
    VerifyOTP$Query$LoggedInUser()
      ..token = json['token'] as String
      ..user = json['user'] == null
          ? null
          : VerifyOTP$Query$LoggedInUser$User.fromJson(
              json['user'] as Map<String, dynamic>);

Map<String, dynamic> _$VerifyOTP$Query$LoggedInUserToJson(
        VerifyOTP$Query$LoggedInUser instance) =>
    <String, dynamic>{
      'token': instance.token,
      'user': instance.user?.toJson(),
    };

VerifyOTP$Query _$VerifyOTP$QueryFromJson(Map<String, dynamic> json) =>
    VerifyOTP$Query()
      ..login = VerifyOTP$Query$LoggedInUser.fromJson(
          json['login'] as Map<String, dynamic>);

Map<String, dynamic> _$VerifyOTP$QueryToJson(VerifyOTP$Query instance) =>
    <String, dynamic>{
      'login': instance.login.toJson(),
    };

LoginOTPLess$Query$LoggedInUser$User
    _$LoginOTPLess$Query$LoggedInUser$UserFromJson(Map<String, dynamic> json) =>
        LoginOTPLess$Query$LoggedInUser$User()..id = json['id'] as int;

Map<String, dynamic> _$LoginOTPLess$Query$LoggedInUser$UserToJson(
        LoginOTPLess$Query$LoggedInUser$User instance) =>
    <String, dynamic>{
      'id': instance.id,
    };

LoginOTPLess$Query$LoggedInUser _$LoginOTPLess$Query$LoggedInUserFromJson(
        Map<String, dynamic> json) =>
    LoginOTPLess$Query$LoggedInUser()
      ..token = json['token'] as String
      ..user = json['user'] == null
          ? null
          : LoginOTPLess$Query$LoggedInUser$User.fromJson(
              json['user'] as Map<String, dynamic>);

Map<String, dynamic> _$LoginOTPLess$Query$LoggedInUserToJson(
        LoginOTPLess$Query$LoggedInUser instance) =>
    <String, dynamic>{
      'token': instance.token,
      'user': instance.user?.toJson(),
    };

LoginOTPLess$Query _$LoginOTPLess$QueryFromJson(Map<String, dynamic> json) =>
    LoginOTPLess$Query()
      ..loginOTPLess = LoginOTPLess$Query$LoggedInUser.fromJson(
          json['loginOTPLess'] as Map<String, dynamic>);

Map<String, dynamic> _$LoginOTPLess$QueryToJson(LoginOTPLess$Query instance) =>
    <String, dynamic>{
      'loginOTPLess': instance.loginOTPLess.toJson(),
    };

CheckUniqueUser$Query$UniqueUserResponse
    _$CheckUniqueUser$Query$UniqueUserResponseFromJson(
            Map<String, dynamic> json) =>
        CheckUniqueUser$Query$UniqueUserResponse()
          ..phone = json['phone'] as bool
          ..username = json['username'] as bool;

Map<String, dynamic> _$CheckUniqueUser$Query$UniqueUserResponseToJson(
        CheckUniqueUser$Query$UniqueUserResponse instance) =>
    <String, dynamic>{
      'phone': instance.phone,
      'username': instance.username,
    };

CheckUniqueUser$Query _$CheckUniqueUser$QueryFromJson(
        Map<String, dynamic> json) =>
    CheckUniqueUser$Query()
      ..checkUniqueUser = CheckUniqueUser$Query$UniqueUserResponse.fromJson(
          json['checkUniqueUser'] as Map<String, dynamic>);

Map<String, dynamic> _$CheckUniqueUser$QueryToJson(
        CheckUniqueUser$Query instance) =>
    <String, dynamic>{
      'checkUniqueUser': instance.checkUniqueUser.toJson(),
    };

GetMyProfile$Query$User _$GetMyProfile$Query$UserFromJson(
        Map<String, dynamic> json) =>
    GetMyProfile$Query$User()
      ..id = json['id'] as int
      ..birthdate = fromGraphQLDateNullableToDartDateTimeNullable(
          json['birthdate'] as String?)
      ..name = json['name'] as String
      ..phone = json['phone'] as String?
      ..image = json['image'] as String?
      ..achievements = UserMixin$Achievement.fromJson(
          json['achievements'] as Map<String, dynamic>)
      ..inviteCode = json['inviteCode'] as String
      ..preferences = json['preferences'] == null
          ? null
          : UserMixin$UserPreference.fromJson(
              json['preferences'] as Map<String, dynamic>)
      ..profiles = (json['profiles'] as List<dynamic>?)
          ?.map((e) => UserMixin$Profile.fromJson(e as Map<String, dynamic>))
          .toList()
      ..wallet =
          UserMixin$Wallet.fromJson(json['wallet'] as Map<String, dynamic>)
      ..flags =
          UserMixin$UserFlags.fromJson(json['flags'] as Map<String, dynamic>)
      ..username = json['username'] as String;

Map<String, dynamic> _$GetMyProfile$Query$UserToJson(
        GetMyProfile$Query$User instance) =>
    <String, dynamic>{
      'id': instance.id,
      'birthdate':
          fromDartDateTimeNullableToGraphQLDateNullable(instance.birthdate),
      'name': instance.name,
      'phone': instance.phone,
      'image': instance.image,
      'achievements': instance.achievements.toJson(),
      'inviteCode': instance.inviteCode,
      'preferences': instance.preferences?.toJson(),
      'profiles': instance.profiles?.map((e) => e.toJson()).toList(),
      'wallet': instance.wallet.toJson(),
      'flags': instance.flags.toJson(),
      'username': instance.username,
    };

GetMyProfile$Query _$GetMyProfile$QueryFromJson(Map<String, dynamic> json) =>
    GetMyProfile$Query()
      ..me =
          GetMyProfile$Query$User.fromJson(json['me'] as Map<String, dynamic>);

Map<String, dynamic> _$GetMyProfile$QueryToJson(GetMyProfile$Query instance) =>
    <String, dynamic>{
      'me': instance.me.toJson(),
    };

UserMixin$Achievement$UserTournament
    _$UserMixin$Achievement$UserTournamentFromJson(Map<String, dynamic> json) =>
        UserMixin$Achievement$UserTournament()
          ..joinedAt = fromGraphQLDateTimeNullableToDartDateTimeNullable(
              json['joinedAt'] as String?)
          ..rank = json['rank'] as int?
          ..score = json['score'] as int?
          ..tournament = UserTournamentMixin$Tournament.fromJson(
              json['tournament'] as Map<String, dynamic>)
          ..squad = json['squad'] == null
              ? null
              : UserTournamentMixin$Squad.fromJson(
                  json['squad'] as Map<String, dynamic>)
          ..tournamentMatchUser = json['tournamentMatchUser'] == null
              ? null
              : UserTournamentMixin$TournamentMatchUser.fromJson(
                  json['tournamentMatchUser'] as Map<String, dynamic>);

Map<String, dynamic> _$UserMixin$Achievement$UserTournamentToJson(
        UserMixin$Achievement$UserTournament instance) =>
    <String, dynamic>{
      'joinedAt':
          fromDartDateTimeNullableToGraphQLDateTimeNullable(instance.joinedAt),
      'rank': instance.rank,
      'score': instance.score,
      'tournament': instance.tournament.toJson(),
      'squad': instance.squad?.toJson(),
      'tournamentMatchUser': instance.tournamentMatchUser?.toJson(),
    };

UserMixin$Achievement$AchievementSummary
    _$UserMixin$Achievement$AchievementSummaryFromJson(
            Map<String, dynamic> json) =>
        UserMixin$Achievement$AchievementSummary()
          ..kDRatio = (json['KDRatio'] as num?)?.toDouble()
          ..eSport = $enumDecode(_$ESportsEnumMap, json['eSport'],
              unknownValue: ESports.artemisUnknown)
          ..group = json['group'] as String
          ..matchType = $enumDecode(_$MatchTypeEnumMap, json['matchType'],
              unknownValue: MatchType.artemisUnknown)
          ..maxTier = json['maxTier'] as String?
          ..played = json['played'] as int
          ..topTenCount = json['topTenCount'] as int;

Map<String, dynamic> _$UserMixin$Achievement$AchievementSummaryToJson(
        UserMixin$Achievement$AchievementSummary instance) =>
    <String, dynamic>{
      'KDRatio': instance.kDRatio,
      'eSport': _$ESportsEnumMap[instance.eSport]!,
      'group': instance.group,
      'matchType': _$MatchTypeEnumMap[instance.matchType]!,
      'maxTier': instance.maxTier,
      'played': instance.played,
      'topTenCount': instance.topTenCount,
    };

const _$ESportsEnumMap = {
  ESports.bgmi: 'BGMI',
  ESports.freefiremax: 'FREEFIREMAX',
  ESports.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$MatchTypeEnumMap = {
  MatchType.classic: 'classic',
  MatchType.headToHead: 'headToHead',
  MatchType.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

UserMixin$Achievement _$UserMixin$AchievementFromJson(
        Map<String, dynamic> json) =>
    UserMixin$Achievement()
      ..bestPerformance = json['bestPerformance'] == null
          ? null
          : UserMixin$Achievement$UserTournament.fromJson(
              json['bestPerformance'] as Map<String, dynamic>)
      ..tournamentSummary = (json['tournamentSummary'] as List<dynamic>)
          .map((e) => e == null
              ? null
              : UserMixin$Achievement$AchievementSummary.fromJson(
                  e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$UserMixin$AchievementToJson(
        UserMixin$Achievement instance) =>
    <String, dynamic>{
      'bestPerformance': instance.bestPerformance?.toJson(),
      'tournamentSummary':
          instance.tournamentSummary.map((e) => e?.toJson()).toList(),
    };

UserMixin$UserPreference _$UserMixin$UserPreferenceFromJson(
        Map<String, dynamic> json) =>
    UserMixin$UserPreference()
      ..playingReason = (json['playingReason'] as List<dynamic>?)
          ?.map((e) => $enumDecode(_$PlayingReasonPreferenceEnumMap, e,
              unknownValue: PlayingReasonPreference.artemisUnknown))
          .toList()
      ..roles = (json['roles'] as List<dynamic>?)
          ?.map((e) => $enumDecode(_$RolePreferenceEnumMap, e,
              unknownValue: RolePreference.artemisUnknown))
          .toList()
      ..timeOfDay = (json['timeOfDay'] as List<dynamic>?)
          ?.map((e) => $enumDecode(_$TimeOfDayPreferenceEnumMap, e,
              unknownValue: TimeOfDayPreference.artemisUnknown))
          .toList()
      ..timeOfWeek = $enumDecodeNullable(
          _$TimeOfWeekPreferenceEnumMap, json['timeOfWeek'],
          unknownValue: TimeOfWeekPreference.artemisUnknown);

Map<String, dynamic> _$UserMixin$UserPreferenceToJson(
        UserMixin$UserPreference instance) =>
    <String, dynamic>{
      'playingReason': instance.playingReason
          ?.map((e) => _$PlayingReasonPreferenceEnumMap[e]!)
          .toList(),
      'roles': instance.roles?.map((e) => _$RolePreferenceEnumMap[e]!).toList(),
      'timeOfDay': instance.timeOfDay
          ?.map((e) => _$TimeOfDayPreferenceEnumMap[e]!)
          .toList(),
      'timeOfWeek': _$TimeOfWeekPreferenceEnumMap[instance.timeOfWeek],
    };

const _$PlayingReasonPreferenceEnumMap = {
  PlayingReasonPreference.competitive: 'Competitive',
  PlayingReasonPreference.fun: 'Fun',
  PlayingReasonPreference.learning: 'Learning',
  PlayingReasonPreference.rewards: 'Rewards',
  PlayingReasonPreference.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$RolePreferenceEnumMap = {
  RolePreference.assaulter: 'Assaulter',
  RolePreference.coach: 'Coach',
  RolePreference.commander: 'Commander',
  RolePreference.fragger: 'Fragger',
  RolePreference.healer: 'Healer',
  RolePreference.igl: 'IGL',
  RolePreference.scout: 'Scout',
  RolePreference.sniper: 'Sniper',
  RolePreference.support: 'Support',
  RolePreference.vehicleSpecialist: 'VehicleSpecialist',
  RolePreference.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$TimeOfDayPreferenceEnumMap = {
  TimeOfDayPreference.morning: 'Morning',
  TimeOfDayPreference.afternoon: 'Afternoon',
  TimeOfDayPreference.evening: 'Evening',
  TimeOfDayPreference.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$TimeOfWeekPreferenceEnumMap = {
  TimeOfWeekPreference.weekdays: 'Weekdays',
  TimeOfWeekPreference.weekends: 'Weekends',
  TimeOfWeekPreference.both: 'Both',
  TimeOfWeekPreference.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

UserMixin$Profile _$UserMixin$ProfileFromJson(Map<String, dynamic> json) =>
    UserMixin$Profile()
      ..eSport = $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown)
      ..metadata = ProfileMixin$ProfileMetadata.fromJson(
          json['metadata'] as Map<String, dynamic>)
      ..username = json['username'] as String?
      ..profileId = json['profileId'] as String?;

Map<String, dynamic> _$UserMixin$ProfileToJson(UserMixin$Profile instance) =>
    <String, dynamic>{
      'eSport': _$ESportsEnumMap[instance.eSport]!,
      'metadata': instance.metadata.toJson(),
      'username': instance.username,
      'profileId': instance.profileId,
    };

UserMixin$Wallet _$UserMixin$WalletFromJson(Map<String, dynamic> json) =>
    UserMixin$Wallet()
      ..id = json['id'] as int
      ..bonus = (json['bonus'] as num).toDouble()
      ..deposit = (json['deposit'] as num).toDouble()
      ..winning = (json['winning'] as num).toDouble();

Map<String, dynamic> _$UserMixin$WalletToJson(UserMixin$Wallet instance) =>
    <String, dynamic>{
      'id': instance.id,
      'bonus': instance.bonus,
      'deposit': instance.deposit,
      'winning': instance.winning,
    };

UserMixin$UserFlags _$UserMixin$UserFlagsFromJson(Map<String, dynamic> json) =>
    UserMixin$UserFlags()
      ..allowSquadCreation = json['allowSquadCreation'] as bool?;

Map<String, dynamic> _$UserMixin$UserFlagsToJson(
        UserMixin$UserFlags instance) =>
    <String, dynamic>{
      'allowSquadCreation': instance.allowSquadCreation,
    };

UserTournamentMixin$Tournament _$UserTournamentMixin$TournamentFromJson(
        Map<String, dynamic> json) =>
    UserTournamentMixin$Tournament()
      ..eSport = $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown)
      ..id = json['id'] as int
      ..name = json['name'] as String
      ..maxPrize = json['maxPrize'] as int
      ..userCount = json['userCount'] as int
      ..gameCount = json['gameCount'] as int
      ..fee = json['fee'] as int
      ..matchType = $enumDecode(_$MatchTypeEnumMap, json['matchType'],
          unknownValue: MatchType.artemisUnknown)
      ..joinBy = fromGraphQLDateTimeNullableToDartDateTimeNullable(
          json['joinBy'] as String?)
      ..joinCode = json['joinCode'] as String?
      ..qualifiers = (json['qualifiers'] as List<dynamic>?)
          ?.map((e) => TournamentMixin$CustomQualificationRules.fromJson(
              e as Map<String, dynamic>))
          .toList()
      ..winningDistribution = (json['winningDistribution'] as List<dynamic>)
          .map((e) => TournamentMixin$WinningDistribution.fromJson(
              e as Map<String, dynamic>))
          .toList()
      ..matches = (json['matches'] as List<dynamic>?)
          ?.map((e) => TournamentMixin$TournamentMatch.fromJson(
              e as Map<String, dynamic>))
          .toList()
      ..rules = TournamentMixin$TournamentRules.fromJson(
          json['rules'] as Map<String, dynamic>)
      ..startTime =
          fromGraphQLDateTimeToDartDateTime(json['startTime'] as String)
      ..endTime = fromGraphQLDateTimeToDartDateTime(json['endTime'] as String);

Map<String, dynamic> _$UserTournamentMixin$TournamentToJson(
        UserTournamentMixin$Tournament instance) =>
    <String, dynamic>{
      'eSport': _$ESportsEnumMap[instance.eSport]!,
      'id': instance.id,
      'name': instance.name,
      'maxPrize': instance.maxPrize,
      'userCount': instance.userCount,
      'gameCount': instance.gameCount,
      'fee': instance.fee,
      'matchType': _$MatchTypeEnumMap[instance.matchType]!,
      'joinBy':
          fromDartDateTimeNullableToGraphQLDateTimeNullable(instance.joinBy),
      'joinCode': instance.joinCode,
      'qualifiers': instance.qualifiers?.map((e) => e.toJson()).toList(),
      'winningDistribution':
          instance.winningDistribution.map((e) => e.toJson()).toList(),
      'matches': instance.matches?.map((e) => e.toJson()).toList(),
      'rules': instance.rules.toJson(),
      'startTime': fromDartDateTimeToGraphQLDateTime(instance.startTime),
      'endTime': fromDartDateTimeToGraphQLDateTime(instance.endTime),
    };

UserTournamentMixin$Squad _$UserTournamentMixin$SquadFromJson(
        Map<String, dynamic> json) =>
    UserTournamentMixin$Squad()
      ..name = json['name'] as String
      ..id = json['id'] as int
      ..inviteCode = json['inviteCode'] as String
      ..members = (json['members'] as List<dynamic>)
          .map(
              (e) => SquadMixin$SquadMember.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$UserTournamentMixin$SquadToJson(
        UserTournamentMixin$Squad instance) =>
    <String, dynamic>{
      'name': instance.name,
      'id': instance.id,
      'inviteCode': instance.inviteCode,
      'members': instance.members.map((e) => e.toJson()).toList(),
    };

UserTournamentMixin$TournamentMatchUser
    _$UserTournamentMixin$TournamentMatchUserFromJson(
            Map<String, dynamic> json) =>
        UserTournamentMixin$TournamentMatchUser()
          ..slotInfo = json['slotInfo'] == null
              ? null
              : TournamentMatchUserMixin$SlotInfo.fromJson(
                  json['slotInfo'] as Map<String, dynamic>)
          ..id = json['id'] as int
          ..tournamentMatchId = json['tournamentMatchId'] as int
          ..tournamentUserId = json['tournamentUserId'] as int
          ..notJoined = json['notJoined'] as bool
          ..sos = fromGraphQLDateTimeNullableToDartDateTimeNullable(
              json['sos'] as String?);

Map<String, dynamic> _$UserTournamentMixin$TournamentMatchUserToJson(
        UserTournamentMixin$TournamentMatchUser instance) =>
    <String, dynamic>{
      'slotInfo': instance.slotInfo?.toJson(),
      'id': instance.id,
      'tournamentMatchId': instance.tournamentMatchId,
      'tournamentUserId': instance.tournamentUserId,
      'notJoined': instance.notJoined,
      'sos': fromDartDateTimeNullableToGraphQLDateTimeNullable(instance.sos),
    };

TournamentMixin$CustomQualificationRules
    _$TournamentMixin$CustomQualificationRulesFromJson(
            Map<String, dynamic> json) =>
        TournamentMixin$CustomQualificationRules()
          ..rule = $enumDecode(
              _$CustomQualificationRuleTypesEnumMap, json['rule'],
              unknownValue: CustomQualificationRuleTypes.artemisUnknown)
          ..value = fromGraphQLJsonToDartMap(json['value'] as Map?);

Map<String, dynamic> _$TournamentMixin$CustomQualificationRulesToJson(
        TournamentMixin$CustomQualificationRules instance) =>
    <String, dynamic>{
      'rule': _$CustomQualificationRuleTypesEnumMap[instance.rule]!,
      'value': fromDartMapToGraphQLJson(instance.value),
    };

const _$CustomQualificationRuleTypesEnumMap = {
  CustomQualificationRuleTypes.numGamesSince: 'NUM_GAMES_SINCE',
  CustomQualificationRuleTypes.numTournamentsSince: 'NUM_TOURNAMENTS_SINCE',
  CustomQualificationRuleTypes.rankByTournament: 'RANK_BY_TOURNAMENT',
  CustomQualificationRuleTypes.rankSince: 'RANK_SINCE',
  CustomQualificationRuleTypes.signupAge: 'SIGNUP_AGE',
  CustomQualificationRuleTypes.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

TournamentMixin$WinningDistribution
    _$TournamentMixin$WinningDistributionFromJson(Map<String, dynamic> json) =>
        TournamentMixin$WinningDistribution()
          ..startRank = json['startRank'] as int
          ..endRank = json['endRank'] as int
          ..value = (json['value'] as num).toDouble();

Map<String, dynamic> _$TournamentMixin$WinningDistributionToJson(
        TournamentMixin$WinningDistribution instance) =>
    <String, dynamic>{
      'startRank': instance.startRank,
      'endRank': instance.endRank,
      'value': instance.value,
    };

TournamentMixin$TournamentMatch _$TournamentMixin$TournamentMatchFromJson(
        Map<String, dynamic> json) =>
    TournamentMixin$TournamentMatch()
      ..tournamentId = json['tournamentId'] as int
      ..endTime = fromGraphQLDateTimeNullableToDartDateTimeNullable(
          json['endTime'] as String?)
      ..startTime =
          fromGraphQLDateTimeToDartDateTime(json['startTime'] as String)
      ..id = json['id'] as int
      ..maxParticipants = json['maxParticipants'] as int
      ..minParticipants = json['minParticipants'] as int?
      ..metadata = json['metadata'] == null
          ? null
          : TournamentMatchMixin$TournamentMatchMetadata.fromJson(
              json['metadata'] as Map<String, dynamic>);

Map<String, dynamic> _$TournamentMixin$TournamentMatchToJson(
        TournamentMixin$TournamentMatch instance) =>
    <String, dynamic>{
      'tournamentId': instance.tournamentId,
      'endTime':
          fromDartDateTimeNullableToGraphQLDateTimeNullable(instance.endTime),
      'startTime': fromDartDateTimeToGraphQLDateTime(instance.startTime),
      'id': instance.id,
      'maxParticipants': instance.maxParticipants,
      'minParticipants': instance.minParticipants,
      'metadata': instance.metadata?.toJson(),
    };

TournamentMixin$TournamentRules$BGMIRules
    _$TournamentMixin$TournamentRules$BGMIRulesFromJson(
            Map<String, dynamic> json) =>
        TournamentMixin$TournamentRules$BGMIRules()
          ..$$typename = json['__typename'] as String?
          ..minUsers = json['minUsers'] as int
          ..maxUsers = json['maxUsers'] as int
          ..maxTeams = json['maxTeams'] as int
          ..bgmiMaxLevel = $enumDecode(
              _$BgmiLevelsEnumMap, json['bgmiMaxLevel'],
              unknownValue: BgmiLevels.artemisUnknown)
          ..bgmiMinLevel = $enumDecode(
              _$BgmiLevelsEnumMap, json['bgmiMinLevel'],
              unknownValue: BgmiLevels.artemisUnknown)
          ..bgmiAllowedModes = (json['bgmiAllowedModes'] as List<dynamic>)
              .map((e) => $enumDecode(_$BgmiModesEnumMap, e,
                  unknownValue: BgmiModes.artemisUnknown))
              .toList()
          ..bgmiAllowedMaps = (json['bgmiAllowedMaps'] as List<dynamic>)
              .map((e) => $enumDecode(_$BgmiMapsEnumMap, e,
                  unknownValue: BgmiMaps.artemisUnknown))
              .toList()
          ..bgmiAllowedGroups = $enumDecode(
              _$BgmiGroupsEnumMap, json['bgmiAllowedGroups'],
              unknownValue: BgmiGroups.artemisUnknown);

Map<String, dynamic> _$TournamentMixin$TournamentRules$BGMIRulesToJson(
        TournamentMixin$TournamentRules$BGMIRules instance) =>
    <String, dynamic>{
      '__typename': instance.$$typename,
      'minUsers': instance.minUsers,
      'maxUsers': instance.maxUsers,
      'maxTeams': instance.maxTeams,
      'bgmiMaxLevel': _$BgmiLevelsEnumMap[instance.bgmiMaxLevel]!,
      'bgmiMinLevel': _$BgmiLevelsEnumMap[instance.bgmiMinLevel]!,
      'bgmiAllowedModes':
          instance.bgmiAllowedModes.map((e) => _$BgmiModesEnumMap[e]!).toList(),
      'bgmiAllowedMaps':
          instance.bgmiAllowedMaps.map((e) => _$BgmiMapsEnumMap[e]!).toList(),
      'bgmiAllowedGroups': _$BgmiGroupsEnumMap[instance.bgmiAllowedGroups]!,
    };

const _$BgmiLevelsEnumMap = {
  BgmiLevels.bronzeFive: 'BRONZE_FIVE',
  BgmiLevels.bronzeFour: 'BRONZE_FOUR',
  BgmiLevels.bronzeThree: 'BRONZE_THREE',
  BgmiLevels.bronzeTwo: 'BRONZE_TWO',
  BgmiLevels.bronzeOne: 'BRONZE_ONE',
  BgmiLevels.silverFive: 'SILVER_FIVE',
  BgmiLevels.silverFour: 'SILVER_FOUR',
  BgmiLevels.silverThree: 'SILVER_THREE',
  BgmiLevels.silverTwo: 'SILVER_TWO',
  BgmiLevels.silverOne: 'SILVER_ONE',
  BgmiLevels.goldFive: 'GOLD_FIVE',
  BgmiLevels.goldFour: 'GOLD_FOUR',
  BgmiLevels.goldThree: 'GOLD_THREE',
  BgmiLevels.goldTwo: 'GOLD_TWO',
  BgmiLevels.goldOne: 'GOLD_ONE',
  BgmiLevels.platinumFive: 'PLATINUM_FIVE',
  BgmiLevels.platinumFour: 'PLATINUM_FOUR',
  BgmiLevels.platinumThree: 'PLATINUM_THREE',
  BgmiLevels.platinumTwo: 'PLATINUM_TWO',
  BgmiLevels.platinumOne: 'PLATINUM_ONE',
  BgmiLevels.diamondFive: 'DIAMOND_FIVE',
  BgmiLevels.diamondFour: 'DIAMOND_FOUR',
  BgmiLevels.diamondThree: 'DIAMOND_THREE',
  BgmiLevels.diamondTwo: 'DIAMOND_TWO',
  BgmiLevels.diamondOne: 'DIAMOND_ONE',
  BgmiLevels.crownFive: 'CROWN_FIVE',
  BgmiLevels.crownFour: 'CROWN_FOUR',
  BgmiLevels.crownThree: 'CROWN_THREE',
  BgmiLevels.crownTwo: 'CROWN_TWO',
  BgmiLevels.crownOne: 'CROWN_ONE',
  BgmiLevels.ace: 'ACE',
  BgmiLevels.aceMaster: 'ACE_MASTER',
  BgmiLevels.aceDominator: 'ACE_DOMINATOR',
  BgmiLevels.conqueror: 'CONQUEROR',
  BgmiLevels.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$BgmiModesEnumMap = {
  BgmiModes.classic: 'classic',
  BgmiModes.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$BgmiMapsEnumMap = {
  BgmiMaps.erangel: 'erangel',
  BgmiMaps.karakin: 'karakin',
  BgmiMaps.livik: 'livik',
  BgmiMaps.miramar: 'miramar',
  BgmiMaps.nusa: 'nusa',
  BgmiMaps.sanhok: 'sanhok',
  BgmiMaps.vikendi: 'vikendi',
  BgmiMaps.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$BgmiGroupsEnumMap = {
  BgmiGroups.duo: 'duo',
  BgmiGroups.solo: 'solo',
  BgmiGroups.squad: 'squad',
  BgmiGroups.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

TournamentMixin$TournamentRules$FFMaxRules
    _$TournamentMixin$TournamentRules$FFMaxRulesFromJson(
            Map<String, dynamic> json) =>
        TournamentMixin$TournamentRules$FFMaxRules()
          ..$$typename = json['__typename'] as String?
          ..minUsers = json['minUsers'] as int
          ..maxUsers = json['maxUsers'] as int
          ..maxTeams = json['maxTeams'] as int
          ..ffMaxLevel = $enumDecode(_$FfMaxLevelsEnumMap, json['ffMaxLevel'],
              unknownValue: FfMaxLevels.artemisUnknown)
          ..ffMinLevel = $enumDecode(_$FfMaxLevelsEnumMap, json['ffMinLevel'],
              unknownValue: FfMaxLevels.artemisUnknown)
          ..ffAllowedModes = (json['ffAllowedModes'] as List<dynamic>)
              .map((e) => $enumDecode(_$FfMaxModesEnumMap, e,
                  unknownValue: FfMaxModes.artemisUnknown))
              .toList()
          ..ffAllowedMaps = (json['ffAllowedMaps'] as List<dynamic>)
              .map((e) => $enumDecode(_$FfMaxMapsEnumMap, e,
                  unknownValue: FfMaxMaps.artemisUnknown))
              .toList()
          ..ffAllowedGroups = $enumDecode(
              _$FfMaxGroupsEnumMap, json['ffAllowedGroups'],
              unknownValue: FfMaxGroups.artemisUnknown);

Map<String, dynamic> _$TournamentMixin$TournamentRules$FFMaxRulesToJson(
        TournamentMixin$TournamentRules$FFMaxRules instance) =>
    <String, dynamic>{
      '__typename': instance.$$typename,
      'minUsers': instance.minUsers,
      'maxUsers': instance.maxUsers,
      'maxTeams': instance.maxTeams,
      'ffMaxLevel': _$FfMaxLevelsEnumMap[instance.ffMaxLevel]!,
      'ffMinLevel': _$FfMaxLevelsEnumMap[instance.ffMinLevel]!,
      'ffAllowedModes':
          instance.ffAllowedModes.map((e) => _$FfMaxModesEnumMap[e]!).toList(),
      'ffAllowedMaps':
          instance.ffAllowedMaps.map((e) => _$FfMaxMapsEnumMap[e]!).toList(),
      'ffAllowedGroups': _$FfMaxGroupsEnumMap[instance.ffAllowedGroups]!,
    };

const _$FfMaxLevelsEnumMap = {
  FfMaxLevels.bronzeOne: 'BRONZE_ONE',
  FfMaxLevels.bronzeTwo: 'BRONZE_TWO',
  FfMaxLevels.bronzeThree: 'BRONZE_THREE',
  FfMaxLevels.silverOne: 'SILVER_ONE',
  FfMaxLevels.silverTwo: 'SILVER_TWO',
  FfMaxLevels.silverThree: 'SILVER_THREE',
  FfMaxLevels.goldOne: 'GOLD_ONE',
  FfMaxLevels.goldTwo: 'GOLD_TWO',
  FfMaxLevels.goldThree: 'GOLD_THREE',
  FfMaxLevels.goldFour: 'GOLD_FOUR',
  FfMaxLevels.platinumOne: 'PLATINUM_ONE',
  FfMaxLevels.platinumTwo: 'PLATINUM_TWO',
  FfMaxLevels.platinumThree: 'PLATINUM_THREE',
  FfMaxLevels.platinumFour: 'PLATINUM_FOUR',
  FfMaxLevels.diamondOne: 'DIAMOND_ONE',
  FfMaxLevels.diamondTwo: 'DIAMOND_TWO',
  FfMaxLevels.diamondThree: 'DIAMOND_THREE',
  FfMaxLevels.diamondFour: 'DIAMOND_FOUR',
  FfMaxLevels.heroic: 'HEROIC',
  FfMaxLevels.master: 'MASTER',
  FfMaxLevels.grandmasterOne: 'GRANDMASTER_ONE',
  FfMaxLevels.grandmasterTwo: 'GRANDMASTER_TWO',
  FfMaxLevels.grandmasterThree: 'GRANDMASTER_THREE',
  FfMaxLevels.grandmasterFour: 'GRANDMASTER_FOUR',
  FfMaxLevels.grandmasterFive: 'GRANDMASTER_FIVE',
  FfMaxLevels.grandmasterSix: 'GRANDMASTER_SIX',
  FfMaxLevels.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$FfMaxModesEnumMap = {
  FfMaxModes.bRRanked: 'BRRanked',
  FfMaxModes.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$FfMaxMapsEnumMap = {
  FfMaxMaps.alpine: 'alpine',
  FfMaxMaps.bermuda: 'bermuda',
  FfMaxMaps.nexterra: 'nexterra',
  FfMaxMaps.purgatory: 'purgatory',
  FfMaxMaps.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$FfMaxGroupsEnumMap = {
  FfMaxGroups.duo: 'duo',
  FfMaxGroups.solo: 'solo',
  FfMaxGroups.squad: 'squad',
  FfMaxGroups.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

TournamentMixin$TournamentRules _$TournamentMixin$TournamentRulesFromJson(
        Map<String, dynamic> json) =>
    TournamentMixin$TournamentRules()
      ..$$typename = json['__typename'] as String?
      ..minUsers = json['minUsers'] as int
      ..maxUsers = json['maxUsers'] as int
      ..maxTeams = json['maxTeams'] as int;

Map<String, dynamic> _$TournamentMixin$TournamentRulesToJson(
        TournamentMixin$TournamentRules instance) =>
    <String, dynamic>{
      '__typename': instance.$$typename,
      'minUsers': instance.minUsers,
      'maxUsers': instance.maxUsers,
      'maxTeams': instance.maxTeams,
    };

TournamentMatchMixin$TournamentMatchMetadata
    _$TournamentMatchMixin$TournamentMatchMetadataFromJson(
            Map<String, dynamic> json) =>
        TournamentMatchMixin$TournamentMatchMetadata()
          ..roomId = json['roomId'] as String?
          ..roomPassword = json['roomPassword'] as String?;

Map<String, dynamic> _$TournamentMatchMixin$TournamentMatchMetadataToJson(
        TournamentMatchMixin$TournamentMatchMetadata instance) =>
    <String, dynamic>{
      'roomId': instance.roomId,
      'roomPassword': instance.roomPassword,
    };

SquadMixin$SquadMember _$SquadMixin$SquadMemberFromJson(
        Map<String, dynamic> json) =>
    SquadMixin$SquadMember()
      ..isReady = json['isReady'] as bool
      ..status = json['status'] as String
      ..user =
          SquadMemberMixin$User.fromJson(json['user'] as Map<String, dynamic>);

Map<String, dynamic> _$SquadMixin$SquadMemberToJson(
        SquadMixin$SquadMember instance) =>
    <String, dynamic>{
      'isReady': instance.isReady,
      'status': instance.status,
      'user': instance.user.toJson(),
    };

SquadMemberMixin$User _$SquadMemberMixin$UserFromJson(
        Map<String, dynamic> json) =>
    SquadMemberMixin$User()
      ..id = json['id'] as int
      ..name = json['name'] as String
      ..phone = json['phone'] as String?
      ..image = json['image'] as String?
      ..username = json['username'] as String;

Map<String, dynamic> _$SquadMemberMixin$UserToJson(
        SquadMemberMixin$User instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'phone': instance.phone,
      'image': instance.image,
      'username': instance.username,
    };

TournamentMatchUserMixin$SlotInfo _$TournamentMatchUserMixin$SlotInfoFromJson(
        Map<String, dynamic> json) =>
    TournamentMatchUserMixin$SlotInfo()
      ..teamNumber = json['teamNumber'] as int?;

Map<String, dynamic> _$TournamentMatchUserMixin$SlotInfoToJson(
        TournamentMatchUserMixin$SlotInfo instance) =>
    <String, dynamic>{
      'teamNumber': instance.teamNumber,
    };

ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevel
    _$ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevelFromJson(
            Map<String, dynamic> json) =>
        ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevel()
          ..bgmiGroup = $enumDecode(_$BgmiGroupsEnumMap, json['bgmiGroup'],
              unknownValue: BgmiGroups.artemisUnknown)
          ..bgmiLevel = $enumDecode(_$BgmiLevelsEnumMap, json['bgmiLevel'],
              unknownValue: BgmiLevels.artemisUnknown);

Map<String, dynamic>
    _$ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevelToJson(
            ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevel
                instance) =>
        <String, dynamic>{
          'bgmiGroup': _$BgmiGroupsEnumMap[instance.bgmiGroup]!,
          'bgmiLevel': _$BgmiLevelsEnumMap[instance.bgmiLevel]!,
        };

ProfileMixin$ProfileMetadata$BgmiProfileMetadata
    _$ProfileMixin$ProfileMetadata$BgmiProfileMetadataFromJson(
            Map<String, dynamic> json) =>
        ProfileMixin$ProfileMetadata$BgmiProfileMetadata()
          ..$$typename = json['__typename'] as String?
          ..levels = (json['levels'] as List<dynamic>?)
              ?.map((e) =>
                  ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevel
                      .fromJson(e as Map<String, dynamic>))
              .toList();

Map<String, dynamic> _$ProfileMixin$ProfileMetadata$BgmiProfileMetadataToJson(
        ProfileMixin$ProfileMetadata$BgmiProfileMetadata instance) =>
    <String, dynamic>{
      '__typename': instance.$$typename,
      'levels': instance.levels?.map((e) => e.toJson()).toList(),
    };

ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevel
    _$ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevelFromJson(
            Map<String, dynamic> json) =>
        ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevel()
          ..ffMaxGroup = $enumDecode(_$FfMaxGroupsEnumMap, json['ffMaxGroup'],
              unknownValue: FfMaxGroups.artemisUnknown)
          ..ffMaxLevel = $enumDecode(_$FfMaxLevelsEnumMap, json['ffMaxLevel'],
              unknownValue: FfMaxLevels.artemisUnknown);

Map<String, dynamic>
    _$ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevelToJson(
            ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevel
                instance) =>
        <String, dynamic>{
          'ffMaxGroup': _$FfMaxGroupsEnumMap[instance.ffMaxGroup]!,
          'ffMaxLevel': _$FfMaxLevelsEnumMap[instance.ffMaxLevel]!,
        };

ProfileMixin$ProfileMetadata$FFMaxProfileMetadata
    _$ProfileMixin$ProfileMetadata$FFMaxProfileMetadataFromJson(
            Map<String, dynamic> json) =>
        ProfileMixin$ProfileMetadata$FFMaxProfileMetadata()
          ..$$typename = json['__typename'] as String?
          ..levels = (json['levels'] as List<dynamic>?)
              ?.map((e) =>
                  ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevel
                      .fromJson(e as Map<String, dynamic>))
              .toList();

Map<String, dynamic> _$ProfileMixin$ProfileMetadata$FFMaxProfileMetadataToJson(
        ProfileMixin$ProfileMetadata$FFMaxProfileMetadata instance) =>
    <String, dynamic>{
      '__typename': instance.$$typename,
      'levels': instance.levels?.map((e) => e.toJson()).toList(),
    };

ProfileMixin$ProfileMetadata _$ProfileMixin$ProfileMetadataFromJson(
        Map<String, dynamic> json) =>
    ProfileMixin$ProfileMetadata()..$$typename = json['__typename'] as String?;

Map<String, dynamic> _$ProfileMixin$ProfileMetadataToJson(
        ProfileMixin$ProfileMetadata instance) =>
    <String, dynamic>{
      '__typename': instance.$$typename,
    };

GetUserListByPreference$Query$User _$GetUserListByPreference$Query$UserFromJson(
        Map<String, dynamic> json) =>
    GetUserListByPreference$Query$User()
      ..id = json['id'] as int
      ..birthdate = fromGraphQLDateNullableToDartDateTimeNullable(
          json['birthdate'] as String?)
      ..name = json['name'] as String
      ..phone = json['phone'] as String?
      ..image = json['image'] as String?
      ..achievements = UserMixin$Achievement.fromJson(
          json['achievements'] as Map<String, dynamic>)
      ..inviteCode = json['inviteCode'] as String
      ..preferences = json['preferences'] == null
          ? null
          : UserMixin$UserPreference.fromJson(
              json['preferences'] as Map<String, dynamic>)
      ..profiles = (json['profiles'] as List<dynamic>?)
          ?.map((e) => UserMixin$Profile.fromJson(e as Map<String, dynamic>))
          .toList()
      ..wallet =
          UserMixin$Wallet.fromJson(json['wallet'] as Map<String, dynamic>)
      ..flags =
          UserMixin$UserFlags.fromJson(json['flags'] as Map<String, dynamic>)
      ..username = json['username'] as String;

Map<String, dynamic> _$GetUserListByPreference$Query$UserToJson(
        GetUserListByPreference$Query$User instance) =>
    <String, dynamic>{
      'id': instance.id,
      'birthdate':
          fromDartDateTimeNullableToGraphQLDateNullable(instance.birthdate),
      'name': instance.name,
      'phone': instance.phone,
      'image': instance.image,
      'achievements': instance.achievements.toJson(),
      'inviteCode': instance.inviteCode,
      'preferences': instance.preferences?.toJson(),
      'profiles': instance.profiles?.map((e) => e.toJson()).toList(),
      'wallet': instance.wallet.toJson(),
      'flags': instance.flags.toJson(),
      'username': instance.username,
    };

GetUserListByPreference$Query _$GetUserListByPreference$QueryFromJson(
        Map<String, dynamic> json) =>
    GetUserListByPreference$Query()
      ..searchByPreferences = (json['searchByPreferences'] as List<dynamic>)
          .map((e) => e == null
              ? null
              : GetUserListByPreference$Query$User.fromJson(
                  e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$GetUserListByPreference$QueryToJson(
        GetUserListByPreference$Query instance) =>
    <String, dynamic>{
      'searchByPreferences':
          instance.searchByPreferences.map((e) => e?.toJson()).toList(),
    };

PreferencesInput _$PreferencesInputFromJson(Map<String, dynamic> json) =>
    PreferencesInput(
      location: json['location'] == null
          ? null
          : LocationInput.fromJson(json['location'] as Map<String, dynamic>),
      playingReason: (json['playingReason'] as List<dynamic>?)
          ?.map((e) => $enumDecode(_$PlayingReasonPreferenceEnumMap, e,
              unknownValue: PlayingReasonPreference.artemisUnknown))
          .toList(),
      roles: (json['roles'] as List<dynamic>?)
          ?.map((e) => $enumDecode(_$RolePreferenceEnumMap, e,
              unknownValue: RolePreference.artemisUnknown))
          .toList(),
      timeOfDay: (json['timeOfDay'] as List<dynamic>?)
          ?.map((e) => $enumDecode(_$TimeOfDayPreferenceEnumMap, e,
              unknownValue: TimeOfDayPreference.artemisUnknown))
          .toList(),
      timeOfWeek: $enumDecodeNullable(
          _$TimeOfWeekPreferenceEnumMap, json['timeOfWeek'],
          unknownValue: TimeOfWeekPreference.artemisUnknown),
    );

Map<String, dynamic> _$PreferencesInputToJson(PreferencesInput instance) =>
    <String, dynamic>{
      'location': instance.location?.toJson(),
      'playingReason': instance.playingReason
          ?.map((e) => _$PlayingReasonPreferenceEnumMap[e]!)
          .toList(),
      'roles': instance.roles?.map((e) => _$RolePreferenceEnumMap[e]!).toList(),
      'timeOfDay': instance.timeOfDay
          ?.map((e) => _$TimeOfDayPreferenceEnumMap[e]!)
          .toList(),
      'timeOfWeek': _$TimeOfWeekPreferenceEnumMap[instance.timeOfWeek],
    };

LocationInput _$LocationInputFromJson(Map<String, dynamic> json) =>
    LocationInput(
      city: json['city'] as String?,
      country: json['country'] as String?,
      lat: (json['lat'] as num?)?.toDouble(),
      lng: (json['lng'] as num?)?.toDouble(),
      region: json['region'] as String?,
    );

Map<String, dynamic> _$LocationInputToJson(LocationInput instance) =>
    <String, dynamic>{
      'city': instance.city,
      'country': instance.country,
      'lat': instance.lat,
      'lng': instance.lng,
      'region': instance.region,
    };

GetMyActiveTournament$Query$ActiveTournamentList$UserTournament
    _$GetMyActiveTournament$Query$ActiveTournamentList$UserTournamentFromJson(
            Map<String, dynamic> json) =>
        GetMyActiveTournament$Query$ActiveTournamentList$UserTournament()
          ..joinedAt = fromGraphQLDateTimeNullableToDartDateTimeNullable(
              json['joinedAt'] as String?)
          ..rank = json['rank'] as int?
          ..score = json['score'] as int?
          ..tournament = UserTournamentMixin$Tournament.fromJson(
              json['tournament'] as Map<String, dynamic>)
          ..squad = json['squad'] == null
              ? null
              : UserTournamentMixin$Squad.fromJson(
                  json['squad'] as Map<String, dynamic>)
          ..tournamentMatchUser = json['tournamentMatchUser'] == null
              ? null
              : UserTournamentMixin$TournamentMatchUser.fromJson(
                  json['tournamentMatchUser'] as Map<String, dynamic>);

Map<String, dynamic>
    _$GetMyActiveTournament$Query$ActiveTournamentList$UserTournamentToJson(
            GetMyActiveTournament$Query$ActiveTournamentList$UserTournament
                instance) =>
        <String, dynamic>{
          'joinedAt': fromDartDateTimeNullableToGraphQLDateTimeNullable(
              instance.joinedAt),
          'rank': instance.rank,
          'score': instance.score,
          'tournament': instance.tournament.toJson(),
          'squad': instance.squad?.toJson(),
          'tournamentMatchUser': instance.tournamentMatchUser?.toJson(),
        };

GetMyActiveTournament$Query$ActiveTournamentList
    _$GetMyActiveTournament$Query$ActiveTournamentListFromJson(
            Map<String, dynamic> json) =>
        GetMyActiveTournament$Query$ActiveTournamentList()
          ..tournaments = (json['tournaments'] as List<dynamic>)
              .map((e) =>
                  GetMyActiveTournament$Query$ActiveTournamentList$UserTournament
                      .fromJson(e as Map<String, dynamic>))
              .toList()
          ..type = json['type'] as String;

Map<String, dynamic> _$GetMyActiveTournament$Query$ActiveTournamentListToJson(
        GetMyActiveTournament$Query$ActiveTournamentList instance) =>
    <String, dynamic>{
      'tournaments': instance.tournaments.map((e) => e.toJson()).toList(),
      'type': instance.type,
    };

GetMyActiveTournament$Query _$GetMyActiveTournament$QueryFromJson(
        Map<String, dynamic> json) =>
    GetMyActiveTournament$Query()
      ..active = (json['active'] as List<dynamic>)
          .map((e) => GetMyActiveTournament$Query$ActiveTournamentList.fromJson(
              e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$GetMyActiveTournament$QueryToJson(
        GetMyActiveTournament$Query instance) =>
    <String, dynamic>{
      'active': instance.active.map((e) => e.toJson()).toList(),
    };

CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualification
    _$CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualificationFromJson(
            Map<String, dynamic> json) =>
        CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualification()
          ..current = json['current'] as int
          ..qualified = json['qualified'] as bool
          ..required = json['required'] as int
          ..rule = json['rule'] as String;

Map<String, dynamic>
    _$CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualificationToJson(
            CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualification
                instance) =>
        <String, dynamic>{
          'current': instance.current,
          'qualified': instance.qualified,
          'required': instance.required,
          'rule': instance.rule,
        };

CheckUserTournamentQualification$Query$TournamentQualificationResult
    _$CheckUserTournamentQualification$Query$TournamentQualificationResultFromJson(
            Map<String, dynamic> json) =>
        CheckUserTournamentQualification$Query$TournamentQualificationResult()
          ..qualified = json['qualified'] as bool
          ..rules = (json['rules'] as List<dynamic>)
              .map((e) =>
                  CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualification
                      .fromJson(e as Map<String, dynamic>))
              .toList();

Map<String, dynamic>
    _$CheckUserTournamentQualification$Query$TournamentQualificationResultToJson(
            CheckUserTournamentQualification$Query$TournamentQualificationResult
                instance) =>
        <String, dynamic>{
          'qualified': instance.qualified,
          'rules': instance.rules.map((e) => e.toJson()).toList(),
        };

CheckUserTournamentQualification$Query
    _$CheckUserTournamentQualification$QueryFromJson(
            Map<String, dynamic> json) =>
        CheckUserTournamentQualification$Query()
          ..getTournamentQualification =
              CheckUserTournamentQualification$Query$TournamentQualificationResult
                  .fromJson(json['getTournamentQualification']
                      as Map<String, dynamic>);

Map<String, dynamic> _$CheckUserTournamentQualification$QueryToJson(
        CheckUserTournamentQualification$Query instance) =>
    <String, dynamic>{
      'getTournamentQualification':
          instance.getTournamentQualification.toJson(),
    };

GetTournamentMatches$Query$TournamentMatch
    _$GetTournamentMatches$Query$TournamentMatchFromJson(
            Map<String, dynamic> json) =>
        GetTournamentMatches$Query$TournamentMatch()
          ..tournamentId = json['tournamentId'] as int
          ..endTime = fromGraphQLDateTimeNullableToDartDateTimeNullable(
              json['endTime'] as String?)
          ..startTime =
              fromGraphQLDateTimeToDartDateTime(json['startTime'] as String)
          ..id = json['id'] as int
          ..maxParticipants = json['maxParticipants'] as int
          ..minParticipants = json['minParticipants'] as int?
          ..metadata = json['metadata'] == null
              ? null
              : TournamentMatchMixin$TournamentMatchMetadata.fromJson(
                  json['metadata'] as Map<String, dynamic>);

Map<String, dynamic> _$GetTournamentMatches$Query$TournamentMatchToJson(
        GetTournamentMatches$Query$TournamentMatch instance) =>
    <String, dynamic>{
      'tournamentId': instance.tournamentId,
      'endTime':
          fromDartDateTimeNullableToGraphQLDateTimeNullable(instance.endTime),
      'startTime': fromDartDateTimeToGraphQLDateTime(instance.startTime),
      'id': instance.id,
      'maxParticipants': instance.maxParticipants,
      'minParticipants': instance.minParticipants,
      'metadata': instance.metadata?.toJson(),
    };

GetTournamentMatches$Query _$GetTournamentMatches$QueryFromJson(
        Map<String, dynamic> json) =>
    GetTournamentMatches$Query()
      ..getTournamentMatches = (json['getTournamentMatches'] as List<dynamic>)
          .map((e) => GetTournamentMatches$Query$TournamentMatch.fromJson(
              e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$GetTournamentMatches$QueryToJson(
        GetTournamentMatches$Query instance) =>
    <String, dynamic>{
      'getTournamentMatches':
          instance.getTournamentMatches.map((e) => e.toJson()).toList(),
    };

GetTopTournaments$Query$UserTournament
    _$GetTopTournaments$Query$UserTournamentFromJson(
            Map<String, dynamic> json) =>
        GetTopTournaments$Query$UserTournament()
          ..joinedAt = fromGraphQLDateTimeNullableToDartDateTimeNullable(
              json['joinedAt'] as String?)
          ..rank = json['rank'] as int?
          ..score = json['score'] as int?
          ..tournament = UserTournamentMixin$Tournament.fromJson(
              json['tournament'] as Map<String, dynamic>)
          ..squad = json['squad'] == null
              ? null
              : UserTournamentMixin$Squad.fromJson(
                  json['squad'] as Map<String, dynamic>)
          ..tournamentMatchUser = json['tournamentMatchUser'] == null
              ? null
              : UserTournamentMixin$TournamentMatchUser.fromJson(
                  json['tournamentMatchUser'] as Map<String, dynamic>);

Map<String, dynamic> _$GetTopTournaments$Query$UserTournamentToJson(
        GetTopTournaments$Query$UserTournament instance) =>
    <String, dynamic>{
      'joinedAt':
          fromDartDateTimeNullableToGraphQLDateTimeNullable(instance.joinedAt),
      'rank': instance.rank,
      'score': instance.score,
      'tournament': instance.tournament.toJson(),
      'squad': instance.squad?.toJson(),
      'tournamentMatchUser': instance.tournamentMatchUser?.toJson(),
    };

GetTopTournaments$Query _$GetTopTournaments$QueryFromJson(
        Map<String, dynamic> json) =>
    GetTopTournaments$Query()
      ..top = (json['top'] as List<dynamic>)
          .map((e) => GetTopTournaments$Query$UserTournament.fromJson(
              e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$GetTopTournaments$QueryToJson(
        GetTopTournaments$Query instance) =>
    <String, dynamic>{
      'top': instance.top.map((e) => e.toJson()).toList(),
    };

GetTournamentsHistory$Query$UserTournament
    _$GetTournamentsHistory$Query$UserTournamentFromJson(
            Map<String, dynamic> json) =>
        GetTournamentsHistory$Query$UserTournament()
          ..joinedAt = fromGraphQLDateTimeNullableToDartDateTimeNullable(
              json['joinedAt'] as String?)
          ..rank = json['rank'] as int?
          ..score = json['score'] as int?
          ..tournament = UserTournamentMixin$Tournament.fromJson(
              json['tournament'] as Map<String, dynamic>)
          ..squad = json['squad'] == null
              ? null
              : UserTournamentMixin$Squad.fromJson(
                  json['squad'] as Map<String, dynamic>)
          ..tournamentMatchUser = json['tournamentMatchUser'] == null
              ? null
              : UserTournamentMixin$TournamentMatchUser.fromJson(
                  json['tournamentMatchUser'] as Map<String, dynamic>);

Map<String, dynamic> _$GetTournamentsHistory$Query$UserTournamentToJson(
        GetTournamentsHistory$Query$UserTournament instance) =>
    <String, dynamic>{
      'joinedAt':
          fromDartDateTimeNullableToGraphQLDateTimeNullable(instance.joinedAt),
      'rank': instance.rank,
      'score': instance.score,
      'tournament': instance.tournament.toJson(),
      'squad': instance.squad?.toJson(),
      'tournamentMatchUser': instance.tournamentMatchUser?.toJson(),
    };

GetTournamentsHistory$Query _$GetTournamentsHistory$QueryFromJson(
        Map<String, dynamic> json) =>
    GetTournamentsHistory$Query()
      ..history = (json['history'] as List<dynamic>)
          .map((e) => GetTournamentsHistory$Query$UserTournament.fromJson(
              e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$GetTournamentsHistory$QueryToJson(
        GetTournamentsHistory$Query instance) =>
    <String, dynamic>{
      'history': instance.history.map((e) => e.toJson()).toList(),
    };

GetLeaderboard$Query$Leaderboard _$GetLeaderboard$Query$LeaderboardFromJson(
        Map<String, dynamic> json) =>
    GetLeaderboard$Query$Leaderboard()
      ..id = json['id'] as String
      ..rank = json['rank'] as int
      ..score = json['score'] as int
      ..behindBy = json['behindBy'] as int
      ..isDisqualified = json['isDisqualified'] as bool
      ..details = json['details'] == null
          ? null
          : LeaderboardRankMixin$LeaderboardInfo.fromJson(
              json['details'] as Map<String, dynamic>)
      ..user = LeaderboardRankMixin$User.fromJson(
          json['user'] as Map<String, dynamic>);

Map<String, dynamic> _$GetLeaderboard$Query$LeaderboardToJson(
        GetLeaderboard$Query$Leaderboard instance) =>
    <String, dynamic>{
      'id': instance.id,
      'rank': instance.rank,
      'score': instance.score,
      'behindBy': instance.behindBy,
      'isDisqualified': instance.isDisqualified,
      'details': instance.details?.toJson(),
      'user': instance.user.toJson(),
    };

GetLeaderboard$Query _$GetLeaderboard$QueryFromJson(
        Map<String, dynamic> json) =>
    GetLeaderboard$Query()
      ..leaderboard = (json['leaderboard'] as List<dynamic>)
          .map((e) => GetLeaderboard$Query$Leaderboard.fromJson(
              e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$GetLeaderboard$QueryToJson(
        GetLeaderboard$Query instance) =>
    <String, dynamic>{
      'leaderboard': instance.leaderboard.map((e) => e.toJson()).toList(),
    };

LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadata
    _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadataFromJson(
            Map<String, dynamic> json) =>
        LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadata()
          ..$$typename = json['__typename'] as String?
          ..kills = json['kills'] as int;

Map<String, dynamic>
    _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadataToJson(
            LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadata
                instance) =>
        <String, dynamic>{
          '__typename': instance.$$typename,
          'kills': instance.kills,
        };

LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadata
    _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadataFromJson(
            Map<String, dynamic> json) =>
        LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadata()
          ..$$typename = json['__typename'] as String?
          ..kills = json['kills'] as int;

Map<String, dynamic>
    _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadataToJson(
            LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadata
                instance) =>
        <String, dynamic>{
          '__typename': instance.$$typename,
          'kills': instance.kills,
        };

LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata
    _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadataFromJson(
            Map<String, dynamic> json) =>
        LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata()
          ..$$typename = json['__typename'] as String?;

Map<String, dynamic>
    _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadataToJson(
            LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata instance) =>
        <String, dynamic>{
          '__typename': instance.$$typename,
        };

LeaderboardRankMixin$LeaderboardInfo$Game
    _$LeaderboardRankMixin$LeaderboardInfo$GameFromJson(
            Map<String, dynamic> json) =>
        LeaderboardRankMixin$LeaderboardInfo$Game()
          ..rank = json['rank'] as int
          ..score = (json['score'] as num).toDouble()
          ..metadata =
              LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata.fromJson(
                  json['metadata'] as Map<String, dynamic>);

Map<String, dynamic> _$LeaderboardRankMixin$LeaderboardInfo$GameToJson(
        LeaderboardRankMixin$LeaderboardInfo$Game instance) =>
    <String, dynamic>{
      'rank': instance.rank,
      'score': instance.score,
      'metadata': instance.metadata.toJson(),
    };

LeaderboardRankMixin$LeaderboardInfo
    _$LeaderboardRankMixin$LeaderboardInfoFromJson(Map<String, dynamic> json) =>
        LeaderboardRankMixin$LeaderboardInfo()
          ..gamesPlayed = json['gamesPlayed'] as int
          ..top = (json['top'] as List<dynamic>)
              .map((e) => e == null
                  ? null
                  : LeaderboardRankMixin$LeaderboardInfo$Game.fromJson(
                      e as Map<String, dynamic>))
              .toList();

Map<String, dynamic> _$LeaderboardRankMixin$LeaderboardInfoToJson(
        LeaderboardRankMixin$LeaderboardInfo instance) =>
    <String, dynamic>{
      'gamesPlayed': instance.gamesPlayed,
      'top': instance.top.map((e) => e?.toJson()).toList(),
    };

LeaderboardRankMixin$User _$LeaderboardRankMixin$UserFromJson(
        Map<String, dynamic> json) =>
    LeaderboardRankMixin$User()
      ..id = json['id'] as int
      ..name = json['name'] as String
      ..phone = json['phone'] as String?
      ..image = json['image'] as String?
      ..username = json['username'] as String;

Map<String, dynamic> _$LeaderboardRankMixin$UserToJson(
        LeaderboardRankMixin$User instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'phone': instance.phone,
      'image': instance.image,
      'username': instance.username,
    };

GetSquadLeaderboard$Query$SquadLeaderboard
    _$GetSquadLeaderboard$Query$SquadLeaderboardFromJson(
            Map<String, dynamic> json) =>
        GetSquadLeaderboard$Query$SquadLeaderboard()
          ..id = json['id'] as String
          ..squad = json['squad'] == null
              ? null
              : SquadLeaderboardMixin$Squad.fromJson(
                  json['squad'] as Map<String, dynamic>)
          ..behindBy = json['behindBy'] as int
          ..rank = json['rank'] as int
          ..score = json['score'] as int
          ..details = json['details'] == null
              ? null
              : SquadLeaderboardMixin$SquadLeaderboardInfo.fromJson(
                  json['details'] as Map<String, dynamic>);

Map<String, dynamic> _$GetSquadLeaderboard$Query$SquadLeaderboardToJson(
        GetSquadLeaderboard$Query$SquadLeaderboard instance) =>
    <String, dynamic>{
      'id': instance.id,
      'squad': instance.squad?.toJson(),
      'behindBy': instance.behindBy,
      'rank': instance.rank,
      'score': instance.score,
      'details': instance.details?.toJson(),
    };

GetSquadLeaderboard$Query _$GetSquadLeaderboard$QueryFromJson(
        Map<String, dynamic> json) =>
    GetSquadLeaderboard$Query()
      ..squadLeaderboard = (json['squadLeaderboard'] as List<dynamic>)
          .map((e) => GetSquadLeaderboard$Query$SquadLeaderboard.fromJson(
              e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$GetSquadLeaderboard$QueryToJson(
        GetSquadLeaderboard$Query instance) =>
    <String, dynamic>{
      'squadLeaderboard':
          instance.squadLeaderboard.map((e) => e.toJson()).toList(),
    };

SquadLeaderboardMixin$Squad _$SquadLeaderboardMixin$SquadFromJson(
        Map<String, dynamic> json) =>
    SquadLeaderboardMixin$Squad()
      ..isDisqualified = json['isDisqualified'] as bool
      ..name = json['name'] as String
      ..id = json['id'] as int;

Map<String, dynamic> _$SquadLeaderboardMixin$SquadToJson(
        SquadLeaderboardMixin$Squad instance) =>
    <String, dynamic>{
      'isDisqualified': instance.isDisqualified,
      'name': instance.name,
      'id': instance.id,
    };

SquadLeaderboardMixin$SquadLeaderboardInfo$Game
    _$SquadLeaderboardMixin$SquadLeaderboardInfo$GameFromJson(
            Map<String, dynamic> json) =>
        SquadLeaderboardMixin$SquadLeaderboardInfo$Game()
          ..score = (json['score'] as num).toDouble()
          ..teamRank = json['teamRank'] as int?;

Map<String, dynamic> _$SquadLeaderboardMixin$SquadLeaderboardInfo$GameToJson(
        SquadLeaderboardMixin$SquadLeaderboardInfo$Game instance) =>
    <String, dynamic>{
      'score': instance.score,
      'teamRank': instance.teamRank,
    };

SquadLeaderboardMixin$SquadLeaderboardInfo
    _$SquadLeaderboardMixin$SquadLeaderboardInfoFromJson(
            Map<String, dynamic> json) =>
        SquadLeaderboardMixin$SquadLeaderboardInfo()
          ..gamesPlayed = json['gamesPlayed'] as int
          ..top = (json['top'] as List<dynamic>)
              .map((e) => (e as List<dynamic>?)
                  ?.map((e) => e == null
                      ? null
                      : SquadLeaderboardMixin$SquadLeaderboardInfo$Game
                          .fromJson(e as Map<String, dynamic>))
                  .toList())
              .toList();

Map<String, dynamic> _$SquadLeaderboardMixin$SquadLeaderboardInfoToJson(
        SquadLeaderboardMixin$SquadLeaderboardInfo instance) =>
    <String, dynamic>{
      'gamesPlayed': instance.gamesPlayed,
      'top':
          instance.top.map((e) => e?.map((e) => e?.toJson()).toList()).toList(),
    };

GetTournament$Query$UserTournament _$GetTournament$Query$UserTournamentFromJson(
        Map<String, dynamic> json) =>
    GetTournament$Query$UserTournament()
      ..joinedAt = fromGraphQLDateTimeNullableToDartDateTimeNullable(
          json['joinedAt'] as String?)
      ..rank = json['rank'] as int?
      ..score = json['score'] as int?
      ..tournament = UserTournamentMixin$Tournament.fromJson(
          json['tournament'] as Map<String, dynamic>)
      ..squad = json['squad'] == null
          ? null
          : UserTournamentMixin$Squad.fromJson(
              json['squad'] as Map<String, dynamic>)
      ..tournamentMatchUser = json['tournamentMatchUser'] == null
          ? null
          : UserTournamentMixin$TournamentMatchUser.fromJson(
              json['tournamentMatchUser'] as Map<String, dynamic>);

Map<String, dynamic> _$GetTournament$Query$UserTournamentToJson(
        GetTournament$Query$UserTournament instance) =>
    <String, dynamic>{
      'joinedAt':
          fromDartDateTimeNullableToGraphQLDateTimeNullable(instance.joinedAt),
      'rank': instance.rank,
      'score': instance.score,
      'tournament': instance.tournament.toJson(),
      'squad': instance.squad?.toJson(),
      'tournamentMatchUser': instance.tournamentMatchUser?.toJson(),
    };

GetTournament$Query _$GetTournament$QueryFromJson(Map<String, dynamic> json) =>
    GetTournament$Query()
      ..tournament = json['tournament'] == null
          ? null
          : GetTournament$Query$UserTournament.fromJson(
              json['tournament'] as Map<String, dynamic>);

Map<String, dynamic> _$GetTournament$QueryToJson(
        GetTournament$Query instance) =>
    <String, dynamic>{
      'tournament': instance.tournament?.toJson(),
    };

GetGameScoring$Query$Scoring$RankPoint
    _$GetGameScoring$Query$Scoring$RankPointFromJson(
            Map<String, dynamic> json) =>
        GetGameScoring$Query$Scoring$RankPoint()
          ..points = json['points'] as int
          ..rank = json['rank'] as int;

Map<String, dynamic> _$GetGameScoring$Query$Scoring$RankPointToJson(
        GetGameScoring$Query$Scoring$RankPoint instance) =>
    <String, dynamic>{
      'points': instance.points,
      'rank': instance.rank,
    };

GetGameScoring$Query$Scoring _$GetGameScoring$Query$ScoringFromJson(
        Map<String, dynamic> json) =>
    GetGameScoring$Query$Scoring()
      ..killPoints = json['killPoints'] as int
      ..rankPoints = (json['rankPoints'] as List<dynamic>)
          .map((e) => GetGameScoring$Query$Scoring$RankPoint.fromJson(
              e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$GetGameScoring$Query$ScoringToJson(
        GetGameScoring$Query$Scoring instance) =>
    <String, dynamic>{
      'killPoints': instance.killPoints,
      'rankPoints': instance.rankPoints.map((e) => e.toJson()).toList(),
    };

GetGameScoring$Query _$GetGameScoring$QueryFromJson(
        Map<String, dynamic> json) =>
    GetGameScoring$Query()
      ..scoring = GetGameScoring$Query$Scoring.fromJson(
          json['scoring'] as Map<String, dynamic>);

Map<String, dynamic> _$GetGameScoring$QueryToJson(
        GetGameScoring$Query instance) =>
    <String, dynamic>{
      'scoring': instance.scoring.toJson(),
    };

GetTransactions$Query$LedgerTransaction
    _$GetTransactions$Query$LedgerTransactionFromJson(
            Map<String, dynamic> json) =>
        GetTransactions$Query$LedgerTransaction()
          ..amount = (json['amount'] as num).toDouble()
          ..subWallet = $enumDecode(_$SubWalletTypeEnumMap, json['subWallet'],
              unknownValue: SubWalletType.artemisUnknown)
          ..createdAt =
              fromGraphQLDateToDartDateTime(json['createdAt'] as String)
          ..status = $enumDecode(_$TransactionStatusEnumMap, json['status'],
              unknownValue: TransactionStatus.artemisUnknown)
          ..description = json['description'] as String
          ..transactionId = json['transactionId'] as String
          ..id = json['id'] as int;

Map<String, dynamic> _$GetTransactions$Query$LedgerTransactionToJson(
        GetTransactions$Query$LedgerTransaction instance) =>
    <String, dynamic>{
      'amount': instance.amount,
      'subWallet': _$SubWalletTypeEnumMap[instance.subWallet]!,
      'createdAt': fromDartDateTimeToGraphQLDate(instance.createdAt),
      'status': _$TransactionStatusEnumMap[instance.status]!,
      'description': instance.description,
      'transactionId': instance.transactionId,
      'id': instance.id,
    };

const _$SubWalletTypeEnumMap = {
  SubWalletType.bonus: 'bonus',
  SubWalletType.deposit: 'deposit',
  SubWalletType.winning: 'winning',
  SubWalletType.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

const _$TransactionStatusEnumMap = {
  TransactionStatus.cancelled: 'cancelled',
  TransactionStatus.failed: 'failed',
  TransactionStatus.processing: 'processing',
  TransactionStatus.successful: 'successful',
  TransactionStatus.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

GetTransactions$Query _$GetTransactions$QueryFromJson(
        Map<String, dynamic> json) =>
    GetTransactions$Query()
      ..transactions = (json['transactions'] as List<dynamic>)
          .map((e) => GetTransactions$Query$LedgerTransaction.fromJson(
              e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$GetTransactions$QueryToJson(
        GetTransactions$Query instance) =>
    <String, dynamic>{
      'transactions': instance.transactions.map((e) => e.toJson()).toList(),
    };

Transaction$Query$LedgerTransaction
    _$Transaction$Query$LedgerTransactionFromJson(Map<String, dynamic> json) =>
        Transaction$Query$LedgerTransaction()
          ..id = json['id'] as int
          ..transactionId = json['transactionId'] as String
          ..status = $enumDecode(_$TransactionStatusEnumMap, json['status'],
              unknownValue: TransactionStatus.artemisUnknown)
          ..amount = (json['amount'] as num).toDouble()
          ..description = json['description'] as String;

Map<String, dynamic> _$Transaction$Query$LedgerTransactionToJson(
        Transaction$Query$LedgerTransaction instance) =>
    <String, dynamic>{
      'id': instance.id,
      'transactionId': instance.transactionId,
      'status': _$TransactionStatusEnumMap[instance.status]!,
      'amount': instance.amount,
      'description': instance.description,
    };

Transaction$Query _$Transaction$QueryFromJson(Map<String, dynamic> json) =>
    Transaction$Query()
      ..transaction = json['transaction'] == null
          ? null
          : Transaction$Query$LedgerTransaction.fromJson(
              json['transaction'] as Map<String, dynamic>);

Map<String, dynamic> _$Transaction$QueryToJson(Transaction$Query instance) =>
    <String, dynamic>{
      'transaction': instance.transaction?.toJson(),
    };

SearchUser$Query$User _$SearchUser$Query$UserFromJson(
        Map<String, dynamic> json) =>
    SearchUser$Query$User()
      ..id = json['id'] as int
      ..name = json['name'] as String
      ..image = json['image'] as String?
      ..profiles = (json['profiles'] as List<dynamic>?)
          ?.map((e) =>
              UserSummaryMixin$Profile.fromJson(e as Map<String, dynamic>))
          .toList()
      ..username = json['username'] as String;

Map<String, dynamic> _$SearchUser$Query$UserToJson(
        SearchUser$Query$User instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'image': instance.image,
      'profiles': instance.profiles?.map((e) => e.toJson()).toList(),
      'username': instance.username,
    };

SearchUser$Query _$SearchUser$QueryFromJson(Map<String, dynamic> json) =>
    SearchUser$Query()
      ..searchUserESports = (json['searchUserESports'] as List<dynamic>)
          .map((e) => e == null
              ? null
              : SearchUser$Query$User.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$SearchUser$QueryToJson(SearchUser$Query instance) =>
    <String, dynamic>{
      'searchUserESports':
          instance.searchUserESports.map((e) => e?.toJson()).toList(),
    };

UserSummaryMixin$Profile _$UserSummaryMixin$ProfileFromJson(
        Map<String, dynamic> json) =>
    UserSummaryMixin$Profile()
      ..eSport = $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown)
      ..metadata = ProfileMixin$ProfileMetadata.fromJson(
          json['metadata'] as Map<String, dynamic>)
      ..username = json['username'] as String?
      ..profileId = json['profileId'] as String?;

Map<String, dynamic> _$UserSummaryMixin$ProfileToJson(
        UserSummaryMixin$Profile instance) =>
    <String, dynamic>{
      'eSport': _$ESportsEnumMap[instance.eSport]!,
      'metadata': instance.metadata.toJson(),
      'username': instance.username,
      'profileId': instance.profileId,
    };

RecentPlayers$Query$User _$RecentPlayers$Query$UserFromJson(
        Map<String, dynamic> json) =>
    RecentPlayers$Query$User()
      ..id = json['id'] as int
      ..name = json['name'] as String
      ..image = json['image'] as String?
      ..profiles = (json['profiles'] as List<dynamic>?)
          ?.map((e) =>
              UserSummaryMixin$Profile.fromJson(e as Map<String, dynamic>))
          .toList()
      ..username = json['username'] as String;

Map<String, dynamic> _$RecentPlayers$Query$UserToJson(
        RecentPlayers$Query$User instance) =>
    <String, dynamic>{
      'id': instance.id,
      'name': instance.name,
      'image': instance.image,
      'profiles': instance.profiles?.map((e) => e.toJson()).toList(),
      'username': instance.username,
    };

RecentPlayers$Query _$RecentPlayers$QueryFromJson(Map<String, dynamic> json) =>
    RecentPlayers$Query()
      ..inviteList = (json['inviteList'] as List<dynamic>)
          .map((e) =>
              RecentPlayers$Query$User.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$RecentPlayers$QueryToJson(
        RecentPlayers$Query instance) =>
    <String, dynamic>{
      'inviteList': instance.inviteList.map((e) => e.toJson()).toList(),
    };

LowRating$Query _$LowRating$QueryFromJson(Map<String, dynamic> json) =>
    LowRating$Query()
      ..lowRatingReasons = (json['lowRatingReasons'] as List<dynamic>)
          .map((e) => e as String)
          .toList();

Map<String, dynamic> _$LowRating$QueryToJson(LowRating$Query instance) =>
    <String, dynamic>{
      'lowRatingReasons': instance.lowRatingReasons,
    };

TopWinners$Query$TopWinners$User _$TopWinners$Query$TopWinners$UserFromJson(
        Map<String, dynamic> json) =>
    TopWinners$Query$TopWinners$User()..username = json['username'] as String;

Map<String, dynamic> _$TopWinners$Query$TopWinners$UserToJson(
        TopWinners$Query$TopWinners$User instance) =>
    <String, dynamic>{
      'username': instance.username,
    };

TopWinners$Query$TopWinners _$TopWinners$Query$TopWinnersFromJson(
        Map<String, dynamic> json) =>
    TopWinners$Query$TopWinners()
      ..amount = (json['amount'] as num).toDouble()
      ..user = TopWinners$Query$TopWinners$User.fromJson(
          json['user'] as Map<String, dynamic>);

Map<String, dynamic> _$TopWinners$Query$TopWinnersToJson(
        TopWinners$Query$TopWinners instance) =>
    <String, dynamic>{
      'amount': instance.amount,
      'user': instance.user.toJson(),
    };

TopWinners$Query _$TopWinners$QueryFromJson(Map<String, dynamic> json) =>
    TopWinners$Query()
      ..topWinners = (json['topWinners'] as List<dynamic>)
          .map((e) =>
              TopWinners$Query$TopWinners.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$TopWinners$QueryToJson(TopWinners$Query instance) =>
    <String, dynamic>{
      'topWinners': instance.topWinners.map((e) => e.toJson()).toList(),
    };

ModelParam$Query$ModelParam$Label _$ModelParam$Query$ModelParam$LabelFromJson(
        Map<String, dynamic> json) =>
    ModelParam$Query$ModelParam$Label()
      ..index = json['index'] as int
      ..name = json['name'] as String
      ..threshold = (json['threshold'] as num).toDouble()
      ..individualOCR = json['individualOCR'] as bool
      ..sortOrder = $enumDecode(_$SortOrderEnumMap, json['sortOrder'],
          unknownValue: SortOrder.artemisUnknown)
      ..mandatory = json['mandatory'] as bool?
      ..shouldPerformScaleAndStitching =
          json['shouldPerformScaleAndStitching'] as bool?;

Map<String, dynamic> _$ModelParam$Query$ModelParam$LabelToJson(
        ModelParam$Query$ModelParam$Label instance) =>
    <String, dynamic>{
      'index': instance.index,
      'name': instance.name,
      'threshold': instance.threshold,
      'individualOCR': instance.individualOCR,
      'sortOrder': _$SortOrderEnumMap[instance.sortOrder]!,
      'mandatory': instance.mandatory,
      'shouldPerformScaleAndStitching': instance.shouldPerformScaleAndStitching,
    };

const _$SortOrderEnumMap = {
  SortOrder.horizontal: 'HORIZONTAL',
  SortOrder.performance: 'PERFORMANCE',
  SortOrder.skip: 'SKIP',
  SortOrder.vertical: 'VERTICAL',
  SortOrder.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

ModelParam$Query$ModelParam$BucketData$Bucket
    _$ModelParam$Query$ModelParam$BucketData$BucketFromJson(
            Map<String, dynamic> json) =>
        ModelParam$Query$ModelParam$BucketData$Bucket()
          ..bufferSize = json['bufferSize'] as int
          ..labels = (json['labels'] as List<dynamic>)
              .map((e) =>
                  BucketFieldsMixin$Label.fromJson(e as Map<String, dynamic>))
              .toList();

Map<String, dynamic> _$ModelParam$Query$ModelParam$BucketData$BucketToJson(
        ModelParam$Query$ModelParam$BucketData$Bucket instance) =>
    <String, dynamic>{
      'bufferSize': instance.bufferSize,
      'labels': instance.labels.map((e) => e.toJson()).toList(),
    };

ModelParam$Query$ModelParam$BucketData
    _$ModelParam$Query$ModelParam$BucketDataFromJson(
            Map<String, dynamic> json) =>
        ModelParam$Query$ModelParam$BucketData()
          ..resultRankRating =
              ModelParam$Query$ModelParam$BucketData$Bucket.fromJson(
                  json['resultRankRating'] as Map<String, dynamic>)
          ..resultRankKills =
              ModelParam$Query$ModelParam$BucketData$Bucket.fromJson(
                  json['resultRankKills'] as Map<String, dynamic>)
          ..resultRank = ModelParam$Query$ModelParam$BucketData$Bucket.fromJson(
              json['resultRank'] as Map<String, dynamic>)
          ..homeScreenBucket =
              ModelParam$Query$ModelParam$BucketData$Bucket.fromJson(
                  json['homeScreenBucket'] as Map<String, dynamic>)
          ..waitingScreenBucket =
              ModelParam$Query$ModelParam$BucketData$Bucket.fromJson(
                  json['waitingScreenBucket'] as Map<String, dynamic>)
          ..gameScreenBucket =
              ModelParam$Query$ModelParam$BucketData$Bucket.fromJson(
                  json['gameScreenBucket'] as Map<String, dynamic>)
          ..loginScreenBucket =
              ModelParam$Query$ModelParam$BucketData$Bucket.fromJson(
                  json['loginScreenBucket'] as Map<String, dynamic>)
          ..myProfileScreen =
              ModelParam$Query$ModelParam$BucketData$Bucket.fromJson(
                  json['myProfileScreen'] as Map<String, dynamic>)
          ..playAgain = ModelParam$Query$ModelParam$BucketData$Bucket.fromJson(
              json['playAgain'] as Map<String, dynamic>);

Map<String, dynamic> _$ModelParam$Query$ModelParam$BucketDataToJson(
        ModelParam$Query$ModelParam$BucketData instance) =>
    <String, dynamic>{
      'resultRankRating': instance.resultRankRating.toJson(),
      'resultRankKills': instance.resultRankKills.toJson(),
      'resultRank': instance.resultRank.toJson(),
      'homeScreenBucket': instance.homeScreenBucket.toJson(),
      'waitingScreenBucket': instance.waitingScreenBucket.toJson(),
      'gameScreenBucket': instance.gameScreenBucket.toJson(),
      'loginScreenBucket': instance.loginScreenBucket.toJson(),
      'myProfileScreen': instance.myProfileScreen.toJson(),
      'playAgain': instance.playAgain.toJson(),
    };

ModelParam$Query$ModelParam _$ModelParam$Query$ModelParamFromJson(
        Map<String, dynamic> json) =>
    ModelParam$Query$ModelParam()
      ..modelName = json['model_name'] as String
      ..modelUrl = json['model_url'] as String
      ..labels = (json['labels'] as List<dynamic>)
          .map((e) => ModelParam$Query$ModelParam$Label.fromJson(
              e as Map<String, dynamic>))
          .toList()
      ..bucket = json['bucket'] == null
          ? null
          : ModelParam$Query$ModelParam$BucketData.fromJson(
              json['bucket'] as Map<String, dynamic>);

Map<String, dynamic> _$ModelParam$Query$ModelParamToJson(
        ModelParam$Query$ModelParam instance) =>
    <String, dynamic>{
      'model_name': instance.modelName,
      'model_url': instance.modelUrl,
      'labels': instance.labels.map((e) => e.toJson()).toList(),
      'bucket': instance.bucket?.toJson(),
    };

ModelParam$Query _$ModelParam$QueryFromJson(Map<String, dynamic> json) =>
    ModelParam$Query()
      ..modelParam = json['modelParam'] == null
          ? null
          : ModelParam$Query$ModelParam.fromJson(
              json['modelParam'] as Map<String, dynamic>);

Map<String, dynamic> _$ModelParam$QueryToJson(ModelParam$Query instance) =>
    <String, dynamic>{
      'modelParam': instance.modelParam?.toJson(),
    };

BucketFieldsMixin$Label _$BucketFieldsMixin$LabelFromJson(
        Map<String, dynamic> json) =>
    BucketFieldsMixin$Label()
      ..index = json['index'] as int
      ..name = json['name'] as String
      ..threshold = (json['threshold'] as num).toDouble()
      ..individualOCR = json['individualOCR'] as bool
      ..sortOrder = $enumDecode(_$SortOrderEnumMap, json['sortOrder'],
          unknownValue: SortOrder.artemisUnknown)
      ..mandatory = json['mandatory'] as bool?
      ..shouldPerformScaleAndStitching =
          json['shouldPerformScaleAndStitching'] as bool?;

Map<String, dynamic> _$BucketFieldsMixin$LabelToJson(
        BucketFieldsMixin$Label instance) =>
    <String, dynamic>{
      'index': instance.index,
      'name': instance.name,
      'threshold': instance.threshold,
      'individualOCR': instance.individualOCR,
      'sortOrder': _$SortOrderEnumMap[instance.sortOrder]!,
      'mandatory': instance.mandatory,
      'shouldPerformScaleAndStitching': instance.shouldPerformScaleAndStitching,
    };

RequestOtp$Mutation$DefaultPayload _$RequestOtp$Mutation$DefaultPayloadFromJson(
        Map<String, dynamic> json) =>
    RequestOtp$Mutation$DefaultPayload()..message = json['message'] as String;

Map<String, dynamic> _$RequestOtp$Mutation$DefaultPayloadToJson(
        RequestOtp$Mutation$DefaultPayload instance) =>
    <String, dynamic>{
      'message': instance.message,
    };

RequestOtp$Mutation _$RequestOtp$MutationFromJson(Map<String, dynamic> json) =>
    RequestOtp$Mutation()
      ..generateOTP = RequestOtp$Mutation$DefaultPayload.fromJson(
          json['generateOTP'] as Map<String, dynamic>);

Map<String, dynamic> _$RequestOtp$MutationToJson(
        RequestOtp$Mutation instance) =>
    <String, dynamic>{
      'generateOTP': instance.generateOTP.toJson(),
    };

AddUser$Mutation$LoggedInUser$User _$AddUser$Mutation$LoggedInUser$UserFromJson(
        Map<String, dynamic> json) =>
    AddUser$Mutation$LoggedInUser$User()..id = json['id'] as int;

Map<String, dynamic> _$AddUser$Mutation$LoggedInUser$UserToJson(
        AddUser$Mutation$LoggedInUser$User instance) =>
    <String, dynamic>{
      'id': instance.id,
    };

AddUser$Mutation$LoggedInUser _$AddUser$Mutation$LoggedInUserFromJson(
        Map<String, dynamic> json) =>
    AddUser$Mutation$LoggedInUser()
      ..token = json['token'] as String
      ..user = json['user'] == null
          ? null
          : AddUser$Mutation$LoggedInUser$User.fromJson(
              json['user'] as Map<String, dynamic>);

Map<String, dynamic> _$AddUser$Mutation$LoggedInUserToJson(
        AddUser$Mutation$LoggedInUser instance) =>
    <String, dynamic>{
      'token': instance.token,
      'user': instance.user?.toJson(),
    };

AddUser$Mutation _$AddUser$MutationFromJson(Map<String, dynamic> json) =>
    AddUser$Mutation()
      ..addUser = AddUser$Mutation$LoggedInUser.fromJson(
          json['addUser'] as Map<String, dynamic>);

Map<String, dynamic> _$AddUser$MutationToJson(AddUser$Mutation instance) =>
    <String, dynamic>{
      'addUser': instance.addUser.toJson(),
    };

CreateGameProfile$Mutation$Profile _$CreateGameProfile$Mutation$ProfileFromJson(
        Map<String, dynamic> json) =>
    CreateGameProfile$Mutation$Profile()
      ..eSport = $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown)
      ..metadata = ProfileMixin$ProfileMetadata.fromJson(
          json['metadata'] as Map<String, dynamic>)
      ..username = json['username'] as String?
      ..profileId = json['profileId'] as String?;

Map<String, dynamic> _$CreateGameProfile$Mutation$ProfileToJson(
        CreateGameProfile$Mutation$Profile instance) =>
    <String, dynamic>{
      'eSport': _$ESportsEnumMap[instance.eSport]!,
      'metadata': instance.metadata.toJson(),
      'username': instance.username,
      'profileId': instance.profileId,
    };

CreateGameProfile$Mutation _$CreateGameProfile$MutationFromJson(
        Map<String, dynamic> json) =>
    CreateGameProfile$Mutation()
      ..createProfile = CreateGameProfile$Mutation$Profile.fromJson(
          json['createProfile'] as Map<String, dynamic>);

Map<String, dynamic> _$CreateGameProfile$MutationToJson(
        CreateGameProfile$Mutation instance) =>
    <String, dynamic>{
      'createProfile': instance.createProfile.toJson(),
    };

FFMaxProfileInput _$FFMaxProfileInputFromJson(Map<String, dynamic> json) =>
    FFMaxProfileInput(
      metadata: json['metadata'] == null
          ? null
          : FFMaxProfileMetadataInput.fromJson(
              json['metadata'] as Map<String, dynamic>),
      profileId: json['profileId'] as String?,
      username: json['username'] as String?,
    );

Map<String, dynamic> _$FFMaxProfileInputToJson(FFMaxProfileInput instance) =>
    <String, dynamic>{
      'metadata': instance.metadata?.toJson(),
      'profileId': instance.profileId,
      'username': instance.username,
    };

FFMaxProfileMetadataInput _$FFMaxProfileMetadataInputFromJson(
        Map<String, dynamic> json) =>
    FFMaxProfileMetadataInput(
      levels: (json['levels'] as List<dynamic>?)
          ?.map((e) => FFMaxProfilemetadataLevelInput.fromJson(
              e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$FFMaxProfileMetadataInputToJson(
        FFMaxProfileMetadataInput instance) =>
    <String, dynamic>{
      'levels': instance.levels?.map((e) => e.toJson()).toList(),
    };

FFMaxProfilemetadataLevelInput _$FFMaxProfilemetadataLevelInputFromJson(
        Map<String, dynamic> json) =>
    FFMaxProfilemetadataLevelInput(
      group: $enumDecode(_$FfMaxGroupsEnumMap, json['group'],
          unknownValue: FfMaxGroups.artemisUnknown),
      level: $enumDecode(_$FfMaxLevelsEnumMap, json['level'],
          unknownValue: FfMaxLevels.artemisUnknown),
    );

Map<String, dynamic> _$FFMaxProfilemetadataLevelInputToJson(
        FFMaxProfilemetadataLevelInput instance) =>
    <String, dynamic>{
      'group': _$FfMaxGroupsEnumMap[instance.group]!,
      'level': _$FfMaxLevelsEnumMap[instance.level]!,
    };

BgmiProfileInput _$BgmiProfileInputFromJson(Map<String, dynamic> json) =>
    BgmiProfileInput(
      metadata: json['metadata'] == null
          ? null
          : BgmiProfileMetadataInput.fromJson(
              json['metadata'] as Map<String, dynamic>),
      profileId: json['profileId'] as String?,
      username: json['username'] as String?,
    );

Map<String, dynamic> _$BgmiProfileInputToJson(BgmiProfileInput instance) =>
    <String, dynamic>{
      'metadata': instance.metadata?.toJson(),
      'profileId': instance.profileId,
      'username': instance.username,
    };

BgmiProfileMetadataInput _$BgmiProfileMetadataInputFromJson(
        Map<String, dynamic> json) =>
    BgmiProfileMetadataInput(
      levels: (json['levels'] as List<dynamic>?)
          ?.map((e) =>
              BgmiProfilemetadataLevelInput.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$BgmiProfileMetadataInputToJson(
        BgmiProfileMetadataInput instance) =>
    <String, dynamic>{
      'levels': instance.levels?.map((e) => e.toJson()).toList(),
    };

BgmiProfilemetadataLevelInput _$BgmiProfilemetadataLevelInputFromJson(
        Map<String, dynamic> json) =>
    BgmiProfilemetadataLevelInput(
      group: $enumDecode(_$BgmiGroupsEnumMap, json['group'],
          unknownValue: BgmiGroups.artemisUnknown),
      level: $enumDecode(_$BgmiLevelsEnumMap, json['level'],
          unknownValue: BgmiLevels.artemisUnknown),
    );

Map<String, dynamic> _$BgmiProfilemetadataLevelInputToJson(
        BgmiProfilemetadataLevelInput instance) =>
    <String, dynamic>{
      'group': _$BgmiGroupsEnumMap[instance.group]!,
      'level': _$BgmiLevelsEnumMap[instance.level]!,
    };

UpdateGameProfile$Mutation$Profile _$UpdateGameProfile$Mutation$ProfileFromJson(
        Map<String, dynamic> json) =>
    UpdateGameProfile$Mutation$Profile()
      ..eSport = $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown)
      ..metadata = ProfileMixin$ProfileMetadata.fromJson(
          json['metadata'] as Map<String, dynamic>)
      ..username = json['username'] as String?
      ..profileId = json['profileId'] as String?;

Map<String, dynamic> _$UpdateGameProfile$Mutation$ProfileToJson(
        UpdateGameProfile$Mutation$Profile instance) =>
    <String, dynamic>{
      'eSport': _$ESportsEnumMap[instance.eSport]!,
      'metadata': instance.metadata.toJson(),
      'username': instance.username,
      'profileId': instance.profileId,
    };

UpdateGameProfile$Mutation _$UpdateGameProfile$MutationFromJson(
        Map<String, dynamic> json) =>
    UpdateGameProfile$Mutation()
      ..updateProfile = UpdateGameProfile$Mutation$Profile.fromJson(
          json['updateProfile'] as Map<String, dynamic>);

Map<String, dynamic> _$UpdateGameProfile$MutationToJson(
        UpdateGameProfile$Mutation instance) =>
    <String, dynamic>{
      'updateProfile': instance.updateProfile.toJson(),
    };

SubmitBGMIGame$Mutation$SubmitGameResponse
    _$SubmitBGMIGame$Mutation$SubmitGameResponseFromJson(
            Map<String, dynamic> json) =>
        SubmitBGMIGame$Mutation$SubmitGameResponse()
          ..game = GameResponseMixin$Game.fromJson(
              json['game'] as Map<String, dynamic>)
          ..tournaments = (json['tournaments'] as List<dynamic>)
              .map((e) => e == null
                  ? null
                  : GameResponseMixin$SubmittedGameTournament.fromJson(
                      e as Map<String, dynamic>))
              .toList();

Map<String, dynamic> _$SubmitBGMIGame$Mutation$SubmitGameResponseToJson(
        SubmitBGMIGame$Mutation$SubmitGameResponse instance) =>
    <String, dynamic>{
      'game': instance.game.toJson(),
      'tournaments': instance.tournaments.map((e) => e?.toJson()).toList(),
    };

SubmitBGMIGame$Mutation _$SubmitBGMIGame$MutationFromJson(
        Map<String, dynamic> json) =>
    SubmitBGMIGame$Mutation()
      ..submitBgmiGame = SubmitBGMIGame$Mutation$SubmitGameResponse.fromJson(
          json['submitBgmiGame'] as Map<String, dynamic>);

Map<String, dynamic> _$SubmitBGMIGame$MutationToJson(
        SubmitBGMIGame$Mutation instance) =>
    <String, dynamic>{
      'submitBgmiGame': instance.submitBgmiGame.toJson(),
    };

GameResponseMixin$Game$GameMetadata$BgmiMetadata
    _$GameResponseMixin$Game$GameMetadata$BgmiMetadataFromJson(
            Map<String, dynamic> json) =>
        GameResponseMixin$Game$GameMetadata$BgmiMetadata()
          ..$$typename = json['__typename'] as String?
          ..initialTier = json['initialTier'] as int
          ..finalTier = json['finalTier'] as int
          ..kills = json['kills'] as int
          ..bgmiGroup = $enumDecode(_$BgmiGroupsEnumMap, json['bgmiGroup'],
              unknownValue: BgmiGroups.artemisUnknown)
          ..bgmiMap = $enumDecode(_$BgmiMapsEnumMap, json['bgmiMap'],
              unknownValue: BgmiMaps.artemisUnknown);

Map<String, dynamic> _$GameResponseMixin$Game$GameMetadata$BgmiMetadataToJson(
        GameResponseMixin$Game$GameMetadata$BgmiMetadata instance) =>
    <String, dynamic>{
      '__typename': instance.$$typename,
      'initialTier': instance.initialTier,
      'finalTier': instance.finalTier,
      'kills': instance.kills,
      'bgmiGroup': _$BgmiGroupsEnumMap[instance.bgmiGroup]!,
      'bgmiMap': _$BgmiMapsEnumMap[instance.bgmiMap]!,
    };

GameResponseMixin$Game$GameMetadata$FfMaxMetadata
    _$GameResponseMixin$Game$GameMetadata$FfMaxMetadataFromJson(
            Map<String, dynamic> json) =>
        GameResponseMixin$Game$GameMetadata$FfMaxMetadata()
          ..$$typename = json['__typename'] as String?
          ..initialTier = json['initialTier'] as int
          ..finalTier = json['finalTier'] as int
          ..kills = json['kills'] as int
          ..ffGroup = $enumDecode(_$FfMaxGroupsEnumMap, json['ffGroup'],
              unknownValue: FfMaxGroups.artemisUnknown)
          ..ffMap = $enumDecode(_$FfMaxMapsEnumMap, json['ffMap'],
              unknownValue: FfMaxMaps.artemisUnknown);

Map<String, dynamic> _$GameResponseMixin$Game$GameMetadata$FfMaxMetadataToJson(
        GameResponseMixin$Game$GameMetadata$FfMaxMetadata instance) =>
    <String, dynamic>{
      '__typename': instance.$$typename,
      'initialTier': instance.initialTier,
      'finalTier': instance.finalTier,
      'kills': instance.kills,
      'ffGroup': _$FfMaxGroupsEnumMap[instance.ffGroup]!,
      'ffMap': _$FfMaxMapsEnumMap[instance.ffMap]!,
    };

GameResponseMixin$Game$GameMetadata
    _$GameResponseMixin$Game$GameMetadataFromJson(Map<String, dynamic> json) =>
        GameResponseMixin$Game$GameMetadata()
          ..$$typename = json['__typename'] as String?;

Map<String, dynamic> _$GameResponseMixin$Game$GameMetadataToJson(
        GameResponseMixin$Game$GameMetadata instance) =>
    <String, dynamic>{
      '__typename': instance.$$typename,
    };

GameResponseMixin$Game _$GameResponseMixin$GameFromJson(
        Map<String, dynamic> json) =>
    GameResponseMixin$Game()
      ..id = json['id'] as int
      ..eSport = $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown)
      ..rank = json['rank'] as int
      ..score = (json['score'] as num).toDouble()
      ..userId = json['userId'] as int
      ..teamRank = json['teamRank'] as int?
      ..metadata = GameResponseMixin$Game$GameMetadata.fromJson(
          json['metadata'] as Map<String, dynamic>);

Map<String, dynamic> _$GameResponseMixin$GameToJson(
        GameResponseMixin$Game instance) =>
    <String, dynamic>{
      'id': instance.id,
      'eSport': _$ESportsEnumMap[instance.eSport]!,
      'rank': instance.rank,
      'score': instance.score,
      'userId': instance.userId,
      'teamRank': instance.teamRank,
      'metadata': instance.metadata.toJson(),
    };

GameResponseMixin$SubmittedGameTournament$Tournament
    _$GameResponseMixin$SubmittedGameTournament$TournamentFromJson(
            Map<String, dynamic> json) =>
        GameResponseMixin$SubmittedGameTournament$Tournament()
          ..id = json['id'] as int
          ..name = json['name'] as String;

Map<String, dynamic>
    _$GameResponseMixin$SubmittedGameTournament$TournamentToJson(
            GameResponseMixin$SubmittedGameTournament$Tournament instance) =>
        <String, dynamic>{
          'id': instance.id,
          'name': instance.name,
        };

GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$User
    _$GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$UserFromJson(
            Map<String, dynamic> json) =>
        GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$User()
          ..id = json['id'] as int
          ..name = json['name'] as String
          ..phone = json['phone'] as String?
          ..image = json['image'] as String?
          ..username = json['username'] as String;

Map<String, dynamic>
    _$GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$UserToJson(
            GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$User
                instance) =>
        <String, dynamic>{
          'id': instance.id,
          'name': instance.name,
          'phone': instance.phone,
          'image': instance.image,
          'username': instance.username,
        };

GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse
    _$GameResponseMixin$SubmittedGameTournament$SubmissionStateResponseFromJson(
            Map<String, dynamic> json) =>
        GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse()
          ..userId = json['userId'] as int
          ..hasSubmitted = json['hasSubmitted'] as bool
          ..user =
              GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$User
                  .fromJson(json['user'] as Map<String, dynamic>);

Map<String, dynamic>
    _$GameResponseMixin$SubmittedGameTournament$SubmissionStateResponseToJson(
            GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse
                instance) =>
        <String, dynamic>{
          'userId': instance.userId,
          'hasSubmitted': instance.hasSubmitted,
          'user': instance.user.toJson(),
        };

GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$User
    _$GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$UserFromJson(
            Map<String, dynamic> json) =>
        GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$User()
          ..username = json['username'] as String
          ..image = json['image'] as String?;

Map<String, dynamic>
    _$GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$UserToJson(
            GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$User
                instance) =>
        <String, dynamic>{
          'username': instance.username,
          'image': instance.image,
        };

GameResponseMixin$SubmittedGameTournament$SquadScoresResponse
    _$GameResponseMixin$SubmittedGameTournament$SquadScoresResponseFromJson(
            Map<String, dynamic> json) =>
        GameResponseMixin$SubmittedGameTournament$SquadScoresResponse()
          ..kills = json['kills'] as int
          ..user =
              GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$User
                  .fromJson(json['user'] as Map<String, dynamic>);

Map<String, dynamic>
    _$GameResponseMixin$SubmittedGameTournament$SquadScoresResponseToJson(
            GameResponseMixin$SubmittedGameTournament$SquadScoresResponse
                instance) =>
        <String, dynamic>{
          'kills': instance.kills,
          'user': instance.user.toJson(),
        };

GameResponseMixin$SubmittedGameTournament
    _$GameResponseMixin$SubmittedGameTournamentFromJson(
            Map<String, dynamic> json) =>
        GameResponseMixin$SubmittedGameTournament()
          ..isAdded = json['isAdded'] as bool
          ..isTop = json['isTop'] as bool
          ..exclusionReason = json['exclusionReason'] as String?
          ..tournament =
              GameResponseMixin$SubmittedGameTournament$Tournament.fromJson(
                  json['tournament'] as Map<String, dynamic>)
          ..submissionState = (json['submissionState'] as List<dynamic>?)
              ?.map((e) =>
                  GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse
                      .fromJson(e as Map<String, dynamic>))
              .toList()
          ..squadScores = (json['squadScores'] as List<dynamic>?)
              ?.map((e) =>
                  GameResponseMixin$SubmittedGameTournament$SquadScoresResponse
                      .fromJson(e as Map<String, dynamic>))
              .toList();

Map<String, dynamic> _$GameResponseMixin$SubmittedGameTournamentToJson(
        GameResponseMixin$SubmittedGameTournament instance) =>
    <String, dynamic>{
      'isAdded': instance.isAdded,
      'isTop': instance.isTop,
      'exclusionReason': instance.exclusionReason,
      'tournament': instance.tournament.toJson(),
      'submissionState':
          instance.submissionState?.map((e) => e.toJson()).toList(),
      'squadScores': instance.squadScores?.map((e) => e.toJson()).toList(),
    };

SquadMemberGameInfo _$SquadMemberGameInfoFromJson(Map<String, dynamic> json) =>
    SquadMemberGameInfo(
      kills: json['kills'] as int,
      username: json['username'] as String,
    );

Map<String, dynamic> _$SquadMemberGameInfoToJson(
        SquadMemberGameInfo instance) =>
    <String, dynamic>{
      'kills': instance.kills,
      'username': instance.username,
    };

SubmitFFMaxGame$Mutation$SubmitGameResponse
    _$SubmitFFMaxGame$Mutation$SubmitGameResponseFromJson(
            Map<String, dynamic> json) =>
        SubmitFFMaxGame$Mutation$SubmitGameResponse()
          ..game = GameResponseMixin$Game.fromJson(
              json['game'] as Map<String, dynamic>)
          ..tournaments = (json['tournaments'] as List<dynamic>)
              .map((e) => e == null
                  ? null
                  : GameResponseMixin$SubmittedGameTournament.fromJson(
                      e as Map<String, dynamic>))
              .toList();

Map<String, dynamic> _$SubmitFFMaxGame$Mutation$SubmitGameResponseToJson(
        SubmitFFMaxGame$Mutation$SubmitGameResponse instance) =>
    <String, dynamic>{
      'game': instance.game.toJson(),
      'tournaments': instance.tournaments.map((e) => e?.toJson()).toList(),
    };

SubmitFFMaxGame$Mutation _$SubmitFFMaxGame$MutationFromJson(
        Map<String, dynamic> json) =>
    SubmitFFMaxGame$Mutation()
      ..submitFfMaxGame = SubmitFFMaxGame$Mutation$SubmitGameResponse.fromJson(
          json['submitFfMaxGame'] as Map<String, dynamic>);

Map<String, dynamic> _$SubmitFFMaxGame$MutationToJson(
        SubmitFFMaxGame$Mutation instance) =>
    <String, dynamic>{
      'submitFfMaxGame': instance.submitFfMaxGame.toJson(),
    };

SubmitPreferences$Mutation$UserPreference
    _$SubmitPreferences$Mutation$UserPreferenceFromJson(
            Map<String, dynamic> json) =>
        SubmitPreferences$Mutation$UserPreference()
          ..playingReason = (json['playingReason'] as List<dynamic>?)
              ?.map((e) => $enumDecode(_$PlayingReasonPreferenceEnumMap, e,
                  unknownValue: PlayingReasonPreference.artemisUnknown))
              .toList()
          ..timeOfDay = (json['timeOfDay'] as List<dynamic>?)
              ?.map((e) => $enumDecode(_$TimeOfDayPreferenceEnumMap, e,
                  unknownValue: TimeOfDayPreference.artemisUnknown))
              .toList()
          ..roles = (json['roles'] as List<dynamic>?)
              ?.map((e) => $enumDecode(_$RolePreferenceEnumMap, e,
                  unknownValue: RolePreference.artemisUnknown))
              .toList()
          ..timeOfWeek = $enumDecodeNullable(
              _$TimeOfWeekPreferenceEnumMap, json['timeOfWeek'],
              unknownValue: TimeOfWeekPreference.artemisUnknown);

Map<String, dynamic> _$SubmitPreferences$Mutation$UserPreferenceToJson(
        SubmitPreferences$Mutation$UserPreference instance) =>
    <String, dynamic>{
      'playingReason': instance.playingReason
          ?.map((e) => _$PlayingReasonPreferenceEnumMap[e]!)
          .toList(),
      'timeOfDay': instance.timeOfDay
          ?.map((e) => _$TimeOfDayPreferenceEnumMap[e]!)
          .toList(),
      'roles': instance.roles?.map((e) => _$RolePreferenceEnumMap[e]!).toList(),
      'timeOfWeek': _$TimeOfWeekPreferenceEnumMap[instance.timeOfWeek],
    };

SubmitPreferences$Mutation _$SubmitPreferences$MutationFromJson(
        Map<String, dynamic> json) =>
    SubmitPreferences$Mutation()
      ..submitPreferences = SubmitPreferences$Mutation$UserPreference.fromJson(
          json['submitPreferences'] as Map<String, dynamic>);

Map<String, dynamic> _$SubmitPreferences$MutationToJson(
        SubmitPreferences$Mutation instance) =>
    <String, dynamic>{
      'submitPreferences': instance.submitPreferences.toJson(),
    };

JoinTournament$Mutation$DefaultPayload
    _$JoinTournament$Mutation$DefaultPayloadFromJson(
            Map<String, dynamic> json) =>
        JoinTournament$Mutation$DefaultPayload()
          ..message = json['message'] as String;

Map<String, dynamic> _$JoinTournament$Mutation$DefaultPayloadToJson(
        JoinTournament$Mutation$DefaultPayload instance) =>
    <String, dynamic>{
      'message': instance.message,
    };

JoinTournament$Mutation _$JoinTournament$MutationFromJson(
        Map<String, dynamic> json) =>
    JoinTournament$Mutation()
      ..joinTournament = JoinTournament$Mutation$DefaultPayload.fromJson(
          json['joinTournament'] as Map<String, dynamic>);

Map<String, dynamic> _$JoinTournament$MutationToJson(
        JoinTournament$Mutation instance) =>
    <String, dynamic>{
      'joinTournament': instance.joinTournament.toJson(),
    };

Withdraw$Mutation$TransactionResponse$Wallet
    _$Withdraw$Mutation$TransactionResponse$WalletFromJson(
            Map<String, dynamic> json) =>
        Withdraw$Mutation$TransactionResponse$Wallet()
          ..id = json['id'] as int
          ..bonus = (json['bonus'] as num).toDouble()
          ..deposit = (json['deposit'] as num).toDouble()
          ..winning = (json['winning'] as num).toDouble();

Map<String, dynamic> _$Withdraw$Mutation$TransactionResponse$WalletToJson(
        Withdraw$Mutation$TransactionResponse$Wallet instance) =>
    <String, dynamic>{
      'id': instance.id,
      'bonus': instance.bonus,
      'deposit': instance.deposit,
      'winning': instance.winning,
    };

Withdraw$Mutation$TransactionResponse
    _$Withdraw$Mutation$TransactionResponseFromJson(
            Map<String, dynamic> json) =>
        Withdraw$Mutation$TransactionResponse()
          ..success = json['success'] as bool
          ..transactionId = json['transactionId'] as String?
          ..wallet = Withdraw$Mutation$TransactionResponse$Wallet.fromJson(
              json['wallet'] as Map<String, dynamic>);

Map<String, dynamic> _$Withdraw$Mutation$TransactionResponseToJson(
        Withdraw$Mutation$TransactionResponse instance) =>
    <String, dynamic>{
      'success': instance.success,
      'transactionId': instance.transactionId,
      'wallet': instance.wallet.toJson(),
    };

Withdraw$Mutation _$Withdraw$MutationFromJson(Map<String, dynamic> json) =>
    Withdraw$Mutation()
      ..withdrawUPI = Withdraw$Mutation$TransactionResponse.fromJson(
          json['withdrawUPI'] as Map<String, dynamic>);

Map<String, dynamic> _$Withdraw$MutationToJson(Withdraw$Mutation instance) =>
    <String, dynamic>{
      'withdrawUPI': instance.withdrawUPI.toJson(),
    };

CreateSquad$Mutation$Squad _$CreateSquad$Mutation$SquadFromJson(
        Map<String, dynamic> json) =>
    CreateSquad$Mutation$Squad()
      ..name = json['name'] as String
      ..id = json['id'] as int
      ..inviteCode = json['inviteCode'] as String
      ..members = (json['members'] as List<dynamic>)
          .map(
              (e) => SquadMixin$SquadMember.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$CreateSquad$Mutation$SquadToJson(
        CreateSquad$Mutation$Squad instance) =>
    <String, dynamic>{
      'name': instance.name,
      'id': instance.id,
      'inviteCode': instance.inviteCode,
      'members': instance.members.map((e) => e.toJson()).toList(),
    };

CreateSquad$Mutation _$CreateSquad$MutationFromJson(
        Map<String, dynamic> json) =>
    CreateSquad$Mutation()
      ..createSquad = CreateSquad$Mutation$Squad.fromJson(
          json['createSquad'] as Map<String, dynamic>);

Map<String, dynamic> _$CreateSquad$MutationToJson(
        CreateSquad$Mutation instance) =>
    <String, dynamic>{
      'createSquad': instance.createSquad.toJson(),
    };

JoinSquad$Mutation$Squad _$JoinSquad$Mutation$SquadFromJson(
        Map<String, dynamic> json) =>
    JoinSquad$Mutation$Squad()
      ..name = json['name'] as String
      ..id = json['id'] as int
      ..inviteCode = json['inviteCode'] as String
      ..members = (json['members'] as List<dynamic>)
          .map(
              (e) => SquadMixin$SquadMember.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$JoinSquad$Mutation$SquadToJson(
        JoinSquad$Mutation$Squad instance) =>
    <String, dynamic>{
      'name': instance.name,
      'id': instance.id,
      'inviteCode': instance.inviteCode,
      'members': instance.members.map((e) => e.toJson()).toList(),
    };

JoinSquad$Mutation _$JoinSquad$MutationFromJson(Map<String, dynamic> json) =>
    JoinSquad$Mutation()
      ..joinSquad = JoinSquad$Mutation$Squad.fromJson(
          json['joinSquad'] as Map<String, dynamic>);

Map<String, dynamic> _$JoinSquad$MutationToJson(JoinSquad$Mutation instance) =>
    <String, dynamic>{
      'joinSquad': instance.joinSquad.toJson(),
    };

UpdateSquadName$Mutation$Squad _$UpdateSquadName$Mutation$SquadFromJson(
        Map<String, dynamic> json) =>
    UpdateSquadName$Mutation$Squad()
      ..name = json['name'] as String
      ..id = json['id'] as int
      ..inviteCode = json['inviteCode'] as String
      ..members = (json['members'] as List<dynamic>)
          .map(
              (e) => SquadMixin$SquadMember.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$UpdateSquadName$Mutation$SquadToJson(
        UpdateSquadName$Mutation$Squad instance) =>
    <String, dynamic>{
      'name': instance.name,
      'id': instance.id,
      'inviteCode': instance.inviteCode,
      'members': instance.members.map((e) => e.toJson()).toList(),
    };

UpdateSquadName$Mutation _$UpdateSquadName$MutationFromJson(
        Map<String, dynamic> json) =>
    UpdateSquadName$Mutation()
      ..updateSquad = UpdateSquadName$Mutation$Squad.fromJson(
          json['updateSquad'] as Map<String, dynamic>);

Map<String, dynamic> _$UpdateSquadName$MutationToJson(
        UpdateSquadName$Mutation instance) =>
    <String, dynamic>{
      'updateSquad': instance.updateSquad.toJson(),
    };

DeleteSquad$Mutation$DefaultPayload
    _$DeleteSquad$Mutation$DefaultPayloadFromJson(Map<String, dynamic> json) =>
        DeleteSquad$Mutation$DefaultPayload()
          ..message = json['message'] as String;

Map<String, dynamic> _$DeleteSquad$Mutation$DefaultPayloadToJson(
        DeleteSquad$Mutation$DefaultPayload instance) =>
    <String, dynamic>{
      'message': instance.message,
    };

DeleteSquad$Mutation _$DeleteSquad$MutationFromJson(
        Map<String, dynamic> json) =>
    DeleteSquad$Mutation()
      ..deleteSquad = DeleteSquad$Mutation$DefaultPayload.fromJson(
          json['deleteSquad'] as Map<String, dynamic>);

Map<String, dynamic> _$DeleteSquad$MutationToJson(
        DeleteSquad$Mutation instance) =>
    <String, dynamic>{
      'deleteSquad': instance.deleteSquad.toJson(),
    };

ChangeSquad$Mutation$Squad _$ChangeSquad$Mutation$SquadFromJson(
        Map<String, dynamic> json) =>
    ChangeSquad$Mutation$Squad()
      ..name = json['name'] as String
      ..id = json['id'] as int
      ..inviteCode = json['inviteCode'] as String
      ..members = (json['members'] as List<dynamic>)
          .map(
              (e) => SquadMixin$SquadMember.fromJson(e as Map<String, dynamic>))
          .toList();

Map<String, dynamic> _$ChangeSquad$Mutation$SquadToJson(
        ChangeSquad$Mutation$Squad instance) =>
    <String, dynamic>{
      'name': instance.name,
      'id': instance.id,
      'inviteCode': instance.inviteCode,
      'members': instance.members.map((e) => e.toJson()).toList(),
    };

ChangeSquad$Mutation _$ChangeSquad$MutationFromJson(
        Map<String, dynamic> json) =>
    ChangeSquad$Mutation()
      ..changeSquad = ChangeSquad$Mutation$Squad.fromJson(
          json['changeSquad'] as Map<String, dynamic>);

Map<String, dynamic> _$ChangeSquad$MutationToJson(
        ChangeSquad$Mutation instance) =>
    <String, dynamic>{
      'changeSquad': instance.changeSquad.toJson(),
    };

UnlockSquad$Mutation _$UnlockSquad$MutationFromJson(
        Map<String, dynamic> json) =>
    UnlockSquad$Mutation()..unlockSquad = json['unlockSquad'] as bool;

Map<String, dynamic> _$UnlockSquad$MutationToJson(
        UnlockSquad$Mutation instance) =>
    <String, dynamic>{
      'unlockSquad': instance.unlockSquad,
    };

SubmitFeedback$Mutation$DefaultPayload
    _$SubmitFeedback$Mutation$DefaultPayloadFromJson(
            Map<String, dynamic> json) =>
        SubmitFeedback$Mutation$DefaultPayload()
          ..message = json['message'] as String;

Map<String, dynamic> _$SubmitFeedback$Mutation$DefaultPayloadToJson(
        SubmitFeedback$Mutation$DefaultPayload instance) =>
    <String, dynamic>{
      'message': instance.message,
    };

SubmitFeedback$Mutation _$SubmitFeedback$MutationFromJson(
        Map<String, dynamic> json) =>
    SubmitFeedback$Mutation()
      ..submitFeedback = json['submitFeedback'] == null
          ? null
          : SubmitFeedback$Mutation$DefaultPayload.fromJson(
              json['submitFeedback'] as Map<String, dynamic>);

Map<String, dynamic> _$SubmitFeedback$MutationToJson(
        SubmitFeedback$Mutation instance) =>
    <String, dynamic>{
      'submitFeedback': instance.submitFeedback?.toJson(),
    };

SubmitFeedbackInput _$SubmitFeedbackInputFromJson(Map<String, dynamic> json) =>
    SubmitFeedbackInput(
      collectionEvent: json['collectionEvent'] as String,
      comments: json['comments'] as String?,
      rating: json['rating'] as int,
      ratingReason: json['ratingReason'] as String?,
    );

Map<String, dynamic> _$SubmitFeedbackInputToJson(
        SubmitFeedbackInput instance) =>
    <String, dynamic>{
      'collectionEvent': instance.collectionEvent,
      'comments': instance.comments,
      'rating': instance.rating,
      'ratingReason': instance.ratingReason,
    };

DepositUPIManual$Mutation$TransactionResponse$Wallet
    _$DepositUPIManual$Mutation$TransactionResponse$WalletFromJson(
            Map<String, dynamic> json) =>
        DepositUPIManual$Mutation$TransactionResponse$Wallet()
          ..id = json['id'] as int
          ..bonus = (json['bonus'] as num).toDouble()
          ..deposit = (json['deposit'] as num).toDouble()
          ..winning = (json['winning'] as num).toDouble();

Map<String, dynamic>
    _$DepositUPIManual$Mutation$TransactionResponse$WalletToJson(
            DepositUPIManual$Mutation$TransactionResponse$Wallet instance) =>
        <String, dynamic>{
          'id': instance.id,
          'bonus': instance.bonus,
          'deposit': instance.deposit,
          'winning': instance.winning,
        };

DepositUPIManual$Mutation$TransactionResponse
    _$DepositUPIManual$Mutation$TransactionResponseFromJson(
            Map<String, dynamic> json) =>
        DepositUPIManual$Mutation$TransactionResponse()
          ..wallet =
              DepositUPIManual$Mutation$TransactionResponse$Wallet.fromJson(
                  json['wallet'] as Map<String, dynamic>)
          ..success = json['success'] as bool
          ..transactionId = json['transactionId'] as String?;

Map<String, dynamic> _$DepositUPIManual$Mutation$TransactionResponseToJson(
        DepositUPIManual$Mutation$TransactionResponse instance) =>
    <String, dynamic>{
      'wallet': instance.wallet.toJson(),
      'success': instance.success,
      'transactionId': instance.transactionId,
    };

DepositUPIManual$Mutation _$DepositUPIManual$MutationFromJson(
        Map<String, dynamic> json) =>
    DepositUPIManual$Mutation()
      ..depositUPIManual =
          DepositUPIManual$Mutation$TransactionResponse.fromJson(
              json['depositUPIManual'] as Map<String, dynamic>);

Map<String, dynamic> _$DepositUPIManual$MutationToJson(
        DepositUPIManual$Mutation instance) =>
    <String, dynamic>{
      'depositUPIManual': instance.depositUPIManual.toJson(),
    };

PaymentCreation$Mutation$PhonepeResponse
    _$PaymentCreation$Mutation$PhonepeResponseFromJson(
            Map<String, dynamic> json) =>
        PaymentCreation$Mutation$PhonepeResponse()
          ..intentUrl = json['intentUrl'] as String
          ..ledgerId = json['ledgerId'] as int
          ..success = json['success'] as bool;

Map<String, dynamic> _$PaymentCreation$Mutation$PhonepeResponseToJson(
        PaymentCreation$Mutation$PhonepeResponse instance) =>
    <String, dynamic>{
      'intentUrl': instance.intentUrl,
      'ledgerId': instance.ledgerId,
      'success': instance.success,
    };

PaymentCreation$Mutation _$PaymentCreation$MutationFromJson(
        Map<String, dynamic> json) =>
    PaymentCreation$Mutation()
      ..paymentCreation = PaymentCreation$Mutation$PhonepeResponse.fromJson(
          json['paymentCreation'] as Map<String, dynamic>);

Map<String, dynamic> _$PaymentCreation$MutationToJson(
        PaymentCreation$Mutation instance) =>
    <String, dynamic>{
      'paymentCreation': instance.paymentCreation.toJson(),
    };

EnterTournament$Mutation$UserTournament
    _$EnterTournament$Mutation$UserTournamentFromJson(
            Map<String, dynamic> json) =>
        EnterTournament$Mutation$UserTournament()
          ..joinedAt = fromGraphQLDateTimeNullableToDartDateTimeNullable(
              json['joinedAt'] as String?)
          ..rank = json['rank'] as int?
          ..score = json['score'] as int?
          ..tournament = UserTournamentMixin$Tournament.fromJson(
              json['tournament'] as Map<String, dynamic>)
          ..squad = json['squad'] == null
              ? null
              : UserTournamentMixin$Squad.fromJson(
                  json['squad'] as Map<String, dynamic>)
          ..tournamentMatchUser = json['tournamentMatchUser'] == null
              ? null
              : UserTournamentMixin$TournamentMatchUser.fromJson(
                  json['tournamentMatchUser'] as Map<String, dynamic>);

Map<String, dynamic> _$EnterTournament$Mutation$UserTournamentToJson(
        EnterTournament$Mutation$UserTournament instance) =>
    <String, dynamic>{
      'joinedAt':
          fromDartDateTimeNullableToGraphQLDateTimeNullable(instance.joinedAt),
      'rank': instance.rank,
      'score': instance.score,
      'tournament': instance.tournament.toJson(),
      'squad': instance.squad?.toJson(),
      'tournamentMatchUser': instance.tournamentMatchUser?.toJson(),
    };

EnterTournament$Mutation _$EnterTournament$MutationFromJson(
        Map<String, dynamic> json) =>
    EnterTournament$Mutation()
      ..enterTournament = EnterTournament$Mutation$UserTournament.fromJson(
          json['enterTournament'] as Map<String, dynamic>);

Map<String, dynamic> _$EnterTournament$MutationToJson(
        EnterTournament$Mutation instance) =>
    <String, dynamic>{
      'enterTournament': instance.enterTournament.toJson(),
    };

TournamentJoiningSquadInfo _$TournamentJoiningSquadInfoFromJson(
        Map<String, dynamic> json) =>
    TournamentJoiningSquadInfo(
      inviteCode: json['inviteCode'] as String?,
      name: json['name'] as String?,
    );

Map<String, dynamic> _$TournamentJoiningSquadInfoToJson(
        TournamentJoiningSquadInfo instance) =>
    <String, dynamic>{
      'inviteCode': instance.inviteCode,
      'name': instance.name,
    };

VerifyOTPArguments _$VerifyOTPArgumentsFromJson(Map<String, dynamic> json) =>
    VerifyOTPArguments(
      otp: json['otp'] as int,
      phoneNum: json['phoneNum'] as String,
    );

Map<String, dynamic> _$VerifyOTPArgumentsToJson(VerifyOTPArguments instance) =>
    <String, dynamic>{
      'otp': instance.otp,
      'phoneNum': instance.phoneNum,
    };

LoginOTPLessArguments _$LoginOTPLessArgumentsFromJson(
        Map<String, dynamic> json) =>
    LoginOTPLessArguments(
      token: json['token'] as String,
    );

Map<String, dynamic> _$LoginOTPLessArgumentsToJson(
        LoginOTPLessArguments instance) =>
    <String, dynamic>{
      'token': instance.token,
    };

CheckUniqueUserArguments _$CheckUniqueUserArgumentsFromJson(
        Map<String, dynamic> json) =>
    CheckUniqueUserArguments(
      phoneNum: json['phoneNum'] as String,
      userName: json['userName'] as String,
    );

Map<String, dynamic> _$CheckUniqueUserArgumentsToJson(
        CheckUniqueUserArguments instance) =>
    <String, dynamic>{
      'phoneNum': instance.phoneNum,
      'userName': instance.userName,
    };

GetUserListByPreferenceArguments _$GetUserListByPreferenceArgumentsFromJson(
        Map<String, dynamic> json) =>
    GetUserListByPreferenceArguments(
      preference:
          PreferencesInput.fromJson(json['preference'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$GetUserListByPreferenceArgumentsToJson(
        GetUserListByPreferenceArguments instance) =>
    <String, dynamic>{
      'preference': instance.preference.toJson(),
    };

GetMyActiveTournamentArguments _$GetMyActiveTournamentArgumentsFromJson(
        Map<String, dynamic> json) =>
    GetMyActiveTournamentArguments(
      eSport: $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown),
    );

Map<String, dynamic> _$GetMyActiveTournamentArgumentsToJson(
        GetMyActiveTournamentArguments instance) =>
    <String, dynamic>{
      'eSport': _$ESportsEnumMap[instance.eSport]!,
    };

CheckUserTournamentQualificationArguments
    _$CheckUserTournamentQualificationArgumentsFromJson(
            Map<String, dynamic> json) =>
        CheckUserTournamentQualificationArguments(
          tournamentId: json['tournamentId'] as int,
        );

Map<String, dynamic> _$CheckUserTournamentQualificationArgumentsToJson(
        CheckUserTournamentQualificationArguments instance) =>
    <String, dynamic>{
      'tournamentId': instance.tournamentId,
    };

GetTournamentMatchesArguments _$GetTournamentMatchesArgumentsFromJson(
        Map<String, dynamic> json) =>
    GetTournamentMatchesArguments(
      tournamentId: json['tournamentId'] as int,
    );

Map<String, dynamic> _$GetTournamentMatchesArgumentsToJson(
        GetTournamentMatchesArguments instance) =>
    <String, dynamic>{
      'tournamentId': instance.tournamentId,
    };

GetTopTournamentsArguments _$GetTopTournamentsArgumentsFromJson(
        Map<String, dynamic> json) =>
    GetTopTournamentsArguments(
      eSport: $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown),
    );

Map<String, dynamic> _$GetTopTournamentsArgumentsToJson(
        GetTopTournamentsArguments instance) =>
    <String, dynamic>{
      'eSport': _$ESportsEnumMap[instance.eSport]!,
    };

GetTournamentsHistoryArguments _$GetTournamentsHistoryArgumentsFromJson(
        Map<String, dynamic> json) =>
    GetTournamentsHistoryArguments(
      eSport: $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown),
    );

Map<String, dynamic> _$GetTournamentsHistoryArgumentsToJson(
        GetTournamentsHistoryArguments instance) =>
    <String, dynamic>{
      'eSport': _$ESportsEnumMap[instance.eSport]!,
    };

GetLeaderboardArguments _$GetLeaderboardArgumentsFromJson(
        Map<String, dynamic> json) =>
    GetLeaderboardArguments(
      userId: json['userId'] as int?,
      tournamentId: json['tournamentId'] as int,
      direction: $enumDecodeNullable(
          _$LeaderboardDirectionEnumMap, json['direction'],
          unknownValue: LeaderboardDirection.artemisUnknown),
      page: json['page'] as int?,
      pageSize: json['pageSize'] as int?,
    );

Map<String, dynamic> _$GetLeaderboardArgumentsToJson(
        GetLeaderboardArguments instance) =>
    <String, dynamic>{
      'userId': instance.userId,
      'tournamentId': instance.tournamentId,
      'direction': _$LeaderboardDirectionEnumMap[instance.direction],
      'page': instance.page,
      'pageSize': instance.pageSize,
    };

const _$LeaderboardDirectionEnumMap = {
  LeaderboardDirection.next: 'Next',
  LeaderboardDirection.prev: 'Prev',
  LeaderboardDirection.artemisUnknown: 'ARTEMIS_UNKNOWN',
};

GetSquadLeaderboardArguments _$GetSquadLeaderboardArgumentsFromJson(
        Map<String, dynamic> json) =>
    GetSquadLeaderboardArguments(
      squadId: json['squadId'] as int?,
      tournamentId: json['tournamentId'] as int,
      direction: $enumDecodeNullable(
          _$LeaderboardDirectionEnumMap, json['direction'],
          unknownValue: LeaderboardDirection.artemisUnknown),
      page: json['page'] as int?,
      pageSize: json['pageSize'] as int?,
    );

Map<String, dynamic> _$GetSquadLeaderboardArgumentsToJson(
        GetSquadLeaderboardArguments instance) =>
    <String, dynamic>{
      'squadId': instance.squadId,
      'tournamentId': instance.tournamentId,
      'direction': _$LeaderboardDirectionEnumMap[instance.direction],
      'page': instance.page,
      'pageSize': instance.pageSize,
    };

GetTournamentArguments _$GetTournamentArgumentsFromJson(
        Map<String, dynamic> json) =>
    GetTournamentArguments(
      tournamentId: json['tournamentId'] as int,
    );

Map<String, dynamic> _$GetTournamentArgumentsToJson(
        GetTournamentArguments instance) =>
    <String, dynamic>{
      'tournamentId': instance.tournamentId,
    };

GetGameScoringArguments _$GetGameScoringArgumentsFromJson(
        Map<String, dynamic> json) =>
    GetGameScoringArguments(
      eSport: $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown),
    );

Map<String, dynamic> _$GetGameScoringArgumentsToJson(
        GetGameScoringArguments instance) =>
    <String, dynamic>{
      'eSport': _$ESportsEnumMap[instance.eSport]!,
    };

TransactionArguments _$TransactionArgumentsFromJson(
        Map<String, dynamic> json) =>
    TransactionArguments(
      transactionId: json['transactionId'] as int,
    );

Map<String, dynamic> _$TransactionArgumentsToJson(
        TransactionArguments instance) =>
    <String, dynamic>{
      'transactionId': instance.transactionId,
    };

SearchUserArguments _$SearchUserArgumentsFromJson(Map<String, dynamic> json) =>
    SearchUserArguments(
      phoneNum: json['phoneNum'] as String?,
      userName: json['userName'] as String?,
      eSport: $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown),
      gameProfileId: json['gameProfileId'] as String?,
    );

Map<String, dynamic> _$SearchUserArgumentsToJson(
        SearchUserArguments instance) =>
    <String, dynamic>{
      'phoneNum': instance.phoneNum,
      'userName': instance.userName,
      'eSport': _$ESportsEnumMap[instance.eSport]!,
      'gameProfileId': instance.gameProfileId,
    };

RecentPlayersArguments _$RecentPlayersArgumentsFromJson(
        Map<String, dynamic> json) =>
    RecentPlayersArguments(
      tournamentId: json['tournamentId'] as int,
    );

Map<String, dynamic> _$RecentPlayersArgumentsToJson(
        RecentPlayersArguments instance) =>
    <String, dynamic>{
      'tournamentId': instance.tournamentId,
    };

TopWinnersArguments _$TopWinnersArgumentsFromJson(Map<String, dynamic> json) =>
    TopWinnersArguments(
      count: json['count'] as int,
      from: fromGraphQLDateTimeToDartDateTime(json['from'] as String),
      to: fromGraphQLDateTimeToDartDateTime(json['to'] as String),
    );

Map<String, dynamic> _$TopWinnersArgumentsToJson(
        TopWinnersArguments instance) =>
    <String, dynamic>{
      'count': instance.count,
      'from': fromDartDateTimeToGraphQLDateTime(instance.from),
      'to': fromDartDateTimeToGraphQLDateTime(instance.to),
    };

ModelParamArguments _$ModelParamArgumentsFromJson(Map<String, dynamic> json) =>
    ModelParamArguments(
      eSport: $enumDecode(_$ESportsEnumMap, json['eSport'],
          unknownValue: ESports.artemisUnknown),
    );

Map<String, dynamic> _$ModelParamArgumentsToJson(
        ModelParamArguments instance) =>
    <String, dynamic>{
      'eSport': _$ESportsEnumMap[instance.eSport]!,
    };

RequestOtpArguments _$RequestOtpArgumentsFromJson(Map<String, dynamic> json) =>
    RequestOtpArguments(
      phoneNum: json['phoneNum'] as String,
    );

Map<String, dynamic> _$RequestOtpArgumentsToJson(
        RequestOtpArguments instance) =>
    <String, dynamic>{
      'phoneNum': instance.phoneNum,
    };

AddUserArguments _$AddUserArgumentsFromJson(Map<String, dynamic> json) =>
    AddUserArguments(
      userName: json['userName'] as String,
      birthDate: fromGraphQLDateToDartDateTime(json['birthDate'] as String),
      name: json['name'] as String,
      phone: json['phone'] as String,
      referralCode: json['referralCode'] as String?,
    );

Map<String, dynamic> _$AddUserArgumentsToJson(AddUserArguments instance) =>
    <String, dynamic>{
      'userName': instance.userName,
      'birthDate': fromDartDateTimeToGraphQLDate(instance.birthDate),
      'name': instance.name,
      'phone': instance.phone,
      'referralCode': instance.referralCode,
    };

CreateGameProfileArguments _$CreateGameProfileArgumentsFromJson(
        Map<String, dynamic> json) =>
    CreateGameProfileArguments(
      ffmaxProfileInput: json['ffmaxProfileInput'] == null
          ? null
          : FFMaxProfileInput.fromJson(
              json['ffmaxProfileInput'] as Map<String, dynamic>),
      bgmiProfileMetadataInput: json['bgmiProfileMetadataInput'] == null
          ? null
          : BgmiProfileInput.fromJson(
              json['bgmiProfileMetadataInput'] as Map<String, dynamic>),
      eSports: $enumDecode(_$ESportsEnumMap, json['eSports'],
          unknownValue: ESports.artemisUnknown),
    );

Map<String, dynamic> _$CreateGameProfileArgumentsToJson(
        CreateGameProfileArguments instance) =>
    <String, dynamic>{
      'ffmaxProfileInput': instance.ffmaxProfileInput?.toJson(),
      'bgmiProfileMetadataInput': instance.bgmiProfileMetadataInput?.toJson(),
      'eSports': _$ESportsEnumMap[instance.eSports]!,
    };

UpdateGameProfileArguments _$UpdateGameProfileArgumentsFromJson(
        Map<String, dynamic> json) =>
    UpdateGameProfileArguments(
      ffmaxProfileInput: json['ffmaxProfileInput'] == null
          ? null
          : FFMaxProfileInput.fromJson(
              json['ffmaxProfileInput'] as Map<String, dynamic>),
      bgmiProfileMetadataInput: json['bgmiProfileMetadataInput'] == null
          ? null
          : BgmiProfileInput.fromJson(
              json['bgmiProfileMetadataInput'] as Map<String, dynamic>),
      eSports: $enumDecode(_$ESportsEnumMap, json['eSports'],
          unknownValue: ESports.artemisUnknown),
    );

Map<String, dynamic> _$UpdateGameProfileArgumentsToJson(
        UpdateGameProfileArguments instance) =>
    <String, dynamic>{
      'ffmaxProfileInput': instance.ffmaxProfileInput?.toJson(),
      'bgmiProfileMetadataInput': instance.bgmiProfileMetadataInput?.toJson(),
      'eSports': _$ESportsEnumMap[instance.eSports]!,
    };

SubmitBGMIGameArguments _$SubmitBGMIGameArgumentsFromJson(
        Map<String, dynamic> json) =>
    SubmitBGMIGameArguments(
      finalTier: $enumDecode(_$BgmiLevelsEnumMap, json['finalTier'],
          unknownValue: BgmiLevels.artemisUnknown),
      initialTier: $enumDecode(_$BgmiLevelsEnumMap, json['initialTier'],
          unknownValue: BgmiLevels.artemisUnknown),
      kills: json['kills'] as int,
      playedAt: fromGraphQLDateTimeToDartDateTime(json['playedAt'] as String),
      rank: json['rank'] as int,
      recording: json['recording'] as String?,
      group: $enumDecode(_$BgmiGroupsEnumMap, json['group'],
          unknownValue: BgmiGroups.artemisUnknown),
      map: $enumDecode(_$BgmiMapsEnumMap, json['map'],
          unknownValue: BgmiMaps.artemisUnknown),
      teamRank: json['teamRank'] as int?,
      squadScoring: (json['squadScoring'] as List<dynamic>?)
          ?.map((e) => SquadMemberGameInfo.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$SubmitBGMIGameArgumentsToJson(
        SubmitBGMIGameArguments instance) =>
    <String, dynamic>{
      'finalTier': _$BgmiLevelsEnumMap[instance.finalTier]!,
      'initialTier': _$BgmiLevelsEnumMap[instance.initialTier]!,
      'kills': instance.kills,
      'playedAt': fromDartDateTimeToGraphQLDateTime(instance.playedAt),
      'rank': instance.rank,
      'recording': instance.recording,
      'group': _$BgmiGroupsEnumMap[instance.group]!,
      'map': _$BgmiMapsEnumMap[instance.map]!,
      'teamRank': instance.teamRank,
      'squadScoring': instance.squadScoring?.map((e) => e.toJson()).toList(),
    };

SubmitFFMaxGameArguments _$SubmitFFMaxGameArgumentsFromJson(
        Map<String, dynamic> json) =>
    SubmitFFMaxGameArguments(
      finalTier: $enumDecode(_$FfMaxLevelsEnumMap, json['finalTier'],
          unknownValue: FfMaxLevels.artemisUnknown),
      initialTier: $enumDecode(_$FfMaxLevelsEnumMap, json['initialTier'],
          unknownValue: FfMaxLevels.artemisUnknown),
      kills: json['kills'] as int,
      playedAt: fromGraphQLDateTimeToDartDateTime(json['playedAt'] as String),
      rank: json['rank'] as int,
      recording: json['recording'] as String?,
      group: $enumDecode(_$FfMaxGroupsEnumMap, json['group'],
          unknownValue: FfMaxGroups.artemisUnknown),
      map: $enumDecode(_$FfMaxMapsEnumMap, json['map'],
          unknownValue: FfMaxMaps.artemisUnknown),
      teamRank: json['teamRank'] as int?,
      squadScoring: (json['squadScoring'] as List<dynamic>?)
          ?.map((e) => SquadMemberGameInfo.fromJson(e as Map<String, dynamic>))
          .toList(),
    );

Map<String, dynamic> _$SubmitFFMaxGameArgumentsToJson(
        SubmitFFMaxGameArguments instance) =>
    <String, dynamic>{
      'finalTier': _$FfMaxLevelsEnumMap[instance.finalTier]!,
      'initialTier': _$FfMaxLevelsEnumMap[instance.initialTier]!,
      'kills': instance.kills,
      'playedAt': fromDartDateTimeToGraphQLDateTime(instance.playedAt),
      'rank': instance.rank,
      'recording': instance.recording,
      'group': _$FfMaxGroupsEnumMap[instance.group]!,
      'map': _$FfMaxMapsEnumMap[instance.map]!,
      'teamRank': instance.teamRank,
      'squadScoring': instance.squadScoring?.map((e) => e.toJson()).toList(),
    };

SubmitPreferencesArguments _$SubmitPreferencesArgumentsFromJson(
        Map<String, dynamic> json) =>
    SubmitPreferencesArguments(
      preferencesInput: PreferencesInput.fromJson(
          json['preferencesInput'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$SubmitPreferencesArgumentsToJson(
        SubmitPreferencesArguments instance) =>
    <String, dynamic>{
      'preferencesInput': instance.preferencesInput.toJson(),
    };

JoinTournamentArguments _$JoinTournamentArgumentsFromJson(
        Map<String, dynamic> json) =>
    JoinTournamentArguments(
      tournamentId: json['tournamentId'] as int,
      phone: json['phone'] as String?,
    );

Map<String, dynamic> _$JoinTournamentArgumentsToJson(
        JoinTournamentArguments instance) =>
    <String, dynamic>{
      'tournamentId': instance.tournamentId,
      'phone': instance.phone,
    };

WithdrawArguments _$WithdrawArgumentsFromJson(Map<String, dynamic> json) =>
    WithdrawArguments(
      upi: json['upi'] as String,
      amount: (json['amount'] as num).toDouble(),
    );

Map<String, dynamic> _$WithdrawArgumentsToJson(WithdrawArguments instance) =>
    <String, dynamic>{
      'upi': instance.upi,
      'amount': instance.amount,
    };

CreateSquadArguments _$CreateSquadArgumentsFromJson(
        Map<String, dynamic> json) =>
    CreateSquadArguments(
      tournamentId: json['tournamentId'] as int,
      squadName: json['squadName'] as String,
    );

Map<String, dynamic> _$CreateSquadArgumentsToJson(
        CreateSquadArguments instance) =>
    <String, dynamic>{
      'tournamentId': instance.tournamentId,
      'squadName': instance.squadName,
    };

JoinSquadArguments _$JoinSquadArgumentsFromJson(Map<String, dynamic> json) =>
    JoinSquadArguments(
      inviteCode: json['inviteCode'] as String,
      tournamentId: json['tournamentId'] as int,
    );

Map<String, dynamic> _$JoinSquadArgumentsToJson(JoinSquadArguments instance) =>
    <String, dynamic>{
      'inviteCode': instance.inviteCode,
      'tournamentId': instance.tournamentId,
    };

UpdateSquadNameArguments _$UpdateSquadNameArgumentsFromJson(
        Map<String, dynamic> json) =>
    UpdateSquadNameArguments(
      squadName: json['squadName'] as String,
      squadId: json['squadId'] as int,
    );

Map<String, dynamic> _$UpdateSquadNameArgumentsToJson(
        UpdateSquadNameArguments instance) =>
    <String, dynamic>{
      'squadName': instance.squadName,
      'squadId': instance.squadId,
    };

DeleteSquadArguments _$DeleteSquadArgumentsFromJson(
        Map<String, dynamic> json) =>
    DeleteSquadArguments(
      squadId: json['squadId'] as int,
    );

Map<String, dynamic> _$DeleteSquadArgumentsToJson(
        DeleteSquadArguments instance) =>
    <String, dynamic>{
      'squadId': instance.squadId,
    };

ChangeSquadArguments _$ChangeSquadArgumentsFromJson(
        Map<String, dynamic> json) =>
    ChangeSquadArguments(
      tournamentId: json['tournamentId'] as int,
      newSquadId: json['newSquadId'] as int,
    );

Map<String, dynamic> _$ChangeSquadArgumentsToJson(
        ChangeSquadArguments instance) =>
    <String, dynamic>{
      'tournamentId': instance.tournamentId,
      'newSquadId': instance.newSquadId,
    };

UnlockSquadArguments _$UnlockSquadArgumentsFromJson(
        Map<String, dynamic> json) =>
    UnlockSquadArguments(
      inviteCode: json['inviteCode'] as String,
    );

Map<String, dynamic> _$UnlockSquadArgumentsToJson(
        UnlockSquadArguments instance) =>
    <String, dynamic>{
      'inviteCode': instance.inviteCode,
    };

SubmitFeedbackArguments _$SubmitFeedbackArgumentsFromJson(
        Map<String, dynamic> json) =>
    SubmitFeedbackArguments(
      input:
          SubmitFeedbackInput.fromJson(json['input'] as Map<String, dynamic>),
    );

Map<String, dynamic> _$SubmitFeedbackArgumentsToJson(
        SubmitFeedbackArguments instance) =>
    <String, dynamic>{
      'input': instance.input.toJson(),
    };

DepositUPIManualArguments _$DepositUPIManualArgumentsFromJson(
        Map<String, dynamic> json) =>
    DepositUPIManualArguments(
      upi: json['upi'] as String,
      amount: (json['amount'] as num).toDouble(),
    );

Map<String, dynamic> _$DepositUPIManualArgumentsToJson(
        DepositUPIManualArguments instance) =>
    <String, dynamic>{
      'upi': instance.upi,
      'amount': instance.amount,
    };

PaymentCreationArguments _$PaymentCreationArgumentsFromJson(
        Map<String, dynamic> json) =>
    PaymentCreationArguments(
      amount: json['amount'] as int,
      targetApp: json['targetApp'] as String,
    );

Map<String, dynamic> _$PaymentCreationArgumentsToJson(
        PaymentCreationArguments instance) =>
    <String, dynamic>{
      'amount': instance.amount,
      'targetApp': instance.targetApp,
    };

EnterTournamentArguments _$EnterTournamentArgumentsFromJson(
        Map<String, dynamic> json) =>
    EnterTournamentArguments(
      squadInfo: json['squadInfo'] == null
          ? null
          : TournamentJoiningSquadInfo.fromJson(
              json['squadInfo'] as Map<String, dynamic>),
      tournamentId: json['tournamentId'] as int,
      phone: json['phone'] as String?,
      joinCode: json['joinCode'] as String?,
    );

Map<String, dynamic> _$EnterTournamentArgumentsToJson(
        EnterTournamentArguments instance) =>
    <String, dynamic>{
      'squadInfo': instance.squadInfo?.toJson(),
      'tournamentId': instance.tournamentId,
      'phone': instance.phone,
      'joinCode': instance.joinCode,
    };
