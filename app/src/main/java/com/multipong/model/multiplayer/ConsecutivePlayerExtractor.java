package com.multipong.model.multiplayer;

import java.util.ArrayList;
import java.util.Iterator;

import com.multipong.model.multiplayer.MultiplayerStateManager.Player;

public class ConsecutivePlayerExtractor implements PlayerExtractor {
    @Override
    public Player getNext(ArrayList<Player> players, Player player) {
        Iterator<Player> playerIterator = players.iterator();
        boolean found = false;
        while (!found && playerIterator.hasNext())
            if ((playerIterator.next()).equals(player))
                found = true;
        Player next;
        if (playerIterator.hasNext())
            next = playerIterator.next();
        else
            next = players.get(0);
        return next;
    }
}
