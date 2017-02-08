package com.multipong.model;

import android.widget.Toast;

import com.multipong.activity.GameActivity;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 *
 * this class uses Template method pattern for manage singleplayer and multiplayer game
 */
public abstract class AbsGameThread implements Runnable {
    private double x = 0;         // ball x position
    private double y = 0;         // ball y position
    private double range = 25;    // see the game frame as a square of range x range
    private double xFactor = 1.0; // ball horizontal multiplier
    private double yFactor = 1.0; // ball vertical multiplier

    private boolean lose = false;
    private int lives;

    private volatile double palettePosition = 0.0;
    private volatile double paletteWidth = 0;

    private int delay = 150;
    private int score = 0;

    private String playerName;
    private GameActivity activity;

    public AbsGameThread(String playerName, GameActivity activity, int lives) {
        this.lives = lives;
        this.activity = activity;
        this.playerName = playerName;
    }

    public double getX() {return x;}
    public double getY() {return y;}
    public double getYFactor() {return yFactor;}
    public double getXFactor() {return xFactor;}

    public void setX(double x) {this.x = x;}
    public void setY(double y) {this.y = y;}
    public void setXFactor(double xFactor) {this.xFactor = xFactor;}
    public void setYFactor(double yFactor) {this.yFactor = yFactor;}

    @Override
    public void run() {
        activity.showPlayerName(playerName);
        while (!lose) {
            initialBallPosition();
            x += xFactor / range;
            y += yFactor / range;
            if (x <= 0.0 || x >= 1.0) xFactor *= -1;
            if (y <= 0.0)             ballOnTopOfTheField();
            if (!lose && y >= 1.0) {
                double ricochetAngle = computeCollision();
                if (ricochetAngle >= - 1 && ricochetAngle <= 1) {
                    if (delay > 11) delay -= 10;
                    // trust me, I've done the math
                    xFactor = ricochetAngle;
                    yFactor = -(1 - (1 - paletteWidth) * ricochetAngle);
                    activity.updateScore(++score);
                } else {
                    lives -= 1;
                    lose = (lives == 0);
                    if (!lose) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //TODO use a string or remove the toast
                                Toast.makeText(activity.getApplication(), "Hai perso una vita!!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                        try {
                            //TODO use a parametrized number
                            Thread.sleep(1500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        resetGame();
                    }
                }
            }
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
        this.palettePosition = (1 - paletteWidth) * palettePosition + paletteWidth / 2;
    }

    /**
     * Computes a collision starting from the ball and the palette position.
     * @return the percentage of mid-palette that the ball is distant from the center of the palette
     */
    private double computeCollision() {
        // TODO: Fix bug when ball is near to the right edge: collision not detected
        return 2 * (x - palettePosition) / paletteWidth;
    }

    public void setPaletteWidth(double paletteWidth) {
        this.paletteWidth = paletteWidth;
    }

    public abstract void ballOnTopOfTheField();

    public abstract void initialBallPosition();

    public void resetGame(){}
}

