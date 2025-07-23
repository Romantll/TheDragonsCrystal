package com.kirbits.thedragonscrystal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.SaveData;
import com.kirbits.thedragonscrystal.utils.SaveManager;

public class LoadGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        // Bind buttons
        Button slot1Button = findViewById(R.id.slot1_button);
        Button slot2Button = findViewById(R.id.slot2_button);
        Button slot3Button = findViewById(R.id.slot3_button);
        Button homeButton = findViewById(R.id.home_button);

        // Load save data for each slot
        setupSlotButton(slot1Button, 1);
        setupSlotButton(slot2Button, 2);
        setupSlotButton(slot3Button, 3);

        // Home button click → Go back to MainMenuActivity
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Loads the save data for a slot and updates the button text.
     * Adds a click listener to load the game or show a message if empty.
     */
    private void setupSlotButton(Button button, int slot) {
        SaveData saveData = SaveManager.loadGame(this, slot);

        if (saveData != null) {
            // Show slot as occupied with current page
            button.setText(getString(R.string.load_slot_filled, saveData.getCurrentPageId()));
            button.setOnClickListener(v -> loadGame(slot, saveData));
        } else {
            // Show slot as empty
            button.setText(getString(R.string.load_slot_empty, slot));
            button.setOnClickListener(v ->
                    Toast.makeText(this, getString(R.string.no_save_data), Toast.LENGTH_SHORT).show()
            );
        }
    }

    /**
     * Starts StoryActivity with the save data loaded.
     */
    private void loadGame(int slot, SaveData saveData) {
        Intent intent = new Intent(this, StoryActivity.class);
        intent.putExtra("slot", slot);
        intent.putExtra("pageId", saveData.getCurrentPageId());
        intent.putIntegerArrayListExtra("visitedPages", new java.util.ArrayList<>(saveData.getVisitedPageIds()));
        intent.putStringArrayListExtra("unlockedEndings", new java.util.ArrayList<>(saveData.getUnlockedEndings()));
        intent.putExtra("isDead", saveData.isDead());
        startActivity(intent);
    }
}
