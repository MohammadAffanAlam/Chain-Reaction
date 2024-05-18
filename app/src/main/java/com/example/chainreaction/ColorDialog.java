package com.example.chainreaction;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;


public class ColorDialog extends Dialog {
    // Class that handles the custom dialog box for choosing colors

    private int selectedColor; // Integer of the color selected by the sliders
    private View colorPreview; // View object that displays the selectedColor
    private SeekBar hueSeekBar; // Slider for color hue
    private SeekBar saturationSeekBar; // Slider for color saturation
    private SeekBar valueSeekBar; // Slider for color value (0 value = black)
    private Button okButton;
    private Button cancelButton;
    private Boolean isOkButtonClicked = false; // Boolean that checks if the dialog was dismissed by the ok button

    public ColorDialog(Context context, int initialColor) {
        super(context);
        selectedColor = initialColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Method that runs when the color dialog pops up
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_color_wheel);

        // Initializes the variables with the android studios component in the XML file
        colorPreview = findViewById(R.id.colorPreview);
        hueSeekBar = findViewById(R.id.hueSeekBar);
        saturationSeekBar = findViewById(R.id.saturationSeekBar);
        valueSeekBar = findViewById(R.id.valueSeekBar);
        okButton = findViewById(R.id.okButton);
        cancelButton = findViewById(R.id.cancelButton);

        colorPreview.setBackgroundColor(selectedColor); // Sets color of the colorPreview to the player color

        // Set the size/layout of the dialog
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Set up the color slider components
        float[] hsv = new float[3];
        Color.colorToHSV(selectedColor, hsv);

        hueSeekBar.setProgress((int) hsv[0]);
        saturationSeekBar.setProgress((int) (hsv[1] * 100));
        valueSeekBar.setProgress((int) (hsv[2] * 100));

        // Creates OnSeekBarChangeListener for each slider
        hueSeekBar.setOnSeekBarChangeListener(createSeekBarChangeListener());
        saturationSeekBar.setOnSeekBarChangeListener(createSeekBarChangeListener());
        valueSeekBar.setOnSeekBarChangeListener(createSeekBarChangeListener());

        okButton.setOnClickListener(new View.OnClickListener() { // Ok Button functionality
            @Override
            public void onClick(View view) {
                isOkButtonClicked = true;
                dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() { // Cancel button functionality (does nothing lol)
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    private SeekBar.OnSeekBarChangeListener createSeekBarChangeListener() { // Adds functionality for each slider
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float hue = hueSeekBar.getProgress();
                float saturation = saturationSeekBar.getProgress() / 100f;
                float value = valueSeekBar.getProgress() / 100f;

                // Updates the selectedColor when the sliders are adjusted
                selectedColor = Color.HSVToColor(new float[]{hue, saturation, value});

                // Updates the color preview
                colorPreview.setBackgroundColor(selectedColor);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }

    public int getSelectedColor() {
        return selectedColor;
    }

    public Boolean getOkButtonClicked() {
        return isOkButtonClicked;
    }
}
