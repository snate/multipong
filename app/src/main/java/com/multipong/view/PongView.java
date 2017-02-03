package com.multipong.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class PongView extends SurfaceView implements SurfaceHolder.Callback {

    private Paint paint = new Paint();
    private int borderSize = 5;
    private int paletteWidth = 200;
    private int paletteHeight = 40;
    private int paletteH = 0;

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
        SurfaceHolder holder = getHolder();
        Canvas canvas = holder.lockCanvas();
        if (canvas != null)
            doDraw(canvas);
        holder.unlockCanvasAndPost(canvas);
    }

    private void setPaletteH(double relativePosition) {
        paletteH = (int) (left() + (right() - paletteWidth - left()) * relativePosition) + borderSize;
    }

    private void doDraw(Canvas canvas) {
        canvas.drawRGB(255,255,255);
        drawFrame(canvas);
        drawPalette(canvas);
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

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        movePalette(0);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) { }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { }

    public int left()         { return getLeft(); }
    public int right()        { return getWidth(); }
    public int scrollTop ()   { return getTop()+(getBottom()-getTop())*4/5; }
    public int scrollBottom() { return getBottom(); }
}
