package com.backtracking.smartsudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.backtracking.smartsudoku.models.Game;
import com.backtracking.smartsudoku.models.ImmutableGrid;
import com.backtracking.smartsudoku.models.SudokuGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    public enum Difficulty {
        EASY, MEDIUM, HARD;
        public static Difficulty fromInt(int i) {
            return Difficulty.values()[i];
        }
    }

    GridLayout gridView;

    LinearLayout ll_number_list;

    Button btnUndo, btnRedo;

    boolean DEBUG_CELL = false;

    List<TextView> cells = new ArrayList<>();

    Integer SIZE;

    Game game = new Game();
    Difficulty difficulty = Difficulty.MEDIUM;
    LocalDateTime timer = LocalDateTime.now();

    /**
     * Forme pour les rectangles des cellules par défaut(initialement défini, sélectionnées et de base
     */
    ShapeDrawable shapeDefaultDrawable;
    ShapeDrawable shapeSelectedDrawable;
    ShapeDrawable shapeBaseDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        defineRects();

        int[] screenSize = getScreenSize();
        //SIZE = getScreenSize()[0] - (getScreenSize()[0] / 8); // TODO: Trouver une meilleure façon de calculer la taille de la grille
        SIZE = screenSize[0]-100;
        setContentView(R.layout.activity_game);
        this.gridView = findViewById(R.id.gridLayout);
        this.ll_number_list = findViewById(R.id.ll_number_list);
        this.btnRedo = findViewById(R.id.btnRedo);
        this.btnUndo = findViewById(R.id.btnUndo);

        this.gridView.setBackgroundColor(Color.BLACK);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        // wrap_content
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;

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
            int step = (SIZE / 9)-2;
            tv.setWidth(step);
            tv.setHeight(step);
            int padding = 0;
            tv.setPadding(padding, padding, padding, padding);
            GridLayout.LayoutParams params1 = new GridLayout.LayoutParams();
            int margin = 1;
            params1.setMargins( margin, margin, margin, margin);
            tv.setLayoutParams(params1);

            tv.setOnClickListener(v -> {
                int id = v.getId();
                int x = id % 9;
                int y = id / 9;

                // Show a message with number of the cell clicked
//                Toast.makeText(this, "Cell " + id, Toast.LENGTH_SHORT).show();

                // Show a dialog to enter a number
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Modifier la case");
                builder.setMessage("Entrez un chiffre entre 1 et 9 \n Numéro de la case : " + game.getGrid().get(x,y) + ".");

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
                        this.playNumber(x, y, Integer.parseInt(value)); // INFO: Save the value in the model (To avoid to be lost when the gridView is redrawn)
                    } else {
                        Toast.makeText(this, R.string.choice_number, Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton(R.string.annuler, (dialog, which) -> dialog.cancel());

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
    }


    @Override
    protected void onStop() {
        super.onStop();
        // when app closes (either via back button or by the system),
        // save the current game if it is not finished.
        SharedPreferences saveStore = getSharedPreferences("save", 0);
        SharedPreferences.Editor storeEditor = saveStore.edit();
        if (!game.isWon()) {
            storeEditor.putString("gameStates", game.serialize());
        } else {
            storeEditor.remove("gameStates");
        }
        storeEditor.apply();
    }


    @Override
    protected void onStart() {
        super.onStart();
        // restore settings
        try {
            SharedPreferences settings = getSharedPreferences("settings", 0);
            this.difficulty = Difficulty.fromInt(settings.getInt("difficulty", Difficulty.MEDIUM.ordinal()));
        } catch (Exception ex) {
            this.difficulty = Difficulty.MEDIUM;
        }
        // check save store for interrupted game and restore it if present
        SharedPreferences save = getSharedPreferences("save", 0);
        String gameStates = save.getString("gameStates", "");
        if (!gameStates.isEmpty()) {
            this.game = Game.deserialize(gameStates);
        } else { // no game saved
            startNewGame(this.difficulty);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor settingsEditor = getSharedPreferences("settings",0).edit();
        settingsEditor.putInt("difficulty", difficulty.ordinal());
        settingsEditor.apply();
    }


    @Override
    protected void onResume() {
        super.onResume();
        drawGrid();
        refreshStateButtons();
    }


    protected void drawGrid() {

        ImmutableGrid grid = game.getGrid();
        ImmutableGrid baseGrid = game.getBaseGrid();

        for (int i = 0; i < 81; ++i) {
            TextView cellView = cells.get(i);
            int cellNumber = grid.get(i);
            if (cellNumber!=0) {
                cellView.setText(String.valueOf(cellNumber));
            } else {
                cellView.setText("");
            }
            if (baseGrid.get(i)!=0) {
                cellView.setBackground(shapeDefaultDrawable);

            } else {
                cellView.setBackground(shapeBaseDrawable);
            }
        }
    }




    /**
     * Défini les paramètres des rectangles en arrirère plan des TextView (cellules)
     */
    private void defineRects() {


        RoundRectShape defautRect = new RoundRectShape(new float[] {
                0,0,0,0,
                0,0,0,0
        }, null, null);

        RoundRectShape selectedRect = new RoundRectShape(new float[] {
                0,0,0,0,
                0,0,0,0
        }, null, null);

        RoundRectShape baseRect = new RoundRectShape(new float[] {
                0,0,0,0,
                0,0,0,0
        }, null, null);
        
        shapeDefaultDrawable = new ShapeDrawable(defautRect);
        shapeSelectedDrawable = new ShapeDrawable(selectedRect);
        shapeBaseDrawable = new ShapeDrawable(baseRect);
        
        shapeDefaultDrawable.getPaint().setColor(Color.parseColor("#FFEEEE"));
        shapeDefaultDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        shapeDefaultDrawable.getPaint().setStrokeWidth(1);
        shapeDefaultDrawable.getPaint().setAntiAlias(true);
        shapeDefaultDrawable.getPaint().setDither(true);
        shapeDefaultDrawable.getPaint().setStrokeJoin(Paint.Join.ROUND);

        shapeSelectedDrawable.getPaint().setColor(Color.parseColor("#FF9000"));
        shapeSelectedDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        shapeSelectedDrawable.getPaint().setStrokeWidth(1);
        shapeSelectedDrawable.getPaint().setAntiAlias(true);
        shapeSelectedDrawable.getPaint().setDither(true);
        shapeSelectedDrawable.getPaint().setStrokeJoin(Paint.Join.ROUND);

        shapeBaseDrawable.getPaint().setColor(Color.WHITE);
        shapeBaseDrawable.getPaint().setStyle(Paint.Style.FILL_AND_STROKE);
        shapeBaseDrawable.getPaint().setStrokeWidth(1);
        shapeBaseDrawable.getPaint().setAntiAlias(true);
        shapeBaseDrawable.getPaint().setDither(true);
        shapeBaseDrawable.getPaint().setStrokeJoin(Paint.Join.ROUND);
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


    /**
     * Handling the UI event to start a new game
     */
    public void startNewGame(View v) {
        startNewGame(this.difficulty);
    }


    public void startNewGame(@NonNull final Difficulty difficulty) {
        SudokuGenerator generator = new SudokuGenerator();
        ImmutableGrid solution = generator.getGrid();
        switch (difficulty) {
            case EASY:
                generator.removeNumbers(18);
//                generator.removeNumbers(1); // to DEBUG win condition
                break;
            case MEDIUM:
                generator.removeNumbers(37);
                break;
            case HARD:
                generator.removeNumbers(56);
                break;
        }
        this.game = new Game(generator.getGrid(), solution);
        this.difficulty = difficulty;
        drawGrid();
        setupInteractiveCells();
        refreshStateButtons();

        // DEBUG
        System.out.println(game.getBaseGrid());
        System.out.println();
        System.out.println(game.getSolution());
    }


    public void playNumber(int x, int y, int value) {
        if (!game.isWon()) {
            game.setNumber(x, y, value);
            if (game.isWon()) {
                setGridViewEnabled(false);
                setStateButtonsEnabled(false);
            } else {
                refreshStateButtons();
            }
            drawGrid();
        }
    }


    public void undo(View v) {
        if (game.isUndoable()) {
            game.undo();
            refreshStateButtons();
            drawGrid();
        }
    }


    public void redo(View v) {
        if (game.isRedoable()) {
            game.redo();
            refreshStateButtons();
            drawGrid();
        }
    }


    protected void refreshStateButtons() {
        btnUndo.setEnabled(game.isUndoable());
        btnRedo.setEnabled(game.isRedoable());
    }


    protected void setStateButtonsEnabled(boolean enabled) {
        btnUndo.setEnabled(enabled);
        btnRedo.setEnabled(enabled);
    }


    protected void setGridViewEnabled(boolean enabled) {
        for (int i=0; i<gridView.getChildCount(); ++i) {
            View cellView = gridView.getChildAt(i);
            cellView.setEnabled(enabled);
        }
    }


    protected void setupInteractiveCells() {
        for (int i=0; i<gridView.getChildCount(); ++i) {
            View cellView = gridView.getChildAt(i);
            cellView.setEnabled(game.getBaseGrid().get(i) == 0);
        }
    }

}
