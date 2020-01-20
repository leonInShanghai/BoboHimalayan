package com.bobo.himalayan.presenters;

import android.support.annotation.Nullable;
import android.util.Log;

import com.bobo.himalayan.data.XimalayaApi;
import com.bobo.himalayan.interfaces.IAlbumDetailPresenter;
import com.bobo.himalayan.interfaces.IAlbumDetailViewCallback;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 公众号IT波 on 2019/11/23. Copyright © Leon. All rights reserved.
 * Functions:
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();

    /**
     * 数据源
     */
    private List<Track> mTracks = new ArrayList<>();

    //单例 自己的静态变量
    private static AlbumDetailPresenter sInstance = null;

    //详情页需要事情的专辑
    private Album mTargetAlbum;

    /**
     * 当前的id上拉加载更多用到 默认为：-1
     */
    private int mCurrentAlbumId = -1;

    /**
     * 当前的页码上拉加载更多用到 默认是0
     */
    private int mCurrentPageIndex = 0;

    /**
     * 单例标配 私有构造方法
     */
    private AlbumDetailPresenter() {
    }

    /**
     * 懒汉式 - 不到万不得已 不实例化自己 注意加线程锁（有线程安全风险）
     */
    public static AlbumDetailPresenter getsInstance() {
        if (sInstance == null) {
            //synchronized 同步锁
            synchronized (AlbumDetailPresenter.class) {
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2Refresh() {

    }

    /**
     * 上拉加载更多
     */
    @Override
    public void loadMore() {
        // 去加载更多内容
        mCurrentPageIndex++;

        // 传入true,表示结果会追加到列表的后方
        doLoaded(true);
    }

    private void doLoaded(final boolean isLoadMore) {

        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();

        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();

                    //if (isLoadMore) {
                    //    // 将请求来的数据添加到数据源
                    //    mTracks.addAll(tracks);
                    //    // mTracks.addAll(mTracks.size() - 1,tracks);
                    //}else{
                    //    mTracks.addAll(0,tracks);
                    //}

                    if (isLoadMore) {

                        int size = tracks.size();
                        handlerLoaderMoreResult(size);
                    }

                    mTracks.addAll(tracks);

                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMessage) {

                if (isLoadMore) {
                    mCurrentPageIndex--;
                }

                Log.e(TAG, "errorCode-->" + errorCode);
                Log.e(TAG, "errorMessage-->" + errorMessage);
                handlerError(errorCode, errorMessage);
            }
        }, mCurrentAlbumId, mCurrentPageIndex);
    }

    /**
     * 处理更多的接口
     *
     * @param size
     */
    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onLoaderMoreFinished(size);
        }
    }

    /**
     * 获取详情页每集的 RecycleView的展示内容
     *
     * @param albumId
     * @param page
     */
    @Override
    public void getAlbumDetail(int albumId, int page) {

        // 先清空原来的老数据
        if (mTracks != null && mTracks.size() > 0) {
            mTracks.clear();
        }

        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;

        //FIXME:修正 开始网络请求显示loading
        updateLoading();

        doLoaded(false);
    }

    /**
     * 如果是发生错误那么就可以通知UI， 网络请求失败回调
     *
     * @param errorCode
     * @param errorMessage
     */
    private void handlerError(int errorCode, String errorMessage) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onNetWorkError(errorCode, errorMessage);
        }
    }

    /**
     * 详情列表加载完成 回调
     */
    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);
        }
    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)) {
            mCallbacks.add(detailViewCallback);

            if (mTargetAlbum != null) {
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    //FIXME:修正
    private void updateLoading() {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.secondaryRefresh();
        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (mCallbacks != null) {
            mCallbacks.remove(detailViewCallback);
        }
    }

    /**
     * 上一页面请求号的值 跳转到详情页直接展示
     *
     * @param targetAlbum 在订阅fragment跳转到DetailActivity 时传递过来的
     */
    public void setTargetAlbum(Album targetAlbum) {
        this.mTargetAlbum = targetAlbum;
    }
}
