import 'package:flutter/material.dart';

class SelectDialog {
  final String title;
  final List<String> list;

  final BuildContext context;

  SelectDialog(this.context, this.title, this.list);

  Future<int?> show() async {
    return await showDialog(
        barrierDismissible: false,
        context: context,
        builder: (BuildContext context) {
          return Material(
            color: Colors.transparent,
            child: GestureDetector(
              onTap: (){
                Navigator.of(context).pop(null);
              },
              child: Container(
                width: double.infinity,
                height: double.infinity,
                color: Colors.transparent,
                child: Center(
                  child: Container(
                    margin: const EdgeInsets.only(left: 55, right: 55),
                    constraints: const BoxConstraints(
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
                        Text(title,style: const TextStyle(color: Color(0xff808080)),),
                        Column(
                          mainAxisSize: MainAxisSize.min,
                          children: listWidget(),
                        ),
                      ],
                    ),
                  ),
                ),
              ),
            ),
          );
        });
  }

  List<Widget> listWidget() {
    List<Widget> listWidget = [];
    for (int i = 0; i < list.length; i++) {
      String value = list[i];
      listWidget.add(TextButton(
          onPressed: () {
            Navigator.of(context).pop(i);
          },
          child: Container(
            constraints: const BoxConstraints(
              minWidth: 200
            ),
            alignment: Alignment.center,
            child: Text(
              value,
              style: const TextStyle(
                fontSize: 16,
                color: Colors.black,
              ),
            ),
          )));
    }
    return listWidget;
  }
}
