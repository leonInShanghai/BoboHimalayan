package com.bobo.himalayan.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2020-01-12 Copyright © Leon. All rights reserved.
 * Functions: 数据库操作类
 */
public class SubscriptionDao implements ISubDao {

    private static final SubscriptionDao ourInstance = new SubscriptionDao();

    private static final String TAG = "SubscriptionDao";

    private final XimalayaDBHelper mXimalayaDBHelper;

    /** 回调接口 */
    private ISubDaoCallback mCallback = null;

    public static SubscriptionDao getInstance() {
        return ourInstance;
    }

    private SubscriptionDao() {
        mXimalayaDBHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(ISubDaoCallback callback) {
        this.mCallback = callback;
    }

    @Override
    public void addAlbum(Album album) {

        SQLiteDatabase db = null;
        boolean isAddSuccess = false;

        try {
            db = mXimalayaDBHelper.getWritableDatabase();

            db.beginTransaction();

            ContentValues contentValues = new ContentValues();

            // 封装数据 9 5:00
            contentValues.put(Constants.SUB_COVER_URL, album.getCoverUrlLarge());
            contentValues.put(Constants.SUB_TITLE, album.getAlbumTitle());
            contentValues.put(Constants.SUB_DESCRIPTION, album.getAlbumIntro());
            contentValues.put(Constants.SUB_TRACKS_COUNT, album.getIncludeTrackCount());
            contentValues.put(Constants.SUB_PLAY_COUNT, album.getPlayCount());
            contentValues.put(Constants.SUB_AUTHOR_NAME, album.getAnnouncer().getNickname());
            contentValues.put(Constants.SUB_ALBUM_ID, album.getId());

            // 插入数据
            db.insert(Constants.SUB_TB_NAME, null, contentValues);
            db.setTransactionSuccessful();
            isAddSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            // 添加失败
            isAddSuccess = false;
        } finally {

            if (db != null) {

                db.endTransaction();

                // 数据库一定要关闭不然会内存泄漏
                db.close();
            }

            // 添加成功
            mCallback.onAddResult(isAddSuccess);
        }
    }

    /**
     * 删除数据库操作
     * @param album
     */
    @Override
    public void delAlbum(Album album) {
        SQLiteDatabase db = null;
        boolean isDeleteSuccess = false;

        try {
            db = mXimalayaDBHelper.getWritableDatabase();

            db.beginTransaction();

            // 删除数据 第一个参数是表名 第二个参数是删除语句
            int delete = db.delete(Constants.SUB_TB_NAME,  Constants.SUB_ALBUM_ID + "?", new String[]
                    {album.getId() + "" });

            Log.e(TAG, "delete--->" + delete);

            db.setTransactionSuccessful();

            isDeleteSuccess = true;
        } catch (Exception e) {
            e.printStackTrace();
            isDeleteSuccess = false;
        } finally {

            if (db != null) {

                db.endTransaction();

                // 数据库一定要关闭不然会内存泄漏
                db.close();
            }
            // 删除成功
            mCallback.onDelResult(isDeleteSuccess);
        }
    }

    /**
     * 获取数据库中的所有数据
     */
    @Override
    public void listAlbums() {

        // FIXME:消灭这里bug
        SQLiteDatabase db = null;

        List<Album> result = new ArrayList<>();

        try {
            db = mXimalayaDBHelper.getWritableDatabase();
            db.beginTransaction();

            // 获取所有数据一直传null就可以了
            Cursor query = db.query(Constants.DB_NAME, null, null, null, null,
                    null, null);

            // 封装数据
            while (query.moveToNext()) {
                Album album = new Album();

                // 获取图片url并保存在集合中
                String coverUrl = query.getString(query.getColumnIndex(Constants.SUB_COVER_URL));
                album.setCoverUrlLarge(coverUrl);

                // 获取标题并保存在集合中
                String title = query.getString(query.getColumnIndex(Constants.SUB_TITLE));
                album.setAlbumTitle(title);

                // 获取描述并保存在集合中
                String description = query.getString(query.getColumnIndex(Constants.SUB_DESCRIPTION));
                album.setAlbumIntro(description);

                // 获取(音频轨道数量)并保存在集合中
                int trackCount = query.getInt(query.getColumnIndex(Constants.SUB_TRACKS_COUNT));
                album.setIncludeTrackCount(trackCount);

                // 获取播放次数并保存在集合中
                int playCount = query.getInt(query.getColumnIndex(Constants.SUB_PLAY_COUNT));
                album.setPlayCount(playCount);

                // 获取album_id并保存在集合中
                int albumId = query.getInt(query.getColumnIndex(Constants.SUB_ALBUM_ID));
                album.setId(albumId);

                // 获取作者名称并保存在集合中
                String authorName = query.getString(query.getColumnIndex(Constants.SUB_AUTHOR_NAME));
                Announcer announcer = new Announcer();
                announcer.setNickname(authorName);
                album.setAnnouncer(announcer);

                result.add(album);
            }

            // 把数据通知出去
            mCallback.onSubListLoaded(result);

            query.close();

            db.setTransactionSuccessful();

        } catch (Exception e) {
            // FIXME:这里
            e.printStackTrace();
        } finally {

            if (db != null) {

                db.endTransaction();

                // 数据库一定要关闭不然会内存泄漏
                db.close();
            }

        }
    }
}
