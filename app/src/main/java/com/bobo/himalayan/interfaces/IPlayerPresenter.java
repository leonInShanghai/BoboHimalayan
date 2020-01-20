package com.bobo.himalayan.interfaces;

import com.bobo.himalayan.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

/**
 * Created by 微信公众号IT波 on 2019/12/1. Copyright © Leon. All rights reserved.
 * Functions: 播放器界面的 presenter接口
 */
public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {

    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 上一首
     */
    void playPre();

    /**
     * 下一首
     */
    void playNext();

    /**
     * 切换播放模式
     */
    void swithPlayMode(XmPlayListControl.PlayMode mode);

    /**
     * 获取播放列表
     */
    void getPlayList();

    /**
     * 根据节目的位置进行播放
     *
     * @param index _节目在列表中的位置
     */
    void playByIndex(int index);

    /**
     * 切换播放进度
     *
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 判断播放器是否在播放
     *
     * @return
     */
    boolean isPlaying();

    /**
     * 把播放器列表内容翻转
     */
    void reversePlayList();

    /**
     * 播放专辑列表中第一个专辑
     *
     * @param id
     */
    void playByAlbumId(long id);

}
