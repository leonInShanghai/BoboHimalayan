package com.bobo.himalayan.data;

import com.bobo.himalayan.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Leon on 2019-12-29 Copyright © Leon. All rights reserved.
 * Functions: 请求参数api
 */
public class XimalayaApi {


    private static XimalayaApi sXimalayaApi;


    /**
     * 私有构造方法单例标配
     */
    private XimalayaApi() {

    }

    public static XimalayaApi getXimalayaApi() {

        // 懒汉式单例务必要加锁
        if (sXimalayaApi == null) {
            synchronized (XimalayaApi.class) {
                if (sXimalayaApi == null) {
                    sXimalayaApi = new XimalayaApi();
                }
            }
        }

        return sXimalayaApi;
    }


    /**
     * 获取推荐内容
     *
     * @param callBack 请求结果的回调
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack) {

        // 获取推荐内容 封装参数
        Map<String, String> map = new HashMap<>();

        //这个参数表示一页数据返回多少条
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMAND + "");

        CommonRequest.getGuessLikeAlbum(map, callBack);
    }

    /**
     * 根据专辑id获取到专辑内容
     *
     * @param callBack  获取专辑详情的回调
     * @param albumId   专辑的id
     * @param pageindex 第几页
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callBack, long albumId, int pageindex) {
        //根据页码和专辑的id获取列表数据
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SORT, "asc");
        // 根据页码和专辑的id获取列表数据 *id
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        // 根据页码和专辑的id获取列表数据 *页码
        map.put(DTransferConstants.PAGE, pageindex + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map, callBack);
    }

    /**
     * 根据关键字，进行搜索
     *
     * @param keyword 关键字
     * @param page    分页 注意：从1开始不是从0开始的
     */
    public void searchByKeyWord(String keyword, int page, IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page + "");

        // 一页显示多少内容
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getSearchedAlbums(map, callback);
    }

    /**
     * 获取推荐热词
     *
     * @param callback
     */
    public void getHotWords(IDataCallBack<HotWordList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, String.valueOf(Constants.COUNT_HOT_WORD));
        CommonRequest.getHotWords(map, callback);
    }

    /**
     * 根据关键字获取联想词
     *
     * @param keyword  关键字
     * @param callback 回调接口
     */
    public void getSuggestWord(String keyword, IDataCallBack<SuggestWords> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map, callback);
    }
}
