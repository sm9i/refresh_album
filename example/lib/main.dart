import 'dart:io';

import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:path_provider/path_provider.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:refresh_album/refresh_album.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  bool hasPermission = false;
  final dio = Dio();

  @override
  void initState() {
    super.initState();
    Future.delayed(Duration(milliseconds: 1000), checkP);
    //getExternalStorageDirectory
    initPlatformState();
  }

  void checkP() async {
    if (Platform.isAndroid) {
      var pState = await PermissionHandler()
          .checkPermissionStatus(PermissionGroup.storage);
      if (pState == PermissionStatus.granted) {
        hasPermission = true;
      } else {
        PermissionHandler().requestPermissions([PermissionGroup.storage]);
      }
    } else {}
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await RefreshAlbum.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: <Widget>[
            Center(
              child: Text('Running on: $_platformVersion\n'),
            ),
            RaisedButton(onPressed: () async {
              if (hasPermission) {
                try {
                  var path = (await getExternalStorageDirectory())
                          .parent
                          .parent
                          .parent
                          .parent
                          .path +
                      '/${DateTime.now().millisecondsSinceEpoch}.jpg';

                  print('下载路径$path');
                  dio.download(
                      'https://www.w3school.com.cn/i/tulip_peach_blossom_w_s.jpg',
                      path, onReceiveProgress: (count, total) {
                    print(count / total);
                    if (count == total) {
                      print('success');
                      RefreshAlbum.refreshAlbum(path);
                    }
                  });
                } catch (e) {
                  print(e);
                }
              } else {
                checkP();
              }
            })
          ],
        ),
      ),
    );
  }
}
