package com.multipong.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import com.multipong.R;
import com.multipong.model.game.Game;
import com.multipong.model.game.SingleGame;
import com.multipong.persistence.MultipongDatabase;
import com.multipong.persistence.pojos.Stats;
import com.multipong.persistence.read.StatsReader;
import com.multipong.persistence.save.StatsSaver;
import com.multipong.utility.PlayerNameUtility;
import com.multipong.view.PongView;

public class GameActivity extends AppCompatActivity {

    private PongView mSurfaceView;
    private SeekBar mBar;
    private TextView mScore;
    private RelativeLayout mLayout;
    private TextView mEndTextView;
    private Button mEndButton;

    private String playerName;
    private Game game;
    private StatsSaver saver;
    private volatile boolean gameEnded = false;

    private final double PALETTE_WIDTH = 0.2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        /*Intent intent = getIntent();
        playerName = intent.getCharSequenceExtra(MainActivity.PLAYER_NAME).toString();*/
        playerName = PlayerNameUtility.getPlayerName();

        mSurfaceView = (PongView) findViewById(R.id.game_surface);
        mLayout = (RelativeLayout) findViewById(R.id.activity_game);
        //mBar = (SeekBar) findViewById(R.id.paletteScroll);
        mScore = (TextView) findViewById(R.id.score_tv);
        mEndTextView = (TextView) findViewById(R.id.end_tv);
        mEndButton = (Button) findViewById(R.id.end_bt);

        mLayout.setOnTouchListener(new View.OnTouchListener() {
            int max, touchLimLeft,touchLimRight;
            double progress, relProgress,percentProgress;
            int offset = getResources().getInteger(R.integer.touch_offset);
            double touchOffset = offset*0.01;
            public boolean onTouch(View v, MotionEvent event) {
                max = mLayout.getRight(); // range: 0 to max (0=mSurfaceView.getLeft(), max=mSurfaceView.getRight()
                touchLimLeft = (int) (max*touchOffset);
                touchLimRight = max - touchLimLeft;
                progress = event.getX();
                if (progress < touchLimLeft) progress = touchLimLeft;
                if (progress > touchLimRight) progress = touchLimRight;
                relProgress = progress - touchLimLeft;
                percentProgress = (relProgress) / (touchLimRight-touchLimLeft);
                game.providePalettePosition(percentProgress);
                mSurfaceView.movePalette(percentProgress);
                Log.d("offsetR________",""+offset);
                Log.d("offsetD________",""+touchOffset);
                Log.d("range__________",""+touchLimLeft+"-"+touchLimRight);
                Log.d("getX()_________",""+progress);
                Log.d("relProgress____",""+relProgress);
                Log.d("percentProgress",""+percentProgress);
                return true;
            }
        });

        mSurfaceView.setPaletteWidth(PALETTE_WIDTH);
        mScore.setText("0");
        MultipongDatabase database = new MultipongDatabase(this);
        saver = new StatsSaver(database);
        Stats best = new StatsReader(database).getBestScoreFor(Stats.Modality.SINGLE_PLAYER);
        if(best == null)
            showShortToast("No best score available");
        else
            showShortToast("BEST: " + best.getName() + " with score " + best.getScore());
        mEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: parameterize single game or multiplayer game choice
        if(game == null) {
            game = new SingleGame(this);
            game.start(playerName);
            game.setPaletteWidth(PALETTE_WIDTH);
        }
    }

    @Override
    public void onBackPressed() {
        if(gameEnded)
            super.onBackPressed();
    }

    public void showPlayerName(String name) {
        TextView mDebugTextView = (TextView) findViewById(R.id.debug_tv);
        mDebugTextView.setText("Welcome " + name);
    }

    public void moveBall(double relX, double relY) {
        mSurfaceView.moveBall(relX, relY);
    }

    public void updateScore(final int score) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mScore.setText(String.valueOf(score));
            }
        });
    }

    public void makeBallDisappear() {
        mSurfaceView.removeBall();
    }

    public void endGame(final int score) {
        makeBallDisappear();
        Stats stats = new Stats().withModality(Stats.Modality.SINGLE_PLAYER)
                                 .withName(playerName)
                                 .withScore(score);
        saver.save(stats);
        gameEnded = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showShortToast("DONE");
                mEndTextView.setVisibility(View.VISIBLE);
                mEndTextView.setText(playerName + ", your score is " + score);
                mEndButton.setVisibility(View.VISIBLE);
                mEndButton.setClickable(true);
            }
        });
        // TODO: Add code to end game
    }

    private void showShortToast(String toastText) {
        Context context = getApplicationContext();
        CharSequence text = toastText;
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, text, duration).show();
    }
}
