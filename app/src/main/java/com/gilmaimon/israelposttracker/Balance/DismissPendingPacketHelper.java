package com.gilmaimon.israelposttracker.Balance;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.gilmaimon.israelposttracker.Balance.Adapter.BranchViewHolder;
import com.gilmaimon.israelposttracker.Balance.Adapter.PendingPacketViewHolder;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;

class DismissPendingPacketHelper extends ItemTouchHelper.SimpleCallback {
    private final PacketSwipedListener listener;

    public DismissPendingPacketHelper(int dragDirs, int swipeDirs, PacketSwipedListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    DismissPendingPacketHelper(PacketSwipedListener listener) {
        super(0, ItemTouchHelper.LEFT);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder instanceof BranchViewHolder) return;

        if (viewHolder != null) {
            final View foregroundView = ((PendingPacketViewHolder) viewHolder).packetForegroundLayout;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView,
                                RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                int actionState, boolean isCurrentlyActive) {
        if(viewHolder instanceof BranchViewHolder) return;

        final View foregroundView = ((PendingPacketViewHolder) viewHolder).packetForegroundLayout;
        getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if(viewHolder instanceof BranchViewHolder) return;

        final View foregroundView = ((PendingPacketViewHolder) viewHolder).packetForegroundLayout;
        getDefaultUIUtil().clearView(foregroundView);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        if(viewHolder instanceof BranchViewHolder) return;

        final View foregroundView = ((PendingPacketViewHolder) viewHolder).packetForegroundLayout;

        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if(viewHolder instanceof BranchViewHolder) return;

        PendingPacketViewHolder holder = (PendingPacketViewHolder) viewHolder;

        listener.onPendingPacketSwiped(holder.packet, viewHolder.getAdapterPosition());
    }

    public interface PacketSwipedListener {
        void onPendingPacketSwiped(PendingPacket packet, int position);
    }
}
