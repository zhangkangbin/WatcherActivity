package com.zkb.watcheractivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class A_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a);
        ActivityMap.get().add("A_Activity", this);
        findViewById(R.id.buttonA).setOnClickListener(v -> {
            startActivity(new Intent(A_Activity.this, B_Activity.class));
        });
    }
}
