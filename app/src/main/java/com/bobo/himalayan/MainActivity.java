package com.bobo.himalayan;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobo.himalayan.adapters.IndicatorAdapter;
import com.bobo.himalayan.adapters.MainContentAdapter;
import com.bobo.himalayan.data.XimalayaDBHelper;
import com.bobo.himalayan.interfaces.IPlayerCallback;
import com.bobo.himalayan.presenters.PlayerPresenter;
import com.bobo.himalayan.presenters.RecommendPresenter;
import com.bobo.himalayan.utils.IsNotFastClickUtils;
import com.bobo.himalayan.utils.LogUtil;
import com.bobo.himalayan.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

public class MainActivity extends FragmentActivity implements IPlayerCallback {

    public static final String TAG = "MainActivity";

    //千变万化的viewpager指示器
    private MagicIndicator mMagicIndicator;

    //显示首页内容的view pager
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;

    // 最下面的播放器相关-最左边的 icon
    private RoundRectImageView mRoundRectImageView;

    // 播放控制相关的-中间上面大标题
    private TextView mHeaderTitle;

    // 播放控制相关的-中间下面小标题（作者）
    private TextView mSubTitle;

    // 播放控制相关的-右边播放按钮
    private ImageView mPlayControl;


    private PlayerPresenter mPlayerPresenter;
    private View mPlayControlItem;

    /**
     * 右上角的搜索🔍按钮
     */
    private View mSearchBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initEvent();

        initPresenter();
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtil.e(TAG, "click inex is -->" + index);
                if (mContentPager != null) {
                    mContentPager.setCurrentItem(index);
                }
            }
        });

        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean hasPlayList = mPlayerPresenter.hasPlayList();
                if (!hasPlayList) {

                    /**
                     * 没有设置播放列表就播放默认的第一个推荐专辑
                     * 第一个推荐专辑每天都会变的
                     */
                    playFirstRecommend();

                } else {
                    // 有播放列表
                    if (mPlayerPresenter.isPlaying()) {
                        mPlayerPresenter.pause();
                    } else {
                        mPlayerPresenter.play();
                    }
                }

            }
        });

        mPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 处理用户多次点击开启重复的页面
                if (!IsNotFastClickUtils.isNotFastClick()) {
                    return;
                }

                /**
                 * 底部播放器容器整体被点击了，跳转到播放器界面
                 * 跳转前也要判断有没有播放列表
                 */
                boolean hasPlayList = mPlayerPresenter.hasPlayList();

                if (!hasPlayList) {
                    playFirstRecommend();
                }

                startActivity(new Intent(MainActivity.this, PlayerActivity.class));

            }
        });

        // 右上角的搜索按钮点击事件的监听-跳转到搜索页面
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 处理用户多次点击开启重复的页面
                if (!IsNotFastClickUtils.isNotFastClick()) {
                    return;
                }

                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 播放第一个推荐的内容
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getsInstance().getCurrentRecommend();

        if (currentRecommend != null && currentRecommend.size() > 0) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }

    }

    private void initView() {
        mMagicIndicator = findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(getResources().getColor(R.color.main_color));

        //创建indicator的适配器
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);//自我调节平分位置
        commonNavigator.setAdapter(mIndicatorAdapter);

        //创建viewpager
        mContentPager = findViewById(R.id.content_pager);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(mainContentAdapter);

        //绑定指示器和view pager
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);

        // 播放控制相关的-左边的图标
        mRoundRectImageView = findViewById(R.id.main_track_cover);

        // 播放控制相关的-中间上面大标题
        mHeaderTitle = findViewById(R.id.mian_head_title);
        // setSelected(true)设置跑马灯效果
        mHeaderTitle.setSelected(true);

        // 播放控制相关的-中间下面小标题（作者）
        mSubTitle = findViewById(R.id.main_sub_title);

        // 播放控制相关的-右边播放按钮
        mPlayControl = findViewById(R.id.main_play_control);

        // 播放控制相关的-整个容器
        mPlayControlItem = findViewById(R.id.main_play_control_item);

        // 右上角的搜索按钮
        mSearchBtn = findViewById(R.id.search_btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onPlayStart() {
        updatePlayControl(true);
    }

    private void updatePlayControl(boolean isPlaying) {

        mPlayControl.setImageResource(isPlaying ? R.drawable.selector_palyer_pause : R.drawable.selector_palyer_play);

    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
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
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();

            // 设置到专辑下每一小节的名称
            if (!TextUtils.isEmpty(trackTitle)) {
                mHeaderTitle.setText(trackTitle);
            }

            // 设置作者名称
            if (!TextUtils.isEmpty(nickname)) {
                mSubTitle.setText(nickname);
            }

            // 设置icon
            if (!TextUtils.isEmpty(coverUrlMiddle)) {
                Picasso.with(MainActivity.this).load(coverUrlMiddle).into(mRoundRectImageView);
            }

        }

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}


//    Map<String,String> map = new HashMap<>();
//
//        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
//@Override
//public void onSuccess(@Nullable CategoryList categoryList) {
//        List<Category> categories = categoryList.getCategories();
//
//        if (categories != null) {
//        int size = categories.size();
//        Log.e(TAG,"categories size ---->"+categories);
//
//        //增强for循环快捷键 iter
//        for (Category category : categories) {
//        Log.e(TAG,"category -->"+category.getCategoryName());
//        }
//
//        }
//        }
//
//@Override
//public void onError(int i, String s) {
//        Log.e(TAG,"error code-- "+i+"error message ==>"+s);
//        }
//        });
