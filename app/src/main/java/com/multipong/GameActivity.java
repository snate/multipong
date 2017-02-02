package com.multipong;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.multipong.view.MySurfaceView;

public class GameActivity extends AppCompatActivity {

    private MySurfaceView mSurfaceView;
    private RelativeLayout mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Remove notification bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game);

        mSurfaceView = (MySurfaceView) findViewById(R.id.game_surface);
        mLayout = (RelativeLayout) findViewById(R.id.activity_game);
    }

    @Override
    public void onBackPressed() { }
}
