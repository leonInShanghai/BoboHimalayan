package com.bobo.himalayan.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by 求知自学网 on 2019/11/23. Copyright © Leon. All rights reserved.
 * Functions:
 */
public interface IAlbumDetailViewCallback {

    /**
     * 专辑详情页面recycleview内容加载出来了
     * @param tracks
     */
    void onDetailListLoaded(List<Track> tracks);

    /**
     * 把album传给UI使用
     * @param album
     */
    void onAlbumLoaded(Album album);
}
