package com.gilmaimon.israelposttracker;

import android.Manifest;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
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
import com.gilmaimon.israelposttracker.Balance.DynamicPostPacketsBalance;
import com.gilmaimon.israelposttracker.Balance.PostPacketsBalance;
import com.gilmaimon.israelposttracker.Branches.Branch;
import com.gilmaimon.israelposttracker.Branches.BranchesProvider;
import com.gilmaimon.israelposttracker.Branches.JsonBranches;
import com.gilmaimon.israelposttracker.Packets.Packet;
import com.gilmaimon.israelposttracker.Packets.PendingPacket;
import com.gilmaimon.israelposttracker.Parsing.RegexPostMessageParser;
import com.gilmaimon.israelposttracker.SMS.NewSmsBroadcastListener;
import com.gilmaimon.israelposttracker.SMS.SMSProvider;
import com.gilmaimon.israelposttracker.Sorting.KeywordsMessagesSorter;
import com.gilmaimon.israelposttracker.UserAppended.SQLiteUserAppendedActions;
import com.gilmaimon.israelposttracker.AndroidUtils.UndoableAction;

import java.util.Date;


public class MainActivity extends AppCompatActivity implements
        BranchesAndPacketsAdapter.ItemClickedListener,
        DismissPendingPacketHelper.PacketSwipedListener {

    private Permissions.OnRequestPermissionHandler mSmsPermissionHandler;
    private NewSmsBroadcastListener newSmsListener;
    private PostPacketsBalance balance;
    private BranchesAndPacketsAdapter branchesPacketsAdapter;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newSmsListener = new NewSmsBroadcastListener(this, new NewSmsBroadcastListener.NewSmsListener() {
            @Override
            public void onSmsReceived() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateRecyclerView();
                    }
                }, 100);
            }
        });

        mSmsPermissionHandler = Permissions.RequirePermission(
                this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS},
                new Permissions.PermissionCallback() {
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
                }
        );

        initDebugControllers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        newSmsListener.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        newSmsListener.unregister();
    }

    private void initDebugControllers() {
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

    private RecyclerView findAndInitRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        return recyclerView;
    }
    private void initRecyclerView() {
        RecyclerView recyclerView = findAndInitRecyclerView();
        branchesPacketsAdapter = new BranchesAndPacketsAdapter(
                this,
                balance
        );

        branchesPacketsAdapter.setClickedListener(this);

        DismissPendingPacketHelper itemTouchHelperCallback = new DismissPendingPacketHelper(this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        recyclerView.setAdapter(branchesPacketsAdapter);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void initLocals() {
        BranchesProvider branchesProvider = new JsonBranches(new RawResource(this, R.raw.branches).readAll());

        balance = new DynamicPostPacketsBalance(
                new SQLiteUserAppendedActions(this, false),
                SMSProvider.from(this, "%1111%"), // todo: change to "Israel Post" or "%1111% for debug
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

    @Override
    public void onPendingPacketSwiped(PendingPacket packet, int position) {
        final UndoableAction undoableDismiss = balance.dismissPendingPacket(packet);
        branchesPacketsAdapter.removeItemAt(position);

        Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout), "Removed " + packet.getPostId(), Snackbar.LENGTH_SHORT);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoableDismiss.undo();
                updateRecyclerView();
            }
        });
        snackbar.show();
    }
}
