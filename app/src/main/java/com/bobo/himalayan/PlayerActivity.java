package com.bobo.himalayan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bobo.himalayan.base.BaseActivity;
import com.bobo.himalayan.interfaces.IPlayerCallback;
import com.bobo.himalayan.presenters.PlayerPresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

/**
 * Created by 微信公众号IT波 on 2019/11/30. Copyright © Leon. All rights reserved.
 * Functions: 以后有空了做一个彩虹加载进度条
 */
public class PlayerActivity extends BaseActivity implements IPlayerCallback {

    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

        //初始化各个UI控件
        initView();

        //实例化点击等事件的监听
        initEvent();

        startPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mPlayerPresenter != null){
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }

    /**
     * 实例化各个UI控件
     */
    private void initView(){
        mControlBtn = findViewById(R.id.paly_or_pause_btn);

    }

    /**
     * 给控件设置相关的事件
     */
    private void initEvent(){
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果当前状态是正在播放，那么就暂停播放
                if (mPlayerPresenter.isPlay()) {
                    mPlayerPresenter.pause();
                }else {
                    //如果现在的状态是非播放的，那么我么就要播放
                    mPlayerPresenter.play();
                }
            }
        });
    }

    /**
     * 调用开始播放
     */
    private void startPlay(){
        if (mPlayerPresenter != null) {
            mPlayerPresenter.play();
        }
    }

    /**
     * 回调 播放了
     */
    @Override
    public void onPlayStart() {
        //开始播放，修改UI层暂停的按钮
        if (mControlBtn != null){
            mControlBtn.setImageResource(R.mipmap.stop_press);
        }
    }

    @Override
    public void onPlayPause() {
        //修改播放暂停按钮为播放按钮 回调有可能比控件初始化要早 做下非空判断
        if (mControlBtn != null){
            mControlBtn.setImageResource(R.mipmap.play_normal);
        }
    }

    @Override
    public void onPlayStop() {
        //修改播放暂停按钮为播放按钮 回调有可能比控件初始化要早 做下非空判断
        if (mControlBtn != null){
            mControlBtn.setImageResource(R.mipmap.play_normal);
        }
    }

    @Override
    public void onPlayerError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChage(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(long currentProgress, long total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdLoaded() {

    }
}


//测试
//PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
//playerPresenter.play();
