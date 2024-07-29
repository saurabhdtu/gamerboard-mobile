import 'dart:convert';

////Created by saurabh.lahoti on 19/09/21

class GameInfo {
  String? type;
  String? view;
  String? group;
  String? mode;

  GameInfo({this.type, this.view, this.group, this.mode});

  factory GameInfo.fromMap(dynamic map) {
    return GameInfo(
      type: map['type'].toString(),
      view: map['view'].toString(),
      group: map['group'].toString(),
      mode: map['mode'].toString(),
    );
  }
}

class GameHistory {
  GameInfo? gameInfo;
  String? kills;
  int? startTimeStamp;
  int? endTimeStamp;
  String? rank;
  String? teamRank;
  int? gameId;
  String? finalTier;

  GameHistory({this.gameInfo,
    this.kills,
    this.startTimeStamp,
    this.endTimeStamp,
    this.rank,
    this.teamRank,
    this.gameId,
    this.finalTier});

  factory GameHistory.fromMap(dynamic map) {
    var temp;
    return GameHistory(
        gameInfo: "Un-Known" == (temp = map['gameInfo'])
            ? GameInfo()
            : GameInfo.fromMap(jsonDecode(temp)),
        kills: int.tryParse(map['kills'].toString()) != null ? (int.tryParse(
            map['kills'].toString())! > 0 ? int.tryParse(map['kills'].toString()).toString():"-"):"-",
        startTimeStamp: null == (temp = map['startTimeStamp'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        endTimeStamp: null == (temp = map['endTimeStamp'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        rank: map['rank'] == "Un-Known" ? "-" : map['rank'],
        teamRank: map['teamRank'] == "Un-Known" ? "-" : map['teamRank'],
        gameId: null == (temp = map['gameId'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        finalTier: map['finalTier']);
  }
}
