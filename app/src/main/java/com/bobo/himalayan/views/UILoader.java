package com.bobo.himalayan.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bobo.himalayan.R;
import com.bobo.himalayan.base.BaseApplication;

/**
 * Created by Leon on 2019/11/17. Copyright © Leon. All rights reserved.
 * Functions: 加载器 帮助解决加载过程中的ui
 */
public abstract class UILoader extends FrameLayout {

    //把加载过程中的几种状态定义成枚举类
    public enum UIStatus {
        LOADING, SUCCESS, NEWWORK_ERROR, EMPTY, NONE
    }

    public UIStatus mCurrentStatus = UIStatus.NONE;

    private View mLoadingView;
    private View mSuccessView;
    private View mNetworkErrorView;
    private View mEmptyView;

    /**
     * 加载失败后用户点击 重新加载的接口
     */
    private OnRetryClickListener mOnRetryClickListener = null;

    public UILoader(@NonNull Context context) {
        this(context, null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    /**
     * 提供更新状态的方法
     * 传入自定义枚举类型
     */
    public void updateStatus(UIStatus status) {
        mCurrentStatus = status;

        //更新ui（一定要在主线程）
        BaseApplication.getsHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }

    /**
     * 初始化UI
     */
    private void init() {
        switchUIByCurrentStatus();
    }

    private void switchUIByCurrentStatus() {
        //加载中
        if (mLoadingView == null) {
            mLoadingView = getLoadingView();
            addView(mLoadingView);
        }
        //根据状态设置是否可见
        mLoadingView.setVisibility(mCurrentStatus == UIStatus.LOADING ? VISIBLE : GONE);

        //加载成功
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        //根据状态设置是否可见
        mSuccessView.setVisibility(mCurrentStatus == UIStatus.SUCCESS ? VISIBLE : GONE);

        //网络错误
        if (mNetworkErrorView == null) {
            mNetworkErrorView = getNetworkErrorView();
            addView(mNetworkErrorView);
        }
        //根据状态设置是否可见
        mNetworkErrorView.setVisibility(mCurrentStatus == UIStatus.NEWWORK_ERROR ? VISIBLE : GONE);

        //数据为空
        if (mEmptyView == null) {
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }
        //根据状态设置是否可见
        mEmptyView.setVisibility(mCurrentStatus == UIStatus.EMPTY ? VISIBLE : GONE);
    }

    // 当有定制化需求的时候可以重写这个空界面
    protected View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
    }

    private View getNetworkErrorView() {
        //mNetworkErrorView
        View networkErrorView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_error_view,
                this, false);

        //网络加载错误 用户可以再点击一次刷新
        networkErrorView.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新获取数据
                if (mOnRetryClickListener != null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });

        return networkErrorView;
    }

    //加载成功创建为抽象方法由子类实现
    protected abstract View getSuccessView(ViewGroup container);


    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view, this,
                false);
    }

    /**
     * 供外界调用前设置的方法
     *
     * @param listener
     */
    public void setOnRetryClickListener(OnRetryClickListener listener) {
        this.mOnRetryClickListener = listener;
    }

    /**
     * 加载失败用户点击了重试 接口回调
     */
    public interface OnRetryClickListener {
        void onRetryClick();
    }
}
