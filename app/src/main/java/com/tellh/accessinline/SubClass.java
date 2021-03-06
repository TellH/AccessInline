package com.tellh.accessinline;

import android.util.Log;

import com.tellh.accessinline.other.Ancestor0;
import com.tellh.accessinline.other.SuperClassInOtherPackage;

public class SubClass extends SuperClassInOtherPackage {
    public static final String TAG = SubClass.class.getSimpleName();

    public SubClass() {
        Inner inner = new Inner();
        inner.getProtectedFieldFromSuper();
        inner.invokeProtectedMethodFromSuper();
        inner.invokeStaticMethod();
    }

    class Inner {
        void invokeProtectedMethodFromSuper() {
            protectedMethod();
            assert call().equals(Ancestor0.TAG);
        }

        void getProtectedFieldFromSuper() {
            Log.d(TAG, "protectedField = " + protectedField);
        }

        void invokeStaticMethod() {
            protectedStatic();
            publicStatic();
        }
    }
}
