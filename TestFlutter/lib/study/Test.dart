import 'dart:js_util';
import 'dart:ui';

class Test {
  Test(String _password, this._userName, {str2}) {
    this._password = _password;
  }

  // 加下划线是属于private变量，不加则是public
  String _userName;

  // 必须加?,因为必须初始化。没有init自动去赋值。
  String? _password;

  void a() {
    // var 确定后是不能变的。
    var b = 10;
    // b="abc";
    // 但是var如果未初始化是可以改的
    var c;
    c = "abc";
    c = 10;

    // dynamic类型是可以变的。
    dynamic a = 10;
    a = "avc";
    a = 1;

    double bb = 10.2;

    num abc = b * a;
    Object o = 1;

    assert(o != null);

    final s = 1234;
    const s2 = 1231;
    // dart中没有数组，下面这个其实是list，它会自动转为list。可以用arr.add(),也可以arr[0],这两操作是一样的。
    final arr = [1, 2];
    const arr2 = [1, 2];
    arr[0] = 1; // final是可以的。
    arr2[0] = 1; // 这样是不行的，const不能修改内容
    // s = 1;
    // s2=2;、
    // 数组排序
    print("arr2排序前：$arr2");
    arr2.sort((a, b) => b.compareTo(a));
    print("arr2排序后：$arr2");

    // 字符串，段落
    String str = """
      asdasd
    """;
    String str1 = '''
      段落，支持换行
      换行了。
    ''';
    // 它相当于省略了中间的+
    String str2 = '第一个字符串' '第二个字符串' '第三个字符串' "第四个字符串";
    // 内外部可以分开。内部是字符串，外部是特殊字符。
    String str3 = "外面'内部'外部";
    String str4 = '外面"内部"外部';
    // 它相当于省略了中间的+
    String str5 = '第一段'
        '第二段'
        '第三段'
        '第四段'
        '第五段';
    // 这点跟kotlin一样，抄VUE。
    String str6 = "可以写代码:${str1 + str2}";
    // 字符串前面加r可以忽略特殊字符
    String str7 = r"可以忽略特殊字符\n\\\\";

    // map
    var map = {1, 2, 3, "", 2.1};
    Map map2 = Map<String, String>();
    Map map3 = {};
    map2["0"] = 1;

    // unicode编码方案,市面上的表情啥的都可以支持。可以展示不同编码图标。字符表情.(emoji等)
    Runes runs = Runes("\u{1f605}");

    double d1 = 10.1;
    int i1 = d1 as int;

    // 链式调用,没有返回值，可以调用多个方法。都是调用i1的，没有返回值。
    i1
      ..toDouble()
      ..toInt()
      ..toString();

    // try catch on
    try {
      String? str = null;
      str!.length;
    } catch (e) {
      // catch和java不一样，不需要异常类型，因为所有异常类型都走这里，如果需要特殊异常处理看下面
      print('$e');
    }
    try {
      String? str = null;
      str!.length;
    } on Exception {
      // 如果这个异常走这里
      print("==============");
    } catch (e) {
      // 不管什么异常类型，都走这里
      print('$e');
    }

    // 匿名函数
    var fun1 = () {
      print('---------------');
    };
    // 简化
    Function fun2 = () => print('---------------');
    // 可以直接定义
    fun3() {}
    MyFun fun4 = (int a) {
      print('--------------');
    };
    fun1();
    fun2();
    fun3();
    fun4(1);

    a1("", str2: "", str: "");
  }

  // 方法重载用{}
  void a1(String? strnul, {String? str2, str}) {
    // 空安全
    strnul?.toString();
  }
}

typedef MyFun(int a);

/// 命名构造 = builder构造器
class Rect {
  late int _width;
  late int _height;

  Rect(this._width, this._height);

  Rect.widthSet(this._width);

  Rect.heightSet(this._height);

  Rect.allSet2()
      : _width = 1,
        _height = 2;

  Rect.allSet() {
    _width = 1;
    _height = 2;
  }

  set setHeight(int value) {
    _height = value;
  }

  get getHeight {
    return _height;
  }
}

/// 没有interface，只有abstract
/// extends with implements 优先级是先with->extends->implements，内部是从后向前，比如（class EA extends A with A1,A2 implements A3,A4 ,那么A2->A1->A->A4->A3）
abstract class A {
  void a() {
    print("==============A a");
  }
}

abstract class B {}

class Rect2 {
  void a() {
    print("==============Rect2 a");
  }
}
// mixin+with叫混入的使用方法
mixin class Rect3 {
  void a() {
    print("==============Rect2 a");
  }
}

class E extends Rect2 with Rect3{
  static int aaaaa=10;
  @override
  void a() {}
}
class Ea extends Rect implements A, B, Rect2 {
  Ea(super.width, super.height);

  @override
  void a() {}
}

/// 扩展函数
extension RectExtension on Rect {
  void aa() {}
}

// 使用，allSet是一个构造函数。
void testRect() {
  Rect rect = Rect.allSet();
  rect.setHeight = 10;
  int height = rect.getHeight;
  rect.aa();
}

/// 不需要继承所有方法，直接实现noSuchMethod即可，这样它就会报错，比如用dynamic不检测，到运行时就报错。
class MockList<T> implements List<T>{

  @override
  void add(T value) {
  }
  // 为了dynamic设计的
  @override
  dynamic noSuchMethod(Invocation invocation) => super.noSuchMethod(invocation);
}

void testMockList(){
  // 不做类型检查。
  dynamic object = int;
  // 这样就会报错noSuchMethodError,因为在每个对象里面都有一个noSuchMethod方法，所以使用dynamic的时候如果int调用add方法就没有这个方法，所以noSuchMethod是为了dynamic设计的
  object.add(10);
}

