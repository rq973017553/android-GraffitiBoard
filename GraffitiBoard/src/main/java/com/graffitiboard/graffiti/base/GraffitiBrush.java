package com.graffitiboard.graffiti.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.view.MotionEvent;

import com.graffitiboard.graffiti.path.CachePath;
import com.graffitiboard.graffiti.path.manager.PathManager;

import java.util.List;

/**
 * Created by Administrator on 2017/7/24.
 */

public abstract class GraffitiBrush extends BaseGraffiti {
    protected PathManager manager;
    protected Path mPath;
    protected Bitmap mCacheBitmap;
    protected Canvas mCacheCanvas;
    protected int mWidth;
    protected int mHeight;
    protected float mGraffitiSize;
    protected int mGraffitiColor;
    public GraffitiBrush(Context context){
        super(context);
        manager = new PathManager();
        mPath = new Path();
        setGraffitiSize(MIN_STROKE_WIDTH);
        setGraffitiColor(DEFAULT_PAINT_COLOR);
    }

    public void setCacheBitmapWidth(int width){
        this.mWidth = width;
    }

    public void setCacheBitmapHeight(int height){
        this.mHeight = height;
    }

    @Override
    public void setGraffitiSize(float size) {
        this.mGraffitiSize = convertDpToPx(size);
        mPaint.setStrokeWidth(mGraffitiSize);
    }

    @Override
    public void setGraffitiColor(int color) {
        this.mGraffitiColor = color;
        mPaint.setColor(mGraffitiColor);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 判断当前是否需要恢复
     * @return
     */
    public boolean isRecovery() {
        List<CachePath> removeList;
        removeList = manager.getRemoveList();
        return removeList != null && removeList.size() > 0;
    }

    /**
     *判断当前是否需要撤销
     * @return
     */
    public boolean isRevoke(){
        List<CachePath> drawList;
        drawList = manager.getDrawList();
        return drawList != null && drawList.size() > 0;
    }

    /**
     * 撤销
     */
    public void revoke(){
        manager.revoke();
        reDraw();
    }

    /**
     * 恢复
     */
    public void recovery(){
        manager.recovery();
        reDraw();
    }

    /**
     * 全部清除
     */
    public void clear() {
        if (mCacheBitmap != null) {
            manager.clearPath();
            mCacheBitmap.eraseColor(Color.TRANSPARENT);
            if (mInvalidateCallback != null){
                mInvalidateCallback.invalidateGraffiti();
            }
        }
    }

    private void reDraw(){
        List<CachePath> drawList = manager.getDrawList();
        if (drawList != null) {
            mCacheBitmap.eraseColor(Color.TRANSPARENT);
            for (CachePath drawingInfo : drawList) {
                drawingInfo.draw(mCacheCanvas);
            }
            if (mInvalidateCallback != null){
                mInvalidateCallback.invalidateGraffiti();
            }
        }
    }


    protected void createCache(){
        if(mCacheBitmap == null){
            mCacheBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
            mCacheCanvas = new Canvas(mCacheBitmap);
        }
    }
}
