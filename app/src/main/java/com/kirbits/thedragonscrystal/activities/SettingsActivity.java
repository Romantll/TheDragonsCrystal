package com.kirbits.thedragonscrystal.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.kirbits.thedragonscrystal.R;
import android.widget.Button;
import android.app.AlertDialog;
import com.kirbits.thedragonscrystal.utils.SaveManager;
import android.widget.Toast;
import android.widget.ImageButton;
import android.content.Intent;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ImageButton homeButton = findViewById(R.id.home_button);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        });

        // TODO: Allow user to toggle sound/music (optional)
        // TODO: Add dark/light mode toggle (optional)
        // TODO: Think of more settings lol

        Button resetProgressButton = findViewById(R.id.reset_progress_button);
        resetProgressButton.setOnClickListener(v -> showResetProgressDialog());
    }

    private void showResetProgressDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset All Progress")
                .setMessage("Are you sure you want to reset ALL game progress? This will delete all save files (including Auto-Save) and cannot be undone.")
                .setPositiveButton("Reset All", (dialog, which) -> resetAllProgress())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void resetAllProgress() {
        for (int slot = 1; slot <= 3; slot++) {
            SaveManager.deleteSave(this, slot);
        }
        // Reset/clear auto-save as well
        SaveManager.clearAutoSave(this);

        Toast.makeText(this, "All game progress (including Auto-Save) has been reset.", Toast.LENGTH_LONG).show();
    }
}
