
import 'package:testflutter/sockets/MessageSocketClient.dart';

class FileManagerBean{
  MessageSocketClient messageSocketClient;
  String filePath;

  FileManagerBean(this.messageSocketClient, this.filePath);
}