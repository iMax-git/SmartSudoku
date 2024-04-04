package com.backtracking.smartsudoku;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.backtracking.smartsudoku.adapter.WallpaperAdapter;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {


    private Spinner spinnerWallpaper;
    private SwitchMaterial switchNotifications;

    private List<String> wallpaperList = new ArrayList<>();
    private List<String> wallpaperLabels = new ArrayList<>();
    private NotificationChannel channel;
    private NotificationManager notificationManager;

    private void changeBackground(String backgroundName) {
        int resId = getResources().getIdentifier(backgroundName, "drawable", getPackageName());
        if (resId != 0) {
            findViewById(R.id.rootLayout).setBackgroundResource(resId);
        } else {
            // Gestion de l'erreur si la ressource n'est pas trouvée
            Log.e("SettingsActivity", "La ressource de fond n'a pas été trouvée: " + backgroundName);
        }
    }


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

        this.spinnerWallpaper = findViewById(R.id.spinnerFond);

        WallpaperAdapter<String> adapter = new WallpaperAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                wallpaperLabels.toArray(new String[0])
        );

        this.spinnerWallpaper.setAdapter(adapter);

        // Récupérer le fond d'écran actuel
        String currentBackground = getSharedPreferences("background", MODE_PRIVATE).getString("background", "");
        int index = wallpaperList.indexOf(currentBackground);
        if (index != -1) {
            this.spinnerWallpaper.setSelection(index);
        }

        this.spinnerWallpaper.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (wallpaperList.get(position) != null) {
                    // Change id in shared preferences
                    SharedPreferences.Editor editor = getSharedPreferences("background", MODE_PRIVATE).edit();
                    editor.putString("background", wallpaperList.get(position));
                    editor.apply();


                    System.out.println("Fond d'écran sélectionné: " + getSharedPreferences("background", MODE_PRIVATE).getString("background", ""));

                } else {
                    System.out.println("Erreur lors de la sélection du fond d'écran");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });






        ImageButton btnCloseSettings = findViewById(R.id.btnCloseSettings);
        btnCloseSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fermer l'activité
                finish();
            }
        });
        }


    }

