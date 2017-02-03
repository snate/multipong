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

        @Override
        public void run() {
            activity.showPlayerName(playerName);
            a = Math.random();
            while (true) {
                a += xFactor / range;
                b += 1 / range;
                if (a <= 0.0 || a >= 1.0)
                    xFactor *= -1;
                if (b > 1.0) b = 0.0;
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
