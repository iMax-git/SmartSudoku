package com.backtracking.smartsudoku;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.time.Clock;
import java.time.LocalDate;


public class GameActivity extends AppCompatActivity {

    Clock clock;

    public GameActivity() {
        this.clock = Clock.systemDefaultZone();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }
}