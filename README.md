
# AccessInline
An Android gradle plugin to inline methods that start with the prefix 'access$' in bytecode.
[![License](https://img.shields.io/badge/license-Apache%202-green.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Download](https://api.bintray.com/packages/tellh/maven/inline-plugin/images/download.svg) ](https://bintray.com/tellh/maven/inline-plugin/_latestVersion)
## What is it used for

Consider the following class definition:
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

The JVM considers direct access to `Foo`'s private members from `Foo$Inner` to be illegal because `Foo` and `Foo$Inner` are different classes, even though the Java language allows an inner class to access an outer class' private members. To bridge the gap, the compiler generates a couple of synthetic methods:

```java
/*package*/ static int Foo.access$100(Foo foo) {
    return foo.mValue;
}
/*package*/ static void Foo.access$200(Foo foo, int value) {
    foo.doStuff(value);
}
```

The inner class code calls these static methods whenever it needs to access the `mValue` field or invoke the `doStuff()`method in the outer class.

However, This compiler syntactic sugar would substantially increase the method count. And this gradle plugin is used for inlining those methods to decrease the method count of release apk.



## How to use it

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



## Optimization

In our project, this plugin would cost 10 seconds each build.

Our release apk has 253108 methods, the plugin would optimize 7601 methods, decrease apk size up to 0.2MB.



## License

Apache 2.0
