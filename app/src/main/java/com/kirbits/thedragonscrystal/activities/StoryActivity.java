package com.kirbits.thedragonscrystal.activities;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.Page;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoryActivity extends AppCompatActivity {
    // Use a map so we can look up pages by their unique ID
    private Map<Integer, Page> pageMap;

    private int currPageId = 0;

    // UI components
    private TextView storyText;
    private Button choice1;
    private Button choice2;
    private ImageView backgroundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        // Bind views from XML layout
        storyText = findViewById(R.id.story_text);
        choice1 = findViewById(R.id.choice1_button);
        choice2 = findViewById(R.id.choice2_button);
        backgroundImage = findViewById(R.id.story_background);

        // Load story pages from assets/story.json
        loadStory();

        // Show the first page
        displayPage(currPageId);

        // Set listeners for the two choice buttons
        choice1.setOnClickListener(v -> goToPage(Objects.requireNonNull(pageMap.get(currPageId)).getChoice1Target()));
        choice2.setOnClickListener(v -> goToPage(Objects.requireNonNull(pageMap.get(currPageId)).getChoice2Target()));
    }

    // Reads the story.json file and loads pages into a map
    private void loadStory() {
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("story.json");
            InputStreamReader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            List<Page> pageList = gson.fromJson(reader, new TypeToken<List<Page>>(){}.getType());

            // Map each page ID to its Page object for quick lookup
            pageMap = new HashMap<>();
            for (Page page : pageList) {
                pageMap.put(page.getId(), page);
            }
        } catch (Exception e) {
            Log.e("StoryActivity", "Error loading story JSON", e);
        }
    }

    // Updates UI based on the current page
    private void displayPage(int pageId) {
        Page page = pageMap.get(pageId);

        // Safeguard in case the page ID isn't found
        if (page == null) {
            Log.e("StoryActivity", "Page ID not found: " + pageId);
            return;
        }

        currPageId = pageId;

        // Set the story text
        storyText.setText(page.getText());

        // Set the background image based on the JSON field
        setBackgroundImage(page.getBackground());

        // Set the buttons if choices are available
        if (page.getChoice1Text() != null && page.getChoice2Text() != null) {
            choice1.setText(page.getChoice1Text());
            choice2.setText(page.getChoice2Text());
            choice1.setVisibility(View.VISIBLE);
            choice2.setVisibility(View.VISIBLE);
        } else {
            // Hide buttons if it's an ending screen
            choice1.setVisibility(View.GONE);
            choice2.setVisibility(View.GONE);
        }
    }

    // Navigate to the next page
    private void goToPage(int targetId) {
        displayPage(targetId);
    }

    // Loads a background image from the assets/backgrounds folder
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
