package com.kirbits.thedragonscrystal.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.kirbits.thedragonscrystal.R;

public class LoadGameActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        // TODO: Load saved story state from storage or shared preferences
        // TODO: Display list of saved games (if more than one)
        // TODO: Resume story from selected save point
    }
}
