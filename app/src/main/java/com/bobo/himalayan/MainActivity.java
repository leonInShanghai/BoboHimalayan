package com.bobo.himalayan;

import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.bobo.himalayan.adapters.IndicatorAdapter;
import com.bobo.himalayan.adapters.MainContentAdapter;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    public static final String TAG = "MainActivity";

    //千变万化的viewpager指示器
    private MagicIndicator mMagicIndicator;

    //显示首页内容的view pager
    private ViewPager mContentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        mMagicIndicator = (MagicIndicator) findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(getResources().getColor(R.color.main_color));

        //创建indicator的适配器
        IndicatorAdapter adapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(adapter);

        //创建viewpager
        mContentPager = (ViewPager)findViewById(R.id.content_pager);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(mainContentAdapter);

        //绑定指示器和view pager
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator,mContentPager);
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
