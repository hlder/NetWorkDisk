import 'dart:io';
import 'dart:typed_data';

class FileTransferSocketClient {
  static int serverPort = 0;
  static FileTransferSocketClient instance = FileTransferSocketClient();

  Future<Socket?> createSocket(String ip) async {
    Socket? socket = await Socket.connect(ip, serverPort,
        timeout: const Duration(milliseconds: 1000));
    return socket;
  }

  // 上传文件
  void uploadFile(Socket socket, File file) async {
    Stream<List<int>> inputStream = file.openRead();
    inputStream.listen((List<int> event) {
      socket.write(event);
    });
    socket.close();
  }

  // 下载文件
  void downloadFile(Socket socket, File file) async{
    if(!await file.exists()){
      file.create();
    }
    var sink = file.openWrite();
    socket.listen((List<int> event) {

      print('================length:${event.length} 下载中:${event}');
      sink.write(event);
    }, onDone: () {
      print('================下载完成');
      sink.close();
    }, onError: (e) {
      sink.close();
    });
  }
}
