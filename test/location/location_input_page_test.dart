import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gamerboard/common/services/analytics/analytic_utils.dart';
import 'package:gamerboard/common/services/location/google_places_api_service.dart';
import 'package:gamerboard/common/services/location/location_provider.dart';
import 'package:gamerboard/common/services/location/model/location.dart';
import 'package:gamerboard/common/services/location/model/place.dart';
import 'package:gamerboard/common/services/location/places_service.dart';
import 'package:gamerboard/common/services/user/api_user_service.dart';
import 'package:gamerboard/feature/location/location_page.dart';
import 'package:gamerboard/feature/location/location_page_bloc.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:path_provider/path_provider.dart';

import '../common/screen_size.dart';
import '../common/screen_size_manager.dart';
import '../common/widget_container.dart';

Widget _widget(Widget child) => singleBlocProviderWidget<LocationPageBloc>(
    child,
    () => LocationPageBloc(
          analyticsService: AnalyticService.getInstance(),
          userService: ApiUserService.instance,
          placesService: MockGooglePlacesApiService(),
          locationProvider: DeviceLocationProvider(),
        ));


class MockGooglePlacesApiService extends PlacesService {
  @override
  Future<List<Place>> query(String text, {List<String>? countries}) {
    if (text.isEmpty) {
      return Future.value([]);
    }
    var items = List.generate(
        3,
            (index) => Place(
              address: 'test',
            city: "test",
            state: "test",
            location: Location(0, 0),
            country: "India"));
    return Future.value(items);
  }

  @override
  Future<Place> getAddress(Location location) async {
    return Place(
      address: 'test',
        city: "test",
        state: "test",
        location: Location(0, 0),
        country: "India");
  }
}

void main() {
  testWidgets('Skip Button is shown', (tester) async {
    await _loadWidget(tester);
    final button = find.text('Skip');
    expect(button, findsOneWidget);
  });

  testWidgets('Add manual location Button is shown', (tester) async {
    await _loadWidget(tester);
    final button = _getManualLocationButton();
    expect(button, findsOneWidget);
  });

  testWidgets('Get my location Button is shown', (tester) async {
    await _loadWidget(tester);
    final button = _getMyLocationButton();
    expect(button, findsOneWidget);
  });

  testWidgets('Manual location form is shown', (tester) async {
    await _goToManualLocation(tester);

    final submitButton = find.widgetWithText(InkWell, AppStrings.submit);
    expect(submitButton, findsOneWidget);
    
    final textfield = find.byType(TextField);
    expect(textfield, findsOneWidget);

  });
  testWidgets('Manual location submit button is disabled when no location is selected', (tester) async {
    await _goToManualLocation(tester);

    InkWell tappable = _findTappableButtonWithText(tester, AppStrings.submit);

    final isSubmitButtonActive = tappable.onTap == null;
    expect(isSubmitButtonActive, isTrue);
  });

  testWidgets('Enter query suggestions are shown ', (tester) async {
    await _goToManualLocation(tester);

    await tester.enterText(find.byType(TextField), "Hello");

    await tester.pumpAndSettle();

    final suggestions = find.text('test');
    expect(suggestions, findsNWidgets(3));

  });

  testWidgets('Enter query submit button is disabled ', (tester) async {
    await _goToManualLocation(tester);

    await tester.enterText(find.byType(TextField), "Hello");

    await tester.pumpAndSettle();

    InkWell tappable = _findTappableButtonWithText(tester, AppStrings.submit);

    final isSubmitButtonActive = tappable.onTap == null;
    expect(isSubmitButtonActive, isTrue);

  });

  testWidgets('Click on suggestion, submit button is enabled ', (tester) async {
    await _goToManualLocation(tester);

    await tester.enterText(find.byType(TextField), "Hello");

    await tester.pumpAndSettle();

    final suggestions = find.text('test');

    await tester.tap(suggestions.first);

    await tester.pumpAndSettle();

    InkWell tappable = _findTappableButtonWithText(tester, AppStrings.submit);

    final isSubmitButtonActive = tappable.onTap != null;
    expect(isSubmitButtonActive, isTrue);

  });
}

InkWell _findTappableButtonWithText(WidgetTester tester, String text) {
  final submitButton = find.widgetWithText(InkWell, text);

  final tappable =  tester.widget<InkWell>(submitButton);
  return tappable;
}

Future<void> _goToManualLocation(WidgetTester tester) async {
  await _loadWidget(tester);
  final button = _getManualLocationButton();

  await tester.tap(button);

  await tester.pumpAndSettle();
}

Finder _getMyLocationButton() => find.text(AppStrings.getMyLocationButtonText);

Finder _getManualLocationButton() => find.text(AppStrings.addManualLocationButtonText);

Future<void> _loadWidget(WidgetTester tester) async {
  await tester.setScreenSize(landscapePixel8);

  await tester.pumpWidget(_widget(MaterialApp(home: LocationPage())));
}

Future<String> get _localPath async {
  final directory = await getApplicationDocumentsDirectory();

  return directory.path;
}

Future<File> get _localFile async {
  final path = await _localPath;
  return File('$path/saved_data.json');
}
