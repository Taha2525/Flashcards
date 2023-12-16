package com.example.flashcards;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button createFlashcardsButton = findViewById(R.id.createFlashcardsButton);
        createFlashcardsButton.setOnClickListener(v -> {
            // Save that the welcome screen has been shown
            SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
            preferences.edit().putBoolean("FirstLaunch", false).apply();

            // Start MainActivity
            Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Close the welcome screen
        });
    }
}