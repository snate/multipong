package com.multipong.model;

import com.multipong.GameActivity;
import com.multipong.persistence.MultipongDatabase;

public class SingleGame implements Game {

    private GameActivity activity;
    private String playerName;
    private GameThread currentGame = null;
    private MultipongDatabase database;

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

    @Override
    public void setPaletteWidth(double width) {
        currentGame.setPaletteWidth(width);
    }

    @Override
    public void providePalettePosition(double position) {
        currentGame.setPalettePosition(position);
    }

    private class GameThread implements Runnable {

        private double x = 0;         // ball x position
        private double y = 0;         // ball y position
        private double range = 25;    // see the game frame as a square of range x range
        private double xFactor = 1.0; // ball horizontal multiplier
        private double yFactor = 1.0; // ball vertical multiplier

        // TODO: Find out more about 0.90 hard-coded value
        // TODO  -> that is, when the ball impacts with the palette
        private double paletteHeight = 0.14;
        private boolean lose = false;

        private double palettePosition = 0.0;
        private double paletteWidth = 0;

        private int delay = 150;
        private int score = 0;

        public void setPaletteWidth(double paletteWidth) {
            this.paletteWidth = paletteWidth;
        }

        @Override
        public void run() {
            activity.showPlayerName(playerName);
            x = Math.random();
            while (!lose && y < 1.0) {
                x += xFactor / range;
                y += yFactor / range;
                if (x <= 0.0 || x >= 1.0) xFactor *= -1;
                if (y <= 0.0)             yFactor *= -1;
                if (!lose && y >= (1.0 - paletteHeight))
                    if (isColliding()) {
                        if (delay > 11) delay -= 10;
                        // TODO: compute bounce direction
                        yFactor *= -1;
                        activity.updateScore(++score);
                    }
                    else
                        lose = true;
                activity.moveBall(x, y);
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            activity.endGame(score);
        }

        public void setPalettePosition(double palettePosition) {
            this.palettePosition = palettePosition;
        }

        private boolean isColliding() {
            // TODO: Fix bug when ball is near to the right edge: collision not detected
            return x >= palettePosition - paletteWidth/2 &&
                   x <= palettePosition + paletteWidth/2;
        }
    }
}
