package com.kirbits.thedragonscrystal.activities;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.Page;
import com.kirbits.thedragonscrystal.models.SaveData;
import com.kirbits.thedragonscrystal.utils.SaveManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class StoryActivity extends AppCompatActivity {

    private static final int AUTO_SAVE_SLOT = 0;

    // Game state
    private Map<Integer, Page> pageMap;
    private List<Integer> visitedPages = new ArrayList<>();
    private Set<String> unlockedEndings = new HashSet<>();
    private int currPageId = 0;
    private int slotId = 1;  // default to manual Slot 1
    private boolean isDead = false;

    // UI
    private TextView storyText;
    private Button choice1, choice2, saveButton, homeButton;
    private ImageView backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        // Restore manual slot & state if coming from LoadGameActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("slot")) {
            slotId = intent.getIntExtra("slot", 1);
            currPageId = intent.getIntExtra("pageId", 0);
            visitedPages = intent.getIntegerArrayListExtra("visitedPages");
            unlockedEndings = new HashSet<>(
                    Objects.requireNonNull(intent.getStringArrayListExtra("unlockedEndings"))
            );
            isDead = intent.getBooleanExtra("isDead", false);
        }
        // ensure slotId is in [1..3]
        if (slotId < 1 || slotId > 3) slotId = 1;

        // bind UI
        storyText      = findViewById(R.id.story_text);
        choice1        = findViewById(R.id.choice1_button);
        choice2        = findViewById(R.id.choice2_button);
        saveButton     = findViewById(R.id.save_button);
        homeButton     = findViewById(R.id.home_button);
        backgroundImage= findViewById(R.id.story_background);

        loadStory();
        displayPage(currPageId);

        choice1.setOnClickListener(v -> goToPage(
                Objects.requireNonNull(pageMap.get(currPageId)).getChoice1Target()
        ));
        choice2.setOnClickListener(v -> goToPage(
                Objects.requireNonNull(pageMap.get(currPageId)).getChoice2Target()
        ));

        saveButton.setOnClickListener(v -> {
            SaveData data = new SaveData();
            data.setCurrentPageId(currPageId);
            data.setVisitedPageIds(new ArrayList<>(visitedPages));
            data.setUnlockedEndings(new HashSet<>(unlockedEndings));
            data.setDead(isDead);
            showSaveSlotDialog(data);
        });

        homeButton.setOnClickListener(v -> {
            startActivity(new Intent(this, MainMenuActivity.class));
            finish();
        });
    }

    private void loadStory() {
        try {
            AssetManager am = getAssets();
            InputStream in = am.open("story.json");
            InputStreamReader reader = new InputStreamReader(in);
            List<Page> pages = new Gson().fromJson(
                    reader, new TypeToken<List<Page>>(){}.getType()
            );
            pageMap = new HashMap<>();
            for (Page p : pages) pageMap.put(p.getId(), p);
        } catch (Exception e) {
            Log.e("StoryActivity", "Error loading story JSON", e);
        }
    }

    private void displayPage(int pageId) {
        Page page = pageMap.get(pageId);
        if (page == null) {
            Log.e("StoryActivity", "Page not found: " + pageId);
            return;
        }
        currPageId = pageId;
        if (!visitedPages.contains(pageId)) {
            visitedPages.add(pageId);
        }

        storyText.setText(page.getText());
        setBackgroundImage(page.getBackground());

        if (page.isDeath() || isFinal(page)) {
            choice1.setText("Continue");
            choice2.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            choice1.setOnClickListener(v -> showRestartDialog(
                    page.isDeath() ? "You Died!" : "The End!",
                    "Would you like to restart and keep your unlocked paths?"
            ));
        } else {
            choice1.setText(page.getChoice1Text());
            choice2.setText(page.getChoice2Text());
            choice2.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            choice1.setOnClickListener(v -> goToPage(page.getChoice1Target()));
            choice2.setOnClickListener(v -> goToPage(page.getChoice2Target()));
        }
    }

    private boolean isFinal(Page page) {
        return page.getChoice1Target() == null && page.getChoice2Target() == null;
    }

    private void goToPage(int targetId) {
        currPageId = targetId;
        Page page = pageMap.get(targetId);
        if (page == null) {
            Log.e("StoryActivity", "Invalid target: " + targetId);
            return;
        }
        if (!visitedPages.contains(targetId)) {
            visitedPages.add(targetId);
        }
        if (!page.isDeath() && isFinal(page)) {
            unlockedEndings.add("Ending: " + page.getId());
        }

        // save into manual slot
        saveProgress(slotId);
        displayPage(currPageId);
    }

    private void saveProgress(int slot) {
        try {
            // load existing
            SaveData existing = (slot == AUTO_SAVE_SLOT)
                    ? SaveManager.loadAutoSave(this)
                    : SaveManager.loadGame(this, slot);

            if (existing != null) {
                if (existing.getGloballyUnlockedPages() != null)
                    visitedPages.addAll(existing.getGloballyUnlockedPages());
                if (existing.getUnlockedEndings() != null)
                    unlockedEndings.addAll(existing.getUnlockedEndings());
            }

            // build and write
            SaveData out = new SaveData();
            out.setCurrentPageId(currPageId);
            out.setVisitedPageIds(new ArrayList<>(visitedPages));
            out.setGloballyUnlockedPages(new HashSet<>(visitedPages));
            out.setUnlockedEndings(new HashSet<>(unlockedEndings));
            out.setDead(isDead);

            SaveManager.saveGame(this, out, slot);
            Log.d("SaveDebug", "Saved slot " + slot + ": page=" + currPageId
                    + " visited=" + visitedPages.size());
        } catch (Exception e) {
            Log.e("SaveDebug", "Error saving slot " + slot, e);
        }
    }

    private void showSaveSlotDialog(SaveData data) {
        String[] options = {"Slot 1", "Slot 2", "Slot 3"};
        new AlertDialog.Builder(this)
                .setTitle("Choose Save Slot")
                .setItems(options, (d, which) -> {
                    int chosen = which + 1;
                    SaveManager.saveGame(this, data, chosen);
                    slotId = chosen;  // track manual slot
                    Toast.makeText(this,
                            "Game saved to Slot " + chosen, Toast.LENGTH_SHORT
                    ).show();
                    Log.d("StoryActivity", "Manual save slot=" + chosen);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showRestartDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Restart", (d, w) -> {
                    isDead = false;
                    currPageId = 0;
                    // choose which slot to merge into
                    showSaveSlotDialogForRestart();
                    displayPage(currPageId);
                })
                .setNegativeButton("Main Menu", (d, w) -> {
                    startActivity(new Intent(this, MainMenuActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showSaveSlotDialogForRestart() {
        String[] options = {"Slot 1", "Slot 2", "Slot 3"};
        new AlertDialog.Builder(this)
                .setTitle("Choose Slot for Progress")
                .setItems(options, (d, which) -> {
                    int chosen = which + 1;
                    // merge old + current
                    SaveData old = SaveManager.loadGame(this, chosen);
                    HashSet<Integer> merged = new HashSet<>();
                    if (old != null && old.getGloballyUnlockedPages() != null) {
                        merged.addAll(old.getGloballyUnlockedPages());
                    }
                    merged.addAll(visitedPages);

                    SaveData out = new SaveData();
                    out.setCurrentPageId(0);
                    out.setVisitedPageIds(new ArrayList<>());
                    out.setUnlockedEndings(new HashSet<>(unlockedEndings));
                    out.setGloballyUnlockedPages(merged);
                    out.setDead(false);

                    SaveManager.saveGame(this, out, chosen);
                    slotId = chosen;  // remember for future
                    Log.d("SaveDebug", "Restart merged into slot " + chosen
                            + " globalCount=" + merged.size());

                    // clear for new run
                    visitedPages.clear();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setBackgroundImage(String filename) {
        if (filename == null || filename.isEmpty()) return;
        try {
            InputStream in = getAssets().open("backgrounds/" + filename);
            Drawable d = Drawable.createFromStream(in, null);
            backgroundImage.setImageDrawable(d);
        } catch (Exception e) {
            Log.e("StoryActivity", "Error loading bg: " + filename, e);
        }
    }
}
