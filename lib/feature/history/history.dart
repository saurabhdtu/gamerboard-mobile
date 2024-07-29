import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/feature/history/history_bloc.dart';
import 'package:gamerboard/feature/history/history_state.dart';
import 'package:gamerboard/feature/history/history_widgets.dart';

////Created by saurabh.lahoti on 19/09/21

class HistoryPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _HistoryState();
}

class _HistoryState extends State<HistoryPage> {
  HistoryBloc? _historyBloc;
  HistoryWidgets _historyWidgets = HistoryWidgets();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        appBar: appBar(context, "Game History"),
        body: _page());
  }

  @override
  void initState() {
    super.initState();
    _historyBloc = context.read<HistoryBloc>();
  }

  Widget _page() {
    return BlocBuilder<HistoryBloc, HistoryState>(
        builder: (context, state) {
          if (state is HistoryLoaded) {
            return ListView.separated(
                itemBuilder: (context, index) {
                  return index == 0
                      ? _historyWidgets.getTableTitle()
                      : _historyWidgets
                          .getTableRow(state.historyList[index - 1]);
                },
                separatorBuilder: (context, index) =>
                    Divider(height: 8.0, thickness: 1.0, color: Colors.grey),
                itemCount: state.historyList.length + 1);
          } else
            return appCircularProgressIndicator();
        },
        bloc: _historyBloc);
  }
}
