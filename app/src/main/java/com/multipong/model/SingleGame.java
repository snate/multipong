package com.multipong.model;

import android.widget.Toast;
import android.util.Log;

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
            currentGame = new SingleGameThread(playerName, activity);
            new Thread(currentGame).start();
        }

    }

    private class SingleGameThread extends AbsGameThread {
        private volatile boolean notStartedYet = true;

        public SingleGameThread(String playerName, GameActivity activity) {
            super(playerName, activity);
        }

        @Override
        public void ballOnTopOfTheField() {
            setYFactor(-1 * getYFactor());
        }

        @Override
        public void initialBallPosition() {
            if (notStartedYet) {
                setX(Math.random());
                notStartedYet = false;
            }
        }

        @Override
        public void resetGame() {
            started = true;
            setY(0.0);
        }

        public void ballBounced() { }
    }
}
