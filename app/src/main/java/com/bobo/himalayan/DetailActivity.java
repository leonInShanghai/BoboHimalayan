package com.bobo.himalayan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobo.himalayan.adapters.DetailListAdapter;
import com.bobo.himalayan.base.BaseActivity;
import com.bobo.himalayan.interfaces.IAlbumDetailViewCallback;
import com.bobo.himalayan.presenters.AlbumDetailPresenter;
import com.bobo.himalayan.utils.ImageBlur;
import com.bobo.himalayan.views.RoundRectImageView;
import com.bobo.himalayan.views.UILoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by IT波 on 2019/11/23 Copyright © Leon. All rights reserved.
 * Functions: 内容详情页     Desktop Android xx13414521  彩虹加载进度条
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClickListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        initView();
        mAlbumDetailPresenter = AlbumDetailPresenter.getsInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
    }

    private void initView(){
        //装显示每一集的listview的容器
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
    }

    /**
     * 配合加载器-加载成功显示的视图
     */
    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list,container,
                false);
        //显示每一集的listview
        mDetailList = detailListView.findViewById(R.id.album_detail_list);
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
                outRect.top = UIUtil.dip2px(view.getContext(),2);//像素转dp
                outRect.bottom = UIUtil.dip2px(view.getContext(),2);//像素转dp
                outRect.left = UIUtil.dip2px(view.getContext(),2);//像素转dp
                outRect.right = UIUtil.dip2px(view.getContext(),2);//像素转dp
            }
        });

        mDetailListAdapter.setItemClickListener(this);

        return detailListView;
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {

        //判断数据结果，根据结果显示对应的UI界面
        if (tracks == null || tracks.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }

        if (mUiLoader != null){
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }

        //更新设置recycleview UI数据
        mDetailListAdapter.setData(tracks);
    }

    /**
     * 网络请求错误，显示网络异常状态
     * @param errorCode
     * @param errorMessage
     */
    @Override
    public void onNetWorkError(int errorCode, String errorMessage) {
        mUiLoader.updateStatus(UILoader.UIStatus.NEWWORK_ERROR);
    }

    /**
     * presenter将上一页面传给它的数据回传过来 album 是上一页面请求好的数据
     * @param album
     */
    @Override
    public void onAlbumLoaded(Album album) {

        if (album != null){

        //FIXME:原来的代码 拿数据显示loading状态
//            if (mUiLoader != null) {
//                mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
//            }

            //获取专辑的详情内容
            mAlbumDetailPresenter.getAlbumDetail((int)album.getId(), mCurrentPage);
            mCurrentId =  album.getId();

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

                            if (drawable != null){
                                //到这里imageview 才有了图片 然后再设置毛玻璃效果
                                ImageBlur.makeBlur(mLargeCover,DetailActivity.this);
                            }
                        }

                        @Override
                        public void onError() {
                            Log.e(TAG,"onError");
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

    /**
     * 加载错误页面 用户点击重新请求网络事件的监听
     */
    @Override
    public void onRetryClick() {
        //这里面表示用户网络不佳的时候 去点击了重新加载
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int)mCurrentId, mCurrentPage);
        }
    }

    /**
     * RecycleView of item click 事件的处理
     */
    @Override
    public void onItemClick() {
        //TODO:跳转到播放器页面
        Intent intent = new Intent(this,PlayerActivity.class);
        startActivity(intent);
    }
}
