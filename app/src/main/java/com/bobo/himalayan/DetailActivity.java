package com.bobo.himalayan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bobo.himalayan.adapters.DetailListAdapter;
import com.bobo.himalayan.base.BaseActivity;
import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.interfaces.IAlbumDetailViewCallback;
import com.bobo.himalayan.interfaces.IPlayerCallback;
import com.bobo.himalayan.interfaces.ISubscriptionCallback;
import com.bobo.himalayan.presenters.AlbumDetailPresenter;
import com.bobo.himalayan.presenters.PlayerPresenter;
import com.bobo.himalayan.presenters.SubscritptionPresenter;
import com.bobo.himalayan.utils.Constants;
import com.bobo.himalayan.utils.ImageBlur;
import com.bobo.himalayan.views.RoundRectImageView;
import com.bobo.himalayan.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by IT波 on 2019/11/23 Copyright © Leon. All rights reserved.
 * Functions: 内容详情页     Desktop Android xx13414521  彩虹加载进度条
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener,
        DetailListAdapter.ItemClickListener, IPlayerCallback, ISubscriptionCallback {

    private static final String TAG = "DetailActivity";

    //顶部的大图
    private ImageView mLargeCover;

    //用户的头像 自定义圆角图
    private RoundRectImageView mSmallCover;

    //标题
    private TextView mAlbunTitle;

    //作者名字
    private TextView mAlbumAuthor;

    private AlbumDetailPresenter mAlbumDetailPresenter;

    //请求页面的变量-默认从第1页开始(喜马拉雅文档上要求的不要从0开始)
    private int mCurrentPage = 1;

    //显示 集 的recycleview
    private RecyclerView mDetailList;

    private long mCurrentId = -1;

    //显示 集 的recycleview 的适配器
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;

    //播放控制的图标
    private ImageView mPlayControlBtn;

    //播放状态文字标题
    private TextView mPlayControlTips;

    //播放器的Presenter 此时本类中持有两个Presenter
    private PlayerPresenter mPlayerPresenter;

    //存储详情页（本页面）列表的 成员变量 默认为null
    private List<Track> mCurrentTracks = null;

    //点击中间的播放按钮发起播放的索引(第几集) 默认为0
    private final static int DEFAULT_PLAY_INDEX = 0;

    /**
     * 下拉刷新上拉加载更多第三方控件
     */
    private TwinklingRefreshLayout mRefreshLayout;

    /**
     * 是否在加载的变量
     */
    private boolean mIsLoaderMore = false;

    /**
     * 每一小节的名称
     */
    private String mTrackTitle;

    /**
     * 订阅按钮
     */
    private TextView mSubBtn;

    /**
     * 订阅相关的Presenter
     */
    private SubscritptionPresenter mSubscritptionPresenter;

    /**
     * 当前的专辑
     */
    private Album mCurrentAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        initView();

        // 初始化Presenter目前本类中用到3个
        initPresenter();

        // 设置订阅按钮的状态
        updateSubState();

        updatePlayState(mPlayerPresenter.isPlaying());

        initListener();
    }

    // 设置订阅按钮的状态
    private void updateSubState(){
        boolean isSub = mSubscritptionPresenter.isSub(mCurrentAlbum);
        mSubBtn.setText(isSub ? R.string.cancel_sub_tips_text : R.string.sub_tip_text);
    }

    private void initPresenter() {
        //这个是专辑详情的Presenter
        mAlbumDetailPresenter = AlbumDetailPresenter.getsInstance();
        mAlbumDetailPresenter.registerViewCallback(this);

        //获取到播放器的 Presenter
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        boolean playing = mPlayerPresenter.isPlaying();

        // 订阅相关的Presenter
        mSubscritptionPresenter = SubscritptionPresenter.getInstance();
        mSubscritptionPresenter.getSubcriptionList();
        mSubscritptionPresenter.registerViewCallback(this);
    }

    @Override
    protected void onDestroy() {
        mAlbumDetailPresenter.unRegisterViewCallback(this);
        mSubscritptionPresenter.unRegisterViewCallback(this);
        mPlayerPresenter.unRegisterViewCallback(this);
        super.onDestroy();
    }

    //初始化各个子控件的点击事件
    private void initListener() {

        //中间区域播放按钮的点击事件
        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //判断播放器是否有播放列表
                boolean has = mPlayerPresenter.hasPlayList();

                if (has) {
                    //有列表的处理逻辑
                    handlePlayControl();
                } else {
                    //没有播放列表的处理
                    handleNoPlayList();
                }
            }
        });

        mSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               boolean isSub = mSubscritptionPresenter.isSub(mCurrentAlbum);

               // 如果没有订阅,就去订阅，如果已经订阅就取消订阅
               if (isSub){
                   // 已经订阅就取消订阅
                   mSubscritptionPresenter.deleteSubscription(mCurrentAlbum);
               }else{
                   // 没有订阅,就去订阅
                   mSubscritptionPresenter.addSubscription(mCurrentAlbum);
               }
            }
        });
    }

    /**
     * 用户点击中间部分播放按钮 没有列表的处理逻辑
     */
    private void handleNoPlayList() {

        //用户点击中间部分播放按钮 没有列表的处理逻辑 就按当前列表从第0个开始播放
        mPlayerPresenter.setPlayList(mCurrentTracks, DEFAULT_PLAY_INDEX);
    }

    //用户点击中间部分播放按钮 有列表的处理逻辑
    private void handlePlayControl() {

        //有播放列表 控制（改变）播放器的状态
        if (mPlayerPresenter.isPlaying()) {
            //正在播放那么就暂停
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.play();
        }
    }

    // 初始化各个子控件
    private void initView() {

        // 装显示每一集的listview的容器
        mDetailListContainer = findViewById(R.id.detail_list_container);

        if (mUiLoader == null) {
            //加载器
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };

            //添加加载器前先移除所有的子view
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);

            //加载失败页面 用户点击事件的监听
            mUiLoader.setOnRetryClickListener(this);
        }

        //顶部的大图
        mLargeCover = findViewById(R.id.iv_large_cover);

        //用户的头像 自定义圆角
        mSmallCover = findViewById(R.id.viv_small_cover);

        //标题
        mAlbunTitle = findViewById(R.id.tv_album_title);

        //作者的名字
        mAlbumAuthor = findViewById(R.id.tv_album_author);

        //播放控制的图标
        mPlayControlBtn = findViewById(R.id.detail_play_control);

        //播放状态文字标题
        mPlayControlTips = findViewById(R.id.play_control_text_tv);

        // 订阅按钮
        mSubBtn = findViewById(R.id.detail_sub_btn);
    }

    /**
     * 配合加载器-加载成功显示的视图
     */
    private View createSuccessView(ViewGroup container) {

        final View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container,
                false);

        //显示每一集的RecyclerView
        mDetailList = detailListView.findViewById(R.id.album_detail_list);

        // 下拉刷新上拉加载更多控件
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);

        //第一步：设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        mDetailList.setLayoutManager(layoutManager);

        //第二部：设置适配器
        mDetailListAdapter = new DetailListAdapter();

        mDetailList.setAdapter(mDetailListAdapter);

        //设置item的(上下)间距
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView
                    parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = UIUtil.dip2px(view.getContext(), 2);//像素转dp
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);//像素转dp
                outRect.left = UIUtil.dip2px(view.getContext(), 2);//像素转dp
                outRect.right = UIUtil.dip2px(view.getContext(), 2);//像素转dp
            }
        });

        mDetailListAdapter.setItemClickListener(this);

        // 自定义下拉刷新头部为贝塞尔雷达
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxBottomHeight(140);

        // 下拉刷新上拉加载更多时间监听
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {

            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);


                BaseApplication.getsHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "刷新成功....", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();

                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);

                // 去加载更多内容
                mAlbumDetailPresenter.loadMore();
                mIsLoaderMore = true;
            }

        });

        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {

        if (mIsLoaderMore && mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore = false;
        }

        //存储详情页（本页面）列表的 成员变量 赋值
        mCurrentTracks = tracks;

        //判断数据结果，根据结果显示对应的UI界面
        if (tracks == null || tracks.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }

        //更新设置recycleview UI数据
        mDetailListAdapter.setData(tracks);
    }

    /**
     * 网络请求错误，显示网络异常状态
     *
     * @param errorCode
     * @param errorMessage
     */
    @Override
    public void onNetWorkError(int errorCode, String errorMessage) {
        mUiLoader.updateStatus(UILoader.UIStatus.NEWWORK_ERROR);
    }

    /**
     * presenter将上一页面传给它的数据回传过来 album 是上一页面请求好的数据
     *
     * @param album
     */
    @Override
    public void onAlbumLoaded(Album album) {

        this.mCurrentAlbum = album;

        if (album != null) {

            //FIXME:原来的代码 拿数据显示loading状态
//            if (mUiLoader != null) {
//                mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
//            }

            //获取专辑的详情内容
            mAlbumDetailPresenter.getAlbumDetail((int) album.getId(), mCurrentPage);
            mCurrentId = album.getId();

            //显示标题
            mAlbunTitle.setText(album.getAlbumTitle() == null ? "" : album.getAlbumTitle());

            //显示作者的名称
            mAlbumAuthor.setText(album.getAnnouncer().getNickname() == null ? "" : album.getAnnouncer().
                    getNickname());

            //显示顶部的大图 - 毛玻璃效果
            //Picasso.with(this).load(album.getCoverUrlLarge() == null ? "" : album.getCoverUrlLarge()).
            //placeholder(R.mipmap.logo).into(mLargeCover);
            Picasso.with(this).load(album.getCoverUrlLarge() == null ? "0" : album.getCoverUrlLarge()).
                    placeholder(R.mipmap.logo).into(mLargeCover, new Callback() {

                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();

                    if (drawable != null) {
                        //到这里imageview 才有了图片 然后再设置毛玻璃效果
                        ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError() {
                    Log.e(TAG, "onError");
                }
            });

            //到这里imageview 才有了图片 然后再设置毛玻璃效果
            //ImageBlur.makeBlur(mLargeCover,this);

            //显示左上角作者的头像
            Picasso.with(this).load(album.getCoverUrlLarge() == null ? "0" : album.getCoverUrlLarge()).
                    placeholder(R.mipmap.logo).into(mSmallCover);
        }
    }

    /**
     * FIXME:修正
     * 开始请求网络进入加载中状态的回调
     */
    @Override
    public void secondaryRefresh() {
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
    }

    @Override
    public void onLoaderMoreFinished(int size) {

        if (size > 0) {
            Toast.makeText(DetailActivity.this, "成功加载" + size + "条内容", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(DetailActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    /**
     * 加载错误页面 用户点击重新请求网络事件的监听
     */
    @Override
    public void onRetryClick() {
        //这里面表示用户网络不佳的时候 去点击了重新加载
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) mCurrentId, mCurrentPage);
        }
    }

    /**
     * RecycleView of item click 事件的处理
     */
    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData, position);
        //跳转到播放器页面
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    /**
     * 根据当前播放器的播放状态 修改图标为播放状态，文字状态对应文字
     *
     * @param playing
     */
    private void updatePlayState(boolean playing) {

        // 切换播放的点击按钮resource
        mPlayControlBtn.setImageResource(playing ? R.drawable.selector_play_control_pause :
                R.drawable.selector_play_control_play);

        // 非播放状态显示点击播放
        if (!playing) {
            mPlayControlTips.setText(R.string.click_play_tipts_text);
        } else if (!TextUtils.isEmpty(mTrackTitle)) {
            mPlayControlTips.setText(mTrackTitle);
        } else {
            mPlayControlTips.setText("正在播放");
        }
    }

    @Override
    public void onPlayStart() {
        //修改图标为暂停状态，文字修改为正在播放
        updatePlayState(true);
    }

    @Override
    public void onPlayPause() {
        //修改图标为播放状态，文字修改为已暂停
        updatePlayState(false);
    }


    @Override
    public void onPlayStop() {
        //修改图标为播放状态，文字修改为已暂停
        updatePlayState(false);
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
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdLoaded() {

    }

    @Override
    public void onTrackUpdated(Track track, int playIndex) {

        if (track != null) {
            mTrackTitle = track.getTrackTitle();

            // 非空判断
            if (!TextUtils.isEmpty(mTrackTitle)) {
                // 播放放状态显示专辑下每一小节的名称
                mPlayControlTips.setText(mTrackTitle);
            }
        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        Log.e("onAddResult:",isSuccess +"");

        /// 注释原因：在onSubscritpionsLoaded方法中处理
//        if (isSuccess){
//            // 如果订阅成功（添加到数据库成功），那就修改UI成取消订阅
//            mSubBtn.setText(R.string.cancel_sub_tips_text);
//        }

        // toast提示
        String tipsText = isSuccess ? "订阅成功" : "订阅失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        /// 注释原因：在onSubscritpionsLoaded方法中处理
//        if (isSuccess){
//            // 如果取消订阅成功（添加到数据库成功），那就修改UI成取消订阅
//            mSubBtn.setText(R.string.sub_tip_text);
//        }

        // toast提示
        String tipsText = isSuccess ? "删除成功" : "删除失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscritpionsLoaded(List<Album> albums) {

        // 这个页面不需要处理
        for (Album album : albums) {
            Log.e(TAG, "album -->" + album.getAlbumTitle());

        }
        Log.e("111111111111111", albums.size() + "");

        // 我增加校验是否订阅（后台退出app再进时这里会起作用）
        updateSubState();
    }

    /**
     * 当用户订阅数量满了会回调这个方法（不能超过100条）
     */
    @Override
    public void onSubFull() {
       // 处理一个即可
       Toast.makeText(this, "订阅数量不可超过" + Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }
}
