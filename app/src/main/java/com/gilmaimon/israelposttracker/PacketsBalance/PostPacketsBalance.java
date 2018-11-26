package com.gilmaimon.israelposttracker.PacketsBalance;

import android.support.annotation.NonNull;

import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.UserAppended.UserAppendedPacketActions;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface PostPacketsBalance extends UserAppendedPacketActions {
    @NonNull
    HashMap<Branch, Set<PendingPacket>> getAllPendingPackets();
    Set<PendingPacket> getPendingFromBranch(int branchId);

    // Should be called when there *might* have been a change in state
    // for example: when a new message was received
    void notifyStateChanged();
}
