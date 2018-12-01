package com.gilmaimon.israelposttracker.Balance.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gilmaimon.israelposttracker.AndroidUtils.StringUtils;
import com.gilmaimon.israelposttracker.Balance.PostPacketsBalance;
import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PacketsBalanceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int ITEM_TYPE_BRANCH = 1;
    private final int ITEM_TYPE_POSTAL_ITEM = 2;

    private Context context;
    private PostPacketsBalance balance;
    private List<Object> dataset;
    private ItemClickedListener clickedListener;

    public boolean isEmpty() {
        return dataset.size() == 0;
    }

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
        if (dataset.size() == 0) {
            notifyDataSetChanged();
            return;
        }
        if (sizeBeforeRemoval - sizeAfterRemoval > 1) {
            notifyItemRemoved(position - 1);
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(Math.max(position, 0), dataset.size());
    }

    public interface ItemClickedListener {
        void onBranchClicked(Branch branch);
        void onPacketClicked(PendingPacket packet);
    }


    public PacketsBalanceAdapter(@NonNull Context context, @NonNull PostPacketsBalance balance) {
        this.context = context;
        this.balance = balance;
        this.dataset = new ArrayList<>();
        updateDatasetFromPendingPacketsMap();
        notifyDataSetChanged();

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


    @Override
    public int getItemViewType(int position) {
        if (dataset.get(position) instanceof Branch)
            return ITEM_TYPE_BRANCH;
        else if (dataset.get(position) instanceof PendingPacket)
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
                        (TextView) branchContainer.findViewById(R.id.branchAddressTV),
                        (ViewGroup) branchContainer.findViewById(R.id.branchPhoneContainer),
                        (TextView) branchContainer.findViewById(R.id.branchPhoneNumberTV)
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

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case ITEM_TYPE_BRANCH:
                BranchViewHolder branchViewHolder = (BranchViewHolder) holder;
                final Branch branch = (Branch) dataset.get(position);

                branchViewHolder.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickedListener != null) {
                            clickedListener.onBranchClicked(branch);
                        }
                    }
                });

                branchViewHolder.branchNameTV.setText(branch.getName());
                branchViewHolder.branchAddressTV.setText(branch.getAddress());
                branchViewHolder.idTV.setText(String.valueOf(branch.getId()));

                if(branch.getPhone() == null) {
                    branchViewHolder.branchPhoneGroup.setVisibility(View.GONE);
                } else {
                    branchViewHolder.branchPhoneGroup.setVisibility(View.VISIBLE);
                    branchViewHolder.branchPhoneGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + branch.getPhone()));
                            context.startActivity(intent);
                        }
                    });
                    branchViewHolder.branchPhoneNumberTV.setText(branch.getPhone());
                }
                break;

            case ITEM_TYPE_POSTAL_ITEM:
                PendingPacketViewHolder packetViewHolder = (PendingPacketViewHolder) holder;
                final PendingPacket packet = (PendingPacket) dataset.get(position);

                packetViewHolder.packet = packet;

                packetViewHolder.packetForegroundLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (clickedListener != null) {
                            clickedListener.onPacketClicked(packet);
                        }
                    }
                });

                packetViewHolder.postalIdTV.setText(packet.getPostId());
                packetViewHolder.packetBranchIdTV.setText(packet.getBranchPacketId());
                packetViewHolder.lastNoticeTV.setText(
                        StringUtils.formatTimeAgo(packet.getLastNotice())
                );
                break;
        }
    }
}
