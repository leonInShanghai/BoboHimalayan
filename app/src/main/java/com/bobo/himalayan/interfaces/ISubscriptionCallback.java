package com.bobo.himalayan.interfaces;

import com.bobo.himalayan.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by Leon on 2020-01-12 Copyright © Leon. All rights reserved.
 * Functions: 订阅Presenter 回调
 */
public interface ISubscriptionCallback {

    /**
     * 调用添加的时候，去通知Ui结果
     *
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);


    /**
     * 删除订阅的回调
     *
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);


    /**
     * 订阅专辑加载的结果回调方法
     *
     * @param albums
     */
    void onSubscritpionsLoaded(List<Album> albums);

}
