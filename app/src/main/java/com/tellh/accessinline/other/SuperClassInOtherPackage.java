package com.tellh.accessinline.other;

import android.util.Log;

public class SuperClassInOtherPackage {
    public static final String TAG = SuperClassInOtherPackage.class.getSimpleName();

    protected String protectedField = TAG;

    protected void protectedMethod() {
        Log.d(TAG, "protectedMethod() called");
    }

    protected static void protectedStatic() {
        Log.d(TAG, "protectedStatic() called");
    }

    public static void publicStatic() {
        Log.d(TAG, "publicStatic() called");
    }
}
