package com.gilmaimon.israelposttracker.Balance;

import android.os.Handler;

import com.gilmaimon.israelposttracker.AndroidUtils.UndoableAction;
import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.SMS.NewSmsBroadcastListener;

public class BalancePresenter implements PacketsBalanceContract.Presenter, NewSmsBroadcastListener.NewSmsListener {

    private PacketsBalanceContract.View view;
    private PostPacketsBalance balance;
    private NewSmsBroadcastListener newSmsBroadcastListener;

    public BalancePresenter(PacketsBalanceContract.View view, PostPacketsBalance balance, NewSmsBroadcastListener newSmsBroadcastListener) {
        this.view = view;
        this.balance = balance;
        this.newSmsBroadcastListener = newSmsBroadcastListener;
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
                balance.notifyStateChanged();
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
                balance.notifyStateChanged();
                view.balanceChanged();
            }
        }, 100);
    }
}
