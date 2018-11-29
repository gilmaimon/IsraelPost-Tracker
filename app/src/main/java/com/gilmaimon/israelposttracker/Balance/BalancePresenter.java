package com.gilmaimon.israelposttracker.Balance;

import android.os.Handler;

import com.gilmaimon.israelposttracker.AndroidUtils.UndoableAction;
import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Branches.BranchesProvider;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.SMS.NewSmsBroadcastListener;

import java.util.Date;

public class BalancePresenter implements PacketsBalanceContract.Presenter, NewSmsBroadcastListener.NewSmsListener {

    private PacketsBalanceContract.View view;
    private PostPacketsBalance balance;
    private NewSmsBroadcastListener newSmsBroadcastListener;
    private BranchesProvider branchProvider;

    public BalancePresenter(PacketsBalanceContract.View view, BranchesProvider branchProvider,
                            PostPacketsBalance balance, NewSmsBroadcastListener newSmsBroadcastListener) {
        this.view = view;
        this.balance = balance;
        this.newSmsBroadcastListener = newSmsBroadcastListener;
        this.branchProvider = branchProvider;
        view.setPresenter(this);
        view.setBalance(balance);

        newSmsBroadcastListener.setListener(this);
    }

    @Override
    public void onPacketSwiped(PendingPacket packet, int position) {
        final UndoableAction undoCallback = balance.dismissPendingPacket(packet);
        view.packetRemoved(packet, position, new UndoableAction() {
            @Override
            public void undo() {
                undoCallback.undo();
                view.balanceChanged();
            }
        });
    }

    @Override
    public void onPacketClicked(PendingPacket packet) {
        // Nothing to do
    }

    @Override
    public void onBranchClicked(Branch branch) {
        // Nothing to do
    }

    @Override
    public void newPostEntryClicked() {
        view.showNewPendingPacketMenu(branchProvider);
    }

    @Override
    public void newPendingPacketSubmitted(String postalID, int branchId, String branchPacketId) {
        if(postalID == null) {
            postalID = branchPacketId + "@" + branchId;
        }
        balance.addPendingPacket(new PendingPacket(postalID, branchId, branchPacketId, new Date()));
        view.balanceChanged();
    }

    @Override
    public void onResume() {
        newSmsBroadcastListener.register();
    }

    @Override
    public void onPause() {
        newSmsBroadcastListener.unregister();
    }

    @Override
    public void onSmsReceived() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.balanceChanged();
            }
        }, 100);
    }
}
