import 'package:testflutter/beans/SocketBaseMessage.dart';

import 'MessageSocketClient.dart';

typedef OnPreviewCallBack(String imgBase64);

class PreviewImageSocketClient {
  static PreviewImageSocketClient client = PreviewImageSocketClient();
  MessageSocketClient messageSocketClient = MessageSocketClient();

  void doConnect(String ip, int port) {
    messageSocketClient.doContent(ip, port, onSocketContentedSuccess: () {},
        onSocketContentedError: () {
      // 链接失败
    }, onSocketDisconnect: () {
      // 链接位置情况断开了
    });
  }

  List<PreviewTask> listQueue = [];

  bool isRun=false;
  void queryPreviewImageBase64(String filePath, OnPreviewCallBack onPreviewCallBack) async{
    listQueue.insert(0, PreviewTask(filePath, onPreviewCallBack));
    if(isRun){
      return;
    }
    isRun = true;
    doRunQueryPreview();
    isRun = false;
  }

  void doRunQueryPreview(){
    for (var task in listQueue) {
      messageSocketClient.sendMessage(SocketBaseMessage(0,task.filePath), (SocketBaseMessage message){
        task.onPreviewCallBack(message.message);
      });
    }
  }
}

class PreviewTask{
  String filePath;
  OnPreviewCallBack onPreviewCallBack;
  PreviewTask(this.filePath, this.onPreviewCallBack);
}