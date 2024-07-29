import 'dart:convert';

import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/constants.dart';
import 'package:gamerboard/feature/history/history_entities.dart';
import 'package:gamerboard/feature/history/history_state.dart';

////Created by saurabh.lahoti on 19/09/21
class HistoryBloc extends Cubit<HistoryState>{
  HistoryBloc() : super(HistoryLoading()){
    loadGames();
  }

  void loadGames() async{
      var data = await Constants.PLATFORM_CHANNEL.invokeMethod("get_game_history");
      Iterable l = jsonDecode(data);
      List<GameHistory> games =  List<GameHistory>.from(l.map((e) => GameHistory.fromMap(e)));
      emit(HistoryLoaded(games));
  }


}