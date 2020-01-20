package com.bobo.himalayan.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import com.bobo.himalayan.R;

/**
 * Created by Leon on 2019-12-21 Copyright © Leon. All rights reserved.
 * Functions: 播放页右下角弹框中间的 RecycleView 的适配
 */
public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {


    //数据源集合
    private List<Track> mData = new ArrayList<>();

    //当前正在播放的位置默认为0
    private int playIndex = 0;

    //某个item被点击回调接口
    private SobPopWindow.PlayListItemClickListener mItemClickListener = null;


    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_play_list, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, final int position) {

        //item点击事件的监听处理
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {

                    //回调传递被点击的item的索引
                    mItemClickListener.itemClick(position);
                }
            }
        });

        //设置数据
        Track track = mData.get(position);


        TextView trackTitle = holder.itemView.findViewById(R.id.track_title_tv);

        //设置播放中和非播放中的文字颜色
        trackTitle.setTextColor(BaseApplication.getAppContext().getResources().getColor(playIndex == position ? R.color.
                second_color : R.color.play_list_text_color));

        //设置播放小节（集）标题
        trackTitle.setText(track.getTrackTitle());

        //实例化播放状态的ImageView 即 找到播放状态的图标
        ImageView playingIconView = holder.itemView.findViewById(R.id.paly_icon_iv);
        playingIconView.setVisibility(playIndex == position ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {

        return mData == null ? 0 : mData.size();
    }

    /**
     * 供外界调用的RecycleView设置数据源的方法
     *
     * @param data
     */
    public void setData(List<Track> data) {

        //设置数据更新列表
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    /**
     * 供外界调用的设置正在播放的小节（集）的索引
     *
     * @param position
     */
    public void setCurrengPlayPosition(int position) {
        playIndex = position;
        notifyDataSetChanged();
    }

    /**
     * 供外界实现回调的RecyclerView item 点击事件的监听
     *
     * @param listener
     */
    public void setOnItemClickListener(SobPopWindow.PlayListItemClickListener listener) {

        this.mItemClickListener = listener;

    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
