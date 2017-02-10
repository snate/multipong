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
        setContentView(R.layout.activity_partecipants);

        Intent intent = getIntent();
        List<String> partecipants = intent.getStringArrayListExtra("participants");
        String host = intent.getStringExtra("hostName");

        TextView text = (TextView)findViewById(R.id.host_name_text);
        CharSequence cs = new StringBuffer().append(getString(R.string.list_of_players_of))
                .append(host).toString();
        text.setText(cs);

        ListView list = (ListView)findViewById(R.id.partecipants_list);
        list.setAdapter(new PartecipantsAdapter(this, partecipants));
    }

    private class PartecipantsAdapter extends BaseAdapter {

        private List<String> partecipants;
        private Activity activity;

        public PartecipantsAdapter(Activity activity, List<String> partecipants) {
            this.partecipants = partecipants;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return partecipants.size();
        }

        @Override
        public Object getItem(int position) {
            return partecipants.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(activity)
                        .inflate(R.layout.row_partecipant_item, null);
            }
            final String partecipantName = (String) getItem(position);
            TextView partecipantNameTextView =
                    (TextView) convertView.findViewById(R.id.partecipant_name_text);
            partecipantNameTextView.setText(partecipantName);
            return convertView;
        }
    }
}
