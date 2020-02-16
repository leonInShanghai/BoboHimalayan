package com.bobo.himalayan.presenters;

import android.util.Log;

import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.data.ISubDaoCallback;
import com.bobo.himalayan.data.SubscriptionDao;
import com.bobo.himalayan.interfaces.ISubscriptionCallback;
import com.bobo.himalayan.interfaces.ISubscriptionPresenter;
import com.bobo.himalayan.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by 微信公众号IT波 on 2020/1/22. Copyright © Leon. All rights reserved.
 * Functions: 订阅（Fragment）的 Presenter
 */
public class SubscritptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {

    // 本类自己对象
    private static SubscritptionPresenter sInstance = null;

    // 数据库操作类对象
    private final SubscriptionDao mSubscriptionDao;

    private Map<Long, Album> mData = new HashMap<>();

    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();

    // 单例标配私有构造函数
    private SubscritptionPresenter(){
        mSubscriptionDao = SubscriptionDao.getInstance();

        // 让本类实现回调方法
        mSubscriptionDao.setCallback(this);
    }

    private void listSubscriptions(){

        // 子线程操作数据库
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                // 获取数据库中的所有数据 只调用,不处理
                mSubscriptionDao.listAlbums();

                Log.e("listSubscriptions():", Thread.currentThread().getName());
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    /**
     * 单例中获取实例化的自己
     * @return
     */
    public static SubscritptionPresenter getInstance(){
        if (sInstance == null){

            // 懒汉式单例一定要加锁
            synchronized (SubscritptionPresenter.class){
                if (sInstance == null){

                    // 懒汉式单例不到万不得已不实例化自己
                    sInstance = new SubscritptionPresenter();
                }
            }
        }

        return sInstance;
    }

    @Override
    public void addSubscription(final Album album) {

        // 判断当前的订阅数量不能超过100条
        if (mData.size() >= Constants.MAX_SUB_COUNT){

            // 给个提示
            for (ISubscriptionCallback callback : mCallbacks) {
              callback.onSubFull();
            }

            return;
        }

        // 子线程操作数据库
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                mSubscriptionDao.addAlbum(album);

            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {

        // 子线程操作数据库
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                mSubscriptionDao.delAlbum(album);

                Log.e("deleteSubscription: ", Thread.currentThread().getName());
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubcriptionList() {
        listSubscriptions();
    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());

        // 返回当前专辑是否已收藏/订阅 (不为空表示已经订阅)
        return result != null;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        // 判断如果回调集合中没有就添加到回调集合中
        if (!mCallbacks.contains(iSubscriptionCallback)){
            mCallbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (mCallbacks != null) {
            mCallbacks.remove(iSubscriptionCallback);
        }
    }

    @Override
    public void onAddResult(final boolean isSuccess) {

        listSubscriptions();

        // 添加结果的回调 - 回到主线程处理
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {

        listSubscriptions();

        // 删除订阅的回调-回到主线程处理
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {

        // 一定要先请空
        mData.clear();

        // 加载数据的回调
        for (Album album : result) {
            mData.put(album.getId(), album);
        }

        // 通知UI更新
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubscritpionsLoaded(result);
                }
            }
        });
    }


}
