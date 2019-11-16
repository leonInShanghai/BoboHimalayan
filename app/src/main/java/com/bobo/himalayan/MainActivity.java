package com.bobo.himalayan;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Map<String,String> map = new HashMap<>();

        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(@Nullable CategoryList categoryList) {
                List<Category> categories = categoryList.getCategories();

                if (categories != null) {
                    int size = categories.size();
                    Log.e(TAG,"categories size ---->"+categories);

                    //增强for循环快捷键 iter
                    for (Category category : categories) {
                        Log.e(TAG,"category -->"+category.getCategoryName());
                    }

                }
            }

            @Override
            public void onError(int i, String s) {
                Log.e(TAG,"error code-- "+i+"error message ==>"+s);
            }
        });

    }
}
