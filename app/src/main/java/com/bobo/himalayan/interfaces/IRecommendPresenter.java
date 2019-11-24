package com.bobo.himalayan.interfaces;



/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions:  订阅界面 UI 和 presenter交互的接口
 */
public interface IRecommendPresenter {

    /**
     * 获取推荐内容
     */
    void getRecommendList();

    /**
     * 下拉加载更多的内容
     */
    void pull2Refresh();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 用于注册UI的回调
     * @param callback
     */
    void registerViewCallback(IRcommendViewCallback callback);

    /**
     * 取消UI的回调注册
     * @param callback
     */
    void unRegisterViewCallback(IRcommendViewCallback callback);

}
