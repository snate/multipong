package com.multipong.model.coordination;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.multipong.activity.GameActivity;
import com.multipong.activity.MainActivity;
import com.multipong.model.formation.Host;
import com.multipong.model.game.MultiplayerGame;
import com.multipong.model.multiplayer.MultiplayerStateManager;
import com.multipong.model.multiplayer.MultiplayerStateManager.Player;
import com.multipong.net.messages.game.AreYouAliveMessage;
import com.multipong.net.send.AckUDPSender;
import com.multipong.utility.PlayerNameUtility;

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
        new AsyncTask<Void, Void, Void>() {

            private boolean pong;
            private int attempts;

            @Override
            protected void onPreExecute() {
                pong = false;
                attempts = getAttempts();
            }

            @Override
            protected Void doInBackground(Void... params) {
                while (!pong && attempts < getAttempts()) {
                    savePlayers(msm.getActivePlayers());
                    AreYouAliveMessage ayaMessage = new AreYouAliveMessage();
                    InetAddress address = getPinged();
                    AckUDPSender.ReliablyDeliverableAddressedContent rdac =
                            new AckUDPSender.ReliablyDeliverableAddressedContent(ayaMessage, address);
                    networkingActivity.addMessageToQueue(rdac);
                    synchronized (rdac) {
                        try {
                            rdac.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    pong = rdac.getB();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                if (!pong)
                    pingedIsNotAlive();
            }

        }.execute(null, null, null);
    }

    private void savePlayers(Collection<Integer> activePlayers) {
        for (Integer id : activePlayers)
            oldPlayers.add(new Player(id));
    }

    protected abstract int getAttempts();

    protected abstract InetAddress getPinged();

    protected abstract void pingedIsNotAlive();
}
