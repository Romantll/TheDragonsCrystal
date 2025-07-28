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

    // Map for quick lookup of pages by ID
    private Map<Integer, Page> pageMap;

    // Tracking game state
    private List<Integer> visitedPages = new ArrayList<>();
    private Set<String> unlockedEndings = new HashSet<>();
    private int currPageId = 0;
    private int slotId = -1;
    private static final int AUTO_SAVE_SLOT = 0;
    private boolean isDead = false;


    // UI components
    private TextView storyText;
    private Button choice1;
    private Button choice2;
    private Button saveButton;
    private ImageView backgroundImage;
    private Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        Intent intent = getIntent();

        // Load save data if this is coming from LoadGameActivity
        if (intent != null && intent.hasExtra("slot")) {
            slotId = intent.getIntExtra("slot", 1);
            currPageId = intent.getIntExtra("pageId", 0);
            visitedPages = intent.getIntegerArrayListExtra("visitedPages");
            unlockedEndings = new HashSet<>(Objects.requireNonNull(intent.getStringArrayListExtra("unlockedEndings")));
            isDead = intent.getBooleanExtra("isDead", false);
        } else {
            // Fresh game: default to slot 1
            slotId = 1;
            currPageId = 0;
            visitedPages = new ArrayList<>();
            unlockedEndings = new HashSet<>();
            isDead = false;
        }

        // Bind views
        storyText = findViewById(R.id.story_text);
        choice1 = findViewById(R.id.choice1_button);
        choice2 = findViewById(R.id.choice2_button);
        backgroundImage = findViewById(R.id.story_background);
        saveButton = findViewById(R.id.save_button);
        homeButton = findViewById(R.id.home_button);

        // Load story
        loadStory();
        displayPage(currPageId);

        // Choice listeners
        choice1.setOnClickListener(v -> goToPage(Objects.requireNonNull(pageMap.get(currPageId)).getChoice1Target()));
        choice2.setOnClickListener(v -> goToPage(Objects.requireNonNull(pageMap.get(currPageId)).getChoice2Target()));

        // Save listener
        saveButton.setOnClickListener(v -> {
            SaveData data = new SaveData();
            data.setCurrentPageId(currPageId);
            data.setVisitedPageIds(visitedPages);
            data.setUnlockedEndings(unlockedEndings);
            data.setDead(isDead);

            showSaveSlotDialog(data); // Open popup instead of saving directly
        });



        // Home button listener
        homeButton.setOnClickListener(v -> {
            Intent homeIntent = new Intent(this, MainMenuActivity.class);
            startActivity(homeIntent);
            finish();
        });
    }

    /** Load story JSON into a map */
    private void loadStory() {
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("story.json");
            InputStreamReader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            List<Page> pageList = gson.fromJson(reader, new TypeToken<List<Page>>(){}.getType());

            pageMap = new HashMap<>();
            for (Page page : pageList) {
                pageMap.put(page.getId(), page);
            }
        } catch (Exception e) {
            Log.e("StoryActivity", "Error loading story JSON", e);
        }
    }

    /** Update the screen to show a specific page */
    private void displayPage(int pageId) {
        Page page = pageMap.get(pageId);
        if (page == null) {
            Log.e("StoryActivity", "Page ID not found: " + pageId);
            return;
        }

        currPageId = pageId;
        storyText.setText(page.getText());
        setBackgroundImage(page.getBackground());

        if (page.isDeath()) {
            // Death page: Show only one "Continue" button
            choice1.setText("Continue");
            choice1.setVisibility(View.VISIBLE);
            choice2.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);

            choice1.setOnClickListener(v -> showRestartDialog(
                    page.isDeath() ? "You Died!" : "The End!",
                    page.isDeath() ? "Would you like to restart and keep your unlocked paths?" :
                            "Would you like to restart and keep your unlocked paths?"
            ));

        } else {
            // Normal page: Show two choices
            if (page.getChoice1Text() != null && page.getChoice2Text() != null) {
                choice1.setText(page.getChoice1Text());
                choice2.setText(page.getChoice2Text());
                choice1.setVisibility(View.VISIBLE);
                choice2.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);

                choice1.setOnClickListener(v -> goToPage(page.getChoice1Target()));
                choice2.setOnClickListener(v -> goToPage(page.getChoice2Target()));
            } else {
                choice1.setVisibility(View.GONE);
                choice2.setVisibility(View.GONE);
            }
        }
    }


    /** Helper: Check if this page is a final ending (no further choices) */
    private boolean isFinalPage(Page page) {
        return page.getChoice1Target() == null && page.getChoice2Target() == null;
    }

    /** Move to selected page and save **/
    private void goToPage(int targetId) {
        visitedPages.add(targetId);
        currPageId = targetId;

        Page page = pageMap.get(targetId);
        if (page == null) {
            Log.e("StoryActivity", "Invalid page target: " + targetId);
            return;
        }

        // Determine page type
        boolean isPageDeath = page.isDeath();
        boolean isTrueEnding = !isPageDeath && page.getChoice1Target() == null && page.getChoice2Target() == null;

        // Save progress for all cases
        if (isTrueEnding) {
            unlockedEndings.add("Ending: " + page.getId());
            Log.d("SaveDebug", "Unlocked true ending: Ending: " + page.getId());
        }
        isDead = isPageDeath;
        saveProgress(AUTO_SAVE_SLOT);

        // Always show the page content first
        displayPage(currPageId);

        // Handle Death or Ending Pages
        if (isPageDeath || isTrueEnding) {
            // Replace choices with a single "Continue" button
            choice1.setText("Continue");
            choice1.setVisibility(View.VISIBLE);
            choice2.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);

            // When tapped, show the restart dialog
            choice1.setOnClickListener(v -> showRestartDialog(
                    isPageDeath ? "You Died!" : "The End!",
                    "Would you like to restart and keep your unlocked paths?"
            ));
            return;
        }

        // Handle Normal Page
        choice1.setOnClickListener(v -> goToPage(page.getChoice1Target()));
        choice2.setOnClickListener(v -> goToPage(page.getChoice2Target()));
    }


    /** Saves progress to a slot (keeps global progress) **/
    private void saveProgress(int slot) {
        SaveData data = new SaveData();
        data.setCurrentPageId(currPageId);
        data.setVisitedPageIds(new ArrayList<>(visitedPages));
        data.setUnlockedEndings(new HashSet<>(unlockedEndings));
        data.setGloballyUnlockedPages(new HashSet<>(visitedPages)); // Merge visited into global
        data.setDead(isDead);
        SaveManager.saveGame(this, data, slot);
    }



    /** Show a popup for selecting a manual save slot */
    private void showSaveSlotDialog(SaveData data) {
        String[] slotOptions = {"Slot 1", "Slot 2", "Slot 3"};

        new AlertDialog.Builder(this)
                .setTitle("Choose Save Slot")
                .setItems(slotOptions, (dialog, which) -> {
                    int chosenSlot = which + 1; // which = 0..2, slots are 1..3
                    SaveManager.saveGame(this, data, chosenSlot);
                    slotId = chosenSlot; // Track the chosen slot for future manual saves
                    Toast.makeText(this, "Game saved to Slot " + chosenSlot, Toast.LENGTH_SHORT).show();
                    Log.d("StoryActivity", "Manual save to slot " + chosenSlot);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }


    /** Load background image for the current page */
    private void setBackgroundImage(String filename) {
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("backgrounds/" + filename);
            Drawable drawable = Drawable.createFromStream(inputStream, null);
            backgroundImage.setImageDrawable(drawable);
        } catch (Exception e) {
            Log.e("StoryActivity", "Error loading background image: " + filename, e);
        }
    }

    /** Unified restart dialog for deaths and endings */
    private void showRestartDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Restart", (dialog, which) -> {
                    isDead = false;
                    currPageId = 0;
                    visitedPages.clear();
                    showSaveSlotDialogForRestart(); // Ask which slot to save to
                    displayPage(currPageId);
                })
                .setNegativeButton("Main Menu", (dialog, which) -> {
                    startActivity(new Intent(this, MainMenuActivity.class));
                    finish();
                })
                .setCancelable(false)
                .show();
    }


    /** Show popup to select a slot for meta-progression on restart */
    private void showSaveSlotDialogForRestart() {
        String[] slotOptions = {"Slot 1", "Slot 2", "Slot 3"};

        new AlertDialog.Builder(this)
                .setTitle("Choose Save Slot for Progress")
                .setItems(slotOptions, (dialog, which) -> {
                    int chosenSlot = which + 1;

                    // Prepare save data
                    SaveData metaSave = new SaveData();
                    metaSave.setCurrentPageId(0); // restart at beginning
                    metaSave.setVisitedPageIds(new ArrayList<>()); // clear session
                    metaSave.setUnlockedEndings(unlockedEndings);
                    metaSave.setGloballyUnlockedPages(new HashSet<>(visitedPages)); // persist unlocked paths
                    metaSave.setDead(false);

                    SaveManager.saveGame(this, metaSave, chosenSlot);
                    Log.d("SaveDebug", "Restart progress saved to slot " + chosenSlot);

                    // Reset state and restart story
                    isDead = false;
                    currPageId = 0;
                    visitedPages.clear();
                    displayPage(currPageId);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
