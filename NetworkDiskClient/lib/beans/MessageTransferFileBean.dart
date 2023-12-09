import 'dart:convert';
import 'dart:core';

class MessageTransferFileBean {
  String address;
  int port;
  String filePath; // 在服务端的文件路径
  bool isClientSendToServer; // true表示客户端发送文件到服务端，false反之
  int fileLength;

  MessageTransferFileBean(this.address, this.port, this.filePath,
      this.isClientSendToServer, this.fileLength); // 文件大小

  static MessageTransferFileBean fromJson(Map<String, dynamic> map) {
    return MessageTransferFileBean(map["address"], map["port"], map["filePath"],
        map["isClientSendToServer"], map["fileLength"]);
  }

  Map toJson() {
    return {
      "address": address,
      "port": port,
      "filePath": filePath,
      "isClientSendToServer":isClientSendToServer,
      "fileLength":fileLength
    };
  }

  String toJsonString() {
    return jsonEncode(toJson());
  }
}
