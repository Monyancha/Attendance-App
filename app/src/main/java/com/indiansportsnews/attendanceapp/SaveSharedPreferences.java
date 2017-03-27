package com.indiansportsnews.attendanceapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreferences {
    static final String PREF_ID = "lastId";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setLastId(Context ctx, int id) {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_ID , id);
        editor.commit();
    }

    public static int getLastId(Context ctx) {
        return getSharedPreferences(ctx).getInt(PREF_ID , 0);
    }
}
