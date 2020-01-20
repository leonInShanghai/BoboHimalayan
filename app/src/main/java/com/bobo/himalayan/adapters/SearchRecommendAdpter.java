package com.bobo.himalayan.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bobo.himalayan.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2020-01-05 Copyright © Leon. All rights reserved.
 * Functions: 搜索推荐（联想关键字）循环视图的适配器
 */
public class SearchRecommendAdpter extends RecyclerView.Adapter<SearchRecommendAdpter.InnerHolder> {

    private List<QueryResult> mData = new ArrayList<>();

    /**
     * 点击事件回调接口
     */
    private ItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {

        // 从XML文件中加载布局文件
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_tecommend, parent, false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {

        TextView text = holder.itemView.findViewById(R.id.search_recommend_item);

        final QueryResult queryResult = mData.get(position);

        text.setText(queryResult.getKeyword());

        // 监听点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(queryResult.getKeyword());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    /**
     * 设置/刷新数据
     *
     * @param keyWordList 数据源
     */
    public void setData(List<QueryResult> keyWordList) {

        // 先清空原来的数据
        if (mData != null && mData.size() > 0) {
            mData.clear();
        }

        // 再添加新的数据
        mData.addAll(keyWordList);

        // 刷新UI界面
        notifyDataSetChanged();

    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface ItemClickListener {
        void onItemClick(String keyword);
    }

}
