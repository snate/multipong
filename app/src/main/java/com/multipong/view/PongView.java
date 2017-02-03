package com.multipong.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.multipong.R;

public class PongView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint paint = new Paint();
    private int borderSize = 5;

    private int paletteWidth = 200;
    private int paletteHeight = 40;
    private int paletteH = 0;

    private Integer ballX = null;
    private Integer ballY = null;
    private int ballSize = 50;

    private Object canvasLock = new Object();

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

    public void movePalette(double relativePosition) {
        setPaletteH(relativePosition);
        doDraw();
    }

    private void setPaletteH(double relativePosition) {
        paletteH = (int) (left() + (right() - paletteWidth - left()) * relativePosition) + borderSize;
    }

    public void moveBall(double relX, double relY) {
        setBallX(relX);
        setBallY(relY);
        doDraw();
    }

    private void setBallX(double relX) {
        ballX = (int) (left() + (right() - ballSize - left()) * relX) + borderSize;
    }

    private void setBallY(double relY) {
        ballY = (int) ((getTop() + scrollTop()) * relY);
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
        if (ballX != null && ballY != null) drawBall(canvas);
        holder.unlockCanvasAndPost(canvas);
        synchronized (canvasLock) {
            canvasLock.notifyAll();
        }
    }

    private void drawFrame(Canvas canvas) {
        paint.setColor(Color.GRAY);
        canvas.drawRect(left(), scrollTop(), right(), scrollBottom(), paint);
        canvas.drawRect(left(), getTop(), left()+borderSize, scrollBottom(), paint);
        canvas.drawRect(right()-borderSize, getTop(), right(), scrollBottom(), paint);
        canvas.drawRect(left(), getTop(), right(), getTop()+borderSize, paint);
    }

    private void drawPalette(Canvas canvas) {
        paint.setColor(Color.BLACK);
        int startH = paletteH;
        if (startH < left()) startH = left() + borderSize;
        if (startH > right() - paletteWidth) startH = right() - paletteWidth;
        int endH   = startH + paletteWidth;
        int startV = scrollTop() - paletteHeight - 20;
        int endV   = startV + paletteHeight;
        canvas.drawRect(new Rect(startH, startV, endH, endV), paint);
    }

    private void drawBall(Canvas canvas) {
        Bitmap ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        Bitmap drawnBall = Bitmap.createScaledBitmap(ball, ballSize, ballSize, true);
        canvas.drawBitmap(drawnBall, ballX, ballY, paint);
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
    private int scrollTop ()   { return getTop()+(getBottom()-getTop())*4/5; }
    private int scrollBottom() { return getBottom(); }
}
