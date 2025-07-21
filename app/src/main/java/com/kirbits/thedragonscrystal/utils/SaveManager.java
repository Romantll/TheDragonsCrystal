package com.kirbits.thedragonscrystal.utils;

import android.content.Context;
import com.google.gson.Gson;
import com.kirbits.thedragonscrystal.models.SaveData;
import java.io.*;


public class SaveManager {
    public static void saveGame(Context context, SaveData data, String slot) {
        try {
            FileOutputStream fos = context.openFileOutput("save_slot_" + slot + ".json", Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            new Gson().toJson(data, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static SaveData loadGame(Context context, String slot) {
        try {
            FileInputStream fis = context.openFileInput("save_slot_" + slot + ".json");
            InputStreamReader reader = new InputStreamReader(fis);
            return new Gson().fromJson(reader, SaveData.class);
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }
}
