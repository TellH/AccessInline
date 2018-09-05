package com.tellh.accessinline;

import android.util.Log;

public class Derive extends OuterClass {

    public static final String TAG = Derive.class.getSimpleName();

    @Override
    protected void hehe() {
        super.hehe();
        Log.d(TAG, "hehe() called");
        enen();
        new Inner().run();
        get();
    }

    private void enen() {
        Log.d(TAG, "enen() called");
    }

    private BaseConsumer get() {
        return new BaseConsumer() {
            @Override
            public void onProgressUpdateImpl(float progress) {
                Derive.this.p();
            }
        };
    }

    class Inner {
        void run() {
            p();
        }
    }

    interface BaseConsumer {
        void onProgressUpdateImpl(float progress);
    }
}
