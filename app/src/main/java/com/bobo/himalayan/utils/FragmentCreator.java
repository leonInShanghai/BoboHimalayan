package com.bobo.himalayan.utils;

import com.bobo.himalayan.base.BaseFragment;
import com.bobo.himalayan.fragments.HistoryFragment;
import com.bobo.himalayan.fragments.RecommendFragmnet;
import com.bobo.himalayan.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 管理  FragmentActivity  的工具类这个项目没有用fragment
 */
public class FragmentCreator {

    public final static int INDEX_RECOMMEND = 0;
    public final static int INDEX_SUBSCRIPTON = 1;
    public final static int INDEX_HISTORY = 2;

    public final static int PAGE_COUNT = 3;

    public static Map<Integer, BaseFragment> sCache = new HashMap<>();

    /**
     * 根据索引获取 fragment
     *
     * @param index
     * @return
     */
    public static BaseFragment getFragment(int index) {

        //优先从缓存中取
        BaseFragment baseFragment = sCache.get(index);
        if (baseFragment != null) {

            //如果缓存中取到的不为null 就返回缓存中取出的baseFragment
            return baseFragment;
        }

        //如果缓存中没有 则根据索引创建出对应的fragment
        switch (index) {
            case INDEX_RECOMMEND:
                baseFragment = new RecommendFragmnet();
                break;
            case INDEX_SUBSCRIPTON:
                baseFragment = new SubscriptionFragment();
                break;
            case INDEX_HISTORY:
                baseFragment = new HistoryFragment();
                break;
        }

        //将创建的baseFragment添加到map中
        sCache.put(index, baseFragment);

        //返回新创建的baseFragment
        return baseFragment;
    }


}
