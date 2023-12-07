import 'dart:convert';

class FileBean {
  String name; // 文件名
  String absolutePath; // 文件绝对路径
  String suffix; // 文件后缀名
  bool isDirectory; // 是否是文件夹

  int fileLength; // 文件大小
  int lastModified; // 最近修改的时间

  FileBean(this.name, this.absolutePath, this.suffix, this.isDirectory,
      this.fileLength, this.lastModified);

  @override
  String toString() {
    return 'FileBean{name: $name, absolutePath: $absolutePath, suffix: $suffix, isDirectory: $isDirectory, fileLength: $fileLength, lastModified: $lastModified}';
  }

  static FileBean fromJson(Map<String, dynamic> map) {
    return FileBean(
        map["name"], map["absolutePath"], map["suffix"], map["isDirectory"],map["fileLength"],map["lastModified"]);
  }

  static List<FileBean> fromJsonArr(List<dynamic> list) {
    List<FileBean> newList = [];
    for (var map in list) {
      newList.add(fromJson(map));
    }
    return newList;
  }
}
