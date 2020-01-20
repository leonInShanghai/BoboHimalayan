package com.bobo.himalayan.presenters;

import android.support.annotation.Nullable;

import com.bobo.himalayan.data.XimalayaApi;
import com.bobo.himalayan.interfaces.IRcommendViewCallback;
import com.bobo.himalayan.interfaces.IRecommendPresenter;
import com.bobo.himalayan.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2019/11/17. Copyright © Leon. All rights reserved.
 * Functions: 订阅页面的 presenter
 */
public class RecommendPresenter implements IRecommendPresenter {

    public static RecommendPresenter sInstance = null;

    public static final String TAG = "RecommendPresenter";

    private List<IRcommendViewCallback> mCallbacks = new ArrayList<>();

    private List<Album> mCurrentRecommend = null;


    /**
     * 单例Java基础 先创建一个私有的构造方法
     */
    private RecommendPresenter() {

    }

    /**
     * 懒汉式单例一定要加线程锁
     *
     * @return
     */
    public static RecommendPresenter getsInstance() {
        if (sInstance == null) {
            synchronized (RecommendPresenter.class) {
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    /**
     * 阳光总在风雨后 获取当前的推荐专辑列表
     *
     * @return 推荐专辑列表，使用前要做非空判断
     */
    public List<Album> getCurrentRecommend() {
        return mCurrentRecommend;
    }

    /**
     * 获取推荐内容，其实就是猜你喜欢
     * 这个接口：3.10.6
     */
    @Override
    public void getRecommendList() {

        // 开始网络请求显示loading
        updateLoading();

        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG, "thread name--->" + Thread.currentThread().getName());
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();

                    //数据请求回来更新UI
                    //upRecommendUI(albumList);
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //数据获取失败
                LogUtil.e(TAG, "error -->" + i);
                LogUtil.e(TAG, "errorMsg -->" + s);

                //请求失败的处理
                handlerError();
            }
        });
    }

    //请求失败通知ui更新
    private void handlerError() {
        //通知UI界面更新
        if (mCallbacks != null) {
            for (IRcommendViewCallback callback : mCallbacks) {
                callback.onNetworkError();
            }
        }
    }

    //请求成功通知ui更新
    private void handlerRecommendResult(List<Album> albumList) {

        //通知UI界面更新
        if (albumList != null) {
            if (albumList.size() == 0) {
                for (IRcommendViewCallback callback : mCallbacks) {
                    //请求回来的数据为空 调用为空接口显示为空的UI
                    callback.onEmpty();
                }
            } else {
                for (IRcommendViewCallback callback : mCallbacks) {
                    //请求回来有数据显示数据
                    callback.onRecommendListLoaded(albumList);
                }

                this.mCurrentRecommend = albumList;
            }
        }
    }

    /**
     * 开始网络请求 显示loading
     */
    private void updateLoading() {
        for (IRcommendViewCallback callback : mCallbacks) {
            //请求回来有数据显示数据
            callback.onLoading();
        }
    }

    @Override
    public void pull2Refresh() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRcommendViewCallback callback) {
        if (mCallbacks != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRcommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}
