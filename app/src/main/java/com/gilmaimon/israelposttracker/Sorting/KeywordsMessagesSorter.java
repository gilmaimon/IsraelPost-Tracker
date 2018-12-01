package com.gilmaimon.israelposttracker.Sorting;

import android.support.annotation.NonNull;

public class KeywordsMessagesSorter implements PostMessageSorter {

    private final String awaitingKeyword;
    private final String pickedUpKeyword;

    public KeywordsMessagesSorter(String awaitingKeyword, String pickedUpKeyword) {
        this.awaitingKeyword = awaitingKeyword;
        this.pickedUpKeyword = pickedUpKeyword;
    }

    public static KeywordsMessagesSorter getDefault() {
        return new KeywordsMessagesSorter(
                "ממתין",
                "שאספת"
        );
    }

    @NonNull
    @Override
    public PostSMSType sortMessage(@NonNull String messageContent) {
        if(messageContent.contains(awaitingKeyword)) {
            return PostSMSType.AwaitingPickup;
        } else if(messageContent.contains(pickedUpKeyword)) {
            return PostSMSType.PickedUp;
        } else {
            return PostSMSType.Unknown;
        }
    }

}
