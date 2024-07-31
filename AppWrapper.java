package com.engineerkoghar.engineerkoghar;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by janardan on 4/14/17.
 */

public class AppWrapper {

    public static boolean darkModeSet;
    public static final String PREFS_NAME = "erkogr";
    public static boolean isSettingChanged=false;

    public static boolean isDarkModeSet(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(PREFS_NAME,context.MODE_PRIVATE);
        boolean isSet = sharedPreferences.getBoolean("isDarkModeSet", false);
        if(isSet){
            darkModeSet=true;
            return true;
        }
        return false;
    }

    public static void setDarkMode(Context context,boolean value ){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDarkModeSet", value);
        editor.apply();
        darkModeSet=value;
    }
}
