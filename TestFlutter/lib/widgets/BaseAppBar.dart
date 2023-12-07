
import 'package:flutter/material.dart';

// AppBar BaseAppBar(context,{title="",bool isShowLeading=true,actions,popAction}){
//
//   var leadingWidget;
//   if(isShowLeading){
//     leadingWidget=GestureDetector(
//       onTap:() {
//         if(popAction != null){
//           popAction();
//         }
//         Navigator.pop(context);
//       },
//       child: Icon(Icons.arrow_back_ios,color:mColors.c_666666,),
//     );
//   }else{
//     leadingWidget=Container();
//   }
//   return AppBar(
//     elevation: 0,
//     backgroundColor: Colors.white,
//     title: Text(title,style: titleTextStyle,),
//     centerTitle: true,
//     actions: actions,
//     automaticallyImplyLeading: isShowLeading,
//     leading: leadingWidget,
//     iconTheme: IconThemeData(
//       color: Colors.grey,
//     ),
//   );
// }