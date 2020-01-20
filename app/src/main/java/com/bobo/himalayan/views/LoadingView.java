package com.bobo.himalayan.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bobo.himalayan.R;

/**
 * Created by Leon on 2019/11/17. Copyright © Leon. All rights reserved.
 * Functions:
 */
@SuppressLint("AppCompatCustomView")
public class LoadingView extends ImageView {

    //旋转角度的变量
    private int rotateDegree = 0;

    //是否需要旋转的变量
    private boolean mNeedRotate = false;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //设置图片
        setImageResource(R.mipmap.loading);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mNeedRotate = true;

        //当绑定在window的时候
        post(new Runnable() {
            @Override
            public void run() {
                //rotateDegree += 30; rotateDegree = rotateDegree <= 360 ? rotateDegree : 0;
                //rotateDegree = rotateDegree <= 360 ? rotateDegree + 30 : 0;
                rotateDegree += 30;

                /**
                 * 使整个视图无效。如果视图是可见的，那么
                 *{@link#onDraw(android.Graphics.canvas)}将在将来的某个时候被调用。
                 * 这必须从UI线程调用。若要从非UI线程调用，请调用
                 * @link#postInValue()}
                 */
                invalidate();

                //判断是否继续旋转
                if (mNeedRotate) {
                    postDelayed(this, 100);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        //从window中解绑了
        mNeedRotate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        //在父类之前重绘 degrees:旋转角度, px,py 坐标
        canvas.rotate(rotateDegree, getWidth() / 2, getHeight() / 2);

        super.onDraw(canvas);
    }
}
