
import 'package:testflutter/sockets/FileSocketClient.dart';

class FileManagerBean{
  FileSocketClient fileSocketClient;
  String filePath;

  FileManagerBean(this.fileSocketClient, this.filePath);
}