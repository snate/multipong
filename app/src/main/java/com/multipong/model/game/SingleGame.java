package com.multipong.model.game;

import com.multipong.activity.GameActivity;
import com.multipong.persistence.MultipongDatabase;

public class SingleGame extends Game {

    private GameActivity activity;
    private String playerName;
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

    @Override
    public boolean isMultiplayer(){
        return false;
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
            if (notStartedYet)
                notStartedYet = false;
        }

        @Override
        public void resetGame() {
            notStartedYet = true;
            setY(0.0);
        }

        public void ballBounced(boolean bounced) { }
    }
}
