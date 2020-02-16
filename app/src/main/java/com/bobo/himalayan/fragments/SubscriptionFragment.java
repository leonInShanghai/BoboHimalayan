package com.bobo.himalayan.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bobo.himalayan.DetailActivity;
import com.bobo.himalayan.R;
import com.bobo.himalayan.adapters.AlbumListAdapter;
import com.bobo.himalayan.base.BaseFragment;
import com.bobo.himalayan.interfaces.ISubscriptionCallback;
import com.bobo.himalayan.presenters.AlbumDetailPresenter;
import com.bobo.himalayan.presenters.SubscritptionPresenter;
import com.bobo.himalayan.utils.Constants;
import com.bobo.himalayan.views.ConfirmDialog;
import com.bobo.himalayan.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 订阅fragment
 */
public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.OnAlbumItemListener,
        AlbumListAdapter.OnAlbumItemLongClickListener, ConfirmDialog.OnDialogActionClickListener {

    private SubscritptionPresenter mSubscritptionPresenter;

    // 显示内容的循环视图
    private RecyclerView mSubListView;

    // 显示内容循环视图的适配器
    private AlbumListAdapter mAlbumListAdapter;

    // 当前专辑
    private Album mCurrentClickAlbum;

    // 加载器
    private UILoader mUiLoader;

    @Override
    public View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_subscription,
                container, false);

        if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

                @Override
                protected View getEmptyView() {

                    // 在订阅（本页面）自定义空页面布局
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,
                            this, false);

                    TextView tipsView = emptyView.findViewById(R.id.empty_view_tips_tv);
                    tipsView.setText(R.string.no_sub_conent_tips_text);

                    return emptyView;
                }
            };

            // 先从父控件移除加载器
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }

            // 再添加加载器
            rootView.addView(mUiLoader);
        }

        return rootView;
    }

    private View createSuccessView() {

        View itemView = LayoutInflater.from(getContext()).inflate( R.layout.item_subscription, null);

        TwinklingRefreshLayout refreshLayout = itemView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);

        mSubListView = itemView.findViewById(R.id.sub_list);
        mSubListView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 设置item之间的间距
        mSubListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView
                    parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);//像素转dp
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);//像素转dp
                outRect.left = UIUtil.dip2px(view.getContext(), 5);//像素转dp
                outRect.right = UIUtil.dip2px(view.getContext(), 5);//像素转dp
            }
        });


        // 设置适配器 注意这个适配器三个地方在用别乱改
        mAlbumListAdapter = new AlbumListAdapter();
        mAlbumListAdapter.setOnAlbumItemListener(this);
        mAlbumListAdapter.setOnAlbumItemLongClickListener(this);
        mSubListView.setAdapter(mAlbumListAdapter);

        mSubscritptionPresenter = SubscritptionPresenter.getInstance();
        mSubscritptionPresenter.registerViewCallback(this);
        mSubscritptionPresenter.getSubcriptionList();

        // 状态为加载状态
        if (mUiLoader != null){
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }

        return itemView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    /**
     * 用户长按发起删除一个专辑之后会回调这个方法
     * @param isSuccess
     */
    @Override
    public void onDeleteResult(boolean isSuccess) {
        // 给出取消订阅的提示
        Toast.makeText(getContext(),getString(isSuccess ? R.string.cancel_sub_success : R.string.cancel_sub_failed), Toast.LENGTH_SHORT).show();
    }

    /**
     * 首次进入页面 删除 添加 后都会回调这里刷新界面
     * @param albums
     */
    @Override
    public void onSubscritpionsLoaded(List<Album> albums) {

        if (albums == null || albums.size() == 0) {
            // 状态为空数据页面
            if (mUiLoader != null){
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }else{
            // 状态为加载成功
            if (mUiLoader != null){
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }

            // 订阅列表数据（从本地数据库）加载完成
            mAlbumListAdapter.setData(albums);
        }
    }

    /**
     * 当用户订阅数量满了会回调这个方法（不能超过100条）
     */
    @Override
    public void onSubFull() {
        // 处理一个即可
        Toast.makeText(getContext(), "订阅数量不可超过" + Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // 取消接口的注册
        if (mSubscritptionPresenter != null){
            mSubscritptionPresenter.unRegisterViewCallback(this);
        }

        // 取消注册
        mAlbumListAdapter.setOnAlbumItemListener(null);
    }

    @Override
    public void onItemClick(int clickPosition, Album album) {
        AlbumDetailPresenter.getsInstance().setTargetAlbum(album);

        // 某个item被点击了跳转到详情页面
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    /**
     * 用户长按了某个item  在订阅fragment中长按要取消订阅
     * @param album item对应的对象
     */
    @Override
    public void onItemLongClick(Album album) {

        this.mCurrentClickAlbum = album;

        // Log.e("1111","onItemLongClick");
        ConfirmDialog confirmDialog = new ConfirmDialog(getContext());
        confirmDialog.setOnDialogActionClickListener(this);
        confirmDialog.show();

    }

    /**
     * 用户点击了对话框上的取消订阅
     */
    @Override
    public void onCancelSubClick() {
        mSubscritptionPresenter.deleteSubscription(mCurrentClickAlbum);
    }

    /**
     * 用户点击了对话框上的我再想想
     */
    @Override
    public void onGiveUpClick() {

    }
}
