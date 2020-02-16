package com.bobo.himalayan.interfaces;


import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by Leon on 2020-02-01 Copyright © Leon. All rights reserved.
 * Functions:
 */
public interface IHistoryCallback {

    /**
     * 历史内容加载结果
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);


}
