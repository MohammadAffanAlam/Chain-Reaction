package com.example.chainreaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.gridlayout.widget.GridLayout;

public class GridActivity extends AppCompatActivity {
    // Class that handles the game grid screen

    private TextView turnText; // Text that displays which players turn it is
    private GridLayout gameGrid; // Grid layout of ImageButtons
    private Boolean firstRound = true; // Boolean for whether it is the first turn of the game or not
    private ConstraintLayout animationLayout; // Layout where the explosion animations are displayed
    private int activeExplosions = 0; // Number of explosion animations running at once
    // Ensures the turn rotates once all explosions are done
    private int explosionSpeed = 250; // Speed of explosion animations (500 - explosionSpeed) milliseconds
    private Boolean buttonsEnabled = true; // Boolean for whether the playing grid is locked
    // If true, players can interact with the playing grid; If false, players cannot (when animations are playing)
    private ImageButton[][] buttons; // 2D array of ImageButtons which comprises the playing grid
    private Cell[][] cells; // Data for each cell in the playing grid
    class Cell {
        private int owner; // Index of which player owns the cell; 9 = empty
        private int orbs; // Number of orbs in the cell
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Method that runs when the grid screen is open
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid);

        // Initializes the variables with the android studios component in the XML file
        turnText = findViewById(R.id.turnText);
        gameGrid = findViewById(R.id.gameGrid);
        animationLayout = findViewById(R.id.animationLayout);
        Button backButtonGrid = (Button) findViewById(R.id.backButtonGrid);

        // Data received from other screens when grid screen opens up; Allows for data to be saved between screens
        Intent intent = getIntent();
        PlayerList players = (PlayerList) intent.getSerializableExtra("players");
        int columns = intent.getIntExtra("xSize", 6);
        int rows = intent.getIntExtra("ySize", 11);
        explosionSpeed = intent.getIntExtra("explosionSpeed", 250);
        if (intent.getIntExtra("bgColor", Color.WHITE) == Color.BLACK) { // Dark mode on
            getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            gameGrid.setBackgroundColor(Color.WHITE);
        }
        else { // Dark mode off
            getWindow().getDecorView().setBackgroundColor(Color.WHITE);
            gameGrid.setBackgroundColor(Color.BLACK);
        }

        if (players.getPointer().getPlayerName().charAt(players.getPointer().getPlayerName().length() - 1) == 's') {
            // If the player's name ends with s, doesn't add another s for possessive form
            turnText.setText(players.getPointer().getPlayerName() + "' turn");
        }
        else {
            turnText.setText(players.getPointer().getPlayerName() + "'s turn");
        }

        turnText.setTextColor(players.getPointer().getPlayerColor()); // Sets text color to the player's color
        // Sets playing grid size to the size inputted by the user
        gameGrid.setColumnCount(columns);
        gameGrid.setRowCount(rows);
        cells = new Cell[rows][columns]; // Initialize with the # of rows and columns inputted by the user
        buttons = new ImageButton[rows][columns];  // Initialize with the # of rows and columns inputted by the user

        backButtonGrid.setOnClickListener(new View.OnClickListener() {  // Back button functionality
            @Override
            public void onClick(View view) {
                // Opening game menu screen with data on players, grid size, and settings
                Intent gameMenuIntent = new Intent(getApplicationContext(), GameMenuActivity.class);
                gameMenuIntent.putExtra("players", players);
                gameMenuIntent.putExtra("xSize", cells[0].length);
                gameMenuIntent.putExtra("ySize", cells.length);
                gameMenuIntent.putExtra("explosionSpeed", explosionSpeed);
                gameMenuIntent.putExtra("bgColor", ((ColorDrawable) getWindow().getDecorView().getBackground()).getColor());
                startActivity(gameMenuIntent);
            }
        });

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Initializes the cell and sets as unoccupied and empty
                cells[i][j] = new Cell();
                cells[i][j].owner = 9;
                cells[i][j].orbs = 0;
                buttons[i][j] = new ImageButton(this); // Initializes the ImageButton

                // Code that sets the layout so the grid displays properly
                GridLayout.Spec rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                GridLayout.Spec colSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
                params.height = 0;
                params.width = 0;
                params.setMargins(4, 4, 4, 4);
                buttons[i][j].setLayoutParams(params);
                buttons[i][j].setScaleType(ImageView.ScaleType.FIT_CENTER);
                if (((ColorDrawable) gameGrid.getBackground()).getColor() == Color.BLACK) { // Dark mode on
                    buttons[i][j].setBackgroundColor(Color.WHITE);
                }
                else { // Dark mode off
                    buttons[i][j].setBackgroundColor(Color.BLACK);
                }

                // Sets final variables set to the for loop variables so they can be used within the ClickListener
                final int x = i;
                final int y = j;
                buttons[x][y].setOnClickListener(new View.OnClickListener() { // Button functionality for a cell
                    @Override
                    public void onClick(View v) {
                        if (buttonsEnabled) {
                            if (cells[x][y].owner == players.getPointer().getPos() || cells[x][y].owner == 9) {
                                // Adds an orb to the cell that the player clicked
                                addOrb(x, y, rows - 1, columns - 1, players);
                            }
                            else {
                                // Error pop-up if the player tries to place an orb in another player's cell
                                Toast.makeText(GridActivity.this, "Cell already occupied!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                gameGrid.addView(buttons[x][y]); // Adds button to the playing grid
            }
        }
    }

    public void addOrb (int x, int y, int i, int j, PlayerList players) {
        // x and y are the cell positions, i and j are the row/column limits (Value of the last row/column)
        if (cells[x][y].owner != players.getPointer().getPos()) {  // Checks if the player does NOT own the cell
            if (cells[x][y].owner != 9) { // Checks if the cell is NOT unoccupied (so if it is owned by another player)
                players.getPlayerNode(cells[x][y].owner).decreaseCellCount();
            }
            players.getPointer().increaseCellCount();
        }
        cells[x][y].orbs++;
        cells[x][y].owner = players.getPointer().getPos();
        buttonsEnabled = false; // Locks the buttons until the addOrb method is complete

        int cellCapacity; // Critical mass of the cells
        if ((x == 0 && y == 0) || (x == 0 && y == j) || (x == i && y == 0) || (x == i && y == j)) { // Corner cells
            cellCapacity = 1;
        }
        else if (x == 0 || y == 0 || x == i || y == j) { // Edge cells
            cellCapacity = 2;
        }
        else { // Center cells
            cellCapacity = 3;
        }

        if (cells[x][y].orbs > cellCapacity) { // Checks if the explosion conditions are met
            buttonsEnabled = false;
            players.getPointer().decreaseCellCount();
            // Resets the cell
            cells[x][y].orbs = 0;
            buttons[x][y].setImageDrawable(null);
            cells[x][y].owner = 9;

            if (x != 0) { // Cell above
                // Creates an imageView for the exploding animation
                ImageView orbView = createOrbVisual(players.getPointer().getPlayerColor(), x, y);
                activeExplosions++;
                orbView.animate()
                        .translationY(buttons[x - 1][y].getY()) // Orb travels to the cell above
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // Removing imageview once the animation is complete
                                animationLayout.removeView(orbView);
                                activeExplosions--;
                                addOrb(x - 1, y, i, j, players); // Recursion for chain reactions
                            }
                        })
                        .start();
            }
            if (y != j) { // Cell to the right
                ImageView orbView = createOrbVisual(players.getPointer().getPlayerColor(), x, y);
                activeExplosions++;
                orbView.animate()
                        .translationX(buttons[x][y + 1].getX()) // Orb travels to the cell to the right
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // Removing imageview once the animation is complete
                                animationLayout.removeView(orbView);
                                activeExplosions--;
                                addOrb(x, y + 1, i, j, players); // Recursion for chain reactions
                            }
                        })
                        .start();
            }
            if (x != i) { // Cell below
                ImageView orbView = createOrbVisual(players.getPointer().getPlayerColor(), x, y);
                activeExplosions++;
                orbView.animate()
                        .translationY(buttons[x + 1][y].getY()) // Orb travels to the cell below
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // Removing imageview once the animation is complete
                                animationLayout.removeView(orbView);
                                activeExplosions--;
                                addOrb(x + 1, y, i, j, players); // Recursion for chain reactions
                            }
                        })
                        .start();
            }
            if (y != 0) { // Cell to the left
                ImageView orbView = createOrbVisual(players.getPointer().getPlayerColor(), x, y);
                activeExplosions++;
                orbView.animate()
                        .translationX(buttons[x][y - 1].getX()) // Orb travels to the cell to the left
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                // Removing imageview once the animation is complete
                                animationLayout.removeView(orbView);
                                activeExplosions--;
                                addOrb(x, y - 1, i, j, players); // Recursion for chain reactions
                            }
                        })
                        .start();
            }
        }
        else { // If the explosion conditions are not met
            // Changes the image of the button in the cell
            Drawable orbDrawable;
            if (cells[x][y].orbs == cellCapacity) {
                orbDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.criticalorb, null);
            }
            else if (cells[x][y].orbs == 1) {
                orbDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.oneorb, null);
            }
            else {
                orbDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.twoorbs, null);
            }

            // Sets the image to be the color of the player
            orbDrawable.setColorFilter(players.getPointer().getPlayerColor(), PorterDuff.Mode.SRC_IN);

            buttons[x][y].setImageDrawable(orbDrawable);

            if (activeExplosions == 0 && !buttonsEnabled) { // If there are no active explosions, the turn rotates
                buttonsEnabled = true;
                rotateTurn(players);
            }
        }
    }
    public ImageView createOrbVisual(int color, int x, int y) { // Method that creates an orb for animations
        ImageView imageView = new ImageView(this); // Creating an imageview for the orb animation
        int buttonWidth = buttons[x][y].getWidth();
        int buttonHeight = buttons[x][y].getWidth();
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(buttonWidth, buttonHeight);
        imageView.setX(buttons[x][y].getX()); // Setting initial position of imageView
        imageView.setY(buttons[x][y].getY());
        imageView.setLayoutParams(layoutParams);
        ShapeDrawable circle = new ShapeDrawable(new OvalShape()); // Orb visual
        circle.setIntrinsicWidth(buttonWidth); // Setting dimensions of orb visual
        circle.setIntrinsicHeight(buttonHeight);
        circle.getPaint().setColor(color); // Setting color of orb visual
        imageView.setImageDrawable(circle); // Setting the orb visual as the image for imageview
        // Sets animation properties
        imageView.animate().setDuration(500 - explosionSpeed).setInterpolator(new LinearInterpolator());
        animationLayout.addView(imageView); // Adding the imageView onto the animationLayout
        return imageView;
    }

    public void rotateTurn(PlayerList players) { // Method that rotates the turn
        int originalIndex = players.getPointer().getPos(); // Index of the player before the turn rotates

        players.rotateTurn();
        if (!firstRound) { // Skips eliminated players (no cells occupied); only if it isn't the first round
            while (players.getPointer().getCellCount() == 0) {
                players.rotateTurn();
            }
        }

        if (players.getPointer().getPos() == 0) {
            // If the turn rotated to the first player then it is no longer the first turn
            firstRound = false;
        }

        if (players.getPointer().getPos() == originalIndex) { // Victory condition (all other players were skipped)
            // Creates and displays dialog with the victory message
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Game Over")
                    .setMessage(players.getPointer().getPlayerName() + " wins")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Opens game menu screen with data on players, grid size, and settings
                            Intent gameMenuIntent = new Intent(getApplicationContext(), GameMenuActivity.class);
                            gameMenuIntent.putExtra("players", players);
                            gameMenuIntent.putExtra("xSize", cells[0].length);
                            gameMenuIntent.putExtra("ySize", cells.length);
                            gameMenuIntent.putExtra("explosionSpeed", explosionSpeed);
                            gameMenuIntent.putExtra("bgColor", ((ColorDrawable) getWindow().getDecorView().getBackground()).getColor());
                            startActivity(gameMenuIntent);
                            dialog.dismiss();
                        }
                    })
                    .create()
                    .show();
        }
        else {
            turnText.setTextColor(players.getPointer().getPlayerColor()); // Sets text color to the player's color

            if (players.getPointer().getPlayerName().charAt(players.getPointer().getPlayerName().length() - 1) == 's') {
                // If the player's name ends with s, doesn't add another s for possessive form
                turnText.setText(players.getPointer().getPlayerName() + "' turn");
            }
            else {
                turnText.setText(players.getPointer().getPlayerName() + "'s turn");
            }
        }
    }
}