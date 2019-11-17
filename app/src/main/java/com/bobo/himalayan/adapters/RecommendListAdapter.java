package com.bobo.himalayan.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bobo.himalayan.R;

import com.bobo.himalayan.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions: 首页 recycleview的适配器
 */
public class RecommendListAdapter extends RecyclerView.Adapter<RecommendListAdapter.InnerHolder> {

    private List<Album> mData = new ArrayList<>();

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //实例化视图
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,
                false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //绑定数据
        holder.itemView.setTag(position);
        holder.setData(mData.get(position));

    }

    @Override
    public int getItemCount() {
        //返回recycleview要显示的个数
        return mData == null ? 0 : mData.size();
    }

    public void setData(List<Album> albumList) {
        if (mData != null){
            mData.clear();
            mData.addAll(albumList);
        }

        //刷新ui
        notifyDataSetChanged();
    }

    class InnerHolder extends RecyclerView.ViewHolder {

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {

            //找到控件设置数据 - 专辑的封面
            RoundRectImageView albumCoverIv = itemView.findViewById(R.id.album_conver);

            //标题（title）
            TextView albumTitleTv = itemView.findViewById(R.id.album_title_tv);

            //描述
            TextView albumDesTv = itemView.findViewById(R.id.album_description_tv);

            //播放数量
            TextView albumPlayCount = itemView.findViewById(R.id.album_play_count);

            //专辑内容数量
            TextView albumContentCountTv = itemView.findViewById(R.id.album_content_size);

            //设置数据 模型 喜马拉雅已经建好了文档上有  本类最下面是用的的部分
            albumTitleTv.setText(album.getAlbumTitle());
            albumDesTv.setText(album.getAlbumIntro());
            albumPlayCount.setText(String.valueOf(album.getPlayCount()));
            albumContentCountTv.setText(album.getIncludeTrackCount()+"");

            Picasso.with(itemView.getContext()).load(album.getCoverUrlLarge()).placeholder(R.mipmap.logo)
                    .into(albumCoverIv);
        }
    }
}

/**
 *
 字段名

 类型

 描述


 id Int ID
 kind String 固定值"album"
 category_id Int 分类ID，为-1时表示分类未知
 album_title String 专辑名称
 album_tags String 专辑标签列表
 album_intro String 专辑简介
 cover_url_small String 专辑封面小，无则返回空字符串””
 cover_url_middle String 专辑封面中，无则返回空字符串””
 cover_url_large String 专辑封面大，无则返回空字符串””
 announer JSON 专辑所属主播信息，包括id（主播用户ID）、nickname（昵称）、avatar_url（头像）和is_verified（
 是否加V）和updated_at 主播更新时间created_at主播创建时间
 play_count Int 专辑播放次数
 favorite_count Int 专辑喜欢数
 include_track_count Int 专辑包含声音数
 last_uptrack JSON 专辑中最新上传的一条声音信息，包括track_id、track_title、duration、created_at、updated_at
 字段
 is_finished Int 是否完结，0-无此属性；1-未完结；2-完结
 can_download Bool 能否下载，true-可下载，false-不可下载
 updated_at Long 专辑最后更新时间，Unix毫秒数时间戳
 created_at Long 专辑创建时间，Unix毫秒数时间戳
 canDownload Bool 专辑是否可以下载
 subscribe_count Long 专辑订阅数
 tracks_natural_ordered Bool 专辑内声音排序是否自然序，自然序是指先上传的声音在前面，晚上传的声音在后面
 is_paid Bool 是否付费
 estimated_track_count Int 预计更新多少集
 album_rich_intro String 专辑富文本简介
 speaker_intro String 主讲人介绍
 free_track_count Int 专辑内包含的整条免费听声音总数
 free_track_ids String 专辑内包含的整条免费声音ID列表，英文逗号分隔
 sale_intro String 营销简介
 expected_revenue String 对应喜马拉雅APP上的“你将获得”，主要卖点，是由UGC主播提供的富文本
 buy_notes String 购买须知，富文本
 speaker_title String 主讲人自定义标题
 speaker_content String 主讲人自定义标题下的内容
 has_sample Bool 是否支持试
 composed_price_type Int 支持的购买类型，1-只支持分集购买，2-只支持整张专辑购买，3-同时支持分集购买和整
 张专辑购买
 price_type_detail JSON Array 支持的详细价格模型列表，每种价格模型包括price_type（1-分集购买，2-整张专
 辑购买）、price（Double，原价）、discounted_price（Double，折后价）、price_unit（String，价格单位）
 detail_banner_url String 付费专辑详情页焦点图，无则返回空字符串””
 album_score String 专辑评分

 */
