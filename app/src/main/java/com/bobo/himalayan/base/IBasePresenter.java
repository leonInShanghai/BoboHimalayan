package com.bobo.himalayan.base;

/**
 * Created by 微信公众号IT波 on 2019/12/1. Copyright © Leon. All rights reserved.
 * Functions: Presenter 接口的基础类 用于抽取重复要写的冗余代码
 */
public interface IBasePresenter<T> {

    /**
     * 注册UI的回调接口
     * @param
     */
    void registerViewCallback(T t);

    /**
     * （取消注册）删除UI通知接口
     * @param
     */
    void unRegisterViewCallback(T t);

}
