package com.kirbits.thedragonscrystal.activities;

import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import android.util.Log;
import com.kirbits.thedragonscrystal.R;
import com.kirbits.thedragonscrystal.models.Page;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class StoryActivity extends AppCompatActivity{
    private List<Page> pages;
    private int currPageId =0;
    private TextView storyText;
    private Button choice1;
    private Button choice2;
    private ImageView backgroundImage;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        storyText = findViewById(R.id.story_text);
        choice1 = findViewById(R.id.choice1_button);
        choice2 = findViewById(R.id.choice2_button);
        backgroundImage = findViewById(R.id.background_image);

        loadStory();

        displayPage(currPageId);

        choice1.setOnClickListener(v-> goToPage(pages.get(currPageId).getChoice1Target()));
        choice2.setOnClickListener(v -> goToPage(pages.get(currPageId).getChoice2Target()));
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

        //Sets background dynamically
        if(page.getBackground() != null){
            try {
                InputStream bgStream = getAssets().open("backgrounds/" + page.getBackground());
                Drawable drawable = Drawable.createFromStream(bgStream, null);
                backgroundImage.setImageDrawable(drawable);
            }catch (IOException e){
                //logging if background is not found
                Log.e("StoryActivity","Background image not found: " + page.getBackground());
            }
        }

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

    //TODO set up dyanmic image display logic via json or case statments
}

