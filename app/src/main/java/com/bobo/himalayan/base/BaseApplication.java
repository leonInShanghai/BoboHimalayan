package com.bobo.himalayan.base;

import android.app.Application;
import android.util.Log;

import com.bobo.himalayan.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;

/**
 * Created by 求知自学网 on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions:
 */
public class BaseApplication extends Application {




    @Override
    public void onCreate() {
        super.onCreate();

        //初始化喜马拉雅SDK
        CommonRequest mXimalaya = CommonRequest.getInstanse();
        if (DTransferConstants.isRelease){
            String mAppSecret = "8646d66d6abe2efd14f2891f9fd1c8af";
            mXimalaya.setAppkey("9f9ef8f10bebeaa83e71e62f935bede8");
            mXimalaya.setPackid("com.app.test.android");
            mXimalaya.init(this, mAppSecret);
            Log.e("BaseApplication","isRelease");
        }else{
            String mAppSecret = "0a09d7093bff3d4947a5c4da0125972e";
            mXimalaya.setAppkey("f4d8f65918d9878e1702d49a8cdf0183");
            mXimalaya.setPackid("com.ximalaya.qunfeng");
            mXimalaya.init(this, mAppSecret);
            Log.e("BaseApplication","isDebug");
        }

        //初始化自定义log  LogUtil
        LogUtil.init(this.getPackageName(),false);

        }
}
