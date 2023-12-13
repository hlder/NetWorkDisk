import 'dart:io';

import 'package:flutter/material.dart';
import 'package:path_provider/path_provider.dart';
import 'package:testflutter/Routers.dart';
import 'package:testflutter/beans/FileBean.dart';
import 'package:testflutter/beans/FileManagerBean.dart';
import 'package:testflutter/beans/MessageTransferFileBean.dart';
import 'package:testflutter/beans/SocketBaseMessage.dart';
import 'package:testflutter/commons/FileCommons.dart';
import 'package:testflutter/commons/MessageCodes.dart';
import 'package:testflutter/pages/filemanager/FileIcon.dart';
import 'package:testflutter/sockets/FileTransferSocketClient.dart';
import 'package:testflutter/sockets/PreviewImageSocketClient.dart';
import 'package:testflutter/utils/TimeUtils.dart';
import 'package:testflutter/widgets/SelectDialog.dart';

class FileListItem extends StatefulWidget {
  final FileBean _fileBean;
  final FileManagerBean fileManagerBean;

  const FileListItem(this._fileBean, this.fileManagerBean, {super.key});

  @override
  State<FileListItem> createState() => _FileListItemState();
}

class _FileListItemState extends State<FileListItem> {
  double downLoadProgress = 0.0;

  int fileStatus = 0; // 0表示文件本地不存在，1表示本地存在文件，2表示正在下载文件

  /// 判断本地是否有该文件
  void isLocalHavaFile() async {
    File localFile = File(
        "${await FileCommons.getBasePath()}/${widget._fileBean.absolutePath}");

    if (await localFile.exists() == true &&
        await localFile.length() == widget._fileBean.fileLength) {
      // 文件存在,且跟网盘文件大小一致，那边认为它存在
      fileStatus = 1;
    } else if (FileTransferSocketClient.isDownLoading(
        widget._fileBean.absolutePath)) {
      fileStatus = 2;
      FileTransferSocketClient.addListener(widget._fileBean.absolutePath,
          onFileDownLoadProgress: (max, progress) {
        // 文件下载进度
        downLoadProgress = progress / max;
        setState(() {});
      }, onFileDownLoadFinish: () {
        // 文件下载完成
        fileStatus = 1;
      });
    }else{
      fileStatus = 0;
    }
    setState(() {});
    print('========================isLocalHavaFile:"${await FileCommons.getBasePath()}/${widget._fileBean.absolutePath}"  fileStatus=${fileStatus}');
  }

  void downLoadFile(String filePath, String fileName) async {
    String ip = widget.fileManagerBean.messageSocketClient.socket!.address.host;
    Socket? socket = await FileTransferSocketClient.instance.createSocket(ip);

    if (socket != null) {
      String json = MessageTransferFileBean(socket.address.host, socket.port,
              "$filePath/$fileName", false, widget._fileBean.fileLength)
          .toJsonString();
      SocketBaseMessage message = SocketBaseMessage(
          MessageCodes.CODE_CLIENT_RECEIVE_FROM_SERVER_FILE, json);

      Directory? directory = await getExternalStorageDirectory();
      String path = "${await FileCommons.getBasePath()}/$filePath";

      print('=======================directory!.path:${directory!.path}');
      widget.fileManagerBean.messageSocketClient.sendMessage(message, (msg) {
        fileStatus = 2;
        FileTransferSocketClient.instance.downloadFile(
            socket,
            widget._fileBean.absolutePath,
            File("$path/$fileName"),
            widget._fileBean.fileLength,
            onFileDownLoadProgress: (max, progress) {
          // 文件下载进度
          downLoadProgress = progress / max;
          setState(() {});
        }, onFileDownLoadFinish: () {
          // 文件下载完成
          fileStatus = 1;
          setState(() {});
        });
      });
    }
  }

  void onClick(BuildContext context) {
    if (widget._fileBean.isDirectory) {
      Navigator.pushNamed(context, Routers.fileManagerPage,
          arguments: FileManagerBean(widget.fileManagerBean.messageSocketClient,
              "${widget.fileManagerBean.filePath}/${widget._fileBean.name}"));
    } else {
      SelectDialog(
              context, widget._fileBean.name, ["下载并打开", "下载", "分享", "详情", "取消"])
          .show()
          .then((value) => {
                if (value == 0)
                  {
                    // 下载并打开
                  }
                else if (value == 1)
                  {
                    // 下载
                    print(
                        '================ip:${widget.fileManagerBean.messageSocketClient.socket!.address.host}'),
                    downLoadFile(
                        widget.fileManagerBean.filePath, widget._fileBean.name)
                  },
                print('=======================value:${value}')
              });
    }
  }

  @override
  void initState() {
    super.initState();
    isLocalHavaFile();
    PreviewImageSocketClient.client.queryPreviewImageBase64(widget._fileBean.absolutePath, (imgBase64) => {
      print('-------------------------查询到base64：${imgBase64.length}')
    });
  }

  @override
  void dispose() {
    super.dispose();
    FileTransferSocketClient.removeListener(widget._fileBean.absolutePath);
  }

  @override
  Widget build(BuildContext context) {
    Widget rightWidget;
    if (fileStatus == 1) {
      // 本地存在
      rightWidget = Container(
        child: Text("打开"),
      );
    } else if (fileStatus == 2) {
      // 正在下载
      rightWidget = Container(
        padding: const EdgeInsets.only(right: 10),
        child: CircularProgressIndicator(
          backgroundColor: const Color(0xffefefef),
          color: const Color(0xff5ac460),
          value: downLoadProgress,
        ),
      );
    } else {
      // 本地没有
      rightWidget = Container(
        child: Text("云盘"),
      );
    }
    return GestureDetector(
      onTap: () {
        onClick(context);
      },
      child: Container(
        padding: const EdgeInsets.only(top: 10, bottom: 10),
        decoration: const BoxDecoration(color: Colors.white),
        child: Row(
          children: [
            Expanded(
                flex: 0,
                child: Container(
                  padding: const EdgeInsets.only(left: 10),
                  child: FileIcon(widget._fileBean),
                )),
            Expanded(
              flex: 1,
              child: Container(
                padding: const EdgeInsets.only(left: 10, right: 10),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.start,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      widget._fileBean.name,
                      overflow: TextOverflow.ellipsis,
                      style: const TextStyle(
                          color: Color(0xFF06091a),
                          fontSize: 16,
                          fontWeight: FontWeight.normal),
                    ),
                    Row(
                      children: [
                        Container(
                          padding: const EdgeInsets.only(top: 2),
                          child: Text(
                            TimeUtils.dateFormat(widget._fileBean.lastModified),
                            style: const TextStyle(
                                color: Color(0xFFa1a0a5), fontSize: 14),
                          ),
                        ),
                        Container(
                          padding: const EdgeInsets.only(
                              top: 2, left: 10, right: 10),
                          child: Text(
                            widget._fileBean.getFileSizeStr(),
                            style: const TextStyle(
                                color: Color(0xFFa1a0a5), fontSize: 14),
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
            ),
            Expanded(flex: 0, child: rightWidget)
          ],
        ),
      ),
    );
  }
}
