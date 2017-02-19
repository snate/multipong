package com.multipong.model.coordination;

import android.os.AsyncTask;
import android.util.Log;

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
    protected ArrayList<Player> oldPlayers = new ArrayList<>();

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
                attempts = 0;
            }

            @Override
            protected Void doInBackground(Void... params) {
                while (!pong && attempts < getAttempts()) {
                    Log.e("PONG-PINA", pong + " initial");
                    savePlayers(msm.getActivePlayers());
                    Log.e("PONG-PINA", 1 + "");
                    AreYouAliveMessage ayaMessage = new AreYouAliveMessage();
                    Log.e("PONG-PINA", 2 + "");
                    InetAddress address = getPinged();
                    Log.e("PONG-PINA", 3 + "");
                    AckUDPSender.ReliablyDeliverableAddressedContent rdac =
                            new AckUDPSender.ReliablyDeliverableAddressedContent(ayaMessage, address);
                    Log.e("PONG-PINA", 4 + "");
                    networkingActivity.addMessageToQueue(rdac);
                    Log.e("PONG-PINA", 5 + "");
                    synchronized (rdac) {
                        try {
                            Log.e("PONG-PINA", 6 + "");
                            //TODO seba I've added some time to wait -> before with only wait -> starvation. Correct?
                            rdac.wait(11000);
                            Log.e("PONG-PINA", 7 + "");
                        } catch (InterruptedException e) {
                            Log.e("PONG-PINA", "cathced exception");
                            e.printStackTrace();
                        } finally {
                            Log.e("PONG-PINA", 8 + "");
                            pong = rdac.getB();
                            Log.e("PONG-PINA", 9 + "");
                            attempts++;
                            Log.e("PONG-PINA", "pong:" + pong + " attempts:" + attempts);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
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
