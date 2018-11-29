package com.gilmaimon.israelposttracker.Balance;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gilmaimon.israelposttracker.AndroidUtils.UndoableAction;
import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.BranchesAndPacketsAdapter;
import com.gilmaimon.israelposttracker.DismissPendingPacketHelper;
import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.R;

public class BalanceFragment extends Fragment implements PacketsBalanceContract.View {

    private BranchesAndPacketsAdapter branchesPacketsAdapter;

    // Balance is assumed to be set when view is rendered
    private PostPacketsBalance balance;
    // Presenter is assumed to be set when view is rendered
    private PacketsBalanceContract.Presenter presenter;

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.balance_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
    }

    private RecyclerView findAndInitRecyclerView() {
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return recyclerView;
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findAndInitRecyclerView();
        branchesPacketsAdapter = new BranchesAndPacketsAdapter(
                getContext(),
                balance
        );

        branchesPacketsAdapter.setClickedListener(new BranchesAndPacketsAdapter.ItemClickedListener() {
            @Override
            public void onBranchClicked(Branch branch) {
                presenter.onBranchClicked(branch);
            }

            @Override
            public void onPacketClicked(PendingPacket packet) {
                presenter.onPacketClicked(packet);
            }
        });

        DismissPendingPacketHelper itemTouchHelperCallback = new DismissPendingPacketHelper(new DismissPendingPacketHelper.PacketSwipedListener() {
            @Override
            public void onPendingPacketSwiped(PendingPacket packet, int position) {
                presenter.onPacketSwiped(packet, position);
            }
        });

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(branchesPacketsAdapter);
    }

    @Override
    public void setBalance(PostPacketsBalance balance) {
        this.balance = balance;
    }

    @Override
    public void balanceChanged() {
        updateRecyclerView();
    }

    @Override
    public void packetRemoved(Packet packet, int position, final UndoableAction undoRemove) {
        branchesPacketsAdapter.removeItemAt(position);
        Snackbar snackbar = Snackbar.make(
                getView().findViewById(R.id.coordinatorLayout),
                "Removed " + packet.getPostId(),
                Snackbar.LENGTH_SHORT);

        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoRemove.undo();
            }
        });
        snackbar.show();
    }

    private void updateRecyclerView() {
        branchesPacketsAdapter.notifyDataSetChanged();
    }

    @Override
    public void setPresenter(PacketsBalanceContract.Presenter presenter) {
        this.presenter = presenter;
    }
}
