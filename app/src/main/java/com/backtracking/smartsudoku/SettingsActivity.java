package com.backtracking.smartsudoku;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.backtracking.smartsudoku.adapter.WallpaperAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private final List<String> wallpaperList = new ArrayList<>();
    private final List<String> wallpaperLabels = new ArrayList<>();

    // Firebase Auth and Google login
    private static final String TAG = "SettingsActivity";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private GoogleSignInClient signInClient;


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

                    // Update background
                    updateBackground();
                    System.out.println("Fond d'écran sélectionné: " + settings.getString("background", ""));

                } else {
                    System.out.println("Erreur lors de la sélection du fond d'écran");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        this.updateBackground();

        ImageButton btnCloseSettings = findViewById(R.id.btnCloseSettings);
        btnCloseSettings.setOnClickListener(v -> {
            // Fermer l'activité
            finish();
        });


        // auth and login

        GoogleSignInOptions options =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        this.signInClient = GoogleSignIn.getClient(this, options);
        findViewById(R.id.btnLoginGoogle).setOnClickListener(view -> signIn());

        this.authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                SettingsActivity.this.onAuthStateChanged(firebaseAuth.getCurrentUser());
            }
        };
    }

    protected void onResume() {
        super.onResume();
        this.updateBackground();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.auth = FirebaseAuth.getInstance();
        this.auth.addAuthStateListener(this.authStateListener);
    }


    @Override
    protected void onStop() {
        super.onStop();
        // detach listener to avoid a memory leak
        if (authStateListener != null) {
            auth.removeAuthStateListener(this.authStateListener);
        }
    }


    private void signIn() {
        Intent signInIntent = signInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // result from GoogleSignInClient.getSignInIntent(...);
            if (resultCode == RESULT_OK) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } else {
                Log.w(TAG, "Google sign-in failed");
                Toast.makeText(this, "Google sign-in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful())
                    {
                        FirebaseUser user = auth.getCurrentUser();

                        if (user == null) {
                            Toast.makeText(SettingsActivity.this, "User is NULL ERROR",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(SettingsActivity.this, "User authenticated: "
                                            + ((user.getDisplayName()!=null) ? user.getDisplayName() : "<ERROR>"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else { // sign in failed
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(SettingsActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    /**
     * Callback on log-in log-off state change
     * Update the activity display depending on whether the user is authenticated or not.
     */
    protected void onAuthStateChanged(@Nullable FirebaseUser user)
    {
        SignInButton signInButton = findViewById(R.id.btnLoginGoogle);
        View loggedInView = findViewById(R.id.loggedInLayout);

        if (user == null) {
            loggedInView.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        } else {
            signInButton.setVisibility(View.GONE);
            loggedInView.setVisibility(View.VISIBLE);
            TextView loggedInMessage = findViewById(R.id.loggedInStatusView);
            String loggedInStatus = getString(R.string.loggedInStatusMessage, user.getDisplayName());
            loggedInMessage.setText(loggedInStatus);
        }
    }

    private void updateBackground() {
        System.out.println("Updating background");
        // Get background from shared preferences
        SharedPreferences settings = getSharedPreferences("settings", 0);
        String backgroundName = settings.getString("background", "");
        LinearLayout rootLayout = findViewById(R.id.rootLayout);
        rootLayout.setBackgroundResource(getResources().getIdentifier(backgroundName, "drawable", getPackageName()));
    }


    /**
     * Call back for log-off button
     */
    public void logOff(View v) {
        auth.signOut();
    }
}

