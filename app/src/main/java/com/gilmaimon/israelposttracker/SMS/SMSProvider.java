package com.gilmaimon.israelposttracker.SMS;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SMSProvider {

    private String fromSenderFilter;
    private Cursor cursor = null;
    private Context context;
    private boolean hasMoreToRead;

    public static SMSProvider all(Context context) {
        return new SMSProvider(context, "%");
    }

    public static SMSProvider from(Context context, String sender) {
        return new SMSProvider(context, sender);
    }

    private SMSProvider(Context context, String sender) {
        this.context = context;
        this.fromSenderFilter = sender;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initCursor() {
        cursor = context.getContentResolver().query(
                Uri.parse("content://sms/inbox"),
                new String[]{ "_id", "body" , "address", "date" },
                "address LIKE ?",
                new String[] {
                        fromSenderFilter
                },
                null,
                null
        );
        hasMoreToRead = cursor.moveToFirst();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void lazyInit() {
        if(cursor == null) initCursor();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean hasNext() {
        lazyInit();
        return hasMoreToRead;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public SMSMessage getNextMessage() {
        lazyInit();

        // Read SMS data
        String id = cursor.getString(cursor.getColumnIndex("_id"));
        String body = cursor.getString(cursor.getColumnIndex("body"));
        String sender = cursor.getString(cursor.getColumnIndex("address"));
        String date = cursor.getString(cursor.getColumnIndex("date"));

        // Advance cursor
        hasMoreToRead = cursor.moveToNext();

        // Return SMSMessage representation of the SMS data
        return new SMSMessage(
                sender,
                body,
                new Date(Long.valueOf(date))
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public List<SMSMessage> readAllMessages() {
        List<SMSMessage> result = new ArrayList<>();
        while(hasNext()) {
            result.add(
                    getNextMessage()
            );
        }
        return result;
    }
}
