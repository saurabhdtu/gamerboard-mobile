// ignore_for_file: require_trailing_commas
// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

import 'dart:ffi';

import 'package:firebase_analytics/firebase_analytics.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:firebase_core_platform_interface/firebase_core_platform_interface.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gamerboard/common/bloc/application/application_bloc.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/services/feature_flag/feature_manager.dart';
import 'package:gamerboard/feature/login/login_bloc.dart';
import 'package:gamerboard/main.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:mockito/annotations.dart';
import 'package:mockito/mockito.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';
@GenerateNiceMocks([MockSpec<MockFirebaseAnalytics>()])
void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  group('$FirebaseApp', () {
    final mock = MockFirebaseAnalytics();

    setUp(() async {
      clearInteractions(mock);
      when(mock.logScreenView(
        screenClass: anyNamed('screenClass'),
        screenName: anyNamed('screenName'),
        callOptions: anyNamed('callOptions'),
      )).thenAnswer((_) async {});
    });

    testWidgets('Skip Button is shown', (tester) async {
      await tester.pumpWidget(_widget());
      final button = find.text(AppStrings.signIn);
      expect(button, findsOneWidget);
    });

  });
}
class MockFeatureManager extends FeatureManager{
  @override
  Future<void> fetchAllFlags(String? userId) {
    return Future.value();
  }

  @override
  Future<bool> getTrait(String flag, String? userId) {
    return Future.value(true);
  }

  @override
  Future<bool> isEnabled(String flag, String? userId) {
    return Future.value(true);
  }

  @override
  Future<void> setTrait(String flag, String? userId, bool value) {
    return Future.value();
  }

}

Widget _widget(){
  return MultiBlocProvider(
    providers: [
      BlocProvider<ApplicationBloc>(create: (ctx) => ApplicationBloc()),
      BlocProvider<LoginBloc>(create: (ctx) => LoginBloc(MockFeatureManager())),
    ],
    child: Directionality(
      textDirection: TextDirection.ltr,
      child: MaterialApp(
        initialRoute: Routes.LOG_IN,
        navigatorKey: NavigatorService.navigatorKey,
        onGenerateRoute: AppRouter().onGenerateRoute,
      ),
    ),
  );
}
class MockFirebaseAnalytics extends Mock implements FirebaseAnalytics{




}

// ignore: avoid_implementing_value_types
class FakeFirebaseAppPlatform extends Fake implements FirebaseAppPlatform {}