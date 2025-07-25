package com.kirbits.thedragonscrystal.activities;

import android.view.View.OnLongClickListener;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.SaveData;
import com.kirbits.thedragonscrystal.utils.SaveManager;
//TODO change "getIdentifier" declarations into direct binds due to prior debug issues
//Works for now but will incur technical debt as codebase expands
public class LoadGameActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        Button autoSaveButton = findViewById(R.id.button_autosave);
        SaveData autoSave = SaveManager.loadAutoSave(this);

        if (autoSave != null) {
            autoSaveButton.setText("Auto-Save - Page " + autoSave.getCurrentPageId());
            autoSaveButton.setEnabled(true);
            autoSaveButton.setOnClickListener(v -> loadGame(0)); // Slot 0 reserved for Auto-Save
        } else {
            autoSaveButton.setText("No Auto-Save Found");
            autoSaveButton.setEnabled(false);
        }

        // Bind buttons
        updateSlotButtons();
        Button homeButton = findViewById(R.id.home_button);

        //Set up long press for deletion
        for (int i =1; i <= 3; i++){
            int slot = i;
            Button button = findViewById(getResources().getIdentifier("button_slot" + i, "id", getPackageName()));

            //Load game on click
            button.setOnClickListener(v -> loadGame(slot));

            //Delete save on long press
            button.setOnLongClickListener(v -> {
                showDeleteDialog(slot);
                return true;
            });
        }
        // Home button click → Go back to MainMenuActivity
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });
    }

    /**
     * Starts StoryActivity with the save data loaded.
     */
    private void loadGame(int slot) {
        SaveData data = (slot == 0) ? SaveManager.loadAutoSave(this) : SaveManager.loadGame(this, slot);
        if (data == null){
            Toast.makeText(this,"No Save data in slot " + slot, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, StoryActivity.class);
        intent.putExtra("slot", slot);
        intent.putExtra("pageId", data.getCurrentPageId());
        intent.putIntegerArrayListExtra("visitedPages", new java.util.ArrayList<>(data.getVisitedPageIds()));
        intent.putStringArrayListExtra("unlockedEndings", new java.util.ArrayList<>(data.getUnlockedEndings()));
        intent.putExtra("isDead", data.isDead());
        startActivity(intent);
    }

    /**
     * Deletes save on long press
     */
    private void showDeleteDialog(int slot){
        new AlertDialog.Builder(this)
                .setTitle("Delete Save?")
                .setMessage("Are you sure you want to delete save slot " + slot + "? This cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    SaveManager.deleteSave(this, slot);
                    Toast.makeText(this, "Save slot " + slot + " deleted.", Toast.LENGTH_SHORT).show();
                    updateSlotButtons();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }


    //TODO add string to strings.xml and replace hardcoded string
    private void updateSlotButtons(){
        for (int i = 1; i <= 3; i++){
            SaveData data = SaveManager.loadGame(this, i);
            Button button = findViewById(getResources().getIdentifier("button_slot" + i, "id", getPackageName()));
            if (data == null){
                button.setText("Empty Slot "+ i);
            }else {
                button.setText("Continue - Page " + data.getCurrentPageId());
            }
        }
    }
}
