package com.consumestatistics.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.text.TextUtils;

import com.consumestatistics.entity.BankList;
import com.consumestatistics.entity.Consume;
import com.consumestatistics.provider.ProviderConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 数据库的操作
 * Created by Wei.Yan on 2015/6/4.
 */
public class DbUtil {
    /**
     * 向数据库中插入一条数据
     *
     * @param context
     * @param consume
     * @return
     */
    public static int insert(Context context, Consume consume) {
        if (consume == null || context == null) return -1;
        ContentValues values = new ContentValues();
        values.put(ProviderConstants.ConsumeColumns.BANK, consume.BANK);
        values.put(ProviderConstants.ConsumeColumns.AMOUNT, consume.AMOUNT);
        values.put(ProviderConstants.ConsumeColumns.DATE, consume.DATE);
        values.put(ProviderConstants.ConsumeColumns.BODY, consume.BODY);
        Uri uri = context.getContentResolver().insert(ProviderConstants.ConsumeColumns.CONTENT_URI, values);
        LogUtil.log("insert uri=" + uri);
        String lastPath = uri.getLastPathSegment();
        if (TextUtils.isEmpty(lastPath)) {
            LogUtil.log("insert failure!");
        } else {
            LogUtil.log("insert success! the id is " + lastPath);
        }
        return Integer.parseInt(lastPath);
    }

    /**
     * 读取系统短信数据库中已有的数据，将读取出来的数据保存到本应用的数据库中，通常在第一次启动应用时调用
     *
     * @param context
     * @return
     */
    public static void ReadMmsDB(Context context) {
        ArrayList<Consume> dataList = getMms(context);
        if (dataList == null || dataList.size() < 1) {
            LogUtil.log("system mms database is null");
            return;
        }
        for (int i = 0; i < dataList.size(); i++) {
            insert(context, dataList.get(i));
        }
        LogUtil.log("read data from system mms database to local database. ");
    }

    private static ArrayList<Consume> getMms(Context context) {
        ArrayList<Consume> list = new ArrayList<Consume>();
        final String SMS_URI_ALL = "content://sms/";
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_DRAFT = "content://sms/draft";
        final String SMS_URI_OUTBOX = "content://sms/outbox";
        final String SMS_URI_FAILED = "content://sms/failed";
        final String SMS_URI_QUEUED = "content://sms/queued";

        try {
            Uri uri = Uri.parse(SMS_URI_ALL);
            String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};
            Cursor cur = context.getContentResolver().query(uri, projection, null, null, "date desc");// 获取手机内部短信
            LogUtil.log("cur.getCount==" + cur.getCount());
            if (cur.moveToFirst()) {
                int index_Address = cur.getColumnIndex("address");
                int index_Person = cur.getColumnIndex("person");
                int index_Body = cur.getColumnIndex("body");
                int index_Date = cur.getColumnIndex("date");
                int index_Type = cur.getColumnIndex("type");
                do {
                    String strAddress = cur.getString(index_Address);
                    String strbody = cur.getString(index_Body);
                    long date = cur.getLong(index_Date);
                    LogUtil.log("strAddress==" + strAddress + "----strbody==" + strbody + "----date==" + date);
                    if (BankList.contains(strAddress)) {
                        list.add(MmsParser.MmsParser(strbody, date, strAddress));
                    }
                } while (cur.moveToNext());

                if (!cur.isClosed()) {
                    cur.close();
                    cur = null;
                }
            }
        } catch (SQLiteException ex) {
        }
        return list;
    }

    /**
     * 查询本地数据库(本应用数据库)中所有的数据
     *
     * @param mContext
     * @return
     */
    public static ArrayList<Consume> queryAllLocalData(Context mContext) {
        String columns[] = new String[]{ProviderConstants.ConsumeColumns.BANK, ProviderConstants.ConsumeColumns.DATE, ProviderConstants.ConsumeColumns.AMOUNT, ProviderConstants.ConsumeColumns.BODY};
        ArrayList<Consume> consumeList = new ArrayList<Consume>();
        Cursor c = mContext.getContentResolver().query(ProviderConstants.ConsumeColumns.CONTENT_URI, columns, null, null, null);
        if (c ==null || c.getCount() ==0) return null;
        if (c.moveToFirst()) {
            Consume p = new Consume();
            p.BANK = c.getString(c.getColumnIndexOrThrow(ProviderConstants.ConsumeColumns.BANK));
            p.AMOUNT = c.getInt(c.getColumnIndexOrThrow(ProviderConstants.ConsumeColumns.AMOUNT));
            p.DATE = c.getInt(c.getColumnIndexOrThrow(ProviderConstants.ConsumeColumns.DATE));
            p.BODY = c.getString(c.getColumnIndexOrThrow(ProviderConstants.ConsumeColumns.BODY));
            LogUtil.log("Consume.name=" + p.BANK + "---Consume.amount=" + p.AMOUNT + "----Consume.date==" + p.DATE +"----Consume.BODY==" + p.BODY);
            consumeList.add(p);
        }
        do {
            Consume p1 = new Consume();
            p1.BANK = c.getString(c.getColumnIndexOrThrow(ProviderConstants.ConsumeColumns.BANK));
            p1.AMOUNT = c.getInt(c.getColumnIndexOrThrow(ProviderConstants.ConsumeColumns.AMOUNT));
            p1.DATE = c.getInt(c.getColumnIndexOrThrow(ProviderConstants.ConsumeColumns.DATE));
            p1.BODY = c.getString(c.getColumnIndexOrThrow(ProviderConstants.ConsumeColumns.BODY));
            consumeList.add(p1);
            LogUtil.log("Consume.name=" + p1.BANK + "---Consume.amount=" + p1.AMOUNT + "----Consume.date==" + p1.DATE+ "----Consume.BODY==" + p1.BODY);
        } while (c.moveToNext());
        return consumeList;
    }


}
