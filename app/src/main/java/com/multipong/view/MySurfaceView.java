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
    private int paletteWidth = 200;
    private int paletteHeight = 40;

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

    public void movePalette(double relativePos) {
        if (relativePos < 0.0) relativePos = 0.0;
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas != null) {
            doDraw(canvas);
            paint.setColor(Color.BLACK);
            int startH = (int) (left() + (right() - paletteWidth - left()) * relativePos);
            int endH   = startH + paletteWidth;
            int startV = scrollTop() - paletteHeight - 20;
            int endV   = startV + paletteHeight;
            canvas.drawRect(new Rect(startH, startV, endH, endV), paint);
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
