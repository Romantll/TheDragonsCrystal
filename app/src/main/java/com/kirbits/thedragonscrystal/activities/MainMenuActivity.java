package com.kirbits.thedragonscrystal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kirbits.thedragonscrystal.R;

public class MainMenuActivity extends AppCompatActivity {
    //Declaring button names
    Button btnStart, btnLoad, btnEndings, btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnStart = findViewById(R.id.btn_start);
        btnLoad = findViewById(R.id.btn_load);
        btnEndings = findViewById(R.id.btn_endings);
        btnSettings = findViewById(R.id.btn_settings);


        //Register callback for button
        btnStart.setOnClickListener(v ->{
           Intent intent = new Intent(this, StoryActivity.class);
           startActivity(intent);
        });

        btnLoad.setOnClickListener(v ->{
            Intent intent = new Intent(this, LoadGameActivity.class);
            startActivity(intent);
        )};

        btnEndings.setOnClickListener(v ->{
            Intent intent = new Intent(this, EndingsActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v ->{
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}