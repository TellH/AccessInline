package com.tellh.accessinline;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

public class TextViewForTest extends TextView {
    public static final String TAG = TextViewForTest.class.getSimpleName();

    public TextViewForTest(Context context) {
        super(context);
        Inner inner = new Inner();
        inner.getProtectedFieldFromSuper();
        inner.invokeProtectedMethodFromSuper();
    }

    class Inner {
        void invokeProtectedMethodFromSuper() {
            Log.d(TAG, "getDefaultEditable = " + getDefaultEditable());
        }

        void getProtectedFieldFromSuper() {
            Log.d(TAG, "VIEW_LOG_TAG: " + VIEW_LOG_TAG);
        }
    }
}