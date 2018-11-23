package com.gilmaimon.israelposttracker.Packets;

public class PendingPacket extends Packet {
    private int branchId;
    private String branchPacketId;

    public PendingPacket(String postId, int branchId, String branchPacketId) {
        super(postId);
        this.branchId = branchId;
        this.branchPacketId = branchPacketId;
    }

    public int getBranchId() {
        return this.branchId;
    }

    public String getBranchPacketId() {
        return branchPacketId;
    }
}
