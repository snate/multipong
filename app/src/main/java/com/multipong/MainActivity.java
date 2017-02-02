package com.multipong;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final TextView mCredits = (TextView) findViewById(R.id.credits_tv);
        final TextView mNameTextView = (TextView) findViewById(R.id.player_name_tv);
        final Button mPlayButton = (Button) findViewById(R.id.play_button);

        mCredits.setOnClickListener(new CreditsListener());
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNameTextView.getText().length() == 0) {
                    showShortToast("Please insert your name");
                    return;
                }
                showShortToast("Play button clicked!");
            }
        });

        bouncingBall();
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
}
