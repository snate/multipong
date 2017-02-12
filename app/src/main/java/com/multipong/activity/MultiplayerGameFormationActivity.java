package com.multipong.activity;

import android.os.Bundle;

import com.multipong.model.Actor;
import com.multipong.net.Utils;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public abstract class MultiplayerGameFormationActivity extends NetworkingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.setActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public abstract boolean isHost();
}
