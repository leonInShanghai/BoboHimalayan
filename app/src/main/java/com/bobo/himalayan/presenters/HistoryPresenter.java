package com.bobo.himalayan.presenters;

import android.util.Log;

import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.data.HistoryDao;
import com.bobo.himalayan.data.IHistoryDaoCallback;
import com.bobo.himalayan.interfaces.IHistoryCallback;
import com.bobo.himalayan.interfaces.IHistoryPresenter;
import com.bobo.himalayan.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by Leon on 2020-02-02 Copyright © Leon. All rights reserved.
 * Functions: 历史页面的的中介  浏览历史最多保存100条，如果超过100条就删除最前面添加的数据，再把当前的历史添加进去
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {


    private List<IHistoryCallback> mCallbacks = new ArrayList<>();

    // 单例对象（this）
    private static HistoryPresenter sHistoryPresenter = null;

    // 数据库操作类
    private final HistoryDao mHistoryDao;

    // 历史数据对象集合
    private List<Track> mCurrentHistories = null;

    // 是否超出历史记录存储数量 超出后要先删除再添加
    private boolean isDoDelAsOutOfSize = false;
    
    // 超出历史记录存储数量时 先删除再添加的对象
    private Track mCurrentAddTrack = null; 

    /**
     * 单例标配私有化构造方法
     */
    private HistoryPresenter(){
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistories();
    }

    public static HistoryPresenter getHistoryPresenter(){
        if (sHistoryPresenter == null){
            synchronized (HistoryPresenter.class){
                if (sHistoryPresenter == null){
                    sHistoryPresenter = new HistoryPresenter();
                }
            }
        }

        return sHistoryPresenter;
    }

    @Override
    public void listHistories() {
        // 子线程操作数据库
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                mHistoryDao.listHistories();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }


    @Override
    public void addHistory(final Track track) {

        // 先判断是否已经保存100条数据在数据库历史表了
        if (mCurrentHistories != null && mCurrentHistories.size() >= Constants.MAX_HISTORY_COUNT){

            // 是否超出历史记录存储数量
            isDoDelAsOutOfSize = true;

            // 类的成员变量待删除成功后添加这个对象到本地数据库历史表
            this.mCurrentAddTrack =track;

            // 数据超过了100 条先删除一条历史 再添加一条新的历史
            delHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));

        }else{
            // 添加一条历史数据
            doAddHistory(track);
        }
    }

    // 添加一条历史数据
    private void doAddHistory(final Track track) {
        // 子线程操作数据库
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                mHistoryDao.addHistory(track);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        // 子线程操作数据库
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                mHistoryDao.delHistory(track);
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistories() {
        // 子线程操作数据库
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                mHistoryDao.clearHistory();
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        // ui层注册过来 回调接口
        if (!mCallbacks.contains(iHistoryCallback)){
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {

        // 删除UI的回调接口
        if (mCallbacks != null){
            mCallbacks.remove(iHistoryCallback);
        }
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        listHistories();
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {

        // 是否超出历史记录存储数量 超出后要先删除，删除成功后再添加
        if (isDoDelAsOutOfSize && mCurrentAddTrack != null){

            // 是否超出历史记录存储数量置为false
            isDoDelAsOutOfSize = false;

            // 添加新数据进入数据库
            addHistory(mCurrentAddTrack);
        }else{
            // 更新数据
            listHistories();
        }
    }

    @Override
    public void onHistoriesLoaded(final List<Track> tracks) {

        this.mCurrentHistories = tracks;

        Log.e("HistoriesLoaded", tracks.size() + "");

        // 通知ui更新数据
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                // 在主线程通知ui更新
                for (IHistoryCallback callback : mCallbacks) {
                    callback.onHistoriesLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClean(boolean isSuccess) {
        listHistories();
    }
}
