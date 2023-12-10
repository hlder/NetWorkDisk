
class TimeUtils{

  /// 时间格式化
  static String dateFormat(int timeStamp) {
    DateTime nowTime = DateTime.now();
    DateTime todayTime =
    DateTime(nowTime.year, nowTime.month, nowTime.day, 0, 0, 0);
    DateTime yesterdayTime = DateTime.fromMillisecondsSinceEpoch(
        todayTime.millisecondsSinceEpoch - 24 * 60 * 60 * 1000);
    DateTime lastModifiedTime =
    DateTime.fromMillisecondsSinceEpoch(timeStamp);

    if (timeStamp > todayTime.millisecondsSinceEpoch) {
      // 今天
      return "${_timeNumToString(lastModifiedTime.hour)}:${_timeNumToString(lastModifiedTime.minute)}";
    } else if (timeStamp > yesterdayTime.millisecondsSinceEpoch) {
      // 昨天
      return "昨天 ${_timeNumToString(lastModifiedTime.hour)}:${_timeNumToString(lastModifiedTime.minute)}";
    } else {
      // 显示年月日
      return "${lastModifiedTime.year}-${_timeNumToString(lastModifiedTime.month)}-${_timeNumToString(lastModifiedTime.day)}";
    }
  }

  static String _timeNumToString(int num) {
    if (num < 10) {
      return "0$num";
    }
    return "$num";
  }
}