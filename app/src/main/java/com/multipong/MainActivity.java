package com.multipong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
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
        transAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                Log.i("bounce", "Starting button dropdown animation");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Log.i("bounce",
                        "Ending button dropdown animation. Clearing animation and setting layout");
                bounceBallImage.clearAnimation();
                final int left = bounceBallImage.getLeft();
                final int top = bounceBallImage.getTop()+110;
                final int right = bounceBallImage.getRight();
                final int bottom = bounceBallImage.getBottom()+110;
                bounceBallImage.layout(left, top, right, bottom);

            }
        });
        bounceBallImage.startAnimation(transAnim);
    }
}
