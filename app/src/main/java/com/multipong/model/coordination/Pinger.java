package com.multipong.model.coordination;

import com.multipong.activity.GameActivity;
import com.multipong.model.game.MultiplayerGame;
import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.net.messages.game.AreYouAliveMessage;
import com.multipong.net.send.AckUDPSender;

import java.net.InetAddress;
import java.util.TimerTask;

public abstract class Pinger extends TimerTask {

    private GameActivity networkingActivity;
    protected MultiplayerStateManager msm;

    Pinger(GameActivity activity) {
        networkingActivity = activity;
    }

    @Override
    public void run() {
        int attempts = getAttempts();
        boolean pong = false;
        while (!pong && attempts < getAttempts()) {
            MultiplayerGame multiplayerGame = (MultiplayerGame) networkingActivity.getGame();
            msm = multiplayerGame.getMSM();
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

    protected abstract int getAttempts();

    protected abstract InetAddress getPinged();

    protected abstract void pingedIsNotAlive();
}
