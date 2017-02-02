package com.multipong.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class MySurfaceView extends SurfaceView {

    public MySurfaceView(Context context) {
        super(context);
    }

    public MySurfaceView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.GRAY);
        canvas.drawRect(left(),scrollTop(),right(),scrollBottom(),paint);
        super.onDraw(canvas);
    }

    public int left()         { return getLeft(); }
    public int right()        { return getRight(); }
    public int scrollTop ()   { return getTop()+(getBottom()-getTop())*4/5; }
    public int scrollBottom() { return getBottom(); }
}
