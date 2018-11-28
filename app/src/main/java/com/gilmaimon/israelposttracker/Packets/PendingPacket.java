package com.gilmaimon.israelposttracker.Packets;

import java.util.Date;

public class PendingPacket extends Packet {
    private int branchId;
    private String branchPacketId;
    private Date lastNotice;

    public PendingPacket(String postId, int branchId, String branchPacketId, Date lastNotice) {
        super(postId);
        this.branchId = branchId;
        this.branchPacketId = branchPacketId;
        this.lastNotice = lastNotice;
    }

    public PendingPacket(String postId, int branchId, String branchPacketId, long lastNotice) {
        this(postId, branchId, branchPacketId, new Date(lastNotice));
    }

    public void setLastNotice(Date date) {
        this.lastNotice = date;
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
