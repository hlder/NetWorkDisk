import 'dart:convert';

class SocketBaseMessage {
  int version = 0;

  int code;

  String message;

  SocketBaseMessage(this.code, this.message);
  SocketBaseMessage.version(this.version, this.code, this.message);

  Map toJson() {
    return {
      "version": version,
      "code": code,
      "message": message,
    };
  }

  String toJsonString() {
    return jsonEncode(toJson());
  }

  static SocketBaseMessage fromJson(String json){
    Map<String, dynamic> map = jsonDecode(json);
    return SocketBaseMessage.version(
      map["version"],map["code"],map["message"]
    );

  }
}
