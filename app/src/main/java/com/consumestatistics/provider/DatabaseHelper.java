package com.consumestatistics.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 操作数据库
 *
 * @author wei.yan
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "consume.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createConsumeTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProviderConstants.ConsumeColumns.TABLE_NAME);
        onCreate(db);
    }

    private void createConsumeTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + ProviderConstants.ConsumeColumns.TABLE_NAME + " ("
                + ProviderConstants.ConsumeColumns._ID + " INTEGER PRIMARY KEY,"
                + ProviderConstants.ConsumeColumns.BODY + " TEXT,"
                + ProviderConstants.ConsumeColumns.AMOUNT + " INTEGER,"
                + ProviderConstants.ConsumeColumns.BANK + " TEXT,"
                + ProviderConstants.ConsumeColumns.DATE + " INTEGER"
                + ");");
    }
}