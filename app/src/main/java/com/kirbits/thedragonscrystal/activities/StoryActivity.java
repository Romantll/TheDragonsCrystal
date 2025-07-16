package com.kirbits.thedragonscrystal.activities;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.util.Log;
import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.Page;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class StoryActivity extends AppCompatActivity{
    private List<Page> pages;
    private int currPageId =0;
    private TextView storyText;
    private Button choice1;
    private Button choice2;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storyText = findViewById(R.id.story_text);
        choice1 = findViewById(R.id.choice1_button);
        choice2 = findViewById(R.id.choice2_button);

        loadStory();

        displayPage(currPageId);

        choice1.setOnClickListener(v-> goToPage(pages.get(currPageId).getChoice1Target()));
    }

    private void loadStory(){
        try {
            AssetManager assetManager = getAssets();
            InputStream inputStream = assetManager.open("story.json");
            InputStreamReader reader = new InputStreamReader(inputStream);

            Gson gson = new Gson();
            pages = gson.fromJson(reader, new TypeToken<List<Page>>() {
            }.getType());
        } catch (Exception e) {
            //Tags error log
            Log.e("StoryActivity", "Error loading story JSON", e);

        }
    }

    private void displayPage(int pageId){
        Page page = pages.get(pageId);
        currPageId = pageId;

        storyText.setText(page.getText());

        if (page.getChoice1Text() != null && page.getChoice2Text() != null) {

            choice1.setText(page.getChoice1Text());
            choice2.setText(page.getChoice2Text());
            choice1.setVisibility(View.VISIBLE);
            choice2.setVisibility(View.VISIBLE);
        }else {
            //If it is an ending screen or no choices to choose from
            choice1.setVisibility(View.GONE);
            choice2.setVisibility(View.GONE);
        }
    }

    private void goToPage(int targetId){
        displayPage(targetId);
    }
}

