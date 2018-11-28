package com.gilmaimon.israelposttracker.Balance;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Branches.BranchesProvider;
import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.Parsing.PostMessageParser;
import com.gilmaimon.israelposttracker.Parsing.UnknownMessageFormat;
import com.gilmaimon.israelposttracker.SMS.SMSMessage;
import com.gilmaimon.israelposttracker.SMS.SMSProvider;
import com.gilmaimon.israelposttracker.Sorting.PostMessageSorter;
import com.gilmaimon.israelposttracker.AndroidUtils.UndoableAction;
import com.gilmaimon.israelposttracker.UserAppended.UserAppendedPacketActions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DynamicPostPacketsBalance implements PostPacketsBalance {

    private final SMSProvider smsProvider;
    private final BranchesProvider branchesProvider;
    private final PostMessageSorter sorter;
    private final PostMessageParser parser;

    private Map<Branch, Set<PendingPacket>> allPendingMessages;

    private UserAppendedPacketActions userAppendedPacketActions;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public DynamicPostPacketsBalance(@NonNull UserAppendedPacketActions userAppendedPacketActions,
                                     @NonNull SMSProvider smsProvider,
                                     @NonNull BranchesProvider branchesProvider,
                                     @NonNull PostMessageSorter sorter,
                                     @NonNull PostMessageParser parser) {

        allPendingMessages = new HashMap<>();

        this.userAppendedPacketActions = userAppendedPacketActions;
        this.smsProvider = smsProvider;
        this.branchesProvider = branchesProvider;
        this.sorter = sorter;
        this.parser = parser;

        reloadPendingPackets();
        Log.v("Done", "Done");
    }

    @NonNull
    @Override
    public HashMap<Branch, Set<PendingPacket>> getAllPendingPackets() {
        return new HashMap<>(allPendingMessages);
    }

    @Override
    public Set<PendingPacket> getPendingFromBranch(int branchId) {
        for(Branch branch : allPendingMessages.keySet()) {
            if(branch.getId() == branchId) {
                return new HashSet<>(allPendingMessages.get(branch));
            }
        }
        return new HashSet<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void notifyStateChanged() {
        reloadPendingPackets();
    }

    private void _addPendingPacket(PendingPacket pendingPacket, Set<PendingPacket> seenPendingPackets) {
        // This trick is for making sure the same packet (or postal id) dosent exist in 2 branches in the same time
        // so when a packet was already seen before (same postal id) we dismiss it from everywhere in our branches map
        // and it can be added to the current branch as it is the latest one
        if(seenPendingPackets.contains(pendingPacket)) {
            _dismissPacket(pendingPacket);
        } else seenPendingPackets.add(pendingPacket);

        Branch branch = branchesProvider.getBranch(pendingPacket.getBranchId());
        if(!allPendingMessages.containsKey(branch)) {
            allPendingMessages.put(branch, new HashSet<PendingPacket>());
        }
        allPendingMessages.get(branch).remove(pendingPacket);
        allPendingMessages.get(branch).add(pendingPacket);
    }

    private boolean processPendingPacket(SMSMessage message, Set<PendingPacket> seenPendingPackets) {
        try {
            PendingPacket pendingPacket = parser.parseAwaitingPacketMessage(message);
            _addPendingPacket(pendingPacket, seenPendingPackets);
            return true;
        } catch (UnknownMessageFormat unknownMessageFormat) {
            unknownMessageFormat.printStackTrace();
            return false;
        }
    }

    private void _dismissPacket(Packet packet) {
        for(Branch branch : allPendingMessages.keySet()) {
            if(allPendingMessages.get(branch).remove(packet)) {
                if(allPendingMessages.get(branch).isEmpty()) {
                    allPendingMessages.remove(branch);
                }
                break;
            }
        }
    }
    private boolean processPickedUpPacket(SMSMessage message) {
        try {
            Packet packet = parser.parsePickedUpMessage(message);
            _dismissPacket(packet);
            return true;
        } catch (UnknownMessageFormat unknownMessageFormat) {
            unknownMessageFormat.printStackTrace();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void reloadPendingPackets() {
        allPendingMessages.clear();

        Set<PendingPacket> seenPendingPackets = new HashSet<>();
        for(SMSMessage message : smsProvider.getAllMessages()) {
            switch (sorter.sortMessage(message.getMessage())) {
                case AwaitingPickup:
                    processPendingPacket(message, seenPendingPackets);
                    break;

                case PickedUp:
                    processPickedUpPacket(message);
                    break;

                case Unknown:
                    // Nothing to do when sorting fails
                    break;
            }
        }

        // Add packets that the user added manually
        for(PendingPacket pendingPacket : userAppendedPacketActions.getPendingPackets()) {
            _addPendingPacket(pendingPacket, seenPendingPackets);
        }

        // remove packets that the user removed manually
        for(Packet packet : userAppendedPacketActions.getDismissedPackets()) {
            _dismissPacket(packet);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public UndoableAction dismissPendingPacket(Packet packet) {
        UndoableAction result = userAppendedPacketActions.dismissPendingPacket(packet);
        notifyStateChanged();
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void addPendingPacket(PendingPacket packet) {
        userAppendedPacketActions.addPendingPacket(packet);
        notifyStateChanged();
    }

    @Override
    public List<Packet> getDismissedPackets() {
        return userAppendedPacketActions.getDismissedPackets();
    }

    @Override
    public List<PendingPacket> getPendingPackets() {
        return userAppendedPacketActions.getPendingPackets();
    }
}
