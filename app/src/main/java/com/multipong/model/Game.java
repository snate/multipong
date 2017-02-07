package com.multipong.model;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public abstract class Game {
    protected AbsGameThread currentGame = null;
    private int lives = 1;

    public abstract void start(String playerName);

    public void setNumberOfLives(int lives) { this.lives = lives; }

    public int getNumberOfLives(){ return lives; }

    public void setPaletteWidth(double width) {
        currentGame.setPaletteWidth(width);
    }

    public void providePalettePosition(double position) {
        currentGame.setPalettePosition(position);
    }
}
