import 'dart:async';

import 'package:flutter/services.dart';

class Logger {
  static const MethodChannel _channel = const MethodChannel('com.gaussian.gsbot/log');

  static Future<void> log(int type, String msg) async {
    return await _channel.invokeMethod('xlog', <String, dynamic>{'type': type, 'msg': msg});
  }
}

void logError(String msg) {
  Logger.log(4, msg);
}

void logInfo(String msg) {
  Logger.log(2, msg);
}

typedef LogCallback = void Function(String);
typedef ExceptionCallback = void Function(dynamic, StackTrace);
Zone? _zone;

Future<void> runApplication(
  void Function() body,
  LogCallback? logCallback,
  ExceptionCallback? exceptionCallback,
) async {
  await runZonedGuarded(
    () async => <void>{body(), _zone = Zone.current},
    (Object obj, StackTrace stack) {
      _collectError(obj, stack);
      if (exceptionCallback != null) {
        _zone?.runBinary(exceptionCallback, obj, stack);
      }
    },
    zoneSpecification: ZoneSpecification(
      print: (Zone self, ZoneDelegate parent, Zone zone, String line) {
        _collectLog(line);

        bool _isDebug = false;
        assert(_isDebug = true);

        // release 不显示 log
        if (_isDebug) parent.print(zone, line);

        if (logCallback != null) {
          _zone?.runUnary(logCallback, line);
        }
      },
    ),
  );
}

void _collectLog(String line) {
  logInfo(line);
}

void _collectError(Object? details, Object? stack) {
  logError('${details?.toString()}\n${stack?.toString()}');
}
