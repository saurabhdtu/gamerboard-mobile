import 'package:gamerboard/feature/history/history_entities.dart';

////Created by saurabh.lahoti on 19/09/21
abstract class HistoryState{}

class HistoryLoading extends HistoryState{}

class HistoryLoaded extends HistoryState{
  List<GameHistory> historyList;

  HistoryLoaded(this.historyList);
}
