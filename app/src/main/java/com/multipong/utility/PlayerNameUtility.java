package com.multipong.utility;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class PlayerNameUtility {
    public static String playerName;

    public static void setPlayerName(String newPlayerName) {
        playerName = newPlayerName;
    }

    public static String getPlayerName() {
        return playerName;
    }
}
