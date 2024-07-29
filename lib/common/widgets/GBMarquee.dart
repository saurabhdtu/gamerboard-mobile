import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:gamerboard/main.dart';

class GBMarquee extends StatefulWidget {
  final Widget Function(BuildContext context, int index) builder;
  final int itemCount;
  final int pace;
  GBMarquee({required this.builder, required this.itemCount, this.pace = 3});

  @override
  State<StatefulWidget> createState() => _GBMarqueeState();
}

class _GBMarqueeState extends State<GBMarquee> {
  late final ScrollController _scrollController;
  Timer? _timer;
  double currentOffset = 0;
  bool offset = false;
  @override
  Widget build(BuildContext context) {
    _scrollController.addListener(() {
      offset = _scrollController.offset > 1 && _scrollController.offset < 5;
      currentOffset = _scrollController.offset;
    });
    return NotificationListener(
        child: ListView.builder(
            itemBuilder: widget.builder,
            itemCount: widget.itemCount,
            shrinkWrap: true,
            scrollDirection: Axis.horizontal,
            controller: _scrollController),
        onNotification: (t) {
          if (t is ScrollEndNotification) {
            if (!offset) {
              offset = true;
              _scrollController.jumpTo(0);
            }
            print(_scrollController.position.pixels);
          }
          return true;
        });
  }

  @override
  void initState() {
    super.initState();
    _scrollController = ScrollController();
    _resetTimer();
  }

  _resetTimer() {
    _timer?.cancel();
    _timer = Timer.periodic(Duration(milliseconds: 33), (_) {
      try {
        _scrollToOffset(_scrollController.offset + (widget.pace * 10),
            Duration(seconds: 1));
      } catch (ex, stack) {
        logException(ex, stack);
      }
    });
  }

  _scrollToOffset(double offset, Duration duration) {
    try {
      _scrollController.animateTo(offset,
          duration: duration, curve: Curves.linear);
    } catch (ex) {
      debugPrint(ex.toString());
    }
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _timer?.cancel();
    super.dispose();
  }
}
