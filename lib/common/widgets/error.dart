import 'package:flutter/cupertino.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/common/widgets/text.dart';

////Created by saurabh.lahoti on 01/02/22

class ErrorPage extends StatelessWidget {
  @override
  Widget build(BuildContext context) => appScaffold(
          body: Center(
              child: Column(mainAxisSize: MainAxisSize.min, children: [
        BoldText(
            "You have landed in parallel universe where you don't belong.... Go back"),
        const SizedBox(height: 20.0),
        primaryButton("Go back", () => Navigator.pop(context))
      ])));
}
