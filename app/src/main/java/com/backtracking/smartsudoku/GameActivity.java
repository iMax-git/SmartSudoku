package com.backtracking.smartsudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.backtracking.smartsudoku.models.Grid;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Stack;


public class GameActivity extends AppCompatActivity {

    static Clock clock = Clock.systemDefaultZone();
    Stack<Grid> stateStack;
    Stack<Grid> redoStateStack;
    LocalDateTime timer;

    public GameActivity() {
        this.stateStack = new Stack<>();
        this.redoStateStack = new Stack<>();
        this.startNewGame();
    }

    public void startNewGame() {
        this.stateStack.clear();
        this.redoStateStack.clear();
        this.timer = LocalDateTime.now();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}