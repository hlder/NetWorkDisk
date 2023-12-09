import 'dart:convert';
import 'dart:io';

import 'package:testflutter/beans/SocketBaseMessage.dart';

typedef OnSocketContentedSuccess(); // socket链接成功
typedef OnSocketContentedError(); // socket链接失败
typedef OnSocketDisconnect(); // socket不明原因，断开了

typedef OnCallBack(SocketBaseMessage message);

class MessageSocketClient {
  Socket? socket;
  Map<int, OnCallBack> mapCallBack = {};
  Utf8Decoder utf8decoder = const Utf8Decoder();

  int messageVersion = 0;
  OnSocketDisconnect? onSocketDisconnect;

  /// 链接socket
  void doContent(String ip, int port,
      {OnSocketContentedSuccess? onSocketContentedSuccess,
      OnSocketContentedError? onSocketContentedError,
      OnSocketDisconnect? onSocketDisconnect}) {
    this.onSocketDisconnect = onSocketDisconnect;
    Future<Socket> futureSocket = Socket.connect(ip, port, timeout: Duration(milliseconds: 1000));
    futureSocket.then((value) => {
              print("============================链接成功"),
              socket = value,
              _doListen(value),
              if (onSocketContentedSuccess != null) {onSocketContentedSuccess()}
            })
        .onError((error, stackTrace) => {
              print("==================onerror:${error}"),
              if (onSocketContentedError != null) {onSocketContentedError()}
            });
  }

  void _doListen(Socket socket) {
    socket.listen((event) {
      // 收到消息
      String message = utf8decoder.convert(event);
      print("================接收消息:$message");
      SocketBaseMessage socketBaseMessage = SocketBaseMessage.fromJson(message);
      OnCallBack? onCallBack = mapCallBack.remove(socketBaseMessage.version);
      if (onCallBack != null) {
        onCallBack(socketBaseMessage);
      }
    }, onDone: () {
      // 执行完成，跟服务端断开了需要提示用户
      print('=======================_doListen onDone');
      if (onSocketDisconnect != null) {
        onSocketDisconnect!();
      }
    }, onError: (e) {
      // 链接错误，链接断开
      this.socket = null;
    });
  }

  void sendMessage(SocketBaseMessage message, OnCallBack onCallBack) {
    int tempVersion = messageVersion++;
    message.version = tempVersion;
    mapCallBack[tempVersion] = onCallBack;
    print("================socket：${socket}   发送消息:${message.toJsonString()}");
    socket?.writeln(message.toJsonString());
    socket?.flush();
    print("================发送结束");
  }

  void closeContent() {
    socket?.close();
  }
}
