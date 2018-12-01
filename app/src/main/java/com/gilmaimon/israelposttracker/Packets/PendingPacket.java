package com.gilmaimon.israelposttracker.Packets;

import java.util.Date;

public class PendingPacket extends Packet {
    private final int branchId;
    private final String branchPacketId;
    private final Date lastNotice;

    public PendingPacket(String postId, int branchId, String branchPacketId, Date lastNotice) {
        super(postId);
        this.branchId = branchId;
        this.branchPacketId = branchPacketId;
        this.lastNotice = lastNotice;
    }

    public PendingPacket(String postId, int branchId, String branchPacketId, long lastNotice) {
        this(postId, branchId, branchPacketId, new Date(lastNotice));
    }

    public int getBranchId() {
        return this.branchId;
    }

    public String getBranchPacketId() {
        return branchPacketId;
    }

    public Date getLastNotice() {
        return this.lastNotice;
    }
}
