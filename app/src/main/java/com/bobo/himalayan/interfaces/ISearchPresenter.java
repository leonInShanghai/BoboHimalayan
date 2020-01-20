package com.bobo.himalayan.interfaces;


import com.bobo.himalayan.base.IBasePresenter;

/**
 * Created by Leon on 2020-01-01 Copyright © Leon. All rights reserved.
 * Functions: 搜索界面调用presenter接口
 */
public interface ISearchPresenter extends IBasePresenter<ISearchCallback> {


    /**
     * 进行搜索
     *
     * @param keyword
     */
    void doSearch(String keyword);

    /**
     * 重新搜索
     */
    void reSearch();

    /**
     * 加载更多的搜索结果
     */
    void loadMore();

    /**
     * 获取热词
     */
    void getHotWork();

    /**
     * 获取推荐的关键字（相似的关键字）
     *
     * @param keyword
     */
    void getRecommendWord(String keyword);
}
