package com.gilmaimon.israelposttracker;

import android.content.Context;

import com.gilmaimon.israelposttracker.AndroidUtils.RawResource;
import com.gilmaimon.israelposttracker.Branches.BranchesProvider;
import com.gilmaimon.israelposttracker.Branches.JsonBranches;
import com.gilmaimon.israelposttracker.Parsing.PostMessageParser;
import com.gilmaimon.israelposttracker.Parsing.RegexPostMessageParser;
import com.gilmaimon.israelposttracker.Sorting.KeywordsMessagesSorter;
import com.gilmaimon.israelposttracker.Sorting.PostMessageSorter;

public class Rules {
    public static PostMessageSorter getDefaultSorter() {
        return KeywordsMessagesSorter.getDefault();
    }

    public static PostMessageParser getDefaultParser() {
        return new RegexPostMessageParser();
    }

    public static boolean isSenderRelevant(String sender) {
        return sender.contains("Israel Post"); //"Israel Post"
    }

    public static String getPostSmsNumber() {
        return "Israel Post"; //"Israel Post";
    }

    public static BranchesProvider getDefaultBranchesProvider(Context context) {
        return new JsonBranches(new RawResource(context, R.raw.branches).readAll());
    }
}
