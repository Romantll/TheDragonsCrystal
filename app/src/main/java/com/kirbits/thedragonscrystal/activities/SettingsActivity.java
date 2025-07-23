package com.kirbits.thedragonscrystal.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.kirbits.thedragonscrystal.R;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // TODO: Allow user to toggle sound/music (optional)
        // TODO: Add dark/light mode toggle (optional)
        // TODO: Think of more settings lol
        Button resetProgressButton = findViewById(R.id.reset_progress_button);
        resetProgressButton.setOnClickListener(v -> showResetProgressDialog());
    }
    private void showResetProgressDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Reset All Progress")
                .setMessage("Are you sure you want to reset ALL game progress?")
                .setPositiveButton("Reset All", (dialog, which) -> {
                    resetAllProgress();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void resetAllProgress() {
        for (int slot = 1; slot <= 3; slot++) {
            SaveManager.deleteSave(this, slot);
        }
        Toast.makeText(this, "All game progress has been reset.", Toast.LENGTH_LONG).show();
        }
}
