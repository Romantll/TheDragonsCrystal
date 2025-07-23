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
    private int slotId = 1;
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
        homeButton = findViewById(R.id.home_button); // <-- New

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

            SaveManager.saveGame(this, data, slotId);
            Toast.makeText(this, "Game saved to slot " + slotId, Toast.LENGTH_SHORT).show();
            Log.d("StoryActivity", "Manual save to slot " + slotId);
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

        // Show/hide choice buttons
        if (page.getChoice1Text() != null && page.getChoice2Text() != null) {
            choice1.setText(page.getChoice1Text());
            choice2.setText(page.getChoice2Text());
            choice1.setVisibility(View.VISIBLE);
            choice2.setVisibility(View.VISIBLE);
        } else {
            choice1.setVisibility(View.GONE);
            choice2.setVisibility(View.GONE);
        }
    }

    /** Move to the selected page, updating save state */
    private void goToPage(int targetId) {
        visitedPages.add(targetId);
        currPageId = targetId;

        Page page = pageMap.get(targetId);

        if (page != null && page.isDeath()) {
            isDead = true;
            unlockedEndings.add("Ending: " + page.getId());
        }

        // Always save to current slot
        SaveData saveData = new SaveData();
        saveData.setCurrentPageId(currPageId);
        saveData.setVisitedPageIds(visitedPages);
        saveData.setUnlockedEndings(unlockedEndings);
        saveData.setDead(isDead);
        SaveManager.saveGame(this, saveData, slotId);
        Log.d("StoryActivity", "Auto-saved to slot " + slotId);

        displayPage(targetId);
    }


    /** Save the game manually */
    private void saveGame() {
        if (slotId == -1) {
            Toast.makeText(this, "No save slot selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        SaveData data = new SaveData();
        data.setCurrentPageId(currPageId);
        data.setVisitedPageIds(visitedPages);
        data.setUnlockedEndings(unlockedEndings);
        data.setDead(isDead);

        SaveManager.saveGame(this, data, slotId);
        Toast.makeText(this, getString(R.string.save_success, slotId), Toast.LENGTH_SHORT).show();
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
}
