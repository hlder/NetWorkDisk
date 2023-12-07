import 'package:flutter/material.dart';
import 'package:testflutter/beans/ServerBean.dart';
import 'package:testflutter/pages/home/HomeServerItem.dart';

class Home extends StatefulWidget {
  Home({super.key});


  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: const Text("网盘"),
      ),
      body: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          HomeServerItem(ServerBean("192.168.1.18",20001,"小米","mi10",""))
        ],
      ),
    );
  }
}
