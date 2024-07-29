import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:gb_payment_plugin/phonepe_sdk.dart';
import 'package:gb_payment_plugin/upi_app.dart';

class UpiAppsChooser extends StatefulWidget {
  final Function(UpiApp upiApp) onSelectUpiApp;
  final Color? selectorColor;
  final Widget? noUpiAppsWidget;
  final Function? upiAppsLoaded;

  const UpiAppsChooser(
      {super.key, required this.onSelectUpiApp, this.selectorColor, this.noUpiAppsWidget, this.upiAppsLoaded});

  @override
  State<UpiAppsChooser> createState() => _UpiAppsChooserState();
}

class _UpiAppsChooserState extends State<UpiAppsChooser> {
  List<UpiApp>? _upiApps = null;
  final _phonepeSdkPlugin = PhonepeSdk();
  UpiApp? _selectedUpiApp;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    List<UpiApp> upiApps;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      upiApps = await _phonepeSdkPlugin.getUpiApps() ?? [];
    } on PlatformException {
      upiApps = [];
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _upiApps = upiApps ;
      if(upiApps.isNotEmpty)
        {
          _selectedUpiApp ??= upiApps[0];
          widget.onSelectUpiApp(_selectedUpiApp!);
        }
      widget.upiAppsLoaded?.call(upiApps);
    });
  }

  @override
  Widget build(BuildContext context) {
    if(_upiApps == null){
      return  Transform.scale(
        scale: 0.5,
        child: const CircularProgressIndicator(),
      );
    }
    if(_upiApps != null && _upiApps?.isEmpty == true){
      return widget.noUpiAppsWidget ?? const Text("No upi apps available");
    }
    return _listUpiApps(context);
  }

  SizedBox _listUpiApps(BuildContext context) {
    return SizedBox(
    height: 50,
    child: SingleChildScrollView(
        scrollDirection: Axis.horizontal,
        child: Row(
          children: (_upiApps ?? []).map((upiApp) {
            return GestureDetector(
              onTap: () {
                setState(() {
                  _selectedUpiApp = upiApp;
                  widget.onSelectUpiApp(_selectedUpiApp!);
                });
              },
              child: Padding(
                padding: const EdgeInsets.only(right: 8.0),
                child: Container(
                  width: 48,
                  height: 48,
                  decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(4),
                      border: Border.all(
                          color: _selectedUpiApp?.packageName == upiApp.packageName
                              ? (widget.selectorColor ??
                              Theme.of(context).colorScheme.primary)
                              : Colors.transparent,
                          width: 2)),
                  child: Padding(
                    padding: const EdgeInsets.all(4.0),
                    child: Image.memory(
                      upiApp.icon,
                      height: 48,
                      width: 48,
                    ),
                  ),
                ),
              ),
            );
          }).toList(),
        )
    ),
  );
  }
}
