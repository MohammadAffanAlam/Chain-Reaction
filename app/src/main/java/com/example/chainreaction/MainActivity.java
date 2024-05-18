package com.example.chainreaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // Class that handles the main menu screen (screen that opens when the app opens)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Method that runs when the main menu screen is open
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView titleText = findViewById(R.id.titleText);

        // Data received from other screens when main menu opens up; Allows for data to be saved between screens
        Intent intent = getIntent();
        PlayerList players = (PlayerList) intent.getSerializableExtra("players");
        int xSize = intent.getIntExtra("xSize", 5);
        int ySize = intent.getIntExtra("ySize", 9);
        int explosionSpeed = intent.getIntExtra("explosionSpeed", 200);
        if (intent.getIntExtra("bgColor", Color.WHITE) == Color.BLACK) { // Dark mode on
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            titleText.setTextColor(Color.WHITE);
        }
        else { // Dark mode off
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            titleText.setTextColor(Color.BLACK);
        }

        // Initializes the variables with the android studios component in the XML file
        Button playButton = (Button) findViewById(R.id.playButton);
        Button howToPlayButton = (Button) findViewById(R.id.howToPlayButton);
        Button settingsButton = (Button) findViewById(R.id.settingsButton);

        playButton.setOnClickListener(new View.OnClickListener() { // Play Button functionality
            @Override
            public void onClick(View view) {
                // Opens game menu screen with data on players, grid size, and settings
                Intent gameMenuIntent = new Intent(getApplicationContext(), GameMenuActivity.class);
                gameMenuIntent.putExtra("players", players);
                gameMenuIntent.putExtra("xSize", xSize);
                gameMenuIntent.putExtra("ySize", ySize);
                gameMenuIntent.putExtra("explosionSpeed", explosionSpeed);
                gameMenuIntent.putExtra("bgColor", ((ColorDrawable) getWindow().getDecorView().getBackground()).getColor());
                startActivity(gameMenuIntent);
            }
        });

        howToPlayButton.setOnClickListener(new View.OnClickListener() { // How to Play Button functionality
            @Override
            public void onClick(View view) {
                // Creates and displays dialog for how to play
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("How to Play")
                        .setMessage(getResources().getString(R.string.howToPlayText))
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Handle the OK button click
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() { // Settings Button functionality
            @Override
            public void onClick(View view) {
                // Opens settings screen with data on players, grid size, and settings
                Intent settingsIntent = new Intent(getApplicationContext(), SettingsActivity.class);
                settingsIntent.putExtra("players", players);
                settingsIntent.putExtra("xSize", xSize);
                settingsIntent.putExtra("ySize", ySize);
                settingsIntent.putExtra("explosionSpeed", explosionSpeed);
                settingsIntent.putExtra("bgColor", ((ColorDrawable) getWindow().getDecorView().getBackground()).getColor());
                startActivity(settingsIntent);
            }
        });
    }
}