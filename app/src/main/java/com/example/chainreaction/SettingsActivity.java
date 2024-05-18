package com.example.chainreaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    // Class that handles the settings screen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Method that runs when the settings screen is open
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initializes the variables with the android studios component in the XML file
        TextView settingsTitleText = findViewById(R.id.settingsTitleText);
        TextView speedText = findViewById(R.id.speedText);
        SeekBar speedSlider = findViewById(R.id.speedSlider);
        Switch darkModeSwitch = findViewById(R.id.darkModeSwitch);
        Button backButtonSettings = findViewById(R.id.backButtonSettings);

        // Data received from other screens when settings screen opens up; Allows for data to be saved between screens
        Intent intent = getIntent();
        PlayerList players = (PlayerList) intent.getSerializableExtra("players");
        int xSize = intent.getIntExtra("xSize", 5);
        int ySize = intent.getIntExtra("ySize", 9);
        speedSlider.setProgress(intent.getIntExtra("explosionSpeed", 200));
        darkModeSwitch.setChecked(intent.getIntExtra("bgColor", Color.WHITE) == Color.BLACK);
        if (darkModeSwitch.isChecked()) { // Dark mode on
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            darkModeSwitch.setTextColor(Color.WHITE);
            settingsTitleText.setTextColor(Color.WHITE);
            speedText.setTextColor(Color.WHITE);
        }
        else { // Dark mode off
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            darkModeSwitch.setTextColor(Color.BLACK);
            settingsTitleText.setTextColor(Color.BLACK);
            speedText.setTextColor(Color.BLACK);
        }

        darkModeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            // Dark mode switch functionality
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) { // Changes background color to black and text color to white
                    getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                    darkModeSwitch.setTextColor(Color.WHITE);
                    settingsTitleText.setTextColor(Color.WHITE);
                    speedText.setTextColor(Color.WHITE);

                } else { // Changes background color to white and text color to black
                    getWindow().getDecorView().setBackgroundColor(Color.WHITE);
                    darkModeSwitch.setTextColor(Color.BLACK);
                    settingsTitleText.setTextColor(Color.BLACK);
                    speedText.setTextColor(Color.BLACK);
                }
            }
        });

        backButtonSettings.setOnClickListener(new View.OnClickListener() { // Back button functionality
            @Override
            public void onClick(View view) {
                // Opening main menu screen with data on players, grid size, and settings
                Intent mainMenuIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainMenuIntent.putExtra("players", players);
                mainMenuIntent.putExtra("xSize", xSize);
                mainMenuIntent.putExtra("ySize", ySize);
                mainMenuIntent.putExtra("explosionSpeed", speedSlider.getProgress());
                mainMenuIntent.putExtra("bgColor", ((ColorDrawable) getWindow().getDecorView().getBackground()).getColor());
                startActivity(mainMenuIntent);
            }
        });
    }
}