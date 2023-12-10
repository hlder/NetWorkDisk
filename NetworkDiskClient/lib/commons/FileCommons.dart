import 'dart:io';

import 'package:path_provider/path_provider.dart';

class FileCommons {
  /// 获取客户端的basePath
  static Future<String> getBasePath() async {
    Directory? directory = await getExternalStorageDirectory();
    return "${directory!.path}/client";
  }
}
