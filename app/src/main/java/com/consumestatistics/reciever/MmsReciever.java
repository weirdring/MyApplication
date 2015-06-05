package com.consumestatistics.reciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.SmsMessage;

import com.consumestatistics.entity.BankList;
import com.consumestatistics.utils.DbUtil;
import com.consumestatistics.utils.MmsParser;

public class MmsReciever extends BroadcastReceiver {
    public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    public MmsReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        StringBuffer sb = new StringBuffer();
        String sender = null;
        String content = null;
        String sendtime = null;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            // 通过pdus获得接收到的所有短信消息，获取短信内容；
            Object[] pdus = (Object[]) bundle.get("pdus");
            // 构建短信对象数组；
            SmsMessage[] mges = new SmsMessage[pdus.length];
            for (int i = 0; i < pdus.length; i++) {
                // 获取单条短信内容，以pdu格式存,并生成短信对象；
                mges[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            }
            for (SmsMessage mge : mges) {
                String address = mge.getDisplayOriginatingAddress();
                if (BankList.contains(address)) {
                    DbUtil.insert(context, MmsParser.MmsParser(mge.getMessageBody(), (int) mge.getTimestampMillis(), address));
                }
//                sb.append("短信内容：" + mge.getMessageBody());
//                sender = mge.getDisplayOriginatingAddress();// 获取短信的发送者
//                content = mge.getMessageBody();// 获取短信的内容
//                Date date = new Date(mge.getTimestampMillis());
//                SimpleDateFormat format = new SimpleDateFormat(
//                        "yyyy-MM-dd HH:mm:ss");
//                sendtime = format.format(date);// 获取短信发送时间；

            }

        }
    }
}
