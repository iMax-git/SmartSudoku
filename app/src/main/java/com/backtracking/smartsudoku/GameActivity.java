package com.backtracking.smartsudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backtracking.smartsudoku.models.ImmutableGrid;
import com.backtracking.smartsudoku.models.SudokuGenerator;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class GameActivity extends AppCompatActivity {

    public enum Difficulty {
        EASY, MEDIUM, HARD;
        public static Difficulty fromInt(int i) {
            return Difficulty.values()[i];
        }
    }

    private static final Clock clock = Clock.systemDefaultZone();

    GridLayout gridView;

    LinearLayout ll_number_list;

    Button btnUndo, btnRedo;

    boolean DEBUG_CELL = false;

    List<TextView> cells = new ArrayList<>();

    Integer SIZE;

    Stack<ImmutableGrid> stateStack;
    Stack<ImmutableGrid> redoStateStack;
    LocalDateTime timer;
    Difficulty difficulty;

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int[] screenSize = getScreenSize();
        //SIZE = getScreenSize()[0] - (getScreenSize()[0] / 8); // TODO: Trouver une meilleure façon de calculer la taille de la grille
        SIZE = screenSize[0]-100;
        setContentView(R.layout.activity_game);
        this.gridView = findViewById(R.id.gridLayout);
        this.ll_number_list = findViewById(R.id.ll_number_list);
        this.btnRedo = findViewById(R.id.btnRedo);
        this.btnUndo = findViewById(R.id.btnUndo);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = SIZE;
        params.height = SIZE;
        this.gridView.setLayoutParams(params);

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
                builder.setMessage("Entrez un chiffre entre 1 et 9 \n Numéro de la case : " + getGrid().get(x,y) + ".");

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
                        this.replaceNumber(x, y, Integer.parseInt(value)); // INFO: Save the value in the model (To avoid to be lost when the gridView is redrawn)
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
            this.gridView.addView(tv);
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

        this.stateStack = new Stack<>();
        this.redoStateStack = new Stack<>();

        this.settings = getSharedPreferences("settings", 0);
        this.difficulty = Difficulty.fromInt(this.settings.getInt("difficulty", Difficulty.MEDIUM.ordinal()));

        if (savedInstanceState == null) {
            startNewGame(this.difficulty);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        drawGrid();
        drawBackground(SIZE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor settingsEditor = this.settings.edit();
        settingsEditor.putInt("difficulty", difficulty.ordinal());
        settingsEditor.apply();
    }


    @Override
    protected void onStop() {
        super.onStop();
        // TODO: don't save states if the active grid is won
        SharedPreferences saveStore = getSharedPreferences("save", 0);
        SharedPreferences.Editor storeEditor = settings.edit();
        storeEditor.putString("states", serializeState(stateStack));
        storeEditor.putString("redoStates", serializeState(redoStateStack));
        storeEditor.apply();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        SharedPreferences saveStore = getSharedPreferences("save", 0);
        String states = saveStore.getString("states", null);
        String redoStates = saveStore.getString("redoStates", null);
        if (states!=null && redoStates!=null) {
            this.stateStack = deserializeState(states);
            this.redoStateStack = deserializeState(redoStates);
        }
    }


    protected void drawGrid() {
        ImmutableGrid grid = getGrid();
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                TextView tv = this.cells.get(i * 9 + j);
                int nb = grid.get(j, i);
                if (nb != 0) {
                    tv.setText(String.valueOf(nb));
                } else {
                    tv.setText("");
                }
            }
        }
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

        this.gridView.setBackground(new BitmapDrawable(getResources(), bmp));
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
        this.difficulty = difficulty;
        this.redoStateStack.clear();
        this.stateStack.clear();

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
        this.stateStack.push(generator.getGrid());

        this.timer = LocalDateTime.now();
    }


    public void replaceNumber(int x, int y, int value) {
        stateStack.push(stateStack.peek().set(x, y, value));
        redoStateStack.clear();
        btnUndo.setEnabled(true);
        btnRedo.setEnabled(false);
        drawGrid();
    }


    public void undo(View v) {
        redoStateStack.push(stateStack.pop());
        btnRedo.setEnabled(true);
        btnUndo.setEnabled(stateStack.size()>1);
        drawGrid();
    }


    public void redo(View v) {
        stateStack.push(redoStateStack.pop());
        btnUndo.setEnabled(true);
        btnRedo.setEnabled(!redoStateStack.isEmpty());
        drawGrid();
    }


    public ImmutableGrid getGrid() {
        return stateStack.peek();
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("difficulty", difficulty.ordinal());
        ArrayList<String> states = new ArrayList<>();
        ArrayList<String> redoStates = new ArrayList<>();
        for (ImmutableGrid state : stateStack) {
            states.add(state.toString());
        }
        for (ImmutableGrid state : redoStateStack) {
            redoStates.add(state.toString());
        }
        savedInstanceState.putStringArrayList("states", states);
        savedInstanceState.putStringArrayList("redoStates", redoStates);
    }


    @Override
    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        difficulty = Difficulty.fromInt(savedInstanceState.getInt("difficulty", difficulty.ordinal()));
        List<String> states = savedInstanceState.getStringArrayList("states");
        List<String> redoStates = savedInstanceState.getStringArrayList("redoStates");
        if (states != null && !states.isEmpty()) {
            stateStack.clear();
            states.forEach(gridStr -> stateStack.push(ImmutableGrid.fromString(gridStr)));
            btnUndo.setEnabled(stateStack.size()>1);
        }
        if (redoStates != null && !redoStates.isEmpty()) {
            redoStateStack.clear();
            redoStates.forEach(gridStr -> redoStateStack.push(ImmutableGrid.fromString(gridStr)));
            btnRedo.setEnabled(true);
        }
    }

    protected String serializeState(final Stack<ImmutableGrid> states) {
        StringBuilder sb = new StringBuilder();
        states.forEach(state -> {
                sb.append(state.toString());
                sb.append(';');
        });
        return sb.toString();
    }

    protected Stack<ImmutableGrid> deserializeState(String data) {
        Stack<ImmutableGrid> states = new Stack<>();
        Arrays.stream(TextUtils.split(data, ";"))
                .forEach(state -> states.push(ImmutableGrid.fromString(state)));
        return states;
    }
}
