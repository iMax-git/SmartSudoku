package com.backtracking.smartsudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridLayout;

import com.backtracking.smartsudoku.models.Grid;

public class TestGridViewActivity extends AppCompatActivity {
    GridView view;
    Grid grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_grid_view);

        this.grid = new Grid();

        this.view = findViewById(R.id.gridView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.view.setModel(this.grid);
        this.view.invalidate(); // this should redraw the gridview. Why is it not drawn? I don't know!
    }
}