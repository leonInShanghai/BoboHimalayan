package com.bobo.himalayan.presenters;

import android.support.annotation.Nullable;
import android.util.Log;

import com.bobo.himalayan.data.XimalayaApi;
import com.bobo.himalayan.interfaces.ISearchCallback;
import com.bobo.himalayan.interfaces.ISearchPresenter;
import com.bobo.himalayan.utils.Constants;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2020-01-01 Copyright © Leon. All rights reserved.
 * Functions:
 */
public class SearchPreseter implements ISearchPresenter {

    private List<Album> mSearchResult = new ArrayList<>();

    private String TAG = "SearchPreseter";

    private static SearchPreseter sSearchPreseter = null;

    private List<ISearchCallback> mCallbacks = new ArrayList<>();

    /**
     * 搜索关键字
     */
    private String mCurrentKeyword;

    /**
     * 请求参数api
     */
    private XimalayaApi mXimalayaApi;

    // 喜马拉雅这边分页是从1开始的定义一个常量
    private static final int DEFAULT_PAGE = 1;

    private int mCurrentPage = DEFAULT_PAGE;

    // 是否是加载更多
    private boolean mIsLoaderMore = false;

    /**
     * 私有化构造方法单例标配
     */
    private SearchPreseter() {
        mXimalayaApi = XimalayaApi.getXimalayaApi();
    }

    /**
     * 懒汉式单例有线程安全风险一定要加锁
     *
     * @return
     */
    public static SearchPreseter getSearchPreseter() {
        if (sSearchPreseter == null) {
            synchronized (SearchPreseter.class) {
                if (sSearchPreseter == null) {
                    sSearchPreseter = new SearchPreseter();
                }
            }
        }
        return sSearchPreseter;
    }

    @Override
    public void doSearch(String keyword) {

        mCurrentPage = DEFAULT_PAGE;

        mSearchResult.clear();

        // 抽取为成员变量用于重新搜索即当网络不好的时候用户点击了重新搜索
        this.mCurrentKeyword = keyword;

        search(keyword);
    }

    /**
     * 根据关键字搜索
     *
     * @param keyword
     */
    private void search(String keyword) {
        mXimalayaApi.searchByKeyWord(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(@Nullable SearchAlbumList searchAlbumList) {

                List<Album> albums = searchAlbumList.getAlbums();

                if (albums != null) {

                    mSearchResult.addAll(albums);

                    // 判断是否是加载更多
                    if (mIsLoaderMore) {
                        // 是否加载更多变量恢复默认值
                        mIsLoaderMore = false;

                        for (ISearchCallback iSearchCallback : mCallbacks) {

                            /// 注释原因
                            // if (albums.size() == 0){
                            //     iSearchCallback.onLoadMoreResult(mSearchResult,false);
                            // }else{
                            //     iSearchCallback.onLoadMoreResult(mSearchResult,true);
                            // }

                            // 代码抽取
                            iSearchCallback.onLoadMoreResult(mSearchResult, albums.size() != 0);
                        }

                    } else {

                        // 请求成功回调UI刷新界面
                        for (ISearchCallback iSearchCallback : mCallbacks) {
                            iSearchCallback.onSearchResultLoad(mSearchResult);
                        }
                    }
                    Log.e(TAG, albums.size() + "");
                } else {
                    Log.e(TAG, "albums is null...");
                }


            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG, "errorCode -->" + errorCode + "errorMenssage" + errorMessage);

                // 请求失败接口的回调
                for (ISearchCallback iSearchCallback : mCallbacks) {
                    // 加载更多数据失败回调
                    if (mIsLoaderMore) {

                        iSearchCallback.onLoadMoreResult(mSearchResult, false);

                        mCurrentPage--;

                        // 是否是加载更多恢复默认值
                        mIsLoaderMore = false;
                    } else {
                        iSearchCallback.onError(errorCode, errorMessage);
                    }
                }

            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    /**
     * 加载更多
     */
    @Override
    public void loadMore() {

        // 判断有没有必要加载更多
        if (mSearchResult.size() < Constants.COUNT_DEFAULT) {
            for (ISearchCallback iSearchCallback : mCallbacks) {
                iSearchCallback.onLoadMoreResult(mSearchResult, false);
            }
        } else {
            // 是加载更多
            mIsLoaderMore = true;

            mCurrentPage++;

            search(mCurrentKeyword);
        }
    }

    @Override
    public void getHotWork() {

        // 获取推荐热词的数据
        mXimalayaApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(@Nullable HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();

                    /// 调试代码
                    // Log.e(TAG,"hotWords size -->" + hotWords.size());

                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onHotWordLoaded(hotWords);
                    }
                }

            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG, "getHotWords errorCode -->" + errorCode + "getHotWords errorMenssage" + errorMessage);

                /// 请求失败接口的回调 这里不做处理请求失败就不显示呗
                // for (ISearchCallback iSearchCallback : mCallbacks) {
                //     iSearchCallback.onError(errorCode,errorMessage);
                // }
            }
        });
    }

    @Override
    public void getRecommendWord(String keyword) {

        // 根据关键字获取联想词
        mXimalayaApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {

            @Override
            public void onSuccess(@Nullable SuggestWords suggestWords) {
                if (suggestWords != null) {
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    Log.e(TAG, keyWordList.size() + "");

                    for (ISearchCallback iSearchCallback : mCallbacks) {
                        iSearchCallback.onRecommendWordLoaded(keyWordList);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG, "getSuggestWord errorCode -->" + errorCode + "getSuggestWord errorMenssage" + errorMessage);

                /// 请求失败接口的回调 请求失败不处理请求失败就不显示不用刻意告诉用户
                // for (ISearchCallback iSearchCallback : mCallbacks) {
                //     iSearchCallback.onError(errorCode,errorMessage);
                // }
            }
        });
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallbacks.contains(iSearchCallback)) {
            mCallbacks.add(iSearchCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        if (mCallbacks != null) {
            mCallbacks.remove(iSearchCallback);
        }
    }
}
