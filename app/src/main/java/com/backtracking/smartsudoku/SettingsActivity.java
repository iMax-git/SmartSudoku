package com.backtracking.smartsudoku;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.backtracking.smartsudoku.adapter.WallpaperAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {



    private final List<String> wallpaperList = new ArrayList<>();
    private final List<String> wallpaperLabels = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        wallpaperList.add("@drawable/fond1");
        wallpaperLabels.add("Fond 1");
        wallpaperList.add("@drawable/fond2");
        wallpaperLabels.add("Fond 2");
        wallpaperList.add("@drawable/fond3");
        wallpaperLabels.add("Fond 3");
        wallpaperList.add("@drawable/fond4");
        wallpaperLabels.add("Fond 4");
        wallpaperList.add("@drawable/fond5");
        wallpaperLabels.add("Fond 5");
        wallpaperList.add("@drawable/fond6");
        wallpaperLabels.add("Fond 6");

        Spinner spinnerWallpaper = findViewById(R.id.spinnerFond);

        WallpaperAdapter<String> adapter = new WallpaperAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                wallpaperLabels.toArray(new String[0])
        );

        spinnerWallpaper.setAdapter(adapter);

        // Récupérer le fond d'écran actuel
        String currentBackground = getSharedPreferences("settings", MODE_PRIVATE).getString("background", "");
        int index = wallpaperList.indexOf(currentBackground);
        if (index != -1) {
            spinnerWallpaper.setSelection(index);
        }

        spinnerWallpaper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (wallpaperList.get(position) != null) {
                    // Change id in shared preferences
                    SharedPreferences settings = getSharedPreferences("settings", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("background", wallpaperList.get(position));
                    editor.apply();


                    System.out.println("Fond d'écran sélectionné: " + settings.getString("background", ""));

                } else {
                    System.out.println("Erreur lors de la sélection du fond d'écran");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });






        ImageButton btnCloseSettings = findViewById(R.id.btnCloseSettings);
        btnCloseSettings.setOnClickListener(v -> {
            // Fermer l'activité
            finish();
        });
        }


    }

