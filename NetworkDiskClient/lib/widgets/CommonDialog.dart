
import 'package:flutter/material.dart';

import 'LsButtonSure.dart';

class CommonDialog  {
  final String title;
  final String content;
  final String sureText;
  final String grayText;

  final BuildContext context;
  CommonDialog(this.context,{this.title = "标题", this.content = "内容",this.sureText = '确认',this.grayText = '取消'});

  Future <String> showMyDialog() async{
    return await showDialog(
        barrierDismissible: false,
        context: context,
        builder: (BuildContext context) {
          return Material(
            color: Colors.transparent,
            child: Center(
              child: Container(
                margin: const EdgeInsets.only(left: 55,right: 55),
                constraints: const BoxConstraints(
                  minHeight: 214,
                ),
                decoration: const BoxDecoration(
                  color: Colors.white,
                  borderRadius: BorderRadius.all(Radius.circular(15)),
                ),
                // width: 285,
                // height: 214,
                child: Column(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    Container(
                      margin: const EdgeInsets.only(top: 25),
                      child: Text(
                        title,
                        style: const TextStyle(fontSize: 18, color: Color(0xFF333333),fontWeight: FontWeight.bold,decoration: TextDecoration.none),
                      ),
                    ),
                    Container(
                      margin: const EdgeInsets.all(15),
                      child: Text(
                        content,
                        textAlign: TextAlign.center,
                        style: const TextStyle(fontSize: 13, color: Color(0xFF999999),decoration: TextDecoration.none),
                      ),
                    ),
                    Container(
                      margin: const EdgeInsets.only( left: 35, right: 35),
                      child: LsButtonSure(
                        btnTitle: sureText,
                        enable: true,
                        onPressed: ()=>{
                          Navigator.of(context).pop('OK'),
                        },
                      ),
                    ),
                    TextButton(
                        onPressed: () {
                          Navigator.of(context).pop('Cancel');
                        },
                        child: Text(
                          grayText,
                          style: const TextStyle(
                            fontSize: 16,
                            color: Color(0xFFCCCCCC),
                          ),
                        )),
                  ],
                ),
              ),
            ),
          );
        });

  }


}
