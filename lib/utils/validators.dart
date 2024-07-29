import 'package:flutter/services.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:intl/intl.dart';

////Created by saurabh.lahoti on 14/12/21

class FieldValidators {
  static RegExp regExpUPI =
      RegExp(r'[a-zA-Z0-9\.\-]{2,256}\@[a-zA-Z][a-zA-Z]{2,64}');

  static String? validatorGamertag(String? value) {
    if (value == null || value.isEmpty)
      return "Username is mandatory";
    else if (value.length < 4)
      return "Username should be minimum 4 characters";
    else if (value.contains(" "))
      return "Username should not contain space or (*?&)";
    return null;
  }

  static String? validateCode(String? value, {int codeLength = 6}) {
    if (value == null || value.isEmpty)
      return "Please provide invite code";
    else if (value.length != codeLength)
      return "Code should be of $codeLength characters";
    else if (value.contains(" ")) return "Invalid invite code";
    return null;
  }

  static String? validateUPI(String value) {
    return regExpUPI.stringMatch(value) == value
        ? null
        : "Enter a valid UPI Id";
  }

  static String? validatorFName(String? value) {
    if (value == null || value.isEmpty) return "First name is mandatory";
    return null;
  }

  static String? validatorLName(String? value) {
    if (value == null || value.isEmpty) return "Last name is mandatory";
    return null;
  }

  static String? validatePhone(String? value) {
    String pattern = r'(^(?:[+0]9)?[0-9]{10,12}$)';
    RegExp regExp = new RegExp(pattern);
    if (value == null) {
      return "Mobile number is mandatory";
    } else if (value.length != 10 || !regExp.hasMatch(value)) {
      return "Invalid mobile number";
    } else
      return null;
  }

  static String? validateDOB(String? value) {
    if (value == null || value.isEmpty) {
      return "Date of birth required";
    } else
      return null;
  }

  static bool validateTeamName(String? val) {
    return !(val == null || val.isEmpty || val.contains(" "));
  }

  static bool validateJoinCode(String? val) {
    return !(val == null || val.isEmpty || val.contains(" ") || val.length != Constants.INVITE_CODE);
  }
}

class AppDateFormats {
  static DateFormat dobFormat = DateFormat('dd / MM / yyyy');
}

String? canJoinTournament(UserTournamentMixin tournamentMixin,
    UserMixin? userMixin, ESports currentGame , bool isHomeCard ) {
  String? error;
  var currentGameProfile = userMixin?.getCurrentGameProfile(currentGame);

  if (userMixin?.profiles != null && userMixin?.profiles!.isNotEmpty == true && currentGameProfile != null) {
    if (tournamentMixin.tournament.rules
        is TournamentMixin$TournamentRules$BGMIRules) {
      final rules = tournamentMixin.tournament.rules
          as TournamentMixin$TournamentRules$BGMIRules;
      final userGameLevel = userMixin?.getGameLevelFromMetaData(
          rules.bgmiAllowedGroups.group(), currentGameProfile!);
      error = (userGameLevel != null &&
          userGameLevel.index >= rules.bgmiMinLevel.index &&
          userGameLevel.index <= rules.bgmiMaxLevel.index)
          ? null
          : "Tier isn't eligible for this tournament";
    } else {
      final rules = tournamentMixin.tournament.rules
          as TournamentMixin$TournamentRules$FFMaxRules;
      final userGameLevel = userMixin?.getGameLevelFromMetaData(
          rules.ffAllowedGroups.group(), currentGameProfile!);
      error = (userGameLevel != null &&
          userGameLevel.index >= rules.ffMinLevel.index &&
          userGameLevel.index <= rules.ffMaxLevel.index)
          ? null
          : "Tier isn't eligible for this tournament";
    }

  } else {
    error = "Your tier for this mode is not available";
  }

  if (DateTime.now().isAfter(tournamentMixin.tournament.endTime)) {
    error = "Tournament has ended";
  }

  if(!isHomeCard)
    {
      if(tournamentMixin.tournament.matchType == MatchType.headToHead && tournamentMixin.tournament.joinBy!.isBefore(DateTime.now()) )
      {
        error = "Tournament started";
      }
    }

  return error;
}

abstract class AppInputFormatters {
  static final spaceFormatter =
      FilteringTextInputFormatter.deny(' ', replacementString: '');
  static final alphaNumericFormatter = FilteringTextInputFormatter.allow(
      RegExp('[a-zA-Z0-9]'),
      replacementString: '');
}
