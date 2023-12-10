import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'package:testflutter/beans/FileBean.dart';
import 'package:testflutter/beans/FileManagerBean.dart';
import 'package:testflutter/beans/SocketBaseMessage.dart';
import 'package:testflutter/commons/FileCommons.dart';
import 'package:testflutter/commons/MessageCodes.dart';
import 'package:testflutter/pages/filemanager/FileListItem.dart';
import 'package:testflutter/widgets/CommonDialog.dart';

class FileManagerPage extends StatefulWidget {
  FileManagerPage(this.fileManagerBean, {super.key});

  FileManagerBean fileManagerBean;

  @override
  State<FileManagerPage> createState() => _FileManagerPageState();
}

class _FileManagerPageState extends State<FileManagerPage> {
  List<FileBean>? _listFile = null;

  void initDirector()async{
    Directory("${await FileCommons.getBasePath()}/${widget.fileManagerBean.filePath}").createSync();
  }
  @override
  void initState() {
    super.initState();
    initDirector();
    widget.fileManagerBean.messageSocketClient.sendMessage(
        SocketBaseMessage(
            MessageCodes.CODE_FILE_LIST, widget.fileManagerBean.filePath),
        (message) {
      // 获取到文件列表
      // message.message
      List<dynamic> listJson = jsonDecode(message.message);
      _listFile = FileBean.fromJsonArr(listJson);
      setState(() {});
      for (var item in _listFile!) {
        print('============item:${item.toString()}');
      }
    });
  }

  void _showDisconnectDialog(String content) async {
    String result = await CommonDialog(context,
            title: "提示", content: content, sureText: "重新连接", grayText: "退出")
        .showMyDialog();
    if (result == "OK") {
      // 点击了重新连接
      print('----------------执行重连');
    } else {
      print('-----------退出');
    }
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    String titleStr = "远程文件";
    if (widget.fileManagerBean.filePath.isNotEmpty) {
      List<String> splitStr = widget.fileManagerBean.filePath.split("/");
      if (splitStr.isNotEmpty) {
        titleStr = splitStr[splitStr.length - 1];
      } else {
        titleStr = widget.fileManagerBean.filePath;
      }
    }
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(titleStr),
      ),
      body: getBody(),
    );
  }

  Widget getBody() {
    int listCount = 0;
    if (_listFile != null) {
      listCount = _listFile!.length;
    }
    if (listCount == 0 && _listFile != null) {
      // 显示无数据
      return Center(
          child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            padding: EdgeInsets.only(bottom: 10),
            child: Image.asset(
              "images/icon_add.png",
              width: 60,
              height: 60,
            ),
          ),
          const Text("空空如也~快上传文件吧"),
        ],
      ));
    } else {
      return ListView.builder(
          scrollDirection: Axis.vertical,
          itemCount: listCount,
          itemBuilder: (BuildContext context, int index) {
            FileBean item = _listFile![index];
            return FileListItem(item,widget.fileManagerBean);
          });

    }
  }
}
