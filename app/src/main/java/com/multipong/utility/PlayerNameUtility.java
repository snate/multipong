package com.multipong.utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class PlayerNameUtility {
    public static String playerName;
    private static final String sharedPrefs = "MULTIPONG_SHARED_PREFS";
    private static final String username = "player_name";
    private static Context context;

    public static void setContext(Context newContext) {
        context = newContext;
    }

    public static void setPlayerName(String newPlayerName) {
        playerName = newPlayerName;
        SharedPreferences.Editor editor =
                context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE).edit();
        editor.putString(username, newPlayerName);
        editor.apply();
    }

    public static String getPlayerName() {
        if (playerName == null) {
            SharedPreferences prefs =
                    context.getSharedPreferences(sharedPrefs, Context.MODE_PRIVATE);
            playerName = prefs.getString(username, null);
        }
        return playerName;
    }
}
