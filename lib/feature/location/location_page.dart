import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gamerboard/common/router.dart';
import 'package:gamerboard/common/services/location/model/place.dart';
import 'package:gamerboard/common/widgets/buttons.dart';
import 'package:gamerboard/common/widgets/input.dart';
import 'package:gamerboard/common/widgets/skeleton.dart';
import 'package:gamerboard/common/widgets/text.dart';
import 'package:gamerboard/feature/location/location_page_bloc.dart';
import 'package:gamerboard/feature/location/location_page_state.dart';
import 'package:gamerboard/feature/location/widgets/flat_location_icon.dart';
import 'package:gamerboard/resources/colors.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/ui_utils.dart';
import 'package:geolocator/geolocator.dart';

class LocationPage extends StatefulWidget {
  @override
  State<StatefulWidget> createState() => _LocationPageState();
}

class _LocationPageState extends State<LocationPage> {
  late LocationPageBloc _locationBloc;
  TextEditingController _controller = TextEditingController();
  GlobalKey<FormState> formKey = GlobalKey();
  bool _showClearManualLocation = false;
  bool _isLoading = false;

  @override
  Widget build(BuildContext context) {
    _showClearManualLocation = _controller.text.isNotEmpty;
    return appScaffold(
        body: _content(child: _buildBlocBuilder()));
  }

  Stack _content({required Widget child}) {
    var containerWidth = MediaQuery.of(context).size.width - 16.0;
    if (MediaQuery.of(context).size.width > 250)
      containerWidth = MediaQuery.of(context).size.width * 0.3;
    return Stack(
      children: [
        Align(
          alignment: Alignment.topRight,
          child: Padding(
            padding: const EdgeInsets.only(right: 24.0, top: 16.0),
            child: TextButton(
                onPressed: () {
                  _locationBloc.navigateBack(false);
                },
                child: SemiBoldText(
                  AppStrings.skip,
                )),
          ),
        ),
        Center(
          child: Container(
            width: containerWidth,
            child: BlocConsumer<LocationPageBloc, LocationPageState>(
              builder: (BuildContext context, LocationPageState state) {
                return SingleChildScrollView(child: child);
              },
              bloc: _locationBloc,
              buildWhen: (p, c) => (c is ShowEnableLocationServiceDialogState ||
                  c is ShowErrorState ||
                  c is ManualLocationState ||
                  c is LocationOptionState ||
                  c is ShowGrantPermissionDialogState ||
                  c is NavigateBackState),
              listener: _onStateChanged,
            ),
          ),
        ),
      ],
    );
  }

  _onStateChanged(BuildContext context, LocationPageState? state) {
    if (state is NavigateBackState) {
      _dismissLoadingIfShowing(context);
      Navigator.of(context).pushNamedAndRemoveUntil(
          Routes.HOME_PAGE, (Route<dynamic> route) => false);
      return;
    }
    if (state is ShowEnableLocationServiceDialogState) {
      _showEnableLocationServiceDialog(context);
      return;
    }
    if (state is ShowErrorState) {
      _showError(state);
      return;
    }
    if (state is ShowGrantPermissionDialogState) {
      _showOpenLocationPermissionSetting(context);
      return;
    }
    if(state is LocationOptionState){
      _showLoading( context, state.isLoading,);
      return;
    }
    if(state is ManualLocationState){
      _showLoading( context, state.isLoading,);
      return;
    }
  }

  void _dismissLoadingIfShowing(BuildContext context) {
    if(_isLoading){
      _isLoading = false;
      Navigator.pop(context);
    }
  }

  void _showLoading( BuildContext context, bool showLoader,) {
    if(showLoader){
      _isLoading = true;
      UiUtils.getInstance.buildLoading(context);
    }else if(_isLoading){
      _isLoading = false;
      Navigator.pop(context);
    }
  }

  void _showEnableLocationServiceDialog(BuildContext context) {
    UiUtils.getInstance.alertDialogV2(
        context,
        AppStrings.locationServiceRationaleTitle,
        AppStrings.locationServiceRationaleDescription,
        yes: AppStrings.goToLocationSettings,
        no: AppStrings.cancel,
        widthFactor: 0.5,
        yesAction: () {
      Geolocator.openLocationSettings();
    });
  }

  void _showError(ShowErrorState state) {
    UiUtils.getInstance.showToast(state.message);
  }

  void _showOpenLocationPermissionSetting(BuildContext context) {
    UiUtils.getInstance.alertDialogV2(
        context,
        AppStrings.locationPermissionDeniedTitle,
        AppStrings.locationPermissionDeniedDescription,
        no: AppStrings.cancel,
        widthFactor: 0.5,
        yes: AppStrings.goToAppSettings, yesAction: () {
      Geolocator.openAppSettings();
    });
  }

  BlocBuilder<LocationPageBloc, Object?> _buildBlocBuilder() {
    return BlocBuilder(
        builder: (BuildContext context, state) {
          if (state is LocationOptionState) {
            return _locationOptionPage(state);
          }
          if (state is ManualLocationState) {
            return _manualLocationPage(state);
          }
          return SizedBox.shrink();
        },
        bloc: _locationBloc,
        buildWhen: (previous, current) =>
            current is LocationOptionState || current is ManualLocationState);
  }

  Column _manualLocationPage(ManualLocationState state) {
    return Column(
      children: [
        const SizedBox(
          height: 16.0,
        ),
        RegularText(AppStrings.manualLocationTitle,
            textAlign: TextAlign.center),
        const SizedBox(
          height: 24.0,
        ),
        if (state.selectedPlace == null)
          ..._inputLocation(state)
        else
          ..._selectedPlace(state.selectedPlace!),
        const SizedBox(
          height: 16.0,
        ),
        secondaryButton(AppStrings.submit, () {
          _locationBloc.onConfirmManualLocation(state.selectedPlace);
        }, active: state.selectedPlace != null && !state.isLoading),
        const SizedBox(
          height: 16.0,
        ),
        TextButton(
            onPressed: () {
              _controller.clear();
              _locationBloc.pop();
              _locationBloc.push(LocationOptionState(selectedPlace: null));
            },
            child: SemiBoldText(
              AppStrings.back,
            )),
        const SizedBox(
          height: 24.0,
        ),
      ],
    );
  }

  TextField _manualLocationQueryTextField() {
    return TextField(
        maxLines: 1,
        keyboardType: TextInputType.text,
        textInputAction: TextInputAction.done,
        controller: _controller,
        onChanged: (val) {
          _locationBloc.onQueryPlace(val);
          setState(() {
            _showClearManualLocation = val.isNotEmpty;
          });
        },
        style: RegularTextStyle().copyWith(),
        decoration: darkTextFieldWithBorderDecoration(
            hintLabel: AppStrings.hintManualAddress,
            error: null,
            suffixIcon: _showClearManualLocation
                ? _clearIconButton()
                : SizedBox.shrink()));
  }

  IconButton _clearIconButton({EdgeInsetsGeometry? padding}) {
    return IconButton(
      padding: padding,
                  onPressed: () {
                    _locationBloc.onQueryPlace('');
                    _controller.clear();
                  },
                  icon: Icon(Icons.clear, color: AppColor.grayB8B8B9,),
                );
  }

  Widget _placesSuggestions() {
    return BlocBuilder(
        builder: (BuildContext context, state) {
          if (state is QueryPlaceState) {
            if (state.isLoading) {
              return SizedBox(
                  width: 24, height: 24, child: appCircularProgressIndicator());
            }
            if (state.suggestedPlaces?.isNotEmpty == true) {
              return _placesSuggestionsList(state.suggestedPlaces ?? [],
                  onSelect: (place) {
                _locationBloc.onSelectPlace(place);
                _controller.text = place.getFormattedAddress();
              });
            }
          }
          return SizedBox.shrink();
        },
        bloc: _locationBloc,
        buildWhen: (previous, current) => current is QueryPlaceState);
  }

  Widget _placesSuggestionsList(List<Place> suggestions,
      {required Function(Place) onSelect}) {
    return ListView.builder(
      itemBuilder: (ctx, index) {
        final place = suggestions[index];
        return _suggestionListItem(onSelect, place);
      },
      itemCount: suggestions.length,
    );
  }

  InkWell _suggestionListItem(Function(Place ) onSelect, Place place) {
    return InkWell(
        onTap: () {
          onSelect.call(place);
        },
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 8.0),
          child: Row(
            children: [
              Icon(Icons.location_on_rounded, color: AppColor.textDarkGray,size:  12),
              const SizedBox(
                width: 4.0,
              ),
              Flexible(
                child: RegularText(
                  place.getFormattedAddress(),
                  fontWeight: FontWeight.normal,
                  color: AppColor.textSubTitle,
                  fontSize: 12.0,
                ),
              ),
            ],
          ),
        ),
      );
  }

  Column _locationOptionPage(LocationOptionState state) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      crossAxisAlignment: CrossAxisAlignment.center,
      children: [
        const SizedBox(
          height: 36.0,
        ),
        FlatRoundLocationIcon(),
        const SizedBox(
          height: 36.0,
        ),
        RegularText(AppStrings.locationShareTitle, textAlign: TextAlign.center),
        const SizedBox(
          height: 24.0,
        ),
        outlineButton(
          AppStrings.addManualLocationButtonText,
          () {
            _locationBloc.push(ManualLocationState());
          },
        ),
        const SizedBox(
          height: 16.0,
        ),
        secondaryButton(AppStrings.getMyLocationButtonText,
            _locationBloc.onGetCurrentLocationClicked,
            active: !state.isLoading),
        const SizedBox(
          height: 16.0,
        ),
        /* if (state.selectedPlace != null)
          secondaryButton(AppStrings.submit,
              (){
                _locationBloc.submitLocation(state.selectedPlace!);
              },
              active: !state.isLoading),*/
      ],
    );
  }

  @override
  void initState() {
    _locationBloc = context.read<LocationPageBloc>();
    super.initState();
  }

  _inputLocation(ManualLocationState state) {
    return [
      _manualLocationQueryTextField(),
      SizedBox(
        height: MediaQuery.of(context).size.height * 0.3,
        width: double.infinity,
        child: _placesSuggestions(),
      ),
    ];
  }

  _selectedPlace(Place place) {
    return [
      InkWell(
        onTap: () {
          _locationBloc.push(ManualLocationState(selectedPlace: null));
          _controller.clear();
        },
        child: TextFormField(
            maxLines: 1,
            focusNode: FocusNode(),
            keyboardType: TextInputType.text,
            textInputAction: TextInputAction.done,
            controller: _controller,

            enabled: false,
            style: RegularTextStyle().copyWith(color: AppColor.whiteEEE9FC),
            decoration: darkTextFieldWithBorderDecoration(
                    error: null,
                    prefix:  Padding(
                      padding: const EdgeInsets.only(right: 12.0),
                      child: Icon(Icons.location_on_rounded, color: AppColor.buttonActive,size:  12),
                    ),
                    suffixIcon: Icon(Icons.clear, color: AppColor.grayB8B8B9,))
                .copyWith(
                    contentPadding: const EdgeInsets.symmetric(
                        horizontal: 10.0, vertical: 12))),
      ),
      SizedBox(
        height: MediaQuery.of(context).size.height * 0.3,
        width: double.infinity,
      ),
    ];
  }
}
