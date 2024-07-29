////Created by saurabh.lahoti on 04/04/22

class DeeplinkData {
  int? tournamentId;
  String? inviteCode;
  String? teamName;
  String? referrerCode;

  int? teamId;
  String? userName;
  int? id;
  int? index;
  String? route;
  String? deeplinkMode;

  DeeplinkData(
      {this.tournamentId,
      this.inviteCode,
      this.id,
      this.index,
      this.route,
      this.teamId,
      this.referrerCode,
      this.teamName,
      this.userName,
      this.deeplinkMode});

  factory DeeplinkData.fromMap(dynamic map) {
    if (null == map) return DeeplinkData();
    var temp;
    return DeeplinkData(
        tournamentId: null == (temp = map['tournamentId'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        inviteCode: map['inviteCode']?.toString(),
        referrerCode: map['referrerCode']?.toString(),

        id: null == (temp = map['id'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        teamId: null == (temp = map['teamId'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        index: null == (temp = map['index'])
            ? null
            : (temp is num ? temp.toInt() : int.tryParse(temp)),
        route: map['route'] ?? map['page'] ?? map['screen']?.toString(),
        userName: map['userName']?.toString(),
        teamName: map['teamName']?.toString(),

        deeplinkMode: map['deeplinkMode']?.toString());
  }
}
