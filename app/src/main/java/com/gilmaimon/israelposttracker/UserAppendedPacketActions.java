package com.gilmaimon.israelposttracker;

import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;

import java.util.List;

public interface UserAppendedPacketActions {
    void DismissPendingPacket(Packet packet);
    void addPendingPacket(PendingPacket packet);

    List<Packet> getDismissedPackets();
    List<PendingPacket> getPendingPackets();
}
