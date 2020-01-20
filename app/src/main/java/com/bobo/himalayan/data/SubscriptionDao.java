package com.bobo.himalayan.data;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by Leon on 2020-01-12 Copyright © Leon. All rights reserved.
 * Functions: 数据库操作类
 */
public class SubscriptionDao implements ISubDao {

    private static final SubscriptionDao ourInstance = new SubscriptionDao();
    private final XimalayaDBHelper mXimalayaDBHelper;

    public static SubscriptionDao getInstance() {
        return ourInstance;
    }

    private SubscriptionDao() {
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void addAlbum(Album album) {

        SQLiteDatabase db = null;

        try {
            db = mXimalayaDBHelper.getWritableDatabase();

            db.beginTransaction();

            ContentValues contentValues = new ContentValues();

            // 封装数据 9 5:00
            //  contentValues.put();

            db.insert(Constants.SUB_TB_NAME, null, contentValues);

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (db != null) {

                db.endTransaction();

                // 数据库一定要关闭不然会内存泄漏
                db.close();
            }

        }
    }

    @Override
    public void delAlbum(Album album) {

    }

    @Override
    public void listAlbums() {

    }
}
