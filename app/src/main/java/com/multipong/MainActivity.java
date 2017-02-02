package com.multipong;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

        final TextView mNameTextView = (TextView) findViewById(R.id.player_name_tv);
        final Button mPlayButton = (Button) findViewById(R.id.play_button);
        mNameTextView.addTextChangedListener(new InputTextWatcher(mPlayButton));

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getApplicationContext();
                CharSequence text = "Play button clicked!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
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

    private class InputTextWatcher implements TextWatcher {
        private Button mButton;

        InputTextWatcher(Button button) { mButton = button; }

        @Override
        public void beforeTextChanged(CharSequence s, int st, int c, int a) { }

        @Override
        public void onTextChanged(CharSequence s, int st, int bef, int c) { }

        @Override
        public void afterTextChanged(Editable s) {
            if(s.length() == 0)
                mButton.setClickable(false);
            else
                mButton.setClickable(true);
        }
    }
}
