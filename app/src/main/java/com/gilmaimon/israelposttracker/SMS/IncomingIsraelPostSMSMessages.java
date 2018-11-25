package com.gilmaimon.israelposttracker.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

import java.util.Date;

public class IncomingIsraelPostSMSMessages extends BroadcastReceiver {

    public static final String NEW_SMS_BROADCAST_ACTION = "NEW_SMS_MESSAGE";
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        SMSMessage message = getSMSMessageFromIntent(intent);
        Intent newSmsIntent = new Intent(NEW_SMS_BROADCAST_ACTION)
            .putExtra("sender", message.getSender())
            .putExtra("body", message.getMessage())
            .putExtra("date", message.getDate().getTime());

        LocalBroadcastManager
                .getInstance(context)
                .sendBroadcast(newSmsIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    SMSMessage getSMSMessageFromIntent(Intent intent) {
        for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            String messageBody = smsMessage.getMessageBody();
            String from = smsMessage.getDisplayOriginatingAddress();
            Date date = new Date(smsMessage.getTimestampMillis());
            return new SMSMessage(from, messageBody, date);
        }
        return null;
    }
}
