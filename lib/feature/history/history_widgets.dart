import 'package:flutter/material.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/history/history_entities.dart';

////Created by saurabh.lahoti on 20/09/21
class HistoryWidgets {
  Widget getTableTitle() {
    return Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        mainAxisSize: MainAxisSize.max,
        children: [
          Expanded(
              child: BoldText("Game Id", textAlign: TextAlign.center),
              flex: 3),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: BoldText("Played On", textAlign: TextAlign.center),
              flex: 3),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: BoldText("Game Info", textAlign: TextAlign.center),
              flex: 4),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: BoldText("Kills", textAlign: TextAlign.center),
              flex: 2),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: BoldText("My Rank", textAlign: TextAlign.center),
              flex: 2),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: BoldText("T.Rank", textAlign: TextAlign.center),
              flex: 2),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: BoldText("Final Tier", textAlign: TextAlign.center),
              flex: 3)
        ]);
  }

  Widget getTableRow(GameHistory history) {
    DateTime dt = history.startTimeStamp != null ? DateTime.fromMillisecondsSinceEpoch(history.startTimeStamp!): DateTime(0);
    String dtt = dt!= DateTime(0)
      ? "${dt.day}/${dt.month}/${dt.year%100} ${dt.hour}:${dt.minute}:${dt.second%100}"
      .replaceAll("T", " ")
        : "NA";
    

    return Row(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        mainAxisSize: MainAxisSize.max,
        children: [
          Expanded(
              child: RegularText(history.gameId.toString(),
                  textAlign: TextAlign.center),
              flex: 3),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: RegularText(dtt, textAlign: TextAlign.center),
               flex: 3),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: RegularText(history.gameInfo?.group == null?
                  "Un-Known":
                  "${history.gameInfo?.type}-${history.gameInfo?.view}-${history.gameInfo?.group} :${history.gameInfo?.mode}",
                  textAlign: TextAlign.center),
              flex: 4),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: RegularText(history.kills.toString(),
                  textAlign: TextAlign.center),
              flex: 2),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: RegularText(history.rank.toString(),
                  textAlign: TextAlign.center),
              flex: 2),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: RegularText(history.teamRank.toString(),
                  textAlign: TextAlign.center),
              flex: 2),
          DecoratedBox(
              decoration: BoxDecoration(color: Colors.grey),
              child: SizedBox(width: 1.0, height: 24.0)),
          Expanded(
              child: RegularText(history.finalTier.toString(),
                  textAlign: TextAlign.center),
              flex: 3)
        ]);
  }
}
