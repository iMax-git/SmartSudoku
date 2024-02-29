package com.backtracking.smartsudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.TextView;

import com.backtracking.smartsudoku.models.Grid;

import java.util.Arrays;

public class GridLayoutActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);

        Bitmap bmp = Bitmap.createBitmap(300,300, Bitmap.Config.ARGB_8888);

        GridDrawer gd = new GridDrawer(bmp);
        gd.setNumColumns(3);
        gd.setNumRows(3);
        gd.draw();

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setBackground(new BitmapDrawable(getResources(), bmp));

        Grid grid = new Grid();
        for (int i=0; i<9; ++i) {
            for (int j=0; j<9; ++j) {
                grid.set((j*9+i), j, i);
            }
        }

        int id=R.id.cell00;
        for (int i=0; i<9; ++i) {
            for (int j=0; j<9; ++j) {
                TextView tv = findViewById(id);
                tv.setText(String.valueOf(grid.get(j, i)));
                ++id;
            }
        }

        final int[] row3 = grid.getRow(3);
        final int[] col4 = grid.getColumn(4);
        final int[] reg0 = grid.getRegion(0);
        final int[] reg8 = grid.getRegion(8);

        System.out.println("row3");
        Arrays.stream(row3).asLongStream().forEach(c -> System.out.printf("%d ", c));

        System.out.println("\ncol4");
        Arrays.stream(col4).asLongStream().forEach(c -> System.out.printf("%d ", c));

        System.out.println("\nreg0");
        Arrays.stream(reg0).asLongStream().forEach(c -> System.out.printf("%d ", c));

        System.out.println("\nreg8");
        Arrays.stream(reg8).asLongStream().forEach(c -> System.out.printf("%d ", c));

        System.out.flush();
    }

}