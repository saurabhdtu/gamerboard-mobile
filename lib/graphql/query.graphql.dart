// GENERATED CODE - DO NOT MODIFY BY HAND

import 'package:artemis/artemis.dart';
import 'package:json_annotation/json_annotation.dart';
import 'package:equatable/equatable.dart';
import 'package:gql/ast.dart';
import 'package:gamerboard/utils/scalar_mapping.dart';
part 'query.graphql.g.dart';

mixin UserMixin {
  late int id;
  @JsonKey(
      fromJson: fromGraphQLDateNullableToDartDateTimeNullable,
      toJson: fromDartDateTimeNullableToGraphQLDateNullable)
  DateTime? birthdate;
  late String name;
  String? phone;
  String? image;
  late UserMixin$Achievement achievements;
  late String inviteCode;
  UserMixin$UserPreference? preferences;
  List<UserMixin$Profile>? profiles;
  late UserMixin$Wallet wallet;
  late UserMixin$UserFlags flags;
  late String username;
}
mixin UserTournamentMixin {
  @JsonKey(
      fromJson: fromGraphQLDateTimeNullableToDartDateTimeNullable,
      toJson: fromDartDateTimeNullableToGraphQLDateTimeNullable)
  DateTime? joinedAt;
  int? rank;
  int? score;
  late UserTournamentMixin$Tournament tournament;
  UserTournamentMixin$Squad? squad;
  UserTournamentMixin$TournamentMatchUser? tournamentMatchUser;
}
mixin TournamentMixin {
  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSport;
  late int id;
  late String name;
  late int maxPrize;
  late int userCount;
  late int gameCount;
  late int fee;
  @JsonKey(unknownEnumValue: MatchType.artemisUnknown)
  late MatchType matchType;
  @JsonKey(
      fromJson: fromGraphQLDateTimeNullableToDartDateTimeNullable,
      toJson: fromDartDateTimeNullableToGraphQLDateTimeNullable)
  DateTime? joinBy;
  String? joinCode;
  List<TournamentMixin$CustomQualificationRules>? qualifiers;
  late List<TournamentMixin$WinningDistribution> winningDistribution;
  List<TournamentMixin$TournamentMatch>? matches;
  late TournamentMixin$TournamentRules rules;
  @JsonKey(
      fromJson: fromGraphQLDateTimeToDartDateTime,
      toJson: fromDartDateTimeToGraphQLDateTime)
  late DateTime startTime;
  @JsonKey(
      fromJson: fromGraphQLDateTimeToDartDateTime,
      toJson: fromDartDateTimeToGraphQLDateTime)
  late DateTime endTime;
}
mixin TournamentMatchMixin {
  late int tournamentId;
  @JsonKey(
      fromJson: fromGraphQLDateTimeNullableToDartDateTimeNullable,
      toJson: fromDartDateTimeNullableToGraphQLDateTimeNullable)
  DateTime? endTime;
  @JsonKey(
      fromJson: fromGraphQLDateTimeToDartDateTime,
      toJson: fromDartDateTimeToGraphQLDateTime)
  late DateTime startTime;
  late int id;
  late int maxParticipants;
  int? minParticipants;
  TournamentMatchMixin$TournamentMatchMetadata? metadata;
}
mixin TournamentMatchMetadataMixin {
  String? roomId;
  String? roomPassword;
}
mixin SquadMixin {
  late String name;
  late int id;
  late String inviteCode;
  late List<SquadMixin$SquadMember> members;
}
mixin SquadMemberMixin {
  late bool isReady;
  late String status;
  late SquadMemberMixin$User user;
}
mixin LeaderboardUserMixin {
  late int id;
  late String name;
  String? phone;
  String? image;
  late String username;
}
mixin TournamentMatchUserMixin {
  TournamentMatchUserMixin$SlotInfo? slotInfo;
  late int id;
  late int tournamentMatchId;
  late int tournamentUserId;
  late bool notJoined;
  @JsonKey(
      fromJson: fromGraphQLDateTimeNullableToDartDateTimeNullable,
      toJson: fromDartDateTimeNullableToGraphQLDateTimeNullable)
  DateTime? sos;
}
mixin AchievementSummaryMixin {
  @JsonKey(name: 'KDRatio')
  double? kDRatio;
  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSport;
  late String group;
  @JsonKey(unknownEnumValue: MatchType.artemisUnknown)
  late MatchType matchType;
  String? maxTier;
  late int played;
  late int topTenCount;
}
mixin ProfileMixin {
  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSport;
  late ProfileMixin$ProfileMetadata metadata;
  String? username;
  String? profileId;
}
mixin WalletMixin {
  late int id;
  late double bonus;
  late double deposit;
  late double winning;
}
mixin LeaderboardRankMixin {
  late String id;
  late int rank;
  late int score;
  late int behindBy;
  late bool isDisqualified;
  LeaderboardRankMixin$LeaderboardInfo? details;
  late LeaderboardRankMixin$User user;
}
mixin SquadLeaderboardMixin {
  late String id;
  SquadLeaderboardMixin$Squad? squad;
  late int behindBy;
  late int rank;
  late int score;
  SquadLeaderboardMixin$SquadLeaderboardInfo? details;
}
mixin LeaderboardSquadMixin {
  late bool isDisqualified;
  late String name;
  late int id;
}
mixin UserSummaryMixin {
  late int id;
  late String name;
  String? image;
  List<UserSummaryMixin$Profile>? profiles;
  late String username;
}
mixin LabelFieldsMixin {
  late int index;
  late String name;
  late double threshold;
  late bool individualOCR;
  @JsonKey(unknownEnumValue: SortOrder.artemisUnknown)
  late SortOrder sortOrder;
  bool? mandatory;
  bool? shouldPerformScaleAndStitching;
}
mixin BucketFieldsMixin {
  late int bufferSize;
  late List<BucketFieldsMixin$Label> labels;
}
mixin GameResponseMixin {
  late GameResponseMixin$Game game;
  late List<GameResponseMixin$SubmittedGameTournament?> tournaments;
}
mixin UserPreferenceMixin {
  @JsonKey(unknownEnumValue: PlayingReasonPreference.artemisUnknown)
  List<PlayingReasonPreference>? playingReason;
  @JsonKey(unknownEnumValue: TimeOfDayPreference.artemisUnknown)
  List<TimeOfDayPreference>? timeOfDay;
  @JsonKey(unknownEnumValue: RolePreference.artemisUnknown)
  List<RolePreference>? roles;
  @JsonKey(unknownEnumValue: TimeOfWeekPreference.artemisUnknown)
  TimeOfWeekPreference? timeOfWeek;
}

@JsonSerializable(explicitToJson: true)
class VerifyOTP$Query$LoggedInUser$User extends JsonSerializable
    with EquatableMixin {
  VerifyOTP$Query$LoggedInUser$User();

  factory VerifyOTP$Query$LoggedInUser$User.fromJson(
          Map<String, dynamic> json) =>
      _$VerifyOTP$Query$LoggedInUser$UserFromJson(json);

  late int id;

  @override
  List<Object?> get props => [id];
  @override
  Map<String, dynamic> toJson() =>
      _$VerifyOTP$Query$LoggedInUser$UserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class VerifyOTP$Query$LoggedInUser extends JsonSerializable
    with EquatableMixin {
  VerifyOTP$Query$LoggedInUser();

  factory VerifyOTP$Query$LoggedInUser.fromJson(Map<String, dynamic> json) =>
      _$VerifyOTP$Query$LoggedInUserFromJson(json);

  late String token;

  VerifyOTP$Query$LoggedInUser$User? user;

  @override
  List<Object?> get props => [token, user];
  @override
  Map<String, dynamic> toJson() => _$VerifyOTP$Query$LoggedInUserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class VerifyOTP$Query extends JsonSerializable with EquatableMixin {
  VerifyOTP$Query();

  factory VerifyOTP$Query.fromJson(Map<String, dynamic> json) =>
      _$VerifyOTP$QueryFromJson(json);

  late VerifyOTP$Query$LoggedInUser login;

  @override
  List<Object?> get props => [login];
  @override
  Map<String, dynamic> toJson() => _$VerifyOTP$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class LoginOTPLess$Query$LoggedInUser$User extends JsonSerializable
    with EquatableMixin {
  LoginOTPLess$Query$LoggedInUser$User();

  factory LoginOTPLess$Query$LoggedInUser$User.fromJson(
          Map<String, dynamic> json) =>
      _$LoginOTPLess$Query$LoggedInUser$UserFromJson(json);

  late int id;

  @override
  List<Object?> get props => [id];
  @override
  Map<String, dynamic> toJson() =>
      _$LoginOTPLess$Query$LoggedInUser$UserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class LoginOTPLess$Query$LoggedInUser extends JsonSerializable
    with EquatableMixin {
  LoginOTPLess$Query$LoggedInUser();

  factory LoginOTPLess$Query$LoggedInUser.fromJson(Map<String, dynamic> json) =>
      _$LoginOTPLess$Query$LoggedInUserFromJson(json);

  late String token;

  LoginOTPLess$Query$LoggedInUser$User? user;

  @override
  List<Object?> get props => [token, user];
  @override
  Map<String, dynamic> toJson() =>
      _$LoginOTPLess$Query$LoggedInUserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class LoginOTPLess$Query extends JsonSerializable with EquatableMixin {
  LoginOTPLess$Query();

  factory LoginOTPLess$Query.fromJson(Map<String, dynamic> json) =>
      _$LoginOTPLess$QueryFromJson(json);

  late LoginOTPLess$Query$LoggedInUser loginOTPLess;

  @override
  List<Object?> get props => [loginOTPLess];
  @override
  Map<String, dynamic> toJson() => _$LoginOTPLess$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class CheckUniqueUser$Query$UniqueUserResponse extends JsonSerializable
    with EquatableMixin {
  CheckUniqueUser$Query$UniqueUserResponse();

  factory CheckUniqueUser$Query$UniqueUserResponse.fromJson(
          Map<String, dynamic> json) =>
      _$CheckUniqueUser$Query$UniqueUserResponseFromJson(json);

  late bool phone;

  late bool username;

  @override
  List<Object?> get props => [phone, username];
  @override
  Map<String, dynamic> toJson() =>
      _$CheckUniqueUser$Query$UniqueUserResponseToJson(this);
}

@JsonSerializable(explicitToJson: true)
class CheckUniqueUser$Query extends JsonSerializable with EquatableMixin {
  CheckUniqueUser$Query();

  factory CheckUniqueUser$Query.fromJson(Map<String, dynamic> json) =>
      _$CheckUniqueUser$QueryFromJson(json);

  late CheckUniqueUser$Query$UniqueUserResponse checkUniqueUser;

  @override
  List<Object?> get props => [checkUniqueUser];
  @override
  Map<String, dynamic> toJson() => _$CheckUniqueUser$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetMyProfile$Query$User extends JsonSerializable
    with EquatableMixin, UserMixin {
  GetMyProfile$Query$User();

  factory GetMyProfile$Query$User.fromJson(Map<String, dynamic> json) =>
      _$GetMyProfile$Query$UserFromJson(json);

  @override
  List<Object?> get props => [
        id,
        birthdate,
        name,
        phone,
        image,
        achievements,
        inviteCode,
        preferences,
        profiles,
        wallet,
        flags,
        username
      ];
  @override
  Map<String, dynamic> toJson() => _$GetMyProfile$Query$UserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetMyProfile$Query extends JsonSerializable with EquatableMixin {
  GetMyProfile$Query();

  factory GetMyProfile$Query.fromJson(Map<String, dynamic> json) =>
      _$GetMyProfile$QueryFromJson(json);

  late GetMyProfile$Query$User me;

  @override
  List<Object?> get props => [me];
  @override
  Map<String, dynamic> toJson() => _$GetMyProfile$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserMixin$Achievement$UserTournament extends JsonSerializable
    with EquatableMixin, UserTournamentMixin {
  UserMixin$Achievement$UserTournament();

  factory UserMixin$Achievement$UserTournament.fromJson(
          Map<String, dynamic> json) =>
      _$UserMixin$Achievement$UserTournamentFromJson(json);

  @override
  List<Object?> get props =>
      [joinedAt, rank, score, tournament, squad, tournamentMatchUser];
  @override
  Map<String, dynamic> toJson() =>
      _$UserMixin$Achievement$UserTournamentToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserMixin$Achievement$AchievementSummary extends JsonSerializable
    with EquatableMixin, AchievementSummaryMixin {
  UserMixin$Achievement$AchievementSummary();

  factory UserMixin$Achievement$AchievementSummary.fromJson(
          Map<String, dynamic> json) =>
      _$UserMixin$Achievement$AchievementSummaryFromJson(json);

  @override
  List<Object?> get props =>
      [kDRatio, eSport, group, matchType, maxTier, played, topTenCount];
  @override
  Map<String, dynamic> toJson() =>
      _$UserMixin$Achievement$AchievementSummaryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserMixin$Achievement extends JsonSerializable with EquatableMixin {
  UserMixin$Achievement();

  factory UserMixin$Achievement.fromJson(Map<String, dynamic> json) =>
      _$UserMixin$AchievementFromJson(json);

  UserMixin$Achievement$UserTournament? bestPerformance;

  late List<UserMixin$Achievement$AchievementSummary?> tournamentSummary;

  @override
  List<Object?> get props => [bestPerformance, tournamentSummary];
  @override
  Map<String, dynamic> toJson() => _$UserMixin$AchievementToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserMixin$UserPreference extends JsonSerializable with EquatableMixin {
  UserMixin$UserPreference();

  factory UserMixin$UserPreference.fromJson(Map<String, dynamic> json) =>
      _$UserMixin$UserPreferenceFromJson(json);

  @JsonKey(unknownEnumValue: PlayingReasonPreference.artemisUnknown)
  List<PlayingReasonPreference>? playingReason;

  @JsonKey(unknownEnumValue: RolePreference.artemisUnknown)
  List<RolePreference>? roles;

  @JsonKey(unknownEnumValue: TimeOfDayPreference.artemisUnknown)
  List<TimeOfDayPreference>? timeOfDay;

  @JsonKey(unknownEnumValue: TimeOfWeekPreference.artemisUnknown)
  TimeOfWeekPreference? timeOfWeek;

  @override
  List<Object?> get props => [playingReason, roles, timeOfDay, timeOfWeek];
  @override
  Map<String, dynamic> toJson() => _$UserMixin$UserPreferenceToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserMixin$Profile extends JsonSerializable
    with EquatableMixin, ProfileMixin {
  UserMixin$Profile();

  factory UserMixin$Profile.fromJson(Map<String, dynamic> json) =>
      _$UserMixin$ProfileFromJson(json);

  @override
  List<Object?> get props => [eSport, metadata, username, profileId];
  @override
  Map<String, dynamic> toJson() => _$UserMixin$ProfileToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserMixin$Wallet extends JsonSerializable
    with EquatableMixin, WalletMixin {
  UserMixin$Wallet();

  factory UserMixin$Wallet.fromJson(Map<String, dynamic> json) =>
      _$UserMixin$WalletFromJson(json);

  @override
  List<Object?> get props => [id, bonus, deposit, winning];
  @override
  Map<String, dynamic> toJson() => _$UserMixin$WalletToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserMixin$UserFlags extends JsonSerializable with EquatableMixin {
  UserMixin$UserFlags();

  factory UserMixin$UserFlags.fromJson(Map<String, dynamic> json) =>
      _$UserMixin$UserFlagsFromJson(json);

  bool? allowSquadCreation;

  @override
  List<Object?> get props => [allowSquadCreation];
  @override
  Map<String, dynamic> toJson() => _$UserMixin$UserFlagsToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserTournamentMixin$Tournament extends JsonSerializable
    with EquatableMixin, TournamentMixin {
  UserTournamentMixin$Tournament();

  factory UserTournamentMixin$Tournament.fromJson(Map<String, dynamic> json) =>
      _$UserTournamentMixin$TournamentFromJson(json);

  @override
  List<Object?> get props => [
        eSport,
        id,
        name,
        maxPrize,
        userCount,
        gameCount,
        fee,
        matchType,
        joinBy,
        joinCode,
        qualifiers,
        winningDistribution,
        matches,
        rules,
        startTime,
        endTime
      ];
  @override
  Map<String, dynamic> toJson() => _$UserTournamentMixin$TournamentToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserTournamentMixin$Squad extends JsonSerializable
    with EquatableMixin, SquadMixin {
  UserTournamentMixin$Squad();

  factory UserTournamentMixin$Squad.fromJson(Map<String, dynamic> json) =>
      _$UserTournamentMixin$SquadFromJson(json);

  @override
  List<Object?> get props => [name, id, inviteCode, members];
  @override
  Map<String, dynamic> toJson() => _$UserTournamentMixin$SquadToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserTournamentMixin$TournamentMatchUser extends JsonSerializable
    with EquatableMixin, TournamentMatchUserMixin {
  UserTournamentMixin$TournamentMatchUser();

  factory UserTournamentMixin$TournamentMatchUser.fromJson(
          Map<String, dynamic> json) =>
      _$UserTournamentMixin$TournamentMatchUserFromJson(json);

  @override
  List<Object?> get props =>
      [slotInfo, id, tournamentMatchId, tournamentUserId, notJoined, sos];
  @override
  Map<String, dynamic> toJson() =>
      _$UserTournamentMixin$TournamentMatchUserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TournamentMixin$CustomQualificationRules extends JsonSerializable
    with EquatableMixin {
  TournamentMixin$CustomQualificationRules();

  factory TournamentMixin$CustomQualificationRules.fromJson(
          Map<String, dynamic> json) =>
      _$TournamentMixin$CustomQualificationRulesFromJson(json);

  @JsonKey(unknownEnumValue: CustomQualificationRuleTypes.artemisUnknown)
  late CustomQualificationRuleTypes rule;

  @JsonKey(fromJson: fromGraphQLJsonToDartMap, toJson: fromDartMapToGraphQLJson)
  late Map value;

  @override
  List<Object?> get props => [rule, value];
  @override
  Map<String, dynamic> toJson() =>
      _$TournamentMixin$CustomQualificationRulesToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TournamentMixin$WinningDistribution extends JsonSerializable
    with EquatableMixin {
  TournamentMixin$WinningDistribution();

  factory TournamentMixin$WinningDistribution.fromJson(
          Map<String, dynamic> json) =>
      _$TournamentMixin$WinningDistributionFromJson(json);

  late int startRank;

  late int endRank;

  late double value;

  @override
  List<Object?> get props => [startRank, endRank, value];
  @override
  Map<String, dynamic> toJson() =>
      _$TournamentMixin$WinningDistributionToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TournamentMixin$TournamentMatch extends JsonSerializable
    with EquatableMixin, TournamentMatchMixin {
  TournamentMixin$TournamentMatch();

  factory TournamentMixin$TournamentMatch.fromJson(Map<String, dynamic> json) =>
      _$TournamentMixin$TournamentMatchFromJson(json);

  @override
  List<Object?> get props => [
        tournamentId,
        endTime,
        startTime,
        id,
        maxParticipants,
        minParticipants,
        metadata
      ];
  @override
  Map<String, dynamic> toJson() =>
      _$TournamentMixin$TournamentMatchToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TournamentMixin$TournamentRules$BGMIRules
    extends TournamentMixin$TournamentRules with EquatableMixin {
  TournamentMixin$TournamentRules$BGMIRules();

  factory TournamentMixin$TournamentRules$BGMIRules.fromJson(
          Map<String, dynamic> json) =>
      _$TournamentMixin$TournamentRules$BGMIRulesFromJson(json);

  @JsonKey(unknownEnumValue: BgmiLevels.artemisUnknown)
  late BgmiLevels bgmiMaxLevel;

  @JsonKey(unknownEnumValue: BgmiLevels.artemisUnknown)
  late BgmiLevels bgmiMinLevel;

  @JsonKey(unknownEnumValue: BgmiModes.artemisUnknown)
  late List<BgmiModes> bgmiAllowedModes;

  @JsonKey(unknownEnumValue: BgmiMaps.artemisUnknown)
  late List<BgmiMaps> bgmiAllowedMaps;

  @JsonKey(unknownEnumValue: BgmiGroups.artemisUnknown)
  late BgmiGroups bgmiAllowedGroups;

  @override
  List<Object?> get props => [
        bgmiMaxLevel,
        bgmiMinLevel,
        bgmiAllowedModes,
        bgmiAllowedMaps,
        bgmiAllowedGroups
      ];
  @override
  Map<String, dynamic> toJson() =>
      _$TournamentMixin$TournamentRules$BGMIRulesToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TournamentMixin$TournamentRules$FFMaxRules
    extends TournamentMixin$TournamentRules with EquatableMixin {
  TournamentMixin$TournamentRules$FFMaxRules();

  factory TournamentMixin$TournamentRules$FFMaxRules.fromJson(
          Map<String, dynamic> json) =>
      _$TournamentMixin$TournamentRules$FFMaxRulesFromJson(json);

  @JsonKey(unknownEnumValue: FfMaxLevels.artemisUnknown)
  late FfMaxLevels ffMaxLevel;

  @JsonKey(unknownEnumValue: FfMaxLevels.artemisUnknown)
  late FfMaxLevels ffMinLevel;

  @JsonKey(unknownEnumValue: FfMaxModes.artemisUnknown)
  late List<FfMaxModes> ffAllowedModes;

  @JsonKey(unknownEnumValue: FfMaxMaps.artemisUnknown)
  late List<FfMaxMaps> ffAllowedMaps;

  @JsonKey(unknownEnumValue: FfMaxGroups.artemisUnknown)
  late FfMaxGroups ffAllowedGroups;

  @override
  List<Object?> get props =>
      [ffMaxLevel, ffMinLevel, ffAllowedModes, ffAllowedMaps, ffAllowedGroups];
  @override
  Map<String, dynamic> toJson() =>
      _$TournamentMixin$TournamentRules$FFMaxRulesToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TournamentMixin$TournamentRules extends JsonSerializable
    with EquatableMixin {
  TournamentMixin$TournamentRules();

  factory TournamentMixin$TournamentRules.fromJson(Map<String, dynamic> json) {
    switch (json['__typename'].toString()) {
      case r'BGMIRules':
        return TournamentMixin$TournamentRules$BGMIRules.fromJson(json);
      case r'FFMaxRules':
        return TournamentMixin$TournamentRules$FFMaxRules.fromJson(json);
      default:
    }
    return _$TournamentMixin$TournamentRulesFromJson(json);
  }

  @JsonKey(name: '__typename')
  String? $$typename;

  late int minUsers;

  late int maxUsers;

  late int maxTeams;

  @override
  List<Object?> get props => [$$typename, minUsers, maxUsers, maxTeams];
  @override
  Map<String, dynamic> toJson() {
    switch ($$typename) {
      case r'BGMIRules':
        return (this as TournamentMixin$TournamentRules$BGMIRules).toJson();
      case r'FFMaxRules':
        return (this as TournamentMixin$TournamentRules$FFMaxRules).toJson();
      default:
    }
    return _$TournamentMixin$TournamentRulesToJson(this);
  }
}

@JsonSerializable(explicitToJson: true)
class TournamentMatchMixin$TournamentMatchMetadata extends JsonSerializable
    with EquatableMixin, TournamentMatchMetadataMixin {
  TournamentMatchMixin$TournamentMatchMetadata();

  factory TournamentMatchMixin$TournamentMatchMetadata.fromJson(
          Map<String, dynamic> json) =>
      _$TournamentMatchMixin$TournamentMatchMetadataFromJson(json);

  @override
  List<Object?> get props => [roomId, roomPassword];
  @override
  Map<String, dynamic> toJson() =>
      _$TournamentMatchMixin$TournamentMatchMetadataToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SquadMixin$SquadMember extends JsonSerializable
    with EquatableMixin, SquadMemberMixin {
  SquadMixin$SquadMember();

  factory SquadMixin$SquadMember.fromJson(Map<String, dynamic> json) =>
      _$SquadMixin$SquadMemberFromJson(json);

  @override
  List<Object?> get props => [isReady, status, user];
  @override
  Map<String, dynamic> toJson() => _$SquadMixin$SquadMemberToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SquadMemberMixin$User extends JsonSerializable
    with EquatableMixin, LeaderboardUserMixin {
  SquadMemberMixin$User();

  factory SquadMemberMixin$User.fromJson(Map<String, dynamic> json) =>
      _$SquadMemberMixin$UserFromJson(json);

  @override
  List<Object?> get props => [id, name, phone, image, username];
  @override
  Map<String, dynamic> toJson() => _$SquadMemberMixin$UserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TournamentMatchUserMixin$SlotInfo extends JsonSerializable
    with EquatableMixin {
  TournamentMatchUserMixin$SlotInfo();

  factory TournamentMatchUserMixin$SlotInfo.fromJson(
          Map<String, dynamic> json) =>
      _$TournamentMatchUserMixin$SlotInfoFromJson(json);

  int? teamNumber;

  @override
  List<Object?> get props => [teamNumber];
  @override
  Map<String, dynamic> toJson() =>
      _$TournamentMatchUserMixin$SlotInfoToJson(this);
}

@JsonSerializable(explicitToJson: true)
class ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevel
    extends JsonSerializable with EquatableMixin {
  ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevel();

  factory ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevel.fromJson(
          Map<String, dynamic> json) =>
      _$ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevelFromJson(
          json);

  @JsonKey(unknownEnumValue: BgmiGroups.artemisUnknown)
  late BgmiGroups bgmiGroup;

  @JsonKey(unknownEnumValue: BgmiLevels.artemisUnknown)
  late BgmiLevels bgmiLevel;

  @override
  List<Object?> get props => [bgmiGroup, bgmiLevel];
  @override
  Map<String, dynamic> toJson() =>
      _$ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevelToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class ProfileMixin$ProfileMetadata$BgmiProfileMetadata
    extends ProfileMixin$ProfileMetadata with EquatableMixin {
  ProfileMixin$ProfileMetadata$BgmiProfileMetadata();

  factory ProfileMixin$ProfileMetadata$BgmiProfileMetadata.fromJson(
          Map<String, dynamic> json) =>
      _$ProfileMixin$ProfileMetadata$BgmiProfileMetadataFromJson(json);

  List<ProfileMixin$ProfileMetadata$BgmiProfileMetadata$BgmiProfileMetadataLevel>?
      levels;

  @override
  List<Object?> get props => [levels];
  @override
  Map<String, dynamic> toJson() =>
      _$ProfileMixin$ProfileMetadata$BgmiProfileMetadataToJson(this);
}

@JsonSerializable(explicitToJson: true)
class ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevel
    extends JsonSerializable with EquatableMixin {
  ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevel();

  factory ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevel.fromJson(
          Map<String, dynamic> json) =>
      _$ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevelFromJson(
          json);

  @JsonKey(unknownEnumValue: FfMaxGroups.artemisUnknown)
  late FfMaxGroups ffMaxGroup;

  @JsonKey(unknownEnumValue: FfMaxLevels.artemisUnknown)
  late FfMaxLevels ffMaxLevel;

  @override
  List<Object?> get props => [ffMaxGroup, ffMaxLevel];
  @override
  Map<String, dynamic> toJson() =>
      _$ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevelToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class ProfileMixin$ProfileMetadata$FFMaxProfileMetadata
    extends ProfileMixin$ProfileMetadata with EquatableMixin {
  ProfileMixin$ProfileMetadata$FFMaxProfileMetadata();

  factory ProfileMixin$ProfileMetadata$FFMaxProfileMetadata.fromJson(
          Map<String, dynamic> json) =>
      _$ProfileMixin$ProfileMetadata$FFMaxProfileMetadataFromJson(json);

  List<ProfileMixin$ProfileMetadata$FFMaxProfileMetadata$FFMaxProfileMetadataLevel>?
      levels;

  @override
  List<Object?> get props => [levels];
  @override
  Map<String, dynamic> toJson() =>
      _$ProfileMixin$ProfileMetadata$FFMaxProfileMetadataToJson(this);
}

@JsonSerializable(explicitToJson: true)
class ProfileMixin$ProfileMetadata extends JsonSerializable
    with EquatableMixin {
  ProfileMixin$ProfileMetadata();

  factory ProfileMixin$ProfileMetadata.fromJson(Map<String, dynamic> json) {
    switch (json['__typename'].toString()) {
      case r'BgmiProfileMetadata':
        return ProfileMixin$ProfileMetadata$BgmiProfileMetadata.fromJson(json);
      case r'FFMaxProfileMetadata':
        return ProfileMixin$ProfileMetadata$FFMaxProfileMetadata.fromJson(json);
      default:
    }
    return _$ProfileMixin$ProfileMetadataFromJson(json);
  }

  @JsonKey(name: '__typename')
  String? $$typename;

  @override
  List<Object?> get props => [$$typename];
  @override
  Map<String, dynamic> toJson() {
    switch ($$typename) {
      case r'BgmiProfileMetadata':
        return (this as ProfileMixin$ProfileMetadata$BgmiProfileMetadata)
            .toJson();
      case r'FFMaxProfileMetadata':
        return (this as ProfileMixin$ProfileMetadata$FFMaxProfileMetadata)
            .toJson();
      default:
    }
    return _$ProfileMixin$ProfileMetadataToJson(this);
  }
}

@JsonSerializable(explicitToJson: true)
class GetUserListByPreference$Query$User extends JsonSerializable
    with EquatableMixin, UserMixin {
  GetUserListByPreference$Query$User();

  factory GetUserListByPreference$Query$User.fromJson(
          Map<String, dynamic> json) =>
      _$GetUserListByPreference$Query$UserFromJson(json);

  @override
  List<Object?> get props => [
        id,
        birthdate,
        name,
        phone,
        image,
        achievements,
        inviteCode,
        preferences,
        profiles,
        wallet,
        flags,
        username
      ];
  @override
  Map<String, dynamic> toJson() =>
      _$GetUserListByPreference$Query$UserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetUserListByPreference$Query extends JsonSerializable
    with EquatableMixin {
  GetUserListByPreference$Query();

  factory GetUserListByPreference$Query.fromJson(Map<String, dynamic> json) =>
      _$GetUserListByPreference$QueryFromJson(json);

  late List<GetUserListByPreference$Query$User?> searchByPreferences;

  @override
  List<Object?> get props => [searchByPreferences];
  @override
  Map<String, dynamic> toJson() => _$GetUserListByPreference$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class PreferencesInput extends JsonSerializable with EquatableMixin {
  PreferencesInput({
    this.location,
    this.playingReason,
    this.roles,
    this.timeOfDay,
    this.timeOfWeek,
  });

  factory PreferencesInput.fromJson(Map<String, dynamic> json) =>
      _$PreferencesInputFromJson(json);

  LocationInput? location;

  @JsonKey(unknownEnumValue: PlayingReasonPreference.artemisUnknown)
  List<PlayingReasonPreference>? playingReason;

  @JsonKey(unknownEnumValue: RolePreference.artemisUnknown)
  List<RolePreference>? roles;

  @JsonKey(unknownEnumValue: TimeOfDayPreference.artemisUnknown)
  List<TimeOfDayPreference>? timeOfDay;

  @JsonKey(unknownEnumValue: TimeOfWeekPreference.artemisUnknown)
  TimeOfWeekPreference? timeOfWeek;

  @override
  List<Object?> get props =>
      [location, playingReason, roles, timeOfDay, timeOfWeek];
  @override
  Map<String, dynamic> toJson() => _$PreferencesInputToJson(this);
}

@JsonSerializable(explicitToJson: true)
class LocationInput extends JsonSerializable with EquatableMixin {
  LocationInput({
    this.city,
    this.country,
    this.lat,
    this.lng,
    this.region,
  });

  factory LocationInput.fromJson(Map<String, dynamic> json) =>
      _$LocationInputFromJson(json);

  String? city;

  String? country;

  double? lat;

  double? lng;

  String? region;

  @override
  List<Object?> get props => [city, country, lat, lng, region];
  @override
  Map<String, dynamic> toJson() => _$LocationInputToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetMyActiveTournament$Query$ActiveTournamentList$UserTournament
    extends JsonSerializable with EquatableMixin, UserTournamentMixin {
  GetMyActiveTournament$Query$ActiveTournamentList$UserTournament();

  factory GetMyActiveTournament$Query$ActiveTournamentList$UserTournament.fromJson(
          Map<String, dynamic> json) =>
      _$GetMyActiveTournament$Query$ActiveTournamentList$UserTournamentFromJson(
          json);

  @override
  List<Object?> get props =>
      [joinedAt, rank, score, tournament, squad, tournamentMatchUser];
  @override
  Map<String, dynamic> toJson() =>
      _$GetMyActiveTournament$Query$ActiveTournamentList$UserTournamentToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class GetMyActiveTournament$Query$ActiveTournamentList extends JsonSerializable
    with EquatableMixin {
  GetMyActiveTournament$Query$ActiveTournamentList();

  factory GetMyActiveTournament$Query$ActiveTournamentList.fromJson(
          Map<String, dynamic> json) =>
      _$GetMyActiveTournament$Query$ActiveTournamentListFromJson(json);

  late List<GetMyActiveTournament$Query$ActiveTournamentList$UserTournament>
      tournaments;

  late String type;

  @override
  List<Object?> get props => [tournaments, type];
  @override
  Map<String, dynamic> toJson() =>
      _$GetMyActiveTournament$Query$ActiveTournamentListToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetMyActiveTournament$Query extends JsonSerializable with EquatableMixin {
  GetMyActiveTournament$Query();

  factory GetMyActiveTournament$Query.fromJson(Map<String, dynamic> json) =>
      _$GetMyActiveTournament$QueryFromJson(json);

  late List<GetMyActiveTournament$Query$ActiveTournamentList> active;

  @override
  List<Object?> get props => [active];
  @override
  Map<String, dynamic> toJson() => _$GetMyActiveTournament$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualification
    extends JsonSerializable with EquatableMixin {
  CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualification();

  factory CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualification.fromJson(
          Map<String, dynamic> json) =>
      _$CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualificationFromJson(
          json);

  late int current;

  late bool qualified;

  late int required;

  late String rule;

  @override
  List<Object?> get props => [current, qualified, required, rule];
  @override
  Map<String, dynamic> toJson() =>
      _$CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualificationToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class CheckUserTournamentQualification$Query$TournamentQualificationResult
    extends JsonSerializable with EquatableMixin {
  CheckUserTournamentQualification$Query$TournamentQualificationResult();

  factory CheckUserTournamentQualification$Query$TournamentQualificationResult.fromJson(
          Map<String, dynamic> json) =>
      _$CheckUserTournamentQualification$Query$TournamentQualificationResultFromJson(
          json);

  late bool qualified;

  late List<
          CheckUserTournamentQualification$Query$TournamentQualificationResult$TournamentEntryRuleQualification>
      rules;

  @override
  List<Object?> get props => [qualified, rules];
  @override
  Map<String, dynamic> toJson() =>
      _$CheckUserTournamentQualification$Query$TournamentQualificationResultToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class CheckUserTournamentQualification$Query extends JsonSerializable
    with EquatableMixin {
  CheckUserTournamentQualification$Query();

  factory CheckUserTournamentQualification$Query.fromJson(
          Map<String, dynamic> json) =>
      _$CheckUserTournamentQualification$QueryFromJson(json);

  late CheckUserTournamentQualification$Query$TournamentQualificationResult
      getTournamentQualification;

  @override
  List<Object?> get props => [getTournamentQualification];
  @override
  Map<String, dynamic> toJson() =>
      _$CheckUserTournamentQualification$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetTournamentMatches$Query$TournamentMatch extends JsonSerializable
    with EquatableMixin, TournamentMatchMixin {
  GetTournamentMatches$Query$TournamentMatch();

  factory GetTournamentMatches$Query$TournamentMatch.fromJson(
          Map<String, dynamic> json) =>
      _$GetTournamentMatches$Query$TournamentMatchFromJson(json);

  @override
  List<Object?> get props => [
        tournamentId,
        endTime,
        startTime,
        id,
        maxParticipants,
        minParticipants,
        metadata
      ];
  @override
  Map<String, dynamic> toJson() =>
      _$GetTournamentMatches$Query$TournamentMatchToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetTournamentMatches$Query extends JsonSerializable with EquatableMixin {
  GetTournamentMatches$Query();

  factory GetTournamentMatches$Query.fromJson(Map<String, dynamic> json) =>
      _$GetTournamentMatches$QueryFromJson(json);

  late List<GetTournamentMatches$Query$TournamentMatch> getTournamentMatches;

  @override
  List<Object?> get props => [getTournamentMatches];
  @override
  Map<String, dynamic> toJson() => _$GetTournamentMatches$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetTopTournaments$Query$UserTournament extends JsonSerializable
    with EquatableMixin, UserTournamentMixin {
  GetTopTournaments$Query$UserTournament();

  factory GetTopTournaments$Query$UserTournament.fromJson(
          Map<String, dynamic> json) =>
      _$GetTopTournaments$Query$UserTournamentFromJson(json);

  @override
  List<Object?> get props =>
      [joinedAt, rank, score, tournament, squad, tournamentMatchUser];
  @override
  Map<String, dynamic> toJson() =>
      _$GetTopTournaments$Query$UserTournamentToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetTopTournaments$Query extends JsonSerializable with EquatableMixin {
  GetTopTournaments$Query();

  factory GetTopTournaments$Query.fromJson(Map<String, dynamic> json) =>
      _$GetTopTournaments$QueryFromJson(json);

  late List<GetTopTournaments$Query$UserTournament> top;

  @override
  List<Object?> get props => [top];
  @override
  Map<String, dynamic> toJson() => _$GetTopTournaments$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetTournamentsHistory$Query$UserTournament extends JsonSerializable
    with EquatableMixin, UserTournamentMixin {
  GetTournamentsHistory$Query$UserTournament();

  factory GetTournamentsHistory$Query$UserTournament.fromJson(
          Map<String, dynamic> json) =>
      _$GetTournamentsHistory$Query$UserTournamentFromJson(json);

  @override
  List<Object?> get props =>
      [joinedAt, rank, score, tournament, squad, tournamentMatchUser];
  @override
  Map<String, dynamic> toJson() =>
      _$GetTournamentsHistory$Query$UserTournamentToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetTournamentsHistory$Query extends JsonSerializable with EquatableMixin {
  GetTournamentsHistory$Query();

  factory GetTournamentsHistory$Query.fromJson(Map<String, dynamic> json) =>
      _$GetTournamentsHistory$QueryFromJson(json);

  late List<GetTournamentsHistory$Query$UserTournament> history;

  @override
  List<Object?> get props => [history];
  @override
  Map<String, dynamic> toJson() => _$GetTournamentsHistory$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetLeaderboard$Query$Leaderboard extends JsonSerializable
    with EquatableMixin, LeaderboardRankMixin {
  GetLeaderboard$Query$Leaderboard();

  factory GetLeaderboard$Query$Leaderboard.fromJson(
          Map<String, dynamic> json) =>
      _$GetLeaderboard$Query$LeaderboardFromJson(json);

  @override
  List<Object?> get props =>
      [id, rank, score, behindBy, isDisqualified, details, user];
  @override
  Map<String, dynamic> toJson() =>
      _$GetLeaderboard$Query$LeaderboardToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetLeaderboard$Query extends JsonSerializable with EquatableMixin {
  GetLeaderboard$Query();

  factory GetLeaderboard$Query.fromJson(Map<String, dynamic> json) =>
      _$GetLeaderboard$QueryFromJson(json);

  late List<GetLeaderboard$Query$Leaderboard> leaderboard;

  @override
  List<Object?> get props => [leaderboard];
  @override
  Map<String, dynamic> toJson() => _$GetLeaderboard$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadata
    extends LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata
    with EquatableMixin {
  LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadata();

  factory LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadata.fromJson(
          Map<String, dynamic> json) =>
      _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadataFromJson(
          json);

  late int kills;

  @override
  List<Object?> get props => [kills];
  @override
  Map<String, dynamic> toJson() =>
      _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadataToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadata
    extends LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata
    with EquatableMixin {
  LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadata();

  factory LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadata.fromJson(
          Map<String, dynamic> json) =>
      _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadataFromJson(
          json);

  late int kills;

  @override
  List<Object?> get props => [kills];
  @override
  Map<String, dynamic> toJson() =>
      _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadataToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata
    extends JsonSerializable with EquatableMixin {
  LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata();

  factory LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata.fromJson(
      Map<String, dynamic> json) {
    switch (json['__typename'].toString()) {
      case r'BgmiMetadata':
        return LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadata
            .fromJson(json);
      case r'FfMaxMetadata':
        return LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadata
            .fromJson(json);
      default:
    }
    return _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadataFromJson(
        json);
  }

  @JsonKey(name: '__typename')
  String? $$typename;

  @override
  List<Object?> get props => [$$typename];
  @override
  Map<String, dynamic> toJson() {
    switch ($$typename) {
      case r'BgmiMetadata':
        return (this
                as LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$BgmiMetadata)
            .toJson();
      case r'FfMaxMetadata':
        return (this
                as LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata$FfMaxMetadata)
            .toJson();
      default:
    }
    return _$LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadataToJson(this);
  }
}

@JsonSerializable(explicitToJson: true)
class LeaderboardRankMixin$LeaderboardInfo$Game extends JsonSerializable
    with EquatableMixin {
  LeaderboardRankMixin$LeaderboardInfo$Game();

  factory LeaderboardRankMixin$LeaderboardInfo$Game.fromJson(
          Map<String, dynamic> json) =>
      _$LeaderboardRankMixin$LeaderboardInfo$GameFromJson(json);

  late int rank;

  late double score;

  late LeaderboardRankMixin$LeaderboardInfo$Game$GameMetadata metadata;

  @override
  List<Object?> get props => [rank, score, metadata];
  @override
  Map<String, dynamic> toJson() =>
      _$LeaderboardRankMixin$LeaderboardInfo$GameToJson(this);
}

@JsonSerializable(explicitToJson: true)
class LeaderboardRankMixin$LeaderboardInfo extends JsonSerializable
    with EquatableMixin {
  LeaderboardRankMixin$LeaderboardInfo();

  factory LeaderboardRankMixin$LeaderboardInfo.fromJson(
          Map<String, dynamic> json) =>
      _$LeaderboardRankMixin$LeaderboardInfoFromJson(json);

  late int gamesPlayed;

  late List<LeaderboardRankMixin$LeaderboardInfo$Game?> top;

  @override
  List<Object?> get props => [gamesPlayed, top];
  @override
  Map<String, dynamic> toJson() =>
      _$LeaderboardRankMixin$LeaderboardInfoToJson(this);
}

@JsonSerializable(explicitToJson: true)
class LeaderboardRankMixin$User extends JsonSerializable
    with EquatableMixin, LeaderboardUserMixin {
  LeaderboardRankMixin$User();

  factory LeaderboardRankMixin$User.fromJson(Map<String, dynamic> json) =>
      _$LeaderboardRankMixin$UserFromJson(json);

  @override
  List<Object?> get props => [id, name, phone, image, username];
  @override
  Map<String, dynamic> toJson() => _$LeaderboardRankMixin$UserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetSquadLeaderboard$Query$SquadLeaderboard extends JsonSerializable
    with EquatableMixin, SquadLeaderboardMixin {
  GetSquadLeaderboard$Query$SquadLeaderboard();

  factory GetSquadLeaderboard$Query$SquadLeaderboard.fromJson(
          Map<String, dynamic> json) =>
      _$GetSquadLeaderboard$Query$SquadLeaderboardFromJson(json);

  @override
  List<Object?> get props => [id, squad, behindBy, rank, score, details];
  @override
  Map<String, dynamic> toJson() =>
      _$GetSquadLeaderboard$Query$SquadLeaderboardToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetSquadLeaderboard$Query extends JsonSerializable with EquatableMixin {
  GetSquadLeaderboard$Query();

  factory GetSquadLeaderboard$Query.fromJson(Map<String, dynamic> json) =>
      _$GetSquadLeaderboard$QueryFromJson(json);

  late List<GetSquadLeaderboard$Query$SquadLeaderboard> squadLeaderboard;

  @override
  List<Object?> get props => [squadLeaderboard];
  @override
  Map<String, dynamic> toJson() => _$GetSquadLeaderboard$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SquadLeaderboardMixin$Squad extends JsonSerializable
    with EquatableMixin, LeaderboardSquadMixin {
  SquadLeaderboardMixin$Squad();

  factory SquadLeaderboardMixin$Squad.fromJson(Map<String, dynamic> json) =>
      _$SquadLeaderboardMixin$SquadFromJson(json);

  @override
  List<Object?> get props => [isDisqualified, name, id];
  @override
  Map<String, dynamic> toJson() => _$SquadLeaderboardMixin$SquadToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SquadLeaderboardMixin$SquadLeaderboardInfo$Game extends JsonSerializable
    with EquatableMixin {
  SquadLeaderboardMixin$SquadLeaderboardInfo$Game();

  factory SquadLeaderboardMixin$SquadLeaderboardInfo$Game.fromJson(
          Map<String, dynamic> json) =>
      _$SquadLeaderboardMixin$SquadLeaderboardInfo$GameFromJson(json);

  late double score;

  int? teamRank;

  @override
  List<Object?> get props => [score, teamRank];
  @override
  Map<String, dynamic> toJson() =>
      _$SquadLeaderboardMixin$SquadLeaderboardInfo$GameToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SquadLeaderboardMixin$SquadLeaderboardInfo extends JsonSerializable
    with EquatableMixin {
  SquadLeaderboardMixin$SquadLeaderboardInfo();

  factory SquadLeaderboardMixin$SquadLeaderboardInfo.fromJson(
          Map<String, dynamic> json) =>
      _$SquadLeaderboardMixin$SquadLeaderboardInfoFromJson(json);

  late int gamesPlayed;

  late List<List<SquadLeaderboardMixin$SquadLeaderboardInfo$Game?>?> top;

  @override
  List<Object?> get props => [gamesPlayed, top];
  @override
  Map<String, dynamic> toJson() =>
      _$SquadLeaderboardMixin$SquadLeaderboardInfoToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetTournament$Query$UserTournament extends JsonSerializable
    with EquatableMixin, UserTournamentMixin {
  GetTournament$Query$UserTournament();

  factory GetTournament$Query$UserTournament.fromJson(
          Map<String, dynamic> json) =>
      _$GetTournament$Query$UserTournamentFromJson(json);

  @override
  List<Object?> get props =>
      [joinedAt, rank, score, tournament, squad, tournamentMatchUser];
  @override
  Map<String, dynamic> toJson() =>
      _$GetTournament$Query$UserTournamentToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetTournament$Query extends JsonSerializable with EquatableMixin {
  GetTournament$Query();

  factory GetTournament$Query.fromJson(Map<String, dynamic> json) =>
      _$GetTournament$QueryFromJson(json);

  GetTournament$Query$UserTournament? tournament;

  @override
  List<Object?> get props => [tournament];
  @override
  Map<String, dynamic> toJson() => _$GetTournament$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetGameScoring$Query$Scoring$RankPoint extends JsonSerializable
    with EquatableMixin {
  GetGameScoring$Query$Scoring$RankPoint();

  factory GetGameScoring$Query$Scoring$RankPoint.fromJson(
          Map<String, dynamic> json) =>
      _$GetGameScoring$Query$Scoring$RankPointFromJson(json);

  late int points;

  late int rank;

  @override
  List<Object?> get props => [points, rank];
  @override
  Map<String, dynamic> toJson() =>
      _$GetGameScoring$Query$Scoring$RankPointToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetGameScoring$Query$Scoring extends JsonSerializable
    with EquatableMixin {
  GetGameScoring$Query$Scoring();

  factory GetGameScoring$Query$Scoring.fromJson(Map<String, dynamic> json) =>
      _$GetGameScoring$Query$ScoringFromJson(json);

  late int killPoints;

  late List<GetGameScoring$Query$Scoring$RankPoint> rankPoints;

  @override
  List<Object?> get props => [killPoints, rankPoints];
  @override
  Map<String, dynamic> toJson() => _$GetGameScoring$Query$ScoringToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetGameScoring$Query extends JsonSerializable with EquatableMixin {
  GetGameScoring$Query();

  factory GetGameScoring$Query.fromJson(Map<String, dynamic> json) =>
      _$GetGameScoring$QueryFromJson(json);

  late GetGameScoring$Query$Scoring scoring;

  @override
  List<Object?> get props => [scoring];
  @override
  Map<String, dynamic> toJson() => _$GetGameScoring$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetTransactions$Query$LedgerTransaction extends JsonSerializable
    with EquatableMixin {
  GetTransactions$Query$LedgerTransaction();

  factory GetTransactions$Query$LedgerTransaction.fromJson(
          Map<String, dynamic> json) =>
      _$GetTransactions$Query$LedgerTransactionFromJson(json);

  late double amount;

  @JsonKey(unknownEnumValue: SubWalletType.artemisUnknown)
  late SubWalletType subWallet;

  @JsonKey(
      fromJson: fromGraphQLDateToDartDateTime,
      toJson: fromDartDateTimeToGraphQLDate)
  late DateTime createdAt;

  @JsonKey(unknownEnumValue: TransactionStatus.artemisUnknown)
  late TransactionStatus status;

  late String description;

  late String transactionId;

  late int id;

  @override
  List<Object?> get props =>
      [amount, subWallet, createdAt, status, description, transactionId, id];
  @override
  Map<String, dynamic> toJson() =>
      _$GetTransactions$Query$LedgerTransactionToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GetTransactions$Query extends JsonSerializable with EquatableMixin {
  GetTransactions$Query();

  factory GetTransactions$Query.fromJson(Map<String, dynamic> json) =>
      _$GetTransactions$QueryFromJson(json);

  late List<GetTransactions$Query$LedgerTransaction> transactions;

  @override
  List<Object?> get props => [transactions];
  @override
  Map<String, dynamic> toJson() => _$GetTransactions$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class Transaction$Query$LedgerTransaction extends JsonSerializable
    with EquatableMixin {
  Transaction$Query$LedgerTransaction();

  factory Transaction$Query$LedgerTransaction.fromJson(
          Map<String, dynamic> json) =>
      _$Transaction$Query$LedgerTransactionFromJson(json);

  late int id;

  late String transactionId;

  @JsonKey(unknownEnumValue: TransactionStatus.artemisUnknown)
  late TransactionStatus status;

  late double amount;

  late String description;

  @override
  List<Object?> get props => [id, transactionId, status, amount, description];
  @override
  Map<String, dynamic> toJson() =>
      _$Transaction$Query$LedgerTransactionToJson(this);
}

@JsonSerializable(explicitToJson: true)
class Transaction$Query extends JsonSerializable with EquatableMixin {
  Transaction$Query();

  factory Transaction$Query.fromJson(Map<String, dynamic> json) =>
      _$Transaction$QueryFromJson(json);

  Transaction$Query$LedgerTransaction? transaction;

  @override
  List<Object?> get props => [transaction];
  @override
  Map<String, dynamic> toJson() => _$Transaction$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SearchUser$Query$User extends JsonSerializable
    with EquatableMixin, UserSummaryMixin {
  SearchUser$Query$User();

  factory SearchUser$Query$User.fromJson(Map<String, dynamic> json) =>
      _$SearchUser$Query$UserFromJson(json);

  @override
  List<Object?> get props => [id, name, image, profiles, username];
  @override
  Map<String, dynamic> toJson() => _$SearchUser$Query$UserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SearchUser$Query extends JsonSerializable with EquatableMixin {
  SearchUser$Query();

  factory SearchUser$Query.fromJson(Map<String, dynamic> json) =>
      _$SearchUser$QueryFromJson(json);

  late List<SearchUser$Query$User?> searchUserESports;

  @override
  List<Object?> get props => [searchUserESports];
  @override
  Map<String, dynamic> toJson() => _$SearchUser$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UserSummaryMixin$Profile extends JsonSerializable
    with EquatableMixin, ProfileMixin {
  UserSummaryMixin$Profile();

  factory UserSummaryMixin$Profile.fromJson(Map<String, dynamic> json) =>
      _$UserSummaryMixin$ProfileFromJson(json);

  @override
  List<Object?> get props => [eSport, metadata, username, profileId];
  @override
  Map<String, dynamic> toJson() => _$UserSummaryMixin$ProfileToJson(this);
}

@JsonSerializable(explicitToJson: true)
class RecentPlayers$Query$User extends JsonSerializable
    with EquatableMixin, UserSummaryMixin {
  RecentPlayers$Query$User();

  factory RecentPlayers$Query$User.fromJson(Map<String, dynamic> json) =>
      _$RecentPlayers$Query$UserFromJson(json);

  @override
  List<Object?> get props => [id, name, image, profiles, username];
  @override
  Map<String, dynamic> toJson() => _$RecentPlayers$Query$UserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class RecentPlayers$Query extends JsonSerializable with EquatableMixin {
  RecentPlayers$Query();

  factory RecentPlayers$Query.fromJson(Map<String, dynamic> json) =>
      _$RecentPlayers$QueryFromJson(json);

  late List<RecentPlayers$Query$User> inviteList;

  @override
  List<Object?> get props => [inviteList];
  @override
  Map<String, dynamic> toJson() => _$RecentPlayers$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class LowRating$Query extends JsonSerializable with EquatableMixin {
  LowRating$Query();

  factory LowRating$Query.fromJson(Map<String, dynamic> json) =>
      _$LowRating$QueryFromJson(json);

  late List<String> lowRatingReasons;

  @override
  List<Object?> get props => [lowRatingReasons];
  @override
  Map<String, dynamic> toJson() => _$LowRating$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TopWinners$Query$TopWinners$User extends JsonSerializable
    with EquatableMixin {
  TopWinners$Query$TopWinners$User();

  factory TopWinners$Query$TopWinners$User.fromJson(
          Map<String, dynamic> json) =>
      _$TopWinners$Query$TopWinners$UserFromJson(json);

  late String username;

  @override
  List<Object?> get props => [username];
  @override
  Map<String, dynamic> toJson() =>
      _$TopWinners$Query$TopWinners$UserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TopWinners$Query$TopWinners extends JsonSerializable with EquatableMixin {
  TopWinners$Query$TopWinners();

  factory TopWinners$Query$TopWinners.fromJson(Map<String, dynamic> json) =>
      _$TopWinners$Query$TopWinnersFromJson(json);

  late double amount;

  late TopWinners$Query$TopWinners$User user;

  @override
  List<Object?> get props => [amount, user];
  @override
  Map<String, dynamic> toJson() => _$TopWinners$Query$TopWinnersToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TopWinners$Query extends JsonSerializable with EquatableMixin {
  TopWinners$Query();

  factory TopWinners$Query.fromJson(Map<String, dynamic> json) =>
      _$TopWinners$QueryFromJson(json);

  late List<TopWinners$Query$TopWinners> topWinners;

  @override
  List<Object?> get props => [topWinners];
  @override
  Map<String, dynamic> toJson() => _$TopWinners$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class ModelParam$Query$ModelParam$Label extends JsonSerializable
    with EquatableMixin, LabelFieldsMixin {
  ModelParam$Query$ModelParam$Label();

  factory ModelParam$Query$ModelParam$Label.fromJson(
          Map<String, dynamic> json) =>
      _$ModelParam$Query$ModelParam$LabelFromJson(json);

  @override
  List<Object?> get props => [
        index,
        name,
        threshold,
        individualOCR,
        sortOrder,
        mandatory,
        shouldPerformScaleAndStitching
      ];
  @override
  Map<String, dynamic> toJson() =>
      _$ModelParam$Query$ModelParam$LabelToJson(this);
}

@JsonSerializable(explicitToJson: true)
class ModelParam$Query$ModelParam$BucketData$Bucket extends JsonSerializable
    with EquatableMixin, BucketFieldsMixin {
  ModelParam$Query$ModelParam$BucketData$Bucket();

  factory ModelParam$Query$ModelParam$BucketData$Bucket.fromJson(
          Map<String, dynamic> json) =>
      _$ModelParam$Query$ModelParam$BucketData$BucketFromJson(json);

  @override
  List<Object?> get props => [bufferSize, labels];
  @override
  Map<String, dynamic> toJson() =>
      _$ModelParam$Query$ModelParam$BucketData$BucketToJson(this);
}

@JsonSerializable(explicitToJson: true)
class ModelParam$Query$ModelParam$BucketData extends JsonSerializable
    with EquatableMixin {
  ModelParam$Query$ModelParam$BucketData();

  factory ModelParam$Query$ModelParam$BucketData.fromJson(
          Map<String, dynamic> json) =>
      _$ModelParam$Query$ModelParam$BucketDataFromJson(json);

  late ModelParam$Query$ModelParam$BucketData$Bucket resultRankRating;

  late ModelParam$Query$ModelParam$BucketData$Bucket resultRankKills;

  late ModelParam$Query$ModelParam$BucketData$Bucket resultRank;

  late ModelParam$Query$ModelParam$BucketData$Bucket homeScreenBucket;

  late ModelParam$Query$ModelParam$BucketData$Bucket waitingScreenBucket;

  late ModelParam$Query$ModelParam$BucketData$Bucket gameScreenBucket;

  late ModelParam$Query$ModelParam$BucketData$Bucket loginScreenBucket;

  late ModelParam$Query$ModelParam$BucketData$Bucket myProfileScreen;

  late ModelParam$Query$ModelParam$BucketData$Bucket playAgain;

  @override
  List<Object?> get props => [
        resultRankRating,
        resultRankKills,
        resultRank,
        homeScreenBucket,
        waitingScreenBucket,
        gameScreenBucket,
        loginScreenBucket,
        myProfileScreen,
        playAgain
      ];
  @override
  Map<String, dynamic> toJson() =>
      _$ModelParam$Query$ModelParam$BucketDataToJson(this);
}

@JsonSerializable(explicitToJson: true)
class ModelParam$Query$ModelParam extends JsonSerializable with EquatableMixin {
  ModelParam$Query$ModelParam();

  factory ModelParam$Query$ModelParam.fromJson(Map<String, dynamic> json) =>
      _$ModelParam$Query$ModelParamFromJson(json);

  @JsonKey(name: 'model_name')
  late String modelName;

  @JsonKey(name: 'model_url')
  late String modelUrl;

  late List<ModelParam$Query$ModelParam$Label> labels;

  ModelParam$Query$ModelParam$BucketData? bucket;

  @override
  List<Object?> get props => [modelName, modelUrl, labels, bucket];
  @override
  Map<String, dynamic> toJson() => _$ModelParam$Query$ModelParamToJson(this);
}

@JsonSerializable(explicitToJson: true)
class ModelParam$Query extends JsonSerializable with EquatableMixin {
  ModelParam$Query();

  factory ModelParam$Query.fromJson(Map<String, dynamic> json) =>
      _$ModelParam$QueryFromJson(json);

  ModelParam$Query$ModelParam? modelParam;

  @override
  List<Object?> get props => [modelParam];
  @override
  Map<String, dynamic> toJson() => _$ModelParam$QueryToJson(this);
}

@JsonSerializable(explicitToJson: true)
class BucketFieldsMixin$Label extends JsonSerializable
    with EquatableMixin, LabelFieldsMixin {
  BucketFieldsMixin$Label();

  factory BucketFieldsMixin$Label.fromJson(Map<String, dynamic> json) =>
      _$BucketFieldsMixin$LabelFromJson(json);

  @override
  List<Object?> get props => [
        index,
        name,
        threshold,
        individualOCR,
        sortOrder,
        mandatory,
        shouldPerformScaleAndStitching
      ];
  @override
  Map<String, dynamic> toJson() => _$BucketFieldsMixin$LabelToJson(this);
}

@JsonSerializable(explicitToJson: true)
class RequestOtp$Mutation$DefaultPayload extends JsonSerializable
    with EquatableMixin {
  RequestOtp$Mutation$DefaultPayload();

  factory RequestOtp$Mutation$DefaultPayload.fromJson(
          Map<String, dynamic> json) =>
      _$RequestOtp$Mutation$DefaultPayloadFromJson(json);

  late String message;

  @override
  List<Object?> get props => [message];
  @override
  Map<String, dynamic> toJson() =>
      _$RequestOtp$Mutation$DefaultPayloadToJson(this);
}

@JsonSerializable(explicitToJson: true)
class RequestOtp$Mutation extends JsonSerializable with EquatableMixin {
  RequestOtp$Mutation();

  factory RequestOtp$Mutation.fromJson(Map<String, dynamic> json) =>
      _$RequestOtp$MutationFromJson(json);

  late RequestOtp$Mutation$DefaultPayload generateOTP;

  @override
  List<Object?> get props => [generateOTP];
  @override
  Map<String, dynamic> toJson() => _$RequestOtp$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class AddUser$Mutation$LoggedInUser$User extends JsonSerializable
    with EquatableMixin {
  AddUser$Mutation$LoggedInUser$User();

  factory AddUser$Mutation$LoggedInUser$User.fromJson(
          Map<String, dynamic> json) =>
      _$AddUser$Mutation$LoggedInUser$UserFromJson(json);

  late int id;

  @override
  List<Object?> get props => [id];
  @override
  Map<String, dynamic> toJson() =>
      _$AddUser$Mutation$LoggedInUser$UserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class AddUser$Mutation$LoggedInUser extends JsonSerializable
    with EquatableMixin {
  AddUser$Mutation$LoggedInUser();

  factory AddUser$Mutation$LoggedInUser.fromJson(Map<String, dynamic> json) =>
      _$AddUser$Mutation$LoggedInUserFromJson(json);

  late String token;

  AddUser$Mutation$LoggedInUser$User? user;

  @override
  List<Object?> get props => [token, user];
  @override
  Map<String, dynamic> toJson() => _$AddUser$Mutation$LoggedInUserToJson(this);
}

@JsonSerializable(explicitToJson: true)
class AddUser$Mutation extends JsonSerializable with EquatableMixin {
  AddUser$Mutation();

  factory AddUser$Mutation.fromJson(Map<String, dynamic> json) =>
      _$AddUser$MutationFromJson(json);

  late AddUser$Mutation$LoggedInUser addUser;

  @override
  List<Object?> get props => [addUser];
  @override
  Map<String, dynamic> toJson() => _$AddUser$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class CreateGameProfile$Mutation$Profile extends JsonSerializable
    with EquatableMixin, ProfileMixin {
  CreateGameProfile$Mutation$Profile();

  factory CreateGameProfile$Mutation$Profile.fromJson(
          Map<String, dynamic> json) =>
      _$CreateGameProfile$Mutation$ProfileFromJson(json);

  @override
  List<Object?> get props => [eSport, metadata, username, profileId];
  @override
  Map<String, dynamic> toJson() =>
      _$CreateGameProfile$Mutation$ProfileToJson(this);
}

@JsonSerializable(explicitToJson: true)
class CreateGameProfile$Mutation extends JsonSerializable with EquatableMixin {
  CreateGameProfile$Mutation();

  factory CreateGameProfile$Mutation.fromJson(Map<String, dynamic> json) =>
      _$CreateGameProfile$MutationFromJson(json);

  late CreateGameProfile$Mutation$Profile createProfile;

  @override
  List<Object?> get props => [createProfile];
  @override
  Map<String, dynamic> toJson() => _$CreateGameProfile$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class FFMaxProfileInput extends JsonSerializable with EquatableMixin {
  FFMaxProfileInput({
    this.metadata,
    this.profileId,
    this.username,
  });

  factory FFMaxProfileInput.fromJson(Map<String, dynamic> json) =>
      _$FFMaxProfileInputFromJson(json);

  FFMaxProfileMetadataInput? metadata;

  String? profileId;

  String? username;

  @override
  List<Object?> get props => [metadata, profileId, username];
  @override
  Map<String, dynamic> toJson() => _$FFMaxProfileInputToJson(this);
}

@JsonSerializable(explicitToJson: true)
class FFMaxProfileMetadataInput extends JsonSerializable with EquatableMixin {
  FFMaxProfileMetadataInput({this.levels});

  factory FFMaxProfileMetadataInput.fromJson(Map<String, dynamic> json) =>
      _$FFMaxProfileMetadataInputFromJson(json);

  List<FFMaxProfilemetadataLevelInput>? levels;

  @override
  List<Object?> get props => [levels];
  @override
  Map<String, dynamic> toJson() => _$FFMaxProfileMetadataInputToJson(this);
}

@JsonSerializable(explicitToJson: true)
class FFMaxProfilemetadataLevelInput extends JsonSerializable
    with EquatableMixin {
  FFMaxProfilemetadataLevelInput({
    required this.group,
    required this.level,
  });

  factory FFMaxProfilemetadataLevelInput.fromJson(Map<String, dynamic> json) =>
      _$FFMaxProfilemetadataLevelInputFromJson(json);

  @JsonKey(unknownEnumValue: FfMaxGroups.artemisUnknown)
  late FfMaxGroups group;

  @JsonKey(unknownEnumValue: FfMaxLevels.artemisUnknown)
  late FfMaxLevels level;

  @override
  List<Object?> get props => [group, level];
  @override
  Map<String, dynamic> toJson() => _$FFMaxProfilemetadataLevelInputToJson(this);
}

@JsonSerializable(explicitToJson: true)
class BgmiProfileInput extends JsonSerializable with EquatableMixin {
  BgmiProfileInput({
    this.metadata,
    this.profileId,
    this.username,
  });

  factory BgmiProfileInput.fromJson(Map<String, dynamic> json) =>
      _$BgmiProfileInputFromJson(json);

  BgmiProfileMetadataInput? metadata;

  String? profileId;

  String? username;

  @override
  List<Object?> get props => [metadata, profileId, username];
  @override
  Map<String, dynamic> toJson() => _$BgmiProfileInputToJson(this);
}

@JsonSerializable(explicitToJson: true)
class BgmiProfileMetadataInput extends JsonSerializable with EquatableMixin {
  BgmiProfileMetadataInput({this.levels});

  factory BgmiProfileMetadataInput.fromJson(Map<String, dynamic> json) =>
      _$BgmiProfileMetadataInputFromJson(json);

  List<BgmiProfilemetadataLevelInput>? levels;

  @override
  List<Object?> get props => [levels];
  @override
  Map<String, dynamic> toJson() => _$BgmiProfileMetadataInputToJson(this);
}

@JsonSerializable(explicitToJson: true)
class BgmiProfilemetadataLevelInput extends JsonSerializable
    with EquatableMixin {
  BgmiProfilemetadataLevelInput({
    required this.group,
    required this.level,
  });

  factory BgmiProfilemetadataLevelInput.fromJson(Map<String, dynamic> json) =>
      _$BgmiProfilemetadataLevelInputFromJson(json);

  @JsonKey(unknownEnumValue: BgmiGroups.artemisUnknown)
  late BgmiGroups group;

  @JsonKey(unknownEnumValue: BgmiLevels.artemisUnknown)
  late BgmiLevels level;

  @override
  List<Object?> get props => [group, level];
  @override
  Map<String, dynamic> toJson() => _$BgmiProfilemetadataLevelInputToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UpdateGameProfile$Mutation$Profile extends JsonSerializable
    with EquatableMixin, ProfileMixin {
  UpdateGameProfile$Mutation$Profile();

  factory UpdateGameProfile$Mutation$Profile.fromJson(
          Map<String, dynamic> json) =>
      _$UpdateGameProfile$Mutation$ProfileFromJson(json);

  @override
  List<Object?> get props => [eSport, metadata, username, profileId];
  @override
  Map<String, dynamic> toJson() =>
      _$UpdateGameProfile$Mutation$ProfileToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UpdateGameProfile$Mutation extends JsonSerializable with EquatableMixin {
  UpdateGameProfile$Mutation();

  factory UpdateGameProfile$Mutation.fromJson(Map<String, dynamic> json) =>
      _$UpdateGameProfile$MutationFromJson(json);

  late UpdateGameProfile$Mutation$Profile updateProfile;

  @override
  List<Object?> get props => [updateProfile];
  @override
  Map<String, dynamic> toJson() => _$UpdateGameProfile$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SubmitBGMIGame$Mutation$SubmitGameResponse extends JsonSerializable
    with EquatableMixin, GameResponseMixin {
  SubmitBGMIGame$Mutation$SubmitGameResponse();

  factory SubmitBGMIGame$Mutation$SubmitGameResponse.fromJson(
          Map<String, dynamic> json) =>
      _$SubmitBGMIGame$Mutation$SubmitGameResponseFromJson(json);

  @override
  List<Object?> get props => [game, tournaments];
  @override
  Map<String, dynamic> toJson() =>
      _$SubmitBGMIGame$Mutation$SubmitGameResponseToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SubmitBGMIGame$Mutation extends JsonSerializable with EquatableMixin {
  SubmitBGMIGame$Mutation();

  factory SubmitBGMIGame$Mutation.fromJson(Map<String, dynamic> json) =>
      _$SubmitBGMIGame$MutationFromJson(json);

  late SubmitBGMIGame$Mutation$SubmitGameResponse submitBgmiGame;

  @override
  List<Object?> get props => [submitBgmiGame];
  @override
  Map<String, dynamic> toJson() => _$SubmitBGMIGame$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GameResponseMixin$Game$GameMetadata$BgmiMetadata
    extends GameResponseMixin$Game$GameMetadata with EquatableMixin {
  GameResponseMixin$Game$GameMetadata$BgmiMetadata();

  factory GameResponseMixin$Game$GameMetadata$BgmiMetadata.fromJson(
          Map<String, dynamic> json) =>
      _$GameResponseMixin$Game$GameMetadata$BgmiMetadataFromJson(json);

  late int initialTier;

  late int finalTier;

  late int kills;

  @JsonKey(unknownEnumValue: BgmiGroups.artemisUnknown)
  late BgmiGroups bgmiGroup;

  @JsonKey(unknownEnumValue: BgmiMaps.artemisUnknown)
  late BgmiMaps bgmiMap;

  @override
  List<Object?> get props =>
      [initialTier, finalTier, kills, bgmiGroup, bgmiMap];
  @override
  Map<String, dynamic> toJson() =>
      _$GameResponseMixin$Game$GameMetadata$BgmiMetadataToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GameResponseMixin$Game$GameMetadata$FfMaxMetadata
    extends GameResponseMixin$Game$GameMetadata with EquatableMixin {
  GameResponseMixin$Game$GameMetadata$FfMaxMetadata();

  factory GameResponseMixin$Game$GameMetadata$FfMaxMetadata.fromJson(
          Map<String, dynamic> json) =>
      _$GameResponseMixin$Game$GameMetadata$FfMaxMetadataFromJson(json);

  late int initialTier;

  late int finalTier;

  late int kills;

  @JsonKey(unknownEnumValue: FfMaxGroups.artemisUnknown)
  late FfMaxGroups ffGroup;

  @JsonKey(unknownEnumValue: FfMaxMaps.artemisUnknown)
  late FfMaxMaps ffMap;

  @override
  List<Object?> get props => [initialTier, finalTier, kills, ffGroup, ffMap];
  @override
  Map<String, dynamic> toJson() =>
      _$GameResponseMixin$Game$GameMetadata$FfMaxMetadataToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GameResponseMixin$Game$GameMetadata extends JsonSerializable
    with EquatableMixin {
  GameResponseMixin$Game$GameMetadata();

  factory GameResponseMixin$Game$GameMetadata.fromJson(
      Map<String, dynamic> json) {
    switch (json['__typename'].toString()) {
      case r'BgmiMetadata':
        return GameResponseMixin$Game$GameMetadata$BgmiMetadata.fromJson(json);
      case r'FfMaxMetadata':
        return GameResponseMixin$Game$GameMetadata$FfMaxMetadata.fromJson(json);
      default:
    }
    return _$GameResponseMixin$Game$GameMetadataFromJson(json);
  }

  @JsonKey(name: '__typename')
  String? $$typename;

  @override
  List<Object?> get props => [$$typename];
  @override
  Map<String, dynamic> toJson() {
    switch ($$typename) {
      case r'BgmiMetadata':
        return (this as GameResponseMixin$Game$GameMetadata$BgmiMetadata)
            .toJson();
      case r'FfMaxMetadata':
        return (this as GameResponseMixin$Game$GameMetadata$FfMaxMetadata)
            .toJson();
      default:
    }
    return _$GameResponseMixin$Game$GameMetadataToJson(this);
  }
}

@JsonSerializable(explicitToJson: true)
class GameResponseMixin$Game extends JsonSerializable with EquatableMixin {
  GameResponseMixin$Game();

  factory GameResponseMixin$Game.fromJson(Map<String, dynamic> json) =>
      _$GameResponseMixin$GameFromJson(json);

  late int id;

  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSport;

  late int rank;

  late double score;

  late int userId;

  int? teamRank;

  late GameResponseMixin$Game$GameMetadata metadata;

  @override
  List<Object?> get props =>
      [id, eSport, rank, score, userId, teamRank, metadata];
  @override
  Map<String, dynamic> toJson() => _$GameResponseMixin$GameToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GameResponseMixin$SubmittedGameTournament$Tournament
    extends JsonSerializable with EquatableMixin {
  GameResponseMixin$SubmittedGameTournament$Tournament();

  factory GameResponseMixin$SubmittedGameTournament$Tournament.fromJson(
          Map<String, dynamic> json) =>
      _$GameResponseMixin$SubmittedGameTournament$TournamentFromJson(json);

  late int id;

  late String name;

  @override
  List<Object?> get props => [id, name];
  @override
  Map<String, dynamic> toJson() =>
      _$GameResponseMixin$SubmittedGameTournament$TournamentToJson(this);
}

@JsonSerializable(explicitToJson: true)
class GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$User
    extends JsonSerializable with EquatableMixin, LeaderboardUserMixin {
  GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$User();

  factory GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$User.fromJson(
          Map<String, dynamic> json) =>
      _$GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$UserFromJson(
          json);

  @override
  List<Object?> get props => [id, name, phone, image, username];
  @override
  Map<String, dynamic> toJson() =>
      _$GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$UserToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse
    extends JsonSerializable with EquatableMixin {
  GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse();

  factory GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse.fromJson(
          Map<String, dynamic> json) =>
      _$GameResponseMixin$SubmittedGameTournament$SubmissionStateResponseFromJson(
          json);

  late int userId;

  late bool hasSubmitted;

  late GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse$User
      user;

  @override
  List<Object?> get props => [userId, hasSubmitted, user];
  @override
  Map<String, dynamic> toJson() =>
      _$GameResponseMixin$SubmittedGameTournament$SubmissionStateResponseToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$User
    extends JsonSerializable with EquatableMixin {
  GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$User();

  factory GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$User.fromJson(
          Map<String, dynamic> json) =>
      _$GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$UserFromJson(
          json);

  late String username;

  String? image;

  @override
  List<Object?> get props => [username, image];
  @override
  Map<String, dynamic> toJson() =>
      _$GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$UserToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class GameResponseMixin$SubmittedGameTournament$SquadScoresResponse
    extends JsonSerializable with EquatableMixin {
  GameResponseMixin$SubmittedGameTournament$SquadScoresResponse();

  factory GameResponseMixin$SubmittedGameTournament$SquadScoresResponse.fromJson(
          Map<String, dynamic> json) =>
      _$GameResponseMixin$SubmittedGameTournament$SquadScoresResponseFromJson(
          json);

  late int kills;

  late GameResponseMixin$SubmittedGameTournament$SquadScoresResponse$User user;

  @override
  List<Object?> get props => [kills, user];
  @override
  Map<String, dynamic> toJson() =>
      _$GameResponseMixin$SubmittedGameTournament$SquadScoresResponseToJson(
          this);
}

@JsonSerializable(explicitToJson: true)
class GameResponseMixin$SubmittedGameTournament extends JsonSerializable
    with EquatableMixin {
  GameResponseMixin$SubmittedGameTournament();

  factory GameResponseMixin$SubmittedGameTournament.fromJson(
          Map<String, dynamic> json) =>
      _$GameResponseMixin$SubmittedGameTournamentFromJson(json);

  late bool isAdded;

  late bool isTop;

  String? exclusionReason;

  late GameResponseMixin$SubmittedGameTournament$Tournament tournament;

  List<GameResponseMixin$SubmittedGameTournament$SubmissionStateResponse>?
      submissionState;

  List<GameResponseMixin$SubmittedGameTournament$SquadScoresResponse>?
      squadScores;

  @override
  List<Object?> get props => [
        isAdded,
        isTop,
        exclusionReason,
        tournament,
        submissionState,
        squadScores
      ];
  @override
  Map<String, dynamic> toJson() =>
      _$GameResponseMixin$SubmittedGameTournamentToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SquadMemberGameInfo extends JsonSerializable with EquatableMixin {
  SquadMemberGameInfo({
    required this.kills,
    required this.username,
  });

  factory SquadMemberGameInfo.fromJson(Map<String, dynamic> json) =>
      _$SquadMemberGameInfoFromJson(json);

  late int kills;

  late String username;

  @override
  List<Object?> get props => [kills, username];
  @override
  Map<String, dynamic> toJson() => _$SquadMemberGameInfoToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SubmitFFMaxGame$Mutation$SubmitGameResponse extends JsonSerializable
    with EquatableMixin, GameResponseMixin {
  SubmitFFMaxGame$Mutation$SubmitGameResponse();

  factory SubmitFFMaxGame$Mutation$SubmitGameResponse.fromJson(
          Map<String, dynamic> json) =>
      _$SubmitFFMaxGame$Mutation$SubmitGameResponseFromJson(json);

  @override
  List<Object?> get props => [game, tournaments];
  @override
  Map<String, dynamic> toJson() =>
      _$SubmitFFMaxGame$Mutation$SubmitGameResponseToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SubmitFFMaxGame$Mutation extends JsonSerializable with EquatableMixin {
  SubmitFFMaxGame$Mutation();

  factory SubmitFFMaxGame$Mutation.fromJson(Map<String, dynamic> json) =>
      _$SubmitFFMaxGame$MutationFromJson(json);

  late SubmitFFMaxGame$Mutation$SubmitGameResponse submitFfMaxGame;

  @override
  List<Object?> get props => [submitFfMaxGame];
  @override
  Map<String, dynamic> toJson() => _$SubmitFFMaxGame$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SubmitPreferences$Mutation$UserPreference extends JsonSerializable
    with EquatableMixin, UserPreferenceMixin {
  SubmitPreferences$Mutation$UserPreference();

  factory SubmitPreferences$Mutation$UserPreference.fromJson(
          Map<String, dynamic> json) =>
      _$SubmitPreferences$Mutation$UserPreferenceFromJson(json);

  @override
  List<Object?> get props => [playingReason, timeOfDay, roles, timeOfWeek];
  @override
  Map<String, dynamic> toJson() =>
      _$SubmitPreferences$Mutation$UserPreferenceToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SubmitPreferences$Mutation extends JsonSerializable with EquatableMixin {
  SubmitPreferences$Mutation();

  factory SubmitPreferences$Mutation.fromJson(Map<String, dynamic> json) =>
      _$SubmitPreferences$MutationFromJson(json);

  late SubmitPreferences$Mutation$UserPreference submitPreferences;

  @override
  List<Object?> get props => [submitPreferences];
  @override
  Map<String, dynamic> toJson() => _$SubmitPreferences$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class JoinTournament$Mutation$DefaultPayload extends JsonSerializable
    with EquatableMixin {
  JoinTournament$Mutation$DefaultPayload();

  factory JoinTournament$Mutation$DefaultPayload.fromJson(
          Map<String, dynamic> json) =>
      _$JoinTournament$Mutation$DefaultPayloadFromJson(json);

  late String message;

  @override
  List<Object?> get props => [message];
  @override
  Map<String, dynamic> toJson() =>
      _$JoinTournament$Mutation$DefaultPayloadToJson(this);
}

@JsonSerializable(explicitToJson: true)
class JoinTournament$Mutation extends JsonSerializable with EquatableMixin {
  JoinTournament$Mutation();

  factory JoinTournament$Mutation.fromJson(Map<String, dynamic> json) =>
      _$JoinTournament$MutationFromJson(json);

  late JoinTournament$Mutation$DefaultPayload joinTournament;

  @override
  List<Object?> get props => [joinTournament];
  @override
  Map<String, dynamic> toJson() => _$JoinTournament$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class Withdraw$Mutation$TransactionResponse$Wallet extends JsonSerializable
    with EquatableMixin, WalletMixin {
  Withdraw$Mutation$TransactionResponse$Wallet();

  factory Withdraw$Mutation$TransactionResponse$Wallet.fromJson(
          Map<String, dynamic> json) =>
      _$Withdraw$Mutation$TransactionResponse$WalletFromJson(json);

  @override
  List<Object?> get props => [id, bonus, deposit, winning];
  @override
  Map<String, dynamic> toJson() =>
      _$Withdraw$Mutation$TransactionResponse$WalletToJson(this);
}

@JsonSerializable(explicitToJson: true)
class Withdraw$Mutation$TransactionResponse extends JsonSerializable
    with EquatableMixin {
  Withdraw$Mutation$TransactionResponse();

  factory Withdraw$Mutation$TransactionResponse.fromJson(
          Map<String, dynamic> json) =>
      _$Withdraw$Mutation$TransactionResponseFromJson(json);

  late bool success;

  String? transactionId;

  late Withdraw$Mutation$TransactionResponse$Wallet wallet;

  @override
  List<Object?> get props => [success, transactionId, wallet];
  @override
  Map<String, dynamic> toJson() =>
      _$Withdraw$Mutation$TransactionResponseToJson(this);
}

@JsonSerializable(explicitToJson: true)
class Withdraw$Mutation extends JsonSerializable with EquatableMixin {
  Withdraw$Mutation();

  factory Withdraw$Mutation.fromJson(Map<String, dynamic> json) =>
      _$Withdraw$MutationFromJson(json);

  late Withdraw$Mutation$TransactionResponse withdrawUPI;

  @override
  List<Object?> get props => [withdrawUPI];
  @override
  Map<String, dynamic> toJson() => _$Withdraw$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class CreateSquad$Mutation$Squad extends JsonSerializable
    with EquatableMixin, SquadMixin {
  CreateSquad$Mutation$Squad();

  factory CreateSquad$Mutation$Squad.fromJson(Map<String, dynamic> json) =>
      _$CreateSquad$Mutation$SquadFromJson(json);

  @override
  List<Object?> get props => [name, id, inviteCode, members];
  @override
  Map<String, dynamic> toJson() => _$CreateSquad$Mutation$SquadToJson(this);
}

@JsonSerializable(explicitToJson: true)
class CreateSquad$Mutation extends JsonSerializable with EquatableMixin {
  CreateSquad$Mutation();

  factory CreateSquad$Mutation.fromJson(Map<String, dynamic> json) =>
      _$CreateSquad$MutationFromJson(json);

  late CreateSquad$Mutation$Squad createSquad;

  @override
  List<Object?> get props => [createSquad];
  @override
  Map<String, dynamic> toJson() => _$CreateSquad$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class JoinSquad$Mutation$Squad extends JsonSerializable
    with EquatableMixin, SquadMixin {
  JoinSquad$Mutation$Squad();

  factory JoinSquad$Mutation$Squad.fromJson(Map<String, dynamic> json) =>
      _$JoinSquad$Mutation$SquadFromJson(json);

  @override
  List<Object?> get props => [name, id, inviteCode, members];
  @override
  Map<String, dynamic> toJson() => _$JoinSquad$Mutation$SquadToJson(this);
}

@JsonSerializable(explicitToJson: true)
class JoinSquad$Mutation extends JsonSerializable with EquatableMixin {
  JoinSquad$Mutation();

  factory JoinSquad$Mutation.fromJson(Map<String, dynamic> json) =>
      _$JoinSquad$MutationFromJson(json);

  late JoinSquad$Mutation$Squad joinSquad;

  @override
  List<Object?> get props => [joinSquad];
  @override
  Map<String, dynamic> toJson() => _$JoinSquad$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UpdateSquadName$Mutation$Squad extends JsonSerializable
    with EquatableMixin, SquadMixin {
  UpdateSquadName$Mutation$Squad();

  factory UpdateSquadName$Mutation$Squad.fromJson(Map<String, dynamic> json) =>
      _$UpdateSquadName$Mutation$SquadFromJson(json);

  @override
  List<Object?> get props => [name, id, inviteCode, members];
  @override
  Map<String, dynamic> toJson() => _$UpdateSquadName$Mutation$SquadToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UpdateSquadName$Mutation extends JsonSerializable with EquatableMixin {
  UpdateSquadName$Mutation();

  factory UpdateSquadName$Mutation.fromJson(Map<String, dynamic> json) =>
      _$UpdateSquadName$MutationFromJson(json);

  late UpdateSquadName$Mutation$Squad updateSquad;

  @override
  List<Object?> get props => [updateSquad];
  @override
  Map<String, dynamic> toJson() => _$UpdateSquadName$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class DeleteSquad$Mutation$DefaultPayload extends JsonSerializable
    with EquatableMixin {
  DeleteSquad$Mutation$DefaultPayload();

  factory DeleteSquad$Mutation$DefaultPayload.fromJson(
          Map<String, dynamic> json) =>
      _$DeleteSquad$Mutation$DefaultPayloadFromJson(json);

  late String message;

  @override
  List<Object?> get props => [message];
  @override
  Map<String, dynamic> toJson() =>
      _$DeleteSquad$Mutation$DefaultPayloadToJson(this);
}

@JsonSerializable(explicitToJson: true)
class DeleteSquad$Mutation extends JsonSerializable with EquatableMixin {
  DeleteSquad$Mutation();

  factory DeleteSquad$Mutation.fromJson(Map<String, dynamic> json) =>
      _$DeleteSquad$MutationFromJson(json);

  late DeleteSquad$Mutation$DefaultPayload deleteSquad;

  @override
  List<Object?> get props => [deleteSquad];
  @override
  Map<String, dynamic> toJson() => _$DeleteSquad$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class ChangeSquad$Mutation$Squad extends JsonSerializable
    with EquatableMixin, SquadMixin {
  ChangeSquad$Mutation$Squad();

  factory ChangeSquad$Mutation$Squad.fromJson(Map<String, dynamic> json) =>
      _$ChangeSquad$Mutation$SquadFromJson(json);

  @override
  List<Object?> get props => [name, id, inviteCode, members];
  @override
  Map<String, dynamic> toJson() => _$ChangeSquad$Mutation$SquadToJson(this);
}

@JsonSerializable(explicitToJson: true)
class ChangeSquad$Mutation extends JsonSerializable with EquatableMixin {
  ChangeSquad$Mutation();

  factory ChangeSquad$Mutation.fromJson(Map<String, dynamic> json) =>
      _$ChangeSquad$MutationFromJson(json);

  late ChangeSquad$Mutation$Squad changeSquad;

  @override
  List<Object?> get props => [changeSquad];
  @override
  Map<String, dynamic> toJson() => _$ChangeSquad$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class UnlockSquad$Mutation extends JsonSerializable with EquatableMixin {
  UnlockSquad$Mutation();

  factory UnlockSquad$Mutation.fromJson(Map<String, dynamic> json) =>
      _$UnlockSquad$MutationFromJson(json);

  late bool unlockSquad;

  @override
  List<Object?> get props => [unlockSquad];
  @override
  Map<String, dynamic> toJson() => _$UnlockSquad$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SubmitFeedback$Mutation$DefaultPayload extends JsonSerializable
    with EquatableMixin {
  SubmitFeedback$Mutation$DefaultPayload();

  factory SubmitFeedback$Mutation$DefaultPayload.fromJson(
          Map<String, dynamic> json) =>
      _$SubmitFeedback$Mutation$DefaultPayloadFromJson(json);

  late String message;

  @override
  List<Object?> get props => [message];
  @override
  Map<String, dynamic> toJson() =>
      _$SubmitFeedback$Mutation$DefaultPayloadToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SubmitFeedback$Mutation extends JsonSerializable with EquatableMixin {
  SubmitFeedback$Mutation();

  factory SubmitFeedback$Mutation.fromJson(Map<String, dynamic> json) =>
      _$SubmitFeedback$MutationFromJson(json);

  SubmitFeedback$Mutation$DefaultPayload? submitFeedback;

  @override
  List<Object?> get props => [submitFeedback];
  @override
  Map<String, dynamic> toJson() => _$SubmitFeedback$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SubmitFeedbackInput extends JsonSerializable with EquatableMixin {
  SubmitFeedbackInput({
    required this.collectionEvent,
    this.comments,
    required this.rating,
    this.ratingReason,
  });

  factory SubmitFeedbackInput.fromJson(Map<String, dynamic> json) =>
      _$SubmitFeedbackInputFromJson(json);

  late String collectionEvent;

  String? comments;

  late int rating;

  String? ratingReason;

  @override
  List<Object?> get props => [collectionEvent, comments, rating, ratingReason];
  @override
  Map<String, dynamic> toJson() => _$SubmitFeedbackInputToJson(this);
}

@JsonSerializable(explicitToJson: true)
class DepositUPIManual$Mutation$TransactionResponse$Wallet
    extends JsonSerializable with EquatableMixin, WalletMixin {
  DepositUPIManual$Mutation$TransactionResponse$Wallet();

  factory DepositUPIManual$Mutation$TransactionResponse$Wallet.fromJson(
          Map<String, dynamic> json) =>
      _$DepositUPIManual$Mutation$TransactionResponse$WalletFromJson(json);

  @override
  List<Object?> get props => [id, bonus, deposit, winning];
  @override
  Map<String, dynamic> toJson() =>
      _$DepositUPIManual$Mutation$TransactionResponse$WalletToJson(this);
}

@JsonSerializable(explicitToJson: true)
class DepositUPIManual$Mutation$TransactionResponse extends JsonSerializable
    with EquatableMixin {
  DepositUPIManual$Mutation$TransactionResponse();

  factory DepositUPIManual$Mutation$TransactionResponse.fromJson(
          Map<String, dynamic> json) =>
      _$DepositUPIManual$Mutation$TransactionResponseFromJson(json);

  late DepositUPIManual$Mutation$TransactionResponse$Wallet wallet;

  late bool success;

  String? transactionId;

  @override
  List<Object?> get props => [wallet, success, transactionId];
  @override
  Map<String, dynamic> toJson() =>
      _$DepositUPIManual$Mutation$TransactionResponseToJson(this);
}

@JsonSerializable(explicitToJson: true)
class DepositUPIManual$Mutation extends JsonSerializable with EquatableMixin {
  DepositUPIManual$Mutation();

  factory DepositUPIManual$Mutation.fromJson(Map<String, dynamic> json) =>
      _$DepositUPIManual$MutationFromJson(json);

  late DepositUPIManual$Mutation$TransactionResponse depositUPIManual;

  @override
  List<Object?> get props => [depositUPIManual];
  @override
  Map<String, dynamic> toJson() => _$DepositUPIManual$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class PaymentCreation$Mutation$PhonepeResponse extends JsonSerializable
    with EquatableMixin {
  PaymentCreation$Mutation$PhonepeResponse();

  factory PaymentCreation$Mutation$PhonepeResponse.fromJson(
          Map<String, dynamic> json) =>
      _$PaymentCreation$Mutation$PhonepeResponseFromJson(json);

  late String intentUrl;

  late int ledgerId;

  late bool success;

  @override
  List<Object?> get props => [intentUrl, ledgerId, success];
  @override
  Map<String, dynamic> toJson() =>
      _$PaymentCreation$Mutation$PhonepeResponseToJson(this);
}

@JsonSerializable(explicitToJson: true)
class PaymentCreation$Mutation extends JsonSerializable with EquatableMixin {
  PaymentCreation$Mutation();

  factory PaymentCreation$Mutation.fromJson(Map<String, dynamic> json) =>
      _$PaymentCreation$MutationFromJson(json);

  late PaymentCreation$Mutation$PhonepeResponse paymentCreation;

  @override
  List<Object?> get props => [paymentCreation];
  @override
  Map<String, dynamic> toJson() => _$PaymentCreation$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class EnterTournament$Mutation$UserTournament extends JsonSerializable
    with EquatableMixin, UserTournamentMixin {
  EnterTournament$Mutation$UserTournament();

  factory EnterTournament$Mutation$UserTournament.fromJson(
          Map<String, dynamic> json) =>
      _$EnterTournament$Mutation$UserTournamentFromJson(json);

  @override
  List<Object?> get props =>
      [joinedAt, rank, score, tournament, squad, tournamentMatchUser];
  @override
  Map<String, dynamic> toJson() =>
      _$EnterTournament$Mutation$UserTournamentToJson(this);
}

@JsonSerializable(explicitToJson: true)
class EnterTournament$Mutation extends JsonSerializable with EquatableMixin {
  EnterTournament$Mutation();

  factory EnterTournament$Mutation.fromJson(Map<String, dynamic> json) =>
      _$EnterTournament$MutationFromJson(json);

  late EnterTournament$Mutation$UserTournament enterTournament;

  @override
  List<Object?> get props => [enterTournament];
  @override
  Map<String, dynamic> toJson() => _$EnterTournament$MutationToJson(this);
}

@JsonSerializable(explicitToJson: true)
class TournamentJoiningSquadInfo extends JsonSerializable with EquatableMixin {
  TournamentJoiningSquadInfo({
    this.inviteCode,
    this.name,
  });

  factory TournamentJoiningSquadInfo.fromJson(Map<String, dynamic> json) =>
      _$TournamentJoiningSquadInfoFromJson(json);

  String? inviteCode;

  String? name;

  @override
  List<Object?> get props => [inviteCode, name];
  @override
  Map<String, dynamic> toJson() => _$TournamentJoiningSquadInfoToJson(this);
}

enum ESports {
  @JsonValue('BGMI')
  bgmi,
  @JsonValue('FREEFIREMAX')
  freefiremax,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum MatchType {
  @JsonValue('classic')
  classic,
  @JsonValue('headToHead')
  headToHead,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum CustomQualificationRuleTypes {
  @JsonValue('NUM_GAMES_SINCE')
  numGamesSince,
  @JsonValue('NUM_TOURNAMENTS_SINCE')
  numTournamentsSince,
  @JsonValue('RANK_BY_TOURNAMENT')
  rankByTournament,
  @JsonValue('RANK_SINCE')
  rankSince,
  @JsonValue('SIGNUP_AGE')
  signupAge,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum BgmiLevels {
  @JsonValue('BRONZE_FIVE')
  bronzeFive,
  @JsonValue('BRONZE_FOUR')
  bronzeFour,
  @JsonValue('BRONZE_THREE')
  bronzeThree,
  @JsonValue('BRONZE_TWO')
  bronzeTwo,
  @JsonValue('BRONZE_ONE')
  bronzeOne,
  @JsonValue('SILVER_FIVE')
  silverFive,
  @JsonValue('SILVER_FOUR')
  silverFour,
  @JsonValue('SILVER_THREE')
  silverThree,
  @JsonValue('SILVER_TWO')
  silverTwo,
  @JsonValue('SILVER_ONE')
  silverOne,
  @JsonValue('GOLD_FIVE')
  goldFive,
  @JsonValue('GOLD_FOUR')
  goldFour,
  @JsonValue('GOLD_THREE')
  goldThree,
  @JsonValue('GOLD_TWO')
  goldTwo,
  @JsonValue('GOLD_ONE')
  goldOne,
  @JsonValue('PLATINUM_FIVE')
  platinumFive,
  @JsonValue('PLATINUM_FOUR')
  platinumFour,
  @JsonValue('PLATINUM_THREE')
  platinumThree,
  @JsonValue('PLATINUM_TWO')
  platinumTwo,
  @JsonValue('PLATINUM_ONE')
  platinumOne,
  @JsonValue('DIAMOND_FIVE')
  diamondFive,
  @JsonValue('DIAMOND_FOUR')
  diamondFour,
  @JsonValue('DIAMOND_THREE')
  diamondThree,
  @JsonValue('DIAMOND_TWO')
  diamondTwo,
  @JsonValue('DIAMOND_ONE')
  diamondOne,
  @JsonValue('CROWN_FIVE')
  crownFive,
  @JsonValue('CROWN_FOUR')
  crownFour,
  @JsonValue('CROWN_THREE')
  crownThree,
  @JsonValue('CROWN_TWO')
  crownTwo,
  @JsonValue('CROWN_ONE')
  crownOne,
  @JsonValue('ACE')
  ace,
  @JsonValue('ACE_MASTER')
  aceMaster,
  @JsonValue('ACE_DOMINATOR')
  aceDominator,
  @JsonValue('CONQUEROR')
  conqueror,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum BgmiModes {
  @JsonValue('classic')
  classic,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum BgmiMaps {
  @JsonValue('erangel')
  erangel,
  @JsonValue('karakin')
  karakin,
  @JsonValue('livik')
  livik,
  @JsonValue('miramar')
  miramar,
  @JsonValue('nusa')
  nusa,
  @JsonValue('sanhok')
  sanhok,
  @JsonValue('vikendi')
  vikendi,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum BgmiGroups {
  @JsonValue('duo')
  duo,
  @JsonValue('solo')
  solo,
  @JsonValue('squad')
  squad,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum FfMaxLevels {
  @JsonValue('BRONZE_ONE')
  bronzeOne,
  @JsonValue('BRONZE_TWO')
  bronzeTwo,
  @JsonValue('BRONZE_THREE')
  bronzeThree,
  @JsonValue('SILVER_ONE')
  silverOne,
  @JsonValue('SILVER_TWO')
  silverTwo,
  @JsonValue('SILVER_THREE')
  silverThree,
  @JsonValue('GOLD_ONE')
  goldOne,
  @JsonValue('GOLD_TWO')
  goldTwo,
  @JsonValue('GOLD_THREE')
  goldThree,
  @JsonValue('GOLD_FOUR')
  goldFour,
  @JsonValue('PLATINUM_ONE')
  platinumOne,
  @JsonValue('PLATINUM_TWO')
  platinumTwo,
  @JsonValue('PLATINUM_THREE')
  platinumThree,
  @JsonValue('PLATINUM_FOUR')
  platinumFour,
  @JsonValue('DIAMOND_ONE')
  diamondOne,
  @JsonValue('DIAMOND_TWO')
  diamondTwo,
  @JsonValue('DIAMOND_THREE')
  diamondThree,
  @JsonValue('DIAMOND_FOUR')
  diamondFour,
  @JsonValue('HEROIC')
  heroic,
  @JsonValue('MASTER')
  master,
  @JsonValue('GRANDMASTER_ONE')
  grandmasterOne,
  @JsonValue('GRANDMASTER_TWO')
  grandmasterTwo,
  @JsonValue('GRANDMASTER_THREE')
  grandmasterThree,
  @JsonValue('GRANDMASTER_FOUR')
  grandmasterFour,
  @JsonValue('GRANDMASTER_FIVE')
  grandmasterFive,
  @JsonValue('GRANDMASTER_SIX')
  grandmasterSix,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum FfMaxModes {
  @JsonValue('BRRanked')
  bRRanked,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum FfMaxMaps {
  @JsonValue('alpine')
  alpine,
  @JsonValue('bermuda')
  bermuda,
  @JsonValue('nexterra')
  nexterra,
  @JsonValue('purgatory')
  purgatory,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum FfMaxGroups {
  @JsonValue('duo')
  duo,
  @JsonValue('solo')
  solo,
  @JsonValue('squad')
  squad,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum PlayingReasonPreference {
  @JsonValue('Competitive')
  competitive,
  @JsonValue('Fun')
  fun,
  @JsonValue('Learning')
  learning,
  @JsonValue('Rewards')
  rewards,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum RolePreference {
  @JsonValue('Assaulter')
  assaulter,
  @JsonValue('Coach')
  coach,
  @JsonValue('Commander')
  commander,
  @JsonValue('Fragger')
  fragger,
  @JsonValue('Healer')
  healer,
  @JsonValue('IGL')
  igl,
  @JsonValue('Scout')
  scout,
  @JsonValue('Sniper')
  sniper,
  @JsonValue('Support')
  support,
  @JsonValue('VehicleSpecialist')
  vehicleSpecialist,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum TimeOfDayPreference {
  @JsonValue('Morning')
  morning,
  @JsonValue('Afternoon')
  afternoon,
  @JsonValue('Evening')
  evening,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum TimeOfWeekPreference {
  @JsonValue('Weekdays')
  weekdays,
  @JsonValue('Weekends')
  weekends,
  @JsonValue('Both')
  both,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum LeaderboardDirection {
  @JsonValue('Next')
  next,
  @JsonValue('Prev')
  prev,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum SubWalletType {
  @JsonValue('bonus')
  bonus,
  @JsonValue('deposit')
  deposit,
  @JsonValue('winning')
  winning,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum TransactionStatus {
  @JsonValue('cancelled')
  cancelled,
  @JsonValue('failed')
  failed,
  @JsonValue('processing')
  processing,
  @JsonValue('successful')
  successful,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

enum SortOrder {
  @JsonValue('HORIZONTAL')
  horizontal,
  @JsonValue('PERFORMANCE')
  performance,
  @JsonValue('SKIP')
  skip,
  @JsonValue('VERTICAL')
  vertical,
  @JsonValue('ARTEMIS_UNKNOWN')
  artemisUnknown,
}

@JsonSerializable(explicitToJson: true)
class VerifyOTPArguments extends JsonSerializable with EquatableMixin {
  VerifyOTPArguments({
    required this.otp,
    required this.phoneNum,
  });

  @override
  factory VerifyOTPArguments.fromJson(Map<String, dynamic> json) =>
      _$VerifyOTPArgumentsFromJson(json);

  late int otp;

  late String phoneNum;

  @override
  List<Object?> get props => [otp, phoneNum];
  @override
  Map<String, dynamic> toJson() => _$VerifyOTPArgumentsToJson(this);
}

final VERIFY_O_T_P_QUERY_DOCUMENT_OPERATION_NAME = 'verifyOTP';
final VERIFY_O_T_P_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'verifyOTP'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'otp')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'phoneNum')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'login'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'phone'),
            value: VariableNode(name: NameNode(value: 'phoneNum')),
          ),
          ArgumentNode(
            name: NameNode(value: 'otp'),
            value: VariableNode(name: NameNode(value: 'otp')),
          ),
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'token'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'user'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'id'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              )
            ]),
          ),
        ]),
      )
    ]),
  )
]);

class VerifyOTPQuery extends GraphQLQuery<VerifyOTP$Query, VerifyOTPArguments> {
  VerifyOTPQuery({required this.variables});

  @override
  final DocumentNode document = VERIFY_O_T_P_QUERY_DOCUMENT;

  @override
  final String operationName = VERIFY_O_T_P_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final VerifyOTPArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  VerifyOTP$Query parse(Map<String, dynamic> json) =>
      VerifyOTP$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class LoginOTPLessArguments extends JsonSerializable with EquatableMixin {
  LoginOTPLessArguments({required this.token});

  @override
  factory LoginOTPLessArguments.fromJson(Map<String, dynamic> json) =>
      _$LoginOTPLessArgumentsFromJson(json);

  late String token;

  @override
  List<Object?> get props => [token];
  @override
  Map<String, dynamic> toJson() => _$LoginOTPLessArgumentsToJson(this);
}

final LOGIN_O_T_P_LESS_QUERY_DOCUMENT_OPERATION_NAME = 'loginOTPLess';
final LOGIN_O_T_P_LESS_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'loginOTPLess'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'token')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'loginOTPLess'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'token'),
            value: VariableNode(name: NameNode(value: 'token')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'token'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'user'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'id'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              )
            ]),
          ),
        ]),
      )
    ]),
  )
]);

class LoginOTPLessQuery
    extends GraphQLQuery<LoginOTPLess$Query, LoginOTPLessArguments> {
  LoginOTPLessQuery({required this.variables});

  @override
  final DocumentNode document = LOGIN_O_T_P_LESS_QUERY_DOCUMENT;

  @override
  final String operationName = LOGIN_O_T_P_LESS_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final LoginOTPLessArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  LoginOTPLess$Query parse(Map<String, dynamic> json) =>
      LoginOTPLess$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class CheckUniqueUserArguments extends JsonSerializable with EquatableMixin {
  CheckUniqueUserArguments({
    required this.phoneNum,
    required this.userName,
  });

  @override
  factory CheckUniqueUserArguments.fromJson(Map<String, dynamic> json) =>
      _$CheckUniqueUserArgumentsFromJson(json);

  late String phoneNum;

  late String userName;

  @override
  List<Object?> get props => [phoneNum, userName];
  @override
  Map<String, dynamic> toJson() => _$CheckUniqueUserArgumentsToJson(this);
}

final CHECK_UNIQUE_USER_QUERY_DOCUMENT_OPERATION_NAME = 'checkUniqueUser';
final CHECK_UNIQUE_USER_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'checkUniqueUser'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'phoneNum')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'userName')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'checkUniqueUser'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'phone'),
            value: VariableNode(name: NameNode(value: 'phoneNum')),
          ),
          ArgumentNode(
            name: NameNode(value: 'username'),
            value: VariableNode(name: NameNode(value: 'userName')),
          ),
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'phone'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'username'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      )
    ]),
  )
]);

class CheckUniqueUserQuery
    extends GraphQLQuery<CheckUniqueUser$Query, CheckUniqueUserArguments> {
  CheckUniqueUserQuery({required this.variables});

  @override
  final DocumentNode document = CHECK_UNIQUE_USER_QUERY_DOCUMENT;

  @override
  final String operationName = CHECK_UNIQUE_USER_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final CheckUniqueUserArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  CheckUniqueUser$Query parse(Map<String, dynamic> json) =>
      CheckUniqueUser$Query.fromJson(json);
}

final GET_MY_PROFILE_QUERY_DOCUMENT_OPERATION_NAME = 'getMyProfile';
final GET_MY_PROFILE_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getMyProfile'),
    variableDefinitions: [],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'me'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'User'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'User'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'birthdate'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'achievements'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'bestPerformance'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FragmentSpreadNode(
                name: NameNode(value: 'UserTournament'),
                directives: [],
              )
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'tournamentSummary'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FragmentSpreadNode(
                name: NameNode(value: 'AchievementSummary'),
                directives: [],
              )
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'preferences'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'playingReason'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'roles'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'timeOfDay'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'timeOfWeek'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'profiles'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Profile'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'wallet'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Wallet'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'flags'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'allowSquadCreation'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'UserTournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'UserTournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'joinedAt'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'rank'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'score'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournament'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Tournament'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'squad'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchUser'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Tournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Tournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxPrize'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'userCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'gameCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'fee'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'matchType'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinBy'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'qualifiers'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'rule'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'winningDistribution'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'startRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'endRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'matches'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatch'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'rules'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BGMIRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'bgmiMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'bgmiMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'bgmiAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'bgmiAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'bgmiAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'ffMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'ffMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'ffAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'ffAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'ffAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'minUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxTeams'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatch'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatch'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'tournamentId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'minParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchMetadata'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchMetadata'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchMetadata'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'roomId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'roomPassword'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchUser'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'slotInfo'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'teamNumber'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentUserId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'notJoined'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'sos'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'AchievementSummary'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'AchievementSummary'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'KDRatio'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'group'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'matchType'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxTier'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'played'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'topTenCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Profile'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Profile'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BgmiProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'bgmiGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'bgmiLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'ffMaxGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'ffMaxLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'profileId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Wallet'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Wallet'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'bonus'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'deposit'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'winning'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class GetMyProfileQuery
    extends GraphQLQuery<GetMyProfile$Query, JsonSerializable> {
  GetMyProfileQuery();

  @override
  final DocumentNode document = GET_MY_PROFILE_QUERY_DOCUMENT;

  @override
  final String operationName = GET_MY_PROFILE_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  List<Object?> get props => [document, operationName];
  @override
  GetMyProfile$Query parse(Map<String, dynamic> json) =>
      GetMyProfile$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class GetUserListByPreferenceArguments extends JsonSerializable
    with EquatableMixin {
  GetUserListByPreferenceArguments({required this.preference});

  @override
  factory GetUserListByPreferenceArguments.fromJson(
          Map<String, dynamic> json) =>
      _$GetUserListByPreferenceArgumentsFromJson(json);

  late PreferencesInput preference;

  @override
  List<Object?> get props => [preference];
  @override
  Map<String, dynamic> toJson() =>
      _$GetUserListByPreferenceArgumentsToJson(this);
}

final GET_USER_LIST_BY_PREFERENCE_QUERY_DOCUMENT_OPERATION_NAME =
    'getUserListByPreference';
final GET_USER_LIST_BY_PREFERENCE_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getUserListByPreference'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'preference')),
        type: NamedTypeNode(
          name: NameNode(value: 'PreferencesInput'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'searchByPreferences'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'preferences'),
            value: VariableNode(name: NameNode(value: 'preference')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'User'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'User'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'birthdate'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'achievements'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'bestPerformance'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FragmentSpreadNode(
                name: NameNode(value: 'UserTournament'),
                directives: [],
              )
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'tournamentSummary'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FragmentSpreadNode(
                name: NameNode(value: 'AchievementSummary'),
                directives: [],
              )
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'preferences'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'playingReason'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'roles'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'timeOfDay'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'timeOfWeek'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'profiles'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Profile'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'wallet'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Wallet'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'flags'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'allowSquadCreation'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'UserTournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'UserTournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'joinedAt'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'rank'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'score'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournament'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Tournament'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'squad'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchUser'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Tournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Tournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxPrize'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'userCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'gameCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'fee'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'matchType'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinBy'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'qualifiers'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'rule'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'winningDistribution'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'startRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'endRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'matches'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatch'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'rules'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BGMIRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'bgmiMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'bgmiMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'bgmiAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'bgmiAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'bgmiAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'ffMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'ffMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'ffAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'ffAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'ffAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'minUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxTeams'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatch'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatch'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'tournamentId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'minParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchMetadata'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchMetadata'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchMetadata'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'roomId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'roomPassword'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchUser'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'slotInfo'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'teamNumber'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentUserId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'notJoined'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'sos'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'AchievementSummary'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'AchievementSummary'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'KDRatio'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'group'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'matchType'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxTier'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'played'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'topTenCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Profile'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Profile'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BgmiProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'bgmiGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'bgmiLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'ffMaxGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'ffMaxLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'profileId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Wallet'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Wallet'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'bonus'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'deposit'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'winning'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class GetUserListByPreferenceQuery extends GraphQLQuery<
    GetUserListByPreference$Query, GetUserListByPreferenceArguments> {
  GetUserListByPreferenceQuery({required this.variables});

  @override
  final DocumentNode document = GET_USER_LIST_BY_PREFERENCE_QUERY_DOCUMENT;

  @override
  final String operationName =
      GET_USER_LIST_BY_PREFERENCE_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final GetUserListByPreferenceArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  GetUserListByPreference$Query parse(Map<String, dynamic> json) =>
      GetUserListByPreference$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class GetMyActiveTournamentArguments extends JsonSerializable
    with EquatableMixin {
  GetMyActiveTournamentArguments({required this.eSport});

  @override
  factory GetMyActiveTournamentArguments.fromJson(Map<String, dynamic> json) =>
      _$GetMyActiveTournamentArgumentsFromJson(json);

  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSport;

  @override
  List<Object?> get props => [eSport];
  @override
  Map<String, dynamic> toJson() => _$GetMyActiveTournamentArgumentsToJson(this);
}

final GET_MY_ACTIVE_TOURNAMENT_QUERY_DOCUMENT_OPERATION_NAME =
    'getMyActiveTournament';
final GET_MY_ACTIVE_TOURNAMENT_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getMyActiveTournament'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'eSport')),
        type: NamedTypeNode(
          name: NameNode(value: 'ESports'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'active'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'eSport'),
            value: VariableNode(name: NameNode(value: 'eSport')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'tournaments'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FragmentSpreadNode(
                name: NameNode(value: 'UserTournament'),
                directives: [],
              )
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'type'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'UserTournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'UserTournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'joinedAt'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'rank'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'score'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournament'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Tournament'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'squad'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchUser'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Tournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Tournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxPrize'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'userCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'gameCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'fee'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'matchType'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinBy'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'qualifiers'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'rule'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'winningDistribution'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'startRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'endRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'matches'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatch'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'rules'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BGMIRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'bgmiMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'bgmiMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'bgmiAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'bgmiAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'bgmiAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'ffMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'ffMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'ffAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'ffAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'ffAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'minUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxTeams'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatch'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatch'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'tournamentId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'minParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchMetadata'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchMetadata'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchMetadata'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'roomId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'roomPassword'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchUser'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'slotInfo'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'teamNumber'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentUserId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'notJoined'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'sos'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class GetMyActiveTournamentQuery extends GraphQLQuery<
    GetMyActiveTournament$Query, GetMyActiveTournamentArguments> {
  GetMyActiveTournamentQuery({required this.variables});

  @override
  final DocumentNode document = GET_MY_ACTIVE_TOURNAMENT_QUERY_DOCUMENT;

  @override
  final String operationName =
      GET_MY_ACTIVE_TOURNAMENT_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final GetMyActiveTournamentArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  GetMyActiveTournament$Query parse(Map<String, dynamic> json) =>
      GetMyActiveTournament$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class CheckUserTournamentQualificationArguments extends JsonSerializable
    with EquatableMixin {
  CheckUserTournamentQualificationArguments({required this.tournamentId});

  @override
  factory CheckUserTournamentQualificationArguments.fromJson(
          Map<String, dynamic> json) =>
      _$CheckUserTournamentQualificationArgumentsFromJson(json);

  late int tournamentId;

  @override
  List<Object?> get props => [tournamentId];
  @override
  Map<String, dynamic> toJson() =>
      _$CheckUserTournamentQualificationArgumentsToJson(this);
}

final CHECK_USER_TOURNAMENT_QUALIFICATION_QUERY_DOCUMENT_OPERATION_NAME =
    'checkUserTournamentQualification';
final CHECK_USER_TOURNAMENT_QUALIFICATION_QUERY_DOCUMENT =
    DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'checkUserTournamentQualification'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'getTournamentQualification'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'tournamentId'),
            value: VariableNode(name: NameNode(value: 'tournamentId')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'qualified'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'rules'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'current'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'qualified'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'required'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'rule'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
        ]),
      )
    ]),
  )
]);

class CheckUserTournamentQualificationQuery extends GraphQLQuery<
    CheckUserTournamentQualification$Query,
    CheckUserTournamentQualificationArguments> {
  CheckUserTournamentQualificationQuery({required this.variables});

  @override
  final DocumentNode document =
      CHECK_USER_TOURNAMENT_QUALIFICATION_QUERY_DOCUMENT;

  @override
  final String operationName =
      CHECK_USER_TOURNAMENT_QUALIFICATION_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final CheckUserTournamentQualificationArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  CheckUserTournamentQualification$Query parse(Map<String, dynamic> json) =>
      CheckUserTournamentQualification$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class GetTournamentMatchesArguments extends JsonSerializable
    with EquatableMixin {
  GetTournamentMatchesArguments({required this.tournamentId});

  @override
  factory GetTournamentMatchesArguments.fromJson(Map<String, dynamic> json) =>
      _$GetTournamentMatchesArgumentsFromJson(json);

  late int tournamentId;

  @override
  List<Object?> get props => [tournamentId];
  @override
  Map<String, dynamic> toJson() => _$GetTournamentMatchesArgumentsToJson(this);
}

final GET_TOURNAMENT_MATCHES_QUERY_DOCUMENT_OPERATION_NAME =
    'getTournamentMatches';
final GET_TOURNAMENT_MATCHES_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getTournamentMatches'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'getTournamentMatches'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'tournamentId'),
            value: VariableNode(name: NameNode(value: 'tournamentId')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatch'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatch'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatch'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'tournamentId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'minParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchMetadata'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchMetadata'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchMetadata'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'roomId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'roomPassword'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class GetTournamentMatchesQuery extends GraphQLQuery<GetTournamentMatches$Query,
    GetTournamentMatchesArguments> {
  GetTournamentMatchesQuery({required this.variables});

  @override
  final DocumentNode document = GET_TOURNAMENT_MATCHES_QUERY_DOCUMENT;

  @override
  final String operationName =
      GET_TOURNAMENT_MATCHES_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final GetTournamentMatchesArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  GetTournamentMatches$Query parse(Map<String, dynamic> json) =>
      GetTournamentMatches$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class GetTopTournamentsArguments extends JsonSerializable with EquatableMixin {
  GetTopTournamentsArguments({required this.eSport});

  @override
  factory GetTopTournamentsArguments.fromJson(Map<String, dynamic> json) =>
      _$GetTopTournamentsArgumentsFromJson(json);

  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSport;

  @override
  List<Object?> get props => [eSport];
  @override
  Map<String, dynamic> toJson() => _$GetTopTournamentsArgumentsToJson(this);
}

final GET_TOP_TOURNAMENTS_QUERY_DOCUMENT_OPERATION_NAME = 'getTopTournaments';
final GET_TOP_TOURNAMENTS_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getTopTournaments'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'eSport')),
        type: NamedTypeNode(
          name: NameNode(value: 'ESports'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'top'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'eSport'),
            value: VariableNode(name: NameNode(value: 'eSport')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'UserTournament'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'UserTournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'UserTournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'joinedAt'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'rank'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'score'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournament'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Tournament'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'squad'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchUser'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Tournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Tournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxPrize'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'userCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'gameCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'fee'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'matchType'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinBy'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'qualifiers'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'rule'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'winningDistribution'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'startRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'endRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'matches'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatch'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'rules'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BGMIRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'bgmiMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'bgmiMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'bgmiAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'bgmiAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'bgmiAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'ffMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'ffMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'ffAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'ffAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'ffAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'minUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxTeams'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatch'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatch'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'tournamentId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'minParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchMetadata'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchMetadata'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchMetadata'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'roomId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'roomPassword'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchUser'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'slotInfo'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'teamNumber'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentUserId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'notJoined'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'sos'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class GetTopTournamentsQuery
    extends GraphQLQuery<GetTopTournaments$Query, GetTopTournamentsArguments> {
  GetTopTournamentsQuery({required this.variables});

  @override
  final DocumentNode document = GET_TOP_TOURNAMENTS_QUERY_DOCUMENT;

  @override
  final String operationName =
      GET_TOP_TOURNAMENTS_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final GetTopTournamentsArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  GetTopTournaments$Query parse(Map<String, dynamic> json) =>
      GetTopTournaments$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class GetTournamentsHistoryArguments extends JsonSerializable
    with EquatableMixin {
  GetTournamentsHistoryArguments({required this.eSport});

  @override
  factory GetTournamentsHistoryArguments.fromJson(Map<String, dynamic> json) =>
      _$GetTournamentsHistoryArgumentsFromJson(json);

  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSport;

  @override
  List<Object?> get props => [eSport];
  @override
  Map<String, dynamic> toJson() => _$GetTournamentsHistoryArgumentsToJson(this);
}

final GET_TOURNAMENTS_HISTORY_QUERY_DOCUMENT_OPERATION_NAME =
    'getTournamentsHistory';
final GET_TOURNAMENTS_HISTORY_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getTournamentsHistory'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'eSport')),
        type: NamedTypeNode(
          name: NameNode(value: 'ESports'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'history'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'eSport'),
            value: VariableNode(name: NameNode(value: 'eSport')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'UserTournament'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'UserTournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'UserTournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'joinedAt'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'rank'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'score'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournament'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Tournament'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'squad'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchUser'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Tournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Tournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxPrize'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'userCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'gameCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'fee'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'matchType'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinBy'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'qualifiers'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'rule'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'winningDistribution'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'startRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'endRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'matches'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatch'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'rules'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BGMIRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'bgmiMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'bgmiMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'bgmiAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'bgmiAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'bgmiAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'ffMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'ffMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'ffAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'ffAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'ffAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'minUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxTeams'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatch'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatch'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'tournamentId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'minParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchMetadata'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchMetadata'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchMetadata'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'roomId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'roomPassword'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchUser'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'slotInfo'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'teamNumber'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentUserId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'notJoined'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'sos'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class GetTournamentsHistoryQuery extends GraphQLQuery<
    GetTournamentsHistory$Query, GetTournamentsHistoryArguments> {
  GetTournamentsHistoryQuery({required this.variables});

  @override
  final DocumentNode document = GET_TOURNAMENTS_HISTORY_QUERY_DOCUMENT;

  @override
  final String operationName =
      GET_TOURNAMENTS_HISTORY_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final GetTournamentsHistoryArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  GetTournamentsHistory$Query parse(Map<String, dynamic> json) =>
      GetTournamentsHistory$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class GetLeaderboardArguments extends JsonSerializable with EquatableMixin {
  GetLeaderboardArguments({
    this.userId,
    required this.tournamentId,
    this.direction,
    this.page,
    this.pageSize,
  });

  @override
  factory GetLeaderboardArguments.fromJson(Map<String, dynamic> json) =>
      _$GetLeaderboardArgumentsFromJson(json);

  final int? userId;

  late int tournamentId;

  @JsonKey(unknownEnumValue: LeaderboardDirection.artemisUnknown)
  final LeaderboardDirection? direction;

  final int? page;

  final int? pageSize;

  @override
  List<Object?> get props => [userId, tournamentId, direction, page, pageSize];
  @override
  Map<String, dynamic> toJson() => _$GetLeaderboardArgumentsToJson(this);
}

final GET_LEADERBOARD_QUERY_DOCUMENT_OPERATION_NAME = 'getLeaderboard';
final GET_LEADERBOARD_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getLeaderboard'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'userId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'direction')),
        type: NamedTypeNode(
          name: NameNode(value: 'LeaderboardDirection'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'page')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'pageSize')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'leaderboard'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'tournamentId'),
                value: VariableNode(name: NameNode(value: 'tournamentId')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'userId'),
                value: VariableNode(name: NameNode(value: 'userId')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'direction'),
                value: VariableNode(name: NameNode(value: 'direction')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'page'),
                value: VariableNode(name: NameNode(value: 'page')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'pageSize'),
                value: VariableNode(name: NameNode(value: 'pageSize')),
              ),
            ]),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardRank'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardRank'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Leaderboard'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'rank'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'score'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'behindBy'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'isDisqualified'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'details'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'gamesPlayed'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'top'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'rank'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'score'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'metadata'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: '__typename'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  InlineFragmentNode(
                    typeCondition: TypeConditionNode(
                        on: NamedTypeNode(
                      name: NameNode(value: 'BgmiMetadata'),
                      isNonNull: false,
                    )),
                    directives: [],
                    selectionSet: SelectionSetNode(selections: [
                      FieldNode(
                        name: NameNode(value: 'kills'),
                        alias: null,
                        arguments: [],
                        directives: [],
                        selectionSet: null,
                      )
                    ]),
                  ),
                  InlineFragmentNode(
                    typeCondition: TypeConditionNode(
                        on: NamedTypeNode(
                      name: NameNode(value: 'FfMaxMetadata'),
                      isNonNull: false,
                    )),
                    directives: [],
                    selectionSet: SelectionSetNode(selections: [
                      FieldNode(
                        name: NameNode(value: 'kills'),
                        alias: null,
                        arguments: [],
                        directives: [],
                        selectionSet: null,
                      )
                    ]),
                  ),
                ]),
              ),
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class GetLeaderboardQuery
    extends GraphQLQuery<GetLeaderboard$Query, GetLeaderboardArguments> {
  GetLeaderboardQuery({required this.variables});

  @override
  final DocumentNode document = GET_LEADERBOARD_QUERY_DOCUMENT;

  @override
  final String operationName = GET_LEADERBOARD_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final GetLeaderboardArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  GetLeaderboard$Query parse(Map<String, dynamic> json) =>
      GetLeaderboard$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class GetSquadLeaderboardArguments extends JsonSerializable
    with EquatableMixin {
  GetSquadLeaderboardArguments({
    this.squadId,
    required this.tournamentId,
    this.direction,
    this.page,
    this.pageSize,
  });

  @override
  factory GetSquadLeaderboardArguments.fromJson(Map<String, dynamic> json) =>
      _$GetSquadLeaderboardArgumentsFromJson(json);

  final int? squadId;

  late int tournamentId;

  @JsonKey(unknownEnumValue: LeaderboardDirection.artemisUnknown)
  final LeaderboardDirection? direction;

  final int? page;

  final int? pageSize;

  @override
  List<Object?> get props => [squadId, tournamentId, direction, page, pageSize];
  @override
  Map<String, dynamic> toJson() => _$GetSquadLeaderboardArgumentsToJson(this);
}

final GET_SQUAD_LEADERBOARD_QUERY_DOCUMENT_OPERATION_NAME =
    'getSquadLeaderboard';
final GET_SQUAD_LEADERBOARD_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getSquadLeaderboard'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'squadId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'direction')),
        type: NamedTypeNode(
          name: NameNode(value: 'LeaderboardDirection'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'page')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'pageSize')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'squadLeaderboard'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'tournamentId'),
                value: VariableNode(name: NameNode(value: 'tournamentId')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'squadId'),
                value: VariableNode(name: NameNode(value: 'squadId')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'direction'),
                value: VariableNode(name: NameNode(value: 'direction')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'page'),
                value: VariableNode(name: NameNode(value: 'page')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'pageSize'),
                value: VariableNode(name: NameNode(value: 'pageSize')),
              ),
            ]),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadLeaderboard'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadLeaderboard'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadLeaderboard'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'squad'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardSquad'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'behindBy'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'rank'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'score'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'details'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'gamesPlayed'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'top'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'score'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'teamRank'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardSquad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isDisqualified'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class GetSquadLeaderboardQuery extends GraphQLQuery<GetSquadLeaderboard$Query,
    GetSquadLeaderboardArguments> {
  GetSquadLeaderboardQuery({required this.variables});

  @override
  final DocumentNode document = GET_SQUAD_LEADERBOARD_QUERY_DOCUMENT;

  @override
  final String operationName =
      GET_SQUAD_LEADERBOARD_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final GetSquadLeaderboardArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  GetSquadLeaderboard$Query parse(Map<String, dynamic> json) =>
      GetSquadLeaderboard$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class GetTournamentArguments extends JsonSerializable with EquatableMixin {
  GetTournamentArguments({required this.tournamentId});

  @override
  factory GetTournamentArguments.fromJson(Map<String, dynamic> json) =>
      _$GetTournamentArgumentsFromJson(json);

  late int tournamentId;

  @override
  List<Object?> get props => [tournamentId];
  @override
  Map<String, dynamic> toJson() => _$GetTournamentArgumentsToJson(this);
}

final GET_TOURNAMENT_QUERY_DOCUMENT_OPERATION_NAME = 'getTournament';
final GET_TOURNAMENT_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getTournament'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'tournament'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'tournamentId'),
            value: VariableNode(name: NameNode(value: 'tournamentId')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'UserTournament'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'UserTournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'UserTournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'joinedAt'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'rank'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'score'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournament'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Tournament'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'squad'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchUser'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Tournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Tournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxPrize'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'userCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'gameCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'fee'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'matchType'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinBy'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'qualifiers'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'rule'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'winningDistribution'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'startRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'endRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'matches'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatch'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'rules'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BGMIRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'bgmiMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'bgmiMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'bgmiAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'bgmiAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'bgmiAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'ffMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'ffMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'ffAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'ffAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'ffAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'minUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxTeams'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatch'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatch'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'tournamentId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'minParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchMetadata'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchMetadata'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchMetadata'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'roomId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'roomPassword'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchUser'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'slotInfo'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'teamNumber'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentUserId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'notJoined'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'sos'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class GetTournamentQuery
    extends GraphQLQuery<GetTournament$Query, GetTournamentArguments> {
  GetTournamentQuery({required this.variables});

  @override
  final DocumentNode document = GET_TOURNAMENT_QUERY_DOCUMENT;

  @override
  final String operationName = GET_TOURNAMENT_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final GetTournamentArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  GetTournament$Query parse(Map<String, dynamic> json) =>
      GetTournament$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class GetGameScoringArguments extends JsonSerializable with EquatableMixin {
  GetGameScoringArguments({required this.eSport});

  @override
  factory GetGameScoringArguments.fromJson(Map<String, dynamic> json) =>
      _$GetGameScoringArgumentsFromJson(json);

  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSport;

  @override
  List<Object?> get props => [eSport];
  @override
  Map<String, dynamic> toJson() => _$GetGameScoringArgumentsToJson(this);
}

final GET_GAME_SCORING_QUERY_DOCUMENT_OPERATION_NAME = 'getGameScoring';
final GET_GAME_SCORING_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getGameScoring'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'eSport')),
        type: NamedTypeNode(
          name: NameNode(value: 'ESports'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'scoring'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'eSport'),
            value: VariableNode(name: NameNode(value: 'eSport')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'killPoints'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'rankPoints'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'points'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'rank'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
        ]),
      )
    ]),
  )
]);

class GetGameScoringQuery
    extends GraphQLQuery<GetGameScoring$Query, GetGameScoringArguments> {
  GetGameScoringQuery({required this.variables});

  @override
  final DocumentNode document = GET_GAME_SCORING_QUERY_DOCUMENT;

  @override
  final String operationName = GET_GAME_SCORING_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final GetGameScoringArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  GetGameScoring$Query parse(Map<String, dynamic> json) =>
      GetGameScoring$Query.fromJson(json);
}

final GET_TRANSACTIONS_QUERY_DOCUMENT_OPERATION_NAME = 'getTransactions';
final GET_TRANSACTIONS_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'getTransactions'),
    variableDefinitions: [],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'transactions'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'amount'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'subWallet'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'createdAt'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'status'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'description'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'transactionId'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'id'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      )
    ]),
  )
]);

class GetTransactionsQuery
    extends GraphQLQuery<GetTransactions$Query, JsonSerializable> {
  GetTransactionsQuery();

  @override
  final DocumentNode document = GET_TRANSACTIONS_QUERY_DOCUMENT;

  @override
  final String operationName = GET_TRANSACTIONS_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  List<Object?> get props => [document, operationName];
  @override
  GetTransactions$Query parse(Map<String, dynamic> json) =>
      GetTransactions$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class TransactionArguments extends JsonSerializable with EquatableMixin {
  TransactionArguments({required this.transactionId});

  @override
  factory TransactionArguments.fromJson(Map<String, dynamic> json) =>
      _$TransactionArgumentsFromJson(json);

  late int transactionId;

  @override
  List<Object?> get props => [transactionId];
  @override
  Map<String, dynamic> toJson() => _$TransactionArgumentsToJson(this);
}

final TRANSACTION_QUERY_DOCUMENT_OPERATION_NAME = 'transaction';
final TRANSACTION_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'transaction'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'transactionId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'transaction'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'id'),
            value: VariableNode(name: NameNode(value: 'transactionId')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'id'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'transactionId'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'status'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'amount'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'description'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      )
    ]),
  )
]);

class TransactionQuery
    extends GraphQLQuery<Transaction$Query, TransactionArguments> {
  TransactionQuery({required this.variables});

  @override
  final DocumentNode document = TRANSACTION_QUERY_DOCUMENT;

  @override
  final String operationName = TRANSACTION_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final TransactionArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  Transaction$Query parse(Map<String, dynamic> json) =>
      Transaction$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class SearchUserArguments extends JsonSerializable with EquatableMixin {
  SearchUserArguments({
    this.phoneNum,
    this.userName,
    required this.eSport,
    this.gameProfileId,
  });

  @override
  factory SearchUserArguments.fromJson(Map<String, dynamic> json) =>
      _$SearchUserArgumentsFromJson(json);

  final String? phoneNum;

  final String? userName;

  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSport;

  final String? gameProfileId;

  @override
  List<Object?> get props => [phoneNum, userName, eSport, gameProfileId];
  @override
  Map<String, dynamic> toJson() => _$SearchUserArgumentsToJson(this);
}

final SEARCH_USER_QUERY_DOCUMENT_OPERATION_NAME = 'searchUser';
final SEARCH_USER_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'searchUser'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'phoneNum')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'userName')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'eSport')),
        type: NamedTypeNode(
          name: NameNode(value: 'ESports'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'gameProfileId')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'searchUserESports'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'eSport'),
            value: VariableNode(name: NameNode(value: 'eSport')),
          ),
          ArgumentNode(
            name: NameNode(value: 'phone'),
            value: VariableNode(name: NameNode(value: 'phoneNum')),
          ),
          ArgumentNode(
            name: NameNode(value: 'profileId'),
            value: VariableNode(name: NameNode(value: 'gameProfileId')),
          ),
          ArgumentNode(
            name: NameNode(value: 'username'),
            value: VariableNode(name: NameNode(value: 'userName')),
          ),
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'UserSummary'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'UserSummary'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'profiles'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Profile'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Profile'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Profile'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BgmiProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'bgmiGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'bgmiLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'ffMaxGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'ffMaxLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'profileId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class SearchUserQuery
    extends GraphQLQuery<SearchUser$Query, SearchUserArguments> {
  SearchUserQuery({required this.variables});

  @override
  final DocumentNode document = SEARCH_USER_QUERY_DOCUMENT;

  @override
  final String operationName = SEARCH_USER_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final SearchUserArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  SearchUser$Query parse(Map<String, dynamic> json) =>
      SearchUser$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class RecentPlayersArguments extends JsonSerializable with EquatableMixin {
  RecentPlayersArguments({required this.tournamentId});

  @override
  factory RecentPlayersArguments.fromJson(Map<String, dynamic> json) =>
      _$RecentPlayersArgumentsFromJson(json);

  late int tournamentId;

  @override
  List<Object?> get props => [tournamentId];
  @override
  Map<String, dynamic> toJson() => _$RecentPlayersArgumentsToJson(this);
}

final RECENT_PLAYERS_QUERY_DOCUMENT_OPERATION_NAME = 'recentPlayers';
final RECENT_PLAYERS_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'recentPlayers'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'inviteList'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'tournamentId'),
            value: VariableNode(name: NameNode(value: 'tournamentId')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'UserSummary'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'UserSummary'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'profiles'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Profile'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Profile'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Profile'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BgmiProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'bgmiGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'bgmiLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'ffMaxGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'ffMaxLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'profileId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class RecentPlayersQuery
    extends GraphQLQuery<RecentPlayers$Query, RecentPlayersArguments> {
  RecentPlayersQuery({required this.variables});

  @override
  final DocumentNode document = RECENT_PLAYERS_QUERY_DOCUMENT;

  @override
  final String operationName = RECENT_PLAYERS_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final RecentPlayersArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  RecentPlayers$Query parse(Map<String, dynamic> json) =>
      RecentPlayers$Query.fromJson(json);
}

final LOW_RATING_QUERY_DOCUMENT_OPERATION_NAME = 'lowRating';
final LOW_RATING_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'lowRating'),
    variableDefinitions: [],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'lowRatingReasons'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      )
    ]),
  )
]);

class LowRatingQuery extends GraphQLQuery<LowRating$Query, JsonSerializable> {
  LowRatingQuery();

  @override
  final DocumentNode document = LOW_RATING_QUERY_DOCUMENT;

  @override
  final String operationName = LOW_RATING_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  List<Object?> get props => [document, operationName];
  @override
  LowRating$Query parse(Map<String, dynamic> json) =>
      LowRating$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class TopWinnersArguments extends JsonSerializable with EquatableMixin {
  TopWinnersArguments({
    required this.count,
    required this.from,
    required this.to,
  });

  @override
  factory TopWinnersArguments.fromJson(Map<String, dynamic> json) =>
      _$TopWinnersArgumentsFromJson(json);

  late int count;

  @JsonKey(
      fromJson: fromGraphQLDateTimeToDartDateTime,
      toJson: fromDartDateTimeToGraphQLDateTime)
  late DateTime from;

  @JsonKey(
      fromJson: fromGraphQLDateTimeToDartDateTime,
      toJson: fromDartDateTimeToGraphQLDateTime)
  late DateTime to;

  @override
  List<Object?> get props => [count, from, to];
  @override
  Map<String, dynamic> toJson() => _$TopWinnersArgumentsToJson(this);
}

final TOP_WINNERS_QUERY_DOCUMENT_OPERATION_NAME = 'topWinners';
final TOP_WINNERS_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'topWinners'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'count')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'from')),
        type: NamedTypeNode(
          name: NameNode(value: 'DateTime'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'to')),
        type: NamedTypeNode(
          name: NameNode(value: 'DateTime'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'topWinners'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'count'),
            value: VariableNode(name: NameNode(value: 'count')),
          ),
          ArgumentNode(
            name: NameNode(value: 'from'),
            value: VariableNode(name: NameNode(value: 'from')),
          ),
          ArgumentNode(
            name: NameNode(value: 'to'),
            value: VariableNode(name: NameNode(value: 'to')),
          ),
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'amount'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'user'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'username'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              )
            ]),
          ),
        ]),
      )
    ]),
  )
]);

class TopWinnersQuery
    extends GraphQLQuery<TopWinners$Query, TopWinnersArguments> {
  TopWinnersQuery({required this.variables});

  @override
  final DocumentNode document = TOP_WINNERS_QUERY_DOCUMENT;

  @override
  final String operationName = TOP_WINNERS_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final TopWinnersArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  TopWinners$Query parse(Map<String, dynamic> json) =>
      TopWinners$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class ModelParamArguments extends JsonSerializable with EquatableMixin {
  ModelParamArguments({required this.eSport});

  @override
  factory ModelParamArguments.fromJson(Map<String, dynamic> json) =>
      _$ModelParamArgumentsFromJson(json);

  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSport;

  @override
  List<Object?> get props => [eSport];
  @override
  Map<String, dynamic> toJson() => _$ModelParamArgumentsToJson(this);
}

final MODEL_PARAM_QUERY_DOCUMENT_OPERATION_NAME = 'ModelParam';
final MODEL_PARAM_QUERY_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.query,
    name: NameNode(value: 'ModelParam'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'eSport')),
        type: NamedTypeNode(
          name: NameNode(value: 'ESports'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'modelParam'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'eSport'),
            value: VariableNode(name: NameNode(value: 'eSport')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'model_name'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'model_url'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'labels'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FragmentSpreadNode(
                name: NameNode(value: 'LabelFields'),
                directives: [],
              )
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'bucket'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'resultRankRating'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'BucketFields'),
                    directives: [],
                  )
                ]),
              ),
              FieldNode(
                name: NameNode(value: 'resultRankKills'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'BucketFields'),
                    directives: [],
                  )
                ]),
              ),
              FieldNode(
                name: NameNode(value: 'resultRank'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'BucketFields'),
                    directives: [],
                  )
                ]),
              ),
              FieldNode(
                name: NameNode(value: 'homeScreenBucket'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'BucketFields'),
                    directives: [],
                  )
                ]),
              ),
              FieldNode(
                name: NameNode(value: 'waitingScreenBucket'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'BucketFields'),
                    directives: [],
                  )
                ]),
              ),
              FieldNode(
                name: NameNode(value: 'gameScreenBucket'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'BucketFields'),
                    directives: [],
                  )
                ]),
              ),
              FieldNode(
                name: NameNode(value: 'loginScreenBucket'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'BucketFields'),
                    directives: [],
                  )
                ]),
              ),
              FieldNode(
                name: NameNode(value: 'myProfileScreen'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'BucketFields'),
                    directives: [],
                  )
                ]),
              ),
              FieldNode(
                name: NameNode(value: 'playAgain'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'BucketFields'),
                    directives: [],
                  )
                ]),
              ),
            ]),
          ),
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LabelFields'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Label'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'index'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'threshold'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'individualOCR'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'sortOrder'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'mandatory'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'shouldPerformScaleAndStitching'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'BucketFields'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Bucket'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'bufferSize'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'labels'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LabelFields'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
]);

class ModelParamQuery
    extends GraphQLQuery<ModelParam$Query, ModelParamArguments> {
  ModelParamQuery({required this.variables});

  @override
  final DocumentNode document = MODEL_PARAM_QUERY_DOCUMENT;

  @override
  final String operationName = MODEL_PARAM_QUERY_DOCUMENT_OPERATION_NAME;

  @override
  final ModelParamArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  ModelParam$Query parse(Map<String, dynamic> json) =>
      ModelParam$Query.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class RequestOtpArguments extends JsonSerializable with EquatableMixin {
  RequestOtpArguments({required this.phoneNum});

  @override
  factory RequestOtpArguments.fromJson(Map<String, dynamic> json) =>
      _$RequestOtpArgumentsFromJson(json);

  late String phoneNum;

  @override
  List<Object?> get props => [phoneNum];
  @override
  Map<String, dynamic> toJson() => _$RequestOtpArgumentsToJson(this);
}

final REQUEST_OTP_MUTATION_DOCUMENT_OPERATION_NAME = 'requestOtp';
final REQUEST_OTP_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'requestOtp'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'phoneNum')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'generateOTP'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'phone'),
            value: VariableNode(name: NameNode(value: 'phoneNum')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'message'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      )
    ]),
  )
]);

class RequestOtpMutation
    extends GraphQLQuery<RequestOtp$Mutation, RequestOtpArguments> {
  RequestOtpMutation({required this.variables});

  @override
  final DocumentNode document = REQUEST_OTP_MUTATION_DOCUMENT;

  @override
  final String operationName = REQUEST_OTP_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final RequestOtpArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  RequestOtp$Mutation parse(Map<String, dynamic> json) =>
      RequestOtp$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class AddUserArguments extends JsonSerializable with EquatableMixin {
  AddUserArguments({
    required this.userName,
    required this.birthDate,
    required this.name,
    required this.phone,
    this.referralCode,
  });

  @override
  factory AddUserArguments.fromJson(Map<String, dynamic> json) =>
      _$AddUserArgumentsFromJson(json);

  late String userName;

  @JsonKey(
      fromJson: fromGraphQLDateToDartDateTime,
      toJson: fromDartDateTimeToGraphQLDate)
  late DateTime birthDate;

  late String name;

  late String phone;

  final String? referralCode;

  @override
  List<Object?> get props => [userName, birthDate, name, phone, referralCode];
  @override
  Map<String, dynamic> toJson() => _$AddUserArgumentsToJson(this);
}

final ADD_USER_MUTATION_DOCUMENT_OPERATION_NAME = 'addUser';
final ADD_USER_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'addUser'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'userName')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'birthDate')),
        type: NamedTypeNode(
          name: NameNode(value: 'Date'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'name')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'phone')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'referralCode')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'addUser'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'birthdate'),
                value: VariableNode(name: NameNode(value: 'birthDate')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'username'),
                value: VariableNode(name: NameNode(value: 'userName')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'name'),
                value: VariableNode(name: NameNode(value: 'name')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'referralCode'),
                value: VariableNode(name: NameNode(value: 'referralCode')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'phone'),
                value: VariableNode(name: NameNode(value: 'phone')),
              ),
            ]),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'token'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'user'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'id'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              )
            ]),
          ),
        ]),
      )
    ]),
  )
]);

class AddUserMutation extends GraphQLQuery<AddUser$Mutation, AddUserArguments> {
  AddUserMutation({required this.variables});

  @override
  final DocumentNode document = ADD_USER_MUTATION_DOCUMENT;

  @override
  final String operationName = ADD_USER_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final AddUserArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  AddUser$Mutation parse(Map<String, dynamic> json) =>
      AddUser$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class CreateGameProfileArguments extends JsonSerializable with EquatableMixin {
  CreateGameProfileArguments({
    this.ffmaxProfileInput,
    this.bgmiProfileMetadataInput,
    required this.eSports,
  });

  @override
  factory CreateGameProfileArguments.fromJson(Map<String, dynamic> json) =>
      _$CreateGameProfileArgumentsFromJson(json);

  final FFMaxProfileInput? ffmaxProfileInput;

  final BgmiProfileInput? bgmiProfileMetadataInput;

  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSports;

  @override
  List<Object?> get props =>
      [ffmaxProfileInput, bgmiProfileMetadataInput, eSports];
  @override
  Map<String, dynamic> toJson() => _$CreateGameProfileArgumentsToJson(this);
}

final CREATE_GAME_PROFILE_MUTATION_DOCUMENT_OPERATION_NAME =
    'createGameProfile';
final CREATE_GAME_PROFILE_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'createGameProfile'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'ffmaxProfileInput')),
        type: NamedTypeNode(
          name: NameNode(value: 'FFMaxProfileInput'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable:
            VariableNode(name: NameNode(value: 'bgmiProfileMetadataInput')),
        type: NamedTypeNode(
          name: NameNode(value: 'BgmiProfileInput'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'eSports')),
        type: NamedTypeNode(
          name: NameNode(value: 'ESports'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'createProfile'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'ffMaxProfile'),
                value: VariableNode(name: NameNode(value: 'ffmaxProfileInput')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'bgmiProfile'),
                value: VariableNode(
                    name: NameNode(value: 'bgmiProfileMetadataInput')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'eSport'),
                value: VariableNode(name: NameNode(value: 'eSports')),
              ),
            ]),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Profile'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Profile'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Profile'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BgmiProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'bgmiGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'bgmiLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'ffMaxGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'ffMaxLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'profileId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class CreateGameProfileMutation extends GraphQLQuery<CreateGameProfile$Mutation,
    CreateGameProfileArguments> {
  CreateGameProfileMutation({required this.variables});

  @override
  final DocumentNode document = CREATE_GAME_PROFILE_MUTATION_DOCUMENT;

  @override
  final String operationName =
      CREATE_GAME_PROFILE_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final CreateGameProfileArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  CreateGameProfile$Mutation parse(Map<String, dynamic> json) =>
      CreateGameProfile$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class UpdateGameProfileArguments extends JsonSerializable with EquatableMixin {
  UpdateGameProfileArguments({
    this.ffmaxProfileInput,
    this.bgmiProfileMetadataInput,
    required this.eSports,
  });

  @override
  factory UpdateGameProfileArguments.fromJson(Map<String, dynamic> json) =>
      _$UpdateGameProfileArgumentsFromJson(json);

  final FFMaxProfileInput? ffmaxProfileInput;

  final BgmiProfileInput? bgmiProfileMetadataInput;

  @JsonKey(unknownEnumValue: ESports.artemisUnknown)
  late ESports eSports;

  @override
  List<Object?> get props =>
      [ffmaxProfileInput, bgmiProfileMetadataInput, eSports];
  @override
  Map<String, dynamic> toJson() => _$UpdateGameProfileArgumentsToJson(this);
}

final UPDATE_GAME_PROFILE_MUTATION_DOCUMENT_OPERATION_NAME =
    'updateGameProfile';
final UPDATE_GAME_PROFILE_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'updateGameProfile'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'ffmaxProfileInput')),
        type: NamedTypeNode(
          name: NameNode(value: 'FFMaxProfileInput'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable:
            VariableNode(name: NameNode(value: 'bgmiProfileMetadataInput')),
        type: NamedTypeNode(
          name: NameNode(value: 'BgmiProfileInput'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'eSports')),
        type: NamedTypeNode(
          name: NameNode(value: 'ESports'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'updateProfile'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'ffMaxProfile'),
                value: VariableNode(name: NameNode(value: 'ffmaxProfileInput')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'bgmiProfile'),
                value: VariableNode(
                    name: NameNode(value: 'bgmiProfileMetadataInput')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'eSport'),
                value: VariableNode(name: NameNode(value: 'eSports')),
              ),
            ]),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Profile'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Profile'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Profile'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BgmiProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'bgmiGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'bgmiLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxProfileMetadata'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'levels'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'ffMaxGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'level'),
                    alias: NameNode(value: 'ffMaxLevel'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              )
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'profileId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class UpdateGameProfileMutation extends GraphQLQuery<UpdateGameProfile$Mutation,
    UpdateGameProfileArguments> {
  UpdateGameProfileMutation({required this.variables});

  @override
  final DocumentNode document = UPDATE_GAME_PROFILE_MUTATION_DOCUMENT;

  @override
  final String operationName =
      UPDATE_GAME_PROFILE_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final UpdateGameProfileArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  UpdateGameProfile$Mutation parse(Map<String, dynamic> json) =>
      UpdateGameProfile$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class SubmitBGMIGameArguments extends JsonSerializable with EquatableMixin {
  SubmitBGMIGameArguments({
    required this.finalTier,
    required this.initialTier,
    required this.kills,
    required this.playedAt,
    required this.rank,
    this.recording,
    required this.group,
    required this.map,
    this.teamRank,
    this.squadScoring,
  });

  @override
  factory SubmitBGMIGameArguments.fromJson(Map<String, dynamic> json) =>
      _$SubmitBGMIGameArgumentsFromJson(json);

  @JsonKey(unknownEnumValue: BgmiLevels.artemisUnknown)
  late BgmiLevels finalTier;

  @JsonKey(unknownEnumValue: BgmiLevels.artemisUnknown)
  late BgmiLevels initialTier;

  late int kills;

  @JsonKey(
      fromJson: fromGraphQLDateTimeToDartDateTime,
      toJson: fromDartDateTimeToGraphQLDateTime)
  late DateTime playedAt;

  late int rank;

  final String? recording;

  @JsonKey(unknownEnumValue: BgmiGroups.artemisUnknown)
  late BgmiGroups group;

  @JsonKey(unknownEnumValue: BgmiMaps.artemisUnknown)
  late BgmiMaps map;

  final int? teamRank;

  final List<SquadMemberGameInfo>? squadScoring;

  @override
  List<Object?> get props => [
        finalTier,
        initialTier,
        kills,
        playedAt,
        rank,
        recording,
        group,
        map,
        teamRank,
        squadScoring
      ];
  @override
  Map<String, dynamic> toJson() => _$SubmitBGMIGameArgumentsToJson(this);
}

final SUBMIT_B_G_M_I_GAME_MUTATION_DOCUMENT_OPERATION_NAME = 'submitBGMIGame';
final SUBMIT_B_G_M_I_GAME_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'submitBGMIGame'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'finalTier')),
        type: NamedTypeNode(
          name: NameNode(value: 'BgmiLevels'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'initialTier')),
        type: NamedTypeNode(
          name: NameNode(value: 'BgmiLevels'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'kills')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'playedAt')),
        type: NamedTypeNode(
          name: NameNode(value: 'DateTime'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'rank')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'recording')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'group')),
        type: NamedTypeNode(
          name: NameNode(value: 'BgmiGroups'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'map')),
        type: NamedTypeNode(
          name: NameNode(value: 'BgmiMaps'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'teamRank')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'squadScoring')),
        type: ListTypeNode(
          type: NamedTypeNode(
            name: NameNode(value: 'SquadMemberGameInfo'),
            isNonNull: true,
          ),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'submitBgmiGame'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'finalTier'),
                value: VariableNode(name: NameNode(value: 'finalTier')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'initialTier'),
                value: VariableNode(name: NameNode(value: 'initialTier')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'kills'),
                value: VariableNode(name: NameNode(value: 'kills')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'playedAt'),
                value: VariableNode(name: NameNode(value: 'playedAt')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'rank'),
                value: VariableNode(name: NameNode(value: 'rank')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'recording'),
                value: VariableNode(name: NameNode(value: 'recording')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'group'),
                value: VariableNode(name: NameNode(value: 'group')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'map'),
                value: VariableNode(name: NameNode(value: 'map')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'teamRank'),
                value: VariableNode(name: NameNode(value: 'teamRank')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'squadScoring'),
                value: VariableNode(name: NameNode(value: 'squadScoring')),
              ),
            ]),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'GameResponse'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'GameResponse'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SubmitGameResponse'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'game'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'id'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'eSport'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'rank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'score'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'userId'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'teamRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'metadata'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: '__typename'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              InlineFragmentNode(
                typeCondition: TypeConditionNode(
                    on: NamedTypeNode(
                  name: NameNode(value: 'BgmiMetadata'),
                  isNonNull: false,
                )),
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'initialTier'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'finalTier'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'kills'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'bgmiGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'map'),
                    alias: NameNode(value: 'bgmiMap'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              ),
              InlineFragmentNode(
                typeCondition: TypeConditionNode(
                    on: NamedTypeNode(
                  name: NameNode(value: 'FfMaxMetadata'),
                  isNonNull: false,
                )),
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'initialTier'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'finalTier'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'kills'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'ffGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'map'),
                    alias: NameNode(value: 'ffMap'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              ),
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'tournaments'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'isAdded'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'isTop'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'exclusionReason'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'tournament'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'id'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'name'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'submissionState'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'userId'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'hasSubmitted'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'user'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'LeaderboardUser'),
                    directives: [],
                  )
                ]),
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'squadScores'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'kills'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'user'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'username'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'image'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              ),
            ]),
          ),
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class SubmitBGMIGameMutation
    extends GraphQLQuery<SubmitBGMIGame$Mutation, SubmitBGMIGameArguments> {
  SubmitBGMIGameMutation({required this.variables});

  @override
  final DocumentNode document = SUBMIT_B_G_M_I_GAME_MUTATION_DOCUMENT;

  @override
  final String operationName =
      SUBMIT_B_G_M_I_GAME_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final SubmitBGMIGameArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  SubmitBGMIGame$Mutation parse(Map<String, dynamic> json) =>
      SubmitBGMIGame$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class SubmitFFMaxGameArguments extends JsonSerializable with EquatableMixin {
  SubmitFFMaxGameArguments({
    required this.finalTier,
    required this.initialTier,
    required this.kills,
    required this.playedAt,
    required this.rank,
    this.recording,
    required this.group,
    required this.map,
    this.teamRank,
    this.squadScoring,
  });

  @override
  factory SubmitFFMaxGameArguments.fromJson(Map<String, dynamic> json) =>
      _$SubmitFFMaxGameArgumentsFromJson(json);

  @JsonKey(unknownEnumValue: FfMaxLevels.artemisUnknown)
  late FfMaxLevels finalTier;

  @JsonKey(unknownEnumValue: FfMaxLevels.artemisUnknown)
  late FfMaxLevels initialTier;

  late int kills;

  @JsonKey(
      fromJson: fromGraphQLDateTimeToDartDateTime,
      toJson: fromDartDateTimeToGraphQLDateTime)
  late DateTime playedAt;

  late int rank;

  final String? recording;

  @JsonKey(unknownEnumValue: FfMaxGroups.artemisUnknown)
  late FfMaxGroups group;

  @JsonKey(unknownEnumValue: FfMaxMaps.artemisUnknown)
  late FfMaxMaps map;

  final int? teamRank;

  final List<SquadMemberGameInfo>? squadScoring;

  @override
  List<Object?> get props => [
        finalTier,
        initialTier,
        kills,
        playedAt,
        rank,
        recording,
        group,
        map,
        teamRank,
        squadScoring
      ];
  @override
  Map<String, dynamic> toJson() => _$SubmitFFMaxGameArgumentsToJson(this);
}

final SUBMIT_F_F_MAX_GAME_MUTATION_DOCUMENT_OPERATION_NAME = 'submitFFMaxGame';
final SUBMIT_F_F_MAX_GAME_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'submitFFMaxGame'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'finalTier')),
        type: NamedTypeNode(
          name: NameNode(value: 'FfMaxLevels'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'initialTier')),
        type: NamedTypeNode(
          name: NameNode(value: 'FfMaxLevels'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'kills')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'playedAt')),
        type: NamedTypeNode(
          name: NameNode(value: 'DateTime'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'rank')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'recording')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'group')),
        type: NamedTypeNode(
          name: NameNode(value: 'FfMaxGroups'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'map')),
        type: NamedTypeNode(
          name: NameNode(value: 'FfMaxMaps'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'teamRank')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'squadScoring')),
        type: ListTypeNode(
          type: NamedTypeNode(
            name: NameNode(value: 'SquadMemberGameInfo'),
            isNonNull: true,
          ),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'submitFfMaxGame'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'finalTier'),
                value: VariableNode(name: NameNode(value: 'finalTier')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'initialTier'),
                value: VariableNode(name: NameNode(value: 'initialTier')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'kills'),
                value: VariableNode(name: NameNode(value: 'kills')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'playedAt'),
                value: VariableNode(name: NameNode(value: 'playedAt')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'rank'),
                value: VariableNode(name: NameNode(value: 'rank')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'recording'),
                value: VariableNode(name: NameNode(value: 'recording')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'group'),
                value: VariableNode(name: NameNode(value: 'group')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'map'),
                value: VariableNode(name: NameNode(value: 'map')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'teamRank'),
                value: VariableNode(name: NameNode(value: 'teamRank')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'squadScoring'),
                value: VariableNode(name: NameNode(value: 'squadScoring')),
              ),
            ]),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'GameResponse'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'GameResponse'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SubmitGameResponse'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'game'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'id'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'eSport'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'rank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'score'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'userId'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'teamRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'metadata'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: '__typename'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              InlineFragmentNode(
                typeCondition: TypeConditionNode(
                    on: NamedTypeNode(
                  name: NameNode(value: 'BgmiMetadata'),
                  isNonNull: false,
                )),
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'initialTier'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'finalTier'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'kills'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'bgmiGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'map'),
                    alias: NameNode(value: 'bgmiMap'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              ),
              InlineFragmentNode(
                typeCondition: TypeConditionNode(
                    on: NamedTypeNode(
                  name: NameNode(value: 'FfMaxMetadata'),
                  isNonNull: false,
                )),
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'initialTier'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'finalTier'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'kills'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'group'),
                    alias: NameNode(value: 'ffGroup'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'map'),
                    alias: NameNode(value: 'ffMap'),
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              ),
            ]),
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'tournaments'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'isAdded'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'isTop'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'exclusionReason'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'tournament'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'id'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'name'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'submissionState'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'userId'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'hasSubmitted'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'user'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FragmentSpreadNode(
                    name: NameNode(value: 'LeaderboardUser'),
                    directives: [],
                  )
                ]),
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'squadScores'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'kills'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'user'),
                alias: null,
                arguments: [],
                directives: [],
                selectionSet: SelectionSetNode(selections: [
                  FieldNode(
                    name: NameNode(value: 'username'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                  FieldNode(
                    name: NameNode(value: 'image'),
                    alias: null,
                    arguments: [],
                    directives: [],
                    selectionSet: null,
                  ),
                ]),
              ),
            ]),
          ),
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class SubmitFFMaxGameMutation
    extends GraphQLQuery<SubmitFFMaxGame$Mutation, SubmitFFMaxGameArguments> {
  SubmitFFMaxGameMutation({required this.variables});

  @override
  final DocumentNode document = SUBMIT_F_F_MAX_GAME_MUTATION_DOCUMENT;

  @override
  final String operationName =
      SUBMIT_F_F_MAX_GAME_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final SubmitFFMaxGameArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  SubmitFFMaxGame$Mutation parse(Map<String, dynamic> json) =>
      SubmitFFMaxGame$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class SubmitPreferencesArguments extends JsonSerializable with EquatableMixin {
  SubmitPreferencesArguments({required this.preferencesInput});

  @override
  factory SubmitPreferencesArguments.fromJson(Map<String, dynamic> json) =>
      _$SubmitPreferencesArgumentsFromJson(json);

  late PreferencesInput preferencesInput;

  @override
  List<Object?> get props => [preferencesInput];
  @override
  Map<String, dynamic> toJson() => _$SubmitPreferencesArgumentsToJson(this);
}

final SUBMIT_PREFERENCES_MUTATION_DOCUMENT_OPERATION_NAME = 'submitPreferences';
final SUBMIT_PREFERENCES_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'submitPreferences'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'preferencesInput')),
        type: NamedTypeNode(
          name: NameNode(value: 'PreferencesInput'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'submitPreferences'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: VariableNode(name: NameNode(value: 'preferencesInput')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'UserPreference'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'UserPreference'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'UserPreference'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'playingReason'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'timeOfDay'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'roles'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'timeOfWeek'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class SubmitPreferencesMutation extends GraphQLQuery<SubmitPreferences$Mutation,
    SubmitPreferencesArguments> {
  SubmitPreferencesMutation({required this.variables});

  @override
  final DocumentNode document = SUBMIT_PREFERENCES_MUTATION_DOCUMENT;

  @override
  final String operationName =
      SUBMIT_PREFERENCES_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final SubmitPreferencesArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  SubmitPreferences$Mutation parse(Map<String, dynamic> json) =>
      SubmitPreferences$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class JoinTournamentArguments extends JsonSerializable with EquatableMixin {
  JoinTournamentArguments({
    required this.tournamentId,
    this.phone,
  });

  @override
  factory JoinTournamentArguments.fromJson(Map<String, dynamic> json) =>
      _$JoinTournamentArgumentsFromJson(json);

  late int tournamentId;

  final String? phone;

  @override
  List<Object?> get props => [tournamentId, phone];
  @override
  Map<String, dynamic> toJson() => _$JoinTournamentArgumentsToJson(this);
}

final JOIN_TOURNAMENT_MUTATION_DOCUMENT_OPERATION_NAME = 'joinTournament';
final JOIN_TOURNAMENT_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'joinTournament'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'phone')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'joinTournament'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'tournamentId'),
            value: VariableNode(name: NameNode(value: 'tournamentId')),
          ),
          ArgumentNode(
            name: NameNode(value: 'phone'),
            value: VariableNode(name: NameNode(value: 'phone')),
          ),
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'message'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      )
    ]),
  )
]);

class JoinTournamentMutation
    extends GraphQLQuery<JoinTournament$Mutation, JoinTournamentArguments> {
  JoinTournamentMutation({required this.variables});

  @override
  final DocumentNode document = JOIN_TOURNAMENT_MUTATION_DOCUMENT;

  @override
  final String operationName = JOIN_TOURNAMENT_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final JoinTournamentArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  JoinTournament$Mutation parse(Map<String, dynamic> json) =>
      JoinTournament$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class WithdrawArguments extends JsonSerializable with EquatableMixin {
  WithdrawArguments({
    required this.upi,
    required this.amount,
  });

  @override
  factory WithdrawArguments.fromJson(Map<String, dynamic> json) =>
      _$WithdrawArgumentsFromJson(json);

  late String upi;

  late double amount;

  @override
  List<Object?> get props => [upi, amount];
  @override
  Map<String, dynamic> toJson() => _$WithdrawArgumentsToJson(this);
}

final WITHDRAW_MUTATION_DOCUMENT_OPERATION_NAME = 'withdraw';
final WITHDRAW_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'withdraw'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'upi')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'amount')),
        type: NamedTypeNode(
          name: NameNode(value: 'Float'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'withdrawUPI'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'upi'),
            value: VariableNode(name: NameNode(value: 'upi')),
          ),
          ArgumentNode(
            name: NameNode(value: 'amount'),
            value: VariableNode(name: NameNode(value: 'amount')),
          ),
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'success'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'transactionId'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'wallet'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FragmentSpreadNode(
                name: NameNode(value: 'Wallet'),
                directives: [],
              )
            ]),
          ),
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Wallet'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Wallet'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'bonus'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'deposit'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'winning'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class WithdrawMutation
    extends GraphQLQuery<Withdraw$Mutation, WithdrawArguments> {
  WithdrawMutation({required this.variables});

  @override
  final DocumentNode document = WITHDRAW_MUTATION_DOCUMENT;

  @override
  final String operationName = WITHDRAW_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final WithdrawArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  Withdraw$Mutation parse(Map<String, dynamic> json) =>
      Withdraw$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class CreateSquadArguments extends JsonSerializable with EquatableMixin {
  CreateSquadArguments({
    required this.tournamentId,
    required this.squadName,
  });

  @override
  factory CreateSquadArguments.fromJson(Map<String, dynamic> json) =>
      _$CreateSquadArgumentsFromJson(json);

  late int tournamentId;

  late String squadName;

  @override
  List<Object?> get props => [tournamentId, squadName];
  @override
  Map<String, dynamic> toJson() => _$CreateSquadArgumentsToJson(this);
}

final CREATE_SQUAD_MUTATION_DOCUMENT_OPERATION_NAME = 'createSquad';
final CREATE_SQUAD_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'createSquad'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'squadName')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'createSquad'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'name'),
                value: VariableNode(name: NameNode(value: 'squadName')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'tournamentId'),
                value: VariableNode(name: NameNode(value: 'tournamentId')),
              ),
            ]),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class CreateSquadMutation
    extends GraphQLQuery<CreateSquad$Mutation, CreateSquadArguments> {
  CreateSquadMutation({required this.variables});

  @override
  final DocumentNode document = CREATE_SQUAD_MUTATION_DOCUMENT;

  @override
  final String operationName = CREATE_SQUAD_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final CreateSquadArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  CreateSquad$Mutation parse(Map<String, dynamic> json) =>
      CreateSquad$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class JoinSquadArguments extends JsonSerializable with EquatableMixin {
  JoinSquadArguments({
    required this.inviteCode,
    required this.tournamentId,
  });

  @override
  factory JoinSquadArguments.fromJson(Map<String, dynamic> json) =>
      _$JoinSquadArgumentsFromJson(json);

  late String inviteCode;

  late int tournamentId;

  @override
  List<Object?> get props => [inviteCode, tournamentId];
  @override
  Map<String, dynamic> toJson() => _$JoinSquadArgumentsToJson(this);
}

final JOIN_SQUAD_MUTATION_DOCUMENT_OPERATION_NAME = 'joinSquad';
final JOIN_SQUAD_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'joinSquad'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'inviteCode')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'joinSquad'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'inviteCode'),
                value: VariableNode(name: NameNode(value: 'inviteCode')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'tournamentId'),
                value: VariableNode(name: NameNode(value: 'tournamentId')),
              ),
            ]),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class JoinSquadMutation
    extends GraphQLQuery<JoinSquad$Mutation, JoinSquadArguments> {
  JoinSquadMutation({required this.variables});

  @override
  final DocumentNode document = JOIN_SQUAD_MUTATION_DOCUMENT;

  @override
  final String operationName = JOIN_SQUAD_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final JoinSquadArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  JoinSquad$Mutation parse(Map<String, dynamic> json) =>
      JoinSquad$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class UpdateSquadNameArguments extends JsonSerializable with EquatableMixin {
  UpdateSquadNameArguments({
    required this.squadName,
    required this.squadId,
  });

  @override
  factory UpdateSquadNameArguments.fromJson(Map<String, dynamic> json) =>
      _$UpdateSquadNameArgumentsFromJson(json);

  late String squadName;

  late int squadId;

  @override
  List<Object?> get props => [squadName, squadId];
  @override
  Map<String, dynamic> toJson() => _$UpdateSquadNameArgumentsToJson(this);
}

final UPDATE_SQUAD_NAME_MUTATION_DOCUMENT_OPERATION_NAME = 'updateSquadName';
final UPDATE_SQUAD_NAME_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'updateSquadName'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'squadName')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'squadId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'updateSquad'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'id'),
            value: VariableNode(name: NameNode(value: 'squadId')),
          ),
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'name'),
                value: VariableNode(name: NameNode(value: 'squadName')),
              )
            ]),
          ),
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class UpdateSquadNameMutation
    extends GraphQLQuery<UpdateSquadName$Mutation, UpdateSquadNameArguments> {
  UpdateSquadNameMutation({required this.variables});

  @override
  final DocumentNode document = UPDATE_SQUAD_NAME_MUTATION_DOCUMENT;

  @override
  final String operationName =
      UPDATE_SQUAD_NAME_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final UpdateSquadNameArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  UpdateSquadName$Mutation parse(Map<String, dynamic> json) =>
      UpdateSquadName$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class DeleteSquadArguments extends JsonSerializable with EquatableMixin {
  DeleteSquadArguments({required this.squadId});

  @override
  factory DeleteSquadArguments.fromJson(Map<String, dynamic> json) =>
      _$DeleteSquadArgumentsFromJson(json);

  late int squadId;

  @override
  List<Object?> get props => [squadId];
  @override
  Map<String, dynamic> toJson() => _$DeleteSquadArgumentsToJson(this);
}

final DELETE_SQUAD_MUTATION_DOCUMENT_OPERATION_NAME = 'deleteSquad';
final DELETE_SQUAD_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'deleteSquad'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'squadId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'deleteSquad'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'id'),
            value: VariableNode(name: NameNode(value: 'squadId')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'message'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      )
    ]),
  )
]);

class DeleteSquadMutation
    extends GraphQLQuery<DeleteSquad$Mutation, DeleteSquadArguments> {
  DeleteSquadMutation({required this.variables});

  @override
  final DocumentNode document = DELETE_SQUAD_MUTATION_DOCUMENT;

  @override
  final String operationName = DELETE_SQUAD_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final DeleteSquadArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  DeleteSquad$Mutation parse(Map<String, dynamic> json) =>
      DeleteSquad$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class ChangeSquadArguments extends JsonSerializable with EquatableMixin {
  ChangeSquadArguments({
    required this.tournamentId,
    required this.newSquadId,
  });

  @override
  factory ChangeSquadArguments.fromJson(Map<String, dynamic> json) =>
      _$ChangeSquadArgumentsFromJson(json);

  late int tournamentId;

  late int newSquadId;

  @override
  List<Object?> get props => [tournamentId, newSquadId];
  @override
  Map<String, dynamic> toJson() => _$ChangeSquadArgumentsToJson(this);
}

final CHANGE_SQUAD_MUTATION_DOCUMENT_OPERATION_NAME = 'changeSquad';
final CHANGE_SQUAD_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'changeSquad'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'newSquadId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'changeSquad'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: ObjectValueNode(fields: [
              ObjectFieldNode(
                name: NameNode(value: 'tournamentId'),
                value: VariableNode(name: NameNode(value: 'tournamentId')),
              ),
              ObjectFieldNode(
                name: NameNode(value: 'newSquadId'),
                value: VariableNode(name: NameNode(value: 'newSquadId')),
              ),
            ]),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class ChangeSquadMutation
    extends GraphQLQuery<ChangeSquad$Mutation, ChangeSquadArguments> {
  ChangeSquadMutation({required this.variables});

  @override
  final DocumentNode document = CHANGE_SQUAD_MUTATION_DOCUMENT;

  @override
  final String operationName = CHANGE_SQUAD_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final ChangeSquadArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  ChangeSquad$Mutation parse(Map<String, dynamic> json) =>
      ChangeSquad$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class UnlockSquadArguments extends JsonSerializable with EquatableMixin {
  UnlockSquadArguments({required this.inviteCode});

  @override
  factory UnlockSquadArguments.fromJson(Map<String, dynamic> json) =>
      _$UnlockSquadArgumentsFromJson(json);

  late String inviteCode;

  @override
  List<Object?> get props => [inviteCode];
  @override
  Map<String, dynamic> toJson() => _$UnlockSquadArgumentsToJson(this);
}

final UNLOCK_SQUAD_MUTATION_DOCUMENT_OPERATION_NAME = 'unlockSquad';
final UNLOCK_SQUAD_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'unlockSquad'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'inviteCode')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'unlockSquad'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'inviteCode'),
            value: VariableNode(name: NameNode(value: 'inviteCode')),
          )
        ],
        directives: [],
        selectionSet: null,
      )
    ]),
  )
]);

class UnlockSquadMutation
    extends GraphQLQuery<UnlockSquad$Mutation, UnlockSquadArguments> {
  UnlockSquadMutation({required this.variables});

  @override
  final DocumentNode document = UNLOCK_SQUAD_MUTATION_DOCUMENT;

  @override
  final String operationName = UNLOCK_SQUAD_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final UnlockSquadArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  UnlockSquad$Mutation parse(Map<String, dynamic> json) =>
      UnlockSquad$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class SubmitFeedbackArguments extends JsonSerializable with EquatableMixin {
  SubmitFeedbackArguments({required this.input});

  @override
  factory SubmitFeedbackArguments.fromJson(Map<String, dynamic> json) =>
      _$SubmitFeedbackArgumentsFromJson(json);

  late SubmitFeedbackInput input;

  @override
  List<Object?> get props => [input];
  @override
  Map<String, dynamic> toJson() => _$SubmitFeedbackArgumentsToJson(this);
}

final SUBMIT_FEEDBACK_MUTATION_DOCUMENT_OPERATION_NAME = 'submitFeedback';
final SUBMIT_FEEDBACK_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'submitFeedback'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'input')),
        type: NamedTypeNode(
          name: NameNode(value: 'SubmitFeedbackInput'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      )
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'submitFeedback'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'input'),
            value: VariableNode(name: NameNode(value: 'input')),
          )
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'message'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      )
    ]),
  )
]);

class SubmitFeedbackMutation
    extends GraphQLQuery<SubmitFeedback$Mutation, SubmitFeedbackArguments> {
  SubmitFeedbackMutation({required this.variables});

  @override
  final DocumentNode document = SUBMIT_FEEDBACK_MUTATION_DOCUMENT;

  @override
  final String operationName = SUBMIT_FEEDBACK_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final SubmitFeedbackArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  SubmitFeedback$Mutation parse(Map<String, dynamic> json) =>
      SubmitFeedback$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class DepositUPIManualArguments extends JsonSerializable with EquatableMixin {
  DepositUPIManualArguments({
    required this.upi,
    required this.amount,
  });

  @override
  factory DepositUPIManualArguments.fromJson(Map<String, dynamic> json) =>
      _$DepositUPIManualArgumentsFromJson(json);

  late String upi;

  late double amount;

  @override
  List<Object?> get props => [upi, amount];
  @override
  Map<String, dynamic> toJson() => _$DepositUPIManualArgumentsToJson(this);
}

final DEPOSIT_U_P_I_MANUAL_MUTATION_DOCUMENT_OPERATION_NAME =
    'depositUPIManual';
final DEPOSIT_U_P_I_MANUAL_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'depositUPIManual'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'upi')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'amount')),
        type: NamedTypeNode(
          name: NameNode(value: 'Float'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'depositUPIManual'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'upi'),
            value: VariableNode(name: NameNode(value: 'upi')),
          ),
          ArgumentNode(
            name: NameNode(value: 'amount'),
            value: VariableNode(name: NameNode(value: 'amount')),
          ),
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'wallet'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FragmentSpreadNode(
                name: NameNode(value: 'Wallet'),
                directives: [],
              )
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'success'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'transactionId'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Wallet'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Wallet'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'bonus'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'deposit'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'winning'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class DepositUPIManualMutation
    extends GraphQLQuery<DepositUPIManual$Mutation, DepositUPIManualArguments> {
  DepositUPIManualMutation({required this.variables});

  @override
  final DocumentNode document = DEPOSIT_U_P_I_MANUAL_MUTATION_DOCUMENT;

  @override
  final String operationName =
      DEPOSIT_U_P_I_MANUAL_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final DepositUPIManualArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  DepositUPIManual$Mutation parse(Map<String, dynamic> json) =>
      DepositUPIManual$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class PaymentCreationArguments extends JsonSerializable with EquatableMixin {
  PaymentCreationArguments({
    required this.amount,
    required this.targetApp,
  });

  @override
  factory PaymentCreationArguments.fromJson(Map<String, dynamic> json) =>
      _$PaymentCreationArgumentsFromJson(json);

  late int amount;

  late String targetApp;

  @override
  List<Object?> get props => [amount, targetApp];
  @override
  Map<String, dynamic> toJson() => _$PaymentCreationArgumentsToJson(this);
}

final PAYMENT_CREATION_MUTATION_DOCUMENT_OPERATION_NAME = 'PaymentCreation';
final PAYMENT_CREATION_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'PaymentCreation'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'amount')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'targetApp')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'paymentCreation'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'amount'),
            value: VariableNode(name: NameNode(value: 'amount')),
          ),
          ArgumentNode(
            name: NameNode(value: 'targetApp'),
            value: VariableNode(name: NameNode(value: 'targetApp')),
          ),
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'intentUrl'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'ledgerId'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'success'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      )
    ]),
  )
]);

class PaymentCreationMutation
    extends GraphQLQuery<PaymentCreation$Mutation, PaymentCreationArguments> {
  PaymentCreationMutation({required this.variables});

  @override
  final DocumentNode document = PAYMENT_CREATION_MUTATION_DOCUMENT;

  @override
  final String operationName =
      PAYMENT_CREATION_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final PaymentCreationArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  PaymentCreation$Mutation parse(Map<String, dynamic> json) =>
      PaymentCreation$Mutation.fromJson(json);
}

@JsonSerializable(explicitToJson: true)
class EnterTournamentArguments extends JsonSerializable with EquatableMixin {
  EnterTournamentArguments({
    this.squadInfo,
    required this.tournamentId,
    this.phone,
    this.joinCode,
  });

  @override
  factory EnterTournamentArguments.fromJson(Map<String, dynamic> json) =>
      _$EnterTournamentArgumentsFromJson(json);

  final TournamentJoiningSquadInfo? squadInfo;

  late int tournamentId;

  final String? phone;

  final String? joinCode;

  @override
  List<Object?> get props => [squadInfo, tournamentId, phone, joinCode];
  @override
  Map<String, dynamic> toJson() => _$EnterTournamentArgumentsToJson(this);
}

final ENTER_TOURNAMENT_MUTATION_DOCUMENT_OPERATION_NAME = 'EnterTournament';
final ENTER_TOURNAMENT_MUTATION_DOCUMENT = DocumentNode(definitions: [
  OperationDefinitionNode(
    type: OperationType.mutation,
    name: NameNode(value: 'EnterTournament'),
    variableDefinitions: [
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'squadInfo')),
        type: NamedTypeNode(
          name: NameNode(value: 'TournamentJoiningSquadInfo'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'tournamentId')),
        type: NamedTypeNode(
          name: NameNode(value: 'Int'),
          isNonNull: true,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'phone')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
      VariableDefinitionNode(
        variable: VariableNode(name: NameNode(value: 'joinCode')),
        type: NamedTypeNode(
          name: NameNode(value: 'String'),
          isNonNull: false,
        ),
        defaultValue: DefaultValueNode(value: null),
        directives: [],
      ),
    ],
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'enterTournament'),
        alias: null,
        arguments: [
          ArgumentNode(
            name: NameNode(value: 'squadInfo'),
            value: VariableNode(name: NameNode(value: 'squadInfo')),
          ),
          ArgumentNode(
            name: NameNode(value: 'tournamentId'),
            value: VariableNode(name: NameNode(value: 'tournamentId')),
          ),
          ArgumentNode(
            name: NameNode(value: 'phone'),
            value: VariableNode(name: NameNode(value: 'phone')),
          ),
          ArgumentNode(
            name: NameNode(value: 'joinCode'),
            value: VariableNode(name: NameNode(value: 'joinCode')),
          ),
        ],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'UserTournament'),
            directives: [],
          )
        ]),
      )
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'UserTournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'UserTournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'joinedAt'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'rank'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'score'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournament'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Tournament'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'squad'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'Squad'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchUser'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Tournament'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Tournament'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'eSport'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxPrize'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'userCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'gameCount'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'fee'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'matchType'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinBy'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'joinCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'qualifiers'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'rule'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'winningDistribution'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'startRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'endRank'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'value'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'matches'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatch'),
            directives: [],
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'rules'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: '__typename'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'BGMIRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'bgmiMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'bgmiMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'bgmiAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'bgmiAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'bgmiAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          InlineFragmentNode(
            typeCondition: TypeConditionNode(
                on: NamedTypeNode(
              name: NameNode(value: 'FFMaxRules'),
              isNonNull: false,
            )),
            directives: [],
            selectionSet: SelectionSetNode(selections: [
              FieldNode(
                name: NameNode(value: 'maxLevel'),
                alias: NameNode(value: 'ffMaxLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'minLevel'),
                alias: NameNode(value: 'ffMinLevel'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedModes'),
                alias: NameNode(value: 'ffAllowedModes'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedMaps'),
                alias: NameNode(value: 'ffAllowedMaps'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
              FieldNode(
                name: NameNode(value: 'allowedGroups'),
                alias: NameNode(value: 'ffAllowedGroups'),
                arguments: [],
                directives: [],
                selectionSet: null,
              ),
            ]),
          ),
          FieldNode(
            name: NameNode(value: 'minUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxUsers'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
          FieldNode(
            name: NameNode(value: 'maxTeams'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          ),
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatch'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatch'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'tournamentId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'endTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'startTime'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'maxParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'minParticipants'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'metadata'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'TournamentMatchMetadata'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchMetadata'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchMetadata'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'roomId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'roomPassword'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'Squad'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'Squad'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'inviteCode'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'members'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'SquadMember'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'SquadMember'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'SquadMember'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'isReady'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'status'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'user'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FragmentSpreadNode(
            name: NameNode(value: 'LeaderboardUser'),
            directives: [],
          )
        ]),
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'LeaderboardUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'User'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'name'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'phone'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'image'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'username'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
  FragmentDefinitionNode(
    name: NameNode(value: 'TournamentMatchUser'),
    typeCondition: TypeConditionNode(
        on: NamedTypeNode(
      name: NameNode(value: 'TournamentMatchUser'),
      isNonNull: false,
    )),
    directives: [],
    selectionSet: SelectionSetNode(selections: [
      FieldNode(
        name: NameNode(value: 'slotInfo'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: SelectionSetNode(selections: [
          FieldNode(
            name: NameNode(value: 'teamNumber'),
            alias: null,
            arguments: [],
            directives: [],
            selectionSet: null,
          )
        ]),
      ),
      FieldNode(
        name: NameNode(value: 'id'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentMatchId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'tournamentUserId'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'notJoined'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
      FieldNode(
        name: NameNode(value: 'sos'),
        alias: null,
        arguments: [],
        directives: [],
        selectionSet: null,
      ),
    ]),
  ),
]);

class EnterTournamentMutation
    extends GraphQLQuery<EnterTournament$Mutation, EnterTournamentArguments> {
  EnterTournamentMutation({required this.variables});

  @override
  final DocumentNode document = ENTER_TOURNAMENT_MUTATION_DOCUMENT;

  @override
  final String operationName =
      ENTER_TOURNAMENT_MUTATION_DOCUMENT_OPERATION_NAME;

  @override
  final EnterTournamentArguments variables;

  @override
  List<Object?> get props => [document, operationName, variables];
  @override
  EnterTournament$Mutation parse(Map<String, dynamic> json) =>
      EnterTournament$Mutation.fromJson(json);
}
