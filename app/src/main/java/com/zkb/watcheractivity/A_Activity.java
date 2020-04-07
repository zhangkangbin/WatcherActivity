package com.zkb.watcheractivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class A_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        ActivityMap.get().add("A_Activity", this);
    }
}
