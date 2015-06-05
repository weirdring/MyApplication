package com.consumestatistics.entity;

import java.util.HashMap;

/**
 * 银行列表
 * Created by Wei.Yan on 2015/6/3.
 */
public class BankList {
    private static HashMap<String, String> bankList;

    static {
        bankList = new HashMap<String, String>();
        bankList.put("95568", "民生银行");
        bankList.put("95588", "工商银行");
        bankList.put("95555", "招商银行");
        bankList.put("//15928650235", "颜伟");
        bankList.put("+8615928650235", "颜伟");
    }

    public static String get(String key) {
        return bankList.get(key);
    }

    public static boolean contains(String key) {
        return bankList.containsKey(key);
    }
}
