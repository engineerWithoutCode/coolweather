package com.example.z.coolweather;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PointTabView extends View {
    private Bitmap pointBlue;
    private Bitmap pointWhite;

    private int viewWidth,viewHeight,cellWidth;
    private Rect pointBlueRect,pointWhiteRect,desRect;

    private int num=0,selectNum=1;


    public PointTabView(Context context) {
        super(context);
        pointBlue = BitmapFactory.decodeResource(getResources(),R.drawable.point_select);
        pointWhite = BitmapFactory.decodeResource(getResources(),R.drawable.point_normal);
    }

    public PointTabView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        pointBlue = BitmapFactory.decodeResource(getResources(),R.drawable.point_select);
        pointWhite = BitmapFactory.decodeResource(getResources(),R.drawable.point_normal);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        cellWidth = h/5;
        Log.d("w",w+"");
        Log.d("h",h+"");
        Log.d("cell",cellWidth+"");
    }

    protected  void onDraw(Canvas canvas){
        super.onDraw(canvas);
        int startLeft = (viewWidth - num*cellWidth)/2;
        int startRight = startLeft + cellWidth;
        pointBlueRect = new Rect(0,0,pointBlue.getWidth(),pointBlue.getHeight());
        pointWhiteRect = new Rect(0,0,pointWhite.getWidth(),pointWhite.getHeight());
        for(int i=1;i <= num;i++){
            desRect = new Rect(startLeft + (i-1)*cellWidth,2*cellWidth,startRight + (i-1)*cellWidth,3*cellWidth);
            if (i == selectNum){
                canvas.drawBitmap(pointBlue,pointBlueRect,desRect,null);
            }else{
                canvas.drawBitmap(pointWhite,pointWhiteRect,desRect,null);
            }
        }
    }

    public void setNum(int i){
        num = i;
        postInvalidate();
    }

    public void setSelectNum(int i){
        selectNum = i;
        postInvalidate();
    }

}
