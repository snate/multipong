package com.multipong.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.multipong.R;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class PlayerNameUtility {
    public static final String PLAYER_NAME = "com.multipong.PLAYER_NAME";

    public static void setPlayerName(Activity activity, String playerName) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.shared_preferences_file), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(PLAYER_NAME, playerName);
        editor.apply();
    }

    public static String getPlayerName(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences(
                activity.getString(R.string.shared_preferences_file), Context.MODE_PRIVATE);
        return sharedPref.getString(PLAYER_NAME, "ERROR");
    }
}
