package com.gilmaimon.israelposttracker.Balance.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

public class BranchViewHolder extends RecyclerView.ViewHolder {

    public final ViewGroup container;
    public final TextView idTV;
    public final TextView branchNameTV;
    public final TextView branchAddressTV;
    public final ViewGroup branchPhoneGroup;
    public final TextView branchPhoneNumberTV;

    public BranchViewHolder(ViewGroup container,
                            TextView idTV,
                            TextView branchNameTV,
                            TextView branchAddressTV,
                            ViewGroup branchPhoneGroup,
                            TextView branchPhoneNumberTV) {

        super(container);
        this.container = container;
        this.idTV = idTV;
        this.branchNameTV = branchNameTV;
        this.branchAddressTV = branchAddressTV;
        this.branchPhoneGroup = branchPhoneGroup;
        this.branchPhoneNumberTV = branchPhoneNumberTV;
    }
}