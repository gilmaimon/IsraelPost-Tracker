package com.gilmaimon.israelposttracker.Branches;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gilmaimon.israelposttracker.AndroidUtils.ItemClickedListener;
import com.gilmaimon.israelposttracker.AndroidUtils.ItemClickedListenerContainer;
import com.gilmaimon.israelposttracker.AndroidUtils.ItemClickedListenerContainerUtil;
import com.gilmaimon.israelposttracker.R;

import java.util.ArrayList;
import java.util.List;

public class BranchesRecyclerViewAdapter
        extends RecyclerView.Adapter<BranchesRecyclerViewAdapter.BranchViewHolder>
        implements ItemClickedListenerContainer<Branch> {

    private final Context context;
    private List<Branch> branches = null;
    private ItemClickedListenerContainerUtil<Branch> listenerUtil;

    public BranchesRecyclerViewAdapter(@NonNull Context context, @NonNull List<Branch> branches) {
        this.branches = new ArrayList<>(branches);
        this.listenerUtil = new ItemClickedListenerContainerUtil<>();
        this.context = context;
    }

    @NonNull
    @Override
    public BranchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        ViewGroup container = (ViewGroup) LayoutInflater.from(context)
                .inflate(R.layout.branch_list_item, parent, false);

        return new BranchViewHolder(
                container,
                (TextView) container.findViewById(R.id.branchIdTV),
                (TextView) container.findViewById(R.id.branchTitleTV),
                (TextView) container.findViewById(R.id.branchAddressTV)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull BranchViewHolder branchViewHolder, final int position) {
        branchViewHolder.idTV.setText(
                String.valueOf(this.branches.get(position).getId())
        );

        branchViewHolder.branchNameTV.setText(
                this.branches.get(position).getName()
        );

        branchViewHolder.branchAddressTV.setText(
                this.branches.get(position).getAddress()
        );

        branchViewHolder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listenerUtil.dispatch(branches.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    @Override
    public void setItemClickedListener(ItemClickedListener<Branch> listener) {
        this.listenerUtil.setItemClickedListener(listener);
    }

    @Override
    public void removeItemClickedListener() {
        this.listenerUtil.removeItemClickedListener();
    }

    public static class BranchViewHolder extends RecyclerView.ViewHolder {

        private ViewGroup container;
        private TextView idTV;
        private TextView branchNameTV;
        private TextView branchAddressTV;

        public BranchViewHolder(ViewGroup container,
                                TextView idTV,
                                TextView branchNameTV,
                                TextView branchAddressTV) {

            super(container);
            this.container = container;
            this.idTV = idTV;
            this.branchNameTV = branchNameTV;
            this.branchAddressTV = branchAddressTV;
        }
    }
}
