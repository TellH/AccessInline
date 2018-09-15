# AccessInline
[English Wiki](https://github.com/TellH/AccessInline/blob/master/README.md)

这是一个在字节码层面内联access$方法的Android gradle 插件。

[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/tellh/maven/inline-plugin/images/download.svg)](https://bintray.com/tellh/maven/inline-plugin/_latestVersion)

## 用途

我们来看看下面这种case：
``` java
public class Foo {
    private int mValue;

    private void doStuff(int value) {
        System.out.println("Value is " + value);
    }
    
    private class Inner {
        void stuff() {
            Foo.this.doStuff(Foo.this.mValue);
        }
    }
}
```

JVM认为从内部类`Foo$Inner`直接访问外部类`Foo`的私有方法是非法的，因为`Foo`和`Foo$Inner` 是两个不同的类，尽管Java语法里允许内部类直接访问外部类的私有成员。编译器为了能实现这种语法，会在编译期生成以下方法：

```java
/*package*/ static int Foo.access$100(Foo foo) {
    return foo.mValue;
}
/*package*/ static void Foo.access$200(Foo foo, int value) {
    foo.doStuff(value);
}
```

当内部类需要访问外部类的`mValue` 或调用`doStuff()`方法时，会借助这些静态方法来间接实现。

然而，编译器的这种语法糖对方法数的增加还是很可观的。而这个gradle插件会内联这些方法来减少release版本apk的方法数。

## 用法

```groovy
    dependencies {
        classpath "com.tellh:inline-plugin:1.0.0-beta2"
    }

apply plugin: 'com.tellh.access_inline'
access_inline {
    logLevel "INFO"
    enableInDebug false
}
```



## 优化效果

在我们的项目里，每次build需要花费10秒左右。

发版apk的方法数有253108，插件能减掉7601个方法，apk大小减少0.2MB左右~



## Thanks

- ASM
- [lancet](https://github.com/eleme/lancet)



## License

Apache 2.0