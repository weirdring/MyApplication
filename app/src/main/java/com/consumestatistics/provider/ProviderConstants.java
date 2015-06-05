package com.consumestatistics.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Wei.Yan on 2015/6/3.
 */
public class ProviderConstants {
    // 这个是每个Provider的标识，在Manifest中使用
    public static final String AUTHORITY = "com.consumestatistics.provider.consume";

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/com.consume";

    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/com.consume";

    /**
     * 跟Person表相关的常量
     *
     * @author weiyan
     */
    public static final class ConsumeColumns implements BaseColumns {
        // CONTENT_URI跟数据库的表关联，最后根据CONTENT_URI来查询对应的表
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/consume");
        public static final String TABLE_NAME = "consume";
        public static final String DEFAULT_SORT_ORDER = "bank desc";
        public static final String _ID = "_id";
        public static final String BANK = "bank";
        public static final String AMOUNT = "amount";
        public static final String DATE = "date";
        public static final String BODY = "body";
    }
}
