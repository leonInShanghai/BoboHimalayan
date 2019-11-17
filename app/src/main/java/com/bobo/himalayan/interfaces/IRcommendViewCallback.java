package com.bobo.himalayan.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by Leon on 2019/11/17. Copyright © Leon. All rights reserved.
 * Functions: 订阅界面 数据层 和 ui层交互的接口
 */
public interface IRcommendViewCallback {

    /**
     * 获取到推荐内容的接口
     */
    void onRecommendListLoaded(List<Album> result);

    /**
     * 网络错误（加载失败）
     */
    void onNetworkError();

    /**
     * 加载到的数据为空
     */
    void onEmpty();

    /**
     * 正在加载
     */
    void onLoading();

    /**
     * 获取到加载更多
     * @param result
     */
    //void onLoadMore(List<Album> result);

    /**
     * 获取到下拉加载更多的结果
     * @param result
     */
    //void onRefreshMore(List<Album> result);
}
