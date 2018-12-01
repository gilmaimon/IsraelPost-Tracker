package com.gilmaimon.israelposttracker.Balance;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Toast;

import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Branches.BranchesProvider;
import com.gilmaimon.israelposttracker.R;

import java.util.ArrayList;
import java.util.List;

class NewPendingPacketDialog extends Dialog {

    interface OnPendingPacketSubmit {
        void onSubmit(String branchPlacement, int branch);
    }

    public NewPendingPacketDialog(@NonNull Context context, BranchesProvider branchesProvider, OnPendingPacketSubmit callback) {
        super(context);
        this.branchesProvider = branchesProvider;
        this.callback = callback;
    }


    private final BranchesProvider branchesProvider;
    private final OnPendingPacketSubmit callback;
    private EditText packetPlacement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.new_pending_packed_dialog_layout);

        final AutoCompleteTextView branchInput = findViewById(R.id.dialogBranchATV);
        packetPlacement = findViewById(R.id.dialogPacketPlacementET);
        Button addBtn = findViewById(R.id.dialogAddBTN);


        final ArrayAdapter<Branch> arrayAdapter = new AutoCompleteAdapterBranches(getContext(), branchesProvider.getAll());
        branchInput.setAdapter(arrayAdapter);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputPlacement = packetPlacement.getText().toString();
                int branchId = branchIdFromName(branchInput.getText().toString());

                if(inputPlacement.isEmpty()) {
                    Toast.makeText(getContext(), R.string.invalid_dialog_input_id, Toast.LENGTH_SHORT).show();
                }
                else if(branchId == -1) {
                    Toast.makeText(getContext(), R.string.invalid_dialog_input_branch, Toast.LENGTH_SHORT).show();
                }
                else {
                    callback.onSubmit(inputPlacement, branchId);
                    hide();
                }
            }
        });

    }

    private int branchIdFromName(String inputBranch) {
        for(Branch branch : branchesProvider.getAll()) {
            if(branch.getName().equals(inputBranch)) return branch.getId();
        }
        return -1;
    }


    class AutoCompleteAdapterBranches extends ArrayAdapter<Branch> {

        private final List<Branch> allBranches;

        AutoCompleteAdapterBranches(@NonNull Context context, @NonNull List<Branch> objects) {
            super(context, android.R.layout.simple_dropdown_item_1line, objects);
            this.allBranches = new ArrayList<>(objects);
        }

        @NonNull
        @Override
        public Filter getFilter() {
            return nameFilter;
        }

        final Filter nameFilter = new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((Branch)(resultValue)).getName();
            }
            @Override
            protected FilterResults performFiltering(CharSequence csConstraint) {
                String constraint = String.valueOf(csConstraint);
                if(constraint != null) {
                    List<Branch> suggestions = new ArrayList<>();
                    for (Branch branch : allBranches) {
                        try {
                            if(branch.getName().contains(constraint) ||
                                    branch.getAddress().contains(constraint)){
                                suggestions.add(branch);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = suggestions;
                    filterResults.count = suggestions.size();
                    return filterResults;
                } else {
                    return new FilterResults();
                }
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                ArrayList<Branch> filteredList = (ArrayList<Branch>) results.values;
                if(results.count > 0) {
                    clear();
                    for (Branch b : filteredList) {
                        add(b);
                    }
                    notifyDataSetChanged();
                }
            }
        };
    }
}
