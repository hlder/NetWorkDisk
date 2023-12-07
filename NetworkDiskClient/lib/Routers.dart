import 'package:flutter/cupertino.dart';
import 'package:testflutter/beans/FileManagerBean.dart';
import 'package:testflutter/pages/errorpage/ErrorPage.dart';
import 'package:testflutter/pages/filemanager/FileManagerPage.dart';
import 'package:testflutter/pages/home/Home.dart';

// 静态路由
Map<String, WidgetBuilder> getStaticRoutes() {
  return {
    "Home": (context) => Home(),
  };
}

///配置动态路由，自定义的builder方法对象
typedef RouteOnePageBuilder = Widget Function(
    BuildContext context, Object? arguments);

Map<String, RouteOnePageBuilder> dynamicRoutes = {
  Routers.fileManagerPage : (context, arguments) => FileManagerPage(arguments as FileManagerBean)
};

// 路由拦截，动态路由
RouteFactory getOnGenerateRoute() {
  return (RouteSettings settings) {
    String? routeName = settings.name;
    var args = settings.arguments;

    return CupertinoPageRoute(builder: (context) {
      if (dynamicRoutes.containsKey(routeName)) {
        RouteOnePageBuilder? builder = dynamicRoutes[routeName];
        if (builder != null) {
          return builder(context, args);
        }
      }
      return const ErrorPage();
    });
  };
}

class Routers{
  static const String fileManagerPage = "fileManagerPage";
}