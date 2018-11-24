package com.gilmaimon.israelposttracker.SMS;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;

import java.util.Date;

public class IncomingIsraelPostSMSMessages extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        SMSMessage message = getSMSMessageFromIntent(intent);
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
