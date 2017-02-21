package com.multipong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.multipong.R;
import com.multipong.utility.PlayerNameUtility;

public class MainActivity extends AppCompatActivity {

    public static final String PLAYER_NAME = "com.multipong.PLAYER_NAME";
    public static final String IS_MULTI = "is_multiplayer_match";

    private TextView mCredits;
    private TextView mNameTextView;
    private FloatingActionButton singlePlay;
    private FloatingActionButton multiPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PlayerNameUtility.setContext(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCredits = (TextView) findViewById(R.id.credits_tv);
        mNameTextView = (TextView) findViewById(R.id.player_name_tv);

        singlePlay = (FloatingActionButton) findViewById(R.id.single_player_button);
        singlePlay.setOnClickListener(new SinglePlayerListener());

        multiPlay = (FloatingActionButton) findViewById(R.id.multi_player_button);
        multiPlay.setOnClickListener(new MultiPlayerListener());

        mCredits.setOnClickListener(new CreditsListener());


        bouncingBall();

        if (PlayerNameUtility.getPlayerName() != null)
            mNameTextView.setText(PlayerNameUtility.getPlayerName());
    }

    private void bouncingBall() {
        final ImageView bounceBallImage = (ImageView) findViewById(R.id.bouncing_ball);
        bounceBallImage.clearAnimation();
        TranslateAnimation transAnim = new TranslateAnimation(0, 0, 0, 110);
        transAnim.setStartOffset(500);
        transAnim.setDuration(4000);
        transAnim.setFillAfter(true);
        transAnim.setInterpolator(new BounceInterpolator());
        bounceBallImage.startAnimation(transAnim);
    }

    private void showShortToast(String toastText) {
        Context context = getApplicationContext();
        CharSequence text = toastText;
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, text, duration).show();
    }

    private class CreditsListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), CreditsActivity.class);
            startActivity(intent);
        }
    }

    private class SinglePlayerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CharSequence playerName = mNameTextView.getText();
            if(playerName.length() == 0) {
                showShortToast(getString(R.string.insert_your_name));
                return;
            }

            if (!PlayerNameUtility.getPlayerName().equals(playerName.toString()))
                PlayerNameUtility.setPlayerName(playerName.toString());
            Intent intent = new Intent(getApplicationContext(), GameActivity.class)
                    .putExtra(PLAYER_NAME, playerName)
                    .putExtra(IS_MULTI, false);
            startActivity(intent);
        }
    }

    private class MultiPlayerListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            CharSequence playerName = mNameTextView.getText();
            if(playerName.length() == 0) {
                showShortToast(getString(R.string.insert_your_name));
                return;
            }

            PlayerNameUtility.setPlayerName(playerName.toString());
            Intent intent = new Intent(getApplicationContext(), MultiplayerGameJoinOrHostActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch(keyCode)
        {
            case KeyEvent.KEYCODE_BACK:

                moveTaskToBack(true);

                return true;
        }
        return false;
    }
}
