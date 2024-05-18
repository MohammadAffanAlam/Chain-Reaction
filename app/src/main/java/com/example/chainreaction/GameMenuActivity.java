package com.example.chainreaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.*;
import android.util.Log;
import android.view.*;
import android.widget.*;
import android.graphics.Color;
import java.util.Random;

import androidx.appcompat.app.AppCompatActivity;

public class GameMenuActivity extends AppCompatActivity {
    // Class that handles the game menu screen where game settings are inputted

    private LinearLayout playerListLayout; // LinearLayout that graphically displays the list of players
    private EditText gridSizeXInput; // Textbox for X dimension (number of columns) of the grid
    private EditText gridSizeYInput; // Textbox for Y dimension (number of rows) of the grid
    private PlayerList players; // Linked list of players

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Method that runs when the game menu screen is open
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_menu);

        // Initializes the variables with the android studios component in the XML file
        playerListLayout = findViewById(R.id.playerListLayout);
        gridSizeXInput = findViewById(R.id.gridSizeXInput);
        gridSizeYInput = findViewById(R.id.gridSizeYInput);
        TextView playerText = findViewById(R.id.playerText);
        TextView gridSizeText = findViewById(R.id.gridSizeText);
        TextView gridDimensionsText = findViewById(R.id.gridDimensionsText);
        Button addPlayerButton = findViewById(R.id.addPlayerButton);
        Button startGameButton = findViewById(R.id.startGameButton);
        Button backButton = findViewById(R.id.backButton);

        // Data received from other screens when game menu opens up; Allows for data to be saved between screens
        Intent intent = getIntent();

        gridSizeXInput.setText(Integer.toString(intent.getIntExtra("xSize", 5)));
        gridSizeYInput.setText(Integer.toString(intent.getIntExtra("ySize", 9)));
        int explosionSpeed = intent.getIntExtra("explosionSpeed", 250);

        if (intent.getIntExtra("bgColor", Color.WHITE) == Color.BLACK) { // Dark mode on
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            playerText.setTextColor(Color.WHITE);
            gridSizeText.setTextColor(Color.WHITE);
            gridDimensionsText.setTextColor(Color.WHITE);
            gridSizeXInput.setTextColor(Color.WHITE);
            gridSizeYInput.setTextColor(Color.WHITE);
        }
        else { // Dark mode off
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            playerText.setTextColor(Color.BLACK);
            gridSizeText.setTextColor(Color.BLACK);
            gridDimensionsText.setTextColor(Color.BLACK);
            gridSizeXInput.setTextColor(Color.BLACK);
            gridSizeYInput.setTextColor(Color.BLACK);
        }

        if (intent.getSerializableExtra("players") != null) { // Copying over player data
            players = (PlayerList) intent.getSerializableExtra("players");
            players.reset();

            for (int i = 0; i < players.size(); i++) {
                addPlayer(players.getPlayerNode(i));
            }
        }
        else { // If there is no player data (meaning the program has just been opened), 2 default players are created
            players = new PlayerList();
            players.addPlayerNode("Player1", Color.RED);
            players.addPlayerNode("Player2", Color.BLUE);
            addPlayer(players.getPlayerNode(0));
            addPlayer(players.getPlayerNode(1));
        }

        addPlayerButton.setOnClickListener(new View.OnClickListener() { // Add Player Button functionality
            @Override
            public void onClick(View view) {
                if (players.size() < 8) { // Adds a (random) new player if there are less than 8 players
                    players.addPlayerNode();
                    addPlayer(players.getPlayerNode(players.size() - 1));
                }
                else { // Error pop-up if trying to add players when there are already 8 players
                    Toast.makeText(GameMenuActivity.this, "Maximum players reached!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() { // Back button functionality
            @Override
            public void onClick(View view) {
                // Opening main menu screen with data on players, grid size, and settings
                Intent mainMenuIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainMenuIntent.putExtra("players", players);
                mainMenuIntent.putExtra("xSize", Integer.parseInt(gridSizeXInput.getText().toString()));
                mainMenuIntent.putExtra("ySize", Integer.parseInt(gridSizeYInput.getText().toString()));
                mainMenuIntent.putExtra("explosionSpeed", explosionSpeed);
                mainMenuIntent.putExtra("bgColor", ((ColorDrawable) getWindow().getDecorView().getBackground()).getColor());
                startActivity(mainMenuIntent);
            }
        });

        gridSizeXInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 2) {
                    // Makes sure X dimension (number of columns) doesn't exceed 2 digits
                    editable.delete(2, editable.length());
                }
            }
        });

        gridSizeYInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 2) {
                    // Makes sure Y dimension (number of rows) doesn't exceed 2 digits
                    editable.delete(2, editable.length());
                }
            }
        });

        startGameButton.setOnClickListener(new View.OnClickListener() { // Start game button functionality
            @Override
            public void onClick(View view) {
                // Error check for blank names (names cannot be blank)
                Boolean validNames = true;
                for(int i = 0; i < players.size(); i++) {
                    if (players.getPlayerNode(i).getPlayerName().trim().equals("")) {
                        validNames = false;
                        break;
                    }
                }

                // Error check for grid size (cannot be smaller than 3x3 or larger than 10x18)
                Boolean validSize = true;
                int xSize = Integer.parseInt(gridSizeXInput.getText().toString());
                int ySize = Integer.parseInt(gridSizeYInput.getText().toString());
                if (xSize < 3 || xSize > 10 || ySize < 3 || ySize > 18) {
                    validSize = false;
                }

                if (validNames && validSize) { // If player names and grid size are valid
                    // Opens grid screen with data on players, grid size, and settings
                    Intent gameIntent = new Intent(getApplicationContext(), GridActivity.class);
                    gameIntent.putExtra("players", players);
                    gameIntent.putExtra("xSize", Integer.parseInt(gridSizeXInput.getText().toString()));
                    gameIntent.putExtra("ySize", Integer.parseInt(gridSizeYInput.getText().toString()));
                    gameIntent.putExtra("explosionSpeed", explosionSpeed);
                    gameIntent.putExtra("bgColor", ((ColorDrawable) getWindow().getDecorView().getBackground()).getColor());
                    startActivity(gameIntent);
                }
                else if (!validSize) { // Error pop-up if grid dimensions are invalid
                    Toast.makeText(GameMenuActivity.this, "Grid dimensions are not valid (must be larger than 3x3 and smaller than 10x18)", Toast.LENGTH_SHORT).show();
                }
                else { // Error pop-up if a player name is blank
                    Toast.makeText(GameMenuActivity.this, "Player names cannot be blank", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addPlayer(PlayerList.PlayerNode player) { // Method for adding players graphically
        // Creates a player item with a textbox, delete button, and color button assosciated to it
        View playerView = LayoutInflater.from(this).inflate(R.layout.playeritem, playerListLayout, false);

        // Adds the player item to the linear layout
        playerListLayout.addView(playerView);

        // Initializes the variables with the android studios component in the XML file
        EditText playerNameTextBox = playerView.findViewById(R.id.playerNameTextBox);
        Button colorButton = playerView.findViewById(R.id.colorButton);
        Button buttonDelete = playerView.findViewById(R.id.deleteButton);

        colorButton.setBackgroundColor(player.getPlayerColor()); // Sets color of color button to player color
        playerNameTextBox.setText(player.getPlayerName()); // Sets textbox text to the player name
        if (((ColorDrawable) getWindow().getDecorView().getBackground()).getColor() == Color.BLACK) {
            playerNameTextBox.setTextColor(Color.WHITE); // Dark mode on
        }
        else {
            playerNameTextBox.setTextColor(Color.BLACK); // Dark mode off
        }

        colorButton.setOnClickListener(new View.OnClickListener() { // Color button functionality
            @Override
            public void onClick(View view) {
                // Creates a color picker dialog
                showColorPickerDialog(players.getPlayerNode(playerListLayout.indexOfChild(playerView)), colorButton);
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() { // Delete player button functionality
            @Override
            public void onClick(View view) {
                if (players.size() == 2) { // Doesn't delete the player if there are only 2 players
                    Toast.makeText(GameMenuActivity.this, "Minimum of 2 players required", Toast.LENGTH_SHORT).show();
                }
                else { // If there are more than 2 players, deletes the player graphically and in the linked list
                    players.removePlayerNode(playerListLayout.indexOfChild(playerView));
                    playerListLayout.removeView(playerView);
                }
            }
        });

        playerNameTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 16) {
                    // Makes sure the name doesn't exceed 16 characters
                    editable.delete(16, editable.length());
                    Toast.makeText(GameMenuActivity.this, "Character limit for player name reached", Toast.LENGTH_SHORT).show();
                }
                else { // Sets the player name to the name inputted by the user in the textbox
                    players.getPlayerNode(playerListLayout.indexOfChild(playerView)).setPlayerName(editable.toString());
                }
            }
        });
    }

    private void showColorPickerDialog(PlayerList.PlayerNode player, Button colorButton) {
        // Create a new ColorDialog instance
        ColorDialog colorDialog = new ColorDialog(GameMenuActivity.this, player.getPlayerColor());

        // Sets the player color and color of the button to the color that the player selected (if ok is pressed)
        colorDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (colorDialog.getOkButtonClicked()) {
                    player.setPlayerColor(colorDialog.getSelectedColor());
                    colorButton.setBackgroundColor(colorDialog.getSelectedColor());
                }
            }
        });

        // Show the color picker dialog
        colorDialog.show();
    }
}