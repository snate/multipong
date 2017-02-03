package com.multipong.model;

import com.multipong.GameActivity;

public interface Game {
    void start(String playerName);
    void setPaletteWidth(double width);
    void providePalettePosition(double position);
}
