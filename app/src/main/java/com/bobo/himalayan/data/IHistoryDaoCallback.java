package com.bobo.himalayan.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by Leon on 2020-02-01 Copyright © Leon. All rights reserved.
 * Functions: 历史 回调ui接口
 */
public interface IHistoryDaoCallback {


    /**
     * 添加历史的结果
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史的结果
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * 历史数据加载的结果
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);

    /**
     * 历史内容清除结果
     */
    void onHistoriesClean(boolean isSuccess);


}
