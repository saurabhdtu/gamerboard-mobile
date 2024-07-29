import 'dart:io';

import 'package:artemis/artemis.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:dio/dio.dart';
import 'package:firebase_crashlytics/firebase_crashlytics.dart';
import 'package:flutter/foundation.dart';
import 'package:gamerboard/common/bloc/application/application_bloc.dart';
import 'package:gamerboard/main.dart';
import 'package:gamerboard/resources/strings.dart';
import 'package:gamerboard/utils/api_client.dart';
import 'package:gamerboard/utils/network_utils.dart';
import 'package:gql_dio_link/gql_dio_link.dart';
import 'package:gql_exec/gql_exec.dart';
import 'package:json_annotation/json_annotation.dart';
////Created by saurabh.lahoti on 16/12/21

// final _apiClient = ApiClient.instance;

abstract class BaseRepository {
  Future<GraphQLResponse<T>> executeCall<T, U extends JsonSerializable>(
      GraphQLQuery<T, U> query) async {
    if ((!await NetworkUtils.isInternetConnected())) {
      return Future.value(GraphQLResponse(errors: [
        GraphQLError(message: ErrorMessages.ERROR_INTERNET_CONNECTIVITY)
      ]));
    }
    try {
      FirebaseCrashlytics.instance.log("operation--> ${query.operationName}\n"
          "variables --> ${_removeNulls(query.getVariablesMap()).toString()}");
      debugPrint("operation--> ${query.operationName}\n"
          "variables --> ${_removeNulls(query.getVariablesMap()).toString()}");
      final request = Request(
          operation: Operation(
              document: query.document, operationName: query.operationName),
          variables: _removeNulls(query.getVariablesMap()),
          context: const Context());

      var response = await DioClient.instance.link.request(request).first;
      if (response.errors != null && response.errors?.isNotEmpty == true) {
        FirebaseCrashlytics.instance.log(response.errors!.first.message);
      }
      if (response.errors != null && response.errors?.isNotEmpty == true)
        FirebaseCrashlytics.instance.recordError(
            Exception(response.errors!.first.message), null,
            reason: "Api error", fatal: false);

      debugPrint(
          "response--> ${response.data}; errors: ${response.errors.toString()}");
      return GraphQLResponse<T>(
        data: response.data == null ? null : query.parse(response.data ?? {}),
        errors: response.errors,
        context: response.context,
      );
    } catch (ex, trace) {
      if (ex is Exception) {
        FirebaseCrashlytics.instance.recordError(ex, trace);
      }
      debugPrintStack(stackTrace: trace, label: ex.toString());
      if (ex is DioLinkServerException && ex.originalException is DioError) {
        switch ((ex.originalException as DioException).type) {
          case DioExceptionType.badResponse:
            switch (ex.response.statusCode) {
              case 401:
                signOut(NavigatorService.navigatorKey.currentContext,
                    message: ErrorMessages.ERROR_SESSION_EXPIRED);
                return _getGraphQLError(ErrorMessages.ERROR_SESSION_EXPIRED);
              case 404:
                return _getGraphQLError(ErrorMessages.ERROR_BAD_REQUEST);
              case 403:
                return _getGraphQLError(ErrorMessages.ERROR_UNAUTHORIZED);
              case 500:
                return _getGraphQLError(ErrorMessages.ERROR_SOME);

              case 502:
              case 503:
                return _getGraphQLError(ErrorMessages.ERROR_UNREACHABLE);

              case 400:
                return _getGraphQLError(ErrorMessages.ERROR_BAD_REQUEST);

              default:
                return _getGraphQLError(ErrorMessages.ERROR_SOME);
            }
          case DioExceptionType.receiveTimeout:
          case DioExceptionType.sendTimeout:
          case DioExceptionType.connectionError:
            return _getGraphQLError(ErrorMessages.ERROR_REQUEST_TIMED_OUT);
          default:
            return _getGraphQLError(ErrorMessages.ERROR_SOME);
        }
      } else if (ex is SocketException) {
        return _getGraphQLError(ErrorMessages.ERROR_INTERNET_CONNECTIVITY);
      } else if (ex is FormatException) {
        return _getGraphQLError(ErrorMessages.ERROR_SOME);
      } else {
        return _getGraphQLError(ErrorMessages.ERROR_SOME);
      }
    }
  }
  /**
   * removeNulls
   */
  Map<String, dynamic> _removeNulls(Map<String, dynamic> map) {
    map.removeWhere((key, value) => value == null);
    map.forEach((key, value) {
      if(value is Map<String, dynamic>){
        _removeNulls(value);
      }
    });
    return map;
  }

}

Future<GraphQLResponse<T>> _getGraphQLError<T>(String message) =>
    Future.value(GraphQLResponse(errors: [GraphQLError(message: message)]));

