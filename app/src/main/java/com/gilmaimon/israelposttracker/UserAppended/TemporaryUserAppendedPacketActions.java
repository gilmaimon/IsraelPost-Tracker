package com.gilmaimon.israelposttracker.UserAppended;

import com.gilmaimon.israelposttracker.AndroidUtils.UndoableAction;
import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TemporaryUserAppendedPacketActions implements UserAppendedPacketActions {

    private Set<Packet> dismissedPackets;
    private Set<PendingPacket> pendingPackets;

    public TemporaryUserAppendedPacketActions() {
        this.dismissedPackets = new HashSet<>();
        this.pendingPackets = new HashSet<>();
    }

    @Override
    public UndoableAction dismissPendingPacket(final Packet packet) {
        dismissedPackets.add(packet);
        return new UndoableAction() {
            @Override
            public void undo() {
                dismissedPackets.remove(packet);
            }
        };
    }

    @Override
    public void addPendingPacket(PendingPacket packet) {
        pendingPackets.add(packet);
    }

    @Override
    public List<Packet> getDismissedPackets() {
        return new ArrayList<>(dismissedPackets);
    }

    @Override
    public List<PendingPacket> getPendingPackets() {
        return new ArrayList<>(pendingPackets);
    }
}
