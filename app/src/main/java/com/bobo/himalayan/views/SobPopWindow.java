package com.bobo.himalayan.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bobo.himalayan.adapters.PlayListAdapter;
import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;


/**
 * Created by 微信公众号IT波 on 2019/12/14. Copyright © Leon. All rights reserved.
 * Functions: 播放页右下角 的popwindow
 * <p>
 * 今天个伙伴们吃烧烤认识了一种新的的啤酒： 新疆夺命大乌苏啤酒，究竟厉害到什么程度？你敢喝吗 😄
 */
public class SobPopWindow extends PopupWindow {

    //这个 mPopView 其实就是通过xml文件实例化出来的自己
    private final View mPopView;

    //底部的 "关闭"
    private TextView mCloseBtn;

    //中间显示内容的RecyclerView
    private RecyclerView mTrackList;

    //中间RecycleView的适配器
    private PlayListAdapter mPlayListAdapter;

    //播放模式文本显示
    private TextView mPlayModeTv;
    //播放模式图片显示
    private ImageView mPlaymodeIv;
    //播放模式容器包裹了 mPlayModeTv 和 mPlaymodeIv
    private LinearLayout mPlayMoeContainer;

    //mPlayMoeContainer 播放模式 正序/倒序 被点击接口
    private PlayListActionClickListener mPlayModeClickListener = null;

    //正序/倒序的容器
    private LinearLayout mOrderBtnContainer;
    //正序/倒序容器中的imageview
    private ImageView mOrderIcon;
    private TextView mOrderText;

    public SobPopWindow() {

        //设置宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        /**
         * 程序员拉大锯说setOutsideTouchable(true);之前先要设置 setBackgroundDrawable();，否则无法关闭
         *（我这不设置也能关闭）
         */
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //用户点击别的地方 SobPopWindow消失
        setOutsideTouchable(true);

        //加载进来view
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);

        //设置内容 这个方法和Activity里的方法有点像😄
        setContentView(mPopView);

        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.pop_animation);

        //初始化各个子控件
        initView();

        //初始化监听事件
        initEvent();
    }


    /**
     * 初始化各个子控件
     */
    private void initView() {

        //关闭按钮
        mCloseBtn = mPopView.findViewById(R.id.play_list_btn);

        //①RecycleView使用第一步实例化 即 先找到控件
        mTrackList = mPopView.findViewById(R.id.play_list_rv);

        //②设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTrackList.setLayoutManager(layoutManager);

        //③设置适配器
        mPlayListAdapter = new PlayListAdapter();
        mTrackList.setAdapter(mPlayListAdapter);

        //播放模式相关
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlaymodeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlayMoeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);

        //正序/倒序
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderIcon = mPopView.findViewById(R.id.play_list_order_iv);
        mOrderText = mPopView.findViewById(R.id.play_list_order_tv);
    }

    /**
     * 初始化监听事件
     */
    private void initEvent() {

        //点击关闭以后，窗口消失
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //用户点击了关闭 PopuWindow 消失

                dismiss(); // == SobPopWindow.this.dismiss();
            }
        });

        //播放模式的点击事件监听处理
        mPlayMoeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //切换播放模式
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onPlayModeClick();
                }
            }
        });

        //正序/倒序被点击了
        mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //切换播放类别的顺序 正序/倒序
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onOrderClick();
                }

            }
        });
    }

    /**
     * 设置RecycleView 的数据源
     *
     * @param data
     */
    public void setListData(List<Track> data) {

        mPlayListAdapter.setData(data);
    }

    /**
     * 设置RecyclerView中播放列表正在播放的位置
     */
    public void setCurrentPlayPosition(int position) {
        mPlayListAdapter.setCurrengPlayPosition(position);
        mTrackList.scrollToPosition(position);
    }


    /**
     * 供外界实现回调的RecyclerView item 点击事件的监听
     *
     * @param listener
     */
    public void setPlayListItemClickListener(PlayListItemClickListener listener) {

        mPlayListAdapter.setOnItemClickListener(listener);
    }

    /**
     * 更新播放模式
     *
     * @param currentMode 播放模式
     */
    public void updatePlayMode(XmPlayListControl.PlayMode currentMode) {

        updatePlayModeBtnImg(currentMode);
    }

    /**
     * 更新播放顺序的ui 正序 / 倒序
     *
     * @param isReverse 是否是正序
     */
    public void updateOrderIcon(boolean isReverse) {

        //根据正序倒序切换对应的drawable
        mOrderIcon.setImageResource(isReverse ? R.drawable.selector_palyer_mode_list_order :
                R.drawable.selector_palyer_mode_list_revers);

        mOrderText.setText(BaseApplication.getAppContext().getString(isReverse ? R.string.order_text :
                R.string.revers_text));
    }

    /**
     * 更新播放模式的图片
     * 1.默认是：PLAY_MODEL_LIST
     * 2.列表循环播放：PLAY_MODEL_LIST_LOOP
     * 3.随机播放：PLAY_MODEL_RANDOM
     * 4.单曲循环：PLAY_MODEL_SINGLE_LOOP
     */
    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode playMode) {

        int resId = R.drawable.selector_palyer_mode_list_order;
        int textId = R.string.play_mode_order_text;

        switch (playMode) {
            case PLAY_MODEL_LIST://列表模式
                //下面这一句写不写都可以 默认就是这个值
                //resId = R.drawable.selector_palyer_mode_list_order;
                //textId = R.string.play_mode_order_text;
                break;

            case PLAY_MODEL_RANDOM://随机模式
                resId = R.drawable.selector_palyer_mode_random;
                textId = R.string.play_mode_random_text;
                break;

            case PLAY_MODEL_LIST_LOOP://循环模式
                resId = R.drawable.selector_palyer_mode_list_order_looper;
                textId = R.string.play_mode_list_play_text;
                break;

            case PLAY_MODEL_SINGLE_LOOP://单曲循环模式
                resId = R.drawable.selector_palyer_mode_single_loop;
                textId = R.string.play_mode_single_play_text;
                break;
        }
        mPlaymodeIv.setImageResource(resId);
        mPlayModeTv.setText(textId);
    }


    //弹窗中间部分RecyclerView点击事件的回调
    public interface PlayListItemClickListener {

        /**
         * SobPopWindow 中RecyclerView被点击了传递索引
         *
         * @param position 索引
         */
        void itemClick(int position);
    }

    /**
     * 供外界实现回调的mPlayMoeContainer 播放模式被点击事件的监听
     *
     * @param listener
     */
    public void setPlayListActionClickListener(PlayListActionClickListener listener) {

        mPlayModeClickListener = listener;
    }

    /**
     * 本弹框顶部 播放模式 正序/倒序 回调接口
     */
    public interface PlayListActionClickListener {

        /**
         * mPlayMoeContainer 播放模式被点击了回调
         */
        void onPlayModeClick();

        /**
         * 播放顺序切换按钮 正序/倒序
         */
        void onOrderClick();
    }

}
