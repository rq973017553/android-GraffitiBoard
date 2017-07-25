package com.graffitiboard.graffiti.base;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Administrator on 2017/7/23.
 */

public abstract class BaseGraffiti implements Graffiti {

    /**
     * 最大尺寸
     */
    protected static final float MAX_STROKE_WIDTH = 30;

    /**
     * 最小尺寸
     */
    protected static final float MIN_STROKE_WIDTH = 3;

    /**
     * 默认颜色
     */
    protected static final int DEFAULT_PAINT_COLOR = Color.BLACK;

    protected Paint mPaint;

    protected InvalidateCallback mInvalidateCallback;

    protected Context mContext;

    public BaseGraffiti(Context context){
        this.mContext = context;
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
    }

    protected int convertDpToPx(float dp){
        Context context = mContext;
        return Math.round(context.getResources().getDisplayMetrics().density * dp);
    }

    @Override
    public void setOnInvalidateCallback(InvalidateCallback callback){
        this.mInvalidateCallback = callback;
    }

    public abstract void setGraffitiSize(float size);

    public abstract void setGraffitiColor(int color);
}
