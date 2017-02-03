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

        private double a = 0;
        private double b = 0;
        private double range = 40;
        private double xFactor = 1.0;
        private double yFactor = 1.0;
        // TODO: Find out more about 0.90 hard-coded value
        // TODO  -> that is, when the ball impacts with the palette
        private double paletteHeight = 0.14;
        private boolean lose = false;

        @Override
        public void run() {
            activity.showPlayerName(playerName);
            a = Math.random();
            while (true) {
                a += xFactor / range;
                b += yFactor / range;
                if (a <= 0.0 || a >= 1.0)
                    xFactor *= -1;
                if (b <= 0.0) yFactor *= -1;
                if (!lose && b >= (1.0 - paletteHeight)) {
                    // TODO: Check impact with palette
                    // TODO  -> if yes, multiply yFactor by -1
                    // TODO  -> otw, set a variable like `lose or something similar to true
                    yFactor *= -1;
                }
                if (lose && b >= 1.0) {
                    // game ends when b >= 1.0 so that the player can see
                    activity.endGame();
                }
                activity.moveBall(a, b);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
