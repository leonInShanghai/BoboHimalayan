package com.bobo.himalayan.fragments;

import com.bobo.himalayan.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bobo.himalayan.base.BaseFragment;

/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 历史fragment
 */
public class HistoryFragment extends BaseFragment {

    @Override
    public View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_history, container, false);
        return rootView;
    }


}
