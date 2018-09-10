package com.tellh.accessinline;

import android.util.Log;

public class Derive extends OuterClass {

    public static final String TAG = Derive.class.getSimpleName();
    protected String protectedField = TAG;

    public Derive() {
        Inner inner = new Inner();
        inner.invokeProtectedMethodFromSuper();
        inner.getProtectedFieldFromSuper();
        inner.invokeStaticMethod();
    }

    @Override
    protected void hehe() {
        Log.d(TAG, "hehe() called");
    }

    class Inner {
        void invokeProtectedMethodFromSuper() {
            Derive.super.hehe();
            Derive.this.p();
            Derive.super.publicMethodInSuper();
        }

        void invokeStaticMethod() {
            protectedStatic();
            publicStatic();
        }

        void getProtectedFieldFromSuper() {
            Log.d(TAG, "protectedField in super: " + Derive.super.protectedField);
            Log.d(TAG, "protectedField in subClass: " + Derive.this.protectedField);
        }
    }

}
