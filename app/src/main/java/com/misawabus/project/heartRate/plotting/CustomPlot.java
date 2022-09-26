package com.misawabus.project.heartRate.plotting;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.androidplot.xy.XYPlot;

public class CustomPlot extends XYPlot {



    public CustomPlot(Context context, String title, RenderMode mode) {
        super(context, title, mode);

    }

    public CustomPlot(Context context, AttributeSet attributes) {
        super(context, attributes);

    }

    public CustomPlot(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);



        paint.setColor(Color.BLACK);

        canvas.drawCircle(getMeasuredWidth()/2.0f, getMeasuredHeight()/2.0f, 100.0f, paint);
    }

    CustomPlot(Context context, String title){
        super(context, title);

    }


}
