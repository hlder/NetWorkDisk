import 'package:flutter/material.dart';

class LsButtonSure extends StatefulWidget {
  final String btnTitle;
  final VoidCallback? onPressed;
  final double width;
  final double height;
  final bool enable;

  const LsButtonSure(
      {super.key,
      this.btnTitle = "",
      this.onPressed,
      this.width = 0,
      this.height = 0,
      this.enable = true});

  @override
  LsButtonSureState createState() => LsButtonSureState();
}

class LsButtonSureState extends State<LsButtonSure> {
  double height = 0;
  double width = double.infinity;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    height = widget.height;
    width = widget.width;
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: widget.enable ? widget.onPressed : null,
      child: Container(
        margin: const EdgeInsets.only(left: 5, right: 5, top: 15, bottom: 5),
        decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: widget.enable
                  ? [
                      const Color.fromARGB(255, 255, 156, 140),
                      const Color.fromARGB(255, 255, 89, 89)
                    ]
                  : [
                      const Color.fromARGB(255, 255, 215, 208),
                      const Color.fromARGB(255, 255, 188, 188)
                    ],
              begin: Alignment.centerLeft,
              end: Alignment.centerRight,
            ),
            borderRadius: BorderRadius.circular(90.0),
            boxShadow: const [
              BoxShadow(
                color: Color.fromARGB(60, 255, 89, 89), //底色,阴影颜色
                offset: Offset(0, 2), //阴影位置,从什么位置开始
                blurRadius: 10, // 阴影模糊层度
                spreadRadius: 0, //阴影模糊大小
              )
            ]),
        height: height,
        width: width,
        padding: const EdgeInsets.only(top: 11, bottom: 12),
        child: Text(
          widget.btnTitle,
          style: const TextStyle(
            fontSize: 16,
            decoration: TextDecoration.none,
            color: Colors.white,
            fontWeight: FontWeight.bold,
          ),
          textAlign: TextAlign.center,
        ),
      ),
    );
  }
}
