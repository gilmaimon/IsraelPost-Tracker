package com.gilmaimon.israelposttracker.Balance;

import com.gilmaimon.israelposttracker.AndroidUtils.BasePresenter;
import com.gilmaimon.israelposttracker.AndroidUtils.BaseView;
import com.gilmaimon.israelposttracker.AndroidUtils.UndoableAction;
import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Branches.BranchesProvider;
import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;

public interface PacketsBalanceContract {
    interface Presenter extends BasePresenter {
        void onPacketSwiped(PendingPacket packet, int position);
        void onPacketClicked(PendingPacket packet);
        void onBranchClicked(Branch branch);
        void newPostEntryClicked();
        void newPendingPacketSubmitted(String postalID, int branchId, String branchPacketId);
    }

    interface View extends BaseView<Presenter> {
        // SetBalance should only be called once
        void setBalance(PostPacketsBalance balance);
        void balanceChanged();
        void packetRemoved(Packet packet, int position, final UndoableAction undoRemove);
        void showNewPendingPacketMenu(BranchesProvider provider);
    }
}
