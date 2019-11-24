package com.bobo.himalayan.presenters;

import android.support.annotation.Nullable;
import android.util.Log;

import com.bobo.himalayan.interfaces.IAlbumDetailPresenter;
import com.bobo.himalayan.interfaces.IAlbumDetailViewCallback;
import com.bobo.himalayan.utils.Constants;
import com.bobo.himalayan.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 求知自学网 on 2019/11/23. Copyright © Leon. All rights reserved.
 * Functions:
 */
public class AlbumDetailPresenter implements IAlbumDetailPresenter {

    private static final String TAG = "AlbumDetailPresenter";
    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();

    //单例 自己的静态变量
    private static AlbumDetailPresenter sInstance = null;

    //详情页需要事情的专辑
    private Album mTargetAlbum;

    /**
     * 单例标配 私有构造方法
     */
    private AlbumDetailPresenter() {
    }

    /**
     * 懒汉式 - 不到万不得已 不实例化自己 注意加线程锁（有线程安全风险）
     */
    public static AlbumDetailPresenter getsInstance() {
        if (sInstance == null){
            //synchronized 同步锁
            synchronized (AlbumDetailPresenter.class){
                if (sInstance == null) {
                    sInstance= new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void pull2Refresh() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        //根据页码和专辑的id获取列表数据
        Map<String,String> map = new HashMap<>();
        map.put(DTransferConstants.ALBUM_ID,albumId+"");
        map.put(DTransferConstants.SORT,"asc");
        map.put(DTransferConstants.PAGE,page+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, tracks.size()+"");
                    handlerAlbumDetailResult(tracks);
                }

            }

            @Override
            public void onError(int errorCode, String errorMessage) {
                Log.e(TAG,"errorCode-->"+errorCode);
                Log.e(TAG,"errorMessage-->"+errorMessage);
            }
        });
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

            if (mTargetAlbum != null){
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    @Override
    public void unregisterViewCallback(IAlbumDetailViewCallback detailViewCallback) {
        if (mCallbacks != null){
            mCallbacks.remove(detailViewCallback);
        }
    }

    public void setTargetAlbum(Album targetAlbum){
        this.mTargetAlbum = targetAlbum;
    }
}
