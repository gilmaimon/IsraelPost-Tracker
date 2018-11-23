package com.gilmaimon.israelposttracker.Parsing;

import android.support.annotation.NonNull;

import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPostMessageParser implements PostMessageParser {

    public RegexPostMessageParser() {}

    @NonNull
    @Override
    public PendingPacket parseAwaitingPacketMessage(@NonNull String content) throws UnknownMessageFormat {
        content = content.replace("\n", " ");
        Matcher m = Pattern.compile("([A-Z]+[0-9]+[A-Z]*) +(.) +([0-9]+).*israelpost.*?([0-9]+)")
                .matcher(content);

        if(!m.find() || m.groupCount() != 4) {
            throw new UnknownMessageFormat(content);
        }

        String postPacketId = m.group(1);
        String branchPacketId = m.group(2) + m.group(3);
        int branchId = Integer.parseInt(m.group(4));

        return new PendingPacket(postPacketId, branchId, branchPacketId);
    }

    @NonNull
    @Override
    public Packet parsePickedUpMessage(@NonNull String content) throws UnknownMessageFormat {
        Matcher m = Pattern.compile("([A-Z]{2}[0-9]{8,15}[A-Z]+)")
                .matcher(content);

        if(!m.find()) {
            throw new UnknownMessageFormat(content);
        }

        String foundPacketId = m.group();
        return new Packet(foundPacketId);
    }
}
