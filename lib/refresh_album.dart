import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class RefreshAlbum {
  static const MethodChannel _channel = const MethodChannel('refresh_album');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<String> refreshAlbum(String filePath) async {
    final String code =
        await _channel.invokeMethod("refreshAlbum", {"path": filePath});
    debugPrint(code);
    switch (code) {
      case "200":
        return "刷新成功";
        break;
      case "404":
        return "文件不存在";
        break;
      case "500":
        return "未知异常";
        break;
      default:
        return "未知异常";
        break;
    }
  }
}
