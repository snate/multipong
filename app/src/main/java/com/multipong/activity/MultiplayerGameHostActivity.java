package com.multipong.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.multipong.R;
import com.multipong.model.formation.Host;
import com.multipong.net.Receiver;

import java.util.Collection;

public class MultiplayerGameHostActivity extends MultiplayerGameFormationActivity {

    private Receiver receiver;
    private Button mButton;

    private ListView playerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        playerList = (ListView) findViewById(R.id.player_list);
        mButton = (Button) findViewById(R.id.host_start_btn);

        setActor(new Host(this));
        receiver = new Receiver(this);
        new Thread(receiver).start();
        mButton.setOnClickListener(new HostGameStarter(playerList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) receiver.stop();
    }

    public void receiveList(Collection<String> players) {
        PlayerAdapter adapter = new PlayerAdapter(this, players);
        playerList.setAdapter(adapter);
        Log.d("HostActivity", "There are " + players.size() + " in this game");
    }

    private void showShortToast(String toastText) {
        Context context = getApplicationContext();
        CharSequence text = toastText;
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, text, duration).show();
    }

    @Override
    public boolean isHost() {
        return true;
    }

    private class PlayerAdapter extends BaseAdapter {
        private Collection<String> players;
        private Activity activity;

        public PlayerAdapter(Activity activity, Collection<String> players){
            this.activity = activity;
            this.players = players;
        }

        @Override
        public int getCount() {
            return players.size();
        }

        @Override
        public Object getItem(int position) {
            return players.toArray()[position];
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

    private class HostGameStarter implements View.OnClickListener {

        private ListView mPlayerList;

        public HostGameStarter(ListView playerList) {
            mPlayerList = playerList;
        }

        @Override
        public void onClick(View v) {
            ListAdapter listAdapter = mPlayerList.getAdapter();
            int howMany = listAdapter.getCount();
            if (howMany <= 1) {
                showShortToast("There should be at least two players in the game");
                return;
            }
            Host host = (Host) getActor();
            host.startGame();
        }
    }
}