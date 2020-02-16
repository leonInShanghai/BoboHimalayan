package com.bobo.himalayan.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bobo.himalayan.utils.Constants;

/**
 * Created by Leon on 2020-01-12 Copyright © Leon. All rights reserved.
 * Functions: 订阅： 数据库操作类
 */
public class XimalayaDBHelper extends SQLiteOpenHelper {

    public XimalayaDBHelper(Context context) {

        /**
         * 第二个参数name：数据库的名字
         * 第三个参数，factory：游标工厂  传null使用默认
         * 第四个参数，version：版本号
         */
        super(context, Constants.DB_NAME, null, Constants.DB_VERSION_CODE);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.e("onCreate", "开始创建表");

        /**
         * 创建数据表 订阅相关的字段: 图片，title，描述，节目数量，作者名称（详情界面）专辑id
         * primary key autoincrement: 自增
         * Constants.SUB_PLAY_COUNT + "tracksCount integer,"
         */
        String subTbSql = "create table " + Constants.SUB_TB_NAME + "(" + ""
                + Constants.SUB_ID + " integer primary key autoincrement,"
                + Constants.SUB_COVER_URL + " varchar,"
                + Constants.SUB_TITLE + " varchar,"
                + Constants.SUB_DESCRIPTION + " varchar,"
                + Constants.SUB_TRACKS_COUNT + " integer,"
                + Constants.SUB_PLAY_COUNT + " integer,"
                + Constants.SUB_AUTHOR_NAME + " varchar,"
                + Constants.SUB_ALBUM_ID + " integer" + ");";

        // 执行建表语句
        db.execSQL(subTbSql);

        /**
         * <column definition name> or <table constraint> expected, got
         * 最后一个字段不要带 ，号
         */
        // 创建历史记录表
        String historyTbSql = "create table " + Constants.HISTORY_TB_NAME + "(" + ""
                + Constants.HISTORY_ID + " integer primary key autoincrement,"
                + Constants.HISTORY_TRACK_ID + " integer,"
                + Constants.HISTORY_TITLE + " varchar,"
                + Constants.HISTORY_COVER+ " varchar,"
                + Constants.HISTORY_PLAY_COUNT + " integer,"
                + Constants.HISTORY_DURATION + " integer,"
                + Constants.HISTORY_UPDATE_TIME + " integer,"
                + Constants.HISTORY_AUTHOR + " varchar,"
                + Constants.HISTORY_ORDER_NUMBER + " integer" +");";



        // 执行建表语句
        db.execSQL(historyTbSql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
