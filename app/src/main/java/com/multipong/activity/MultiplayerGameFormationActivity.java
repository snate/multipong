package com.multipong.activity;

import android.net.wifi.p2p.WifiP2pDevice;
import android.support.v7.app.AppCompatActivity;

import com.multipong.model.Actor;

import java.util.Collection;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public abstract class MultiplayerGameFormationActivity extends AppCompatActivity{

    private Actor actor;

    public void setActor(Actor actor) {
        this.actor = actor;
    }

    public Actor getActor() {
        return actor;
    }

    public abstract void receiveList(Collection<WifiP2pDevice> list);

    public abstract boolean isHost();
}
