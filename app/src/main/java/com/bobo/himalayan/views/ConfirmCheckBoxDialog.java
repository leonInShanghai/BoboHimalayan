package com.bobo.himalayan.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bobo.himalayan.R;

/**
 * Created by Leon on 2020-02-01 Copyright © Leon. All rights reserved.
 * Functions: 确认是否要删除历史的dialog
 */
public class ConfirmCheckBoxDialog extends Dialog {

    private TextView mCancelSub;
    private TextView mGiveUp;

    // 点击事件回调接口
    private OnDialogActionClickListener mClickListener = null;

    // 全部删除选择框
    private CheckBox mCheckBox;

    public ConfirmCheckBoxDialog(Context context) {
        this(context, 0);
    }

    public ConfirmCheckBoxDialog(Context context, int themeResId) {
         // super(context, themeResId);

         // 第二个参数代表点击空白处对话框是否消失
         this(context, false,null);
    }

    protected ConfirmCheckBoxDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_check_box_confirm);

        initView();

        initListener();
    }

    // 实例化子控件
    private void initView() {

        // 确认删除按钮
        mCancelSub = findViewById(R.id.dialog_check_box_confirm);


        // 我再想想按钮
        mGiveUp = findViewById(R.id.dialog_check_box_cancle);

        // 全部删除选择框
        mCheckBox = findViewById(R.id.dialog_check_box);
    }

    // 处理点击事件
    private void initListener() {

        // 用户点击了我再想想
        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mClickListener != null) {
                    mClickListener.onGiveUpClick();

                    // mCheckBox.setChecked(false);
                    dismiss();
                }
            }
        });

        // 用户点击了取消订阅
        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {

                    // 将用户是否勾选删除全部传递出去
                    boolean checked = mCheckBox.isChecked();

                    mClickListener.onCancelSubClick(checked);
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
         * @param checked
         */
        void onCancelSubClick(boolean checked);

        /**
         * 用户点击了我再想想
         */
        void onGiveUpClick();
    }
}
