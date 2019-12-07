package com.bobo.himalayan.presenters;

import android.util.Log;

import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.interfaces.IPlayerCallback;
import com.bobo.himalayan.interfaces.IPlayerPresenter;
import com.bobo.himalayan.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by 微信公众号IT波 on 2019/12/1. Copyright © Leon. All rights reserved.
 * Functions: 播放activity的 中介
 *  有时间做个彩虹加载进度条   代理 setProxyNew null
 */
public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private List<IPlayerCallback> mIPlayerCallbacks = new ArrayList<>();

    private static final String TAG = "PlayerPresenter";
    private static PlayerPresenter sPlayerPresenter = null;
    private final XmPlayerManager mPlayerManager;

    private boolean isPlayListSet = false;

    /**
     * 单例标配 私有的构造方法
     */
    private PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getContext());
        //广告相关的接口（注意广告有时候有有时候没有）
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器相关的接口
        mPlayerManager.addPlayerStatusListener(this);
    }

    /**
     * 懒汉式单例  不到万不得已 不实例化自己  有线程安全风险 必须加锁
     * @return
     */
    public static PlayerPresenter getPlayerPresenter() {

        if (sPlayerPresenter == null){
            //synchronized 同步的锁
            synchronized (PlayerPresenter.class){
                if (sPlayerPresenter == null){
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }

        return sPlayerPresenter;
    }

    /**
     * 用于 activity跳转时候传递参数
     * @param list
     */
    public void setPlayList(List<Track> list,int palyIndex){
        isPlayListSet = true;
        if (mPlayerManager != null){
            //播放声音
            mPlayerManager.setPlayList(list,palyIndex);
        }else{
            LogUtil.d(TAG,"mPlayerManager is null");
        }
    }
    
    @Override
    public void play() {
        if (isPlayListSet){
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {

    }

    @Override
    public void playNext() {

    }

    @Override
    public void swithPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {

    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTo(int progress) {

    }

    @Override
    public boolean isPlay() {
        //返回播放器是否正在播放
        return  mPlayerManager.isPlaying();
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback){
        if (mIPlayerCallbacks != null){
            mIPlayerCallbacks.remove(iPlayerCallback);
        }
    }


    ///////////////////////////////以下为广告相关的内容/////////////////////////////////

    @Override
    public void onStartGetAdsInfo() {
        Log.e(TAG,"onStartGetAdsInfo..");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        Log.e(TAG,"onGetAdsInfo..");
    }

    @Override
    public void onAdsStartBuffering() {
        Log.e(TAG,"onAdsStartBuffering..");
    }

    @Override
    public void onAdsStopBuffering() {
        Log.e(TAG,"onAdsStopBuffering..");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        Log.e(TAG,"onStartPlayAds..");
    }

    @Override
    public void onCompletePlayAds() {
        Log.e(TAG,"onCompletePlayAds..");
    }

    @Override
    public void onError(int what, int extra) {
        Log.e(TAG,"onError.."+" what:"+what+" extra:"+extra);
    }

    ///////////////////////////////以上为广告相关的内容/////////////////////////////////

    ///////////////////////////////以下是播放器相关的内容////////////////////////////////

    @Override
    public void onPlayStart() {
        Log.e(TAG,"onPlayStart...");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        Log.e(TAG,"onPlayPause");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        Log.e(TAG,"onPlayStop");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        Log.e(TAG,"onSoundPlayComplete");
    }

    @Override
    public void onSoundPrepared() {
        Log.e(TAG,"onSoundPrepared");
    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
        Log.e(TAG,"onSoundSwitch");
    }

    @Override
    public void onBufferingStart() {
        Log.e(TAG,"onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        Log.e(TAG,"onBufferingStop...");
    }

    @Override
    public void onBufferProgress(int progress) {
        Log.e(TAG,"onBufferProgress缓冲进度..."+progress);
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        //Log.e(TAG,"onPlayProgress..."+currPos+"---->"+duration);
        //单位是毫秒
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currPos,duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        Log.e(TAG,"onError:"+e.toString());
        return false;
    }

    ///////////////////////////////以上是播放器相关的内容////////////////////////////////

}


/**
 *每次加载10条数据
 *到达10条上啦，显示正在加载
 *湛江-小团团 14:17:30
 *湛江-小团团 14:17:34
 * 显示到底
 */
