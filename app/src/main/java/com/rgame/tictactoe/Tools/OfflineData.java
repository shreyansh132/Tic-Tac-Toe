package com.rgame.tictactoe.Tools;

import android.content.Context;
import android.content.SharedPreferences;

public class OfflineData {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public OfflineData(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(AppVariables.SHARED_PREFERENCE_TITLE,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public String getStoredName() {
        return sharedPreferences.getString(AppVariables.STORED_NAME,AppVariables.NOT_INITIALISED);
    }

    public void storeName(String name) {
        editor.putString(AppVariables.STORED_NAME,name);
        editor.commit();
    }

    public String getMusicMode() {
        return sharedPreferences.getString(AppVariables.STORED_NAME,AppVariables.NOT_INITIALISED);
    }

    public void setMusicMode(String name) {
        editor.putString(AppVariables.MUSIC_MODE,name);
        editor.commit();
    }
}
