package com.multipong.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.multipong.R;
import com.multipong.model.Actor;
import com.multipong.model.formation.Participant;
import com.multipong.model.game.Game;
import com.multipong.model.game.MultiplayerGame;
import com.multipong.model.game.SingleGame;
import com.multipong.model.multiplayer.GameRouter;
import com.multipong.net.receive.AckUDPReceiver;
import com.multipong.net.receive.Receiver;
import com.multipong.net.send.AckUDPSender;
import com.multipong.net.send.Sender;
import com.multipong.persistence.MultipongDatabase;
import com.multipong.persistence.pojos.Stats;
import com.multipong.persistence.read.StatsReader;
import com.multipong.persistence.save.StatsSaver;
import com.multipong.utility.PlayerNameUtility;
import com.multipong.view.PongView;

import java.util.List;

public class GameActivity extends NetworkingActivity {

    private PongView mSurfaceView;
    private SeekBar mBar;
    private TextView mScore;
    private RelativeLayout mLayout;
    private TextView mEndTextView;
    private Button mEndButton;

    private String playerName;
    private Boolean isMultiplayer;
    private List<Integer> playerIDs = null;
    private Boolean isHost;
    private volatile Game game;
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

        mSurfaceView = (PongView) findViewById(R.id.game_surface);
        mLayout = (RelativeLayout) findViewById(R.id.activity_game);
        //mBar = (SeekBar) findViewById(R.id.paletteScroll);
        mScore = (TextView) findViewById(R.id.score_tv);
        mEndTextView = (TextView) findViewById(R.id.end_tv);
        mEndButton = (Button) findViewById(R.id.end_bt);

        Intent intent = getIntent();
        isMultiplayer = intent.getBooleanExtra(MainActivity.IS_MULTI, false);
        isHost = intent.getBooleanExtra(MultiplayerGameHostActivity.IS_HOST, false);
        if (isMultiplayer) {
            playerIDs = intent.getIntegerArrayListExtra(Participant.PLAYERS);
            // TODO: Notify PongView that this game is a multiplayer game
        }
        playerName = PlayerNameUtility.getPlayerName();

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
                return true;
            }
        });

        mSurfaceView.setPaletteWidth(PALETTE_WIDTH);
        mScore.setText("0");
        MultipongDatabase database = new MultipongDatabase(this);
        if (!isMultiplayer) {
            saver = new StatsSaver(database);
            Stats best = new StatsReader(database).getBestScoreFor(Stats.Modality.SINGLE_PLAYER);
            if (best == null)
                showShortToast("No best score available");
            else
                showShortToast("BEST: " + best.getName() + " with score " + best.getScore());
        }
        mEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getActor().receive(Actor.MessageType.POISON_PILL, null, null);
    }

    public Game getGame() {
        return game;
    }

    @Override
    protected Sender getSender() {
        return new AckUDPSender();
    }

    @Override
    protected Receiver getReceiver() {
        return new AckUDPReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: parameterize single game or multiplayer game choice
        if(game == null) {
            if (isMultiplayer) {
                game = new MultiplayerGame(this);
                ((MultiplayerGame)game).setStartingPlayer(isHost);
                ((MultiplayerGame)game).setAllPlayers(playerIDs);
                setActor(new GameRouter(this));
            }
            else
                game = new SingleGame(this);
            game.start(playerName);
            game.setPaletteWidth(PALETTE_WIDTH);
        }
    }

    @Override
    public void onBackPressed() {
        if(gameEnded){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
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

    public void endGame(final int score, final boolean win) {
        makeBallDisappear();
        gameEnded = true;
        if (!win) {
            if (!isMultiplayer) {
                Stats stats = new Stats().withModality(Stats.Modality.SINGLE_PLAYER)
                        .withName(playerName)
                        .withScore(score);
                saver.save(stats);
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showShortToast("DONE");
                    if (isMultiplayer) {
                        ImageView image = (ImageView) findViewById(R.id.image_end_game);
                        image.setBackgroundResource(R.drawable.lose_game);
                        image.setVisibility(View.VISIBLE);
                    }
                    mEndTextView.setVisibility(View.VISIBLE);
                    mEndTextView.setText(new StringBuffer()
                            .append(playerName)
                            .append(", ")
                            .append(getString(R.string.your_score_is))
                            .append(" ")
                            .append(score).toString());
                    mEndButton.setVisibility(View.VISIBLE);
                    mEndButton.setClickable(true);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView image = (ImageView) findViewById(R.id.image_end_game);
                    image.setBackgroundResource(R.drawable.win_image);
                    image.setVisibility(View.VISIBLE);
                    showShortToast("DONE");
                    mEndTextView.setVisibility(View.VISIBLE);
                    mEndTextView.setText(new StringBuffer()
                            .append(playerName)
                            .append(", ")
                            .append(getString(R.string.you_win))
                            .append("\n")
                            .append(getString(R.string.your_score_is))
                            .append(" ")
                            .append(score).toString());
                    mEndButton.setVisibility(View.VISIBLE);
                    mEndButton.setClickable(true);
                }
            });
        }
        // TODO: Add code to end game
    }

    private void showShortToast(String toastText) {
        Context context = getApplicationContext();
        CharSequence text = toastText;
        int duration = Toast.LENGTH_SHORT;
        Toast.makeText(context, text, duration).show();
    }
}
