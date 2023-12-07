
import 'package:flutter/material.dart';

class ImTextButton extends StatefulWidget {
  const ImTextButton(this._onPressed, this._text,{super.key});

  final VoidCallback? _onPressed;
  final String _text;

  @override
  State<ImTextButton> createState() => _ImTextButtonState();
}

class _ImTextButtonState extends State<ImTextButton> {
  @override
  Widget build(BuildContext context) {
    return Container(
      child: TextButton(onPressed: widget._onPressed, child: Text(widget._text)),
    );
  }
}
