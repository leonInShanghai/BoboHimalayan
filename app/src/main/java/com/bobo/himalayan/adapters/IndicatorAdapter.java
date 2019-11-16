package com.bobo.himalayan.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.bobo.himalayan.MainActivity;
import com.bobo.himalayan.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * Created by 求知自学网 on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 首页的适配器继承自 MagicIndicator框架中的 CommonNavigatorAdapter
 */
public class IndicatorAdapter extends CommonNavigatorAdapter {

    private String[] mTitles;

    //title点击事件传递的接口
    private OnTitleSelectedListener mListener;

    public IndicatorAdapter(Context context) {
        //获取在xml中定义好的数组
        mTitles = context.getResources().getStringArray(R.array.indicater_name);
    }

    @Override
    public int getCount() {
        return mTitles == null ? 0 : mTitles.length;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {

        //创建view
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);

        //设置一般情况下的颜色
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));

        //设置选择情况下颜色
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));

        //设置字体大小单位是sp
        colorTransitionPagerTitleView.setTextSize(18);

        //设置要显示的内容
        colorTransitionPagerTitleView.setText(mTitles[index]);

        //设置title点击事件，这里如果点击了title那么就选中下面的view pager  也就是说点了view pager也跟着切换
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onTitleSelected(index);
                }
            }
        });

       return  colorTransitionPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.WHITE);
        return linePagerIndicator;
    }


    public void setOnTitleSelectedListener(OnTitleSelectedListener listener){
        this.mListener = listener;
    }

    //colorTransitionPagerTitleView点击回调接口
    public interface OnTitleSelectedListener{
        void onTitleSelected(int index);
    }

}


//    SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
//        simplePagerTitleView.setNormalColor(Color.GRAY);
//                simplePagerTitleView.setSelectedColor(Color.WHITE);
//                simplePagerTitleView.setText(mTitles[index]);
//                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
//@Override
//public void onClick(View v) {
//
//        }
//        });
//        return simplePagerTitleView;