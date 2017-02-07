package com.multipong.model;

import com.multipong.activity.GameActivity;
import com.multipong.persistence.MultipongDatabase;

public class SingleGame extends Game {

    private GameActivity activity;
    private String playerName;
    private SingleGameThread currentGame = null;
    private MultipongDatabase database;

    public SingleGame(GameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void start(String playerName) {
        this.playerName = playerName;
        if (currentGame == null) {
            currentGame = new SingleGameThread(playerName, activity);
            new Thread(currentGame).start();
        }

    }

    private class SingleGameThread extends AbsGameThread {
        private volatile boolean started = true;

        public SingleGameThread(String playerName, GameActivity activity) {
            super(playerName, activity);
        }

        @Override
        public void ballOnTopOfTheField() {
            setXFactor(-1 * getYFactor());
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
