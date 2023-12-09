import 'package:flutter/material.dart';
import 'package:testflutter/Routers.dart';
import 'package:testflutter/pages/home/Home.dart';
import 'package:testflutter/pages/home/TestHome.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      routes: getStaticRoutes(), // 静态路由,无法传值。
      onGenerateRoute: getOnGenerateRoute(), // 动态路由(路由拦截，拦截后传值)
      home: TestHome(), // 主页
    );
  }
}
