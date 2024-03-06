package com.backtracking.smartsudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnGridLayout = findViewById(R.id.btnGridLayout);
        btnGridLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, GridLayoutActivity.class);
            startActivity(intent);
        });

        Button btnTestGridView = findViewById(R.id.btnTestGridView);
        btnTestGridView.setOnClickListener(v -> {
            Intent intent = new Intent(this, TestGridViewActivity.class);
            startActivity(intent);
        });
    }
}