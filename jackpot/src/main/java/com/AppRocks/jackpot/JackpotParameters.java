package com.AppRocks.jackpot;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class JackpotParameters {

    public static final String APP_PREFERENCES = "PrayerProPrefs";
    public static final String TAG = "PrayerProParameters";
    SharedPreferences PrayerProSettings;
    Editor editor;
    Context context;


    public JackpotParameters(Context context) {
        this.context = context;
    }

    private void openConnection() {
        PrayerProSettings = context.getSharedPreferences(APP_PREFERENCES,
                Context.MODE_PRIVATE);
        editor = PrayerProSettings.edit();
    }

    private void closeConnection() {
        editor = null;
        PrayerProSettings = null;
    }


    public void setInt(int value, String key) {
        openConnection();
        editor.putInt(key, value);
        editor.commit();
        closeConnection();
    }

    public void setLong(long value, String key) {
        openConnection();
        editor.putLong(key, value);
        editor.commit();
        closeConnection();
    }

    public void setFloat(float value, String key) {
        openConnection();
        editor.putFloat(key, value);
        editor.commit();
        closeConnection();
    }

    public void setString(String value, String key) {
        openConnection();
        editor.putString(key, value);
        editor.commit();
        closeConnection();
    }


    public void setBoolean(Boolean value, String key) {
        openConnection();
        editor.putBoolean(key, value);
        editor.commit();
        closeConnection();
    }


    public boolean getBoolean(String key, boolean defValue) {
        boolean result = defValue;
        openConnection();

        if (PrayerProSettings.contains(key)) {
            result = PrayerProSettings.getBoolean(key, defValue);
        }

        closeConnection();
        return result;
    }

    public int getInt(String key, int defValue) {
        int result = defValue;
        openConnection();

        if (PrayerProSettings.contains(key)) {
            result = PrayerProSettings.getInt(key, defValue);
        }

        closeConnection();
        return result;
    }

    public Long getLong(String key, long defValue) {
        long result = defValue;
        openConnection();

        if (PrayerProSettings.contains(key)) {
            result = PrayerProSettings.getLong(key, defValue);
        }

        closeConnection();
        return result;
    }

    public float getFloat(String key) {
        float result = 0;
        openConnection();

        if (PrayerProSettings.contains(key)) {
            result = PrayerProSettings.getFloat(key, 0);
        }

        closeConnection();
        return result;
    }

    public String getString(String key) {
        String result = "";
        openConnection();

        if (PrayerProSettings.contains(key)) {
            result = PrayerProSettings.getString(key, "");
        }

        closeConnection();
        return result;
    }


}
