package com.gilmaimon.israelposttracker;

import android.content.Context;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class RawResource {
    private int resourceId;
    private Context context;

    RawResource(Context context, int id) {
        this.resourceId = id;
        this.context = context;
    }

    public String readAll() throws IOException {
        InputStream is = context.getResources().openRawResource(resourceId );
        String content = null;
        try {
            content = IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
            IOUtils.closeQuietly(is);
            throw e;
        }

        IOUtils.closeQuietly(is);
        return content;
    }
}
