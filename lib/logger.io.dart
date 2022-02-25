import 'package:flutter/services.dart';
import 'dart:io';
import 'package:flutter/foundation.dart';

class XLog {
  static const MethodChannel _channel = const MethodChannel('com.gaussian.gsbot/log');
  static void _log(int type, Object? object) {

    if (kReleaseMode) {
      if (Platform.isIOS || Platform.isAndroid) {
          _channel.invokeMethod('xlog', <String, dynamic>{'type': type, 'msg': object.toString()});
        }
    } else {
      debugPrint(object.toString());
    }
  }

  static void i(Object? object) {
    _log(2, object);
  }

  static void e(Object? object) {
    _log(4, object);
  }

  static void d(Object? object) {
    debugPrint(object.toString());
  }
}
