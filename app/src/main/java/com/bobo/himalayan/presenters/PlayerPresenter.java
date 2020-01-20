package com.bobo.himalayan.presenters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.bobo.himalayan.data.XimalayaApi;
import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.interfaces.IPlayerCallback;
import com.bobo.himalayan.interfaces.IPlayerPresenter;
import com.bobo.himalayan.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
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

    public static final int DEFAULT_PLAY_INDEX = 0;

    //用户选中第几集 从详情页跳转过来以及后来用户改变
    private int mCurrentIndex = DEFAULT_PLAY_INDEX;

    //列表是否 倒序排列 默认是false  FIXME:我改成为true
    private boolean mIsRveverse = true;

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

    // 当前播放进度成员变量默认为0
    private int mCurrentProgress = 0;

    // 小节时长的成员变量默认为0
    private int mProgressDuration = 0;

    /**
     * 单例标配 私有的构造方法
     */
    private PlayerPresenter() {
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
     *
     * @return
     */
    public static PlayerPresenter getPlayerPresenter() {

        if (sPlayerPresenter == null) {
            //synchronized 同步的锁
            synchronized (PlayerPresenter.class) {
                if (sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }

        return sPlayerPresenter;
    }

    /**
     * 用于 activity跳转时候传递参数
     *
     * @param list
     */
    public void setPlayList(List<Track> list, int playIndex) {

        if (mPlayerManager != null) {
            //播放声音
            mPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        } else {
            LogUtil.e(TAG, "mPlayerManager is null");
        }
    }

    @Override
    public void play() {
        if (isPlayListSet) {
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

    /**
     * 判断是否有播放节目列表
     *
     * @return 是/否
     */
    public boolean hasPlayList() {
        ///第一种判断方式可以使用
        //List<Track> playList = mPlayerManager.getPlayList();
        //return playList == null || playList.size() == 0;

        return isPlayListSet;
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
            edit.putInt(PLAY_MODE_KEY, getIntByPlayMode(mode));
            edit.commit();//提交保存到本地
        }
    }

    /**
     * 根据播放模式（枚举）返回对应的 int值 做本地持久化保存 ：枚举转int
     *
     * @param mode
     * @return
     */
    private int getIntByPlayMode(XmPlayListControl.PlayMode mode) {
        switch (mode) {
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
     *
     * @param index
     * @return
     */
    private XmPlayListControl.PlayMode getModeByInt(int index) {
        switch (index) {
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
    public boolean isPlaying() {
        //返回播放器是否正在播放
        return mPlayerManager.isPlaying();
    }

    @Override
    public void reversePlayList() {

        //把播放器列表（排序）翻转
        List<Track> playList = mPlayerManager.getPlayList();

        //将集合（排序）翻转
        Collections.reverse(playList);

        //是否倒序 状态取反切换
        mIsRveverse = !mIsRveverse;


        /**
         * 第一个参数：将翻转过的集合重新设置给 PlayerManager ，第二个参数：是开始播放的索引
         *
         * 注意翻转之后下标（索引）变化如下图：
         *  0 1 2 3 4 5 6
         *    *
         *
         *  6 5 4 3 2 1 0
         *            *
         *
         *  翻转后新下标 = 总的内容个数 -1 - 当前的下标
         */
        mCurrentIndex = playList.size() - 1 - mCurrentIndex;
        mPlayerManager.setPlayList(playList, mCurrentIndex);

        //更新UI
        mCurrentTrack = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdated(mCurrentTrack, mCurrentIndex);
            iPlayerCallback.updateListOrder(mIsRveverse);
        }

    }

    /**
     * 根据id播放专辑列表中第一个专辑
     *
     * @param id
     */
    @Override
    public void playByAlbumId(long id) {
        // 1.要获取到专辑的列表内容
        XimalayaApi ximalayaApi = XimalayaApi.getXimalayaApi();
        ximalayaApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                // 2.把内容设置给播放器
                List<Track> tracks = trackList.getTracks();

                if (tracks != null && tracks.size() > 0) {
                    mPlayerManager.setPlayList(tracks, DEFAULT_PLAY_INDEX);
                    isPlayListSet = true;
                    mCurrentTrack = tracks.get(DEFAULT_PLAY_INDEX);
                    mCurrentIndex = DEFAULT_PLAY_INDEX;
                }
            }

            @Override
            public void onError(int errorCode, String message) {

                Log.e(TAG, "errorCode-->" + errorCode + " message-->" + message);
                Toast.makeText(BaseApplication.getAppContext(), "请求失败" + message, Toast.LENGTH_SHORT).show();
            }
        }, (int) id, 1); // ←FIXME:注意这里请求页要从1开始 不是从0开始

    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {

        // 通知当前的节目
        iPlayerCallback.onTrackUpdated(mCurrentTrack, mCurrentIndex);
        iPlayerCallback.onProgressChange(mCurrentProgress, mProgressDuration);

        // 更新状态
        handlePlayState(iPlayerCallback);

        //从sp里头拿 上次保存的播放模式(int类型需要转换) 获取不到为0即默认为0
        int modelIn = mPlayModeSp.getInt(PLAY_MODE_KEY, 0);
        mCurrentPlayMode = getModeByInt(modelIn);//int 转 枚举

        //进入页面就回调一次 设置ui层播放模式
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);

        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
    }

    /**
     * 更新播放状态的方法
     *
     * @param iPlayerCallback
     */
    private void handlePlayState(IPlayerCallback iPlayerCallback) {
        int playerStatus = mPlayerManager.getPlayerStatus();

        // 根据状态调用接口的方法
        if (PlayerConstants.STATE_STARTED == playerStatus) {
            iPlayerCallback.onPlayStart();
        } else {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
        if (mIPlayerCallbacks != null) {
            mIPlayerCallbacks.remove(iPlayerCallback);
        }
    }


    ///////////////////////////////以下为广告相关的内容/////////////////////////////////

    @Override
    public void onStartGetAdsInfo() {
        Log.e(TAG, "onStartGetAdsInfo..");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        Log.e(TAG, "onGetAdsInfo..");
    }

    @Override
    public void onAdsStartBuffering() {
        Log.e(TAG, "onAdsStartBuffering..");
    }

    @Override
    public void onAdsStopBuffering() {
        Log.e(TAG, "onAdsStopBuffering..");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        Log.e(TAG, "onStartPlayAds..");
    }

    @Override
    public void onCompletePlayAds() {
        Log.e(TAG, "onCompletePlayAds..");
    }

    @Override
    public void onError(int what, int extra) {
        Log.e(TAG, "onError.." + " what:" + what + " extra:" + extra);
    }

    ///////////////////////////////以上为广告相关的内容/////////////////////////////////

    ///////////////////////////////以下是播放器相关的内容////////////////////////////////

    @Override
    public void onPlayStart() {
        Log.e(TAG, "onPlayStart...");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        Log.e(TAG, "onPlayPause");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        Log.e(TAG, "onPlayStop");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        Log.e(TAG, "onSoundPlayComplete");
    }

    @Override
    public void onSoundPrepared() {
        Log.e(TAG, "onSoundPrepared");

        //设置播放模式为最新的播放模式
        mPlayerManager.setPlayMode(mCurrentPlayMode);

        //判断 播放器准备好了 可以去播放了
        if (mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            //开始播放
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastMode, PlayableModel curModel) {
        Log.e(TAG, "onSoundSwitch");

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
        if (curModel instanceof Track) {
            Track currentTrack = (Track) curModel;
            mCurrentTrack = currentTrack;
            //更新UI
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onTrackUpdated(mCurrentTrack, mCurrentIndex);
            }
        }
    }

    @Override
    public void onBufferingStart() {
        Log.e(TAG, "onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        Log.e(TAG, "onBufferingStop...");
    }

    @Override
    public void onBufferProgress(int progress) {
        Log.e(TAG, "onBufferProgress缓冲进度..." + progress);
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {

        this.mCurrentProgress = currPos;
        this.mProgressDuration = duration;

        //Log.e(TAG,"onPlayProgress..."+currPos+"---->"+duration);
        //单位是毫秒
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currPos, duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        Log.e(TAG, "onError:" + e.toString());
        return false;
    }


    ///////////////////////////////以上是播放器相关的内容////////////////////////////////

}



