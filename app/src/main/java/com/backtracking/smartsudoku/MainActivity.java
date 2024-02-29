package com.backtracking.smartsudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Intent intent = new Intent(this, GridLayoutActivity.class);
        startActivity(intent);

//        GridView pixelGrid = new GridView(this);
//        pixelGrid.setNumColumns(4);
//        pixelGrid.setNumRows(6);
//        setContentView(pixelGrid);
    }
}