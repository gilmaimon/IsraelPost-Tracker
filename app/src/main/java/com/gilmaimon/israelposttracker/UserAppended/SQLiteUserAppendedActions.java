package com.gilmaimon.israelposttracker.UserAppended;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gilmaimon.israelposttracker.AndroidUtils.UndoableAction;
import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SQLiteUserAppendedActions extends SQLiteOpenHelper implements UserAppendedPacketActions {

    private static final String DATABASE_NAME = "UserAppendedActions.db";

    private static final String TABLE_NAME = "actions";
    private static final String ID = "_id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_POST_ID = "post_id";
    private static final String COLUMN_BRANCH_ID = "branch_id";
    private static final String COLUMN_BRANCH_PLACEMENT_ID = "branch_placement_id";
    private static final String COLUMN_LAST_NOTICE = "last_notice";


    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_TYPE + " TEXT," +
                    COLUMN_POST_ID + " TEXT," +
                    COLUMN_BRANCH_ID + " INTEGER," +
                    COLUMN_LAST_NOTICE + " INTEGER," +
                    COLUMN_BRANCH_PLACEMENT_ID + " TEXT)";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    private static final String TYPE_DISMISS = "DISMISS";
    private static final String TYPE_PENDING = "PENDING";

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO: move data to the new format
        sqLiteDatabase.execSQL(SQL_DELETE_TABLE);
        onCreate(sqLiteDatabase);
    }

    public SQLiteUserAppendedActions(Context context, boolean debugStartFresh) {
        super(context, DATABASE_NAME, null, 1);
        if(debugStartFresh) {
            onUpgrade(getWritableDatabase(), 1, 1);
        }
    }

    @Override
    public UndoableAction dismissPendingPacket(final Packet packet) {
        // construct the new entry
        ContentValues dismissedPacket = new ContentValues();
        dismissedPacket.put(COLUMN_TYPE, TYPE_DISMISS);
        dismissedPacket.put(COLUMN_POST_ID, packet.getPostId());
        dismissedPacket.put(COLUMN_BRANCH_ID, -1);
        dismissedPacket.put(COLUMN_BRANCH_PLACEMENT_ID, "Unknown");
        dismissedPacket.put(COLUMN_LAST_NOTICE, 0);

        // add the new entry for the dismissed packet
        getWritableDatabase().insert(TABLE_NAME, null, dismissedPacket);
        return new UndoableAction() {
            @Override
            public void undo() {
                getWritableDatabase().delete(
                        TABLE_NAME,
                        COLUMN_POST_ID + " = ? AND " + COLUMN_TYPE + " = ?",
                        new String[]{packet.getPostId(), TYPE_DISMISS});
            }
        };
    }

    @Override
    public void addPendingPacket(PendingPacket packet) {
        // remove packet actions with the same postal id in the database
        getWritableDatabase().delete(TABLE_NAME, COLUMN_POST_ID + " = ?", new String[]{packet.getPostId()});

        // construct the new entry
        ContentValues newPendingPacketEntry = new ContentValues();
        newPendingPacketEntry.put(COLUMN_TYPE, TYPE_PENDING);
        newPendingPacketEntry.put(COLUMN_POST_ID, packet.getPostId());
        newPendingPacketEntry.put(COLUMN_BRANCH_ID, packet.getBranchId());
        newPendingPacketEntry.put(COLUMN_BRANCH_PLACEMENT_ID, packet.getBranchPacketId());
        newPendingPacketEntry.put(COLUMN_LAST_NOTICE, packet.getLastNotice().getTime());

        // add the new entry for the dismissed packet
        getWritableDatabase().insert(TABLE_NAME, null, newPendingPacketEntry);
    }

    @Override
    public List<Packet> getDismissedPackets() {
        List<Packet> packets = new ArrayList<>();

        Cursor dismissedPacketsCursor = getReadableDatabase().query(
                TABLE_NAME,
                null,
                COLUMN_TYPE + " = ?",
                new String[]{ TYPE_DISMISS },
                null, null, null);

        if(dismissedPacketsCursor.moveToFirst()) {
            do {
                String postalId = dismissedPacketsCursor.getString(dismissedPacketsCursor.getColumnIndex(COLUMN_POST_ID));
                packets.add(new Packet(postalId));
            } while (dismissedPacketsCursor.moveToNext());
        }

        dismissedPacketsCursor.close();
        return packets;
    }

    private List<PendingPacket> getAllPendingPacketEntries() {
        List<PendingPacket> packets = new ArrayList<>();

        Cursor pendingPacketsCursor = getReadableDatabase().query(
                TABLE_NAME,
                null,
                COLUMN_TYPE + " = ?",
                new String[]{ TYPE_PENDING },
                null, null, null);

        if(pendingPacketsCursor .moveToFirst()) {
            do {
                String postalId = pendingPacketsCursor.getString(pendingPacketsCursor.getColumnIndex(COLUMN_POST_ID));
                int branchId = pendingPacketsCursor.getInt(pendingPacketsCursor.getColumnIndex(COLUMN_BRANCH_ID));
                String branchPacketId = pendingPacketsCursor.getString(pendingPacketsCursor.getColumnIndex(COLUMN_BRANCH_PLACEMENT_ID));
                long lastNotice = pendingPacketsCursor.getLong(pendingPacketsCursor.getColumnIndex(COLUMN_LAST_NOTICE));
                packets.add(
                        new PendingPacket(postalId, branchId, branchPacketId, lastNotice)
                );
            } while (pendingPacketsCursor .moveToNext());
        }

        pendingPacketsCursor.close();
        return packets;
    }

    @Override
    public List<PendingPacket> getPendingPackets() {
        List<PendingPacket> pendingPacketEntries = getAllPendingPacketEntries();
        List<Packet> dismissedPacketEntries = getDismissedPackets();
        Set<PendingPacket> pendingPackets = new HashSet<>(pendingPacketEntries);
        for(Packet dismissedPacket : dismissedPacketEntries) {
            pendingPackets.remove(dismissedPacket);
        }

        return new ArrayList<>(pendingPackets);
    }

}
