package com.kirbits.thedragonscrystal.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.SaveData;
import com.kirbits.thedragonscrystal.utils.SaveManager;

public class LoadGameActivity extends AppCompatActivity {
    private Button slot1Button, slot2Button, slot3Button, autoSaveButton, homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        // 1) Direct binds instead of getIdentifier
        autoSaveButton = findViewById(R.id.button_autosave);
        slot1Button    = findViewById(R.id.button_slot1);
        slot2Button    = findViewById(R.id.button_slot2);
        slot3Button    = findViewById(R.id.button_slot3);
        homeButton     = findViewById(R.id.home_button);

        // Auto-save slot
        SaveData autoSave = SaveManager.loadAutoSave(this);
        if (autoSave != null) {
            autoSaveButton.setText(
                    getString(R.string.slot_continue, autoSave.getCurrentPageId())
            );
            autoSaveButton.setEnabled(true);
            autoSaveButton.setOnClickListener(v -> loadGame(0));
        } else {
            autoSaveButton.setText(R.string.auto_save_error);
            autoSaveButton.setEnabled(false);
        }

        // Slots 1–3
        slot1Button.setOnClickListener(v -> loadGame(1));
        slot1Button.setOnLongClickListener(v -> { showDeleteDialog(1); return true; });

        slot2Button.setOnClickListener(v -> loadGame(2));
        slot2Button.setOnLongClickListener(v -> { showDeleteDialog(2); return true; });

        slot3Button.setOnClickListener(v -> loadGame(3));
        slot3Button.setOnLongClickListener(v -> { showDeleteDialog(3); return true; });

        // Home
        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
        });

        updateSlotButtons();
    }

    private void loadGame(int slot) {
        SaveData data = (slot == 0)
                ? SaveManager.loadAutoSave(this)
                : SaveManager.loadGame(this, slot);

        if (data == null) {
            Toast.makeText(this,
                    getString(R.string.slot_empty, slot),
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Intent intent = new Intent(this, StoryActivity.class);
        intent.putExtra("slot", slot);
        intent.putExtra("pageId", data.getCurrentPageId());
        intent.putIntegerArrayListExtra(
                "visitedPages",
                new java.util.ArrayList<>(data.getVisitedPageIds())
        );
        intent.putStringArrayListExtra(
                "unlockedEndings",
                new java.util.ArrayList<>(data.getUnlockedEndings())
        );
        intent.putExtra("isDead", data.isDead());
        startActivity(intent);
    }

    private void showDeleteDialog(int slot){
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_save_title)            // you can also put these in strings.xml
                .setMessage(getString(R.string.delete_save_msg, slot))
                .setPositiveButton(R.string.delete, (d, w) -> {
                    SaveManager.deleteSave(this, slot);
                    Toast.makeText(
                            this,
                            getString(R.string.save_deleted, slot),
                            Toast.LENGTH_SHORT
                    ).show();
                    updateSlotButtons();
                })
                .setNegativeButton(R.string.cancel, (d, w) -> d.dismiss())
                .show();
    }

    private void updateSlotButtons(){
        // Slot 1
        applySlotText(slot1Button, 1);
        // Slot 2
        applySlotText(slot2Button, 2);
        // Slot 3
        applySlotText(slot3Button, 3);
    }

    private void applySlotText(Button btn, int slot){
        SaveData data = SaveManager.loadGame(this, slot);
        if (data == null) {
            btn.setText(getString(R.string.slot_empty, slot));
            btn.setEnabled(false);
        } else {
            btn.setText(
                    getString(R.string.slot_continue, data.getCurrentPageId())
            );
            btn.setEnabled(true);
        }
    }
}
