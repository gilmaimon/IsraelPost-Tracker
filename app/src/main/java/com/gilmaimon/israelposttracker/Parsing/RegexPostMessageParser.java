package com.gilmaimon.israelposttracker.Parsing;

import android.support.annotation.NonNull;

import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.SMS.SMSMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPostMessageParser implements PostMessageParser {

    public RegexPostMessageParser() {}

    @NonNull
    @Override
    public PendingPacket parseAwaitingPacketMessage(@NonNull SMSMessage message) throws UnknownMessageFormat {
        String content = message.getMessage().replace("\n", " ");
        Matcher m = Pattern.compile("([A-Z]+[0-9]+[A-Z]*) +(.) +([0-9]+).*israelpost.*?([0-9]+)")
                .matcher(content);

        if(!m.find() || m.groupCount() != 4) {
            throw new UnknownMessageFormat(content);
        }

        String postPacketId = m.group(1);
        String branchPacketId = m.group(2) + m.group(3);
        int branchId = Integer.parseInt(m.group(4));

        return new PendingPacket(
                postPacketId,
                branchId,
                branchPacketId,
                message.getDate()
        );
    }

    @NonNull
    @Override
    public Packet parsePickedUpMessage(@NonNull SMSMessage message) throws UnknownMessageFormat {
        Matcher m = Pattern.compile("([A-Z]+[0-9]+[A-Z]*)")
                .matcher(message.getMessage());

        if(!m.find()) {
            throw new UnknownMessageFormat(message.getMessage());
        }

        String foundPacketId = m.group();
        return new Packet(foundPacketId);
    }
}
