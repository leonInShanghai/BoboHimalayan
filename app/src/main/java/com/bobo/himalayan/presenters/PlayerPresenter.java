package com.bobo.himalayan.presenters;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;


/**
 * Created by 微信公众号IT波 on 2019/12/1. Copyright © Leon. All rights reserved.
 * Functions: 播放activity的 中介
 * 代理 setProxyNew null 喜马拉雅官方说这个可以忽略
 */
public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private List<IPlayerCallback> mIPlayerCallbacks = new ArrayList<>();

    private static final String TAG = "PlayerPresenter";
    private static PlayerPresenter sPlayerPresenter = null;
    private final XmPlayerManager mPlayerManager;

    private boolean isPlayListSet = false;
    private Track mCurrentTrack;


    //用户选中第几集 从详情页跳转过来以及后来用户改变
    private int mCurrentIndex = 0;

    //本地持久化保存数据
    private final SharedPreferences mPlayModeSp;

    //当前播放 模式
    private XmPlayListControl.PlayMode mCurrentPlayMode = PLAY_MODEL_LIST;

    //定义下面的常量是为了将枚举保存到本地
    public static final int PLAY_MODEL_LIST_INT = 0;
    public static final int PLAY_MODEL_LIST_LOOP_INT = 1;
    public static final int PLAY_MODEL_RANDOM_INT = 2;
    public static final int PLAY_MODEL_SINGLE_LOOP_INT = 3;

    //SharedPreferences 的name
    public static final String PLAY_MODE_SP_NAME = "PlayMode";
    //SharedPreferences 存取播放模型的key
    public static final String PLAY_MODE_KEY = "currentPlayMode";

    /**
     * 单例标配 私有的构造方法
     */
    private PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关的接口（注意广告有时候有有时候没有）
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器相关的接口
        mPlayerManager.addPlayerStatusListener(this);

        //需要本地持久化保存记录当前的播放模式
        mPlayModeSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME,
                Context.MODE_PRIVATE);
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
    public void setPlayList(List<Track> list,int playIndex){

        if (mPlayerManager != null){
            //播放声音
            mPlayerManager.setPlayList(list,playIndex);
            isPlayListSet = true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
            //FIXME:修正空指针异常 原来没有这句
            //mPlayList = list;
        }else{
            LogUtil.e(TAG,"mPlayerManager is null");
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
        //播放上一首
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        //播放下一首
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    @Override
    public void swithPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {

            mCurrentPlayMode = mode;

            mPlayerManager.setPlayMode(mode);

            //通知ui更新播放模式
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onPlayModeChange(mode);
            }
            //进入编辑模式
            SharedPreferences.Editor edit = mPlayModeSp.edit();
            edit.putInt(PLAY_MODE_KEY,getIntByPlayMode(mode));
            edit.commit();//提交保存到本地
        }
    }

    /**
     * 根据播放模式（枚举）返回对应的 int值 做本地持久化保存 ：枚举转int
     * @param mode
     * @return
     */
    private int getIntByPlayMode(XmPlayListControl.PlayMode mode){
        switch (mode){
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODEL_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODEL_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODEL_RANDOM_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODEL_LIST_INT;
        }
        return PLAY_MODEL_LIST_INT;
    }

    /**
     * 根据 int值 返回 对应的 放模式（枚举） 即： int 转 枚举
     * @param index
     * @return
     */
    private XmPlayListControl.PlayMode getModeByInt(int index){
        switch (index){
            case PLAY_MODEL_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODEL_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODEL_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODEL_LIST_INT:
                return PLAY_MODEL_LIST;
        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            //原来的代码
            List<Track> playList = mPlayerManager.getPlayList();
            //FIXME:修正空指针异常
            //List<Track> playList = mPlayerManager.getPlayList();
            //if (playList != null){
            //    mPlayList = mPlayerManager.getPlayList();
            //}
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                     iPlayerCallback.onListLoaded(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        //切换播放器播放 index 集 的内容
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
    }

    @Override
    public void seekTo(int progress) {
        if (mPlayerManager != null) {
            //更新播放器的进度
            mPlayerManager.seekTo(progress);
        }
    }

    @Override
    public boolean isPlay() {
        //返回播放器是否正在播放
        return  mPlayerManager.isPlaying();
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        iPlayerCallback.onTrackUpdated(mCurrentTrack,mCurrentIndex);

        //从sp里头拿 上次保存的播放模式(int类型需要转换) 获取不到为0即默认为0
        int modelIn = mPlayModeSp.getInt(PLAY_MODE_KEY, 0);
        mCurrentPlayMode = getModeByInt(modelIn);//int 转 枚举

        //进入页面就回调一次 设置ui层播放模式
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);

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

        //设置播放模式为最新的播放模式
        mPlayerManager.setPlayMode(mCurrentPlayMode);

        //判断 播放器准备好了 可以去播放了
        if (mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED){
            //开始播放
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastMode, PlayableModel curModel) {
        Log.e(TAG,"onSoundSwitch");

        /**
         * curModel代表的是当前播放的内容 通过getKind()方法来获取它是什么类型的
         * track 表示的是 track类型
         */
        //第一种写法不推荐
        //if (curModel != null && curModel.getKind().equals("track")){
        //    Track currentTrack = (Track)curModel;
        //    Log.e(TAG,currentTrack.getTrackTitle());
        //}

        //当前的节目（集）改变以后，要修改页面中间的封面图片
        mCurrentIndex = mPlayerManager.getCurrentIndex();

        //第二种写法
        if (curModel instanceof Track){
            Track currentTrack = (Track)curModel;
            mCurrentTrack = currentTrack;
            //更新UI
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onTrackUpdated(mCurrentTrack,mCurrentIndex);
            }
        }
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



