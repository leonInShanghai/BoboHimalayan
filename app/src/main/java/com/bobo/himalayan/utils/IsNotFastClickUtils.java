package com.bobo.himalayan.utils;

/**
 * Created by 公众号IT波  on 2019/6/3. Copyright © Leon. All rights reserved.
 * Functions: 判断用户是否是第一次点击的工具类（避免用户重复点击开启2个页面）
 */
public class IsNotFastClickUtils {

    public static final int DELAY = 1200;
    private static long lastClickTime = 0;

    public static boolean isNotFastClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > DELAY) {
            lastClickTime = currentTime;
            return true;
        } else {
            return false;
        }
    }

}
