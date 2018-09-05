package com.tellh.accessinline;

import android.util.Log;
import android.util.Size;

public class OuterClass {
    public static final String TAG = OuterClass.class.getSimpleName();
    private int a = 10;
    private double d = 10;
    public String s = "abc";

    private String haha() {
        return "haha";
    }

    private void enen() {
    }

    protected void hehe() {
        Log.d(TAG, "hehe() called");
    }

    public void run() {
        Inner inner = new Inner();
        inner.ha();
        inner.en();
        inner.he();
        inner.getA();
        inner.getD();
        inner.getStr();

        VectorDrawableCompatState vectorDrawableCompatState = new VectorDrawableCompatState();

    }

    protected void p() {
        Log.d(TAG, "p was called.");
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

    private static class VectorDrawableCompatState {
        VPathRenderer mVPathRenderer;

        public VectorDrawableCompatState() {
        }

        public VectorDrawableCompatState(VectorDrawableCompatState copy) {
            if (copy != null) {
                mVPathRenderer = new VPathRenderer(copy.mVPathRenderer);
                if (copy.mVPathRenderer.mFillPaint != null) {
                    mVPathRenderer.mFillPaint = new Size(1, 1);
                    mVPathRenderer.multi(1D, "", 1L, 1);
                    mVPathRenderer.couple(1D, 1);
                }

            }
        }
    }

    private static class VPathRenderer {
        private Size mFillPaint;

        public VPathRenderer(VPathRenderer copy) {

        }

        private void multi(Double d, String s, long l, int i) {

        }

        private int couple(Double d, int i) {
            return i;
        }
    }

}