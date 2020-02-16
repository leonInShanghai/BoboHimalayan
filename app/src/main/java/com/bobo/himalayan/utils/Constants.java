package com.bobo.himalayan.utils;

/**
 * Created by Leon on 2019/11/16. Copyright © Leon. All rights reserved.
 * Functions:
 */
public class Constants {

    /**
     * 获取推荐列表的专辑数量
     */
    public static int COUNT_RECOMMAND = 50;

    /**
     * 详情页recycleView展示的数据 一次请求的数量
     */
    public static int COUNT_DEFAULT = 50;

    /**
     * 热词的搜索数量1-20
     */
    public static int COUNT_HOT_WORD = 10;

    /**
     * 订阅（最先用到）数据库的名字"ximala.db"
     */
    public static final String DB_NAME = "bobo_ximala.db";

    /**
     * 数据库的版本
     */
    public static final int DB_VERSION_CODE = 1;

    /**
     * 订阅的表名
     */
    public static final String SUB_TB_NAME = "tb_subscription";

    /**
     * 订阅表中id
     */
    public static final String SUB_ID = "_id";

    /**
     * 订阅表中coverUrl
     */
    public static final String SUB_COVER_URL = "coverUrl";

    /**
     * 订阅表中title
     */
    public static final String SUB_TITLE = "title";

    /**
     * 订阅表中description
     */
    public static final String SUB_DESCRIPTION = "description";

    /**
     * 订阅表中tracksCount
     */
    public static final String SUB_TRACKS_COUNT = "tracksCount";

    /**
     * 订阅表中playCount
     */
    public static final String SUB_PLAY_COUNT = "playCount";

    /**
     * 订阅表中authorName
     */
    public static final String SUB_AUTHOR_NAME = "authorName";

    /**
     * 订阅表中albumId
     */
    public static final String SUB_ALBUM_ID = "albumId";

    /**
     * 订阅最多个数不能超过100条
     */
    public static final int MAX_SUB_COUNT = 100;

    /**
     * 历史记录的表名
     */
    public static final String HISTORY_TB_NAME = "tb_history";

    /**
     * 历史表中的id
     * 原来：_id  编译报错
     */
    public static final String HISTORY_ID = "_id";

    /**
     * 历史表中章节（集）的id
     */
    public static final String HISTORY_TRACK_ID = "historyTrackId";

    /**
     * 历史表中的标题
     */
    public static final String HISTORY_TITLE = "historyTitle";

    /**
     * 历史表中的播放次数
     */
    public static final String HISTORY_PLAY_COUNT = "historyPlayCount";

    /**
     * 历史表中的播放时长
     */
    public static final String HISTORY_DURATION = "historyDuration";

    /**
     * 历史表中更新的时间
     */
    public static final String HISTORY_UPDATE_TIME = "historyUpdateTime";

    /**
     * 历史列表中章节（集）封面的URL
     */
    public static final String HISTORY_COVER = "historyCover";

    /**
     * 历史列表中 订单序号 （展示在item最左边序号）
     */
    public static final String HISTORY_ORDER_NUMBER = "history_order_nubber";


    /**
     * 历史列表中 作者
     */
    public static final String HISTORY_AUTHOR = "history_author";

    /**
     * 历史数据 最大保存到数据库的数量
     */
    public static final int MAX_HISTORY_COUNT = 100;



}
