package com.gilmaimon.israelposttracker.Balance.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gilmaimon.israelposttracker.Packets.PendingPacket;

public class PendingPacketViewHolder extends RecyclerView.ViewHolder {
    public PendingPacket packet;
    public final ViewGroup packetForegroundLayout;
    public final TextView postalIdTV;
    public final TextView packetBranchIdTV;
    public final TextView lastNoticeTV;

    PendingPacketViewHolder(ViewGroup packetContainer, ViewGroup packetForegroundLayout,
                            TextView postalIdTV,
                            TextView packetBranchIdTV,
                            TextView lastNoticeTV){
        super(packetContainer);

        this.packetForegroundLayout = packetForegroundLayout;
        this.postalIdTV = postalIdTV;
        this.packetBranchIdTV = packetBranchIdTV;
        this.lastNoticeTV = lastNoticeTV;
    }
}
