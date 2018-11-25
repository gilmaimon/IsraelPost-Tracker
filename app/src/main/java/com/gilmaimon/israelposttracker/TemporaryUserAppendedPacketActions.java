package com.gilmaimon.israelposttracker;

import android.support.annotation.NonNull;

import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Branches.BranchesProvider;
import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TemporaryUserAppendedPacketActions implements UserAppendedPacketActions {

    private BranchesProvider branchesProvider;

    private Set<Packet> dismissedPackets;
    private Set<PendingPacket> pendingPackets;

    public TemporaryUserAppendedPacketActions(@NonNull BranchesProvider branchesProvider) {
        this.branchesProvider = branchesProvider;
        this.dismissedPackets = new HashSet<>();
        this.pendingPackets = new HashSet<>();
    }

    @Override
    public void DismissPendingPacket(Packet packet) {
        if (!pendingPackets.remove(packet)) {
            dismissedPackets.add(packet);
        }
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