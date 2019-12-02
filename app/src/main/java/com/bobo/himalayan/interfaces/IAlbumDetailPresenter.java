package com.bobo.himalayan.interfaces;

import com.bobo.himalayan.base.IBasePresenter;

/**
 * Created by 公众号IT波 on 2019/11/23. Copyright © Leon. All rights reserved.
 * Functions:
 */
public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallback> {

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

}






//原来的方法已经被抽取到父类中了
//    /**
//     * 注册通知的接口
//     * @param detailViewCallback
//     */
//    void registerViewCallback(IAlbumDetailViewCallback detailViewCallback);
//
//    /**
//     * 删除UI通知接口
//     * @param detailViewCallback
//     */
//    void unregisterViewCallback(IAlbumDetailViewCallback detailViewCallback);