package com.gilmaimon.israelposttracker.Branches;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AndroidBranchWebsiteDispatcher implements BranchWebsiteDispatcher {
    private final Context context;

    public AndroidBranchWebsiteDispatcher(Context context) {
        this.context = context;
    }

    @Override
    public void openBranchPage(Branch branch) {
        String url = "http://www.israelpost.co.il/m/" + String.valueOf(branch.getId());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }
}
