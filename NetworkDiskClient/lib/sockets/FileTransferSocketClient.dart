import 'dart:collection';
import 'dart:io';

typedef OnFileDownLoadProgress(int max, int progress);
typedef OnFileDownLoadFinish();

class FileDownLoadCallBack {
  OnFileDownLoadProgress? onFileDownLoadProgress;
  OnFileDownLoadFinish? onFileDownLoadFinish;

  FileDownLoadCallBack.add(
      this.onFileDownLoadProgress, this.onFileDownLoadFinish);

  void onProgress(int max, int progress) {
    if (onFileDownLoadProgress != null) {
      onFileDownLoadProgress!(max, progress);
    }
  }

  void onFinish() {
    if (onFileDownLoadFinish != null) {
      onFileDownLoadFinish!();
    }
  }
}

class FileTransferSocketClient {
  static int serverPort = 0;
  static FileTransferSocketClient instance = FileTransferSocketClient();

  static Map<String, FileDownLoadCallBack> map = HashMap();

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

  /// 是否正字啊下载中
  static bool isDownLoading(String cloudFilePath) {
    return map.containsKey(cloudFilePath);
  }

  static void addListener(String cloudFilePath,
      {OnFileDownLoadProgress? onFileDownLoadProgress,
      OnFileDownLoadFinish? onFileDownLoadFinish}) {
    FileDownLoadCallBack? callBack = map[cloudFilePath];
    if (callBack != null) {
      callBack.onFileDownLoadProgress = onFileDownLoadProgress;
      callBack.onFileDownLoadFinish = onFileDownLoadFinish;
    }
  }

  static void removeListener(String cloudFilePath) {
    FileDownLoadCallBack? callBack = map[cloudFilePath];
    if (callBack != null) {
      callBack.onFileDownLoadProgress = null;
      callBack.onFileDownLoadFinish = null;
    }
  }

  /// 下载文件
  void downloadFile(
      Socket socket, String cloudFilePath, File file, int fileLength,
      {OnFileDownLoadProgress? onFileDownLoadProgress,
      OnFileDownLoadFinish? onFileDownLoadFinish}) async {
    if (!await file.exists()) {
      file.create();
    }
    FileDownLoadCallBack callBack =
        FileDownLoadCallBack.add(onFileDownLoadProgress, onFileDownLoadFinish);
    if (onFileDownLoadProgress != null) {
      map[cloudFilePath] = callBack;
    }
    int count = 0;
    var sink = file.openWrite();
    socket.listen((List<int> event) {
      sink.add(event);
      // 监听文件下载
      count += event.length;
      callBack.onProgress(fileLength, count);
      print('================fileLength:${fileLength}  length:${count} 下载中');
    }, onDone: () {
      print('================下载完成');
      callBack.onProgress(fileLength, fileLength);
      callBack.onFinish();
      sink.close();
    }, onError: (e) {
      print('================下载失败');
      sink.close();
    });
  }
}
