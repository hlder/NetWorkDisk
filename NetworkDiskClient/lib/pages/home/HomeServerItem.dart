import 'package:flutter/material.dart';
import 'package:testflutter/Routers.dart';
import 'package:testflutter/beans/FileManagerBean.dart';
import 'package:testflutter/beans/ServerBean.dart';
import 'package:testflutter/sockets/MessageSocketClient.dart';
import 'package:testflutter/widgets/CommonDialog.dart';

class HomeServerItem extends StatefulWidget {
  ServerBean serverBean;

  HomeServerItem(this.serverBean, {super.key});

  @override
  State<HomeServerItem> createState() => _HomeServerItemState();
}

class _HomeServerItemState extends State<HomeServerItem> {
  void _doConnect() {
    MessageSocketClient messageSocketClient = MessageSocketClient();
    messageSocketClient.doContent(widget.serverBean.ip, 20001,
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

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.only(top: 20),
      child: Center(
        child: GestureDetector(
          onTap: () {
            _doConnect();
          },
          child: Container(
            decoration: BoxDecoration(
                color: Colors.white,
                border: Border.all(color: Colors.black, width: 1),
                borderRadius: BorderRadius.circular(10)),
            padding: const EdgeInsets.all(10),
            child: Column(
              children: [
                Text("手机:${widget.serverBean.phoneName}  型号:${widget.serverBean.phoneModel}"),
                Text("连接地址:${widget.serverBean.ip}:${widget.serverBean.port}"),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
