package com.multipong.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.multipong.model.Actor;
import com.multipong.model.formation.Host;
import com.multipong.utility.DeviceIdUtility;
import com.multipong.utility.PlayerNameUtility;

import java.util.ArrayList;
import java.util.Collection;

public class MultiplayerGameHostActivity extends MultiplayerGameFormationActivity {

    private Button mButton;

    private ListView playerList;
    private PlayerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        Log.d("CREATED", "HOST ACTIVITY");

        ArrayList<String> listWithOnlyme = new ArrayList<>();
        listWithOnlyme.add(PlayerNameUtility.getPlayerName());
        playerList = (ListView) findViewById(R.id.player_list);
        mAdapter = new PlayerAdapter(this, listWithOnlyme);
        playerList.setAdapter(mAdapter);
        mButton = (Button) findViewById(R.id.host_start_btn);

        Log.d("CREATING HOST", "ciao");
        Actor host = new Host(this);
        Log.d("CREATING HOST", String.valueOf(host));
        setActor(host);
        Log.d("CREATING HOST", "PIPPO");
        mButton.setOnClickListener(new HostGameStarter(this, playerList));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void receiveList(Collection<String> players) {
        mAdapter.players = players;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
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

    public static final String IS_HOST = "is_Host";
    public static final String PLAYERS = "player_list";

    private class HostGameStarter implements View.OnClickListener {

        private ListView mPlayerList;
        public static final String PLAYER_NAME = "com.multipong.PLAYER_NAME";
        private Activity activity;

        public HostGameStarter(Activity activity, ListView playerList) {
            this.activity = activity;
            mPlayerList = playerList;
        }

        @Override
        public void onClick(View v) {
            ListAdapter listAdapter = mPlayerList.getAdapter();
            if (mPlayerList.getAdapter() == null || listAdapter.getCount() <= 1) {
                showShortToast(getString(R.string.two_player_at_least));
                return;
            }
            Host host = (Host) getActor();
            host.startGame();

            final AlertDialog dialog = new AlertDialog.Builder(MultiplayerGameHostActivity.this)
                    .setTitle(R.string.match_creation)
                    .setCancelable(false)
                    .create();
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected void onPreExecute() {
                    dialog.show();
                }

                @Override
                protected Void doInBackground(Void... params) {
                    waitForEmptyMessageQueue();
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    dialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), GameActivity.class)
                            .putExtra(PLAYER_NAME, PlayerNameUtility.getPlayerName())
                            .putExtra(MainActivity.IS_MULTI, true)
                            .putExtra(IS_HOST, true)
                            .putExtra(GameActivity.HOST, DeviceIdUtility.getId())
                            .putIntegerArrayListExtra(PLAYERS, ((Host) getActor()).getPlayerIDs());
                    startActivity(intent);
                    activity.finish();
                }

            }.execute(null, null, null);
        }
    }
}