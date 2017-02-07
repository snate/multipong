package com.multipong.model;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public abstract class AbsGame implements Game {
    AbsGameThread currentGame = null;

    @Override
    public void setPaletteWidth(double width) {
        currentGame.setPaletteWidth(width);
    }

    @Override
    public void providePalettePosition(double position) {
        currentGame.setPalettePosition(position);
    }
}
