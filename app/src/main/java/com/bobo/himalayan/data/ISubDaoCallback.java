package com.bobo.himalayan.data;

import com.bobo.himalayan.presenters.AlbumDetailPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by 微信公众号IT波 on 2020/1/22. Copyright © Leon. All rights reserved.
 * Functions: 用户浏览记录 增删改查
 */
public interface ISubDaoCallback {

    /**
     * 添加用户浏览记录的接口回调方法
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除用户浏览记录的接口回调方法
     * @param isSuccess
     */
    void onDelResult(boolean isSuccess);

    /**
     * 加载的结果
     * @param result
     */
    void onSubListLoaded(List<Album> result);

}
