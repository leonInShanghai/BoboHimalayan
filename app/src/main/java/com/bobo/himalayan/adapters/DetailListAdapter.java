package com.bobo.himalayan.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import com.bobo.himalayan.R;

/**
 * Created by 公众号IT波 on 2019/11/24. Copyright © Leon. All rights reserved.
 * Functions: 点击推荐条目进入详情页 recycleview的适配器
 *    https://www.bilibili.com/video/av69452769?p=28
 */
public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {

    //recycleview的数据源
    private List<Track> mDetailData = new ArrayList<>();

    //格式化时间将 时间戳 转换为正常的时间
    private SimpleDateFormat mUpdateDateFormat = new SimpleDateFormat( "yyyy-MM-dd");

    //将秒转换为 分秒
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat( "mm:ss");

    //供外界调用的点击事件接口
    private ItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public DetailListAdapter.InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_detail, parent,
                false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailListAdapter.InnerHolder holder, final int position) {

        //实例化控件再设置数据
        View itemView = holder.itemView;

        //顺序id
        TextView ordetTv = itemView.findViewById(R.id.order_text);

        //标题
        TextView titleTv = itemView.findViewById(R.id.detail_item_title);

        //播放的次数
        TextView playCountTv = itemView.findViewById(R.id.detail_item_play_count);

        //时长
        TextView durationTv = itemView.findViewById(R.id.detail_item_duration);

        //更新日期
        TextView updateDateTv = itemView.findViewById(R.id.dwtail_item_update_time);

        //设置数据
        Track track = mDetailData.get(position);

        //设置数据
        ordetTv.setText(track.getOrderNum()+"");
        titleTv.setText(track.getTrackTitle());
        playCountTv.setText(track.getPlayCount()+"");
        //先转换时间格式 （（秒 * 1000） 转换格式）再设置显示 text
        int durationMil = track.getDuration() * 1000;
        String duration = mDurationFormat.format(durationMil);
        durationTv.setText(duration);
        //先转换时间格式再设置显示 text
        String updateTimeText = mUpdateDateFormat.format(track.getUpdatedAt());
        updateDateTv.setText(updateTimeText);

        //设置item的点击事件监听
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击事件通过接口传递到外界
                if (mItemClickListener != null) {
                    //参数需要有列表位置
                    mItemClickListener.onItemClick(mDetailData,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailData == null ? 0 : mDetailData.size();
    }

    /**供外界调用的设置数据源的方法(不适合上来加载更多)*/
    public void setData(List<Track> tracks) {
        //清除原来的数据
        mDetailData.clear();
        //添加新的数据
        mDetailData.addAll(tracks);
        //recycle view 自带的 刷新ui的方法
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * RecycleView 传递点击事件的接口 供外界调用的set方法
     */
    public void setItemClickListener(ItemClickListener listener){
        mItemClickListener = listener;
    }

    /**
     * RecycleView 传递点击事件的接口
     */
    public interface ItemClickListener{

        /**
         * 用户点击 RecycleView的其中一个 item 进入播放页面
         * @param detailData
         * @param position
         */
        void onItemClick(List<Track> detailData, int position);
    }
}
