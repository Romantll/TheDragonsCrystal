package com.kirbits.thedragonscrystal.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast; //Debug use

import androidx.compose.material3.AlertDialogKt;

import com.google.gson.Gson;
import com.kirbits.thedragonscrystal.models.SaveData;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class SaveManager {

    private static final String TAG = "SaveManager";
    private static final String[] SLOT_FILENAMES = {"save_slot1.json", "save_slot2.json", "save_slot3.json"};
    private static final String AUTOSAVE_FILENAME = "autosave.json";

    // Save the game to a file
    public static void saveGame(Context context, SaveData data, int slot) {
        try {
            // Load existing save first (if any)
            SaveData existing = loadGame(context, slot);
            if (existing != null) {
                // Merge globally unlocked pages
                Set<Integer> mergedUnlocked = new HashSet<>();
                if (existing.getGloballyUnlockedPages() != null)
                    mergedUnlocked.addAll(existing.getGloballyUnlockedPages());
                if (data.getGloballyUnlockedPages() != null)
                    mergedUnlocked.addAll(data.getGloballyUnlockedPages());
                data.setGloballyUnlockedPages(mergedUnlocked);
            }

            // Save to file
            File file = (slot == 0)
                    ? new File(context.getFilesDir(), AUTOSAVE_FILENAME)
                    : new File(context.getFilesDir(), SLOT_FILENAMES[slot - 1]);
            FileWriter writer = new FileWriter(file);
            new Gson().toJson(data, writer);
            writer.close();
            Log.d(TAG, "Game saved successfully to " + (slot == 0 ? "Auto-Save" : "slot " + slot));
        } catch (Exception e) {
            Log.e(TAG, "Error saving game", e);
        }
    }




    //Auto Save
    public static void saveAuto(Context context, SaveData data) {
        try {
            File file = new File(context.getFilesDir(), AUTOSAVE_FILENAME);
            FileWriter writer = new FileWriter(file);
            new Gson().toJson(data, writer);
            writer.close();
            Log.d(TAG, "Auto-saved successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error auto-saving game", e);
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

    public static SaveData loadAutoSave(Context context) {
        try {
            File file = new File(context.getFilesDir(), AUTOSAVE_FILENAME);
            if (!file.exists()) return null;
            FileReader reader = new FileReader(file);
            SaveData data = new Gson().fromJson(reader, SaveData.class);
            reader.close();
            return data;
        } catch (Exception e) {
            Log.e(TAG, "Error loading auto-save", e);
            return null;
        }
    }

    // Delete a save file
    public static void deleteSave(Context context, int slot) {
        File file = new File(context.getFilesDir(), SLOT_FILENAMES[slot - 1]);
        if (file.exists()) {
            if (!file.delete()) {
                Log.w(TAG, "Failed to delete save file: " + file.getName());
            } else {
                Log.d(TAG, "Deleted save file: " + file.getName());
            }
        } else {
            Log.d(TAG, "No save file found to delete for slot " + slot);
        }
    }

}
