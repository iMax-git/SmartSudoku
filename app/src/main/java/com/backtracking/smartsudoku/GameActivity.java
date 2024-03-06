package com.backtracking.smartsudoku;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backtracking.smartsudoku.models.Grid;
import com.backtracking.smartsudoku.models.SudokuGenerator;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class GameActivity extends AppCompatActivity {

    static Clock clock = Clock.systemDefaultZone();

    public enum Difficulty {
        EASY, MEDIUM, HARD;
        public static Difficulty fromInt(int i) {
            return Difficulty.values()[i];
        }
    };

    GridLayout view;
    Grid grid;

    LinearLayout ll_number_list;

    boolean DEBUG_CELL = false;

    List<TextView> cells = new ArrayList<>();

    Integer SIZE;

    Stack<Grid> stateStack = new Stack<>();
    Stack<Grid> redoStateStack = new Stack<>();
    LocalDateTime timer;
    Difficulty difficulty;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int[] screenSize = getScreenSize();
        //SIZE = getScreenSize()[0] - (getScreenSize()[0] / 8); // TODO: Trouver une meilleure façon de calculer la taille de la grille
        SIZE = screenSize[0]-100;
        setContentView(R.layout.activity_grid_layout);
        this.view = findViewById(R.id.gridLayout);
        this.ll_number_list = findViewById(R.id.ll_number_list);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = SIZE;
        params.height = SIZE;
        this.view.setLayoutParams(params);

        // get difficulty from bundle
        final Intent intent = getIntent();
        this.difficulty = Difficulty.fromInt(intent.getIntExtra("difficulty", Difficulty.MEDIUM.ordinal()));

        // TODO: remove this code
        // insert some sudoku values in the model to test the display and the methods of the model
        // Created before the view for lambda onClickListener
        SudokuGenerator generator = new SudokuGenerator();

        switch (this.difficulty){
            case EASY:
                generator.removeNumbers(18);
                break;
            case MEDIUM:
                generator.removeNumbers(37);
                break;
            case HARD:
                generator.removeNumbers(56);
                break;
        }
        this.grid = generator.getGrid();
        for (int i = 0; i < 81 ; ++i) {
            TextView tv = new TextView(this);
            tv.setId(i);
            tv.setText(" ");
            tv.setTextSize(20);
            tv.setGravity(1);
            if (DEBUG_CELL) {
                tv.setTextColor(Color.WHITE);
                tv.setBackgroundColor(Color.BLACK);
            } else {
                tv.setTextColor(Color.BLACK);
                tv.setBackgroundColor(Color.TRANSPARENT);
            }
            int step = (SIZE / 9);
            tv.setWidth(step);
            tv.setHeight(step);
            tv.setPadding(0, 0, 0, 0);
            GridLayout.LayoutParams params1 = new GridLayout.LayoutParams();
            params1.setMargins(0, 0, 0, 0);
            tv.setLayoutParams(params1);

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


        for (int j = 0; j < 9; ++j) {
            TextView tv_number = new TextView(this);
            tv_number.setText(String.valueOf(j+1));
            tv_number.setTextSize(20);
            tv_number.setGravity(1);
            tv_number.setWidth(60);
            tv_number.setHeight(60);
            tv_number.setPadding(5, 5, 5, 5);

            if (Math.random() > 0.5) {
                tv_number.setBackgroundColor(Color.GRAY);
                tv_number.setTextColor(Color.BLACK);
            } else {
                tv_number.setBackgroundColor(Color.WHITE);
                tv_number.setTextColor(Color.BLACK);
            }

            // set margin
            GridLayout.LayoutParams params2 = new GridLayout.LayoutParams();
            params2.setMargins(5, 5, 5, 5);
            tv_number.setLayoutParams(params2);


            tv_number.setOnClickListener(v -> {
                Toast.makeText(this, "Number " + tv_number.getText(), Toast.LENGTH_SHORT).show();
                //tv.setText(tv_number.getText());
            });
            ll_number_list.addView(tv_number);

        }

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
                int nb = grid.get(j, i);
                if(nb!=0) {
                    tv.setText(String.valueOf(nb));
                }

            }
        }

        // TODO: strangely this.view.getWidth() returns 0 at this point in time.
        //       Find a way to compute or pass the desired size for the grid,
        //       or find where to call view.getWidth after its size is known.
        // assume the view is square sized!
        this.drawBackground(SIZE);
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

    /**
     * Get the screen size in pixels.
     * @return int[] {width, height}
     */
    private int[] getScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return new int[]{metrics.widthPixels, metrics.heightPixels};
    }


    public void startNewGame(final Difficulty difficulty) {
        this.stateStack.clear();
        this.redoStateStack.clear();
        this.timer = LocalDateTime.now();
    }

}
