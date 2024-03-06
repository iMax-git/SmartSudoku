package com.backtracking.smartsudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backtracking.smartsudoku.models.Grid;
import com.backtracking.smartsudoku.models.SudokuGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GridLayoutActivity extends AppCompatActivity {

    GridLayout view;
    Grid grid;

    boolean DEBUG_CELL = false;

    List<TextView> cells = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout);
        this.view = findViewById(R.id.gridLayout);


        // TODO: remove this code
        // insert some sudoku values in the model to test the display and the methods of the model
        // Created before the view for lambda onClickListener
        SudokuGenerator generator = new SudokuGenerator();
        generator.removeNumbers(20); // Ajustez le nombre d'essais pour augmenter ou diminuer la difficulté
        this.grid = generator.getGrid();

        for (int i = 0; i < 81 ; ++i) {
            TextView tv = new TextView(this);
            tv.setId(i);
            tv.setText("X");
            tv.setTextSize(20);
            tv.setGravity(1);
            if (DEBUG_CELL) {
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.BLACK);
            } else {
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundColor(Color.WHITE);
            }
            tv.setWidth(44);
            tv.setHeight(44);
            tv.setPadding(0, 0, 0, 0);

            tv.setOnClickListener(v -> {
                int id = v.getId();
                int x = id % 9;
                int y = id / 9;

                // Show a message with number of the cell clicked
                Toast.makeText(this, "Cell " + id, Toast.LENGTH_SHORT).show();

                // Show a dialog to enter a number
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Modifier la case");
                builder.setMessage("Entrez un chiffre entre 1 et 9 \n Numéro de la case : " + grid.get(x,y) + ".");

                // Create Layout for the form
                LinearLayout ll_form = new LinearLayout(this);
                ll_form.setOrientation(LinearLayout.VERTICAL);

                // Create EditText
                TextView tv_form = new TextView(this);
                tv_form.setText(R.string.chiffre);
                ll_form.addView(tv_form);

                EditText et_form = new EditText(this);
                ll_form.addView(et_form);

                builder.setView(ll_form);

                builder.setPositiveButton("Valider", (dialog, which) -> {
                    String value = et_form.getText().toString();
                    if (value.matches("[1-9]")) {
                        grid.set(Integer.parseInt(value), x,y); // INFO: Save the value in the model (To avoid to be lost when the view is redrawn)
                        tv.setText(value);

                    } else {
                        Toast.makeText(this, R.string.choice_number, Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton(R.string.annuler, (dialog, which) -> {
                    dialog.cancel();
                });

                builder.show();
            });

            this.cells.add(tv);
            this.view.addView(tv);
        }




        // TODO: remove this code
        // testing Grid model methods.
        // See output in the Logcat tab to check the numbers.
        final List<Integer> row3 = grid.getRow(3);
        final List<Integer> col4 = grid.getColumn(4);
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

        //int id = R.id.cell0;
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                TextView tv = this.cells.get(i * 9 + j);

                tv.setText(String.valueOf(grid.get(j, i)));
                //++id;
            }
        }


        // TODO: strangely this.view.getWidth() returns 0 at this point in time.
        //       Find a way to compute or pass the desired size for the grid,
        //       or find where to call view.getWidth after its size is known.
        // assume the view is square sized!
        this.drawBackground(300);
    }


    /**
     * Draw the background of the grid.
     * @param sideSize the size of the grid (in pixels ?)
     */
    private void drawBackground(final int sideSize) {
        Bitmap bmp = Bitmap.createBitmap(sideSize,sideSize, Bitmap.Config.ARGB_8888);

        // the canvas will draw on the bitmap
        Canvas canvas = new Canvas(bmp);

        canvas.drawColor(Color.WHITE);

        final float cellBorderWidth = 1.f; // BORDER WIDTH & HEIGHT OF CELL ?
        final float regBorderWidth = 2.f; // BORDER WIDTH & HEIGHT OF GRID ?
        final float cellSize = (sideSize - 3*regBorderWidth) / 9.f;

        Paint painter = new Paint();
        painter.setStyle(Paint.Style.STROKE);

        /*
         * draw cells
         */
        painter.setStrokeWidth(cellBorderWidth);

        for (int i=1; i < 9; ++i) {
            // vertical lines
            canvas.drawLine(
                    i * cellSize + regBorderWidth,
                    regBorderWidth,
                    i * cellSize + regBorderWidth,
                    sideSize - 2*regBorderWidth,
                    painter
            );
        }

        for (int i=1; i < 9; ++i) {
            // horizontal lines
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
            // vertical lines MORE LARGE
            canvas.drawLine(
                    i * regSize + regBorderWidth,
                    regBorderWidth,
                    i * regSize + regBorderWidth,
                    sideSize - 2*regBorderWidth,
                    painter
            );
        }

        for (int i=0; i <= 3; ++i) {
            // horizontal lines MORE LARGE
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