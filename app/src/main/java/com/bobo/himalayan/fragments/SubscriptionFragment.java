package com.bobo.himalayan.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bobo.himalayan.R;
import com.bobo.himalayan.base.BaseFragment;

/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 订阅fragment
 */
public class SubscriptionFragment extends BaseFragment {

    @Override
    public View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.fragment_subscription, container, false);
        return rootView;
    }
}
