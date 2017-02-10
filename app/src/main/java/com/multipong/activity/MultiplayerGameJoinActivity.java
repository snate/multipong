package com.multipong.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.multipong.R;
import com.multipong.model.Actor;
import com.multipong.model.formation.Participant;
import com.multipong.net.PeerExplorer;
import com.multipong.net.Receiver;
import com.multipong.net.Utils;
import com.multipong.net.WifiP2pListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class MultiplayerGameJoinActivity extends MultiplayerGameFormationActivity {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private PeerExplorer mExplorer;
    private WifiP2pListener mWifiP2pListener;
    private IntentFilter mIntentFilter;
    private Receiver receiver;

    private ListView matchesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mExplorer = new PeerExplorer(this);
        mWifiP2pListener = new WifiP2pListener(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        matchesList = (ListView) findViewById(R.id.matches_list);
        setActor(new Participant(this));
        receiver = new Receiver(this);
        new Thread(receiver).start();

        //TODO remove -> only debug purpose
        receiveList(new ArrayList<WifiP2pDevice>());
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

        //TODO remove -> only debug purpose
        for  (int i = 0; i < 100; i++)
            names.add(i + ". match");

        MatchAdapter adapter = new MatchAdapter(this, names);
        matchesList.setAdapter(adapter);


        Log.d("Game Formation", "Found " + list.size() + " devices");
        if(list.isEmpty()) return;
        Iterator<WifiP2pDevice> iterator = list.iterator();
        WifiP2pDevice device = iterator.next();
        Utils.connectTo(device, mManager, mChannel, this);
    }

    @Override
    public boolean isHost() {
        return false;
    }

    public void showShortToast(String toastText) {
        Context context = getApplicationContext();
        CharSequence text = toastText;
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, text, duration).show();
    }

    private class MatchAdapter extends BaseAdapter {
        private List<String> matches;
        private Activity activity;

        public MatchAdapter(Activity activity, List<String> matches){
            this.activity = activity;
            this.matches = matches;
        }

        @Override
        public int getCount() {
            return matches.size();
        }

        @Override
        public Object getItem(int position) {
            return matches.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(activity)
                        .inflate(R.layout.row_match_item, null);
            }
            final String playerName = (String) getItem(position);

            TextView matchNameTextView = (TextView) convertView.findViewById(R.id.match_name_text);
            Button joinMatch = (Button) convertView.findViewById(R.id.join_match);
            Button partecipants = (Button) convertView.findViewById(R.id.match_partecipants);

            matchNameTextView.setText(playerName);
            joinMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO change -> only debug purpose
                    Intent intent = new Intent(getApplicationContext(), DummyActivity.class);
                    startActivity(intent);
                }
            });

            partecipants.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO change -> only debug purpose
                    Intent intent = new Intent(getApplicationContext(), DummyActivity.class);
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
}
