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
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 首页的适配器继承自 MagicIndicator框架中的 CommonNavigatorAdapter
 */
public class IndicatorAdapter extends CommonNavigatorAdapter {

    private String[] mTitles;

    //title点击事件传递的接口
    private OnIndicatorTapClickListener mOnTapClickListener;

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

//        ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
//
//        clipPagerTitleView.setText(mTitles[index]);
//        clipPagerTitleView.setTextColor(Color.parseColor("#f2c4c4"));
//        clipPagerTitleView.setClipColor(Color.WHITE);
//
//        clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               //如果index不一样的话 切换viewpager的内容
//                if (mOnTapClickListener != null){
//                    mOnTapClickListener.onTabClick(index);
//                }
//            }
//        });
//        return clipPagerTitleView;

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
                //如果index不一样的话 切换viewpager的内容
                if (mOnTapClickListener != null){
                    mOnTapClickListener.onTabClick(index);
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


    //供外界调用设置 点击事件监听
    public void setOnIndicatorTapClickListener(OnIndicatorTapClickListener listener){
        this.mOnTapClickListener = listener;
    }

    //colorTransitionPagerTitleView点击回调接口
    public interface OnIndicatorTapClickListener{
        void onTabClick(int index
        );
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