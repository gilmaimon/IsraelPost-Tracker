package com.gilmaimon.israelposttracker.UserAppended;

import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;

import java.util.List;

public interface UserAppendedPacketActions {
    void dismissPendingPacket(Packet packet);
    void addPendingPacket(PendingPacket packet);

    List<Packet> getDismissedPackets();
    List<PendingPacket> getPendingPackets();
}
