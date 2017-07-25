package com.graffitiboard.graffiti.path;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.graffitiboard.graffiti.base.Graffiti;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/7/23.
 */

public class BrushPath implements CachePath {

    private List<Point> mPointList = new ArrayList<>();

    private Rect mRect = new Rect();

    Graffiti.InvalidateCallback mCallback;

//    private Paint paint = new Paint();

    @Override
    public void draw(Canvas canvas){
//        paint.setColor(Color.BLUE);
        for (Point point :mPointList){
            canvas.drawPoint(point.mPointX, point.mPointY, point.mPointPaint);
        }
//        canvas.drawRect(mRect, paint);

        //这里的局部刷新可能有问题
        mCallback.invalidateGraffiti(mRect.left, mRect.top, mRect.right, mRect.bottom);
    }

    public void setPointList(float x, float y, Paint paint){
        Point point = new Point();
        point.mPointX = x;
        point.mPointY = y;
        point.mPointPaint = paint;
        mPointList.add(point);
    }

    public void setInvalidateCallback(Graffiti.InvalidateCallback callback){
        this.mCallback = callback;
    }

    public void setInvalidateRect(Rect rect){
        this.mRect = rect;
    }

    private class Point{
        float mPointX;
        float mPointY;
        Paint mPointPaint;
    }
}
