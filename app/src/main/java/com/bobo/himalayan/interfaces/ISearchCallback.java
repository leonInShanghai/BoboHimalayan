package com.bobo.himalayan.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.List;

/**
 * Created by Leon on 2020-01-01 Copyright © Leon. All rights reserved.
 * Functions: 搜索presenter回调ui的接口
 */
public interface ISearchCallback {


    /**
     * 搜索结果的回调方法
     *
     * @param result
     */
    void onSearchResultLoad(List<Album> result);

    /**
     * 获取推荐热词的结果回调方法
     *
     * @param hotWordList
     */
    void onHotWordLoaded(List<HotWord> hotWordList);

    /**
     * 加载更多的结果返回
     *
     * @param result 结果
     * @param isOkay true表示加载更多成功，false表示没有更多
     */
    void onLoadMoreResult(List<Album> result, boolean isOkay);

    /**
     * 联想关键字的结果回调方法
     *
     * @param keyWordList
     */
    void onRecommendWordLoaded(List<QueryResult> keyWordList);

    /**
     * 请求错误的回调
     *
     * @param errorCode
     * @param errorMsg
     */
    void onError(int errorCode, String errorMsg);

}
