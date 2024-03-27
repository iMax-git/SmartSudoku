package com.backtracking.smartsudoku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences prefs;

    private void changeBackground(String backgroundName) {
        int resId = getResources().getIdentifier(backgroundName, "drawable", getPackageName());
        findViewById(R.id.rootLayout).setBackgroundResource(resId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);

        // Gestion du choix du fond d'écran
        RadioGroup radioGroupFond = findViewById(R.id.radioGroupFond);
        radioGroupFond.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                SharedPreferences.Editor editor = prefs.edit();
                String background = "fond1"; // Valeur par défaut
                if (checkedId == R.id.radioFond1) {
                    background = "fond1";
                } else if (checkedId == R.id.radioFond2) {
                    background = "fond2";
                }
                // Continuez avec les autres cas si nécessaire

                editor.putString("selected_background", background);
                editor.apply();

                changeBackground(background); // Appliquer le changement immédiatement
            }
        });

        Button btnCloseSettings = findViewById(R.id.btnCloseSettings);
        btnCloseSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fermer l'activité
                finish();
            }
        });
        }
    }

