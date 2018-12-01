package com.gilmaimon.israelposttracker.AndroidUtils;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class RawResource {
    private final int resourceId;
    private final Context context;

    public RawResource(Context context, int id) {
        this.resourceId = id;
        this.context = context;
    }

    public String readAll() {
        InputStream is = context.getResources().openRawResource(resourceId );
        String content;
        try {
            content = IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.closeQuietly(is);
            return "-1";
        }

        IOUtils.closeQuietly(is);
        return content;
    }
}
