package com.bobo.himalayan.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

/**
 * Created by Leon on 2020-02-01 Copyright © Leon. All rights reserved.
 * Functions: 历史 数据库操作类  注意订阅对应的是专辑 历史对应的是集
 */
public interface IHistoryDao {

    /**
     * 设置回调（UI）接口
     * @param callback
     */
    void setCallback(IHistoryDaoCallback callback);

    /**
     * 添加历史
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     * @param track
     */
    void delHistory(Track track);


    /**
     * 清除（空）历史
     */
    void clearHistory();

    /**
     * 获取历史内容（列表）
     */
    void listHistories();

}
