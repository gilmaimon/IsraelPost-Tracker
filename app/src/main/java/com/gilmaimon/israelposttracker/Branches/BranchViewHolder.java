package com.gilmaimon.israelposttracker.Branches;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

public class BranchViewHolder extends RecyclerView.ViewHolder {

    public ViewGroup container;
    public TextView idTV;
    public TextView branchNameTV;
    public TextView branchAddressTV;

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
