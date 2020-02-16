package com.bobo.himalayan.views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bobo.himalayan.R;

/**
 * Created by Leon on 2020-02-01 Copyright © Leon. All rights reserved.
 * Functions: 确认是否要删除订阅的dialog
 */
public class ConfirmDialog extends Dialog {

    private TextView mCancelSub;
    private TextView mGiveUp;

    // 点击事件回调接口
    private OnDialogActionClickListener mClickListener = null;

    public ConfirmDialog(Context context) {
        this(context, 0);
    }

    public ConfirmDialog(Context context, int themeResId) {
         // super(context, themeResId);

         // 第二个参数代表点击空白处对话框是否消失
         this(context, false,null);
    }

    protected ConfirmDialog(Context context, boolean cancelable, DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_confirm);

        initView();

        initListener();
    }

    // 实例化子控件
    private void initView() {

        // 取消订阅按钮
        mCancelSub = findViewById(R.id.dialog_cancel_sub);


        // 我再想想按钮
        mGiveUp = findViewById(R.id.dialog_give_up_tv);
    }

    // 处理点击事件
    private void initListener() {

        // 用户点击了我再想想
        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mClickListener != null) {
                    mClickListener.onGiveUpClick();
                    dismiss();
                }
            }
        });

        // 用户点击了取消订阅
        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onCancelSubClick();
                    dismiss();
                }
            }
        });
    }

    /**
     * 供外界设置点击事件的公开方法
     * @param listener
     */
    public void setOnDialogActionClickListener(OnDialogActionClickListener listener) {
        mClickListener = listener;
    }

    /**
     * 点击事件回调接口
     */
    public interface OnDialogActionClickListener{

        /**
         * 用户点击了取消订阅
         */
        void onCancelSubClick();

        /**
         * 用户点击了我再想想
         */
        void onGiveUpClick();
    }
}
