package com.multipong.model;

import com.multipong.GameActivity;

public class SingleGame implements Game {

    private GameActivity activity;
    private String playerName;
    private GameThread currentGame = null;

    public SingleGame(GameActivity activity) {
        this.activity = activity;
    }

    @Override
    public void start(String playerName) {
        this.playerName = playerName;
        if (currentGame == null) {
            currentGame = new GameThread();
            new Thread(currentGame).start();
        }
    }

    private class GameThread implements Runnable {

        private double x = 0;
        private double y = 0;
        private double range = 40;    // see the game frame as a square of range x range
        private double xFactor = 1.0; // ball horizontal multiplier
        private double yFactor = 1.0; // ball vertical multiplier

        // TODO: Find out more about 0.90 hard-coded value
        // TODO  -> that is, when the ball impacts with the palette
        private double paletteHeight = 0.14;
        private boolean lose = false;

        @Override
        public void run() {
            activity.showPlayerName(playerName);
            x = Math.random();
            while (true) {
                x += xFactor / range;
                y += yFactor / range;
                if (x <= 0.0 || x >= 1.0)
                    xFactor *= -1;
                if (y <= 0.0) yFactor *= -1;
                if (!lose && y >= (1.0 - paletteHeight)) {
                    // TODO: Check impact with palette
                    // TODO  -> if yes, multiply yFactor by -1
                    // TODO  -> otw, set a variable like `lose or something similar to true
                    yFactor *= -1;
                }
                if (lose && y >= 1.0) {
                    // game ends when b >= 1.0 so that the player can see
                    activity.endGame();
                }
                activity.moveBall(x, y);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
