package com.tellh.accessinline;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tellh.accessinline.other.SubClassInOtherPackage;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OuterClass outer = new OuterClass();
        outer.run();

        new Derive();
        new SubClass();
        new TextViewForTest(this);

        new SubClassInOtherPackage();
    }
}
