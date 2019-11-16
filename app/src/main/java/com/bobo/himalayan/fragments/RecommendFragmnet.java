package com.bobo.himalayan.fragments;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bobo.himalayan.R;
import com.bobo.himalayan.adapters.RecommendListAdapter;
import com.bobo.himalayan.base.BaseFragment;
import com.bobo.himalayan.utils.Constants;
import com.bobo.himalayan.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 求知自学网 on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 推荐页面的fragment
 */
public class RecommendFragmnet extends BaseFragment {


    public static final String TAG = "RecommendFragmnet";

    private  View mRootView;

    private RecyclerView mRecyclerView;

    //适配器
    private RecommendListAdapter mRecommendListAdapter;

    @Override
    public View onSubViewLoaded(LayoutInflater layoutInflater,ViewGroup container) {
        //view加载完成
        mRootView = layoutInflater.inflate(R.layout.fragment_recommend, container,false);

        //RecyclerView 的使用①实例化控件
        mRecyclerView = mRootView.findViewById(R.id.recommend_list);

        //RecyclerView 的使用②设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        //设置item之间的间距
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView
                    parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);//像素转dp
                outRect.bottom = UIUtil.dip2px(view.getContext(),5);//像素转dp
                outRect.left = UIUtil.dip2px(view.getContext(),5);//像素转dp
                outRect.right = UIUtil.dip2px(view.getContext(),5);//像素转dp
            }
        });

        //RecyclerView 的使用③设置适配器
        mRecommendListAdapter = new RecommendListAdapter();
        mRecyclerView.setAdapter(mRecommendListAdapter);

        //去拿数据过来
        getRecommendData();

        //返回view，给界面显示
        return mRootView;
    }

    /**
     * 获取推荐内容，其实就是猜你喜欢
     * 这个接口：3.10.6
     */
    private void getRecommendData() {
        //封装参数
        Map<String,String> map = new HashMap<>();

        //这个参数表示一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMAND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG,"thread name--->"+Thread.currentThread().getName());
                //数据获取成功
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();

                    //数据请求回来更新UI
                    upRecommendUI(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //数据获取失败
                LogUtil.e(TAG,"error -->"+i);
                LogUtil.e(TAG,"errorMsg -->"+s);
            }
        });

    }

    //更新UI
    private void upRecommendUI(List<Album> albumList) {
        //把数据设置给适配器并且更新
        mRecommendListAdapter.setData(albumList);
    }
}
