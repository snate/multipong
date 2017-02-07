package com.multipong.model;

import com.multipong.activity.GameActivity;
import com.multipong.persistence.MultipongDatabase;

public class SingleGame extends Game {

    private GameActivity activity;
    private String playerName;
    private MultipongDatabase database;

    public SingleGame(GameActivity activity) {
        this.activity = activity;
    }

    //TODO invoke setNumberOfLives before start  on SingleGame to have a game with more than one live
    @Override
    public void start(String playerName) {
        this.playerName = playerName;
        if (currentGame == null) {
            currentGame = new SingleGameThread(playerName, activity, getNumberOfLives());
            new Thread(currentGame).start();
        }

    }

    private class SingleGameThread extends AbsGameThread {
        private volatile boolean started = true;

        public SingleGameThread(String playerName, GameActivity activity, int lives) {
            super(playerName, activity, lives);
        }

        @Override
        public void ballOnTopOfTheField() {
            setYFactor(-1 * getYFactor());
        }

        @Override
        public void initialBallPosition() {
            if (started) {
                setX(Math.random());
                started = false;
            }
        }
    }
}
