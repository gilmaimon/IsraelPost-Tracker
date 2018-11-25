package com.gilmaimon.israelposttracker.Parsing;

import android.support.annotation.NonNull;

import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.SMS.SMSMessage;

public interface PostMessageParser {

    @NonNull
    PendingPacket parseAwaitingPacketMessage(@NonNull SMSMessage message)
            throws UnknownMessageFormat;

    @NonNull
    Packet parsePickedUpMessage(@NonNull SMSMessage message)
            throws UnknownMessageFormat;
}
