package com.bobo.himalayan.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bobo.himalayan.DetailActivity;
import com.bobo.himalayan.R;
import com.bobo.himalayan.adapters.AlbumListAdapter;
import com.bobo.himalayan.base.BaseFragment;
import com.bobo.himalayan.interfaces.IRcommendViewCallback;
import com.bobo.himalayan.presenters.AlbumDetailPresenter;
import com.bobo.himalayan.presenters.RecommendPresenter;
import com.bobo.himalayan.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 推荐页面的fragment
 */
public class RecommendFragmnet extends BaseFragment implements IRcommendViewCallback, UILoader.
        OnRetryClickListener, AlbumListAdapter.OnRecommendItemListener {

    private View mRootView;

    private RecyclerView mRecyclerView;

    // 适配器
    private AlbumListAdapter mRecommendListAdapter;

    private RecommendPresenter mRecommendPresenter;

    private UILoader mUILoader;

    @Override
    public View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {

        mUILoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container1) {
                return createSuccessView(layoutInflater, container1);
            }
        };

        //设置加载失败用户点击重新请求的接口代理为this
        mUILoader.setOnRetryClickListener(this);

        //获取到逻辑层的数据对象
        mRecommendPresenter = RecommendPresenter.getsInstance();

        //先要设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);

        //获取推荐列表
        mRecommendPresenter.getRecommendList();

        if (mUILoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }

        //返回view，给界面显示
        return mUILoader;
    }

    /**
     * 创建网路请求成功展示数据的界面
     *
     * @return
     */
    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        //view加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container, false);

        //RecyclerView 的使用①实例化控件
        mRecyclerView = mRootView.findViewById(R.id.recommend_list);

        TwinklingRefreshLayout twinklingRefreshLayout = mRootView.findViewById(R.id.over_scroll_view);
        // 设置上拉和下拉回弹效果
        twinklingRefreshLayout.setPureScrollModeOn();

        //RecyclerView 的使用②设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置item之间的间距
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView
                    parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);//像素转dp
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);//像素转dp
                outRect.left = UIUtil.dip2px(view.getContext(), 5);//像素转dp
                outRect.right = UIUtil.dip2px(view.getContext(), 5);//像素转dp
            }
        });

        //RecyclerView 的使用③设置适配器
        mRecommendListAdapter = new AlbumListAdapter();
        mRecyclerView.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setOnRecommendItemListener(this);

        return mRootView;
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当我们获取到推荐内容的时候这个方法就会被调用（成功了）数据回来以后就是更新UI
        mRecommendListAdapter.setData(result); //把数据设置给适配器并且更新

        //切换到成功的界面
        mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        //切换到加载失败页面
        mUILoader.updateStatus(UILoader.UIStatus.NEWWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        //切换到空的界面
        mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        //切换到加载的界面
        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        //在onCreateView 方法中注册 对应的也要在本方法中解除注册,避免内存泄露
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //加载失败用户点击了重新请求
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getsInstance().setTargetAlbum(album);
        //recycleview 中的 某个item被点击了,跳转到对应的详情页
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
