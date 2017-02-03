package com.multipong;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.multipong.model.Game;
import com.multipong.model.SingleGame;
import com.multipong.view.PongView;

public class GameActivity extends AppCompatActivity {

    private PongView mSurfaceView;
    private SeekBar mBar;
    private TextView mScore;
    private RelativeLayout mLayout;
    private CharSequence playerName;
    private Game game;


    private final double PALETTE_WIDTH = 0.2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        playerName = intent.getCharSequenceExtra(MainActivity.PLAYER_NAME);

        mSurfaceView = (PongView) findViewById(R.id.game_surface);
        mLayout = (RelativeLayout) findViewById(R.id.activity_game);
        mBar = (SeekBar) findViewById(R.id.paletteScroll);
        mScore = (TextView) findViewById(R.id.score_tv);

        mBar.setOnSeekBarChangeListener(new PaletteListener());
        mSurfaceView.setPaletteWidth(PALETTE_WIDTH);
        mScore.setText("0");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: parameterize single game or multiplayer game choice
        if(game == null) {
            game = new SingleGame(this);
            game.start(playerName.toString());
            game.setPaletteWidth(PALETTE_WIDTH);
        }
    }

    @Override
    public void onBackPressed() { }

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

    public void endGame() {
        mSurfaceView.removeBall();
        // TODO: Add code to end game
    }

    private class PaletteListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int max = seekBar.getMax();
            double percentProgress = (double) (seekBar.getProgress() - 1) / (double) max;
            game.providePalettePosition(percentProgress);
            mSurfaceView.movePalette(percentProgress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) { }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) { }
    }
}
