package com.bobo.himalayan;

import android.graphics.Canvas;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobo.himalayan.adapters.DetailListAdapter;
import com.bobo.himalayan.base.BaseActivity;
import com.bobo.himalayan.interfaces.IAlbumDetailPresenter;
import com.bobo.himalayan.interfaces.IAlbumDetailViewCallback;
import com.bobo.himalayan.presenters.AlbumDetailPresenter;
import com.bobo.himalayan.utils.ImageBlur;
import com.bobo.himalayan.views.RoundRectImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by 求知自学网 on 2019/11/23 Copyright © Leon. All rights reserved.
 * Functions: 内容详情页
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback {

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

    //显示 集 的recycleview 的适配器
    private DetailListAdapter mDetailListAdapter;



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
        //顶部的大图
        mLargeCover = findViewById(R.id.iv_large_cover);
        //用户的头像 自定义圆角
        mSmallCover = findViewById(R.id.viv_small_cover);
        //标题
        mAlbunTitle = findViewById(R.id.tv_album_title);
        //作者的名字
        mAlbumAuthor = findViewById(R.id.tv_album_author);
        //显示每一集的listview
        mDetailList = findViewById(R.id.album_detail_list);
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
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        //更新设置recycleview UI数据
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onAlbumLoaded(Album album) {

        if (album != null){

            //获取专辑的详情内容
            mAlbumDetailPresenter.getAlbumDetail((int)album.getId(), mCurrentPage);

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
}
