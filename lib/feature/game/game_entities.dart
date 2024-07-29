import 'dart:convert';

import 'package:gamerboard/graphql/query.dart';

////Created by saurabh.lahoti on 19/12/21

class Game {
  String? rank;
  String? initialTier;
  String? finalTier;
  bool? teamRank;
  String? map;
  String? group;
  int? kills;
  int? startTimeStamp;
  String? gameProfileId;
  String? gameProfileUsername;
  int? endTimeStamp;
  List<SquadMemberGameInfo>? squadScoring;
  int? meta;

  Game(
      {this.rank,
      this.initialTier,
      this.finalTier,
      this.teamRank,
      this.kills,
      this.gameProfileId,
      this.gameProfileUsername,
      this.startTimeStamp,
      this.endTimeStamp,
      this.map,
      this.group,
      this.squadScoring,
      this.meta});

  factory Game.fromMap(dynamic map) {
    var temp;
    var gameInfo = jsonDecode(map['gameInfo']);
    return Game(
        rank: map['rank']?.toString(),
        initialTier: map['initialTier']?.toString(),
        finalTier: map['finalTier']?.toString(),
        teamRank: map['teamRank'] != null &&
            map['teamRank'].toString().toLowerCase() == "true",
        gameProfileId: map['userId']?.toString(),
        map: gameInfo?['mode']?.toString(),
        group: gameInfo?['group']?.toString(),
        kills: null == (temp = map['kills'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        startTimeStamp: null == (temp = map['startTimeStamp'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        endTimeStamp: null == (temp = map['endTimeStamp'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        squadScoring: map['squadScoring'] != null
            ? (jsonDecode(map['squadScoring']) as List)
                .map((e) => SquadMemberGameInfo.fromJson(e))
                .toList()
            : null,
        meta: null == (temp = map['meta'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)));
  }

  DateTime get playedAt => DateTime.fromMillisecondsSinceEpoch(
      startTimeStamp ?? DateTime.now().millisecondsSinceEpoch,
      isUtc: true);
}

class LeaderboardItemMapper {
  String? name;
  int? score;
  int? behindBy;
  TopGames? topGames;
  int? rank;
  int? matchesPlayed;
  int? myId;
  bool? isDisqualified;
  String? myPhoto;

  LeaderboardItemMapper(
      {this.name,
      this.score,
      this.behindBy,
      this.topGames,
      this.rank,
      this.matchesPlayed,
      this.isDisqualified,
      this.myId,
      this.myPhoto});

  LeaderboardItemMapper.fromSoloLeaderboard(
      LeaderboardRankMixin leaderboardRankMixin) {
    name = leaderboardRankMixin.user.username;
    score = leaderboardRankMixin.score;
    behindBy = leaderboardRankMixin.behindBy;
    isDisqualified = leaderboardRankMixin.isDisqualified;
    rank = leaderboardRankMixin.rank;
    matchesPlayed = leaderboardRankMixin.details?.gamesPlayed;
    myId = leaderboardRankMixin.user.id;
    myPhoto = leaderboardRankMixin.user.image;
    topGames = TopGames.forSoloGame(leaderboardRankMixin.details?.top);
  }

  LeaderboardItemMapper.fromTeamLeaderboard(
      SquadLeaderboardMixin squadLeaderboardMixin) {
    name = squadLeaderboardMixin.squad?.name;
    score = squadLeaderboardMixin.score;
    behindBy = squadLeaderboardMixin.behindBy;
    rank = squadLeaderboardMixin.rank;
    isDisqualified = squadLeaderboardMixin.squad?.isDisqualified == true;
    myId = squadLeaderboardMixin.squad?.id;
    matchesPlayed = squadLeaderboardMixin.details?.gamesPlayed;
    topGames = TopGames.forSquadGame(squadLeaderboardMixin.details?.top);
  }

  Map<String, dynamic> toMap() {
    return {
      'name': name,
      'score': score,
      'behindBy': behindBy,
      'topGames': topGames?.toMap(),
      'rank': rank,
      'matchesPlayed': matchesPlayed,
      'myId': myId,
      'myPhoto': myPhoto
    };
  }

  factory LeaderboardItemMapper.fromMap(dynamic map) {
    var temp;
    return LeaderboardItemMapper(
        name: map['name']?.toString(),
        score: null == (temp = map['score'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        behindBy: null == (temp = map['behindBy'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        topGames: TopGames.fromMap(map['topGames']),
        rank: null == (temp = map['rank'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        matchesPlayed: null == (temp = map['matchesPlayed'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        myId: null == (temp = map['myId'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        myPhoto: map['myPhoto']?.toString());
  }
}

class TopGames {
  List<GameResult>? gameResults;

  TopGames({this.gameResults});

  TopGames.forSoloGame(
      List<LeaderboardRankMixin$LeaderboardInfo$Game?>? games) {
    gameResults = [];
    games?.forEach((element) {
      gameResults?.add(GameResult.forSoloGame(element));
    });
  }

  Map<String, dynamic> toMap() {
    return {
      'gameResults': gameResults?.map((map) => map.toMap()).toList() ?? [],
    };
  }

  factory TopGames.fromMap(dynamic map) {
    var temp;
    return TopGames(
        gameResults: null == (temp = map['gameResults'])
            ? []
            : (temp is List
                ? temp.map((map) => GameResult.fromMap(map)).toList()
                : []));
  }

  TopGames.forSquadGame(
      List<List<SquadLeaderboardMixin$SquadLeaderboardInfo$Game?>?>? games) {
    gameResults = [];
    games?.forEach((element) {
      if (element?.isNotEmpty ?? false)
        gameResults?.add(GameResult.forSquadGame(element!.first));
    });
  }
}

class GameResult {
  int? rank;
  double? score;
  GameResult({this.rank, this.score});

  GameResult.forSoloGame(LeaderboardRankMixin$LeaderboardInfo$Game? game) {
    rank = game?.rank;
    score = game?.score;
  }

  GameResult.forSquadGame(
      SquadLeaderboardMixin$SquadLeaderboardInfo$Game? game) {
    rank = game?.teamRank;
    score = game?.score;
  }

  factory GameResult.fromMap(dynamic map) {
    var temp;
    return GameResult(
        rank: null == (temp = map['rank'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        score: null == (temp = map['score'])
            ? null
            : (temp is num ? temp.toDouble() : double.tryParse(temp)));
  }

  Map<String, dynamic> toMap() {
    return {'rank': rank, 'score': score};
  }
}
