package com.tellh.accessinline;

import android.util.Log;

public class OuterClass {
    public static final String TAG = OuterClass.class.getSimpleName();
    private int a = 10;
    private double d = 10;
    public String s = "abc";
    protected String protectedField = TAG;

    private String haha() {
        return "haha";
    }

    private void enen() {
    }

    protected void hehe() {
        Log.d(TAG, "hehe() called. ");
    }

    public void publicMethodInSuper() {
        Log.d(TAG, "publicMethodInSuper() called");
    }

    public void run() {
        Inner inner = new Inner();
        inner.ha();
        inner.en();
        inner.he();
        inner.getA();
        inner.getD();
        inner.getStr();
    }

    protected void p() {
        Log.d(TAG, "p was called.");
    }

    protected static void protectedStatic() {
        Log.d(TAG, "protectedStatic() called");
    }

    public static void publicStatic() {
        Log.d(TAG, "publicStatic() called");
    }

    class Inner {
        final String TAG = Inner.class.getSimpleName();

        private void ha() {
            haha();
        }

        private void en() {
            enen();
        }

        private int getA() {
            return a;
        }

        private double getD() {
            return d;
        }

        private String getStr() {
            return s;
        }

        private void he() {
            Log.d(TAG, "he() called");
            hehe();
        }
    }
}