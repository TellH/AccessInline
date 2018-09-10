package com.tellh.accessinline.other;

import com.tellh.accessinline.OuterClass;

public class SubClassInOtherPackage extends OuterClass {
    public SubClassInOtherPackage() {
        Inner inner = new Inner();
        inner.invokeProtectMethod();
    }

    class Inner {
        void invokeProtectMethod() {
            hehe();
        }
    }
}
