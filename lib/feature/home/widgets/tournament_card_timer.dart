import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import '../../../common/widgets/text.dart';
import '../../../resources/colors.dart';
import '../../../utils/time_utils.dart';
import '../home_bloc.dart';
import '../home_state.dart';

class TournamentCardTimer extends StatefulWidget {
  final DateTime dateTime;
  final bool isCustomRoom;
  TournamentCardTimer(this.dateTime,this.isCustomRoom);


  @override
  State<StatefulWidget> createState() => _TournamentCardTimerState();
}

class _TournamentCardTimerState extends State<TournamentCardTimer> {
  String? remainingTime;

  @override
  Widget build(BuildContext context) {
    remainingTime = TimeUtils.instance.getDurationString(
        widget.dateTime.difference(DateTime.now()),
        shortify: true);
    return BlocListener<HomeBloc, HomeState>(
        listener: (context, state) {
          setState(() {
            remainingTime =  TimeUtils.instance.getDurationString(
                widget.dateTime.difference(DateTime.now()),
                shortify: true);
          });
        },
        listenWhen: (previous, current) => current is Tick,
        child:widget.dateTime.difference(DateTime.now()).isNegative ?SizedBox():!widget.isCustomRoom  ?
        Row(children: [
          const Icon(Icons.circle, color: AppColor.errorRed, size: 6.0),
          const SizedBox(width: 4),
          Expanded(
              child: RegularText(" LIVE - $remainingTime rem.",
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  color: AppColor.whiteTextColor,
                  fontSize: 14))
        ]):
        RegularText("$remainingTime",
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            color: AppColor.whiteTextColor,
            fontSize: 14),
        bloc: context.read<HomeBloc>());
  }
}
