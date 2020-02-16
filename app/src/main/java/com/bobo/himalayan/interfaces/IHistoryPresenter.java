package com.bobo.himalayan.interfaces;

import com.bobo.himalayan.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;


/**
 * Created by Leon on 2020-02-01 Copyright © Leon. All rights reserved.
 * Functions: 历史 ui调用presenter 的接口
 */
public interface IHistoryPresenter extends IBasePresenter<IHistoryCallback> {

    /**
     * 获取历史内容
     */
    void listHistories();

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
     * 清除历史
     */
    void cleanHistories();

}
