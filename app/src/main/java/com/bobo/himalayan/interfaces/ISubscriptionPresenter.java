package com.bobo.himalayan.interfaces;

import com.bobo.himalayan.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by Leon on 2020-01-12 Copyright © Leon. All rights reserved.
 * Functions: 订阅Presenter 接口
 * 订阅我们一般有上限，比如说不能超过100个
 */
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback> {

    /**
     * 添加订阅
     *
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     *
     * @param album
     */
    void deleteSubscription(Album album);

    /**
     * 获取订阅列表
     */
    void getSubcriptionList();

    /**
     * 判断当前专辑是否已经收藏/订阅
     * @param album ：专辑对象
     */
    boolean isSub(Album album);
}
