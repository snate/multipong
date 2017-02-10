package com.multipong.activity;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.multipong.R;
import com.multipong.model.Actor;
import com.multipong.model.formation.Host;
import com.multipong.net.Receiver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MultiplayerGameHostActivity extends MultiplayerGameFormationActivity {

    private Receiver receiver;
    private Actor mActor;

    private ListView playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        playerList = (ListView) findViewById(R.id.player_list);
        mActor = new Host(this);
        receiver = new Receiver(this);
        new Thread(receiver).start();
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
    }

    @Override
    public boolean isHost() {
        return true;
    }

    public Actor getActor() {
        return mActor;
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