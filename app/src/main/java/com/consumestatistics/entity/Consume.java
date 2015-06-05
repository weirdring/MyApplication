package com.consumestatistics.entity;

import com.consumestatistics.utils.LogUtil;

/**
 * 消费实体，对应数据库中的字段
 * Created by Wei.Yan on 2015/6/3.
 */
public class Consume {
    public int _ID;
    public String BANK;
    public int AMOUNT;
    public long DATE;
    public String BODY;

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Consume)) {
            LogUtil.log("equals::false");
            return false;
        }
        Consume cp = (Consume) o;
        return cp.BANK.equals(this.BANK);
    }
}
