// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility that Flutter provides. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:gamerboard/graphql/query.dart';
import 'package:gamerboard/utils/app_update.dart';
import 'package:gamerboard/utils/graphql_ext.dart';
import 'package:test/test.dart';

void main() {
  test('Value should be bronze 5', () {
    final result = [];
    [
      BgmiLevels.bronzeFive,
      BgmiLevels.bronzeFour,
      BgmiLevels.bronzeThree,
      BgmiLevels.bronzeTwo,
      BgmiLevels.bronzeOne,
      BgmiLevels.silverFive,
      BgmiLevels.silverFour,
      BgmiLevels.silverThree,
      BgmiLevels.silverTwo,
      BgmiLevels.silverOne,
      BgmiLevels.goldFive,
      BgmiLevels.goldFour,
      BgmiLevels.goldThree,
      BgmiLevels.goldTwo,
      BgmiLevels.goldOne,
      BgmiLevels.diamondFive,
      BgmiLevels.diamondFour,
      BgmiLevels.diamondThree,
      BgmiLevels.diamondTwo,
      BgmiLevels.diamondOne,
      BgmiLevels.platinumFive,
      BgmiLevels.platinumFour,
      BgmiLevels.platinumThree,
      BgmiLevels.platinumTwo,
      BgmiLevels.platinumOne,
      BgmiLevels.crownFive,
      BgmiLevels.crownFour,
      BgmiLevels.crownThree,
      BgmiLevels.crownTwo,
      BgmiLevels.crownOne,
      BgmiLevels.ace,
      BgmiLevels.aceMaster,
      BgmiLevels.aceDominator,
      BgmiLevels.conqueror
    ]..forEach((element) {
        result.add(getBaseLevel(element));
      });
    expect(result, [
      BgmiLevels.bronzeFive,
      BgmiLevels.bronzeFive,
      BgmiLevels.bronzeFive,
      BgmiLevels.bronzeFive,
      BgmiLevels.bronzeFive,
      BgmiLevels.silverFive,
      BgmiLevels.silverFive,
      BgmiLevels.silverFive,
      BgmiLevels.silverFive,
      BgmiLevels.silverFive,
      BgmiLevels.goldFive,
      BgmiLevels.goldFive,
      BgmiLevels.goldFive,
      BgmiLevels.goldFive,
      BgmiLevels.goldFive,
      BgmiLevels.diamondFive,
      BgmiLevels.diamondFive,
      BgmiLevels.diamondFive,
      BgmiLevels.diamondFive,
      BgmiLevels.diamondFive,
      BgmiLevels.platinumFive,
      BgmiLevels.platinumFive,
      BgmiLevels.platinumFive,
      BgmiLevels.platinumFive,
      BgmiLevels.platinumFive,
      BgmiLevels.crownFive,
      BgmiLevels.crownFive,
      BgmiLevels.crownFive,
      BgmiLevels.crownFive,
      BgmiLevels.crownFive,
      BgmiLevels.ace,
      BgmiLevels.ace,
      BgmiLevels.ace,
      BgmiLevels.conqueror
    ]);
  });

  test('App update', () {
    final testCases = [
      TestAppUpdate(270, 1),
      TestAppUpdate(100, 9),
      TestAppUpdate(4500, 10),
      TestAppUpdate(450, 20),
      TestAppUpdate(121411210, 100),
      TestAppUpdate(12211, 1),
      TestAppUpdate(121221, 9),
      TestAppUpdate(55671, 10),
      TestAppUpdate(1897982121, 20),
      TestAppUpdate(1111, 100),
      TestAppUpdate(732882, 1),
      TestAppUpdate(21442, 9),
      TestAppUpdate(999092, 10),
      TestAppUpdate(897871982, 20),
      TestAppUpdate(89172381722, 25),
      TestAppUpdate(03192801932, 100),
      TestAppUpdate(81918, 90),
      TestAppUpdate(229, 10),
      TestAppUpdate(9, 20),
      TestAppUpdate(99, 80),
      TestAppUpdate(12119, 90),
      TestAppUpdate(98712319, 99),
      TestAppUpdate(128371982319, 100),
      TestAppUpdate(125, 80),
    ];
    final expectations = [
      true,
      true,
      true,
      true,
      true,
      false,
      false,
      true,
      true,
      true,
      false,
      false,
      false,
      true,
      true,
      true,
      true,
      false,
      false,
      false,
      true,
      true,
      true,
      true
    ];
    expect(
        testCases
            .map((e) => AppUpdateUtil.eligibleForUpdate(e.id, e.rolloutValue))
            .toList(),
        expectations);
  });
}

class TestAppUpdate {
  int id;
  int rolloutValue;

  TestAppUpdate(this.id, this.rolloutValue);
}
