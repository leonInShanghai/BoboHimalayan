package com.bobo.himalayan.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.R;

/**
 * Created by 微信公众号IT波 on 2019/12/14. Copyright © Leon. All rights reserved.
 * Functions: 播放页右下角 的popwindow
 */
public class SobPopWindow extends PopupWindow {

    public SobPopWindow(){

        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        /**
         * 程序员拉大锯说setOutsideTouchable(true);之前先要设置 setBackgroundDrawable();，否则无法关闭
         *（我这不设置也能关闭）
         */
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //用户点击别的地方 SobPopWindow消失
        setOutsideTouchable(true);

        //加载进来view
        View popView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list,null);

        //设置内容 这个方法和Activity里的方法有点像😄
        setContentView(popView);
    }

}
