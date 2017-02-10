package com.multipong.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.multipong.R;
import com.multipong.model.Actor;
import com.multipong.net.Receiver;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class MultiplayerGameJoinActivity extends MultiplayerGameFormationActivity {

    private Receiver receiver;
    private ListView matchesList;
    private MatchAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        receiver = new Receiver(this);
        new Thread(receiver).start();

        //TODO remove -> only debug purpose
        receiveList(0,"",new ArrayList<String>());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(receiver != null) receiver.stop();
    }

    public void receiveList(int hostID, String hostName, List<String> partecipants) {
        adapter.addMatch(hostID, hostName, partecipants);
    }

    @Override
    public boolean isHost() {
        return false;
    }

    @Override
    public Actor getActor() {
        return null;
    }

    private class MatchAdapter extends BaseAdapter {
        private class Match {
            String host;
            List<String> partecipants;
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
            /**/    m.partecipants = names;
            /**/    matches.put(i, m);
            /**/}
            //-----------------------------------------------//
        }

        public void addMatch(int hostID, String hostName, List<String> partecipants) {
            Match newMatch = new Match();
            newMatch.host = hostName;
            newMatch.partecipants = partecipants;
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
            final String matchName = ((Match)getItem(position)).host;
            final List<String> partecipants = ((Match)getItem(position)).partecipants;

            TextView matchNameTextView = (TextView) convertView.findViewById(R.id.match_name_text);
            Button joinMatchButton = (Button) convertView.findViewById(R.id.join_match);
            Button partecipantsButton = (Button) convertView.findViewById(R.id.match_partecipants);

            matchNameTextView.setText(matchName);
            joinMatchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO change -> only debug purpose
                    Intent intent = new Intent(getApplicationContext(), DummyActivity.class);
                    startActivity(intent);
                }
            });

            partecipantsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO change -> only debug purpose
                    Intent intent = new Intent(getApplicationContext(), DummyActivity.class);
                    ArrayList<String> extra = new ArrayList<String>(partecipants.size());
                    extra.addAll(partecipants);
                    intent.putStringArrayListExtra("partecipants", extra);
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
}
