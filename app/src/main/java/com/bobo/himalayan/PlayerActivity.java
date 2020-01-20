package com.bobo.himalayan;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bobo.himalayan.adapters.PlayerTrackPagerAdapter;
import com.bobo.himalayan.base.BaseActivity;
import com.bobo.himalayan.interfaces.IPlayerCallback;
import com.bobo.himalayan.presenters.PlayerPresenter;
import com.bobo.himalayan.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

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

    //左下角切换播放模式的按钮
    private ImageView mPlayModeSwitchBtn;

    //右下角点击显示播放列表的按钮
    private ImageView mPalayListBtn;

    //右下角点击显示播放列表的按钮被点击后显示的那个播放列表
    private SobPopWindow mSobPopWindow;

    //弹出 SobPopWindow播放列表时的属性动画
    private ValueAnimator mEnterBgAnimator;

    //SobPopWindow Dismiss时 播放列表时的属性动画
    private ValueAnimator mOutBgAnimator;

    public final int BG_ANIMATION_DURATION = 500;

    //当前播放的模式 - 默认为列表播放
    private XmPlayListControl.PlayMode mCurrentMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;

    //存放播放模型的键值对
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();

    /**
     * 处理播放模式的切换
     * 1.默认是：PLAY_MODEL_LIST
     * 2.列表循环播放：PLAY_MODEL_LIST_LOOP
     * 3.随机播放：PLAY_MODEL_RANDOM
     * 4.单曲循环：PLAY_MODEL_SINGLE_LOOP
     */
    static {
        sPlayModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        //初始化各个UI控件放在最前面减少空指针
        initView();

        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

        //获取详情列表 - 这个方法一定要放在 initView()后面
        mPlayerPresenter.getPlayList();

        //实例化点击等事件的监听
        initEvent();

        //初始化动画
        initBganimation();
    }

    //初始化动画
    private void initBganimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f, 0.7f);
        mEnterBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();

                //处理一下背景，有点透明度，俗称加个蒙版
                updateBgAlpha(value);
            }
        });

        //退出的属性动画
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f, 1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                float value = (float) animation.getAnimatedValue();

                //处理一下背景，有点透明度，俗称加个蒙版
                updateBgAlpha(value);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }

    /**
     * 实例化各个UI控件
     */
    private void initView() {
        mControlBtn = findViewById(R.id.paly_or_pause_btn);
        mTotalDuration = findViewById(R.id.track_duration);
        mCurrentPosition = findViewById(R.id.current_position);
        mDurationBar = findViewById(R.id.track_seek_bar);
        mPlayNextBtn = findViewById(R.id.play_next);
        mPlayPreBtn = findViewById(R.id.play_pre);
        mTrackTitleTv = findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitleTv.setText(mTrackTitleText);
        }
        mTrackPageView = findViewById(R.id.track_pager_view);
        //给viewpeger创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTrackPageView.setAdapter(mTrackPagerAdapter);
        //切换播放模式的按钮
        mPlayModeSwitchBtn = findViewById(R.id.player_mode_swith_btn);
        //右下角点击显示播放列表的按钮
        mPalayListBtn = findViewById(R.id.player_list);
        //右下角点击显示播放列表的按钮被点击后显示的那个播放列表
        mSobPopWindow = new SobPopWindow();
    }

    /**
     * 给控件设置相关的事件
     */
    private void initEvent() {
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果当前状态是正在播放，那么就暂停播放
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                } else {

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
                if (isFromUser) {
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
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        //是用户触摸了view pager  区分 setCurrentItem 默认为false
                        mIsUserSlidePage = true;
                        break;
                }
                return false;
            }
        });

        //切换播放模式的按钮-点击事件的处理
        mPlayModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchPlayMode();
            }
        });

        //右下角点击显示播放列表的按钮-点击时间的监听处理
        mPalayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示播放列表（PopuWindoew）
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                //处理一下背景，有点透明度，俗称加个蒙版
                //updateBgAlpha(0.8f);  //←移动到属性动画中
                //修改背景的透明度有一个渐变的过程
                mEnterBgAnimator.start();
            }
        });

        //SobPopWindow dissmiss 的时候会回调这个方法
        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                //pop窗体消失恢复透明度
                //updateBgAlpha(1.0f);
                mOutBgAnimator.start();
            }
        });

        mSobPopWindow.setPlayListItemClickListener(new SobPopWindow.PlayListItemClickListener() {

            /**
             * SobPopWindow 弹窗中 RecyclerView被点击了
             * @param position 索引
             */
            @Override
            public void itemClick(int position) {
                mPlayerPresenter.playByIndex(position);
            }
        });

        mSobPopWindow.setPlayListActionClickListener(new SobPopWindow.PlayListActionClickListener() {
            @Override
            public void onPlayModeClick() {
                //切换播放模式
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                //点击了 顺序/逆序
                //Toast.makeText(PlayerActivity.this,"切换列表顺序",Toast.LENGTH_SHORT).show();
                mPlayerPresenter.reversePlayList();
            }
        });
    }

    //切换播放模式
    private void switchPlayMode() {

        //播放模式的切换: 根据当前的播放model 获取并 切换到下一个播放 model
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);

        //修改播放模式
        mPlayerPresenter.swithPlayMode(playMode);
    }

    /**
     * 设置Window 透明度的方法，即加蒙版
     *
     * @param alpha
     */
    public void updateBgAlpha(float alpha) {
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    /**
     * 更新播放模式的图片
     */
    private void updatePlayModeBtnImg() {

        int resId = R.drawable.selector_palyer_mode_list_order;

        switch (mCurrentMode) {
            case PLAY_MODEL_LIST://列表模式
                //下面这一句写不写都可以 默认就是这个值
                //resId = R.drawable.selector_palyer_mode_list_order;
                break;

            case PLAY_MODEL_RANDOM://随机模式
                resId = R.drawable.selector_palyer_mode_random;
                break;

            case PLAY_MODEL_LIST_LOOP://循环模式
                resId = R.drawable.selector_palyer_mode_list_order_looper;
                break;

            case PLAY_MODEL_SINGLE_LOOP://单曲循环模式
                resId = R.drawable.selector_palyer_mode_single_loop;
                break;
        }
        mPlayModeSwitchBtn.setImageResource(resId);
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
        if (mControlBtn != null) {
            //mControlBtn.setImageResource(R.mipmap.stop_press);
            mControlBtn.setImageResource(R.drawable.selector_palyer_pause);
        }
    }

    @Override
    public void onPlayPause() {
        //修改播放暂停按钮为播放按钮 回调有可能比控件初始化要早 做下非空判断
        if (mControlBtn != null) {
            //mControlBtn.setImageResource(R.mipmap.play_normal);
            mControlBtn.setImageResource(R.drawable.selector_palyer_play);
        }
    }

    @Override
    public void onPlayStop() {
        //修改播放暂停按钮为播放按钮 回调有可能比控件初始化要早 做下非空判断
        if (mControlBtn != null) {
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
        mTrackPageView.setCurrentItem(mPageViewIndex, true);
        //}

        //数据请求回来也要给，右下角的播放类别弹窗一份
        mSobPopWindow.setListData(list);
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式
        mCurrentMode = playMode;

        //修改ui（本页面）
        updatePlayModeBtnImg();
        //更新SobPopWindow里的播放模式
        mSobPopWindow.updatePlayMode(mCurrentMode);
    }

    @Override
    public void onProgressChange(int currentDuration, int total) {
        mDurationBar.setMax(total);

        //更新播放进度-更新进度条
        String totalDuration;//总时间
        String currentPosition;//当前时间

        if (total > 1000 * 60 * 60) {//大于一小时
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentDuration);
        } else {
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
        if (!mIsUserTouchProgressBar) {
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
     *
     * @param track 每集节目的bean对象
     */
    @Override
    public void onTrackUpdated(Track track, int playIndex) {

        if (track == null) {
            Log.e(TAG, "onTrackUpdated 空指针：track对象");
            return;
        }

        mTrackTitleText = track.getTrackTitle();

        //FIXME:修正viewpager位置不对的bug
        mPageViewIndex = playIndex;

        if (mTrackTitleTv != null) {
            mTrackTitleTv.setText(mTrackTitleText == null ? "" : mTrackTitleText);
        }

        if (mTrackPageView != null) {
            //当节目改变的时候，我们就获取到当前播放节目的position 第二个参数是动画
            mTrackPageView.setCurrentItem(playIndex, true);
        }

        //设置播放列表当前播放的位置
        mSobPopWindow.setCurrentPlayPosition(playIndex);
    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mSobPopWindow.updateOrderIcon(isReverse);
    }

    ///////////////////////中间展示封面内容的viewpager 划动的事件的监听↓//////////////////////////////
    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int position) {

        if (mIsUserSlidePage) {
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
