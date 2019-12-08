package com.bobo.himalayan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bobo.himalayan.adapters.PlayerTrackPagerAdapter;
import com.bobo.himalayan.base.BaseActivity;
import com.bobo.himalayan.interfaces.IPlayerCallback;
import com.bobo.himalayan.presenters.PlayerPresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by 微信公众号IT波 on 2019/11/30. Copyright © Leon. All rights reserved.
 * Functions: 以后有空了做一个彩虹加载进度条
 */
public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private static final String TAG = "PlayerActivity";

    //控制播放和暂停的按钮
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;

    //分秒格式（用作总时长和当前播放进度）
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");

    //时分秒格式（用作总时长和当前播放进度）hh : 代表时(区分大小写,大写为24进制计时,小写为12进制计时)
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("HH：mm:ss");

    //显示总时间
    private TextView mTotalDuration;
    //当前播放时间
    private TextView mCurrentPosition;

    //显示 进度的 播放进度条
    private SeekBar mDurationBar;

    //当前进度 默认为0
    private int mCurrentProgress = 0;

    //是否是用户触摸了 seekbar 默认是false
    private boolean mIsUserTouchProgressBar = false;

    //播放下一首（集）的按钮
    private ImageView mPlayNextBtn;
    //播放上一首（集）的按钮
    private ImageView mPlayPreBtn;

    //上面的标题
    private TextView mTrackTitleTv;

    //页面跳转时传递过来的标题
    private String mTrackTitleText;

    //中间显示每集封面图片的viewpager
    private ViewPager mTrackPageView;
    //中间显示每集封面图片的viewpager 的适配器
    private PlayerTrackPagerAdapter mTrackPagerAdapter;

    //是否是用户划动了view pager 区分 setCurrentItem 默认为false
    private boolean mIsUserSlidePage = false;

    //FIXME:修正viewpager位置不对的bug
    private int mPageViewIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

        //初始化各个UI控件
        initView();

        //获取详情列表 - 这个方法一定要放在 initView()后面
        mPlayerPresenter.getPlayList();

        //实例化点击等事件的监听
        initEvent();

        //startPlay();
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
        mTotalDuration = findViewById(R.id.track_duration);
        mCurrentPosition = findViewById(R.id.current_position);
        mDurationBar = findViewById(R.id.track_seek_bar);
        mPlayNextBtn = findViewById(R.id.play_next);
        mPlayPreBtn = findViewById(R.id.play_pre);
        mTrackTitleTv = findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(mTrackTitleText)){
            mTrackTitleTv.setText(mTrackTitleText);
        }
        mTrackPageView = findViewById(R.id.track_pager_view);
        //给viewpeger创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTrackPageView.setAdapter(mTrackPagerAdapter);
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

        //进度条点击事件的监听
        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {

                //判断是否是用户（拖到）改变了进度条
                if (isFromUser){
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //用户的手触摸到了进度条 调用这个方法
                mIsUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //用户的手离开的时候调用这个方法 - 在此时更新进度比较合适
                mIsUserTouchProgressBar = false;

                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });

        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放前一个节目
                mPlayerPresenter.playPre();
            }
        });

        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放下一个节目
                mPlayerPresenter.playNext();
            }
        });

        //监听viewpage页面的改变 内容也随之改变
        mTrackPageView.addOnPageChangeListener(this);

        //viewpager的触摸事件监听和处理
        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action){
                  case  MotionEvent.ACTION_DOWN:
                      //是用户触摸了view pager  区分 setCurrentItem 默认为false
                      mIsUserSlidePage = true;
                        break;
                }
                return false;
            }
        });
    }

//    /**
//     * 调用开始播放
//     */
//    private void startPlay(){
//        if (mPlayerPresenter != null) {
//            mPlayerPresenter.play();
//        }
//    }

    /**
     * 回调 播放了
     */
    @Override
    public void onPlayStart() {
        //开始播放，修改UI层暂停的按钮
        if (mControlBtn != null){
            //mControlBtn.setImageResource(R.mipmap.stop_press);
            mControlBtn.setImageResource(R.drawable.selector_palyer_stop);
        }
    }

    @Override
    public void onPlayPause() {
        //修改播放暂停按钮为播放按钮 回调有可能比控件初始化要早 做下非空判断
        if (mControlBtn != null){
            //mControlBtn.setImageResource(R.mipmap.play_normal);
            mControlBtn.setImageResource(R.drawable.selector_palyer_play);
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
        //Log.e(TAG,list.toString());
        //if (mTrackPagerAdapter != null) {
            //把数据设置到中间展示每一集封面的适配器中
            mTrackPagerAdapter.setData(list);
            //FIXME:修正viewpager位置不对的bug
            mTrackPageView.setCurrentItem(mPageViewIndex,true);
        //}
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentDuration, int total) {
        mDurationBar.setMax(total);

        //更新播放进度-更新进度条
        String totalDuration;//总时间
        String currentPosition;//当前时间

        if (total > 1000 * 60 * 60){//大于一小时
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentDuration);
        }else{
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentDuration);
        }

        //设置总时长
        mTotalDuration.setText(totalDuration);

        //更新当前时间
        mCurrentPosition.setText(currentPosition);

        /**
         * 更新进度
         * 计算公式  （float类型）当前的进度  / 总时长 * 100
         */
        if (!mIsUserTouchProgressBar){
            //int percent = (int)(currentDuration * 1.0f / total * 100);
            //mDurationBar.setProgress(percent);
            mDurationBar.setProgress(currentDuration);
        }
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdLoaded() {

    }

    /**
     * 更新 当前节目的标题 封面等
     * @param track 每集节目的bean对象
     */
    @Override
    public void onTrackUpdated(Track track,int playIndex) {

        mTrackTitleText = track.getTrackTitle();

        //FIXME:修正viewpager位置不对的bug
        mPageViewIndex = playIndex;

        if (mTrackTitleTv != null){
            mTrackTitleTv.setText(mTrackTitleText == null ? "" : mTrackTitleText);
        }

        if (mTrackPageView != null) {
            //当节目改变的时候，我们就获取到当前播放节目的position 第二个参数是动画
            mTrackPageView.setCurrentItem(playIndex,true);
        }
    }

    ///////////////////////中间展示封面内容的viewpager 划动的事件的监听↓//////////////////////////////
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {

        if (mIsUserSlidePage){
            //当页面选中的时候调用这个方法-此时就去切换播放的内容
            mPlayerPresenter.playByIndex(position);
        }

        //是setCurrentItem 滚动了view pager 不是用户划动view pager
        mIsUserSlidePage = false;
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }
    ///////////////////////中间展示封面内容的viewpager 划动的事件的监听↑//////////////////////////////
}


//测试
//PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
//playerPresenter.play();
