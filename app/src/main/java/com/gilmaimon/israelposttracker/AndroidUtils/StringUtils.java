package com.gilmaimon.israelposttracker.AndroidUtils;

import android.text.format.DateUtils;

import java.util.Date;

public class StringUtils {

    public static String formatTimeAgo(Date date) {
        long time = date.getTime();
        long now = System.currentTimeMillis();

        CharSequence ago =
                DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
        return String.valueOf(ago);
    }
}
