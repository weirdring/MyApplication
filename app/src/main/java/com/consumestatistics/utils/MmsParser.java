package com.consumestatistics.utils;

import com.consumestatistics.entity.Consume;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析短信类容的工具类
 * Created by Wei.Yan on 2015/6/3.
 */
public class MmsParser {
    public static Consume MmsParser(String content) {
        Consume consume = new Consume();

        return consume;
    }

    public static Consume MmsParser(String body, long date, String address) {
        Consume consume = new Consume();
        consume.BODY = body;
        consume.DATE = date;
        consume.BANK = address;
        consume.AMOUNT = getAmountFromBody(body);
        return consume;
    }

    /**
     * 从短信内容中获取支出的金额
     *
     * @param body
     * @return
     */
    private static int getAmountFromBody(String body) {
        int index = body.indexOf("支出");
        if (index < 0) return 0;
        String subString = body.substring(index, body.length() - 1);
        int amount = 0;
        String regEx = "\\d*";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(subString);
        while (m.find()) {
            if (!"".equals(m.group())) {
                System.out.println("come here:" + m.group());
                amount = Integer.parseInt(m.group());
                break;
            }
        }
        return amount;
    }


}
