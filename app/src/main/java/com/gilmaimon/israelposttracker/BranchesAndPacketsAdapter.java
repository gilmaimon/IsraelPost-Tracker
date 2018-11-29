package com.gilmaimon.israelposttracker;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Branches.BranchViewHolder;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.Balance.PostPacketsBalance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BranchesAndPacketsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_TYPE_BRANCH = 1;
    private final int ITEM_TYPE_POSTAL_ITEM = 2;

    private Context context;
    private PostPacketsBalance balance;
    private List<Object> dataset;

    public void removeClickedListener() {
        clickedListener = null;
    }

    public void setClickedListener(ItemClickedListener listener) {
        this.clickedListener = listener;
    }

    public void removeItemAt(int position) {
        int sizeBeforeRemoval = dataset.size();
        updateDatasetFromPendingPacketsMap();
        int sizeAfterRemoval = dataset.size();
        if(dataset.size() == 0) {
            notifyDataSetChanged();
            return;
        }
        if(sizeBeforeRemoval - sizeAfterRemoval > 1) {
            notifyItemRemoved(position - 1);
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(Math.max(position, 0), dataset.size());
    }

    public interface ItemClickedListener {
        void onBranchClicked(Branch branch);
        void onPacketClicked(PendingPacket packet);
    }

    private ItemClickedListener clickedListener;

    public BranchesAndPacketsAdapter(@NonNull Context context, @NonNull PostPacketsBalance balance) {
        this.context = context;
        this.balance = balance;
        this.dataset = new ArrayList<>();
        updateDatasetFromPendingPacketsMap();

        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                updateDatasetFromPendingPacketsMap();
            }
        });

    }

    private void updateDatasetFromPendingPacketsMap() {
        Map<Branch, Set<PendingPacket>> pendingPackets = balance.getAllPendingPackets();
        this.dataset.clear();
        for (Branch branch : pendingPackets.keySet()) {
            this.dataset.add(branch);
            List<PendingPacket> branchPackets = new ArrayList<>(pendingPackets.get(branch));
            Collections.sort(branchPackets, new Comparator<PendingPacket>() {
                @Override
                public int compare(PendingPacket lhs, PendingPacket rhs) {
                    return lhs.getLastNotice().compareTo(rhs.getLastNotice());
                }
            });
            this.dataset.addAll(branchPackets);
        }
    }

    class PendingPacketViewHolder extends RecyclerView.ViewHolder {
        public PendingPacket packet;
        ViewGroup packetForegroundLayout;
        TextView postalIdTV;
        TextView packetBranchIdTV;
        TextView lastNoticeTV;

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

        @Override
        public int getItemViewType(int position) {
            if(dataset.get(position) instanceof Branch)
                return ITEM_TYPE_BRANCH;
            else if(dataset.get(position) instanceof PendingPacket)
                return ITEM_TYPE_POSTAL_ITEM;
            return -1;
        }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_TYPE_BRANCH:
                    ViewGroup branchContainer = (ViewGroup) LayoutInflater.from(this.context)
                            .inflate(R.layout.branch_list_item, parent, false);

                    return new BranchViewHolder(
                        branchContainer,
                        (TextView) branchContainer.findViewById(R.id.branchIdTV),
                        (TextView) branchContainer.findViewById(R.id.branchTitleTV),
                        (TextView) branchContainer.findViewById(R.id.branchAddressTV)
                    );
                case ITEM_TYPE_POSTAL_ITEM:
                    ViewGroup packetContainer = (ViewGroup) LayoutInflater.from(this.context)
                            .inflate(R.layout.packet_list_item, parent, false);

                    return new PendingPacketViewHolder(
                        packetContainer,
                        (ViewGroup) packetContainer.findViewById(R.id.packetForegroundLayout),
                        (TextView) packetContainer.findViewById(R.id.postalIdTV),
                        (TextView) packetContainer.findViewById(R.id.packetBranchIdTV),
                        (TextView) packetContainer.findViewById(R.id.lastNoticeTV)
                    );
            }
            return null;
        }

        private String timeAgo(Date date) {
            long time = date.getTime();
            long now = System.currentTimeMillis();

            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.SECOND_IN_MILLIS);
            return String.valueOf(ago);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            switch (holder.getItemViewType()) {
                case ITEM_TYPE_BRANCH:
                    BranchViewHolder branchViewHolder = (BranchViewHolder) holder;
                    final Branch branch = (Branch) dataset.get(position);

                    branchViewHolder.container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(clickedListener != null) {
                                clickedListener.onBranchClicked(branch);
                            }
                        }
                    });

                    branchViewHolder.branchNameTV.setText(branch.getName());
                    branchViewHolder.branchAddressTV.setText(branch.getAddress());
                    branchViewHolder.idTV.setText(String.valueOf(branch.getId()));
                    break;

                case ITEM_TYPE_POSTAL_ITEM:
                    PendingPacketViewHolder packetViewHolder = (PendingPacketViewHolder) holder;
                    final PendingPacket packet = (PendingPacket) dataset.get(position);

                    packetViewHolder.packet = packet;

                    packetViewHolder.packetForegroundLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(clickedListener != null) {
                                clickedListener.onPacketClicked(packet);
                            }
                        }
                    });

                    packetViewHolder.postalIdTV.setText(packet.getPostId());
                    packetViewHolder.packetBranchIdTV.setText(packet.getBranchPacketId());
                    packetViewHolder.lastNoticeTV.setText(
                            timeAgo(packet.getLastNotice())
                    );
                    break;
            }
        }
    }
