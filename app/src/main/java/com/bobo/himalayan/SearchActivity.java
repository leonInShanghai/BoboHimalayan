package com.bobo.himalayan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bobo.himalayan.adapters.AlbumListAdapter;
import com.bobo.himalayan.adapters.SearchRecommendAdpter;
import com.bobo.himalayan.base.BaseActivity;
import com.bobo.himalayan.interfaces.ISearchCallback;
import com.bobo.himalayan.presenters.AlbumDetailPresenter;
import com.bobo.himalayan.presenters.SearchPreseter;
import com.bobo.himalayan.utils.Constants;
import com.bobo.himalayan.views.FlowTextLayout;
import com.bobo.himalayan.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Leon on 2020-01-01 Copyright © Leon. All rights reserved.
 * Functions: 搜索页面
 */
public class SearchActivity extends BaseActivity implements ISearchCallback, AlbumListAdapter.OnRecommendItemListener {

    private String TAG = "SearchActivity";

    /**
     * 左上角的返回按钮
     */
    private ImageView mBackBtn;

    /**
     * 上中搜索框
     */
    private EditText mInputBox;

    /**
     * 右上角搜索按钮
     */
    private TextView mSearchBtn;

    /**
     * 显示内容的帧布局
     */
    private FrameLayout mResultContainer;

    private SearchPreseter mSearchPreseter;
    private UILoader mUILoader;

    /**
     * 展示搜索内容的循环视图
     */
    private RecyclerView mResultListView;
    private AlbumListAdapter mAlbumListAdapter;
    private FlowTextLayout mFlowTextLayout;

    /**
     * 输入方法(键盘)管理器
     */
    private InputMethodManager mImm;

    /**
     * 输入框中的删除按钮
     */
    private ImageView mDelBtn;

    /**
     * 延时显示键盘时间
     */
    public static final int TIME_SHOW_IMM = 500;

    /**
     * 显示搜索推荐（联想关键字）的循环视图
     */
    private RecyclerView mSearchRecommendList;

    /**
     * 搜索推荐（联想关键字）循环视图的适配器
     */
    private SearchRecommendAdpter mRecommendAdpter;

    /**
     * 刷新控件
     */
    private TwinklingRefreshLayout mRefreshLayout;

    /**
     * 是否需要联想 当推荐词进入输入框的时候，不需要再联想. 其他时候需要联想
     */
    private boolean mNeedSuggesWord = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // 初始化各个子控件
        initView();

        // 初始化各个子控件的点击事件
        initEvent();

        initPresenter();
    }

    private void initPresenter() {

        mImm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        // 实例化Preseter并注册U更新的接口
        mSearchPreseter = SearchPreseter.getSearchPreseter();
        mSearchPreseter.registerViewCallback(this);

        // 获取推荐热词的数据
        mSearchPreseter.getHotWork();
    }

    // 初始化各个子控件
    private void initView() {
        // 左上角的返回按钮
        mBackBtn = findViewById(R.id.search_back);
        // 上部中间搜索框
        mInputBox = findViewById(R.id.search_input);

        // 输入框中的删除按钮-默认没有输入框没有内容隐藏删除按钮
        mDelBtn = findViewById(R.id.search_input_delete);
        mDelBtn.setVisibility(View.GONE);

        // 用户进入搜索页面自动弹出键盘
        mInputBox.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputBox.requestFocus();
                mImm.showSoftInput(mInputBox, InputMethodManager.SHOW_IMPLICIT);
            }
        }, TIME_SHOW_IMM);

        // 右上角搜索按钮
        mSearchBtn = findViewById(R.id.search_btn);
        // 显示内容的帧布局
        mResultContainer = findViewById(R.id.search_container);

        if (mUILoader == null) {
            mUILoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSucessView();
                }
            };

            // 避免重复添加（判断是否已经存在）
            if (mUILoader.getParent() instanceof ViewGroup) {

                // 避免重复添加（存在就将自己移除）
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }

            // 添加到帧布局中
            mResultContainer.addView(mUILoader);
        }

    }

    /**
     * 创建请求成功的视图
     *
     * @return
     */
    private View createSucessView() {

        // 从xml文件加载布局
        View resultView = LayoutInflater.from(SearchActivity.this).inflate(R.layout.search_result_layout, null);

        // 刷新控件
        mRefreshLayout = resultView.findViewById(R.id.search_result_refresh_layout);
        // 不启用下拉刷新
        mRefreshLayout.setEnableRefresh(false);

        // 显示热词的自定义view
        mFlowTextLayout = resultView.findViewById(R.id.recommend__hot_word_view);

        // 展示内容的循环视图  1
        mResultListView = resultView.findViewById(R.id.result_list_view);

        // 设置布局管理器 2
        LinearLayoutManager resultLayoutManager = new LinearLayoutManager(this);
        mResultListView.setLayoutManager(resultLayoutManager);

        // 设置适配器 3
        mAlbumListAdapter = new AlbumListAdapter();
        mResultListView.setAdapter(mAlbumListAdapter);

        //设置item之间的间距
        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView
                    parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);//像素转dp
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);//像素转dp
                outRect.left = UIUtil.dip2px(view.getContext(), 5);//像素转dp
                outRect.right = UIUtil.dip2px(view.getContext(), 5);//像素转dp
            }
        });

        // 搜索推荐（联想关键字） 1实例化recyclerview
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list);

        // 2设置布局管理器 注意：布局管理器不可共用
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(linearLayoutManager);

        // 设置item之间的间距
        mSearchRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView
                    parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);//像素转dp
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);//像素转dp
                outRect.left = UIUtil.dip2px(view.getContext(), 5);//像素转dp
                outRect.right = UIUtil.dip2px(view.getContext(), 5);//像素转dp
            }
        });

        // 3设置适配器
        mRecommendAdpter = new SearchRecommendAdpter();
        mSearchRecommendList.setAdapter(mRecommendAdpter);

        return resultView;
    }

    // 各个子控件的点击事件
    private void initEvent() {

        // 显示搜索结果的RecyclerView的适配器
        mAlbumListAdapter.setOnRecommendItemListener(this);

        // 上拉加载更多拖拽事件的处理
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                Log.e("mRefreshLayout", "load more...");

                mSearchPreseter.loadMore();
            }
        });

        // 联想词RecyclerView点击事件的回调
        mRecommendAdpter.setItemClickListener(new SearchRecommendAdpter.ItemClickListener() {
            @Override
            public void onItemClick(String keyword) {
                Log.e(TAG, "mRecommendAdpter.setItemClick :" + keyword);

                // 当推荐词进入输入框的时候，不需要再联想
                mNeedSuggesWord = false;

                // 执行搜索动作
                switchToSearch(keyword);
            }
        });

        // 输入框中的删除按钮
        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInputBox.setText("");
            }
        });

        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {

                // 当推荐词进入输入框的时候，不需要再联想
                mNeedSuggesWord = false;

                // 执行搜索动作
                switchToSearch(text);
            }
        });

        // 设置网络请求错误点击再次请求事件的监听
        mUILoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
            @Override
            public void onRetryClick() {

                // Preseter重新发起一次关键字（上次已经在Preseter中了）网络请求
                mSearchPreseter.reSearch();

                // 加载器的状态重新切换到加载中
                mUILoader.updateStatus(UILoader.UIStatus.LOADING);
            }
        });

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 左上角的返回按钮被点击了
                finish();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 右上角的搜索按钮被点击了-获取用户输入的关键字 执行搜索
                String keyword = mInputBox.getText().toString().trim();

                // 用户输入的关键字为空
                if (TextUtils.isEmpty(keyword)) {
                    // 提示用户请输入搜索关键字并不发送搜索请求
                    Toast.makeText(SearchActivity.this, "请输入搜索关键字", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 执行搜索Presenter开始请求数据
                mSearchPreseter.doSearch(keyword);

                // 加载器切换到加载的状态
                mUILoader.updateStatus(UILoader.UIStatus.LOADING);
            }
        });

        // 监听输入框内容改变
        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                /// 调试代码
                // Log.e(TAG,"content -- > "+ charSequence);
                // Log.e(TAG,"start -- > "+ start);
                // Log.e(TAG,"before -- > "+ before);
                // Log.e(TAG,"count -- > "+ count);

                // 当用户删除完输入框中的内容时自动显示热词
                if (TextUtils.isEmpty(charSequence)) {
                    mSearchPreseter.getHotWork();

                    // 输入框中内容为空的时候也要隐藏
                    mDelBtn.setVisibility(View.GONE);

                } else {
                    mDelBtn.setVisibility(View.VISIBLE);

                    // 当用户点击热词热词进入输入框不用联想，其他需要联想
                    if (mNeedSuggesWord) {
                        // 触发联想查询
                        getSuggestWord(charSequence);
                    } else {
                        // 修改是否需要联想变量为默认值
                        mNeedSuggesWord = true;
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    /**
     * 执行搜索动作
     *
     * @param text
     */
    private void switchToSearch(String text) {

        // 第一步,把热词扔到输入框里
        mInputBox.setText(text);

        // 然输入框中的光标在最后（默认是在最前面的）
        mInputBox.setSelection(text.length());

        // 第二步，发起搜索
        mSearchPreseter.doSearch(text);

        // 第三步，改变UI状态
        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    /**
     * 获取联想的关键词
     *
     * @param keyWorld 用户在输入框输入过的搜索关键字
     */
    private void getSuggestWord(CharSequence keyWorld) {

        Log.e(TAG, "getSuggestWord: " + keyWorld.toString());

        mSearchPreseter.getRecommendWord(keyWorld.toString());
    }

    @Override
    protected void onDestroy() {
        // 取消注册，有注册就要有取消注册避免内存泄漏
        if (mSearchPreseter != null) {
            mSearchPreseter.unRegisterViewCallback(this);
            mSearchPreseter = null;
        }

        super.onDestroy();
    }

    /**
     * 搜索结果回调
     *
     * @param result
     */
    @Override
    public void onSearchResultLoad(List<Album> result) {

        /// 程序员拉大锯的写法我觉有问题于是注释掉自己写了
        // if (result != null) {
        //     if (result.size() == 0) {
        //         // 数据为空处理
        //         if (mContent != null) {
        //             // 加载器切换大数据为空的状态
        //             mContent.updateStatus(UILoader.UIStatus.EMPTY);
        //         }
        //     }else{
        //         // 数据不为空且数据集合大小>0
        //         mAlbumListAdapter.setData(result);
        //     }
        // }

        ///隐藏热词自定义view 后面的新方法替代本方法
        // mFlowTextLayout.setVisibility(View.GONE);
        handleSearchResult(result);

        // 请求成功了(无论有没有数据)帮用户隐藏掉键盘
        mImm.hideSoftInputFromWindow(mInputBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void handleSearchResult(List<Album> result) {
        // 隐藏导航栏一下所有控件让该显示的控件显示
        hideSuccessView();

        // 显示展示内容的RecycleView
        mRefreshLayout.setVisibility(View.VISIBLE);


        if (result == null || result.size() == 0) {
            // 数据为空处理
            if (mUILoader != null) {
                // 加载器切换到数据为空的状态
                mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        } else {
            // 数据不为空
            mAlbumListAdapter.setData(result);
            // 加载器切换到 成功状态
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    /**
     * 热词请求成功
     *
     * @param hotWordList
     */
    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {

        Log.e(TAG, "hotWords size -->" + hotWordList.size());

        /// 热词请求回来要显示热词就要先隐藏ResultList不然ResultList会盖住热词 新方法替代
        // mResultListView.setVisibility(View.GONE);

        // 隐藏导航栏一下所有控件让该显示的控件显示
        hideSuccessView();

        // 让自定义显示热词的自定义view显示
        mFlowTextLayout.setVisibility(View.VISIBLE);

        if (mUILoader != null) {
            mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }

        List<String> hotWords = new ArrayList<>();
        // hotWords.clear();
        for (HotWord hotWord : hotWordList) {

            String searchWord = hotWord.getSearchword();
            hotWords.add(searchWord);
        }

        // 给集合中的数据排序使页面好看一些
        Collections.sort(hotWords);

        // 更新UI
        mFlowTextLayout.setTextContents(hotWords);

    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {
        // 处理加载更多的结果 ①不管结果如何首先先结束加载更多
        mRefreshLayout.finishLoadmore();

        // 判断是否加载跟多成功
        if (isOkay) {
            handleSearchResult(result);
        } else {
            Toast.makeText(SearchActivity.this, "没有更多内容", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 联想关键字请求成功
     *
     * @param keyWordList
     */
    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {

        Log.e(TAG, "keyWordList size -->" + keyWordList.size());

        // 搜索推荐（联想关键字）循环视图的适配器 设置刷新数据
        mRecommendAdpter.setData(keyWordList);

        // 控制UI的状态(哪些控件该隐藏哪些控件该显示)
        mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
        hideSuccessView();

        // 显示搜索联想关键字的Recycler要显示
        mSearchRecommendList.setVisibility(View.VISIBLE);

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            // 加载器切换到请求错误的界面
            mUILoader.updateStatus(UILoader.UIStatus.NEWWORK_ERROR);
        }
    }

    private void hideSuccessView() {

        // 显示搜索联想关键字的Recycler要隐藏
        mSearchRecommendList.setVisibility(View.GONE);

        // 显示搜索内容的RecyclerView要隐藏
        mRefreshLayout.setVisibility(View.GONE);

        // 显示热词的自定义view要隐藏
        mFlowTextLayout.setVisibility(View.GONE);
    }

    /**
     * 显示内容的RecyclerView item 点击事件的回调
     *
     * @param clickPosition
     * @param album
     */
    @Override
    public void onItemClick(int clickPosition, Album album) {
        AlbumDetailPresenter.getsInstance().setTargetAlbum(album);
        // recycleview 中的 某个item被点击了,跳转到对应的详情页
        Intent intent = new Intent(this, DetailActivity.class);
        startActivity(intent);
    }
}
