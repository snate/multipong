package com.multipong.model.coordination;

import com.multipong.activity.GameActivity;
import com.multipong.model.game.MultiplayerGame;
import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.model.multiplayer.MultiplayerStateManager.Player;
import com.multipong.net.messages.game.AreYouAliveMessage;
import com.multipong.net.send.AckUDPSender;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TimerTask;

public abstract class Pinger extends TimerTask {

    private GameActivity networkingActivity;
    protected MultiplayerStateManager msm;
    protected ArrayList<Player> oldPlayers;

    Pinger(GameActivity activity) {
        networkingActivity = activity;
    }

    @Override
    public void run() {
        MultiplayerGame multiplayerGame = (MultiplayerGame) networkingActivity.getGame();
        msm = multiplayerGame.getMSM();
        int attempts = getAttempts();
        boolean pong = false;
        while (!pong && attempts < getAttempts()) {
            savePlayers(msm.getActivePlayers());
            AreYouAliveMessage ayaMessage = new AreYouAliveMessage();
            InetAddress address = getPinged();
            AckUDPSender.ReliablyDeliverableAddressedContent rdac =
                    new AckUDPSender.ReliablyDeliverableAddressedContent(ayaMessage, address);
            Boolean messagehasBeenSent = rdac.getB();
            networkingActivity.addMessageToQueue(rdac);
            synchronized (messagehasBeenSent) {
                try {
                    messagehasBeenSent.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            pong = messagehasBeenSent;
        }
        if (!pong)
            pingedIsNotAlive();
    }

    private void savePlayers(Collection<Integer> activePlayers) {
        for (Integer id : activePlayers)
            oldPlayers.add(new Player(id));
    }

    protected abstract int getAttempts();

    protected abstract InetAddress getPinged();

    protected abstract void pingedIsNotAlive();
}
