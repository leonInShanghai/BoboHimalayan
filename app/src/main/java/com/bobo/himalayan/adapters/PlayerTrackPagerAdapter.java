package com.bobo.himalayan.adapters;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import java.util.ArrayList;
import java.util.List;
import com.bobo.himalayan.R;

/**
 * Created by 微信公众号IT波 on 2019/12/7. Copyright © Leon. All rights reserved.
 * Functions: 播放器页面中间 展示每集封面的 viewpage的适配器
 */
public class PlayerTrackPagerAdapter extends PagerAdapter {

    //适配器的数据源
    private List<Track> mData = new ArrayList<>();

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_track_pager,container,
                false);
        container.addView(itemView);

        //设置数据 - 找到控件
        ImageView item = itemView.findViewById(R.id.track_pager_item);

        //设置图片
        Track track = mData.get(position);
        String coverUrlLarge = track.getCoverUrlLarge();
        //Picasso.with(container.getContext()).load(coverUrlLarge).placeholder(R.mipmap.logo).into(item);
        if (TextUtils.isEmpty(coverUrlLarge)){
            item.setImageResource(R.mipmap.logo);
        }else {
            Picasso.with(container.getContext()).load(coverUrlLarge).into(item);
        }
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        //super.destroyItem(container, position, object);
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<Track> list) {
        mData.clear();//清空原来的数据
        mData.addAll(list);//添加新的数据
        notifyDataSetChanged();//刷新UI界面
    }
}
