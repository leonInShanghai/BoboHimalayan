package com.bobo.himalayan.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentContainer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.bobo.himalayan.utils.FragmentCreator;

/**
 * Created by 求知自学网 on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 首页 viewpager的适配器
 */
public class MainContentAdapter extends FragmentPagerAdapter {


    public MainContentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentCreator.getFragment(position);
    }

    @Override
    public int getCount() {
        return FragmentCreator.PAGE_COUNT;
    }
}
