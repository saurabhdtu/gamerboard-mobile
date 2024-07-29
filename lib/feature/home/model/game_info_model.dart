import 'package:gamerboard/graphql/query.graphql.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/graphql_ext.dart';

class GameInfoModel {
  String iconPath;
  ESports gameType;

  GameInfoModel(this.iconPath, this.gameType);

  static final List<GameInfoModel> allTypes = ESports.values
      .where((element) => element != ESports.artemisUnknown)
      .map((e) => GameInfoModel("${imageAssets}${e.getGameLogo()}", e))
      .toList();
}
