package com.bobo.himalayan.interfaces;

/**
 * Created by 求知自学网 on 2019/11/23. Copyright © Leon. All rights reserved.
 * Functions:
 */
public interface IAlbumDetailPresenter {

    /**
     * 下拉加载更多的内容
     */
    void pull2Refresh();

    /**
     * 上拉加载更多
     */
    void loadMore();

    /**
     * 获取专辑详情
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId,int page);

    /**
     * 注册通知的接口
     * @param detailViewCallback
     */
    void registerViewCallback(IAlbumDetailViewCallback detailViewCallback);

    /**
     * 删除UI通知接口
     * @param detailViewCallback
     */
    void unregisterViewCallback(IAlbumDetailViewCallback detailViewCallback);
}
