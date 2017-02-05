package com.multipong.model;

public interface Game {
    void start(String playerName);
    void setPaletteWidth(double width);
    void providePalettePosition(double position);
}
