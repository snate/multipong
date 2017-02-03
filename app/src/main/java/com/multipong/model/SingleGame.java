package com.multipong.model;

import com.multipong.GameActivity;

public class SingleGame implements Game {

    private GameActivity activity;
    private String playerName;

    public SingleGame(GameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void start(String playerName) {
        this.playerName = playerName;
        new Thread(new GameThread()).start();
    }

    private class GameThread implements Runnable {

        @Override
        public void run() {
            activity.showPlayerName(playerName);
        }
    }
}
