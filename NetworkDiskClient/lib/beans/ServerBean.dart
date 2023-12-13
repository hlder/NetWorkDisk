
class ServerBean {
  String ip;
  int port;
  int filePort;
  int preViewImagePort;
  String phoneName; // 手机名称
  String phoneModel;
  String filePath;

  ServerBean(this.ip, this.port, this.filePort, this.preViewImagePort, this.phoneName, this.phoneModel,
      this.filePath); // 默认打开的文件目录

}
