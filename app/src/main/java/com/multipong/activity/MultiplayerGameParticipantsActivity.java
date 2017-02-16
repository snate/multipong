package com.multipong.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.multipong.R;

import java.util.List;

/**
 * @author Marco Zanella
 * @version 0.01
 * @since 0.01
 */
public class MultiplayerGameParticipantsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);

        Intent intent = getIntent();
        List<String> participants = intent.getStringArrayListExtra("participants");
        String host = intent.getStringExtra("hostName");

        TextView text = (TextView)findViewById(R.id.host_name_text);
        CharSequence cs = new StringBuffer().append(getString(R.string.list_of_players_of))
                .append(" ").append(host).toString();
        text.setText(cs);

        ListView list = (ListView)findViewById(R.id.participants_list);
        list.setAdapter(new ParticipantsAdapter(this, participants));
    }

    private class ParticipantsAdapter extends BaseAdapter {

        private List<String> participants;
        private Activity activity;

        public ParticipantsAdapter(Activity activity, List<String> participants) {
            this.participants = participants;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return participants.size();
        }

        @Override
        public Object getItem(int position) {
            return participants.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(activity)
                        .inflate(R.layout.row_participant_item, null);
            }
            final String participantName = (String) getItem(position);
            TextView participantNameTextView =
                    (TextView) convertView.findViewById(R.id.participant_name_text);
            participantNameTextView.setText(participantName);
            return convertView;
        }
    }
}
