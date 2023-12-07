import 'package:flutter/material.dart';
import 'package:testflutter/beans/FileBean.dart';
import 'package:testflutter/pages/filemanager/FileIcon.dart';

class FileListItem extends StatelessWidget {
  final FileBean _fileBean;

  const FileListItem(this._fileBean, {super.key});

  String dateFormat(int timeStamp) {
    DateTime nowTime = DateTime.now();
    DateTime todayTime =
        DateTime(nowTime.year, nowTime.month, nowTime.day, 0, 0, 0);
    DateTime yesterdayTime = DateTime.fromMillisecondsSinceEpoch(
        todayTime.millisecondsSinceEpoch - 24 * 60 * 60 * 1000);
    DateTime lastModifiedTime =
        DateTime.fromMillisecondsSinceEpoch(_fileBean.lastModified);

    if (_fileBean.lastModified > todayTime.millisecondsSinceEpoch) {
      // 今天
      return "${timeNumToString(lastModifiedTime.hour)}:${timeNumToString(lastModifiedTime.minute)}";
    } else if (_fileBean.lastModified > yesterdayTime.millisecondsSinceEpoch) {
      // 昨天
      return "昨天 ${timeNumToString(lastModifiedTime.hour)}:${timeNumToString(lastModifiedTime.minute)}";
    } else {
      // 显示年月日
      return "${lastModifiedTime.year}-${timeNumToString(lastModifiedTime.month)}-${timeNumToString(lastModifiedTime.day)}";
    }
  }

  String timeNumToString(int num) {
    if (num < 10) {
      return "0$num";
    }
    return "$num";
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.only(top: 10, bottom: 10),
      decoration: const BoxDecoration(color: Colors.white),
      child: Row(
        children: [
          Expanded(
              flex: 0,
              child: Container(
                padding: const EdgeInsets.only(left: 10),
                child: FileIcon(_fileBean),
              )),
          Expanded(
            flex: 1,
            child: Container(
              padding: const EdgeInsets.only(left: 10,right: 10),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.start,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    _fileBean.name,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(color: Color(0xFF06091a),fontSize: 16,fontWeight:FontWeight.normal),
                  ),
                  Container(
                    padding: const EdgeInsets.only(top: 2),
                    child: Text(dateFormat(_fileBean.lastModified),
                      style: const TextStyle(color: Color(0xFFa1a0a5),fontSize: 14),
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
