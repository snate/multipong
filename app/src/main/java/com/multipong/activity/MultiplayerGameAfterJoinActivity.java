package com.multipong.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.multipong.R;
import com.multipong.model.formation.Participant;

import java.util.Collection;
import java.util.List;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class MultiplayerGameAfterJoinActivity extends MultiplayerGameFormationActivity implements ParticipantActivity {
    private ListView playerList;
    private PlayerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);

        Intent intent = getIntent();
        List<String> participants = intent.getStringArrayListExtra("participants");
        String host = intent.getStringExtra("hostName");

        TextView text = (TextView)findViewById(R.id.host_name_text);
        CharSequence cs = new StringBuffer().append(getString(R.string.list_of_players_of))
                .append(" ").append(host).toString();
        text.setText(cs);

        playerList = (ListView) findViewById(R.id.player_list);
        mAdapter = new PlayerAdapter(this, participants);
        playerList.setAdapter(mAdapter);

        setActor(new Participant(this));
    }

    @Override
    public boolean isHost() {
        return false;
    }

    @Override
    public void receiveList(int hostID, String hostName, List<String> participants) {
        mAdapter.players = participants;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
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
}
