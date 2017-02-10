package com.multipong.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.multipong.R;
import com.multipong.model.formation.Participant;
import com.multipong.net.WifiP2pListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class MultiplayerGameJoinActivity extends MultiplayerGameFormationActivity {

    private ListView matchesList;
    private MatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        adapter = new MatchAdapter(this);
        matchesList = (ListView)findViewById(R.id.matches_list);
        matchesList.setAdapter(adapter);

        setActor(new Participant(this));

        WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        Channel channel = manager.initialize(this, getMainLooper(), null);
        new WifiP2pListener(manager, channel, this);
    }

    public void receiveList(int hostID, String hostName, List<String> participants) {
        adapter.addMatch(hostID, hostName, participants);
    }

    @Override
    public boolean isHost() {
        return false;
    }

    private class MatchAdapter extends BaseAdapter {
        private class Match {
            Integer hostId;
            String host;
            List<String> participants;
        }
        private LinkedHashMap<Integer, Match> matches;
        private Activity activity;

        public MatchAdapter(Activity activity){
            this.activity = activity;
            matches = new LinkedHashMap<>();

            //TODO remove -> only debug purpose------------//
            /**/ArrayList<String> names = new ArrayList<>(); //
            /**/names.add("Minnie");
            /**/names.add("Pippo");
            /**/names.add("Pluto");
            /**/names.add("Topolino");
            /**/for (int i = 0; i < 10; i++) {
            /**/    Match m = new Match();
            /**/    m.host = "host" + i;
            /**/    m.participants = names;
            /**/    matches.put(i, m);
            /**/}
            //-----------------------------------------------//
        }

        public void addMatch(int hostID, String hostName, List<String> participants) {
            Match newMatch = new Match();
            newMatch.hostId = hostID;
            newMatch.host = hostName;
            newMatch.participants = participants;
            matches.put(hostID, newMatch);

            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return matches.size();
        }

        @Override
        public Object getItem(int position) {
            return matches.get((matches.keySet().toArray())[position]);
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
            final Match match = (Match) getItem(position);
            final String matchName = match.host;
            final List<String> participants = ((Match)getItem(position)).participants;

            final TextView matchNameTextView = (TextView) convertView.findViewById(R.id.match_name_text);
            final Button joinMatchButton = (Button) convertView.findViewById(R.id.join_match);
            Button participantsButton = (Button) convertView.findViewById(R.id.match_participants);

            matchNameTextView.setText(matchName);
            joinMatchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Participant participant = (Participant) getActor();
                    participant.join(match.hostId);
                }
            });

            participantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO change -> only debug purpose
                    Intent intent = new Intent(getApplicationContext(),
                            MultiplayerGameParticipantsActivity.class);
                    ArrayList<String> extra = new ArrayList<>(participants.size());
                    extra.addAll(participants);
                    intent.putStringArrayListExtra("participants", extra);
                    intent.putExtra("hostName", matchName);
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
}
