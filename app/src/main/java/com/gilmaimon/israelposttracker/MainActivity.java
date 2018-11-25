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
import android.widget.Toast;

import com.gilmaimon.israelposttracker.AndroidUtils.Permissions;
import com.gilmaimon.israelposttracker.AndroidUtils.RawResource;
import com.gilmaimon.israelposttracker.Branches.JsonBranches;
import com.gilmaimon.israelposttracker.Parsing.RegexPostMessageParser;
import com.gilmaimon.israelposttracker.SMS.IncomingIsraelPostSMSMessages;
import com.gilmaimon.israelposttracker.SMS.SMSProvider;
import com.gilmaimon.israelposttracker.Sorting.KeywordsMessagesSorter;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements Permissions.PermissionCallback {

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
                try {
                    showAllPendingPackets();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showAllPendingPackets() throws IOException {
        PostPacketsBalance balance = new DynamicPostPacketsBalance(
                SMSProvider.from(this, "Israel Post"),
                new JsonBranches(new RawResource(this, R.raw.branches).readAll()),
                KeywordsMessagesSorter.getDefault(),
                new RegexPostMessageParser()
        );

        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        BranchesAndPacketsAdapter adapter = new BranchesAndPacketsAdapter(
                this,
                balance.getAllPendingPackets()
        );
        recyclerView.setAdapter(adapter);
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
        try {
            showAllPendingPackets();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void permissionDenied(String[] permissions) {
        Toast.makeText(MainActivity.this, "Gotta have dat permission", Toast.LENGTH_LONG).show();
        finish();
    }
}
