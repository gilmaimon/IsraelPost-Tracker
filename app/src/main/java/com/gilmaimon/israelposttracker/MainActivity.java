package com.gilmaimon.israelposttracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gilmaimon.israelposttracker.AndroidUtils.Permissions;
import com.gilmaimon.israelposttracker.AndroidUtils.RawResource;
import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Branches.BranchesProvider;
import com.gilmaimon.israelposttracker.Branches.JsonBranches;
import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.Balance.DynamicPostPacketsBalance;
import com.gilmaimon.israelposttracker.Balance.PostPacketsBalance;
import com.gilmaimon.israelposttracker.Parsing.RegexPostMessageParser;
import com.gilmaimon.israelposttracker.SMS.IncomingIsraelPostSMSMessages;
import com.gilmaimon.israelposttracker.SMS.SMSProvider;
import com.gilmaimon.israelposttracker.Sorting.KeywordsMessagesSorter;
import com.gilmaimon.israelposttracker.UserAppended.TemporaryUserAppendedPacketActions;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements Permissions.PermissionCallback, BranchesAndPacketsAdapter.ItemClickedListener {

    private Permissions.OnRequestPermissionHandler mSmsPermissionHandler;
    private BroadcastReceiver newSmsMessageReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        mSmsPermissionHandler = Permissions.RequirePermission(
                this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                this
        );

        newSmsMessageReceiver = new BroadcastReceiver() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onReceive(Context context, Intent intent) {
                updateRecyclerView();
            }
        };

        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(
                        newSmsMessageReceiver ,
                        new IntentFilter(IncomingIsraelPostSMSMessages.NEW_SMS_BROADCAST_ACTION)
                );
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(newSmsMessageReceiver);
    }

    private BranchesProvider branchesProvider;
    private PostPacketsBalance balance;
    private BranchesAndPacketsAdapter branchesPacketsAdapter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.debugRemoveBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packetId =((EditText) findViewById(R.id.debugPacketIdToRemove)).getText().toString();
                balance.dismissPendingPacket(new Packet(packetId));
                updateRecyclerView();
            }
        });

        findViewById(R.id.debugAddBTN).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String packetId =((EditText) findViewById(R.id.debugPacketIdET)).getText().toString();
                String branchId =((EditText) findViewById(R.id.debugBranchIdET)).getText().toString();
                PendingPacket pendingPacket = new PendingPacket(
                        branchId + '@' + packetId,
                        Integer.valueOf(branchId),
                        packetId,
                        new Date()
                );
                balance.addPendingPacket(pendingPacket);
                updateRecyclerView();
            }
        });
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        branchesPacketsAdapter = new BranchesAndPacketsAdapter(
                this,
                balance
        );

        branchesPacketsAdapter.setClickedListener(this);

        DismissPendingMessageItemTouchHelper itemTouchHelperCallback = new DismissPendingMessageItemTouchHelper(
                0,
                ItemTouchHelper.LEFT,
                new DismissPendingMessageItemTouchHelper.RecyclerItemTouchHelperListener() {
                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
                        String swipedPacketId = ((BranchesAndPacketsAdapter.PendingPacketViewHolder) viewHolder).postalIdTV.getText().toString();
                        balance.dismissPendingPacket(new Packet(swipedPacketId));
                        branchesPacketsAdapter.removeItemAt(position);
                    }
                });

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(branchesPacketsAdapter);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    void initLocals() {
        branchesProvider = new JsonBranches(new RawResource(this, R.raw.branches).readAll());

        balance = new DynamicPostPacketsBalance(
                new TemporaryUserAppendedPacketActions(branchesProvider),
                SMSProvider.from(this, "Israel Post"), // todo: change to "Israel Post"
                branchesProvider,
                KeywordsMessagesSorter.getDefault(),
                new RegexPostMessageParser()
        );
        initRecyclerView();
    }

    private void updateRecyclerView() {
        balance.notifyStateChanged();
        branchesPacketsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mSmsPermissionHandler.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void permissionGranted(String[] permissions) {
        Toast.makeText(MainActivity.this, "Got it, tnx!", Toast.LENGTH_LONG).show();
        initLocals();
    }

    @Override
    public void permissionDenied(String[] permissions) {
        Toast.makeText(MainActivity.this, "Gotta have dat permission", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onBranchClicked(Branch branch) {
        EditText debugBranchIdET = findViewById(R.id.debugBranchIdET);
        debugBranchIdET.setText(String.valueOf(branch.getId()));
    }

    @Override
    public void onPacketClicked(PendingPacket packet) {
        EditText debugPacketIdToRemove = findViewById(R.id.debugPacketIdToRemove);
        debugPacketIdToRemove.setText(packet.getPostId());
    }
}
