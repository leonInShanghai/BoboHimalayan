package com.bobo.himalayan.fragments;

import com.bobo.himalayan.PlayerActivity;
import com.bobo.himalayan.R;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bobo.himalayan.adapters.DetailListAdapter;
import com.bobo.himalayan.base.BaseFragment;
import com.bobo.himalayan.interfaces.IHistoryCallback;
import com.bobo.himalayan.presenters.HistoryPresenter;
import com.bobo.himalayan.presenters.PlayerPresenter;
import com.bobo.himalayan.views.ConfirmCheckBoxDialog;
import com.bobo.himalayan.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 历史fragment
 */
public class HistoryFragment extends BaseFragment implements IHistoryCallback, DetailListAdapter.
        ItemClickListener, DetailListAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.OnDialogActionClickListener {

    /**
     * 加载器
     */
    private UILoader mUiLoader;
    private DetailListAdapter mTrackListAdapter;
    private HistoryPresenter mHistoryPresenter;

    /**
     * 用户要删除的章节（集）对象
     */
    private Track mCurrentClickHistoryItem = null;


    @Override
    public View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {

        FrameLayout rootView = (FrameLayout)layoutInflater.inflate(R.layout.fragment_history,
                container, false);

        if (mUiLoader == null) {
            // BaseApplication.getAppContext
            mUiLoader = new UILoader(getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEmptyView() {

                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,
                            this, false);

                    TextView tips = emptyView.findViewById(R.id.empty_view_tips_tv);
                    tips.setText("没有历史记录呢!");

                    return emptyView;
                }
            };

        }else {
            // 先从父控件移除老的加载器
            if(mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        //HistoryPresenter
        mHistoryPresenter = HistoryPresenter.getHistoryPresenter();
        mHistoryPresenter.registerViewCallback(this);
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);

        // 加载本地数据库中的历史数据
        mHistoryPresenter.listHistories();

        // 再添加加载器
        rootView.addView(mUiLoader);

        return rootView;
    }

    /**
     * 加载器加载成功的布局
     * @param container
     * @return
     */
    private View createSuccessView(ViewGroup container) {

       View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history, container,
                false);

       TwinklingRefreshLayout refreshLayout = successView.findViewById(R.id.over_scroll_view);
       refreshLayout.setEnableRefresh(false);
       refreshLayout.setEnableLoadmore(false);
       RecyclerView historyList = successView.findViewById(R.id.history_list);
       historyList.setLayoutManager(new LinearLayoutManager(container.getContext()));

       // 设置item的(上下)间距
       historyList.addItemDecoration(new RecyclerView.ItemDecoration() {
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

        // 设置适配器
        mTrackListAdapter = new DetailListAdapter();
        mTrackListAdapter.setItemClickListener(this);
        mTrackListAdapter.setItemLongClickListener(this);
        historyList.setAdapter(mTrackListAdapter);

        mHistoryPresenter = HistoryPresenter.getHistoryPresenter();
        mHistoryPresenter.registerViewCallback(this);

        // 加载本地数据库中的历史数据
        mHistoryPresenter.listHistories();

        return successView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 解绑回调接口
        if (mHistoryPresenter != null){
            mHistoryPresenter.unRegisterViewCallback(this);
        }

    }

    @Override
    public void onHistoriesLoaded(List<Track> tracks) {

        if (tracks == null || tracks.size() == 0){
            mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
        }else{
            // 更新数据
            mTrackListAdapter.setData(tracks);

            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(detailData, position);
        //跳转到播放器页面
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }


    @Override
    public void onItemLongClick(Track track) {

        // 用户要删除的章节（集）对象
        this.mCurrentClickHistoryItem = track;

        // 删除历史
        ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(getActivity());
        dialog.setOnDialogActionClickListener(this);
        dialog.show();
    }

    /**
     * 用户确定要删除历史
     * @param checked  是否删除全部 true 删除全部 false 删除当前对象
     */
    @Override
    public void onCancelSubClick(boolean checked) {

        // 判断用户是否要删除全部
        if (checked){
            // 删除全部内容
            if (mHistoryPresenter != null){
                mHistoryPresenter.cleanHistories();
            }
        }else{
            // 删除一条（当前）数据
            if (mHistoryPresenter != null && mCurrentClickHistoryItem != null) {
                mHistoryPresenter.delHistory(mCurrentClickHistoryItem);
            }
        }
    }

    /**
     * 用户点了了我再想想
     */
    @Override
    public void onGiveUpClick() {
        // 取消啥都不用去做 ConfirmCheckBoxDialog内部会调用dismiss
    }
}
