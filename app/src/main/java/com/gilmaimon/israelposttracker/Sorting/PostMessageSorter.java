package com.gilmaimon.israelposttracker.Sorting;

import android.support.annotation.NonNull;

public interface PostMessageSorter {
    enum PostSMSType {
        AwaitingPickup,
        PickedUp,
        Unknown
    }

    @NonNull PostSMSType sortMessage(@NonNull String messageContent);
}
