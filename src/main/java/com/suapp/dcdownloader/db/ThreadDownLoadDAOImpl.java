package com.suapp.dcdownloader.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.suapp.dcdownloader.model.ThreadDownLoadInfo;

import java.util.ArrayList;
import java.util.List;

import static com.suapp.dcdownloader.db.DBHelper.COLUMN_FINISH_LENGTH;
import static com.suapp.dcdownloader.db.DBHelper.COLUMN_START_LENGTH;
import static com.suapp.dcdownloader.db.DBHelper.COLUMN_STOP_LENGTH;
import static com.suapp.dcdownloader.db.DBHelper.COLUMN_THREAD_ID;
import static com.suapp.dcdownloader.db.DBHelper.COLUMN_URL;
import static com.suapp.dcdownloader.db.DBHelper.TABLE_NAME;

/**
 * Created by zhaojing on 2017/12/21.
 */

public class ThreadDownLoadDAOImpl implements ThreadDownLoadDAO {
    private DBHelper mDBHelper;

    public ThreadDownLoadDAOImpl(Context context) {
        mDBHelper = new DBHelper(context);
    }

    @Override
    public void insertThread(ThreadDownLoadInfo threadDownLoadInfo) {
        SQLiteDatabase writableDatabase = mDBHelper.getWritableDatabase();
        writableDatabase.execSQL("insert table " +
                        TABLE_NAME + "(" +
                        COLUMN_THREAD_ID + "," +
                        COLUMN_URL + "," +
                        COLUMN_START_LENGTH + "," +
                        COLUMN_STOP_LENGTH + "," +
                        COLUMN_FINISH_LENGTH + ") value (?,?,?,?,?)",
                new Object[]{threadDownLoadInfo.mId, threadDownLoadInfo.mUrl, threadDownLoadInfo.mStartLength,
                        threadDownLoadInfo.mEndLength, threadDownLoadInfo.mFinishedLength});
        writableDatabase.close();
    }

    @Override
    public void deleteThread(String url, int threadId) {
        SQLiteDatabase writableDatabase = mDBHelper.getWritableDatabase();
        writableDatabase.execSQL("delete from " +
                        TABLE_NAME + " where " +
                        COLUMN_URL + " = ? and " +
                        COLUMN_THREAD_ID + " = ?",
                new Object[]{url, threadId});
        writableDatabase.close();
    }

    @Override
    public synchronized void deleteThread(String url) {
        Log.e("deleteThread: ", "deleteThread");
        SQLiteDatabase db = mDBHelper.getWritableDatabase();
        db.execSQL("delete from " +
                        TABLE_NAME + " where " +
                        COLUMN_URL + " = ?",
                new Object[]{url});
        db.close();
    }

    @Override
    public void updateThread(String url, int threadId, long finishedLength) {
        SQLiteDatabase writableDatabase = mDBHelper.getWritableDatabase();
        writableDatabase.execSQL("update " +
                        TABLE_NAME + " set " +
                        COLUMN_FINISH_LENGTH + " =? where " +
                        COLUMN_URL + " = ? and " +
                        COLUMN_THREAD_ID + " = ?",
                new Object[]{finishedLength, url, threadId});
        writableDatabase.close();
    }

    @Override
    public List<ThreadDownLoadInfo> getThreadList(String url) {
        List<ThreadDownLoadInfo> list = new ArrayList<>();
        SQLiteDatabase writableDatabase = mDBHelper.getWritableDatabase();
        Cursor cursor = writableDatabase.rawQuery("select * from " +
                        TABLE_NAME + " where " +
                        COLUMN_URL + " =?",
                new String[]{url});
        while (cursor.moveToNext()) {
            ThreadDownLoadInfo threadDownLoadInfo = new ThreadDownLoadInfo();
            threadDownLoadInfo.mId = cursor.getInt(cursor.getColumnIndex(COLUMN_THREAD_ID));
            threadDownLoadInfo.mUrl = cursor.getString(cursor.getColumnIndex(COLUMN_URL));
            threadDownLoadInfo.mStartLength = cursor.getInt(cursor.getColumnIndex(COLUMN_START_LENGTH));
            threadDownLoadInfo.mEndLength = cursor.getInt(cursor.getColumnIndex(COLUMN_STOP_LENGTH));
            threadDownLoadInfo.mFinishedLength = cursor.getInt(cursor.getColumnIndex(COLUMN_FINISH_LENGTH));
            list.add(threadDownLoadInfo);
        }
        cursor.close();
        writableDatabase.close();
        return list;
    }

    @Override
    public boolean isExists(String url, int threadId) {
        SQLiteDatabase writableDatabase = mDBHelper.getWritableDatabase();
        Cursor cursor = writableDatabase.rawQuery("select * from " +
                        TABLE_NAME + " where " +
                        COLUMN_URL + "= ? and " +
                        COLUMN_THREAD_ID + " =?",
                new String[]{url, threadId + ""});
        boolean isExists = cursor.moveToNext();
        cursor.close();
        writableDatabase.close();
        return isExists;
    }
}
