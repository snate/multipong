package com.multipong.activity;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.multipong.R;
import com.multipong.model.Actor;
import com.multipong.model.formation.Host;
import com.multipong.net.PeerExplorer;
import com.multipong.net.Receiver;
import com.multipong.net.Utils;
import com.multipong.net.WifiP2pListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MultiplayerGameHostActivity extends MultiplayerGameFormationActivity {

    private WifiP2pManager mManager;
    private Channel mChannel;
    private PeerExplorer mExplorer;
    private WifiP2pListener mWifiP2pListener;
    private IntentFilter mIntentFilter;
    private Receiver receiver;
    private Actor mActor;

    private ListView playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game_formation);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mExplorer = new PeerExplorer(this);
        mWifiP2pListener = new WifiP2pListener(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        playerList = (ListView) findViewById(R.id.player_list);
        mActor = new Host(this);
        receiver = new Receiver(this);
        new Thread(receiver).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mWifiP2pListener, mIntentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mWifiP2pListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) receiver.stop();
    }

    public void receiveList(Collection<WifiP2pDevice> list) {
        ArrayList<String> names = new ArrayList<>();

        for(WifiP2pDevice dev:list)
            names.add(dev.deviceName);
        // TODO: Add content to adapter only when further info are available
        PlayerAdapter adapter = new PlayerAdapter(this, names);
        playerList.setAdapter(adapter);


        Log.d("Game Formation", "Found " + list.size() + " devices");
        if(list.isEmpty()) return;
        Iterator<WifiP2pDevice> iterator = list.iterator();
        WifiP2pDevice device = iterator.next();
        Utils.connectTo(device, mManager, mChannel, this);
    }

    @Override
    public boolean isHost() {
        return true;
    }

    public Actor getActor() {
        return mActor;
    }

    public void showShortToast(String toastText) {
        Context context = getApplicationContext();
        CharSequence text = toastText;
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, text, duration).show();
    }

    private class PlayerAdapter extends BaseAdapter {
        private List<String> players;
        private Activity activity;

        public PlayerAdapter(Activity activity, List<String> players){
            this.activity = activity;
            this.players = players;
        }

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int position) {
            return players.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(activity)
                        .inflate(R.layout.row_player_item, null);
            }
            final String playerName = (String) getItem(position);
            TextView playerNameTextView = (TextView) convertView.findViewById(R.id.player_name_text);
            playerNameTextView.setText(playerName);
            return convertView;
        }
    }
}