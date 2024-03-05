package com.backtracking.smartsudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.GridLayout;
import android.widget.TextView;

import com.backtracking.smartsudoku.models.Grid;

import java.util.Arrays;

public class GridLayoutActivity extends AppCompatActivity {

    GridLayout view;
    Grid grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);
        this.view = findViewById(R.id.gridLayout);


        // TODO: remove this code
        // insert some sudoku values in the model to test the display and the methods of the model
        this.grid = new Grid();
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                grid.set((j * 9 + i), i, j);
            }
        }

        // TODO: remove this code
        // testing Grid model methods.
        // See output in the Logcat tab to check the numbers.
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

    @Override
    protected void onResume() {
        super.onResume();

        // insert the numbers on the view
        // TODO: use the ViewGroup API of GridLayout to iterate through cell views,
        //       don't use this hacky increment on a view id.
        int id = R.id.cell00;
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                TextView tv = findViewById(id);
                tv.setText(String.valueOf(grid.get(j, i)));
                ++id;
            }
        }

        // TODO: strangely this.view.getWidth() returns 0 at this point in time.
        //       Find a way to compute or pass the desired size for the grid,
        //       or find where to call view.getWidth after its size is known.
        // assume the view is square sized!
        this.drawBackground(300);
    }


    private void drawBackground(final int sideSize) {
        Bitmap bmp = Bitmap.createBitmap(sideSize,sideSize, Bitmap.Config.ARGB_8888);

        // the canvas will draw on the bitmap
        Canvas canvas = new Canvas(bmp);

        canvas.drawColor(Color.WHITE);

        final float cellBorderWidth = 1.f;
        final float regBorderWidth = 2.f;
        final float cellSize = (sideSize - 3*regBorderWidth) / 9.f;

        Paint painter = new Paint();
        painter.setStyle(Paint.Style.STROKE);

        /*
         * draw cells
         */
        painter.setStrokeWidth(cellBorderWidth);

        for (int i=1; i < 9; ++i) {
            canvas.drawLine(
                    i * cellSize + regBorderWidth,
                    regBorderWidth,
                    i * cellSize + regBorderWidth,
                    sideSize - 2*regBorderWidth,
                    painter
            );
        }

        for (int i=1; i < 9; ++i) {
            canvas.drawLine(
                    regBorderWidth,
                    i * cellSize + regBorderWidth,
                    sideSize - 2*regBorderWidth,
                    i * cellSize + regBorderWidth,
                    painter
            );
        }

        /*
         * draw regions and grid border
         */
        final float regSize = 3*cellSize;
        painter.setStrokeWidth(regBorderWidth);

        for (int i=0; i <= 3; ++i) {
            canvas.drawLine(
                    i * regSize + regBorderWidth,
                    regBorderWidth,
                    i * regSize + regBorderWidth,
                    sideSize - 2*regBorderWidth,
                    painter
            );
        }

        for (int i=0; i <= 3; ++i) {
            canvas.drawLine(
                    regBorderWidth,
                    i * regSize + regBorderWidth,
                    sideSize - 2*regBorderWidth,
                    i * regSize + regBorderWidth,
                    painter
            );
        }

        this.view.setBackground(new BitmapDrawable(getResources(), bmp));
    }

}