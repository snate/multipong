package com.multipong.model.multiplayer;

import java.util.ArrayList;
import com.multipong.model.multiplayer.MultiplayerStateManager.Player;

public interface PlayerExtractor {
    Player getNext(ArrayList<Player> players, Player player);
}
