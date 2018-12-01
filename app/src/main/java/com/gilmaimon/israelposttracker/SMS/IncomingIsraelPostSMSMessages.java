package com.gilmaimon.israelposttracker.SMS;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Telephony;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.gilmaimon.israelposttracker.MainActivity;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.Parsing.PostMessageParser;
import com.gilmaimon.israelposttracker.Parsing.UnknownMessageFormat;
import com.gilmaimon.israelposttracker.PostTrackerApplication;
import com.gilmaimon.israelposttracker.R;
import com.gilmaimon.israelposttracker.Rules;
import com.gilmaimon.israelposttracker.Sorting.KeywordsMessagesSorter;
import com.gilmaimon.israelposttracker.Sorting.PostMessageSorter;

import java.util.Date;

public class IncomingIsraelPostSMSMessages extends BroadcastReceiver {

    public static final String NEW_SMS_BROADCAST_ACTION = "NEW_SMS_MESSAGE";
    public final static int APP_NOTIFICATION_ID = 1337;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        SMSMessage message = getSMSMessageFromIntent(intent);
        if(!Rules.isSenderRelevant(message.getSender())) {
            return;
        }

        Intent newSmsIntent = new Intent(NEW_SMS_BROADCAST_ACTION)
            .putExtra("sender", message.getSender())
            .putExtra("body", message.getMessage())
            .putExtra("date", message.getDate().getTime());

        LocalBroadcastManager
                .getInstance(context)
                .sendBroadcast(newSmsIntent);

        PostMessageSorter sorter = Rules.getDefaultSorter();
        PostMessageParser parser = Rules.getDefaultParser();
        switch(sorter.sortMessage(message.getMessage())) {
            case AwaitingPickup:
                try {
                    Log.v("PostNotification", "Making Pending Pickup Notification");
                    Log.v("PostNotification", message.getMessage());
                    notifyNewAwaitingPacket(context, parser.parseAwaitingPacketMessage(message));
                } catch (UnknownMessageFormat unknownMessageFormat) {
                    unknownMessageFormat.printStackTrace();
                    Log.v("PostNotification", unknownMessageFormat.getMessage());
                }
                break;
            case PickedUp:
                // No need to notify the user on packets he picked up
                break;
            case Unknown:
                break;
        }
    }

    private final static String CHANNEL_ID = "channel_israel_post_tracker_app_notification";
    private final static String CHANNEL_NAME = "Israel-Post Tracker Notifications";
    private final static String CHANNEL_DESC = "PostTracker App Notifications";

    public void initChannel(NotificationManager notificationManager) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setDescription(CHANNEL_DESC);
            mChannel.setLightColor(Color.CYAN);
            mChannel.canShowBadge();
            mChannel.setShowBadge(true);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    private void notifyNewAwaitingPacket(Context context, PendingPacket pendingPacket) {

        PostTrackerApplication application = (PostTrackerApplication) context.getApplicationContext();
        if(application.isAppShowing()) {
            return;
        }

        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        initChannel(notificationManager);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);

        String branchName = Rules.getDefaultBranchesProvider(context).getBranch(pendingPacket.getBranchId()).getName();

        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(context);
        }

        builder
            .setContentTitle("Israel-Post Tracker")
            .setContentText("You've an SMS about a packet. Click for more information.")
            .setSubText(branchName)
            .setNumber(5)
            .setContentIntent(intent)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setAutoCancel(true);

        notificationManager.notify(APP_NOTIFICATION_ID, builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    SMSMessage getSMSMessageFromIntent(Intent intent) {
        StringBuilder messageBody = new StringBuilder();
        String from = null;
        Date date = null;
        for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
            String content = smsMessage.getMessageBody();
            from = smsMessage.getDisplayOriginatingAddress();
            date = new Date(smsMessage.getTimestampMillis());
            messageBody.append(content);
        }
        return new SMSMessage(from, messageBody.toString(), date);
    }
}
