import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:testflutter/beans/ServerBean.dart';
import 'package:testflutter/pages/home/HomeServerItem.dart';

class Home extends StatefulWidget {
  Home({super.key});

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  void a() async{
    var channel = const MethodChannel("flutter_plugin_base");
    var result = await channel.invokeMethod("getPlatformVersion");
    print('-------------------------a:result:$result');
  }
  @override
  Widget build(BuildContext context) {
    a();
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: const Text("网盘"),
      ),
      body: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          //   31099
          HomeServerItem(ServerBean("127.0.0.1", 9584, "小米", "mi10", ""))
        ],
      ),
    );
  }
}