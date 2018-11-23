package com.gilmaimon.israelposttracker.Parsing;

import android.support.annotation.NonNull;

import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;

public interface PostMessageParser {

    @NonNull
    PendingPacket parseAwaitingPacketMessage(@NonNull String content)
            throws UnknownMessageFormat;

    @NonNull
    Packet parsePickedUpMessage(@NonNull String content)
            throws UnknownMessageFormat;
}
