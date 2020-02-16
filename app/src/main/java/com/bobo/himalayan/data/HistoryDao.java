package com.bobo.himalayan.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bobo.himalayan.base.BaseApplication;
import com.bobo.himalayan.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leon on 2020-02-01 Copyright © Leon. All rights reserved.
 * Functions: 历史数据数据库操作类
 */
public class HistoryDao implements IHistoryDao {

    private final XimalayaDBHelper mDbHelper;


    private IHistoryDaoCallback mCallback = null;

    // 访问数据库加锁
    private Object mLock = new Object();

    public HistoryDao(){
        mDbHelper = new XimalayaDBHelper(BaseApplication.getAppContext());
    }

    @Override
    public void setCallback(IHistoryDaoCallback callback) {

        this.mCallback = callback;

    }

    /**
     * 添加历史数据
     * @param track
     */
    @Override
    public void addHistory(Track track) {

        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isSucess = false;

            try {
                db = mDbHelper.getWritableDatabase();

                // 先删除再添加避免重复的数据
                db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?",
                        new String[]{track.getDataId() + ""});

                db.beginTransaction();
                ContentValues values = new ContentValues();

                // 封装数据
                values.put(Constants.HISTORY_TRACK_ID, track.getDataId());
                values.put(Constants.HISTORY_TITLE, track.getTrackTitle());
                values.put(Constants.HISTORY_PLAY_COUNT, track.getPlayCount());
                values.put(Constants.HISTORY_DURATION, track.getDuration());
                values.put(Constants.HISTORY_UPDATE_TIME, track.getUpdatedAt());
                values.put(Constants.HISTORY_COVER, track.getCoverUrlLarge());
                values.put(Constants.HISTORY_AUTHOR, track.getAnnouncer().getNickname());
                values.put(Constants.HISTORY_ORDER_NUMBER, track.getOrderNum());

                // 执行插入语句
                // db.replace(Constants.HISTORY_TB_NAME, null, values);  自增idreplace 无法去重
                db.insert(Constants.HISTORY_TB_NAME, null, values);

                db.setTransactionSuccessful();
                isSucess = true;
            } catch (Exception e) {

                isSucess = false;
                e.printStackTrace();

            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }

                mCallback.onHistoryAdd(isSucess);
            }
        }
    }

    @Override
    public void delHistory(Track track) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isDeleteSucess = false;

            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();

                int delete = db.delete(Constants.HISTORY_TB_NAME, Constants.HISTORY_TRACK_ID + "=?",
                        new String[]{track.getDataId() + ""});

                Log.e("删除历史---->", delete + "");

                db.setTransactionSuccessful();
                isDeleteSucess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isDeleteSucess = false;
            } finally {

                if (db != null) {
                    db.endTransaction();
                    db.close();
                }

                mCallback.onHistoryDel(isDeleteSucess);

            }
        }

    }

    @Override
    public void clearHistory() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            boolean isDeleteSucess = false;

            try {
                db = mDbHelper.getWritableDatabase();
                db.beginTransaction();

                // 清空就直接传个表名后面的参数传null就可以了
                int delete = db.delete(Constants.HISTORY_TB_NAME, null, null);

                Log.e("清空历史---->", delete + "");

                db.setTransactionSuccessful();
                isDeleteSucess = true;
            } catch (Exception e) {
                e.printStackTrace();
                isDeleteSucess = false;
            } finally {

                if (db != null) {
                    db.endTransaction();
                    db.close();
                }

                mCallback.onHistoriesClean(isDeleteSucess);

            }
        }
    }

    /**
     * 从历史表中查出所有历史数据
     */
    @Override
    public void listHistories() {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            List<Track> histories = new ArrayList<>();

            try {

                db = mDbHelper.getReadableDatabase();
                db.beginTransaction();

                // 获取所有数据
                Cursor cursor = db.query(Constants.HISTORY_TB_NAME, null, null, null,
                        null, null, "_id  desc");

                // 循环遍历取出数据库中所有对象
                while (cursor.moveToNext()) {
                    Track track = new Track();
                    int trackId = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_TRACK_ID));
                    track.setDataId(trackId);
                    String title = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_TITLE));
                    track.setTrackTitle(title);
                    int playCount = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_PLAY_COUNT));
                    track.setPlayCount(playCount);
                    int duration = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_DURATION));
                    track.setDuration(duration);
                    long updateTime = cursor.getLong(cursor.getColumnIndex(Constants.HISTORY_UPDATE_TIME));
                    track.setUpdatedAt(updateTime);
                    int orderNum = cursor.getInt(cursor.getColumnIndex(Constants.HISTORY_ORDER_NUMBER));
                    track.setOrderNum(orderNum);
                    String cover = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_COVER));
                    track.setCoverUrlLarge(cover);
                    track.setCoverUrlSmall(cover);
                    track.setCoverUrlMiddle(cover);

                    String author = cursor.getString(cursor.getColumnIndex(Constants.HISTORY_AUTHOR));
                    Announcer announcer = new Announcer();
                    announcer.setNickname(author);
                    track.setAnnouncer(announcer);

                    histories.add(track);
                }

                db.setTransactionSuccessful();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.endTransaction();
                    db.close();
                }
            }

            // 通知ui刷新
            mCallback.onHistoriesLoaded(histories);
        }
    }
}
