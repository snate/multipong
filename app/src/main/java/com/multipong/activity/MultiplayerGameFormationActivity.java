package com.multipong.activity;

import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.multipong.model.Actor;
import com.multipong.net.Utils;

import java.util.Collection;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public abstract class MultiplayerGameFormationActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        Utils.setActivity(this);
    }

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
