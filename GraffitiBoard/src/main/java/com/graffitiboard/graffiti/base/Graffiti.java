package com.graffitiboard.graffiti.base;

import android.graphics.Canvas;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2017/7/23.
 */

public interface Graffiti {

    enum Mode {
        DRAW,
        ERASER
    }

    interface InvalidateCallback{
        void invalidateGraffiti();
        void invalidateGraffiti(int left, int top, int right, int bottom);
    }

    void draw(Canvas canvas);

    boolean onTouchEvent(MotionEvent event);

    void setOnInvalidateCallback(InvalidateCallback callback);
}
