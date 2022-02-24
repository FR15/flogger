import 'dart:async';

import 'package:flutter/services.dart';
import 'dart:io' if (dart.library.html) 'dart:html';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:logger/logger.dart';

class XLog {
  static const MethodChannel _channel = const MethodChannel('com.gaussian.gsbot/log');
  static void _log(int type, Object? object) {

    if (kReleaseMode) {
      if (!kIsWeb) {
        if (Platform.isIOS || Platform.isAndroid) {
          _channel.invokeMethod('xlog', <String, dynamic>{'type': type, 'msg': object.toString()});
        }
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
}

