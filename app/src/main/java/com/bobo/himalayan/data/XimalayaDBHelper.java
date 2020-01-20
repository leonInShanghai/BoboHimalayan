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

        // 创建数据表 订阅相关的字段: 图片，title，描述，节目数量，作者名称（详情界面）专辑id   primary key autoincrement: 自增
        String subTbSql = "create table " + Constants.SUB_TB_NAME + "(" + ""
                + Constants.SUB_ID + " integer primary key autoincrement,"
                + Constants.SUB_COVER_URL + " varchar,"
                + Constants.SUB_TITLE + " varchar,"
                + Constants.SUB_DESCRIPTION + " varchar,"
                + Constants.SUB_TRACKSCOUNT + " integer,"
                + Constants.SUB_PLAYCOUNT + "tracksCount integer,"
                + Constants.SUB_AUTHORNAME + " varchar,"
                + Constants.SUB_ALBUMID + " integer" + ");";

        // 执行建表语句
        db.execSQL(subTbSql);

        // test data
        String testSql = "insert into subTB(title,description) values ('title','descrption.')";

        for (int i = 0; i < 10000; i++) {
            db.execSQL(testSql);
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
