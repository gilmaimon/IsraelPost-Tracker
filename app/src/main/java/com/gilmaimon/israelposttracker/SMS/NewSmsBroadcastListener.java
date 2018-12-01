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

    private final Context context;
    private final BroadcastReceiver newSmsMessageReceiver;
    private NewSmsListener listener;

    public NewSmsBroadcastListener(@NonNull Context context) {
        this.context = context;
        newSmsMessageReceiver = new BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onReceive(Context context, Intent intent) {
                if(listener == null) return;
                listener.onSmsReceived();
            }
        };
    }

    public void setListener(NewSmsListener listener) {
        this.listener = listener;
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
