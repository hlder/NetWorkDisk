
import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:testflutter/Routers.dart';
import 'package:testflutter/beans/FileManagerBean.dart';
import 'package:testflutter/beans/ServerBean.dart';
import 'package:testflutter/sockets/FileTransferSocketClient.dart';
import 'package:testflutter/sockets/MessageSocketClient.dart';
import 'package:testflutter/widgets/CommonDialog.dart';

import 'HomeServerItem.dart';

class TestHome extends StatefulWidget {
  const TestHome({super.key});

  @override
  State<TestHome> createState() => _TestHomeState();
}

class _TestHomeState extends State<TestHome> {
  TextEditingController messagePortController = TextEditingController();
  TextEditingController filePortController = TextEditingController();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: const Text("网盘"),
      ),
      body: Container(
        padding: const EdgeInsets.all(20),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            //   31099
            Container(
              decoration: BoxDecoration(
                  color: Colors.white,
                  border: Border.all(color: Colors.black, width: 1),
                  borderRadius: BorderRadius.circular(10)),
              child: TextField(
                controller: messagePortController,
                maxLength: 10,
                // textAlignVertical: TextAlignVertical.bottom,
                decoration: const InputDecoration(
                  hintText: "输入message端口",
                  contentPadding: EdgeInsets.all(0),
                  border:InputBorder.none,
                  counterText: '',  //去除底部字数统计
                ),
              ),
            ),
            Container(
              decoration: BoxDecoration(
                  color: Colors.white,
                  border: Border.all(color: Colors.black, width: 1),
                  borderRadius: BorderRadius.circular(10)),
              margin: const EdgeInsets.only(top: 10),
              child: TextField(
                controller: filePortController,
                maxLength: 10,
                decoration: const InputDecoration(
                  hintText: "输入File端口",
                  contentPadding: EdgeInsets.all(0),
                  border:InputBorder.none,
                  counterText: '',  //去除底部字数统计
                ),
              ),
            ),
            Container(
              margin: const EdgeInsets.only(top: 10),
              child: TextButton(onPressed: (){
                FileTransferSocketClient.serverPort = int.parse(filePortController.text);
                _doConnect("192.168.1.120", int.parse(messagePortController.text));
              }, child: const Text("点击连接")),
            ),
          ],
        ),
      ),
    );
  }

  void _doConnect(String ip,int port) {
    MessageSocketClient messageSocketClient = MessageSocketClient();
    messageSocketClient.doContent(ip, port,
        onSocketContentedSuccess: () {
          // 连接成功
          Navigator.pushNamed(context, Routers.fileManagerPage,
              arguments: FileManagerBean(messageSocketClient, ""));
        }, onSocketContentedError: () {
          // 链接失败
          _showDisconnectDialog("连接失败");
        }, onSocketDisconnect: () {
          // 链接位置情况断开了
          _showDisconnectDialog("连接被断开了");
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
}
