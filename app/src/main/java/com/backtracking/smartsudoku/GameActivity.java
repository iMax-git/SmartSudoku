package com.backtracking.smartsudoku;

import static android.opengl.ETC1.getWidth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.backtracking.smartsudoku.models.Game;
import com.backtracking.smartsudoku.models.ImmutableGrid;
import com.backtracking.smartsudoku.models.SudokuGenerator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    public enum Difficulty {
        EASY, MEDIUM, HARD;
        public static Difficulty fromInt(int i) {
            return Difficulty.values()[i];
        }
    }

    private LinearLayout rootLayout;
    private LinearLayout gridContainer;
    private  GridLayout gridView;
    private ImageButton btnSettings;

    private LinearLayout view_keyboard;
    private LinearLayout end_menu;

    private ImageButton btnUndo, btnRedo;

    private final boolean DEBUG = false;

    private final List<TextView> cells = new ArrayList<>();

    private final Integer[] selectedCell = new Integer[2];

    private Game game;
    private Difficulty difficulty = Difficulty.MEDIUM;
    private LocalDateTime startTime;
    private final Handler timerUpdateHandler = new Handler();
    private TextView timerView;

    /**
     * Forme pour les rectangles des cellules par défaut(initialement défini, sélectionnées et de base
     */
    private ShapeDrawable shapeDefaultDrawable;
    private ShapeDrawable shapeSelectedDrawable;
    private ShapeDrawable shapeBaseDrawable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        defineRects();
        this.rootLayout = findViewById(R.id.rootLayout);
        this.gridContainer = findViewById(R.id.gridContainer);
        this.gridView = findViewById(R.id.gridLayout);
        this.view_keyboard = findViewById(R.id.view_keyboard);
        this.btnSettings = findViewById(R.id.btnSettings);
        this.end_menu = findViewById(R.id.endGameMenu);
        this.btnRedo = findViewById(R.id.btnRedo);
        this.btnUndo = findViewById(R.id.btnUndo);
        this.timerView = findViewById(R.id.timerView);
        this.setupKeyboard();
        this.updateBackground();

        this.btnSettings.setOnClickListener(v -> buttonSwitchActivity(SettingsActivity.class));
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
        Game savedGame = Game.deserialize(gameStates);
        if (savedGame != null) {
            this.game = savedGame;
        }
        else { // no game saved
            createGrid(getScreenSize()[0] - 150);
            startNewGame(this.difficulty);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        // save settings
        SharedPreferences.Editor settingsEditor = getSharedPreferences("settings",0).edit();
        settingsEditor.putInt("difficulty", difficulty.ordinal());
        settingsEditor.apply();

        // save game
        SharedPreferences saveStore = getSharedPreferences("save", 0);
        SharedPreferences.Editor saveEditor = saveStore.edit();
        if (game.isWon()) {
            saveEditor.clear();
        } else {
            saveEditor.putString("gameStates", game.serialize());
        }
        saveEditor.apply();
    }


    @Override
    protected void onPause() {
        super.onPause();
        updateGameTime();
        stopTimerUpdater();
    }


    @Override
    protected void onResume() {
        super.onResume();
        createGrid(getScreenSize()[0]-120);
        drawGrid();
        refreshStateButtons();
        setupInteractiveCells();
        this.updateBackground();
        startTimerUpdater();
    }


    protected void createGrid(int gridSize)
    {
        this.gridView.removeAllViewsInLayout();
        this.gridView.setBackgroundColor(Color.BLACK);

        for (int i = 0; i < 81 ; ++i) {
            TextView tv = new TextView(this);
            tv.setId(i);
            tv.setText(" ");
            tv.setTextSize(20);
            tv.setGravity(1);
            tv.setTextColor(Color.BLACK);
            tv.setBackgroundColor(Color.TRANSPARENT);

            int step = (gridSize / 9)-2;
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
                // Toast.makeText(this, "Cell " + id, Toast.LENGTH_SHORT).show();
                System.out.println("Cell " + id + " clicked" + " x: " + x + " y: " + y);

                ImmutableGrid baseGrid = game.getBaseGrid();

                if (baseGrid.get(x,y) != 0) {
                    System.out.println(baseGrid.get(x,y) + " is a default cell");
                    return;
                }
                if (selectedCell[0] != null) {
                    cells.get(selectedCell[0] + selectedCell[1] * 9).setBackground(shapeBaseDrawable);
                }
                selectedCell[0] = x;
                selectedCell[1] = y;

                cells.get(id).setBackground(shapeSelectedDrawable);

                // setup keyboard numbers
                ImmutableGrid grid = game.getGrid();
                List<Integer> row = grid.getRow(y);
                List<Integer> column = grid.getColumn(x);
                List<Integer> region = grid.getRegion(ImmutableGrid.indexToRegion(id));

                // debug code
                System.out.printf("row: %s\n", row);
                System.out.printf("col: %s\n", column);
                System.out.printf("region: %s\n", region);

                GridLayout keyboard = findViewById(R.id.keyboard);

                // enable all keyboard buttons
                for (int btnIndex=0; btnIndex<keyboard.getChildCount(); ++btnIndex) {
                    Button button = (Button) keyboard.getChildAt(btnIndex);
                    button.setEnabled(true);
                }

                // disable relevant keyboard buttons
                for (Integer number : row) {
                    keyboard.findViewById(number).setEnabled(false);
                }
                for (Integer number : column) {
                    keyboard.findViewById(number).setEnabled(false);
                }
                for (Integer number : region) {
                    keyboard.findViewById(number).setEnabled(false);
                }

                this.view_keyboard.setVisibility(View.VISIBLE);
            });

            tv.setOnLongClickListener((v) -> {
                // Remove number
                int id = v.getId();
                int x = id % 9;
                int y = id / 9;
                if (game.getBaseGrid().get(x,y) != 0) {
                    return true;
                }
                selectedCell[0] = x;
                selectedCell[1] = y;
                playNumber(x, y, 0);
                this.view_keyboard.setVisibility(View.GONE);
                if (selectedCell[0] != null) {
                    cells.get(selectedCell[0] + selectedCell[1] * 9).setBackground(shapeBaseDrawable);
                }
                return true;
            });

            this.cells.add(tv);
            this.gridView.addView(tv);
        }

        // region borders
        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.BLACK);
        gridPaint.setStrokeWidth(6);

        final int regionSize = gridSize/3;
        System.out.printf("regionSize: %d\n", regionSize);

        Bitmap bmp = Bitmap.createBitmap(gridSize, gridSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmp);

        for (int i = 0; i <= regionSize; i++) {
            canvas.drawLine(0, i * regionSize, gridSize, i * regionSize, gridPaint);
        }
        for (int i = 0; i <= regionSize; i++) {
            canvas.drawLine(i * regionSize, 0, i * regionSize, gridSize, gridPaint);
        }

        Drawable bmpDrawable = new BitmapDrawable(getResources(), bmp);
        gridView.setForeground(bmpDrawable);
    }


    protected void drawGrid() {
        ImmutableGrid grid = game.getGrid();
        ImmutableGrid baseGrid = game.getBaseGrid();

        for (int i = 0; i < 81; ++i) {
            TextView cellView = (TextView) this.gridView.getChildAt(i);
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

        shapeSelectedDrawable.getPaint().setColor(Color.parseColor("#14a7fc"));
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
     * Handle the UI event to start a new game
     */
    public void startNewGame(View v) {
        // Création d'une boîte de dialogue pour choisir la difficulté
        final String[] difficulties = {"EASY", "MEDIUM", "HARD"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choisissez une difficulté")
                .setItems(difficulties, (dialog, which) -> {
                    // 'which' correspond à l'index de l'élément sélectionné
                    // Démarrez ici votre jeu avec la difficulté choisie
                    startNewGame(Difficulty.fromInt(which));
                });
        builder.create().show();
    }


    public void startNewGame(@NonNull final Difficulty difficulty) {
        SudokuGenerator generator = new SudokuGenerator();
        ImmutableGrid solution = generator.getGrid();

        switch (difficulty) {
            case EASY:
                generator.removeNumbers(DEBUG ? 1 : 25); // Si DEBUG est vrai, on ne retire qu'une seule cellule

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
        refreshStateButtons();
        setupInteractiveCells(); // Réactivation des cellules interactives
        setValuesToDefault();
        startTime = LocalDateTime.now();
    }


    public void playNumber(int x, int y, int value) {
        if (!game.isWon()) {
            game.setNumber(x, y, value);
            if (game.isWon()) {
                System.out.println("Game won!");
                setGridViewEnabled(false);
                endGame();
            }
            refreshStateButtons();
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
        if (!game.isWon()) {
            btnUndo.setEnabled(game.isUndoable());
            btnRedo.setEnabled(game.isRedoable());
        } else {
            btnUndo.setEnabled(false);
            btnRedo.setEnabled(false);
        }
    }


    protected void setGridViewEnabled(boolean enabled) {
        for (int i=0; i<gridView.getChildCount(); ++i) {
            View cellView = gridView.getChildAt(i);
            cellView.setEnabled(enabled);
        }
    }


    protected void setupInteractiveCells() {
        for (int i=0; i<gridView.getChildCount(); ++i) {
            this.cells.get(i).setEnabled(game.getBaseGrid().get(i) == 0);
        }
    }


    private void setupKeyboard() {
        /*
        Dispose keyboard buttons in a 3x3 grid
            1 2 3
            4 5 6
            7 8 9
         */

        GridLayout keyboard = findViewById(R.id.keyboard);
        for (int i=1; i<=9; ++i) {
            Button btn = new Button(this);
            btn.setText(String.valueOf(i));
            btn.setId(i);
            btn.setOnClickListener(v -> {
                int id = v.getId();
                int x = selectedCell[0];
                int y = selectedCell[1];
                playNumber(x, y, id);
                view_keyboard.setVisibility(View.GONE);
            });
            view_keyboard.setVisibility(View.GONE);
            keyboard.addView(btn);
        }
    }

    public void setValuesToDefault() {
        selectedCell[0] = null; selectedCell[1] = null;
        this.end_menu.setVisibility(View.GONE);
        this.view_keyboard.setVisibility(View.GONE);
    }

    public void endGame() {

        Button btnNewGame = findViewById(R.id.btnNewGameEnd);
        btnNewGame.setOnClickListener(v -> startNewGame(this.gridView));
        stopTimerUpdater();

        this.end_menu.setVisibility(View.VISIBLE);

    }




    private void buttonSwitchActivity(Class<?> activity) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
    }

    private void updateBackground() {
        System.out.println("Updating background");
        // Get background from shared preferences
        SharedPreferences settings = getSharedPreferences("settings", 0);
        String backgroundName = settings.getString("background", "");
        rootLayout.setBackgroundResource(getResources().getIdentifier(backgroundName, "drawable", getPackageName()));
    }



    /*
        handle timer
     */

    protected long getTimerAsSeconds() {
        return game.getTimer() + Duration.between(this.startTime, LocalDateTime.now()).getSeconds();
    }

    protected String getTimerAsString() {
        final long value = getTimerAsSeconds();
        final int seconds = (int) (value % 60L);
        final int minutes = (int) (value / 60L) % 60;
        final int hours = (int) (value / 60L / 60L);
        if (hours == 0) {
            return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        } else {
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        }
    }

    protected void updateGameTime() {
        game.setTimer(getTimerAsSeconds());
    }

    protected void startTimerUpdater() {
        this.startTime = LocalDateTime.now();
        Runnable updateTimer = new Runnable() {
            @Override
            public void run() {
                timerView.setText(getTimerAsString());
                timerUpdateHandler.postDelayed(this, 1000);
            }
        };
        timerUpdateHandler.postDelayed(updateTimer, 1000);
    }

    protected void stopTimerUpdater() {
        timerUpdateHandler.removeCallbacksAndMessages(null);
    }
}
