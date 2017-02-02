package com.multipong.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint paint = new Paint();

    public MySurfaceView(Context context) {
        super(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void doDraw(Canvas canvas) {
        canvas.drawRGB(255,255,255);
        paint.setColor(Color.GRAY);
        canvas.drawRect(left(), scrollTop(), right(), scrollBottom(), paint);
    }

    public void movePalette() {
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            doDraw(canvas);
            paint.setColor(Color.BLUE);
            canvas.drawRect(new Rect(getLeft(), getTop(), getLeft()+100, getTop()+100), paint);
        }
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas canvas = holder.lockCanvas();
        doDraw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public int left()         { return getLeft(); }
    public int right()        { return getRight(); }
    public int scrollTop ()   { return getTop()+(getBottom()-getTop())*4/5; }
    public int scrollBottom() { return getBottom(); }
}
