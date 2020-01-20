package com.bobo.himalayan.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by IT波 on 2019/11/23. Copyright © Leon. All rights reserved.
 * Functions:
 */
public interface IAlbumDetailViewCallback {

    /**
     * 专辑详情页面recycleview内容加载出来了
     *
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 网络错误-请求失败
     */
    void onNetWorkError(int errorCode, String errorMessage);

    /**
     * 把album传给UI使用
     *
     * @param album
     */
    void onAlbumLoaded(Album album);

    /**
     * 原来的刷新只有首次刷新有loading 修改成 请求错误后 用户点击了也 先启动loading再刷新
     */
    void secondaryRefresh();

    /**
     * 加载更多的结果
     *
     * @param size size > 0 表示加载成功，反之表示加载失败
     */
    void onLoaderMoreFinished(int size);

    /**
     * 下拉加载更多的结果
     *
     * @param size size > 0 表示加载成功，反之表示加载失败
     */
    void onRefreshFinished(int size);
}
