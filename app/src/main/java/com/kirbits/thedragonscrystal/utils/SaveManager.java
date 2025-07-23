package com.kirbits.thedragonscrystal.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.kirbits.thedragonscrystal.models.SaveData;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;

public class SaveManager {

    private static final String TAG = "SaveManager";
    private static final String[] SLOT_FILENAMES = {"save_slot1.json", "save_slot2.json", "save_slot3.json"};

    // Save the game to a file
    public static void saveGame(Context context, SaveData data, int slot) {
        try {
            File file = new File(context.getFilesDir(), SLOT_FILENAMES[slot - 1]);
            FileWriter writer = new FileWriter(file);
            Gson gson = new Gson();
            gson.toJson(data, writer);
            writer.close();
            Log.d(TAG, "Game saved successfully to slot " + slot);
        } catch (Exception e) {
            Log.e(TAG, "Error saving game to slot " + slot, e);
        }
    }

    // Load the game from a file
    public static SaveData loadGame(Context context, int slot) {
        try {
            File file = new File(context.getFilesDir(), SLOT_FILENAMES[slot - 1]);
            if (!file.exists()) return null;

            FileReader reader = new FileReader(file);
            Gson gson = new Gson();
            SaveData data = gson.fromJson(reader, SaveData.class);
            reader.close();
            Log.d(TAG, "Loading game from: " + file.getAbsolutePath());
            return data;
        } catch (Exception e) {
            Log.e(TAG, "Error loading game from slot " + slot, e);
            return null;
        }
    }

    // Delete a save file
    public static void deleteSave(Context context, int slot) {
        File file = new File(context.getFilesDir(), SLOT_FILENAMES[slot - 1]);
        if (file.exists() && !file.delete()) {
            Log.w(TAG, "Failed to delete save file: " + file.getName());
        }
    }
}
