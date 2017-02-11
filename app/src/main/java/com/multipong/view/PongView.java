package com.multipong.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.multipong.R;
import com.multipong.activity.GameActivity;
import com.multipong.activity.MainActivity;

import java.util.concurrent.atomic.AtomicInteger;

public class PongView extends SurfaceView implements SurfaceHolder.Callback {

    private int playerColor = ContextCompat.getColor(getContext(), R.color.player_color);
    private int backgroundColor = ContextCompat.getColor(getContext(), R.color.background_color);
    private int ballColor = ContextCompat.getColor(getContext(), R.color.ball_color);

    private Paint paint = new Paint();
    private Paint borderColor = new Paint();
    private int borderSize = getContext().getResources().getInteger(R.integer.border_size);

    private double paletteWidthPerc = getContext().getResources().getInteger(R.integer.palette_width); // in %
    private int paletteHeight = getContext().getResources().getInteger(R.integer.palette_height);
    private int paletteH = 0;

    private AtomicInteger ballX = new AtomicInteger(50);
    private AtomicInteger ballY = new AtomicInteger(50);
    private int ballSize = getContext().getResources().getInteger(R.integer.ball_size);

    private Object canvasLock = new Object();
    private Object ball = new Object();

    public PongView(Context context) {
        super(context);
    }

    public PongView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    public PongView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setPaletteWidth(double percentage) {
        paletteWidthPerc = percentage;
    }

    private int getPaletteWidth() {
        return (int) (paletteWidthPerc * right());
    }

    public void movePalette(double relativePosition) {
        setPaletteH(relativePosition);
        doDraw();
    }

    private void setPaletteH(double relativePosition) {
        int pWidth = getPaletteWidth();
        int maxLeft = getLeft() + borderSize + pWidth/2;
        int maxRight = getRight() - borderSize - pWidth/2;
        paletteH = (int) (maxLeft + (maxRight-maxLeft)*relativePosition);
        //paletteH = (int) (left() + (right() - pWidth - left()) * relativePosition) + borderSize;
    }

    public void moveBall(double relX, double relY) {
        setBallX(relX);
        setBallY(relY);
        doDraw();
    }

    private void setBallX(double relX) {
        if (relX < 0.0) relX = 0.0;
        if (relX > 1.0) relX = 1.0;
        int maxLeft = getLeft()+borderSize;
        int maxRight = getRight() - ballSize - borderSize;
        ballX.set( (int) (maxLeft + (maxRight-maxLeft)*relX) );

        //ballX.set((int) (left() + (right() - ballSize - left()) * relX) + borderSize);
        //Log.d(""+relX,""+((int) (left() + (right() - ballSize - left()) * relX) + borderSize));
    }

    private void setBallY(double relY) {
        // Da rivedere, mi aspetto che relY e relX siano compresi fra 0 e 1
        if (relY < 0.0) relY = 0.0;
        if (relY > 1.0) relY = 1.0;
        int maxTop = getTop() + borderSize;
        int maxBottom = getBottom() - ballSize - borderSize - 2*paletteHeight;
        ballY.set( (int) (maxTop + (maxBottom-maxTop)*relY) );
    }

    public void removeBall() {
        //ballX.getAndSet(-100);
        //ballY.getAndSet(-100);
        doDraw();
    }

    private void doDraw() {
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        synchronized (canvasLock) {
            while (canvas == null) {
                try {
                    canvasLock.wait();
                    canvas = holder.lockCanvas();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        canvas.drawRGB(255,255,255);
        drawFrame(canvas);
        drawPalette(canvas);
        drawBall(canvas);
        holder.unlockCanvasAndPost(canvas);
        synchronized (canvasLock) {
            canvasLock.notifyAll();
        }
    }

    private void drawFrame(Canvas canvas) {
        paint.setColor(backgroundColor);
        borderColor.setColor(playerColor);
        canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), borderColor);
        canvas.drawRect(getLeft()+borderSize,
                        getTop()+borderSize,
                        getRight()-borderSize,
                        getBottom()-borderSize, //R.dimen.pongview_space_at_bottom
                        paint);
    }

    private void drawPalette(Canvas canvas) {
        paint.setColor(playerColor);
        int pWidth = getPaletteWidth();
        int startH = paletteH - pWidth/2;
        int endH   = startH + pWidth;
        int startV = getBottom() - borderSize - 2*paletteHeight;
        int endV   = startV + paletteHeight;
        canvas.drawRect(new Rect(startH, startV, endH, endV), paint);
    }

    private void drawBall(Canvas canvas) {
        if (ballX == null || ballY == null) return;
        paint.setColor(ballColor);
        canvas.drawCircle(ballX.get()+ballSize/2, ballY.get()+ballSize/2, ballSize/2, paint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        movePalette(0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    private int left()         { return getLeft(); }
    private int right()        { return getWidth(); }
    private int scrollTop ()   { return getTop()+(getBottom()-getTop())/*4/5*/; }
    private int scrollBottom() { return getBottom(); }
}
