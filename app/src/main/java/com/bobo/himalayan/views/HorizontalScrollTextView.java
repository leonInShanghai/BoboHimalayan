package com.bobo.himalayan.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * Created by Leon on 2019-10-22 Copyright © Leon. All rights reserved.
 * Functions: 仿抖音自定义横向滚动的 textview
 */
@SuppressLint("AppCompatCustomView")
public class HorizontalScrollTextView extends TextView {

    public HorizontalScrollTextView(Context context) {
        super(context);
    }

    public HorizontalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScrollTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
