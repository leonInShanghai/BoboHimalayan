package com.bobo.himalayan.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by Leon on 2020-01-12 Copyright © Leon. All rights reserved.
 * Functions: 数据库操作接口
 */
public interface ISubDao {

    /**
     * 添加专辑订阅
     *
     * @param album
     */
    void addAlbum(Album album);


    /**
     * 删除订阅内容
     *
     * @param album
     */
    void delAlbum(Album album);

    /**
     * 获取订阅内容
     */
    void listAlbums();

}
