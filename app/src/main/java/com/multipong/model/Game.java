package com.multipong.model;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public abstract class Game {
    AbsGameThread currentGame = null;

    public abstract void start(String playerName);

    public void setPaletteWidth(double width) {
        currentGame.setPaletteWidth(width);
    }

    public void providePalettePosition(double position) {
        currentGame.setPalettePosition(position);
    }
}
