package com.suapp.dcdownloader.httpurlconnect.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by zhaojing on 2017/12/21.
 */

public class DBHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "autoDownload.db";
    private static int DB_VERSION = 1;
    public static final String TABLE_NAME = "thread_download_table";
    public static final String COLUMN_THREAD_ID = "thread_id";
    public static final String COLUMN_START_LENGTH = "start_length";
    public static final String COLUMN_STOP_LENGTH = "stop_length";
    public static final String COLUMN_FINISH_LENGTH = "finish_length";
    public static final String COLUMN_URL = "column_url";
    private String SQL_CREATE = "create table " + TABLE_NAME + "(_id integer primary key autoincrement," +
            COLUMN_THREAD_ID + " integer," +
            COLUMN_URL + " text," +
            COLUMN_START_LENGTH + " integer ," +
            COLUMN_STOP_LENGTH + " integer," +
            COLUMN_FINISH_LENGTH + " integer)";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
