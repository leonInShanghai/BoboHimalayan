package com.bobo.himalayan.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

/**
 * Created by 微信公众号IT波 on 2019/12/1. Copyright © Leon. All rights reserved.
 * Functions:
 */
public interface IPlayerCallback {

    /**
     * 开始播放
     */
    void onPlayStart();

    /**
     * 播放暂停
     */
    void onPlayPause();

    /**
     * 播放停止
     */
    void onPlayStop();

    /**
     * 播放错误
     */
    void onPlayerError();

    /**
     * 下一首播放
     */
    void nextPlay(Track track);

    /**
     * 上一首播放
     */
    void onPrePlay(Track track);

    /**
     * 播放列表数据加载完成
     * @param list 播放器列表数据
     */
    void onListLoaded(List<Track> list);

    /**
     * 播放器模式改变了
     */
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);

    /**
     * 进度条的改变
     * @param currentProgress 当前的进度
     * @param total 总的进度
     */
    void onProgressChange(long currentProgress,long total);

    /**
     * 广告正在加载
     */
    void onAdLoading();

    /**
     * 广告结束
     */
    void onAdLoaded();
}
