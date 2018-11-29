package com.gilmaimon.israelposttracker.SMS;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;

import com.gilmaimon.israelposttracker.SMS.IncomingIsraelPostSMSMessages;

public class NewSmsBroadcastListener {
    public interface NewSmsListener {
        void onSmsReceived();
    }

    private Context context;
    private BroadcastReceiver newSmsMessageReceiver;

    public NewSmsBroadcastListener(@NonNull Context context, final NewSmsListener listener) {
        this.context = context;
        newSmsMessageReceiver = new BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onReceive(Context context, Intent intent) {
                listener.onSmsReceived();
            }
        };
    }

    public void register() {
        LocalBroadcastManager
                .getInstance(context)
                .registerReceiver(
                        newSmsMessageReceiver ,
                        new IntentFilter(IncomingIsraelPostSMSMessages.NEW_SMS_BROADCAST_ACTION)
                );
    }

    public void unregister() {
        LocalBroadcastManager
                .getInstance(context)
                .unregisterReceiver(newSmsMessageReceiver);
    }
}
