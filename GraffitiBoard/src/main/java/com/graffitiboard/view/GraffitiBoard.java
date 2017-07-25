package com.graffitiboard.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.graffitiboard.graffiti.base.Graffiti;
import com.graffitiboard.graffiti.pen.GraffitiPen;
import com.graffitiboard.view.utils.ViewCompat;
import com.graffitiboard.view.utils.ViewTreeObserverCompat;

/**
 * Created by Administrator on 2017/7/23.
 */

public class GraffitiBoard extends View implements Graffiti.InvalidateCallback{

    private Graffiti mGraffiti;

    private UndoRedoCallback mCallback;

    public GraffitiBoard(Context context) {
        super(context);
    }

    public GraffitiBoard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GraffitiBoard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public interface UndoRedoCallback {
        void onUndoRedoStatusChanged();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initData();
    }

    private void initData(){
        if (ViewCompat.isLaidOut(this)){
            if (mGraffiti == null){
                mGraffiti = createGraffitiPen();
            }
        }else {
            final ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    initData();
                    if (observer.isAlive()){
                        ViewTreeObserverCompat.removeOnGlobalLayoutListener(observer, this);
                    }
                }
            });
        }
    }

    public Graffiti createGraffitiPen(){
        GraffitiPen pen = new GraffitiPen(getContext());
        pen.setOnInvalidateCallback(this);
        pen.setCacheBitmapWidth(getMeasuredWidth());
        pen.setCacheBitmapHeight(getMeasuredHeight());
        return pen;
    }

    public void setUndoRedoCallback(UndoRedoCallback callback){
        this.mCallback = callback;
    }

    public void setGraffitiSize(float graffitiSize){
        GraffitiPen pen = (GraffitiPen)mGraffiti;
        pen.setGraffitiSize(graffitiSize);
    }

    /**
     * 全部清除
     */
    public void doClear(){
        GraffitiPen pen = (GraffitiPen)mGraffiti;
        pen.clear();
        if (mCallback != null){
            mCallback.onUndoRedoStatusChanged();
        }
    }

    /**
     * 撤销
     */
    public void revoke(){
        GraffitiPen pen = (GraffitiPen)mGraffiti;
        pen.revoke();
        if (mCallback != null){
            mCallback.onUndoRedoStatusChanged();
        }
    }

    /**
     * 恢复
     */
    public void recovery(){
        GraffitiPen pen = (GraffitiPen)mGraffiti;
        pen.recovery();
        if (mCallback != null){
            mCallback.onUndoRedoStatusChanged();
        }
    }

    /**
     *判断当前是否需要撤销
     * @return
     */
    public boolean isRevoke(){
        GraffitiPen pen = (GraffitiPen)mGraffiti;
        return pen.isRevoke();
    }

    /**
     * 判断当前是否需要恢复
     * @return
     */
    public boolean isRecovery(){
        GraffitiPen pen = (GraffitiPen)mGraffiti;
        return pen.isRecovery();
    }

    public Bitmap buildBitmap() {
        buildDrawingCache();
        Bitmap bitmap = getDrawingCache();
        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN){
            getParent().requestDisallowInterceptTouchEvent(true);
        }else if(action == MotionEvent.ACTION_UP){
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        if (mGraffiti != null){
            boolean touch = mGraffiti.onTouchEvent(event);
            if (action == MotionEvent.ACTION_UP){
                if (mCallback != null){
                    mCallback.onUndoRedoStatusChanged();
                }
            }
            return touch;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mGraffiti != null){
            mGraffiti.draw(canvas);
        }
    }

    @Override
    public void invalidateGraffiti() {
        invalidate();
    }

    @Override
    public void invalidateGraffiti(int left, int top, int right, int bottom) {
        invalidate(left, top, right, bottom);
    }
}
