package com.gilmaimon.israelposttracker;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class DynamicPostPacketsBalance implements PostPacketsBalance {

    private final SMSProvider smsProvider;
    private final BranchesProvider branchesProvider;
    private final PostMessageSorter sorter;
    private final PostMessageParser parser;
    private Map<Branch, Set<PendingPacket>> allPendingMessages;
    private UserAppendedPacketActions userAppendedPacketActions;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    DynamicPostPacketsBalance(@NonNull UserAppendedPacketActions userAppendedPacketActions,
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

    private void addPendingPacket(PendingPacket pendingPacket) {
        Branch branch = branchesProvider.getBranch(pendingPacket.getBranchId());
        if(!allPendingMessages.containsKey(branch)) {
            allPendingMessages.put(branch, new HashSet<PendingPacket>());
        }
        allPendingMessages.get(branch).remove(pendingPacket);
        allPendingMessages.get(branch).add(pendingPacket);
    }

    private boolean processPendingPacket(SMSMessage message) {
        try {
            PendingPacket pendingPacket = parser.parseAwaitingPacketMessage(message);
            addPendingPacket(pendingPacket);
            return true;
        } catch (UnknownMessageFormat unknownMessageFormat) {
            unknownMessageFormat.printStackTrace();
            return false;
        }
    }

    private void dismissPacket(Packet packet) {
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
            dismissPacket(packet);
            return true;
        } catch (UnknownMessageFormat unknownMessageFormat) {
            unknownMessageFormat.printStackTrace();
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void reloadPendingPackets() {
        allPendingMessages.clear();

        for(SMSMessage message : smsProvider.getAllMessages()) {
            switch (sorter.sortMessage(message.getMessage())) {
                case AwaitingPickup:
                    processPendingPacket(message);
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
            addPendingPacket(pendingPacket);
        }

        // remove packets that the user removed manually
        for(Packet packet : userAppendedPacketActions.getDismissedPackets()) {
            dismissPacket(packet);
        }
    }
}
