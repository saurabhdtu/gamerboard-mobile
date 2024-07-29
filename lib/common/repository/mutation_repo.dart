import 'package:artemis/artemis.dart';
import 'package:gamerboard/common/base_respository.dart';
import 'package:gamerboard/feature/login/login_entities.dart';
import 'package:gamerboard/utils/graphql_ext.dart';

import '../../graphql/query.graphql.dart';

////Created by saurabh.lahoti on 21/12/21

class MutationRepository extends BaseRepository {
  static MutationRepository? _instance;

  MutationRepository._();

  static MutationRepository get instance =>
      _instance ??= MutationRepository._();

  Future<GraphQLResponse<RequestOtp$Mutation>> requestOTP(String mobile) async {
    var mutation =
    RequestOtpMutation(variables: RequestOtpArguments(phoneNum: mobile));
    return executeCall(mutation);
  }

  Future<GraphQLResponse<AddUser$Mutation>> createUser(
      UserProfileRequest userProfileRequest, String mobile) async {
    var mutation = AddUserMutation(
        variables: AddUserArguments(
            birthDate: userProfileRequest.dateOfBirth!,
            name: userProfileRequest.name,
            phone: mobile,
            referralCode: userProfileRequest.referralCode,
            userName: userProfileRequest.userName));

    return executeCall(mutation);
  }
  Future<GraphQLResponse<SubmitPreferences$Mutation>> submitUserPreference(
      PreferencesInput preferencesInput,{String? phoneNumber}) {
    var mutation = SubmitPreferencesMutation(
        variables: SubmitPreferencesArguments(preferencesInput: preferencesInput));
    return executeCall(mutation);
  }


  Future<GraphQLResponse<EnterTournament$Mutation>> enterTournament(
      int tournamentId,
      {final String? phoneNumber, final TournamentJoiningSquadInfo? squadInfo, final String? joinCode}) {
    var mutation = EnterTournamentMutation(
        variables: EnterTournamentArguments(
            tournamentId: tournamentId, phone: phoneNumber, squadInfo:squadInfo, joinCode: joinCode));
    return executeCall(mutation);
  }


  Future<GraphQLResponse<Withdraw$Mutation>> withdrawAmount(String upiId,
      double withdraw) {
    var mutation = WithdrawMutation(
        variables: WithdrawArguments(amount: withdraw, upi: upiId));
    return executeCall(mutation);
  }

  Future<GraphQLResponse<DepositUPIManual$Mutation>> depositUPIManual(
      String upiId, double amount) {
    var mutation = DepositUPIManualMutation(
        variables: DepositUPIManualArguments(amount: amount, upi: upiId));
    return executeCall(mutation);
  }

  Future<GraphQLResponse<PaymentCreation$Mutation>> paymentCreation(int amount,
      String targetApp) {
    var mutation = PaymentCreationMutation(
        variables:
        PaymentCreationArguments(amount: amount, targetApp: targetApp));
    return executeCall(mutation);
  }

  Future<GraphQLResponse<dynamic>> updateGameProfile(
      Map<String, dynamic> levelGroupMap, ESports eSports,
      {String? gameProfileId, String? gameProfileUsername}) async {
    var mutation;
    if (eSports == ESports.freefiremax) {
      FfMaxGroups ffgroup =
      (levelGroupMap["group"] as GameTeamGroup).toFreeFireGroup();
      mutation = UpdateGameProfileMutation(
          variables: UpdateGameProfileArguments(
            ffmaxProfileInput: FFMaxProfileInput(
                metadata: FFMaxProfileMetadataInput(levels: [
                  FFMaxProfilemetadataLevelInput(
                      level: levelGroupMap["level"], group: ffgroup)
                ]),
                profileId: gameProfileId,
                username: gameProfileUsername),
            eSports: ESports.freefiremax,
          ));
    } else {
      BgmiGroups bgmiGroups =
      (levelGroupMap["group"] as GameTeamGroup).toBGMIGroup();
      mutation = UpdateGameProfileMutation(
          variables: UpdateGameProfileArguments(
            bgmiProfileMetadataInput: BgmiProfileInput(
                metadata: BgmiProfileMetadataInput(levels: [
                  BgmiProfilemetadataLevelInput(
                      level: levelGroupMap["level"], group: bgmiGroups)
                ]),
                profileId: gameProfileId,
                username: gameProfileUsername),
            eSports: ESports.bgmi,
          ));
    }
    return executeCall(mutation);
  }

  Future<GraphQLResponse<dynamic>> createGameProfile(
      Map<String, dynamic> levelGroupMap, ESports eSports,
      {String? gameProfileId, String? gameProfileUsername}) {
    var mutation;
    if (eSports == ESports.freefiremax) {
      FfMaxGroups ffgroup =
      (levelGroupMap["group"] as GameTeamGroup).toFreeFireGroup();
      mutation = CreateGameProfileMutation(
          variables: CreateGameProfileArguments(
              ffmaxProfileInput: FFMaxProfileInput(
                  metadata: FFMaxProfileMetadataInput(levels: [
                    FFMaxProfilemetadataLevelInput(
                        level: levelGroupMap["level"], group: ffgroup)
                  ]),
                  profileId: gameProfileId,
                  username: gameProfileUsername),
              eSports: ESports.freefiremax));
    } else {
      BgmiGroups bgmiGroup =
      (levelGroupMap["group"] as GameTeamGroup).toBGMIGroup();
      mutation = CreateGameProfileMutation(
          variables: CreateGameProfileArguments(
              bgmiProfileMetadataInput: BgmiProfileInput(
                  metadata: BgmiProfileMetadataInput(levels: [
                    BgmiProfilemetadataLevelInput(
                        level: levelGroupMap["level"], group: bgmiGroup)
                  ]),
                  profileId: gameProfileId,
                  username: gameProfileUsername),
              eSports: ESports.bgmi));
      return executeCall(mutation);
    }
    return executeCall(mutation);
  }

  Future<GraphQLResponse<UpdateSquadName$Mutation>> updateSquad(int squadId,
      String squadName) {
    var mutation = UpdateSquadNameMutation(
        variables:
        UpdateSquadNameArguments(squadId: squadId, squadName: squadName));
    return executeCall(mutation);
  }

  Future<GraphQLResponse<JoinSquad$Mutation>> joinSquad(int tournamentId,
      String inviteCode) {
    var mutation = JoinSquadMutation(
        variables: JoinSquadArguments(
            tournamentId: tournamentId, inviteCode: inviteCode));
    return executeCall(mutation);
  }

  void deleteSquad(int squadId) {
    var mutation =
    DeleteSquadMutation(variables: DeleteSquadArguments(squadId: squadId));
    executeCall(mutation);
  }

  Future<GraphQLResponse<ChangeSquad$Mutation>> changeSquad(int newSquadId,
      int tournamentId) {
    var mutation = ChangeSquadMutation(
        variables: ChangeSquadArguments(
            tournamentId: tournamentId, newSquadId: newSquadId));
    return executeCall(mutation);
  }

  Future<GraphQLResponse<UnlockSquad$Mutation>> validateSquadCreationPermission(
      String inviteCode) {
    var mutation = UnlockSquadMutation(
        variables: UnlockSquadArguments(inviteCode: inviteCode));
    return executeCall(mutation);
  }
}
