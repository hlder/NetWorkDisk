
import 'package:flutter/material.dart';
import 'package:testflutter/beans/FileBean.dart';

class FileIcon extends StatelessWidget {
  final FileBean _fileBean;
  const FileIcon(this._fileBean,{super.key});

  @override
  Widget build(BuildContext context) {
    if(_fileBean.isDirectory){
      return Image.asset(
        "images/icon_folder.png",
        width: 40,
        height: 40,
      );
    }
    return Image.asset(
      "images/icon_file_unknown.png",
      width: 40,
      height: 40,
    );
  }
}
