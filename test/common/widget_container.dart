import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

Widget singleBlocProviderWidget<T extends  StateStreamableSource<Object?>>(Widget child,T Function() createBlock) => MultiBlocProvider(
  providers: [
    BlocProvider<T>(create: (ctx) => createBlock()),
  ],
  child: Directionality(
    textDirection: TextDirection.ltr,
    child: Material(
      child: child,
    ),
  ),
);

