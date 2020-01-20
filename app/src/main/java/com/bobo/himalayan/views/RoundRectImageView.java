package com.bobo.himalayan.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 自定义圆角imageview
 */
public class RoundRectImageView extends AppCompatImageView {

    //修改圆角的大小这个变量↓
    private float roundRatio = 0.1f;
    private Path path;

    public RoundRectImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (path == null) {
            path = new Path();
            path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), roundRatio * getWidth(),
                    roundRatio * getHeight(), Path.Direction.CW);
        }

        //抗锯齿 Leon增加 感觉没有什么用
        canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint
                .FILTER_BITMAP_FLAG));

        canvas.save();
        canvas.clipPath(path);
        super.onDraw(canvas);
        canvas.restore();
    }

}
