class Attribution {
  Attribution({
    String? androidPassiveDeepview,
    String? marketingTitle,
    String? referrer,
    int? clickTimestamp,
    String? feature,
    bool? matchGuaranteed,
    String? desktopUrl,
    bool? marketing,
    bool? clickedBranchLink,
    int? id,
    int? referrerId,
    String? campaign,
    bool? isFirstSession,
    String? advertiserPartner,
    String? referringLink,
    String? channel,
  });

  Attribution.fromJson(dynamic json) {
    marketingTitle = json["\$marketing_title"];
    referrer = json['+referrer'];
    clickTimestamp = json['+click_timestamp'];
    feature = json['~feature'];
    matchGuaranteed = json['+match_guaranteed'];
    marketing = json['~marketing'];
    clickedBranchLink = json['+clicked_branch_link'];
    campaign = json['~campaign'];
    isFirstSession = json['+is_first_session'];
    advertiserPartner = json['~advertising_partner_name'];
    referringLink = json['~referring_link'];
    channel = json['~channel'];
    referrerId =
        json['referrerId'] != null ? int.tryParse(json['referrerId']) : null;
  }

  String? marketingTitle;
  String? referrer;
  int? clickTimestamp;
  String? feature;
  bool? matchGuaranteed;
  bool? marketing;
  bool? clickedBranchLink;
  String? campaign;
  bool? isFirstSession;
  String? advertiserPartner;
  String? referringLink;
  String? channel;
  int? referrerId;

  String? get utmMedium => feature;

  String? get utmCampaign => campaign;

  String? get utmSource => channel;

}
