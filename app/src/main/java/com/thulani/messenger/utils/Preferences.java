package com.thulani.messenger.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static SharedPreferences settings;
    private static final String PREFS_NAME = "APP_PREFS";
    public static String PIC_URL = "http://192.168.1.103/messenger/profile-pictures";
    public static String MAIN_URL = "http://192.168.1.103/messenger/api";
    public static final String DOWNLOADSDIR = "TMessenger";

    public static String getUserId(Context contxt) {
        settings = contxt.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString("id", "");
    }

    public static int getMaxPrice(Context contxt) {
        settings = contxt.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int id = settings.getInt("maxprice", 300);
        return id;
    }

    public static int getMinPrice(Context contxt) {
        settings = contxt.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int id = settings.getInt("minprice", 5);
        return id;
    }

    public static void setUserID(Context contxt, int id) {
        SharedPreferences.Editor editor;
        settings = contxt.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putInt("id", id);
        editor.apply();
    }

    public static String getPrefName(Context contxt, String key) {
        settings = contxt.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String value = settings.getString(key, "");
        return value;
    }

    public static void savePrefPair(Context contxt, String name, String value) {
        SharedPreferences.Editor editor;
        settings = contxt.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.putString(name, value);
        editor.apply();
    }

    public static String getNumber(Context contxt) {
        settings = contxt.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString("id", "");
    }

    public static String getUserName(Context contxt) {
        settings = contxt.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString("username", "");
    }

    public static String getProfilePic(Context contxt) {
        settings = contxt.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return settings.getString("profilepic", "");
    }

    public static void logout(Context contxt) {
        contxt.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
