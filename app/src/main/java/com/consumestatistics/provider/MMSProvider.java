package com.consumestatistics.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.content.ContentProvider;

import java.util.HashMap;

/**
 * 操作数据库consume表的ContentProvider
 *
 * @author wei.yan
 */
public class MMSProvider extends ContentProvider {

    private static HashMap<String, String> sConsumeProjectionMap;

    private static final int CONSUME = 1;
    private static final int CONSUME_ID = 2;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(ProviderConstants.ConsumeColumns.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case CONSUME:
                qb.setProjectionMap(sConsumeProjectionMap);
                break;

            case CONSUME_ID:
                qb.setProjectionMap(sConsumeProjectionMap);
                qb.appendWhere(ProviderConstants.ConsumeColumns._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // If no sort order is specified use the default
        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = ProviderConstants.ConsumeColumns.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case CONSUME:
                return ProviderConstants.CONTENT_TYPE;
            case CONSUME_ID:
                return ProviderConstants.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Validate the requested uri
        if (sUriMatcher.match(uri) != CONSUME) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Make sure that the fields are all set
        if (values.containsKey(ProviderConstants.ConsumeColumns.BANK) == false) {
            values.put(ProviderConstants.ConsumeColumns.BANK, "");
        }
        if (values.containsKey(ProviderConstants.ConsumeColumns.AMOUNT) == false) {
            values.put(ProviderConstants.ConsumeColumns.AMOUNT, "");
        }
        if (values.containsKey(ProviderConstants.ConsumeColumns.DATE) == false) {
            values.put(ProviderConstants.ConsumeColumns.DATE, "");
        }
        if (values.containsKey(ProviderConstants.ConsumeColumns.BODY) == false) {
            values.put(ProviderConstants.ConsumeColumns.BODY, "");
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(ProviderConstants.ConsumeColumns.TABLE_NAME, ProviderConstants.ConsumeColumns.BANK, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(ProviderConstants.ConsumeColumns.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case CONSUME:
                count = db.delete(ProviderConstants.ConsumeColumns.TABLE_NAME, where, whereArgs);
                break;

            case CONSUME_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.delete(ProviderConstants.ConsumeColumns.TABLE_NAME, ProviderConstants.ConsumeColumns._ID + "=" + noteId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case CONSUME:
                count = db.update(ProviderConstants.ConsumeColumns.TABLE_NAME, values, where, whereArgs);
                break;

            case CONSUME_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(ProviderConstants.ConsumeColumns.TABLE_NAME, values, ProviderConstants.ConsumeColumns._ID + "=" + noteId
                        + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        // 这个地方的consume要和ConsumeColumns.CONTENT_URI中最后面的一个Segment一致
        sUriMatcher.addURI(ProviderConstants.AUTHORITY, "consume", CONSUME);
        sUriMatcher.addURI(ProviderConstants.AUTHORITY, "consume/#", CONSUME_ID);

        sConsumeProjectionMap = new HashMap<String, String>();
        sConsumeProjectionMap.put(ProviderConstants.ConsumeColumns._ID, ProviderConstants.ConsumeColumns._ID);
        sConsumeProjectionMap.put(ProviderConstants.ConsumeColumns.BANK, ProviderConstants.ConsumeColumns.BANK);
        sConsumeProjectionMap.put(ProviderConstants.ConsumeColumns.AMOUNT, ProviderConstants.ConsumeColumns.AMOUNT);
        sConsumeProjectionMap.put(ProviderConstants.ConsumeColumns.DATE, ProviderConstants.ConsumeColumns.DATE);
        sConsumeProjectionMap.put(ProviderConstants.ConsumeColumns.BODY, ProviderConstants.ConsumeColumns.BODY);
    }
}
